# PRD-003 — FOUNDATION Domain

| | |
|---|---|
| **Status** | 🟡 Draft v0.2 · last synced 2026-09-24 (T-6 disposition on actual; OEE POT/PBT; Part-4 min spine; S-6a clarified) |
| **Specs synced to** | `docs-molcadx@30c7087` (2026-10-01) — the sync point the [`prd-sync`](https://github.com/molca-id/docs-molcadx/blob/main/.claude/skills/prd-sync/SKILL.md) skill diffs from |
| **Owner** | `product-owner` (writes) · `foundation-domain` (owns the data model behind it) |
| **Scope** | **14 modules, 53 live stories.** 13 closed/retired stories are kept only for their IDs |
| **Formula standard** | ISO 22400-2:2014, through [F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md). This PRD defines no formula of its own. **If it disagrees with F-07, F-07 wins** |
| **Other PRDs** | This is the **only live FOUNDATION PRD**. [PRD-001](../PRD-001-molcadx-core-q3-en.md) is frozen (2026-09-03) and is read only for its PRODUCTION stories |

## What FOUNDATION is, in one paragraph

FOUNDATION is the master data every other part of MolcaDx reads: where things are (site → work unit),
what the machines are, when shifts run, which fiscal year a date belongs to, the reason and unit lists,
products, routings, and the KPI formulas. Nobody "uses" FOUNDATION on its own. It succeeds when the pilot
line can start recording its first shift with every number traceable to a defined source. When it's wrong,
the error doesn't show here. It shows up later as an OEE figure that won't reconcile.

## How to read this folder

| You are | Start with | Then |
|---------|-----------|------|
| **Reviewer / PO** | This page | [Release, risks & open questions](30-release-risks-questions.md) |
| **Designer** | [Screen map](20-cross-cutting.md#71-screen-map-for-design) | Section **3 Expectation** of each story in the module file |
| **Developer** | [Entity summary](20-cross-cutting.md#72-entity-summary-for-engineering) | Sections **4 Calculation**, **5 Data & entities**, **6 Rules** and **7 Acceptance criteria** |
| **Anyone asking "why"** | [Problem, users, goals & scope](00-problem-goals-scope.md) | [History](99-history.md) (not needed to build anything) |

Every module file has the same shape:
1. **Open items in this module:** what is still undecided, and who decides it.
2. **Stories:** a one-line table with a **Status** column.
3. **Detail blocks:** each opens with an **In short** line, then the 8 mandatory parts as numbered sections
   ([AGENTS.md §4](https://github.com/molca-id/docs-molcadx/blob/main/AGENTS.md#4-format-prd--berbasis-user-story)): 1 Story · 2 Context ·
   3 Expectation (with a states table) · 4 Calculation · 5 Data & entities · 6 Rules · 7 Acceptance criteria ·
   8 Metrics & events.
4. **Change notes:** history, at the very bottom. Skip it unless you need the "why".

**Status markers**

| Marker | Meaning |
|--------|---------|
| 🟢 Ready | Buildable as written |
| 🟡 Partly blocked | Most of it is buildable; the named part waits on an open item |
| 🔴 Blocked | Can't be built until the named open item is answered |
| 🟠 Concept only | The module has no modelled entities yet. **Don't estimate or build it** |

**One example shift runs through all the numbers.** The `REF`, `PRO` and `KPI` worked examples all use the
same shift, so you can trace a figure from one story to the next: 08:00–16:00 (POT 480 min), 60 min
planned downtime (PBT 420), 42 min unplanned across 3 stops (APT 378), 1,000 units of output with
50 rejects, two SKUs.

## Glossary

Words the stories use often. The full product glossary is [01-glossary.md](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/01-glossary.md).

| Term | Meaning here |
|------|--------------|
| **Implementor** | Plant Admin/IT: the person who sets up master data at a desk before a line starts recording |
| **Node** | One item in the location tree: Enterprise, Site, Area, Work Center or Work Unit |
| **Work Center / Work Unit** | A line (or cell, unit, storage zone) / one station or machine position on it |
| **IDP** | The identity provider (SSO/Keycloak). It knows who the user is and what they may access. FOUNDATION stores no roles |
| **Site access** (also *enterprise*, *area*, *work-center*, *work-unit* and *tenant access*) | The IDP says this user may act on that site (or area, work unit…). Access to a bigger scope covers everything inside it: enterprise or tenant access covers every site. Formally this is an *IDP-asserted scope claim* under the [ABAC contract](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp) |
| **Effective dating** | Rows carry `valid_from` / `valid_to` instead of being overwritten or deleted, so past reports never change |
| **Deactivate** | Set `valid_to`. The row disappears from new-input pickers but stays in history. Master data is never deleted |
| **POT / PBT / APT** | Planned operation time / planned busy time (POT minus planned downtime) / actual production time. ISO 22400-2 time terms, see [KPI](07-kpi.md) |
| **`TBD — perlu konfirmasi …`** | Undecided. Don't build that part; the named owner decides |
| **B5, A-5, Q-21, UX-SIT-3…** | IDs of open items. Each module lists its own at the top; the full list is in [Release, risks & open questions](30-release-risks-questions.md#11-open-questions) |

## Modules

Numbered to match the specs in [`DOMAINS/FOUNDATION/DOCS-EN/`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/README.md).

| # | Module | Code | Live stories | Status | What it gives the rest of MolcaDx |
|---|--------|------|:---:|-----------|-----------------------------------|
| 01 | [Site Hierarchy](01-site-hierarchy.md) | `SIT` | 3 | 🟢 1 · 🟡 2 | Enterprise → Site → Area → Work Center → Work Unit, plus a class list per level for cross-site grouping. Every number's home and every role's scope |
| 02 | [Shift](02-shift.md) | `SHF` | 2 | 🟢 1 · 🟡 1 | Enterprise shift labels, each site's shift hours, and shift instances generated ahead of time |
| 03 | [Yearly & Quarterly Declaration](03-yearly-quarterly-declaration.md) | `FYR` | 3 | 🟢 3 | One fiscal year and quarter split, so "Q1 2026" means one date range everywhere |
| 04 | [Equipment Taxonomy](04-equipment-taxonomy.md) | `AST` | 3 | 🟢 2 · 🟡 1 | Asset class/type tree, registered machines with their floor tag, placement history |
| 05 | [Reference & Master Data](05-reference-master-data.md) | `REF` | 6 | 🟢 5 · 🟡 1 | Downtime and reject reason lists, units of measure, time conversions |
| 06 | [Product](06-product.md) | `PRO` | 3 | 🟢 3 | One product list with base units, attributes and per-product unit conversions; five product types with packaging level; Product detail also holds the BOM and Routing tabs (reference cycle times retired 2026-09-28) |
| 07 | [KPI](07-kpi.md) | `KPI` | 13 | 🟢 6 · 🟡 4 · 🔴 3 | Availability, Performance, Quality, OEE, MTTR/MTBF, five ISO ratios (incl. Fall-off per Job Order), tag-to-formula binding by domain |
| 08 | [Work Master](08-work-master.md) | `WMS` | 3 | 🟢 2 · 🟡 1 | BOM (shown as a tree) and Routing as tabs on the Product detail page; versions with status (W-8 closed); routings with time-versioned cycle times per item unit ("20 seconds / pack"); machine product codes mapped per product; the only cycle-time source (defect codes are reject reasons now, in `REF`) |
| 09 | [Workflow Engine](09-workflow-engine.md) | `WFE` | 4 | 🟠 4 | No-code workflows for plant SOPs |
| 10 | [Notification](10-notification.md) | `NTF` | 3 | 🟠 3 | Rules for who gets told what, with acknowledgement and escalation |
| 11 | [Integration](11-integration.md) | `INT` | 2 | 🔴 2 (also concept only) | Offline sync and data-acquisition health |
| 12 | [Configuration Service](12-configuration-service.md) | `CFG` | 3 | 🟠 3 | Tiered settings, history and rollback, modules on/off per plant |
| 13 | [Data Source](13-data-source.md) | `DSR` | 1 | 🔴 1 (waits on `CFG`) | Declare a machine connection and map its tags to assets |
| 14 | [Audit Trail](14-audit-trail.md) | `AUD` | 4 | 🟢 1 · 🟡 3 | One audit trail for the whole system: who created, changed, deactivated or acted on any record, when, why, and from when it applies. History on every record, plus one searchable, downloadable Audit log. Read access per module |
| — | [Closed & retired](90-closed-stories.md) | — | 0 | — | 13 IDs kept so they are never reused. **Do not build** |

**Totals: 24 🟢 ready · 13 🟡 partly blocked · 6 🔴 blocked · 10 🟠 concept only = 53.** The 10 modelled
modules hold 41 stories; the 4 concept-only modules hold 12. Each story's status and reason is in its
module's Stories table.

## Build order

```
F1  SIT + AUD-001 ──► F2  AST · SHF · FYR ──► F3  REF · PRO ──► F4  WMS ──► F5  KPI
                                          ▲
                              T-1: real reason lists from Operations

F6  WFE · NTF · INT · CFG · DSR   cannot start: 4 modules have no data model
F7  KPI-006                        thresholds set (K-6); waits on threshold storage (F-12) and B5
```

Details and the "done when" for each stage: [Release](30-release-risks-questions.md#9-release).

## What is stopping this

In order of how much they block:

1. **`ASSET_STATE_LOG` has no finished entity spec (B5).** Ownership is settled since 2026-09-28: PRODUCTION
   owns and writes it, MAINTENANCE reads it ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)). Its spec in
   [PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time) is a draft: state field names (row ID `asset_state_log_id` since 2026-10-01), the recording level (SL-1, was A-5)
   and the shift-split rule are `TBD`. Every downtime figure reads it: Availability, the OEE loss waterfall,
   MTTR/MTBF, and the blocker check when deactivating a line. Planned downtime is settled on the shift side (`SHIFT_INSTANCE` never deducts it,
   F-02 H-7), so this entity is the remaining gap.
2. **The real downtime and reject reason lists are unknown (T-1).** Their owner, `ops-domain`, doesn't exist
   yet. This blocks stage F3 and everything after it.
3. **Four platform modules have no data model** (`WFE`, `NTF`, `INT`, `CFG`). Their 12 stories, and
   `DSR-001` through its dependency on `CFG-001`, can't be estimated until `foundation-domain` models them.
4. **Individual blocked stories:** `KPI-001`, `KPI-004`, `KPI-005` (B5, item 1), `KPI-006` is now partly
   blocked (thresholds set 2026-10-01; storage `TBD` for engineering, K-11 open), `INT-002` / `INT-003` (ownership moved to PRODUCTION and `DSR`), and `DSR-001` (waits on `CFG-001`).
5. **No one to hand it to.** `design-domain` and `engineering-domain` don't exist yet.

The full list of open questions is in [Release, risks & open questions §11](30-release-risks-questions.md#11-open-questions).

## Dependencies outside FOUNDATION

Four KPI stories depend on two PRODUCTION stories in the frozen [PRD-001](../PRD-001-molcadx-core-q3-en.md).
**Those `US-PROD-*` IDs are retired (2026-09-24).** PRODUCTION's own PRD is now [PRD-004](../PRD-004-production/README.md);
the output and downtime capture stories that replace them belong to Production Monitoring (`MON`), not written yet:

| PRODUCTION story | What it provides | Needed by |
|------------------|------------------|-----------|
| `US-PROD-SHF-001` | Output recorded | `KPI-002`, `KPI-003`, `KPI-011` |
| `US-PROD-SHF-003` | Downtime recorded | `KPI-001` |

`US-PROD-OEE-001` (line OEE screen) and `US-PROD-JOB-005` (work orders, moved out of `WMS`) are cited for
context, not as dependencies.

## Files in this folder

| File | Contents |
|------|----------|
| [00-problem-goals-scope.md](00-problem-goals-scope.md) | Problem, personas, goals & metrics, scope, assumptions |
| `01-` … `14-` | One file per module: open items, stories, detail blocks |
| [20-cross-cutting.md](20-cross-cutting.md) | Screen map, entity summary, access control, offline, NFRs, migration, instrumentation |
| [30-release-risks-questions.md](30-release-risks-questions.md) | Release stages, risks, open questions, next steps |
| [90-closed-stories.md](90-closed-stories.md) | Closed, retired and moved stories. Not to be built |
| [99-history.md](99-history.md) | Version notes, decision log, sync records |
