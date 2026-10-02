# PRD-004 · Production Performance Analysis (`PRF`)

> Part of [PRD-004 PRODUCTION](README.md). Spec: [PRODUCTION 03-performance](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/03-performance.md).
> Terms are defined in the [README glossary](README.md#glossary).

> **Module layout (spec 2026-09-27, replacing the Figma's 2026-09-24 split).** Production reporting is **not OEE
> only**: four **report pages** — Overview, Availability, Performance, Quality — each with a **Live** view (running
> shift) and a **Period** view (any date range) ([spec](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/03-performance.md#report-pages--one-per-kpi-family-po-decision-2026-09-27)). The Figma's *OEE live dashboard* is now the Live
> view and its *OEE report* the Period view. *OEE KPI for Management* and *OEE Performance* (KPI evaluation) stay.
> *Failure redefinition* is a proposed capability on the Period view. The performance **log** is shared and stricter
> than the other two modules' logs. The Figma still shows the old split — redraw is `TBD — design`.
>
> **The numbers are the KPIs declared for the Production domain** in FOUNDATION's KPI Selection. This module
> **shows** them; it never computes a formula. Every figure is read from `KPI_RESULT`, which the FOUNDATION KPI
> stories compute ([PRD-003 KPI](../PRD-003-foundation/07-kpi.md)). If this file and F-07 disagree, F-07 wins.

## Which KPIs, and which FOUNDATION story computes each

| KPI | Report page | Computed by | Status there |
|-----|-------------|-------------|--------------|
| Availability | Availability | [`US-FND-KPI-001`](../PRD-003-foundation/07-kpi.md#us-fnd-kpi-001) | 🔴 B5 |
| Performance | Performance | [`US-FND-KPI-002`](../PRD-003-foundation/07-kpi.md#us-fnd-kpi-002) | 🟡 |
| Quality ratio | Quality | [`US-FND-KPI-003`](../PRD-003-foundation/07-kpi.md#us-fnd-kpi-003) | 🟢 |
| OEE index (+ loss waterfall) | Overview | [`US-FND-KPI-004`](../PRD-003-foundation/07-kpi.md#us-fnd-kpi-004) | 🔴 B5 |
| MTTR (Figma "MTBR"), MTTF, MTBF | Availability | [`US-FND-KPI-005`](../PRD-003-foundation/07-kpi.md#us-fnd-kpi-005) | 🔴 B5 |
| Throughput rate | Performance | [`US-FND-KPI-011`](../PRD-003-foundation/07-kpi.md#us-fnd-kpi-011) | 🟢 |
| Production process ratio | Availability | [`US-FND-KPI-012`](../PRD-003-foundation/07-kpi.md#us-fnd-kpi-012) | 🟡 |
| Scrap ratio | Quality | [`US-FND-KPI-009`](../PRD-003-foundation/07-kpi.md#us-fnd-kpi-009) | 🟢 |
| Rework ratio | Quality | [`US-FND-KPI-010`](../PRD-003-foundation/07-kpi.md#us-fnd-kpi-010) | 🟢 |
| Fall-off ratio (Job Order only) | Quality | [`US-FND-KPI-013`](../PRD-003-foundation/07-kpi.md#us-fnd-kpi-013) (added 2026-09-27) | 🟢 — shown as "Not available yet" until built |

**All of these are Production KPIs** (PO, 2026-09-27; [F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#scope-which-parts-of-iso-22400-2-molca-adopts),
`KPI_FORMULA_SLOT.domain = production`, [F-07.1 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#91-kpi_formula_slot)). That includes
Quality ratio, Scrap ratio, Rework ratio and Fall-off ratio, which ISO 22400-2's grouping files under Quality, and
MTTR / MTTF / MTBF, which it files under Maintenance: they come from the production equipment's own counters and
stops. Quality, Maintenance and Inventory have no adopted KPI yet.

## Report pages

Each KPI sits on the page whose loss it explains ([F-07 KPI → report page](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#kpi--report-page)).

| Page | Question it answers | Shows |
|------|---------------------|-------|
| **Overview** | How are we doing overall? | OEE index; A × P × Q side by side; the loss waterfall |
| **Availability** | Where did the time go? | Availability, Production process ratio, MTTR / MTTF / MTBF; downtime planned vs unplanned and by downtime reason |
| **Performance** | Did we run at standard speed? | Performance, Throughput rate; per-SKU run segments |
| **Quality** | How much came out good? | Quality ratio, Scrap ratio, Rework ratio, Fall-off ratio (Job Order only); reject quantities by reject reason |

- The breakdowns (by reason, by SKU segment) are for analysis. Fixing a stop's reason or adding rework stays in
  Monitoring ([`MON-004`…`MON-006`](02-monitoring.md)); the Availability and Quality pages link there.
- Rejects by reason are **quantities**, not ratios per reason: per-defect-type KPIs are still blocked in F-07.

**Where each KPI exists** (F-07 scope bindings). A screen shows a KPI only at a scope F-07 adopted; any other cell
reads "Not calculated at this level".

| KPI | Work unit, per shift | Date range (work unit) | Area / site | Job Order |
|-----|:---:|:---:|:---:|:---:|
| Availability, Performance, Quality ratio, OEE | ✅ | ✅ | ✅ | — |
| Throughput rate, Production process ratio | ✅ | — | ✅ site | ✅ |
| Scrap ratio, Rework ratio | ✅ | — | — | ✅ |
| Fall-off ratio | — | — | — | ✅ |
| MTTR, MTTF, MTBF | ✅ | ✅ | — | — |

> [!note]- Sources (for developers)
> Date range = `KPI_RESULT.period` as a `business_date` range ("Time period" scope, adopted for A / P / Q / OEE).
> Site = `aggregation_level = site` ("Plant" scope, adopted for Throughput rate and Production process ratio; the
> existing A / OEE rollup). Job Order = `aggregation_level = work_order` ("Production order" scope). MTTR / MTTF / MTBF
> are per work unit over a window ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#mttr-mttf-and-mtbf-from-unplanned-downtime-only)).

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| **B5** Machine state | all | Availability, OEE and reliability KPIs can't be computed until `ASSET_STATE_LOG` is fully specified. Owner settled 2026-09-28: PRODUCTION ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)); the spec ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)) settled the recording level on 2026-09-29 (SL-1: per asset, `asset_id` and `work_unit_id` both required, the work unit is the reporting level) but still has a `TBD` state field name (row ID `asset_state_log_id`). MAINTENANCE reads the same log for its own MTBF / MTTR / MTTF. Screens show "not available yet" for them | `production-domain` |
| **R-01** KPI Selection storage | all | The Figma's "KPI Selection by domain" (Library Configuration) has no entity. Until it does, the Production set is the fixed list above `[proposed]` | `foundation-domain` |
| **R-02** KPI targets | `PRF-004` | "KPI evaluation" compares against targets. No target entity exists, and targets must not be locked before 30 days of MolcaDx data | PO + `foundation-domain` |
| **R-03** Report lifecycle | `PRF-005` | The Figma log names *reset*, *override* and *approve* on a report. No report entity or status exists | PO + `production-domain` |
| **R-04** Failure redefinition | `PRF-006` | What may be redefined, by whom, and whether closed reports recalculate: `TBD — PO` | PO |
| ~~P-08~~ Log storage | `PRF-007` | **Resolved 2026-09-25 (PO):** FOUNDATION's shared `AUDIT_LOG`, `module_code = PRF` | — |
| ~~R-06~~ Fall-off ratio story | `PRF-002` | **Resolved 2026-09-27 (PO):** story `US-FND-KPI-013` added to PRD-003. Until it is built, `PRF-002` shows the column as "Not available yet" | — |
| **R-07** New F-07 display cases | `PRF-001`, `PRF-002`, `MON-001` | F-07 (2026-09-30, [which standard, and batch work units](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#which-standard-and-batch-work-units-po-2026-09-30-issue-58), [reject entered by weight](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#reject-entered-manually-in-any-unit--convert-to-the-step-unit-and-time-po-2026-09-30-2026-10-01)) adds two display states: Performance **"pending, batch open"** on earlier shifts of an open batch, and Quality **estimated** when a reject is known but the output count is not. `KPI_RESULT.status` has only `computed` / `no_data` / `cannot_compute` / `anomaly`. Question: which field or status value tells the page to show "pending, batch open" or "estimated"? Also: rejects of several work units add up only as **reject time**, never kg — where does reject time reach the Quality page? `TBD — needs PO confirmation` | PO → `foundation-domain` |
| **R-05** Monitoring rows in this log | `PRF-007` | Reason edits and rework adds are written by Monitoring (`module_code = MON`). Should this log show them, and does the reader then also need `production:mon:<scope>:audit_view`? | PO |

## Stories

| ID | User story title | Feature | Status | Description | Dependencies |
|----|------------------|---------|--------|-------------|--------------|
| [`US-PROD-PRF-001`](#us-prod-prf-001) | See Production KPIs live, page by page | Report pages — Live view | 🟡 Partly blocked | As a **production supervisor**, I want each report page (Overview, Availability, Performance, Quality) for the running shift, so that I can see which loss is growing and act before the shift ends. → [detail](#us-prod-prf-001) | `US-FND-KPI-001`…`005`, `US-FND-KPI-009`…`012` |
| [`US-PROD-PRF-002`](#us-prod-prf-002) | Report Production KPIs over a period, page by page | Report pages — Period view | 🟡 Partly blocked | As a **plant manager**, I want each report page for any date range and scope, with download, so that I can compare lines, weeks and Job Orders by the loss that matters. → [detail](#us-prod-prf-002) | `US-FND-KPI-001`…`005`, `US-FND-KPI-009`…`013` |
| [`US-PROD-PRF-003`](#us-prod-prf-003) | See management OEE rollups | OEE KPI for Management | 🟡 Partly blocked | As a **plant manager**, I want OEE rolled up by site, product and period, so that I see the plant-level picture without adding up lines myself. → [detail](#us-prod-prf-003) | `US-PROD-PRF-002` |
| [`US-PROD-PRF-004`](#us-prod-prf-004) | Evaluate KPIs against targets | OEE Performance | 🔴 Blocked | Waits on **R-02**: no target exists to evaluate against. → [why](#us-prod-prf-004) | `US-PROD-PRF-003` |
| [`US-PROD-PRF-005`](#us-prod-prf-005) | Approve, override or reset a report | OEE report | 🔴 Blocked | Waits on **R-03**: reports have no lifecycle yet. → [why](#us-prod-prf-005) | `US-PROD-PRF-002` |
| [`US-PROD-PRF-006`](#us-prod-prf-006) | Redefine what counts as a failure | OEE report | 🔴 Blocked | Waits on **R-04** and B5. → [why](#us-prod-prf-006) | `US-FND-KPI-005` |
| [`US-PROD-PRF-007`](#us-prod-prf-007) | See and download the performance audit log | Log (all features) | 🟡 Partly blocked | As a **plant manager**, I want every action that can change a published number logged with who, when, before and after, so that reported performance can be trusted. → [detail](#us-prod-prf-007) | `US-PROD-PRF-002` |

**Totals: 0 🟢 ready · 4 🟡 partly blocked · 3 🔴 blocked = 7.**

## Detail blocks

---

#### US-PROD-PRF-001

**See Production KPIs live, page by page**

**Status:** 🟡 Partly blocked: the Performance and Quality pages can show now (Performance, Throughput rate,
Quality ratio, Scrap ratio, Rework ratio). Overview (OEE) and the Availability page show "not available yet" until
B5.

> **In short:** the Live view of the four report pages for the running shift, per work unit, marked provisional
> until the shift closes. Each page shows the KPIs of one loss. Nothing is computed here; every figure is read from
> `KPI_RESULT`.

**1. Story**

As a **production supervisor**, I want each report page (Overview, Availability, Performance, Quality) for the
running shift, so that I can see which loss is growing and act before the shift ends.

**2. Context**

- **Replaces the Figma "OEE live dashboard".** The spec split reporting into four pages, each with a Live and a
  Period view ([PRODUCTION 03-performance](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/03-performance.md#report-pages--one-per-kpi-family-po-decision-2026-09-27)).
- Today supervisors learn OEE from the H+1 report, one shift late — and a low OEE alone doesn't say which loss to fix.
- **Who and where:** supervisor, tablet or phone on the floor; also a wall screen `[proposed]`.

**3. Expectation**

- Four tabs: **Overview · Availability · Performance · Quality**, Overview first. Each tab shows one card per work
  unit, labelled provisional with "updated `<hh:mm>`".
- **Overview:** OEE large, then A / P / Q. Tapping A, P or Q opens that page.
- **Availability:** Availability, Production process ratio, and the shift's downtime so far split planned /
  unplanned. A link opens the Monitoring losses list ([`MON-004`](02-monitoring.md#us-prod-mon-004)).
- **Performance:** Performance and Throughput rate; when the shift ran more than one SKU, one line per SKU segment.
- **Quality:** Quality ratio, Scrap ratio, Rework ratio, and reject quantities by reason so far.
- Tap a KPI → its calculation breakdown ([`US-FND-KPI-008`](../PRD-003-foundation/07-kpi.md#us-fnd-kpi-008)).
- What the supervisor reads within 3 seconds: which of A, P, Q is lowest on each line.

| State | What the user sees |
|-------|--------------------|
| Empty | "No data yet this shift" — never 0% |
| Loading | Card placeholders; old values not kept as if new |
| Error | Last loaded values marked stale |
| Offline | Last loaded values with an offline marker and time |
| Success | Not applicable |
| No permission | Only work units in the user's access |

**4. Calculation** — none here. Display rule per `KPI_RESULT.status`: `computed` → value; `no_data` → "no data
yet"; `cannot_compute` → "can't be calculated" with the reason; `anomaly` → value with a warning. Values above
100% show as they are, never cut. A KPI whose inputs wait on B5 reads "not available yet".

Cases F-07 added on 2026-09-30 (how `KPI_RESULT` flags the first two is **R-07**):
- **Batch work unit, batch still open** → earlier shifts show Performance as "pending, batch open"; Availability
  shows normally. When the batch closes, its output is split across shifts by running time and those shifts are
  recalculated. *Example (F-07):* 3 h running in shift 1, 1 h in shift 2, closes with 800 kg → 600 kg to shift 1,
  200 kg to shift 2.
- **Reject known, no output count** → Quality shows only as **estimated**, labelled as such.
- **Reject entered by weight, no weight per unit at that step** → Quality "can't be calculated", reason "Weight
  per unit missing". **Reject entered in a unit with no conversion path to the step's unit** (PO 2026-10-01) →
  Quality "can't be calculated", reason "No conversion for this unit", naming both units and the product.
  **No cycle time on the step** → reject time "can't be calculated", reason "Cycle time
  missing". These are `cannot_compute` with their reason.
- Reject quantities by reason show in each row's own unit; across work units rejects add up only as reject time,
  never in kg ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#rejects-across-work-units-add-up-in-time-never-in-weight-po-2026-09-30)).

*Example (the PRD-003 example shift):* PBT 420, APT 378, 1,000 units, 950 good, 50 scrapped, none reworked →
Overview: A 90%, P 88.2%, Q 95%, OEE 75.4% once B5 lets A and OEE compute. Quality page: Quality 95%, Scrap
50 ÷ 1,000 = **5%**, Rework **0%**. Today Overview shows P 88.2%, Q 95% and "A, OEE — not available yet".

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `KPI_RESULT` (`work_unit_id`, `metric`, `period = shift_instance_id`, `value`, `status`, `is_provisional`, `is_stale`, `computed_at`, `waterfall`) · `WORK_UNIT` · reject quantities by `defect_code_id` from `WORK_ORDER_OPERATION_DEFECT` (Quality page) |
| **Writes** | None |

**6. Rules & constraints**

1. Only the Production KPIs, each on its page (table "Report pages" above), and only at work unit per shift.
2. Provisional while the shift is open; final only after close.
3. Reject reasons show quantities, not ratios per reason (Defect type scope is blocked in F-07).
4. No editing here: reason fixes and rework adds stay in Monitoring.
5. **Who may see it:** access to the work unit or above.

**7. Acceptance criteria**

- **AC-1** **Given** `KPI_RESULT` Quality 95% computed for S03, **when** the Overview opens, **then** S03 shows Q 95%
  labelled provisional.
- **AC-2 (no data)** **Given** `status = no_data`, **when** a card shows, **then** it reads "no data yet", not 0%.
- **AC-3 (over 100%)** **Given** Performance 104% with `anomaly`, **when** the card shows, **then** it reads 104%
  with a warning.
- **AC-4 (Quality page)** **Given** Scrap ratio 5% and Rework ratio 0% computed for S03 this shift, **when** the
  Quality page opens, **then** S03 shows Quality 95%, Scrap 5%, Rework 0%, provisional.
- **AC-5 (B5)** **Given** APT isn't available, **when** the Availability page opens, **then** Availability and
  Production process ratio read "not available yet".
- **AC-6 (drill in)** **Given** the Overview is open, **when** the supervisor taps P on S03, **then** the Performance
  page opens filtered to S03.
- **AC-7 (no editing)** **Given** the Availability page, **when** the supervisor wants to fix a stop's reason,
  **then** the only action is the link to the Monitoring losses list.
- **AC-8 (open batch)** **Given** a process cell whose batch started in the previous shift and is still open,
  **when** the Performance page opens, **then** that work unit reads "pending, batch open" and its Availability
  still shows.
- **AC-9 (weight per unit missing)** **Given** a reject entered in kg on a step with no weight of one unit,
  **when** the Quality page opens, **then** Quality reads "can't be calculated" with "Weight per unit missing".

**8. Metrics & events**

- **Metric:** share of shifts where a Live page was opened during the shift. Baseline 0, target `TBD`.
- **Events:** `performance_page_opened` (`page`, `view = live`, `scope_id`, `shift_instance_id`) ·
  `oee_breakdown_opened` (`work_unit_id`, `metric`).

**Dependencies:** `US-FND-KPI-001`, `US-FND-KPI-002`, `US-FND-KPI-003`, `US-FND-KPI-004`, `US-FND-KPI-005`, `US-FND-KPI-009`, `US-FND-KPI-010`, `US-FND-KPI-011`, `US-FND-KPI-012`

---

#### US-PROD-PRF-002

**Report Production KPIs over a period, page by page**

**Status:** 🟡 Partly blocked: the Performance and Quality pages can be built (Performance, Throughput rate,
Quality, Scrap, Rework); Overview, Availability and MTTR / MTTF / MTBF wait on B5; Fall-off ratio shows "Not
available yet" until `US-FND-KPI-013` is built.

> **In short:** the Period view of the same four pages, for any date range and scope (work unit, area, site, Job
> Order). Each page shows its KPIs at the scopes F-07 adopted them for. Downloadable.

**1. Story**

As a **plant manager**, I want each report page for any date range and scope, with download, so that I can compare
lines, weeks and Job Orders by the loss that matters.

**2. Context**

- **Replaces the Figma "OEE report → MTBR, MTTF included".** MTBR on the Figma **is** MTTR (alias locked
  2026-09-24); it now sits on the Availability page.
- Period = a `business_date` range; scope = `aggregation_level` in `KPI_RESULT`.

**3. Expectation**

- Same four tabs as `PRF-001`. Filters shared by all tabs: date range, scope level and item, compare-to period.
- **Overview:** OEE, A, P, Q per item, loss waterfall, trend of OEE.
- **Availability:** Availability, Production process ratio, MTTR, MTTF, MTBF per item; downtime by reason.
- **Performance:** Performance and Throughput rate per item; per-SKU segments.
- **Quality:** Quality ratio, Scrap ratio, Rework ratio per item; Fall-off ratio at Job Order level; reject quantities
  by reason.
- A cell for a KPI not adopted at the chosen scope reads "Not calculated at this level" — never blank or 0%.
- **Download** exports the open tab's table with its filters (logged, `PRF-007`).

| State | What the user sees |
|-------|--------------------|
| Empty | "No results for this range" |
| Loading | Table placeholder |
| Error | "Report couldn't load — try again" |
| Offline | "Reports need a connection" |
| Success | Not applicable |
| No permission | Only items in the user's access |

**4. Calculation** — none here. Range results are the `KPI_RESULT` rows with that `business_date` range and
`aggregation_level`, rolled up by `sum_of_terms` in FOUNDATION, never an average of shift percentages. The
display cases of `PRF-001` apply here too ("pending, batch open", estimated Quality, `cannot_compute` reasons; R-07).
A back-dated standard fix or a batch closing recalculates past shifts, so a range can change after it was first
viewed (F-07).

*Example:* two shifts at one work unit: shift A PBT 420 / APT 378, shift B PBT 420 / APT 336 → range Availability =
(378 + 336) ÷ 840 = **85%**, not the average of 90% and 80% (also 85% here, but they differ when PBT differs).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `KPI_RESULT` (`period` as a `business_date` range, `aggregation_level`, `aggregation_method`, `metric`, `value`, `status`) · reject quantities by `defect_code_id` from `WORK_ORDER_OPERATION_DEFECT` (Quality tab) |
| **Writes** | On download: one `AUDIT_LOG` row, `action = report_downloaded` (`domain_code = PROD`, `module_code = PRF`), with `download_filter` (page, period, scope) and `download_row_count`; no `changes`. Its `entity_type` waits on the report entity (R-03). No reason is asked; the row's scope is the filter's scope, `tenant` if unfiltered (PO, 2026-09-25). |

**6. Rules & constraints**

1. Each KPI only on its page and only at the scopes marked ✅ in "Where each KPI exists".
2. Range at most `[proposed]` 366 days.
3. Open shifts in the range make the result provisional.
4. Reject reasons show quantities, not ratios per reason. No editing on these pages.
5. **Who may see it:** access to the scope item.

**7. Acceptance criteria**

- **AC-1** **Given** a week at site JKT1, **when** the manager opens the Overview, **then** each KPI shows the
  site-level `KPI_RESULT` for that range.
- **AC-2 (alias)** **Given** the Availability tab, **when** it renders, **then** the column is labelled "MTTR" with
  "MTBR" as a tooltip alias.
- **AC-3 (download)** **Given** a download from the Quality tab, **when** it finishes, **then** the file holds that
  tab's table and the log records who, which page, range and when.
- **AC-4 (Job Order)** **Given** scope level Job Order and `WO-1042`, **when** the Quality tab opens, **then** it
  shows Scrap ratio, Rework ratio and Fall-off ratio for that order, and Quality ratio reads "Not calculated at
  this level".
- **AC-5 (scope)** **Given** scope level site, **when** the Quality tab opens, **then** Scrap ratio and Rework ratio
  read "Not calculated at this level"; **when** the Performance tab opens, **then** Throughput rate shows the site
  value.

**8. Metrics & events**

- **Metric:** reports run per week by managers. Baseline 0.
- **Events:** `performance_page_opened` (`page`, `view = period`, `scope_level`, `date_from`, `date_to`) ·
  `oee_report_downloaded` (`page`, `row_count`).

**Dependencies:** `US-FND-KPI-001`, `US-FND-KPI-002`, `US-FND-KPI-003`, `US-FND-KPI-004`, `US-FND-KPI-005`, `US-FND-KPI-009`, `US-FND-KPI-010`, `US-FND-KPI-011`, `US-FND-KPI-012`, `US-FND-KPI-013`

---

#### US-PROD-PRF-003

**See management OEE rollups**

**Status:** 🟡 Partly blocked: same B5 gap as `PRF-002`; product rollup needs `KPI_RESULT` at product level,
which doesn't exist (only `work_unit` / `area` / `site` / `work_order`).

> **In short:** a management view: OEE by site and by period, with the biggest losses. Product view is limited to
> what Job Order scope gives.

**1. Story**

As a **plant manager**, I want OEE rolled up by site, product and period, so that I see the plant-level picture
without adding up lines myself.

**2. Context** — Figma "OEE KPI for Management": rolled views by site, product, period.

**3. Expectation** — site cards with OEE and trend, plus the site's Throughput rate and Production process ratio
(adopted at site level); a period switch (week, month, quarter from the fiscal
calendar); by-product shows Job Order results grouped by SKU, labelled "per Job Order".

| State | What the user sees |
|-------|--------------------|
| Empty | "No results yet" |
| Loading | Card placeholders |
| Error | "Couldn't load — try again" |
| Offline | Last loaded view with offline marker |
| Success | Not applicable |
| No permission | Only sites in the user's access |

**4. Calculation** — none here; site and period values are `KPI_RESULT` at `site` level over the range.
Quarters come from the fiscal declaration (F-03).

**5. Data & entities** — reads `KPI_RESULT` (`aggregation_level` `site` / `work_order`) · `WORK_ORDER.product_id` ·
fiscal periods (F-03). Writes nothing.

**6. Rules & constraints** — never average OEE across items; a product total that would need a `product`
aggregation level is not shown (would need F-07 to add it).

**7. Acceptance criteria** — **AC-1** **Given** Q3 is selected, **when** the view opens, **then** each site shows
its site-level OEE for the fiscal Q3 range. **AC-3** **Given** a site card, **when** it shows, **then** it also
shows the site Throughput rate and Production process ratio, and no site-level Scrap or Rework ratio. **AC-2** **Given** by-product is selected, **when** it shows, **then**
values are per Job Order, labelled so.

**8. Metrics & events** — event `oee_management_opened` (`period_type`).

**Dependencies:** `US-PROD-PRF-002`

---

#### US-PROD-PRF-004

**Evaluate KPIs against targets**

**Status:** 🔴 Blocked — waits on **R-02**.

**1. Story** — As a **plant manager**, I want each Production KPI shown against its target, so that I know which
lines are below plan.

**2. Context — why it is blocked** — Figma "OEE Performance → KPI evaluation". No target entity exists, and the
metrics framework forbids locking OEE targets before `[proposed]` 30 days of MolcaDx's own data.

**3–8.** not applicable until R-02 is decided.

**Unblocked when:** a target entity (per KPI, scope, period) is modelled and the 30-day baseline exists.

**Dependencies:** `US-PROD-PRF-003`

---

#### US-PROD-PRF-005

**Approve, override or reset a report**

**Status:** 🔴 Blocked — waits on **R-03**.

**1. Story** — As a **plant manager**, I want to approve a period's report, and to override or reset it with a
reason, so that published numbers are signed off and every change is on record.

**2. Context — why it is blocked** — the Figma log names reset, override and approve. There is no report entity
or status, and "override" on a computed KPI conflicts with "one number, one definition" unless its rules are set.

**3–8.** not applicable until R-03 is decided.

**Dependencies:** `US-PROD-PRF-002`

---

#### US-PROD-PRF-006

**Redefine what counts as a failure**

**Status:** 🔴 Blocked — waits on **R-04** and B5.

**1. Story** — As a **plant manager**, I want to reclassify which stops count as failures after they are recorded,
so that MTTR and MTBF reflect real breakdowns.

**2. Context — why it is blocked** — Figma dashed box "Pendefinisian ulang failure". Rules for what, who, and
whether closed reports recalculate are `TBD — PO`. Stops themselves live in `ASSET_STATE_LOG` (PRODUCTION-owned since 2026-09-28, [spec](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time); B5: recording level settled 2026-09-29, state field and row ID names `TBD`).

**3–8.** not applicable until R-04 and B5 close.

**Dependencies:** `US-FND-KPI-005`

---

#### US-PROD-PRF-007

**See and download the performance audit log**

**Status:** 🟡 Partly blocked: reset / override / approve rows wait on `PRF-005` (R-03); showing Monitoring rows here is R-05. Storage is the shared `AUDIT_LOG` (P-08 resolved).

**1. Story** — As a **plant manager**, I want every action that can change a published number logged with who,
when, before and after, so that reported performance can be trusted.

**2. Context** — Figma "Log": reason edits, rework adds, report reset, override, approve, downloads. Stricter than
the other logs only in what it covers (actions that change published numbers); storage and rules are the same shared
`AUDIT_LOG`. Reason edits and rework adds come from [`MON-007`](02-monitoring.md#us-prod-mon-007); whether they show
here is R-05.

**3. Expectation** — same layout and states as [`PLN-011`](01-planning.md#us-prod-pln-011), actions
`create`, `update`, `deactivate`, `report_downloaded` (and later `report_reset`, `report_overridden`,
`report_approved`), plus Monitoring's `reason_corrected` / `rework_added` if R-05 says so. Downloading this log itself
is an `FND` · `AUD` `download` row on the central Audit log.

**4. Calculation** — not applicable.

**5. Data & entities** — reads `AUDIT_LOG` where `domain_code = PROD`, `module_code = PRF` (and `MON` if R-05 says so). Written through `US-FND-AUD-001`.

**6. Rules & constraints** — F-14 rules. **Who may see it:** `production:prf:<scope>:audit_view` / `:audit_download`.

**7. Acceptance criteria** — **AC-1** **Given** a report was downloaded, **when** the log opens, **then** a row shows
who, range and time (`report_downloaded`). **AC-2** **Given** a reason was corrected in Monitoring, **when** this log opens, **then** that
row is listed.

**8. Metrics & events** — changes with a missing log row: must be 0.

**Dependencies:** `US-PROD-PRF-002`

---

## Change notes

| Date | Change |
|------|--------|
| 2026-10-01 | prd-sync `docs-molcadx` 787e819..725362a (F-07 Q-28): `PRF-001` — new display case "No conversion for this unit" for a manual reject entered in a unit with no conversion path; F-07 anchor renamed (R-07 link) |
| 2026-10-01 | prd-sync `docs-molcadx` 147e8d2..787e819 (F-07 2026-09-30: batch Performance, reject by weight, rejects add up in time; PRODUCTION SL-1 resolved): B5 open item and `PRF-006` context — SL-1 settled, field names still `TBD`. `PRF-001` — new display cases ("pending, batch open", estimated Quality, "Weight per unit missing" / "Cycle time missing"), AC-8, AC-9. `PRF-002` — same cases; ranges can change after back-dated fixes or batch close. New open item **R-07**. No status change. The 03-performance spec hunk was a rendering fix only (ignored) |
| 2026-09-28 | prd-sync to `docs-molcadx@513c4f9` ([asset status decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)): B5 open item and `PRF-006` context — `ASSET_STATE_LOG` owner = PRODUCTION, spec still a draft. No status change |
| 2026-09-27 | prd-sync `docs-molcadx` 7ad3ed8..7a59537 (spec 03-performance "Report pages", F-07 KPI → report page): reporting reshaped into four pages (Overview / Availability / Performance / Quality), each with Live and Period views. `PRF-001` = Live view (AC-6, AC-7 new), `PRF-002` = Period view per tab (download per tab). Module layout note and KPI table (new Report page column) updated; Figma redraw `TBD — design` |
| 2026-09-27 | PO request: all adopted Production KPIs added. Scrap ratio, Rework ratio and Fall-off ratio join the KPI table (all Production KPIs, `KPI_FORMULA_SLOT.domain = production`); new "Where each KPI exists" scope table. `PRF-001` adds Production process ratio, Scrap and Rework (AC-4, AC-5); `PRF-002` adds them plus Fall-off at Job Order level (AC-4, AC-5); `PRF-003` adds site Throughput rate and Production process ratio (AC-3). R-06 (Fall-off ratio had no PRD-003 story) opened, then resolved the same day by `US-FND-KPI-013` |
| 2026-09-25 | prd-sync to `docs-molcadx@3cb17fa`: `PRF-002` states the PO-confirmed `report_downloaded` rules (no reason, filter scope) |
| 2026-09-25 | prd-sync to `docs-molcadx@327bb64`: P-10 closed (PO) — report download is `report_downloaded`. `PRF-002` writes it; `PRF-007` lists it instead of `download` |
| 2026-09-25 | prd-sync to `docs-molcadx@171a333`: `PRF-007` — `report_downloaded` replaced by the generic `download` the spec names (03-performance § Log); generic `create` / `update` / `deactivate` added; report download pending P-10 |
| 2026-09-24 | Module created from the PO's Figma (OEE live · OEE report · OEE KPI for Management · OEE Performance + log) and PRODUCTION 03-performance. 7 stories; all numbers read from FOUNDATION `KPI_RESULT` |
