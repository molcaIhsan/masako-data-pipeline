package id.molca.pipeline.silver.stall;

import id.molca.pipeline.silver.master.MasterSnapshot;
import id.molca.pipeline.silver.model.StopRow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Stall detection for ONE work unit (pipeline_contract F7-F13). Pure logic, driven in event-time order:
 *  - increment of the watched counter  -> ends a stall; schedules checks at +60 s and +300 s
 *  - check at lastInc + 60 s            -> opens a stop (small_stop), start = lastInc
 *  - check at lastInc + 300 s           -> promotes it: planned if the primary reason is planned, else unplanned
 *  - check at the open piece's shift end-> closes the piece at the boundary, opens the next piece (same stop_group_id)
 *  - next increment                     -> closes; total gap < 60 s = delete (false stop), <= 300 s = small_stop
 *  - reason tag turned on / incremented inside the stall window -> appended to the ordered reason list
 * Every change is returned as StopRow upserts (or deletes); the caller registers the returned timer instants.
 */
public final class StallLogic {
    public static final long MIN_STOP_MS = 60_000L;          // < 60 s: no stop (speed loss)
    public static final long SMALL_STOP_MAX_MS = 300_000L;   // 60..300 s: small stop; > 300 s: downtime

    public static final class Out {
        public final List<StopRow> rows = new ArrayList<>();
        public final List<Long> timers = new ArrayList<>();
    }

    private StallLogic() {}

    public static Out onIncrement(StallState s, StallContext ctx, long workUnitId, long tagId, long assetId, long siteId,
                                  Long productId, long ts) {
        Out out = new Out();
        if (s.lastIncMs != null && ts <= s.lastIncMs) return out;          // duplicate / out of order
        if (s.stopActive()) closeStop(s, ctx, ts, out);
        s.lastIncMs = ts; s.watchedTagId = tagId; s.assetId = assetId; s.siteId = siteId; s.lastProductId = productId;
        s.pendingReasons = new ArrayList<>();
        out.timers.add(ts + MIN_STOP_MS);
        out.timers.add(ts + SMALL_STOP_MAX_MS);
        return out;
    }

    public static Out onReason(StallState s, StallContext ctx, long reasonTagId, Long reasonId, String value, long ts) {
        Out out = new Out();
        String prev = s.reasonValues.put(reasonTagId, value);
        if (reasonId == null || !turnedOn(prev, value)) return out;
        if (s.lastIncMs == null || ts < s.lastIncMs) return out;          // not inside a stall window
        if (s.stopActive()) {
            if (!s.reasons.contains(reasonId)) {
                s.reasons.add(reasonId);
                if (s.promoted) recategorize(s, ctx);
                for (StopRow p : s.pieces) { p.reasonIds = new ArrayList<>(s.reasons); out.rows.add(copy(p)); }
            }
        } else if (!s.pendingReasons.contains(reasonId)) {
            s.pendingReasons.add(reasonId);
        }
        return out;
    }

    public static Out onTimer(StallState s, StallContext ctx, long workUnitId, long t) {
        Out out = new Out();
        if (s.lastIncMs == null) return out;
        // 1. open at +60 s
        if (!s.stopActive() && t == s.lastIncMs + MIN_STOP_MS) {
            openStop(s, ctx, workUnitId, t, out);
        }
        if (!s.stopActive()) return out;
        // 2. shift boundary splits (possibly several if the job was behind)
        while (s.openPieceShiftEndMs != null && s.openPieceShiftEndMs <= t) splitAtBoundary(s, ctx, workUnitId, out);
        // 3. promote at +300 s
        if (!s.promoted && t == s.lastIncMs + SMALL_STOP_MAX_MS) {
            s.promoted = true;
            recategorize(s, ctx);
            for (StopRow p : s.pieces) out.rows.add(copy(p));
        }
        if (s.openPieceShiftEndMs != null) out.timers.add(s.openPieceShiftEndMs);
        return out;
    }

    // ------------------------------------------------------------------

    private static void openStop(StallState s, StallContext ctx, long workUnitId, long now, Out out) {
        long start = s.lastIncMs;
        MasterSnapshot.Shift shift = ctx.shiftAt(s.siteId, start);
        long pieceStart = start;
        if (shift == null) {                                   // stall began outside any shift
            shift = ctx.nextShiftFrom(s.siteId, start);
            if (shift == null || shift.startMs > now) return;   // no shift yet: nothing to record
            pieceStart = shift.startMs;
        }
        s.stallStartMs = start;
        s.stopGroupId = stopGroupId(workUnitId, start);
        s.promoted = false;
        s.reasons = new ArrayList<>(s.pendingReasons);
        s.pieces = new ArrayList<>();
        s.pieces.add(newPiece(s, workUnitId, shift, pieceStart));
        s.openPieceShiftEndMs = shift.endMs;
        out.rows.add(copy(s.openPiece()));
    }

    private static void splitAtBoundary(StallState s, StallContext ctx, long workUnitId, Out out) {
        StopRow open = s.openPiece();
        long boundary = s.openPieceShiftEndMs;
        if (open != null) { open.endMs = boundary; out.rows.add(copy(open)); }
        MasterSnapshot.Shift next = ctx.shiftAt(s.siteId, boundary);
        if (next == null) next = ctx.nextShiftFrom(s.siteId, boundary);
        if (next == null) { s.openPieceShiftEndMs = null; return; }   // no next shift known yet
        StopRow p = newPiece(s, workUnitId, next, next.startMs);
        s.pieces.add(p);
        s.openPieceShiftEndMs = next.endMs;
        out.rows.add(copy(p));
    }

    private static void closeStop(StallState s, StallContext ctx, long ts, Out out) {
        long gap = ts - s.stallStartMs;
        StopRow open = s.openPiece();
        if (open != null) open.endMs = ts;
        if (gap < MIN_STOP_MS) {                                    // a false stop (late data): remove it
            for (StopRow p : s.pieces) { StopRow d = copy(p); d.delete = true; out.rows.add(d); }
        } else {
            if (gap <= SMALL_STOP_MAX_MS) { s.promoted = false; for (StopRow p : s.pieces) p.category = "small_stop"; }
            for (StopRow p : s.pieces) out.rows.add(copy(p));
        }
        s.clearStop();
    }

    private static void recategorize(StallState s, StallContext ctx) {
        String cat = "unplanned";
        if (!s.reasons.isEmpty() && "planned".equals(ctx.reasonCategory(s.reasons.get(0)))) cat = "planned";
        for (StopRow p : s.pieces) p.category = cat;
    }

    private static StopRow newPiece(StallState s, long workUnitId, MasterSnapshot.Shift shift, long start) {
        StopRow p = new StopRow();
        p.sourceEventId = "wu" + workUnitId + "-t" + s.watchedTagId + "-" + start;
        p.assetId = s.assetId; p.tagId = s.watchedTagId; p.workUnitId = workUnitId;
        p.shiftInstanceId = shift.shiftInstanceId; p.businessDate = shift.businessDate;
        p.category = s.promoted ? (s.pieces.isEmpty() ? "unplanned" : s.pieces.get(0).category) : "small_stop";
        p.reasonIds = new ArrayList<>(s.reasons);
        p.productId = s.lastProductId;
        p.startMs = start; p.endMs = null; p.stopGroupId = s.stopGroupId;
        return p;
    }

    /** Deterministic: the same stall replayed gives the same group id. */
    static long stopGroupId(long workUnitId, long stallStartMs) {
        return (stallStartMs / 1000L) * 100_000L + Math.floorMod(workUnitId, 100_000L);
    }

    static boolean turnedOn(String prev, String value) {
        BigDecimal v = num(value);
        if (v == null || v.signum() <= 0) return false;
        BigDecimal p = num(prev);
        return p == null || v.compareTo(p) > 0;      // 0 -> 1, or a counter that incremented
    }

    private static BigDecimal num(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.equalsIgnoreCase("true")) return BigDecimal.ONE;
        if (t.equalsIgnoreCase("false")) return BigDecimal.ZERO;
        try { return new BigDecimal(t); } catch (NumberFormatException e) { return null; }
    }

    static StopRow copy(StopRow p) {
        StopRow c = new StopRow();
        c.sourceEventId = p.sourceEventId; c.delete = p.delete; c.assetId = p.assetId; c.tagId = p.tagId;
        c.workUnitId = p.workUnitId; c.shiftInstanceId = p.shiftInstanceId; c.businessDate = p.businessDate;
        c.category = p.category; c.reasonIds = new ArrayList<>(p.reasonIds); c.productId = p.productId;
        c.startMs = p.startMs; c.endMs = p.endMs; c.stopGroupId = p.stopGroupId; c.assumedStart = p.assumedStart;
        return c;
    }
}
