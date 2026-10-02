package id.molca.pipeline.silver.sink;

import id.molca.pipeline.silver.model.FactRow;
import id.molca.pipeline.silver.model.StopRow;

import java.sql.Array;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

/** SQL and binders for the three silver fact tables. Natural keys make every write idempotent. */
public final class SilverStatements {
    private SilverStatements() {}

    private static final String FACT_COLS = "event_time, tag_id, asset_id, asset_code, work_unit_id, shift_instance_id, "
            + "business_date, product_id, product_code, %s uom_id, raw_value, cleaned_value, is_reset, source_tag, raw_payload";
    private static final String FACT_UPDATE = "asset_id = EXCLUDED.asset_id, asset_code = EXCLUDED.asset_code, "
            + "work_unit_id = EXCLUDED.work_unit_id, shift_instance_id = EXCLUDED.shift_instance_id, "
            + "business_date = EXCLUDED.business_date, product_id = EXCLUDED.product_id, product_code = EXCLUDED.product_code, "
            + "uom_id = EXCLUDED.uom_id, raw_value = EXCLUDED.raw_value, cleaned_value = EXCLUDED.cleaned_value, "
            + "is_reset = EXCLUDED.is_reset, source_tag = EXCLUDED.source_tag, raw_payload = EXCLUDED.raw_payload";

    public static final String PRODUCTION = "INSERT INTO silver.production_events (" + String.format(FACT_COLS, "role,")
            + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT (tag_id, event_time) DO UPDATE SET role = EXCLUDED.role, "
            + FACT_UPDATE;
    public static final String REJECT = "INSERT INTO silver.reject_events (" + String.format(FACT_COLS,
            "reject_reason_id, reject_reason_code,") + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
            + "ON CONFLICT (tag_id, event_time) DO UPDATE SET reject_reason_id = EXCLUDED.reject_reason_id, "
            + "reject_reason_code = EXCLUDED.reject_reason_code, " + FACT_UPDATE;

    /** Facts: statement 0 = production_events, 1 = reject_events. */
    public static final PgUpsertSink.Binder<FactRow> FACT_BINDER = (f, st, conn) -> {
        int idx = f.reject ? 1 : 0;
        PreparedStatement ps = st.get(idx);
        int i = 1;
        ps.setObject(i++, utc(f.eventTimeMs));
        ps.setLong(i++, f.tagId);
        ps.setLong(i++, f.assetId);
        ps.setString(i++, f.assetCode);
        ps.setLong(i++, f.workUnitId);
        ps.setLong(i++, f.shiftInstanceId);
        ps.setDate(i++, Date.valueOf(LocalDate.ofEpochDay(f.businessDate)));
        setLong(ps, i++, f.productId);
        ps.setString(i++, f.productCode);
        if (f.reject) { setLong(ps, i++, f.rejectReasonId); ps.setString(i++, f.rejectReasonCode); }
        else ps.setString(i++, f.role);
        setLong(ps, i++, f.uomId);
        ps.setBigDecimal(i++, f.rawValue);
        ps.setBigDecimal(i++, f.cleanedValue);
        ps.setBoolean(i++, f.isReset);
        ps.setString(i++, f.sourceTag);
        ps.setString(i, f.rawPayload);
        return idx;
    };

    public static final String STOP_UPSERT = "INSERT INTO silver.downtime_events (source_event_id, asset_id, tag_id, "
            + "work_unit_id, shift_instance_id, business_date, category, downtime_reason_id, downtime_reason_ids, product_id, "
            + "source, start_at, end_at, stop_group_id, assumed_start) VALUES (?,?,?,?,?,?,?,?,?,?,'derived',?,?,?,?) "
            + "ON CONFLICT (source_event_id) DO UPDATE SET category = EXCLUDED.category, "
            + "downtime_reason_id = EXCLUDED.downtime_reason_id, downtime_reason_ids = EXCLUDED.downtime_reason_ids, "
            + "product_id = EXCLUDED.product_id, end_at = EXCLUDED.end_at, updated_at = now()";
    public static final String STOP_DELETE = "DELETE FROM silver.downtime_events WHERE source_event_id = ? AND source = 'derived'";

    /** Stops: statement 0 = upsert, 1 = delete (a false stop). */
    public static final PgUpsertSink.Binder<StopRow> STOP_BINDER = (s, st, conn) -> {
        if (s.delete) { st.get(1).setString(1, s.sourceEventId); return 1; }
        PreparedStatement ps = st.get(0);
        int i = 1;
        ps.setString(i++, s.sourceEventId);
        ps.setLong(i++, s.assetId);
        ps.setLong(i++, s.tagId);
        ps.setLong(i++, s.workUnitId);
        ps.setLong(i++, s.shiftInstanceId);
        ps.setDate(i++, Date.valueOf(LocalDate.ofEpochDay(s.businessDate)));
        ps.setString(i++, s.category);
        setLong(ps, i++, s.reasonIds.isEmpty() ? null : s.reasonIds.get(0));
        Array arr = conn.createArrayOf("bigint", s.reasonIds.toArray(new Long[0]));
        ps.setArray(i++, arr);
        setLong(ps, i++, s.productId);
        ps.setObject(i++, utc(s.startMs));
        if (s.endMs == null) ps.setNull(i++, Types.TIMESTAMP_WITH_TIMEZONE); else ps.setObject(i++, utc(s.endMs));
        ps.setLong(i++, s.stopGroupId);
        ps.setBoolean(i, s.assumedStart);
        return 0;
    };

    public static List<String> factSql() { return List.of(PRODUCTION, REJECT); }
    public static List<String> stopSql() { return List.of(STOP_UPSERT, STOP_DELETE); }

    /** Explicit UTC instant: independent of the JVM time zone. */
    private static OffsetDateTime utc(long ms) { return OffsetDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneOffset.UTC); }

    private static void setLong(PreparedStatement ps, int i, Long v) throws java.sql.SQLException {
        if (v == null) ps.setNull(i, Types.BIGINT); else ps.setLong(i, v);
    }
}
