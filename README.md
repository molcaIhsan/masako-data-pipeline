# masako-data-pipeline

Kafka machine data → **Flink** → TimescaleDB `silver` (+ live views) → **Airflow** → `gold` reports, for MolcaDx.
Design, contracts and decisions: [`Documents/README.md`](Documents/README.md) (start with `05_contracts/pipeline_contract.md`).

| Path | What |
|---|---|
| `flink/` | Java 17 / Flink 2.3.0 silver job: Kafka (Telegraf JSON) → counter deltas, product, stops → `silver.*`; master data live via Postgres CDC; DLQ topic |
| `airflow/` | Airflow 3.3.2 DAGs (no dbt): `gold_shift_close`, `gold_rerun_window`, `gold_apply_edits`, `gold_rollups`, `gold_backfill`, `gold_quality_checks` |
| `Documents/03_silver/ddl_silver.sql` → `Documents/04_gold/ddl_gold.sql` → `Documents/03_silver/ddl_views.sql` | DDL, in this run order |
| `db/` | `roles.sql` (least privilege), `cdc/enable_cdc.sh` (open the WAL for CDC), `seed/dev_master.sql` |
| `docker/` | Dev / test stack (TimescaleDB, Flink, Airflow, optional local Kafka for simulation) |
| `tools/` | `telegraf_sim.py` (simulated machines), `dev_reset.sh`, `reapply_views.sh` |

## Run locally (simulation)

```bash
cp .env.example .env.dev                      # dev values; the real broker goes in .env later
cd flink && docker run --rm -v "$PWD":/src -v masako-m2:/root/.m2 -w /src maven:3.9-eclipse-temurin-17 mvn -B package && cd ..
cd docker && ENV_FILE=../.env.dev docker compose --env-file ../.env.dev --profile sim up -d && cd ..
ENV_FILE=../.env.dev tools/dev_reset.sh --recreate-topic           # (re)submits the Flink job
for s in worked_example edge_cases tail; do python3 tools/telegraf_sim.py --scenario $s; done | \
  docker compose -f docker/docker-compose.yml --env-file .env.dev exec -T kafka /opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server kafka:9092 --topic machine_metrics --reader-property parse.key=true --reader-property "key.separator=	"
docker compose -f docker/docker-compose.yml --env-file .env.dev exec -T airflow airflow dags test gold_shift_close
```
Expected for work unit 7, shift 500 (2026-09-30 shift 1), after the "Break" relabel (`gold.downtime_edit`):
**A 95.111 · P 91.121 · Q 98.000 · OEE 84.933**. UIs: Flink http://localhost:8081, Airflow http://localhost:8080.

## Production (real Kafka)
1. Put broker / topic / credentials in `.env` (see `.env.example`; `KAFKA_PROP_*` for SASL/SSL).
2. Database: run the 3 DDL files, `db/roles.sql`, then `db/cdc/enable_cdc.sh` (sets `wal_level=logical`, needs one restart).
3. Flink: `flink run -d silver-job-0.1.0.jar` with the `.env` variables in the client environment. Restart from the
   latest retained checkpoint with `flink run -s <checkpoint path> ...` (offsets + CDC position + state).
4. Airflow: mount `airflow/dags` and `airflow/include` (on `PYTHONPATH`), connection `masako_db`, unpause the DAGs.
