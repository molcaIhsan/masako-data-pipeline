"""
F. gold_quality_checks — daily: error checks fail the run (a row returned = a violation); warn checks only log.
pipeline_contract.md §2.3.
"""
from datetime import datetime

from airflow.sdk import dag, task

from gold.checks import ERROR_CHECKS, WARN_CHECKS   # plain dicts of SQL text: no DB access at parse time


@dag(dag_id="gold_quality_checks", schedule="0 2 * * *", start_date=datetime(2026, 10, 1), catchup=False,
     max_active_runs=1, tags=["gold", "checks"])
def gold_quality_checks():
    for name, sql in ERROR_CHECKS.items():
        @task(task_id=f"error__{name}")
        def check(sql=sql, name=name):
            from gold.db import query
            bad = query(sql)
            if bad:
                raise ValueError(f"{name}: {len(bad)} violation(s), e.g. {bad[:5]}")
            return 0
        check()

    for name, sql in WARN_CHECKS.items():
        @task(task_id=f"warn__{name}")
        def warn(sql=sql, name=name):
            import logging
            from gold.db import query
            rows = query(sql)
            if rows:
                logging.warning("%s: %d row(s): %s", name, len(rows), rows[:20])
            return len(rows)
        warn()


gold_quality_checks()
