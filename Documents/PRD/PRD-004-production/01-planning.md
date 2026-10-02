# PRD-004 · Production Planning (`PLN`)

> Part of [PRD-004 PRODUCTION](README.md). Spec: [PRODUCTION 01-planning](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md)
> and [05-work-master](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/05-work-master.md).
> Terms like *site access*, *Job Order* and *POT / PBT* are defined in the [README glossary](README.md#glossary).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.

> **Module layout (Figma, 2026-09-24).** Production Planning has three features: **Job Order**, **Breaktime**
> and **Execution**. The fourth box, *Product Tracing*, is deferred (lot records belong to the future
> Inventory domain). The planning **log** is a capability shared by all three features.
> Taxonomy: [`docs-molcadx` DOCS/02-domains.md](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/02-domains.md#taksonomi-platform-8-level--mengikat-semua-domain-baru).

> **Shift instances are not here.** The daily `SHIFT_INSTANCE` generator is built by `production-domain`, but
> its story is already [`US-FND-SHF-002`](../PRD-003-foundation/02-shift.md#us-fnd-shf-002) in PRD-003. It is
> not written twice.

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| **P-01** Sequence field | `PLN-002`, `PLN-004`, `PLN-005` | The Figma asks for "SKU order to be processed" (1st, 2nd, 3rd) per machine. `WORK_ORDER_OPERATION` has no sequence field, and `WORK_ORDER.priority` has values `TBD`. Which field holds the queue position? | `production-domain` |
| **P-02** Target per schedule row | `PLN-002` | One uploaded row carries a target for one SKU on one machine at one time. This PRD treats **one row as one Job Order** (`planned_qty` = target) `[proposed]`. A per-shift target on `WORK_ORDER_OPERATION` does not exist | `production-domain` |
| **P-03** Upload file | `PLN-002` | File type (`[proposed]` `.xlsx` and `.csv`), the template, and which fields are mandatory before dispatch (WM-P-2, parked 2026-09-27: the release rule already needs an active routing and BOM; still open is whether every `OPERATION` must carry its standard — `cycle_time_value` or `standard_speed_value` — before dispatch, `standard_setup_time` / `crew_size` are optional since F-08 W-7 closed 2026-09-30) | PO + ops |
| **P-04** What "schedule approval" is | `PLN-003` | `WORK_ORDER` has no `approved` status. This PRD treats **approval as the release action**, done by a supervisor `[proposed]` | PO |
| **P-05** What "achievement" counts | `PLN-004`, `PLN-007` | This PRD uses `total_output ÷ planned_qty`, the same basis as the operation close rule. Whether it should count good units only is undecided | PO |
| **P-06** Where break windows live | `PLN-008`, `PLN-009` | **Conflict.** FOUNDATION F-02 H-2 (resolved 2026-08-19) says breaks are planned stops on `ASSET_STATE_LOG` and reduce **PBT**. PRODUCTION 01-planning (2026-09-24) says breaks are excluded from **POT** and are never planned downtime. No break entity exists. Same conflict is recorded as **SL-3** in PRODUCTION's `ASSET_STATE_LOG` spec ([open questions](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#open-questions), 2026-09-28) | PO → `foundation-domain` + `production-domain` |
| **P-07** Readiness checks | `PLN-010` | No entity holds material availability, personnel readiness or work instructions. Hard block vs warning per check is `TBD` | PO + ops |
| ~~P-08~~ Log storage | `PLN-011` | **Resolved 2026-09-25 (PO):** the planning log is a view of FOUNDATION's shared `AUDIT_LOG` ([F-14](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md)), filtered to `domain_code = PROD`, `module_code = PLN`. No PRODUCTION log entity | — |
| **P-09** Planner persona | all | *Planner* is not a persona in [00-product-overview §3](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/00-product-overview.md#3-target-user) and not yet an IDP role (PRD-003 Q-02). Until it is, the production supervisor plays the planner | PO |
| ~~B3~~ Locked version type | `PLN-003` | **Resolved 2026-10-01 (PO, PLN-Q6):** FOUNDATION versions `ROUTING` and `BOM` (one row per version, `version` + `status`). `routing_id` / `bom_id` pin one `released` version; `routing_version_locked` / `bom_version_locked` are removed ([01-planning § Routing/BOM locked at release](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#routingbom-locked-at-release-b3)) | — |
| **P-11** Stage-order drafts | `PLN-003` | Releasing a finished-good order creates draft orders for the earlier stages (PLN-Q3, 2026-10-01). The spec gives the quantities (flow's BOM + planned shrinkage) but not the drafts' planned start / end (both required on `WORK_ORDER`). Question: are they derived from the finished-good order's window, or left for the planner to fill before release? `TBD — needs PO confirmation` | PO + `production-domain` |
| **P-2** `site_id` on `WORK_ORDER` | `PLN-001` | `wo_number` is "unique within a site", but `WORK_ORDER` has no `site_id`. The site is reached through `routing_id → ROUTING → WORK_CENTER → AREA → SITE` | `production-domain` |
| PLN-Q10 | `US-PROD-PLN-001` | Keys (2026-10-01): `wo_number` is unique within a site, but `WORK_ORDER` stores no site column for the unique index | `production-domain` / PO |
| PLN-Q11 | `US-PROD-MON-005`, `US-PROD-MON-006` | Keys (2026-10-01): is (`wo_operation_id`, `defect_code_id`, `disposition`) unique on `WORK_ORDER_OPERATION_DEFECT`, given a sensor row and a manual override can both exist? | `production-domain` / PO |
| PLN-Q12 | — (lot capture) | Keys (2026-10-01): should (`wo_operation_id`, `direction`, `lot_id`) be unique on `WORK_ORDER_OPERATION_LOT`? | `production-domain` / PO |

## Stories

| ID | User story title | Feature | Status | Description | Dependencies |
|----|------------------|---------|--------|-------------|--------------|
| [`US-PROD-PLN-001`](#us-prod-pln-001) | Create a Job Order | Job Order | 🟢 Ready | As a **planner**, I want to create a Job Order for one SKU with its quantity, time window and work unit, so that the shop floor has a clear instruction instead of a whiteboard. → [detail](#us-prod-pln-001) | `US-FND-WMS-002`, `US-FND-WMS-003`, `US-FND-SHF-002` |
| [`US-PROD-PLN-002`](#us-prod-pln-002) | Upload a day's schedule from a file | Job Order | 🟡 Partly blocked | As a **planner**, I want to upload the whole day's schedule from one file, so that I don't have to type every Job Order by hand. → [detail](#us-prod-pln-002) | `US-PROD-PLN-001` |
| [`US-PROD-PLN-003`](#us-prod-pln-003) | Approve and release the schedule | Job Order | 🟡 Partly blocked | As a **production supervisor**, I want to approve the schedule and release only complete Job Orders, so that operators only see work that is really ready, running on a locked recipe. → [detail](#us-prod-pln-003) | `US-PROD-PLN-001` |
| [`US-PROD-PLN-004`](#us-prod-pln-004) | See today's schedule with live achievement | Job Order | 🟡 Partly blocked | As a **production supervisor**, I want to see today's schedule in priority order with each job's live achievement, so that I can act on a job falling behind before the shift ends. → [detail](#us-prod-pln-004) | `US-PROD-PLN-003` |
| [`US-PROD-PLN-005`](#us-prod-pln-005) | Dispatch the job queue to each work unit | Job Order | 🟡 Partly blocked | As a **line operator**, I want my work unit to show its released jobs in queue order, so that I always know which job runs next without asking. → [detail](#us-prod-pln-005) | `US-PROD-PLN-003` |
| [`US-PROD-PLN-006`](#us-prod-pln-006) | Change a released schedule with a reason | Job Order | 🟢 Ready | As a **planner**, I want to move, resize, hold or cancel a released Job Order while recording why, so that a schedule that keeps slipping can be learned from. → [detail](#us-prod-pln-006) | `US-PROD-PLN-003` |
| [`US-PROD-PLN-007`](#us-prod-pln-007) | Download the schedule and its status | Job Order | 🟢 Ready | As a **production supervisor**, I want to download a schedule with each job's status and achievement, so that I can report the day without retyping it. → [detail](#us-prod-pln-007) | `US-PROD-PLN-004` |
| [`US-PROD-PLN-008`](#us-prod-pln-008) | Declare break windows per shift | Breaktime | 🔴 Blocked | Waits on **P-06**: FOUNDATION and PRODUCTION disagree on what a break does to the clock, and no entity holds it. → [why](#us-prod-pln-008) | `US-FND-SHF-001` |
| [`US-PROD-PLN-009`](#us-prod-pln-009) | See breaks as a table and a timeline | Breaktime | 🔴 Blocked | Waits on `US-PROD-PLN-008`. → [why](#us-prod-pln-009) | `US-PROD-PLN-008` |
| [`US-PROD-PLN-010`](#us-prod-pln-010) | Check readiness before a job starts | Execution | 🔴 Blocked | Waits on **P-07**: none of the four checks has data to read yet. → [why](#us-prod-pln-010) | `US-PROD-PLN-005` |
| [`US-PROD-PLN-011`](#us-prod-pln-011) | See and download the planning log | Log (all features) | 🟢 Ready | As a **production supervisor**, I want to see who created, changed, deleted or downloaded a schedule, with before and after values, so that every schedule change can be traced. → [detail](#us-prod-pln-011) | `US-PROD-PLN-001`, `US-FND-AUD-001` |

**Totals: 4 🟢 ready · 4 🟡 partly blocked · 3 🔴 blocked = 11.**

## Detail blocks

---

#### US-PROD-PLN-001

**Create a Job Order**

**Status:** 🟢 Ready

> **In short:** the planner creates one Job Order: which SKU, how many, when, and on which work unit. It starts
> as a draft that nobody on the floor sees yet. MolcaDx is where Job Orders are made; there is no ERP feed today.

**1. Story**

As a **planner** (played by the production supervisor until P-09 is decided), I want to create a Job Order for
one SKU with its quantity, time window and work unit, so that the shop floor has a clear instruction instead of
a whiteboard.

**2. Context**

- **Why:** a Job Order turns a plan into an instruction. The spec calls it the *plan becomes a transaction*
  step ([01-planning § WORK_ORDER](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#work_order--plan-becomes-a-transaction)).
- **Where Job Orders come from (resolved W-1/B4, 2026-09-12):** the planner creates them in MolcaDx. An ERP
  connection is a later possibility, not built now. `source_system` stays empty
  ([01-planning § Source of WORK_ORDER](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#source-of-work_order-w-1b4--resolved-2026-09-12)).
- **Recipe comes from FOUNDATION:** the SKU's routing, operations, BOM and cycle time are read from F-08 Work
  Master. Production does not keep its own recipe
  ([05-work-master](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/05-work-master.md#one-sentence)).
- **Flows and versions (PO, 2026-10-01, PLN-Q6 / PLN-Q7):** a product can be made in more than one way. Each way
  is a **flow** with its own BOM and routings ([F-08 §9.0](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#90-product_flow--one-way-of-making-a-product-po-2026-09-30)).
  The Job Order stores the chosen flow (`product_flow_id`, default = the product's primary flow). `ROUTING` and
  `BOM` are versioned, one row per version, so `routing_id` / `bom_id` already point at one version
  ([F-08 versions and status](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#versions-and-status-po-2026-09-30)).
- **Who and where:** the planner, at a desktop, during a planning session.

**3. Expectation**

- A **Job Order form**: SKU (picker of active products), flow (pre-selected with the product's primary flow; only
  `released` flows), routing (only `released` routings of that flow; pre-selected with the primary one), BOM (the
  flow's `released` BOM), planned quantity with its unit, planned start and end, and priority.
- A product made only one way never sees the word "flow": the flow picker shows only when the product has more
  than one flow.
- When a routing is picked, its **operations** appear as rows, one per step, in step order. The planner
  confirms the work unit and shift for each row. For a step that any machine of a class can run, the planner
  picks one of the stations listed for that step. A row the planner leaves unassigned is allowed on a draft.
- A **workload** figure per operation row, in minutes, so the planner sees how long the job takes.
- The **Job Order list** shows number, SKU, quantity, planned window, status and progress, newest first.
- What the planner reads within 3 seconds: whether the new Job Order is saved, and its status (`draft`).

| State | What the user sees |
|-------|--------------------|
| Empty | "No Job Orders yet" with a **Create Job Order** button and an **Upload schedule** button (`PLN-002`) |
| Loading | A list placeholder. The **Save** button stays disabled until the SKU's routings have loaded |
| Error | The form keeps everything typed, plus "Job Order not saved — try again". Nothing half-saved |
| Offline | "Planning needs a connection". The form is read-only |
| Success | "Job Order `<wo_number>` saved as draft" and the row appears at the top of the list |
| No permission | "Your access doesn't cover site `<site>`. Ask a site administrator for access" |

**4. Calculation**

*Workload of one operation row*
- Minutes needed = the time to make the planned quantity at that work unit's standard cycle time, plus the
  standard setup time.
- The cycle time used is the version in effect on the shift's business date, not the newest one.
- For a step any machine of a class can run, the cycle time is the chosen station's own override when it has one,
  else the step's default.
- A continuous work unit uses its standard speed instead of a cycle time.
- Setup time is converted from its own time unit (e.g. "15 minutes") to minutes.

> [!note]- Exact formula (for developers)
> ```
> discrete / batch:  load_min = (planned_qty ÷ batch_size) × cycle_time_value_in_seconds ÷ 60 + setup_min
> continuous:        load_min = planned_qty ÷ standard_speed_value_per_minute + setup_min
> setup_min        = standard_setup_time × TIME_CONVERSIONS.seconds_per_unit (setup_time_conversion_id) ÷ 60
> cycle_time_value / standard_speed_value: OPERATION row with valid_from ≤ business_date < valid_to
> class-based step (required_work_unit_class_id set): OPERATION_WORK_UNIT.cycle_time_value of the chosen
>   work_unit_id if set, else OPERATION.cycle_time_value
> ```

*Example*
- `planned_qty` 1,000, `batch_size` 1, cycle time 20 s, setup 30 min → 1,000 × 20 ÷ 60 + 30 = **363.3 min**.
- Class-based step "Mix", class Mixer, default 600 s per batch, Mixer 2 override 540 s; 10 batches, no setup →
  on Mixer 1 10 × 600 ÷ 60 = **100 min**, on Mixer 2 10 × 540 ÷ 60 = **90 min**.

*Edge cases*
- No cycle time in effect on that date → the load reads "can't be calculated" and the Job Order can still be saved.
- `standard_setup_time` empty (optional since F-08 W-7 closed 2026-09-30: no setup standard) → treated as 0 and
  marked "setup not declared".
- The product has no `released` flow, routing or BOM → nothing to pick; the Job Order can't be saved.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `PRODUCT` · `PRODUCT_FLOW` (`product_id`, `flow_code`, `flow_name`, `is_primary`, `status`, `valid_from`, `valid_to`) · `ROUTING` (`flow_id`, `work_center_id`, `version`, `status`, `is_primary`) · `OPERATION` (`work_unit_id`, `required_work_unit_class_id`, `step_name`, `step_number`, `cycle_time_value`, `batch_size`, `standard_speed_value`, `standard_setup_time`, `setup_time_conversion_id`, `valid_from`, `valid_to`) · `OPERATION_WORK_UNIT` (`operation_id`, `work_unit_id`, `cycle_time_value`) · `BOM` (`flow_id`, `version`, `status`) · `TIME_CONVERSIONS` · `UNIT_OF_MEASUREMENTS` · `SHIFT_INSTANCE` · `WORK_UNIT` |
| **Writes** | `WORK_ORDER` (`wo_number`, `product_id`, `product_flow_id`, `routing_id`, `bom_id`, `planned_qty`, `uom_id`, `planned_start`, `planned_end`, `status = draft`, `priority`) · `WORK_ORDER_OPERATION` (`work_order_id`, `operation_id`, `work_unit_id`, `shift_instance_id`, `status = pending`) |
| **Left empty here** | `source_system` (no ERP path). `routing_version_locked` / `bom_version_locked` no longer exist (removed 2026-10-01, PLN-Q6) |

**6. Rules & constraints**

1. `planned_qty` must be greater than 0. Message: "Planned quantity must be greater than 0".
2. `planned_end` must be after `planned_start`. Message: "End must be after start".
3. `wo_number` is unique within the site. If the plant has no numbering standard, the pattern
   `WO-<year>-<nnnnn>` is used ([F-00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md)).
   How the site is found is open (P-2): through the routing's work center today.
4. Only a `released` flow of the chosen SKU, and only `released` routing and BOM versions of that flow, can be
   picked. `routing_id` and `bom_id` must belong to `product_flow_id`.
5. A `draft` Job Order can be edited freely and can be cancelled. It is never deleted; cancelling keeps the row.
6. **Who may do it:** creating and editing needs **site access** to the routing's site (or enterprise or tenant
   access). Viewing needs access to that site or anything inside it.
7. Every create and edit is written to the planning log (`PLN-011`).

**7. Acceptance criteria**

- **AC-1** **Given** SKU `A-100` has one active routing and BOM, **when** the planner creates a Job Order for
  1,000 pcs on 2026-10-01 07:00–14:59, **then** it is saved as `draft` with one operation row per routing step.
- **AC-2 (quantity)** **Given** the planned quantity is 0, **when** **Save** is pressed, **then** the form is
  not saved and "Planned quantity must be greater than 0" shows under the field.
- **AC-3 (window)** **Given** the end is before the start, **when** **Save** is pressed, **then** "End must be
  after start" shows and nothing is saved.
- **AC-4 (workload)** **Given** cycle time 20 s, batch 1 and setup 30 min, **when** 1,000 pcs are entered,
  **then** the operation row shows 363.3 min.
- **AC-5 (no cycle time)** **Given** the operation has no cycle time in effect on that date, **when** the row
  is shown, **then** it reads "can't be calculated" and the Job Order can still be saved.
- **AC-6 (duplicate number)** **Given** `WO-2026-00012` already exists at the site, **when** a second Job Order
  uses it, **then** it is refused with "This number is already used at this site".
- **AC-7 (permission)** **Given** a user without access to the site, **when** they open the form, **then** it is
  refused with the no-permission message.
- **AC-8 (flow)** **Given** SKU `A-100` has two released flows, "Standard" (primary) and "Wet granulation",
  **when** the planner opens the form, **then** "Standard" is pre-selected and only its released routings and BOM
  can be picked; **given** a SKU with one flow, the flow picker is not shown.
- **AC-9 (station override)** **Given** a class-based step with default 600 s per batch and Mixer 2 override
  540 s, **when** the planner places 10 batches on Mixer 2, **then** the row shows 90 min.

**8. Metrics & events**

- **Metric:** `jobs_created_in_molcadx`: share of the pilot line's jobs that exist as a Job Order in MolcaDx.
  Baseline 0 (nothing digital today), target `TBD — perlu konfirmasi PO` after the pilot starts.
- **Counter-metric:** `time_to_schedule_one_shift`: median minutes for a planner to plan one shift. Must not be
  slower than today's whiteboard or spreadsheet. Baseline `not yet measured`: observe n ≥ 5 shifts first.
- **Events:** `job_order_created` (`work_order_id`, `product_id`, `product_flow_id`, `planned_qty`, `operation_count`, `via` = `form` / `upload`) ·
  `job_order_edited` (`work_order_id`, `fields_changed`).

**Dependencies:** `US-FND-WMS-002`, `US-FND-WMS-003` (routing and BOM exist), `US-FND-SHF-002` (shifts exist)

---

#### US-PROD-PLN-002

**Upload a day's schedule from a file**

**Status:** 🟡 Partly blocked: the file template and mandatory fields (P-03) and the sequence field (P-01) are
open. The upload flow, validation and result screen can be built.

> **In short:** the planner downloads a template, fills in the day's schedule (one row per SKU per machine),
> and uploads it. Each valid row becomes a draft Job Order. Bad rows are listed with the reason; good rows are
> not held back by bad ones.

**1. Story**

As a **planner**, I want to upload the whole day's schedule from one file, so that I don't have to type every
Job Order by hand.

**2. Context**

- **Why:** plants already plan in a spreadsheet. Typing 20–40 jobs into a form one by one would make MolcaDx
  slower than what it replaces.
- **Figma "Upload form" fields:** schedule list, SKU name, target, machine name, when to be processed, SKU order
  to be processed, schedule approval, production notes, time information of the uploaded schedule
  ([01-planning § Job Order](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#job-order)).
- **One row = one Job Order** `[proposed]` (P-02), with *target* as its planned quantity.
- **Approval is not a column.** The uploaded rows arrive as drafts; approving them is `PLN-003` (P-04).
- **Who and where:** the planner, at a desktop, usually the day before.

**3. Expectation**

- An **Upload schedule** page with a **Download template** link and a file drop area.
- After upload, a **preview table**: one line per row, with a ✔ or the reason it failed, before anything is saved.
- The planner presses **Create Job Orders**; only the ✔ rows are created.
- A summary: "`<n>` Job Orders created, `<m>` rows skipped", with the skipped rows downloadable.

| State | What the user sees |
|-------|--------------------|
| Empty | The drop area and "Download the template first" |
| Loading | "Checking `<n>` rows…" with a progress bar |
| Error | A file that can't be read: "This file can't be read. Use the template (`.xlsx` or `.csv`)". Row errors show per line, never as one generic failure |
| Offline | "Uploading needs a connection" |
| Success | The summary above, and the new drafts at the top of the Job Order list |
| No permission | Same as `PLN-001` |

**4. Calculation**

*Matching each row*
- SKU name → the active product with that code or name at the site. More than one match is an error, never a guess.
- Machine name → the work unit with that code at the site.
- *When to be processed* → planned start and end, read in the site's timezone.
- The flow is the SKU's primary flow (the file has no flow column). The routing is that flow's `released`
  routing that runs on that work unit; the BOM is that flow's `released` BOM.
- If the flow has several released routings there, the primary one is used. If none of them is primary, the row
  fails and asks the planner to pick in the form.

*Example*
- 30 rows: 27 match, 2 name an unknown SKU, 1 has target 0 → 27 drafts created, 3 rows skipped with reasons.

*Edge cases*
- The same SKU twice on the same work unit and same time → both rows fail as a conflict.
- A row whose time has already passed → created, and flagged "late".
- An empty file, or only headers → "No rows found".

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | Same as `PLN-001`, plus `SITE.timezone` |
| **Writes** | Same as `PLN-001`, one `WORK_ORDER` per ✔ row. Production notes go to `TBD` — no notes field exists on `WORK_ORDER` (part of P-03) |
| **Not written** | Sequence ("SKU order") until P-01 names the field; the preview shows it, the save ignores it |

**6. Rules & constraints**

1. Each row passes the same checks as `PLN-001`.
2. Rows are independent: a bad row never stops the good ones.
3. Nothing is saved until the planner confirms the preview.
4. File types `[proposed]` `.xlsx` and `.csv`; size limit `[proposed]` 500 rows per file.
5. **Who may do it:** same as `PLN-001`.
6. The upload is written to the planning log with the file name and the number of rows created.

**7. Acceptance criteria**

- **AC-1** **Given** a template file with 30 valid rows, **when** the planner uploads and confirms, **then** 30
  draft Job Orders exist and the summary says 30 created, 0 skipped.
- **AC-2 (partial)** **Given** 2 of 30 rows name an unknown SKU, **when** the file is uploaded, **then** the
  preview marks those 2 rows "SKU `<name>` not found" and confirming creates the other 28.
- **AC-3 (preview first)** **Given** a file has been uploaded, **when** the planner leaves the page without
  confirming, **then** no Job Order is created.
- **AC-4 (unreadable)** **Given** a `.pdf` file, **when** it is uploaded, **then** "This file can't be read" shows
  and nothing is checked.
- **AC-5 (ambiguous routing)** **Given** the SKU's primary flow has two released routings on that work unit and
  neither is primary, **when** the row is checked, **then** it fails with "More than one routing — create this
  job in the form". **Given** one of them is primary, **then** the row passes with that routing.

**8. Metrics & events**

- **Metric:** `upload_row_success_rate`: ✔ rows ÷ all rows uploaded. Low means the template or master data
  doesn't fit the plant. Baseline `not yet measured`, target `TBD` after 30 days.
- **Events:** `schedule_uploaded` (`row_count`, `valid_count`, `file_type`) · `schedule_upload_confirmed`
  (`created_count`, `skipped_count`).

**Dependencies:** `US-PROD-PLN-001`

---

#### US-PROD-PLN-003

**Approve and release the schedule**

**Status:** 🟡 Partly blocked: approval, the completeness check, bulk release and the recipe lock can be built
(B3 resolved 2026-10-01: the lock is the `released` version `routing_id` / `bom_id` point at). The stage-order
drafts a finished-good release creates (PLN-Q3) wait on **P-11** for their planned start and end.

> **In short:** the supervisor looks at the draft schedule, sees which Job Orders are complete, and releases
> them. A released Job Order is visible to operators and runs on the recipe that was in effect at release, even
> if the master changes later.

**1. Story**

As a **production supervisor**, I want to approve the schedule and release only complete Job Orders, so that
operators only see work that is really ready, running on a locked recipe.

**2. Context**

- **Approval = release** `[proposed]` (P-04). The Figma's "Schedule approval" has no separate status.
- **Release rule:** a Job Order may be released only with a valid routing and BOM on its planned start, a
  quantity above 0, and at least one operation placed on a work unit
  ([01-planning § WORK_ORDER status](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#work_order-status)).
- **Recipe lock (B3, resolved 2026-10-01, PLN-Q6):** FOUNDATION keeps one `ROUTING` / `BOM` row per version, with
  `version` and `status` (`draft` / `released` / `retired`). `routing_id` and `bom_id` point at one version row,
  so **the version picked at release is the lock**; only `released` versions can be picked. A change to a
  released version opens a new version, so the Job Order never follows later master changes
  ([01-planning § Routing/BOM locked at release](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#routingbom-locked-at-release-b3),
  [F-08 versions and status](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#versions-and-status-po-2026-09-30)).
  The former `routing_version_locked` / `bom_version_locked` placeholders are removed. Until a Work Directive is
  modelled, the pinned version stands in for it (WM-P-1, closed). It freezes the routing/BOM **identity** only:
  which steps, work units and materials the Job Order runs under. No cycle time or speed is copied onto the Job
  Order
  ([05-work-master § Interim Directive-equivalent](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/05-work-master.md#interim-directive-equivalent-on-work_order)).
- **Stage orders (PLN-Q3, PO 2026-10-01):** when a line spans several work centers with one product per work
  center (Dough → Baked biscuit → Pack), releasing the finished-good order **creates draft orders for the earlier
  stages automatically**, from the flow's BOM and the planned shrinkage of each step. The planner can edit or
  delete each draft before releasing it
  ([01-planning § Stage orders](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#stage-orders-from-a-finished-good-order-pln-q3-po-2026-10-01),
  [F-08 line across work centers](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#a-line-across-several-work-centers--biscuit-example-po-2026-09-30)).
- **Lock ≠ OEE standard.** OEE Performance reads `OPERATION.cycle_time_value` / `standard_speed_value` in force
  on the shift's `business_date` (F-07, FOUNDATION issue #58 Q1), not as of release. A standard change is an
  effective-dated row inside the same routing version, so it reaches the Job Order without a new version. The two
  guarantees are kept apart.
- **Who and where:** the supervisor, at a desktop, at the end of planning.

**3. Expectation**

- A **Ready to release** list for a chosen date: each draft with checks for routing, BOM, quantity and work
  unit placement, each ✔ or ✖ with what is missing.
- A **Release** button per row and **Release all complete** for the date.
- Before releasing, a panel shows **the recipe that will be locked**: flow, routing and BOM, with their version.
- Releasing a finished-good order whose BOM has semi-finished lines made at other work centers also creates the
  **stage-order drafts**; the success message lists them, and they appear in the Job Order list as `draft`.
- What the supervisor reads within 3 seconds: how many are ready, how many are not, and why.

| State | What the user sees |
|-------|--------------------|
| Empty | "No drafts for `<date>`" with a link to the Job Order list |
| Loading | A list placeholder. **Release** stays disabled until the checks finish |
| Error | "Release failed — status not changed". In a bulk release, each Job Order reports its own result |
| Offline | "Releasing needs a connection" |
| Success | "`<n>` Job Orders released". They show as `released` and appear on the work units' queues (`PLN-005`) |
| No permission | The **Release** buttons are hidden and a line explains release needs site access |

**4. Calculation**

*Ready to release*

A Job Order is ready when all four are true: it has a routing, it has a BOM, its quantity is above 0, and at
least one operation row has a work unit.

> [!note]- Exact formula (for developers)
> ```
> ready = routing_id IS NOT NULL
>     AND bom_id IS NOT NULL
>     AND planned_qty > 0
>     AND COUNT(WORK_ORDER_OPERATION WHERE work_unit_id IS NOT NULL) ≥ 1
>     AND ROUTING(routing_id).status = 'released' AND BOM(bom_id).status = 'released'
>     AND both are active on DATE(planned_start)   (valid_from ≤ date < valid_to)
>     AND ROUTING.flow_id = BOM.flow_id = product_flow_id
> ```

*Stage-order quantities* — each earlier stage gets the quantity the next stage needs, from the BOM and the planned
shrinkage of the steps after it (F-08 planned input, net recipe plus losses).

> [!note]- Exact formula (for developers)
> ```
> planned qty of a component per 1 unit of the product
>   = (qty_per_unit ÷ base_qty) × (1 + scrap_factor) ÷ Π (1 − planned_shrinkage of the line's step and each later step)
> ```

*Example*
- 8 drafts, 2 without a work unit → **Release all complete** releases 6, and lists the 2 with "No work unit placed".
- Biscuit line (F-08 example data): releasing 5,000 packs at 40 pcs per pack → a Baked biscuit draft of 200,000
  pcs → a Dough draft of 200,000 × 0.96 g ÷ (1 − 0.04) = **200 kg** of wet dough.

*Edge cases*
- Planned start already passed → released and flagged "late"; holding it back doesn't make the work go away.
- No `released` routing or BOM version active on the planned start date → refused, naming which one is missing
  (nothing to lock).
- A stage product with no `released` flow, routing or BOM → its draft can't be made; `TBD — needs PO confirmation`:
  does the finished-good release still go through, with the missing stage listed?
- One failure in a bulk release never cancels the others.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER` (`product_flow_id`, `routing_id`, `bom_id`) · `WORK_ORDER_OPERATION` · `PRODUCT_FLOW` · `ROUTING` (`flow_id`, `version`, `status`, `valid_from`, `valid_to`) · `BOM` (`flow_id`, `version`, `status`, `base_qty`, `base_uom_id`, `valid_from`, `valid_to`) · `BOM_LINE` (`component_product_id`, `qty_per_unit`, `uom_id`, `scrap_factor`, `operation_id`) · `OPERATION` (`planned_shrinkage`) |
| **Writes** | `WORK_ORDER.status` `draft → released` · `WORK_ORDER_OPERATION.status` `pending → scheduled` for rows with a work unit · stage-order drafts: new `WORK_ORDER` rows (`status = draft`, `product_id` = the stage product, `planned_qty` from the formula above; `planned_start` / `planned_end` `TBD`, P-11). No lock field is written: the lock is the `routing_id` / `bom_id` already on the row |

**6. Rules & constraints**

1. Only `draft` Job Orders can be released.
2. The recipe is locked at release: release is refused unless `routing_id` and `bom_id` point at `released`
   versions of the Job Order's flow, active on the planned start.
3. After release, changing `routing_id` / `bom_id` is an explicit action that records who, when and why; a newer
   released version never replaces the pinned one by itself.
3a. The lock carries no cycle-time field. The Job Order's OEE Performance uses the cycle time effective on each
   shift's `business_date`, even when the pinned routing version is older.
3b. Releasing a finished-good order creates draft orders for its earlier stages (one product per work center).
   They are drafts: the planner can edit or delete each one, and each is released on its own.
4. Release is per Job Order: bulk release reports each one.
5. **Who may do it:** site access to the Job Order's site. The planner persona may prepare, but release is the
   supervisor's `[proposed]` (P-04, P-09).
6. Every release and failed release is written to the planning log.

**7. Acceptance criteria**

- **AC-1** **Given** a complete draft, **when** the supervisor presses **Release**, **then** its status is
  `released` and the message names the locked routing and BOM.
- **AC-2 (incomplete)** **Given** a draft with no work unit on any operation, **when** **Release** is pressed,
  **then** it is refused with "No work unit placed".
- **AC-3 (bulk)** **Given** 8 selected drafts of which 2 are incomplete, **when** **Release all complete** runs,
  **then** 6 are released and 2 are listed with their reasons.
- **AC-4 (lock holds)** **Given** a Job Order released on routing version 1, **when** version 2 is released,
  **then** the Job Order still reads version 1.
- **AC-4a (draft version)** **Given** the only routing version of the SKU's flow is a `draft`, **when** **Release**
  is pressed, **then** it is refused naming the routing as missing.
- **AC-4b (stage orders)** **Given** the biscuit example (5,000 packs, 40 pcs per pack, 0.96 g per biscuit, oven
  shrinkage 0.04), **when** the pack order is released, **then** a Baked biscuit draft of 200,000 pcs and a Dough
  draft of 200 kg appear as `draft`, and each can be edited or deleted.
- **AC-5 (late)** **Given** the planned start has passed, **when** it is released, **then** release succeeds and
  the row is flagged "late".
- **AC-6 (error)** **Given** the connection drops during release, **when** release fails, **then** the status is
  still `draft` and the message says nothing changed.

**8. Metrics & events**

- **Metric:** `released_first_try`: share of Job Orders that pass the check on their first release attempt.
  Low means master data isn't ready at planning time. Baseline `not yet measured`.
- **Counter-metric:** `lead_time_to_start`: median time from release to the first operation starting. Must not grow.
- **Events:** `job_order_released` (`work_order_id`, `was_late`, `bulk`, `stage_drafts_created`) · `job_order_release_blocked` (`work_order_id`, `missing`).

**Dependencies:** `US-PROD-PLN-001`

---

#### US-PROD-PLN-004

**See today's schedule with live achievement**

**Status:** 🟡 Partly blocked: the order within a work unit needs P-01, and "live" needs output to be captured.
Output comes from the sensor path (sensor is the source of truth) or from recording on the floor; that story
belongs to Production Monitoring and is not written yet. The screen can be built on whatever
`WORK_ORDER_OPERATION` output exists.

> **In short:** one screen for today. Each work unit shows its released jobs in order, each with how much of its
> target is done so far. A job falling behind stands out.

**1. Story**

As a **production supervisor**, I want to see today's schedule in priority order with each job's live
achievement, so that I can act on a job falling behind before the shift ends.

**2. Context**

- **Figma "Final Output":** (a) the daily production schedule in order (1st, 2nd, 3rd); (b) live achievement %;
  (c) jobs dispatched to each machine in queue order (that part is `PLN-005`).
- **Why:** today the supervisor learns a job is behind from the H+1 report, one shift too late.
- **Who and where:** the supervisor, often on a tablet or phone while walking the floor, on unstable Wi-Fi.

**3. Expectation**

- A **Today** view: rows = work units, and in each row the jobs in queue order, each as a bar with SKU, target,
  done so far, achievement % and status.
- A job whose achievement is behind the elapsed share of its planned window is marked **behind** (amber).
- Date and shift pickers; the default is the running shift.
- "Updated `<hh:mm>`" at the top.

| State | What the user sees |
|-------|--------------------|
| Empty | "Nothing released for this shift" — distinct from a work unit with jobs but no output yet |
| Loading | Row placeholders; old numbers are not kept on screen as if they were new |
| Error | The last loaded numbers stay, marked "not refreshed since `<hh:mm>`" |
| Offline | The last loaded view with an offline marker and its time |
| Success | Not applicable (read-only) |
| No permission | Only work units in the user's access are listed |

**4. Calculation**

*Achievement of one job*
- Achievement = everything produced on the job so far ÷ its target (P-05: total output, not good units only).
- Behind = achievement is lower than the share of the planned window that has already passed.

> [!note]- Exact formula (for developers)
> ```
> done            = Σ WORK_ORDER_OPERATION.total_output  (good_qty + scrap_qty + rework_qty) for the job
> achievement_pct = done ÷ WORK_ORDER.planned_qty × 100
> elapsed_pct     = (now − planned_start) ÷ (planned_end − planned_start) × 100, clipped to 0–100
> behind          = status = in_progress AND achievement_pct < elapsed_pct
> ```

*Example*
- Target 1,000, window 07:00–15:00, now 11:00 → elapsed 50%. Done 420 → achievement **42%** → behind.

*Edge cases*
- No output yet → "no output yet", never "0%".
- Achievement above 100% → shown as is (e.g. 108%), never cut to 100%.
- `planned_qty` 0 can't happen (refused in `PLN-001`).
- `hold` quantities are not counted in `scrap_qty` / `rework_qty` today, so they are not in `done`. A pending
  spec change may add them; this story follows the spec when it lands.
- Reject rows now carry their own unit (`WORK_ORDER_OPERATION_DEFECT.uom_id`, PLN-Q1, 2026-10-01): a reject
  weighed in kg can't be added to pieces as is. Whether `scrap_qty` / `rework_qty` are stored converted to the
  operation's unit is open (**M-04** in [Monitoring](02-monitoring.md#open-items-in-this-module)).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER` (`planned_qty`, `planned_start`, `planned_end`, `status`) · `WORK_ORDER_OPERATION` (`good_qty`, `scrap_qty`, `rework_qty`, `work_unit_id`, `shift_instance_id`, `status`) · `SHIFT_INSTANCE` · `WORK_UNIT` |
| **Writes** | None |

**6. Rules & constraints**

1. Only `released`, `in_progress` and `on_hold` Job Orders are shown; `draft` and `cancelled` are not.
2. Refresh `[proposed]` every 60 seconds while the screen is open.
3. **Who may see it:** access to the work unit or anything above it.

**7. Acceptance criteria**

- **AC-1** **Given** a job with target 1,000 and 420 done at 11:00 in a 07:00–15:00 window, **when** the
  supervisor opens Today, **then** it shows 42% and is marked behind.
- **AC-2 (no output)** **Given** a released job with no output, **when** Today opens, **then** it reads "no
  output yet", not 0%.
- **AC-3 (over)** **Given** 1,080 done on a target of 1,000, **when** Today opens, **then** it shows 108%.
- **AC-4 (offline)** **Given** the device goes offline, **when** the supervisor keeps the screen open, **then**
  the last view stays with an offline marker and its time.

**8. Metrics & events**

- **Metric:** `schedule_viewed_same_shift`: share of shifts where a supervisor opened Today during the shift.
  Baseline 0, target `TBD`.
- **Counter-metric:** load time p95 ≤ `[proposed]` 2.5 s on 3G.
- **Events:** `schedule_today_opened` (`site_id`, `shift_instance_id`, `job_count`, `behind_count`).

**Dependencies:** `US-PROD-PLN-003`

---

#### US-PROD-PLN-005

**Dispatch the job queue to each work unit**

**Status:** 🟡 Partly blocked: queue order needs P-01, and the floor device is still undecided (shared tablet,
phone or fixed terminal — SF-4). The queue itself can be built.

> **In short:** each work unit has its own queue screen. It shows the released jobs for that unit, the running
> one first and then the next ones in order, so the operator never has to ask what's next.

**1. Story**

As a **line operator**, I want my work unit to show its released jobs in queue order, so that I always know
which job runs next without asking.

**2. Context**

- **Figma "Final Output" (c):** jobs dispatched digitally to each machine (work unit), with the queue prioritised.
- **Who and where:** the operator, standing at the machine, gloved, variable light, unstable network.
- **Starting and recording on a job** are Production Monitoring stories, not written yet. This story only
  shows the queue. When written, starting a step must follow PLN-Q7 (PO, 2026-10-01): if the routing has
  `enforce_sequence = true`, a step can't start until the steps it waits for are closed — blocked, not a warning,
  message "Finish \<step\> first."
  ([01-planning § Steps must be done in order](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#steps-must-be-done-in-order-pln-q7-po-2026-10-01)).

**3. Expectation**

- A **Queue** screen for one work unit: the running job at the top (large), then the next jobs in order, each
  with SKU, target and planned start.
- Touch targets ≥ 44 × 44 px, high contrast.

| State | What the user sees |
|-------|--------------------|
| Empty | "No jobs released for this unit" and who to ask (the shift supervisor) |
| Loading | Placeholder rows |
| Error | The last loaded queue, marked "not refreshed" |
| Offline | The last loaded queue with an offline marker; it never goes blank |
| Success | Not applicable |
| No permission | "This unit isn't in your access. Ask your supervisor" |

**4. Calculation**

*Queue order:* running job first, then by queue position (field `TBD`, P-01), then by planned start.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER_OPERATION` (`work_unit_id`, `status` in `scheduled` / `in_progress` / `on_hold`) · `WORK_ORDER` (`planned_qty`, `planned_start`, `product_id`) · `PRODUCT` |
| **Writes** | None |

**6. Rules & constraints**

1. A Job Order shows on the queue only after release (`PLN-003`).
2. A change from `PLN-006` shows on the queue within `[proposed]` 60 seconds when online.
3. The queue is kept on the device so it still shows offline.
4. **Who may see it:** access to that work unit.

**7. Acceptance criteria**

- **AC-1** **Given** three jobs released on unit S03, **when** the operator opens S03's queue, **then** the
  running job is first and the others follow by planned start.
- **AC-2 (release)** **Given** a draft on S03, **when** it has not been released, **then** it is not on the queue.
- **AC-3 (offline)** **Given** the device goes offline, **when** the queue is opened, **then** the last loaded
  queue shows with an offline marker.

**8. Metrics & events**

- **Metric:** `lead_time_to_start` (shared with `PLN-003`).
- **Events:** `job_queue_opened` (`work_unit_id`, `queue_length`, `is_offline`).

**Dependencies:** `US-PROD-PLN-003`

---

#### US-PROD-PLN-006

**Change a released schedule with a reason**

**Status:** 🟢 Ready

> **In short:** plans change. The planner can move, resize, hold or cancel a released Job Order, but must say why.
> Output that is already recorded stays where it was.

**1. Story**

As a **planner**, I want to move, resize, hold or cancel a released Job Order while recording why, so that a
schedule that keeps slipping can be learned from.

**2. Context**

- **What the spec allows by status**
  ([01-planning § WORK_ORDER status](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#work_order-status)):
  - `draft`: edit freely.
  - `released` or `on_hold`: edit with a reason.
  - `in_progress`: only the remaining quantity, with supervisor approval.
  - `completed` or `cancelled`: no edits.
- **Who and where:** the planner, at a desktop, whenever the plan changes.

**3. Expectation**

- From the Job Order or the Today view: **Change**, **Hold**, **Resume** and **Cancel**.
- Every one of them asks for a **reason** (free text, required) before it can be confirmed.
- **Change** shows the new values next to the old ones before confirming.

| State | What the user sees |
|-------|--------------------|
| Empty | Not applicable |
| Loading | **Confirm** stays disabled until the Job Order is loaded |
| Error | "Change not saved — the schedule is unchanged" |
| Offline | "Changing the schedule needs a connection" |
| Success | "Job Order `<wo_number>` updated" and the change is in the log with its reason |
| No permission | The actions are hidden and a line explains why |

**4. Calculation**

*How much can still move on a running job*

Only what isn't produced yet: the target minus what is already done.

> [!note]- Exact formula (for developers)
> ```
> movable_qty = max(0, planned_qty − Σ WORK_ORDER_OPERATION.total_output)
> ```

*Example:* target 1,000, done 400 → at most 600 can move to another shift. The 400 stay on their original shift.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER` · `WORK_ORDER_OPERATION` · `SHIFT_INSTANCE` |
| **Writes** | `WORK_ORDER` (`planned_qty`, `planned_start`, `planned_end`, `status` → `on_hold` / `released` / `cancelled`) · `WORK_ORDER_OPERATION` (`shift_instance_id`, `work_unit_id` of rows not yet started) · the reason and before/after values to the planning log (`PLN-011`) |

**6. Rules & constraints**

1. A reason is required. Message: "A reason is required".
2. `completed` and `cancelled` can't be changed. Message: "This Job Order is final".
3. On `in_progress`, only `movable_qty` can move, and a supervisor must approve first.
4. Output already recorded never moves to another shift.
5. A job can move only to a work unit that has the same operation in its pinned routing version (`routing_id`):
   the step's own work unit, or, for a class-based step, one of the stations listed in `OPERATION_WORK_UNIT`.
6. Moving to a `closed` shift is refused.
7. **Who may do it:** site access; the approval on `in_progress` is a supervisor with site access.

**7. Acceptance criteria**

- **AC-1** **Given** a released Job Order, **when** the planner moves it to tomorrow's Shift 1 with reason
  "material late", **then** it moves and the log shows old and new window and the reason.
- **AC-2 (reason)** **Given** the reason is empty, **when** **Confirm** is pressed, **then** "A reason is
  required" shows and nothing changes.
- **AC-3 (output stays)** **Given** 400 of 1,000 are done on Shift 1, **when** the remainder moves to Shift 2,
  **then** Shift 1 still shows 400 and only 600 move.
- **AC-4 (final)** **Given** a `completed` Job Order, **when** anyone tries to change it, **then** "This Job
  Order is final" shows.
- **AC-5 (wrong unit)** **Given** unit S05 doesn't have that operation, **when** the job is moved to S05,
  **then** it is refused naming the missing operation.

**8. Metrics & events**

- **Metric:** `reschedules_per_job_order`: high means planning upstream is the problem, not the floor. Baseline
  `not yet measured`, target after 30 days.
- **Events:** `job_order_changed` (`work_order_id`, `action` = `move` / `resize` / `hold` / `resume` / `cancel`, `was_in_progress`, `moved_qty`).

**Dependencies:** `US-PROD-PLN-003`

---

#### US-PROD-PLN-007

**Download the schedule and its status**

**Status:** 🟢 Ready

> **In short:** the supervisor downloads a date's schedule as a file: every job with its details, whether it ran,
> and how much of its target was reached.

**1. Story**

As a **production supervisor**, I want to download a schedule with each job's status and achievement, so that I
can report the day without retyping it.

**2. Context**

- **Figma "Download form":** schedule details; schedule status (executed or not, target achievement).
- **Who and where:** the supervisor, at a desktop, at shift end or the next morning.

**3. Expectation**

- A **Download** button on the Job Order list and on Today, for a chosen date range.
- The file has one row per Job Order: number, SKU, work unit, planned window, target, done, achievement %,
  status, executed (yes/no).

| State | What the user sees |
|-------|--------------------|
| Empty | "No Job Orders in this range" and no file |
| Loading | "Preparing file…" |
| Error | "Download failed — try again" |
| Offline | "Downloading needs a connection" |
| Success | The file downloads; the download is logged |
| No permission | Only Job Orders in the user's access are in the file |

**4. Calculation**

- **Executed** = at least one operation of the Job Order has started.
- **Achievement %** = same formula as `PLN-004`.

> [!note]- Exact formula (for developers)
> ```
> executed = EXISTS WORK_ORDER_OPERATION WHERE actual_start IS NOT NULL
> ```

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | Same as `PLN-004`, plus `WORK_ORDER_OPERATION.actual_start` |
| **Writes** | One `AUDIT_LOG` row, `action = schedule_downloaded` (`domain_code = PROD`, `module_code = PLN`, `entity_type = WORK_ORDER`), with `download_filter` (date range, work units) and `download_row_count`; no `changes` ([F-14 core concept 9](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md#core-concepts)). Shows in the planning log (`PLN-011`). No reason is asked; the row's scope is the filter's scope, `tenant` if unfiltered (PO, 2026-09-25). |

**6. Rules & constraints**

1. File type `[proposed]` `.xlsx`; range at most `[proposed]` 31 days.
2. **Who may do it:** access to the site; the file only holds what the user can see.

**7. Acceptance criteria**

- **AC-1** **Given** 12 Job Orders on 2026-10-01, **when** the supervisor downloads that day, **then** the file
  has 12 rows with status, executed and achievement.
- **AC-2 (not run)** **Given** a released job that never started, **when** it is in the file, **then** executed
  reads "no".
- **AC-3 (log)** **Given** a download, **when** it finishes, **then** the planning log shows who downloaded
  which range and when.

**8. Metrics & events**

- **Metric:** `schedule_downloads_per_week`, to see whether the file replaces manual reports. Baseline 0.
- **Events:** `schedule_downloaded` (`date_from`, `date_to`, `row_count`).

**Dependencies:** `US-PROD-PLN-004`

---

#### US-PROD-PLN-008

**Declare break windows per shift**

**Status:** 🔴 Blocked — waits on **P-06**.

> **In short:** the Figma asks for a form to enter each shift's breaks, recurring or one-time. It can't be
> built until the two domains agree on what a break does to the clock, and where it is stored.

**1. Story**

As a **planner**, I want to enter the breaks of each shift, recurring or one-time, so that planned capacity and
OEE don't count break time as lost production.

**2. Context — why it is blocked**

- **FOUNDATION F-02 H-2 (resolved 2026-08-19):** break time is **not** on the shift. Real planned stops,
  breaks included, are rows in `ASSET_STATE_LOG` with a *planned* downtime reason, which reduces **PBT**. "A
  recurring break table, if any, is `production-domain` later and is not a PBT input"
  ([F-02](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md)).
- **PRODUCTION 01-planning (2026-09-24):** a break is excluded from **POT** and is **never** planned downtime
  ([01-planning § Breaktime](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#breaktime)).
- The two give the **same Availability** but a **different POT**, and a different place to store the break. Take an 8-hour shift (POT 480) with a 30-minute
  break and 42 minutes of unplanned stops. Under F-02 the break is planned downtime: PBT = 450, APT = 408,
  A = 408 ÷ 450 = **90.7%**. Under 01-planning the break is out of POT: POT = 450, PBT = 450, APT = 408, A is the
  same **90.7%**, but POT differs by 30 minutes (it matters for any KPI built on POT, and for whether the break
  is a stop row in `ASSET_STATE_LOG` or a calendar row).
- **PRODUCTION's own `ASSET_STATE_LOG` spec (2026-09-28)** — PRODUCTION owns the log now ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)) — takes
  the 01-planning side as its rule 6 ("break is not downtime"; planned downtime on the log shrinks PBT only) and
  records the conflict with F-02 H-2 as **SL-3**, needing a FOUNDATION wording fix via the PO
  ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)). P-06 stays open until that fix lands.
- No entity holds break windows (recurring or one-time).

**3. Expectation** — not applicable until P-06 closes. Figma intent: a form per shift with break start, end,
and recurring or one-time; output as a table and a timeline (`PLN-009`).

**4. Calculation** — not applicable until P-06 closes. The canonical Availability chain stays FOUNDATION F-07.

**5. Data & entities** — no entity. Needs a `production-domain` model (and a matching F-02 / F-07 update).

**6. Rules & constraints** — not applicable. Known rule from both sides: a break is never an *unplanned* stop.

**7. Acceptance criteria** — not applicable.

**8. Metrics & events** — not applicable.

**Unblocked when:** the PO decides P-06, F-02 H-2 or 01-planning is corrected to match, and `production-domain`
models the break window entity.

**Dependencies:** `US-FND-SHF-001`

---

#### US-PROD-PLN-009

**See breaks as a table and a timeline**

**Status:** 🔴 Blocked — waits on `US-PROD-PLN-008`.

> **In short:** Figma "Output": the declared breaks shown as a table and on a timeline next to the shifts.
> Nothing to show until breaks can be declared.

**1. Story** — As a **production supervisor**, I want to see each shift's breaks as a table and on a timeline,
so that I can check the break plan at a glance.

**2–8.** not applicable until `US-PROD-PLN-008` is unblocked. The views read the same entity `PLN-008` writes.

**Dependencies:** `US-PROD-PLN-008`

---

#### US-PROD-PLN-010

**Check readiness before a job starts**

**Status:** 🔴 Blocked — waits on **P-07**.

> **In short:** before a job starts on a work unit, check four things: material, equipment, people and the work
> instruction. None of them has data MolcaDx can read yet.

**1. Story**

As a **line operator**, I want the work unit to check that material, equipment, people and instructions are
ready before a job starts, so that a job doesn't start and then stop five minutes later.

**2. Context — why it is blocked**

- **Figma "Execution":** interlock by verifying production conditions, as a checklist of material availability,
  equipment readiness, personnel readiness and digital work instruction
  ([01-planning § Execution interlock](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#execution-interlock--checklist)).
- **Material:** stock belongs to the future Inventory domain; only the BOM (what is needed) exists.
- **Equipment:** machine state (`ASSET_STATE_LOG`) is owned by PRODUCTION since 2026-09-28 ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)). The recording level is settled (SL-1, 2026-09-29: per asset, `asset_id` and `work_unit_id` both required), but the state field's name is still `TBD` ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)) (B5).
- **Personnel:** no crew entity since Organization moved to the identity provider (`US-FND-SHF-004`, crew assignment, was retired on 2026-09-25).
- **Work instruction:** no entity.
- Hard block vs warning per check is `TBD` (PO + ops).

**3–8.** not applicable until P-07 is decided. A first slice could be a **manual checklist** the operator ticks,
stored with the job. That would need a `production-domain` entity and a PO decision that manual ticks are enough.

**Unblocked when:** the PO decides P-07, per check, and each check has a data source.

**Dependencies:** `US-PROD-PLN-005`

---

#### US-PROD-PLN-011

**See and download the planning log**

**Status:** 🟢 Ready. The log is FOUNDATION's shared `AUDIT_LOG` (P-08 resolved 2026-09-25); its rules —
append-only, reason required, kept 3 years — come from [F-14](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md).

> **In short:** every create, edit, deactivate (Figma "delete"), release, hold, resume, cancel, upload and schedule
> download leaves a row: who, when, what changed from what to what. The supervisor can filter it and download it.

**1. Story**

As a **production supervisor**, I want to see who created, changed, deleted or downloaded a schedule, with before
and after values, so that every schedule change can be traced.

**2. Context**

- **Figma "Log" under Planning:** who made the schedule, who edited it (before and after), who deleted it, and a
  downloadable log ([01-planning § Log](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#log-planning)).
- The same pattern is used by FOUNDATION library config and by the other two Production modules.
- "Delete" in the Figma maps to **`deactivate`** (no hard delete). Cancelling a Job Order is its own business
  action, `cancel`. Log rows are never removed.

**3. Expectation**

- A **Planning log** page: newest first, filters for date range, user, action and Job Order number.
- Each row: time, user, action, Job Order, and for edits the field with its before and after values, plus the
  reason when there is one.
- **Download** exports the filtered rows.

| State | What the user sees |
|-------|--------------------|
| Empty | "No changes in this range" |
| Loading | Row placeholders |
| Error | "Log couldn't load — try again" |
| Offline | "The log needs a connection" |
| Success | Not applicable (read-only) |
| No permission | Only rows the user holds `production:pln:<scope>:audit_view` for, judged on the row's scope snapshot (`tenant` until `WORK_ORDER`'s site scope is set, P-2). Holding view on the Job Order is not enough |

**4. Calculation** — not applicable (no numbers).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `AUDIT_LOG` ([F-14 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md#91-audit_log--one-append-only-row-per-change)) where `domain_code = PROD` and `module_code = PLN`: `occurred_at`, `action` (`create` / `update` / `deactivate` / `release` / `hold` / `resume` / `cancel` / `upload` / `schedule_downloaded`), `entity_type`, `entity_id` (the Job Order or operation), `changes` (field, before, after), `reason`, `actor_display_name`, `source` |
| **Writes** | Downloading this log adds an `AUDIT_LOG` row `action = download`, owned by FOUNDATION (`domain_code = FND`, `module_code = AUD`), so it shows on the central Audit log, not here. Every action in `PLN-001`–`PLN-007` writes its row through [`US-FND-AUD-001`](../PRD-003-foundation/14-audit-trail.md#us-fnd-aud-001) |

**6. Rules & constraints**

1. Rows are never edited or removed; kept 3 years (F-14 AU-1).
2. Every action in `PLN-001` to `PLN-007` writes a row in the same transaction as the change; a person's change
   without a reason is refused (F-14 AU-2).
3. Download format follows F-14 AU-9 (`[proposed]` CSV, still open). Downloading a *schedule* (`PLN-007`) is the
   business action `schedule_downloaded`: no `changes`, no reason asked (PO, 2026-09-25); the row's scope is the
   filter's scope, `tenant` if unfiltered.
4. **Who may see it:** `production:pln:<scope>:audit_view`; download `production:pln:<scope>:audit_download`.
   Separate from viewing the Job Order itself.

**7. Acceptance criteria**

- **AC-1** **Given** a planner changed a Job Order's quantity from 1,000 to 800 with a reason, **when** the log
  is opened, **then** one row shows the user, time, `planned_qty` 1,000 → 800 and the reason.
- **AC-2 (cancel)** **Given** a Job Order was cancelled, **when** the log is opened, **then** a `cancel` row
  shows and the Job Order still exists with status `cancelled`.
- **AC-3 (download)** **Given** the log is filtered to one day, **when** it is downloaded, **then** the file holds
  only that day's rows and a `download` row is added to the central Audit log (`FND` · `AUD`).

**8. Metrics & events**

- **Metric:** changes with a missing log row: must be 0 (checked by comparing `job_order_*` events with log rows).
- **Events:** `planning_log_opened` (`filters`) · `planning_log_downloaded` (`row_count`).

**Dependencies:** `US-PROD-PLN-001`

---

## Change notes

| Date | Change |
|------|--------|
| 2026-10-01 | prd-sync `docs-molcadx` 147e8d2..787e819 ([open items wrap-up](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-10-01-open-items-wrap-up.md), F-08 Work Master waves 1–2): **B3 closed** (PLN-Q6) — `routing_id` / `bom_id` pin a `released` version, lock fields removed. `PLN-001` — flow picker (`product_flow_id`, PLN-Q7), released versions only, class-based step station override, setup time unit (W-7 closed); AC-8, AC-9. `PLN-002` — primary flow and primary routing used on upload; AC-5 reworded. `PLN-003` — lock rewritten, stage-order drafts on release (PLN-Q3), AC-4a, AC-4b; status reason now **P-11** (new). `PLN-004` — reject unit note (M-04). `PLN-005` — `enforce_sequence` note for the unwritten start story. `PLN-006` rule 5, `PLN-010` SL-1 resolved. P-03 updated (W-7 closed) |
| 2026-09-27 | prd-sync `docs-molcadx` 3cb17fa..7ad3ed8 (spec 05-work-master, issue #42): `PLN-003` — the release lock freezes routing/BOM identity only; no cycle time on the Job Order; OEE reads the `business_date` standard (new rule 3a). P-03 carries the parked WM-P-2 question |
| 2026-09-25 | prd-sync to `docs-molcadx@3cb17fa`: `PLN-007` states the PO-confirmed `schedule_downloaded` rules (no reason, filter scope) |
| 2026-09-25 | prd-sync to `docs-molcadx@327bb64`: P-10 closed (PO) — a schedule download is `schedule_downloaded`. `PLN-007` writes that row; `PLN-011` lists it, and its own log download is an `FND` · `AUD` row |
| 2026-09-25 | prd-sync to `docs-molcadx@171a333`: `PLN-011` — Figma "delete" = `deactivate`, `cancel` is its own business action (spec 01-planning § Log); no-permission state uses `production:pln:<scope>:audit_view` on the row's scope snapshot. New open item P-10 (`download` inconsistency with F-14) |
| 2026-09-24 | Module created, from the PO's Figma (Production Planning = Job Order · Breaktime · Execution; Product Tracing deferred) and PRODUCTION 01-planning / 05-work-master. 11 stories. Content carried over from frozen PRD-001 `US-PROD-JOB-001/002/004/005` where the spec still supports it; their IDs stay retired |
