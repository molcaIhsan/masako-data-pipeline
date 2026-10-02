"""
C. gold_apply_edits — every 5 min: reload the shifts touched by edits / entries / voids (silver and gold) or by a
report reopen since the last run, then roll up; advance the per-edit-table watermarks only after success.
pipeline_contract.md §2.1 C, editing_model.md.
"""
from datetime import datetime, timedelta

from airflow.sdk import dag, task


@dag(dag_id="gold_apply_edits", schedule="*/5 * * * *", start_date=datetime(2026, 10, 1), catchup=False,
     max_active_runs=1, default_args={"retries": 2, "retry_delay": timedelta(minutes=1)}, tags=["gold", "edits"])
def gold_apply_edits():
    @task
    def find_touched():
        from gold.shifts import touched_by_edits
        shifts, marks = touched_by_edits()
        return {"shifts": shifts, "marks": {k: v.isoformat() for k, v in marks.items()}}

    @task
    def shifts_of(found):
        return found["shifts"]

    @task(max_active_tis_per_dagrun=4)
    def load(shift_id, run_id=None):
        from gold.loader import load_shift
        return load_shift(shift_id, run_id)

    @task
    def rollup_and_advance(results, found, run_id=None):
        from gold.loader import set_edit_watermarks
        from gold.rollups import run_rollups
        dates = sorted({r["business_date"] for r in results})
        out = run_rollups(dates, run_id) if dates else {}
        set_edit_watermarks(found["marks"], run_id)
        return out

    found = find_touched()
    rollup_and_advance(load.expand(shift_id=shifts_of(found)), found)


gold_apply_edits()
