package id.molca.pipeline.silver.stall;

import id.molca.pipeline.silver.model.StopRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Keyed state of one work unit's stall detector (POJO for Flink ValueState). */
public class StallState {
    public Long lastIncMs;                 // last real increment of the watched counter
    public Long watchedTagId;
    public long assetId;
    public long siteId;
    public Long lastProductId;             // product running at the last increment
    public List<Long> pendingReasons = new ArrayList<>();      // reasons seen after lastInc, before a stop opened
    public Map<Long, String> reasonValues = new HashMap<>();   // last value per reason tag (turned-on detection)

    // the stop in progress (null pieces = none)
    public Long stallStartMs;              // = lastIncMs when the stall began
    public long stopGroupId;
    public boolean promoted;               // passed small_stop_max (downtime)
    public List<Long> reasons = new ArrayList<>();
    public List<StopRow> pieces = new ArrayList<>();          // one per shift; the last may be open (endMs null)
    public Long openPieceShiftEndMs;       // when the open piece must be split

    public StallState() {}

    public boolean stopActive() { return stallStartMs != null; }

    public StopRow openPiece() {
        if (pieces.isEmpty()) return null;
        StopRow last = pieces.get(pieces.size() - 1);
        return last.endMs == null ? last : null;
    }

    public void clearStop() {
        stallStartMs = null; promoted = false; reasons = new ArrayList<>(); pieces = new ArrayList<>();
        openPieceShiftEndMs = null; stopGroupId = 0;
    }
}
