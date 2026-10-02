# PRD-003 · Release, risks and open questions

> Part of [PRD-003 FOUNDATION](README.md). What gets built in which order, what could go wrong, and what is
> still undecided. Per-story open items are also listed at the top of each module file.

## 9. Release

Stages are ordered by data dependency: a stage can't start until the stages it depends on are done.

| Stage | Contents | Done when | Depends on |
|-------|----------|-----------|------------|
| **F1** | `SIT` (3; `SIT-003` retired 2026-09-27), `AUD-001` | The pilot site's hierarchy exists down to work unit, and every write to it already leaves an audit row | Nothing. This is the first work in any implementation. `AUD-001` ships with the first screen: audit rows can't be written after the fact. `AUD-002`–`004` (History, Audit log, download) follow in any later stage |
| **F2** | `AST` (3), `SHF` (2; `SHF-004` retired 2026-09-25), `FYR` (3) | Assets registered and placed; shift definitions and instances generated; a fiscal year covering the pilot period declared | F1. `FYR` needs only `SIT` and can run in parallel with `AST` |
| **F3** | `REF` (6), `PRO` (3; `PRO-004` retired 2026-09-28) | Downtime and reject taxonomies populated from the Operations list; every pilot product has a base unit and its conversions | F2, plus **T-1 answered by Operations**, the earliest hard blocker |
| **F4** | `WMS` (3) | Routings and `OPERATION` standards exist for every pilot product at its work unit | F3. Standard cycle times may not exist on the floor at all |
| **F5** | `KPI` (12; `KPI-001`, `004`, `005` blocked on B5; `KPI-006` partly blocked on Q-31 / Q-32) | Availability computes for the pilot line on every closed shift; Quality and reliability follow | F2 for Availability, F4 for Performance and therefore OEE. Availability also needs `ASSET_STATE_LOG` specified (B5: owner PRODUCTION since 2026-09-28; its [spec](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time) is a draft with `TBD` fields and recording level SL-1) |
| **F6** | `WFE` (4), `NTF` (3), `INT` (2), `CFG` (3) | **Cannot start.** These modules have no modelled entities | `foundation-domain` modelling the four platform modules first ([F-Q-03](#11-open-questions)) |
| **F6** | `DSR` (1) | `DATA_SOURCE`/`ASSET_TAGS` are modelled, but `US-FND-DSR-001` depends on `US-FND-CFG-001`, which is in F6 | Same as F6: `foundation-domain` modelling `CFG` |
| **F7** | `US-FND-KPI-006` | Thresholds set 2026-10-01 (K-6); waiting on [Q-31 / Q-32](#11-open-questions) and B5 | Threshold storage and the duration-vs-reason rule |

**Why `FYR` sits in F2, not later.** Its only dependency is `US-FND-SIT-001`, and nothing needs it until
the first period report. But a fiscal year declared *after* production starts can't be applied to dates
already closed without triggering the amendment refusal in
[`US-FND-FYR-002`](03-yearly-quarterly-declaration.md#us-fnd-fyr-002).
Declaring it late is expensive in a way that is easy to miss.

**Rollback.** Each module can be switched off on its own, except where the table shows a dependency.
Switching off `KPI` leaves master data intact. Switching off `REF` breaks downtime recording in
PRODUCTION, so it can't be rolled back once the pilot line is live.

## 10. Risks

| # | Risk | Impact | Likelihood | Mitigation | Owner |
|---|------|--------|------------|------------|-------|
| **F-R-02** | The real `DOWNTIME_REASON` / `REJECT_REASON` lists are unknown ([T-1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#96-whats-still-unknown)) and `ops-domain` does not exist | Stage F3 can't complete; downtime analysis collapses into one unlabelled column | **High** | Appoint an interim Operations owner; start from the list the pilot line uses today and correct it after 14 days | `ops-domain`, PO |
| **F-R-03** | 12 stories describe entities that don't exist (`WFE`, `NTF`, `INT`, `CFG`) | They look buildable. A plan estimated from them can't be executed | **High** | Every one is marked 🟠 Concept only in its module file; stage F6 is explicitly "cannot start" | `foundation-domain` |
| **F-R-04** | A fiscal year is declared after production has started | Closed periods can't be relabelled without the amendment refusal, so early production data may have no period label at all | **Medium** | `FYR` placed in stage F2, before any recording; `dates_resolving_to_a_fiscal_year` tracks it from day one | PO |
| **F-R-05** | The retired calendar concept gets rebuilt because a working-day list is genuinely needed | FOUNDATION grows a concern the PO removed, and the boundary with `production-domain` blurs again | **Medium** | `US-FND-SHF-003` is closed with its reasoning in [90-closed-stories.md](90-closed-stories.md); the need belongs to `production-domain` | PO, `production-domain` |
| **F-R-06** | `UNIT_OF_MEASUREMENTS` has no generic conversion factor (by design) | Mixed-unit output can't be reconciled without a per-product conversion row | **Low** | Per-product conversions in `US-FND-PRO-003` | `foundation-domain` |
| **F-R-07** | Planned downtime is subtracted twice, or not at all | Availability is silently wrong in one direction or the other | **Medium** | The shift side is settled: `SHIFT_INSTANCE` never pre-deducts it ([F-02](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md) H-7). The remaining gap is `ASSET_STATE_LOG`: owned by PRODUCTION since 2026-09-28 ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)), its entity spec is still a draft (B5) | `production-domain` |
| **F-R-08** | `design-domain` and `engineering-domain` don't exist | 50 stories have no recipient for screens or schema | **High** | Needs a PO decision on forming them | PO |

> **F-R-01 is retired.** It was "PRD-001 and PRD-003 drift apart". PRD-001 was frozen on 2026-09-03, so
> it no longer needs to be kept in sync; PRD-003 is the only live FOUNDATION PRD. See [99-history.md](99-history.md).
> Risk IDs carry an `F-` prefix so they can't be confused with PRD-001's own `R-01` to `R-23`.

## 11. Open questions

Question numbers are the same as in [PRD-001 v0.2](../PRD-001-molcadx-core-q3-en.md), so an ID means the
same thing in both. Only the questions FOUNDATION stories reference are listed.

### Still open

| # | Question | Owner | Deadline | Blocks |
|---|----------|-------|----------|--------|
| **Q-02** | The IDP-side role vocabulary, specifically `technician` and `planner`. They're used by [P-01 §2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#capabilities-figma) & [P-03 §3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#job-order) but not declared anywhere ([O-2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#open-questions-at-a-glance)). This is an IDP configuration question, not a FOUNDATION `ROLE` table, per the [ABAC contract](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp) | PO | before pilot go-live | `US-PROD-SHF-004`, `US-PROD-JOB-*`, [access control](20-cross-cutting.md#73-access-control) |
| **Q-04** | Are successfully reworked units recounted as `good_qty` ([SF-3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#boundaries), [W-5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown))? If yes, a double-counting rule is required | PO | before `US-PROD-OEE-001` | The Quality metric, `US-PROD-SHF-005`, `US-PROD-OEE-001` |
| **Q-09** | The test definition for KR 1B.2's "0 data loss": how long an outage, how many scenarios, how fast a recovery? Proposal: 8 hours × 3 scenarios, 100% synced ≤15 minutes | PO + `engineering-domain` | before `US-FND-INT-002` | KR 1B.2, [NFRs](20-cross-cutting.md#75-non-functional-requirements-numbers-mandatory) |
| **Q-10** | What is configurable in the first release ([F-12](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#implications-for-molcadx))? Candidates are listed in `US-FND-CFG-001`. **Partly answered 2026-10-01:** the stop thresholds are confirmed ([F-12](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#confirmed-first-release-item-stop-thresholds-po-2026-10-01)); the rest still need the PO | PO | before `US-FND-CFG-001` | `US-FND-CFG-*` and every threshold in other stories |
| **Q-11** | `foundation-domain`'s approval of the **new fields** and **new entities** proposed in the [entity summary](20-cross-cutting.md#72-entity-summary-for-engineering) | PO → `foundation-domain` | before the first sprint | The 12 platform stories, plus corrections and carry-over |
| **Q-21** | What automatic detection still needs: the `product_code` debounce window. (The stall threshold, K-6, is answered 2026-10-01: two [stop thresholds](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#confirmed-first-release-item-stop-thresholds-po-2026-10-01), per work center with work unit override. The event-origin marker on `ASSET_STATE_LOG` is answered 2026-09-28: PRODUCTION's `source` field, a stall-detected row is `derived` — [spec](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)) | `ops-domain`, PO, `production-domain` | before R6 | `US-FND-KPI-002` |
| **Q-22** | How many active products does the pilot line have? Above ~50, `US-FND-PRO-001` needs bulk import, which is out of scope | PO | before R2 | `US-FND-PRO-001` |
| **Q-23** | Is changeover between two SKU segments its own Performance-loss bucket, or a `planned` `DOWNTIME_REASON`? (K-5) | `ops-domain`, PO | before R5 | `US-FND-KPI-002` |
| **Q-24** | What count tolerance makes `good + Σ reject` count as matching `total`? | `ops-domain`, PO | before R4 | `US-FND-KPI-003` |
| **Q-26** | Who owns the normal-operating band (e.g. 40–100) in range detection, where does it live (per `ASSET_TAGS` row, `WORK_UNIT` override, or a Configuration Service setting), and is it effective-dated? This is F-07.1 **K-10**, split out of K-8 on 2026-10-01; the threshold part of K-8 is answered (one shared pair) ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#whats-still-unknown)) | `ops-domain`, PO | before R6 | `US-FND-KPI-008` |
| **Q-31** | F-07.1 **K-11**: (a) a detected stop longer than `small_stop_max_seconds` whose reason has `downtime_category = small_stop` — duration or reason wins? (b) do the stop thresholds also apply to stops an operator opens manually? ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#whats-still-unknown)) | PO + Operations | before R6 | `US-FND-KPI-006` |
| **Q-32** | Where are the stop thresholds stored: a generic config-value table, or a dedicated table for these two values? F-12 has no entity model ([F-12](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#confirmed-first-release-item-stop-thresholds-po-2026-10-01)) | engineering + PO | before R6 | `US-FND-KPI-006`, `US-FND-CFG-001` |
| **F-Q-02** | Does `production-domain` own the working-day / production-date list that `US-FND-SHF-003` used to cover? **Partly answered:** `production-domain` owns the `SHIFT_INSTANCE` generator ([B1 closed 2026-09-10](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-10-b1-shift-instance-generator-owner.md)). Whether it also owns the working-day list is not recorded yet | PO, `production-domain` | before F2 | `US-FND-SHF-002` |
| **F-Q-03** | When will `foundation-domain` model the entities behind `WFE`, `NTF`, `INT` and `CFG`? Until then 12 stories can't be estimated, let alone built | `foundation-domain`, PO | before F6 | 12 stories, and `US-FND-DSR-001` via `CFG-001` |
| **A-11** | Tag hand-off list: file format, a "configured on server" status, and whether exports are kept ([F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address)) | PO + Molca hardware | — | part of `US-FND-DSR-001` |
| **A-13** | `asset_tag` is floor-facing but now an address segment: keep checking its characters only at tag save, or restrict it everywhere? | PO + ops | — | part of `US-FND-AST-002`, `US-FND-DSR-001` |
| **A-14** | Renaming a code or `asset_tag` that is in a live address: warn and confirm (current spec) or block? | PO | — | part of `US-FND-SIT-001`, `US-FND-AST-002` |
| **H-DS-5 / H-DS-6 / H-DS-7** | Namespace URI format; who may export the tag hand-off list; MQTT topic root value when used (2026-09-28) | PO + `foundation-domain` | — | part of `US-FND-DSR-001` |
| **Q-27** | Readiness with versions: does a product whose only routing at the line is `draft` count as ready for Performance, or only one with a `released` routing? | PO / `foundation-domain` | before R2 | `US-FND-PRO-001` |
| **Q-29** | Are tag roles `lot_marker` and `activity_signal` per-lane or machine-wide (F-04 §9.7.1 rule 9)? UX 13 has no fields or text yet for `count_side` or the two roles | PO / `foundation-domain` | before R2 | `US-FND-DSR-001` |
| **Q-30** | F-07.1's Availability section still calls `speed` "the one exception"; range detection now also allows `activity_signal`. Align the wording | `foundation-domain` | — | `US-FND-KPI-001`, `KPI-008` |
| **Q-33** | UX-WMS-4: who owns the record of raw product codes read with no active mapping ("Seen but not mapped": raw value, tag, first and last seen), FOUNDATION or PRODUCTION, and how long is it kept? | PO → `foundation-domain` / `production-domain` | before R2 | `US-FND-WMS-006` AC-11 |
| **Q-34** | UX-WMS-5: a product with two flows and routings at the same work center — which routing's step does a product code resolve to? (interim: the primary flow) | PO → `foundation-domain` | before R2 | `US-FND-WMS-006` |
| **E-1** | How "No overlap" (one active row at a time) is enforced on every table: a PostgreSQL exclusion constraint on the range, or an application check in the same transaction ([F-00 keys rule](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#keys-and-constraints-on-every-table-po-2026-10-01)). Plus the keys questions raised per module: S-8, S-9, H-8, Y-3, A-17–A-19, T-7, T-8, P-8, P-9, K-12–K-15, W-18, W-19, AU-14 | engineering + `foundation-domain` | before the first sprint | every story that writes an effective-dated entity |

### Resolved

| # | Question | Answer |
|---|----------|--------|
| Q-28 | Reject entered by weight: is a manual reject plus the machine's counted output an allowed pair, and may Quality convert units? | **Yes (PO, 2026-10-01).** Manual reject in any unit with a conversion path to `OPERATION.uom_id`; Quality calculates in that one unit; PLC Quality tags still share one unit at binding ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#reject-entered-manually-in-any-unit--convert-to-the-step-unit-and-time-po-2026-09-30-2026-10-01)) |
| K-6 / K-8 (part of Q-21, Q-26) | Stop thresholds: value, scope, one pair or one per detection method? | **PO, 2026-10-01:** `min_stop_seconds` 60 s / `small_stop_max_seconds` 300 s defaults, per work center with work unit override, one pair for stall and range detection ([F-12](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#confirmed-first-release-item-stop-thresholds-po-2026-10-01)) |
| Q-20 | What unit are `min_weight` / `max_weight` / `standard_weight` on `PRODUCT_DETAIL` in? | **`PRODUCT_DETAIL.weight_uom_id`** (a mass unit), PO 2026-10-01 ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-10-01-open-items-wrap-up.md)) |
| Q-16 | What marks a unit's dimension, so a kg-to-piece conversion can be refused? | [F-05 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#94-measurement_group-and-unit_of_measurements): `MEASUREMENT_GROUP` and `UNIT_OF_MEASUREMENTS.measurement_group_id`. `US-FND-REF-004` reads/writes both (2026-09-03) |
| Q-17 | Is `time_conversions` on `TIME_CONVERSIONS` a mistyped numeric field, two merged fields, or a defect? | A defect, dropped. Replaced by the required numeric `seconds_per_unit`, base unit **seconds** (T-4, 2026-09-02). `US-FND-REF-005` is unblocked |
| Q-18 | Are `REJECT_REASON` and `QUALITY_DEFECT_CODE` the same thing? | **Yes (PO, 2026-09-23).** Merged into `REJECT_REASON`; `US-FND-WMS-005` closed into `US-FND-REF-002` ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-23-defect-code-merged-into-reject-reason.md)) |
| B5 (ownership) | Who owns `ASSET_STATE_LOG`? | **PRODUCTION (PO, 2026-09-28)** — only writer; MAINTENANCE reads; FOUNDATION keeps only `DOWNTIME_REASON` and `ASSET_TAGS` ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)). The entity spec itself is still a draft, so B5 stays open as a spec blocker |
| F-Q-01 | Back-port the 3 `FYR` stories into PRD-001? | No longer needed. PRD-001 was frozen on 2026-09-03 and is not kept in sync |

## 14. What to do next

| # | Action | Owner | Unblocks |
|---|--------|-------|----------|
| 1 | Finish the `ASSET_STATE_LOG` spec (B5). Ownership is settled — PRODUCTION, 2026-09-28 ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)); left: state field names (row ID `asset_state_log_id` since 2026-10-01), the recording level SL-1 (was A-5), the shift-split rule ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)). Planned and unplanned downtime, Availability, the `SIT-002` blocker count and stall detection all read it | `production-domain` (SL-1: PO + ops) | Stage F5 and every `KPI` story that reads downtime |
| 2 | Get the real `DOWNTIME_REASON` / `REJECT_REASON` lists (T-1). `ops-domain` doesn't exist, so appoint an interim owner | PO → `ops-domain` | Stage F3 and everything after it |
| 3 | Confirm `production-domain` owns the working-day list ([F-Q-02](#11-open-questions)). `US-FND-SHF-002` depends on it | PO + `production-domain` | Stage F2 |
| 4 | Schedule the modelling of the `WFE`, `NTF`, `INT` and `CFG` entities ([F-Q-03](#11-open-questions)) | `foundation-domain` | 12 stories plus `DSR-001`, stage F6 |
| 5 | Decide whether `design-domain` and `engineering-domain` are created now | Product Owner | Every story in this PRD needs a recipient |
| 6 | Route the open `TBD — perlu konfirmasi PO` items to `foundation-domain`. Each module file lists its own under "Open items in this module" | PO → `foundation-domain` | The stories named in those lists |
