# PRD-003 · Reference & Master Data (`REF`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-05. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Terms like *tenant access*, *site access*, *implementor* and *POT / PBT / APT* are defined in the [README glossary](README.md#glossary).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| T-1 | `US-FND-REF-001`, `US-FND-REF-002` | The real downtime reason and reject reason lists. This PRD builds the container only. | Operations (`ops-domain`) |
| T-2 | `US-FND-REF-001` | Whether a more detailed sub-category exists above the 3-value `downtime_category`. | `ops-domain` (per F-05) |
| ~~[T-6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#96-whats-still-unknown)~~ | `US-FND-REF-002` | ~~Does a reject reason need disposition?~~ **Closed 2026-09-24:** disposition on reject/actual (`WORK_ORDER_OPERATION_DEFECT`), not on `REJECT_REASON`. | — |
| `TBD` (unique factor) | `US-FND-REF-005` | Is `seconds_per_unit` unique per enterprise, or may several named durations share a factor? Decides whether the screen warns. | `foundation-domain` |
| ~~`TBD` (1-second row)~~ | `US-FND-REF-005` | ~~Is the "row worth exactly 1 second" summary still needed?~~ **Resolved 2026-09-28 (PO): retired.** A typed rate is saved in the time unit the user picked, so no 1-second row is needed ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md), addendum) | — |
| `TBD` (deactivation field) | `US-FND-REF-005`, `US-FND-REF-006` | Which deactivation field `TIME_CONVERSIONS` and `UNIT_OF_MEASUREMENTS` carry. F-05 §9.5 forbids hard delete, but §9.3/§9.4 list no `deleted_at` or `valid_from`/`valid_to`. | `foundation-domain` |
| [UX-REF-2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#open-questions) | `US-FND-REF-001`, `002`, `004`, `005` | Is each Reference page title the same as its sidebar label (e.g. "Downtime reasons"), or the screen name (e.g. "Downtime reason manager")? Until decided every `ref-*.title` row is `TBD — PO`. | PO |
| [UX-REF-3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#open-questions) | `US-FND-REF-001`, `002`, `004`, `005`, `006` | Use the offline text "A connection is required to change master data" (`ref-003.offline`) and the save-error text "Could not save. Your entries are not lost. Try saving again." (`ref-002`/`ref-003.error.save`) on every Reference screen? Until then those rows on the other screens are `TBD — PO`. | PO |
| Screen text `TBD — PO` rows | `US-FND-REF-001`, `002`, `004`, `005`, `006` | Many labels, columns, summaries and messages in the *Screen text* tables have no wording yet ([UX 05 Screen text](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#screen-text)). Builds show `[TBD copy: <key>]`. | PO |
| T-7 | `US-FND-REF-001`, `US-FND-REF-002`, `US-FND-REF-004`, `US-FND-REF-005` | Keys (2026-10-01): is `code` unique on the reason, unit and time-conversion lists — per tenant, among non-deleted rows? | `foundation-domain` / PO |
| T-8 | `US-FND-REF-003` | Keys (2026-10-01): may the same (reason, asset) pair appear twice among non-deleted rows? If not, a partial unique index | `foundation-domain` / PO |

## Stories


| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-REF-001`](#us-fnd-ref-001) | Manage the downtime reason taxonomy | 🟢 Ready | As a **plant admin**, I want to build the list of downtime reasons with each one classed planned, unplanned, or small stop, so that Availability can separate time that was meant to stop from time that was lost. → [detail](#us-fnd-ref-001) | `—` |
| [`US-FND-REF-002`](#us-fnd-ref-002) | Manage the reject reason taxonomy | 🟢 Ready | As a **plant admin**, I want to build the reject reason list as a list separate from downtime reasons, so that quality analysis does not get mixed into machine-stop analysis. → [detail](#us-fnd-ref-002) | `—` |
| [`US-FND-REF-003`](#us-fnd-ref-003) | Scope the reason list per asset | 🟢 Ready | As a **line operator**, I want to see only the reasons that can actually happen on my machine, so that I am not scrolling past other machines' reasons while the line is stopped. → [detail](#us-fnd-ref-003) | `US-FND-REF-001`, `US-FND-REF-002`, `US-FND-AST-002` |
| [`US-FND-REF-004`](#us-fnd-ref-004) | Manage the unit of measure list | 🟢 Ready | As a **plant admin**, I want to register the units this plant uses along with their symbols, so that every quantity carries a unit that is named the same way across the system. → [detail](#us-fnd-ref-004) | `—` |
| [`US-FND-REF-005`](#us-fnd-ref-005) | Manage time conversions | 🟢 Ready | As a **plant admin**, I want to register named durations whose seconds factor the system computes, so that every operation standard resolves to seconds. → [detail](#us-fnd-ref-005) | `—` |
| [`US-FND-REF-006`](#us-fnd-ref-006) | Deactivate a reference row without breaking history | 🟡 Partly blocked | As a **plant admin**, I want to stop a reason or unit being offered without deleting it, so that older records referencing that row stay readable. → [detail](#us-fnd-ref-006) | `US-FND-REF-001`, `US-FND-REF-002`, `US-FND-REF-004` |

## Reference data sidebar

What users see: every flat pick-list lives in one **Reference data** group in the sidebar
([UX 05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#reference-data-sidebar-po-decision-2026-09-23)). Each list is edited **only** there; other
screens show it read-only. Stories stay in the module that owns the data, so some of these screens are built
by stories in other files.

| # | Sidebar item | Screen | Built by | Story file |
|---|---|---|---|---|
| 1 | Units of measure | `SCR-FND-REF-001` | [`US-FND-REF-004`](#us-fnd-ref-004) | this file |
| 2 | Time conversions | `SCR-FND-REF-002` | [`US-FND-REF-005`](#us-fnd-ref-005) | this file |
| 3 | Downtime reasons | `SCR-FND-REF-003` | [`US-FND-REF-001`](#us-fnd-ref-001) | this file |
| 4 | Reject reasons | `SCR-FND-REF-004` | [`US-FND-REF-002`](#us-fnd-ref-002) | this file |
| 5–8 | Site / Area / Work center / Work unit classes | `SCR-FND-REF-005`–`008` | [`US-FND-SIT-004`](01-site-hierarchy.md#us-fnd-sit-004) | Site Hierarchy |
| 9 | Asset classes | `SCR-FND-REF-009` | [`US-FND-AST-001`](04-equipment-taxonomy.md#us-fnd-ast-001) | Equipment Taxonomy |
| 10 | Asset types | `SCR-FND-REF-010` | [`US-FND-AST-001`](04-equipment-taxonomy.md#us-fnd-ast-001) | Equipment Taxonomy |

**Not here: data sources.** They carry connection secrets and a live status, so they have their own sidebar
group, **Connections** ([UX 13](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/13-data-source.md#connections-sidebar-group-po-decision-2026-09-25)),
built by [`US-FND-DSR-001`](13-data-source.md#us-fnd-dsr-001) (PO, 2026-09-25). Sidebar labels above are the
`ref-sidebar.item.*` screen text rows in [UX 05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#reference-data-sidebar--shared-text); the group label is `ref-sidebar.group` "Reference data".

Not in the sidebar: deactivating a row ([`US-FND-REF-006`](#us-fnd-ref-006)) happens on each list's own
screen, and **which reasons apply to which machine** ([`US-FND-REF-003`](#us-fnd-ref-003)) is set on the
machine's asset form, because it belongs to one machine. **Defect codes are item 4:** they were merged into
reject reasons on 2026-09-23 ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-23-defect-code-merged-into-reject-reason.md)).

## Detail blocks

---

#### US-FND-REF-001

**Manage the downtime reason taxonomy**

**Status:** 🟢 Ready

> **In short:** the implementor builds the list of downtime reasons and gives each one a class: planned,
> unplanned or small stop. The class decides how a stop's minutes count towards Availability, so one wrong
> class quietly changes the number.

**1. Story**

As a **plant admin**, I want to build the list of downtime reasons with each one classed planned, unplanned,
or small stop, so that Availability can separate time that was meant to stop from time that was lost.

**2. Context**

- **Why:** when the cause of a stop is typed as free text, nobody can analyse root causes. F-05 calls this the
  number one way downtime data goes wrong
  ([F-05 §3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#why-it-matters--what-goes-wrong-without-it)).
- **The class matters:** it isn't just a label. It decides whether a stop's minutes come out of POT or out of
  PBT, and that changes the Availability figure directly.
- **Who and where:** an implementor at a desk while setting up a plant, in long sessions, on a stable network.
- **What goes in the list:** not known yet. It must come from Operations (T-1).

**3. Expectation**

- Its own menu entry and screen, `SCR-FND-REF-003` ([UX 05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md)),
  not a section inside a combined "Reference & Master Data" screen.
- A table of reasons with code, name and class columns, which can be filtered by class.
- A main button, "Add reason".
- **What the user can read within 3 seconds:** how many reasons each class has, because an empty class means
  one way of counting Availability will never be used.

| State | What the user sees |
|-------|--------------------|
| Empty | "No downtime reasons yet", plus the consequence: every stop will be recorded unlabelled and counted as unplanned |
| Loading | A table placeholder. The filters can still be pressed |
| Save error | The form keeps everything typed, with "Could not save. Your entries are not lost. Try saving again." |
| Offline | Master data can't be changed offline. Show "A connection is required to change master data" and let the user read what is already loaded |
| Success | The new row appears at the top, marked new. The form is ready for the next entry, because implementors enter many rows one after another |
| No permission | "Only subjects whose IDP-asserted scope claim covers the tenant can change reference master data. Contact your plant admin." |

Text limits: codes up to 20 characters, names up to 80 characters.

*Screen text* — copied word for word from [UX 05 `SCR-FND-REF-003`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#scr-fnd-ref-003--downtime-reason-manager).
The UX doc is the source; if this table and UX 05 differ, UX 05 wins. `TBD — PO` = wording not decided
yet (the build shows `[TBD copy: <key>]`, never its own words). The state table above says what each state
does; this table says what it reads.

| Key | Where / state | Text |
|---|---|---|
| `ref-sidebar.item.downtime-reasons` | Sidebar item 3 → `SCR-FND-REF-003` | "Downtime reasons" |
| `ref-003.title` | Page title | TBD — PO (see UX-REF-2) |
| `ref-003.summary` | Number of active reasons per class | TBD — PO |
| `ref-003.filter.class` | Class select above the table, and its options (unplanned, planned, small stop) | TBD — PO (PRD names the three classes, gives no labels) |
| `ref-003.table.columns` | Columns: code, name, class | TBD — PO (PRD names the columns, gives no labels) |
| `ref-003.action.add` | Main button | "Add reason" |
| `ref-003.form.fields` | Form labels: code, name, class | TBD — PO |
| `ref-003.form.save` | Form button | "Save" |
| `ref-003.default` | Default state | N/A — no text: table (rows above) |
| `ref-003.empty` | Empty state | "No downtime reasons yet" |
| `ref-003.empty.consequence` | Empty state, consequence line — must say every stop will be recorded unlabelled and counted as unplanned | TBD — PO |
| `ref-003.empty.filtered` | Empty after filtering by class (from the Screens table above) | "no matches for this category" |
| `ref-003.warning.planned-empty` | No active reason in the planned class (AC-4) — must say planned stops will be recorded as unplanned while that class is empty | TBD — PO |
| `ref-003.loading` | Loading state (filters stay usable) | N/A — no text: "A table placeholder" |
| `ref-003.error.save` | Save error — form keeps everything typed | "Could not save. Your entries are not lost. Try saving again." |
| `ref-003.offline` | Offline state — loaded data stays readable, add button disabled | "A connection is required to change master data" |
| `ref-003.success` | Success — new row at the top, "marked new"; the marker label is not given | TBD — PO |
| `ref-003.no-permission` | No permission — read only, no edit buttons | "Only subjects whose IDP-asserted scope claim covers the tenant can change reference master data. Contact your plant admin." |
| `ref-003.error.duplicate-code` | Save, code already used — message under the code field naming the reason already using it | TBD — PO |
| `ref-003.error.class-required` | Save, no class chosen — under the class field | "Reason class is required" |
| `ref-003.error.class-change-blocked` | Change the class of a reason past stops use (AC-5) — must say past Availability would change, and the way out: deactivate it and create a new reason | TBD — PO |

**4. Calculation**

*The class decides which subtraction a stop goes into.* The two must never be mixed
([F-07.1 Availability](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#availability)):
- Planned stops are taken off POT, which gives PBT.
- Unplanned stops are then taken off PBT, which gives APT.
- Small stops are taken off neither. They count as a Performance loss instead ([US-FND-KPI-002](07-kpi.md#us-fnd-kpi-002)).
- **Example:** a shift from 08:00 to 16:00 gives a POT of 480 minutes. One 60-minute "scheduled cleaning" stop
  classed planned gives a PBT of 480 − 60 = 420. Three unplanned stops of 12, 18 and 12 minutes give an APT of
  420 − 42 = 378. Availability = 378 / 420 = **90.0%**.
- **If the same stop gets the wrong class:** had that 60-minute stop been classed unplanned, PBT would be 480
  and APT 480 − 102 = 378, so Availability = 378 / 480 = **78.8%**. One wrong class moves the number by 11.2
  percentage points, with no sign of it anywhere on screen.

> [!note]- Exact formula (for developers)
> ```
> PBT = POT − Σ duration of stops with downtime_category = planned
> APT = PBT − Σ duration of stops with downtime_category = unplanned
> ```
> `small_stop` is subtracted from neither.

*The summary figure on the screen*
- The number of active (not deactivated) reasons in each class.

> [!note]- Exact formula (for developers)
> ```
> active_reasons_per_class = COUNT(DOWNTIME_REASON WHERE downtime_category = <class> AND deleted_at IS NULL)
> ```

*Edge cases*
- If the planned class has no active reasons, every planned stop will be recorded as unplanned. Show a warning
  on the normal screen instead of staying silent.
- Deactivated reasons are left out of the summary, but can still be read on past records.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `DOWNTIME_REASON` (`downtime_reason_id`, `downtime_category`, `code`, `name`, `created_at`, `updated_at`, `deleted_at`) per [F-05 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#91-downtime_reason-and-reject_reason) |
| **Reads only** | `ASSET_STATE_LOG` (owned by PRODUCTION, [PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)), to count how many historical records reference a row before it is deactivated |
| **Not real fields: don't add** | `REASON_CODE.category` (a seven-value detailed category) does not exist in F-05. No new fields. Whether a finer sub-category exists is still open (T-2) |

**6. Rules & constraints**

1. Codes can't repeat anywhere in the enterprise. The refusal message names the row already using that code.
2. Every reason must have a class: unplanned, planned or small stop, stored as `unplanned` / `planned` /
   `small_stop`. No fourth class without a decision by `foundation-domain` and the Product Owner.
3. Nothing is physically deleted. Deactivating goes through [`US-FND-REF-006`](#us-fnd-ref-006).
4. Changing the class of a reason that past stop records already use would change past Availability, so that
   change is refused. The right way: deactivate the old reason and create a new one with the right class.
5. Downtime reasons and reject reasons are two separate lists. This screen must not show them as one nested
   table.
6. **Who may change it:** creating, editing or deactivating a reason needs **tenant access**. Downtime reasons
   belong to the whole tenant and aren't tied to any place in the site hierarchy
   ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#reference--master-data-ref--05-reference-master-datamd-9)),
   so site or enterprise access alone is not enough. Viewing only needs access that covers the reason, at any
   level.
7. Master data needs a connection. There is no offline mode.

**7. Acceptance criteria**

- **AC-1** **Given** an empty reason list, **when** the implementor saves a reason with code `BRK-01`, name
  "Bearing failure" and class unplanned, **then** it is saved and appears in the table with class unplanned.
- **AC-2 (validation)** **Given** code `BRK-01` is already used, **when** the implementor saves a new reason
  with code `BRK-01`, **then** the save is refused and a message under the code field names the reason already
  using it.
- **AC-3 (validation)** **Given** the form is open with no class chosen, **when** "Save" is pressed, **then**
  the form is not sent and "Reason class is required" (`ref-003.error.class-required`) appears under the class field.
- **AC-4 (empty)** **Given** no reason has the class planned, **when** the implementor opens the screen,
  **then** a warning says planned stops will be recorded as unplanned while that class is empty.
- **AC-5 (history)** **Given** reason `BRK-01` is used by 40 past stop records, **when** the implementor
  changes its class from unplanned to planned, **then** the change is refused with an explanation that past
  Availability would change, plus the way out: deactivate it and create a new reason.
- **AC-6 (error)** **Given** the connection drops during a save, **when** the save is tried, **then** the form
  keeps everything typed and the message says the data was not saved.
- **AC-7 (offline)** **Given** the device is offline, **when** the implementor opens the screen, **then** a
  message says master data needs a connection and the add button doesn't work.
- **AC-8 (permission)** **Given** a user without tenant access, **when** they open this screen, **then** they
  can only read, there are no edit buttons, and the access they need is explained.

**8. Metrics & events**

- **Metric:** `stops_labelled`, the share of recorded stops that have a downtime reason. The baseline isn't
  measured yet: measure it over the first 14 days of stop recording on the pilot line, before 2026-10-15. The
  target is set once the baseline exists. Source: the stop log.
- **Counter-metric:** how many reasons are deactivated within 14 days of being created. A high number means
  the list was guessed instead of taken from Operations.
- **Events:** `downtime_reason_created` (`downtime_category`, `site_id`) ·
  `downtime_reason_category_change_blocked` (`reason_code`, `referencing_log_count`).

**Dependencies:** `—`

---

#### US-FND-REF-002

**Manage the reject reason taxonomy**

**Status:** 🟢 Ready

> **In short:** the implementor builds a flat list of reject reasons, kept apart from downtime reasons. It
> breaks rejects down for a Pareto chart; it never changes the Quality figure.

**1. Story**

As a **plant admin**, I want to build the reject reason list as a list separate from downtime reasons, so
that quality analysis does not get mixed into machine-stop analysis.

**2. Context**

- **Also the defect code list:** since 2026-09-23 there is one list for reject reasons and quality defect codes
  ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-23-defect-code-merged-into-reject-reason.md)). It is not license-gated.
- **Flat on purpose:** a reject reason has only a code and a name, with no class at all. This difference from
  downtime reasons is intended, not a mistake
  ([F-05 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#91-downtime_reason-and-reject_reason)).
  Copying the downtime class onto reject reasons is a mistake F-05 already warns about, as its pitfall
  number 4.
- **Who and where:** an implementor at a desk while setting up a plant.
- **What goes in the list:** not known yet (T-1).

**3. Expectation**

- Its own menu entry and screen, `SCR-FND-REF-004` ([UX 05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md)),
  not a section inside a combined "Reference & Master Data" screen.
- A two-column table, code and name, with search.
- No class filter, because there is no class.
- **What the user can read within 3 seconds:** how many active reject reasons there are.

| State | What the user sees |
|-------|--------------------|
| Empty | "No reject reasons yet", plus the consequence: the reject Pareto will have no breakdown and every reject lands in a single bar |
| Loading | A table placeholder |
| Save error | The form keeps everything typed, with a message that the save failed |
| Offline | Not available, like all master data |
| Success | The new row appears at the top, marked new. The form is ready for the next entry |
| No permission | As in [`US-FND-REF-001`](#us-fnd-ref-001) |

Text limits: codes up to 20 characters, names up to 80 characters.

*Screen text* — copied word for word from [UX 05 `SCR-FND-REF-004`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#scr-fnd-ref-004--reject-reason-manager).
The UX doc is the source; if this table and UX 05 differ, UX 05 wins. `TBD — PO` = wording not decided
yet (the build shows `[TBD copy: <key>]`, never its own words). The state table above says what each state
does; this table says what it reads.

| Key | Where / state | Text |
|---|---|---|
| `ref-sidebar.item.reject-reasons` | Sidebar item 4 → `SCR-FND-REF-004` | "Reject reasons" |
| `ref-004.title` | Page title | TBD — PO (see UX-REF-2) |
| `ref-004.summary` | Number of active reject reasons | TBD — PO |
| `ref-004.search` | Search field | TBD — PO |
| `ref-004.table.columns` | Columns: code, name | TBD — PO (PRD names the columns, gives no labels) |
| `ref-004.action.add` | Button to add a reject reason | TBD — PO |
| `ref-004.form.fields` | Form labels: code, name | TBD — PO |
| `ref-004.form.save` | Form button | "Save" |
| `ref-004.default` | Default state | N/A — no text: table (rows above) |
| `ref-004.empty` | Empty state | "No reject reasons yet" |
| `ref-004.empty.consequence` | Empty state, consequence line — must say the reject Pareto will have no breakdown and every reject lands in a single bar | TBD — PO |
| `ref-004.loading` | Loading state | N/A — no text: "A table placeholder" |
| `ref-004.error.save` | Save error — form keeps everything typed; PRD: "a message that the save failed" (see UX-REF-3) | TBD — PO |
| `ref-004.offline` | Offline state — PRD: "Not available, like all master data" (see UX-REF-3) | TBD — PO |
| `ref-004.success` | Success — new row at the top, "marked new"; the marker label is not given | TBD — PO |
| `ref-004.no-permission` | No permission (PRD: as in US-FND-REF-001) | "Only subjects whose IDP-asserted scope claim covers the tenant can change reference master data. Contact your plant admin." |
| `ref-004.error.duplicate-code` | Save, code already used — message under the field naming the reason already using it | TBD — PO |
| `ref-004.error.name-required` | Save, name empty — under the name field | "Reason name is required" |

**4. Calculation**

- Reject reasons don't enter the Quality formula. Quality stays good quantity divided by produced quantity
  ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#quality-iso-quality-ratio-clause-6-table-11)).
  This list only splits up the rejects for a Pareto chart.
- The number of rejects per reason is used in two ways:
  1. as the Pareto breakdown;
  2. to work out the good quantity when the line has no counter for good parts: good = total − all rejects
     ([US-FND-KPI-003](07-kpi.md#us-fnd-kpi-003)).
- **Example:** of the 50 rejects on the example shift, 30 are "weight out of range", 15 "seal not tight" and
  5 "label skewed". Pareto: 60%, 30%, 10%. Quality stays 950 / 1,000 = **95.0%**; this breakdown doesn't
  change it.

> [!note]- Exact formula (for developers)
> ```
> Quality stays GQ / PQ
> Σ reject per reason   → the Pareto breakdown
> good = total − Σ reject   (when the line has no good counter)
> ```

*The summary figure on the screen*
- The number of active (not deactivated) reject reasons.

> [!note]- Exact formula (for developers)
> ```
> active_reject_reasons = COUNT(REJECT_REASON WHERE deleted_at IS NULL)
> ```

*Edge cases*
- A reject recorded without a reason still counts in the total of rejects and still lowers Quality. It shows
  in the Pareto as "no reason given" instead of being dropped. Dropping rejects without a reason would make
  Quality look better than it is.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `REJECT_REASON` (`reject_reason_id`, `code`, `name`, `created_at`, `updated_at`, `deleted_at`) per [F-05 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#91-downtime_reason-and-reject_reason) |
| **Not real fields: don't add** | No new fields. No class field (`REJECT_REASON` has nothing like `DOWNTIME_REASON`'s `downtime_category`) |
| **Boundary note** | **This is also the defect code list.** `QUALITY_DEFECT_CODE` was merged into `REJECT_REASON` on 2026-09-23 ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-23-defect-code-merged-into-reject-reason.md)), and `US-FND-WMS-005` is closed into this story. Defect recording in Production ([02-monitoring.md § Log](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md)) picks from this list |

**6. Rules & constraints**

1. Codes can't repeat anywhere in the enterprise.
2. A reject reason has no class or category. Disposition is **not** on this master — it is on the reject/actual (T-6 closed 2026-09-24).
3. Nothing is physically deleted. Deactivating goes through [`US-FND-REF-006`](#us-fnd-ref-006).
4. This list must not appear in the downtime reason picker, and the other way round.
5. **Who may change it:** creating, editing or deactivating a reject reason needs **tenant access**. Reject
   reasons belong to the whole tenant and aren't tied to any place in the site hierarchy
   ([F-00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md);
   [authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#reference--master-data-ref--05-reference-master-datamd-9)),
   so only tenant access (or broader) counts, never site access. Viewing only needs access that covers the
   reason, at any level.
6. Needs a connection.
7. ~~A reject reason has no scrap/rework disposition yet~~ **Resolved 2026-09-24:** disposition on `WORK_ORDER_OPERATION_DEFECT`, not on `REJECT_REASON`
   (`TBD — perlu konfirmasi PO + production-domain`); don't add the field before it's decided.

**7. Acceptance criteria**

- **AC-1** **Given** an empty list, **when** the plant admin saves a reason with code `WGT-01` and name "Weight
  out of range", **then** it is saved and appears in the table.
- **AC-2 (validation)** **Given** code `WGT-01` already exists, **when** it is saved again, **then** it is
  refused with a message under the field naming the reason already using it.
- **AC-3 (validation)** **Given** the name field is empty, **when** "Save" is pressed, **then** it is refused
  with "Reason name is required" (`ref-004.error.name-required`) under the name field.
- **AC-4 (list separation)** **Given** 8 downtime reasons and 5 reject reasons exist, **when** a user opens
  the reason picker on a downtime screen, **then** only the 8 downtime reasons appear and no reject reason does.
- **AC-5 (empty)** **Given** the reject reason list is empty, **when** the plant admin opens the screen,
  **then** it explains that the reject Pareto will have no breakdown while the list is empty.
- **AC-6 (error)** **Given** the connection drops during a save, **when** the save is tried, **then** the form
  keeps everything typed and the message says the data was not saved.
- **AC-7 (permission)** **Given** a user without tenant access, **when** they open this screen, **then** they
  can only read.

**8. Metrics & events**

- **Metric:** `rejects_labelled`, the share of the rejected quantity that has a reject reason. The baseline
  isn't measured yet: measure it together with `stops_labelled` over the first 14 days. The target is set once
  the baseline exists. Source: Production's defect records.
- **Counter-metric:** how many reject reasons weren't used even once in 30 days. A high number means the list
  is too long and operators give up choosing.
- **Events:** `reject_reason_created` (`site_id`) · `reject_reason_selected` (`reject_reason_id`, `asset_id`).

**Dependencies:** `—`

---

#### US-FND-REF-003

**Scope the reason list per asset**

**Status:** 🟢 Ready

> **In short:** the implementor ticks which reasons can happen on each machine, so the operator at a stopped
> line sees a short list, most-used first. A machine with no ticks shows the full list, never an empty one.

**1. Story**

As a **line operator**, I want to see only the reasons that can actually happen on my machine, so that I am
not scrolling past other machines' reasons while the line is stopped.

**2. Context**

- **Why:** without a list per machine, operators are offered reasons that don't apply, for example "product
  changeover" on a utility machine that never changes over
  ([F-05 §3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#why-it-matters--what-goes-wrong-without-it)).
- **Per machine, not per kind of machine:** there is no field that links a reason to a machine class. Don't
  set the list per class: F-05 names that too-coarse approach as its pitfall number 2.
- **Two users, two settings:**
  - The **operator** is the only user of this module who isn't an implementor. They stand on the shop floor
    with the line stopped, maybe wearing gloves, with a small screen and an unreliable network. Every second
    spent choosing a reason is a second the line isn't being fixed.
  - Setting up which reasons belong to which machine is **implementor** work at a desk.

**3. Expectation**

*Implementor screen*
- On the machine's **asset form** (`SCR-FND-AST-007`, [UX 04](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/04-asset.md)):
  a tick list of reasons in two groups, downtime reasons and reject reasons. Exact placement in the form is
  `TBD` with UX. The reason lists themselves are edited only in the Reference sidebar (`SCR-FND-REF-003`/`004`).
- **What the user can read within 3 seconds:** how many reasons are already ticked for this machine.

*Operator screen*
- Only the reasons ticked for the machine that has stopped, ordered by how often each was used on that machine
  in the last 30 days, most used first.
- Touch areas at least 44 by 44 pixels.

| State | What the user sees |
|-------|--------------------|
| Empty, implementor side | "No reasons marked for this asset yet", plus the consequence: the operator will see the entire list |
| Empty, operator side | If this machine has no reasons ticked at all, every active reason is shown instead of an empty list. A stopped line must always be possible to label |
| Loading, operator side | The last list saved on the device stays on screen with a refreshing indicator. The list is never held back waiting for the network |
| Error | The list saved on the device is used, with a "list may be out of date" indicator |
| Offline, operator side | The list saved on the device stays usable, and the choice is queued to be sent when the connection is back. This is the only part of this module that works offline, because it is part of recording a stop |
| Offline, implementor side | Not available |
| Success | The tick is saved without reloading the page and the ticked count updates straight away |
| No permission | An implementor without site access (or enterprise or tenant access) sees the ticks read-only. Operators never see the tick screen |

**4. Calculation**

*The list the operator sees*
- The reasons ticked for this machine, leaving out any tick or reason that has been deactivated.
- If that leaves nothing, every active reason is shown.
- Reject reasons work the same way, through their own ticks.

> [!note]- Exact formula (for developers)
> ```
> downtime_list(asset) = SELECT DOWNTIME_REASON JOIN DOWNTIME_REASON_ASSET USING (downtime_reason_id) WHERE DOWNTIME_REASON_ASSET.asset_id = <asset> AND DOWNTIME_REASON_ASSET.deleted_at IS NULL AND DOWNTIME_REASON.deleted_at IS NULL
>
> if empty → SELECT DOWNTIME_REASON WHERE deleted_at IS NULL
>
> The same shape applies to REJECT_REASON through REJECT_REASON_ASSET.
> ```

*Order*
- The reason used most often on this machine in the last 30 days comes first. Reasons used equally often are
  in name order.
- **Example:** the plant has 24 active downtime reasons. Filler F-01 has 7 of them ticked. The operator sees 7
  instead of 24, so 71% of the rows are gone from the list. Of those 7, "bearing failure" was used 11 times and
  "film out" 4 times in 30 days, so "bearing failure" comes first.

> [!note]- Exact formula (for developers)
> ```
> ORDER BY COUNT(ASSET_STATE_LOG WHERE asset_id = <asset> AND downtime_reason_id = this AND started_at >= now − 30 days) DESC, name ASC
> ```

*Edge cases*
- A machine whose ticked reasons have all been deactivated is treated like a machine with no ticks: show the
  full active list.
- A reason deactivated after it was used can still be read on old records, but doesn't appear in pickers for
  new entries.
- A part that sits inside another machine (a child asset) can't have reasons ticked. Both kinds of tick must
  point at a whole Equipment unit ([F-05 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#95-rules)).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `DOWNTIME_REASON_ASSET` (`downtime_reason_asset_id` PK, `downtime_reason_id`, `asset_id`, `downtime_code`, `asset_tag`, timestamps) · `REJECT_REASON_ASSET` (`reject_reason_asset_id` PK, `reject_reason_id`, `asset_id`, `reject_code`, `asset_tag`, timestamps), per [F-05 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#92-downtime_reason_asset-and-reject_reason_asset--asset-scoped-join-tables) |
| **Reads only** | `DOWNTIME_REASON`, `REJECT_REASON`, `ASSET` (`asset_id` on both join tables must be an Equipment unit: an `ASSET` with `parent_asset_id IS NULL`), and `ASSET_STATE_LOG` (owned by PRODUCTION, [PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)) for the frequency ordering. Ordering by `asset_id` assumes the log carries `asset_id`; whether it is required depends on PRODUCTION's open item SL-1 (recording level) |
| **Copies, not keys** | `downtime_code`, `reject_code`, and `asset_tag` are denormalised copies for readability. They are **not** the source of truth and must not be used as lookup keys. The source stays the parent row, through the foreign key |
| **Not real fields: don't add** | `REASON_CODE.applies_to_asset_class_id` does not exist. Neither join table has an `ASSET_CLASS` field |

**6. Rules & constraints**

1. Reasons are ticked per machine, not per machine class. Neither kind of tick has a class field.
2. A tick must point at an Equipment unit, meaning a machine that doesn't sit inside another machine. A child
   part is refused with a message naming the machine it belongs to.
3. A machine with no ticks means the full list, not an empty list. Never turn this rule around: an empty list
   makes it impossible to label a stop at all.
4. The same reason can be ticked only once (one active tick) per machine.
5. Downtime reasons and reject reasons are ticked in two separate places and must not be mixed on one operator
   screen.
6. **Who may change it:** creating or editing the ticks needs **site access** (or enterprise or tenant access),
   because the ticks belong to a site. Viewing the operator's reason list only needs access that covers the
   work unit where the machine is placed, at any level.
7. The operator side must keep working offline for up to 8 hours from the list saved on the device. The
   implementor side doesn't work offline.

**7. Acceptance criteria**

- **AC-1** **Given** filler F-01 has 7 of 24 downtime reasons ticked, **when** the operator opens the reason
  picker for a stop on F-01, **then** exactly 7 reasons appear.
- **AC-2 (ordering)** **Given** "bearing failure" was used 11 times and "film out" 4 times on F-01 in 30 days,
  **when** the operator opens the picker, **then** "bearing failure" appears above "film out".
- **AC-3 (empty)** **Given** filler F-02 has no reasons ticked at all, **when** the operator opens the reason
  picker on F-02, **then** every active downtime reason appears and the list is never empty.
- **AC-4 (validation)** **Given** the implementor chooses a part (a child module) inside F-01, **when** they
  try to tick a reason for it, **then** it is refused with a message naming the parent machine as the right
  choice.
- **AC-5 (separation)** **Given** F-01 has 7 downtime reasons and 3 reject reasons ticked, **when** the
  operator opens the picker on a downtime screen, **then** those 3 reject reasons don't appear.
- **AC-6 (offline)** **Given** the operator's device is offline, **when** they pick a reason from the list
  saved on the device, **then** the choice is saved on the device and sent when the connection returns,
  without making the operator pick again.
- **AC-7 (deactivated)** **Given** "film out" is deactivated today, **when** the operator opens the picker
  tomorrow, **then** that reason doesn't appear, and last month's stops that used it still show its name.
- **AC-8 (permission)** **Given** a user without site access to that site (and without enterprise or tenant
  access), **when** they open the tick screen, **then** they can only read and the checkboxes don't work.

**8. Metrics & events**

- **Metric:** `reason_selection_time`, the median time from opening the reason picker to choosing a reason.
  The baseline isn't measured yet: measure it over the first 14 days of use on the pilot line. The target is
  set once the baseline exists. Source: the difference in `client_ts` between `reason_picker_opened` and
  `downtime_reason_selected`.
- **Counter-metric:** the share of stops that stay unlabelled. If the list per machine is too short, the right
  reason is missing and the operator gives up. Limit: it must not rise above the `stops_labelled` baseline.
- **Events:** `reason_picker_opened` (`asset_id`, `list_size`, `is_fallback_full_list`) ·
  `downtime_reason_selected` (`downtime_reason_id`, `asset_id`, `position_in_list`) ·
  `reason_asset_mapping_changed` (`asset_id`, `mapped_count`).

**Dependencies:** `US-FND-REF-001`, `US-FND-REF-002`, `US-FND-AST-002`

---

#### US-FND-REF-004

**Manage the unit of measure list**

**Status:** 🟢 Ready

> **In short:** the implementor registers the plant's units (kg, pcs, box…), each inside a measurement group.
> This screen holds no conversion factors: converting between units is always per product.

**1. Story**

As a **plant admin**, I want to register the units this plant uses along with their symbols, so that every
quantity carries a unit that is named the same way across the system.

**2. Context**

- **Why:** without one shared unit list, each module makes its own, and the same fact gets typed twice and then
  drifts apart ([F-05 §3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#why-it-matters--what-goes-wrong-without-it)).
- **Know this before building:** **a unit has no general conversion factor.** Converting between units only
  exists per product, in that product's unit conversions
  ([`US-FND-PRO-003`](06-product.md#us-fnd-pro-003)), because one case of Product A holds 24 pieces while one
  case of Product B holds 36. A general unit list can't hold that fact.
- **Measurement groups:** [F-05 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#94-measurement_group-and-unit_of_measurements)
  defines measurement groups: a controlled top-level list, at the same level as asset classes, not free text
  typed by the implementor. Every unit is truly linked to one group.
- **One story, one screen:** [F-05's own UX doc](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md) treats units
  and measurement groups as one screen (`SCR-FND-REF-001`).
- **Who and where:** an implementor at a desk while setting up a plant, once, with only a few rows.

**3. Expectation**

- Its own menu entry and screen, `SCR-FND-REF-001` ([UX 05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md)),
  not a section inside a combined "Reference & Master Data" screen.
- Units are grouped and shown **by measurement group**, for example a "Mass" section holding `kg`, `g`,
  `ton` and a "Count" section holding `pcs`, `box`, `case`. This shows at a glance which units measure the same
  kind of thing:
  - `kg` and `g` sit in the same group: the kind of pair a per-product conversion can sensibly link.
  - `kg` and `pcs` sit in different groups and are never offered as a general conversion pair.
- Each unit row shows its code, name, symbol and group.
- **No** conversion factor column and **no** "set conversion" button, because conversions don't belong here.
  A link goes to the per-product conversion screen, so the implementor doesn't look in the wrong place.
- **What the user can read within 3 seconds:** how many active units each group has, and which units are
  already used as a product's base unit.

| State | What the user sees |
|-------|--------------------|
| Empty | "No units yet", plus the consequence: products cannot be saved, because the base unit is mandatory |
| Loading | A table placeholder |
| Save error | The form keeps everything typed, with a message that the save failed |
| Offline | Not available |
| Success | The new row appears at the top of its group, marked new |
| No permission | As in [`US-FND-REF-001`](#us-fnd-ref-001). Adding a new unit is ordinary upkeep. Adding a new measurement group is a controlled addition that `foundation-domain` reviews: the button to create a new group is offered only on that review path, not mixed into ordinary unit entry |

Text limits: codes up to 20 characters, names up to 60, symbols up to 10.

*Screen text* — copied word for word from [UX 05 `SCR-FND-REF-001`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#scr-fnd-ref-001--uom-manager).
The UX doc is the source; if this table and UX 05 differ, UX 05 wins. `TBD — PO` = wording not decided
yet (the build shows `[TBD copy: <key>]`, never its own words). The state table above says what each state
does; this table says what it reads.

| Key | Where / state | Text |
|---|---|---|
| `ref-sidebar.item.units` | Sidebar item 1 → `SCR-FND-REF-001` | "Units of measure" |
| `ref-001.title` | Page title | TBD — PO (see UX-REF-2) |
| `ref-001.summary` | Per measurement group: active units, and units used as a product's base unit | TBD — PO |
| `ref-001.group-heading` | Section heading per measurement group | "\<measurement group name\>" (e.g. "Mass", "Count" — data, not copy) |
| `ref-001.table.columns` | Unit row columns: code, name, symbol, group | TBD — PO (PRD names the columns, gives no labels) |
| `ref-001.action.add-unit` | Button to add a unit | TBD — PO |
| `ref-001.action.add-group` | Button to create a measurement group — only on the `foundation-domain` review path | TBD — PO |
| `ref-001.link.product-conversions` | Link to the per-product conversion screen, with a short note on why conversions are per product (AC-4) | TBD — PO |
| `ref-001.form.fields` | Unit form labels: code, name, symbol, measurement group | TBD — PO |
| `ref-001.form.save` | Form button | "Save" |
| `ref-001.default` | Default state | N/A — no text: units grouped by measurement group (rows above) |
| `ref-001.empty` | Empty state | "No units yet" |
| `ref-001.empty.consequence` | Empty state, consequence line — must say products cannot be saved, because the base unit is mandatory | TBD — PO |
| `ref-001.loading` | Loading state | N/A — no text: "A table placeholder" |
| `ref-001.error.save` | Save error — form keeps everything typed; PRD: "a message that the save failed" (see UX-REF-3) | TBD — PO |
| `ref-001.offline` | Offline state — PRD: "Not available" (see UX-REF-3) | TBD — PO |
| `ref-001.success` | Success — new row at the top of its group, "marked new"; the marker label is not given | TBD — PO |
| `ref-001.no-permission` | No permission (PRD: as in US-FND-REF-001) | "Only subjects whose IDP-asserted scope claim covers the tenant can change reference master data. Contact your plant admin." |
| `ref-001.error.duplicate-code` | Save, code already used — message under the code field naming the unit (or group) already using it | TBD — PO |
| `ref-001.error.symbol-required` | Save, symbol empty — under the symbol field | "Unit symbol is required" |
| `ref-001.error.group-required` | Save, measurement group empty | "Measurement group is required" |
| `ref-001.error.group-has-units` | Delete a measurement group that still has units — refused, names the units | TBD — PO |

**4. Calculation**

*Can two numbers be added?* Nothing on this screen can convert one unit into another. The only thing worked out
is whether two numbers may be added or compared: only when they're in the same unit.
- If the units differ, the system looks up that product's own conversion.
- If the product has no such conversion, the system refuses instead of guessing.
- **Example:** work unit A records 40 cases and work unit B records 960 pieces of the same product. They can't
  be added as they are. The system looks up the product's case-to-piece conversion and finds 24. So 40 cases =
  960 pieces, and the total is 1,920 pieces. If that conversion doesn't exist, the system shows "Cannot add:
  the case to piece conversion is not defined for this product" instead of adding 40 + 960 = 1,000.

> [!note]- Exact formula (for developers)
> ```
> can_be_added(qty_a, qty_b) = (qty_a.uom_id = qty_b.uom_id)
>
> if not: look up PRODUCT_UOM_CONVERSION for that product
>         e.g. from_uom_id = case, to_uom_id = piece → conversion_value = 24
>         no row → refuse, never estimate
> ```

*Do two units measure the same kind of thing?*
- Yes when they're in the same measurement group.
- A product conversion between units in **different** groups (for example `kg` ↔ `pcs`) is exactly the case
  F-05 §9.4 says is never general. It only makes sense as one product's known unit weight, which is why
  product conversions are always tied to a product.
- This screen doesn't refuse such a pair itself. That check belongs to `US-FND-PRO-003`, which owns product
  conversions. This screen supplies the group that the check reads.

> [!note]- Exact formula (for developers)
> ```
> same_dimension(uom_a, uom_b) = (uom_a.measurement_group_id = uom_b.measurement_group_id)
> ```

*The summary figures on the screen* (per measurement group)
- The number of active units, and the number of different units used as a product's base unit.

> [!note]- Exact formula (for developers)
> ```
> grouped by measurement_group_id:
> active_units       = COUNT(UNIT_OF_MEASUREMENTS WHERE deleted_at IS NULL)
> used_as_base_unit  = COUNT(DISTINCT PRODUCT.base_uom_id)
> ```

*Edge cases*
- A unit without a measurement group can't be saved (the group is required, see Rules).
- The starting "other" group (`other`,
  [F-05 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#94-measurement_group-and-unit_of_measurements))
  catches any unit that doesn't fit a settled group yet. Using it is allowed, not an error, but the screen
  shouldn't let it quietly become the default for every new unit.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `UNIT_OF_MEASUREMENTS` (`uom_id`, `code`, `name`, `unit`, `measurement_group_id`) per [F-05 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#94-measurement_group-and-unit_of_measurements) · `MEASUREMENT_GROUP` (`measurement_group_id`, `code`, `name`, `valid_from`, `valid_to`), same file, same section |
| **Reads only** | `PRODUCT.base_uom_id`, `PRODUCT_UOM_CONVERSION.from_uom_id` and `.to_uom_id`, `OPERATION.uom_id` (the item unit an operation's standard is per, never a time unit — [F-08 §9.2.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#921-item-unit-and-conversion-po-2026-09-28), 2026-09-28), to compute usage before deactivation. `PRODUCT_CYCLE.uom_id` is no longer read: that entity is retired (2026-09-28) |
| **Not real fields: don't add** | `UNIT_OF_MEASUREMENTS` has no `conversion_factor` field. No new fields |

**6. Rules & constraints**

1. Codes can't repeat anywhere in the enterprise, for both units and measurement groups.
2. Every unit needs a symbol. The symbol is shown next to numbers everywhere in the product, for example `kg`,
   `pcs`, `box`.
3. Every unit needs a measurement group. A unit can't be saved without one.
4. This screen must not offer a conversion factor field. Offering one would invent a field that doesn't exist
   in FOUNDATION.
5. Nothing is physically deleted. Deactivating goes through [`US-FND-REF-006`](#us-fnd-ref-006). Deleting a
   measurement group that still has units is refused, and the message names them.
6. Adding a unit is ordinary upkeep and needs no special approval. Adding a measurement group is a controlled
   addition that `foundation-domain` reviews, as serious as adding a new tag role value (`tag_role`)
   ([F-05 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#94-measurement_group-and-unit_of_measurements)).
7. **Who may change it:**
   - Creating, editing or deactivating a unit needs **tenant access**, because units aren't tied to any place
     in the site hierarchy ([F-00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md);
     [authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#reference--master-data-ref--05-reference-master-datamd-9)).
     Only tenant access (or broader) counts, never site access.
   - The same actions on a measurement group need **enterprise access** (or tenant access), because groups
     are controlled across sites.
   - Viewing only needs access that covers the item, at any level.
8. Needs a connection.

**7. Acceptance criteria**

- **AC-1** **Given** an empty unit list, **when** the implementor saves a unit with code `PCS`, name "Pieces",
  symbol `pcs` and group `count`, **then** it is saved and appears under the "Count" group in the table.
- **AC-2 (validation)** **Given** code `PCS` already exists, **when** it is saved again, **then** it is refused
  with a message under the field naming the unit already using it.
- **AC-3 (validation)** **Given** the symbol field is empty, **when** "Save" is pressed, **then** it is refused
  with "Unit symbol is required" under the symbol field.
- **AC-4 (entity boundary)** **Given** the implementor is looking for how to say that 1 box equals 12 pieces,
  **when** they open this screen, **then** there is no conversion factor field, and there is a link to the
  per-product conversion screen with a short explanation of why conversions are per product.
- **AC-5 (differing units)** **Given** two work units record the same product in `case` and `piece`, and that
  product has no conversion for that pair, **when** the system adds up the output, **then** the addition is
  refused with a message naming the unit pair that has no conversion.
- **AC-6 (dimension grouping)** **Given** `kg` is in group `mass` and `pcs` is in group `count`, **when** the
  implementor views the unit list, **then** the two appear under separate group headings and nothing on this
  screen lets anyone set a general `kg`-to-`pcs` factor.
- **AC-7 (mandatory group)** **Given** the group field is left empty, **when** "Save" is pressed, **then** it
  is refused with "Measurement group is required".
- **AC-8 (error)** **Given** the connection drops during a save, **when** the save is tried, **then** the form
  keeps everything typed and the message says the data was not saved.
- **AC-9 (permission)** **Given** a user without tenant access, **when** they open this screen, **then** they
  can only read.

**8. Metrics & events**

- **Metric:** `products_with_base_unit`, the share of active products that have a base unit filled in.
  Baseline 0% on 2026-08-24, because there are no products yet. Target 100% before the pilot line starts
  recording production. Source: the product table.
- **Counter-metric:** how many additions are refused because a conversion is missing. A number that keeps
  rising means the per-product conversions haven't been filled in, not that the rule is wrong.
- **Events:** `uom_created` (`code`, `measurement_group_id`, `site_id`) · `measurement_group_created` (`code`) ·
  `uom_conversion_missing_blocked` (`from_uom_id`, `to_uom_id`, `product_id`).

**Dependencies:** `—`

---

#### US-FND-REF-005

**Manage time conversions**

**Status:** 🟢 Ready

> **In short:** the implementor declares named durations like "30 minutes", and the system works out how many
> seconds each one is. Operation standards use these rows to turn raw numbers into seconds.

> Each time conversion stores one required number: how many seconds it is worth
> ([F-05 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#93-time_conversions)). The base unit of every conversion is **seconds**.
> A row is declared as a whole quantity plus a fixed base unit, and the system works out the seconds.

**1. Story**

As a **plant admin**, I want to register the named durations (time conversions) that operation standards are
expressed in, so that every raw cycle-time number resolves to seconds without anyone typing a seconds figure by
hand.

**2. Context**

- **Why it matters:** operations in Work Master point at a time conversion, so the list is tiny but sits on the
  path to Performance. The cycle-time multiplier in the Performance calculation ends up reading it
  ([F-05 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#93-time_conversions)).
  On an operation, the time conversion is the **only** time unit; `uom_id` there is the item unit
  ([F-08 §9.2.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#921-item-unit-and-conversion-po-2026-09-28), 2026-09-28).
- **Operations are the only user now:** product cycle times also pointed here until 2026-09-28, when
  `PRODUCT_CYCLE` was retired ([F-06 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#93-product_cycle--retired-2026-09-28)). This matches F-05 §9.3, which names operations as the only
  known user.
- **Why "30 minutes", not "1800":** a half-hour cycle is far easier to state as 30 minutes than as 0.5 hours,
  and typing seconds by hand invites the wrong number of zeros.
- **Screen:** its own menu entry and screen, `SCR-FND-REF-002`
  ([UX 05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md)), not a tab inside a combined reference screen.
- **Who and where:** an implementor at a desk, once, with very few rows.

**3. Expectation**

*Table*
- Columns: code, name, the friendly form ("30 minutes"), the stored seconds ("1,800 s"), and how many
  operations use it.
- A main button, "Add time conversion".
- **No "row worth exactly 1 second" summary** (retired 2026-09-28, PO, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) addendum). Throughput
  entry on Routing ([`US-FND-WMS-003`](08-work-master.md#us-fnd-wms-003)) saves a typed rate in the time unit the
  user picked (e.g. hour), not against a seconds row, so the page does not need to show whether one exists.

*Add / Edit dialog*
- Code and name are free-text labels. A row may be a base unit such as "Hours" or any named duration such as
  "Standard changeover".
- A **whole-number quantity** field.
- A **base unit** picker with only second / minute / hour / day. It is a fixed list that only this form
  offers: not a table, and users can't add to it.
- A live, read-only preview, "= 1,800 seconds", that updates when either input changes.
- **There is no field for typing seconds directly.**

*Editing an existing row*
- The dialog doesn't remember what was typed. It rebuilds the friendly form from the stored seconds, using the
  largest whole unit that divides them exactly: 10800 seconds opens as "3 hours", 90 seconds opens as
  "90 seconds", never "1.5 minutes".

| State | What the user sees |
|-------|--------------------|
| Empty | "No time conversions yet", plus the consequence: no operation cycle time can be saved (its time unit is mandatory) and no operation standard can be turned into seconds |
| Loading | A table placeholder |
| Save error | The dialog keeps everything typed, with "Could not save. Your entries are not lost. Try saving again." |
| Offline | Not available; master data needs a connection |
| Success | The new row appears at the top, marked new, showing both the friendly form and the seconds |
| No permission | "Only subjects whose IDP-asserted scope claim covers the tenant can change time conversions. Contact your plant admin." |

Text limits: codes up to 20 characters, names up to 80 characters.

*Screen text* — copied word for word from [UX 05 `SCR-FND-REF-002`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#scr-fnd-ref-002--time-conversion-manager).
The UX doc is the source; if this table and UX 05 differ, UX 05 wins. `TBD — PO` = wording not decided
yet (the build shows `[TBD copy: <key>]`, never its own words). The state table above says what each state
does; this table says what it reads.

| Key | Where / state | Text |
|---|---|---|
| `ref-sidebar.item.time-conversions` | Sidebar item 2 → `SCR-FND-REF-002` | "Time conversions" |
| `ref-002.title` | Page title | TBD — PO (see UX-REF-2) |
| ~~`ref-002.summary.one-second-row`~~ | ~~Shows whether a row worth exactly 1 second exists~~ **Retired 2026-09-28 (PO)** — throughput entry is saved in the unit the user picked, so no 1-second row is needed. Key not reused | — |
| `ref-002.table.columns` | Columns: code, name, friendly form, stored seconds, usage count (operations; product cycle times dropped — `PRODUCT_CYCLE` retired 2026-09-28) | TBD — PO (PRD names the columns, gives no labels) |
| `ref-002.friendly-form` | Table cell; also how the Edit dialog reopens a row | "\<quantity\> \<base unit\>" — e.g. "30 minutes", "3 hours", "90 seconds", "1 day" |
| `ref-002.seconds` | Table cell, stored seconds | "\<seconds\> s" — e.g. "1,800 s" |
| `ref-002.action.add` | Main button | "Add time conversion" |
| `ref-002.dialog.fields` | Add / Edit dialog labels: code, name, quantity, base unit | TBD — PO |
| `ref-002.dialog.base-unit.options` | Base unit picker options (fixed: second / minute / hour / day) | TBD — PO (PRD lists the four, gives no labels) |
| `ref-002.dialog.preview` | Live read-only preview under the inputs | "= \<seconds\> seconds" — e.g. "= 1,800 seconds" |
| `ref-002.dialog.save` | Dialog button | "Save" |
| `ref-002.default` | Default state | N/A — no text: table (rows above) |
| `ref-002.empty` | Empty state | "No time conversions yet" |
| `ref-002.empty.consequence` | Empty state, consequence line — must say no operation cycle time can be saved (its time unit is mandatory) and no operation standard can be turned into seconds (product cycle time dropped — `PRODUCT_CYCLE` retired 2026-09-28) | TBD — PO |
| `ref-002.loading` | Loading state | N/A — no text: "A table placeholder" |
| `ref-002.error.save` | Save error — dialog keeps everything typed | "Could not save. Your entries are not lost. Try saving again." |
| `ref-002.offline` | Offline state — PRD: "Not available; master data needs a connection" (see UX-REF-3) | TBD — PO |
| `ref-002.success` | Success — new row at the top, "marked new", showing friendly form and seconds; the marker label is not given | TBD — PO |
| `ref-002.no-permission` | No permission | "Only subjects whose IDP-asserted scope claim covers the tenant can change time conversions. Contact your plant admin." |
| `ref-002.error.quantity` | Save, quantity 0, negative or not whole | "Quantity must be a whole number greater than 0" |
| `ref-002.error.duplicate-code` | Save, code already used — message under the field naming the existing row | TBD — PO |
| `ref-002.warning.same-seconds` | Two rows worth the same seconds — whether the screen warns at all is still open (PRD open item "unique factor", `foundation-domain`) | TBD — PO |

**4. Calculation**

*Declaring a row*
- The seconds are the quantity times the seconds in the chosen base unit: 1 for a second, 60 for a minute,
  3600 for an hour, 86400 for a day. These numbers are fixed in the form, not data.
- Examples: 30 minutes → 30 × 60 = 1800. 3 hours → 3 × 3600 = 10800. 1 hour → 3600 (a row named "Hours").
  1 second → 1.

> [!note]- Exact formula (for developers)
> ```
> seconds_per_unit = quantity × base_unit_seconds
> base_unit_seconds: 1 (second), 60 (minute), 3600 (hour), 86400 (day)   ← fixed constants of this form, not data
> ```

*Rebuilding the friendly form when editing*
- Take the largest of day, hour, minute and second that divides the stored seconds with nothing left over. The
  quantity is the stored seconds divided by that unit's seconds.
- 10800 → it doesn't divide evenly by a day (86400), but it does by an hour (3600) → "3 hours".
- 1800 → "30 minutes".
- 90 → it doesn't divide evenly by a minute (60) → "90 seconds".
- 86400 → "1 day".

> [!note]- Exact formula (for developers)
> ```
> base_unit = the largest of {day, hour, minute, second} whose seconds divide seconds_per_unit with no remainder
> quantity  = seconds_per_unit / base_unit_seconds
>
> 10800 mod 86400 ≠ 0, 10800 mod 3600 = 0 → "3 hours"
> 90 mod 60 ≠ 0 → "90 seconds"
> ```

*How other records use the row*
- A raw number times the row's seconds.
- An operation cycle time of 0.5 pointing at the "Hours" row → 0.5 × 3600 = 1800 seconds.
- An operation cycle time of 20 pointing at the "Seconds" row, item unit pack → 20 seconds per pack.
- The system does this multiplication wherever the link is followed. The result is never stored as a second,
  hand-entered figure ([F-05 §9.6, T-4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#96-whats-still-unknown)).

> [!note]- Exact formula (for developers)
> ```
> duration_seconds = raw_value × seconds_per_unit
>
> OPERATION.cycle_time_value = 0.5      → "Hours" row   → 0.5 × 3600 = 1800 seconds
> OPERATION.cycle_time_value = 20       → "Seconds" row → 20 seconds (per OPERATION.uom_id, e.g. pack)
> ```

*How many records use each row* (shown per row)
- The number of operations that point at this row.

> [!note]- Exact formula (for developers)
> ```
> references = COUNT(OPERATION WHERE time_conversion_id = this)
> ```

*Edge cases*
- A quantity of 0 or less, or one that isn't a whole number, is refused before any seconds are worked out, so
  the stored seconds are always a positive whole number. A stored value that isn't one is a data fault to show,
  not to round.
- Two rows worth the same number of seconds under different names ("Hours" and "Standard shift block", both
  3600) are not forbidden by F-05. Whether the screen should warn is `TBD — perlu konfirmasi PO` (question for
  `foundation-domain`: is `seconds_per_unit` unique per enterprise, or may several named durations share a
  factor?).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `TIME_CONVERSIONS` (`time_conversions_id`, `code`, `name`, `seconds_per_unit`) per [F-05 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#93-time_conversions) |
| **Reads only** | `OPERATION.time_conversion_id`, to compute the usage count (`PRODUCT_CYCLE.time_conversion_id` no longer read — entity retired 2026-09-28) |
| **Not stored** | The quantity and base unit used to produce the factor. They are re-derived on every edit. No new fields |
| **Open** | `TBD — perlu konfirmasi PO` (route to `foundation-domain`): F-05 §9.5 forbids hard deletion of every entity in the module, yet §9.3 lists neither `deleted_at` nor `valid_from`/`valid_to` on `TIME_CONVERSIONS`. Which deactivation field this entity carries must be named before [`US-FND-REF-006`](#us-fnd-ref-006) can cover it |

**6. Rules & constraints**

1. Codes can't repeat anywhere in the enterprise. The refusal names the row already using the code.
2. The quantity is a whole number greater than 0. The base unit is one of exactly four choices. No other unit
   can be added from this screen.
3. The system always works out the seconds from those two inputs. The form must not offer a field for typing
   seconds.
4. Changing the seconds of a row that operations already use would rescale every
   standard pointing at it. FOUNDATION's shared rule applies: deactivate the old row and create a new one. How
   this list is deactivated is the `TBD` above.
5. Nothing is physically deleted ([F-05 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#95-rules)).
6. **Who may change it:** creating or editing a time conversion needs **tenant access**. Time conversions
   aren't tied to any place in the site hierarchy
   ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#reference--master-data-ref--05-reference-master-datamd-9)),
   so only tenant access (or broader) counts. That fits, because operation standards across sites and
   enterprises use these rows. Viewing only needs access that covers the item, at any level.
7. Needs a connection.

**7. Acceptance criteria**

- **AC-1** **Given** an empty list, **when** the admin saves code `STD-CHG`, name "Standard changeover",
  quantity 30 and base unit minutes, **then** the row is saved as 1800 seconds and the table shows
  "30 minutes" next to "1,800 s".
- **AC-2 (hours)** **Given** the list has one row, **when** the admin saves quantity 3 and base unit hours,
  **then** 10800 seconds is saved.
- **AC-3 (no raw input)** **Given** the Add dialog is open, **when** the admin looks for a field to type
  seconds, **then** there is none, and the seconds figure is a read-only preview that updates when the
  quantity or unit changes.
- **AC-4 (re-edit, round)** **Given** a row worth 10800 seconds, **when** the admin opens Edit, **then** the
  dialog shows quantity 3 and base unit hours.
- **AC-5 (re-edit, not round)** **Given** a row worth 90 seconds, **when** the admin opens Edit, **then** the
  dialog shows quantity 90 and base unit seconds, not 1.5 minutes.
- **AC-6 (validation)** **Given** the quantity is entered as 0, or as 2.5, **when** "Save" is pressed,
  **then** it is refused with "Quantity must be a whole number greater than 0" and no seconds are worked out.
- **AC-7 (validation)** **Given** code `STD-CHG` already exists, **when** it is saved again, **then** it is
  refused with a message under the field naming the existing row.
- **AC-8 (consumer)** **Given** an operation cycle time of 0.5 whose time conversion is worth 3600 seconds,
  **when** the KPI job works out that standard, **then** it uses 1,800 seconds.
- **AC-9 (error)** **Given** the connection drops during a save, **when** the save is tried, **then** the
  dialog keeps everything typed and the message says the data was not saved.
- **AC-10 (permission)** **Given** a user without tenant access, **when** they open this screen, **then** they
  can only read and there is no Add or Edit control.

**8. Metrics & events**

- **Metric:** `time_conversion_rows_resolvable`, the share of active operations whose
  time conversion is worth more than 0 seconds. Baseline: can't be measured yet, because no time conversion
  exists on 2026-09-03; measure it from the first pilot product set up. Target 100% before the pilot line
  records its first shift. Source: operations, joined to their time conversions.
- **Counter-metric:** how many rows have their seconds changed within 14 days of being created. A high number
  means the durations were guessed.
- **Events:** `time_conversion_created` (`quantity`, `base_unit`, `seconds_per_unit`) ·
  `time_conversion_updated` (`old_seconds_per_unit`, `new_seconds_per_unit`, `reference_count`).

**Dependencies:** `—`

---

#### US-FND-REF-006

**Deactivate a reference row without breaking history**

**Status:** 🟡 Partly blocked — the field that marks a unit or a time conversion as inactive isn't named yet (`TBD — foundation-domain`); reasons can be deactivated now.

> **In short:** retiring a reason or unit hides it from new entries but keeps it on old records. The system
> refuses if active master data still uses it, and always shows the impact before the user confirms.

**1. Story**

As a **plant admin**, I want to stop a reason or unit being offered without deleting it, so that older records
referencing that row stay readable.

**2. Context**

- **Why:** deleting a reference row leaves old records pointing at nothing. F-05 names this its pitfall
  number 3, and the rule applies to all four lists in the module
  ([F-05 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#95-rules)).
- **What most often gets lost:** a stop record from years ago that uses a reason no longer in use. It happens
  rarely, but each time it risks breaking an earlier period's reports.
- **Who and where:** the implementor, at a desk, in a short session.

**3. Expectation**

- The selected row shows a **"Deactivate"** button.
- Before the user confirms, the system shows an **impact list**:
  - how many old records use this row;
  - how many machines will lose this reason's tick along with it;
  - how many entries still in progress are blocking it.
- **What the user can read within 3 seconds:** whether anything is blocking it.

| State | What the user sees |
|-------|--------------------|
| Empty | not applicable |
| Loading | The impact list shows a placeholder while it loads. The confirm button stays locked until the impact is worked out: the user must never be able to confirm without seeing the impact |
| Error | "Could not compute impact. Deactivation was not performed." |
| Offline | Not available |
| Success | The row is shown greyed out and labelled "inactive since `<date>`", stays in the table, and disappears from every picker used for new entries |
| No permission | As in [`US-FND-REF-001`](#us-fnd-ref-001) |

*Screen text* — copied word for word from [UX 05 `SCR-FND-REF-001 … 004 (shared)`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#scr-fnd-ref-001--004--deactivate-a-row-shared).
The UX doc is the source; if this table and UX 05 differ, UX 05 wins. `TBD — PO` = wording not decided
yet (the build shows `[TBD copy: <key>]`, never its own words). The state table above says what each state
does; this table says what it reads.

| Key | Where / state | Text |
|---|---|---|
| `ref-deactivate.action` | Button on the selected row | "Deactivate" |
| `ref-deactivate.impact` | Impact list lines: old records using the row; machines losing this reason's tick; open entries blocking it | TBD — PO |
| `ref-deactivate.effective-date` | Effective date field label | TBD — PO |
| `ref-deactivate.confirm` | Confirm button | TBD — PO |
| `ref-deactivate.default` | Default state | N/A — no text: impact list (rows above) |
| `ref-deactivate.empty` | Empty state | N/A — PRD: "not applicable" |
| `ref-deactivate.loading` | Loading — impact placeholder, confirm locked; pressing confirm must say the impact is still being worked out (AC-7) | TBD — PO |
| `ref-deactivate.error` | Impact could not be computed | "Could not compute impact. Deactivation was not performed." |
| `ref-deactivate.offline` | Offline state — PRD: "Not available" (see UX-REF-3) | TBD — PO |
| `ref-deactivate.success` | Success — row greyed out, stays in the table | "inactive since \<date\>" |
| `ref-deactivate.no-permission` | No permission — no "Deactivate" button, the access needed is explained (PRD: as in US-FND-REF-001) | "Only subjects whose IDP-asserted scope claim covers the tenant can change reference master data. Contact your plant admin." |
| `ref-deactivate.error.blocked` | Refused because something blocks it — names each blocker (e.g. count and some product names; the open stop) | TBD — PO |
| `ref-deactivate.error.past-date` | Save, effective date in the past | "The effective date cannot be in the past. Historical data must not change." |

**4. Calculation**

*How many old records use the row*
- A downtime reason: the stop records that use it.
- A reject reason: the defect records that use it.
- A unit: the products that use it as their base unit, plus the product unit conversions (from or to it) and
  the operations that use it as their item unit. (Product cycle times were counted here until `PRODUCT_CYCLE`
  was retired on 2026-09-28.)
- A time conversion: the operations that use it.

> [!note]- Exact formula (for developers)
> ```
> references(DOWNTIME_REASON)      = COUNT(ASSET_STATE_LOG WHERE downtime_reason_id = this)
> references(REJECT_REASON)        = COUNT(defect records WHERE reject_reason_id = this)
> references(UNIT_OF_MEASUREMENTS) = COUNT(PRODUCT WHERE base_uom_id = this) + COUNT(PRODUCT_UOM_CONVERSION WHERE from_uom_id = this OR to_uom_id = this) + COUNT(OPERATION WHERE uom_id = this)
> references(TIME_CONVERSIONS)     = COUNT(OPERATION WHERE time_conversion_id = this)
> ```

*What blocks it* (these make the deactivation refused, not just warned about)
- Stops using this reason that haven't ended yet, plus any active master data still using this row.
- The line is clear:
  - Finished old records **don't** block. Protecting them is exactly why rows are deactivated instead of
    deleted.
  - Active master data still using the row **does** block, because new entries would fail.
- **Example:** reason "film out" is used by 312 stop records, all closed, and no active master data uses it.
  So 312 records use it, nothing blocks it, the deactivation goes ahead, and those 312 records keep showing
  the name "film out" on old reports. By contrast, unit `pcs` is the base unit of 47 active products, so there
  are 47 blockers and the deactivation is refused, with the count and some of the products named.

> [!note]- Exact formula (for developers)
> ```
> blockers = COUNT(ASSET_STATE_LOG WHERE downtime_reason_id = this AND ended_at IS NULL) + COUNT(active master rows referencing this row)
>
> "film out": references = 312, blockers = 0   → deactivation goes ahead
> pcs:        blockers = 47                     → refused
> ```

*Edge cases*
- Deactivating a downtime reason also deactivates every machine tick for it, because a tick for a retired
  reason means nothing.
- An effective date in the past is refused, because it would change history.
- A row that is already inactive can't be deactivated again.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Writes** | The deactivation marker on `DOWNTIME_REASON` and `REJECT_REASON` via `deleted_at` (named in [F-05 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#91-downtime_reason-and-reject_reason)), and on the `DOWNTIME_REASON_ASSET` or `REJECT_REASON_ASSET` rows that cascade. For `UNIT_OF_MEASUREMENTS` and `TIME_CONVERSIONS`: **`TBD — foundation-domain`**. [F-05 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#95-rules) requires deactivate rather than hard-delete (`deleted_at`/`valid_to`), but the §9.3/`§9.4` field tables list no deactivation column. Don't assume a column the field list does not name until `foundation-domain` names it |
| **Reads** | `ASSET_STATE_LOG` (owned by PRODUCTION, [PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)), Production defect records, `PRODUCT`, `PRODUCT_UOM_CONVERSION`, `OPERATION` (`PRODUCT_CYCLE` retired 2026-09-28, no longer read) |
| **Not real fields: don't add** | No physical deletion, and no extra `status` or `is_active` field |

**6. Rules & constraints**

1. Nothing is physically deleted, in any of the four lists.
2. A deactivation with blockers is refused. The message names each blocker instead of only counting them.
3. Finished old records don't block.
4. Deactivating a reason also removes its machine ticks.
5. The effective date can't be in the past.
6. An inactive row disappears from pickers for new entries and stays visible, with its name, in old data.
7. **Who may do it:** deactivating needs **tenant access** (or broader) for all four lists (downtime reasons,
   reject reasons, units and time conversions), because each belongs to the whole tenant. None of them is
   tied to a place in the site hierarchy ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#reference--master-data-ref--05-reference-master-datamd-9)).
8. Needs a connection.

**7. Acceptance criteria**

- **AC-1** **Given** reason "film out" is used by 312 records, all closed, **when** the implementor
  deactivates it with today as the effective date, **then** the deactivation succeeds and the reason
  disappears from the operator's picker.
- **AC-2 (history)** **Given** that reason is now inactive, **when** the manager opens last month's downtime
  report, **then** those 312 records still show the name "film out".
- **AC-3 (blocker)** **Given** unit `pcs` is the base unit of 47 active products, **when** the implementor
  deactivates it, **then** it is refused and the message gives the count 47 and names some of the products.
- **AC-4 (open stop blocker)** **Given** one stop using this reason is still open, **when** the deactivation is
  confirmed, **then** it is refused and that open stop is named.
- **AC-5 (cascade)** **Given** this reason is ticked for 6 machines, **when** the deactivation succeeds,
  **then** those 6 ticks are inactive too and don't appear in any machine's picker.
- **AC-6 (validation)** **Given** an effective date in the past, **when** it is saved, **then** it is refused
  with "The effective date cannot be in the past. Historical data must not change." (`ref-deactivate.error.past-date`)
- **AC-7 (loading)** **Given** the impact list is still loading, **when** the implementor presses confirm,
  **then** the button isn't active yet and the system says the impact is still being worked out.
- **AC-8 (permission)** **Given** a user without tenant access, **when** they open a reference row, **then**
  there is no "Deactivate" button and the access they need is explained.

**8. Metrics & events**

- **Metric:** `deactivations_refused`, the share of deactivation attempts refused because something blocks
  them. A high share means the impact list isn't visible early enough. The baseline isn't measured yet.
  Source: the `reference_row_deactivate_blocked` event.
- **Counter-metric:** how many old reports have different numbers after a deactivation. Must be 0.
- **Events:** `reference_row_deactivated` (`entity`, `reference_count`, `cascaded_mapping_count`,
  `effective_date`) · `reference_row_deactivate_blocked` (`entity`, `blocker_type`, `blocker_count`).

**Dependencies:** `US-FND-REF-001`, `US-FND-REF-002`, `US-FND-REF-004`

## Change notes (history — not needed to build)

- `US-FND-REF-003` · 2026-10-01 — prd-sync c1f1848..30c7087 (keys rule): PKs `downtime_reason_asset_id` / `reject_reason_asset_id` in Writes; open items T-7, T-8.
- `US-FND-REF-001` — 2026-09-02: PO decided the downtime reason list gets its own screen, `SCR-FND-REF-003`, not a section inside a combined "Reference & Master Data" screen.
- `US-FND-REF-001` — the seven-value category used to live on `REASON_CODE.category`; that field was never in F-05.
- `US-FND-REF-003` — the old `REASON_CODE.applies_to_asset_class_id` model scoped reasons per asset class; replaced by per-asset-instance join tables.
- `US-FND-REF-004` — 2026-09-03 ([Q-16](30-release-risks-questions.md), resolved): an earlier draft said `measurement_group_id` "is not used because its parent entity does not exist in F-05". That was wrong; F-05 §9.4 defines `MEASUREMENT_GROUP`, and the story now includes it. `conversion_factor` stayed out; only the `measurement_group_id` correction was new.
- `US-FND-REF-005` — BLOCKED from 2026-08-24 because `TIME_CONVERSIONS` had no numeric field (T-4). `foundation-domain` resolved T-4 on 2026-09-02 ([F-05 §9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#96-whats-still-unknown)): the stray `time_conversions` string was dropped and replaced by `seconds_per_unit`, and the base unit became seconds, not the hours earlier drafts implied. Unblocked 2026-09-03; the placeholder rows were replaced in full, per [AGENTS.md §5 rule 1b](https://github.com/molca-id/docs-molcadx/blob/main/AGENTS.md#5-aturan-kerja-berlaku-untuk-semua-agent).
- `US-FND-REF-005` — 2026-09-02: PO gave the "30 minutes, not 1800" rationale for declaring rows as quantity plus base unit.
- All stories (2026-09-23): detail blocks rewritten from one dense table into numbered sections, and
  "subject whose IDP-asserted scope claim covers …" replaced by *tenant access* / *site access* (defined in the
  README glossary). Meaning unchanged; UI copy kept verbatim.
- All stories (2026-09-23, second pass): rewritten in plain language. Entity and field names now appear only in
  section 5 and in the collapsible "Exact formula (for developers)" boxes; everywhere else items are named in
  plain words (downtime reason, reject reason, unit, measurement group, time conversion, stop record). Meaning
  unchanged; UI copy kept verbatim.
- 2026-09-23 (PO decision, Reference data sidebar): added the sidebar table above. `US-FND-REF-003`'s implementor screen is now the machine's asset form on `SCR-FND-AST-007`; the retired `SCR-FND-AST-004` no longer edits reasons.
- `US-FND-REF-002` (2026-09-23, PO decision): now also the defect code list — `QUALITY_DEFECT_CODE` merged into `REJECT_REASON`, `US-FND-WMS-005` closed into this story. Q-18 resolved; the disposition question moved here as T-6. WMS-005's offline AC (list available on the operator's device) is covered by `US-FND-REF-003`'s offline state. Sidebar item 11 removed.
- `US-FND-REF-001`, `002`, `004`, `005`, `006` — 2026-09-27 (prd-sync docs-molcadx 3cb17fa..7ad3ed8, UX 05 Screen text, PO decision 2026-09-25 "UX is the source of screen text"): each story's Expectation gained a *Screen text* table copied word for word from UX 05 with its keys (`ref-001.*` … `ref-004.*`, `ref-deactivate.*`, `ref-sidebar.*`). New rows the PRD did not have (page titles, columns, form labels, `ref-003.empty.filtered`, …) are mostly `TBD — PO`. Open items UX-REF-2 and UX-REF-3 added. The sidebar section now notes that data sources live in their own **Connections** group (UX 13). State tables unchanged. `US-FND-REF-003` unchanged: its screens (asset form, operator picker) have no Screen text block yet.
- `US-FND-REF-004`, `005`, `006` — 2026-09-28 (PO, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) B/C; prd-sync docs-molcadx 7fb3a9a..1285317):
  `PRODUCT_CYCLE` retired, so it is no longer read for unit or time-conversion usage counts, blockers or the
  REF-005 metric. `OPERATION.uom_id` is now named as the item unit an operation's standard is per, never a time
  unit. REF-005: the F-06 §9.3 link points at the renamed "retired" heading; the "F-05 list of users needs
  updating" note is dropped (operations are the only user again); `ref-002.table.columns` and
  `ref-002.empty.consequence` updated word for word from UX 05; new `TBD` on whether the 1-second row summary is
  still needed now that `US-FND-PRO-004` is retired.
- `US-FND-REF-005` · 2026-09-28 (PO answer, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) addendum; prd-sync docs-molcadx 1285317..513c4f9) — the
  "row worth exactly 1 second" summary is **retired** (a typed rate is saved in the unit picked). Removed from the
  Expectation and the declaring example; `ref-002.summary.one-second-row` shown as retired, as in UX 05. `TBD` and
  open item closed.
- `US-FND-REF-001`, `003`, `006` · 2026-09-28 (same sync, [asset status decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)) — `ASSET_STATE_LOG`
  reads now name its owner, PRODUCTION. `REF-003`: its frequency ordering by `asset_id` depends on PRODUCTION's
  SL-1 (recording level). No status change.
