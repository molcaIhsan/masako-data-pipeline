package id.molca.pipeline.silver.model;

/** A reading resolved against master data, for one work unit (machine-wide reason tags are fanned out per lane). */
public class Reading {
    public long eventTimeMs;
    public String sourceTag;
    public String valueText;
    public String rawPayload;
    public long tagId;
    public long assetId;
    public String assetCode;
    public long workUnitId;
    public long siteId;
    public long shiftInstanceId;
    public int businessDate;      // epoch day of shift_instance.business_date
    public String tagRole;        // total | good | reject | product_code | downtime_reason | ...
    public String kind;           // cumulative_counter | delta_counter | boolean | value
    public Long uomId;
    public String countBasis;
    public Long rejectReasonId;
    public Long downtimeReasonId;

    public Reading() {}

    public String laneKey() { return assetId + "|" + workUnitId; }
}
