# PRD-004 — PRODUCTION Domain

| | |
|---|---|
| **Status** | 🟡 Draft v0.1 · started 2026-09-24 · all 3 modules written |
| **Specs synced to** | `docs-molcadx@30c7087` (2026-10-01) — the sync point the [`prd-sync`](https://github.com/molca-id/docs-molcadx/blob/main/.claude/skills/prd-sync/SKILL.md) skill diffs from |
| **Owner** | `product-owner` (writes) · `production-domain` (owns the flows and entities behind it) |
| **Scope** | **3 modules, 25 stories:** Planning 11 · Monitoring 7 · Performance Analysis 7 |
| **Source** | The PO's Figma "Production" diagram (2026-09-24) and the PRODUCTION specs in [`docs-molcadx` DOMAINS/PRODUCTION/DOCS](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/00-overview.md) |
| **Formula standard** | ISO 22400-2, through FOUNDATION [F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md). **If this PRD disagrees with F-07, F-07 wins** |
| **Other PRDs** | Replaces the PRODUCTION part of frozen [PRD-001](../PRD-001-molcadx-core-q3-en.md). Its `US-PROD-SHF-*`, `US-PROD-OEE-*` and `US-PROD-JOB-*` IDs are retired and never reused. FOUNDATION is [PRD-003](../PRD-003-foundation/README.md) |

## What PRODUCTION is, in one paragraph

PRODUCTION is where the plan meets the floor. It takes the master data from FOUNDATION (sites, machines,
products, recipes, shifts) and turns it into Job Orders the floor runs, a live view of what is running and what
is lost, and the OEE and KPI analysis afterwards. It owns no master data. It succeeds when the pilot line's day
is planned, dispatched and measured in MolcaDx instead of on a whiteboard and a spreadsheet.

## How to read this folder

| You are | Start with | Then |
|---------|-----------|------|
| **Reviewer / PO** | This page | [Release, risks & open questions](30-release-risks-questions.md) |
| **Designer** | [Screen map](20-cross-cutting.md#1-screen-map-for-design) | Section **3 Expectation** of each story |
| **Developer** | [Entity summary](20-cross-cutting.md#2-entity-summary-for-engineering) and your module's repo (table below) | Sections **4–7** of each story |
| **Anyone asking "why"** | [Problem, users, goals & scope](00-problem-goals-scope.md) | [Retired IDs](90-closed-stories.md), [history](99-history.md) |

Status markers are the same as PRD-003: 🟢 ready · 🟡 partly blocked · 🔴 blocked.

## Glossary

Terms used here. Shared terms (*site access*, *implementor*, *effective dating*, *POT / PBT / APT*) are defined
in the [PRD-003 glossary](../PRD-003-foundation/README.md#glossary).

| Term | Meaning here |
|------|--------------|
| **Job Order** | One planned run of one SKU: how many, when, on which work units. Stored as `WORK_ORDER` with one `WORK_ORDER_OPERATION` per routing step |
| **Planner** | The person who builds the schedule. Not yet a persona or IDP role (P-09); the production supervisor plays it for now |
| **Release** | Making a Job Order visible to the floor. Locks the recipe version (the released `ROUTING` / `BOM` version `routing_id` / `bom_id` point at). Treated as the Figma's "schedule approval" (P-04) |
| **Achievement** | Output so far ÷ target, per Job Order |
| **Report page** | One of four Performance Analysis pages — Overview, Availability, Performance, Quality — each showing the KPIs of one loss, with a Live and a Period view |
| **Work Directive** | The job's frozen copy of the recipe (IEC 62264 Part 4), created by production execution management. Not modelled yet; the routing/BOM version pinned at release stands in for it and freeze the routing/BOM identity only — never the cycle time OEE uses |

## Modules

Module = one blue box on the Figma. Feature = a white box under it.
Taxonomy: [`docs-molcadx` DOCS/02-domains.md](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/02-domains.md#taksonomi-platform-8-level--mengikat-semua-domain-baru).

| # | Module | Code | Features | Stories | Status | Implementation repo |
|---|--------|------|----------|:---:|--------|---------------------|
| 01 | [Production Planning](01-planning.md) | `PLN` | Job Order · Breaktime · Execution (Product Tracing deferred) | 11 | 🟢 4 · 🟡 4 · 🔴 3 | `TBD — perlu konfirmasi PO` |
| 02 | [Production Monitoring](02-monitoring.md) | `MON` | Dashboard · Live Monitoring · Losses List | 7 | 🟢 2 · 🟡 4 · 🔴 1 | `TBD — perlu konfirmasi PO` |
| 03 | [Production Performance Analysis](03-performance.md) | `PRF` | Report pages (Overview · Availability · Performance · Quality; Live and Period) · OEE KPI for Management · OEE Performance | 7 | 🟡 4 · 🔴 3 | `TBD — perlu konfirmasi PO` |

**Totals: 6 🟢 · 12 🟡 · 7 🔴 = 25.**

**Each module is built in its own repository.** The last column names it. If you are implementing a story, work
in that module's repo; stories in another module are another team's code, even when yours depends on them (the
Dependencies column says which).

Performance Analysis shows the KPIs declared for the Production domain; every number is read from FOUNDATION's
`KPI_RESULT`, computed by the [PRD-003 KPI](../PRD-003-foundation/07-kpi.md) stories.

The daily `SHIFT_INSTANCE` generator is built by `production-domain`, but its story is
[`US-FND-SHF-002`](../PRD-003-foundation/02-shift.md#us-fnd-shf-002) in PRD-003.

## Build order

```
PLN-001 create ──► PLN-003 release ──► PLN-004 today · PLN-005 queue · PLN-006 change ──► PLN-007 download
     │                                                                        
     └──► PLN-002 upload (template: P-03)          PLN-011 log alongside every step (shared AUDIT_LOG)

Blocked: PLN-008 / PLN-009 breaktime (P-06) · PLN-010 readiness checks (P-07)

MON-004 losses ──► MON-005 correct reason · MON-006 add rework ──► MON-007 log
MON-001 dashboard ──► MON-002 timeline        (MON-003 live state: B5)

PRF-001 live · PRF-002 report ──► PRF-003 management ──► PRF-007 log
     (A, OEE, MTTR/MTTF/MTBF wait on B5 in PRD-003)       Blocked: PRF-004 targets · PRF-005 report lifecycle · PRF-006 failure redefinition
```

## What is stopping this

1. **Breaktime conflict (P-06).** FOUNDATION F-02 H-2 says a break is planned downtime (reduces PBT);
   PRODUCTION 01-planning says it is excluded from POT and never planned downtime. Availability
   comes out the same, but POT and where the break is stored differ. Needs a PO decision before `PLN-008`/`PLN-009`.
2. **No data for readiness checks (P-07).** Material, equipment state, crew and work instructions have no
   entities MolcaDx can read.
3. **Machine state has only a draft entity (B5).** `ASSET_STATE_LOG` is owned by PRODUCTION since 2026-09-28
   ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)) and drafted in [02-monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time); the recording level is settled (2026-09-29, SL-1: per asset, both FKs; two write paths; sensor decides when,
   operator decides why); the state field name is still `TBD` (row ID `asset_state_log_id`). Live state (`MON-003`), downtime losses, Availability, OEE and MTTR / MTTF / MTBF all wait on it.
   Same blocker as PRD-003.
4. **How output arrives is not specified (M-01).** Achievement and the dashboards read recorded output; the
   Figma has no manual entry, and no spec says who writes it from the sensor path.
5. **No targets, report lifecycle or KPI Selection entity (R-01 to R-03)** for Performance Analysis.
6. **Fields the Figma needs but the spec lacks:** queue position (P-01), notes on a Job Order (P-03), and the
   stage-order draft window (P-11). (Recipe lock B3 resolved 2026-10-01.)
7. **Planner persona (P-09).** Not in the product overview and not an IDP role yet.

Each module file lists its own open items at the top; all of them together are in [30-release-risks-questions.md](30-release-risks-questions.md#3-open-questions).

## Files in this folder

| File | Contents |
|------|----------|
| [00-problem-goals-scope.md](00-problem-goals-scope.md) | Problem, personas, 3 goals & counter-metrics, scope, assumptions |
| [01-planning.md](01-planning.md) | Production Planning: open items, 11 stories, detail blocks |
| [02-monitoring.md](02-monitoring.md) | Production Monitoring: open items, 7 stories, detail blocks |
| [03-performance.md](03-performance.md) | Production Performance Analysis: KPI source table, open items, 7 stories, detail blocks |
| [20-cross-cutting.md](20-cross-cutting.md) | Screen map, entity summary, access, offline, NFRs, migration, instrumentation |
| [30-release-risks-questions.md](30-release-risks-questions.md) | Release stages P1–P5, risks, every open question, next steps |
| [90-closed-stories.md](90-closed-stories.md) | The 18 retired PRD-001 PRODUCTION IDs and where their content went. Not to be built |
| [99-history.md](99-history.md) | Version notes |
