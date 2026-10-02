package id.molca.pipeline.silver.lane;

import id.molca.pipeline.silver.Tags;
import id.molca.pipeline.silver.master.MasterBroadcast;
import id.molca.pipeline.silver.master.MasterSnapshot;
import id.molca.pipeline.silver.model.DlqRecord;
import id.molca.pipeline.silver.model.FactRow;
import id.molca.pipeline.silver.model.MasterChange;
import id.molca.pipeline.silver.model.Reading;
import id.molca.pipeline.silver.model.StallInput;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * F4-F6, keyed by lane (asset|work unit). Readings are buffered and handled in event-time order when the
 * watermark passes them. A reading behind the watermark is still accepted if it is newer than the last one of its
 * tag; an older one is out of order and goes to the DLQ.
 *  - product_code -> ProductTracker (30 s debounce)
 *  - total/good/reject -> CounterLogic delta -> FactRow (product attached); an increment of the stall-watched tag
 *    also goes to the stall detector (side output STALL)
 *  - downtime_reason -> side output STALL (REASON)
 */
public class LaneProcessor extends KeyedBroadcastProcessFunction<String, Reading, MasterChange, FactRow> {
    private final MasterBroadcast master = new MasterBroadcast();
    private transient MapState<Long, List<Reading>> buffer;      // event time -> readings
    private transient MapState<Long, String> lastValue;          // tag -> last raw value (decimal text)
    private transient MapState<Long, Long> lastTs;               // tag -> last processed event time
    private transient ValueState<ProductTracker> product;
    private transient ValueState<Long> productTagId;

    @Override
    public void open(OpenContext ctx) {
        buffer = getRuntimeContext().getMapState(new MapStateDescriptor<>("buffer", Types.LONG,
                TypeInformation.of(new TypeHint<List<Reading>>() {})));
        lastValue = getRuntimeContext().getMapState(new MapStateDescriptor<>("last-value", Types.LONG, Types.STRING));
        lastTs = getRuntimeContext().getMapState(new MapStateDescriptor<>("last-ts", Types.LONG, Types.LONG));
        product = getRuntimeContext().getState(new ValueStateDescriptor<>("product", ProductTracker.class));
        productTagId = getRuntimeContext().getState(new ValueStateDescriptor<>("product-tag", Types.LONG));
    }

    @Override
    public void processBroadcastElement(MasterChange c, Context ctx, Collector<FactRow> out) throws Exception {
        master.apply(c, ctx.getBroadcastState(MasterBroadcast.DESCRIPTOR));
    }

    @Override
    public void processElement(Reading r, ReadOnlyContext ctx, Collector<FactRow> out) throws Exception {
        long wm = ctx.timerService().currentWatermark();
        if (r.eventTimeMs > wm) {
            List<Reading> l = buffer.get(r.eventTimeMs);
            if (l == null) l = new ArrayList<>();
            l.add(r);
            buffer.put(r.eventTimeMs, l);
            ctx.timerService().registerEventTimeTimer(r.eventTimeMs);
            return;
        }
        // behind the watermark: fine if still in order for its tag
        Long last = lastTs.get(r.tagId);
        if (last != null && r.eventTimeMs <= last) {
            if (r.eventTimeMs < last) ctx.output(Tags.DLQ, DlqRecord.of("out_of_order",
                    "older than the last reading of tag " + r.tagId, r.sourceTag, r.rawPayload, r.eventTimeMs));
            return;
        }
        handle(List.of(r), master.get(ctx.getBroadcastState(MasterBroadcast.DESCRIPTOR)), ctx, out);
    }

    @Override
    public void onTimer(long ts, OnTimerContext ctx, Collector<FactRow> out) throws Exception {
        List<Reading> l = buffer.get(ts);
        if (l == null) return;
        buffer.remove(ts);
        handle(l, master.get(ctx.getBroadcastState(MasterBroadcast.DESCRIPTOR)), ctx, out);
    }

    private void handle(List<Reading> readings, MasterSnapshot m, ReadOnlyContext ctx, Collector<FactRow> out) throws Exception {
        List<Reading> sorted = new ArrayList<>(readings);
        // same instant: product code first, then counters, then reasons
        sorted.sort(Comparator.comparingInt(x -> "product_code".equals(x.tagRole) ? 0 : "downtime_reason".equals(x.tagRole) ? 2 : 1));
        for (Reading r : sorted) {
            Long last = lastTs.get(r.tagId);
            if (last != null && r.eventTimeMs <= last) continue;          // duplicate
            lastTs.put(r.tagId, r.eventTimeMs);
            switch (r.tagRole == null ? "" : r.tagRole) {
                case "product_code" -> {
                    ProductTracker p = product.value();
                    if (p == null) p = new ProductTracker();
                    p.onCode(r.valueText, r.eventTimeMs);
                    product.update(p);
                    productTagId.update(r.tagId);
                }
                case "total", "good", "reject" -> counter(r, m, ctx, out);
                case "downtime_reason" -> {
                    StallInput s = new StallInput();
                    s.type = StallInput.REASON; s.eventTimeMs = r.eventTimeMs; s.workUnitId = r.workUnitId;
                    s.siteId = r.siteId; s.tagId = r.tagId; s.assetId = r.assetId;
                    s.downtimeReasonId = r.downtimeReasonId; s.valueText = r.valueText;
                    ctx.output(Tags.STALL, s);
                }
                default -> { }   // machine_state, speed, activity_signal, ...: not stored in v1
            }
        }
    }

    private void counter(Reading r, MasterSnapshot m, ReadOnlyContext ctx, Collector<FactRow> out) throws Exception {
        BigDecimal value;
        try { value = new BigDecimal(r.valueText); }
        catch (NumberFormatException e) {
            ctx.output(Tags.DLQ, DlqRecord.of("parse_error", "counter value is not a number", r.sourceTag, r.rawPayload, r.eventTimeMs));
            return;
        }
        String prevText = lastValue.get(r.tagId);
        CounterLogic.Result d = CounterLogic.delta(r.kind, prevText == null ? null : new BigDecimal(prevText), value);
        lastValue.put(r.tagId, value.toPlainString());

        Long productId = currentProduct(r, m);
        FactRow f = new FactRow();
        f.reject = "reject".equals(r.tagRole);
        f.eventTimeMs = r.eventTimeMs; f.tagId = r.tagId; f.assetId = r.assetId; f.assetCode = r.assetCode;
        f.workUnitId = r.workUnitId; f.shiftInstanceId = r.shiftInstanceId; f.businessDate = r.businessDate;
        f.productId = productId; f.productCode = m.productCode(productId); f.role = r.tagRole; f.uomId = r.uomId;
        f.rawValue = value; f.cleanedValue = d.delta; f.isReset = d.reset;
        f.sourceTag = r.sourceTag; f.rawPayload = r.rawPayload;
        f.rejectReasonId = r.rejectReasonId; f.rejectReasonCode = m.rejectReasonCode(r.rejectReasonId);
        out.collect(f);

        if (d.delta.signum() > 0 && m.isStallWatched(r.workUnitId, r.tagId, r.businessDate)) {
            StallInput s = new StallInput();
            s.type = StallInput.INC; s.eventTimeMs = r.eventTimeMs; s.workUnitId = r.workUnitId; s.siteId = r.siteId;
            s.tagId = r.tagId; s.assetId = r.assetId; s.productId = productId;
            ctx.output(Tags.STALL, s);
        }
    }

    private Long currentProduct(Reading r, MasterSnapshot m) throws Exception {
        ProductTracker p = product.value();
        if (p == null) return null;
        p.settle(r.eventTimeMs);
        product.update(p);
        Long codeTag = productTagId.value();
        if (p.confirmed == null || codeTag == null) return null;
        MasterSnapshot.Tag t = m.tag(codeTag);
        return t == null ? null : m.productForCode(t.assetId, p.confirmed, r.eventTimeMs);
    }
}
