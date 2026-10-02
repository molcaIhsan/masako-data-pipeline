"""
B. gold_rerun_window — hourly: reload every shift that ended in the last 72 h (48 h shopfloor edits, late data,
DLQ replays), then roll up. pipeline_contract.md §2.1 B.
"""
from datetime import datetime, timedelta

from airflow.sdk import dag, task


@dag(dag_id="gold_rerun_window", schedule="17 * * * *", start_date=datetime(2026, 10, 1), catchup=False,
     max_active_runs=1, default_args={"retries": 2, "retry_delay": timedelta(minutes=2)}, tags=["gold"])
def gold_rerun_window():
    @task
    def find_shifts(data_interval_end=None):
        from gold.shifts import ended_within
        return ended_within(data_interval_end.isoformat(), hours=72)

    @task(max_active_tis_per_dagrun=4)
    def load(shift_id, run_id=None):
        from gold.loader import load_shift
        return load_shift(shift_id, run_id)

    @task
    def rollup(results, run_id=None):
        from gold.rollups import run_rollups
        dates = sorted({r["business_date"] for r in results})
        return run_rollups(dates, run_id) if dates else {}

    rollup(load.expand(shift_id=find_shifts()))


gold_rerun_window()
