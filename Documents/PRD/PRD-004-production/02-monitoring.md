# PRD-004 · Production Monitoring (`MON`)

> Part of [PRD-004 PRODUCTION](README.md). Spec: [PRODUCTION 02-monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md).
> Terms are defined in the [README glossary](README.md#glossary).

> **Module layout (Figma, 2026-09-24).** Production Monitoring has three features: **Dashboard** (with the
> event timeline), **Live Monitoring** and **Losses List**. The monitoring **log** is a capability shared by all
> three. KPI numbers here come from the KPIs declared for the Production domain; this module computes none.

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| **B5** Machine state (SL-2…SL-4) | `MON-003`, `MON-004`, `MON-005` | `ASSET_STATE_LOG` (running / idle / down / setup, with reason) is **owned by PRODUCTION since 2026-09-28** ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)) and specified in [PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time). **Settled 2026-09-29 (PO):** SL-1 — per asset, `asset_id` and `work_unit_id` both required; one owner, two write paths (Silver job writes `plc` / `derived`, MES app writes `manual` rows and reasons); overlap rule — the sensor decides **when**, the operator decides **why** (`entered_by` / `entered_at`); a Silver recompute never overwrites an operator's reason. **Still open:** state field names (row ID `asset_state_log_id` since 2026-10-01), Maintenance-only tenants SL-2, break wording SL-3 (= P-06), offline operator entry SL-4 (queueing half), sensor rows arriving after a `manual` row (overlap rule 3), the shift-split rule. Live state, downtime losses and downtime reason edits all read it | `production-domain` (SL-4: PO + ops) |
| **M-01** How output arrives | `MON-001`, `MON-002` | The Figma has no manual output entry. Output reaches `WORK_ORDER_OPERATION` from the sensor path (data platform, sensor is the source of truth) — but no story or spec says who writes `good_qty` and when. Whether operators ever type output is undecided | PO + `production-domain` |
| **M-02** Ranking basis | `MON-002` | The Figma's "Ranking" doesn't say ranked by what. This PRD ranks work units by achievement % `[proposed]` | PO |
| **M-03** Speed losses | `MON-004` | The Figma lists "losses". Downtime and quality losses have data; speed loss per event has none | PO |
| ~~P-08~~ Log storage | `MON-007` | **Resolved 2026-09-25 (PO):** FOUNDATION's shared `AUDIT_LOG`, `module_code = MON` | — |
| **SF-4** Floor device | `MON-003` | Shared tablet, phone or fixed terminal is undecided | PO |
| **M-04** Unit of reject rows vs `scrap_qty` / `rework_qty` | `MON-004`, `MON-005`, `MON-006`, `PLN-004` | `WORK_ORDER_OPERATION_DEFECT` now has `uom_id` (PLN-Q1, 2026-10-01): a reject can be counted in pcs or punches, or weighed in kg. The spec still sums `scrap_qty` / `rework_qty` as Σ `qty` per fate, which mixes units when rows differ. Since 2026-10-01 (Q-28) F-07 converts every reject row from its `uom_id` to `OPERATION.uom_id` before Quality — that settles the KPI side only. Question: are `scrap_qty` / `rework_qty` stored converted to the operation's unit, or summed per unit? `TBD — needs PO confirmation` | PO + `production-domain` |
| **M-05** `origin = manual` details | `MON-005`, `MON-006` | New `origin = manual` (PLN-Q2): on a work unit with no reject tag, the operator's entry is the main source. The spec doesn't say (a) whether `entered_by` / `entered_at` are required on a `manual` row (it names only `manual_override`), nor (b) which `origin` a supervisor's **correction** of a `manual` row writes. `TBD — needs PO confirmation` | `production-domain` |
| SL-5 | `US-PROD-MON-003`, `US-PROD-MON-004` | Keys (2026-10-01): may two sensor rows (`plc` / `derived`) of the same asset overlap in time on `ASSET_STATE_LOG`? | `production-domain` |

## Stories

| ID | User story title | Feature | Status | Description | Dependencies |
|----|------------------|---------|--------|-------------|--------------|
| [`US-PROD-MON-001`](#us-prod-mon-001) | See production status at a glance | Dashboard | 🟡 Partly blocked | As a **production supervisor**, I want one screen with output vs target, OEE and open losses for my area, so that I know where to go first. → [detail](#us-prod-mon-001) | `US-PROD-PLN-004`, `US-FND-KPI-003` |
| [`US-PROD-MON-002`](#us-prod-mon-002) | See the shift's event timeline and ranking | Dashboard | 🟡 Partly blocked | As a **production supervisor**, I want a timeline of target results over the shift and a ranking of work units, so that I can see when things went wrong and who is furthest behind. → [detail](#us-prod-mon-002) | `US-PROD-MON-001` |
| [`US-PROD-MON-003`](#us-prod-mon-003) | Watch each work unit live | Live Monitoring | 🔴 Blocked | Waits on **B5**: machine state (`ASSET_STATE_LOG`, PRODUCTION's since 2026-09-28) has its recording level settled (SL-1, 2026-09-29), but the state field's name is still `TBD` (row ID `asset_state_log_id`, 2026-10-01). The running-job part is buildable from `PLN-005`. → [why](#us-prod-mon-003) | `US-PROD-PLN-005` |
| [`US-PROD-MON-004`](#us-prod-mon-004) | See the list of open and recent losses | Losses List | 🟡 Partly blocked | As a **production supervisor**, I want one list of the shift's losses, open ones first, so that nothing lost goes unexplained. → [detail](#us-prod-mon-004) | `US-FND-REF-002` |
| [`US-PROD-MON-005`](#us-prod-mon-005) | Correct a loss reason | Losses List | 🟡 Partly blocked | As a **production supervisor**, I want to correct the reason on a loss, with the old value kept, so that the loss analysis is right without hiding what was first recorded. → [detail](#us-prod-mon-005) | `US-PROD-MON-004` |
| [`US-PROD-MON-006`](#us-prod-mon-006) | Add rework to a job | Losses List | 🟢 Ready | As a **production supervisor**, I want to add a rework quantity with its reason to a job, so that units sent back for rework are counted correctly. → [detail](#us-prod-mon-006) | `US-FND-REF-002` |
| [`US-PROD-MON-007`](#us-prod-mon-007) | See and download the monitoring log | Log (all features) | 🟢 Ready | As a **production supervisor**, I want to see who changed a loss reason or added rework, with before and after, so that every correction can be traced. → [detail](#us-prod-mon-007) | `US-PROD-MON-005`, `US-PROD-MON-006`, `US-FND-AUD-001` |

**Totals: 2 🟢 ready · 4 🟡 partly blocked · 1 🔴 blocked = 7.**

## Detail blocks

---

#### US-PROD-MON-001

**See production status at a glance**

**Status:** 🟡 Partly blocked: output vs target and quality losses can be built. Availability, OEE and downtime
figures wait on B5 (through `US-FND-KPI-001` / `KPI-004`) and show "not available yet" until then.

> **In short:** one screen per area for the running shift: how much each work unit made against its target,
> its OEE, and how many losses are still open. It tells the supervisor where to walk first.

**1. Story**

As a **production supervisor**, I want one screen with output vs target, OEE and open losses for my area, so
that I know where to go first.

**2. Context**

- **Figma "Dashboard":** at-a-glance production status for a chosen site, area or work units.
- **Numbers are not computed here.** Output comes from `WORK_ORDER_OPERATION`; OEE and its parts come from
  FOUNDATION's `KPI_RESULT`, for the KPIs declared for the Production domain
  ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md)).
- **Who and where:** the supervisor, on a tablet or phone while walking the floor, unstable Wi-Fi.

**3. Expectation**

- Scope picker (site → area → work units); default = the user's own area, running shift.
- One tile per work unit: achievement % (from `PLN-004`), OEE with A / P / Q, open losses count, and the running job.
- Tiles behind target are amber; tiles with open losses show the count in red.
- "Provisional" label on every KPI while the shift is open. "Updated `<hh:mm>`" at the top.

| State | What the user sees |
|-------|--------------------|
| Empty | "Nothing released for this shift" per work unit without jobs |
| Loading | Tile placeholders; old numbers are not kept as if new |
| Error | Last loaded numbers, marked "not refreshed since `<hh:mm>`" |
| Offline | Last loaded view with an offline marker and its time |
| Success | Not applicable (read-only) |
| No permission | Only work units in the user's access |

**4. Calculation**

- Achievement %: same as [`US-PROD-PLN-004`](01-planning.md#us-prod-pln-004).
- OEE, A, P, Q: read from `KPI_RESULT` for the work unit and running shift. Its `status` decides what shows:
  `computed` → the value; `no_data` → "no data yet" (never 0%); `cannot_compute` → "can't be calculated";
  `anomaly` → the value with a warning.
- Two newer F-07 cases (2026-09-30): a batch work unit whose batch is still open shows Performance as "pending,
  batch open" (Availability shows normally), and a work unit with a reject weight but no output count shows
  Quality only as **estimated**, labelled as such. How `KPI_RESULT` carries either is open (**R-07** in
  [Performance](03-performance.md#open-items-in-this-module)).
- Open losses: count of loss rows in `MON-004` with no reason yet.
- Rolling up to area or site uses `KPI_RESULT` at that `aggregation_level` (`sum_of_terms`), **never** an average
  of work unit OEEs.

*Example:* Work unit S03, target 1,000, done 420 → 42%. `KPI_RESULT` Quality = 95% `computed`, Availability
`no_data` (B5) → the tile shows Q 95% and "A — not available yet".

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER`, `WORK_ORDER_OPERATION` (as `PLN-004`) · `KPI_RESULT` (`work_unit_id`, `metric`, `period`, `value`, `status`, `is_provisional`, `aggregation_level`) · `WORK_ORDER_OPERATION_DEFECT` (for quality loss counts) |
| **Writes** | None |

**6. Rules & constraints**

1. KPI values shown only for KPIs declared for the Production domain.
2. While the shift is open, every KPI is labelled provisional (`is_provisional`).
3. Refresh `[proposed]` every 60 s.
4. **Who may see it:** access to the work unit or anything above it.

**7. Acceptance criteria**

- **AC-1** **Given** S03 has 420 of 1,000 done and Quality 95% computed, **when** the supervisor opens the
  dashboard, **then** S03's tile shows 42% and Q 95% labelled provisional.
- **AC-2 (no data)** **Given** `KPI_RESULT` for Availability is `no_data`, **when** the tile shows, **then** it
  reads "not available yet", never 0%.
- **AC-3 (rollup)** **Given** the area is selected, **when** area OEE shows, **then** it is the area-level
  `KPI_RESULT`, not an average of tiles.
- **AC-4 (offline)** **Given** the device goes offline, **when** the screen stays open, **then** the last view
  stays with an offline marker and its time.

**8. Metrics & events**

- **Metric:** share of shifts in which a supervisor opened the dashboard during the shift. Baseline 0, target `TBD`.
- **Counter-metric:** load time p95 ≤ `[proposed]` 2.5 s on 3G.
- **Events:** `monitoring_dashboard_opened` (`scope_level`, `scope_id`, `shift_instance_id`).

**Dependencies:** `US-PROD-PLN-004`, `US-FND-KPI-003`

---

#### US-PROD-MON-002

**See the shift's event timeline and ranking**

**Status:** 🟡 Partly blocked: the ranking basis (M-02) is proposed, and downtime events on the timeline wait on B5.

> **In short:** under the dashboard, a timeline shows how the shift's output built up against target, hour by
> hour, and a ranking lists work units from furthest behind to furthest ahead.

**1. Story**

As a **production supervisor**, I want a timeline of target results over the shift and a ranking of work units,
so that I can see when things went wrong and who is furthest behind.

**2. Context**

- **Figma "Event timeline":** target result, ranking.
- **Who and where:** same as `MON-001`.

**3. Expectation**

- **Timeline:** one line per work unit, cumulative output vs a straight target line across the planned window, per
  hour. Quality events (rejects) as markers. Downtime bands appear once B5 closes.
- **Ranking:** work units ordered by achievement %, lowest first `[proposed]` (M-02), with the gap to target in units.

| State | What the user sees |
|-------|--------------------|
| Empty | "No output yet this shift" |
| Loading | Chart placeholder |
| Error | "Timeline couldn't load" and the ranking still shows if it loaded |
| Offline | Last loaded view with an offline marker |
| Success | Not applicable |
| No permission | Only work units in the user's access |

**4. Calculation**

- Target line at time *t* = target × share of the planned window elapsed at *t*.
- Gap = target line at now − done.

> [!note]- Exact formula (for developers)
> ```
> target_at(t) = planned_qty × clip((t − planned_start) ÷ (planned_end − planned_start), 0, 1)
> gap_now      = target_at(now) − Σ total_output
> rank         = ORDER BY achievement_pct ASC
> ```

*Example:* target 1,000 over 07:00–15:00; at 11:00 the target line is 500; done 420 → gap 80 units.

*Edge cases:* output with no time stamp can't be placed on the timeline; it counts in the total and is listed as "untimed".

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER` · `WORK_ORDER_OPERATION` · `WORK_ORDER_OPERATION_DEFECT` (`recorded_at`, `qty`) |
| **Writes** | None |

**6. Rules & constraints**

1. Hourly buckets in the site's timezone.
2. **Who may see it:** as `MON-001`.

**7. Acceptance criteria**

- **AC-1** **Given** target 1,000 over 07:00–15:00 and 420 done at 11:00, **when** the timeline opens, **then**
  the target line reads 500 and the gap reads 80.
- **AC-2 (ranking)** **Given** three work units at 42%, 80% and 105%, **when** the ranking shows, **then** the 42%
  unit is first.

**8. Metrics & events**

- **Events:** `monitoring_timeline_opened` (`scope_id`, `shift_instance_id`).

**Dependencies:** `US-PROD-MON-001`

---

#### US-PROD-MON-003

**Watch each work unit live**

**Status:** 🔴 Blocked — waits on **B5**.

> **In short:** a live board with each work unit's state (running, idle, down, setup) and its running job. The
> state part has only a draft data model.

**1. Story** — As a **production supervisor**, I want to see each work unit's live state and its running job, so
that I notice a stopped machine within minutes.

**2. Context — why it is blocked**

- **Figma "Live Monitoring":** real-time machine / work unit state with the running Job Order.
- Machine state lives in `ASSET_STATE_LOG`. **Owner settled 2026-09-28: PRODUCTION** (only owner; MAINTENANCE
  reads) ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)). Its spec ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)) names the states running / idle / down /
  setup, the sources (`plc` / `manual` / `derived`) and `asset_id`, `work_unit_id`, `shift_instance_id`,
  `downtime_reason_id`, `started_at`, `ended_at`, `entered_by`, `entered_at`.
- **Settled 2026-09-29 (PO):** recording is **per asset**, and `asset_id` and `work_unit_id` are both required
  (SL-1). The work unit is resolved from the placement active at the event time and is the reporting level; a
  work unit has one primary asset, so **a live tile per work unit shows its primary asset's state**. Two write
  paths, one owner: the data-platform Silver job writes `plc` / `derived` rows, the MES app writes `manual` rows
  and reasons.
- Still `TBD`: the state field's name and whether more values exist. The row ID is `asset_state_log_id` ([F-00 keys rule](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#keys-and-constraints-on-every-table-po-2026-10-01), 2026-10-01).
- The read-only history of those rows is a separate screen, `SCR-PROD-MON-001` state log browser, filterable by
  work unit and by asset
  ([spec](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#screen--state-log-browser)), replacing FOUNDATION's retired `SCR-FND-AST-005`. No PRD-004 story covers it yet; the PO was asked for one (docs-molcadx #57).
- The running job part is already visible through [`US-PROD-PLN-005`](01-planning.md#us-prod-pln-005).

**3–8.** not applicable until B5 closes.

**Unblocked when:** the state field (and the row ID) is named in PRODUCTION's `ASSET_STATE_LOG` spec. The owner
(2026-09-28) and the recording level (SL-1, 2026-09-29) are already settled.

**Dependencies:** `US-PROD-PLN-005`

---

#### US-PROD-MON-004

**See the list of open and recent losses**

**Status:** 🟡 Partly blocked: quality losses (rejects, rework, hold) can be built now. Downtime losses wait on
B5; speed losses wait on M-03.

> **In short:** one list of what the shift lost, open ones (no reason yet) at the top. Today it holds rejects and
> rework; stops join once machine state exists.

**1. Story**

As a **production supervisor**, I want one list of the shift's losses, open ones first, so that nothing lost goes
unexplained.

**2. Context**

- **Figma "Losses List":** open and recent production losses.
- Quality losses are `WORK_ORDER_OPERATION_DEFECT` rows: one reason and one fate (`scrap`, `rework`, `hold`) per
  row, with its own unit (`uom_id`: pcs, kg, punch — PLN-Q1, 2026-10-01). A row comes from the sensor, a manual
  override, or — on a work unit with **no reject tag bound** — a `manual` entry that is the main source, not an
  override (PLN-Q2, 2026-10-01)
  ([01-planning § WORK_ORDER_OPERATION_DEFECT](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#work_order_operation_defect--one-row--one-code--one-fate-on-one-operation)).
- A loss is **open** while its reason is the generic "unclassified" reject reason.

**3. Expectation**

- A list for the chosen scope and shift: time, work unit, job, loss type, quantity with its unit, reason, fate,
  source (sensor / manual override / manual).
- Open losses first, then newest first. Filters: work unit, loss type, open only.
- Each row has **Correct reason** (`MON-005`).

| State | What the user sees |
|-------|--------------------|
| Empty | "No losses recorded this shift" |
| Loading | Row placeholders |
| Error | "Losses couldn't load — try again" |
| Offline | Last loaded list with an offline marker |
| Success | Not applicable |
| No permission | Only work units in the user's access |

**4. Calculation**

- Open = reason is "unclassified".
- Totals per fate: scrap, rework and hold summed separately. Hold is never added to scrap or rework.
- Rows in different units are never added as is (1 kg and 1 pc are not the same amount). Until **M-04** is
  answered, totals show one figure per unit `[proposed]`.

*Example:* 3 rows on S03: 20 scrap (reason "dent"), 10 rework ("unclassified"), 5 hold ("paint") → 1 open loss;
totals scrap 20, rework 10, hold 5.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER_OPERATION_DEFECT` (`defect_code_id`, `disposition`, `qty`, `uom_id`, `origin`, `recorded_at`) · `REJECT_REASON` · `UNIT_OF_MEASUREMENTS` · `WORK_ORDER_OPERATION` · `WORK_ORDER` |
| **Not yet** | Downtime rows from `ASSET_STATE_LOG` (PRODUCTION-owned, [spec](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time); B5 — recording level settled 2026-09-29, state field and row ID names `TBD`) |
| **Writes** | None |

**6. Rules & constraints**

1. A structured reason is always required; free text is never a reason.
2. **Who may see it:** access to the work unit or anything above it.

**7. Acceptance criteria**

- **AC-1** **Given** the three rows in the example, **when** the list opens, **then** the rework row is first,
  marked open, and the totals read scrap 20, rework 10, hold 5.
- **AC-2 (empty)** **Given** no defect rows this shift, **when** the list opens, **then** "No losses recorded this
  shift" shows.
- **AC-3 (source)** **Given** a row with `origin = manual` on a work unit with no reject tag, **when** the list
  opens, **then** its source reads "manual", not "manual override".
- **AC-4 (units)** **Given** a scrap row of 20 pcs and a scrap row of 2 kg on S03, **when** the totals show,
  **then** scrap reads 20 pcs and 2 kg separately, never 22.

**8. Metrics & events**

- **Metric:** `open_losses_at_shift_end`: losses still "unclassified" when the shift closes. Target falling to
  `[proposed]` 0 within 30 days of go-live.
- **Events:** `losses_list_opened` (`scope_id`, `open_count`).

**Dependencies:** `US-FND-REF-002`

---

#### US-PROD-MON-005

**Correct a loss reason**

**Status:** 🟡 Partly blocked: reject/rework reasons can be corrected now (which `origin` a correction of a `manual` row writes is **M-05**). Downtime reasons wait on B5: the rules are settled (below), but the state field and row ID names are still `TBD` in PRODUCTION's `ASSET_STATE_LOG` spec. When they come, a downtime reason change is the `reason_corrected` action on `AUDIT_LOG` with `domain_code = PROD`, `module_code = MON`, and a row of a closed shift is never edited in place — signed adjustment only ([spec](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)).

> **In short:** the supervisor picks the right reason for a loss. The first value is never lost: the change goes
> to the log with who, when, before and after.

**1. Story**

As a **production supervisor**, I want to correct the reason on a loss, with the old value kept, so that the loss
analysis is right without hiding what was first recorded.

**2. Context**

- **Figma "Log (monitoring)":** who and when edited the reason, with before and after.
- **Spec rule:** an "unclassified" reason is reclassified later with an audit trail; the fate (`disposition`)
  stays required on every row.
- A sensor row is the source of truth. A correction is a **manual override**, never a silent edit of the sensor row.
- **Downtime reasons, when B5 closes (PO 2026-09-29, SL-4):** the sensor decides **when**, the person decides
  **why**. Where a `plc` or `derived` row covers the time, its start and end stand: a reason entry adds or
  corrects `downtime_reason_id` on that row and sets `entered_by` / `entered_at`, it never creates a second row.
  A Silver recompute may re-derive times but **never overwrites a reason a person entered** (a row with
  `entered_by` set) ([spec § Operator entry](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#operator-entry--reject-and-offline-behaviour)).

**3. Expectation**

- From a loss row: **Correct reason** opens a picker of active reject reasons for that work unit, the current
  fate (changeable), and a required note.
- Confirm shows old → new before saving.

| State | What the user sees |
|-------|--------------------|
| Empty | Not applicable |
| Loading | **Confirm** disabled until the reason list loads |
| Error | "Correction not saved — the loss is unchanged" |
| Offline | "Correcting needs a connection" |
| Success | "Reason updated" and the row leaves the open list |
| No permission | **Correct reason** hidden |

**4. Calculation** — changing the fate moves the quantity between scrap, rework and hold. The job's
`scrap_qty` / `rework_qty` are recomputed from the rows, never typed.

> [!note]- Exact formula (for developers)
> ```
> scrap_qty  = Σ qty WHERE disposition = 'scrap'
> rework_qty = Σ qty WHERE disposition = 'rework'
> (hold is not added to either)
> ```

*Example:* a 10-unit row "unclassified / rework" corrected to "burr / scrap" → `rework_qty` −10, `scrap_qty` +10.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER_OPERATION_DEFECT` (incl. `uom_id`, `origin`) · `REJECT_REASON` |
| **Writes** | A `WORK_ORDER_OPERATION_DEFECT` row with `origin = manual_override` (for a corrected `manual` row: `TBD`, M-05), the same `uom_id` as the corrected row, `entered_by`, `entered_at` · recomputed `WORK_ORDER_OPERATION.scrap_qty` / `rework_qty` (unit handling: M-04) · a log row (`MON-007`) with before, after and note |
| **Later (B5)** | `ASSET_STATE_LOG` (`downtime_reason_id`, `entered_by`, `entered_at`) on the existing row — times untouched |

**6. Rules & constraints**

1. The note is required. Message: "Say why the reason changed".
2. Only active reasons valid for that work unit can be picked.
3. After the shift is closed, a correction is still allowed but is flagged "after close".
4. **Who may do it:** supervisor with access to the work unit.

**7. Acceptance criteria**

- **AC-1** **Given** a 10-unit "unclassified / rework" row, **when** it is corrected to "burr / scrap" with a note,
  **then** `rework_qty` drops by 10, `scrap_qty` rises by 10, and the log shows both values and the note.
- **AC-2 (note)** **Given** the note is empty, **when** **Confirm** is pressed, **then** "Say why the reason
  changed" shows and nothing changes.
- **AC-3 (sensor kept)** **Given** the original row came from the sensor, **when** it is corrected, **then** the
  sensor value is still visible in the log.
- **AC-4 (downtime, once B5 closes)** **Given** a `plc` stop row 10:02–10:20 with no reason, **when** the
  supervisor sets reason "jam", **then** the same row keeps 10:02–10:20, carries "jam" with `entered_by` /
  `entered_at`, and no second row is created; **when** Silver later recomputes that stop, **then** "jam" stays.

**8. Metrics & events**

- **Metric:** share of losses corrected after shift close; high means reasons aren't picked in time. Baseline
  `not yet measured`.
- **Events:** `loss_reason_corrected` (`wo_operation_defect_id`, `from_reason`, `to_reason`, `from_disposition`, `to_disposition`, `after_close`).

**Dependencies:** `US-PROD-MON-004`

---

#### US-PROD-MON-006

**Add rework to a job**

**Status:** 🟢 Ready

> **In short:** when units are sent back for rework, the supervisor adds the quantity and the reason to the job.
> It is saved as a manual override row with who and when.

**1. Story**

As a **production supervisor**, I want to add a rework quantity with its reason to a job, so that units sent back
for rework are counted correctly.

**2. Context**

- **Figma "Log (monitoring)":** who and when added rework.
- The fate lives on the row, not on the reason master (T-6 closed 2026-09-24).

**3. Expectation**

- From a job (Today view or the losses list): **Add rework** with quantity and its unit, reason (picker) and an
  optional note.

| State | What the user sees |
|-------|--------------------|
| Empty | Not applicable |
| Loading | **Save** disabled until the reason list loads |
| Error | "Rework not saved — try again" |
| Offline | "Adding rework needs a connection" `[proposed]` |
| Success | "`<n>` units added as rework" and the job's rework total updates |
| No permission | **Add rework** hidden |

**4. Calculation** — `rework_qty` = Σ rework rows (as `MON-005`; unit handling: M-04). *Example:* job rework 0 →
add 15 → 15.

*Entered manually, in any unit* (work unit without a reject sensor; PO 2026-10-01): FOUNDATION first brings the
reject to the step's unit (`OPERATION.uom_id`) — none for the same unit, the product's unit conversion for another
count/pack unit, the weight of one unit at this step for kg — then to time
([F-07 reject entered manually](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#reject-entered-manually-in-any-unit--convert-to-the-step-unit-and-time-po-2026-09-30-2026-10-01)): reject units = weight ÷ weight of one unit at this step; reject time =
reject units × this step's cycle time. *Example (F-07 example data):* Mold, 2 kg at 20 g per punch → 100 punches →
× 1 s = 100 s lost.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER_OPERATION` · `REJECT_REASON` |
| **Writes** | `WORK_ORDER_OPERATION_DEFECT` (`defect_code_id`, `disposition = rework`, `qty`, `uom_id`, `origin`, `entered_by`, `entered_at`, `recorded_at`) · recomputed `rework_qty` · a log row. `origin = manual_override` when the work unit has a reject tag bound; `origin = manual` when it has none (PLN-Q2) |

**6. Rules & constraints**

1. Quantity greater than 0. Message: "Quantity must be greater than 0".
2. A reason is required.
2a. A unit is required (`uom_id`, PLN-Q1).
2b. A weight entry whose converted units exceed the step's counted output for the period is refused: "Reject is
   more than this shift's output" (F-07).
3. The job must be `in_progress` or `completed` in a shift that isn't closed; after close it is flagged "after close".
4. **Who may do it:** supervisor with access to the work unit.

**7. Acceptance criteria**

- **AC-1** **Given** a running job with rework 0, **when** the supervisor adds 15 with reason "burr", **then**
  rework reads 15 and the log shows who, when and the quantity.
- **AC-2 (quantity)** **Given** quantity 0, **when** **Save** is pressed, **then** "Quantity must be greater than
  0" shows.
- **AC-3 (origin)** **Given** the work unit has no reject tag bound, **when** rework is added, **then** the row is
  saved with `origin = manual`; **given** it has one, **then** `origin = manual_override`.
- **AC-4 (too much)** **Given** the step counted 3,600 punches this shift, **when** 80 kg is entered at 20 g per
  punch (4,000 punches), **then** "Reject is more than this shift's output" shows and nothing is saved.

**8. Metrics & events**

- **Events:** `rework_added` (`wo_operation_id`, `qty`, `uom_id`, `defect_code_id`, `origin`, `after_close`).

**Dependencies:** `US-FND-REF-002`

---

#### US-PROD-MON-007

**See and download the monitoring log**

**Status:** 🟢 Ready — a view of the shared `AUDIT_LOG` (P-08 resolved), as in Planning.

> **In short:** every reason correction and every rework added leaves a row with who, when, before and after.
> Filterable and downloadable.

**1. Story**

As a **production supervisor**, I want to see who changed a loss reason or added rework, with before and after,
so that every correction can be traced.

**2. Context** — Figma "Log (monitoring)": who/when edited the reason (before/after), who/when added rework,
downloadable log. Same shape as [`US-PROD-PLN-011`](01-planning.md#us-prod-pln-011).

**3. Expectation** — same page layout, filters and states as `PLN-011`, with actions `create`, `update`,
`deactivate`, `reason_corrected`, `rework_added`.

**4. Calculation** — not applicable.

**5. Data & entities** — reads `AUDIT_LOG` where `domain_code = PROD`, `module_code = MON` (`action` =
`create` / `update` / `deactivate` / `reason_corrected` / `rework_added`, `entity_id` = the `WORK_ORDER_OPERATION_DEFECT` row, `changes`, `reason`,
actor, `occurred_at`). Rows are written through `US-FND-AUD-001`.

**6. Rules & constraints** — F-14 rules (append-only, same transaction, reason required, 3 years). **Who may see it:**
`production:mon:<scope>:audit_view` / `:audit_download`.

**7. Acceptance criteria** — **AC-1** **Given** a reason was corrected, **when** the log opens, **then** one row
shows user, time, old and new reason, and the note. **AC-2** **Given** a download of this log, **when** it finishes,
**then** a `download` row is added to the central Audit log (`FND` · `AUD`).

**8. Metrics & events** — changes with a missing log row: must be 0. Event `monitoring_log_downloaded` (`row_count`).

**Dependencies:** `US-PROD-MON-005`, `US-PROD-MON-006`

---

## Change notes

| Date | Change |
|------|--------|
| 2026-10-01 | prd-sync `docs-molcadx` 787e819..725362a (F-07 Q-28: manual reject in any unit, converted to the step unit before Quality): `MON-006` *Entered by weight* → *Entered manually, in any unit*; F-07 anchor renamed. M-04 note: F-07 now converts every reject row to `OPERATION.uom_id` for Quality; how `scrap_qty` / `rework_qty` are stored is still open |
| 2026-10-01 | prd-sync `docs-molcadx` 147e8d2..787e819 (docs-molcadx #57 SL-1 / SL-4 / two write paths; PLN-Q1 / PLN-Q2): B5 open item narrowed (SL-1 resolved, overlap and recompute rules settled). `MON-003` — per-asset recording, live tile = primary asset's state, unblock condition = state field and row ID names; **status unchanged (🔴)**. `MON-004` — reject unit and `manual` source shown, AC-3, AC-4. `MON-005` — downtime reason rule (sensor decides when, person decides why; recompute keeps the reason), AC-4. `MON-006` — unit, `origin` by reject tag, weight conversion (F-07), AC-3, AC-4. `MON-001` — "pending, batch open" and "estimated" display cases (R-07). New open items **M-04** (reject unit vs `scrap_qty`) and **M-05** (`manual` origin details). `SCR-PROD-MON-001` still has no story (asked of the PO) |
| 2026-09-28 | prd-sync to `docs-molcadx@513c4f9` ([asset status decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)): `ASSET_STATE_LOG` is PRODUCTION's (spec in 02-monitoring). B5 open item, `MON-003` (context, unblock condition, `SCR-PROD-MON-001` noted), `MON-004` reads, `MON-005` status updated. **No status change:** the spec still has `TBD` fields and SL-1 |
| 2026-09-25 | prd-sync to `docs-molcadx@327bb64`: `MON-007` — generic `download` removed from the module's actions; downloading the log is an `FND` · `AUD` row |
| 2026-09-25 | prd-sync to `docs-molcadx@171a333`: `MON-007` lists the generic actions `create` / `update` / `deactivate` alongside `reason_corrected` / `rework_added` / `download` (spec 02-monitoring § Log) |
| 2026-09-24 | Module created from the PO's Figma (Dashboard · Live Monitoring · Losses List + log) and PRODUCTION 02-monitoring. 7 stories |
