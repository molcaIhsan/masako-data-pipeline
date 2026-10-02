#!/usr/bin/env bash
# Dev only: stop the silver job, wipe silver facts + gold reports (edit tables are kept), reset the Kafka group and the
# CDC slot, resubmit the job. --recreate-topic also empties the (simulation) topics.
set -euo pipefail
cd "$(dirname "$0")/../docker"
export ENV_FILE=${ENV_FILE:-../.env.dev}
dc() { docker compose --env-file "$ENV_FILE" "$@"; }
for j in $(curl -s localhost:8081/jobs/overview | python3 -c "import sys,json;[print(j['jid']) for j in json.load(sys.stdin)['jobs'] if j['state'] not in ('FAILED','CANCELED','FINISHED')]"); do
  dc exec -T jobmanager flink cancel "$j" >/dev/null 2>&1 || true
done
sleep 5
dc exec -T timescaledb psql -U postgres -d masako -q -c "
  TRUNCATE silver.production_events, silver.reject_events, silver.downtime_events;
  TRUNCATE gold.work_unit_shift_report, gold.work_unit_day_report, gold.work_unit_month_report, gold.work_unit_year_report,
           gold.work_center_shift_report, gold.work_center_day_report, gold.work_center_month_report,
           gold.work_center_year_report, gold.product_count, gold.product_count_day, gold.reject_count,
           gold.downtime_report, gold.loss_by_reason, gold.product_perf_hourly, gold.etl_watermarks;
  SELECT pg_drop_replication_slot(slot_name) FROM pg_replication_slots WHERE NOT active;" >/dev/null
if [[ "${1:-}" == "--recreate-topic" ]]; then
  for t in "${KAFKA_TOPIC:-machine_metrics}" "${KAFKA_TOPIC:-machine_metrics}_dlq"; do
    dc exec -T kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --delete --topic "$t" >/dev/null 2>&1 || true
    sleep 2
    dc exec -T kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --topic "$t" --partitions 6 --replication-factor 1 >/dev/null
  done
fi
dc exec -T kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server kafka:9092 --group flink-silver \
  --reset-offsets --to-earliest --all-topics --execute >/dev/null 2>&1 || true
dc exec -T jobmanager flink run -d /opt/flink/usrlib/silver-job-0.1.0.jar 2>&1 | grep -E "submitted|Exception" || true
