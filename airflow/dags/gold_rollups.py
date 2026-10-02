"""
D. gold_rollups — daily safety net: rebuild yesterday's and today's day rows plus the current month and year
(A/B/C already roll up what they load). pipeline_contract.md §2.1 D.
"""
from datetime import datetime, timedelta

from airflow.sdk import dag, task


@dag(dag_id="gold_rollups", schedule="30 1 * * *", start_date=datetime(2026, 10, 1), catchup=False,
     max_active_runs=1, default_args={"retries": 2, "retry_delay": timedelta(minutes=5)}, tags=["gold"])
def gold_rollups():
    @task
    def rollup(data_interval_end=None, run_id=None):
        from gold.rollups import run_rollups
        end = data_interval_end.date()
        return run_rollups([end - timedelta(days=2), end - timedelta(days=1), end], run_id)

    rollup()


gold_rollups()
