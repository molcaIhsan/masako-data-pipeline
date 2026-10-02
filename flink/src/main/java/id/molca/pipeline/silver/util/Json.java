package id.molca.pipeline.silver.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/** JSON helpers tolerant of Debezium encodings (dates as epoch days, timestamps as ISO strings or micros). */
public final class Json {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    private Json() {}

    public static JsonNode parse(String s) {
        try { return MAPPER.readTree(s); } catch (Exception e) { throw new IllegalArgumentException("bad json: " + e.getMessage(), e); }
    }

    public static String write(Object o) {
        try { return MAPPER.writeValueAsString(o); } catch (Exception e) { throw new IllegalStateException(e); }
    }

    public static boolean isNull(JsonNode n, String f) { JsonNode v = n.get(f); return v == null || v.isNull(); }

    public static Long longOrNull(JsonNode n, String f) {
        JsonNode v = n.get(f);
        if (v == null || v.isNull()) return null;
        if (v.isNumber()) return v.asLong();
        String t = v.asText().trim();
        return t.isEmpty() ? null : Long.parseLong(t);
    }

    public static String textOrNull(JsonNode n, String f) {
        JsonNode v = n.get(f);
        return (v == null || v.isNull()) ? null : v.asText();
    }

    public static Boolean boolOrNull(JsonNode n, String f) {
        JsonNode v = n.get(f);
        if (v == null || v.isNull()) return null;
        if (v.isBoolean()) return v.asBoolean();
        String t = v.asText();
        return "t".equalsIgnoreCase(t) || "true".equalsIgnoreCase(t) || "1".equals(t);
    }

    /** A Postgres `date`: Debezium sends epoch days (int); also accept "yyyy-MM-dd". */
    public static Integer epochDayOrNull(JsonNode n, String f) {
        JsonNode v = n.get(f);
        if (v == null || v.isNull()) return null;
        if (v.isNumber()) return v.asInt();
        String t = v.asText();
        return (int) LocalDate.parse(t.length() > 10 ? t.substring(0, 10) : t).toEpochDay();
    }

    /** A Postgres `timestamptz`: Debezium ZonedTimestamp (ISO string); also accept epoch micros/millis. */
    public static Long instantMsOrNull(JsonNode n, String f) {
        JsonNode v = n.get(f);
        if (v == null || v.isNull()) return null;
        if (v.isNumber()) {
            long x = v.asLong();
            return x > 100_000_000_000_000L ? x / 1000 : x;   // micros -> ms (ms passes through)
        }
        String t = v.asText();
        try { return OffsetDateTime.parse(t).toInstant().toEpochMilli(); }
        catch (Exception e) { return Instant.parse(t).toEpochMilli(); }
    }

    /** Numeric value: Debezium decimal.handling.mode=string gives text; numbers also accepted. */
    public static BigDecimal decimalOrNull(JsonNode n, String f) {
        JsonNode v = n.get(f);
        if (v == null || v.isNull()) return null;
        return v.isNumber() ? v.decimalValue() : new BigDecimal(v.asText().trim());
    }
}
