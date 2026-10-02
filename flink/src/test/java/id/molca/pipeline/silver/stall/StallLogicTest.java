package id.molca.pipeline.silver.stall;

import id.molca.pipeline.silver.master.MasterSnapshot;
import id.molca.pipeline.silver.model.StopRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/** Drives StallLogic like the operator does: inputs in event-time order, timers fired in order up to "now". */
class StallLogicTest {
    static final long WU = 7, TAG = 101, ASSET = 1, SITE = 1;
    static final long S500_START = 0, S500_END = 8 * 3_600_000L, S501_END = 16 * 3_600_000L;
    static final long M = 60_000L;

    StallState s;
    final TreeSet<Long> timers = new TreeSet<>();
    final List<StopRow> rows = new ArrayList<>();
    StallContext ctx;

    static MasterSnapshot.Shift shift(long id, long start, long end) {
        MasterSnapshot.Shift x = new MasterSnapshot.Shift();
        x.shiftInstanceId = id; x.siteId = SITE; x.startMs = start; x.endMs = end; x.businessDate = 20361; return x;
    }

    @BeforeEach void init() {
        s = new StallState(); timers.clear(); rows.clear();
        List<MasterSnapshot.Shift> shifts = List.of(shift(500, S500_START, S500_END), shift(501, S500_END, S501_END));
        Map<Long, String> cat = Map.of(1L, "unplanned", 2L, "unplanned", 3L, "planned");
        ctx = new StallContext() {
            public MasterSnapshot.Shift shiftAt(long site, long t) {
                return shifts.stream().filter(x -> x.startMs <= t && t < x.endMs).findFirst().orElse(null); }
            public MasterSnapshot.Shift nextShiftFrom(long site, long t) {
                return shifts.stream().filter(x -> x.startMs >= t).findFirst().orElse(null); }
            public String reasonCategory(Long r) { return cat.get(r); }
        };
    }

    void collect(StallLogic.Out o) { rows.addAll(o.rows); timers.addAll(o.timers); }
    void advanceTo(long now) {
        while (!timers.isEmpty() && timers.first() <= now) collect(StallLogic.onTimer(s, ctx, WU, timers.pollFirst()));
    }
    void inc(long t) { advanceTo(t); collect(StallLogic.onIncrement(s, ctx, WU, TAG, ASSET, SITE, 11L, t)); }
    void reason(long tag, long reasonId, String v, long t) { advanceTo(t); collect(StallLogic.onReason(s, ctx, tag, reasonId, v, t)); }
    StopRow last(String id) {
        StopRow r = null; for (StopRow x : rows) if (x.sourceEventId.equals(id)) r = x; return r;
    }

    @Test void gapUnder60sIsNothing() {
        inc(10 * M); inc(10 * M + 40_000);
        assertTrue(rows.isEmpty());
    }

    @Test void gap90sIsSmallStop() {
        inc(10 * M); inc(10 * M + 90_000);
        StopRow r = last("wu7-t101-" + 10 * M);
        assertEquals("small_stop", r.category);
        assertEquals(10 * M, r.startMs); assertEquals(10 * M + 90_000, r.endMs);
    }

    @Test void openRowAt60sThenPromotedWithReasons() {
        inc(400 * M);                                        // last increment 06:40
        reason(901, 2, "1", 400 * M + 60_000);               // Material jam turns on 06:41
        advanceTo(400 * M + 61_000);
        StopRow open = last("wu7-t101-" + 400 * M);
        assertEquals("small_stop", open.category); assertNull(open.endMs);   // live open stop
        reason(902, 1, "1", 405 * M);                        // Film out 06:45
        advanceTo(406 * M);
        assertEquals("unplanned", last("wu7-t101-" + 400 * M).category);  // promoted at +5 min
        inc(422 * M);                                        // resumes 07:02
        StopRow r = last("wu7-t101-" + 400 * M);
        assertEquals("unplanned", r.category);
        assertEquals(List.of(2L, 1L), r.reasonIds);          // ordered by when they turned on
        assertEquals(22 * M, r.endMs - r.startMs);
        assertEquals(11L, r.productId);
    }

    @Test void plannedReasonMakesItPlanned() {
        inc(300 * M); reason(903, 3, "1", 301 * M); inc(330 * M);
        assertEquals("planned", last("wu7-t101-" + 300 * M).category);
    }

    @Test void reasonAlreadyOnIsNotTurnedOnAgain() {
        reason(901, 2, "1", 0);                              // was already 1 before the stall
        inc(100 * M); reason(901, 2, "1", 102 * M); inc(110 * M);
        assertTrue(last("wu7-t101-" + 100 * M).reasonIds.isEmpty());
    }

    @Test void stopIsSplitAtShiftEnd() {
        inc(470 * M);                                        // 07:50, then stopped until 08:20 (next shift)
        inc(500 * M);
        StopRow a = last("wu7-t101-" + 470 * M), b = last("wu7-t101-" + S500_END);
        assertEquals(500, a.shiftInstanceId); assertEquals(S500_END, a.endMs);
        assertEquals(501, b.shiftInstanceId); assertEquals(S500_END, b.startMs); assertEquals(500 * M, b.endMs);
        assertEquals(a.stopGroupId, b.stopGroupId);
        assertEquals("unplanned", a.category); assertEquals("unplanned", b.category);
    }

    @Test void lateIncrementInside60sDeletesFalseStop() {
        inc(10 * M);
        advanceTo(10 * M + 70_000);                          // watermark moved on, stop opened
        assertNotNull(last("wu7-t101-" + 10 * M));
        collect(StallLogic.onIncrement(s, ctx, WU, TAG, ASSET, SITE, 11L, 10 * M + 50_000));  // late, in order
        assertTrue(last("wu7-t101-" + 10 * M).delete);
    }

    @Test void replayGivesSameIds() {
        inc(10 * M); inc(20 * M);
        String id = last("wu7-t101-" + 10 * M).sourceEventId; long g = last(id).stopGroupId;
        init(); inc(10 * M); inc(20 * M);
        assertEquals(id, last(id).sourceEventId); assertEquals(g, last(id).stopGroupId);
    }

    @Test void duplicateOrOlderIncrementIgnored() {
        inc(10 * M); inc(20 * M); int n = rows.size();
        collect(StallLogic.onIncrement(s, ctx, WU, TAG, ASSET, SITE, 11L, 15 * M));
        assertEquals(n, rows.size());
    }

    @Test void stallJustBeforeShiftEndIsSplitAndStaysSmallUntil300s() {
        inc(479 * M + 50_000);                               // last increment 07:59:50
        advanceTo(484 * M);                                  // watermark 08:04:00 (< +300 s)
        StopRow a = last("wu7-t101-" + (479 * M + 50_000)), b = last("wu7-t101-" + S500_END);
        assertEquals("small_stop", a.category); assertEquals(S500_END, a.endMs);
        assertEquals("small_stop", b.category); assertNull(b.endMs);
        advanceTo(485 * M);                                  // passes +300 s (08:04:50)
        assertEquals("unplanned", last(a.sourceEventId).category);
        assertEquals("unplanned", last(b.sourceEventId).category);
    }
}
