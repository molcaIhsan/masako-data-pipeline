"""Load gold shift tables from the silver views: one transaction per shift, advisory lock, approved work units frozen."""
from gold.db import transaction, query

# (gold table, silver view, has work_unit_id column for the approval freeze)
SHIFT_TABLES = [
    ("work_unit_shift_report", "v_wu_shift_report", True),
    ("product_count", "v_product_count", True),
    ("reject_count", "v_reject_count", True),
    ("downtime_report", "v_downtime_report", True),
    ("loss_by_reason", "v_loss_by_reason", True),
    ("product_perf_hourly", "v_product_perf_hourly", True),
    ("work_center_shift_report", "v_wc_shift_report", False),   # always recomputed (no approval of its own)
]
LOCK_NS = 7101   # advisory lock namespace for gold shift loads (A, B, C, E never load the same shift concurrently)


def load_shift(shift_id, run_id="manual"):
    """Rebuild every gold shift table for one shift. Returns rows written per table. Idempotent."""
    written = {}
    with transaction() as cur:
        cur.execute("SELECT pg_advisory_xact_lock(%s, %s)", (LOCK_NS, shift_id))
        cur.execute("""SELECT array_agg(work_unit_id) FROM gold.report_approval
                       WHERE shift_instance_id = %s AND status = 'approved'""", (shift_id,))
        frozen = cur.fetchone()[0] or []
        for table, view, freezable in SHIFT_TABLES:
            keep = "AND NOT (work_unit_id = ANY(%(frozen)s))" if freezable else ""
            cur.execute(f"DELETE FROM gold.{table} WHERE shift_instance_id = %(s)s {keep}", {"s": shift_id, "frozen": frozen})
            cur.execute(f"INSERT INTO gold.{table} SELECT * FROM silver.{view} WHERE shift_instance_id = %(s)s {keep}",
                        {"s": shift_id, "frozen": frozen})
            written[table] = cur.rowcount
        cur.execute("SELECT business_date FROM silver.shift_instance WHERE shift_instance_id = %s", (shift_id,))
        bdate = cur.fetchone()[0]
        for table, _, _ in SHIFT_TABLES:
            stamp(cur, table, bdate, run_id)
    return {"shift_instance_id": shift_id, "business_date": str(bdate), "frozen": frozen, "rows": written}


def stamp(cur, table, business_date, run_id, rows=None):
    cur.execute("""
        INSERT INTO gold.etl_watermarks (table_name, last_run_at, last_business_date, rows_written, run_id, writer)
        VALUES (%s, now(), %s, %s, %s, 'airflow')
        ON CONFLICT (table_name) DO UPDATE SET
          last_run_at = EXCLUDED.last_run_at,
          last_business_date = greatest(gold.etl_watermarks.last_business_date, EXCLUDED.last_business_date),
          rows_written = EXCLUDED.rows_written, run_id = EXCLUDED.run_id""", (table, business_date, rows, run_id))


def set_edit_watermarks(marks, run_id):
    """Advance the per-edit-table watermarks of DAG C (only after the reload succeeded)."""
    if not marks:
        return
    with transaction() as cur:
        for name, hi in marks.items():
            cur.execute("""
                INSERT INTO gold.etl_watermarks (table_name, last_run_at, run_id, writer) VALUES (%s, %s, %s, 'airflow')
                ON CONFLICT (table_name) DO UPDATE SET last_run_at = greatest(gold.etl_watermarks.last_run_at, EXCLUDED.last_run_at),
                                                       run_id = EXCLUDED.run_id""", (name, hi, run_id))


def business_dates_of(shift_ids):
    if not shift_ids:
        return []
    rows = query("SELECT DISTINCT business_date FROM silver.shift_instance WHERE shift_instance_id = ANY(%(s)s) ORDER BY 1",
                 {"s": list(shift_ids)})
    return [str(r[0]) for r in rows]
