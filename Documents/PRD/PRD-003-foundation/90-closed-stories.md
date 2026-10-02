# PRD-003 · Closed, retired and moved stories

> Part of [PRD-003 FOUNDATION](README.md). **Nothing in this file is to be built.** IDs stay here so they are never reused (AGENTS.md §4, dependency rule 3).

## Summary

| ID | User story title | Description | Dependencies |
|----|------------------|-------------|--------------|
| [`US-FND-PRO-004`](#us-fnd-pro-004) | ~~Manage per-unit reference cycle times~~ **RETIRED 2026-09-28** | **Retired by the PO on 2026-09-28:** `PRODUCT_CYCLE` retired ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) C). Nothing read it, and it could contradict the routing. Cycle time per item now lives only on `OPERATION` (`uom_id` = item unit), built by [`US-FND-WMS-003`](08-work-master.md#us-fnd-wms-003). Its screen `SCR-FND-PRO-002` (Cycle time tab) is retired too. ID kept, not reused. → [why](#us-fnd-pro-004) | `US-FND-PRO-001`, `US-FND-REF-004`, `US-FND-REF-005` |
| [`US-FND-SIT-003`](#us-fnd-sit-003) | ~~Pick the location context once for the whole app~~ **RETIRED 2026-09-27** | **Retired by the PO on 2026-09-27: not a FOUNDATION story** ([docs-molcadx#46](https://github.com/molca-id/docs-molcadx/issues/46)). No FOUNDATION app shows a context picker; a picker in the Production apps is parked. Its screen `SCR-FND-SIT-003` is retired too. ID kept, not reused. → [why](#us-fnd-sit-003) | `US-FND-SIT-001` |
| `US-FND-WMS-005` | ~~Manage the defect code taxonomy~~ **CLOSED 2026-09-23** | Defect codes merged into reject reasons ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-23-defect-code-merged-into-reject-reason.md)). Replaced by [`US-FND-REF-002`](05-reference-master-data.md#us-fnd-ref-002). | `—` |
| [`US-FND-SHF-004`](#us-fnd-shf-004) | ~~Assign crews to shift instances~~ **RETIRED 2026-09-25** | **Retired by the PO on 2026-09-25: not needed for now.** Its screen `SCR-FND-SHF-004` is retired too. `CREW` has no FOUNDATION entity since Organization was removed ([01-glossary.md](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/01-glossary.md)). ID kept, not reused. → [why](#us-fnd-shf-004) | `US-FND-SHF-002` |
| [`US-FND-SHF-003`](#us-fnd-shf-003) | ~~Manage the working calendar & exception days~~ **CLOSED** | **Closed: the concept was retired.** `SHIFT_CALENDAR` and `NON_WORKING_DAY` no longer exist; which dates production runs is owned by `production-domain`. → [why](99-history.md#132-stories-closed-and-retired) | `—` |
| [`US-FND-AST-004`](#us-fnd-ast-004) | ~~Manage the reason code taxonomy~~ **CLOSED** | **This story is closed and must not be built.** Replaced by [`US-FND-REF-001`](05-reference-master-data.md#us-fnd-ref-001), [`US-FND-REF-002`](05-reference-master-data.md#us-fnd-ref-002), [`US-FND-REF-003`](05-reference-master-data.md#us-fnd-ref-003). → [why](99-history.md#132-stories-closed-and-retired) | `—` |
| ~~`US-FND-AST-005`~~ | ~~Record component swaps as installation history~~ **MOVED, 2026-09-10** | ID retired, never reused (`AGENTS.md` §4 dependency rule 3). Moved to the new Maintenance domain — component-swap history is a MOM execution/event record, not FOUNDATION master data, per [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-10-mom-domain-alignment.md). Full story content preserved in [`MAINTENANCE/DOCS-EN/01-asset-history.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/MAINTENANCE/DOCS-EN/01-asset-history.md) and [`MAINTENANCE/UX/01-asset-history.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/MAINTENANCE/UX/01-asset-history.md); no Maintenance PRD exists yet (rule 1 — not created as a side effect of this move). → [why](99-history.md#132-stories-closed-and-retired) | `—` |
| [`US-FND-WMS-001`](#us-fnd-wms-001) | ~~Manage items & units of measure~~ **CLOSED** | **This story is closed and must not be built.** Replaced by [`US-FND-PRO-001`](06-product.md#us-fnd-pro-001), [`US-FND-REF-004`](05-reference-master-data.md#us-fnd-ref-004), [`US-FND-PRO-003`](06-product.md#us-fnd-pro-003). → [why](99-history.md#132-stories-closed-and-retired) | `—` |
| ~~`US-FND-WMS-004`~~ | ~~Create & release a work order~~ **RENAMED, 2026-09-03** | ID retired, never reused (`AGENTS.md` §4 dependency rule 3). Renamed to [`US-PROD-JOB-005`](../PRD-001-molcadx-core-q3-en.md#us-prod-job-005--create--release-a-work-order-with-locked-versions) because `WORK_ORDER`/`WORK_ORDER_OPERATION` are PRODUCTION entities — out of this PRD's FOUNDATION scope per the sync rule at the top of this file (§"Anything touching PRODUCTION → PRD-001"). Full story now lives only in PRD-001 §6.14 Job Schedule. → [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-03-foundation-contest-blockers.md) | `—` |
| [`US-FND-INT-001`](#us-fnd-int-001) | ~~Declare connectors & tag mappings~~ **CLOSED** | **This story is closed and must not be built.** Replaced by [`US-FND-DSR-001`](13-data-source.md#us-fnd-dsr-001) (all of its actual `DATA_SOURCE`/`ASSET_TAGS` content). No ERP-only replacement exists yet — [F-11](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md) has no concrete ERP fields/screens to build a story from. → [why](99-history.md#132-stories-closed-and-retired) | `—` |

## Detail blocks

#### US-FND-PRO-004

> **Retired 2026-09-28 (PO). Do not build.** `PRODUCT_CYCLE` is retired; cycle time lives only on Work Master's `OPERATION`. Kept below for history only.

**Manage per-unit reference cycle times — CLOSED (retired)**

**Status:** ⚫ Closed, retired 2026-09-28 (PO, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) C).
`PRODUCT_CYCLE` is retired ([F-06 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#93-product_cycle--retired-2026-09-28)): nothing read it (not the KPI calculation, not PRODUCTION),
and it could contradict `OPERATION`, where cycle time depends on the machine (the product could say 18 s / pack
while its routing said 20 s on one work unit and 18 s on another). Its per-item purpose is now covered by
`OPERATION.uom_id`, the item unit the standard is per ([`US-FND-WMS-003`](08-work-master.md#us-fnd-wms-003),
[F-08 §9.2.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#921-item-unit-and-conversion-po-2026-09-28)).
Its screen, the **Cycle time** tab `SCR-FND-PRO-002`, is retired in
[UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md#screens); the ID is not reused.
The events `product_cycle_created` and `product_cycle_inconsistent` are dropped with it. Existing
`PRODUCT_CYCLE` rows are closed with `valid_to`, not deleted; `product_cycle_id` values are never reused.
Do not build it. The story ID is kept and not reused. The text below is kept as it stood before retirement,
for history only.

> **In short:** the implementor records a product's cycle time per item unit (per pack, per carton), for
> planning and human reading. These figures never feed Performance; that standard lives on `OPERATION`.

> **Warning:** `PRODUCT_CYCLE.cycle_time_value` is **not** the source of the Performance calculation — that source is `OPERATION.cycle_time_value` in Work Master, a different entity that happens to share the field name ([F-06 §7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#common-pitfalls), pitfall number 2).
> If this screen ever becomes a Performance source, OEE will be wrong with no symptom anywhere.

**1. Story**

As a **plant admin**, I want to state one product's cycle time in several units at once, so that reference
figures exist for planning without changing where Performance gets its standard.

**2. Context**

- **What it is:** a product-level restatement per unit, with no work unit involved at all.
- **Several rows at once:** a product often has more than one way of being counted, for example a cycle per
  pack and a cycle per carton, and both hold at the same time. Multiple active rows are valid, one per item
  unit, with exactly one row flagged `is_main_uom = true` (F-06 P-2).
- **Item and time are two separate FKs**
  ([F-06 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#93-product_cycle--retired-2026-09-28)):
  - `uom_id` names only the **item** (pack, carton: an ordinary `count`-group `UNIT_OF_MEASUREMENTS` row).
  - `time_conversion_id` names the **time unit** `cycle_time_value` is written in, resolved through
    `TIME_CONVERSIONS.seconds_per_unit` ([`US-FND-REF-005`](05-reference-master-data.md#us-fnd-ref-005)).
  - There is no compound "seconds/pack" unit. Don't add one.
- **Throughput entry mode:** exists so an admin who knows "500 per hour" doesn't divide by hand. Its use is
  limited, and the screen must say so honestly: planning and human reading, not KPI calculation.
- **Who and where:** the implementor, at a desk, a few rows per product.
- **Screen:** the **Cycle time** tab of the product detail page (`SCR-FND-PRO-002`, [UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md)).

**3. Expectation**

*Table*
- A table inside the product detail (`SCR-FND-PRO-002`, [UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md)).
  Columns: item unit, cycle time as entered with its time unit ("0.5 minutes"), the resolved seconds per item
  ("30 s"), and the main-unit flag.
- Above the table, one permanent sentence: the figures in this table are not used to compute Performance, with
  a link to where that figure does come from, the operation standard in Work Master. This sentence is not a
  tooltip and must not be hidden behind an icon.
- **Readable within 3 seconds:** the resolved seconds on the main unit.

*Add / Edit dialog*
- An **item** picker (count-type units only).
- An **entry-mode toggle** with two modes:
  - *Cycle time:* a number plus a **time unit** picker.
  - *Throughput:* a rate plus a time unit ("500 per hour"). The dialog shows the derived cycle time
    ("= 7.2 s per pack") before submit, so the conversion is not a black box, and stores that derived value
    against the "seconds" time unit.
- In both modes the item picker and the time picker are two separate fields, never one combined "seconds/pack"
  input.

| State | What the user sees |
|-------|--------------------|
| Throughput mode unavailable | If there is no "seconds" time unit yet (a time unit of exactly 1 second per unit), the toggle explains that a "seconds" time conversion must be declared first, with a link to [`US-FND-REF-005`](05-reference-master-data.md#us-fnd-ref-005) |
| Empty | "No reference cycle time for this product yet", plus a statement that this does not block KPI calculation |
| Loading | A table skeleton |
| Save error | The dialog keeps its values, with a save-failed message |
| Offline | Not available |
| Success | The new row appears with both the entered form and the resolved seconds. If it is the first row, it is automatically flagged as the main unit |
| No permission | As in [`US-FND-PRO-001`](06-product.md#us-fnd-pro-001) |

Numeric limits: the cycle time greater than 0; in throughput mode, the rate greater than 0.

**4. Calculation**

*Resolved duration per item*
([F-06 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#93-product_cycle--retired-2026-09-28))
- The entered cycle time, converted into seconds per item with the chosen time unit.
- Example: 0.5 with time unit "Minutes" (60 seconds per unit) → **30 s per pack**. 18 with "Seconds" (1) →
  **18 s**.

> [!note]- Exact formula (for developers)
> ```
> seconds_per_item = cycle_time_value × TIME_CONVERSIONS.seconds_per_unit
> ```

*Throughput entry*
- "How many per time unit" is turned into "how many seconds per one": the length of the time unit in seconds,
  divided by the rate. Only the derived number is stored, against the "seconds" time unit; the rate itself is
  never persisted.
- Example: "500 per hour" → 3,600 ÷ 500 = **7.2 s per pack**. "120 per minute" → 60 ÷ 120 = **0.5 s**.

> [!note]- Exact formula (for developers)
> ```
> cycle_time_value = TIME_CONVERSIONS.seconds_per_unit(rate's time unit) ÷ rate
> stored with time_conversion_id = the row where seconds_per_unit = 1
> ```

- Seconds is used because a derived quotient rarely lands on a round number in a larger unit.

*Consistency check between rows* (in resolved seconds, using the conversions from [`US-FND-PRO-003`](06-product.md#us-fnd-pro-003))
- The bigger item unit should take proportionally longer: its expected seconds are the smaller unit's seconds
  multiplied by how many small units fit in one big one. A gap is shown, never silently accepted or blocked.
- Example: `FG-1001` at 20 s per pack, 1 carton = 12 packs → expected 20 × 12 = 240 s per carton. If the
  implementor enters 200 s per carton, the gap is (240 − 200) / 240 = **16.7%** and the row is marked
  inconsistent. It may still be saved, because cartons might genuinely be packed faster per unit, but the gap
  must be visible, not silent.

> [!note]- Exact formula (for developers)
> ```
> expected_seconds(larger_unit) = seconds_per_item(smaller_unit) × conversion_value(larger_unit → smaller_unit)
> ```

*What is not computed here: Performance*
- Performance is the ideal time for the output divided by the actual production time, and its standard comes
  from the operation step in Work Master for that product and work unit pair, not from this table
  ([US-FND-KPI-002](07-kpi.md#us-fnd-kpi-002)).

> [!note]- Exact formula (for developers)
> ```
> Performance = Σᵢ (standard_i × output_i) / APT        standard_i from OPERATION.cycle_time_value or OPERATION.standard_speed_value
> ```

*Edge cases*
- If the conversion between the two item units does not exist, the consistency check cannot run: show "cannot
  be checked", not "consistent".
- A product's first row automatically becomes the main unit.
- Flagging another row as main clears the flag from the previous one in the same transaction, so there is
  never more than one main row and never zero.
- A rate of 0 or below is rejected before any division.
- A quotient that does not terminate (`3600 ÷ 7 = 514.2857…`): the stored precision of `cycle_time_value` is not
  stated in F-06. `TBD — perlu konfirmasi PO` (question for `foundation-domain`: how many decimal places does
  `cycle_time_value` keep, and does the on-screen preview round to match?).
- A time unit picked in cycle-time mode is stored as picked. Only throughput mode normalises to seconds.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `PRODUCT_CYCLE` (`product_cycle_id`, `product_id`, `uom_id`, `time_conversion_id`, `cycle_time_value`, `is_main_uom`, `valid_from`, `valid_to`) per [F-06 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#93-product_cycle--retired-2026-09-28) |
| **Reads only** | `UNIT_OF_MEASUREMENTS` for the item picker · `TIME_CONVERSIONS` (`seconds_per_unit`) for the time picker, the resolved seconds, and the `seconds` row throughput mode stores against · `PRODUCT_UOM_CONVERSION` for the consistency check · `OPERATION` only to show a comparison against the standard the KPI actually uses |
| **Not stored** | The throughput rate |
| **Never writes** | `OPERATION`: **none**. This screen must not change the KPI standard. No new fields |

**6. Rules & constraints**

1. Multiple rows may be active at once, one per item unit. Two active rows with the same item unit are
   refused.
2. Exactly one active row per product carries `is_main_uom = true`. Zero or two main rows is an invalid state and
   must be prevented within one transaction, not repaired afterwards.
3. The cycle time must be greater than 0. The time unit is required.
4. The item picker names the item, never a time unit. It offers item-unit rows only and the
   time picker time-unit rows only. The two lists never mix, and no combined "seconds/pack" option
   exists.
5. Throughput mode stores only the derived cycle time against the "seconds" row. There is no rate field
   on the entity, and this PRD may not add one.
6. Inconsistency between units is warned about, not blocked.
7. This screen does not write to `OPERATION` and must not offer a button that suggests it does, for example
   "apply as standard".
8. Changing a value closes the old row with its end date and opens a new one.
9. **Who may do it:** creating or updating a reference cycle time needs **tenant access**.
   `TBD — foundation-domain: scope level undetermined (P-1)`: `PRODUCT_CYCLE` inherits `PRODUCT`'s own
   unresolved tenant-vs-enterprise scope question, with no Site Hierarchy
   FK of its own ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#work-master-wms--08-work-mastermd-9)).
   Until P-1 resolves, treat it as `tenant`-scoped per the ABAC "no FK → resolves upward" default.
10. Needs a connection.

**7. Acceptance criteria**

- **AC-1** **Given** product `FG-1001` has no cycle rows, **when** the implementor saves item pack, cycle time
  20, time unit seconds, **then** the row is stored as 20 seconds per pack
  and is automatically flagged as the main unit.
- **AC-2 (multiple rows)** **Given** the per-pack row exists, **when** the implementor adds item carton, 240,
  seconds, **then** both rows are active and only the pack row carries the main flag.
- **AC-3 (single main)** **Given** two active rows with the pack row flagged main, **when** the implementor flags
  the carton row as main, **then** the pack row's flag is cleared in the same transaction, so there is never
  more than one main row.
- **AC-4 (consistency)** **Given** 20 s per pack and a conversion of 1 carton = 12 packs, **when** the
  implementor saves 200 s per carton, **then** the row is stored with an inconsistency marker naming the
  expected 240 s and the 16.7% gap.
- **AC-5 (cannot check)** **Given** no pack-to-carton conversion exists, **when** the implementor saves the
  carton row, **then** the consistency marker reads "cannot be checked" rather than "consistent".
- **AC-6 (validation)** **Given** the cycle time is entered as 0, **when** it is saved, **then** it is
  refused with "The cycle time must be greater than 0".
- **AC-7 (duplicate unit)** **Given** a per-pack row is already active, **when** the implementor adds another
  per-pack row, **then** it is refused with a link to the existing row.
- **AC-8 (screen boundary)** **Given** the implementor opens this screen, **when** they look for a way to use
  these figures as the Performance standard, **then** the screen says these figures are not used by
  Performance, names the operation standard in Work Master as the source, and offers no button that applies
  them as a standard.
- **AC-9 (permission)** **Given** a user without tenant access (this entity's scope pending P-1), **when** they
  open this table, **then** they can only read.
- **AC-10 (two fields)** **Given** the Add dialog is open, **when** the implementor inspects the unit controls,
  **then** the item picker lists only item units, the time picker lists only
  time units, and no combined "seconds/pack" option exists anywhere.
- **AC-11 (time unit)** **Given** item pack, cycle time 0.5, time unit minutes, **when** saved, **then**
  0.5 is stored in minutes and the table shows "0.5 minutes" and "30 s".
- **AC-12 (throughput)** **Given** throughput mode, rate 500, time unit hours, item pack, **when** the
  implementor saves, **then** the dialog previewed "7.2 s per pack" before submit, the stored row is
  7.2 seconds per pack (against the "seconds" time unit), no rate is stored, and reopening Edit
  shows 7.2 seconds per pack.
- **AC-13 (throughput validation)** **Given** throughput mode with rate 0, **when** "Save" is pressed, **then**
  it is refused with "Rate must be greater than 0" and nothing is divided.
- **AC-14 (seconds row missing)** **Given** no "seconds" time unit exists yet (no time conversion of exactly 1
  second per unit), **when** the implementor switches to throughput mode, **then**
  the mode is unavailable, with an explanation and a link to the time conversion screen.

**8. Metrics & events**

- **Metric:** `reference_cycles_consistent`, the share of non-main `PRODUCT_CYCLE` rows passing the consistency
  check within a 5% tolerance. Baseline not yet measured. The target is set once the baseline exists. Source:
  the `PRODUCT_CYCLE`, `TIME_CONVERSIONS`, and `PRODUCT_UOM_CONVERSION` tables.
- **Counter-metric:** the count of times a `PRODUCT_CYCLE` figure is read by the KPI calculation job. Must be
  exactly 0. Anything above 0 means a wiring mistake that makes OEE wrong.
- **Events:** `product_cycle_created` (`product_id`, `uom_id`, `time_conversion_id`, `entry_mode`,
  `is_main_uom`) · `product_cycle_inconsistent` (`product_id`, `uom_id`, `expected_value`, `entered_value`,
  `delta_percent`).

**Dependencies:** `US-FND-PRO-001`, `US-FND-REF-004`, `US-FND-REF-005`

---

#### US-FND-SIT-003

> **Retired 2026-09-27 (PO). Do not build.** Not a FOUNDATION story; a Production picker is parked. Kept below for history only.

**Pick the location context once for the whole app — CLOSED (retired)**

**Status:** ⚫ Closed, retired 2026-09-27 (PO, [docs-molcadx#46](https://github.com/molca-id/docs-molcadx/issues/46)).
The picker is not a FOUNDATION story: it was filed under Site Hierarchy, but its user and example are
production-side, and no FOUNDATION app (Master Data) should show it. Its screen `SCR-FND-SIT-003` is retired in
[UX 01](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md#screen-text)
and its `sit-003.*` screen text keys are removed and not reused. The `context_changed` event is dropped with it.
Do not build it, in any app. The ID is kept and not reused.

**Parked, not decided (issue #46):** whether a `US-PROD-MON-*` story replaces it in Production Monitoring, whether
Planning and Performance share it, and where the choice is stored until `US-FND-CFG-001` exists. If it comes back,
these rules are worth keeping: lines outside the user's access are never listed, one line means no picker, offline
shows only lines saved on the device, and the touch target is at least 44 × 44 px. The text below is kept as it
stood before retirement, for history only.

> **In short:** the supervisor picks their line once at the top of the app, and every screen follows it.
> They only ever see lines they have access to.

**1. Story**

As a **production supervisor**, I want to pick site → area → line once and have that context carry across
every screen, so I don't re-pick it on every view.

**2. Context**

- **Why:** the location hierarchy is the picker used across the whole app
  ([F-01, what this module is for](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#what-this-module-is-for)).
  A supervisor switches screens many times per shift while walking the floor; picking the line again on every
  screen is a cost paid over and over, every day.
- **Where:** a small screen, one hand, an unstable network.
- **Screen:** the location context picker `SCR-FND-SIT-003`, which remembers the user's last context ([UX 01](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md)).

**3. Expectation**

- A picker at the top shows the current line.
- If the user has access to only one line, there's **no** choice to make: that line is simply selected.
- Readable within 3 seconds: the current line's name.

Screen text is copied word for word from [UX 01 § Screen text](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md#screen-text)
(`SCR-FND-SIT-003`), with its key. If they differ, UX 01 is right.

- Picker label: "Line" (draft) (`sit-003.label`); the value shown is "\<line name\>" (`sit-003.value`).

| State | What the user sees |
|-------|--------------------|
| Default | Remembers the last context; one line only → no picker (`sit-003.default`: N/A — no text beyond `sit-003.value`) |
| Empty | No access at all → "You have not been granted a role on any line. Contact your Plant Admin/IT." (`sit-003.empty`) |
| Loading | The last line name stays visible (saved on the device), with a refreshing indicator (`sit-003.loading`: N/A — no text) |
| Error | The last line saved on the device is used, with a "line list may not be current" marker (`sit-003.error`) |
| Offline | The picker lists only lines already saved on the device; the offline marker reads "Offline — showing lines saved on this device" (draft) (`sit-003.offline`) |
| Success | Every screen switches to the chosen line without reloading the page. The choice stays until changed (`sit-003.success`: N/A — no text) |
| No permission | Lines the user can't access never appear in the list at all (they aren't shown and then refused) (`sit-003.no-permission`: N/A) |

**4. Calculation**

*Which lines the user can pick*
- Every active line that falls inside the user's access. Access to an area includes every line in it.
- The login system (IDP) decides what the user can access. FOUNDATION only knows which line belongs to which
  area and site, and never stores the user's access
  ([ABAC contract](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp)).
- Example: the user has access to the Assembly area (which contains lines L01 and L02) and to line L05 → they
  can pick L01, L02 and L05.

> [!note]- Exact formula (for developers)
> ```
> SELECT WORK_CENTER WHERE valid_to IS NULL AND work_center_id ∈ effective_scope(subject)
>
> effective_scope(subject) = ⋃ subtree(claim.scope_id)   over every scope claim the IDP asserts for the subject
> ```

*Edge cases*
- Access that has expired doesn't count (the login system handles this; it isn't read here).
- If the user can pick no lines at all → show the Empty state, never an empty picker.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `SITE`, `AREA`, `WORK_CENTER` (only to know which line belongs where: the `site_id` / `area_id` each `WORK_CENTER` resolves to). `ROLE` / `ROLE_ASSIGNMENT` are **not** FOUNDATION entities: the IDP asserts the user's access and it's never stored here |
| **Writes** | None. The chosen line is saved as the user's preference in [Configuration Service](12-configuration-service.md#us-fnd-cfg-001), at `user` level |

**6. Rules & constraints**

1. The list only contains items inside the user's access. Anything outside it is never shown.
2. Inactive items don't appear.
3. The chosen site and line are passed along with every data request and every event, as `site_id` and
   `line_id` ([04 §4](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/04-metrics-framework.md#4-konvensi-event)).
4. Access to only one line → no picker.
5. The picker's touch area is at least 44×44 px (it's used with gloves).

**7. Acceptance criteria**

- **AC-1** **Given** a supervisor with access to 3 lines, **when** they choose L02 on the OEE screen and then
  open the schedule screen, **then** the schedule screen already shows L02 without choosing again.
- **AC-2 (empty)** **Given** a user who hasn't been given any access, **when** they open the app, **then**
  they see an explanation that they have no access yet and who to contact, and no empty picker.
- **AC-3 (permission)** **Given** L09 is outside the user's access, **when** they open the line picker,
  **then** L09 is not in the list.
- **AC-4 (offline)** **Given** the device is offline, **when** the user opens the picker, **then** only the
  lines saved on the device appear and the offline marker is visible.
- **AC-5 (single line)** **Given** the user has access to only 1 line, **when** they open any screen,
  **then** no picker is shown and that line is selected straight away.

**8. Metrics & events**

- **Metric:** `pergantian_konteks_per_sesi` (*how often the user switches line in one session*): the median
  number of switches per session. A rise with no reason means the choice isn't sticking. Baseline
  `not yet measured`; measured from the `context_changed` event.
- **Counter-metric:** `waktu_muat_layar` (*screen load time*): the 95th-percentile load time must not get
  worse because of the picker.
- **Events:** `context_changed` (`from_level`, `to_level`, `scope_size`).

**Dependencies:** `US-FND-SIT-001`

---

#### US-FND-SHF-004

> **Retired 2026-09-25 (PO). Do not build.** Crew assignment is not needed for now; `SCR-FND-SHF-004` is retired in UX 02. Kept below for history only.

**Assign crews to shift instances — CLOSED (retired)**

**Status:** ⚫ Closed, retired 2026-09-25 (PO): crew assignment is not needed for now. Its screen
`SCR-FND-SHF-004` is retired in [UX 02](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/02-shift.md#screen-text)
and its `shf-004.*` screen text keys are removed and not reused. Do not build it. The ID is kept and not reused;
if crew assignment comes back, it starts from a new screen ID once the crew-grouping question (FOUNDATION vs
`production-domain`) is decided. The text below is kept as it stood before retirement, for history only.

> **In short:** the supervisor would pick which crew works each shift, so numbers can be credited to a
> crew. Retired: not needed for now, and there are no crews to pick from. Don't build it.

> This story reads and writes `CREW` and `SHIFT_ASSIGNMENT` (`crew_id` / `employee_id`), but `CREW` **has no FOUNDATION entity** since Organization left FOUNDATION scope ([01-glossary.md](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/01-glossary.md); [§13.5](99-history.md#135-organization-removed-2026-09-03)). Do not build this story against a `CREW` table that does not exist, and do not invent a replacement.
>
> **Open — `TBD — perlu konfirmasi PO`** (`foundation-domain`/PO decide where crew grouping lives): does crew grouping belong back in FOUNDATION as a narrower entity that does **not** reintroduce Organization/roles, does it move entirely to `production-domain` (e.g. a per-shift free-text or ad-hoc employee list, no persistent `CREW` master), or does "crew" become a `production-domain` concept scoped to a single site's roster? Until answered, `US-FND-SHF-004` stays BLOCKED, and [`US-FND-KPI-005`](07-kpi.md#us-fnd-kpi-005)/other stories that mention per-crew accountability as a benefit remain aspirational, not built on anything. The rest of this story is kept in full so work can start once this is decided.

**1. Story**

As a **production supervisor**, I want to assign crews to shifts, so production numbers can be attributed
to a crew.

**2. Context**

- **Why:** without crew assignment, nobody knows which crew produced which numbers. It's one of the three
  things that break at once without a firm shift concept ([F-02](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md)).
- **Value:** comparing Shift 1 with Shift 3 is one of the most useful analyses in a plant.
- **Who and where:** supervisors, at the start of a shift, often while walking. A small screen, and it has
  to be fast.
- **Screen:** crew assignment `SCR-FND-SHF-004` ([UX 02](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/02-shift.md)) — retired 2026-09-25 with the story.

**3. Expectation**

- A list of shifts a few days ahead, for each line. Each row shows the crew assigned (empty until the
  supervisor picks one), how many members are present, and an edit button.
- No crew is suggested from a pattern, because there is no shift pattern (no rotation, F-02 core concept 2).
- What the user can read within 3 seconds: this shift's crew, and whether it has enough people.

| State | What the user sees |
|-------|--------------------|
| Empty | No real shifts yet → point them to shift generation |
| Loading | A list placeholder |
| Error | "Could not save the assignment — try again"; everything typed stays in place |
| Offline | Assigning **may** need a connection, but a crew that's already saved must be readable offline so the work session can start |
| Success | A short confirmation that doesn't get in the way of the next row |
| No permission | Explained under Rules & constraints below |

**4. Calculation**

*No crew suggested from a pattern*
- There is no shift pattern ([F-02 core concept 2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#core-concepts):
  no rotation, every declared shift happens every working day), so nothing in FOUNDATION suggests a crew for
  each date.
- Whether a default crew should be suggested at all, and from what, is `TBD — perlu konfirmasi PO`. Until
  that's decided, the supervisor picks from the list of active crews with nothing filled in.

*Does the crew have enough people*
- The number of members present, minus the standard crew size for the operation.
- Example: 6 present against a standard of 8 → −2. A negative number shows as a warning, not a refusal.

> [!note]- Exact formula (for developers)
> ```
> crew_gap = members_present − OPERATION.standard_crew_size
> ```

*Edge cases*
- Assigning a person to two shifts whose hours overlap is refused, naming the other shift.
- Assigning a crew to a real shift that's already closed is refused.
- Inactive crews don't appear among the choices.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `SHIFT_INSTANCE`, `OPERATION.standard_crew_size` (`SHIFT_PATTERN` does not exist) |
| **Writes** | `SHIFT_ASSIGNMENT` (`shift_instance_id`, `crew_id` / `employee_id`, attendance flag). This entity is itself unconfirmed until the `CREW` question above resolves, since its FK target does not exist |
| **Don't exist in FOUNDATION** | **`CREW` and `EMPLOYEE`**: see the BLOCKED notice above |

**6. Rules & constraints**

1. No crew is filled in from a pattern. The supervisor picks one for each real shift (see the `TBD` in
   Calculation).
2. A person can't be assigned to two overlapping shifts.
3. Assigning a crew to a real shift that's already closed is refused.
4. Being short of the standard crew size is a warning, not a refusal: the system must not block a production
   decision.
5. **Who may do it:** changing a crew assignment needs **work-center access** to that work center (or
   broader). Viewing only needs access to that item.

**7. Acceptance criteria**

- **AC-1** **Given** tomorrow's real shifts exist, **when** the supervisor opens the shift list, **then** each
  row shows an empty crew slot and a crew can be picked and confirmed in one tap.
- **AC-2 (warning)** **Given** the crew has 6 members present while the standard crew size is 8, **when** the
  assignment is saved, **then** it is saved with a shortfall-of-2 warning visible on that row.
- **AC-3 (validation)** **Given** a person is already assigned to S1 that day, **when** they are assigned
  to an overlapping S2, **then** it is refused, naming the other shift they are already assigned to.
- **AC-4 (closed)** **Given** the shift is already closed, **when** the supervisor changes its assignment,
  **then** it is refused with an explanation that a final shift can only be changed through a flagged
  correction.
- **AC-5 (offline)** **Given** the device is offline at the start of the shift, **when** the operator starts
  the session, **then** the crew that's already saved can still be read and the session can start.
- **AC-6 (permission)** **Given** a user without work-center access to this work center, **when** they open
  the shift list, **then** they can see their crew but can't change the assignment.

**8. Metrics & events**

- **Metric:** `shift_dengan_regu_terkonfirmasi` (*shifts with a confirmed crew*): the % of real production
  shifts that have a crew assigned before the shift starts. Target **`[proposed]` ≥90%**, baseline
  `not yet measured`, measured from `shift_assignment_saved`.
- **Counter-metric:** confirming the crew takes 30 seconds or less per shift (it's done while walking).
- **Events:** `shift_assignment_saved` (`crew_id`, `member_present`, `crew_size_gap`).

**Dependencies:** `US-FND-SHF-002`

---

#### US-FND-WMS-005

> **Closed 2026-09-23. Do not build.** `QUALITY_DEFECT_CODE` was merged into `REJECT_REASON`; build
> [`US-FND-REF-002`](05-reference-master-data.md#us-fnd-ref-002) instead. Kept below for history only.

**Manage the quality defect code taxonomy**

**Status:** 🟡 Partly blocked — AC-5 (default disposition) waits on the disposition `TBD`; the real code list waits on W-4.

> **In short:** the implementor defines the defect codes operators pick when they record a defect. This lets
> Quality be broken down by cause instead of being one number.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to define defect codes, so the Quality metric can be
analysed by cause instead of being a single number.

**2. Context**

- **Why:** free-text defect codes strip the Quality metric of any Pareto analysis
  ([F-08, common pitfalls](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#common-pitfalls)).
- **List contents:** the real list must come from `ops-domain`
  ([W-4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown)). This screen provides the
  container.
- **Who uses the result:** operators pick codes one-handed on the shop floor, so the number of choices per tier
  must be kept low.
- **What the entity holds:** [F-08 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#97-quality_defect_code--retired-2026-09-23-merged-into-reject_reason)
  defines only 5 fields on `QUALITY_DEFECT_CODE`: `defect_code_id`, `code`, `name`, `valid_from`, `valid_to`.
  It has no disposition (`scrap`/`rework`), no parent code for hierarchy, and no product restriction. Don't
  add them. That behaviour is marked `TBD` below.
- **Screen:** the defect code list `SCR-FND-WMS-005`, reached from the **Reference data** sidebar (item 11). It
  keeps Work Master's Production license gate ([UX 08](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md), [UX 05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md)).

**3. Expectation**

- A defect code list with columns code and name.
- **Readable within 3 seconds:** the number of active codes.
- `TBD — perlu konfirmasi foundation-domain`: no disposition field exists on `QUALITY_DEFECT_CODE`. So every
  code appears in every operator's list, with no default `scrap`/`rework` value pre-filled, and there is no
  per-item/per-class restriction: an operator sees the full active code list, whatever they're recording
  against. Is a disposition field, a parent-code hierarchy, and a product-scoping join table needed, and should
  they be added to F-08 §9.7 first?

| State | What the user sees |
|-------|--------------------|
| Empty | "No defect codes yet", plus a warning: "Without defect codes, defects cannot be recorded and the Quality metric cannot be broken down by cause." |
| Loading | A table skeleton |
| Error | Input intact, plus a failure message |
| Offline | Not editable. The active list must be stored on the operator's device, so defects can be recorded offline |
| Success | The new code appears in the list |
| No entitlement (license gate) | Module-level, gated by **Production**. A tenant without a Production license sees this screen visible but locked, not hidden. This is separate from "No permission". See [`PLATFORM/FLOW/01-module-entitlement-check.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PLATFORM/FLOW/01-module-entitlement-check.md) |
| No permission | Read-only |

**4. Calculation**

*How the defect codes feed Quality* ([04 §5](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/04-metrics-framework.md#5-metrik-operasional-oee-family))
- Quality is the share of the total output that was good. The total output is the good part plus everything
  scrapped or reworked — each scrapped or reworked unit carries one of the codes defined here.
- Example: 1,000 units of output: 950 good, 30 scrap (code `SC-01`), 20 rework (code `RW-03`) →
  `Quality = 950 / 1,000 = 95%`.

> [!note]- Exact formula (for developers)
> ```
> Quality      = good_qty / total_output
> total_output = good_qty + scrap_qty + rework_qty
> ```

*Edge cases*
- No output at all → Quality is "cannot be computed", not 0%.
- **How successfully reworked units count is undecided**
  ([SF-3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#boundaries),
  [W-5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown)). Until decided, a
  successfully reworked unit is **not** counted as good. If that changes, a double-counting rule is mandatory
  ([§11 Q-04](30-release-risks-questions.md)).
- Until the `TBD` above resolves, the operator enters the disposition (`scrap` vs `rework`) of a recorded
  defect by hand at record time. This screen supplies the code, not the disposition.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `QUALITY_DEFECT_CODE` (`defect_code_id`, `code`, `name`, `valid_from`, `valid_to`) per [F-08 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#97-quality_defect_code--retired-2026-09-23-merged-into-reject_reason) |
| **Reads** | `WORK_ORDER_OPERATION`, for usage counts |
| **Not real fields: don't add** | `parent_defect_code_id`, `default_disposition`, `applies_to_product_id`: none exist in F-08 today, and this screen does not write them |
| **Boundary note** | Whether `QUALITY_DEFECT_CODE` and `REJECT_REASON` (F-05 §9.1) are the same entity under two names is [Q-18](30-release-risks-questions.md), still open |

**6. Rules & constraints**

1. A defect code is unique within the enterprise.
2. Codes are never deleted, only deactivated. Inactive codes still appear in historical data.
3. **Who may do it:** creating, updating or deactivating a defect code needs **tenant access**, never site
   access, and reading needs any access that covers it. This entity is scoped at `enterprise` or `tenant`,
   pending P-1: `QUALITY_DEFECT_CODE` has no Site Hierarchy foreign key
   ([F-00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md);
   [authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#work-master-wms--08-work-mastermd-9)).
   Until P-1 resolves, treat it as `tenant`-scoped per the ABAC "no FK → resolves upward" default.

**7. Acceptance criteria**

- **AC-1** **Given** an empty list, **when** the plant admin creates code `SC-01` with name "Weight out of
  range", **then** the code is saved and appears among the choices when an operator records a defect.
- **AC-2 (validation)** **Given** code `SC-01` already exists, **when** the same code is saved, **then** it is
  rejected, naming the existing holder.
- **AC-3 (offline)** **Given** the operator's device is offline, **when** they record a defect, **then** the
  active code list is still available from local storage.
- **AC-4 (permission)** **Given** a user without tenant access (this entity's scope pending P-1), **when** they
  open the defect taxonomy screen, **then** they can only read.
- **AC-5 (disposition, not yet testable)** Whether a code can carry a default disposition, and whether the
  operator can override it per entry, cannot be given pass/fail criteria until the `TBD` in Expectation is
  resolved. **Waiting on `foundation-domain`.**
- **AC-6 (entitlement)** **Given** a tenant without a Production license, **when** any user at that tenant opens
  the defect taxonomy screen, **then** the screen is visible but every action is locked with an upsell message,
  independent of that user's own ABAC scope (AC-4).

**8. Metrics & events**

- **Metric:** `defect_ber_kode` (*defects with a code*): the share of scrapped and reworked units that carry a
  defect code. Target **`[proposed]` ≥95%** within 30 days of the pilot line going active. Baseline
  `not yet measured`, source `defect_recorded`.
- **Counter-metric:** % of defects recorded under a generic "other" code. A high value means the taxonomy
  doesn't reflect floor reality.
- **Events:** `defect_code_created` (`code`) · `defect_code_deactivated` (`usage_count`).

**Dependencies:** `US-FND-PRO-001`


#### US-FND-SHF-003

**Manage the working calendar & exception days — CLOSED, CONCEPT RETIRED**

> **This story is closed. Do not build it.** It models `SHIFT_CALENDAR` and `NON_WORKING_DAY`, a fixed
> holiday/exception list. [F-03](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/03-yearly-quarterly-declaration.md#what-this-module-does-not-cover)
> records that the PO **retired that concept entirely**, because a fixed exception list does not fit when
> holidays and working patterns vary per enterprise, tenant, and site with no settled way to declare them.
>
> **Not replaced inside FOUNDATION.** "Which dates production is actually planned on" is owned entirely by
> `production-domain` ([Work Schedule](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md)). FOUNDATION does not
> model, and does not need to understand, how those dates get scheduled.
>
> What *did* stay in FOUNDATION from the old Calendar module is the fiscal-year declaration, now its own
> module: [`FYR`](03-yearly-quarterly-declaration.md). The ID `US-FND-SHF-003` is retired and
> never reused. Stories that depended on it are listed in [§13.2](99-history.md#132-stories-closed-and-retired).

#### US-FND-AST-004

**Manage the downtime reason code taxonomy — CLOSED, REPLACED**

> **This story is closed. Do not build it.** It was written against a single `DOWNTIME_REASON` table with a
> seven-value branching `category`, an `downtime_category` boolean, and per-`ASSET_CLASS` scoping. None of those
> exist any more. F-05 now has two separate taxonomies with a three-value `downtime_category` and scoping
> per asset instance, and the entities moved from F-04 to F-05, so the `AST` module code is no longer
> their home.
>
> **Replaced by** [`US-FND-REF-001`](05-reference-master-data.md#us-fnd-ref-001) (downtime
> taxonomy), [`US-FND-REF-002`](05-reference-master-data.md#us-fnd-ref-002) (reject taxonomy), and
> [`US-FND-REF-003`](05-reference-master-data.md#us-fnd-ref-003) (per-asset scoping). The ID
> `US-FND-AST-004` is retired and never reused. See [§13.2](99-history.md#132-stories-closed-and-retired).

#### US-FND-AST-005

**Record component swaps as installation history — MOVED, RETIRED**

> **This story is retired from PRD-003, 2026-09-10.** `ASSET_COMPONENT_LINK` — and the screen
> that recorded it, `SCR-FND-AST-006` — moved to the new Maintenance domain the same day (see
> [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-10-mom-domain-alignment.md)): component-swap history
> is a MOM execution/event record ("what happened to this asset, and when"), not FOUNDATION
> master data, following the same rule already applied to `WORK_ORDER` on 2026-09-03. The full
> story content (Story/Context/Expectation/Rules/AC/Metrics) is preserved verbatim in
> [`MAINTENANCE/DOCS-EN/01-asset-history.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/MAINTENANCE/DOCS-EN/01-asset-history.md)
> and [`MAINTENANCE/UX/01-asset-history.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/MAINTENANCE/UX/01-asset-history.md), not
> deleted. No Maintenance PRD exists yet (rule 1 — never created as a side effect of a move); a
> `US-MNT-AST-001` should be written from that preserved content once PO commissions one. →
> [why](99-history.md#132-stories-closed-and-retired)

#### US-FND-WMS-001

**Manage items & units of measure — CLOSED, SPLIT**

> **This story is closed. Do not build it.** It used the `PRODUCT` entity, since renamed `PRODUCT` and moved
> to F-06, and its calculation was `qty_in_base_uom = qty × UOM.conversion_value`. **`conversion_value`
> exists in no FOUNDATION file.** `UNIT_OF_MEASUREMENTS` carries only `uom_id`, `code`, `name`, `unit`;
> conversion exists solely per product through `PRODUCT_UOM_CONVERSION`.
>
> **Split into** [`US-FND-PRO-001`](06-product.md#us-fnd-pro-001) (product
> identity), [`US-FND-REF-004`](05-reference-master-data.md#us-fnd-ref-004) (units), and
> [`US-FND-PRO-003`](06-product.md#us-fnd-pro-003) (conversion). The ID
> `US-FND-WMS-001` is retired and never reused. Stories `US-FND-WMS-002` through `US-FND-WMS-005` that
> depended on it now depend on `US-FND-PRO-001`. See [§13.2](99-history.md#132-stories-closed-and-retired).
>
> **2026-09-28:** its screen, `SCR-FND-WMS-001` "Items & UOM", is retired too (PO, duplicate of the Product
> screens `SCR-FND-PRO-*`; ID not reused — [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) addendum, UX-WMS-3).

#### US-FND-INT-001

**Declare connectors & tag mappings as configuration — CLOSED, SPLIT**

> **This story is closed. Do not build it.** Closed 2026-09-04 (GitHub issue #20), executing the split its
> own 2026-09-03 note proposed and left open: *"should this story split into `US-FND-DSR-001` ... and a
> narrower `US-FND-INT-001` (ERP connector declaration only, once an ERP path exists)"*. All of this
> story's actual content — the `DATA_SOURCE` connection declaration and its paired `ASSET_TAGS` tag-to-asset
> mapping, described as one screen in its own Expectation row — is real, buildable FOUNDATION content, not
> ERP content; `ASSET_TAGS` is a per-asset signal declaration ([F-04 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#97-asset_tags--per-asset-tag--signal-declaration)),
> not something that changes meaning once split out of `INT`. It moves as-is.
>
> **Why closed rather than narrowed to a hollow ERP-only shell:** the proposed narrower `US-FND-INT-001`
> would have had **no source content at all** — [F-11 Integration](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md)
> is a problem-statement/boundary document with no ERP fields, screens, or entities specified anywhere.
> Every one of Expectation/Calculation/AC would be `TBD — perlu konfirmasi PO`, which fails the
> "designer/developer can start without asking PO" test in [`AGENTS.md` §4](https://github.com/molca-id/docs-molcadx/blob/main/AGENTS.md#4-format-prd--berbasis-user-story)
> — a hollow story is worse than no story. Per `US-FND-AST-004`'s precedent, this ID is retired instead.
> When an ERP path is scoped, it gets a **new** ID under `INT`, not a resurrected `US-FND-INT-001`.
>
> **Replaced by** [`US-FND-DSR-001`](13-data-source.md#us-fnd-dsr-001)
> (all `DATA_SOURCE`/`ASSET_TAGS` content, moved verbatim). The `scale`/`offset`/`interpretation_rules`
> gap this story had surfaced (neither real entity has these fields) is **not resolved by this split** —
> it moves to `US-FND-DSR-001` unchanged, still `TBD — perlu konfirmasi foundation-domain`. The ID
> `US-FND-INT-001` is retired and never reused. See [§13.2](99-history.md#132-stories-closed-and-retired).


## Organization module (removed 2026-09-03)

#### 6.4 FOUNDATION — Organization — CLOSED, RETIRED

> **This entire section is closed. Do not build any of it.** Per
> [`AGENTS.md` §4](https://github.com/molca-id/docs-molcadx/blob/main/AGENTS.md#4-format-prd--berbasis-user-story) (2026-08-24 decision,
> same date as this PRD): *"modul Organization dihapus dari kode FOUNDATION: peran/akses kini
> murni milik identity provider (SSO/Keycloak), tidak dimodelkan FOUNDATION."* Organization
> was removed from FOUNDATION's scope entirely — roles, scopes, employees, and crews are not
> FOUNDATION entities. `00-foundation.md` in the current spec has zero mention of `ORG_UNIT`
> or Organization anywhere.
>
> **`US-FND-ORG-001`/`002`/`003` are retired and never reused.** Every other section's story
> that cited one of these as a dependency has been reviewed below (per the dependency rule:
> "story yang menjadi dependensi tidak boleh dibatalkan diam-diam"). Where a story genuinely
> cannot function without more detail about identity-provider integration than currently
> exists in FOUNDATION's docs, that gap is marked `TBD — perlu konfirmasi PO` rather than
> invented here. See [§13.2](99-history.md#132-stories-closed-and-retired).

<!-- ORIGINAL SECTION CONTENT RETIRED 2026-08-28, KEPT BELOW FOR HISTORY ONLY — DO NOT BUILD -->

#### US-FND-ORG-001

~~**Manage org units & employee records**~~

| Part | Content |
|------|---------|
| **Story** | As a **Plant Admin/IT (internal implementor)**, I want to register work units and employees including those without accounts, so crew attendance can be recorded and every entry has an owner. |
| **Context** | Not everyone who works has an account — operators are often recorded by a leader, or one terminal is shared; forcing a one-to-one relation makes attendance data impossible to capture ([F-03 §3.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#31-employee--user-account)). An employee who leaves must still appear as the recorder of old entries ([F-03 §3.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#35-nonaktif--hapus)). Desktop, implementor; the employee data source (HRIS or manual entry) is unsettled ([O-3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#open-questions-at-a-glance)), so bulk import is mandatory. |
| **Expectation** | **Default:** two panels — the `ORG_UNIT` tree on the left, the employee list for the selected unit on the right with columns employee number, name, employment status, default crew, and a "has account" marker.<br>Readable within 3 seconds: active employee count per unit.<br>**Empty:** "No org units yet" + a note that the org unit tree is **not** the location hierarchy (a department is not an area).<br>**Loading:** skeleton.<br>**Error:** input intact + failure message; import reports failures per row.<br>**Offline:** unavailable.<br>**Success:** the new record is selected; form ready for the next entry.<br>**No permission:** read-only.<br>Text limits: name ≤120, employee number ≤30 chars. |
| **Calculation** | `active_employees_per_unit = COUNT(EMPLOYEE WHERE org_unit_id = X AND employment_status = 'active' AND (valid_to IS NULL OR valid_to > today))`.<br>Example: the Assembly unit has 24 employees, 2 `on_leave`, 1 `inactive` → 21 active.<br>Edge cases: employees whose `valid_to` has passed are not counted but still appear in production entry history; a unit with no employees is still valid. |
| **Data & entities** | Read: `SITE`, `CREW`.<br>Write: `ORG_UNIT` (`org_unit_code`, name, `parent_org_unit_id`, `valid_from`, `valid_to`), `EMPLOYEE` (`employee_number`, `full_name`, `org_unit_id`, `primary_site_id`, `default_crew_id`, `employment_status`, `valid_from`, `valid_to`). |
| **Rules & constraints** | ① The org structure is recursive and time-variant — a placement change closes the old row and creates a new one, never overwrites ([F-03 §3.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#34-struktur-organisasi-bersifat-rekursif-dan-time-variant)).<br>② An `EMPLOYEE` without a `USER_ACCOUNT` is valid.<br>③ Employees are never deleted — `employment_status = inactive` + `valid_to`; production entries they recorded still refer to them.<br>④ Roles are **not** stored on the employee record — they belong in `ROLE_ASSIGNMENT` ([F-03 §7.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md)).<br>⑤ An org unit is **not** a location hierarchy node.<br>⑥ Access: `admin` at site or enterprise scope. |
| **Acceptance criteria** | **AC-1** Given an org unit exists, When the implementor saves an employee without an account, Then the employee is saved and can be selected as a crew member.<br>**AC-2 (validation)** Given the employee number is already used, When saved, Then it is rejected naming the holder of that number.<br>**AC-3 (deactivation)** Given the employee has recorded 50 production entries, When they are deactivated, Then all 50 entries still show their name as the recorder.<br>**AC-4 (time-variant)** Given the employee moves units, When the change is saved effective next month, Then the old row closes and last month's report still uses their old unit.<br>**AC-5 (import)** Given an employee import file with 2 duplicate employee numbers, When the import runs, Then the other rows are saved and the 2 failures are reported with reasons.<br>**AC-6 (permission)** Given a user with the `supervisor` role, When they open the employee list, Then they can only read employees within their scope. |
| **Metrics & events** | Metric: `karyawan_lini_pilot_terdaftar` (*pilot-line employees registered*) — % of pilot-line crew members registered before the line starts recording production; target 100%.<br>Baseline 0 (2026-08-07), source: `EMPLOYEE` table.<br>Counter-metric: % of production entries with no identifiable recorder — must be 0.<br>Events: `employee_created` (`org_unit_id`, `has_account`, `entry_mode`), `employee_deactivated` (`entry_count`). |
| **Dependencies** | `US-FND-SIT-001` |

#### US-FND-ORG-002

~~**Grant roles with a scope**~~

| Part | Content |
|------|---------|
| **Story** | As a **Plant Admin/IT (internal implementor)**, I want to grant roles within a specific scope, so the Line A supervisor can neither see nor change Line B's data. |
| **Context** | Binary access lets the Line A supervisor change Line B's data ([F-03 §2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md)). Global roles without a scope look simple at first and are impossible to fix once there is a lot of data ([F-03 §7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md)). The **role X on scope Y** model binds every other module. Desktop, implementor. The final `ROLE` list has not been set ([O-2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#open-questions-at-a-glance)) — this screen works against whatever `ROLE` rows are registered. |
| **Expectation** | **Default:** a list of role grants per user: columns user, role, scope type, scope name, validity. An "effective permissions" panel shows the union across all of the selected user's grants — this is what answers "what is this person actually allowed to do, and where".<br>Readable within 3 seconds: how many active grants that user has.<br>**Empty:** a user with no roles → "No roles yet. Without a role, the user cannot open any screen."<br>**Loading:** skeleton; the effective-permissions panel loads separately.<br>**Error:** input intact + failure message.<br>**Offline:** unavailable.<br>**Success:** the new row appears and the effective-permissions panel refreshes immediately.<br>**No permission:** "Only the `admin` role on this scope or above may grant roles." |
| **Calculation** | Effective permission: `allowed(user, action, node) = ∃ ROLE_ASSIGNMENT ra : ra.user_id = user AND action ∈ permissions(ra.role_id) AND node ∈ subtree(ra.scope_id) AND ra active today`.<br>Example: a user holds `supervisor` on `AREA` Assembly (containing L01, L02) → may close shifts on L01 and L02, not on L05. If they also hold `operator` on L05 → may record output on L05 but not close its shift.<br>Edge cases: overlapping grants **union** their permissions rather than taking the strictest; there is no global role except `scope_type = enterprise`; grants whose `valid_to` has passed do not count. |
| **Data & entities** | Read: `USER_ACCOUNT`, `ROLE`, `EMPLOYEE`, `SITE`, `AREA`, `WORK_CENTER`, `ENTERPRISE`.<br>Write: `ROLE_ASSIGNMENT` (`user_id`, `role_id`, `scope_type`, `scope_id`, `valid_from`, `valid_to`), `USER_ACCOUNT` (account creation/deactivation). |
| **Rules & constraints** | ① Every grant **must** have a `scope_type` + `scope_id`; there is no role without a scope.<br>② A user may hold multiple grants.<br>③ `scope_type` is limited to `enterprise` / `site` / `area` / `work_center`.<br>④ A grantor cannot grant roles outside their own scope.<br>⑤ Revocation sets `valid_to`, never deletes — the audit trail must stay intact.<br>⑥ One `EMPLOYEE` has 0 or 1 active `USER_ACCOUNT`.<br>⑦ The operator login model (per person vs shared account per station) is undecided ([O-4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#open-questions-at-a-glance)) — assumption A6 in [§4](README.md) holds until it is decided. |
| **Acceptance criteria** | **AC-1** Given a user has an account, When the implementor grants `supervisor` on the Assembly area, Then the effective-permissions panel lists every line beneath Assembly.<br>**AC-2 (validation)** Given no scope has been selected, When saved, Then it is rejected with "Scope is required — roles without a scope are not permitted".<br>**AC-3 (grantor limit)** Given the implementor only holds `admin` on site JKT1, When they try to grant a role on site BDG1, Then it is rejected with an explanation of their scope limit.<br>**AC-4 (access denial)** Given a supervisor holds a role only on L01, When they open L02's data via a direct link, Then access is denied with an explanation and who to contact — not a blank page.<br>**AC-5 (union)** Given a user holds `operator` on L05 and `supervisor` on Assembly, When their effective permissions are computed, Then they can record output on L05 and close shifts on L01/L02, and cannot close the shift on L05.<br>**AC-6 (revocation)** Given the role is revoked today, When the user opens the related screen, Then access is denied, and the history of entries they made earlier stays intact. |
| **Metrics & events** | Metric: `akses_ditolak_per_user_per_minggu` (*access denials per user per week*) — high means scopes were granted wrongly, not that users are wrong; baseline `not yet measured`, source `access_denied`.<br>Counter-metric: 0 incidents of a user seeing data outside their scope (tested, not assumed).<br>Events: `role_assigned` (`role_id`, `scope_type`), `role_revoked` (`role_id`, `scope_type`), `access_denied` (`attempted_scope`, `role_held`). |
| **Dependencies** | `US-FND-ORG-001`, `US-FND-SIT-001` |

#### US-FND-ORG-003

~~**Manage crews**~~

| Part | Content |
|------|---------|
| **Story** | As a **Plant Admin/IT (internal implementor)**, I want to group employees into crews, so shift assignment isn't filled in person by person every day. |
| **Context** | A crew is a group scheduled together on a shift and often rotating — different from an org unit, which is structural and rarely changes ([F-03 §3.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#33-crew-regu--pengelompokan-untuk-penjadwalan)). The real crew count and rotation pattern are unknown ([H-2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#94-open-questions)). Desktop, implementor; edited when the rotation changes. |
| **Expectation** | **Default:** crew list with member count and validity; the crew detail shows its members and the lines it usually staffs.<br>Readable within 3 seconds: number of active crews and members per crew.<br>**Empty:** "No crews yet" + a note that without crews, shift assignment must be filled per person every day.<br>**Loading:** skeleton.<br>**Error:** input intact + failure message.<br>**Offline:** unavailable.<br>**Success:** the crew is saved; members can be added without leaving the screen.<br>**No permission:** read-only. |
| **Calculation** | `active_members = COUNT(EMPLOYEE WHERE default_crew_id = X AND employment_status = 'active')`.<br>Comparison against standard: `crew_gap = active_members − OPERATION.standard_crew_size` for the operations that line runs.<br>Example: crew A has 6 active members, `standard_crew_size = 8` → `crew_gap = −2`, shown as a warning during shift assignment, not a rejection.<br>Edge cases: a crew with no members is still valid (prepared ahead) but flagged; employees who are `on_leave` are not counted as active members. |
| **Data & entities** | Read: `EMPLOYEE`, `WORK_CENTER`, `OPERATION.standard_crew_size`. Write: `CREW` (`crew_code`, name, `site_id`, `valid_from`, `valid_to`), `EMPLOYEE.default_crew_id`. |
| **Rules & constraints** | ① `crew_code` unique within the site.<br>② `CREW` is time-variant — composition changes apply forward.<br>③ Crews are never deleted, only deactivated; a crew still used by future `SHIFT_ASSIGNMENT` rows cannot be deactivated — reject naming the assignment dates.<br>④ An employee has at most one default crew; they can still be assigned to another crew's shift as an exception.<br>⑤ Access: `admin` at site scope; `supervisor` may change crew members on their line. |
| **Acceptance criteria** | **AC-1** Given active employees exist, When the implementor creates a crew and adds 6 members, Then the crew is saved with 6 members and can be selected during shift assignment.<br>**AC-2 (validation)** Given the crew code is already used on that site, When saved, Then it is rejected naming the crew holding that code.<br>**AC-3 (crew warning)** Given the operation's `standard_crew_size` is 8 and the crew has 6 active members, When the crew is assigned to a shift, Then a warning about the shortfall of 2 appears and the assignment can still proceed.<br>**AC-4 (deactivation)** Given the crew has shift assignments next week, When it is deactivated, Then it is rejected naming the blocking assignment dates.<br>**AC-5 (empty)** Given a new crew with no members, When the crew list is opened, Then that crew is flagged "no members yet".<br>**AC-6 (permission)** Given a user with the `operator` role, When they open the crew list, Then they can only read. |
| **Metrics & events** | Metric: `shift_dengan_regu_teridentifikasi` (*shifts with an identified crew*) — % of production `SHIFT_INSTANCE` rows having a `SHIFT_ASSIGNMENT`; a precondition for per-crew accountability ([F-06 §2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md)). Target **`[proposed]` ≥90%**, baseline `not yet measured`, source: `SHIFT_ASSIGNMENT` table.<br>Counter-metric: weekly time spent on shift assignment (must not be slower than a whiteboard).<br>Events: `crew_created` (`site_id`, `member_count`), `crew_member_changed` (`crew_id`, `delta`). |
| **Dependencies** | `US-FND-ORG-001` |
