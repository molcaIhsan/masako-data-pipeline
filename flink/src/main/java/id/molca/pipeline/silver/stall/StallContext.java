package id.molca.pipeline.silver.stall;

import id.molca.pipeline.silver.master.MasterSnapshot;

/** What the stall logic needs from master data (a fake in tests, the CDC snapshot in the job). */
public interface StallContext {
    MasterSnapshot.Shift shiftAt(long siteId, long tsMs);
    MasterSnapshot.Shift nextShiftFrom(long siteId, long tsMs);
    String reasonCategory(Long reasonId);

    static StallContext of(MasterSnapshot m) {
        return new StallContext() {
            public MasterSnapshot.Shift shiftAt(long s, long t) { return m.shiftAt(s, t); }
            public MasterSnapshot.Shift nextShiftFrom(long s, long t) { return m.nextShiftFrom(s, t); }
            public String reasonCategory(Long r) { return m.reasonCategory(r); }
        };
    }
}
