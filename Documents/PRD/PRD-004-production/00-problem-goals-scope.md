# PRD-004 · Problem, users, goals and scope

> Part of [PRD-004 PRODUCTION](README.md). Read this once to understand *why* the stories exist.
> You don't need it to build a single story. That's what the module files are for.

## 1. Problem

| # | Problem | Evidence | Consequence |
|---|---------|----------|-------------|
| P1 | The pilot line's day is planned on a whiteboard or spreadsheet | No Job Order exists in any system today; Q3 goal 3 is "active floor execution on live pilot lines" ([Goals Q3](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/goalsQ3.md)) | Plan and actual can't be compared, so nobody can learn why a day slipped |
| P2 | Supervisors learn a line is behind from the H+1 report | Same as PRD-001's founding problem; nothing has replaced it | Corrections always come one shift late |
| P3 | Losses are recorded without a usable reason, or not at all | Reject reasons were only settled on 2026-09-23 (defect codes merged into `REJECT_REASON`) | The loss tree has nothing in it worth analysing |
| P4 | The only PRODUCTION PRD was frozen against specs that no longer exist | PRD-001 was frozen 2026-09-03; the Shopfloor / OEE Monitoring / Job Schedule specs were replaced by the Figma rewrite on 2026-09-24 | Implementers would build from a document that no longer matches the spec |

## 2. Target users

Personas come from [00-product-overview §3](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/00-product-overview.md#3-target-user),
plus one that is **not there yet** (P-09).

| Persona | Role in this PRD | Stories (of 25) |
|---------|------------------|-----------------|
| **Planner** ⚠ (not yet a persona or IDP role; played by the production supervisor) | Builds and changes the schedule | 4 |
| **Production supervisor** | Releases the schedule, watches achievement and losses, corrects reasons, adds rework | 13 |
| **Plant manager** | Reads the report pages (OEE and the KPIs behind it) and management rollups, evaluates KPIs, owns the audit log | 6 |
| **Line operator** | Sees the job queue at the work unit; readiness checks | 2 |

**Two contexts.** Planning is desk work on a stable network. Monitoring and live OEE are used on the floor, on a
tablet or phone, often one-handed, on unreliable Wi-Fi. See the [screen map](20-cross-cutting.md#1-screen-map-for-design).

## 3. Goals & metrics

Format per [03-prd-standard §2](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/03-prd-standard.md#2-aturan-menulis-tujuan--metrik). Three goals maximum.

### Goal 1 (primary): the pilot line's day is planned and released in MolcaDx

**`pilot_shifts_released_in_molcadx`** goes from **0%** (baseline 2026-09-24, no Job Orders exist) to
**`[proposed]` ≥ 90% for 14 consecutive production days**, by a date **`TBD — perlu konfirmasi PO`**.

- Formula: pilot-line production shifts with at least one Job Order released for them ÷ all pilot-line production shifts.
- Source: `job_order_released` events joined to `SHIFT_INSTANCE`.
- Why primary: this is Q3 goal 3 in PRODUCTION's terms. Everything in Monitoring and Performance reads Job Orders.

### Goal 2: losses are explained by the end of the shift

**`losses_classified_at_shift_end`**: share of loss quantity with a real reason (not "unclassified") when the shift
closes. Baseline **`not yet measured`** (measure the first 14 days after go-live). Target set after 30 days, per
[04-metrics-framework §6](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/04-metrics-framework.md#6-aturan-baseline).
Source: `WORK_ORDER_OPERATION_DEFECT` at shift close.

### Goal 3: supervisors look during the shift, not the day after

**`shifts_viewed_during_shift`**: share of pilot-line shifts in which a supervisor opened the Monitoring dashboard,
Today view or live OEE while the shift was open. Baseline **0%** (no screen exists). Target **`TBD`** after 30 days.
Source: `monitoring_dashboard_opened`, `schedule_today_opened`, `oee_live_opened`.

### Counter-metrics

| Metric | Threshold | Why |
|--------|-----------|-----|
| `time_to_schedule_one_shift` | Not slower than today's whiteboard / spreadsheet (baseline: observe n ≥ 5 shifts) | If planning in MolcaDx is slower, planners go back to the spreadsheet |
| `figures_shown_without_complete_input` | **must be 0** | Missing data shows as "no data", never as 0% |
| `changes_without_log_row` | **must be 0** | Every change to a schedule, a loss or a report leaves a log row |

### When these goals are declared failed

If Goal 1 stays below 50% after 30 production days, stop adding Monitoring and Performance work and find out why
planners aren't using Planning. The other two modules have nothing to show without Job Orders.

## 4. Scope

### In scope: 3 modules, 25 stories

The module list, with readiness per module, is in the [README](README.md#modules).

### Out of scope

| Not done here | Reason | Owner |
|---------------|--------|-------|
| Master data: sites, machines, products, routings, BOMs, shifts, reasons, KPI formulas | FOUNDATION owns them ([PRD-003](../PRD-003-foundation/README.md)) | `foundation-domain` |
| Generating shift instances | Built by `production-domain`, but the story is `US-FND-SHF-002` in PRD-003 | `production-domain` |
| KPI formulas | FOUNDATION F-07 / F-07.1; this PRD only shows `KPI_RESULT` | `foundation-domain` |
| Product Tracing (Material → WIP → FG) | Deferred: lots belong to the future Inventory domain; only the lot hook exists | `inventory-domain` (not created) |
| ERP order import, MRP / capacity planning | Planning is "next to be ERP"; MolcaDx is the primary Job Order path | PO |
| Service monitoring of data acquisition (KR 1A.1) | System health, not production — a different module | `engineering-domain` (not created) |
| Andon, shift close, operator output entry | In PRD-001 but **not** in the PO's Figma. Not carried over until the PO says where they belong | PO |
| Quality and Maintenance KPIs (Scrap / Rework ratio, reliability for maintenance) | Other domains' KPI selections | Quality, Maintenance domains |

### Assumptions

| # | Assumption | If it is wrong |
|---|------------|----------------|
| A1 | Output reaches `WORK_ORDER_OPERATION` from the sensor path, with manual rows only as overrides | Operators need an output entry screen that no story covers yet (M-01) |
| A2 | One uploaded schedule row is one Job Order (P-02) | Per-shift targets need a new field on `WORK_ORDER_OPERATION` |
| A3 | Schedule approval is the release action (P-04) | A separate approval status and screen are needed |
| A4 | The production supervisor plays the planner until the IDP has a planner role (P-09) | Access rules in `PLN-001`/`002`/`006` change |
