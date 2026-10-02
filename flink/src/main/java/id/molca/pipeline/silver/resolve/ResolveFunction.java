package id.molca.pipeline.silver.resolve;

import id.molca.pipeline.silver.Tags;
import id.molca.pipeline.silver.master.MasterBroadcast;
import id.molca.pipeline.silver.master.MasterSnapshot;
import id.molca.pipeline.silver.model.DlqRecord;
import id.molca.pipeline.silver.model.MasterChange;
import id.molca.pipeline.silver.model.RawReading;
import id.molca.pipeline.silver.model.Reading;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * F3: source_tag -> tag, asset, work unit(s), shift (all as-of). Keyed by source_tag.
 * Machine-wide roles (downtime_reason, machine_state, runtime) fan out to every lane the asset is placed on.
 * A reading that cannot be resolved yet (e.g. master data still loading, tag declared a moment later) is
 * retried every RETRY_EVERY_MS until retryWindowMs has passed, then sent to the DLQ with the reason.
 */
public class ResolveFunction extends KeyedBroadcastProcessFunction<String, RawReading, MasterChange, Reading> {
    private static final long RETRY_EVERY_MS = 15_000L;
    private static final List<String> MACHINE_WIDE = List.of("downtime_reason", "machine_state", "runtime");

    private final long retryWindowMs;
    private final MasterBroadcast master = new MasterBroadcast();
    private transient ListState<RawReading> pending;

    public ResolveFunction(long retryWindowMs) { this.retryWindowMs = retryWindowMs; }

    @Override
    public void open(OpenContext ctx) {
        pending = getRuntimeContext().getListState(new ListStateDescriptor<>("unresolved", RawReading.class));
    }

    @Override
    public void processBroadcastElement(MasterChange c, Context ctx, Collector<Reading> out) throws Exception {
        master.apply(c, ctx.getBroadcastState(MasterBroadcast.DESCRIPTOR));
    }

    @Override
    public void processElement(RawReading r, ReadOnlyContext ctx, Collector<Reading> out) throws Exception {
        MasterSnapshot m = master.get(ctx.getBroadcastState(MasterBroadcast.DESCRIPTOR));
        String problem = resolve(m, r, out, ctx);
        if (problem != null) {
            pending.add(r);
            ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime() + RETRY_EVERY_MS);
        }
    }

    @Override
    public void onTimer(long ts, OnTimerContext ctx, Collector<Reading> out) throws Exception {
        MasterSnapshot m = master.get(ctx.getBroadcastState(MasterBroadcast.DESCRIPTOR));
        List<RawReading> keep = new ArrayList<>();
        for (RawReading r : pending.get()) {
            String problem = resolve(m, r, out, ctx);
            if (problem == null) continue;
            if (ts - r.firstSeenMs >= retryWindowMs) {
                ctx.output(Tags.DLQ, DlqRecord.of(problem, "unresolved after " + retryWindowMs / 1000 + " s",
                        r.sourceTag, r.rawPayload, r.eventTimeMs));
            } else keep.add(r);
        }
        pending.update(keep);
        if (!keep.isEmpty()) ctx.timerService().registerProcessingTimeTimer(ts + RETRY_EVERY_MS);
    }

    /** Emits the resolved readings and returns null, or returns the reason it cannot be resolved (nothing emitted). */
    static String resolve(MasterSnapshot m, RawReading r, Collector<Reading> out, ReadOnlyContext ctx) {
        if (m.isEmpty()) return "master_data_not_loaded";
        MasterSnapshot.Tag tag = m.tagForNode(r.sourceTag, r.eventTimeMs);
        if (tag == null) return "unknown_source_tag";

        List<Long> workUnits = new ArrayList<>();
        if (tag.workUnitId != null) workUnits.add(tag.workUnitId);
        else for (MasterSnapshot.Placement p : m.placementsAround(tag.assetId, r.eventTimeMs)) {
            if (!workUnits.contains(p.workUnitId)) workUnits.add(p.workUnitId);
        }
        if (workUnits.isEmpty()) return "not_placed";
        if (workUnits.size() > 1 && !MACHINE_WIDE.contains(tag.role)) return "ambiguous_lane";

        List<Reading> resolved = new ArrayList<>();
        for (long wu : workUnits) {
            Long site = m.siteOfWorkUnit(wu);
            if (site == null) return "work_unit_without_site";
            MasterSnapshot.Shift shift = m.shiftAt(site, r.eventTimeMs);
            if (shift == null) return "no_shift_instance";
            if (!m.tagValidOn(tag, shift.businessDate)) return "tag_not_valid";
            if (tag.workUnitId == null && !m.placedOn(tag.assetId, wu, shift.businessDate)) continue;
            Reading x = new Reading();
            x.eventTimeMs = r.eventTimeMs; x.sourceTag = r.sourceTag; x.valueText = r.valueText; x.rawPayload = r.rawPayload;
            x.tagId = tag.tagId; x.assetId = tag.assetId; x.assetCode = m.assetCode(tag.assetId);
            x.workUnitId = wu; x.siteId = site; x.shiftInstanceId = shift.shiftInstanceId; x.businessDate = shift.businessDate;
            x.tagRole = tag.role; x.kind = tag.kind; x.uomId = tag.uomId; x.countBasis = tag.countBasis;
            x.rejectReasonId = tag.rejectReasonId; x.downtimeReasonId = tag.downtimeReasonId;
            resolved.add(x);
        }
        if (resolved.isEmpty()) return "not_placed";
        for (Reading x : resolved) out.collect(x);
        return null;
    }
}
