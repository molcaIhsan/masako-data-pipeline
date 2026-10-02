"""Database access. Connection id `masako_db` (env AIRFLOW_CONN_MASAKO_DB), role airflow_gold, application_name airflow-gold."""
from contextlib import contextmanager

CONN_ID = "masako_db"


@contextmanager
def transaction():
    """One transaction: commit on success, rollback on error."""
    from airflow.providers.postgres.hooks.postgres import PostgresHook   # imported lazily (lean DAG parsing)

    conn = PostgresHook(postgres_conn_id=CONN_ID).get_conn()
    try:
        with conn.cursor() as cur:
            yield cur
        conn.commit()
    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()


def query(sql, params=None):
    with transaction() as cur:
        cur.execute(sql, params or {})
        return cur.fetchall() if cur.description else []
