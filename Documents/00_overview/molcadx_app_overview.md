# MolcaDx — how the app works (pipeline view)

> Learned from [[PRD/AGENTS|PRD]] on 2026-10-01: PRD-003 FOUNDATION (`PRD/PRD-003-foundation/`), PRD-004 PRODUCTION (`PRD/PRD-004-production/`). PRD-001 is frozen; ignore it except for history.
> Master data model: [[erd_spec_2026-10-01]]. Goal of this repo: [[objective]]. Pipeline picture: [[01_architecture/data pipeline.png]].
> **Precedence:** PRD formulas come from spec F-07 (ISO 22400-2). Where the PRD and the spec disagree, the spec wins. Several specs (F-07.1 §9, F-01, F-12) live in the external `docs-molcadx` repo, which is **not** in this repo.

## 1. The three domains

| Domain | Owns | What the pipeline gets from it |
|---|---|---|
| **FOUNDATION** (PRD-003) | All master data: hierarchy, shifts, fiscal year, assets + tags, reasons, UOM, products, routings/cycle times, **KPI formulas + bindings**, `KPI_RESULT` | Lookup and dimension tables, the "formula dictionary" |
| **PRODUCTION** (PRD-004) | `WORK_ORDER*`, **`ASSET_STATE_LOG`**, generates `SHIFT_INSTANCE` | `ASSET_STATE_LOG` `plc`/`derived` rows are written by the **"data-platform Silver job"**, which is us |
| **MAINTENANCE** | PM procedures, component links | Reads `ASSET_STATE_LOG` (MTTR/MTBF) |

PRODUCTION's screens (Monitoring, Performance Analysis) compute nothing. **Every number is read from `KPI_RESULT`**, which the KPI calculation job writes.

## 2. From the machine to the number

```
PLC/IoT ─(OPC UA / MQTT, Telegraf)─► raw reading keyed by tag_id   [bronze]
   ASSET_TAGS: tag_id → asset_id, tag_role, kind, uom, count_basis, count_side, lane(work_unit_id)
   ASSET_PLACEMENT (as-of event time): asset → work_unit (primary / secondary)
                         │
                         ▼   Flink: delta counters, state changes, stops, product runs   [silver]
   counter deltas · ASSET_STATE_LOG (running/idle/down/setup, plc|derived) · product segments
                         │
                         ▼   WORK_UNIT_KPI_BINDING: (work_unit, slot) → asset_tag_id + transform
   KPI terms per WORK_UNIT × SHIFT_INSTANCE: POT, PBT, APT, ideal time, total, good, reject
                         │
                         ▼   sum_of_terms rollup → work_center / area / site, date ranges
   KPI_RESULT  /  gold report tables   [gold]
```

Key identity rules:
- **Readings are keyed by `tag_id`, never by address.** The OPC UA node or MQTT topic is *generated* from the placement and hierarchy codes and changes when a machine moves or a code is renamed. `tag_id` stays the same.
- **A work unit has no tags of its own.** It borrows the tags of the asset placed on it **at the event timestamp**.
- **Multi-lane machine** = one asset placed on several work units. Per-lane roles (`total`, `good`, `reject`, `reject_reason`, `product_code`, `speed`) count only in `ASSET_TAGS.work_unit_id`. Machine-wide roles (`machine_state`, `downtime_reason`, `runtime`) apply to every lane. A counter is never counted twice.
- Facts are keyed by `asset_id`, never `asset_tag`, because plants renumber.

## 3. "Direct binding" — how a total is found (replaces flow tracing)

Old way: walk `work_unit_flow` to find the first and last machine. See [[NEW_oee_or_kpi_calculation]].
New way:
1. `KPI_FORMULA_SLOT` is the fixed formula dictionary, seeded and not plant-editable: `quality.total`, `quality.good`, `quality.reject`, `performance.output`, `performance.product_code`, `availability.downtime_reason`, plus non-bindable slots `rework.output`, `throughput.order_execution_time`, `reliability.interval`.
2. `KPI_PARAMETERS` lists which `(slot, tag_role, transform)` triples are legal.
3. `WORK_UNIT_KPI_BINDING` says, for one work unit and one slot, **which tag** feeds it and **how** (`transform`). It is effective-dated, so a past period recomputed later uses the binding in force at that time.

So **total of a work unit for a shift** = Σ silver counter deltas of the `asset_tag_id` bound to `quality.total` for that work unit, where the binding is valid at the reading time and the reading falls inside the `SHIFT_INSTANCE` window. The slot is the question; the binding is the answer.

Transforms:

| Transform | Slot area | Meaning |
|---|---|---|
| `none` | availability | A direct state tag opens and closes stops |
| `reason_tag_drives_stop` | availability | A reason-register bank opens and closes stops |
| `stall_bucket` | availability | A stalled `total` counter becomes a stop (see §5) |
| `range_bucket` | availability | A `speed` or `activity_signal` value out of its band means down |
| `reason_match_enrich` | availability | Labels a stop only |
| `none` | quality | Read the pair directly |
| `complement_derive` | quality | good = total − Σreject |
| `infeed_outfeed_derive` | quality | Two stations; needs `second_asset_tag_id` |
| `reconcile_check` | quality | All three counts present; a mismatch sets `is_anomaly` |

No binding gives `no_data`, **never 0%**.

## 4. Formulas (F-07, ISO 22400-2)

| KPI | Formula | Notes |
|---|---|---|
| Time terms | `POT` = SHIFT_INSTANCE end − start · `PBT` = POT − planned_dt · `APT` = PBT − unplanned_dt | Stops come from `ASSET_STATE_LOG` of the **primary** asset, clipped to the shift window. An unlabelled stop counts as unplanned. `small_stop` reduces neither PBT nor APT; it is a Performance loss |
| Availability | `APT / PBT` | |
| Performance | `Σᵢ(standard_i × output_i) / APT` | Per product segment. The standard is `OPERATION_WORK_UNIT.cycle_time_value` (station override) else `OPERATION.cycle_time_value`, valid on `business_date`, × `TIME_CONVERSIONS.seconds_per_unit`. Counts are converted to `OPERATION.uom_id` and divided by `batch_size` when `count_basis = unit`. No standard or no conversion → segment excluded, result "partial". Never an averaged standard |
| Quality | `GQ / PQ` | Pair fill order: good+total → total−Σreject → total + manual reject → good+Σreject → no data. Never mix sources within a pair |
| OEE | `A × P × Q` | Only when all three exist. Waterfall: POT → PBT → APT → net run time → good run time |
| MTTR / MTBF | closed unplanned stops only | |
| Scrap / Rework / Throughput / Process ratio / Fall-off | `SQ/PQ`, `RWQ/PQ`, `PQ/AOET`, `APT/AOET`, (PQ_first − GQ_cur)/PQ_first | From job (`WORK_ORDER_OPERATION`) data |

**Rollup = `sum_of_terms`**, never an average of percentages. Area, site and date-range results sum APT, PBT, ideal time, good and total, then divide. Rejects across work units add up as **reject time**, never as weight.

## 5. Stops, small stops, thresholds

> **Pipeline decision (2026-10-02):** the thresholds are fixed in Flink at 60 s / 300 s for every work unit. There is no config table, which deviates from the PRD's per-work-unit, effective-dated settings.

- `min_stop_seconds` = 60 and `small_stop_max_seconds` = 300 by default. They resolve per work unit → work center → site → global, effective-dated by `business_date`. Storage is TBD (Q-32).
- `stall_bucket`:

  | Stall length d | Result |
  |---|---|
  | d < min | No event; it is a speed loss |
  | min ≤ d ≤ max | `small_stop` |
  | d > max | Real downtime |

- A backwards counter is a **reset**, not negative output.
- **The sensor decides *when*, the person decides *why*.** A recompute may re-derive times but never overwrites a reason a person entered (a row with `entered_by` set).

## 6. Time

- `SHIFT_INSTANCE` rows are pre-generated (production-domain), stored in UTC, and shown in `SITE.timezone` (IANA). Hours are 07–15, 15–23, 23–07 local. **`business_date` = the date the shift starts.**
- Every effective-dated lookup (cycle time, conversion, thresholds, binding) uses `business_date`. `ASSET_PRODUCT_CODES` and `ASSET_PLACEMENT` use the event timestamp.
- An open shift gives **provisional** figures, refreshed at least every 5 min. A closed shift gives final figures, ready within **60 s p95** of shift close.
- Closed periods are frozen, **except** for these, which recompute closed shifts:
  - back-dated standard (cycle time) fix
  - back-dated product-code mapping
  - batch close (output split by running time)
  - late manual reject or correction

## 7. Known open items that hit the pipeline

- B5 `ASSET_STATE_LOG` spec draft: state field names and the shift-split rule.
- `delta_counter` vs `cumulative_counter` exact semantics; rollover.
- No scale/offset fields on tags (the transformation gap).
- Q-21 product-code debounce.
- Q-32 threshold storage.
- K-10 range band.
- K-12 `KPI_RESULT` keys and list storage.
- A-19 placement validity: date or timestamp.
- M-01 who writes `good_qty`.
- **Kafka, Flink, Airflow and Timescale are not mentioned in any PRD.** This pipeline is our design; the PRD only fixes the contracts.

Next: [[silver_model]] · [[gold_tables]]
