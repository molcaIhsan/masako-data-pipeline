# PRD-003 · KPI Formula & Tag Mapping (`KPI`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-07 / F-07.1. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Stories are written in plain language. Exact field names are in section 5 and in the collapsible
> "Exact formula (for developers)" boxes, each under its plain explanation.


> Every formula in this file is quoted from [F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md), which quotes
> ISO 22400-2:2014 directly. How each element is filled from FOUNDATION entities is quoted from
> [F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md). This PRD **derives no new formula**. Where
> this PRD and F-07 disagree, F-07 wins.
>
> **Step 0 applies to the first five stories in this section.** A work unit has no tags of its own. It
> borrows the tags of whichever asset is placed there **at the timestamp of the event being calculated**,
> not whichever asset is placed there now. Resolving the placement wrongly means every metric below reads
> the wrong machine's data, for example using today's asset for a shift last week after a machine swap.
>
> **Multi-lane machine (2026-09-25, [F-07.1 where tags live](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#where-tags-live),
> [F-04 §9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#96-asset-to-work-unit-placement-rules)).** One machine may be placed on several work
> units at once, one per lane. Step 0 then keeps only the tags that belong to **this** work unit: per-lane roles
> (`product_code`, `total`, `good`, `reject`, `reject_reason`, `speed`) only where `ASSET_TAGS.work_unit_id` =
> this work unit; machine-wide roles (`machine_state`, `downtime_reason`, `runtime`) on every lane the machine
> is placed on. A counter is never counted in two work units. Tags are resolved and read by `tag_id`, never by
> address: an OPC UA tag's address is generated from the placement ([F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address)),
> so a move changes the address, not the tag or its KPI history.

## Terms used in this module

Definitions from [F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#iso-time-elements-clause-51) (ISO 22400-2) and from the stories below.

| Term | Meaning |
|------|---------|
| **POT** | Planned operation time. The scheduled time a work unit can be used. Here: `SHIFT_INSTANCE` start to end. |
| **PBT** | Planned busy time. POT minus planned downtime. |
| **APT** | Actual production time. The time a work unit is actually producing. Here: PBT minus unplanned downtime. |
| **PRI** | Planned run time per item. The planned time to produce one unit (cycle time in Work Master). |
| **PQ** | Produced quantity (total output). |
| **GQ** | Good quantity. Produced quantity that meets quality requirements. |
| **SQ** | Scrap quantity. Produced quantity that did not meet quality requirements. |
| **RWQ** | Rework quantity. `WORK_ORDER_OPERATION.rework_qty`. |
| **AOET** | Actual order execution time. `WORK_ORDER_OPERATION.actual_end − actual_start` for one operation. |
| **Effectiveness** | ISO name for what MolcaDx calls **Performance**. Same formula. |
| **`small_stop`** | A short stop: from `min_stop_seconds` (default 60 s) up to `small_stop_max_seconds` (default 300 s) — the [stop thresholds](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#confirmed-first-release-item-stop-thresholds-po-2026-10-01). Counts as a Performance loss, never reduces PBT or APT. A stop shorter than `min_stop_seconds` is not a stop event at all (speed loss). |
| **OEE** | Overall equipment effectiveness: Availability × Performance × Quality. Shown only when all three exist. |
| **MTTR / MTBF** | Mean time to repair (average length of an unplanned stop) / mean time between failures (average running time between unplanned stops). |
| **SKU** | One product variant. A shift can run several SKUs, each with its own cycle time. |
| **WIP buffer** | Work in progress held between two stations, so their counters don't line up unit for unit. |
| **Batch** | One run of a batch process (ISA-88), for example one mixer cycle from start to discharge. A process term. **Not** SAP's "batch" (that is a lot). On a work center of type `process_cell` ([glossary](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/01-glossary.md)). |
| **Lot** | A labelled quantity of material treated as one unit for traceability and stock. Usually 1 batch = 1 lot, but a batch can be split into several lots and several batches merged into one ([glossary](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/01-glossary.md)). |

## Open items in this module

Index only. Each item is still stated inline in its story. Question details: [30-release-risks-questions.md](30-release-risks-questions.md).

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| B5 | `US-FND-KPI-001`, `US-FND-KPI-002` (batch split), `US-FND-KPI-004`, `US-FND-KPI-005`, `US-FND-KPI-012` | `ASSET_STATE_LOG` (basis of APT, MTTR/MTBF, and the running-time split of a batch's output). **Ownership resolved 2026-09-28: PRODUCTION** ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)). **SL-1 resolved 2026-09-29:** recording is per asset, `asset_id` and `work_unit_id` both required. Its entity spec is still a PRODUCTION draft ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)): state field names and the shift-split rule are still `TBD` (row ID named `asset_state_log_id`, 2026-10-01) | `production-domain` |
| ~~Q-21 / K-6~~ | `US-FND-KPI-006` | ~~Stall threshold separating `small_stop` from real downtime: value, global or per work unit, and where stored~~ **Resolved 2026-10-01 (PO):** two [stop thresholds](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#confirmed-first-release-item-stop-thresholds-po-2026-10-01) — `min_stop_seconds` (default 60 s) and `small_stop_max_seconds` (default 300 s), per work center with optional work unit override, effective-dated. Storage table still `TBD` for engineering (F-12) | engineering + PO (storage) |
| Q-21 | `US-FND-KPI-002` | Debounce window for `product_code` tag transitions | `ops-domain`, PO, `production-domain` |
| ~~Q-21 (origin marker)~~ | `US-FND-KPI-006` | ~~Event origin marker field on `ASSET_STATE_LOG` does not exist~~ **Resolved 2026-09-28:** PRODUCTION's `ASSET_STATE_LOG` spec has `source` (`plc` / `manual` / `derived`); a stall-detected row is `derived` ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)) | — |
| Q-23 (K-5) | `US-FND-KPI-002` | Is changeover between two SKU segments its own Performance-loss bucket or a `planned` `DOWNTIME_REASON`? | `ops-domain`, PO |
| Q-24 | `US-FND-KPI-003` | Count tolerance for `good + Σ reject` to match `total` | `ops-domain`, PO |
| Two-station path | `US-FND-KPI-003` | Validity of the two-station reject derivation is pending Operations | Operations (not named further) |
| ~~K-8 (Q-26)~~ → K-10 | `US-FND-KPI-008` | ~~Does range detection share stall detection's threshold (K-6)?~~ **Resolved 2026-10-01 (PO): one shared pair.** Still open as **K-10**: who owns the normal-operating band bounds, where are they stored, and are they effective-dated ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#whats-still-unknown)) | `ops-domain`, PO |
| K-11 | `US-FND-KPI-006` | (a) A detected stop longer than `small_stop_max_seconds` whose reason has `downtime_category = small_stop`: duration or reason wins? (b) Do the stop thresholds also apply to stops an operator opens manually, or only to detected ones? ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#whats-still-unknown)) | PO + Operations |
| `WORK_CENTER.is_oee_tracked` | `US-FND-KPI-004` (also `US-FND-KPI-001`, `US-FND-SIT-001`) | OEE line-eligibility gating field does not exist in F-01 (`TBD — perlu konfirmasi foundation-domain`) | `foundation-domain` |
| Multiple reason tags | `US-FND-KPI-006` | No selection rule when more than one reason tag is active in a stall window (interim rule: earliest) | not stated |
| MTBF on low-volume lines | `US-FND-KPI-005` | Whether non-production gaps (e.g. holidays) should be adjusted; not yet raised | Operations |
| Metric target | `US-FND-KPI-008` | Target for `kpi_breakdown_viewed_before_edit` (`TBD — perlu konfirmasi PO`) | PO |
| ~~Conversion in breakdown~~ | `US-FND-KPI-008` | ~~Conversion path or worked figure?~~ **Resolved 2026-09-28 (PO): the rule, not live numbers** — keys `kpi-004.conversion.rule` / `.missing` ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md), addendum) | — |
| Product for conversion rule | `US-FND-KPI-008` | ~~`kpi-004.conversion.rule` is per product, the page is per KPI × work unit: which product's `OPERATION` is shown when several products run on the work unit? (`TBD — needs PO confirmation`)~~ **Resolved 2026-09-28 (PO):** the product **running now** on the work unit (from its `product_code` tag); if nothing runs, the **last product run** (UX 07 `kpi-004.conversion.product`). | PO |
| ~~Reject by weight vs one-source pair~~ | `US-FND-KPI-003` (also `US-FND-KPI-009`) | ~~Is a manual weight reject + counted output one allowed pair?~~ **Resolved 2026-10-01 (PO, Q-28):** yes. Manual reject may be entered in any unit with a conversion path; Quality calculates in `OPERATION.uom_id`; machine total + manual reject is an allowed pair, `source_tier = manual` ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#reject-entered-manually-in-any-unit--convert-to-the-step-unit-and-time-po-2026-09-30-2026-10-01)) | — |
| `activity_signal` in F-07.1 Availability | `US-FND-KPI-001`, `US-FND-KPI-007`, `US-FND-KPI-008` | F-07.1's range detection, reverse table and transform enum let an `activity_signal` tag drive Availability (2026-10-01), but its Availability section still says `speed` is "the one exception" with a named path. Wording lag in the spec, flagged to `foundation-domain` | `foundation-domain` |
| [A-15](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#98-open-questions) | `US-FND-KPI-001`, `US-FND-KPI-006`, `US-FND-KPI-007` | Multi-lane machine-wide signals: one address under one assigned work unit applied to every lane (default), or aliased per lane? Does any real machine have a per-lane stop signal (then declared as a per-lane `machine_state` tag)? | PO / Operations |
| Metric target | `US-FND-KPI-010` | Target for `rework_ratio_computed` (`TBD — perlu konfirmasi PO`) | PO |
| ~~Quality unit conversion~~ | `US-FND-KPI-003` (also `US-FND-KPI-007`, `US-FND-KPI-009`) | ~~Is "same unit" enforced at binding, or does the conversion edge case stay?~~ **Resolved 2026-09-28 (PO): blocked at binding** — all Quality tags of a work unit share one unit; Quality never converts ([F-07.1 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#94-work_unit_kpi_binding), [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md), addendum) | — |
| K-12 | `US-FND-KPI-001` to `013` (every story that writes `KPI_RESULT`) | Keys (2026-10-01): `inputs_used` and `waterfall` are lists, which a stored column may not be: child table or other storage? And the result's natural key — `period` has two shapes, and does a recompute replace the row? | `foundation-domain` |
| K-13 | `US-FND-KPI-007` | Keys (2026-10-01): a binding is "one row per work unit per slot", but the spec's example has two active rows for `availability.downtime_reason`. What is the no-overlap rule? | `foundation-domain` |
| K-14 | `US-FND-KPI-007` | Keys (2026-10-01): is (`slot_id`, `tag_role`, `transform`) unique in `KPI_PARAMETERS`, and `rank` unique per slot? | `foundation-domain` |
| K-15 | `US-FND-KPI-007`, `US-FND-KPI-008` | Keys (2026-10-01): `KPI_FORMULA_SLOT.slot_id` is a readable text key used as the PK. Add a surrogate key, or allow the exception? | `foundation-domain` / PO |

## Stories


| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-KPI-001`](#us-fnd-kpi-001) | Compute Availability from POT to PBT to APT | 🔴 Blocked | As a **production supervisor**, I want my line's Availability computed with PBT as the denominator, so that the line is not penalised for downtime that was planned. → [detail](#us-fnd-kpi-001) | `US-FND-SHF-002`, `US-FND-AST-003`, `US-FND-REF-001`, `US-PROD-SHF-003`, `US-FND-KPI-007` |
| [`US-FND-KPI-002`](#us-fnd-kpi-002) | Compute Performance with per-SKU segmentation | 🟡 Partly blocked | As a **production supervisor**, I want Performance computed per SKU run and then summed, so that a shift running two products is not judged against one averaged standard. → [detail](#us-fnd-kpi-002) | `US-FND-KPI-001`, `US-FND-PRO-001`, `US-FND-PRO-003`, `US-FND-WMS-003`, `US-PROD-SHF-001`, `US-FND-KPI-007` |
| [`US-FND-KPI-003`](#us-fnd-kpi-003) | Compute Quality from whichever input pair exists | 🟢 Ready | As a **production supervisor**, I want Quality computed from the pair of figures my line actually has, so that a line without a good counter still gets a Quality number without inventing the missing side. → [detail](#us-fnd-kpi-003) | `US-PROD-SHF-001`, `US-FND-REF-002`, `US-FND-KPI-007` |
| [`US-FND-KPI-004`](#us-fnd-kpi-004) | Compute OEE and the loss waterfall from PBT | 🔴 Blocked | As a **production supervisor**, I want OEE to appear only when all three of its components are computed, together with the minutes lost at each step, so that one composite number does not hide where the time went. → [detail](#us-fnd-kpi-004) | `US-FND-KPI-001`, `US-FND-KPI-002`, `US-FND-KPI-003` |
| [`US-FND-KPI-005`](#us-fnd-kpi-005) | Compute MTTR and MTBF from unplanned stops | 🔴 Blocked | As a **plant manager**, I want mean repair duration and mean gap between failures per line, so that I can compare line reliability without waiting for a Maintenance module. → [detail](#us-fnd-kpi-005) | `US-FND-KPI-001`, `US-FND-REF-001` |
| [`US-FND-KPI-006`](#us-fnd-kpi-006) | Open downtime from a stalled counter | 🟡 Partly blocked | As a **production supervisor**, I want a stopped line recorded automatically from a counter that stops incrementing, so that stops are captured even when no operator presses anything. → [detail](#us-fnd-kpi-006) | `US-FND-KPI-001`, `US-FND-AST-002`, `US-FND-KPI-007` |
| [`US-FND-KPI-007`](#us-fnd-kpi-007) | Bind a work unit's tags to its KPI formula slots | 🟢 Ready | As an **Admin/IT pabrik**, I want to pick which tag and which math fills each mandatory KPI formula slot for a work unit, so that the calculation job has an explicit, traceable source for every metric instead of a guessed "first that works" order. → [detail](#us-fnd-kpi-007) | `US-FND-AST-002`, `US-FND-AST-003` |
| [`US-FND-KPI-008`](#us-fnd-kpi-008) | View KPI calculation breakdown | 🟡 Partly blocked | As an **Admin/IT pabrik**, I want to see, for one KPI on one work unit, which formula, tags, and downtime-detection method actually feed it, so that I can trust or troubleshoot a number without asking `foundation-domain` to read the config for me. → [detail](#us-fnd-kpi-008) | `US-FND-KPI-007` |
| [`US-FND-KPI-009`](#us-fnd-kpi-009) | Compute Scrap ratio from the same Quality inputs | 🟢 Ready | As a **production supervisor**, I want a Scrap ratio next to Quality, so that I can see what share of output was scrapped without doing the subtraction from Quality myself. → [detail](#us-fnd-kpi-009) | `US-FND-KPI-003` |
| [`US-FND-KPI-010`](#us-fnd-kpi-010) | Compute Rework ratio from job rework quantity | 🟢 Ready | As a **production supervisor**, I want a Rework ratio next to Quality and Scrap ratio, so that I can tell apart output that was scrapped from output that was reworked and still recovered. → [detail](#us-fnd-kpi-010) | `US-FND-KPI-003` |
| [`US-FND-KPI-011`](#us-fnd-kpi-011) | Compute Throughput rate from actual order execution time | 🟢 Ready | As a **production supervisor**, I want a Throughput rate for the operation currently running, so that I can see output per unit of actual run time without waiting for a full shift's Performance figure. → [detail](#us-fnd-kpi-011) | `US-FND-KPI-003`, `US-PROD-SHF-001` |
| [`US-FND-KPI-012`](#us-fnd-kpi-012) | Compute Production process ratio from APT and AOET | 🟡 Partly blocked | As a **production supervisor**, I want to see what share of an operation's actual run window was genuinely productive time, so that I can tell a slow-starting operation from one that ran productively the whole time it was open. → [detail](#us-fnd-kpi-012) | `US-FND-KPI-001`, `US-FND-KPI-011` |
| [`US-FND-KPI-013`](#us-fnd-kpi-013) | Compute Fall-off ratio per Job Order | 🟢 Ready | As a **production supervisor**, I want the Fall-off ratio of each Job Order, so that I can see what share of what entered the routing did not come out good at a later step. → [detail](#us-fnd-kpi-013) | `US-FND-KPI-003` |

## Detail blocks

---

#### US-FND-KPI-001

**Compute Availability from POT to PBT to APT**

**Status:** 🔴 Blocked — not buildable end-to-end until B5 resolves: `ASSET_STATE_LOG` is owned by PRODUCTION since 2026-09-28 and its recording level (SL-1) was settled on 2026-09-29, but its entity spec is still a draft (state field names, shift-split rule `TBD`; row ID `asset_state_log_id` since 2026-10-01). Rules and ACs can be designed now.

> **In short:** Availability is the share of the line's planned busy time (PBT) that it actually spent
> producing (APT). Planned stops come out first, so the supervisor's line is never penalised for downtime
> that was planned.

**1. Story**

As a **production supervisor**, I want my line's Availability computed with PBT as the denominator, so that
the line is not penalised for downtime that was planned.

**2. Context**

- **Risk (B5):** POT rests on `SHIFT_INSTANCE`, generated daily before each business day by `production-domain`
  ([F-02 §H-6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#9-entity-specification)). APT rests on
  `ASSET_STATE_LOG`. **Owner settled 2026-09-28: PRODUCTION** writes it, MAINTENANCE only reads it
  ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)). Its entity spec is PRODUCTION's draft ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)): `asset_id`,
  `work_unit_id`, `shift_instance_id`, `downtime_reason_id`, `source`, `started_at`, `ended_at`, `entered_by`,
  `entered_at` are named. The recording level (SL-1, was FOUNDATION A-5) was resolved on 2026-09-29: per asset,
  with `asset_id` and `work_unit_id` both required. The row ID and state field names and the rule for a row
  spanning two shifts are still `TBD`. This story can be designed and its rules/AC below stand,
  but it is **not buildable end-to-end** until B5 resolves. See [F-07's status table](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#status-what-foundation-already-provides-for-production-domain-to-build-on).
- **The first metric to come alive:** Availability needs only two things, the shift hours and the stop history.
  It needs no product data, no cycle time standard, and no output counter
  ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#metrics-light-up-independently)).
  That is why an Availability-only screen is a valid screen rather than a half-finished one.
- **The most expensive mistake in this module:** conflating two different subtractions. Planned downtime comes
  out of POT to give PBT. Unplanned downtime comes out of PBT to give APT. Planned downtime is **not**
  subtracted twice.
- **Who and where:** a supervisor walking the shop floor, small screen, one handed, unreliable network.
- **Timing matters:** the number is used to act before the shift ends, so a correct number that arrives after
  the shift closes does not solve the problem.

**3. Expectation**

- The Availability figure is large at the top. Beneath it, three lines show where it came from: POT, PBT, and
  APT in minutes.
- Showing all three is not decoration. Without them a supervisor cannot tell a line that stopped often from a
  line whose working hours were simply short.
- While the shift is still `open`, the figure is labelled provisional with the last update time.
- Readable within 3 seconds: the Availability figure and whether it is provisional or final.

| State | What the user sees |
|-------|--------------------|
| Default | The figure with POT, PBT and APT beneath, as above |
| Empty | No stop records yet and the shift just started: Availability is **partly uncomputable**, but POT already exists. Show POT and state that PBT and APT are waiting on stop records. Do not show 100%, because the absence of stop records is not evidence that there were no stops |
| No data | This work unit's `primary` asset has no tag with role `downtime_reason`, `machine_state`, or `runtime` (nor a `speed` or `activity_signal` tag bound through range detection, [F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#how-downtime-gets-detected-from-an-out-of-range-analog-value)), and there are no manual entries. Show "no data" with the reason in one sentence, for example "this line has no machine status source yet". **Never 0%** |
| Loading | A display skeleton. The old figure is not held on screen, so it cannot be misread as the new one |
| Error | The last successfully loaded figure stays, with a stale marker and its timestamp |
| Offline | An offline marker with the timestamp of the last data. Locally stored figures stay visible |
| No permission | Explain that this line is outside the role's scope, and name who to contact |

**4. Calculation**

*How Availability is worked out* ([F-07, ISO 22400-2 Clause 6 Table 9](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#availability-iso-clause-6-table-9))
- Availability is the share of the planned busy time (PBT) that the line actually spent producing (APT).
  Planned stops come out of the shift's scheduled time (POT) to give PBT; unplanned stops come out of PBT
  to give APT. Short stops come out of neither — they become a Performance loss.
- Example: 480 scheduled minutes, minus 60 planned and 42 unplanned → 378 of 420 minutes = **90.0%**
  (full walk-through below).

> [!note]- Exact formula (for developers)
> ```
> Availability = APT / PBT
> PBT          = POT − planned_dt
> APT          = PBT − unplanned_dt
> ```
>
> Where the inputs come from:
>
> - `POT` ← `SHIFT_INSTANCE` start to end, for the work unit and `business_date` being computed. POT is **not** a
>   tag and is never filled from a sensor.
> - `planned_dt` ← `Σ (ended_at − started_at)` over `ASSET_STATE_LOG` rows for that work unit's `primary` asset
>   whose `downtime_reason_id` points at a `DOWNTIME_REASON` with `downtime_category = planned`, and which overlap
>   the shift window.
> - `unplanned_dt` ← the same, but `downtime_category = unplanned`, **plus** every stop that has no
>   `downtime_reason_id` yet. An unlabelled stop is treated as unplanned until it is labelled, not discarded.
> - `small_stop` ← `downtime_category = small_stop` (for a detected stop: its duration between the two stop thresholds). **Subtracted from neither POT nor PBT.** It becomes a
>   Performance loss.

*Worked example*
1. A shift from 08:00 to 16:00 gives `POT = 480` minutes.
2. One planned "cleaning" stop of 60 minutes gives `PBT = 480 − 60 = 420`.
3. Three unplanned stops of 12, 18, and 12 minutes give `unplanned_dt = 42`, so `APT = 420 − 42 = 378`.
4. `Availability = 378 / 420 = 0.900`, that is **90.0%**.

*If the denominator is wrong*
- Using POT as the denominator gives `378 / 480 = 78.8%`, which is 11.2 percentage points lower, because the
  line is penalised for 60 minutes it was never scheduled to produce in.
- The opposite error also exists: storing PBT inside the shift clock, so planned downtime is never subtracted
  from anything.

*Edge cases*
- `PBT = 0`, for example a whole shift scheduled as planned downtime, makes the division invalid. Show "cannot
  compute" with the reason, not 0%.
- `planned_dt > POT` is a data anomaly, for example a planned stop running past the shift boundary. Mark it as
  an anomaly, do not silently clamp to 0.
- `APT < 0`, which happens when total stops exceed PBT, is also an anomaly. Do not clamp to 0 without a marker.
- A stop crossing midnight or a shift boundary counts only for the portion overlapping that shift window.
- A stop still open on an `open` shift counts up to now, and the figure must be labelled provisional.
- A `secondary` asset has no Availability of its own. Its `downtime_reason` tags may **enrich** a stop's label,
  but must not open a stop and must not determine whether a stop exists.

*Time period scope*
- The same formula also computes over an explicit `business_date` range, not only per `SHIFT_INSTANCE`.
- Sum `APT` and `PBT` across every `SHIFT_INSTANCE` in the range first, then divide (`sum_of_terms`, never an
  average of per-shift Availability). This is the identical rollup discipline rule 8 already uses for
  area/site aggregation ([F-07 — Time period scope](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#time-period-scope--adopted-no-new-modeling-needed)).
- No new field — this is the `KPI_RESULT.period` option that was already typed to allow it.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `SHIFT_INSTANCE`, `ASSET_STATE_LOG` (PRODUCTION-owned, [PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)), `DOWNTIME_REASON`, `ASSET_PLACEMENT`, `ASSET`, `ASSET_TAGS` (incl. `work_unit_id` for a multi-lane machine), `WORK_UNIT`, and `WORK_UNIT_KPI_BINDING` for the `availability.downtime_reason` slot ([F-07.1 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#94-work_unit_kpi_binding)) |
| **Writes** | `KPI_RESULT` (PK `kpi_result_id`; how `inputs_used` and `waterfall` are stored is `TBD`, K-12) ([F-07.1 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#95-kpi_result)) with `metric = availability`, `value`, `status`, `inputs_used` pointing at the `WORK_UNIT_KPI_BINDING` row(s) actually resolved, `is_provisional`. `period` is either a `shift_instance_id` or an explicit `business_date` range. Both use the same field; no schema change. No writes to any other FOUNDATION entity |
| **Not real fields: don't add** | No new fields. **`KPI_INDEX`/`KPI_FORMULA` as a per-plant-editable table does not exist (K-1)** ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#what-molca-does-not-seed-from-this)). `WORK_CENTER.is_oee_tracked` is not read or invented (see below) |

- **What the binding tells the calc job:** the `availability.downtime_reason` binding says which asset tag and
  which timing transform authoritatively opens/closes a stop for this work unit.
- **Four competing transforms:** per [F-07.1 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#92-transform-enum),
  four transforms compete for that same authoritative timing role — `none` (direct boolean tag),
  `reason_tag_drives_stop` (register bank), `stall_bucket` (stalled counter), or `range_bucket` (out-of-range
  analog) — and a work unit binds exactly one; `reason_match_enrich` only labels.
- **`range_bucket` watches a `speed` or an `activity_signal` tag** (2026-10-01, [F-07.1 range detection](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#how-downtime-gets-detected-from-an-out-of-range-analog-value),
  [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-10-01-open-items-wrap-up.md)): `activity_signal` is a value tag (motor current, voltage from a load sensor, vibration,
  pressure, unit on `ASSET_TAGS.uom_id`) for a machine with no counter and no speed signal. It shows whether the
  machine is working and feeds **Availability only**, through the band check, never Performance or Quality.
- **K-1 is about editability only:** K-1 is about formula **editability**, not about whether
  `KPI_FORMULA_SLOT`/`KPI_PARAMETERS`/`WORK_UNIT_KPI_BINDING`/`KPI_RESULT` exist. They do, seeded, per
  [F-07.1 §9](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#9-entity-specification), and this story
  reads/writes two of them.
- **`WORK_CENTER.is_oee_tracked`:** [F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md)
  mentions it "when used", but [F-01 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#95-work_center--production-line--process-cell--production-unit--storage-zone)
  has no such field. Treat the F-07.1 reference as stale; prefer F-01 (no field). This story does **not**
  invent or read that field. See also [`US-FND-SIT-001`](01-site-hierarchy.md#us-fnd-sit-001) /
  [`US-FND-KPI-004`](#us-fnd-kpi-004).

**6. Rules & constraints**

1. The Availability denominator is PBT. Using POT or the shift clock as the denominator is forbidden.
2. Planned downtime is subtracted once only, from POT.
3. `small_stop` never reduces PBT or APT.
4. An unlabelled stop counts as unplanned, not discarded.
5. Availability is computed only for work units with an active `primary` asset over that time range **and** a
   `WORK_UNIT_KPI_BINDING` row for `availability.downtime_reason`. Without either, the result is "no data" —
   never a fabricated 0% or a silently skipped work unit.
6. Asset placement is resolved at the event timestamp, not at calculation time.
7. Figures from an `open` shift are always labelled provisional. Final figures come only from `closed` shifts.
8. Aggregating to area or site is done by summing APT and PBT and then dividing, **not** by averaging
   Availability across lines. Averaging percentages gives equal weight to lines with very different working
   hours.
9. **Access:** `view` on a `KPI_RESULT` scoped at `work_unit` needs **work-unit access** to that work unit (or
   work-center/area/site/enterprise/tenant access). Work units outside the user's access do not appear in the
   list, rather than appearing and then being refused.
10. Offline: show the locally stored figure with its timestamp. Never show an empty figure.
11. When `period` is an explicit `business_date` range instead of one `shift_instance_id`, the same
    `sum_of_terms` rule as rule 8 applies — sum APT and PBT across every `SHIFT_INSTANCE` in the range, then
    divide, never average per-shift Availability.
12. **Multi-lane machine** ([F-07.1 primary vs secondary](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#multiple-assets-under-one-work-unit--primary-vs-secondary), 2026-09-25):
    the machine is `primary` on each of its lanes, and each lane is its own work unit with its own
    Availability. All lanes read the same machine-wide `machine_state`/`downtime_reason`/`runtime` tags, so one
    machine stop is downtime on **every** lane — each lane computed on its own, never one lane's downtime summed
    into another's. Rolling up to line/area sums the base terms per work unit as in rule 8.

**7. Acceptance criteria**

- **AC-1** **Given** a 480-minute shift with one 60-minute planned stop and three unplanned stops totalling 42
  minutes, **when** the calculation job runs, **then** Availability is recorded as 90.0% with PBT 420 minutes
  and APT 378 minutes.
- **AC-2 (denominator)** **Given** the same data, **when** the result is inspected, **then** the denominator
  used is 420 minutes and not 480.
- **AC-3 (small stop)** **Given** one additional stop classed `small_stop` lasting 5 minutes, **when**
  Availability is recomputed, **then** it stays 90.0% and those 5 minutes reduce neither PBT nor APT.
- **AC-4 (unlabelled stop)** **Given** one 10-minute stop with no `downtime_reason_id`, **when** Availability
  is computed, **then** those 10 minutes count as unplanned, so APT becomes 368 and Availability 87.6%.
- **AC-5 (no data)** **Given** the work unit's primary asset has no machine status tag and there are no manual
  entries, **when** the screen is opened, **then** "no data" appears with its reason, and **0% is not shown**.
- **AC-6 (edge case)** **Given** the entire shift is scheduled as planned downtime so PBT is 0, **when**
  Availability is computed, **then** "cannot compute" appears with its reason.
- **AC-7 (anomaly)** **Given** recorded stops exceed PBT, **when** Availability is computed, **then** the
  result is marked as an anomaly and not clamped to 0% without a marker.
- **AC-8 (placement)** **Given** the machine at this work unit was swapped on 2026-09-10, **when** Availability
  for the 2026-09-05 shift is computed, **then** the data read comes from the asset placed there on 2026-09-05,
  not the current one.
- **AC-9 (aggregation)** **Given** line A with PBT 420 and APT 378, and line B with PBT 120 and APT 60, **when**
  area Availability is computed, **then** the result is (378 + 60) / (420 + 120) = 81.1% and not the 70.0%
  average of 90.0% and 50.0%.
- **AC-10 (multi-lane)** **Given** a 2-lane machine placed on `S01` and `S02`, each with PBT 420 minutes, and
  one 10-minute unplanned machine stop, **when** Availability is computed, **then** each lane has 10 minutes of
  unplanned downtime, APT 410 and Availability 410 / 420 = 97.6%, and the line rollup is
  (410 + 410) / (420 + 420) = 97.6% with 20 downtime minutes across the two work units.
- **AC-10 (provisional)** **Given** the shift is still `open`, **when** the supervisor opens the screen,
  **then** the figure is labelled provisional with the last update time.
- **AC-11 (permission)** **Given** a user without work-unit access to this work unit, **when** they try to
  open it, **then** that line is not in their list.
- **AC-12 (no binding — the entity-existence guarantee)** **Given** a work unit has **no**
  `WORK_UNIT_KPI_BINDING` row for the `availability.downtime_reason` slot, **when** Availability is computed
  for that work unit, **then** the result reads `no_data`, **never** `0%` and never a fabricated value.
  This is the guarantee [F-07.1's "Metrics light up independently" table](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#metrics-light-up-independently)
  exists to provide, and it applies the same way to Performance ([`US-FND-KPI-002`](#us-fnd-kpi-002)) and
  Quality ([`US-FND-KPI-003`](#us-fnd-kpi-003)) against their own required slots.
- **AC-13 (time period scope)** **Given** a 7-day `business_date` range covering five `SHIFT_INSTANCE` rows
  for one work unit, with total PBT 2,940 minutes and total APT 2,646 minutes across those shifts, **when**
  Availability is computed for that date range instead of one shift, **then** the result is 2,646 / 2,940 =
  90.0%, computed from summed PBT/APT across the five shifts, not an average of five per-shift percentages.

**8. Metrics & events**

- **Metric:** `daily_availability_computed`, this PRD's primary metric. Definition, formula, baseline, and
  target are in [§3 Goal 1](README.md).
- **Counter-metrics:** `figures_shown_without_complete_input` must be 0, meaning no Availability may be
  displayed as a number when PBT or APT cannot be computed. And `shift_kpi_compute_time` p95 no more than 60
  seconds after the shift closes.
- **Events:** `availability_computed` (`work_unit_id`, `business_date`, `pot_minutes`, `pbt_minutes`,
  `apt_minutes`, `is_provisional`) · `availability_no_data` (`work_unit_id`, `missing_input`) ·
  `availability_anomaly` (`work_unit_id`, `anomaly_type`).

**Dependencies:** `US-FND-SHF-002`, `US-FND-AST-003`, `US-FND-REF-001`, `US-PROD-SHF-003`, `US-FND-KPI-007`

---

#### US-FND-KPI-002

**Compute Performance with per-SKU segmentation**

**Status:** 🟡 Partly blocked — segmentation by `product_code` tag waits on Q-21 (debounce). Job-based segmentation can be built now. Also needs APT from `US-FND-KPI-001` (B5), and on batch work units the split of a closed batch's output across shifts reads running time from `ASSET_STATE_LOG` (B5).

> **In short:** Performance compares what the line produced with what the standard cycle time allows in the
> time it actually ran (APT). Each product run is judged against its own standard, so a shift running two
> products is not judged against one averaged standard.

> Segmentation by `WORK_ORDER_OPERATION` can be built now; segmentation by `product_code` tag transitions **cannot**, because its debounce threshold is undefined and one noisy reading would split a run into two false segments ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#more-than-one-sku-in-the-same-period)).
> Build the job-based path first. See [Q-21](30-release-risks-questions.md).

**1. Story**

As a **production supervisor**, I want Performance computed per SKU run and then summed, so that a shift
running two products is not judged against one averaged standard.

**2. Context**

- **Why segments:** one cycle time for a whole period is only correct if one SKU ran for that whole period. A
  shift running several SKUs must be split per run, each segment using its own standard, then summed
  ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#more-than-one-sku-in-the-same-period)).
  Using one averaged standard produces a number that is arithmetically fine and means nothing, because it
  matches no SKU's performance.
- **Naming:** ISO calls this metric **Effectiveness**. The name MolcaDx uses is **Performance**, a naming
  decision already recorded in [F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#performance--iso-effectiveness-clause-6-table-10).
  The formula is unchanged, only the label differs.
- **Who and where:** a supervisor on the shop floor. A low Performance usually means the machine is running
  slowly or stopping very briefly and often, and those two need different responses from a long downtime.

**3. Expectation**

- The Performance figure, with a per-SKU segment breakdown beneath it: product name, segment duration, output,
  and the standard used.
- The segment breakdown is not an optional extra. Without it the supervisor sees one low number with no idea
  which SKU caused it.
- Readable within 3 seconds: the Performance figure and whether it is whole or partial.

| State | What the user sees |
|-------|--------------------|
| Default | The figure with the per-SKU segment breakdown, as above |
| Empty | No output recorded yet: Performance is "no data", not 0% |
| No data | No `OPERATION` matches that product and work unit pair. Show "no data" naming the product whose standard is missing, so the implementor knows what to fill in. Availability is **unaffected** and still shows |
| Cannot be calculated (no unit conversion) | The counter's unit differs from the operation's item unit and the product has no conversion path between them on the `business_date`. That segment shows "cannot be calculated", with the reason naming both units and the product ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#item-unit-of-pri-and-pq-po-2026-09-28)). Never 0%, never the unconverted count. In a multi-SKU period the segment is excluded and the total marked partial |
| Pending, batch open | Batch work unit (work center type `process_cell`) whose batch has not closed yet: earlier shifts show Performance as **"pending, batch open"** ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#which-standard-and-batch-work-units-po-2026-09-30-issue-58)); Availability shows normally. When the batch closes, those shifts are recalculated. No UX key exists for this text yet |
| Pending (no batch boundary) | Batch work unit with no job step and no `lot_marker` tag to mark the batch's start and end: the batch's Performance stays "pending" ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#which-standard-and-batch-work-units-po-2026-09-30-issue-58)) |
| Partial | One segment has a standard and another does not. Show Performance from the segments that have standards, mark the result partial, and name the segments that were not computed. A segment without a standard is **not** folded into the average of the others |
| Loading | Display skeleton |
| Error | The last figure with a stale marker |
| Offline | The locally stored figure with an offline marker |
| No permission | As in [`US-FND-KPI-001`](#us-fnd-kpi-001) |

**4. Calculation**

*How Performance is worked out* ([F-07, Clause 6 Table 10](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#performance--iso-effectiveness-clause-6-table-10))
- Performance is the ideal time the output should have taken, divided by the time the line actually ran
  (APT). Each product run is judged on its own standard cycle time, and the ideal times are added up before
  dividing (ISO calls this metric Effectiveness; MolcaDx calls it Performance).
- Example: two product runs, 166.67 and 150.00 minutes of ideal time over 378 minutes of APT → **83.8%**
  (full walk-through below).

> [!note]- Exact formula (for developers)
> ```
> Effectiveness = PRI × PQ / APT                       ISO shape
> Performance = Σᵢ (standard_i × output_i) / APT       the shape used when more than one SKU runs
> ```
>
> Where the inputs come from:
>
> - `APT` ← the result of [`US-FND-KPI-001`](#us-fnd-kpi-001). Performance uses APT, not PBT and not POT.
> - `standard_i` ← `OPERATION.cycle_time_value` for that segment's product at that work unit, the version
>   effective on the shift's `business_date`, per `OPERATION.uom_id` (the item unit the standard is per: pack,
>   carton, kg). On a class-based step, the station's override `OPERATION_WORK_UNIT.cycle_time_value` (valid on the
>   same `business_date`) is used when set, else the step's default. For continuous processes,
>   `OPERATION.standard_speed_value`. `OPERATION` is the only cycle-time
>   source (`PRODUCT_CYCLE` retired 2026-09-28, [`US-FND-PRO-004`](90-closed-stories.md#us-fnd-pro-004)).
> - `output_i` ← that segment's job quantity, or the `total` tag, or `good` plus `reject`, counted inside that
>   segment only. A counter's unit is `ASSET_TAGS.uom_id`. A counter with `ASSET_TAGS.count_side = input` is
>   converted to output with the step ratio and marked `derived`; a measured output count for the same work unit
>   and period always wins.
> - `batch_size` ← `OPERATION`; `count_basis` ← the counter tag's `ASSET_TAGS.count_basis`. Used when the counter
>   counts batches rather than units.
>
> Order of the ideal-time step for a discrete/batch segment ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#item-unit-of-pri-and-pq-po-2026-09-28), [F-08 §9.2.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#921-item-unit-and-conversion-po-2026-09-28)):
>
> ```
> count_basis = 'unit':   cycles = convert(sensor_count, ASSET_TAGS.uom_id → OPERATION.uom_id) / batch_size
> count_basis = 'cycle':  cycles = sensor_count            (a cycle has no item unit to convert)
> ideal_seconds (PRI × PQ) = cycles × cycle_time_value × TIME_CONVERSIONS.seconds_per_unit
> Performance = ideal_seconds / APT
> ```
>
> `convert` reads the product's `PRODUCT_UOM_CONVERSION` rows active on the `business_date`
> ([F-06 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#94-product_uom_conversion--fromto-uom-conversion-factor-scoped-to-one-product)): the same unit; one row read forward (×) or backward (÷); or two rows joined through
> `PRODUCT.base_uom_id`.

*When the counter counts in another unit than the standard* ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#item-unit-of-pri-and-pq-po-2026-09-28), 2026-09-28)
- The standard is per the operation's item unit. If the counter counts units in a different unit, the count is
  converted to the operation's unit first, through the product's conversions in force on the business date,
  then divided by the batch size. A counter that counts machine cycles skips the conversion.
- Example: WU_02 standard 216 s / carton, the counter reports 1,200 pack, product conversion 1 carton = 12 pack
  → 1,200 ÷ 12 = 100 carton → 100 × 216 s = 21,600 s ideal time. With APT = 25,200 s (7 h), Performance =
  21,600 / 25,200 = **85.7%**.
- Quality (good ÷ total) never converts units: all Quality tags of a work unit must share one unit, enforced when the tags are bound ([`US-FND-KPI-007`](#us-fnd-kpi-007), [F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#item-unit-of-pri-and-pq-po-2026-09-28), PO 2026-09-28).

*Which standard, and batch work units* ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#which-standard-and-batch-work-units-po-2026-09-30-issue-58), PO 2026-09-30, issue #58)
- **Standard as of the run date.** Performance uses the step's standard — cycle time or speed, and a station's
  override on a class-based step ([F-08 §9.2.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#922-operation_work_unit--which-stations-can-run-a-class-based-step-po-2026-09-30)) — valid on the shift's business date, not the value
  when the work order was released. A back-dated fix recalculates the affected past shifts; the editor warns, and the
  audit trail records it.
- **Batch or discrete** comes from the work center type: `process_cell` = batch, `production_line` = discrete.
- **Where a batch starts and ends:** the job step's start and close. On a process cell running without a job, the
  machine's `lot_marker` tag marks it instead: rising edge = start, falling edge = end
  ([F-04 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#97-asset_tags--per-asset-tag--signal-declaration)). With neither, the batch's Performance stays "pending".
- **A batch's output is known when it closes.** It is then split across the shifts and work units it ran on,
  weighted by **running time**, never by clock hours. Until the batch closes, earlier shifts show Performance as
  "pending, batch open"; Availability shows normally. When it closes, those shifts are recalculated.
- Example: a batch runs 3 h in shift 1 and 1 h in shift 2 (running time) and closes with 800 kg → 600 kg to
  shift 1, 200 kg to shift 2.
- **Input-side counters:** a counter on the material going **in** (for example dough fed to the mold) is converted
  to output with the step ratio and marked derived. A measured output count always wins over a derived one.

> [!note]- Exact formula (for developers)
> ```
> batch?            = WORK_CENTER.type = 'process_cell'         ('production_line' = discrete)
> batch start / end = the job step's (WORK_ORDER_OPERATION) start / close, else the lot_marker tag's
>                     rising / falling edge, else "pending"
> output_to_shift_s = batch_output × running_time_in_s / Σ running_time over all shifts and work units of the batch
>                     (running time from ASSET_STATE_LOG, never clock hours)
> standard          = OPERATION_WORK_UNIT.cycle_time_value (station override, if set) else OPERATION.cycle_time_value,
>                     the rows valid on SHIFT_INSTANCE.business_date
> output (input-side counter, ASSET_TAGS.count_side = 'input') = converted with the step ratio, marked derived;
>                     a measured output count for the same work unit and period wins
> ```
> Worked: 800 kg × 3 h / 4 h = 600 kg (shift 1); 800 kg × 1 h / 4 h = 200 kg (shift 2).

*How segments are found (first that works)*
1. `WORK_ORDER_OPERATION` start and end.
2. `product_code` tag transitions with debounce.
3. Nothing. If nothing works, Performance stays "no data" for that window. An averaged standard across unknown
   SKUs must not be used.

*Worked example* (APT 378 minutes, two SKUs running)
1. Segment 1, product `FG-1001`, 200 minutes, 20 seconds per piece, output 500 pieces. Ideal time
   `500 × 20 = 10,000` seconds = **166.67 minutes**.
2. Segment 2, product `FG-2002`, 178 minutes, 18 seconds per piece, output 500 pieces. Ideal time
   `500 × 18 = 9,000` seconds = **150.00 minutes**.
3. `Performance = (166.67 + 150.00) / 378 = 316.67 / 378 = 0.8377`, that is **83.8%**.

*If one averaged standard were used*
- At 19 seconds for 1,000 pieces, the result would be `19,000` seconds = 316.67 minutes. That happens to match
  here because both segments produced the same output.
- As soon as the output is unbalanced, say 800 pieces of `FG-1001` and 200 of `FG-2002`, the averaged standard
  still gives 316.67 minutes while the per-segment sum gives `(800 × 20 + 200 × 18) / 60 = 326.67` minutes, a
  2.6 percentage point difference in Performance.
- The gap grows with the difference in cycle time between SKUs.

*Edge cases*
- `APT = 0` makes the division invalid. Show "cannot compute", not 0%.
- `Performance > 100%` means output exceeded what the standard allows. Mark it a data anomaly, do not silently
  clamp to 100%. The usual causes are an out-of-date cycle time or a double-counting counter.
- Changeover time between the last unit of SKU A and the first of SKU B is **not** charged to either segment's
  ideal-time arithmetic. Until [Q-23](30-release-risks-questions.md) is answered, changeover minutes stay
  inside APT and inflate the Performance loss of whichever segment they fall in. This is a known distortion
  rather than a correct result, and must be noted on screen rather than hidden.
- A segment with no matching `OPERATION` is excluded from the sum and the result is marked partial. It appears
  as its own "no data" sub-range in the breakdown rather than being folded into the others' average.
- No conversion path between the counter's unit and `OPERATION.uom_id` on the `business_date` (row missing or
  closed) → that segment is "cannot be calculated", the reason naming both units and the product. Never 0%,
  never the raw, unconverted count. In a multi-SKU period the segment is excluded and the total marked partial,
  the same as a segment with no `OPERATION`.
- `small_stop` is a Performance loss, not an Availability loss. Its minutes stay inside APT and surface
  automatically as the gap between APT and ideal time.
- A batch still open at the end of a shift: that shift's Performance is "pending, batch open", never a figure from
  part of the output and never 0%. It is recalculated when the batch closes.
- A product-code change in the middle of a shift is attributed at the exact time it happens: the product-code
  mapping (`ASSET_PRODUCT_CODES`) is valid from/to a **timestamp** since 2026-09-30, not a date
  ([F-04 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#97-asset_tags--per-asset-tag--signal-declaration)).

*Time period scope*
- The same formula also computes over an explicit `business_date` range.
- Sum `APT` and each segment's ideal time (`standard_i × output_i`) across every `SHIFT_INSTANCE` in the range
  first, then divide (`sum_of_terms`, never an average of per-shift Performance). Same rollup discipline
  [`US-FND-KPI-001`](#us-fnd-kpi-001) uses for its own Time period scope ([F-07 — Time period scope](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#time-period-scope--adopted-no-new-modeling-needed)).
- Segments are still per-SKU-run within that wider window, not per shift.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `OPERATION` (`cycle_time_value`, `standard_speed_value`, `batch_size`, `uom_id`, `time_conversion_id`, `valid_from`, `valid_to`), `WORK_ORDER_OPERATION`, `PRODUCT` (`base_uom_id`), `PRODUCT_UOM_CONVERSION` (`product_id`, `from_uom_id`, `to_uom_id`, `conversion_value`, `valid_from`, `valid_to`) for the counter → item-unit conversion, `TIME_CONVERSIONS` (`seconds_per_unit`), `OPERATION_WORK_UNIT` (`operation_id`, `work_unit_id`, `cycle_time_value`, `valid_from`, `valid_to` — station override, [F-08 §9.2.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#922-operation_work_unit--which-stations-can-run-a-class-based-step-po-2026-09-30)), `WORK_CENTER` (`type` — batch vs discrete), `ASSET_PRODUCT_CODES` (`valid_from`/`valid_to` are timestamps since 2026-09-30), `ASSET_TAGS` (`uom_id`, `count_basis`, `count_side`, role `lot_marker` for batch boundaries; per-lane `total`/`product_code` filtered by `ASSET_TAGS.work_unit_id` on a multi-lane machine), `ASSET_PLACEMENT`, `SHIFT_INSTANCE`, `DOWNTIME_REASON` for `small_stop`, `ASSET_STATE_LOG` (PRODUCTION-owned, running time for the batch split), and `WORK_UNIT_KPI_BINDING` for the `performance.output` and `performance.product_code` slots ([F-07.1 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#94-work_unit_kpi_binding)) |
| **Writes** | `KPI_RESULT` ([F-07.1 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#95-kpi_result)) with `metric = performance`. `period` is either a `shift_instance_id` or an explicit `business_date` range, the same field as in [`US-FND-KPI-001`](#us-fnd-kpi-001); no schema change. No writes to any other FOUNDATION entity |
| **Not read** | `PRODUCT_CYCLE` — retired 2026-09-28 ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) C); it never was the Performance standard source |
| **Not real fields: don't add** | No new fields |

- **The binding decides:** which tag/transform fills output, and how the running product resolves, are picks
  recorded in `WORK_UNIT_KPI_BINDING`, not re-derived at run time.
- `KPI_FORMULA_SLOT`/`KPI_PARAMETERS`/`WORK_UNIT_KPI_BINDING`/`KPI_RESULT` are real, seeded
  [F-07.1 §9](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#9-entity-specification) entities —
  K-1 only rejects a per-plant-editable formula table, not these.

**6. Rules & constraints**

1. The Performance denominator is APT.
2. The standard comes from `OPERATION`, the version effective on the `business_date`. A standard updated today
   must not change last month's Performance.
3. The count is converted to `OPERATION.uom_id` before it is divided by `batch_size`, and only when the counter
   counts units (`count_basis = 'unit'`). Only conversion rows active on the `business_date` count. No path →
   "cannot be calculated" for that segment. (Until 2026-09-28 this rule said `PRODUCT_CYCLE` must not be read;
   that entity is retired.)
4. A period with more than one SKU must be segmented. One averaged standard is forbidden.
5. A segment without a standard is excluded and the result is marked partial.
6. Performance above 100% is marked an anomaly, not clamped.
7. Performance only appears when Availability is also computed, because it uses APT. Availability may appear
   without Performance.
8. Access and offline behaviour as in [`US-FND-KPI-001`](#us-fnd-kpi-001).
9. When `period` is an explicit `business_date` range, sum APT and every segment's ideal time across every
   `SHIFT_INSTANCE` in the range before dividing — never average per-shift Performance.
10. **Multi-lane machine:** output and running product come only from that lane's own per-lane tags
    (`ASSET_TAGS.work_unit_id` = this work unit). Lane 2's counter never adds to Lane 1's output (Step 0 above).
11. On a class-based step, the standard is the override of the station the job actually ran on, else the step's
    default; both read as of the `business_date`. A back-dated fix to a standard recalculates the affected past
    shifts, with a warning in the editor and an audit-trail row.
12. Batch vs discrete comes only from `WORK_CENTER.type` (`process_cell` = batch, `production_line` = discrete).
13. A batch's output is assigned only when the batch closes, split by running time from `ASSET_STATE_LOG`, never
    by clock hours. Until then the earlier shifts' Performance is "pending, batch open"; Availability is unaffected.
14. An input-side counter (`count_side = input`) is converted with the step ratio and marked derived; a measured
    output count for the same work unit and period always wins.

**7. Acceptance criteria**

- **AC-1** **Given** APT 378 minutes with the two SKU segments in the example above, **when** Performance is
  computed, **then** the result is 83.8% and the segment breakdown shows ideal times of 166.67 and 150.00
  minutes.
- **AC-2 (segmentation)** **Given** output of 800 pieces `FG-1001` and 200 pieces `FG-2002` in one shift,
  **when** Performance is computed, **then** the total ideal time is 326.67 minutes, the per-segment sum rather
  than one averaged standard.
- **AC-3 (effective standard)** **Given** `FG-1001`'s cycle time is changed from 20 to 22 seconds today,
  **when** last month's shift is recomputed, **then** it still uses 20 seconds.
- **AC-4 (no data)** **Given** `FG-2002` has no `OPERATION` at this work unit, **when** Performance is
  computed, **then** the `FG-2002` segment is marked no data, the result is marked partial, and Availability
  still displays normally.
- **AC-5 (unit conversion)** **Given** WU_02's standard is 216 s / carton, the product conversion 1 carton =
  12 pack is in force, the counter (counts units, batch size 1) reports 1,200 pack and APT is 25,200 s, **when**
  Performance is computed, **then** the ideal time is 21,600 s and Performance is 85.7%. (Replaces the earlier
  AC-5 on reference cycle times, retired with `PRODUCT_CYCLE` on 2026-09-28.)
- **AC-5b (no conversion)** **Given** the same segment but the carton ↔ pack conversion row was closed before the
  `business_date`, **when** Performance is computed, **then** the segment shows "cannot be calculated" naming
  carton, pack and the product, not 0% and not a figure from the raw 1,200.
- **AC-6 (edge case)** **Given** APT is 0 minutes, **when** Performance is computed, **then** "cannot compute"
  appears rather than 0%.
- **AC-7 (anomaly)** **Given** output exceeds the standard so the result is 108%, **when** the figure is
  displayed, **then** it is marked an anomaly and not clamped to 100%.
- **AC-8 (small stop)** **Given** 3 small stops totalling 6 minutes in the shift, **when** Performance is
  computed, **then** those 6 minutes stay inside APT and surface as part of the Performance loss rather than an
  Availability loss.
- **AC-9 (no segmentation)** **Given** no job exists and the `product_code` tag is unavailable, **when**
  Performance is computed, **then** the result is "no data" and no averaged standard is used.
- **AC-10 (permission)** **Given** a supervisor with no role on that line, **when** they try to open it,
  **then** that line is not in their list.
- **AC-11 (time period scope)** **Given** a 3-day `business_date` range covering three shifts with total APT
  1,134 minutes and total ideal time 950.0 minutes across all segments in that range, **when** Performance is
  computed for that date range, **then** the result is 950.0 / 1,134 = 83.8%, from summed APT and summed ideal
  time, not an average of three per-shift Performance figures.

- **AC-12 (batch split)** **Given** a batch on a `process_cell` work unit runs 3 h in shift 1 and 1 h in shift 2
  (running time) and closes with 800 kg, **when** Performance is computed, **then** 600 kg is counted in shift 1 and
  200 kg in shift 2.
- **AC-13 (batch open)** **Given** the same batch has not closed when shift 1 ends, **when** shift 1 is shown,
  **then** its Performance reads "pending, batch open" and its Availability shows normally; **when** the batch
  closes, **then** shift 1 is recalculated.
- **AC-14 (no batch boundary)** **Given** a process cell running without a job and with no `lot_marker` tag,
  **when** Performance is computed, **then** that batch's Performance stays "pending".
- **AC-15 (station override)** **Given** step "Mix", class Mixer, default 600 s / batch, and Mixer 2's override
  540 s, **when** a batch on Mixer 1 and a batch on Mixer 2 are computed, **then** they are measured against 600 s
  and 540 s.
- **AC-16 (input-side counter)** **Given** a work unit with both an input-side counter and a measured output counter
  for the same period, **when** Performance is computed, **then** the measured output count is used; **given** only
  the input-side counter, **then** its count is converted with the step ratio and marked derived.

**8. Metrics & events**

- **Metric:** `performance_computed`, the share of the pilot line's `closed` shifts whose Performance is
  computed in full, neither partial nor "no data". Baseline 0% on 2026-08-24. Target 90% by 30 days after
  release. Source: the calculation job's output table.
- **Counter-metrics:** the share of Performance results marked partial. A high number means `OPERATION` is
  incomplete, which is a master data problem rather than a calculation problem. And
  `figures_shown_without_complete_input` must be 0.
- **Events:** `performance_computed` (`work_unit_id`, `business_date`, `segment_count`, `apt_minutes`,
  `ideal_minutes`, `is_partial`) · `performance_segment_no_standard` (`work_unit_id`, `product_id`) ·
  `performance_anomaly` (`work_unit_id`, `value`, `anomaly_type`).

**Dependencies:** `US-FND-KPI-001`, `US-FND-PRO-001`, `US-FND-PRO-003`, `US-FND-WMS-003`, `US-PROD-SHF-001`, `US-FND-KPI-007`

---

#### US-FND-KPI-003

**Compute Quality from whichever input pair exists**

**Status:** 🟢 Ready — the four counter paths are ready (interim rules cover Q-24 and the two-station path). Q-28 resolved 2026-10-01 (PO): a manual reject, entered in any unit with a conversion path, is converted to the step's unit and may be paired with the machine's counted total.

> **In short:** Quality is the share of output that was good. The formula never changes; the system fills it
> from whichever pair of counters the line really has, and says "no data" rather than inventing the missing
> side.

**1. Story**

As a **production supervisor**, I want Quality computed from the pair of figures my line actually has, so that
a line without a good counter still gets a Quality number without inventing the missing side.

**2. Context**

- **What changes is the fill, not the formula:** the Quality formula never changes: good quantity divided by
  total quantity. What changes is **how those two figures are filled**, because not every line has all three
  counters ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#quality)).
- A line with only total and reject can still compute Quality. A line with only one of the three cannot, and
  that must be stated as "no data" rather than as 100%.
- **The trap:** mixing a job total with a PLC reject in the same ratio produces a number nobody can account
  for, because the two count different populations at different times. The pair must come from one source.
- **Who and where:** a supervisor on the shop floor. ISO notes Quality as the indicator most directly usable at
  operator level.

**3. Expectation**

- The Quality figure with its three supporting numbers: good quantity, total quantity, and reject quantity.
- Plus one line stating **which pair filled it**, for example "from the total and reject counters". That
  provenance line matters because two lines showing the same Quality figure can have very different levels of
  trustworthiness.
- Readable within 3 seconds: the Quality figure and which pair filled it.

| State | What the user sees |
|-------|--------------------|
| Default | The figure, its three supporting numbers and the provenance line, as above |
| Empty | No output at all: "no data". Not 100%, and not 0% |
| Cannot compute | `total = 0` while other records exist. Show "cannot compute" with its reason |
| No data | Only one of the three figures is available. State which are missing |
| Anomaly | All three figures exist but good plus reject does not equal total. Show an anomaly marker with all three numbers, and **do not** silently overwrite the total |
| Loading | Display skeleton |
| Error | The last figure with a stale marker |
| Offline | The locally stored figure with an offline marker |
| No permission | As in [`US-FND-KPI-001`](#us-fnd-kpi-001) |

**4. Calculation**

*How Quality is worked out* ([F-07, ISO 22400-2 Clause 6 Table 11](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#quality-iso-quality-ratio-clause-6-table-11))
- Quality is the share of the total output that was good. The formula never changes; what changes is which
  pair of counters fills good and total (the fill order is listed below).
- Example: total 1,000 and 50 rejects, with no good counter → good = 950 → Quality = **95.0%** (full
  walk-through below).

> [!note]- Exact formula (for developers)
> ```
> Quality = GQ / PQ          good quantity over total quantity, provided the total is greater than 0
> ```
>
> Where the inputs come from:
>
> - `good` ← `good_qty` on the job row, or a tag with role `good` on an asset at that work unit.
> - `total` ← the job total, or a tag with role `total`.
> - `Σ reject` ← every tag row with role `reject` for that asset in the same unit, each with its
>   `reject_reason_id`.
> - For job quantities the relationship is `total_output = good + scrap + rework`. Do not add the job's
>   `rework_qty` on top of `Σ reject` from tags for the same units, because that double counts.

*Pair fill order (first that works)*

1. `good` and `total` from the same source, either tags or one job row. Use both. `reject` is then only a
   Pareto breakdown.
2. `total` and `reject`, no `good`. Then `good = total − Σ reject`, provided `total > Σ reject`.
3. The machine's `total` tag (or the job total) and a **manual reject entry**, no `good` (PO 2026-10-01, Q-28).
   Then `good = total − Σ reject`, each manual reject row first converted to the step's unit (see *Reject entered
   manually* below). The result is marked as coming from manual input (`source_tier = manual`, the lowest tier).
4. `good` and `reject`, no `total`. Then `total = good + Σ reject`.
5. Only one of the three. The result is "no data". The missing side must not be invented.

Mixing sources within one pair is forbidden, for example a job total with a PLC reject **tag**, or a `good` tag with
a job scrap: they count the same units from two systems and double-count. Manual reject entry is **not** a PLC
reject tag, so path 3 is allowed ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#quality)).

*Worked example*
1. The example shift produces `total = 1,000` pieces and `Σ reject = 50` pieces, with no good counter.
2. Path 2 applies, so `good = 1,000 − 50 = 950`.
3. `Quality = 950 / 1,000 = 0.950`, that is **95.0%**.
4. Reject Pareto breakdown: 30 for weight out of range, 15 for a loose seal, 5 for a skewed label, that is 60%,
   30%, and 10% of rejects. The breakdown does not change the 95.0%.

*Two-station derivation*
- This is not a different Quality formula. It is another way to **fill reject** on one work unit
  ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#two-stations-total-of-one-machine-minus-total-of-another)).
  Subtract infeed from outfeed, not the higher-numbered machine from the lower-numbered one.
- If machine 1 is **upstream** and machine 2 counts what got through, the rejected count is the upstream
  count minus the downstream count, floored at zero (the recommended default):

> [!note]- Exact formula (for developers)
> ```
> reject_1 = max(0, total_1 − total_2)
> ```

- **Worked example:** the upstream filler counts 1,000 and the downstream checkweigher counts 950, so
  `reject_1 = max(0, 1,000 − 950) = 50`, and machine 1's Quality is 950 / 1,000 = 95.0%.
- `total_2 − total_1` with machine 1 upstream is usually negative, because the upstream count is greater than or
  equal to the downstream count. A negative value must never be treated as extra good quantity.
- **When it is valid:** only for one-piece or tight flow, the same unit, one next station, no rework loop, and
  comparable counts for the same `SHIFT_INSTANCE`. With a WIP buffer, or scrap pulled before the next counter,
  or three work units sharing one asset, this path **must not be used**.
- **Status:** pending Operations. Until then, use the machine's own `good` or `reject`, or state "no data".
- This derived reject feeds **machine 1's** Quality and waterfall only, never machine 2's OEE.

*Reject entered manually, in any unit* ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#reject-entered-manually-in-any-unit--convert-to-the-step-unit-and-time-po-2026-09-30-2026-10-01), PO 2026-09-30 and 2026-10-01, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-line-across-work-centers-reject-weight.md))
- Some work units have **no reject sensor**: the operator enters the reject per work unit, in PRODUCTION's reject
  entry ([PRODUCTION Planning](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#work_order_operation_defect--one-row--one-code--one-fate-on-one-operation)).
- **The unit depends on the SKU and the site:** one SKU's reject is weighed (kg), another is counted (pcs, punch).
  The operator picks the unit; it is stored on the reject row (`WORK_ORDER_OPERATION_DEFECT.uom_id`). No new field.
- Before Quality is calculated, the reject is brought to the step's unit (`OPERATION.uom_id`):

| Entered unit | Conversion |
|---|---|
| Same as the step's unit (e.g. pieces at a step counted in pieces) | None |
| Another count or pack unit of the product | The product's unit conversion active on `business_date` — the same path Performance uses for PQ |
| Weight (kg) | Weight of one unit at this step — below |

- A weight is converted with numbers the step already has: first to units at **this** step, then to time.
- **Weight of one unit at this step** is derived from the product's standard weight and the planned shrinkage of the
  later steps ([F-08 line across work centers](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#a-line-across-several-work-centers--biscuit-example-po-2026-09-30)). For a count unit such as `punch`, it is the declared
  weight of one unit (`1 punch = 20 g`). Wet before the shrinkage step, dry after it: each step uses its own weight,
  so wet and dry never mix.
- **Quality at that work unit** uses the converted units, so good and total stay in one unit (the step's
  `OPERATION.uom_id`).
- Example (biscuit line, example data): Mold, wet, counted in punches: 2 kg ÷ 20 g = **100 punches** → × 1 s =
  **100 s lost**. Oven, dry, counted in pieces: 0.5 kg ÷ 0.96 g = **521 pcs** → × the oven's time per piece (e.g.
  0.05 s) = **26 s lost**. The Mold counted 3,600 punches, reject = 100 punches → Quality = (3,600 − 100) ÷ 3,600 =
  **97.2%** (path 3: machine total + manual reject, PO 2026-10-01).
- Example (count unit, example data): a step counted in pieces, product conversion 1 pack = 10 pcs. The operator
  enters 5 packs → 50 pcs. The machine counted 2,000 pcs → Quality = (2,000 − 50) ÷ 2,000 = **97.5%**.

> [!note]- Exact formula (for developers)
> ```
> reject units = reject weight ÷ weight of one unit at THIS step
> reject time  = reject units × cycle time of THIS step (per unit; ÷ batch_size when the standard is per cycle)
>
> weight of one unit leaving step k = PRODUCT_DETAIL.standard_weight ÷ Π (1 − planned_shrinkage of each later step)
> ```
>
> Where the inputs come from:
>
> - `reject weight` (or any entered reject) ← `WORK_ORDER_OPERATION_DEFECT.qty` in `WORK_ORDER_OPERATION_DEFECT.uom_id`
>   (PRODUCTION; `uom_id` added 2026-10-01).
> - A count/pack unit other than `OPERATION.uom_id` ← converted through `PRODUCT_UOM_CONVERSION` active on
>   `business_date` (forward ×, backward ÷, or two rows joined through `PRODUCT.base_uom_id`), the same path as PQ in
>   [F-07 item unit of PRI and PQ](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#item-unit-of-pri-and-pq-po-2026-09-28).
>
> ```
> good = total − Σ reject (each WORK_ORDER_OPERATION_DEFECT row converted from its uom_id to OPERATION.uom_id)
> KPI_RESULT.source_tier = manual     when any input is a manual reject entry
> ```
> - `standard_weight` ← `PRODUCT_DETAIL.standard_weight`, in `PRODUCT_DETAIL.weight_uom_id` (a mass unit). For a count
>   unit such as `punch`, the weight of one unit is a product conversion (`PRODUCT_UOM_CONVERSION`, e.g.
>   `1 punch = 20 g`).
> - `planned_shrinkage` ← `OPERATION.planned_shrinkage` of each later step.
> - `cycle time of THIS step` ← that step's `OPERATION` standard (`cycle_time_value`, `batch_size`), in the same unit.

Edge cases of the manual reject path (copied from F-07):

| Case | Result |
|---|---|
| No weight of one unit at that step (no `standard_weight`, or no weight for the count unit) | Reject stored in kg; Quality "cannot be calculated", reason "Weight per unit missing" — never a guessed number |
| Entered in a unit with no conversion path to `OPERATION.uom_id` for that product on `business_date` | Reject stored as entered; Quality "cannot be calculated", reason "No conversion for this unit" naming both units and the product — never a guessed number |
| No cycle time on the step | Reject units shown, reject time "cannot be calculated", reason "Cycle time missing" |
| Converted reject > the step's counted output for the period | Entry refused: "Reject is more than this shift's output" |
| No output count at all on that work unit (reject known, total unknown) | Reject time is exact; Quality ratio is shown only as **estimated** (total ≈ running time ÷ cycle time, which assumes full speed and overstates Quality), labelled as such |
| Entered after the shift closed | Accepted; that shift's KPIs are recalculated, same as other late corrections |

The quoted reasons and the refusal message have no UX 07 key yet.

*Rejects across work units add up in time, never in weight* ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#rejects-across-work-units-add-up-in-time-never-in-weight-po-2026-09-30), PO 2026-09-30)
- 1 kg of wet dough and 1 kg of baked biscuit are not the same amount of product. When the rejects of several work
  units are combined (a line, a work center, a site), each is first converted to **reject time** at its own step,
  and the times are added. Adding kg across steps is not allowed.
- This follows the rule that roll-ups add base terms, never averages.

*Edge cases*
- `total = 0` makes the division invalid. Show "cannot compute", not 0% and not 100%.
- `Σ reject > total` on path 2 is an anomaly. Do not produce a negative good quantity.
- All three figures present with `good + Σ reject` not equal to `total` within a small count tolerance is an
  anomaly. That tolerance is undefined. See [Q-24](30-release-risks-questions.md). Until it is set, treat a zero
  difference as the only match and mark everything else an anomaly.
- A reject with no `reject_reason_id` still counts in `Σ reject` and still lowers Quality. It appears in the
  Pareto as "no reason given". Dropping it would raise Quality falsely.
- **Quality calculates in one unit: the step's `OPERATION.uom_id`** (PO 2026-10-01, Q-28, [F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#item-unit-of-pri-and-pq-po-2026-09-28), [F-07.1 Quality](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#quality)).
  Every input is brought to that unit before good and total are formed. Two separate rules apply:
  - **PLC tags:** every tag bound to a work unit's `quality.good` / `quality.total` / `quality.reject` slots,
    including a `second_asset_tag_id`, still shares one `ASSET_TAGS.uom_id`. That is **enforced when the tags are
    bound** ([`US-FND-KPI-007`](#us-fnd-kpi-007), [F-07.1 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#94-work_unit_kpi_binding)): a binding in mixed units cannot be saved. This catches
    wiring mistakes; it is not a conversion rule.
  - **Manual reject entry** may be in any unit with a conversion path to `OPERATION.uom_id` for that product, and is
    converted before Quality (*Reject entered manually* above).

*Time period scope*
- The same formula also computes over an explicit `business_date` range.
- Sum `good` and `total` across every `SHIFT_INSTANCE` in the range first, then divide (`sum_of_terms`, never
  an average of per-shift Quality). Same rollup discipline [`US-FND-KPI-001`](#us-fnd-kpi-001) uses for its own
  Time period scope ([F-07 — Time period scope](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#time-period-scope--adopted-no-new-modeling-needed)).
- Each shift in the range still resolves its own pair-fill path independently before the sum.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `ASSET_TAGS` with roles `good`, `total`, `reject`, `reject_reason` (on a multi-lane machine only those with `ASSET_TAGS.work_unit_id` = this work unit), `WORK_ORDER_OPERATION` for job quantities, `REJECT_REASON` for the Pareto, `ASSET_PLACEMENT`, `WORK_UNIT_FLOW` for resolving the neighbouring station on the two-station path, `SHIFT_INSTANCE`, and `WORK_UNIT_KPI_BINDING` for the `quality.good`/`quality.total`/`quality.reject` slots ([F-07.1 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#94-work_unit_kpi_binding)) |
| **Writes** | `KPI_RESULT` ([F-07.1 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#95-kpi_result)) with `metric = quality`, `is_anomaly` set when `reconcile_check` fires a mismatch. `period` is either a `shift_instance_id` or an explicit `business_date` range, the same field as in [`US-FND-KPI-001`](#us-fnd-kpi-001); no schema change. No writes to any other FOUNDATION entity |
| **Reads (manual reject)** | `WORK_ORDER_OPERATION_DEFECT` (`qty`, `uom_id` — PRODUCTION, [Planning](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#work_order_operation_defect--one-row--one-code--one-fate-on-one-operation)), `PRODUCT_DETAIL` (`standard_weight`, `weight_uom_id`), `OPERATION` (`planned_shrinkage`, `cycle_time_value`, `batch_size`, `uom_id`), `PRODUCT` (`base_uom_id`), `PRODUCT_UOM_CONVERSION` (`from_uom_id`, `to_uom_id`, `conversion_value`, `valid_from`, `valid_to`) — to bring a manual reject to `OPERATION.uom_id`, and for the declared weight of a count unit (e.g. `1 punch = 20 g`) |
| **Not read for Quality** | `runtime`, `machine_state`, `downtime_reason`, `speed`, `activity_signal`. Those tag roles are forbidden inputs to Quality. PLC tag values are never converted: bound Quality tags share one unit (PO 2026-09-28) |
| **Not real fields: don't add** | No new fields |

- **The binding decides:** which pair-fill order (`none` / `complement_derive` / `infeed_outfeed_derive` /
  `reconcile_check`) applies to this work unit is the pick recorded in `WORK_UNIT_KPI_BINDING`.

**6. Rules & constraints**

1. The formula stays good over total. Only the way the two are filled may differ.
2. One pair must come from one source. Mixing a job total with a PLC reject tag, or a `good` tag with a job scrap,
   in one ratio is forbidden. A machine (or job) total with a **manual reject entry** is allowed (path 3, PO
   2026-10-01) and marks the result `source_tier = manual`.
3. Only one of the three figures means "no data". The missing side is not invented.
4. A mismatch between the three figures produces an anomaly marker, not a silent overwrite of the total.
5. Rejects with no reason still count.
6. The two-station path subtracts infeed from outfeed, and a negative result never becomes extra good quantity.
7. The two-station path must not be used with a WIP buffer, scrap pulled between counters, or three work units
   sharing one asset.
8. Quality may appear without Availability and without Performance, because its inputs stand alone.
9. Access and offline behaviour as in [`US-FND-KPI-001`](#us-fnd-kpi-001).
10. When `period` is an explicit `business_date` range, sum `good` and `total` across every `SHIFT_INSTANCE` in
    the range before dividing — never average per-shift Quality.
11. **Multi-lane machine:** `good`/`total`/`reject`/`reject_reason` are per-lane roles — each counts only in the
    work unit named in its `ASSET_TAGS.work_unit_id`, never in every lane. A per-lane tag with no work unit is
    not read (Step 0 above).
12. **Reject entered manually, in any unit:** brought to the step's `OPERATION.uom_id` first — no conversion for the
    same unit, the product's unit conversion for another count/pack unit, the weight of one unit at **that** step
    for kg — then to time with that step's cycle time. A missing weight per unit gives "cannot be calculated"
    (reason "Weight per unit missing"); a unit with no conversion path gives "cannot be calculated" (reason "No
    conversion for this unit", naming both units and the product). Never a guessed number. A converted reject
    larger than the step's counted output for the period is refused. It may be paired with a counted total
    (rule 2).
13. **Rejects of several work units are combined in time, never in kg:** each is converted to reject time at its own
    step first, then the times are added.

**7. Acceptance criteria**

- **AC-1** **Given** the line has a total counter of 1,000 and a reject counter of 50 with no good counter,
  **when** Quality is computed, **then** the result is 95.0% and the screen states the pair was filled from
  total and reject.
- **AC-2 (path 3)** **Given** the line has a good counter of 950 and a reject counter of 50 with no total
  counter, **when** Quality is computed, **then** total is filled as 1,000 and the result is 95.0%.
- **AC-3 (path 4)** **Given** the line has only a total counter, **when** Quality is computed, **then** the
  result is "no data" with a statement that both good and reject are unavailable.
- **AC-4 (anomaly)** **Given** all three figures exist with good 940, reject 50, and total 1,000, **when**
  Quality is computed, **then** the result is marked an anomaly with all three numbers shown and the total not
  overwritten.
- **AC-5 (edge case)** **Given** total is 0 while other records exist for that shift, **when** Quality is
  computed, **then** "cannot compute" appears rather than 100%.
- **AC-6 (reject exceeds total)** **Given** total 100 and Σ reject 120, **when** Quality is computed on path 2,
  **then** the result is marked an anomaly and the good quantity is never negative.
- **AC-7 (no source mixing)** **Given** the total is available from a job and the reject from a PLC reject tag,
  **when** Quality is computed, **then** those two sources are not paired, and the system either picks one intact
  source or states "no data".
- **AC-8 (two stations)** **Given** the upstream filler counts 1,000 and the downstream checkweigher counts 950
  on the same shift in the same unit, **when** the filler's reject is derived, **then** it is 50 and the
  filler's Quality is 95.0%.
- **AC-9 (two stations negative)** **Given** the downstream count exceeds the upstream count, **when** reject
  is derived, **then** the result is 0 and the negative difference does not become extra good quantity.
- **AC-10 (unreasoned reject)** **Given** 10 of the 50 rejects have no reason, **when** Quality and the Pareto
  are displayed, **then** Quality stays 95.0% and those 10 appear as "no reason given".
- **AC-11 (PLC tags, no conversion)** **Given** the good and total tags bound to a work unit are both in pieces,
  **when** Quality is computed, **then** good ÷ total is used as read, with no unit conversion step. A good tag in
  pieces and a total tag in kg on one work unit cannot occur: that binding is refused at save
  ([`US-FND-KPI-007`](#us-fnd-kpi-007) AC-12).
- **AC-12 (permission)** **Given** a supervisor with no role on that line, **when** they try to open it,
  **then** that line is not in their list.
- **AC-14 (weight → units → time)** **Given** the Mold step counts in punches with `1 punch = 20 g` and a cycle time
  of 1 s / punch, **when** the operator enters a 2 kg reject, **then** it converts to 100 punches and 100 s lost.
- **AC-15 (weight per unit missing)** **Given** a step with no weight of one unit, **when** a reject is entered in kg,
  **then** the reject is stored in kg and Quality reads "cannot be calculated", reason "Weight per unit missing".
- **AC-16 (reject over output)** **Given** the converted reject is larger than the step's counted output for the
  shift, **when** it is entered, **then** the entry is refused with "Reject is more than this shift's output".
- **AC-17 (roll-up in time)** **Given** 100 s of reject time at the Mold and 26 s at the Oven, **when** the line's
  rejects are combined, **then** the result is 126 s, and the 2 kg and 0.5 kg are never added.
- **AC-18 (machine total + manual reject)** **Given** the Mold counted 3,600 punches and a manual reject of 100
  punches, **when** Quality is computed, **then** the result is 97.2% and `KPI_RESULT.source_tier = manual`.
- **AC-19 (manual reject in a pack unit)** **Given** a step counted in pieces, the product conversion 1 pack = 10 pcs
  active on the shift's `business_date`, and a machine total of 2,000 pcs, **when** the operator enters a reject of
  5 packs, **then** it is converted to 50 pcs and Quality is 97.5%.
- **AC-20 (no conversion path)** **Given** a reject entered in a unit with no conversion path to the step's unit for
  that product on `business_date`, **when** Quality is computed, **then** the reject is stored as entered and Quality
  reads "cannot be calculated", reason "No conversion for this unit" naming both units and the product.
- **AC-13 (time period scope)** **Given** a 5-day `business_date` range covering five shifts with total good
  4,750 and total total 5,000 across those shifts, **when** Quality is computed for that date range, **then**
  the result is 4,750 / 5,000 = 95.0%, from summed good and summed total, not an average of five per-shift
  Quality figures.

**8. Metrics & events**

- **Metric:** `quality_computed`, the share of the pilot line's `closed` shifts whose Quality is computed.
  Baseline 0% on 2026-08-24. Target 90% by 30 days after release. Source: the calculation job's output table.
- **Counter-metrics:** the share of Quality results marked anomalous. A high number means a stuck or
  double-counting counter, which is an acquisition problem rather than a formula problem. And
  `figures_shown_without_complete_input` must be 0.
- **Events:** `quality_computed` (`work_unit_id`, `business_date`, `fill_path`, `good_qty`, `total_qty`,
  `reject_qty`) · `quality_anomaly` (`work_unit_id`, `good_qty`, `reject_qty`, `total_qty`) ·
  `quality_no_data` (`work_unit_id`, `available_input`).

**Dependencies:** `US-PROD-SHF-001`, `US-FND-REF-002`, `US-FND-KPI-007`

---

#### US-FND-KPI-004

**Compute OEE and the loss waterfall from PBT**

**Status:** 🔴 Blocked — needs A/P/Q from `US-FND-KPI-001` to `003`, and reads `ASSET_STATE_LOG` directly (B5).

> **In short:** OEE (overall equipment effectiveness) is Availability × Performance × Quality. It appears only
> when all three exist, and always with a waterfall showing how many minutes were lost at each step, so one
> number never hides where the time went.

**1. Story**

As a **production supervisor**, I want OEE to appear only when all three of its components are computed,
together with the minutes lost at each step, so that one composite number does not hide where the time went.

**2. Context**

- **Risk (B5, see [`US-FND-KPI-001`](#us-fnd-kpi-001)):** this story reads `SHIFT_INSTANCE` and
  `ASSET_STATE_LOG` directly for the waterfall's step-1 breakdown. `ASSET_STATE_LOG` is PRODUCTION's (since
  2026-09-28) and its entity spec is still a draft with `TBD` fields, so the same gap applies here.
- **No data source of its own:** OEE is the product of three other metrics, and it exists only when all three
  are computed for the same work unit and period ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#oee)).
  A missing component must not be treated as zero, because zero is a statement about performance while absent
  data is a statement about data.
- **Two mandatory display rules:** first, OEE never appears alone without its three components. Second, OEE
  always appears with the loss waterfall for the same period.
- **Why the waterfall is always possible:** OEE is defined from loss types rather than from a fourth sensor. If
  A, P, and Q are computed, all three loss steps are computed too.
- **Where the waterfall starts:** at PBT, not at the shift clock. The shift clock is POT, and planned downtime
  is the bar before PBT rather than one of the OEE factors.
- **Who and where:** read by a supervisor on the shop floor, and used by managers to compare periods. ISO warns
  that comparing OEE across hierarchy levels is only useful when the process characteristics are comparable.

**3. Expectation**

- The OEE figure large at the top, its three components A, P, and Q beneath, then the loss waterfall in
  minutes and percent.
- While the shift is `open`, all of it is labelled provisional with the last update time.
- Readable within 3 seconds: the OEE figure, or if OEE is absent, which component is blocking it.

| State | What the user sees |
|-------|--------------------|
| Default | OEE, A/P/Q and the waterfall, as above |
| Empty | No entries at all: "no data", not 0% |
| One component missing | OEE is **not displayed at all**. What is displayed is whichever components are computed, plus a statement of which component is missing and what is needed to complete it. An Availability-only screen is a valid result, and that screen has **no** OEE waterfall, because the Performance and Quality losses do not exist yet |
| Loading | Display skeleton, old figures not held on screen |
| Error | The last successfully loaded figures with a stale marker |
| Offline | Locally stored figures with an offline marker and the last data timestamp |
| No permission | Explain that this line is outside the role's scope and name who to contact |

**4. Calculation**

*How OEE is worked out* ([F-07, ISO 22400-2 Clause 6 Table 7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#oee-iso-overall-equipment-effectiveness-index-clause-6-table-7))
- OEE multiplies the three components: Availability × Performance × Quality. It exists only when all three
  are computed for the same line and period.
- Example: 90.0% × 83.8% × 95.0% → **71.6%** (full walk-through below).

> [!note]- Exact formula (for developers)
> ```
> OEE = Availability × Performance × Quality
> ```
> ISO names it `Availability × Effectiveness × Quality ratio`, and Effectiveness is Performance in MolcaDx
> naming.
>
> Where the inputs come from:
>
> - All three come from [`US-FND-KPI-001`](#us-fnd-kpi-001), [`US-FND-KPI-002`](#us-fnd-kpi-002), and
>   [`US-FND-KPI-003`](#us-fnd-kpi-003), for the same work unit and the same period.
> - There is no tag and no tag role named `oee`.

*Worked example*
1. A = 90.0%, P = 83.8%, Q = 95.0%.
2. `OEE = 0.900 × 0.8377 × 0.950 = 0.7163`, that is **71.6%**.

*The waterfall, starting at PBT.* Each step names its loss type, its minutes, and what remains:

| Step | Loss type | Minutes | What remains |
|------|-----------|---------|--------------|
| −1 | the shift clock. Scheduled, not an OEE factor | — | `POT` **480** |
| 0 | planned downtime removed | −60 | `PBT` **420** |
| 1 | **Availability** | −42 | `APT` **378** |
| 2 | **Performance** | −61.33 | net run time **316.67** |
| 3 | **Quality** | −15.83 | good run time **300.83** |
| 4 | **OEE** | — | fully productive **300.83** |

*How each bar is computed*
- Each bar is one loss: the unplanned stop minutes (step 1), the gap between actual and ideal run time
  (step 2), the not-good share of the net run time (step 3), and a closing check (step 4).

> [!note]- Exact formula (for developers)
> ```
> Step 1 = total unplanned downtime                   = 42 minutes
> Step 2 = APT − ideal_time         = 378 − 316.67      = 61.33 minutes
> Step 3 = net run time × (1 − Q)   = 316.67 × (1 − 0.950) = 15.83 minutes
> Step 4 = OEE × PBT                = 0.7163 × 420       = 300.83 minutes
> ```

- Step 1 is total unplanned downtime, 42 minutes.
- Step 2: `ideal_time` is the per-SKU segment sum from [`US-FND-KPI-002`](#us-fnd-kpi-002). `small_stop`
  minutes are included in it.
- Step 3 is the not-good share of output times the net run time.
- Step 4 is a check. This figure **must** equal the result of step 3. If it does not, one of the bars is wrong
  and the result must not be displayed as a valid waterfall.

*Edge cases*
- If any of A, P, or Q is not computed, OEE does not exist. Do not multiply by 0 and do not multiply by 1.
- Planned downtime is not an OEE bar. It sits on the POT to PBT segment, and showing it is useful for the
  whole-shift story, but not as a fourth factor.
- A waterfall on an Availability-only screen is impossible, and must not be forced with empty bars.
- A bar must not be built from a tag forbidden for that metric, for example using `total` on the Availability
  step.
- Aggregating to area or site is done by summing the base terms, APT, PBT, ideal time, and output quantities,
  then recomputing. Averaging OEE across lines produces a figure that matches no minute count anywhere.
- Rejects of several work units are combined as **reject time**, each converted at its own step, never by adding
  kg across steps ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#rejects-across-work-units-add-up-in-time-never-in-weight-po-2026-09-30), PO 2026-09-30; see [`US-FND-KPI-003`](#us-fnd-kpi-003)).
- Comparing OEE across hierarchy levels is shown only when the process characteristics are comparable. When
  that is unknown, show a warning rather than an unqualified comparison.

*Time period scope*
- OEE also computes over an explicit `business_date` range, the same way as its three components.
- Once Availability, Performance, and Quality are each computed for that range ([`US-FND-KPI-001`](#us-fnd-kpi-001),
  [`US-FND-KPI-002`](#us-fnd-kpi-002), [`US-FND-KPI-003`](#us-fnd-kpi-003)), `OEE = A × P × Q` for that same
  range.
- The waterfall is built from the range's own summed PBT/APT/ideal-time/output terms rather than one shift's
  ([F-07 — Time period scope](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#time-period-scope--adopted-no-new-modeling-needed)).
- Provisional labelling still applies if any `SHIFT_INSTANCE` inside the range is still `open`.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | The three component `KPI_RESULT` rows (`metric = availability`/`performance`/`quality`) for the same work unit and period, plus `SHIFT_INSTANCE` for POT, `ASSET_STATE_LOG` and `DOWNTIME_REASON` for the step 1 breakdown, `REJECT_REASON` for the step 3 breakdown |
| **Writes** | `KPI_RESULT` with `metric = oee`, `waterfall` populated ([F-07.1 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#95-kpi_result)). `period` is either a `shift_instance_id` or an explicit `business_date` range, matching whichever `period` the three component results were computed for. Same field as in [`US-FND-KPI-001`](#us-fnd-kpi-001); no schema change. No writes to any other FOUNDATION entity |
| **Not real fields: don't add** | No new fields. No separate OEE entity. `WORK_CENTER.is_oee_tracked` (see rule 10) |

**6. Rules & constraints**

1. OEE exists only when A, P, and Q are all computed for the same work unit and period.
2. A missing component is never treated as 0 or 1.
3. OEE is never displayed without its three components.
4. OEE is never displayed without the waterfall for the same period.
5. The waterfall starts at PBT. Starting it at the shift clock is forbidden.
6. Planned downtime is not an OEE factor.
7. Step 4 must equal `OEE × PBT`. A mismatch stops the waterfall from being displayed.
8. Aggregation is from summed base terms, not averaged percentages. Rejects from several work units are summed as
   reject time, never as kg (F-07, 2026-09-30).
9. When `period` is an explicit `business_date` range, all three components and the waterfall are computed from
   that range's own summed terms, not one shift's — same rule as rule 8, applied to time instead of space.
10. **`TBD — perlu konfirmasi foundation-domain`** (see the same gap in [`US-FND-SIT-001`](01-site-hierarchy.md#us-fnd-sit-001)
    and [`US-FND-KPI-001`](#us-fnd-kpi-001)): OEE line-eligibility gating on `WORK_CENTER.is_oee_tracked = true`
    is not a real field — do not build it.
    - [F-01 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#95-work_center--production-line--process-cell--production-unit--storage-zone)
      has no such field; [F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md) mentions it
      "when used"; that reference is stale, prefer F-01, do not invent the field.
    - Until a field (or a `WORK_CENTER_CLASS`-based equivalent) is added, this gating rule cannot be enforced —
      OEE is computed wherever A, P, and Q all exist, with no additional line-eligibility filter.
11. Access and offline behaviour as in [`US-FND-KPI-001`](#us-fnd-kpi-001).

**7. Acceptance criteria**

- **AC-1** **Given** A 90.0%, P 83.8%, and Q 95.0% for the same shift and work unit, **when** OEE is computed,
  **then** the result is 71.6%.
- **AC-2 (waterfall)** **Given** the same data, **when** the waterfall is displayed, **then** its bars run 420,
  378, 316.67, and 300.83 minutes, starting at PBT and not at 480 minutes.
- **AC-3 (closing check)** **Given** the same data, **when** the last bar is checked, **then** `OEE × PBT`
  equals 300.83 minutes, matching the step 3 result.
- **AC-4 (missing component)** **Given** Availability is computed but Performance is "no data", **when** the
  screen is opened, **then** OEE is not displayed at all, Availability is still displayed, and the screen names
  Performance as the blocker with what is needed.
- **AC-5 (no zero substitution)** **Given** Quality is "no data", **when** OEE is computed, **then** it is not
  computed as A times P times 0.
- **AC-6 (no waterfall)** **Given** only Availability is computed, **when** the screen is opened, **then**
  there is no OEE waterfall and no forced empty bars.
- **AC-7 (planned downtime)** **Given** 60 minutes of planned downtime, **when** the waterfall is displayed,
  **then** those 60 minutes appear on the POT to PBT segment and not as a fourth OEE factor.
- **AC-8 (aggregation)** **Given** line A at OEE 71.6% with PBT 420 and line B at OEE 40.0% with PBT 120,
  **when** area OEE is computed, **then** it is recomputed from summed base terms rather than from the average
  of 71.6% and 40.0%.
- **AC-9 (provisional)** **Given** the shift is still `open`, **when** the screen is opened, **then** OEE and
  all three components are labelled provisional with the last update time.
- **AC-10 (drill down)** **Given** the waterfall is displayed, **when** the supervisor taps the Availability
  bar, **then** its breakdown uses the same tables and reasons as that metric, namely unplanned reasons on
  `ASSET_STATE_LOG`.
- **AC-11 (permission)** **Given** a supervisor with no role on that line, **when** they try to open it,
  **then** that line is not in their list.
- **AC-12 (time period scope)** **Given** Availability, Performance, and Quality are each computed for the same
  5-day `business_date` range as 90.0%, 83.8%, and 95.0%, **when** OEE is computed for that range, **then** the
  result is 71.6%, and the waterfall bars are built from the range's own summed PBT/APT/ideal-time/output
  terms, not one shift's.

**8. Metrics & events**

- **Metric:** `oee_computed`, the share of the pilot line's `closed` shifts whose OEE is computed in full.
  Baseline 0% on 2026-08-24. Target 85% by 30 days after release. Source: the calculation job's output table.
- **Counter-metrics:** `figures_shown_without_complete_input` must be 0, and the count of waterfall closing
  mismatches must be 0. A closing mismatch means an arithmetic error that must be fixed before the figures are
  trusted.
- **Events:** `oee_computed` (`work_unit_id`, `business_date`, `availability`, `performance`, `quality`, `oee`,
  `is_provisional`) · `oee_suppressed_missing_component` (`work_unit_id`, `missing_component`) ·
  `oee_waterfall_mismatch` (`work_unit_id`, `expected_minutes`, `actual_minutes`).

**Dependencies:** `US-FND-KPI-001`, `US-FND-KPI-002`, `US-FND-KPI-003`

---

#### US-FND-KPI-005

**Compute MTTR and MTBF from unplanned stops**

**Status:** 🔴 Blocked — its whole population reads `ASSET_STATE_LOG` (B5: owned by PRODUCTION since 2026-09-28; recording level SL-1 settled 2026-09-29, but the entity spec is still a draft with `TBD` state field names and shift-split rule; row ID `asset_state_log_id` since 2026-10-01).

> **In short:** MTTR (mean time to repair) is how long an unplanned stop lasts on average; MTBF (mean time
> between failures) is the average gap between two unplanned stops. Together they let a plant manager compare
> line reliability without waiting for a Maintenance module.

**1. Story**

As a **plant manager**, I want mean repair duration and mean gap between failures per line, so that I can
compare line reliability without waiting for a Maintenance module.

**2. Context**

- **Risk (B5, see [`US-FND-KPI-001`](#us-fnd-kpi-001)):** this story's entire population reads
  `ASSET_STATE_LOG`. Its owner is settled (PRODUCTION, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)), but its entity spec
  ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)) is still a draft: the row ID and state field names and the shift-split rule are
  `TBD`. The recording level (SL-1, was A-5) was resolved on 2026-09-29: per asset, `asset_id` and `work_unit_id`
  both required. Not buildable end-to-end until B5 resolves. MTTR / MTBF use closed intervals only (`ended_at` set),
  as that spec states.
- **Same data, different purpose:** both figures come from the same `ASSET_STATE_LOG` rows that cut APT, so no
  new tags and no new module are needed ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#mttr-mttf-and-mtbf-from-unplanned-downtime-only)).
  Availability answers how much time was lost, MTTR and MTBF answer how often and how long each occurrence was.
- **Not the ISO definition, stated honestly:** these are **not** TBF and TTR as ISO defines them. ISO's TBF
  spans setup time, production time, and repair time between two failures, and MolcaDx does not track those
  three as one element. What is computed here is the gap between unplanned stops only.
- F-07 records this as a known and accepted simplification for v1 rather than a faithful reading of ISO
  ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#reliability-elements-iso-clause-514)). Any
  screen showing these figures must name their basis, so they are not compared against an MTBF from another
  system using the ISO definition.
- **Who and where:** a plant manager at a desk, over weekly or monthly periods rather than per shift. A single
  shift rarely has enough occurrences to produce a meaningful average.

**3. Expectation**

- Two figures side by side, MTTR and MTBF, in minutes, along with the number of occurrences behind them.
- The occurrence count must appear next to the figure. An average of 3 occurrences and an average of 30 mean
  very different things, and hiding the divisor makes them look identical.
- Beneath, one permanent line naming the basis, namely unplanned production stops rather than maintenance work
  orders.
- Readable within 3 seconds: both figures with their occurrence counts.

| State | What the user sees |
|-------|--------------------|
| Default | MTTR and MTBF with occurrence counts and the basis line, as above |
| No data, MTTR | No closed unplanned stop in the period. Show "no data", **not 0 seconds**. Zero seconds would mean instantaneous repair, the opposite of what actually happened |
| No data, MTBF | Fewer than 2 closed stops, because a gap needs two occurrences. With exactly 1 stop, MTTR still shows and MTBF reads "cannot compute" with its reason |
| Loading | Display skeleton |
| Error | The last figures with a stale marker |
| Offline | Not applicable. This is a desk analysis screen, not a shop floor screen |
| No permission | Explain that this line is outside the role's scope and name who to contact |

**4. Calculation**

*Population*
- `ASSET_STATE_LOG` rows for that work unit's `primary` asset whose `downtime_reason_id` points at a
  `DOWNTIME_REASON` with `downtime_category = unplanned`, **including** stops that were once unlabelled and
  stayed unplanned.
- Excluded: `planned` and `small_stop`. Including `small_stop` would turn MTBF into a micro-stop statistic.
- Only **closed** stops count, meaning `ended_at` is populated. Order by `started_at`.

*How MTTR and MTBF are worked out*
- MTTR is the average length of one closed unplanned stop. MTBF is the average running gap between two
  consecutive closed unplanned stops.
- Example: stops of 12, 18 and 12 minutes → MTTR **14.0 minutes**; gaps of 103 and 197 minutes → MTBF
  **150.0 minutes** (full walk-through below).

> [!note]- Exact formula (for developers)
> ```
> MTTR = Σ (ended_at − started_at) / n                  for n closed unplanned stops
> MTBF = Σ (started_at_{i+1} − ended_at_i) / (n − 1)    for n of at least 2
> ```

*Worked example* (the example shift has 3 closed unplanned stops)
1. Stop 1 from 09:10 to 09:22, that is 12 minutes.
2. Stop 2 from 11:05 to 11:23, that is 18 minutes.
3. Stop 3 from 14:40 to 14:52, that is 12 minutes.
4. `Σ duration = 12 + 18 + 12 = 42` minutes, matching `unplanned_dt` in [`US-FND-KPI-001`](#us-fnd-kpi-001).
   `MTTR = 42 / 3 = 14.0` minutes.
5. Gap 1 from 09:22 to 11:05, that is 103 minutes. Gap 2 from 11:23 to 14:40, that is 197 minutes.
6. `Σ gaps = 103 + 197 = 300` minutes, `n − 1 = 2`, so `MTBF = 300 / 2 = 150.0` minutes.

*Edge cases*
- `n = 0` makes both "no data", not 0.
- `n = 1` gives MTTR a figure and MTBF "cannot compute", because there is no gap between two occurrences.
- A still-open stop is skipped until it closes. A running clock is not a finished repair, and including it would
  lower MTTR falsely every time the screen reloads.
- A stop crossing a period boundary counts whole in the period containing its `started_at`, not split. This
  differs deliberately from the Availability treatment, which splits by overlap: one repair is one occurrence,
  not two half occurrences.
- A gap crossing a shift or a day still counts, because machine reliability does not stop at a shift boundary.
- A period with no production at all, for example a holiday between two stops, still counts inside the gap. If
  that turns out to make MTBF misleading for lines that produce infrequently, adjusting it needs an Operations
  decision, and that has not been raised.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `ASSET_STATE_LOG` (PRODUCTION-owned, [PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time); `started_at`, `ended_at`, `downtime_reason_id`, `asset_id`), `DOWNTIME_REASON` (`downtime_category`), `ASSET_PLACEMENT`, `WORK_UNIT`, `SHIFT_INSTANCE` for the period boundaries |
| **Writes** | `KPI_RESULT` with `metric = mttr`/`mtbf` ([F-07.1 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#95-kpi_result)). No writes to any other FOUNDATION entity |
| **Not real fields: don't add** | No new fields. No maintenance work order entity, because that module does not exist and these figures do not wait for it |

- **No separate binding lookup:** `KPI_FORMULA_SLOT.reliability.interval` is `bindable = false` —
  [F-07.1 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#91-kpi_formula_slot) states it is
  computed directly from whatever `WORK_UNIT_KPI_BINDING` row already feeds `availability.downtime_reason` for
  this work unit, never bound on its own.

**6. Rules & constraints**

1. Only stops with `downtime_category = unplanned`. `planned` and `small_stop` are excluded.
2. Only closed stops. Open stops are skipped.
3. MTBF needs at least 2 closed stops.
4. The occurrence count must be displayed with the figure.
5. The screen must state that the basis is unplanned production stops, not maintenance work orders, and not the
   ISO TBF definition.
6. Display MTTR in minutes, and MTBF in hours once the gap exceeds 120 minutes, so the figure stays readable.
   The unit must be written next to the number.
7. Aggregating to area or site pools all occurrences and recomputes, rather than averaging MTTR across lines.
8. **Access:** `view` on a `KPI_RESULT` scoped at `work_unit` needs **work-unit access** to that work unit (or
   work-center/area/site/enterprise/tenant access).

**7. Acceptance criteria**

- **AC-1** **Given** 3 closed unplanned stops of 12, 18, and 12 minutes, **when** MTTR is computed, **then**
  the result is 14.0 minutes with the occurrence count 3 shown beside it.
- **AC-2** **Given** those three stops with gaps of 103 and 197 minutes, **when** MTBF is computed, **then**
  the result is 150.0 minutes with a divisor of 2.
- **AC-3 (zero occurrences)** **Given** no closed unplanned stop in the period, **when** the screen is opened,
  **then** both MTTR and MTBF read "no data" and **not** 0.
- **AC-4 (one occurrence)** **Given** exactly 1 closed stop, **when** the screen is opened, **then** MTTR shows
  a figure and MTBF reads "cannot compute" with its reason.
- **AC-5 (open stop)** **Given** one stop is still open alongside the 3 closed ones, **when** MTTR is computed,
  **then** the divisor stays 3 and the open stop is excluded.
- **AC-6 (small stops excluded)** **Given** 8 small stops totalling 12 minutes in the same period, **when**
  MTTR and MTBF are computed, **then** those 8 are outside the population and the figures stay 14.0 and 150.0
  minutes.
- **AC-7 (planned excluded)** **Given** one planned stop of 60 minutes, **when** MTTR is computed, **then** that
  stop is outside the population.
- **AC-8 (basis stated)** **Given** both figures are displayed, **when** the manager reads them, **then** the
  screen states the basis is unplanned production stops and not the ISO TBF definition.
- **AC-9 (aggregation)** **Given** line A has 3 occurrences and line B has 5, **when** area MTTR is computed,
  **then** it is computed from all 8 occurrences rather than from the average of two MTTR figures.
- **AC-10 (permission)** **Given** a user with the `operator` role, **when** they try to open the reliability
  screen, **then** access is refused with an explanation and a contact.

**8. Metrics & events**

- **Metric:** `reliability_computed`, the share of pilot lines with a computed MTTR over the rolling 30-day
  period. Baseline 0% on 2026-08-24. Target 100% by 30 days after release, for lines that actually had
  unplanned stops. Source: the calculation job's output table.
- **Counter-metric:** the count of times MTTR or MTBF is displayed as 0 when the population is empty. Must be 0.
- **Events:** `reliability_computed` (`work_unit_id`, `period`, `mttr_minutes`, `mtbf_minutes`, `event_count`)
  · `reliability_no_data` (`work_unit_id`, `period`, `closed_stop_count`).

**Dependencies:** `US-FND-KPI-001`, `US-FND-REF-001`

---

#### US-FND-KPI-006

**Open downtime from a stalled counter**

**Status:** 🟡 Partly blocked — K-6 resolved 2026-10-01 (PO): two [stop thresholds](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#confirmed-first-release-item-stop-thresholds-po-2026-10-01), per work center with an optional work unit override. Still open: where the values are stored (`TBD` for engineering, F-12), and K-11 (duration vs reason category; manual stops). `ASSET_STATE_LOG` spec is still a PRODUCTION draft (B5).

> **In short:** when a line's output counter stops going up, the system opens a stop on its own, so stops are
> recorded even when no operator presses anything. How long the pause lasts decides what it is: shorter than 60 s
> by default is not a stop at all, up to 300 s is a small stop, longer is real downtime.

**1. Story**

As a **production supervisor**, I want a stopped line recorded automatically from a counter that stops
incrementing, so that stops are captured even when no operator presses anything.

**2. Context**

- **Two ways a stop enters the system:** in the first, a boolean tag directly opens and closes an
  `ASSET_STATE_LOG` row. In the second, the `primary` asset's output counter stops incrementing and the reason
  is matched afterwards. Both exist in the field, and this story is about the second
  ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#how-downtime-gets-detected-from-a-stalled-counter)).
- **Why it matters:** a line with no machine status tag at all can still have Availability, because a stopped
  counter is evidence that the line stopped. Without this path, such a line stays permanently at "no data" for
  Availability even though the data does exist in another form.
- **Same shape as other stops:** the result lands in the same `ASSET_STATE_LOG` shape. This is not a different
  table and not a different Availability formula. Only the way the row gets filled differs.

**3. Expectation**

- An automatically opened stop appears in that shift's stop list with an origin marker, namely detected from
  counter, distinguished from a stop opened by an operator.
- A pause shorter than the minimum stop never appears as a stop. Its lost time shows only as lower speed
  (Performance).
- That origin marker is mandatory, because the supervisor needs to know which stops no human has looked at yet.
- Readable within 3 seconds: how many stops need a label.

| State | What the user sees |
|-------|--------------------|
| Default | The detected stop in the stop list with its origin marker, as above |
| Unlabelled | A detected stop with no matching reason appears as needing a label, with a one-tap prompt to label it. It counts as unplanned while it stays unlabelled |
| Small stop | A stall from the minimum stop up to the small-stop limit appears separately as a Performance loss, does not enter the downtime list, and does not reduce PBT |
| Still running | If the counter is still not incrementing when the screen opens, the stop is still open. Show a running clock with an unfinished marker |
| Loading | List skeleton |
| Error | The last list with a stale marker |
| Offline | Detection runs server side from tag data, so it does not depend on the supervisor's device. The locally stored list stays visible with an offline marker |
| No permission | As in [`US-FND-KPI-001`](#us-fnd-kpi-001) |

**4. Calculation**

*Steps*
1. **Watch the `total` tag on the `primary` asset.** While its value increases, the work unit is producing.
   When it stops increasing, start the clock from the timestamp of the last real increment.
   (The stall runs from the last real increment to the next one — or to the end of the period, if
   the counter is still stalled. Exact formula in the box below.)
2. **Bucket by stall duration** with the two [stop thresholds](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#confirmed-first-release-item-stop-thresholds-po-2026-10-01) that apply to this work unit on the shift's
   `business_date` (PO, 2026-10-01, closes K-6):
   - shorter than the **minimum stop** (`min_stop_seconds`, default 60 s) → **not a stop event**. No row is
     created; the lost time stays inside APT and counts as speed loss (Performance);
   - from the minimum stop up to the **small-stop limit** (`small_stop_max_seconds`, default 300 s) → `small_stop`
     (Performance loss, not Availability);
   - longer than the small-stop limit → real downtime (Availability), `planned` or `unplanned` from the matched
     reason (step 3; unlabelled = `unplanned`).

   **Which values apply:** each value is looked up on its own, first match wins: work unit → work center → site →
   global default. A work unit may override only one of the two and inherit the other. Exact formula in the box
   below.
3. **Match the stall window against reason tags.** Any tag with role `downtime_reason` on the `primary` **or**
   any `secondary` asset that turned on or incremented within the same window supplies a `downtime_reason_id`
   to enrich the event.
4. **No reason tag active in that window** means the event is still created, because the stall itself is the
   evidence, but stays unlabelled. It is treated as unplanned until a reason is set, and is never discarded for
   lack of a match.
5. The result lands in the same `ASSET_STATE_LOG` shape as any other path.

> [!note]- Exact formula (for developers)
> ```
> stall_duration = next_increment_time − last_increment_time
> stall_duration = period_end − last_increment_time      if the period closes with the counter still stalled
> ```
>
> ```
> stall_duration < min_stop_seconds                          →  no event (speed loss, stays inside APT)
> min_stop_seconds ≤ stall_duration ≤ small_stop_max_seconds →  small_stop (Performance loss)
> stall_duration > small_stop_max_seconds                    →  real downtime (Availability loss)
>
> each threshold = the value valid on SHIFT_INSTANCE.business_date, first found in:
>   WORK_UNIT → WORK_CENTER → SITE → global default (min_stop_seconds 60, small_stop_max_seconds 300)
> ```

*Worked example* (stop 2 from the example shift)
1. The `total` tag's last real increment is at 11:05:00 and the next at 11:23:00, so `stall_duration = 18`
   minutes.
2. With the defaults (60 s / 300 s), 18 minutes = 1,080 s is longer than 300 s, so it is real downtime.
3. Within the 11:05 to 11:23 window, a `downtime_reason` tag named "film out" on the `primary` asset turned on
   at 11:06, so the event takes the `downtime_reason_id` for "film out" and lands as 18 unplanned minutes.

*Second example*
1. The last increment is at 13:40:00 and the next at 13:41:30, so `stall_duration = 90` s.
2. With the defaults, 90 s is between 60 s and 300 s, so it is a `small_stop`: it does not reduce PBT, and its
   minutes stay inside APT as a Performance loss.

*Third example*
1. The last increment is at 14:10:00 and the next at 14:10:40, so `stall_duration = 40` s.
2. 40 s is shorter than 60 s, so **no event** is created. The 40 s stay inside APT as speed loss.

*Why the thresholds matter*
- If Line C's work center sets `small_stop_max_seconds = 60`, that 90 s stall becomes Availability downtime and
  APT drops by 1.5 minutes.
- On a line with 40 such stalls per shift the difference is 60 minutes of APT, which is 14 percentage points of
  Availability on a PBT of 420. This is why the values are recorded, effective-dated settings, not constants.

*Edge cases*
- A counter that goes backwards, for example after a PLC reset, is not a stall. Treat it as a reset and do not
  count a negative difference as a gap.
- A counter that never incremented since the shift began has no last real increment to start from. Use the
  shift start as the starting point, and mark the event as resting on that assumption.
- A `total` tag on a `secondary` asset is **never** watched for stalling and never opens or closes an event by
  itself. A buffer between `primary` and `secondary` equipment means their counters can legitimately disagree
  about exactly when a stop began.
- A stop already opened manually by an operator over the same window must not be duplicated. Merge them, and
  keep the operator's label.
- A stall window with more than one active reason tag has no selection rule yet. Until one is set, use the tag
  that turned on earliest in the window and record the others as candidates on the event.
- A stall exactly at a threshold: exactly `min_stop_seconds` is a `small_stop`; exactly `small_stop_max_seconds`
  is still a `small_stop` ("from … up to", F-12).
- A stall longer than the small-stop limit whose matched reason has `downtime_category = small_stop`: duration or
  reason wins? `TBD — needs PO confirmation` (K-11a). Until answered, do not build this case.
- A shift is bucketed with the thresholds valid on its `business_date`. Changing a threshold never recalculates a
  closed shift.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `ASSET_TAGS` with roles `total` and `downtime_reason` (on a multi-lane machine, `total` only where `ASSET_TAGS.work_unit_id` = this work unit), the tag value history, `ASSET_PLACEMENT` (`placement_role`), `ASSET`, `SHIFT_INSTANCE`, `DOWNTIME_REASON`, and `WORK_UNIT_KPI_BINDING` to confirm this work unit's `availability.downtime_reason` slot is actually bound with `transform = stall_bucket` ([F-07.1 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#92-transform-enum)) before running this detection path at all |
| **Writes** | `ASSET_STATE_LOG` rows (`started_at`, `ended_at`, `downtime_reason_id`, `asset_id`, `source = derived`) — through PRODUCTION, the log's only writer ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)) |
| **Reads (thresholds)** | `min_stop_seconds`, `small_stop_max_seconds` from [Configuration Service](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#confirmed-first-release-item-stop-thresholds-po-2026-10-01), resolved for this `WORK_UNIT` → its `WORK_CENTER` → `SITE` → global default, valid on `SHIFT_INSTANCE.business_date`. Storage table: `TBD` for engineering (F-12 has no entity model yet) |
| **Not real fields: don't add** | No new fields. The origin marker is PRODUCTION's `source` field (`plc` / `manual` / `derived`; a stall-detected row is `derived`) — resolved 2026-09-28, no FOUNDATION field is added. Do **not** add threshold columns to `WORK_CENTER` or `WORK_UNIT` (F-12) |

- A work unit bound to `none` or `reason_tag_drives_stop` does not run stall detection.
- **Owner writes, this story detects:** `ASSET_STATE_LOG` is owned by PRODUCTION, which is its **only writer**
  (decision 2026-09-28). PRODUCTION's spec already lists stall and out-of-range detection as a row source with
  `source = derived` ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)), so the detected stop is written through PRODUCTION, never
  by a second writer.

**6. Rules & constraints**

1. Only the `total` tag on the `primary` asset is watched for stalling. On a **multi-lane machine** it is
   **that lane's** `total` tag ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#multiple-assets-under-one-work-unit--primary-vs-secondary), 2026-09-25).
2. The two stop thresholds are recorded, effective-dated values from Configuration Service, never constants in
   code. Lookup: work unit → work center → site → global default (60 s / 300 s); each value resolves on its own.
3. A stall shorter than `min_stop_seconds` creates no event (speed loss). A stall from `min_stop_seconds` up to
   `small_stop_max_seconds` becomes a `small_stop` and does not reduce PBT. A longer stall is real downtime.
4. An event with no matching reason is still created, not discarded.
5. An unlabelled event is treated as unplanned.
6. A counter going backwards is not a stall.
7. Do not duplicate a stop an operator already opened over the same window.
8. The result lands in the same `ASSET_STATE_LOG` shape, not a new table.
9. A `secondary` asset may contribute a reason, and may not open an event.
10. Read access as in [`US-FND-KPI-001`](#us-fnd-kpi-001). Writing is done by the job, not by a user.

**7. Acceptance criteria**

The thresholds are set (K-6, PO 2026-10-01). Criteria below use the global defaults (60 s / 300 s) unless they
say otherwise.

- **AC-1 (threshold independent)** **Given** the primary asset's `total` tag stops incrementing at 11:05 and
  increments again at 11:23, **when** detection runs, **then** one event is created starting at 11:05 and
  ending at 11:23.
- **AC-2 (threshold independent)** **Given** a reason tag "film out" turns on at 11:06 within that window,
  **when** the event is created, **then** it takes the `downtime_reason_id` for "film out".
- **AC-3 (threshold independent)** **Given** no reason tag is active within the window, **when** the event is
  created, **then** it is still created unlabelled and counted as unplanned.
- **AC-4 (threshold independent)** **Given** the counter goes backwards after a PLC reset, **when** detection
  runs, **then** no event is created from that negative difference.
- **AC-5 (threshold independent)** **Given** a `total` tag on a secondary asset stops incrementing while the
  primary keeps incrementing, **when** detection runs, **then** no event is created.
- **AC-6 (threshold independent)** **Given** an operator already opened a manual stop from 11:05 to 11:23,
  **when** detection finds a stall over the same window, **then** the two are merged into one event and the
  operator's label is kept.
- **AC-7 (small stop)** **Given** a stall of 90 s, **when** detection runs, **then** it is bucketed as a
  `small_stop` and does not reduce PBT.
- **AC-8 (real downtime)** **Given** a stall of 18 minutes, **when** detection runs, **then** it is bucketed as
  real downtime and reduces APT.
- **AC-10 (below minimum stop)** **Given** a stall of 40 s, **when** detection runs, **then** no event is created
  and the 40 s stay inside APT.
- **AC-11 (work unit override)** **Given** the work center sets `small_stop_max_seconds = 600` and the work unit
  overrides only `min_stop_seconds = 30`, **when** a 45 s stall and a 400 s stall are detected on that work unit,
  **then** both are `small_stop` (30 s from the work unit, 600 s from the work center).
- **AC-12 (effective-dated)** **Given** a closed shift on 2026-10-01 bucketed with 300 s, **when** the work
  center's `small_stop_max_seconds` is changed to 240 s from 2026-10-02, **then** the 2026-10-01 shift is not
  recalculated and shifts from 2026-10-02 use 240 s.
- **AC-9 (threshold independent)** **Given** the supervisor opens the stop list, **when** an automatically
  detected event appears, **then** it is marked as originating from the counter and distinguished from an
  operator-opened stop.

**8. Metrics & events**

- **Metric:** `stops_detected_automatically`, the share of the pilot line's downtime events opened by stall
  detection rather than by an operator tap. Baseline 0% on 2026-08-24. Target: `TBD — needs PO confirmation`
  (the thresholds are now set; this figure moves directly with them). Source: `ASSET_STATE_LOG` rows with `source = derived`.
- **Counter-metric:** the share of detected events later cancelled by a supervisor because they were not
  actually stops. A high number means `min_stop_seconds` is too short.
- **Events:** `downtime_detected_from_stall` (`work_unit_id`, `stall_seconds`, `bucket`, `matched_reason_id`,
  `min_stop_seconds_used`, `small_stop_max_seconds_used`) · `stall_detection_merged_with_manual` (`work_unit_id`, `manual_event_id`) ·
  `stall_event_rejected` (`work_unit_id`, `stall_seconds`).

**Dependencies:** `US-FND-KPI-001`, `US-FND-AST-002`, `US-FND-KPI-007`

---

#### US-FND-KPI-007

**Bind a work unit's tags to its KPI formula slots**

**Status:** 🟢 Ready

> **In short:** for each work unit, the Admin/IT picks which machine tag, and which math, fills each input a
> KPI formula needs. Until every mandatory input is picked, that work unit's KPIs read "no data" instead of a
> guessed number.

**1. Story**

As an **Admin/IT pabrik**, I want to pick which tag and which math fills each mandatory KPI formula slot for a
work unit, so that the calculation job has an explicit, traceable source for every metric instead of a guessed
"first that works" order.

**2. Context**

- **Why:** every story in this module (`US-FND-KPI-001` to `006`) reads `WORK_UNIT_KPI_BINDING` as an input and
  returns `no_data` — never a fabricated 0% — when a mandatory slot has no binding. Without this story, that row
  can never exist, and every KPI on every work unit stays permanently at "no data."
- **When:** a one-time setup step per work unit, done once during provisioning
  ([FLOW-02](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/FLOW/02-production-readiness.md)) and revisited only when the physical
  wiring changes — a machine swap, a new sensor, a checkweigher added.
- **Who may do it:** per K-7, this is an ordinary site-scoped FOUNDATION admin action, the same role model as
  Site Hierarchy, Shift, Products, and Reference Data — not a formula-design act and not a PO-gated one
  ([F-07.1 §K-7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#9-entity-specification)).
- **What this story explicitly does not do:** it does not define which slots exist or which tag-role/transform
  combinations are legal for them — that catalog (`KPI_FORMULA_SLOT`/`KPI_PARAMETERS`) is seeded, closed, and
  `foundation-domain`/PO-only ([`SCR-FND-KPI-001`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md), read-only). This
  story only picks, per work unit, one already-legal combination per slot.
- **Where:** desk-based, Admin/IT pabrik ([00-product-overview §3](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/00-product-overview.md#3-target-user)),
  done once per work unit during setup, occasionally rebound afterward.

**3. Expectation**

- A list of this work unit's mandatory bindable slots (`KPI_FORMULA_SLOT` where `bindable = true`), each
  showing its current binding — tag, asset, transform — or "not bound" if none exists yet.
- **A KPI item inside each domain's sidebar group** (PO, 2026-09-28, [UX 07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md#kpi-item-inside-each-domain-group-po-decision-2026-09-28),
  [UX 00 § App shell](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md#app-shell--sidebar-grouped-by-domain-po-decision-2026-09-28); replaces the 2026-09-27 "KPI" group with one item per domain): the sidebar is
  grouped by domain. The shared FOUNDATION items sit at the top with **no umbrella heading** (no "Master data"
  label, PO 2026-09-28), then the groups Production, Maintenance, Quality, Inventory. Each domain group holds a
  **KPI** item next to its other domain pages, listed by what the page is — Production: KPI (BOM and Routing left
  the Production group on 2026-09-30 and are now tabs on the Product detail page, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-bom-routing-as-product-tabs.md);
  `nav-sidebar.item.bom` / `.routing` retired, keys not reused); Maintenance: PM checklist · KPI · Asset history;
  Quality and Inventory: KPI only ([UX 00 § App shell](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md#app-shell--sidebar-grouped-by-domain-po-decision-2026-09-28),
  [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md), addendum). The KPI item opens this editor with only that domain's slots
  (`KPI_FORMULA_SLOT.domain`).
- **Domain not licensed** ([F-07.1 K-9](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#whats-still-unknown), revised 2026-09-28; [PLT-01 core concept 3a](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PLATFORM/DOCS-EN/01-license-manager.md#core-concepts)): the domain
  group shows as **one locked heading** — collapsed, lock icon, not expandable; clicking it opens the upsell /
  "contact your administrator" surface. Its items, KPI included, are not listed. Tabs the domain gates on shared
  screens (BOM and Routing on Product detail, for Production) are hidden too ([UX 00 § App shell](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md#app-shell--sidebar-grouped-by-domain-po-decision-2026-09-28), 2026-09-30). A direct link to a KPI screen of
  that domain still lands on the visible-but-locked screen state.
- Today only Production has slots; a licensed Quality, Maintenance or Inventory group's KPI item opens to
  "No \<domain\> KPIs yet." (`kpi-002.empty.domain`, draft) and "Molca hasn't adopted a KPI for \<domain\> yet.
  Production KPIs are under Production." (`kpi-002.empty.domain.note`, draft).

Sidebar text is copied word for word from [UX 00 § App shell](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md#app-shell--sidebar-grouped-by-domain-po-decision-2026-09-28) (navigation text), with its key. If they
differ, UX 00 is right. The earlier `kpi-sidebar.*` keys are retired in UX 07 and not reused.

| Key | Where / state | Text |
|---|---|---|
| `nav-sidebar.group.production` | Group label | "Production" (draft) |
| `nav-sidebar.group.maintenance` | Group label | "Maintenance" (draft) |
| `nav-sidebar.group.quality` | Group label | "Quality" (draft) |
| `nav-sidebar.group.inventory` | Group label | "Inventory" (draft) |
| `nav-sidebar.item.kpi` | Item inside a domain group | "KPI" (draft) |
| `nav-sidebar.locked.tooltip` | Locked group heading, hover/long-press | "Not included in your plan" (draft) |
| `nav-sidebar.locked.label` | Locked group heading, screen-reader label | "\<domain\>, locked. Not included in your plan." (draft) |

*KPI page: cards, one side panel per KPI* (PO, 2026-09-28, [UX 07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md#kpi-page-cards-one-side-panel-per-kpi-po-decision-2026-09-28))
- Each KPI takes different inputs (Availability a status tag, Performance a counter plus a cycle time, Rework
  ratio job data with nothing to bind), so there is no page listing every KPI's slots or every KPI's results. The
  **"KPI Formula Slots" and "KPI results" header buttons are removed**; the Cards / Diagram toggle stays.
- **Clicking a KPI card opens a side panel** (from the right; the page stays visible behind it) for **that KPI
  only**, in the work units of the current site / area / work center filter, and in the page's domain. Sections,
  top to bottom:
  1. **Formula** — the F-07 formula and its slots (`KPI_FORMULA_SLOT`, this KPI only). Read-only (K-1); "suggest
     change" goes to the PO. (Was the `SCR-FND-KPI-001` page.)
  2. **Inputs** — per slot, where it comes from: a tag (`tag_role` + legal `transform`s from `KPI_PARAMETERS`) or
     job data. Read-only.
  3. **Work units** — one row per work unit: each bindable slot bound (tag + transform + detection method) or
     "Not bound" with **Bind**, which opens this editor on that work unit + slot. This section is
     [`US-FND-KPI-008`](#us-fnd-kpi-008)'s view (was the `SCR-FND-KPI-004` page).
  4. **Latest result** — per work unit: latest `KPI_RESULT` value, `computed_at`, `is_stale`; "not computed" is
     never 0%. (Was the `SCR-FND-KPI-003` page.)
- A KPI worked out from job data shows sections 1, 2 and 4, and section 3 reads the job-data text below. A KPI
  "waiting on B5" shows sections 1–3, and section 4 reads the card's waiting text.
- An unbound slot whose machine has no tag for that role links to the **Tags** page (`SCR-FND-DSR-002`,
  [`US-FND-DSR-001`](13-data-source.md#us-fnd-dsr-001)) filtered to that machine.
- Screen IDs stay: `SCR-FND-KPI-001`, `-003`, `-004` are now panel sections, not pages; this editor
  (`SCR-FND-KPI-002`) is the page under the cards.

Panel text, word for word from [UX 07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md#kpi-page-cards-one-side-panel-per-kpi-po-decision-2026-09-28):

| Key | Where / state | Text |
|---|---|---|
| `kpi-panel.section.formula` | Panel section 1 heading | "Formula" (draft) |
| `kpi-panel.section.inputs` | Panel section 2 heading | "Inputs" (draft) |
| `kpi-panel.section.work-units` | Panel section 3 heading | "Work units" (draft) |
| `kpi-panel.section.result` | Panel section 4 heading | "Latest result" (draft) |
| `kpi-panel.input.job-data` | Inputs row for a slot from job data | "From job data — nothing to bind" (draft) |
| `kpi-panel.work-units.none-to-bind` | Section 3, KPI from job data | "Nothing to bind — this KPI comes from job data." (draft) |
| `kpi-panel.slot.not-bound` | Section 3, unbound slot | "Not bound" + button "Bind" (draft) |
| `kpi-panel.slot.no-tag` | Section 3, unbound slot and the machine has no tag for this role | "No tag declared for \<machine\>." + link "Declare a tag" → `SCR-FND-DSR-002` tag form, machine and role pre-filled (draft) |
| `kpi-panel.result.not-computed` | Section 4, no `KPI_RESULT` yet | "Not computed yet" (draft) — never 0% |

- Picking a slot opens an editor offering only the tag + transform combinations `KPI_PARAMETERS` marks legal
  for that slot, restricted further to tags belonging to an asset actually placed on this work unit.
- A worked preview shows what the binding would resolve to using the asset's most recent tag values, before
  saving.

| State | What the user sees |
|-------|--------------------|
| Default | The slot list and editor with preview, as above |
| Empty (new work unit) | Every mandatory slot shows "not bound," with an explicit warning that every KPI story for this work unit will read `no_data` until binding is complete — not a silent gap |
| Illegal combination | If an implementor somehow reaches a tag/transform pair not in `KPI_PARAMETERS` for that slot (e.g. a stale link), the save is rejected naming the slot and why the combination isn't legal |
| Tag not eligible | Picking a tag that does not belong to an asset placed on this work unit at the binding's `valid_from` is rejected, naming the tag and where it actually belongs |
| Paired transform | When the chosen transform needs a second tag (`infeed_outfeed_derive`), a second picker appears; for any other transform it stays hidden |
| Quality unit mismatch | Saving a `quality.good` / `quality.total` / `quality.reject` binding whose tag unit differs from the other Quality tags of this work unit is refused: "Cannot save: the Quality tags on \<work unit\> must all use the same unit. \<tag\> is in \<unit A\>, \<tag\> is in \<unit B\>. Pick tags in one unit." (`kpi-002.error.quality-uom-mismatch`, draft) ([UX 07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md#screen-text)) |
| Loading | List/editor skeleton |
| Error | Last successfully loaded binding list stays visible with a stale marker |
| Offline | Not applicable — desktop admin screen, same as every other FOUNDATION master-data editor |
| Success | Confirmation naming which slot was bound, and if this was the work unit's last unbound mandatory slot, a note that its KPI stories can now compute |
| No permission | Explains this work unit is outside the user's site access, names who to contact |

**4. Calculation**

This story checks and saves a choice. It doesn't calculate a KPI itself.

*When a save is accepted* — all three must be true:
1. The chosen tag is a kind of signal that this formula slot accepts, with the chosen math.
2. The tag belongs to a machine that is actually placed on this work unit from the date the choice takes
   effect — not just any tag in the system.
3. A second tag is given only for the "infeed minus outfeed" math, which compares two machines. The
   "good = total − reject" math uses two counts from the **same** machine, so it never needs a second tag.
4. All Quality tags of the work unit (good, total, reject, and any second tag) are in the **same unit**. Quality
   never converts units, so a mix is refused rather than converted (PO, 2026-09-28).

> [!note]- Exact rules (for developers)
> This story validates and writes a pick — it does not compute a KPI value itself.
>
> *Validation rule: a save is accepted only when **all** of the following hold*
> 1. A `KPI_PARAMETERS` row exists for `(slot_id, tag_role of the chosen asset_tag, transform)`
>    ([F-07.1 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#93-kpi_parameters)).
> 2. `asset_tag_id` resolves through `WORK_UNIT → ASSET_PLACEMENT → ASSET → ASSET_TAGS` to an asset placed on this
>    work unit at the binding's `valid_from` ([F-07.1 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#94-work_unit_kpi_binding))
>    — not any tag in the system. **Multi-lane machine (2026-09-25):** a per-lane-role tag must also have
>    `ASSET_TAGS.work_unit_id` = this `work_unit_id`; machine-wide roles (`machine_state`, `downtime_reason`,
>    `runtime`) may be bound on any of the machine's work units.
> 3. `second_asset_tag_id` is present when, and only when, `transform = infeed_outfeed_derive`
>    ([F-07.1 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#94-work_unit_kpi_binding)).
>    `complement_derive` needs two quantity sides on the **same** asset (filled from other quality slot
>    bindings), not a `second_asset_tag_id`.
> 4. **Quality unit** ([F-07.1 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#94-work_unit_kpi_binding), PO 2026-09-28): all active bindings of one work unit whose
>    `slot_id` is `quality.good` / `quality.total` / `quality.reject` (both `asset_tag_id` and
>    `second_asset_tag_id`) resolve to the same `ASSET_TAGS.uom_id` over the overlapping `valid_from`/`valid_to`
>    range. A mismatch is **refused**, not converted, with `kpi-002.error.quality-uom-mismatch`.

*Worked example* — one work unit with a primary asset (the line) and a secondary asset (an inline
checkweigher), the exact shape [F-07.1 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#94-work_unit_kpi_binding)
documents:
```
quality.total                ← primary asset's total tag, transform none
quality.good                 ← secondary asset's total tag, transform infeed_outfeed_derive,
                               paired against the primary's total tag
availability.downtime_reason ← primary asset's total tag, transform stall_bucket
```
Four rows, four slots, no priority-order lookup left for the calc job to guess at run time.

*Edge cases*
- Slots that are worked out from other slots (for example `reliability.interval`, marked `bindable = false`)
  never appear in this editor. They aren't a choice; they're derived.
- Changing a choice never overwrites the old one. The old choice ends on the day the new one starts
  (`valid_to` = the new row's `valid_from`), so a past period recalculated later still uses the choice that
  was in force at the time.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `KPI_FORMULA_SLOT` ([F-07.1 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#91-kpi_formula_slot)) for the slot list, grouped by `domain`, `KPI_PARAMETERS` ([F-07.1 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#93-kpi_parameters)) for legal combinations, `WORK_UNIT`, `ASSET_PLACEMENT`, `ASSET`, `ASSET_TAGS` (incl. `tag_role` and `work_unit_id`) to resolve which tags this work unit may bind |
| **Writes** | `WORK_UNIT_KPI_BINDING` ([F-07.1 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#94-work_unit_kpi_binding)) — `work_unit_id`, `slot_id`, `asset_tag_id`, `transform`, `second_asset_tag_id` (nullable), `valid_from`/`valid_to` |
| **Never written** | `KPI_FORMULA_SLOT`/`KPI_PARAMETERS` — that catalog is not implementor-writable (K-1/K-7 boundary), and this story must not blur it |

**6. Rules & constraints**

1. Binding is mandatory for every `bindable = true` slot on every work unit tracked for KPI — a work unit with
   unbound mandatory slots is a valid, expected state during setup, not an error, but its KPI stories read
   `no_data` until complete.
2. A save is rejected if the tag/transform pair is not `KPI_PARAMETERS`-legal for that slot.
3. A save is rejected if the tag does not belong to an asset placed on this work unit at `valid_from`. On a
   multi-lane machine, a per-lane tag (e.g. a total or good counter) assigned to another lane is rejected too —
   Lane 2's counter can't be bound to Lane 1. Machine-wide tags may be bound on any lane.
4. `second_asset_tag_id` is populated only when `transform = infeed_outfeed_derive`, never for
   `complement_derive` or any other transform ([F-07.1 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#94-work_unit_kpi_binding)).
5. Deactivate rather than hard-delete — the same effective-dating discipline as every other FOUNDATION master
   entity ([F-00 §4.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md)).
6. **Access:**
   - `create`/`update` on `WORK_UNIT_KPI_BINDING` needs **site access** to the site owning this work unit (or
     enterprise/tenant access).
   - `view` on `KPI_FORMULA_SLOT`/`KPI_PARAMETERS` for everyone with access to this screen.
   - `create`/`update` on those two is **never** granted through this story, per K-1/K-7.
   - The domain adds no permission of its own. **Entitlement** is a separate check: a domain the tenant isn't
     entitled to shows as one locked sidebar group heading, its items (KPI included) not listed (F-00.1 KPI
     note, F-07.1 K-9 revised 2026-09-28, PLT-01 core concept 3a).
7. A save is refused when the work unit's Quality bindings (`quality.good` / `quality.total` / `quality.reject`,
   incl. `second_asset_tag_id`) would not all share one `ASSET_TAGS.uom_id` over overlapping validity. Quality
   never converts units (PO, 2026-09-28).

**7. Acceptance criteria**

- **AC-1** **Given** a work unit with a primary asset placed and no bindings yet, **when** the implementor opens
  the binding editor, **then** every mandatory slot shows "not bound," with a warning that this work unit's KPIs
  will read `no_data` until bound.
- **AC-2** **Given** the implementor picks a tag/transform pair present in `KPI_PARAMETERS` for that slot,
  **when** they save, **then** the binding is saved and the preview shown before saving
  matches the value now returned by `US-FND-KPI-001`'s (or the relevant metric's) calculation for the same
  input.
- **AC-3** **Given** the implementor picks a tag/transform pair **not** present in `KPI_PARAMETERS` for that
  slot, **when** they try to save, **then** the save is rejected, naming the slot and why the pair isn't legal.
- **AC-4** **Given** the implementor picks a tag belonging to an asset not currently placed on this work unit,
  **when** they try to save, **then** the save is rejected, naming the tag and its actual location.
- **AC-5** **Given** `transform = infeed_outfeed_derive` is selected, **when** the editor renders, **then** a
  second tag picker appears and the save is rejected if it is left empty.
- **AC-5b** **Given** `transform = complement_derive` is selected, **when** the editor renders and the binding
  is saved, **then** no second-tag picker is required and no second tag is stored (complement is
  same-asset).
- **AC-6** **Given** a slot already bound, **when** the Admin/IT pabrik rebinds it to a different tag, **then**
  the prior binding row is closed with an end date equal to the new row's start date, and a KPI recomputed
  for a period before the rebind still resolves through the old row.
- **AC-7** **Given** a user without site access to this work unit's site, **when** they try to open the editor,
  **then** access is refused, naming who to contact.
- **AC-8** **Given** the implementor opens a KPI card's side panel, **when** they look in its Formula / Inputs
  sections (`SCR-FND-KPI-001`, the slot/parameter catalog) for a create/edit affordance, **then** none exists — the catalog is read-only, with a note that changes go
  through `foundation-domain`/PO.
- **AC-9 (multi-lane)** **Given** a 2-lane machine placed on `S01` and `S02`, with `Lane2_Total_Count` assigned
  to `S02` and machine-wide `Machine_Run` assigned to `S01`, **when** the implementor binds a `S01` slot to
  `Lane2_Total_Count`, **then** the save is rejected, naming the tag's lane (`S02`); binding `Machine_Run` on
  `S02` is accepted.

- **AC-10 (domain sidebar)** **Given** the seeded slot catalog, **when** the user opens Production › KPI for a work
  unit, **then** every bindable slot is listed; **when** they open Quality › KPI, **then** the page shows "No Quality
  KPIs yet." and no slot.
- **AC-11 (entitlement)** **Given** a tenant entitled to Production and Maintenance only, **when** a user opens the
  sidebar, **then** the Production and Maintenance groups each list a "KPI" item, and Quality and Inventory each
  show as one locked heading with no items listed; hovering a locked heading shows "Not included in your plan";
  clicking it opens the upsell surface.
- **AC-12 (Quality unit mismatch)** **Given** a work unit whose `quality.total` is bound to a tag in kg, **when**
  the implementor binds `quality.good` to a tag in pcs and saves, **then** the save is refused with "Cannot save:
  the Quality tags on \<work unit\> must all use the same unit. \<tag\> is in \<unit A\>, \<tag\> is in \<unit B\>. Pick
  tags in one unit." (names and units filled in) and nothing is stored; binding a `quality.good` tag in kg is
  accepted.
- **AC-13 (sidebar items)** **Given** a tenant entitled to Production and Maintenance, **when** a user opens the
  sidebar, **then** the shared items sit at the top with no "Master data" heading, Production lists KPI only (no BOM
  or Routing item since 2026-09-30) and Maintenance lists PM checklist, KPI, Asset history.
- **AC-14 (side panel)** **Given** Production › KPI for a line with work units WU_01 (Performance bound) and WU_02
  (Performance not bound, no counter tag declared on its machine), **when** the user clicks the Performance card,
  **then** a side panel opens for Performance only with sections "Formula", "Inputs", "Work units", "Latest
  result"; WU_02 reads "Not bound" with "Bind" and "No tag declared for \<machine\>." with "Declare a tag"; "Bind"
  opens this editor on WU_02's Performance slot. The page shows no "KPI Formula Slots" or "KPI results" button.
  **Given** the user clicks the Rework ratio card, **then** section 3 reads "Nothing to bind — this KPI comes from
  job data."

**8. Metrics & events**

- **Metric:** `work_units_fully_bound`, the share of KPI-tracked work units with every mandatory slot bound.
  Baseline 0% at first deployment on a new site. Target: 100% before that site's KPI stories are considered
  live — this metric is the entity-existence guarantee `US-FND-KPI-001`'s AC-12 depends on, made visible as a
  rollout dashboard rather than discovered one work unit at a time. Source: `WORK_UNIT_KPI_BINDING` joined
  against `KPI_FORMULA_SLOT WHERE bindable = true`.
- **Events:** `kpi_binding_created` (`work_unit_id`, `slot_id`, `asset_tag_id`, `transform`) ·
  `kpi_binding_rebound` (`work_unit_id`, `slot_id`, `old_asset_tag_id`, `new_asset_tag_id`) ·
  `kpi_binding_rejected` (`work_unit_id`, `slot_id`, `reason`).

**Dependencies:** `US-FND-AST-002`, `US-FND-AST-003`

---

#### US-FND-KPI-008

**View KPI calculation breakdown**

**Status:** 🟡 Partly blocked — AC-3 (range band values) waits on K-10 (who owns the normal-operating band). The threshold part of K-8 is resolved 2026-10-01: range detection uses the same stop thresholds as stall detection.

> **In short:** a read-only page that shows, for one KPI on one work unit, the formula, the bound tags, and how
> downtime is detected. The Admin/IT can check why a number reads what it does without asking
> `foundation-domain`.

**1. Story**

As an **Admin/IT pabrik**, I want to see, for one KPI on one work unit, which formula, tags, and
downtime-detection method actually feed it, so that I can trust or troubleshoot a number without asking
`foundation-domain` to read the config for me.

**2. Context**

- **The gap:** `US-FND-KPI-007` makes `WORK_UNIT_KPI_BINDING` exist, but exposes it only through an editor built
  for writing a pick, not reading one back. Once that binding is made, the only way to answer "why does this
  line's Availability read 62% and not 90%, and what is actually watching it" is to reopen the edit form and
  infer the answer from it — there is no page whose job is simply to show the current wiring.
- **F-07.1 names this page:** with site-scoped admin writing `WORK_UNIT_KPI_BINDING` (K-7, the same role model as
  every other FOUNDATION master-data screen), "a view-only reflection of whichever detection method a work unit
  binds is now also buildable: `SCR-FND-KPI-004`" ([F-07.1 "Next step"](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#next-step)).
- **Screen and flow already declared:** UX 07 and FLOW-05 declare that screen and its activity in full
  ([UX 07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md), [FLOW-05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/FLOW/05-kpi-computation.md#activity-user-opens-the-kpi-calculation-page-view-only)).
  This story is the user story for that page.
- **Who and where:** desk-based, same audience and device context as `US-FND-KPI-007`'s binding editor — an
  implementor or Admin/IT engineer verifying wiring, not a shopfloor screen.

**3. Expectation**

- **Where it lives (PO, 2026-09-28, [UX 07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md#kpi-page-cards-one-side-panel-per-kpi-po-decision-2026-09-28)):** not a separate page any more — it is **section 3 "Work
  units" of the per-KPI side panel** on a domain's KPI page, opened by clicking the KPI card
  ([`US-FND-KPI-007`](#us-fnd-kpi-007) Expectation). One KPI per panel, one row per work unit in the page's
  filter. "The page" in the rest of this story means that panel section; behavior, states and criteria are
  unchanged ([FLOW-05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/FLOW/05-kpi-computation.md#activity-user-opens-the-kpi-calculation-page-view-only) note, 2026-09-28).
- Anchor: [`SCR-FND-KPI-004`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md), flow:
  [FLOW-05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/FLOW/05-kpi-computation.md#activity-user-opens-the-kpi-calculation-page-view-only).
  No deviation from the anchor's state matrix.
- The page shows the F-07 formula for the chosen KPI × work unit, then one row per bindable slot showing the
  bound tag + transform.
- For `availability.downtime_reason` specifically, it shows which single detection method that transform
  implies (direct tag, register bank, stalled counter, or out-of-range analog).
- Read-only, no create/edit affordance anywhere.
- Readable within 3 seconds: which detection method is bound for Availability, since that is the question this
  page exists to answer.
- **Unit conversion shows the rule, not live numbers** (PO, 2026-09-28, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md), addendum;
  [FLOW-05 design implications](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/FLOW/05-kpi-computation.md#design-implications)).
  On the Performance slot, when the counter tag's unit (`ASSET_TAGS.uom_id`) differs from the standard's item unit
  (`OPERATION.uom_id`), the row shows the conversion rule. Text from [UX 07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md#screen-text), word for word:
  - `kpi-004.conversion.rule`: "Counter: \<counter unit\> → standard per \<item unit\>: ÷ \<factor\> (1 \<item unit\> = \<factor\> \<counter unit\>)" — e.g. "Counter: pack → standard per carton: ÷ 12 (1 carton = 12 pack)" (draft)
  - `kpi-004.conversion.missing` (no conversion path): "Cannot be calculated: no \<counter unit\> ↔ \<item unit\> conversion for \<product code\>" (draft)
- **Which product (PO, 2026-09-28, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md)):** the rule is shown for the product **running now** on
  this work unit, resolved from its `product_code` tag; if nothing is running, the **last product run**. Text
  from [UX 07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md#screen-text), word for word: `kpi-004.conversion.product` "Rule for \<product code\> — running now" /
  "Rule for \<product code\> — last run \<date time\>" (draft).

| State | What the user sees |
|-------|--------------------|
| Default | The formula and the slot rows, as above |
| Empty | Any bindable slot with no `WORK_UNIT_KPI_BINDING` row reads "not mapped yet," per slot, and does not block the rest of the page — Availability's detection method can show bound while Performance's slot still reads "not mapped yet," the same partial-mapping rule `KPI_RESULT` already follows |
| Loading | Skeleton, no stale numbers held as if fresh |
| Error | The last successfully loaded breakdown stays visible, marked stale, with an explicit retry |
| Offline | Last cached breakdown with an offline marker |
| Unit conversion | Performance slot, counter unit ≠ standard's item unit: the rule line `kpi-004.conversion.rule` (above). No live counts, no worked figure |
| No conversion path | Performance slot, no conversion between the two units for the product: `kpi-004.conversion.missing` (above), never 0% |
| No permission | Explains the work unit's scope and names who to contact, same pattern as `US-FND-KPI-007`'s editor |
| Success | Not applicable — view-only, no submit action |

**4. Calculation**

This story looks up and shows a choice already made. It doesn't calculate a KPI or detect anything itself.

*What the page shows for each slot of the KPI that can be chosen*
1. Which part of the F-07 formula the slot fills (for example APT).
2. Which tag and which math are chosen for this work unit right now.
3. For the downtime slot only: which **one** of the four ways of detecting a stop is used — a direct on/off
   signal, a register of reason codes, a counter that stops increasing, or a value that leaves its normal
   range. The page names that one plainly and never lists all four as if several applied.

> [!note]- Exact rules (for developers)
> This story resolves and displays an existing pick — it computes no KPI value and runs no detection itself.
>
> *What the page resolves* for each of the KPI's bindable slots (`KPI_FORMULA_SLOT` where `metric` matches,
> [F-07.1 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#91-kpi_formula_slot)):
> 1. The F-07 formula element the slot fills (`iso_element`).
> 2. The `WORK_UNIT_KPI_BINDING` row currently in effect for `(work_unit_id, slot_id)`, giving the bound
>    `asset_tag_id` (and its `tag_role`) plus `transform`.
> 3. For `availability.downtime_reason` specifically, which one of the four competing timing transforms is bound
>    — `none` (direct boolean tag), `reason_tag_drives_stop` (register bank), `stall_bucket` (stalled counter),
>    or `range_bucket` (out-of-range analog) — naming that one plainly, never listing all four as if several
>    applied at once ([F-07.1 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#92-transform-enum)).

*Worked example, stall-detection binding* (same shape as `US-FND-KPI-006`)
- `availability.downtime_reason` is bound to the primary asset's `total` tag with `transform = stall_bucket`
  ([stalled-counter detection](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#how-downtime-gets-detected-from-a-stalled-counter)).
- The page shows: formula element `APT` (Availability), bound tag "total (primary)", detection method "Stalled
  counter (`stall_bucket`)".

*Worked example, range-detection binding*
- The same slot is instead bound to the primary asset's `speed`-role tag with `transform = range_bucket`,
  against a configured normal-operating band — Product's own example is 40–100, where a value inside the band
  means running and outside means down ([range detection](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#how-downtime-gets-detected-from-an-out-of-range-analog-value)).
- The page shows: formula element `APT`, bound tag "speed (primary)", detection method "Out-of-range analog
  (`range_bucket`)".
- Since 2026-10-01 the same transform can also watch the primary asset's `activity_signal`-role tag (motor current,
  voltage, vibration, pressure) on a machine with no speed signal ([F-07.1 range detection](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#how-downtime-gets-detected-from-an-out-of-range-analog-value)). The
  detection method is still "Out-of-range analog"; the bound tag is named by its own role.
- Once the value returns inside the band, the excursion is bucketed with **the same pair of
  [stop thresholds](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#confirmed-first-release-item-stop-thresholds-po-2026-10-01)** stall detection uses (PO 2026-10-01, closes the threshold part of K-8): shorter than
  `min_stop_seconds` → not a stop event (speed loss); up to `small_stop_max_seconds` → `small_stop` (Performance
  loss); longer → real downtime (Availability). There is no separate threshold per detection method.
- The band's lower/upper bound value itself is still `TBD`: who owns it and where it is stored is **K-10**
  ([F-07.1 "What's still unknown"](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#whats-still-unknown)).

*Edge cases*
- The reliability interval slot (`reliability.interval`, marked `bindable = false`) never gets its own row. The
  page says it's worked out from the downtime slot's data (`availability.downtime_reason`), not chosen separately.
- A work unit with no binding at all for a slot shows "not mapped yet" rather than guessing a default detection
  method.
- **Unit conversion rule:** the rule line appears only when the Performance counter counts units
  (`count_basis = 'unit'`) and its `ASSET_TAGS.uom_id` differs from `OPERATION.uom_id`. A `count_basis = 'cycle'`
  counter skips conversion, so no rule line appears. The conversion path is one of: the same unit (no line); one
  `PRODUCT_UOM_CONVERSION` row read forward (×) or backward (÷); or two rows joined through
  `PRODUCT.base_uom_id` — only rows active on the date shown count ([F-08 §9.2.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#921-item-unit-and-conversion-po-2026-09-28)). Example: 1 carton =
  12 pack, counter in pack, standard per carton → "Counter: pack → standard per carton: ÷ 12 (1 carton = 12
  pack)".
- Until K-10 resolves, the page can name the bound detection method (`stall_bucket` vs `range_bucket`) but
  **cannot** render the numeric band bounds — that part of the breakdown is not yet buildable and must say so
  rather than showing a guessed number. Whether the page also shows the stop thresholds in force is not in UX 07
  (no screen text): not shown until UX declares it.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `KPI_FORMULA_SLOT` (`slot_id`, `metric`, `iso_element`, `bindable` — [F-07.1 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#91-kpi_formula_slot)), `WORK_UNIT_KPI_BINDING` (`work_unit_id`, `slot_id`, `asset_tag_id`, `transform`, `second_asset_tag_id`, `valid_from`/`valid_to` — [F-07.1 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#94-work_unit_kpi_binding)), `ASSET_TAGS` (`tag_role`, `uom_id`, `count_basis`) and `ASSET`/`ASSET_PLACEMENT` to name the bound tag's owning asset; for the conversion rule, `OPERATION` (`uom_id`), `PRODUCT` (`base_uom_id`, `product_code`), `PRODUCT_UOM_CONVERSION` (`from_uom_id`, `to_uom_id`, `conversion_value`, `valid_from`, `valid_to`) |
| **Writes** | None. This is the only KPI story besides `US-FND-KPI-005` with no write at all |
| **Not read** | `KPI_PARAMETERS` — this page shows what **is** bound, not what is legal to bind (that belongs to `US-FND-KPI-007`'s editor) |

**6. Rules & constraints**

1. No create/edit affordance anywhere on this page — a "change this" action routes to `SCR-FND-KPI-002` for a
   binding, or to `SCR-FND-KPI-001`/Product Owner for the closed slot/parameter catalog, per
   [K-1/K-7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#9-entity-specification).
2. Exactly one Availability-timing detection method is named per work unit — never rendered as a menu of
   possibilities.
3. An unbound slot reads "not mapped yet" and never blocks the rest of the page.
4. Until [K-10](30-release-risks-questions.md) resolves, the page must not display a normal-operating band value
   for a `range_bucket` binding — name the detection method, state the band value as not yet available. Stall
   and range detection share one pair of stop thresholds (K-8 resolved 2026-10-01); the page does not show them
   until UX 07 declares their text.
5. **Access:** `view` on `WORK_UNIT_KPI_BINDING`/`KPI_FORMULA_SLOT` needs **site access** to the site owning
   this work unit (or enterprise/tenant access), same as `US-FND-KPI-007`'s read access.
6. Offline: last cached breakdown, read-only, same as every other FOUNDATION master-data screen.
7. **Domain (PO, 2026-09-27):** the page is reached from a domain's KPI page (KPI › Production / Quality /
   Maintenance / Inventory, [UX 07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/07-kpi.md#kpi-item-inside-each-domain-group-po-decision-2026-09-28))
   and keeps that domain: it only lists KPIs whose slots have `KPI_FORMULA_SLOT.domain` = that domain.

**7. Acceptance criteria**

Some criteria below depend on [K-10](30-release-risks-questions.md) and **cannot be judged pass or fail** until
it is answered. Those that do not depend on it are already testable.

- **AC-1** **Given** `availability.downtime_reason` is bound to the primary asset's `total` tag with
  `transform = stall_bucket`, **when** a user with view access opens the page for that KPI × work unit,
  **then** it shows formula element `APT`, bound tag "total (primary)", and detection method "Stalled counter."
- **AC-2** **Given** the same slot is instead bound with `transform = range_bucket` on the primary's `speed`
  (or `activity_signal`) tag, **when** the page is opened, **then** detection method reads "Out-of-range analog" and not "Stalled
  counter."
- **AC-3 (K-10 dependent, not yet testable)** **Given** a `range_bucket` binding, **when** the page attempts to
  render the configured normal-operating band, **then** it shows the actual lower/upper bound value. **Waiting
  on [K-10](30-release-risks-questions.md).**
- **AC-4** **Given** a bindable slot has no `WORK_UNIT_KPI_BINDING` row, **when** the page is opened, **then**
  that slot reads "not mapped yet" and every other bound slot still renders normally.
- **AC-5** **Given** a fetch fails after a previously successful load, **when** the user reopens the page,
  **then** the last successfully loaded breakdown stays visible, marked stale, with a retry affordance.
- **AC-6** **Given** the device is offline after a previously successful load, **when** the user reopens the
  page, **then** the last cached breakdown displays with an offline marker.
- **AC-7** **Given** a user without site access to this work unit's site, **when** they try to open the page,
  **then** access is refused, naming who to contact.
- **AC-8** **Given** the page is open, **when** the user looks for a way to change a binding, **then** no
  create/edit affordance exists anywhere on the page, and the only path forward is a link to `SCR-FND-KPI-002`
  (or `SCR-FND-KPI-001`/Product Owner for the catalog).
- **AC-9 (conversion rule)** **Given** the Performance counter counts in pack and the operation's standard is per
  carton, with the product's conversion 1 carton = 12 pack, **when** the page is opened, **then** the Performance
  row reads "Counter: pack → standard per carton: ÷ 12 (1 carton = 12 pack)" and shows no live counts.
- **AC-10 (no conversion path)** **Given** the same units with no active conversion for the product, **when** the
  page is opened, **then** the Performance row reads "Cannot be calculated: no pack ↔ carton conversion for
  \<product code\>" (product code filled in), never 0%.
- **AC-11 (which product)** **Given** `FG-1001` is running now on the work unit and `FG-2002` ran earlier today,
  **when** the page is opened, **then** the rule shows "Rule for FG-1001 — running now". **Given** nothing is
  running, **then** it shows the last product run with its date and time.

**8. Metrics & events**

- **Metric:** `kpi_breakdown_viewed_before_edit`, the share of `WORK_UNIT_KPI_BINDING` edits on
  `SCR-FND-KPI-002` for a slot that were preceded, in the same session, by opening this page for the same slot
  — evidence the breakdown page is informing edits rather than being skipped. Baseline: not measured yet —
  measure first at 30 days after release. Target: `TBD — perlu konfirmasi PO`, depends on how often bindings
  are expected to change post-rollout.
- **Counter-metric:** the count of support/troubleshooting requests to `foundation-domain` asking "why does this
  KPI read X" for a work unit that already has this page — should trend toward 0 as this page absorbs that need.
- **Events:** `kpi_breakdown_viewed` (`work_unit_id`, `metric`, `resolved_detection_method`) ·
  `kpi_breakdown_slot_unmapped` (`work_unit_id`, `slot_id`) · `kpi_breakdown_edit_link_followed`
  (`work_unit_id`, `slot_id`, `target_screen`).

**Dependencies:** `US-FND-KPI-007`

---

> **Stories `US-FND-KPI-009` to `012`** cover four more ISO 22400-2 KPIs from [F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#newly-adopted-kpis-2026-09-10) and [F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#newly-adopted-kpis-2026-09-10): Scrap ratio, Rework ratio, Throughput rate, Production process ratio.
> - All four bind at **Work Unit** scope, the same `WORK_UNIT × SHIFT_INSTANCE` grain as `US-FND-KPI-001` to `003`, and reuse existing `WORK_UNIT_KPI_BINDING` slots.
> - Scrap ratio uses exactly the same inputs as Quality. The other three read two inputs that come straight from the job record, not from a machine tag (non-bindable `KPI_FORMULA_SLOT` rows `throughput.order_execution_time` and `rework.output`, read from `WORK_ORDER_OPERATION`). No new kind of tag (`tag_role`) is needed.
> - Risk: anything read from the machine-stop log waits on B5, as in `US-FND-KPI-001`/`004`/`005` (`ASSET_STATE_LOG` is PRODUCTION's since 2026-09-28; its entity spec is still a draft). B5 doesn't affect `US-FND-KPI-009`/`010`/`011`, because they read tags or job fields (`WORK_ORDER_OPERATION`), not the stop log (`ASSET_STATE_LOG`). `US-FND-KPI-012` reads APT, so it inherits B5 through `US-FND-KPI-001`.

---

#### US-FND-KPI-009

**Compute Scrap ratio from the same Quality inputs**

**Status:** 🟢 Ready

> **In short:** Scrap ratio is the share of output that was thrown away. It uses exactly the same figures as
> Quality and sits next to it, so the supervisor doesn't have to work it out from Quality.

**1. Story**

As a **production supervisor**, I want a Scrap ratio next to Quality, so that I can see what share of output
was scrapped without doing the subtraction from Quality myself.

**2. Context**

- **Nothing new needed:** Scrap ratio is a restatement of the same figures [`US-FND-KPI-003`](#us-fnd-kpi-003)
  already computes — it needs no new tag, no new entity, and no new `WORK_UNIT_KPI_BINDING` row
  ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#scrap-ratio-iso-clause-6-catalog-quality-domain)).
- **ISO grouping:** ISO groups it as a Quality-domain KPI in the Clause 6 catalog, distinct from — but built from
  the same numbers as — Quality ratio (`GQ/PQ`).
- **Why both together:** where Quality answers "how good was output," Scrap ratio answers "how much of it was
  thrown away," and the two numbers are shown together because a supervisor reading one usually wants the other
  in the same glance.
- **Who and where:** a production supervisor on the shop floor, same device and network conditions as
  `US-FND-KPI-003`.

**3. Expectation**

- The Scrap ratio figure is shown alongside the Quality figure on the same screen, sharing its provenance line
  (which pair filled good/total/reject).
- While the shift is `open`, labelled provisional with the last update time, same as Quality.
- Readable within 3 seconds: the Scrap ratio figure next to Quality.

| State | What the user sees |
|-------|--------------------|
| Default | Scrap ratio next to Quality, as above |
| Empty | No output recorded yet: "no data", not 0% |
| Cannot compute | `total = 0` while other records exist. Show "cannot compute" with its reason, same as Quality's own `PQ = 0` rule |
| No data | Whichever pair-fill rule leaves Quality at "no data" leaves Scrap ratio at "no data" too — they share one input resolution, not two independent ones |
| Loading | Display skeleton, shared with Quality's |
| Error | The last figure with a stale marker |
| Offline | The locally stored figure with an offline marker |
| No permission | As in [`US-FND-KPI-001`](#us-fnd-kpi-001) |

**4. Calculation**

*How Scrap ratio is worked out* ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#scrap-ratio-iso-clause-6-catalog-quality-domain))
- Scrap ratio is the share of the total output that was thrown away. It restates the same figures Quality
  already resolved.
- Example: 50 rejects out of 1,000 total → **5.0%** (full walk-through below).

> [!note]- Exact formula (for developers)
> ```
> Scrap ratio = SQ / PQ
> ```
>
> Where the inputs come from:
>
> - `SQ` ← the same `Σ reject` figure [`US-FND-KPI-003`](#us-fnd-kpi-003) resolves through whichever pair-fill
>   path applied (`WORK_ORDER_OPERATION.scrap_qty`, or tag role `reject`, or derived).
> - `PQ` ← the same `total` figure Quality uses.

*Worked example* (same shift as Quality's own worked example)
1. `total = 1,000`, `Σ reject = 50`.
2. `Scrap ratio = 50 / 1,000 = 0.050`, that is **5.0%**.

*Edge cases*
- `PQ = 0` → "cannot compute," not 0%, same rule as Quality's own `total = 0` case.
- Any anomaly `reconcile_check` raises on Quality's inputs (§`US-FND-KPI-003` AC-4) applies identically here —
  Scrap ratio is never shown as a clean number while its source figures are flagged anomalous.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | The same inputs as [`US-FND-KPI-003`](#us-fnd-kpi-003) — `ASSET_TAGS` with roles `good`/`total`/`reject`/`reject_reason`, `WORK_ORDER_OPERATION` for job quantities, `WORK_UNIT_KPI_BINDING` for the `quality.good`/`quality.total`/`quality.reject` slots, and a manual reject entry (`WORK_ORDER_OPERATION_DEFECT`) converted to `OPERATION.uom_id` exactly as in `US-FND-KPI-003` (PO 2026-10-01, Q-28). Bound PLC tags share one unit, enforced at binding |
| **Writes** | `KPI_RESULT` (PK `kpi_result_id`) with `metric = scrap_ratio`, `inputs_used` (storage `TBD`, K-12) pointing at the same `WORK_UNIT_KPI_BINDING` rows Quality's result already cites for the same period. No writes to any other FOUNDATION entity |
| **Not real fields: don't add** | No new slot, no new entity |

**6. Rules & constraints**

1. Scrap ratio and Quality share one input resolution — computing one without the other from the same period's
   data is forbidden.
2. `PQ = 0` → "cannot compute", never 0%.
3. An anomaly on Quality's inputs (mismatched good+reject vs total) blocks Scrap ratio the same way it blocks
   Quality.
4. Access and offline behaviour as in [`US-FND-KPI-001`](#us-fnd-kpi-001).
5. Aggregating to area or site sums `SQ` and `PQ` first, then divides — never an average of per-line Scrap
   ratios, same discipline as every other ratio in this module.

**7. Acceptance criteria**

- **AC-1** **Given** `total = 1,000` and `Σ reject = 50` (Quality's own worked example), **when** Scrap ratio is
  computed, **then** the result is 5.0%.
- **AC-2 (shared no-data)** **Given** Quality is "no data" because only one of good/total/reject is available,
  **when** Scrap ratio is computed for the same period, **then** it is also "no data", not a separately-resolved
  value.
- **AC-3 (cannot compute)** **Given** `total = 0`, **when** Scrap ratio is computed, **then** "cannot compute"
  appears, not 0%.
- **AC-4 (anomaly)** **Given** Quality's inputs are flagged anomalous (good + reject ≠ total), **when** Scrap
  ratio is computed for the same period, **then** it is also marked anomalous and not shown as a clean number.
- **AC-5 (aggregation)** **Given** line A with `SQ` 50/`PQ` 1,000 and line B with `SQ` 20/`PQ` 500, **when**
  area Scrap ratio is computed, **then** the result is (50 + 20) / (1,000 + 500) = 4.7%, not the average of
  5.0% and 4.0%.
- **AC-6 (permission)** **Given** a supervisor with no role on that line, **when** they try to open it,
  **then** that line is not in their list.

**8. Metrics & events**

- **Metric:** `scrap_ratio_computed`, the share of the pilot line's `closed` shifts whose Scrap ratio is
  computed. Baseline 0% on 2026-09-10. Target 90% by 30 days after release, tracking `quality_computed`'s own
  target since the two share inputs. Source: the calculation job's output table.
- **Counter-metric:** `figures_shown_without_complete_input` must be 0, same counter-metric as every other
  ratio in this module.
- **Events:** `scrap_ratio_computed` (`work_unit_id`, `business_date`, `scrap_qty`, `produced_qty`) ·
  `scrap_ratio_no_data` (`work_unit_id`, `available_input`).

**Dependencies:** `US-FND-KPI-003`

---

#### US-FND-KPI-010

**Compute Rework ratio from job rework quantity**

**Status:** 🟢 Ready

> **In short:** Rework ratio is the share of output that needed rework but was recovered. Shown next to
> Quality and Scrap ratio, it tells the supervisor whether a low Quality comes from lost output or from
> output fixed at extra cost.

**1. Story**

As a **production supervisor**, I want a Rework ratio next to Quality and Scrap ratio, so that I can tell apart
output that was scrapped from output that was reworked and still recovered.

**2. Context**

- **Why separate rework:** scrap and rework are both "not good on the first pass," but they have very different
  cost and floor-response implications — scrap is lost, rework is recovered at extra cost. Folding both into one
  "not good" bucket hides which one is driving a low Quality figure.
- **Uses an existing field:** Rework ratio isolates the rework share using a field FOUNDATION already reads
  elsewhere (`WORK_ORDER_OPERATION.rework_qty`, the same field job-schedule's
  `total_output = good_qty + scrap_qty + rework_qty` already names)
  ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#rework-ratio-iso-clause-6-catalog-quality-domain),
  [F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#newly-adopted-kpis-2026-09-10)).
- **Who and where:** a production supervisor on the shop floor, same conditions as Quality and Scrap ratio,
  shown in the same group.

**3. Expectation**

- The Rework ratio figure is shown alongside Quality and Scrap ratio, sharing the same "which pair filled it"
  provenance line for `PQ`.
- While the shift is `open`, labelled provisional with the last update time.
- Readable within 3 seconds: the Rework ratio figure and, when absent, which side (PQ or rework tracking) is
  missing.

| State | What the user sees |
|-------|--------------------|
| Default | Rework ratio next to Quality and Scrap ratio, as above |
| Empty | No output recorded yet: "no data", not 0% |
| Cannot compute | `PQ = 0` while other records exist. Show "cannot compute" with its reason |
| No data (rework not tracked) | `rework_qty` not recorded — the work order predates `WORK_ORDER_OPERATION_DEFECT`, or defect-code split isn't active for that work order ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#newly-adopted-kpis-2026-09-10)). Shows "no data" naming that reason specifically, **not** 0%, and distinct from the "PQ unavailable" no-data case so the supervisor knows which side is missing |
| Loading | Display skeleton |
| Error | The last figure with a stale marker |
| Offline | The locally stored figure with an offline marker |
| No permission | As in [`US-FND-KPI-001`](#us-fnd-kpi-001) |

**4. Calculation**

*How Rework ratio is worked out* ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#rework-ratio-iso-clause-6-catalog-quality-domain))
- Rework ratio is the share of the total output that needed rework but was recovered. The rework quantity
  comes from the job record only.
- Example: 20 reworked out of 1,000 total → **2.0%** (full walk-through below).

> [!note]- Exact formula (for developers)
> ```
> Rework ratio = RWQ / PQ
> ```
>
> Where the inputs come from:
>
> - `RWQ` ← `WORK_ORDER_OPERATION.rework_qty` (PRODUCTION) — a job-quantity field, not a `tag_role`, the same
>   non-tag treatment `good_qty`/`scrap_qty` already have under Quality.
> - `PQ` ← the same `total` figure Quality and Scrap ratio use.

*Worked example*
1. 1,000 total, 950 good, 30 scrap, 20 rework.
2. `Rework ratio = 20 / 1,000 = 0.020`, that is **2.0%**.

*Edge cases*
- `PQ = 0` → "cannot compute", not 0%.
- `rework_qty` not recorded for that work order → "no data", distinct from `PQ = 0`.
- Do not add `rework_qty` on top of `Σ reject` from tags for the same units — that double-counts, the same rule
  Quality's own tag-vs-job mixing prohibition already states ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#quality)).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER_OPERATION.rework_qty` (PRODUCTION), plus the same `total`/`PQ` inputs [`US-FND-KPI-003`](#us-fnd-kpi-003) and [`US-FND-KPI-009`](#us-fnd-kpi-009) already read |
| **Writes** | `KPI_RESULT` (PK `kpi_result_id`) with `metric = rework_ratio`, `inputs_used` (storage `TBD`, K-12) pointing at the `quality.total` binding plus a direct reference to the `WORK_ORDER_OPERATION` row (not a binding, since `rework.output` isn't bindable). No writes to any other FOUNDATION entity |
| **Not real fields: don't add** | No new `tag_role` |

- **No binding for the numerator:** `RWQ` resolves through the non-bindable `KPI_FORMULA_SLOT` row
  `rework.output` ([F-07.1 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#91-kpi_formula_slot),
  `bindable = false`) — no `WORK_UNIT_KPI_BINDING` row is created or read for it, the value comes straight off
  the job record.

**6. Rules & constraints**

1. The Rework ratio denominator is `PQ`, the same figure Quality and Scrap ratio use.
2. `rework_qty` comes only from `WORK_ORDER_OPERATION` — never derived from tags, never summed with `Σ reject`.
3. `PQ = 0` → "cannot compute". `rework_qty` not recorded → "no data" — the two are distinct statuses, not
   interchangeable.
4. No `WORK_UNIT_KPI_BINDING` row exists or is expected for this metric's numerator — `rework.output` is
   `bindable = false`.
5. Access and offline behaviour as in [`US-FND-KPI-001`](#us-fnd-kpi-001).
6. Aggregating to area or site sums `RWQ` and `PQ` first, then divides — never an average of per-line Rework
   ratios.

**7. Acceptance criteria**

- **AC-1** **Given** `total = 1,000` and `rework_qty = 20`, **when** Rework ratio is computed, **then** the
  result is 2.0%.
- **AC-2 (cannot compute)** **Given** `PQ = 0`, **when** Rework ratio is computed, **then** "cannot compute"
  appears, not 0%.
- **AC-3 (rework not tracked)** **Given** the work order has no rework quantity recorded because the defect-code split
  isn't active for it, **when** Rework ratio is computed, **then** "no data" appears naming rework tracking as
  the missing side, distinct from a `PQ = 0` case.
- **AC-4 (no double-count)** **Given** tags report `reject = 50` and the job separately reports
  `rework_qty = 20` for the same units, **when** Rework ratio is computed, **then** the 20 rework units are not
  also added into Scrap ratio's `SQ`.
- **AC-5 (aggregation)** **Given** line A with `RWQ` 20/`PQ` 1,000 and line B with `RWQ` 5/`PQ` 500, **when**
  area Rework ratio is computed, **then** the result is (20 + 5) / (1,000 + 500) = 1.7%, not the average of 2.0%
  and 1.0%.
- **AC-6 (permission)** **Given** a supervisor with no role on that line, **when** they try to open it,
  **then** that line is not in their list.

**8. Metrics & events**

- **Metric:** `rework_ratio_computed`, the share of the pilot line's `closed` shifts whose Rework ratio is
  computed (neither "no data" nor "cannot compute"). Baseline 0% on 2026-09-10. Target:
  `TBD — perlu konfirmasi PO` — depends on how widely `WORK_ORDER_OPERATION_DEFECT`/rework tracking is deployed
  at the pilot site, which this PRD does not yet know. Source: the calculation job's output table.
- **Counter-metric:** `figures_shown_without_complete_input` must be 0.
- **Events:** `rework_ratio_computed` (`work_unit_id`, `business_date`, `rework_qty`, `produced_qty`) ·
  `rework_ratio_no_data` (`work_unit_id`, `reason`).

**Dependencies:** `US-FND-KPI-003`

---

#### US-FND-KPI-011

**Compute Throughput rate from actual order execution time**

**Status:** 🟢 Ready

> **In short:** Throughput rate is how many units came out per minute while this operation was actually
> running (AOET). The supervisor can spot a slow run mid-shift, without waiting for the shift's Performance
> figure.

**1. Story**

As a **production supervisor**, I want a Throughput rate for the operation currently running, so that I can see
output per unit of actual run time without waiting for a full shift's Performance figure.

**2. Context**

- **A narrower question than Performance:** not "how close to standard," but "how much came out per minute this
  operation actually ran," using only the operation's own wall-clock window rather than the whole shift's APT
  ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#throughput-rate-iso-clause-6-catalog-production-domain)).
- **A new element, AOET:** Actual order execution time, read directly off
  `WORK_ORDER_OPERATION.actual_start`/`.actual_end` — a job-level timestamp pair, not a PLC signal, the same
  non-tag treatment `SHIFT_INSTANCE` already gets for POT
  ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#newly-adopted-kpis-2026-09-10)).
- **Who and where:** a production supervisor on the shop floor, same conditions as the rest of this module.
- **When it helps most:** mid-shift, on an operation that is still open, to catch a slow run before the shift
  ends rather than after.

**3. Expectation**

- The Throughput rate figure (units per minute) is shown per running or just-closed operation, with the
  operation's `actual_start`/current time (or `actual_end`) shown beneath it.
- While the operation is still open, labelled provisional with the last update time — the same "temporary" rule
  the rest of this module applies to an open shift.
- Readable within 3 seconds: the Throughput rate figure and whether it is provisional.

| State | What the user sees |
|-------|--------------------|
| Default | The rate with its timestamps, as above |
| Empty | No output recorded yet for this operation: "no data", not 0 |
| No data (timestamps missing) | `actual_start` not yet recorded (operation not yet started) or `actual_end` not yet recorded on an operation that should have closed. Show "no data" naming the missing timestamp, never a divide-by-zero 0 |
| Anomaly | `AOET` computes to 0 or negative (bad timestamp data, e.g. `actual_start = actual_end`). Flagged as an anomaly, not silently shown as an infinite or zero rate |
| Loading | Display skeleton |
| Error | The last figure with a stale marker |
| Offline | The locally stored figure with an offline marker |
| No permission | As in [`US-FND-KPI-001`](#us-fnd-kpi-001) |

**4. Calculation**

*How Throughput rate is worked out* ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#throughput-rate-iso-clause-6-catalog-production-domain))
- Throughput rate is the output per minute while the operation was actually open (AOET).
- Example: 600 units over a 30-minute run window → **20 units/minute** (full walk-through below).

> [!note]- Exact formula (for developers)
> ```
> Throughput rate = PQ / AOET
> AOET            = actual_end − actual_start      in minutes
> ```
>
> Where the inputs come from:
>
> - `PQ` ← the same `total` figure Quality reads, scoped to this operation's `WORK_ORDER_OPERATION` row.
> - `AOET` ← `WORK_ORDER_OPERATION.actual_end − .actual_start`, in minutes, for the same row.

*Worked example*
1. An operation ran from 08:00 to 08:30 (30 minutes) and produced 600 units.
2. `Throughput rate = 600 / 30 = 20` units/minute.

*Edge cases*
- `actual_start`/`actual_end` not yet recorded (operation still open, or never captured) → "no data", not a
  divide-by-zero 0.
- `AOET` computes to 0 (bad data, `actual_start = actual_end`) → status `anomaly`, not a crash and not an
  infinite rate.
- For a still-open operation, `AOET` is computed as `now − actual_start` and the result is always provisional.

*Plant scope*
- The same formula also rolls up to site — "Plant" in ISO 22400-2 naming
  ([F-01](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md)) — as `ΣPQ / ΣAOET` across every work unit
  at the site, never an average of per-line rates.
- Same `sum_of_terms` rule this story already uses in rule 6 for per-operation rollup
  ([F-07 — Plant scope](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#plant-scope--adopted-for-throughput-rate-and-production-process-ratio)).
- No new field — `KPI_RESULT.aggregation_level = site` already exists and is used today for Availability/OEE
  rollups.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER_OPERATION` (`actual_start`, `actual_end`, and the same `total`/output figure Quality reads for that operation's rows), `ASSET_TAGS` where output comes from counters rather than the job row |
| **Writes** | `KPI_RESULT` with `metric = throughput_rate`, scoped to the operation's period (not the whole shift). `aggregation_level` is `work_unit` by default, or `site` for the Plant-scope rollup (`aggregation_method = sum_of_terms`), the same existing field Availability/OEE rollups already use ([F-07.1 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#95-kpi_result)). No writes to any other FOUNDATION entity |
| **Not real fields: don't add** | No `work_order`-level `aggregation_level` exists yet — Production order scope stays out of this story, see [F-07's deferred catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#deferred-iso-22400-2-kpis--why-each-is-blocked) |

- **No binding for AOET:** `AOET` resolves through the non-bindable `KPI_FORMULA_SLOT` row
  `throughput.order_execution_time` ([F-07.1 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#91-kpi_formula_slot),
  `bindable = false`) — no `WORK_UNIT_KPI_BINDING` row exists or is read for it.

**6. Rules & constraints**

1. `AOET` comes only from `WORK_ORDER_OPERATION.actual_start`/`.actual_end` — never approximated from the shift
   clock or from PBT/APT.
2. A missing timestamp → "no data", never a fabricated value.
3. `AOET ≤ 0` → status `anomaly`, never shown as a computed rate.
4. A still-open operation's Throughput rate is always labelled provisional.
5. Access and offline behaviour as in [`US-FND-KPI-001`](#us-fnd-kpi-001).
6. Aggregating beyond one operation sums `PQ` and `AOET` first, then divides — never an average of
   per-operation rates.
7. Plant-scope (`aggregation_level = site`) rollup follows the same rule as rule 6: sum `PQ` and `AOET` across
   every work unit at the site, then divide — never average per-line rates.

**7. Acceptance criteria**

- **AC-1** **Given** an operation ran 08:00 to 08:30 and produced 600 units, **when** Throughput rate is
  computed, **then** the result is 20 units/minute.
- **AC-2 (no data — not started)** **Given** `actual_start` is not yet recorded, **when** Throughput rate is
  computed, **then** "no data" appears naming the missing start timestamp.
- **AC-3 (no data — still open)** **Given** `actual_start` exists but `actual_end` does not, **when** the screen
  is opened mid-operation, **then** Throughput rate is computed as provisional using `now − actual_start` as
  `AOET`, labelled provisional.
- **AC-4 (anomaly)** **Given** `actual_start = actual_end` due to bad data, **when** Throughput rate is
  computed, **then** the result is marked an anomaly, not an infinite or zero rate.
- **AC-5 (permission)** **Given** a supervisor with no role on that line, **when** they try to open it,
  **then** that line is not in their list.
- **AC-6 (Plant scope)** **Given** line A at 600 units / 30 minutes and line B at 400 units / 40 minutes on the
  same site, **when** Throughput rate is computed at Plant (site) scope, **then** the result is (600 + 400) /
  (30 + 40) = 14.3 units/minute, not the 15.0 average of 20 and 10 units/minute.

**8. Metrics & events**

- **Metric:** `throughput_rate_computed`, the share of the pilot line's `closed` work-order operations whose
  Throughput rate is computed. Baseline 0% on 2026-09-10. Target 90% by 30 days after release. Source: the
  calculation job's output table.
- **Counter-metric:** `figures_shown_without_complete_input` must be 0.
- **Events:** `throughput_rate_computed` (`work_unit_id`, `work_order_operation_id`, `produced_qty`,
  `aoet_minutes`, `is_provisional`) · `throughput_rate_no_data` (`work_unit_id`, `work_order_operation_id`,
  `missing_timestamp`) · `throughput_rate_anomaly` (`work_unit_id`, `work_order_operation_id`).

**Dependencies:** `US-FND-KPI-003`, `US-PROD-SHF-001`

---

#### US-FND-KPI-012

**Compute Production process ratio from APT and AOET**

**Status:** 🟡 Partly blocked — APT comes from `US-FND-KPI-001` (B5). Until then the result is "no data" (AC-3).

> **In short:** Production process ratio is how much of an operation's open window (AOET) was really spent
> producing (APT). A low ratio tells the supervisor the operation was open but not producing, which is a
> different problem from a shift-level Availability loss.

**1. Story**

As a **production supervisor**, I want to see what share of an operation's actual run window was genuinely
productive time, so that I can tell a slow-starting operation from one that ran productively the whole time it
was open.

**2. Context**

- **Two windows, two questions:** `APT` is how much of the *shift* was spent producing (Availability's own
  numerator), and `AOET` is how long this *operation* was actually open end to end.
- **What a low ratio means:** a ratio well under 100% on an operation that otherwise shows good Availability
  points at time spent open but not producing within that operation's own window — a distinct signal from a
  shift-level Availability loss ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#production-process-ratio-iso-clause-6-catalog-production-domain)).
- **Who and where:** a production supervisor on the shop floor, shown next to Throughput rate since both use
  `AOET`.

**3. Expectation**

- The Production process ratio figure is shown next to Throughput rate for the same operation, with `APT` and
  `AOET` shown beneath it in minutes.
- While the operation is open, labelled provisional.
- Readable within 3 seconds: the Production process ratio figure and whether it is provisional.

| State | What the user sees |
|-------|--------------------|
| Default | The ratio with APT and AOET beneath, as above |
| No data | Either `APT` (from [`US-FND-KPI-001`](#us-fnd-kpi-001)) or `AOET` ([`US-FND-KPI-011`](#us-fnd-kpi-011)) is itself "no data". Show "no data" naming which side is missing, do not partially compute |
| Anomaly | `AOET = 0` or not yet recorded, or the ratio computes above 100% (APT exceeding the operation's own window, a data anomaly rather than a valid result). Flagged, not silently clamped |
| Loading | Display skeleton |
| Error | The last figure with a stale marker |
| Offline | The locally stored figure with an offline marker |
| No permission | As in [`US-FND-KPI-001`](#us-fnd-kpi-001) |

**4. Calculation**

*How Production process ratio is worked out* ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#production-process-ratio-iso-clause-6-catalog-production-domain))
- The ratio is the producing time (APT) as a share of the window the operation was open (AOET).
- Example: 378 minutes of APT inside a 400-minute run window → **94.5%** (full walk-through below).

> [!note]- Exact formula (for developers)
> ```
> Production process ratio = APT / AOET
> ```
>
> Where the inputs come from:
>
> - `APT` ← the same Availability numerator [`US-FND-KPI-001`](#us-fnd-kpi-001) already computes, for the shift
>   containing this operation.
> - `AOET` ← the same value [`US-FND-KPI-011`](#us-fnd-kpi-011) computes for this operation.

*Worked example*
1. `APT` 378 minutes (from the Availability worked example), this operation's `AOET` 400 minutes.
2. `Production process ratio = 378 / 400 = 0.945`, that is **94.5%**.

*Edge cases*
- `AOET = 0` or not yet recorded → "no data".
- `APT` "no data" (Availability itself unresolved, per B5) → this metric is "no data" too, it never substitutes
  a partial value.
- A result above 100% is a data anomaly (APT drawn from a shift window inconsistent with this operation's own
  `AOET`), flagged, never clamped to 100%.

*Plant scope*
- The same formula also rolls up to site — "Plant" in ISO 22400-2 naming
  ([F-01](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md)) — as `ΣAPT / ΣAOET` across every work unit
  at the site, never an average of per-line ratios.
- Same `sum_of_terms` rule this story already uses in rule 5 for per-operation rollup
  ([F-07 — Plant scope](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#plant-scope--adopted-for-throughput-rate-and-production-process-ratio)).
- No new field — `KPI_RESULT.aggregation_level = site` already exists and is used today for Availability/OEE
  rollups.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | The `APT` component of [`US-FND-KPI-001`](#us-fnd-kpi-001)'s `KPI_RESULT` (`metric = availability`), and `WORK_ORDER_OPERATION.actual_start`/`.actual_end` for `AOET`, the same non-bindable `throughput.order_execution_time` slot [`US-FND-KPI-011`](#us-fnd-kpi-011) reads |
| **Writes** | `KPI_RESULT` with `metric = production_process_ratio`. `aggregation_level` is `work_unit` by default, or `site` for the Plant-scope rollup (`aggregation_method = sum_of_terms`), the same existing field Availability/OEE rollups already use ([F-07.1 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#95-kpi_result)). No writes to any other FOUNDATION entity |
| **Not real fields: don't add** | No new slot, no new `tag_role` |

**6. Rules & constraints**

1. Both `APT` and `AOET` must be individually computed before this ratio is computed — no partial substitution.
2. `AOET = 0` or missing → "no data".
3. A result above 100% is an anomaly, never clamped.
4. Access and offline behaviour as in [`US-FND-KPI-001`](#us-fnd-kpi-001).
5. Aggregating beyond one operation sums `APT` and `AOET` first, then divides — never an average of
   per-operation ratios.
6. Plant-scope (`aggregation_level = site`) rollup follows the same rule as rule 5: sum `APT` and `AOET` across
   every work unit at the site, then divide — never average per-line ratios.

**7. Acceptance criteria**

- **AC-1** **Given** `APT` 378 minutes and this operation's `AOET` 400 minutes, **when** Production process
  ratio is computed, **then** the result is 94.5%.
- **AC-2 (no data — AOET)** **Given** `AOET` is not yet recorded, **when** the ratio is computed, **then**
  "no data" appears naming `AOET` as the missing side.
- **AC-3 (no data — APT)** **Given** Availability itself reads "no data" for this shift (per B5), **when** the
  ratio is computed, **then** it also reads "no data", not a partial value.
- **AC-4 (anomaly)** **Given** `APT` 450 minutes and `AOET` 400 minutes for the same operation, **when** the
  ratio is computed, **then** the result is marked an anomaly at 112.5% rather than clamped to 100%.
- **AC-5 (permission)** **Given** a supervisor with no role on that line, **when** they try to open it,
  **then** that line is not in their list.
- **AC-6 (Plant scope)** **Given** line A with APT 378 and AOET 400, and line B with APT 180 and AOET 200 on the
  same site, **when** Production process ratio is computed at Plant (site) scope, **then** the result is
  (378 + 180) / (400 + 200) = 93.0%, not the 92.25% average of 94.5% and 90.0%.

**8. Metrics & events**

- **Metric:** `production_process_ratio_computed`, the share of the pilot line's `closed` work-order operations
  whose Production process ratio is computed. Baseline 0% on 2026-09-10. Target 90% by 30 days after release.
  Source: the calculation job's output table.
- **Counter-metrics:** `figures_shown_without_complete_input` must be 0, and the count of ratios shown above
  100% without an anomaly marker must be 0.
- **Events:** `production_process_ratio_computed` (`work_unit_id`, `work_order_operation_id`, `apt_minutes`,
  `aoet_minutes`, `is_provisional`) · `production_process_ratio_no_data` (`work_unit_id`,
  `work_order_operation_id`, `missing_input`) · `production_process_ratio_anomaly` (`work_unit_id`,
  `work_order_operation_id`, `value`).

**Dependencies:** `US-FND-KPI-001`, `US-FND-KPI-011`

#### US-FND-KPI-013

**Compute Fall-off ratio per Job Order**

**Status:** 🟢 Ready — added 2026-09-27 at the PO's request. Every input already exists; Fall-off ratio is adopted
only at Job Order scope ([F-07](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#production-order-scope--adopted-2026-09-10)). Until this
story is built, Production screens show the Fall-off column as "Not available yet".

> **In short:** for one Job Order, Fall-off ratio is the share of what entered the routing at the first step that
> did not come out good at a later step. It exists only per Job Order, never per shift or per line.

**1. Story**

As a **production supervisor**, I want the Fall-off ratio of each Job Order, so that I can see what share of what
entered the routing did not come out good at a later step.

**2. Context**

- **Why:** Scrap ratio and Rework ratio look at one step. On a multi-step routing, loss piles up step by step;
  Fall-off ratio shows the total loss between the first step and the step being checked
  ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#production-order-scope-2026-09-10-po-decision)).
- **A Production KPI** (`KPI_FORMULA_SLOT.domain = production`, PO 2026-09-27), although ISO 22400-2's grouping
  files it under Quality.
- **Who and where:** supervisor or plant manager, on the Job Order report (PRD-004 `US-PROD-PRF-002`) after or
  during the order.

**3. Expectation**

- For a chosen Job Order and a chosen step, one figure: Fall-off ratio, with the two quantities it came from
  ("entered at step 1" and "good at step N").
- While any shift in the order is `open`, labelled provisional.

| State | What the user sees |
|-------|--------------------|
| Default | The figure and its two quantities |
| Empty | The order has no recorded output at its first step yet: "no data", not 0% |
| Cannot compute | Output at the first step is 0: "cannot compute" with its reason |
| Wrong scope | Asked for per shift, per line or per site: not offered — Fall-off ratio exists only per Job Order |
| Loading | Display skeleton |
| Error | The last figure with a stale marker |
| Offline | The locally stored figure with an offline marker |
| No permission | As in [`US-FND-KPI-001`](#us-fnd-kpi-001) |

**4. Calculation**

*How Fall-off ratio is worked out* ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#production-order-scope-2026-09-10-po-decision))
- Take everything produced at the routing's first step across the whole order (good, scrap and rework). Take the
  good output of the step being checked, across the whole order. Fall-off ratio is the share of the first that is
  missing from the second.
- Example: 980 entered at step 1, 860 good at step 3 → **12.2%**.

> [!note]- Exact formula (for developers)
> ```
> Fall-off ratio = (PQ_first_op − GQ_current_op) / PQ_first_op
> ```
>
> - `PQ_first_op` ← `Σ (good_qty + scrap_qty + rework_qty)` of every `WORK_ORDER_OPERATION` row for that
>   `work_order_id` whose `operation_id` is the routing's first step (`OPERATION.step_number` minimum for
>   `WORK_ORDER.routing_id`).
> - `GQ_current_op` ← `Σ good_qty` of every `WORK_ORDER_OPERATION` row for that `work_order_id` and the `operation_id`
>   being checked.
> - Stored as `KPI_RESULT` with `metric = fall_off_ratio`, `aggregation_level = work_order`.

*Worked example* (F-07.1)
1. Job Order `WO-1042`, routing with 3 steps. Step 1 ran two shifts: 500 + 480 = 980 produced.
2. Step 3 ran one shift: 860 good.
3. `Fall-off ratio = (980 − 860) / 980 = 0.122`, that is **12.2%**.

*Edge cases*
- No `WORK_ORDER_OPERATION` row yet for the first step → "no data".
- `PQ_first_op = 0` → "cannot compute", not 0%.
- Checking the first step itself gives the share of step-1 output that wasn't good — the formula applies unchanged.
- Rows without `actual_start` / `actual_end` still count here (Fall-off ratio doesn't use AOET).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER_OPERATION` (`work_order_id`, `operation_id`, `good_qty`, `scrap_qty`, `rework_qty`) and `WORK_ORDER.routing_id` (PRODUCTION); `OPERATION.step_number` ([F-08 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#92-operation--one-step-of-a-routing-at-one-work-unit-with-its-standard)) |
| **Writes** | `KPI_RESULT` with `metric = fall_off_ratio`, `aggregation_level = work_order`, `aggregation_method = sum_of_terms` ([F-07.1 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#95-kpi_result)) |
| **Not real fields: don't add** | No new `tag_role`, no slot binding — the inputs are job quantities |

**6. Rules & constraints**

1. Only at Job Order scope (`aggregation_level = work_order`). Never per shift, work unit, area or site.
2. Sum each side across every shift and row of the order first, then divide — never an average of per-shift ratios.
3. `PQ_first_op = 0` → "cannot compute"; no first-step row → "no data". Two different statuses.
4. Quantities come only from `WORK_ORDER_OPERATION`, never from tags.
5. Access and offline behaviour as in [`US-FND-KPI-001`](#us-fnd-kpi-001).

**7. Acceptance criteria**

- **AC-1** **Given** `WO-1042` with 980 produced at step 1 and 860 good at step 3, **when** Fall-off ratio is
  computed for step 3, **then** the result is 12.2%.
- **AC-2 (no data)** **Given** an order with no recorded row for its first step, **when** it is computed, **then**
  "no data" appears, not 0%.
- **AC-3 (cannot compute)** **Given** step 1's produced quantity is 0, **when** it is computed, **then** "cannot
  compute" appears.
- **AC-4 (scope)** **Given** a request for Fall-off ratio per shift or per site, **when** results are listed,
  **then** none is produced.
- **AC-5 (sum first)** **Given** step 1 ran in two shifts (500 and 480), **when** it is computed, **then** 980 is
  used as `PQ_first_op`, not each shift on its own.

**8. Metrics & events**

- **Metric:** `fall_off_ratio_computed`, the share of completed Job Orders on the pilot line with a computed
  Fall-off ratio. Baseline 0% on 2026-09-27; target `TBD — perlu konfirmasi PO`. Source: the calculation job's output.
- **Counter-metric:** `figures_shown_without_complete_input` must be 0.
- **Events:** `fall_off_ratio_computed` (`work_order_id`, `operation_id`, `pq_first_op`, `gq_current_op`) ·
  `fall_off_ratio_no_data` (`work_order_id`, `reason`).

**Dependencies:** `US-FND-KPI-003`

---

## Change notes (history — not needed to build)

- `KPI_RESULT` stories · 2026-10-01 — prd-sync c1f1848..30c7087 (keys rule): PK `kpi_result_id`; `inputs_used` / `waterfall` storage `TBD` (K-12); B5 notes: row ID now `asset_state_log_id`; open items K-12–K-15.
- `US-FND-KPI-003`, `US-FND-KPI-009` · 2026-10-01 — prd-sync `docs-molcadx` 787e819..725362a, Q-28 resolved (PO): manual reject in any unit with a conversion path, Quality calculates in `OPERATION.uom_id`; machine total + manual reject is an allowed pair (new path 3, `source_tier = manual`); edge case "No conversion for this unit"; AC-18 now testable, AC-19/AC-20 added. `KPI-003` 🟡 → 🟢. F-07 anchor renamed (*Reject entered manually, in any unit*).
- `US-FND-KPI-006`, `US-FND-KPI-008` · 2026-10-01 — prd-sync `docs-molcadx` 787e819..725362a, K-6 and the threshold part of K-8 resolved (PO): two stop thresholds `min_stop_seconds` 60 s / `small_stop_max_seconds` 300 s, per work center with work unit override, one pair for stall and range detection, in F-12. `KPI-006` 🔴 → 🟡: three-way bucketing, AC-7/AC-8 testable, AC-10–AC-12 added; still open K-11 (Q-31) and storage (Q-32). `KPI-008`: AC-3 now waits on K-10 (band ownership, split from K-8). Terms table `small_stop` updated.
- `US-FND-KPI-001` · 2026-09-03 — Risk note added after contest review B1/B5. F-02 (Shift) module was downgraded `ACTIVE` → `DRAFT`; A-5 (`ASSET_STATE_LOG` ownership) was reopened.
- `US-FND-KPI-001` · (PRD-001, frozen 2026-09-03) — Storing PBT inside the shift clock is the shape PRD-001 uses; its consequence is worked out in [PRD-001 §13](../PRD-001-molcadx-core-q3-en.md#131-the-availability-formula-that-was-corrected).
- `US-FND-KPI-001`, `US-FND-KPI-002`, `US-FND-KPI-003`, `US-FND-KPI-004` · 2026-09-10 — Time period scope (explicit `business_date` range via `KPI_RESULT.period`) added, with rules ⑪/⑨/⑩/⑨ and ACs AC-13/AC-11/AC-13/AC-12. No schema change.
- `US-FND-KPI-004` · undated — An earlier draft gated OEE on `WORK_CENTER.is_oee_tracked = true`; F-01 has no such field.
- `US-FND-KPI-005` · 2026-09-03 — A-5 reopened.
- `US-FND-KPI-007` · undated — K-7 resolved: binding is an ordinary site-scoped FOUNDATION admin action.
- `US-FND-KPI-008` · undated — F-07.1 recorded the missing view-only page once K-7 resolved ("is now also buildable"); UX 07 and FLOW-05 declared the screen afterwards, and this story was added for it.
- `US-FND-KPI-009` to `US-FND-KPI-012` · 2026-09-10 — Added on Product Owner request to widen ISO 22400-2 adoption, the same day F-07 and F-07.1 adopted the four KPIs.
- `US-FND-KPI-010` · 2026-09-10 — `WORK_ORDER_OPERATION.rework_qty` was not wired into any KPI before this date.
- `US-FND-KPI-011`, `US-FND-KPI-012` · 2026-09-10 — Plant (site) scope rollup added, with rules ⑦/⑥ and AC-6.
- `US-FND-KPI-001`/`004`/`012` · 2026-09-23 — B1 dropped from their risk notes and status: B1 closed 2026-09-10/11 (`SHIFT_INSTANCE` generator owned by `production-domain`, F-02 H-6). The 2026-09-11 sync only updated `US-FND-SHF-002`. B5 remains.
- `US-FND-KPI-012` · 2026-09-23 — AC-6 arithmetic corrected: line B is 180/200 = 90.0% (was written 95.0%), so the naive average is 92.25% (was 94.75%). The expected result, 93.0%, was already right.
- All stories · 2026-09-23 — detail blocks rewritten from one dense table into numbered sections (Story … Metrics & events), with an "In short" summary per story; "subject whose IDP-asserted scope claim covers …" replaced by *work-unit access* / *site access* (defined in the README glossary); rule/path numbers ①②③ written as 1, 2, 3. Meaning unchanged; UI copy kept verbatim.
- All stories · 2026-09-23, second pass — every formula now has a plain-language explanation and a quick
  example above it, and the exact formula with its variable sources (field names) moved verbatim into a
  collapsible "Exact formula (for developers)" box under the explanation. Worked examples, rules,
  acceptance criteria, UI copy, story IDs and event names unchanged; KPI variable sources stay named in the
  boxes because F-07 requires every formula to name each variable's source down to the field.
- `US-FND-KPI-007`/`008` and the 009–012 intro note (2026-09-23, finishing the plain-language pass): calculation rules restated in plain words; the exact original rules kept word for word in "Exact rules (for developers)" boxes.
- `US-FND-KPI-001`, `002`, `003`, `006`, `007` and the Step 0 note — 2026-09-27 (prd-sync docs-molcadx 3cb17fa..7ad3ed8; PO decision 2026-09-25 multi-lane machine and generated OPC UA addresses, F-07.1 "Where tags live" / primary vs secondary / §9.4, F-04 §9.6/§9.7.1): Step 0 now filters per-lane tag roles by `ASSET_TAGS.work_unit_id` while machine-wide roles apply to every lane; tags are read by `tag_id`, never by address. `KPI-001` rule 12 + AC-10 (one machine stop = downtime on every lane, each lane computed on its own); `KPI-002` rule 10; `KPI-003` rule 11; `KPI-006` rule 1 (that lane's `total`); `KPI-007` validation rule 2/rule 3 + AC-9 (Lane 2's counter can't be bound to Lane 1; machine-wide tags on any lane). No status change.
- `US-FND-KPI-007` · 2026-09-27 — One sidebar item per domain (`KPI_FORMULA_SLOT.domain`, F-07.1 §9.1; UX 07 KPI sidebar group, PO): every adopted KPI is a Production KPI; Quality / Maintenance / Inventory are always shown and open empty. New AC-10; screen text copied (draft).
- `US-FND-KPI-013` · 2026-09-27 — New story (PO request): Fall-off ratio per Job Order, the one adopted KPI (F-07.1, 2026-09-10) that had no story.
- `US-FND-KPI-008` · 2026-09-27 — Rule 7: the calculation page keeps the domain of the KPI page it was opened from (UX 07 KPI sidebar group, PO).
- `US-FND-KPI-007` · 2026-09-27 (prd-sync docs-molcadx 7ad3ed8..7a59537) — K-9 (PO): only domains the tenant is entitled to appear in the KPI sidebar; hidden, not locked (PLT-01). Access rule adds the entitlement check; new AC-11.
- `US-FND-KPI-007` · 2026-09-28 (PO, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) A; prd-sync docs-molcadx 7fb3a9a..1285317) — sidebar grouped by
  domain: KPI is an item inside each domain group, not a "KPI" group with one item per domain. K-9 revised: an
  unlicensed domain shows as one locked group heading (PLT-01 core concept 3a), its items hidden. Retired
  `kpi-sidebar.*` keys replaced by the `nav-sidebar.*` keys from UX 00, copied word for word. AC-10 and AC-11
  reworded to the new layout.
- `US-FND-KPI-002` · 2026-09-28 (PO, decision B/C; same sync) — `OPERATION.uom_id` is the item unit the standard is
  per; a counter in another unit is converted through `PRODUCT_UOM_CONVERSION` (active on the `business_date`)
  before dividing by `batch_size`, only when `count_basis = 'unit'`. New state and edge case "cannot be
  calculated" when no conversion path exists; F-07 worked example (1,200 pack → 21,600 s, 85.7%). Rule 3 and AC-5
  replaced (the `PRODUCT_CYCLE` "not read" rule is moot — entity retired); AC-5b added. `count_basis` now cited
  on `ASSET_TAGS`, where F-08 §9.2 puts it, not on `OPERATION`. New dependency `US-FND-PRO-003`.
- `US-FND-KPI-008` · 2026-09-28 (same sync) — UX 07 link re-pointed to the renamed section "KPI item inside each
  domain group". New `TBD` and open item: FLOW-05's new design implication (breakdown shows the unit conversion
  used) against this story's configuration-only scope. Behaviour otherwise unchanged.
- `US-FND-KPI-003` · 2026-09-28 (same sync) — no behaviour change. Added a `TBD` and a module open item: F-07's new
  line "Quality ratio needs no conversion" versus this story's edge case that converts differing good/total/reject
  units. Reported to the PO as a real inconsistency.
- `US-FND-KPI-003`, `007`, `009` · 2026-09-28 (PO answer, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) addendum; prd-sync docs-molcadx 1285317..513c4f9) —
  Quality tags must share one unit, **blocked at binding** (F-07.1 §9.4). `KPI-003`: the unit-conversion edge case
  and the `TBD` are gone, AC-11 rewritten (no conversion step), `PRODUCT_UOM_CONVERSION` no longer read.
  `KPI-007`: validation rule 4, rule 7, state "Quality unit mismatch" with `kpi-002.error.quality-uom-mismatch`
  word for word, AC-12. `KPI-009`: `PRODUCT_UOM_CONVERSION` dropped from reads. Open item closed.
- `US-FND-KPI-007` · 2026-09-28 (same sync; decision addenda) — no "Master data" label: shared items at the top
  with no heading; Work Master flattened to BOM · Routing (Production) and PM checklist · Asset history
  (Maintenance) next to KPI. New AC-13.
- `US-FND-KPI-008` · 2026-09-28 (PO answer; same sync) — the breakdown shows the conversion **rule**, not live
  numbers: `kpi-004.conversion.rule` / `.missing` copied from UX 07 word for word; two new states, an edge case,
  reads, AC-9/AC-10. Old `TBD` closed; new `TBD`: which product's operation the rule is shown for when several
  products run on the work unit.
- `US-FND-KPI-001`, `004`, `005`, `006`, `012` · 2026-09-28 ([asset status decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md); same sync) —
  B5 ownership resolved: `ASSET_STATE_LOG` is PRODUCTION's (only writer; MAINTENANCE reads). Risk notes and
  reads point to PRODUCTION Monitoring. **Status unchanged (still blocked):** the entity spec there is a draft —
  row ID/state field names, recording level SL-1 (was A-5) and the shift-split rule are `TBD`. `KPI-006`: the
  origin marker is PRODUCTION's `source = derived` (Q-21 origin-marker item closed); the detected row is written
  through PRODUCTION, the log's only writer. Status unchanged (stall threshold K-6).
- `US-FND-KPI-008` (2026-09-28, PO, prd-sync docs-molcadx 4cb81d6..dd0a64e): the conversion rule is shown for the
  product running now, else the last product run (`kpi-004.conversion.product`); `TBD` closed, AC-11 added.
- `US-FND-KPI-007`, `US-FND-KPI-008` (2026-09-28, PO, prd-sync docs-molcadx dd0a64e..0dea5c3): KPI page drops the
  "KPI Formula Slots" / "KPI results" buttons; clicking a KPI card opens a per-KPI side panel (Formula · Inputs ·
  Work units · Latest result; UX 07 `kpi-panel.*`). `SCR-FND-KPI-001`/`-003`/`-004` become panel sections, IDs
  kept. KPI-007 Expectation + AC-8 updated, AC-14 added; KPI-008 now lives in panel section 3, behavior unchanged.
- `US-FND-KPI-007` (2026-09-28, prd-sync docs-molcadx 0dea5c3..147e8d2; UX 07/UX 13): "Declare a tag" opens the
  Tags page's tag form with the machine and role pre-filled.
- `US-FND-KPI-001`, `002`, `003`, `004`, `005`, `007`, `008` (2026-10-01, prd-sync docs-molcadx 147e8d2..787e819;
  PO decisions 2026-09-29 SL-1, 2026-09-30 issue #58 / reject weight / BOM-Routing tabs, 2026-10-01 wrap-up):
  B5 row and `KPI-001`/`005` risk notes — SL-1 resolved (per asset, both FKs required); still blocked on row ID /
  state field names / shift split. `KPI-001` and `KPI-008`: `range_bucket` can watch an `activity_signal` tag
  (Availability only). `KPI-002`: standard as of the `business_date` incl. station override
  (`OPERATION_WORK_UNIT`), back-dated fix recalculates; batch vs discrete from `WORK_CENTER.type`; batch boundaries
  from job step or `lot_marker`; batch output split by running time, "pending, batch open" until close;
  input-side counters (`count_side`) derived, measured output wins; `ASSET_PRODUCT_CODES` timestamps. Rules 11–14,
  AC-12–16, two states. `KPI-003`: reject entered by weight → units → time, edge cases, rejects combine in time;
  rules 12–13, AC-14–18. **Status 🟢 → 🟡**: the manual-weight-reject + counted-total pairing contradicts
  F-07.1's one-source rule (`TBD`, new open item). `KPI-004`: roll-up of rejects in time. `KPI-007`: BOM/Routing
  no longer in the Production group (tabs on Product detail); AC-13 reworded. Terms Batch / Lot added. New open
  item: F-07.1 Availability wording lags `activity_signal`.
