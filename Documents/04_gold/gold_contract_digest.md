# Gold write contract — digest for the pipeline (2026-10-02)

> Digest of [[gold-write-contract]] (normative, ~1,800 lines) and of the downtime notes in [[downtime_detector]]. **Where this digest and the contract disagree, the contract wins.**
> It maps the contract's old master-data vocabulary onto the new ERD ([[erd_spec_2026-10-01]]) and our silver ([[silver_model]]).
> **Decision 2026-10-02:** our Gold is the **`gold` schema** ([[gold_model]], `ddl_gold.sql`), not `oee`. Use this contract only for mechanics: idempotent upserts, rollup and NULL rules, watermarks, tests.

## 1. The ground rules

| Rule | Consequence for us |
|---|---|
| Gold = the **`oee` schema** in the `mes-master-data` **plain Postgres 18.4** (not Timescale; §8 measured it and kept the heap) | Our `gold` schema and `ddl_gold.sql` are **not** the target. `mes-oee-monitor` owns all `oee` DDL through numbered migrations. If we need a column, we ask for a migration |
| Airflow / dbt owns every bulk write, directly in the database | Upsert on the enforced keys (§2). Connect with `application_name = dbt-gold` (§5) |
| The near-realtime dashboard reads **Silver**; Gold is cold (reports) | Silver views serve live screens; Gold only needs closed or scheduled periods |
| Schedule `0 0,8,16 * * *` UTC = shifts ending 07/15/23 WIB (§7) | One DAG run per shift close |
| **Never write** `downtime_report_history`, `work_unit_shift_report_approvals(_events)`, `work_center_quality_entries` | **Read** two of them: §13 overrides, §18 operator rejects |
| `etl_watermarks` post-hook on **every** model (§4). `ck_etl_watermarks_table` only accepts real table names | Model name must equal table name |
| Tables with GENERATED columns (`product_perf_*_hour`, `reject_count_*`) | Use `delete+insert`, or merge with `merge_exclude_columns` |
| NULL means unknown, never 0 (rates, mttr/mtbf on day/month/year, weight_gram, ideal_rate) | Never COALESCE to 0 |
| Rollups: **sum durations and counts; recompute rates; never `avg()`** (§15.5) | Applies to every time hop: shift → day → month → year |

## 2. Tables we must fill (current set, migrations 0003–0028)

| Subject | Tables | Key | Notes |
|---|---|---|---|
| Machine (work unit) | `work_unit_{shift,day,month,year}_reports` | shift: (`date` = shift start instant, `shift`, `work_unit_id`); day/month: (`date`, [`month`], `work_unit_id`); year: `business_date` | Also write `business_date`, `time_zone`, `work_center_*`, `area/site/enterprise_*`, `mttr`, `mtbf` |
| Machine per SKU | `product_count_wu_{shift,day}` | (`work_unit_id`, `shift_date`, `shift`, `product_id`) / (…, `date`, …) | total_out, reject, rework × `factor_to_base` (base unit); **good_out is NOT scaled** |
| Machine hourly | `product_perf_wu_hour` | (`work_unit_id`, `bucket_start`, `product_id`) | Local-hour buckets; one row per product per bucket; Σ runtime ≤ 1 h; `ideal_rate_per_hour` as of the bucket |
| Machine rejects | `reject_count_wu_shift` | (…, `product_id`, `reason`) | `reason` = canonical counter tag name (a key member, must stay stable); `weight_gram` NULL if not weighed |
| Stops | `downtime_reports` | `source_event_id` (UNIQUE) | Operator-editable; apply the §13 override |
| Stops (immutable log) | `downtime_log_events` | `event_id` | Same id as `source_event_id`; `reasons text[]`; never UPDATE or DELETE |
| Line (work center) | `work_center_{shift,day,month,year}_reports` | (`business_date`, `shift`, `work_center_id`) … | **Measured, not summed** (see §4); two units (infeed / output) |
| Line per SKU | `product_count_wc_{shift,day}`, `product_perf_wc_hour`, `reject_count_wc_shift` | as machine, one level up | From the line's own count points, **not** a rollup of the machine tables |
| SKU | `product_report_wc_day` | (`work_center_id`, `business_date`, `product_id`) | Measured per SKU, never apportioned from the line; NULL if not measurable; upsert, not delete+insert |
| Area | `area_{day,month,year}_reports` | (`area_id`, `business_date`) | **Composed** = Σ of its lines; durations in line-ms; counts as a **mass** (std weight) in one unit |

Every table also carries the hierarchy above its own grain (0028): area, site and enterprise ids and names. **`area_id` is the priority**, because a NULL `area_id` row is invisible on every screen.

## 3. Time rules

- `business_date` runs **07:00 → 07:00 plant-local**; shift 3 belongs to the day it started. Compute it with `AT TIME ZONE`, never by adding hours (DST).
- **We get this from `silver.shift_instance.business_date`**, which is consistent. Day = all shift instances with that `business_date`.
- Month row: `business_date` = 1st of the month. Year row: January 1. `month` label = `YYYY-MM`.
- **Hourly buckets on the plant's local clock** (`date_trunc('hour', ts AT TIME ZONE tz)`), never UTC hours. Test: 7–9 buckets per shift.
- `downtime_reports.date` (a timestamptz holding a day) is being retired (§22, TODOS §71); write `business_date` and `time_zone`. **Never derive a day with `date::date`.**

## 4. Decisions 2026-10-02 (user): anchor to the ERD + PRD, not to every contract section

- **Work center = Σ of its work units** through `work_unit_kpi_binding` (PRD `sum_of_terms`). The contract's "line is measured" model (infeed/output count points, line wall clock, routing steps) is **not adopted**. Gold `work_center_*` rows are filled from the summed work-unit terms.
- **No dbt.** Airflow with plain SQL/Python tasks: read Silver (Timescale), write Gold (`oee`, plain Postgres, no Timescale). Keep the contract's upsert keys, watermark rows, `application_name`, the §13 override and the §18 operator-entry join, implemented in Airflow SQL.
- **Stops:** `silver.downtime_events` now has `source_event_id`, `downtime_reason_ids[]` (all cited reasons, ordered) and `product_id` (SKU at the start). Tested. Gold reason labels = **`downtime_reason.name`**. A person's relabel applies to that stop piece only.
- **Product runtime per hour:** derived from per-minute counts (no `product_runs` table); about 1 minute of accuracy per changeover.

### Original conflict list (kept for reference)

| # | Our decision | Contract (normative; §16.10 answers came from the pipeline owner) | Impact |
|---|---|---|---|
| C1 | **Method B:** work center = Σ of its work units' terms | **A line is MEASURED, not summed.** It has its own count points (infeed + output), its own wall clock (`availability_time` 8 h for a 4-machine line, not 32 h), and quality is **time-derived**. `line <> sum(machines)`. Built independently, not chained from the machine reports | Work-center time terms must **not** be Σ work units. The line needs its own infeed/output count points and its own stops |
| C2 | Work-center finished good = last routing step's work units | Output count point = the line's outfeed counter; infeed = the line's infeed counter (`total_out`, `reject`, `rework`, `line_out` in **infeed** units; `good_out`, `effective_out` in **output** units) | Method B's routing first/last step **can** identify these count points, so the view `v_routing_step_work_unit` is still useful. We still need a rule for the line's **stops and runtime** |
| C3 | Gold = our `gold` schema / [[gold_tables]] shape | Gold = the `oee` schema owned by `mes-oee-monitor` (~30 tables, keys and CHECKs fixed by migrations) | `ddl_gold.sql` is a design artifact only. Do not deploy it |
| C4 | Silver `product_id` only on count rows | `product_perf_*_hour` needs **runtime per product per hour** (a changeover mid-hour = 2 rows) | Needs product-run intervals in Silver (the gap flagged earlier) |
| C5 | `downtime_events` has one `downtime_reason_id` | Gold needs `reason` = jsonb array of labels (element 0 = primary), `reasons text[]` (all cited), `actions`, `detail_reason`, `input_by`, `maintained_by` | Silver must keep **all** reason signals seen in a stop window, ordered |
| C6 | `downtime_events` has no stable public id | `source_event_id` / `event_id` = **deterministic** text from the Silver event, identical in both tables | Add `source_event_id` to `silver.downtime_events` (e.g. `{asset_id}:{watched tag_id}:{start_at epoch ms}`) |

## 5. What each Gold value needs from Silver and master data

| Gold value | Source in our world |
|---|---|
| total / good / reject counts per work unit | `production_events_1m` / `reject_events_1m` through `work_unit_kpi_binding` (`quality.*`) |
| `factor_to_base`, base unit | `product_uom_conversion` (tag unit → `product.base_uom_id`) as of `business_date`; must be the same for all rows sharing (product, uom, base_uom) (§3.4) |
| `ideal_rate_per_hour` (in the row's `uom_id`) | `3600 / (cycle_time_value × seconds_per_unit / batch_size)` from `operation_work_unit` / `operation` as of the bucket; NULL if none; `ideal_rate_source` ∈ master-data / work-unit-default / carried-forward / unknown; `ideal_rate_valid_from` |
| Stops (pdt / updt / ms times, downtime_reports rows) | `silver.downtime_events` (already split at shift boundaries, as §21 requires) + the §13 operator override on category / reason / detail_reason / actions |
| mttr / mtbf | Closed **unplanned** stops (PRD KPI-005); small stops excluded. Rollups recompute from Σ repair time and failure count (failures = updt_time / mttr) |
| `reject_count_wu_shift.reason` | `asset_tags.tag_name` of the reject counter (stable); `reason_id/code` ← `asset_tags.reject_reason_id`; `source_tag` ← `asset_tags.node`; `counter_value` in the tag unit |
| `weight_gram` | Only from a real weight tag; otherwise NULL |
| Product of a stop (`product_id`, `product_name`) | **The SKU running at `start_at`** (§21 Q1); raw code → `asset_product_codes` |
| Area mass | `product_detail.standard_weight` + `weight_uom_id` (the new ERD has no `product_packaging`) |
| Line operator rejects | `oee.work_center_quality_entries` (`voided_at IS NULL`, `target_qty`), joined **once** at shift grain with the unit gate `target_uom_id = infeed_uom_id` (§18) |
| `expected_work_unit_count` | Number of work units in the work center from master data |

## 6. Old vocabulary → new ERD

| Contract (old master data) | New ERD |
|---|---|
| `ms_core.count_points` (`source_tag`, `uom_id`) | `asset_tags` (`node`, `uom_id`, `tag_role`) |
| `ms_core.product_code_alias` | `asset_product_codes` (`tag_id`, `raw_value`, valid as of time) |
| `ms_product.product_packaging` (`factor_to_base`, `std_weight`) | `product_uom_conversion` + `product_detail.standard_weight` / `weight_uom_id` |
| `ms_reject.reject_reasons` | `reject_reason` |
| `ms_core.enterprices / sites / areas / work_centers / work_units` | `enterprise / site / area / work_center / work_unit` |
| "equipment" | asset |
| `factory_*` | `site_*` (renamed in 0028) |

## 7. dbt tests the contract requires (error severity unless noted)

1. Hourly: 7–9 buckets per shift; Σ runtime per bucket ≤ 3,600,000 ms; hourly total = `product_count_wu_shift` (§3.7).
2. `reject_count`: `converted_value` = round(counter × factor, 3); Σ ranked vs the tile (report the gap) (§3.8).
3. `downtime_log_events`: no blank or untrimmed reasons; count vs `downtime_reports` (report); duplicate reasons (warn) (§3.9).
4. §13 override applied (reconciliation query).
5. Day = Σ shifts and month = Σ days on runtime and availability_time, for each subject (§15.6, §16.8, extended to reject/rework by §18).
6. Stranded quality entries (warn) (§18.6).

## 8. Open decisions (need the user)

- **D1 (C1/C2): how is a line measured?** Proposal:
  - infeed count point = first routing step's work units;
  - output count point = last routing step's work units (Method B view);
  - line stop = its output count point stalled (all output work units stalled);
  - line `availability_time` = shift length (one wall clock).
- **D2 (C4):** add `silver.product_runs`, or approximate product runtime per minute?
- **D3 (C5):** reason labels in `reason` / `reasons`: `downtime_reason.code`, `.name`, or the PLC tag name? Order of the elements?
- **D4:** Gold is a different database from Silver (PG 18 vs Timescale). Does Airflow move the aggregates, or is there a foreign-data wrapper or shared cluster?
- **D5:** dbt (as the contract assumes) or plain Airflow SQL operators?
