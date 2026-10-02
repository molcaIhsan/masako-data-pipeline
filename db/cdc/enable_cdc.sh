#!/usr/bin/env bash
# Open the Postgres WAL for Flink CDC (master data -> Flink broadcast state).
#   1. wal_level = logical (+ enough slots / senders)   -> needs a Postgres RESTART
#   2. replication role flink_cdc, publication flink_master_pub, REPLICA IDENTITY FULL on the master tables
# Usage:  PGHOST=... PGPORT=5432 PGUSER=postgres PGPASSWORD=... PGDATABASE=... FLINK_CDC_PASSWORD=... ./enable_cdc.sh
# Afterwards:  ./enable_cdc.sh status    (shows wal_level, publication tables, replication slots and lag)
set -euo pipefail
cd "$(dirname "$0")"
PSQL=(psql -v ON_ERROR_STOP=1 -X -q)

if [[ "${1:-}" == "status" ]]; then
  "${PSQL[@]}" -c "SHOW wal_level" \
    -c "SELECT pubname, schemaname||'.'||tablename AS published FROM pg_publication_tables WHERE pubname='flink_master_pub' ORDER BY 2" \
    -c "SELECT slot_name, plugin, active, pg_size_pretty(pg_wal_lsn_diff(pg_current_wal_lsn(), confirmed_flush_lsn)) AS lag FROM pg_replication_slots"
  exit 0
fi

current=$("${PSQL[@]}" -At -c "SHOW wal_level")
if [[ "$current" != "logical" ]]; then
  echo "wal_level is '$current' -> setting logical (restart required)"
  "${PSQL[@]}" -c "ALTER SYSTEM SET wal_level = 'logical'" \
               -c "ALTER SYSTEM SET max_replication_slots = 10" \
               -c "ALTER SYSTEM SET max_wal_senders = 10"
  echo ">>> Restart Postgres now (e.g. docker restart <container> / systemctl restart postgresql), then run this script again."
  exit 2
fi

"${PSQL[@]}" -f enable_cdc.sql
: "${FLINK_CDC_PASSWORD:?set FLINK_CDC_PASSWORD}"
"${PSQL[@]}" -c "ALTER ROLE flink_cdc WITH PASSWORD '${FLINK_CDC_PASSWORD}'"
echo "CDC ready: publication flink_master_pub. Flink creates its own slot (FLINK_CDC_SLOT)."
echo "IMPORTANT: an unused slot keeps WAL forever. Drop it if Flink is decommissioned:"
echo "  SELECT pg_drop_replication_slot('<slot>');"
