#!/usr/bin/env bash
# Dev only: set the pipeline role passwords from the container environment.
set -euo pipefail
psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<SQL
ALTER ROLE flink_silver PASSWORD '${FLINK_PG_PASSWORD}';
ALTER ROLE airflow_gold PASSWORD '${AIRFLOW_PG_PASSWORD}';
ALTER ROLE flink_cdc    PASSWORD '${FLINK_CDC_PASSWORD}';
SQL
