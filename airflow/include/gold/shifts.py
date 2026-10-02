"""Which shifts must be (re)loaded. All times come from silver.shift_instance (never hard-coded hours)."""
from gold.db import query

# a shift is loadable this long after it ends: 1 min Flink watermark + 1 min rollup refresh + margin
SETTLE = "interval '5 minutes'"


def closed_not_loaded(ref_ts, lookback_days=7):
    """DAG A: shifts that ended (>= SETTLE ago) and have no gold rows yet, or rows computed before the shift settled."""
    rows = query(f"""
        SELECT si.shift_instance_id
        FROM silver.shift_instance si
        WHERE si.end_datetime <= %(ref)s::timestamptz - {SETTLE}
          AND si.end_datetime >  %(ref)s::timestamptz - make_interval(days => %(days)s)
          AND NOT EXISTS (SELECT 1 FROM gold.work_unit_shift_report g
                          WHERE g.shift_instance_id = si.shift_instance_id
                            AND g.computed_at >= si.end_datetime + {SETTLE})
        ORDER BY si.end_datetime""", {"ref": ref_ts, "days": lookback_days})
    return [r[0] for r in rows]


def ended_within(ref_ts, hours=72):
    """DAG B: every shift that ended in the last `hours` (48 h shopfloor edits + late data + DLQ replays)."""
    rows = query(f"""
        SELECT shift_instance_id FROM silver.shift_instance
        WHERE end_datetime <= %(ref)s::timestamptz - {SETTLE}
          AND end_datetime >  %(ref)s::timestamptz - make_interval(hours => %(h)s)
        ORDER BY end_datetime""", {"ref": ref_ts, "h": hours})
    return [r[0] for r in rows]


def in_date_range(date_from, date_to, work_unit_ids=None):
    """DAG E: shifts of a business-date range (closed ones only)."""
    rows = query(f"""
        SELECT DISTINCT si.shift_instance_id
        FROM silver.shift_instance si
        WHERE si.business_date BETWEEN %(f)s AND %(t)s AND si.end_datetime <= now() - {SETTLE}
        ORDER BY 1""", {"f": date_from, "t": date_to})
    return [r[0] for r in rows]


# ---- DAG C: edits since the last run -------------------------------------------------------------------------
EDIT_SOURCES = {
    # watermark name -> SQL returning (changed_at, shift_instance_id) of rows created or voided after %(since)s
    "edits:silver.downtime_edit": "SELECT greatest(created_at, coalesce(voided_at, created_at)), shift_instance_id FROM silver.downtime_edit",
    "edits:gold.downtime_edit": "SELECT greatest(created_at, coalesce(voided_at, created_at)), shift_instance_id FROM gold.downtime_edit",
    "edits:silver.downtime_entry": "SELECT greatest(created_at, coalesce(voided_at, created_at)), shift_instance_id FROM silver.downtime_entry",
    "edits:silver.quality_entry": "SELECT greatest(created_at, coalesce(voided_at, created_at)), shift_instance_id FROM silver.quality_entry",
    "edits:gold.quality_entry": "SELECT greatest(created_at, coalesce(voided_at, created_at)), shift_instance_id FROM gold.quality_entry",
    # a reopened report must be refreshed (approval itself changes nothing to compute)
    "edits:gold.report_approval_event": "SELECT created_at, shift_instance_id FROM gold.report_approval_event WHERE to_status = 'open'",
}


def touched_by_edits():
    """Returns (shift ids to reload, {watermark name: new high-water mark}) for edit rows newer than each watermark."""
    shifts, marks = set(), {}
    for name, src in EDIT_SOURCES.items():
        since = query("SELECT last_run_at FROM gold.etl_watermarks WHERE table_name = %(n)s", {"n": name})
        since = since[0][0] if since else None
        rows = query(f"""SELECT max(x.changed_at), array_agg(DISTINCT x.shift_instance_id)
                         FROM ({src}) AS x(changed_at, shift_instance_id)
                         WHERE %(since)s::timestamptz IS NULL OR x.changed_at > %(since)s::timestamptz""", {"since": since})
        hi, ids = rows[0]
        if hi is not None:
            marks[name] = hi
            shifts.update(i for i in ids if i is not None)
    return sorted(shifts), marks
