# PRD-003 · Problem, users, goals and scope

> Part of [PRD-003 FOUNDATION](README.md). Read this once to understand *why* the stories exist.
> You do not need it to build a single story. That's what the module files are for.

## 1. Problem

FOUNDATION is the data model every other domain reads. When it is wrong or missing, the failure does not
show up in FOUNDATION. It shows up somewhere else, as a number that won't reconcile.

| # | Problem | Evidence | Consequence |
|---|---------|----------|-------------|
| M1 | The fiscal-year declaration was never specified as work | [F-03](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/03-yearly-quarterly-declaration.md) has a full, 🟢 ACTIVE `FISCAL_YEAR` spec, but `FISCAL_YEAR` appeared **0 times** in PRD-001 and PRD-002 | "Q1 2026" means a different date range in every report, and totals never reconcile |
| M2 | Shift generation was written against a calendar concept the PO retired | `SHIFT_CALENDAR` and `NON_WORKING_DAY` are retired in F-03 | Shift generation would depend on entities nobody will build, and the boundary with `production-domain` would sit in the wrong place |
| M3 | Four platform modules have stories but no data model | `WFE`, `NTF`, `INT`, `CFG` are concept-only in [FOUNDATION/AGENTS.md](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/AGENTS.md). Their **12 stories** name entities that don't exist | The stories look buildable when they aren't, so a plan estimated from them can't be executed |
| M4 | Shared reference data has a gap that blocks calculation | `UNIT_OF_MEASUREMENTS` has no generic conversion factor. That's by design: conversion is per product | Output in mixed units can't be reconciled without a per-product conversion row |
| M5 | The domain had no document of its own | FOUNDATION stories were spread across a cross-domain PRD, mixed with PRODUCTION | `foundation-domain` had no scoped brief to work from |

## 2. Target users

Personas come from [00-product-overview §3](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/00-product-overview.md). No new persona is invented.

| Persona | Role in this PRD | Stories (of 50) |
|---------|------------------|-----------------|
| **Plant admin / IT** (implementor) | Builds and maintains every master data set before any line can record anything | 32 |
| **Production supervisor** | Reads the KPI figures FOUNDATION defines | 13 |
| **Plant manager** | Compares reliability and period performance | 2 |
| **Line operator** | Picks a downtime reason from a list scoped to their machine; entries survive a dropped connection | 2 |

`US-FND-SHF-004` (assign crews) was retired on 2026-09-25 (PO) and is kept only in [90-closed-stories.md](90-closed-stories.md#us-fnd-shf-004).

**Two different contexts.** Most of this domain is implementor work at a desk: long onboarding sessions on
a stable network. The exceptions are the KPI read stories and the operator reason picker. Those are used
on the shop floor, on a small screen, one-handed, on an unreliable network. The two contexts need
different design languages; see [screen map](20-cross-cutting.md#71-screen-map-for-design).

## 3. Goals & metrics

Format per [03-prd-standard §2](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/03-prd-standard.md#2-aturan-menulis-tujuan--metrik). Three goals maximum.

### Goal 1 (primary): FOUNDATION is ready before the pilot line starts

**`foundation_ready_for_pilot`** goes from **0%** (baseline 2026-08-24, no FOUNDATION table populated) to
**100%** by **the day the pilot line records its first shift**. It is measured as the share of six
preconditions met:

1. a site hierarchy down to work unit exists;
2. assets are registered and placed;
3. shift definitions and instances are generated;
4. a fiscal year covering that date is declared;
5. downtime and reject taxonomies are populated;
6. every pilot product has a base unit and an effective `OPERATION`.

- Formula: `COUNT(preconditions met) / 6` for the pilot work centre.
- Population: the pilot `WORK_CENTER` and everything in its scope.
- Why a composite: FOUNDATION has no user-facing outcome of its own. It succeeds when the domains above
  it can start. One missing precondition blocks as much as all six.

### Goal 2: every production date resolves to a fiscal year

**`dates_resolving_to_a_fiscal_year`** goes from **0%** (baseline 2026-08-24, no `FISCAL_YEAR` row) to
**100%** from **the pilot line's first production date onward**, measured from
`SHIFT_INSTANCE.business_date` joined against `FISCAL_YEAR`. This is the `FYR` module's own success measure.

### Goal 3: fewer stories built on unmodelled entities

**`stories_on_unmodelled_entities`** falls from **12** (all `WFE`, `NTF`, `INT` and `CFG` stories) to a
number **decided once `foundation-domain` has scoped the platform modules**. It is measured by counting
stories whose Data & entities row names an entity absent from the F-0x specs.

The target is deliberately not set, per
[04-metrics-framework §6](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/04-metrics-framework.md#6-aturan-baseline) rule 1. Until the modelling
decision is made, any number would be invented.

### Counter-metrics

| Metric | Threshold | Why |
|--------|-----------|-----|
| `closed_period_totals_changed` | **must be 0** | No amendment to a fiscal year may change a period that has already closed and been reported |
| `figures_shown_without_complete_input` | **must be 0** | No KPI may show a number when one of its inputs is missing. 0% is a statement about performance; missing data is a statement about data |
| `master_data_edits_7d` | no more than baseline + 5pp | Master rows corrected within 7 days of creation mean the form guessed rather than guided |

### When these goals are declared failed

If the pilot line's first shift arrives with `foundation_ready_for_pilot` below 100%, the line does not
start recording. Partial master data produces figures that look valid and aren't. Those figures then have
to be thrown away, which costs more than the delay.

## 4. Scope

### In scope: 14 modules, 54 live stories

The module list, with readiness per module, is in the [README](README.md#modules).

`Organization` (`ORG`) is **not** a FOUNDATION module. Roles and access live in the identity provider
(SSO/Keycloak), not here ([01-glossary.md](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/01-glossary.md)). Its three stories are listed as
removed in [90-closed-stories.md](90-closed-stories.md).

### Out of scope

| Not done here | Reason | Owner |
|---------------|--------|-------|
| Every PRODUCTION story: shopfloor, OEE screens, job schedule | This PRD covers one domain. PRODUCTION has no PRD of its own yet; the only reference is the frozen [PRD-001 v0.2](../PRD-001-molcadx-core-q3-en.md) | `production-domain` |
| Which dates production is planned on, working-day calendars, holiday lists | The concept was **retired** from FOUNDATION ([F-03](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/03-yearly-quarterly-declaration.md#what-this-module-does-not-cover)) | `production-domain` |
| Modelling the entities behind `WFE`, `NTF`, `INT`, `CFG` | Those four are concept documents. Modelling them is a `foundation-domain` task that comes before building | `foundation-domain` |
| `KPI_INDEX`, `KPI_PARAMETERS`, `KPI_FORMULA` as editable master data | Decided as K-1 in F-07: OEE recipes stay fixed in the product, not per-plant editable | Product Owner |
| The other 25 ISO 22400-2 KPIs | Decided as K-4 (widened 2026-09-10). v1 is the Production OEE family plus Scrap ratio, Rework ratio, Throughput rate and Production process ratio (`US-FND-KPI-009`–`012`); the reason each other KPI is deferred is in [F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#deferred-iso-22400-2-kpis--why-each-is-blocked) | Product Owner |
| Physical database schema, APIs, migrations | FOUNDATION owns the logical model. Schema and API design belong to `engineering-domain`, which does not exist yet | `engineering-domain` |

### Assumptions

| # | Assumption | If it is wrong |
|---|------------|----------------|
| A1 | The pilot enterprise has exactly one fiscal year definition, enterprise-wide | If a site needs its own, `FISCAL_YEAR` gains a `site_id` and Y-2 reopens. That is a `foundation-domain` decision, not a configuration change |
| A2 | Planned downtime is recorded as an `ASSET_STATE_LOG` row, never deducted inside `SHIFT_INSTANCE` | Planned downtime is subtracted twice or not at all. The shift side is settled ([F-02](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md) H-7: `PBT = POT − planned downtime` in the KPI layer, never stored on Shift). What remains open is `ASSET_STATE_LOG` itself: owned by PRODUCTION since 2026-09-28 ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)), its entity spec ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)) is still a draft (B5) |
| A3 | `business_date` is the date a shift **starts**, including shifts crossing midnight | Night-shift output lands in the wrong day, and therefore possibly the wrong quarter |
| A4 | The four platform modules will be modelled before their stories are estimated | 12 stories get estimated as buildable, and the plan slips by the modelling effort |
| A5 | Field codes already used on the shop floor are kept, not replaced with a new scheme | Operators reference machines by one code and the system by another |
