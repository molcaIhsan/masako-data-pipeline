package id.molca.pipeline.silver.model;

/** One Telegraf message as read from Kafka (not yet resolved against master data). */
public class RawReading {
    public String sourceTag;     // tags.source_tag  (= asset_tags.node)
    public String valueText;     // text after the last '=' in fields.payload
    public long eventTimeMs;     // timestamp (UTC)
    public String rawPayload;    // fields.payload as received
    public String equipmentCode; // diagnostics only
    public String lineCode;      // diagnostics only
    public long firstSeenMs;     // processing time the job first saw it (unresolved retry deadline)

    public RawReading() {}
}
