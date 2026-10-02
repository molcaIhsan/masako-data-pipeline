package id.molca.pipeline.silver.model;

import java.math.BigDecimal;

/** A row of silver.production_events (role total/good) or silver.reject_events (role reject). */
public class FactRow {
    public boolean reject;
    public long eventTimeMs;
    public long tagId;
    public long assetId;
    public String assetCode;
    public long workUnitId;
    public long shiftInstanceId;
    public int businessDate;
    public Long productId;
    public String productCode;
    public String role;
    public Long uomId;
    public BigDecimal rawValue;
    public BigDecimal cleanedValue;
    public boolean isReset;
    public String sourceTag;
    public String rawPayload;
    public Long rejectReasonId;
    public String rejectReasonCode;

    public FactRow() {}
}
