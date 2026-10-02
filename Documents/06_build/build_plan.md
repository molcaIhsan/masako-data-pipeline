# Build plan — Flink + Airflow (2026-10-02)

> Implements [[pipeline_contract]] (F1–F16, DAGs A–F) on top of the tested DDL (`ddl_silver.sql` → `ddl_gold.sql` → `ddl_views.sql`).
> Context: [[silver_model]] · [[gold_model]] · [[editing_model]] · [[app_contract]] · [[flink_rules]] · [[airflow_rules]] · [[objective]].
> Human in the loop: open questions are in §5. **Blocking** ones must be answered before that phase starts.
>
> **Status 2026-10-02:** phases 0–5 built and tested end to end on simulated data (see §6). Waiting for: the real Kafka (`.env`) and real messages (B3).

## 1. Versions (checked 2026-10-02 against Maven Central / PyPI / Docker Hub)

| Component | Version | Note |
|---|---|---|
| Flink | **2.3.0**, image `flink:2.3.0-java17` | Java **17** (Flink 2.x default; local Java 25 is not used, the build runs in Docker) |
| Kafka source/sink | `flink-connector-kafka` **5.0.0-2.2** | ⚠ Built for Flink 2.2; no 2.3 build yet. Verified in phase 1 (smoke test); fallback = Flink 2.2.1 |
| JDBC | Plain **pgjdbc** in our own `Sink` (SinkV2) writer | No dependency on `flink-connector-jdbc` 4.1.0-2.2; full control of the upsert SQL (open/close stop rows) |
| Build | Maven 3.9 in Docker (`maven:3.9-eclipse-temurin-17`) | Shaded fat JAR, Flink deps `provided` (lean JAR, [[flink_rules]]) |
| Airflow | **3.3.2**, image `apache/airflow:3.3.2-python3.12` | `apache-airflow-providers-postgres` 7.1.0, `-common-sql` 2.2.0. **No dbt** |
| DB | TimescaleDB 2.30 / PG 16 (dev) | Same DDL as production |

## 2. Repository layout

```
flink/                               Maven project (silver job)
  pom.xml
  src/main/java/id/molca/pipeline/silver/
    SilverJob.java                   main(): wiring + config
    config/JobConfig.java            env / args (Kafka, JDBC, intervals)
    model/                           RawReading, ResolvedReading, CounterRow, RejectRow, StopRow, DlqRecord
    parse/TelegrafParser.java        JSON -> RawReading (value after last '='), errors -> DLQ
    master/MasterData.java           immutable snapshot (tags by node, placements, shifts, bindings, product codes, reasons)
    master/MasterDataCache.java      JDBC reload every 60 s, one per TaskManager JVM (as-of lookups)
    resolve/ResolveFunction.java     F3: source_tag -> tag, asset, work unit, shift; fan-out machine-wide reason tags
    lane/LaneProcessor.java          F4-F6: keyed (asset, work_unit); event-time ordered buffer; delta/reset; product debounce
    stall/StallDetector.java         F7-F13: keyed work_unit; 60/300 s timers; reasons; shift split; source_event_id
    sink/PgUpsertSink.java           batched upsert writers for production_events / reject_events / downtime_events
    dlq/DlqSerializer.java           F14 -> Kafka DLQ topic
  src/test/java/...                  operator test harness tests (delta, reset, ordering, stall, shift split)
airflow/
  dags/
    gold_shift_close.py              A: every 15 min, shifts ended >= 5 min ago and not loaded
    gold_rerun_window.py             B: hourly, reload shifts that ended in the last 72 h
    gold_apply_edits.py              C: every 5 min, shifts touched by edits / voids / reopen
    gold_rollups.py                  D: day / month / year (triggered + daily)
    gold_backfill.py                 E: manual (date range, work units)
    gold_quality_checks.py           F: SQLCheckOperator checks
  include/gold/                      shared Python (shift finders, loader, advisory locks, watermarks)
  include/sql/                       load_shift.sql, rollup_*.sql, checks/*.sql
  tests/                             DAG import test + SQL tests against the dev DB
db/                                  copy of the run-order DDL used by docker init (source of truth stays in Documents/)
docker/
  docker-compose.yml                 timescaledb, kafka (KRaft), flink jobmanager + taskmanager, airflow, init
  init/                              apply DDL + seed, create Kafka topics
tools/
  telegraf_sim.py                    produce Telegraf-shaped messages (scenarios: worked example, reset, late, stall, shift split)
```

## 3. Design decisions already fixed (from our sessions)

- **Silver tables Flink writes.** `production_events` / `reject_events` upsert on `(tag_id, event_time)`. `downtime_events` upsert on `source_event_id`. Flink never touches edit tables.
- **Counter rules.**
  - `delta_counter` → value as-is.
  - `cumulative_counter` → difference from the previous value. A reset (value < previous) counts the new value; the first value is a baseline of 0.
  - Reject nodes give weight (option B), with the reason taken from the tag.
- **Stops.**
  - Stall of the tag bound to `availability.downtime_reason` (`stall_bucket`): <60 s nothing, 60–300 s small stop, >300 s downtime (fixed in code).
  - Open row at +60 s; promoted at +300 s, to planned only if the primary reason is planned; closed on the next increment.
  - Split at the shift end with the same `stop_group_id`; `source_event_id` = `wu{wu}-t{tag}-{start ms}`.
  - Reasons are the reason tags that turned on or incremented in the window, ordered; product = the product at stop start.
- **Product.** The `product_code` value must hold 30 s, then maps through `asset_product_codes` as of the event time.
- **Time.** Event time with a 1 min watermark and idleness; data more than 1 min late goes to the DLQ.
- **Airflow load.** Delete + insert per shift in one transaction, approved work units skipped, plus a `pg_advisory_xact_lock(shift_instance_id)` so DAGs A, B, C and E never load the same shift at once.
- **Rollups** are new SQL (gold views or functions) built in phase 5; DAG D only does `INSERT … SELECT`.

## 4. Phases and to-do

Status: ☐ todo · ◐ in progress · ☑ done · ⛔ blocked on a question

### Phase 0 — decisions and DDL hardening
- ⛔ Answer the blocking questions (§5: B1–B4)
- ☐ Add CHECK constraints for the pipeline-critical enums (`tag_role`, `kind`, `count_basis`, `transform`, `shift_instance.status`, `node_naming`, `work_center.type`, `component_level`) and seed `kpi_formula_slot` (Q B4)
- ☐ DB roles: `flink_silver` (INSERT/UPDATE on the 3 fact tables + SELECT master), `airflow_gold` (gold report tables + SELECT silver) (Q N5)
- **Done when:** the DDL re-applies cleanly and all earlier tests still pass

### Phase 1 — dev environment
- ☐ `docker/docker-compose.yml`: TimescaleDB, Kafka KRaft (+ topics `machine_metrics`, DLQ), Flink 2.3.0 jobmanager/taskmanager, Airflow 3.3.2 (standalone)
- ☐ Init container: apply the 3 DDL files + seed master data
- ☐ `tools/telegraf_sim.py`: scenarios as message files plus "live" mode
- ☐ Smoke test: an empty Flink job with the Kafka 5.0.0-2.2 connector runs on Flink 2.3.0 (otherwise switch to 2.2.1, Q N6)
- **Done when:** `docker compose up` brings everything up healthy and messages are visible on the topic

### Phase 2 — Flink core: parse → resolve → counters → silver
- ☐ `TelegrafParser` + DLQ side output (parse errors)
- ☐ `MasterDataCache` (60 s JDBC reload, as-of lookups) + `ResolveFunction` (tag, work unit, shift; unresolved → DLQ)
- ☐ `LaneProcessor`: event-time ordered buffer, delta/reset/baseline, product debounce + mapping
- ☐ `PgUpsertSink` for `production_events` and `reject_events` (batch 500 / 1 s, flush on checkpoint)
- ☐ Checkpointing (RocksDB incremental, 60 s), restart strategy, parallelism config
- ☐ Unit tests (harness): ordering, reset, baseline, duplicate replay, late → DLQ, product switch at 30 s
- **Done when:** the simulator replays the worked example and `silver.v_wu_shift_report` shows total 24,000 / good 23,520 / reject 480 for WU 7

### Phase 3 — Flink stops
- ☐ `StallDetector`: watched tags from the bindings (as of `business_date`), timers +60 s / +300 s, open/promote/close upserts
- ☐ Reason tracking (machine-wide reason tags fanned out to lanes), ordered `downtime_reason_ids`, planned/unplanned promotion
- ☐ Shift split (timer at shift end, same `stop_group_id`), `source_event_id`, product at start
- ☐ Unit tests: 40 s (nothing), 90 s (small stop), 18 min (downtime + reason), stop across the shift end, stop open while the feed is idle, replay gives the same ids
- **Done when:** the worked-example replay gives **A 95.111 · P 91.121 · Q 98.000 · OEE 84.933** through the views, with a live open stop visible during the stall

### Phase 4 — Airflow load (A, B, C)
- ☐ `include/gold`: shift finder queries, loader (load_shift.sql in one transaction + advisory lock + frozen skip), watermark upsert
- ☐ DAG A `gold_shift_close`, DAG B `gold_rerun_window`, DAG C `gold_apply_edits` (edit-table watermarks, reopen events)
- ☐ Tests: DAG import; load twice = same; silver edit → reload in B; gold edit → reload in C; approved stays frozen; reopen → reloaded
- **Done when:** all 7 gold shift tables fill automatically after a simulated shift end, and edits flow through

### Phase 5 — rollups, backfill, checks (D, E, F)
- ☐ SQL: work unit / work center day, month, year (sum durations and counts, recompute rates, MTTR/MTBF via `fn_reliability`), `product_count_day`
- ☐ DAG D `gold_rollups` (triggered by A/B/C + daily), DAG E `gold_backfill`, DAG F `gold_quality_checks`
- ☐ Tests: day = Σ shifts, month = Σ days, year = Σ months; rates recomputed, not averaged
- **Done when:** every gold table in the first delivery is filled and the checks are green

### Phase 6 — hardening
- ☐ Kill / restart Flink mid-stall → state restored, no duplicate or lost stops
- ☐ Replay from offset 0 → silver identical (idempotency)
- ☐ Load test: 1,000 tags × 1 msg/s, 1 day → throughput, checkpoint size, view latency for one shift
- ☐ Runbook (deploy, replay, backfill, reopen) + update the contracts

## 5. Questions (human in the loop)

**Blocking**

| # | Question | Default if you say "ok" | Blocks |
|---|---|---|---|
| B1 | Kafka: bootstrap servers, security (none / SASL / SSL), topic name(s) and partition count; first deploy reads from **earliest** or **latest**; DLQ topic name | Dev: local Kafka, topic `machine_metrics` (6 partitions), DLQ `machine_metrics_dlq`, start = earliest | Phase 1–2 |
| B2 | Where do Flink and Airflow run in production? (standalone cluster, Kubernetes + Flink operator, an existing Airflow?) | Build and test on docker-compose now, same images for production; decide the prod target later | Phase 6 |
| B3 | Real messages for: **`good_weight`** (running total or per piece? g or kg?), the full **ANRITSU node list** (where does `ng_count` fit?), a **`product_code`** tag (value is a string or a number?), a **`downtime_reason`** tag (0/1 bit, or a counter?) | Reason tags = 0/1 bits ("turned on" = 0→1, also accept "incremented"); product code = string compared exactly | Phase 2–3 |
| B4 | Add CHECK constraints for the enums + seed `kpi_formula_slot` | Yes, with the PRD values | Phase 0 |

**Non-blocking (defaults apply unless you object)**

| # | Question | Default |
|---|---|---|
| N1 | Java package name | `id.molca.pipeline.silver` |
| N2 | Airflow alerting on failure | Task failure callback → log only (add email/Slack later) |
| N3 | After the very first deploy (no state yet), the first value of each counter is a baseline, so no stop is recorded for the time before it | Accept (later restarts keep state through checkpoints) |
| N4 | Telegraf sends a value even when unchanged? | Either works: no increase in any form = stall |
| N5 | DB roles for least privilege (`flink_silver`, `airflow_gold`) | Create them in the DDL |
| N6 | Kafka connector 5.0.0-2.2 fails on Flink 2.3 | Fall back to Flink 2.2.1 (same API) |

## 6. Status and what testing taught us (2026-10-02)

| Phase | Status | Evidence |
|---|---|---|
| 0 | ☑ | Enum CHECKs + `kpi_formula_slot` seed in `ddl_silver.sql`; `db/roles.sql`; `db/cdc/enable_cdc.{sh,sql}` |
| 1 | ☑ | `docker/docker-compose.yml`, `tools/telegraf_sim.py` (worked example, edge cases, tail) |
| 2–3 | ☑ | 24 unit tests (counter, product, parsers, stall) + replay: WU 7 total 24,000 / good 23,520 / reject 6.0 + 3.6 kg; stops 4.5 small, 30 break, 22 with `{Material jam, Film out}` |
| 4–5 | ☑ | DAGs A–F run green; WU 7 shift 500 **A 95.111 · P 91.121 · Q 98.000 · OEE 84.933**; line good from the final machine; day/month/year rollups; approval freeze holds |
| 6 | ◐ | Kill / restore from checkpoint ☑, live master change via CDC ☑, replay idempotency ☑. Load test (1,000 tags) and runbook: ☐ |

Fixes found by running it (all in the code now):
1. Flink CDC 3.6.0-2.2 on Flink 2.3 needs its own **shaded Guava 31, relocated** (`org.apache.flink.*` loads parent-first).
2. Debezium's JsonConverter targets **kafka-clients 3.x** and breaks next to the Kafka connector's 4.x → our own `MasterChangeDeserializer` (no JsonConverter).
3. **Whole-job failover** (`jobmanager.execution.failover-strategy: full`): Flink CDC's split assigner fails on regional restart.
4. The master stream must mark itself **idle** (not emit a max watermark), or event time jumps to infinity when Kafka is quiet.
5. Event time is assigned **in the Kafka source** (per-partition watermarks); sink batches are **collapsed per key** (an upsert cannot touch a row twice).
6. `no_data` status: a work-unit shift with no readings and no stops has NULL rates and is excluded from line and period sums (PRD: no data is never 100% availability).
7. A count without a product (e.g. a checkweigher with no product-code tag) stays in its tag's own unit.

Known limitation: if the machine feed stops mid-shift and never resumes before the report, the rest of the shift has no
stop decision (event time stands still). When the feed resumes, the gap becomes a stop retroactively and DAG B (72 h)
reloads the shift.

