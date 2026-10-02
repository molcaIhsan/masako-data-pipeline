package id.molca.pipeline.silver.model;

/** Input of the stall detector (keyed by work unit): an increment of the watched counter, or a reason-tag value. */
public class StallInput {
    public static final String INC = "INC";
    public static final String REASON = "REASON";

    public String type;
    public long eventTimeMs;
    public long workUnitId;
    public long siteId;
    public long tagId;
    public long assetId;
    public Long productId;        // INC: product running at that moment
    public Long downtimeReasonId; // REASON: the reason this tag stands for
    public String valueText;      // REASON: raw value (0/1 or counter)

    public StallInput() {}
}
