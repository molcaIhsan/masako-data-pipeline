package id.molca.pipeline.silver.model;

/** A message the job could not use, with the reason. Goes to the DLQ topic; never dropped silently. */
public class DlqRecord {
    public String reason;      // parse_error | unknown_source_tag | not_placed | ambiguous_lane | no_shift_instance | out_of_order
    public String detail;
    public String sourceTag;
    public String rawPayload;
    public Long eventTimeMs;
    public long receivedAtMs;

    public DlqRecord() {}

    public static DlqRecord of(String reason, String detail, String sourceTag, String rawPayload, Long eventTimeMs) {
        DlqRecord r = new DlqRecord();
        r.reason = reason; r.detail = detail; r.sourceTag = sourceTag; r.rawPayload = rawPayload;
        r.eventTimeMs = eventTimeMs; r.receivedAtMs = System.currentTimeMillis();
        return r;
    }
}
