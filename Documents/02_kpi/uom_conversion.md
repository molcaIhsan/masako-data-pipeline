# UOM — how counts are converted

> Related: [[gold_model]] (§6 worked OEE example, §7 UOM reference) · [[silver_model]] · [[molcadx_app_overview]] · [[erd_spec_2026-10-01]]
> Source: PRD-003 `05-reference-master-data` (REF-004), `06-product` (PRO-003), `08-work-master` (WMS-003 §9.2.1), `07-kpi` (KPI-002, KPI-003).

## 1. Several units are involved for each count

| Unit | Field | Example |
|---|---|---|
| **What the machine counts in** | `asset_tags.uom_id` + `count_basis` (`unit` = items, `cycle` = machine cycles) | tag 101 counts **pack** |
| **What the standard is written per** | `operation.uom_id` + `time_conversion_id` + `batch_size` | packing step: 216 **s per carton** |
| **What reports are in** | `product.base_uom_id` | Biscuit A base = **pack** |
| **Per-product factors that connect them** | `product_uom_conversion` (+ `product_detail.standard_weight` for weights) | carton → pack = 12 |

- `unit_of_measurements` has **no conversion factor**. 1 carton is 12 packs for one product and 24 for another, so conversion is always **per product and per date**.
- `measurement_group` only says the dimension (count, mass, volume). Cross-dimension rows (kg ↔ pack) are allowed because they are per product.

**Silver stores counts in the tag's own unit**, unconverted (`production_events.uom_id`). Conversion happens when views or gold read the data, with rows valid on the shift's `business_date`. A factor change later never alters past shifts.

## 2. How a conversion is found (first path that works)

1. **Same unit:** no change.
2. **A direct row:** × factor when the row goes from → to; ÷ factor when the row only exists to → from. If both exist, the forward row wins.
3. **Two rows chained through the product's base unit:** from → base → to.
4. **No path:** "cannot compute". That product's segment is left out and the result is marked `partial`. A count is never estimated or used unconverted.

## 3. Where it's applied

| Use | Conversion |
|---|---|
| **P (Performance)** | `count_basis = unit`: cycles = convert(count, tag uom → `operation.uom_id`) ÷ `batch_size`. `count_basis = cycle`: cycles = count. Then ideal_s = cycles × `cycle_time_value` × `seconds_per_unit` |
| **Q (Quality)** | **No conversion.** All `quality.*` bindings of a work unit must use the same tag unit; the PRD refuses the binding otherwise. Q is a ratio, so the unit cancels |
| **Gold quantities** (total_out, good_out, reject, product_count) | convert(count, tag uom → `product.base_uom_id`) |
| **Manual reject by weight** | units = kg ÷ weight of one unit at that step (`standard_weight`, adjusted for later-step shrinkage) |
| **Line / area totals** | Only add products with the same base unit. Rejects across machines are added as **reject time**, never kg |

## 4. Example

- Product **Biscuit A**, base unit **pack**.
- `product_uom_conversion`: carton → pack = 12.
- Packing `operation`: uom = **carton**, cycle_time_value = 216, time unit = s, batch_size = 1.
- Tag 101 counts **pack**, `count_basis = unit`.

| Step | Calculation | Result |
|---|---|---|
| Silver | Σ cleaned_value of tag 101 | 1,200 pack |
| P: to the operation unit | no pack→carton row, so read carton→pack backwards: 1,200 ÷ 12 | 100 carton |
| P: cycles | 100 ÷ batch_size 1 | 100 |
| P: ideal time | 100 × 216 × 1 s | 21,600 s |
| Gold total_out | pack is already the base unit | 1,200 pack |

## 5. The Kafka sample

`good_weight` from the ANRITSU checkweigher counts **kg**, not packs. To use it:
- **Units:** it needs either a per-product kg ↔ pack row in `product_uom_conversion`, or `product_detail.standard_weight`.
- **Quality:** it can't be paired with `ng_count` (pieces), because all Q bindings must share one unit.

So bind count tags for Q, and use weight only for P or reporting.

## 6. Open

- A work unit that runs products with different base units in one shift can't put one `uom_id` on its gold row. Proposal: one gold row per base unit. **TBD — confirm.**
