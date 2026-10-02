package id.molca.pipeline.silver.stall;

import id.molca.pipeline.silver.master.MasterBroadcast;
import id.molca.pipeline.silver.model.MasterChange;
import id.molca.pipeline.silver.model.StallInput;
import id.molca.pipeline.silver.model.StopRow;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * F7-F13, keyed by work unit. Inputs (increments of the watched counter, reason-tag values) are buffered and
 * applied in event-time order; StallLogic decides, event-time timers drive +60 s / +300 s / shift-end checks.
 */
public class StallDetector extends KeyedBroadcastProcessFunction<Long, StallInput, MasterChange, StopRow> {
    private final MasterBroadcast master = new MasterBroadcast();
    private transient ValueState<StallState> state;
    private transient MapState<Long, List<StallInput>> buffer;

    @Override
    public void open(OpenContext ctx) {
        state = getRuntimeContext().getState(new ValueStateDescriptor<>("stall", StallState.class));
        buffer = getRuntimeContext().getMapState(new MapStateDescriptor<>("buffer", Types.LONG,
                TypeInformation.of(new TypeHint<List<StallInput>>() {})));
    }

    @Override
    public void processBroadcastElement(MasterChange c, Context ctx, Collector<StopRow> out) throws Exception {
        master.apply(c, ctx.getBroadcastState(MasterBroadcast.DESCRIPTOR));
    }

    @Override
    public void processElement(StallInput in, ReadOnlyContext ctx, Collector<StopRow> out) throws Exception {
        long wm = ctx.timerService().currentWatermark();
        if (in.eventTimeMs > wm) {
            List<StallInput> l = buffer.get(in.eventTimeMs);
            if (l == null) l = new ArrayList<>();
            l.add(in);
            buffer.put(in.eventTimeMs, l);
            ctx.timerService().registerEventTimeTimer(in.eventTimeMs);
        } else {
            apply(List.of(in), ctx, out);   // late but per-key order is checked by StallLogic
        }
    }

    @Override
    public void onTimer(long ts, OnTimerContext ctx, Collector<StopRow> out) throws Exception {
        List<StallInput> l = buffer.get(ts);
        if (l != null) { buffer.remove(ts); apply(l, ctx, out); }
        StallState s = state.value();
        if (s == null) return;
        StallContext sc = StallContext.of(master.get(ctx.getBroadcastState(MasterBroadcast.DESCRIPTOR)));
        emit(StallLogic.onTimer(s, sc, ctx.getCurrentKey(), ts), ctx, out);
        state.update(s);
    }

    private void apply(List<StallInput> inputs, ReadOnlyContext ctx, Collector<StopRow> out) throws Exception {
        StallState s = state.value();
        if (s == null) s = new StallState();
        StallContext sc = StallContext.of(master.get(ctx.getBroadcastState(MasterBroadcast.DESCRIPTOR)));
        List<StallInput> sorted = new ArrayList<>(inputs);
        sorted.sort(Comparator.comparingInt(x -> StallInput.REASON.equals(x.type) ? 0 : 1));  // reason before increment
        for (StallInput in : sorted) {
            StallLogic.Out o = StallInput.INC.equals(in.type)
                    ? StallLogic.onIncrement(s, sc, in.workUnitId, in.tagId, in.assetId, in.siteId, in.productId, in.eventTimeMs)
                    : StallLogic.onReason(s, sc, in.tagId, in.downtimeReasonId, in.valueText, in.eventTimeMs);
            emit(o, ctx, out);
        }
        state.update(s);
    }

    private void emit(StallLogic.Out o, ReadOnlyContext ctx, Collector<StopRow> out) {
        for (StopRow r : o.rows) out.collect(r);
        for (Long t : o.timers) ctx.timerService().registerEventTimeTimer(t);
    }
}
