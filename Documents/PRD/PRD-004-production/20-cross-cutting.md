# PRD-004 · Cross-story concerns

> Part of [PRD-004 PRODUCTION](README.md). What applies to more than one story: screens, entities, access,
> offline, non-functional requirements and instrumentation.

## 1. Screen map (for Design)

| Screen | Module / feature | Stories | Device | Network |
|--------|------------------|---------|--------|---------|
| Job Order list + form | PLN / Job Order | `PLN-001`, `PLN-006` | Desktop | Stable |
| Upload schedule (template, preview, summary) | PLN / Job Order | `PLN-002` | Desktop | Stable |
| Ready to release | PLN / Job Order | `PLN-003` | Desktop | Stable |
| Today (schedule by work unit, achievement) | PLN / Job Order | `PLN-004`, `PLN-006`, `PLN-007` | Tablet / phone / desktop | Unstable |
| Work unit queue | PLN / Job Order | `PLN-005` | Floor device (SF-4) | Unstable |
| Breaks (form, table, timeline) | PLN / Breaktime | `PLN-008`, `PLN-009` | Desktop | Blocked |
| Readiness checklist | PLN / Execution | `PLN-010` | Floor device | Blocked |
| Planning log | PLN / log | `PLN-011` | Desktop | Stable |
| Monitoring dashboard + timeline | MON / Dashboard | `MON-001`, `MON-002` | Tablet / phone | Unstable |
| Live board | MON / Live Monitoring | `MON-003` | Wall screen / tablet | Blocked |
| Losses list + correct reason + add rework | MON / Losses List | `MON-004`, `MON-005`, `MON-006` | Tablet / phone | Unstable |
| Monitoring log | MON / log | `MON-007` | Desktop | Stable |
| Report pages — Live (Overview / Availability / Performance / Quality) | PRF / Report pages, Live view | `PRF-001` | Tablet / wall screen | Unstable |
| Report pages — Period (same four tabs) | PRF / Report pages, Period view | `PRF-002`, `PRF-005`, `PRF-006` | Desktop | Stable |
| Management rollups, KPI evaluation | PRF / KPI for Management, Performance | `PRF-003`, `PRF-004` | Desktop | Stable |
| Performance audit log | PRF / log | `PRF-007` | Desktop | Stable |

**Rules for every screen:** "no data" is never shown as 0%; values above 100% are never cut; while a shift is open,
KPI values carry a *provisional* label; every screen shows when its data was last updated.

## 2. Entity summary (for Engineering)

> **Keys on every table (PO, 2026-10-01, [F-00 keys rule](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#keys-and-constraints-on-every-table-po-2026-10-01)).** Every entity in the specs has one surrogate
> primary key and a **Keys** line under its field table: **PK** · **Unique** (unique index, partial when filtered) ·
> **No overlap** (one active row at a time over `valid_from`/`valid_to`). Build from that line; it is what change data
> capture (Debezium) needs — PostgreSQL refuses `UPDATE`/`DELETE` on a published table without a primary key. The key
> is never updated, and no stored column holds a list. How "No overlap" is enforced (exclusion constraint or
> application check) is open for engineering: **E-1**.

| Entity | Owner | Read by | Written by |
|--------|-------|---------|------------|
| `WORK_ORDER` | PRODUCTION | PLN, MON, PRF | `PLN-001`, `002`, `003` (incl. stage-order drafts), `006` |
| `WORK_ORDER_OPERATION` | PRODUCTION | PLN, MON, PRF | `PLN-001`, `003`, `006`; quantities by the sensor path (M-01) |
| `WORK_ORDER_OPERATION_DEFECT` | PRODUCTION | MON, PRF | Sensor path; `MON-005` (manual override); `MON-006` (manual override, or `manual` when no reject tag is bound) |
| `WORK_ORDER_OPERATION_LOT` | PRODUCTION | — (Product Tracing deferred) | — |
| `KPI_RESULT` | FOUNDATION (freshness fields: PRODUCTION) | MON, PRF | FOUNDATION KPI job |
| `PRODUCT`, `PRODUCT_FLOW`, `ROUTING`, `OPERATION`, `OPERATION_WORK_UNIT`, `BOM`, `BOM_LINE`, `REJECT_REASON`, `SHIFT_INSTANCE`, `WORK_UNIT` | FOUNDATION | PLN, MON | — |
| `AUDIT_LOG` | FOUNDATION (F-14, shared by every domain) | `PLN-011`, `MON-007`, `PRF-007` | Every write in PLN / MON (through `US-FND-AUD-001`) |
| `ASSET_STATE_LOG` | **PRODUCTION** (Monitoring, since 2026-09-28, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md); [spec](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time) B5 — SL-1 resolved 2026-09-29; state field name `TBD` (row ID `asset_state_log_id`)) | MON, PRF; MAINTENANCE (MTBF / MTTR / MTTF, read only); FOUNDATION KPI job (APT) | PRODUCTION — data-platform Silver job (`plc`, `derived`), MES app (`manual` rows and reasons; a recompute never overwrites an operator's reason) |

**Entities this PRD needs that don't exist** (not invented; each is an open item):

| Needed for | Open item | Proposed owner |
|------------|-----------|----------------|
| Queue position per work unit | P-01 | `production-domain` |
| Notes on a Job Order | P-03 | `production-domain` |
| Break windows | P-06 | `production-domain` (+ F-02 decision) |
| Readiness checks | P-07 | `production-domain` |
| KPI Selection per domain | R-01 | `foundation-domain` |
| KPI targets | R-02 | `foundation-domain` |
| Report status (approve / override / reset) | R-03 | `production-domain` |

## 3. Access control

Same model as PRD-003: **scope and action, resolved per resource** through the
[ABAC contract](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp).
Role names below are labels, not a contract. Each story's **Rules & constraints** line is what is enforced.

| Role label | May | May not |
|------------|-----|---------|
| `operator` (work unit) | See its queue and readiness checks | Change the schedule, correct losses |
| `supervisor` (area / work center) | Everything in PLN for its scope (until `planner` exists), release, correct loss reasons, add rework, see all monitoring and live OEE | Change master data, approve reports |
| `planner` ⚠ (not an IDP role yet, P-09) | Create, upload, change Job Orders | Release (`[proposed]`, P-04), correct losses |
| `plant_manager` (site) | All PRF views, downloads, audit log; approve / override / reset reports once R-03 exists | Change the schedule |

**Rules on every screen:** items outside scope are never listed; a direct link outside scope explains who to ask;
every refusal is recorded as `access_denied`.

## 4. Offline behaviour

The floor rule from the specs holds: **never discard what was recorded, and never show a blank screen.**

| Action | Offline |
|--------|---------|
| Create, upload, release, change a Job Order | **Unavailable** — say so |
| Today view, queue, dashboard, live OEE, losses list | **Last loaded data**, with an offline marker and its time |
| Correct a reason, add rework | **Unavailable** `[proposed]` — say so |
| Reports, logs, downloads | **Unavailable** |

## 5. Non-functional requirements

Numbers marked `[proposed]` are not decided by the PO; while any remain, this PRD can't become `v1.0`.

| Category | Requirement | Verification |
|----------|-------------|--------------|
| Performance | Today view, queue, dashboard and live OEE render in **≤ 2.5 s (p95)** on 3G | Throttled synthetic test with one full shift of data |
| Performance | Upload preview of 500 rows in **`[proposed]` ≤ 10 s** | Test file with 500 rows |
| Performance | One Period-view tab for 90 days × 1 site in **`[proposed]` ≤ 3 s (p95)** | Seeded data |
| Freshness | Live screens refresh at least every **`[proposed]` 60 s**; KPI values follow FOUNDATION's 5-minute refresh | Timestamps on screen vs source |
| Freshness | A release or change shows on the work unit queue within **`[proposed]` 60 s** online | Release, then time until visible |
| Capacity | **`[proposed]` 40 Job Orders per site per day**, 20 work units | Load test |
| Integrity | 0 changes without a log row; log rows can't be edited or deleted through UI or API | Attempt edit / delete per log type |
| Security | Data only visible within the user's IDP scope; **0** cross-scope leaks | Cross-scope tests per role |
| Physical | Floor touch targets ≥ 44 × 44 px; text contrast ≥ 4.5 : 1 | Design audit + glove test |
| Language | UI terms match [01-glossary](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/01-glossary.md) and ISO 22400 (KR 2.2); Figma "MTBR" shown as MTTR | Terminology audit |

## 6. Migration

No production history is migrated: today's plans live on whiteboards and spreadsheets. Consequence: OEE and
achievement baselines start at go-live, and targets are not locked before 30 days of MolcaDx data. Open orders at
go-live are entered through `PLN-002` (upload).

## 7. Instrumentation (for `data-domain`)

| Event | Properties | Story |
|-------|------------|-------|
| `job_order_created` | `work_order_id`, `product_id`, `product_flow_id`, `planned_qty`, `operation_count`, `via` | `PLN-001`, `002` |
| `job_order_edited` | `work_order_id`, `fields_changed` | `PLN-001` |
| `schedule_uploaded` / `schedule_upload_confirmed` | `row_count`, `valid_count`, `file_type` / `created_count`, `skipped_count` | `PLN-002` |
| `job_order_released` / `job_order_release_blocked` | `work_order_id`, `was_late`, `bulk`, `stage_drafts_created` / `missing` | `PLN-003` |
| `schedule_today_opened` | `site_id`, `shift_instance_id`, `job_count`, `behind_count` | `PLN-004` |
| `job_queue_opened` | `work_unit_id`, `queue_length`, `is_offline` | `PLN-005` |
| `job_order_changed` | `work_order_id`, `action`, `was_in_progress`, `moved_qty` | `PLN-006` |
| `schedule_downloaded` | `date_from`, `date_to`, `row_count` | `PLN-007` |
| `planning_log_opened` / `_downloaded` | `filters` / `row_count` | `PLN-011` |
| `monitoring_dashboard_opened` | `scope_level`, `scope_id`, `shift_instance_id` | `MON-001` |
| `monitoring_timeline_opened` | `scope_id`, `shift_instance_id` | `MON-002` |
| `losses_list_opened` | `scope_id`, `open_count` | `MON-004` |
| `loss_reason_corrected` | `wo_operation_defect_id`, `from_reason`, `to_reason`, `from_disposition`, `to_disposition`, `after_close` | `MON-005` |
| `rework_added` | `wo_operation_id`, `qty`, `uom_id`, `defect_code_id`, `origin`, `after_close` | `MON-006` |
| `monitoring_log_downloaded` | `row_count` | `MON-007` |
| `oee_live_opened` / `oee_breakdown_opened` | `scope_id`, `shift_instance_id` / `work_unit_id`, `metric` | `PRF-001` |
| `oee_report_run` / `oee_report_downloaded` | `scope_level`, `date_from`, `date_to` / `row_count` | `PRF-002` |
| `oee_management_opened` | `period_type` | `PRF-003` |
| `access_denied` | `attempted_scope`, `claim_scope_held` | all |

Event conventions (`client_ts`, `server_ts`, `is_offline_sync`) follow
[04-metrics-framework §4](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/04-metrics-framework.md#4-konvensi-event).
