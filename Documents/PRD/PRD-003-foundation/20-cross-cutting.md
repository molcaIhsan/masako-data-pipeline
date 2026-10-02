# PRD-003 · Cross-story concerns

> Part of [PRD-003 FOUNDATION](README.md). Screen map, entity summary, access, offline, NFRs, migration and instrumentation — everything that spans more than one module.


### 7.1 Screen map (for Design)

**Device context — two different worlds that must not be conflated:**

| Screen group | Device | Conditions | Design consequence |
|--------------|--------|------------|--------------------|
| **Shopfloor** (`PROD-SHF`) | `TBD` — shared tablet / phone / fixed terminal ([SF-4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#boundaries)); the design must withstand all three | Standing, working simultaneously, gloved/dirty hands, variable lighting, intermittent network | Touch targets ≥44×44 px, high contrast, primary action within one thumb, minimal typing, **every screen must have an offline state** |
| **Monitoring** (`PROD-OEE`) | Small screen (supervisor, walking) **and** desktop (manager) | A few seconds of reading time | 3-second priority: **status** before numbers; every number needs a comparator |
| **Planning** (`PROD-JOB`) | Desktop | Sessions of minutes–hours, stable network | May be information-dense; needs impact before confirmation |
| **Master data & platform** (all `FND-*`) | Desktop | Long sessions (hours), technical implementor, stable network | May be information-dense provided **the origin of each value and its impact are clear**; consecutive entries must be fast (form ready for the next one) |
| Reference & product master data (`REF`, `PRO`) | Desktop | Long onboarding sessions, stable network, hundreds of rows entered in sequence | Table-first layouts; the form stays filled and ready for the next row after each save; forms never lose input on a failed save; **no offline mode** — master data needs cross-user uniqueness checks |
| Operator reason picker (`US-FND-REF-003`) | Handheld, small screen | Standing at a stopped line, possibly gloved, poor network, every second counts | Touch targets ≥44×44 px; list pre-filtered to that asset and ordered by 30-day usage; **works offline up to 8 hours**; an asset with no mapping shows the full list, never an empty one |
| A/P/Q/OEE summary and loss waterfall (`KPI`) | Handheld | Walking the shop floor, one handed, unstable network | Headline figure first with a provisional/final label always visible; POT, PBT, and APT shown beneath so a stoppage-heavy line reads differently from a short-hours line; waterfall scrolls horizontally in its own container |
| Line reliability, MTTR/MTBF (`US-FND-KPI-005`) | Desktop | Weekly or monthly analysis, not per shift | The occurrence count sits beside every average, because a mean of 3 and a mean of 30 must not look alike; the basis is stated as unplanned production stops, not ISO TBF |

| Screen | Purpose | Stories served | Entry point | Exit point |
|--------|---------|----------------|-------------|------------|
| Location structure — Tree / Diagram / Table (`SCR-FND-SIT-001`, `002`) | Build & maintain the location hierarchy via ⋮ dialogs; Shift panel on `SITE`; Fiscal Year panel on `ENTERPRISE` (declare) / `SITE` (fully read-only, no override) — `SCR-FND-FSC-001` retired into that panel | `US-FND-SIT-001`, `US-FND-SIT-002`, `US-FND-SHF-001` (site hours), `US-FND-FYR-001` | Sidebar, shared items (no heading) | Back to menu; on to asset registration |
| ~~Context picker~~ (`SCR-FND-SIT-003`, retired 2026-09-27) | **Not in FOUNDATION** — no FOUNDATION app shows a context picker; a picker in the Production apps is parked ([docs-molcadx#46](https://github.com/molca-id/docs-molcadx/issues/46)) | ~~`US-FND-SIT-003`~~ retired | — | — |
| Asset Registry (`SCR-FND-AST-001`) | Equipment list and detail with class/type badges (read only — the breadcrumb opens `SCR-FND-AST-007`, which links on to the Reference class/type managers); asset register, placement history | `US-FND-AST-002`, `US-FND-AST-003` (`US-FND-AST-001` via badge links) | Master data menu; link from location structure | Back to menu |
| ~~Component installation history~~ (`SCR-FND-AST-006`) | **Not in FOUNDATION** — lives in the Maintenance domain as `SCR-MNT-AST-001`, see [`MAINTENANCE/UX/01-asset-history.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/MAINTENANCE/UX/01-asset-history.md) | ~~`US-FND-AST-005`~~ retired | — | — |
| ~~Reason code taxonomy~~ (`SCR-FND-AST-004`, retired 2026-09-23) | Replaced by the Reference downtime/reject reason managers (`SCR-FND-REF-003`/`004`); per-machine reasons on the asset form (`SCR-FND-AST-007`, `US-FND-REF-003`) | — | — | — |
| ~~Items & UOM~~ (`SCR-FND-WMS-001`, retired 2026-09-28) | **Retired** (PO, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) addendum) — duplicated the Product screens; products and their units are managed on `SCR-FND-PRO-*`. ID not reused | ~~`US-FND-WMS-001`~~ closed 2026-08-28 | — | — |
| ~~State log browser~~ (`SCR-FND-AST-005`, retired 2026-09-28) | **Not in FOUNDATION** — `ASSET_STATE_LOG` is PRODUCTION's ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)); replaced by `SCR-PROD-MON-001` in [PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time). ID not reused | — (PRD-004 Monitoring) | — | — |
| BOM tab (`SCR-FND-WMS-002`) | Build versioned component structures, shown and edited as a tree (2026-09-30) | `US-FND-WMS-002` | Product detail page, **BOM tab** (2026-09-30; no longer a sidebar page); hidden without a Production license | Deeper tree levels → that product's BOM tab |
| Routing & operation editor (`SCR-FND-WMS-003`) | Manage process sequence & versioned cycle time — value + time unit + item unit ("20 seconds / pack"); save blocked without a conversion path (2026-09-28) | `US-FND-WMS-003` | Product detail page, **Routing tab** (2026-09-30; no longer a sidebar page); gap badges live in the product list's Routing column; hidden without a Production license | Neighbouring BOM tab; product Conversions tab (`SCR-FND-PRO-003`) to add a missing conversion |
| Work order | Create & release WOs | `US-PROD-JOB-005` (PRODUCTION story, in PRD-001) | Planning menu | On to the schedule board |
| ~~Defect code taxonomy~~ (`SCR-FND-WMS-005`, retired 2026-09-23) | Merged into the Reject reason manager (`SCR-FND-REF-004`) | `US-FND-REF-002` | — | — |
| Enterprise shift labels (`SCR-FND-SHF-001`) | Declare shift labels once; edit in place | `US-FND-SHF-001` | Sidebar, shared items (no heading) | On to each site's Shift panel |
| ~~Working calendar~~ | Retired with `US-FND-SHF-003` — no screen | — | — | — |
| Shift instance monitor (`SCR-FND-SHF-003`) | Generate real shifts per site, view the horizon, surface gaps/duplicates | `US-FND-SHF-002` | Sidebar, shared items (no heading) | Back to menu |
| Crew-to-shift assignment | **Retired 2026-09-25 (PO)** — not needed for now; `SCR-FND-SHF-004` retired in UX 02 | ~~`US-FND-SHF-004`~~ RETIRED | — | `TBD` pending what "crew" means now |
| Flow designer | Build low-code workflows | `US-FND-WFE-001`, `US-FND-WFE-002`, `US-FND-WFE-003` | Platform menu | Back to the flow list |
| My tasks & instance trail | Work tasks, diagnose flows | `US-FND-WFE-004` | Notification; menu | Back to the task list |
| Notification rules | Configure trigger → role → channel | `US-FND-NTF-001` | Platform menu | Back to list |
| Notification centre | Read, acknowledge, view groups | `US-FND-NTF-002`, `US-FND-NTF-003` | Notification icon (global) | Back to the originating screen |
| Data sources (`SCR-FND-DSR-001`) + Tags (`SCR-FND-DSR-002`, 2026-09-28) (`DATA_SOURCE` / `ASSET_TAGS`) | Declare machine data sources; list, declare and export every tag in the site across sources | `US-FND-DSR-001` | Sidebar: Connections › Data sources / Tags; Tags also from the asset form and the KPI side panel | On to service monitoring (ownership TBD — DSR) |
| Sync queue (component) | Offline marker & awaiting count — **ownership TBD (PRODUCTION vs INT)** | `US-FND-INT-002` | Header of shopfloor screens | — |
| Service Monitoring | Monitor data acquisition health — **ownership TBD (DSR); blocked H-DS-1** | `US-FND-INT-003` | Platform menu | Back to menu |
| Configuration | Tiered values, value origin, history, feature flags | `US-FND-CFG-001`, `US-FND-CFG-002`, `US-FND-CFG-003` | Platform menu | Back to menu |
| UOM manager (`SCR-FND-REF-001`) | Maintain units — own nav entry | `US-FND-REF-004`, `US-FND-REF-006` | Sidebar, shared items (no heading) | Back to menu |
| Time conversion manager (`SCR-FND-REF-002`) | Declare named durations via quantity + base unit | `US-FND-REF-005` | Sidebar, shared items (no heading) | Back to menu |
| Downtime reason manager (`SCR-FND-REF-003`) | Maintain the downtime taxonomy — own nav entry | `US-FND-REF-001`, `US-FND-REF-006` | Sidebar, shared items (no heading) | Back to menu |
| Reject reason manager (`SCR-FND-REF-004`) | Maintain the reject taxonomy — own nav entry | `US-FND-REF-002`, `US-FND-REF-006` | Sidebar, shared items (no heading) | Back to menu |
| **Reference data sidebar** (`SCR-FND-REF-001`–`010`) | One sidebar group for every flat pick-list; each list edited only here, shown read-only elsewhere ([UX 05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md)) | see [05-reference-master-data.md](05-reference-master-data.md#reference-data-sidebar) | Sidebar | — |
| Site / Area / Work center / Work unit class managers (`SCR-FND-REF-005`–`008`) | Maintain the four flat location class lists — own nav entries | `US-FND-SIT-004` | Sidebar, shared items (no heading) | Class picker in the node dialogs (`US-FND-SIT-001`) |
| Asset class manager (`SCR-FND-REF-009`) · Asset type manager (`SCR-FND-REF-010`) | Create/edit the asset class tree and the types under it — moved from `SCR-FND-AST-007`, 2026-09-23 | `US-FND-AST-001` | Sidebar, shared items (no heading) | Asset registration (`SCR-FND-AST-007`) |
| ~~Product cycle times~~ (`SCR-FND-PRO-002`, retired 2026-09-28) | **Retired** with `PRODUCT_CYCLE` — cycle time is entered only on the routing & operation editor (`SCR-FND-WMS-003`). Product detail tabs are now Measurement / Conversions / History | ~~`US-FND-PRO-004`~~ retired | — | — |
| **App sidebar, grouped by domain** (app shell, 2026-09-28) | Shared FOUNDATION items at the top with **no heading** (site, shift, fiscal, asset, Reference data and Connections with their own group headings, product) + one group per MOM domain, pages listed by what they are: **Production** KPI (BOM and Routing are Product detail tabs since 2026-09-30); **Maintenance** PM checklist · KPI · Asset history; **Quality** KPI; **Inventory** KPI. No "Work Master" item. An unlicensed domain shows as one locked heading, items not listed. Runtime logs (e.g. the state log) are not in this sidebar ([UX 00 § App shell](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md#app-shell--sidebar-grouped-by-domain-po-decision-2026-09-28)) | `US-FND-KPI-007` (KPI item, locked heading), `US-FND-WMS-002` (Production › BOM), `US-FND-WMS-003` (Production › Routing). PM checklist and Asset history: MAINTENANCE, no PRD | — | Each item's screen; a locked heading opens the upsell surface |

**Information priority — what must be readable in the first 3 seconds:**

| Screen | First 3 seconds |
|--------|-----------------|
| Running job (operator) | Which job, target vs actual, primary action (record output / line stopped) |
| OEE line view | **Status** (good / needs attention) — before the numbers |
| Plant view | The top line needing attention |
| Schedule board | Which shift is overloaded; carry-over at the very top |
| Close shift | Whether it can be closed, and if not, why |
| Master data screens | How much is filled in and what is still missing for the line to run |
| Service Monitoring | Whether any data source has stopped |
| Configuration | The effective value and **its origin level** |

### 7.2 Entity summary (for Engineering)

> **Keys on every table (PO, 2026-10-01, [F-00 keys rule](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#keys-and-constraints-on-every-table-po-2026-10-01)).** Every entity in the specs has one surrogate
> primary key and a **Keys** line under its field table: **PK** · **Unique** (unique index, partial when filtered) ·
> **No overlap** (one active row at a time over `valid_from`/`valid_to`). Build from that line; it is what change data
> capture (Debezium) needs — PostgreSQL refuses `UPDATE`/`DELETE` on a published table without a primary key. The key
> is never updated, and no stored column holds a list. How "No overlap" is enforced (exclusion constraint or
> application check) is open for engineering: **E-1**.

> **⚠ = proposed, not modelled.** Rows marked ⚠ belong to the concept-only modules (`WFE`, `NTF`, `INT`, `CFG`).
> Their entities are PRD proposals; `foundation-domain` has not modelled them yet
> ([F-Q-03](30-release-risks-questions.md#11-open-questions)). Do not build against them.

| Entity | Read by | Written by | New fields required |
|--------|---------|------------|---------------------|
| `ENTERPRISE`, `SITE`, `AREA`, `WORK_CENTER`, `WORK_UNIT` | nearly all | `US-FND-SIT-001`, `US-FND-SIT-002` | `—` |
| `ASSET_CLASS`, `ASSET_TYPE` | `US-FND-AST-002` | `US-FND-AST-001` | `—` |
| `ASSET` | `US-PROD-SHF-003/004`, `US-PROD-OEE-*`, `US-FND-DSR-001` | `US-FND-AST-002` | `—` |
| `ASSET_PLACEMENT` | `US-PROD-OEE-003` | `US-FND-AST-003` | `—` |
| `ASSET_STATE_LOG` — **PRODUCTION-owned** (2026-09-28, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md), [spec](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time); only writer PRODUCTION, MAINTENANCE reads) | `US-PROD-OEE-001/003/005`, `US-PROD-SHF-007` | `US-PROD-SHF-003`, `US-PROD-SHF-004`, `US-PROD-SHF-008` (corrections); **not** `US-FND-DSR-001` (F-13 does not store readings) | **`is_micro_stop`** (bool, derived from the threshold) — to be requested from `foundation-domain`; **an `adjustment` flag + `original_entry_id` + `delta` + `reason`** for corrections ([§11 Q-11](30-release-risks-questions.md)) |
| `DOWNTIME_REASON` / `REJECT_REASON` (`TBD` after the split — see the note under `US-FND-AST-004`/`US-PROD-SHF-004`) | `US-PROD-SHF-004`, `US-PROD-OEE-001/003` | `US-FND-AST-004` | The tiering (`parent_reason_code_id`) referenced in this row **does not exist** in the current `DOWNTIME_REASON`/`REJECT_REASON` shape ([F-01 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#91-downtime_reason-and-reject_reason)) — whether a hierarchy is still needed is `TBD`, to be requested from `foundation-domain`/`ops-domain` |
| `PRODUCT`, `UNIT_OF_MEASUREMENTS` | `US-FND-WMS-002/003`, `US-PROD-JOB-005`, `US-PROD-SHF-002/005` | `US-FND-PRO-001` (`PRODUCT`), `US-FND-REF-004` (`UNIT_OF_MEASUREMENTS`) — not `US-FND-WMS-001` (closed; its screen `SCR-FND-WMS-001` retired 2026-09-28) | `—` |
| `BOM`, `BOM_LINE` | `US-PROD-JOB-005` (PRODUCTION story, in PRD-001) | `US-FND-WMS-002` | `—` |
| `ROUTING`, `OPERATION` | `US-PROD-OEE-001/003`, `US-PROD-JOB-001/004` | `US-FND-WMS-003` | `—` |
| `PRODUCT_FLOW` | `US-FND-WMS-002`, `US-FND-WMS-003` | no story yet (flow bar, add flow) | `—` |
| `OPERATION_WORK_UNIT`, `OPERATION_DEPENDENCY` | `US-FND-KPI-002` (station override) | `US-FND-WMS-003` | `—` |
| `OPERATION_PARAMETER` | — | no story yet | `—` |
| `WORK_ORDER` | `US-PROD-JOB-*`, `US-PROD-SHF-002` | `US-PROD-JOB-005` (PRODUCTION story, in PRD-001) | `—` |
| `WORK_ORDER_OPERATION` | `US-PROD-OEE-*`, `US-PROD-SHF-007` | `US-PROD-SHF-002/005/008`, `US-PROD-JOB-001/003/004` (**not** `US-FND-DSR-001` — readings are PRODUCTION) | **`carry_over_from_shift_id`**, **`carry_over_count`**, **reschedule history** (`reschedule_reason`, `was_in_progress`, actor, time), **an `adjustment` flag** for corrections — to be requested from `foundation-domain` ([§11 Q-11](30-release-risks-questions.md)) |
| ~~`QUALITY_DEFECT_CODE`~~ | — | — | **Retired 2026-09-23**, merged into `REJECT_REASON` ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-23-defect-code-merged-into-reject-reason.md)); readers `US-PROD-SHF-005`, `US-PROD-OEE-003` now read `REJECT_REASON` |
| `ENTERPRISE_SHIFT`, `SITE_SHIFT` (replace the retired `SHIFT_DEFINITION`/`SHIFT_PATTERN`) | `US-FND-SHF-002`, `US-FND-SIT-001` (missing-shift badge) | `US-FND-SHF-001` | `—` |
| ~~`SHIFT_CALENDAR`, `NON_WORKING_DAY`~~ | retired — nothing reads them | `US-FND-SHF-003` (closed) | `—` |
| `SHIFT_INSTANCE` | nearly every production story | `US-FND-SHF-002`, `US-PROD-SHF-001` (→`open`), `US-PROD-SHF-007` (→`closed`) | `—` |
| `SHIFT_ASSIGNMENT` | `US-PROD-SHF-001` | ~~`US-FND-SHF-004`~~ **retired 2026-09-25 — nothing in PRD-003 writes it** | `—` |
| ⚠ `WORKFLOW_DEFINITION`, `WORKFLOW_VERSION`, `WORKFLOW_INSTANCE`, `WORKFLOW_TASK`, `WORKFLOW_AUDIT_LOG`, `WORKFLOW_SIMULATION_RUN` | `US-FND-WFE-*` | `US-FND-WFE-*` | ⚠ **Proposed, not modelled** — every entity is missing; a PRD proposal; must be modelled by `foundation-domain` first |
| ⚠ `NOTIFICATION_RULE`, `NOTIFICATION`, `NOTIFICATION_DELIVERY`, `NOTIFICATION_OCCURRENCE` | `US-FND-NTF-*`, `US-PROD-SHF-006` | `US-FND-NTF-*` | ⚠ **Proposed, not modelled** — every entity is missing; a PRD proposal |
| ⚠ `SYNC_QUEUE_ENTRY`, `SYNC_RECEIPT`, `CONNECTOR_HEALTH` | `US-FND-INT-002`/`003` (ownership TBD) | same | ⚠ **Proposed, not modelled**; machine tag mapping is **not** here — see `DATA_SOURCE` / `ASSET_TAGS` under `US-FND-DSR-001` |
| `DATA_SOURCE`, `ASSET_TAGS` | `US-FND-DSR-001`, `US-FND-INT-003` (read) | `US-FND-DSR-001` | `site_id` required on `DATA_SOURCE`; `last_seen_at` placeholder only (H-DS-1); no `scale`/`offset` |
| ⚠ `CONFIG_KEY`, `CONFIG_VALUE`, `CONFIG_CHANGE_LOG`, `FEATURE_FLAG` | every story that uses a threshold | `US-FND-CFG-*` | ⚠ **Proposed, not modelled** — every entity is missing; a PRD proposal |
| `DOWNTIME_REASON`, `REJECT_REASON` | `US-FND-REF-001/002/003`, `US-PROD-SHF-004`, `US-FND-KPI-001/005` | `US-FND-REF-001/002/006` | none |
| `DOWNTIME_REASON_ASSET`, `REJECT_REASON_ASSET` | `US-FND-REF-003`, `US-PROD-SHF-004` | `US-FND-REF-003` | none — scoping is per asset instance, never per `ASSET_CLASS` |
| `UNIT_OF_MEASUREMENTS`, `MEASUREMENT_GROUP` | `US-FND-REF-004`, `US-FND-PRO-001/003`, `US-FND-WMS-003` (`OPERATION.uom_id` = item unit) | `US-FND-REF-004/006` | none — the dimension marker ([Q-16](30-release-risks-questions.md), resolved) is `measurement_group_id`, already in F-05 §9.4 |
| `TIME_CONVERSIONS` | `US-FND-REF-005`, `OPERATION` resolution (`US-FND-WMS-003`, `US-FND-KPI-002`) | `US-FND-REF-005` | none — `seconds_per_unit` already exists ([Q-17](30-release-risks-questions.md) closed) |
| `PRODUCT` | `US-FND-PRO-002/003`, `US-FND-KPI-002`, `US-FND-WMS-003` (`base_uom_id` for the save rule), plus `ROUTING`, `OPERATION`, `ASSET_PRODUCT_CODES` | `US-FND-PRO-001` | none — this is the entity Work Master used to call `ITEM` |
| `PRODUCT_DETAIL` | `US-FND-PRO-002`, `US-FND-KPI-003`, `US-FND-WMS-003` | `US-FND-PRO-002` | none — `weight_uom_id` added 2026-10-01 (Q-20 resolved) |
| `PRODUCT_UOM_CONVERSION` | `US-FND-PRO-003`, `US-FND-KPI-002` (counter → `OPERATION.uom_id`, 2026-09-28), `US-FND-KPI-008` (conversion rule shown, 2026-09-28), `US-FND-WMS-003` (item-unit picker and save rule). **Not** `US-FND-KPI-003`/`009`: Quality never converts units (2026-09-28) | `US-FND-PRO-003` | none |
| ~~`PRODUCT_CYCLE`~~ | **Retired 2026-09-28** — no reader, no writer (`US-FND-PRO-004` retired). Cycle time lives only on `OPERATION`. Existing rows are closed with `valid_to`, not deleted | — | — |
| `ASSET_STATE_LOG` (PRODUCTION-owned, [spec](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)) | `US-FND-KPI-001/004/005/012`, `US-FND-SIT-002`, `US-FND-REF-001/003/006`, `US-PROD-OEE-001` | PRODUCTION only; `US-PROD-SHF-003/004`; `US-FND-KPI-006` detects stalls, written through PRODUCTION as `source = derived` | none from FOUNDATION — the origin marker is PRODUCTION's `source` field (`plc`/`manual`/`derived`), resolved 2026-09-28; state field names are a PRODUCTION `TBD` (row ID `asset_state_log_id` since 2026-10-01; recording level SL-1 settled 2026-09-29) |
| `KPI_FORMULA_SLOT`, `KPI_PARAMETERS` | `US-FND-KPI-001` to `006`, `009` to `012` (read-only, resolved by the calc job), `US-FND-KPI-008` (read-only, displayed) | seeded by `foundation-domain`/Product, never implementor-writable (K-1) — includes two non-bindable rows, `throughput.order_execution_time` and `rework.output`, per [F-07.1 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#91-kpi_formula_slot) | none |
| `WORK_UNIT_KPI_BINDING` | `US-FND-KPI-001/002/003/006/009`, `US-FND-KPI-008` (read-only, displayed) | ordinary site-scoped admin edit (K-7, resolved) | none — `US-FND-KPI-010/011/012` read no `WORK_UNIT_KPI_BINDING` row at all, their inputs resolve directly off `WORK_ORDER_OPERATION` through the two non-bindable slots above |
| `WORK_ORDER_OPERATION` (PRODUCTION) | `US-FND-KPI-002/003`, `US-FND-KPI-010/011/012` (`rework_qty`, `actual_start`, `actual_end`) | `US-PROD-SHF-001` and PRODUCTION's own job-schedule stories, not this PRD | none — FOUNDATION reads this PRODUCTION entity, never writes or redefines it |
| `KPI_RESULT` | every KPI screen | `US-FND-KPI-001` to `006`, `009` to `012` | `computed_at`/`is_stale` owned by `production-domain`; the rest per [F-07.1 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#95-kpi_result); `metric` enum includes `scrap_ratio`, `rework_ratio`, `throughput_rate`, `production_process_ratio` |
| Calculation job output table | every KPI screen | `US-FND-KPI-001` to `006`, `009` to `012` | owned by `production-domain`; not a FOUNDATION entity |

> **What this table implies.** Every ⚠ row and every entry in the "new fields" column **must be requested
> from `foundation-domain` through the Product Owner** before the related story enters a sprint
> ([AGENTS.md §5 rule 6](https://github.com/molca-id/docs-molcadx/blob/main/AGENTS.md#5-aturan-kerja-berlaku-untuk-semua-agent)).
> Inventing new fields during implementation is forbidden.

### 7.3 Access control

> **Read this before the table below.**
> - **The role names are labels, not a contract.** `operator`, `supervisor`, `plant_manager`, `admin` and the two
>   `⚠` proposed ones are **illustrative subject-side labels**. Design and PRODUCTION-facing stories use them to talk
>   about who does what. They are **not** a FOUNDATION-verified role list, and FOUNDATION stores none of them.
> - **What FOUNDATION contributes.** Per the
>   [ABAC contract](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp),
>   only the **resource** side: it resolves every entity to its `tenant`/`enterprise`/`site`/`area`/`work_center`/`work_unit`
>   scope, plus a fixed `view`/`create`/`update`/`deactivate` action vocabulary (plus `audit_view` / `audit_download` on every module, for reading the audit trail — F-14). Whether a subject actually holds
>   "`supervisor`" is an IDP-asserted claim this table cannot verify.
> - **How to read a cell.** Read every "May"/"May not" cell as shorthand for *"a subject whose IDP claim is
>   conventionally labelled this way, scoped as shown"*.
> - **What is enforceable.** Each story's own **Rules & constraints → Access** line, written in scope-and-action
>   form. This table is orientation, not the contract.

Model: **scope-and-action, resolved per resource** ([ABAC contract](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp)), not a FOUNDATION-stored role table.
Permissions from multiple IDP-asserted claims are **unioned**. The final role vocabulary the IDP will use has not been set ([O-2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#open-questions-at-a-glance)).

| Role | May | May not | Behaviour when denied |
|------|-----|---------|------------------------|
| `operator` (work center scope) | Start shift, record output/defects, open & close downtime, raise andon, correct **their own entries** on an `open` shift, read their line's jobs & OEE | Close the shift, correct others' entries, change the schedule, change master data, open platform screens | The action is unavailable (rather than shown and then failing); a brief explanation + which role to contact |
| `technician` ⚠ (not yet in `ROLE`) | Everything `operator` can + closing downtime with special technical codes, recording actions | Close the shift, change the schedule, change master data | As `operator` |
| `supervisor` (area / work center scope) | Everything `operator` can on their line + close the shift, correct any entry within their scope (≤48 h after `closed`), assign crews, allocate & release & reschedule jobs, read every OEE view for their lines, change **operational parameters** for their lines | Change master data (assets, items, routings, hierarchy), change choice lists/feature flags/technical settings, open Service Monitoring, correct >48 h without approval | Explanation + who to contact; for corrections >48 h: redirected into the `plant_manager` approval flow |
| `planner` ⚠ (not yet in `ROLE`; played by `supervisor` for now) | Create WOs, allocate & release & reschedule jobs across lines within their scope | Record production entries, close shifts, change master data | As `supervisor` |
| `plant_manager` (site scope) | Read every OEE view and schedule for their site, approve corrections >48 h | Record production entries, change master data & technical configuration | Explanation; write actions are unavailable |
| `admin` (site / enterprise scope) | All master data & platform within their scope: hierarchy, assets, items, routings, organisation & roles, shifts & calendars, workflows, notifications, connectors, configuration, feature flags | Grant roles **outside** their own scope; record production entries on behalf of operators on lines outside their scope | An explanation of the scope boundary, naming the scope they do hold |

**Rules that apply on every screen:**
1. Nodes/lines outside scope are **never displayed** in lists or pickers — not shown and then refused.
2. Direct-link access outside scope is refused with an explanation **and who to contact** — never a blank page or a technical error.
3. Every refusal is recorded as `access_denied` (`attempted_scope`, `claim_scope_held`) to detect mis-granted claims.
4. Integration credentials are never displayed to any role, including `admin`.

**Audit trail rule for every FOUNDATION screen (2026-09-25, [`US-FND-AUD-001`](14-audit-trail.md#us-fnd-aud-001)):**
every edit, deactivate and reactivate form has a **required Reason** field; create forms show it as optional; a bulk
import asks for one reason per file. Without a reason the save is refused ("Say why you're making this change") and
nothing is written. This applies to every FOUNDATION story that edits or deactivates data, even where the story's
own section 3 doesn't mention the field yet.

### 7.4 Offline behaviour

The governing rule ([P-01 §5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#log-monitoring)): **never discard an operator
entry.** Doubtful data is better flagged than lost. Offline endurance is at least one full shift —
**`[proposed]` 8 hours**.

| Action | While offline | On reconnect | Data conflict |
|--------|---------------|--------------|---------------|
| Record output | **Works** — stored locally, message "saved on device" | Sent in `client_ts` order; idempotent | Two entries on the same job & period → **both kept** (output is additive) |
| Open downtime | **Works** — recorded locally without waiting on the server | Sent; duration computed from `client_ts` | Two overlapping downtimes on the same asset → both kept, **flagged as a conflict**, the supervisor resolves before closing the shift |
| Close downtime | **Works** — the active `DOWNTIME_REASON` list must be available locally | Sent | As above |
| Record defect | **Works** — the active `REJECT_REASON` list (which includes defect codes) must be available locally | Sent | Additive, like output |
| Raise andon | **Works**, but the notification follows — stated plainly in the UI + a fallback route suggested | The notification is sent flagged **"past event"** with the event's age | Similar queued andons are grouped (suppression) before display |
| Correct an entry (`open` shift, own entry) | **Works** | Sent with the original `client_ts` | A correction chain; the effective value is the accumulation of deltas |
| Start shift / view jobs | **Works** from local data | Refreshed | — |
| Read line OEE | **Works** — the last locally stored numbers + a data-age marker | Recomputed | — |
| Close shift | **Unavailable** — requires a connection; name how many entries are still awaiting sync | — | — |
| Cross-line dashboards, trends | **Unavailable** — state it | — | — |
| Change the schedule, release jobs | **Unavailable** — state it | — | — |
| All master data & platform screens | **Not editable**; configuration values & active taxonomies **must** be stored locally so recording can continue | — | — |
| Work a workflow task | **Unavailable** (it touches a shared flow); the last task list stays readable with a staleness marker | — | — |

**Rules across every action:**
1. Every entry carries a **device-generated idempotency key**; re-sending never duplicates (`US-FND-INT-002`).
2. Durations and ordering use `client_ts`; `server_ts` is for audit only ([04 §4](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/04-metrics-framework.md#4-konvensi-event)).
3. Unsynced entries are **never** discarded, including when local storage fills up — already-synced entries are discarded first, and the operator is informed.
4. Entries arriving for an already-`closed` shift **are accepted** as corrections and flag the shift for review.
5. The count of entries awaiting sync is always visible while offline; when online with an empty queue, the indicator is **not shown at all**.
6. Every event carries `is_offline_sync` so synced entries can be distinguished.

### 7.5 Non-functional requirements (numbers mandatory)

> Numbers marked **`[proposed]`** come from domain documents and have not been decided by the PO — while any
> `[proposed]` remains, the PRD cannot be promoted to `v1.0` ([§0](../PRD-001-molcadx-core-q3-en.md#0-how-to-read-this-document--compliance-notes)).

| Category | Requirement | Verification method |
|----------|-------------|---------------------|
| **Performance** | The operator's running-job screen renders fully in **≤2.5 seconds (p95)** on a 3G network | Synthetic load test with 3G throttling, 100 sessions, measure p95 from `output_screen_opened` |
| Performance | The OEE line view renders fully in **≤2.5 seconds (p95)** on a 3G network | Same, on the running-shift OEE screen with one full shift of data |
| Performance | A 20-line plant view renders in **`[proposed]` ≤3 seconds (p95)** on an office network | Test with 20 lines × 3 shifts of data |
| Performance | A 90-day trend renders in **`[proposed]` ≤3 seconds (p95)** | Test with 90 days × 3 shifts × 5 lines |
| Performance | Saving one output entry feels complete in **≤1.5 seconds** (including offline: instant locally) | Measure `output_recorded.client_ts` − the save tap on the test device |
| Performance | Opening downtime is recorded **≤1 second** after the tap, without waiting on the server | Measure on the test device, offline and online |
| **Capacity** | Supports **`[proposed]` 200 concurrent active operators per plant** | Load test with 200 concurrent sessions recording output & downtime |
| Capacity | Supports **`[proposed]` 20 work centers × 3 shifts/day × 90 days** of generated shift instances with no screen performance degradation | Generate the full horizon, then repeat the performance tests above |
| Capacity | One device holds **`[proposed]` ≥500 unsynced entries** without data loss | Offline test over 8 hours at the highest expected entry rate |
| **Availability** | **`[proposed]` 99.5% monthly uptime** during operating hours (per-site operating hours from the calendar) | Monthly uptime monitoring; operating hours from production `SHIFT_INSTANCE` rows |
| Availability | Shopfloor recording **keeps working when the server is unavailable** — target: 100% of the must-work-offline actions in [§7.4](#74-offline-behaviour) succeed | Scheduled disconnection test |
| **Offline** | Shopfloor input remains possible offline for **`[proposed]` ≤8 hours**, syncing automatically once online | Test **`[proposed]` 3 scenarios** (total connection loss, intermittent, device powered off and restarted) × 8 hours |
| Offline | **0 entries lost**; **100% synced ≤`[proposed]` 15 minutes** after the connection recovers | Compare device vs server entry counts after each scenario (reconciliation, `US-FND-INT-002` AC-8) |
| Offline | Re-sending **produces no duplicate entries** | Deliberate double-send test on 100 entries; the server row count must remain 100 |
| **Security** | Production data is accessible only to a subject whose IDP-asserted scope claim covers that data's resolved node ([ABAC contract](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp)) | Cross-scope access tests for every scope tier in [§7.3](#73-access-control); **0** leaks |
| Security | Integration credentials are never displayed nor returned by the API to a client | Code review + connector API response testing |
| Security | No **write** access to PLCs from MolcaDx | Architecture review + test: no write path exists on any connector |
| Security | Audit trails (configuration history, workflow trails, correction entries) **cannot be altered or deleted** via the UI or the API | Attempt alteration/deletion on each trail type; all must be refused |
| **Data freshness** | Machine-integrated lines: OEE reflects data **≤`[proposed]` 5 minutes** old; manual recording: **≤1 shift** ([OEE-2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/03-performance.md#boundaries)) | Compare `last_updated_at` against the source event time |
| Data freshness | Configuration changes take effect **≤`[proposed]` 5 minutes** on online devices | Change a value, measure until behaviour changes on the test device |
| Data freshness | Data acquisition failures are detected **≤`[proposed]` 5 minutes** after occurrence, covering **`[proposed]` 100%** of registered sources (KR 1A.1) | Kill a test source, measure the delay until the alarm is sent |
| **Accessibility & physical context** | Primary shopfloor touch targets **≥44×44 px**; primary text contrast **≥4.5:1** | Design audit + field test with gloves |
| **Language** | The entire UI is in Indonesian; terminology must match [01-glossary](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/01-glossary.md) and ISO 22400 (KR 2.2) | Audit UI terminology against the glossary; **0** conflicting terms |
| Performance, KPI screens | The A/P/Q/OEE summary renders fully within **2.5 s (p95)** on 3G | Synthetic run on a throttled connection against a pilot-line dataset |
| Performance, reason picker | The reason list renders within **1 s (p95)** from locally stored data | Instrumented from `reason_picker_opened` to first paint |
| Performance, calculation job | One shift's result available within **60 s (p95)** after the shift closes | `shift_kpi_compute_time` from the job's own timing |
| Performance, master data screens | A 500-row table renders fully within **2 s (p95)** on an office network | Synthetic run against a seeded 500-row table |
| Freshness, running shift | Figures for an `open` shift refresh at least every **5 minutes** | Job scheduling interval plus `availability_computed` timestamps |
| Capacity | **200 work units** per plant computed in one job pass; **1,000 active products** per site | Load test against a synthetic plant |
| Capacity, tags | **50 tags per asset** monitored without losing readings | Tag replay test with a known event count |
| Accuracy | The waterfall closing gap between `OEE × PBT` and the third bar is at most **0.1 minutes** | `oee_waterfall_mismatch` must stay at 0 |
| Offline, reason picker | Works offline for at least **8 hours**, syncs automatically on reconnect | Airplane-mode run across a full shift |
| Security | KPI data is readable only by users holding a role on that work unit; lines outside scope never appear in the list | Access test per role against an out-of-scope line |

### 7.6 Migration / backfill

| Aspect | Content |
|--------|---------|
| **Legacy operational data** | **No migration.** Today's production data lives on paper and in spreadsheets, in no form that can be reliably mapped to `SHIFT_INSTANCE`/`WORK_ORDER_OPERATION`. Paper history is **not** backfilled. |
| **Consequence** | Historical OEE baselines are **unavailable** from the system; per-line OEE targets therefore must not be locked before **`[proposed]` 30 days** of MolcaDx's own data exists ([OEE-3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/03-performance.md#boundaries), [04 §6](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/04-metrics-framework.md#6-aturan-baseline)). |
| **Initial master data** | Loaded via **bulk import** (items, employees) and manual entry (hierarchy, assets, routings, shifts). Standard cycle times that are currently undocumented ([W-3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown)) must be **measured or agreed** with `ops-domain` before the pilot line starts — without them, Performance and capacity cannot be computed. |
| **Work orders from ERP** | If WOs originate in ERP ([W-1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown)), WOs open at go-live are imported as `released` with `source_system` populated; already-completed WOs are **not** imported. |
| **Data rollback** | If the pilot line is halted, data already captured is **not deleted** — the line is deactivated ([`US-FND-SIT-002`](01-site-hierarchy.md#us-fnd-sit-002)) so its history stays readable. |
| **Reason and unit master data** | Entered manually through `US-FND-REF-001/002/004`. Field codes already in use for years are **kept**, not replaced with a new pattern |
| **Availability figures already computed** | None exist; the calculation job has never run. If any exist at cut-over they must be **recomputed and the old figures discarded**, not kept for comparison, because they were produced by the pre-correction formula ([§13.1](../PRD-001-molcadx-core-q3-en.md#131-the-availability-formula-that-was-corrected)) |
| **`US-FND-AST-004` and `US-FND-WMS-001`** | Never built, so there is no code or data to migrate. Both are closed at document level only |

## 8. Instrumentation (for `data-domain`)

Mandatory global properties on **every** event: `user_id`, `role`, `plant_id`, `line_id`, `shift_id`,
`client_ts`, `server_ts`, `app_version`, `is_offline_sync` ([04 §4](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/04-metrics-framework.md#4-konvensi-event)).
The table below lists only the **additional** properties. Duration metrics use `client_ts`.

| Event | Trigger | Additional properties | Story | Metric served |
|-------|---------|-----------------------|-------|---------------|
| `hierarchy_node_created` | A location node is saved | `level`, `parent_level` | `US-FND-SIT-001` | `waktu_setup_hierarki_site`, G2 |
| `hierarchy_node_deactivated` | A node is deactivated | `level`, `descendant_count`, `effective_date` | `US-FND-SIT-002` | History integrity |
| `hierarchy_node_deactivate_blocked` | Deactivation refused | `blocker_type`, `blocker_count` | `US-FND-SIT-002` | `penonaktifan_ditolak_karena_penghalang` |
| ~~`context_changed`~~ | Dropped 2026-09-27 with the retired picker | — | ~~`US-FND-SIT-003`~~ retired | — |
| `asset_class_created` / `asset_type_created` | Taxonomy saved | `depth` / `asset_class_id` | `US-FND-AST-001` | `kedalaman_taksonomi_terpakai` |
| `asset_created` | An asset is saved | `asset_class_id`, `criticality`, `has_serial` | `US-FND-AST-002` | `aset_terdaftar_per_lini_pilot` |
| `asset_relocated` | Placement changes | `from_work_unit_id`, `to_work_unit_id`, `effective_date` | `US-FND-AST-003` | `perpindahan_tercatat_lengkap` |
| `reason_code_created` | A reason code is saved | `category`, `downtime_category`, `depth` | `US-FND-AST-004` | Taxonomy readiness |
| `item_created` / `item_import_completed` | Item saved / import finished | `product_type`, `entry_mode` / `row_total`, `row_failed` | `US-FND-WMS-001` | `item_siap_produksi`, G2 |
| `bom_version_created` | A BOM version is saved | `product_id`, `line_count`, `version` | `US-FND-WMS-002` | `bom_aktif_per_item_produksi` |
| `cycle_time_version_created` | A cycle time version is saved | `operation_id`, `old_value`, `new_value`, `valid_from` | `US-FND-WMS-003` | `akurasi_standar_waktu` |
| `wo_created` / `wo_release_blocked` | WO created / release refused | `product_id`, `planned_qty`, `source_system` / `missing` | `US-PROD-JOB-005` (PRODUCTION story, in PRD-001) | `wo_dirilis_lengkap_sekali_jadi` |
| ~~`defect_code_created`~~ | Dropped 2026-09-23 with `US-FND-WMS-005`; reject reasons have their own events in `US-FND-REF-002` | — | — | — |
| `access_denied` | Access refused | `attempted_scope`, `claim_scope_held` | every story | `akses_ditolak_per_user_per_minggu`, security NFR |
| `enterprise_shift_created` / `enterprise_shift_updated` / `enterprise_shift_delete_blocked` | Enterprise label saved / edited / delete refused | `shift_label` / `adopting_site_count` | `US-FND-SHF-001` | `site_shift_adoption_complete` |
| `site_shift_declared` / `site_shift_overlap_rejected` | Site hours saved / overlapping window refused | `site_id`, `crosses_midnight` / `site_id`, `conflicting_enterprise_shift_id` | `US-FND-SHF-001` | `site_shift_adoption_complete` |
| `shift_instances_generated` / `shift_instance_missing` | Generation run / transaction without a parent | `site_id`, `date_from`, `date_to`, `instance_count` / `site_id`, `attempted_date` | `US-FND-SHF-002` | `hari_horizon_tersisa` |
| `calendar_created` / `non_working_day_created` | Calendar/exception saved | `scope_type`, `pattern_id` / `category`, `affects_scheduled_time`, `lead_days` | `US-FND-SHF-003` | `hari_non_kerja_tercatat_sebelum_terjadi` |
| ~~`shift_assignment_saved`~~ | ~~A crew is assigned~~ **Retired with `US-FND-SHF-004` (2026-09-25)** | `crew_id`, `member_present`, `crew_size_gap` | ~~`US-FND-SHF-004`~~ | `shift_dengan_regu_terkonfirmasi` |
| `workflow_definition_created` | A flow is saved | `step_count`, `from_template` | `US-FND-WFE-001` | `alur_dibuat_tanpa_developer` (low-code KR) |
| `workflow_simulation_run` | A simulation runs | `version_id`, `result`, `failed_step` | `US-FND-WFE-002` | `alur_lulus_uji_sebelum_aktif` |
| `workflow_version_activated` | A version is activated | `from_version`, `to_version`, `running_instance_count`, `reason` | `US-FND-WFE-003` | `instance_berubah_versi_di_tengah_jalan` |
| `workflow_task_assigned` / `_acknowledged` / `_completed` / `_escalated` | Task lifecycle | `role`, `scope_type` / `wait_sec` / `duration_sec`, `outcome` / `from_role`, `to_role`, `timer_min` | `US-FND-WFE-004` | `waktu_tanggap_task`, % expired tasks |
| `workflow_instance_error` | An instance fails | `step`, `error_type` | `US-FND-WFE-004` | Flow health |
| `notification_rule_created` / `_no_recipient` | Rule saved / has no recipient | `event_type`, `severity`, `target_role`, `recipient_count` | `US-FND-NTF-001` | `rasio_notifikasi_diakui` |
| `notification_delivered` / `_acknowledged` / `_escalated` / `_resolved` | Notification lifecycle | `severity`, `channel`, `event_age_sec` / `response_sec` / `from_role`, `to_role` / `resolution_sec` | `US-FND-NTF-002` | `waktu_tanggap_alarm`, % alarms escalated |
| `notification_suppressed` | Events grouped | `dedup_key`, `occurrence_count`, `window_sec` | `US-FND-NTF-003` | `notifikasi_per_penerima_per_shift` |
| `connector_created` / `tag_mapping_created` / `tag_first_value_received` / `tag_unknown_value` | Declaration & reading (`DATA_SOURCE` / `ASSET_TAGS` — not ERP) | `protocol` / `signal_type`, `asset_id` / `setup_duration_sec` / `raw_value` | `US-FND-DSR-001` | `waktu_tambah_tipe_aset_baru` (KR 1B.1) |
| `sync_queue_enqueued` / `sync_batch_completed` / `sync_duplicate_rejected` / `sync_reconciliation_mismatch` | Sync lifecycle | `entity_type`, `queue_depth` / `entry_count`, `duration_sec`, `conflict_count` / `idempotency_key` / `sent`, `received`, `delta` | `US-FND-INT-002` | `entri_offline_tersinkron_tanpa_konflik` (KR 1B.2) |
| `source_health_changed` / `source_stopped_alerted` | Source health | `source_type`, `from_status`, `to_status`, `data_age_sec` / `detection_sec` | `US-FND-INT-003` | `waktu_deteksi_kegagalan_akuisisi` (KR 1A.1) |
| `config_value_set` / `config_resolved` (sampled) | Configuration changed/read | `config_key`, `scope_type`, `old_value`, `new_value` / `source_level` | `US-FND-CFG-001` | `pertanyaan_asal_nilai_ke_developer` |
| `config_history_opened` / `config_rolled_back` | History opened / rollback | `filter_type` / `config_key`, `from_value`, `to_value`, `age_days` | `US-FND-CFG-002` | `waktu_menjawab_kenapa_perilaku_berubah` |
| `feature_flag_changed` | A flag is changed | `module_key`, `scope_type`, `enabled`, `affected_row_count` | `US-FND-CFG-003` | `modul_dimatikan_per_pabrik` |
| `master_data_setup_started` / `first_output_recorded` | Site setup begins / site's first output | `site_id` | cross-story | **G2** `waktu_setup_site_oleh_implementor` |
| `downtime_reason_created` | A downtime reason is saved | `downtime_category`, `site_id` | `US-FND-REF-001` | Taxonomy completeness |
| `downtime_reason_category_change_blocked` | A class change is refused on a referenced row | `reason_code`, `referencing_log_count` | `US-FND-REF-001` | History integrity |
| `reject_reason_created` | A reject reason is saved | `site_id` | `US-FND-REF-002` | Taxonomy completeness |
| `reject_reason_selected` | An operator picks a reject reason | `reject_reason_id`, `asset_id` | `US-FND-REF-002` | `rejects_labelled` |
| `reason_picker_opened` | The reason picker opens on a stop | `asset_id`, `list_size`, `is_fallback_full_list` | `US-FND-REF-003` | `reason_selection_time` |
| `downtime_reason_selected` | An operator picks a downtime reason | `downtime_reason_id`, `asset_id`, `position_in_list` | `US-FND-REF-003` | `reason_selection_time`, `stops_labelled` |
| `reason_asset_mapping_changed` | A per-asset reason mapping is edited | `asset_id`, `mapped_count` | `US-FND-REF-003` | Scoping effectiveness |
| `uom_created` | A unit is saved | `code`, `site_id` | `US-FND-REF-004` | Unit completeness |
| `uom_conversion_missing_blocked` | An addition is refused for a missing conversion | `from_uom_id`, `to_uom_id`, `product_id` | `US-FND-REF-004` | `conversions_available` |
| `time_conversion_created` / `time_conversion_updated` | A named duration is declared / its factor changed | `quantity`, `base_unit`, `seconds_per_unit` / `old_seconds_per_unit`, `new_seconds_per_unit`, `reference_count` | `US-FND-REF-005` | `time_conversion_rows_resolvable` |
| `reference_row_deactivated` | A reference row is deactivated | `entity`, `reference_count`, `cascaded_mapping_count`, `effective_date` | `US-FND-REF-006` | History integrity |
| `reference_row_deactivate_blocked` | A deactivation is refused | `entity`, `blocker_type`, `blocker_count` | `US-FND-REF-006` | `deactivations_refused` |
| `product_created` | A product is saved | `product_type`, `base_uom_id`, `site_id` | `US-FND-PRO-001` | Product completeness |
| `product_readiness_checked` | Readiness is evaluated for a product | `product_id`, `is_ready`, `missing_requirement` | `US-FND-PRO-001` | `products_ready_for_performance` |
| `product_detail_saved` | Physical attributes are saved | `product_id`, `has_weight_envelope`, `is_batch_tracked` | `US-FND-PRO-002` | `products_with_weight_envelope` |
| `weight_envelope_validation_failed` | An envelope fails validation | `product_id`, `rule_violated` | `US-FND-PRO-002` | Entry quality |
| `product_uom_conversion_created` | A per-product conversion is saved | `product_id`, `from_uom_id`, `to_uom_id` | `US-FND-PRO-003` | `conversions_available` |
| ~~`uom_conversion_reverse_missing`~~ | Dropped 2026-09-28: one conversion row covers both directions, so a reverse row is never missing | — | `US-FND-PRO-003` | — |
| ~~`product_cycle_created`~~ | Dropped 2026-09-28 with the retired `PRODUCT_CYCLE` | — | ~~`US-FND-PRO-004`~~ retired | — |
| ~~`product_cycle_inconsistent`~~ | Dropped 2026-09-28 with the retired `PRODUCT_CYCLE` | — | ~~`US-FND-PRO-004`~~ retired | — |
| `availability_computed` | Availability is computed for a period | `work_unit_id`, `business_date`, `pot_minutes`, `pbt_minutes`, `apt_minutes`, `is_provisional` | `US-FND-KPI-001` | `daily_availability_computed` |
| `availability_no_data` | Availability cannot be computed | `work_unit_id`, `missing_input` | `US-FND-KPI-001` | `figures_shown_without_complete_input` |
| `availability_anomaly` | Availability inputs are inconsistent | `work_unit_id`, `anomaly_type` | `US-FND-KPI-001` | Data quality |
| `performance_computed` | Performance is computed | `work_unit_id`, `business_date`, `segment_count`, `apt_minutes`, `ideal_minutes`, `is_partial` | `US-FND-KPI-002` | `performance_computed` |
| `performance_segment_no_standard` | A SKU segment has no `OPERATION` | `work_unit_id`, `product_id` | `US-FND-KPI-002` | `products_ready_for_performance` |
| `performance_anomaly` | Performance exceeds 100% or similar | `work_unit_id`, `value`, `anomaly_type` | `US-FND-KPI-002` | Data quality |
| `quality_computed` | Quality is computed | `work_unit_id`, `business_date`, `fill_path`, `good_qty`, `total_qty`, `reject_qty` | `US-FND-KPI-003` | `quality_computed` |
| `quality_anomaly` | good + reject does not match total | `work_unit_id`, `good_qty`, `reject_qty`, `total_qty` | `US-FND-KPI-003` | Counter quality |
| `quality_no_data` | Only one of the three quantities exists | `work_unit_id`, `available_input` | `US-FND-KPI-003` | `figures_shown_without_complete_input` |
| `oee_computed` | OEE is computed | `work_unit_id`, `business_date`, `availability`, `performance`, `quality`, `oee`, `is_provisional` | `US-FND-KPI-004` | `oee_computed` |
| `oee_suppressed_missing_component` | OEE withheld because a component is absent | `work_unit_id`, `missing_component` | `US-FND-KPI-004` | `figures_shown_without_complete_input` |
| `oee_waterfall_mismatch` | The waterfall closing figure disagrees | `work_unit_id`, `expected_minutes`, `actual_minutes` | `US-FND-KPI-004` | Arithmetic accuracy |
| `reliability_computed` | MTTR/MTBF computed for a period | `work_unit_id`, `period`, `mttr_minutes`, `mtbf_minutes`, `event_count` | `US-FND-KPI-005` | `reliability_computed` |
| `reliability_no_data` | Too few closed unplanned stops | `work_unit_id`, `period`, `closed_stop_count` | `US-FND-KPI-005` | `figures_shown_without_complete_input` |
| `downtime_detected_from_stall` | A stalled counter opens an event | `work_unit_id`, `stall_seconds`, `bucket`, `matched_reason_id`, `min_stop_seconds_used`, `small_stop_max_seconds_used` | `US-FND-KPI-006` | `stops_detected_automatically` |
| `stall_detection_merged_with_manual` | A detected stall merges with an operator stop | `work_unit_id`, `manual_event_id` | `US-FND-KPI-006` | Duplicate prevention |
| `stall_event_rejected` | A supervisor cancels a detected event | `work_unit_id`, `stall_seconds` | `US-FND-KPI-006` | Threshold accuracy |
| `scrap_ratio_computed` | Scrap ratio is computed | `work_unit_id`, `business_date`, `scrap_qty`, `produced_qty` | `US-FND-KPI-009` | `scrap_ratio_computed` |
| `scrap_ratio_no_data` | Scrap ratio cannot be computed | `work_unit_id`, `available_input` | `US-FND-KPI-009` | `figures_shown_without_complete_input` |
| `rework_ratio_computed` | Rework ratio is computed | `work_unit_id`, `business_date`, `rework_qty`, `produced_qty` | `US-FND-KPI-010` | `rework_ratio_computed` |
| `rework_ratio_no_data` | Rework ratio cannot be computed | `work_unit_id`, `reason` | `US-FND-KPI-010` | `figures_shown_without_complete_input` |
| `throughput_rate_computed` | Throughput rate is computed | `work_unit_id`, `work_order_operation_id`, `produced_qty`, `aoet_minutes`, `is_provisional` | `US-FND-KPI-011` | `throughput_rate_computed` |
| `throughput_rate_no_data` | Throughput rate cannot be computed | `work_unit_id`, `work_order_operation_id`, `missing_timestamp` | `US-FND-KPI-011` | `figures_shown_without_complete_input` |
| `throughput_rate_anomaly` | `AOET` computes to 0 or negative | `work_unit_id`, `work_order_operation_id` | `US-FND-KPI-011` | Data quality |
| `production_process_ratio_computed` | Production process ratio is computed | `work_unit_id`, `work_order_operation_id`, `apt_minutes`, `aoet_minutes`, `is_provisional` | `US-FND-KPI-012` | `production_process_ratio_computed` |
| `production_process_ratio_no_data` | Production process ratio cannot be computed | `work_unit_id`, `work_order_operation_id`, `missing_input` | `US-FND-KPI-012` | `figures_shown_without_complete_input` |
| `production_process_ratio_anomaly` | Ratio computes above 100% | `work_unit_id`, `work_order_operation_id`, `value` | `US-FND-KPI-012` | Data quality |

## Change notes (history — not needed to build)

- §7.1 `SCR-FND-AST-001` (2026-09-04) — the asset browser and taxonomy manager screens were merged into one screen.
- §7.1 `SCR-FND-AST-006` / `US-FND-AST-005` (2026-09-10) — component installation history moved to the Maintenance domain as `SCR-MNT-AST-001`.
- §7.1, §7.2, §8 `US-PROD-JOB-005` — renamed from `US-FND-WMS-004` when `WORK_ORDER`/`WORK_ORDER_OPERATION` moved to PRODUCTION (2026-09-03).
- §7.1 Crew-to-shift assignment (2026-09-03, [§13.5](99-history.md#135-organization-removed-2026-09-03)) — `CREW` went away with the Organization module.
- §7.1 — four device-context rows (`REF`/`PRO` master data, operator reason picker, KPI summary, line reliability) sat under the "first 3 seconds" table by mistake; they are moved, unchanged, into the device-context table (2026-09-23).
- §7.2 `DOWNTIME_REASON` / `REJECT_REASON` — split from the old single `DOWNTIME_REASON` entity.
- §7.2 `TIME_CONVERSIONS` (2026-09-02) — `seconds_per_unit` was added by `foundation-domain` (T-4 resolved).
- §7.2 `PRODUCT_CYCLE` (2026-09-02) — `time_conversion_id` was added by `foundation-domain`.
- §7.2 `KPI_FORMULA_SLOT` (2026-09-10) — two non-bindable rows added: `throughput.order_execution_time` and `rework.output`.
- §7.2 `KPI_FORMULA_SLOT` (2026-09-27, PO) — new field `domain` (production / quality / maintenance / inventory); all rows `production`. KPI screens: one sidebar item per domain (`US-FND-KPI-007`, `008`).
- §7.2 `KPI_RESULT` (2026-09-10) — `metric` enum extended with `scrap_ratio`, `rework_ratio`, `throughput_rate`, `production_process_ratio`.
- §7.3 (2026-09-03) — the ABAC contract was resolved.
- 2026-09-23 — counts checked against the module files: 13 modules, 50 live stories, 12 of them in the concept-only modules `WFE` (4), `NTF` (3), `INT` (2), `CFG` (3). This file stated no story counts, so none needed correcting. It also has no instruction to keep PRD-001 in sync; PRD-001 is referenced only as the frozen home of `US-PROD-JOB-005` and for two older sections (§0, §13.1).
- §7.1 (2026-09-23): asset class/type editing moved to `SCR-FND-REF-009`/`010`; the `SCR-FND-AST-001` row no longer claims taxonomy editing. Location class managers `SCR-FND-REF-005`–`008` added.
- §7.1 (2026-09-23): Reference data sidebar row added; retired `SCR-FND-AST-004` marked; `US-FND-AST-004` (closed) dropped as a reader of `ASSET_CLASS`/`ASSET_TYPE`.
- 2026-09-23: `QUALITY_DEFECT_CODE` merged into `REJECT_REASON` — screen map, entity summary, offline table and event list updated.
- 2026-09-28 (PO, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md); prd-sync docs-molcadx 7fb3a9a..1285317):
  §7.1 `SCR-FND-PRO-002` struck (retired with `PRODUCT_CYCLE`); BOM and routing editors reached from
  Production › Work Master; new row for the app sidebar grouped by domain (Master data + one group per MOM domain,
  locked heading when unlicensed). §7.2 `PRODUCT_CYCLE` struck; `US-FND-PRO-004` dropped as a reader;
  `US-FND-WMS-003` and `US-FND-KPI-002` added as readers of `PRODUCT_UOM_CONVERSION` (`OPERATION.uom_id` is the item
  unit). §8 `product_cycle_created` / `product_cycle_inconsistent` dropped.
- 2026-09-28 (prd-sync docs-molcadx 1285317..513c4f9; [work master decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) addenda, [asset status decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)):
  §7.1 no "Master data" label — shared items at the top with no heading ("Master data menu" entry points
  renamed); Work Master flattened (Production › BOM / Routing, Maintenance › PM checklist / Asset history);
  `SCR-FND-WMS-001` Items & UOM struck (retired); `SCR-FND-AST-005` state log browser struck (→ `SCR-PROD-MON-001`,
  PRODUCTION). §7.2 `ASSET_STATE_LOG` rows name PRODUCTION as owner and only writer; origin marker = `source`;
  `PRODUCT_UOM_CONVERSION` readers: `KPI-003` out (Quality never converts), `KPI-008` in (conversion rule).
