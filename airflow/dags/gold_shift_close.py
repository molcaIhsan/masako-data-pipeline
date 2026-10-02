"""
A. gold_shift_close — every 15 min: load every shift that ended >= 5 min ago and is not in gold yet, then roll up.
pipeline_contract.md §2.1 A. Approved work units are skipped by the loader (frozen).
"""
from datetime import datetime, timedelta

from airflow.sdk import dag, task

DEFAULT_ARGS = {"retries": 2, "retry_delay": timedelta(minutes=2)}


@dag(dag_id="gold_shift_close", schedule="*/15 * * * *", start_date=datetime(2026, 10, 1), catchup=False,
     max_active_runs=1, default_args=DEFAULT_ARGS, tags=["gold"])
def gold_shift_close():
    @task
    def find_shifts(data_interval_end=None):
        from gold.shifts import closed_not_loaded
        return closed_not_loaded(data_interval_end.isoformat())

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


gold_shift_close()
