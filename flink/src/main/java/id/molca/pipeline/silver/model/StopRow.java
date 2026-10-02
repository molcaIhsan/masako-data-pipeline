package id.molca.pipeline.silver.model;

import java.util.ArrayList;
import java.util.List;

/** Upsert (or delete) of one piece of a stop in silver.downtime_events, keyed by sourceEventId. */
public class StopRow {
    public String sourceEventId;
    public boolean delete;          // a false stop (gap turned out < 60 s): remove the row
    public long assetId;
    public long tagId;
    public long workUnitId;
    public long shiftInstanceId;
    public int businessDate;
    public String category;         // small_stop | unplanned | planned
    public List<Long> reasonIds = new ArrayList<>();
    public Long productId;
    public long startMs;
    public Long endMs;              // null = still stopped
    public long stopGroupId;
    public boolean assumedStart;

    public StopRow() {}
}
