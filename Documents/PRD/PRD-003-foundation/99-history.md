# PRD-003 · History

> Part of [PRD-003 FOUNDATION](README.md). Why things changed. **You do not need this file to build anything** — every change recorded here is already applied in the module files.

## Version notes (moved from the old header)

**Version:** v0.2 (draft) — FOUNDATION sync 2026-09-03, see [§13.4](#134-synchronised-to-foundation-decisions-2026-09-03). **Second same-day sync** (rule 1b, [contest-review decisions](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-03-foundation-contest-blockers.md)): `WORK_ORDER`/`WORK_ORDER_OPERATION` moved FOUNDATION → PRODUCTION (`US-FND-WMS-004`), F-02 downgraded to `DRAFT` (`US-FND-SHF-002` generator ownership unassigned), F-07 Availability/APT qualified not-Ready (`US-FND-KPI-001/004/005`), `ASSET_STATE_LOG` reopened (`US-FND-SIT-002`), F-13 Data Source flagged against `US-FND-INT-001`. **Third same-day sync:** the pending `US-FND-WMS-004` → `US-PROD-JOB-*` rename above is now executed — the story is retired from this PRD and lives only in PRD-001 as `US-PROD-JOB-005`. **Fourth same-day addition:** `US-FND-KPI-007` (Bind a work unit's tags to its KPI formula slots) added — closes the gap where `US-FND-KPI-001/002/003/006` all read `WORK_UNIT_KPI_BINDING` but no story existed to create it; cites `SCR-FND-KPI-002` ([UX 07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md)), which already existed

2026-09-04: **`US-FND-INT-001` body actually fixed** — the 2026-09-03 note above it claimed a rewrite to `ASSET_TAGS`/`DATA_SOURCE`, but the story's Data & entities/Context/Rules/AC/Metrics still described `CONNECTOR`/`TAG_MAPPING` verbatim; now genuinely rewritten, and `US-FND-INT-003`'s stale `Data & entities` reference fixed to match. Surfaced (not resolved) a new gap: no field for `scale`/`offset`/`interpretation_rules` exists on either real entity, so several ACs are now marked blocked pending a `foundation-domain` decision. **Same day, `US-FND-AST-005` added** — closes the `ASSET_COMPONENT_LINK` gap found by `prd-pilot`, see [§13.6](#136-asset_component_link-gap-closed-2026-09-04). **2026-09-04, second addition (GitHub issue #20):** `US-FND-INT-001` is **closed, replaced by `US-FND-DSR-001`** — the split its own note proposed and left for a PO decision is now made. `DATA_SOURCE` connection declaration and its paired `ASSET_TAGS` tag-mapping (one screen, per `US-FND-INT-001`'s Expectation row) move to the new `DSR` module. `INT` (ERP/system-to-system only) has no ERP content anywhere in the docs yet, so it is retired rather than left as a near-empty story — see [§13.2](#132-stories-closed-and-retired). `US-FND-INT-002`/`US-FND-INT-003` Dependencies updated to point at `US-FND-DSR-001`. **2026-09-08 addition:** `US-FND-KPI-008` (View KPI calculation breakdown) added for `SCR-FND-KPI-004`, the view-only KPI Calculation page declared in [UX 07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md) and [FLOW-05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/FLOW/05-kpi-computation.md) — closes the gap where that screen existed with no matching story; folds in the `range_bucket`/out-of-range-analog detection method's explanation, since this page is the only surface for it. **2026-09-10 retirement (MOM domain alignment):** `US-FND-AST-005` retired — component-swap history moved out of FOUNDATION to the new Maintenance domain, see [§13.2](#132-stories-closed-and-retired) and [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-10-mom-domain-alignment.md). **2026-09-11 sync (rule 1b, see [§13.7](#137-synchronised-to-b1-closure-2026-09-11)):** `US-FND-SHF-002`'s blocking banner removed — B1 (`SHIFT_INSTANCE` generator ownership) closed, assigned to `production-domain`; F-02 reverted `DRAFT` → `ACTIVE`. **2026-09-12 sync (rule 1b, see [§13.8](#138-synchronised-to-the-work-master-license-gate-decision-2026-09-12)):** `US-FND-WMS-002`/`003`/`005` gained a "No entitlement" (license-gate) state — Work Master is data-owned by FOUNDATION but license-gated by PRODUCTION, found via a `prd-pilot` check against the same-session `08-work-master.md` UX decision

**Date:** 2026-09-12 (`US-FND-WMS-002`/`003`/`005` synced to Work Master license-gate decision, rule 1b) · 2026-09-11 (`US-FND-SHF-002` synced to B1 closure, rule 1b) · 2026-09-10 (`US-FND-AST-005` retired, moved to Maintenance domain) · 2026-09-08 (full `prd-pilot` contradiction sync, rule 1b — see [§12](#12-decision-log)) · 2026-09-03 (v0.2) · 2026-08-24 (v0.1)

## 12. Decision log

| Date | Decision | Rationale | Decided by |
|------|----------|-----------|------------|
| 2026-10-01 | **Keys on every table** ([F-00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#keys-and-constraints-on-every-table-po-2026-10-01)): one surrogate PK per table, a Keys line (PK · Unique · No overlap) per entity, no list-valued columns. New PKs `downtime_reason_asset_id`, `reject_reason_asset_id`, `kpi_result_id`, `asset_placement_id`, `asset_state_log_id`. Synced into the Writes of `REF-003`, `AST-003`, the `KPI_RESULT` stories, and the B5 notes | Change data capture (Debezium) keys events on the PK; PostgreSQL refuses UPDATE/DELETE without one | PO |
| 2026-10-01 | **Product codes: full screen and a story** (`US-FND-WMS-006`, `SCR-FND-WMS-007`). Codes are per asset; one product may have several; overlap checked on `(asset_id, raw_value)` | The Routing tab section had only an outline and no story; PLC-only Performance needs codes mapped | PO |
| 2026-10-01 | **Work Master step detail like an EBR master recipe** (F-08 §9.2.5–§9.2.7, §9.4, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-10-01-work-master-ebr-step-detail.md)): instructions, in-process checks, yield range, hold time on a step; dispensing tolerance on a BOM line. FOUNDATION holds the standards, execution is PRODUCTION's. Synced into `WMS-002` / `WMS-003`; screen text waits on UX 08 | Routing should match an EBR master recipe where that also helps food lines; sign-off and line clearance wait on pharma as a target | PO |
| 2026-10-01 | **Site Hierarchy search + Tree keyboard navigation; no "Generate" for a site without shift hours** (UX 01, UX 02, from the usability audit in PR #22). Synced into `SIT-001` and `SHF-002`; gap count indicator open as UX-SHF-3 | Generate on a site with no hours created nothing and still reported success; finding a node in a large hierarchy needed search | PO |
| 2026-10-01 | **Stop thresholds** (F-12, closes F-07.1 K-6 and the threshold part of K-8): `min_stop_seconds` 60 s / `small_stop_max_seconds` 300 s, per work center with work unit override, one pair for stall and range detection; band ownership split out as K-10, duration-vs-reason as K-11. **Manual reject in any unit** (Q-28): Quality calculates in `OPERATION.uom_id`; machine total + manual reject is an allowed pair. Synced into `KPI-003`, `KPI-006`, `KPI-008`, `KPI-009`, `CFG-001` | Thresholds were fixed in code (1 min / 5 min) and differ per line; reject units differ per SKU and site | PO |
| 2026-09-28 | **Tag declaration improved; MQTT topics can be generated** (UX 13 § Declaring a tag, F-13 §9.1, F-04 §9.7.1): address naming stays per data source, default generated, typed kept for fixed vendor servers; MQTT generated as `[topic_root/]SITE/AREA/WC/WU/ASSET/TAG`, one value per topic, `topic_root` optional; tag form reordered with role pre-fill, "Missing for KPIs" panel, edit only on Tags (asset detail read-only tab); tag template per machine type logged as a future improvement (A-16) | Some sites connect straight to a vendor/machine OPC UA server with fixed names; Telegraf reads one value per topic; declaring from gaps is faster than a blank form | PO |
| 2026-09-28 | **List pages, per-KPI side panel, Tags page** (UX 08, UX 07, UX 13): BOM and Routing open on a product list with gap badges (picker retired); the KPI page drops its Formula Slots / results buttons and opens a side panel per KPI card (`SCR-FND-KPI-001`/`-003`/`-004` become panel sections); tags get their own Connections page, Tags (`SCR-FND-DSR-002`), which holds the export | The picker hid which products lack a BOM/routing; each KPI takes different inputs so one catalog/results page did not fit; MolcaDx only declares tags, so the declared list is the hand-off and needs its own page | PO |
| 2026-09-28 | **One product conversion row covers both directions** ([F-06 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#94-product_uom_conversion--fromto-uom-conversion-factor-scoped-to-one-product)): forward ×, backward ÷; forward row wins if both exist. | Resolves the `US-FND-PRO-003` vs F-08 conversion-path conflict found in the task breakdown. `US-FND-PRO-003` updated. | PO |
| 2026-09-28 | **Asset status ownership** ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)): `ASSET_STATE_LOG` is owned by PRODUCTION (only writer; MAINTENANCE reads); B5/A-5 ownership resolved; the recording level moves to PRODUCTION SL-1; `SCR-FND-AST-005` retired → `SCR-PROD-MON-001`. `ASSET.lifecycle_status` current value stays FOUNDATION, its history goes to MAINTENANCE (entity `TBD`). Synced (docs-molcadx 1285317..513c4f9) into `KPI-001/004/005/006/012`, `SIT-002`, `AST-002`, `REF-001/003/006`, `AUD-001`, README, cross-cutting, release | Transactional event log belongs to the MOM domain that writes it (2026-09-10 splitting rule). **No status change:** PRODUCTION's entity spec is still a draft (field names, SL-1 `TBD`), so the B5 stories stay blocked | Product Owner |
| 2026-09-28 | **Work Master flattened; no "Master data" label; four PO answers** ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) addenda): Production = BOM · Routing · KPI, Maintenance = PM checklist · KPI · Asset history; BOM and Routing separate pages with a carried-over item picker. Quality tags share one unit (blocked at binding); a typed rate is saved as typed (precision `TBD`); the KPI breakdown shows the conversion rule; `SCR-FND-WMS-001` retired. Synced into `WMS-002/003`, `KPI-003/007/008/009`, `REF-005`, closed `WMS-001` note | BOM and Routing are independent entities; Quality never converts units; seconds normalisation lost the typed value | Product Owner |
| 2026-09-28 | **Work Master per domain; `OPERATION.uom_id` is the item unit; `PRODUCT_CYCLE` retired** ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md)). A: the sidebar is grouped by domain — Master data + Production / Maintenance / Quality / Inventory, each holding Work Master and KPI; an unlicensed domain is one locked group heading (K-9 revised, PLT-01 concept 3a). B: `OPERATION.uom_id` names the item the standard is per; the KPI converts the counter's unit through `PRODUCT_UOM_CONVERSION` before `batch_size`; save blocked without a conversion path; W-6 resolved. C: `PRODUCT_CYCLE` and `SCR-FND-PRO-002` retired; `US-FND-PRO-004` closed and moved to [90-closed-stories.md](90-closed-stories.md#us-fnd-pro-004). Synced into `WMS-002`, `WMS-003`, `KPI-002`, `KPI-007`, `KPI-008`, `PRO-001`, `PRO-003`, `REF-004`…`006` (prd-sync docs-molcadx 7fb3a9a..1285317) | Licences are sold per domain, so navigation follows what the tenant bought; one product can run 20 s / pack on the filler and 216 s / carton on the cartoner, so the item unit must sit on the operation; nothing read `PRODUCT_CYCLE` and it could contradict the routing | PO |
| 2026-09-27 | **Location context picker retired from FOUNDATION:** `US-FND-SIT-003` closed and moved to [90-closed-stories.md](90-closed-stories.md#us-fnd-sit-003); `SCR-FND-SIT-003` retired in UX 01, `context_changed` dropped. A Production picker is parked ([docs-molcadx#46](https://github.com/molca-id/docs-molcadx/issues/46)) | Its user and example are production-side, the app now ships as separate apps, and no FOUNDATION app should show it | PO |
| 2026-09-25 | **Audit log stays central; every row shows its domain and module.** Downloads of a domain's own data are that domain's `*_downloaded` business actions; `download` = audit rows only | One place to audit the whole system, without losing where each change came from. Closes PRD-004 P-10 | Product Owner |
| 2026-09-25 | **OPC UA tag addresses generated from the hierarchy; multi-lane machines** ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-25-generated-opcua-tag-nodes.md)). Users declare `tag_name`; for `node_naming = generated` sources the NodeId is built from placement (`nsu=…;s=SITE.AREA.WC.WU.ASSET.tag_name`). Codes become one segment per level (F-01 S-4 partly reversed). One asset may hold several active placements only as a multi-lane machine, tags assigned per lane via `ASSET_TAGS.work_unit_id`. Synced 2026-09-27 into `SIT-001`, `AST-002`, `AST-003`, `DSR-001`, `KPI-001/002/003/006/007` | Hardware team configures servers to match Molca's addresses, not the reverse; one machine with per-lane counters needs one OEE per lane | Product Owner |
| 2026-09-25 | **Crew assignment retired:** `US-FND-SHF-004` closed and moved to [90-closed-stories.md](90-closed-stories.md#us-fnd-shf-004); `SCR-FND-SHF-004` retired in UX 02. Replaces the earlier "blocked, not retired" status | Not needed for now; no live story depends on it | Product Owner |
| 2026-09-25 | **Screen text lives in UX; the PRD copies it word for word with its key.** Synced 2026-09-27 into the `SIT`, `SHF`, `FYR`, `REF`, `AST-001` and `DSR-001` stories | One source for every word on screen; designers and developers stop guessing text | Product Owner |
| 2026-09-25 | **Audit trail widened to the whole system**, reading per module | The Figma has the same Log box on every column, FOUNDATION and PRODUCTION; one shared trail instead of one per domain | Product Owner |
| 2026-09-25 | **New module F-14 Audit Trail (`AUD`)**, first scoped FOUNDATION only, 4 stories `US-FND-AUD-001…004`. An effective-dated edit is one audit row carrying its effective dates; every write source is covered, including other domains' jobs that write FOUNDATION data | The PO wants to know who edits FOUNDATION data; effective dating shows *what* was valid when, never *who* wrote it | Product Owner |
| 2026-09-24 | **T-6 closed:** disposition on `WORK_ORDER_OPERATION_DEFECT`, not `REJECT_REASON`. PRODUCTION job-schedule / shopfloor / OEE synced (Q-18 FK + ISO 22400-2 POT/PBT Availability; drop `is_production_shift`). F-08 Part-4 min spine declared; S-6a clarified (not a second EQUIPMENT_FLOW editor). | Align domain + PRD to ot-it standards lens without inventing new domains | Product Owner |
| 2026-08-24 | **A standalone FOUNDATION PRD is created**, duplicating 48 stories from PRD-001 v0.2 | Requested by the Product Owner after the duplication cost was set out. `foundation-domain` gets a scoped brief; the price is that every FOUNDATION correction must be applied twice, tracked as [F-R-01](30-release-risks-questions.md) | Product Owner |
| 2026-08-24 | Module code **`FYR`** assigned to Yearly & Quarterly Declaration, registered in [AGENTS.md §4](https://github.com/molca-id/docs-molcadx/blob/main/AGENTS.md#kode-domain--modul) | F-03 has been 🟢 ACTIVE with a full `FISCAL_YEAR` spec and no module code, so no story ID could be formed for it | Product Owner |
| 2026-08-24 | `US-FND-SHF-003` **closed**; the working-calendar concept is not rebuilt inside FOUNDATION | F-03 records the PO retiring `SHIFT_CALENDAR` and `NON_WORKING_DAY` entirely. Which dates production runs on is `production-domain`'s concern | Product Owner |
| 2026-08-24 | `US-FND-SHF-002` corrected to stop reading the retired entities | It generated shift instances from a calendar that will not be built. It now takes production dates from `production-domain` | Product Owner |
| 2026-08-24 | Question numbers **kept identical to PRD-001 v0.2**; risk IDs given an `F-` prefix | Questions are shared concerns and should mean the same thing in both files. Risks are scoped differently per document, so they must not collide | Product Owner |
| 2026-08-24 | Both `calendar_basis` modes ship — `monthly` and `weekly_iso` | F-03 §9.1 is explicit that Y-1 only confirms which mode Molca happens to use, and is not a scope decision limiting what the product supports | Product Owner |
| 2026-08-24 | `period_pattern` is **hidden**, not merely disabled, in `monthly` mode | F-03 pitfall 2: surfacing it invites someone to fill in a value that means nothing in that mode | Product Owner |
| 2026-08-24 | The quarter is **computed at read time**, never stored on other entities | Storing it would leave stale quarter labels scattered across tables whenever a fiscal year is amended | Product Owner |
| 2026-09-03 | **`US-FND-REF-005` unblocked** and rewritten around `seconds_per_unit`, declared as quantity + fixed base unit | T-4 was resolved by `foundation-domain` on 2026-09-02; [AGENTS.md §5 rule 1b](https://github.com/molca-id/docs-molcadx/blob/main/AGENTS.md#5-aturan-kerja-berlaku-untuk-semua-agent) requires a BLOCKED story to be reopened in the same session its blocker clears | Product Owner |
| 2026-09-03 | `US-FND-SHF-001` retitled and rewritten onto `ENTERPRISE_SHIFT` / `SITE_SHIFT`; `US-FND-SHF-002` reads `SITE_SHIFT` | The story still described `SHIFT_DEFINITION`, `SHIFT_PATTERN`, `break_minutes`, a stored `crosses_midnight`, and `scheduled_minutes` — all retired in F-02. `SITE_SHIFT` no-overlap rule (2026-09-02) added as an AC | Product Owner |
| 2026-09-03 | `US-FND-PRO-004` split `uom_id` (item) from `time_conversion_id` (time) and gained throughput entry | F-06 §9.3 resolved both on 2026-09-02; the story still assumed one compound "seconds/pack" unit | Product Owner |
| 2026-09-03 | `US-FND-SIT-001` synced to UX 01: Tree/Diagram/Table, ⋮ dialogs, read-only pane, missing-shift badge; `REF` stories reference their own screens `SCR-FND-REF-001..004` | UX decisions of 2026-09-02 changed screen states the Expectation rows describe | Product Owner |
| 2026-09-04 | **`US-FND-AST-005`** (record component swaps as installation history) added, covering `ASSET_COMPONENT_LINK` | `ASSET_COMPONENT_LINK` has had a full entity spec in F-04 §9.5 since before this PRD existed but had never been given a screen or a story anywhere — a real gap found by a `prd-pilot` diagnostic run. `foundation-domain` added the matching screen, `SCR-FND-AST-006`, the same session | Product Owner |
| 2026-09-04 | **`US-FND-INT-001` closed; `US-FND-DSR-001` added** in a new `DSR` module, carrying all of `US-FND-INT-001`'s `DATA_SOURCE`/`ASSET_TAGS` content verbatim | Approved via GitHub issue #20. `US-FND-INT-001`'s own 2026-09-03 note had already proposed this split and left it as a PO decision; a narrowed ERP-only `US-FND-INT-001` would have been hollow (`TBD` in every section) because [F-11](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md) has no ERP content specified anywhere — closed per the `US-FND-AST-004` precedent instead of left as a shell. `US-FND-INT-002`/`US-FND-INT-003` Dependencies updated to `US-FND-DSR-001` | Product Owner |
| 2026-09-08 | **`US-FND-KPI-008`** (View KPI calculation breakdown) added, covering `SCR-FND-KPI-004` | The KPI Calculation page (view-only, no new entity) had already been declared in [UX 07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md) and [FLOW-05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/FLOW/05-kpi-computation.md), including the range-detection (`range_bucket`) method's own explanation, with no matching story — a `prd-pilot` diagnostic run confirmed the docs were sufficient to write the story in full. `range_bucket` is folded into this story rather than given its own, because the KPI Calculation page is its only surface. New open question [Q-26](30-release-risks-questions.md) added for K-8 (whether the small_stop/downtime threshold is shared with stall detection's K-6), same treatment as K-6/Q-21 on `US-FND-KPI-006` | Product Owner |
| 2026-09-08 | **Full `prd-pilot` contradiction sync** across PRD-003 (beyond the earlier 6-fix batch) | Align PRD to current DOCS-EN/UX where the pilot found bucket-(c) staleness: AST field names / placement counting; SIT collapse TBD + no `WORK_UNIT.type`; SHF/FYR empty copy + retired screens / no site fiscal override; KPI four timing transforms + `complement_derive` same-asset + Admin/IT persona on KPI-007; WMS deps off closed WMS-001 + no fake dispositions/site scope; PRO/REF/NTF/CFG/WFE IDP-role and empty-copy hygiene; INT-002/003 ownership-BLOCKED; DSR `site_id` + no reading writes; `AGENTS.md`/`02-domains` §3/§4 index (`FYR`, no Organization/calendar in FOUNDATION blurb, KPI stories exist). No new product inventing — open questions stay TBD/BLOCKED | Product Owner |
| 2026-09-29 | PRODUCTION SL-1 resolved: `ASSET_STATE_LOG` per asset, `asset_id` and `work_unit_id` both required | `US-FND-SIT-002` buildable (🟢); B5 now waits only on row ID / state field names and shift split | Product Owner (docs-molcadx #57) |
| 2026-09-30 | **BOM and Routing are Product detail tabs** (hidden without a Production license); **five product types** + packaging level; phantom BOM flag; BOM tab is a tree | Data is FOUNDATION's and shared; one recipe belongs to one product — `US-FND-PRO-001`, `WMS-002`, `WMS-003`, `KPI-007` | Product Owner ([tabs](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-bom-routing-as-product-tabs.md), [types](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-product-type-packaging-phantom-bom-tree.md)) |
| 2026-09-30 | **Net recipe**, `OPERATION.planned_shrinkage`, recipe base quantity; line across work centers (one product per work center, derived unit weight, cycle time per punch); reject by weight → units → time; rejects add up in time | `US-FND-WMS-002`, `WMS-003`, `KPI-003`, `KPI-004` | Product Owner ([net recipe](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-bom-net-recipe-shrinkage-base-qty.md), [line](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-line-across-work-centers-reject-weight.md)) |
| 2026-09-30 | **Work Master waves 1–2** (issue #58): W-7 and W-8 closed (version + status), class-based steps, dependencies, material per step, product codes section, `PRODUCT_FLOW`, `OPERATION_PARAMETER`, `enforce_sequence`; batch / standard rules in F-07 | `WMS-002` and `WMS-003` 🟢; `KPI-002` | Product Owner ([wave 1](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-work-master-wave-1.md), [wave 2](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-work-master-wave-2-flows-parameters.md)) |
| 2026-10-01 | `PRODUCT_DETAIL.weight_uom_id` (Q-20 closed); `step_type` free text; `activity_signal` tag role; knife not modelled | `PRO-002` 🟢; `KPI-001`, `KPI-008`, `DSR-001` | Product Owner ([wrap-up](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-10-01-open-items-wrap-up.md)) |

## 13. Changes and corrections

### 13.1 What this PRD adds

The `FYR` module, 3 stories, covering [F-03](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/03-yearly-quarterly-declaration.md).
Its entity spec has been marked **🟢 ACTIVE, ready to implement**, while `FISCAL_YEAR` appeared **zero
times** across PRD-001 and PRD-002. Without the module, every period report groups by a range nobody
declared.

The module is deliberately narrow. It declares a reporting window and nothing else: no working days, no
production scheduling, no calculation. Both `calendar_basis` modes ship, quarters are always 13 weeks in
`weekly_iso` regardless of pattern, and the quarter is derived at read time so an amendment can never
leave stale labels behind.

### 13.2 Stories closed and retired

| Story | Why closed | Replaced by |
|-------|------------|-------------|
| `US-FND-PRO-004` Manage per-unit reference cycle times | Retired 2026-09-28 (PO, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) C): `PRODUCT_CYCLE` retired — nothing read it and it could contradict `OPERATION`; `SCR-FND-PRO-002` retired | Cycle time per item on `OPERATION` (`uom_id` = item unit), [`US-FND-WMS-003`](08-work-master.md#us-fnd-wms-003) |
| `US-FND-SIT-003` Pick the location context once for the whole app | Retired 2026-09-27 (PO): not a FOUNDATION story; no FOUNDATION app shows the picker. A Production picker is parked on docs-molcadx#46 | — (parked) |
| `US-FND-SHF-003` Manage the working calendar & exception days | Models `SHIFT_CALENDAR` and `NON_WORKING_DAY`. [F-03](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/03-yearly-quarterly-declaration.md#what-this-module-does-not-cover) records the PO retiring that concept **entirely**, because a fixed exception list does not fit when working patterns vary per enterprise, tenant, and site | **Nothing inside FOUNDATION.** Production dates belong to `production-domain` ([Work Schedule](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md)). The fiscal-year half of the old Calendar module became [`FYR`](03-yearly-quarterly-declaration.md) |
| `US-FND-AST-004` Manage the downtime reason code taxonomy | Written against a single `REASON_CODE` table with a seven-value `category`, an `is_planned` boolean, and per-`ASSET_CLASS` scoping. None exist | `US-FND-REF-001`, `US-FND-REF-002`, `US-FND-REF-003` |
| `US-FND-WMS-001` Manage items & units of measure | Used `ITEM`, since renamed `PRODUCT`, and computed `qty × UOM.conversion_factor`, a field in no FOUNDATION file | `US-FND-PRO-001`, `US-FND-REF-004`, `US-FND-PRO-003` |
| `US-FND-ORG-001` Manage org units & employee records | Organization removed from FOUNDATION entirely, 2026-08-24 ([`AGENTS.md` §4](https://github.com/molca-id/docs-molcadx/blob/main/AGENTS.md#4-format-prd--berbasis-user-story)) — roles/access now live in the identity provider, not modeled by FOUNDATION | **Nothing inside FOUNDATION.** Not modeled by any domain in this PRD |
| `US-FND-ORG-002` Grant roles with a scope | Same 2026-08-24 removal | **Nothing inside FOUNDATION.** Identity provider (SSO/Keycloak), external to this PRD |
| `US-FND-ORG-003` Manage crews | Same 2026-08-24 removal — `CREW` had no home even before this, now doubly so | **Nothing yet.** See `US-FND-SHF-004` below |
| `US-FND-INT-001` Declare connectors & tag mappings as configuration | 2026-09-04 (GitHub issue #20): all of its actual content was `DATA_SOURCE`/`ASSET_TAGS` declaration, not ERP integration; a narrowed ERP-only version would have had zero source content in [F-11](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md) | [`US-FND-DSR-001`](13-data-source.md#us-fnd-dsr-001), new `DSR` module. No ERP-connector story exists yet — will get a new ID when scoped |
| `US-FND-AST-005` Record component swaps as installation history | 2026-09-10 ([MOM domain alignment](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-10-mom-domain-alignment.md)): `ASSET_COMPONENT_LINK` is component-swap *history* — a MOM execution/event record, not FOUNDATION master data, the same rule already applied to `WORK_ORDER` on 2026-09-03. Moved out, not deleted — full content preserved verbatim | [`MAINTENANCE/DOCS-EN/01-asset-history.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/MAINTENANCE/DOCS-EN/01-asset-history.md), new `MAINTENANCE` domain. No PRD story exists yet for that domain (rule 1) — a `US-MNT-AST-001` should be written from the preserved content once one is commissioned |

All eight IDs above are retired and never reused. `US-FND-SHF-002` was corrected in place rather than closed,
because only its calendar dependency was wrong, not its purpose — though this audit (2026-08-28) notes
`02-shift.md` itself now states `SHIFT_INSTANCE` generation's *mechanism* is out of FOUNDATION's scope
entirely (owned by `production-domain`), which `US-FND-SHF-002` as currently written does not yet reflect;
not resolved in this pass.

`US-FND-SHF-004` Assign crews to shift instances is **blocked, not retired** — it keeps its ID pending
`TBD — perlu konfirmasi PO` on what "crew" refers to now that `CREW`/Organization are gone. See its entry
in [§6.5](02-shift.md).

### 13.3 Inherited from PRD-001 v0.2

The Availability formula in the `KPI` module is `APT / PBT`, corrected on 2026-08-24 from
`operating_time / scheduled_time`. The full account, including the worked comparison showing 90.0% against
88.3% and the 60 maintenance minutes that used to vanish, is in
[PRD-001 v0.2 §13.1](../PRD-001-molcadx-core-q3-en.md#131-the-availability-formula-that-was-corrected). It is
not repeated here, so there is one account of that correction rather than two that can diverge.

### 13.4 Synchronised to FOUNDATION decisions (2026-09-03)

Applied under [AGENTS.md §5 rule 1b](https://github.com/molca-id/docs-molcadx/blob/main/AGENTS.md#5-aturan-kerja-berlaku-untuk-semua-agent), after this document was found
to lag decisions already made in `DOMAINS/FOUNDATION/DOCS-EN/` and `UX/` on 2026-09-02/03. The same changes were applied to
[PRD-001 v0.2](../PRD-001-molcadx-core-q3-en.md) in the same session (sync rule at the head of this file).

| Story | What changed | Source of truth |
|-------|--------------|-----------------|
| `US-FND-REF-005` | BLOCKED banner removed; Expectation, Calculation, Data, Rules, AC, Metrics written in full around `seconds_per_unit`; target unit **seconds**, not hours | F-05 §9.3, §9.6 (T-4 resolved) |
| `US-FND-PRO-004` | `uom_id` = item only; new `time_conversion_id` FK; resolved seconds = `cycle_time_value × seconds_per_unit`; throughput entry mode (`seconds_per_unit ÷ rate`, stored against the seconds row) | F-06 §9.3; UX 06 `SCR-FND-PRO-002` |
| `US-FND-SIT-001` | Tree / Diagram / Table view modes; ⋮ menu with Edit / Add {child kind} dialogs; read-only detail pane; no left-pane collapse; solid alert badge on `SITE` nodes with incomplete `SITE_SHIFT` adoption (new AC-7); `default_shift_calendar_id` dropped from Write | UX 01 |
| `US-FND-SHF-001` | Retitled; rewritten onto `ENTERPRISE_SHIFT` (label, `is_production_shift`, editable, delete blocked while adopted) and `SITE_SHIFT` (`start_time`/`end_time`, `crosses_midnight` derived, no break, no rotation, no `scheduled_minutes`); no-overlap-within-site AC | F-02 core concepts 1–2, 5–6, §9.1–9.2, H-1/H-2/H-5; UX 02 |
| `US-FND-SHF-002` | Reads `SITE_SHIFT`/`ENTERPRISE_SHIFT`; writes F-02 §9.3 fields only; per site, not per line; `US-FND-SHF-003` dependency removed | F-02 §9.3 |
| `US-FND-SHF-004` | `SHIFT_PATTERN`-proposed crew removed (dependent of `US-FND-SHF-001`); how a default crew is proposed is `TBD — perlu konfirmasi PO` | F-02 core concept 2 |
| `US-FND-REF-001`, `002`, `004` | Each taxonomy is its own nav entry and screen (`SCR-FND-REF-003`, `004`, `001`), not a combined switcher | UX 05 |
| `US-FND-FYR-001` | Checked, no change — already states enterprise level only, no site override (Y-2), consistent with UX 03 | F-03, UX 03 |

**Left as `TBD — perlu konfirmasi PO` because the domain docs do not answer them:** deactivation field on `TIME_CONVERSIONS`
(F-05 §9.5 vs §9.3); uniqueness of `seconds_per_unit`; stored precision of a derived `PRODUCT_CYCLE.cycle_time_value`;
`SITE_SHIFT` `end_time` inclusive-vs-boundary and `start_time = end_time`; crew pre-fill without a rotation.

### 13.5 Organization removed (2026-09-03)

A `prd-pilot` doc-vs-PRD comparison found that this document had resurrected `Organization` (`ORG`) as a
13th in-scope module — mapped to [F-00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md), which is
FOUNDATION's global-rules map, not an Organization spec — with three fully-specced stories
(`US-FND-ORG-001/002/003`) building `CREW`, `ROLE_ASSIGNMENT`, and `USER_ACCOUNT` as live FOUNDATION
entities. Both `AGENTS.md` §4 and [01-glossary.md](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/01-glossary.md) already record Organization as
removed from FOUNDATION scope on **2026-08-24** — roles and access moved entirely to the identity provider.
The three `US-FND-ORG-*` stories, and the `ORG` row in [§4](README.md), were never valid scope for this
document; they are removed outright, not closed (closing implies they were once correctly in scope).

**What depended on them, and how each dependency was resolved:**

| Dependent | What it needed from `ORG` | Resolution |
|---|---|---|
| `US-FND-SIT-001` missing-shift badge, context picker | Nothing structural — no change | — |
| `US-FND-SIT-003` (pick location context) | Read `ROLE_ASSIGNMENT`/`ROLE` to compute a subject's effective scope | Rewritten to resolve scope from the subject's IDP-asserted claim, per the [ABAC contract](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp) — FOUNDATION supplies only the resource-scope side of that check |
| `US-FND-SHF-004` (assign crews to shift instances) | `CREW`, `EMPLOYEE` as FOUNDATION entities to assign | **BLOCKED** — `CREW` has no FOUNDATION entity ([01-glossary.md](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/01-glossary.md)); no replacement entity is invented here. See the story's own BLOCKED notice |
| `US-FND-WFE-001`, `US-FND-NTF-001`, `US-FND-CFG-001` (each depended on `US-FND-ORG-002` for role granting) | An implied "roles get granted somewhere" precondition | Dependency removed; each story's Access line now cites the ABAC contract directly instead of a FOUNDATION role-grant story |
| `US-FND-AST-002` (`owner_org_unit_id`) | `ORG_UNIT` as the FK target | `owner_org_unit_id` is a real [F-04 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#94-asset--core-attributes) field pointing at an entity that no longer exists — flagged `TBD — perlu konfirmasi foundation-domain`, not invented a replacement |
| §7.1/§7.2/§8 cross-cutting tables | Rows citing `US-FND-ORG-*`, `ORG_UNIT`, `CREW`, `ROLE_ASSIGNMENT`, `USER_ACCOUNT` | Rows removed; the `access_denied` event's `role_held` property renamed `claim_scope_held` to match the ABAC vocabulary |

**Why this matters beyond bookkeeping:** every story in this document previously asserted concrete role
names (`admin`, `supervisor`, `plant_manager`) in its Access line with no FOUNDATION doc to back them —
`00-foundation.md` has always said FOUNDATION doesn't model roles. The ABAC contract added 2026-09-03 is
the resolution: every story's Access line now states a scope tier and an action, not a role name. See the
per-story edits throughout §6 and the access-control note at the top of [§7.3](20-cross-cutting.md#73-access-control).

### 13.6 `ASSET_COMPONENT_LINK` gap closed (2026-09-04)

A `prd-pilot` diagnostic run against the Asset Ontology module found that `ASSET_COMPONENT_LINK` —
specced in full in [F-04 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#95-component-structure)
since before this PRD existed — had no screen and no story anywhere in either PRD. Not a duplicate, not a
rename: a genuine gap. `foundation-domain` added the screen, `SCR-FND-AST-006` ("Component installation
history"), to [`UX/04-asset.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/04-asset.md); this document adds the matching
story, [`US-FND-AST-005`](90-closed-stories.md#us-fnd-ast-005), the same
session. `US-FND-AST-003` (asset placement history) is the closest structural precedent — same
"history belongs to the position, not the thing" rule, one level deeper.

**Left open, not assumed:** `UX-AST-4` ([`UX/04-asset.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/04-asset.md), owner
`foundation-domain`) — whether `ASSET.component_level` must always agree with the depth implied by
`parent_asset_id`, or the two can drift. `US-FND-AST-005`'s Rule ③ and AC-4 describe the screen's current
assumption (filter `Part`-level assets out of the parent picker outright) as provisional, pending that
answer.

**Follow-up, 2026-09-10:** `US-FND-AST-005` and `SCR-FND-AST-006` moved out of FOUNDATION to the new
Maintenance domain (now `SCR-MNT-AST-001`) — see [§13.2](#132-stories-closed-and-retired) and the
[MOM domain alignment decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-10-mom-domain-alignment.md). The gap this
section closed is not reopened by the move; the story and screen still exist, just outside this PRD.
`UX-AST-4` (renamed `UX-MNT-1`) is still `foundation-domain`'s to resolve — only the screen's home moved.

### 13.7 Synchronised to B1 closure (2026-09-11)

Applied under [AGENTS.md §5 rule 1b](https://github.com/molca-id/docs-molcadx/blob/main/AGENTS.md#5-aturan-kerja-berlaku-untuk-semua-agent):
[B1](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-10-b1-shift-instance-generator-owner.md) (`SHIFT_INSTANCE`
generator ownership) closed 2026-09-10, assigning it to `production-domain`. F-02 (Shift)
reverted `DRAFT` → `ACTIVE` the same day.

| Story | What changed | Source of truth |
|-------|--------------|-----------------|
| `US-FND-SHF-002` | Blocking banner removed (was: "do not start implementation until PO confirms ownership"). Context/Calculation updated from "working assumption" to confirmed: `production-domain` owns the generation mechanism | [F-02 §H-6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#94-open-questions), [B1 decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-10-b1-shift-instance-generator-owner.md) |

**Not touched by this sync:** the story's Calculation/Rules/ACs describing *what* gets generated
(timing, uniqueness, idempotency) were already correct and unaffected — only the ownership
hedge language changed. `TENANT`'s new `keycloak_org_id` field (same decision batch, separate
[decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-11-tenant-keycloak-organizations.md)) touches no
FOUNDATION story in this PRD — no story in scope reads or writes `TENANT` fields, so no sync
was needed there; checked, not assumed.

### 13.8 Synchronised to the Work Master license-gate decision (2026-09-12)

Applied under [AGENTS.md §5 rule 1b](https://github.com/molca-id/docs-molcadx/blob/main/AGENTS.md#5-aturan-kerja-berlaku-untuk-semua-agent):
Work Master (`WMS`) was decided to be license-gated by **Production**, not FOUNDATION, even
though FOUNDATION owns its data (`ROUTING`/`OPERATION`/`BOM`/`QUALITY_DEFECT_CODE`) — see
[`PLATFORM/DOCS-EN/01-license-manager.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PLATFORM/DOCS-EN/01-license-manager.md#module-license-gate--data-ownership)
and the generic check in
[`PLATFORM/FLOW/01-module-entitlement-check.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PLATFORM/FLOW/01-module-entitlement-check.md).
Found via a `prd-pilot` run against `WMS` specifically to check whether this decision (made
the same session, in `DOMAINS/FOUNDATION/UX/08-work-master.md`) had made it into this PRD yet
— it hadn't.

| Story | What changed | Source of truth |
|-------|--------------|-----------------|
| `US-FND-WMS-002`, `US-FND-WMS-003`, `US-FND-WMS-005` | Added a **"No entitlement" (license-gate)** state to Expectation, distinct from the existing "No permission" (ABAC) state — visible-but-locked when the tenant lacks a Production license, independent of the subject's own scope. Added a matching Acceptance Criterion to each story | [`FOUNDATION/UX/08-work-master.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md) license-gate note, [`PLATFORM/FLOW/01-module-entitlement-check.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PLATFORM/FLOW/01-module-entitlement-check.md) |

**Not touched by this sync:** `US-FND-WMS-001` (closed/split) and `US-FND-WMS-004` (moved to
PRODUCTION) — neither is a live FOUNDATION story. The entitlement mechanism itself
(`TENANT_DOMAIN_ENTITLEMENT`) is still a sketch in `PLT-01` §9, not built — these three
stories describe the *behavior*, which is decided, not the schema, which isn't.
