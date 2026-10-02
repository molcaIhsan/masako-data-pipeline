# PRD-004 · Release, risks and open questions

> Part of [PRD-004 PRODUCTION](README.md).

## 1. Release

Stages follow data dependency. A stage can't start before the stages it depends on.

| Stage | Contents | Done when | Depends on |
|-------|----------|-----------|------------|
| **P1** | `PLN-001`, `PLN-003`, `PLN-011` (log as it goes) | The pilot line's Job Orders are created and released in MolcaDx | PRD-003 stages F1–F4 (hierarchy, shifts, products, routings) |
| **P2** | `PLN-002`, `PLN-004`, `PLN-005`, `PLN-006`, `PLN-007` | A day is uploaded, released, followed live and downloaded | P1; P-01 for queue order; M-01 for live achievement |
| **P3** | `MON-004`, `MON-005`, `MON-006`, `MON-007`, `MON-001`, `MON-002` | Rejects and rework are visible and corrected during the shift; the dashboard shows output and Quality | P2; PRD-003 `US-FND-REF-002`, `US-FND-KPI-003` |
| **P4** | `PRF-001`, `PRF-002`, `PRF-003`, `PRF-007` | Live OEE and reports show every Production KPI that `KPI_RESULT` computes | P3; PRD-003 KPI stories. **Availability, OEE and reliability wait on B5** |
| **P5** | `PLN-008`, `PLN-009`, `PLN-010`, `MON-003`, `PRF-004`, `PRF-005`, `PRF-006` | **Cannot start** | P-06, P-07, B5, R-02, R-03, R-04 |

**Rollback.** Planning can't be switched off once the pilot line runs on it: Monitoring and Performance read its
Job Orders. Monitoring and Performance can each be switched off without losing data.

## 2. Risks

| # | Risk | Impact | Likelihood | Mitigation | Owner |
|---|------|--------|------------|------------|-------|
| **P-R-01** | `ASSET_STATE_LOG` spec stays a draft (B5). Owner is PRODUCTION since 2026-09-28 ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)); SL-1 resolved 2026-09-29, state field name `TBD` (row ID `asset_state_log_id`) | No Availability, OEE, downtime losses or live state; PRF shows half its numbers | **High** | Build the Quality / Performance / Throughput parts first (stages P3–P4); `production-domain` closes SL-2…SL-4 in [02-monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time) | `production-domain` |
| **P-R-02** | Nobody specifies how output arrives (M-01) | Achievement and dashboards stay empty on the pilot line | **High** | Decide before stage P2 whether operators ever enter output | PO, `production-domain` |
| **P-R-03** | Breaktime conflict (P-06) settled late | Break data stored twice or in the wrong place; POT differs between domains | **Medium** | Keep `PLN-008`/`009` blocked; decide before any break is recorded | PO |
| **P-R-04** | Planning in MolcaDx is slower than the spreadsheet | Planners stop using it; Goal 1 fails | **Medium** | Upload (`PLN-002`) in stage P2; counter-metric `time_to_schedule_one_shift` | PO |
| **P-R-05** | No planner role in the IDP (P-09) | Access rules are wrong or too broad | **Medium** | Supervisor plays planner until Q-02 closes | PO |
| **P-R-06** | `design-domain` and `engineering-domain` don't exist | 25 stories have no recipient for screens or schema | **High** | PO decision on forming them (same as PRD-003 F-R-08) | PO |
| **P-R-07** | Each module is built in a different repo (implementation repos still `TBD`) | Shared entities (`WORK_ORDER`, log rows) get defined twice | **Medium** | Entity owner per the [entity summary](20-cross-cutting.md#2-entity-summary-for-engineering); name the repos in the README | PO |

## 3. Open questions

IDs are kept from the module files so they mean the same thing everywhere.

| # | Question | Owner | Blocks |
|---|----------|-------|--------|
| **P-01** | Which field holds the queue position (1st, 2nd, 3rd) per work unit? | `production-domain` | `PLN-002`, `004`, `005` |
| **P-02** | Is one uploaded row one Job Order, or does a target per shift need a field? | `production-domain` | `PLN-002` |
| **P-03** | Upload file type, template, mandatory fields (WM-P-2 — parked 2026-09-27: must every `OPERATION` carry its standard before dispatch? setup time / crew size optional since F-08 W-7, 2026-09-30), and where production notes are stored | PO + ops | `PLN-002` |
| **P-04** | Is "schedule approval" the release action? | PO | `PLN-003` |
| **P-05** | Does achievement count all output or good units only? | PO | `PLN-004`, `007`, `MON-001` |
| **P-06** | Break = planned downtime in `ASSET_STATE_LOG` (F-02 H-2) or excluded from POT (PRODUCTION 01-planning)? And which entity stores break windows? | PO → `foundation-domain` + `production-domain` | `PLN-008`, `009` |
| **P-07** | Data source and hard-block-or-warn for each readiness check | PO + ops | `PLN-010` |
| ~~**P-08**~~ | Resolved 2026-09-25: one shared FOUNDATION `AUDIT_LOG` for every domain, read per module | — | — |
| **R-05** | Should the Performance log show Monitoring's reason-edit and rework rows, and with which permission? | PO | `PRF-007` |
| ~~**P-10**~~ | Resolved 2026-09-25 (PO): a domain's own downloads are business actions (`schedule_downloaded`, `report_downloaded`); `download` = audit rows only | — | — |
| **P-09** | Add *planner* to the product personas and the IDP roles (PRD-003 Q-02) | PO | access rules across PLN |
| **M-01** | How output reaches `WORK_ORDER_OPERATION`, and whether operators ever enter it | PO + `production-domain` | `PLN-004`, `MON-001`, `002` |
| **M-02** | What the dashboard ranking ranks by | PO | `MON-002` |
| **M-03** | Are speed losses listed per event, and from what data? | PO | `MON-004` |
| **R-01** | Where KPI Selection per domain is stored | `foundation-domain` | all PRF |
| **R-02** | KPI target entity (per KPI, scope, period) | PO + `foundation-domain` | `PRF-004` |
| **R-03** | Report lifecycle: approve, override, reset — and what "override" means for a computed KPI | PO + `production-domain` | `PRF-005`, `PRF-007` |
| **R-04** | Failure redefinition rules | PO | `PRF-006` |
| ~~**B3**~~ | Resolved 2026-10-01 (PO, PLN-Q6): `routing_id` / `bom_id` pin a released version; lock fields removed | — | — |
| **P-11** | Stage-order drafts: are `planned_start` / `planned_end` derived from the finished-good order's window, or left for the planner? | PO + `production-domain` | `PLN-003` |
| **M-04** | Reject rows now carry `uom_id`: are `scrap_qty` / `rework_qty` stored converted to the operation's unit, or summed per unit? | PO + `production-domain` | `MON-004`, `005`, `006`, `PLN-004` |
| **M-05** | `origin = manual`: are `entered_by` / `entered_at` required? Which `origin` does a supervisor's correction of a `manual` row write? | `production-domain` | `MON-005`, `006` |
| **R-07** | Which `KPI_RESULT` field or status flags "pending, batch open" and estimated Quality? How does reject time reach the Quality page for multi-work-unit rollups? | PO → `foundation-domain` | `PRF-001`, `002`, `MON-001` |
| **B5 (SL-2…SL-4)** | `ASSET_STATE_LOG` entity spec. Owner settled 2026-09-28: PRODUCTION; SL-1 resolved 2026-09-29. Open: row ID / state field names, Maintenance-only tenant SL-2, break wording SL-3 (= P-06), offline queueing SL-4, late sensor rows after a manual row, shift-split rule ([open questions](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#open-questions)) | `production-domain` (SL-4: PO + ops; SL-2/SL-3: PO) | `MON-003`, `004`, `005`, PRF |
| **P-2** | `site_id` on `WORK_ORDER` | `production-domain` | `PLN-001` |
| **Q-04** | (from PRD-003) Are reworked units recounted as good? | PO | Quality in PRF |

## 4. What to do next

1. PO: decide P-04, P-05, P-06, P-09 and M-01. These block the most stories.
2. PO: name the implementation repo for each module (README module table).
3. `production-domain`: add the fields for P-01 and P-03 to [01-planning](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md), then sync the stories (rule 1b).
4. `production-domain`: finish the `ASSET_STATE_LOG` spec (B5, SL-2…SL-4), shared with PRD-003.
5. Task breakdown for stages P1–P3, once the above are in, in the same format as PRD-003's.
