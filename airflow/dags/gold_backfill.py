"""
E. gold_backfill — manual: reload a business-date range after a back-dated master-data change (cycle time,
product-code mapping, binding, flow, conversion), then roll up. Approved shifts stay frozen: reopen them first.
pipeline_contract.md §2.1 E.
"""
from datetime import datetime, timedelta

from airflow.sdk import Param, dag, task


@dag(dag_id="gold_backfill", schedule=None, start_date=datetime(2026, 10, 1), catchup=False, max_active_runs=1,
     default_args={"retries": 1, "retry_delay": timedelta(minutes=2)}, tags=["gold", "manual"],
     params={"date_from": Param("2026-09-30", type="string", format="date"),
             "date_to": Param("2026-09-30", type="string", format="date")})
def gold_backfill():
    @task
    def find_shifts(params=None):
        from gold.shifts import in_date_range
        return in_date_range(params["date_from"], params["date_to"])

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


gold_backfill()
