# App contract — frontend / backend ↔ database (2026-10-02)

> **Audience:** whoever builds the MolcaDx app (backend API + frontend).
> **The database is the contract.** One TimescaleDB database with two schemas: `silver` (master data, machine facts, live views, shopfloor edits) and `gold` (report tables, report edits, approval).
> **Sources:** [[silver_model]] · [[gold_model]] · [[editing_model]] · [[molcadx_app_overview]] · [[erd_spec_2026-10-01]] · PRD-003 / PRD-004 · [[pipeline_contract]] (what Flink and Airflow do).
> **DDL** (run in this order): `03_silver/ddl_silver.sql` → `04_gold/ddl_gold.sql` → `03_silver/ddl_views.sql`.

## 1. Who writes what

| Data | Written by | The app may |
|---|---|---|
| Master data: the 55 ERD tables in `silver` | **The app** | Create, update (see §3 rules), deactivate. Never delete |
| Machine facts: `silver.production_events`, `reject_events`, `downtime_events` (+ 1-minute rollups) | **Flink only** | Read only |
| Shopfloor edits: `silver.downtime_edit`, `downtime_entry`, `quality_entry` | **The app** | Insert and void only (append-only) |
| Report tables: `gold.*_report`, `product_count*`, `reject_count`, `downtime_report`, `loss_by_reason`, `product_perf_hourly` | **Airflow only** | Read only |
| Report edits: `gold.downtime_edit`, `gold.quality_entry` | **The app** | Insert and void only |
| Approval: `gold.report_approval`, `report_approval_event` | **The app**, only through `gold.fn_set_report_status()` | Call the function |
| `silver.audit_log` | Triggers (for edits); **the app** (for master-data writes) | Insert; never update or delete |
| `gold.etl_watermarks` | Airflow | Read (freshness) |

## 2. Reading the calculations

### 2.1 Where each screen reads from

| Screen | Live (current or recent shift) | Report (closed shifts) |
|---|---|---|
| Work unit OEE / A / P / Q, waterfall, times | `silver.v_wu_shift_report` | `gold.work_unit_shift_report` (day / month / year: `gold.work_unit_*_report`) |
| Work center (line) | `silver.v_wc_shift_report` | `gold.work_center_shift_report` (+ day / month / year) |
| SKU / product counts | `silver.v_product_count` | `gold.product_count`, `gold.product_count_day` |
| Reject Pareto (per reason) | `silver.v_reject_count` | `gold.reject_count` |
| Downtime list / timeline | `silver.v_downtime_report` | `gold.downtime_report` |
| Schedule / availability / performance loss Pareto | `silver.v_loss_by_reason` | `gold.loss_by_reason` |
| Hourly performance chart | `silver.v_product_perf_hourly` | `gold.product_perf_hourly` |
| Shift list, `shift_no`, label, time zone | `silver.v_shift` | — |

The live views and the gold tables have **identical columns**, so one payload type serves both. **Always filter live views by `shift_instance_id`** (and `work_unit_id` / `work_center_id`). They compute on the fly.

### 2.2 Meaning of the values

| Item | Rule |
|---|---|
| Times | **Milliseconds** (`bigint`): `availability_time` (POT), `production_time` (PBT), `operating_time` (APT), `runtime`, `net_time`, losses |
| Rates | `availability`, `performance`, `quality`, `oee`, `teep` are **percent 0–100**, `numeric(10,3)`, and may exceed 100 (never clipped) |
| Counts | In the product's **base unit** (`uom_id` / `uom_code` on the row). A work center row is only filled when all members share one base unit |
| **NULL** | **Not known / not computable.** Render "—", **never 0**. 0 is a real measurement |
| `data_status` | `ok` · `no_binding` (no KPI binding: show "not configured") · `no_cycle_time` (no standard: Performance/OEE not computable) · `partial` (some products had no standard / no conversion, or units differ) |
| `business_date` | The date the shift **started**: 07:00 → 07:00 local. Shift 3 belongs to the day it started |
| `shift_no` / `shift_label` | Order of the shift in its business day (1, 2, 3) / `enterprise_shift.shift_label` |
| Time zone | All instants are UTC (`timestamptz`); show them in `time_zone` (`site.timezone`) |
| Work center | Every count and duration is Σ of its work units; **`good_out` comes only from the final work unit(s)** of `equipment_flow` (`final_work_unit_ids`); rates are recomputed, never averaged |
| `binding_ids` | Which `work_unit_kpi_binding` rows produced the numbers (for a "how was this calculated" panel) |
| Downtime | `primary_reason_id/name` (the Pareto groups on these), `reason_names text[]` (all, primary first), `is_edited`, `last_edited_by` |
| Reject count | `origin` = `sensor` (reject node) / `override` (moved to another reason) / `manual` (entered). `weight_gram` NULL = not weighed |

### 2.3 Freshness ("not loaded yet" vs "zero")

- `gold.etl_watermarks` (`table_name`, `last_run_at`, `last_business_date`) tells whether Airflow has reached a date.
- **An empty gold result for a date after `last_business_date` means "not loaded yet", not "nothing happened".**
- After a **gold** edit, show "recalculating" until `gold.etl_watermarks.last_run_at` of that table is later than the edit's `created_at`.

## 3. Master data CRUD (`silver.*`, ERD tables)

### 3.1 Universal rules (PRD-003 cross-cutting)

1. **Never DELETE.** Deactivate by setting `valid_to`, or `deleted_at` on reason tables. History must keep resolving.
2. **Versioned rows are never edited in place.** To change a value that calculations depend on, set `valid_to` on the current row and insert a new row with `valid_from`. A past report recomputed later then still uses the old value (that is how history is protected, instead of dbt snapshots). Applies to:
   - `asset_placement`, `work_unit_kpi_binding`
   - `operation`, `operation_work_unit` (cycle times)
   - `product_uom_conversion`, `product_detail`
   - `asset_product_codes`
   - `routing`, `bom` (versions)
   - `site_shift`, `fiscal_year`
   - `equipment_flow`, `work_unit_flow`
3. **Names and codes may be updated in place** (`*_name`, `*_code`). Gold keeps the name a row had when it was loaded.
4. **Validity is half-open:** a row is valid on date d if `valid_from <= d AND (valid_to IS NULL OR valid_to > d)`. One live row per key: no overlaps, no gaps where continuity is required (cycle time).
5. **Every master-data write also inserts one `silver.audit_log` row** in the same transaction: who (`actor_subject_id` = IDP subject), `action`, `entity_type`, `entity_id`, `changes` (before → after), a **required `reason`** for edit, deactivate and reactivate (PRD F-14).
6. IDs are `bigint`. Primary keys are `GENERATED BY DEFAULT AS IDENTITY`, so you may supply them or let the database assign them.

### 3.2 The master data the calculations depend on (get these right)

| What | Tables | Rule |
|---|---|---|
| Hierarchy | `site` (with IANA `timezone`) → `area` → `work_center` → `work_unit` | Every work unit has a work center |
| Machine on a work unit | `asset_placement` (`primary` / `secondary`) | At most one live `primary` per work unit |
| Signals | `data_source`, `asset_tags` | `node` must equal the Kafka `source_tag` exactly. `tag_role`, `kind`, `uom_id`, `count_basis`, `reject_reason_id` / `downtime_reason_id` decide how Flink reads it |
| **KPI binding** | `kpi_formula_slot` (seeded), `work_unit_kpi_binding` | One live row per work unit per slot, except `quality.reject` (one per reject node). The tag must belong to an asset placed on that work unit. `availability.downtime_reason` with `transform = stall_bucket` is what makes Flink detect stops on that tag |
| Line order | `equipment_flow` (`work_center_id`, `from_work_unit_id` → `to_work_unit_id`) | Needed for the work center's finished good. **A serial line without flow rows treats every machine as final (double counting)** |
| Shifts | `enterprise_shift`, `site_shift`, `shift_instance` | `shift_instance` rows must be **generated before the business day starts** (PRD SHF-002). A reading with no shift instance is rejected by Flink |
| Standards | `routing` (released, primary) → `operation` (`cycle_time_value`, `time_conversion_id`, `uom_id`, `batch_size`), optional `operation_work_unit` override | No standard = Performance not computable (`no_cycle_time` / `partial`) |
| Units | `unit_of_measurements`, `product_uom_conversion`, `product_detail.standard_weight` + `weight_uom_id` | Reject weights become units through `standard_weight`; counts become base units through conversions |
| Product codes | `asset_product_codes` (`tag_id`, `raw_value`, valid times) | The PLC product code → `product_id`. Unmapped = product unknown |
| Reasons | `downtime_reason` (category planned / unplanned / small_stop), `reject_reason` | Reason rows are created by users; a downtime reason's category can't change once used |

## 4. Editing (see [[editing_model]])

### 4.1 Windows

| When | Use |
|---|---|
| Shift open → 48 h after shift end | `silver.*` edit tables: visible **instantly** in live views |
| After 48 h | `gold.*` edit tables: visible in gold after the next Airflow run (§2.3) |
| Report approved | **Nothing**: reopen first (`fn_set_report_status(..., 'open', ...)`) |

The database enforces the windows. Use the error to tell the user where to go (§5).

### 4.2 Calls

All edit rows need `reason` (why, non-blank), `created_by` (IDP subject) and optionally `created_by_name`. Never UPDATE an edit row except to void it.

```sql
-- S1/G1 relabel a stop (order = priority, element 1 = primary). Category follows the primary reason.
INSERT INTO silver.downtime_edit (source_event_id, field, new_reason_ids, reason, created_by, created_by_name)
VALUES ('wu7-t101-1759213200000', 'reasons', '{3,1}', 'It was a break', 'kc-sub-123', 'Supervisor A');
-- S2/G1 change the category only
INSERT INTO silver.downtime_edit (source_event_id, field, new_category, reason, created_by)
VALUES ('wu7-t101-1759213200000', 'category', 'planned', 'Planned changeover', 'kc-sub-123');
-- S3/G1 text fields (new_detail_reason NULL = cleared on purpose)
INSERT INTO silver.downtime_edit (source_event_id, field, new_detail_reason, reason, created_by) VALUES (..., 'detail_reason', 'Film roll empty', '...', '...');
INSERT INTO silver.downtime_edit (source_event_id, field, new_actions, reason, created_by)       VALUES (..., 'actions', '{"Replaced roll"}', '...', '...');
INSERT INTO silver.downtime_edit (source_event_id, field, new_maintained_by, reason, created_by) VALUES (..., 'maintained_by', 'Tech B', '...', '...');
-- S4 manual stop inside one shift (silver only)
INSERT INTO silver.downtime_entry (work_unit_id, asset_id, shift_instance_id, category, reason_ids, start_at, end_at, reason, created_by)
VALUES (7, 1, 500, 'planned', '{3}', '2026-09-30 05:00+00', '2026-09-30 05:30+00', 'Lunch break', 'kc-sub-123');
-- S5/G2 manual reject (any unit with a conversion path; weight via product standard weight)
INSERT INTO silver.quality_entry (kind, work_unit_id, shift_instance_id, product_id, reject_reason_id, entered_qty, entered_uom_id, reason, created_by)
VALUES ('reject', 7, 500, 11, 1, 1.0, 2, 'Hand-sorted underweight', 'kc-sub-456');
-- S6/G2 rework
INSERT INTO silver.quality_entry (kind, work_unit_id, shift_instance_id, product_id, entered_qty, entered_uom_id, reason, created_by)
VALUES ('rework', 7, 500, 12, 20, 1, 'Re-sealed', 'kc-sub-456');
-- S7/G2 move quantity of a sensor reject node to another reason (qty in the node's unit)
INSERT INTO silver.quality_entry (kind, work_unit_id, shift_instance_id, product_id, reject_reason_id, entered_qty, entered_uom_id, override_tag_id, reason, created_by)
VALUES ('reject_override', 7, 500, 11, 2, 1.0, 2, 103, 'Misclassified', 'kc-sub-789');
-- Void (the only allowed UPDATE)
UPDATE silver.downtime_edit SET voided_at = now(), voided_by = 'kc-sub-123', void_reason = 'Wrong stop' WHERE edit_id = 42;
-- G3 approve / reopen (work unit × shift)
SELECT gold.fn_set_report_status(7, 500, 'approved', 'kc-sub-mgr', 'Manager', 'Reviewed');
SELECT gold.fn_set_report_status(7, 500, 'open',     'kc-sub-mgr', 'Manager', 'Late data');
```

For gold edits, use the same statements against `gold.downtime_edit` / `gold.quality_entry` (no `downtime_entry` in gold).

- **History panel:** `silver/gold.downtime_edit` rows (old_* → new_*, who, when, reason, voided) and `silver.audit_log`.
- **Effective values:** `silver.v_downtime_effective`, `silver.v_quality_entry_effective`.

### 4.3 What is NOT editable

- Sensor total / good counts.
- The start / end of a machine stop.
- Facts tables (`production_events`, `reject_events`, `downtime_events`).
- Gold report tables (Airflow rebuilds them).
- Edit rows (void only).
- Approval rows (use the function).
- Audit rows.

## 5. Errors the database raises (map to HTTP 409 / 422)

| Message starts with | Meaning | Suggested response |
|---|---|---|
| `shift … ended more than 48 h ago: edit it in gold` | Silver window passed | 409, route to the report edit |
| `shift … is still in the shopfloor window (48 h): edit it in silver` | Gold too early | 409, route to the shopfloor edit |
| `report (work unit …, shift …) is approved: reopen it before editing` | Frozen | 409 |
| `report … is already approved` / `… already open` | No-op status change | 409 |
| `… is append-only: void the row …` / `… an update must void the row …` / `… only voided_at …` | Illegal UPDATE / DELETE | 400 (app bug) |
| `… is an immutable trail` | Update of approval events | 400 (app bug) |
| `unknown stop …` / `unknown shift_instance_id …` | Bad reference | 404 |
| `manual stop must lie inside shift …` | Entry outside its shift | 422 |
| `override_tag_id … must be a reject node whose unit is the entered unit` | Bad override | 422 |
| CHECK violation on `reason` | Blank reason | 422 |

## 6. Never

- Never write machine facts or gold report tables.
- Never UPDATE / DELETE edit, entry, approval-event or audit rows (void instead).
- Never compute OEE in the API or frontend. Read it. **Never average rates** across rows: a different period or scope has its own row (day / month / year / work center).
- Never show NULL as 0.
- Never edit a versioned master row in place (close + insert).
