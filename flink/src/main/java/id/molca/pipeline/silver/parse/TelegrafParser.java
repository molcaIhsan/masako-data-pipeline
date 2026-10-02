package id.molca.pipeline.silver.parse;

import com.fasterxml.jackson.databind.JsonNode;
import id.molca.pipeline.silver.model.RawReading;
import id.molca.pipeline.silver.util.Json;

import java.time.Instant;
import java.time.OffsetDateTime;

/**
 * Parses one Telegraf JSON message:
 * {"name":"machine_metrics","tags":{"equipment_code":"..","line_code":"..","source_tag":"PLC/FI2-L1/ANRITSU57-1/ng_count"},
 *  "fields":{"payload":"PLC/FI2-L1/ANRITSU57-1/ng_count=206"},"timestamp":"2026-09-30T03:05:55Z"}
 * The value is the text after the LAST '=' of fields.payload. timestamp may be ISO text or epoch s / ms / ns.
 */
public final class TelegrafParser {
    private TelegrafParser() {}

    public static RawReading parse(String message) {
        JsonNode root = Json.parse(message);
        JsonNode tags = root.path("tags");
        JsonNode fields = root.path("fields");
        String payload = Json.textOrNull(fields, "payload");
        if (payload == null) throw new IllegalArgumentException("fields.payload missing");
        int eq = payload.lastIndexOf('=');
        if (eq < 0) throw new IllegalArgumentException("payload has no '=': " + payload);

        String sourceTag = Json.textOrNull(tags, "source_tag");
        if (sourceTag == null || sourceTag.isBlank()) sourceTag = payload.substring(0, eq);

        RawReading r = new RawReading();
        r.sourceTag = sourceTag.trim();
        r.valueText = payload.substring(eq + 1).trim();
        r.rawPayload = payload;
        r.equipmentCode = Json.textOrNull(tags, "equipment_code");
        r.lineCode = Json.textOrNull(tags, "line_code");
        r.eventTimeMs = parseTimestamp(root.get("timestamp"));
        r.firstSeenMs = System.currentTimeMillis();
        if (r.valueText.isEmpty()) throw new IllegalArgumentException("empty value: " + payload);
        return r;
    }

    /** Event time of a raw message for the Kafka source's per-partition watermarks; fallback if unparsable. */
    public static long eventTimeOrFallback(String message, long fallbackMs) {
        try { return parseTimestamp(Json.parse(message).get("timestamp")); }
        catch (Exception e) { return fallbackMs; }
    }

    static long parseTimestamp(JsonNode ts) {
        if (ts == null || ts.isNull()) throw new IllegalArgumentException("timestamp missing");
        if (ts.isNumber()) {
            long x = ts.asLong();
            if (x > 100_000_000_000_000L) return x / 1_000_000;   // ns
            if (x > 100_000_000_000L) return x;                   // ms
            return x * 1000;                                      // s
        }
        String t = ts.asText().trim();
        try { return OffsetDateTime.parse(t).toInstant().toEpochMilli(); }
        catch (Exception e) { return Instant.parse(t).toEpochMilli(); }
    }
}
