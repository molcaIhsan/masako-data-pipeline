#!/usr/bin/env bash
# Re-create silver views (+ fn_reliability) from Documents/03_silver/ddl_views.sql without touching tables or caggs.
set -euo pipefail
cd "$(dirname "$0")/../docker"
export ENV_FILE=${ENV_FILE:-../.env.dev}
psqlx() { docker compose --env-file "$ENV_FILE" exec -T timescaledb psql -U postgres -d "${PG_DATABASE:-masako}" -v ON_ERROR_STOP=1 -q "$@"; }
psqlx -c "DO \$\$ DECLARE r record; BEGIN
  FOR r IN SELECT table_name FROM information_schema.views WHERE table_schema = 'silver' AND table_name LIKE 'v\_%' LOOP
    EXECUTE format('DROP VIEW IF EXISTS silver.%I CASCADE', r.table_name);
  END LOOP; END \$\$;
  DROP FUNCTION IF EXISTS silver.fn_reliability(bigint[], timestamptz, timestamptz) CASCADE;"
psqlx -f - < ../Documents/03_silver/ddl_views.sql
psqlx -f - < ../db/roles.sql >/dev/null
echo "views re-applied"
