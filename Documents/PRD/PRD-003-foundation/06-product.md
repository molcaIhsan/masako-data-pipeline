# PRD-003 · Product (`PRO`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-06. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Terms like *tenant access* and *implementor* are defined in the [README glossary](README.md#glossary).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| [Q-22](30-release-risks-questions.md) | `US-FND-PRO-001` | How many active products the pilot line has. Above roughly 50, bulk import is needed (out of scope). | PO |
| P-1 | `US-FND-PRO-001`, `US-FND-PRO-002`, `US-FND-PRO-003` | Scope level of `PRODUCT` and the entities that FK into it (tenant vs enterprise). Until resolved, treat as `tenant`-scoped. | `foundation-domain` |
| ~~[Q-20](30-release-risks-questions.md)~~ | `US-FND-PRO-002` | ~~What unit `min_weight` / `max_weight` / `standard_weight` are in. There is no unit field.~~ **Resolved 2026-10-01 (PO):** `PRODUCT_DETAIL.weight_uom_id`, a mass unit ([F-06 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#92-product_detail--physical--tracking-attributes-11-with-product)). | — |
| Readiness with versions | `US-FND-PRO-001` | `TBD — needs PO confirmation`: since routings have a `status` (2026-09-30), does a product whose only routing at the line is `draft` count as ready for Performance, or only one with a `released` routing? | PO / `foundation-domain` |
| P-8 | `US-FND-PRO-001` | Keys (2026-10-01): is `PRODUCT.code` unique — per tenant or enterprise, all rows or active only? | `foundation-domain` / PO |
| P-9 | `US-FND-PRO-003` | Keys (2026-10-01): may one product have two active conversion rows for the same from → to pair on one date? | `foundation-domain` / PO |

## Stories


| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-PRO-001`](#us-fnd-pro-001) | Register a product with its base unit | 🟢 Ready | As a **plant admin**, I want to register raw materials, packaging, consumables, semi-finished goods, and finished goods in one product list with their base units, so that routing, BOM, machine tags, and KPIs all have one product identity to reference. → [detail](#us-fnd-pro-001) | `US-FND-REF-004` |
| [`US-FND-PRO-002`](#us-fnd-pro-002) | Record physical and tracking attributes | 🟢 Ready | As a **plant admin**, I want to record a product's weight envelope, dimensions, barcode, and batch tracking rule, so that a checkweigher reading can be judged pass or fail. → [detail](#us-fnd-pro-002) | `US-FND-PRO-001` |
| [`US-FND-PRO-003`](#us-fnd-pro-003) | Manage per-product unit conversions | 🟢 Ready | As a **plant admin**, I want to state that one case of this product holds a specific number of pieces, so that output counted in cases can be compared with output counted in pieces. → [detail](#us-fnd-pro-003) | `US-FND-PRO-001`, `US-FND-REF-004` |

## Detail blocks

---

#### US-FND-PRO-001

**Register a product with its base unit**

**Status:** 🟢 Ready

> **In short:** the implementor registers every raw material, packaging material, consumable, semi-finished good
> and finished good in one product list, each with a base unit. Routing, BOM, machine tags and KPIs all point at
> this one identity.

**1. Story**

As a **plant admin**, I want to register raw materials, packaging, consumables, semi-finished goods, and finished
goods in one product list with their base units, so that routing, BOM, machine tags, and KPIs all have one product identity to
reference.

**2. Context**

- **Why:** four other things point at the product list: routings, operation steps, machine tag mappings
  and unit conversions. Without a stable product identity, none of the four has a
  target ([F-06 §3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#why-it-matters--what-goes-wrong-without-it)).
- **One entity, one name:** there is no separate `ITEM` entity. The name is `PRODUCT`
  ([F-06 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#91-product--the-thing-being-made)). Raw
  materials, packaging, consumables, semi-finished goods, and finished goods live in one table, because
  splitting them forces data to be copied at every handover.
- **Five product types (PO, 2026-09-30, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-product-type-packaging-phantom-bom-tree.md)):**
  raw material, packaging and consumable are **bought**; semi-finished and finished good are **made**. The split
  matches the SAP material types the ERP side already uses (ROH, VERP, HIBE, HALB, FERT), so a product imported
  from an ERP keeps its type. Packaging also has a **packaging level**: primary (touches the product, e.g. the
  bag) or secondary (protects the primary, e.g. the carton, the tape). The type is still a flat flag: the product
  hierarchy is BOM nesting, and "phantom" is a flag on a semi-finished product's BOM, not a type.
- **Who and where:** an implementor at a desk during onboarding, long sessions, stable network.
- **Volume:** the pilot line's product count is not yet known. Above roughly 50, one-at-a-time entry becomes a
  real obstacle and bulk import is needed. See [Q-22](30-release-risks-questions.md).
- **Screen:** the product list `SCR-FND-PRO-001`. Selecting a product opens **one detail page with five tabs**: Measurement, Conversions, BOM, Routing, History ([UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md#screens)). **BOM** and **Routing** became tabs here on 2026-09-30 (they were Production sidebar pages, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-bom-routing-as-product-tabs.md)); their editors, and the BOM and Routing status columns on the product list, are built in [`US-FND-WMS-002`](08-work-master.md#us-fnd-wms-002) and [`US-FND-WMS-003`](08-work-master.md#us-fnd-wms-003). The Cycle time tab (`SCR-FND-PRO-002`) was retired on 2026-09-28: cycle time is entered only on the Routing tab ([`US-FND-WMS-003`](08-work-master.md#us-fnd-wms-003)). History is the audit capability ([Audit Trail](14-audit-trail.md)), not a new screen.

**3. Expectation**

- A product table with code, name, type (shown as a badge on each row), base unit, and a readiness marker: does
  this product have an operation step in force?
- Search and a type filter above the table. The type filter has the same five values as the type picker.
- With a Production license, the table also has the **BOM** and **Routing** status columns and their two
  filters, specified in [`US-FND-WMS-002`](08-work-master.md#us-fnd-wms-002) and
  [`US-FND-WMS-003`](08-work-master.md#us-fnd-wms-003).

*Product form: type and packaging level* (screen text from [UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md#screens), word for word)
- Type picker: "Type" · options "Raw material" / "Packaging" / "Consumable" / "Semi-finished" / "Finished good"
  (draft) (`pro-001.field.type`).
- Choosing **Packaging** shows a required field "Packaging level" · options "Primary" / "Secondary" (draft)
  (`pro-001.field.packaging-level`). Any other type hides it.
- **Readable within 3 seconds:** the product count per type, and how many are not yet ready for Performance.

| State | What the user sees |
|-------|--------------------|
| Empty | "no products — declare one before routing/BOM" ([UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md)), plus the correct order of work: units first ([`US-FND-REF-004`](05-reference-master-data.md#us-fnd-ref-004)), then products |
| Loading | A table skeleton. Filters stay pressable |
| Save error | The form keeps its values, with "Could not save. Your entries are not lost." |
| Save blocked: packaging level missing | Type is Packaging and no level is chosen: "Choose a packaging level: Primary or Secondary." (draft) (`pro-001.error.packaging-level-missing`). The form keeps its values |
| Offline | Not available |
| Success | The new product appears at the top, marked new. On the **first** product saved, the success state says that mapping product codes to machine tags is now unlocked ([UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md), [FLOW-02](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/FLOW/02-production-readiness.md)); since 2026-09-30 product codes are mapped in the product codes section of the product's Routing tab (`SCR-FND-WMS-007`, FLOW-02). It also prompts to continue to its physical attributes ([`US-FND-PRO-002`](#us-fnd-pro-002)). The form is ready for the next product |
| No Production license | The BOM and Routing tabs, both status columns and both filters are **hidden**. The rest of the Product screens is never locked (FOUNDATION is bundled). A direct link to a hidden tab opens the Measurement tab ([UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md#screens)) |
| No permission | "Only subjects whose IDP-asserted scope claim covers `PRODUCT`'s scope (`TBD — foundation-domain: scope level undetermined, pending P-1`) may change product master data. Contact your plant admin." |

Text limits: code at most 30 characters, name at most 120.

**4. Calculation**

*Is this product ready for Performance?* (used by the `products_ready_for_performance` metric in
[§3](README.md))
- The product is ready when it has a base unit **and** an operation step on this line in force on that date.
  On lines that read the product from a sensor, a third condition applies: the product code must be mapped to
  a tag on the line's primary machine.
- Example: the pilot line has 12 active finished-good products. Ten have a base unit and an operation step
  effective on 2026-09-01; two have no operation step yet. So `products_ready_for_performance = 10 / 12 =
  83.3%`. Those two products make Performance show "no data" for the time segments in which they run, without
  affecting Availability.

> [!note]- Exact formula (for developers)
> ```
> ready(product, work_unit, date) = (base_uom_id IS NOT NULL) AND EXISTS(OPERATION o JOIN ROUTING r ON o.routing_id = r.routing_id WHERE r.product_id = product AND (o.work_unit_id = work_unit OR EXISTS(OPERATION_WORK_UNIT WHERE operation_id = o.operation_id AND work_unit_id = work_unit AND active on date)) AND o.valid_from <= date AND (o.valid_to IS NULL OR o.valid_to > date))
> ```
>
> The `OPERATION_WORK_UNIT` branch covers a step that names a machine class instead of one work unit
> ([F-08 §9.2.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#922-operation_work_unit--which-stations-can-run-a-class-based-step-po-2026-09-30), 2026-09-30).
> Whether `r.status` must be `released` is `TBD — needs PO confirmation` (open item "Readiness with versions").
>
> For lines that read the product from a sensor, a third condition is added:
>
> ```
> AND EXISTS(ASSET_PRODUCT_CODES WHERE product_id = product AND tag_id = a tag with tag_role = product_code on that work unit's primary asset)
> ```

*How many products of a type*
- The count of active products of that type (records whose end date hasn't passed).
- Example: 4 active `finished_good` products and 8 active `raw_material` products → the summary shows 4 and 8.

> [!note]- Exact formula (for developers)
> ```
> products_per_type = COUNT(PRODUCT WHERE product_type = <type> AND valid_to IS NULL)
> ```

*Edge cases*
- A product whose end date has passed is left out of the readiness count and does not appear in pickers for
  new entries.
- A product without a base unit cannot be saved, because every quantity calculation depends on it.
- A bought product (`raw_material`, `packaging`, `consumable`) needs no operation step to count as ready, because
  it is not produced on this line. So the readiness figure applies only to `semi_finished` and `finished_good`.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `PRODUCT` (`product_id`, `code`, `name`, `product_type`, `packaging_level`, `base_uom_id`, `valid_from`, `valid_to`) per [F-06 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#91-product--the-thing-being-made) |
| **Reads only** | `UNIT_OF_MEASUREMENTS` for the base unit picker · `ROUTING`, `OPERATION`, `OPERATION_WORK_UNIT` and `ASSET_PRODUCT_CODES` for the readiness marker · `BOM` for the usage marker |
| **Not real fields: don't add** | No other new fields. No `status` or `is_active` field, because deactivation in FOUNDATION is done by closing `valid_to`. No product family or category master, because the finished-good → semi-finished → raw-material hierarchy is BOM nesting in Work Master, not a new master here (P-6 in F-06, reopened and resolved again 2026-09-30: five types, still no family master). No `phantom` product type: phantom is `BOM.is_phantom` ([F-08 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#93-bom--from-what-for-one-product)) |

**6. Rules & constraints**

1. A product code is unique across the enterprise. The rejection message names the product already using it.
2. The product type is required, one of `raw_material`, `packaging`, `consumable`, `semi_finished`,
   `finished_good` (five values since 2026-09-30). These values stay flat and must not be turned into a tree.
   **Packaging level** (2026-09-30): required when the type is `packaging`, empty otherwise. Saving a packaging
   product without a level, or a non-packaging product with one, is rejected. On screen the field is hidden for
   other types, and a missing level shows `pro-001.error.packaging-level-missing`.
3. A base unit is required, chosen from the unit list. A product without a base unit cannot be saved.
4. No physical deletion. Retirement is done by closing the record's end date.
5. A product still referenced by an active component list (BOM), routing, or work order cannot be closed. The
   refusal names the referencing rows.
6. Changing the base unit on a product that already has recorded output is refused, because it changes the
   meaning of every quantity already stored.
7. **Who may do it:** creating, updating or deactivating a product needs **tenant access**.
   `TBD — foundation-domain: scope level undetermined (P-1)`: `PRODUCT` has no `tenant_id`/`enterprise_id` FK
   in its own §9.1 field table
   ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#product-pro--06-productmd-9)).
   Until P-1 resolves, treat it as `tenant`-scoped per the ABAC "no FK → resolves upward" default.
8. Needs a connection.

**7. Acceptance criteria**

- **AC-1** **Given** an empty product list and unit `pcs` already existing, **when** the implementor saves a
  product with code `FG-1001`, name "Snack 100g", type `finished_good`, base unit `pcs`, **then** the product is
  stored and appears in the table.
- **AC-2 (validation)** **Given** code `FG-1001` already exists, **when** it is saved again, **then** it is
  rejected with an inline message naming the product using it.
- **AC-3 (validation)** **Given** no base unit is selected, **when** "Save" is pressed, **then** it is rejected
  with "Base unit is required" under the unit field.
- **AC-4 (order of work)** **Given** no units are registered at all, **when** the implementor opens the product
  form, **then** the base unit picker is empty, with a link to the unit screen and an explanation that units
  must be created first.
- **AC-5 (readiness)** **Given** product `FG-1001` has no operation step in force on any work unit, **when** the
  implementor opens the product table, **then** `FG-1001` is marked not ready, with an explanation that
  Performance will not be computed for it.
- **AC-6 (closure refused)** **Given** `FG-1001` is referenced by an active BOM, **when** the implementor closes
  its record (sets the end date), **then** it is refused with the referencing BOM named.
- **AC-7 (base unit locked)** **Given** `FG-1001` already has recorded output, **when** the implementor changes
  its base unit from `pcs` to `box`, **then** it is refused with an explanation that stored quantities would
  change meaning.
- **AC-8 (error)** **Given** the connection fails during save, **when** the save is attempted, **then** the form
  keeps its values and the message says the data was not stored.
- **AC-9 (permission)** **Given** a user without tenant access (`PRODUCT`'s scope pending P-1), **when** they
  open the product screen, **then** they can only read.
- **AC-10 (packaging level)** **Given** the implementor picks type "Packaging" and no packaging level, **when**
  "Save" is pressed, **then** it is refused with "Choose a packaging level: Primary or Secondary." and the form
  keeps its values. Choosing "Secondary" and saving stores `product_type = packaging`,
  `packaging_level = secondary`.
- **AC-11 (packaging level hidden)** **Given** the implementor picks any type other than "Packaging", **when** the
  form is shown, **then** the "Packaging level" field is not shown and the product is saved with no level.
- **AC-12 (bought products not counted)** **Given** an active `packaging` product with no operation step, **when**
  readiness is computed, **then** it is left out of `products_ready_for_performance` (only `semi_finished` and
  `finished_good` count).
- **AC-13 (no Production license)** **Given** a tenant without a Production license, **when** a user opens a
  product, **then** the tabs are Measurement, Conversions and History only, the list has no BOM or Routing column
  and no BOM / routing filter, and a direct link to the product's BOM tab opens its Measurement tab.

**8. Metrics & events**

- **Metric:** `products_ready_for_performance`, the share of active pilot-line `semi_finished` and
  `finished_good` products passing the `ready()` formula above. Baseline 0% on 2026-08-24. Target 100% on the
  day the pilot line starts recording production. Source: the `PRODUCT`, `OPERATION`, and
  `ASSET_PRODUCT_CODES` tables.
- **Counter-metric:** `product_master_edits_7d`, the share of products edited within 7 days of creation. Must not
  rise above baseline + 5pp.
- **Events:** `product_created` (`product_type`, `base_uom_id`, `site_id`) · `product_readiness_checked`
  (`product_id`, `is_ready`, `missing_requirement`).

**Dependencies:** `US-FND-REF-004`

---

#### US-FND-PRO-002

**Record physical and tracking attributes**

**Status:** 🟢 Ready — Q-20 resolved 2026-10-01: the weight envelope now has its unit (`weight_uom_id`).

> **In short:** the implementor records a product's weight envelope (min, standard, max) with its weight unit,
> dimensions, barcode and batch rule. The envelope lets a checkweigher reading be judged pass or fail.

**1. Story**

As a **plant admin**, I want to record a product's weight envelope, dimensions, barcode, and batch tracking
rule, so that a checkweigher reading can be judged pass or fail.

**2. Context**

- **Why:** the weight envelope is the tolerance band a checkweigher reading is classified against, meaning a
  tag with `tag_role` of `total`, `good`, or `reject` in kg
  ([F-06 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#92-product_detail--physical--tracking-attributes-11-with-product)).
  Without that band, a weight reading is a number with no verdict, and weight-based rejects cannot be
  determined automatically.
- **The checkweigher:** usually a `secondary` machine under the same work unit as its packer. Its role is limited
  to Quality ([F-07.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#multiple-assets-under-one-work-unit--primary-vs-secondary)).
- **Weight unit (PO, 2026-10-01, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-10-01-open-items-wrap-up.md)):**
  the three weights had no unit, so 495 could mean grams or kilograms (Q-20). The envelope now carries one
  weight unit, a **mass** unit (g, kg). The same standard weight and unit are also read to convert a reject
  entered by weight into units and time
  ([F-07 reject entered manually](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-0-kpi-formula.md#reject-entered-manually-in-any-unit--convert-to-the-step-unit-and-time-po-2026-09-30-2026-10-01))
  and to derive the weight of one unit at each routing step
  ([F-08 line across work centers](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#a-line-across-several-work-centers--biscuit-example-po-2026-09-30)).
  `standard_weight` is the finished weight of the product at the end of its own routing.
- **Optional parts:** dimensions and barcode are optional, and nothing in the KPI scope reads them.
- **Who and where:** the implementor, at a desk, one form per product.
- **Screen:** the **Measurement** tab of the product detail page (`SCR-FND-PRO-001`, [UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md)).

**3. Expectation**

- A single-page form inside the product detail, in three groups: weight envelope, dimensions, tracking.
- The weight envelope is three side-by-side fields ordered minimum, standard, maximum, so a wrong order is
  visible at once, with one weight unit for all three. The unit picker offers only mass units.
  UX 06 declares no screen text for the unit field yet.
- **Readable within 3 seconds:** the weight envelope, and whether this product is batch tracked.

| State | What the user sees |
|-------|--------------------|
| Empty | A new product with no attributes shows an empty form, explaining that only the weight envelope and the batch flag are mandatory; the rest can follow later |
| Loading | A form skeleton |
| Save error | The form keeps its values, with a save-failed message |
| Offline | Not available |
| Success | The stored values appear as a summary on the product page, for example "495 to 505 g, standard 500 g" |
| No permission | As in [`US-FND-PRO-001`](#us-fnd-pro-001) |

Limits: barcode at most 30 characters. Weights and dimensions must not be negative. Shelf life is an
integer from 0 to 3,650 days.

**4. Calculation**

*Does one weight reading pass?*
- The reading passes when it sits inside the envelope, between the minimum and the maximum (both included).
- Example: an envelope of 495–505 g → a reading of 503 g passes, a reading of 492 g fails.

> [!note]- Exact formula (for developers)
> ```
> passes(weight) = (weight >= min_weight) AND (weight <= max_weight)
> ```

*How far a reading is from the standard* (to spot a machine drifting before it starts producing rejects)
- The difference from the standard weight, as a share of the standard.
- Example: standard 500 g, reading 492 g → (492 − 500) / 500 × 100 = **−1.6%**. A reading of 503 g gives
  +0.6%.

> [!note]- Exact formula (for developers)
> ```
> deviation_percent = (weight − standard_weight) / standard_weight × 100
> ```

*Expiry date* (only for products with a shelf life filled in)
- The production date plus the shelf life, in days.
- Example: a product made on 2026-09-01 with a shelf life of 180 days expires on 2027-02-28.

> [!note]- Exact formula (for developers)
> ```
> expiry_date = production_date + shelf_life_days
> ```

*Edge cases*
- A standard weight of 0 makes the deviation uncomputable: show "cannot compute", not 0%.
- An envelope where the minimum exceeds the maximum is refused at save time, rather than accepted and then
  rejecting every reading.
- A reading in another mass unit than the envelope (for example a checkweigher reporting kg against an envelope
  in g) is converted to the envelope's unit before it is compared; the calculation converts the weight to the
  unit it needs ([F-08](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#a-line-across-several-work-centers--biscuit-example-po-2026-09-30)).
  Example: envelope 495–505 g, reading 0.503 kg → 503 g → passes.
- A standard weight outside its own envelope is also refused.
- A product with no shelf life shows no expiry date, rather than showing the production date.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `PRODUCT_DETAIL` (`product_detail_id`, `product_id`, `min_weight`, `max_weight`, `standard_weight`, `weight_uom_id`, `length`, `width`, `height`, `barcode`, `is_batch_tracked`, `shelf_life_days`, `valid_from`, `valid_to`) per [F-06 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#92-product_detail--physical--tracking-attributes-11-with-product). One row per product (one-to-one) |
| **Reads only** | `PRODUCT`, for the parent · `UNIT_OF_MEASUREMENTS` (and its measurement group) for the weight unit picker |
| **Not real fields: don't add** | No other new fields. `weight_uom_id` (added 2026-10-01) is the one unit for all three weights; there is no separate unit per weight |

**6. Rules & constraints**

1. One product has exactly one active attribute row.
2. The minimum, maximum and standard weight are required, and so is their weight unit, which must be in the
   *mass* measurement group (g, kg); a non-mass unit is refused. The batch flag is required, default `false`.
3. The minimum must be ≤ the standard, and the standard ≤ the maximum. A violation is refused, with
   all three numbers named in the message.
4. Dimensions, barcode and shelf life are optional. Dimensions are used only by packaging and
   logistics calculations; nothing in the KPI scope reads them.
5. Changing the weight envelope closes the old row with its end date and opens a new one, rather than
   overwriting. Last month's readings must still be judged against the envelope in force then.
6. The same barcode must not be used by two active products.
7. **Who may do it:** creating or updating a product's attributes needs **tenant access**.
   `TBD — foundation-domain: scope level undetermined (P-1)`: `PRODUCT_DETAIL` inherits `PRODUCT`'s own
   unresolved tenant-vs-enterprise scope question, with no Site Hierarchy FK of its own
   ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#product-pro--06-productmd-9)).
   Until P-1 resolves, treat it as `tenant`-scoped per the ABAC "no FK → resolves upward" default.
8. Needs a connection.

**7. Acceptance criteria**

- **AC-1** **Given** product `FG-1001` exists, **when** the implementor saves an envelope of 495, 500, 505 and
  `is_batch_tracked = true`, **then** the attributes are stored and their summary appears on the product page.
- **AC-2 (order validation)** **Given** the implementor enters minimum 505 and maximum 495, **when** "Save" is
  pressed, **then** it is refused with a message naming all three numbers and explaining the correct order.
- **AC-3 (standard validation)** **Given** minimum 495, maximum 505, and standard 520, **when** it is saved,
  **then** it is refused because the standard falls outside its own envelope.
- **AC-4 (mandatory validation)** **Given** the weight envelope fields are left empty, **when** it is saved,
  **then** it is refused with a required message under each empty field.
- **AC-5 (edge case)** **Given** a standard weight of 0, **when** the screen shows a reading's deviation, **then**
  "cannot compute" appears rather than 0%.
- **AC-6 (versioning)** **Given** the weight envelope is changed today, **when** last month's reading report is
  opened, **then** those readings are still judged against last month's envelope.
- **AC-7 (duplicate barcode)** **Given** barcode `8991234567890` is already used by another active product,
  **when** it is saved, **then** it is refused with that product named.
- **AC-8 (permission)** **Given** a user without tenant access (`PRODUCT_DETAIL`'s scope pending P-1), **when**
  they open the product attributes page, **then** they can only read.
- **AC-9 (weight unit)** **Given** the implementor enters an envelope of 495, 500, 505, **when** they save without
  a weight unit, **then** it is refused as required; **when** they pick `pcs`, **then** it is refused because the
  unit is not a mass unit; **when** they pick `g`, **then** it is stored and the summary reads "495 to 505 g,
  standard 500 g".

**8. Metrics & events**

- **Metric:** `products_with_weight_envelope`, the share of active pilot-line `finished_good` products with a
  complete weight envelope. Baseline 0% on 2026-08-24. Target 100% for products on lines that have a
  checkweigher, before the line starts recording production. Source: the `PRODUCT_DETAIL` table.
- **Counter-metric:** the share of weight readings that cannot be judged because no envelope exists. Must fall
  to 0 once the target is met.
- **Events:** `product_detail_saved` (`product_id`, `has_weight_envelope`, `is_batch_tracked`) ·
  `weight_envelope_validation_failed` (`product_id`, `rule_violated`).

**Dependencies:** `US-FND-PRO-001`

---

#### US-FND-PRO-003

**Manage per-product unit conversions**

**Status:** 🟢 Ready

> **In short:** the implementor states, per product, how units relate ("1 case = 24 pcs"). Without it, output
> counted in cases and output counted in pieces can't be added up.

**1. Story**

As a **plant admin**, I want to state that one case of this product holds a specific number of pieces, so that
output counted in cases can be compared with output counted in pieces.

**2. Context**

- **Why:** a product counted in boxes at one work unit and in kilograms at another cannot be reconciled without
  a manual fudge factor ([F-06 §3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#why-it-matters--what-goes-wrong-without-it)).
- **Per product, not generic:** one case of Product A holds 24 pieces while one case of Product B holds 36. A
  shared unit table cannot hold two contradictory facts. That is why Product owns this conversion, not
  Reference & Master Data
  ([F-06 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#94-product_uom_conversion--fromto-uom-conversion-factor-scoped-to-one-product)).
- **Generic shape on purpose:** it is not named or shaped as a packaging conversion. A density conversion, for
  example 1 litre of Product X equals 0.95 kg, varies per product in exactly the same way packaging ratios do
  and needs exactly the same shape. Narrowing the name now would exclude that case later.
- **Also read by the KPI calculation (2026-09-28):** an operation step's cycle time is per an item unit
  (`OPERATION.uom_id`: pack, carton, kg), and a machine counter may count in another unit. The Performance
  calculation converts the counter to the operation's unit through this product's rows
  ([`US-FND-KPI-002`](07-kpi.md#us-fnd-kpi-002),
  [F-08 §9.2.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#921-item-unit-and-conversion-po-2026-09-28)).
  An operation step whose item unit is not the product's base unit can't be saved without a conversion path
  here ([`US-FND-WMS-003`](08-work-master.md#us-fnd-wms-003)).
- **Also read by Work Master (2026-09-30):** a BOM's recipe base unit must be the product's base unit or have a
  conversion path here ([`US-FND-WMS-002`](08-work-master.md#us-fnd-wms-002)). A punching step that counts in
  `punch` declares the weight of one punch as a conversion row on this tab, for example `1 punch = 20 g`, so a
  reject entered in kg can be converted
  ([F-08 line across work centers](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#a-line-across-several-work-centers--biscuit-example-po-2026-09-30)).
  Rules, calculation and acceptance criteria of this story are unchanged.
- **Who and where:** the implementor, at a desk, a few rows per product.
- **Screen:** the **Conversion** tab of the product detail page (`SCR-FND-PRO-003`, [UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md)).

**3. Expectation**

- A conversion table inside the product detail, with columns for source unit, target unit, and factor.
- Each row shows a readable example beside it, for example "1 case = 24 pcs", because a bare factor is easy to
  read backwards.
- **Readable within 3 seconds:** how many conversions exist, and whether this product's base unit is covered.

| State | What the user sees |
|-------|--------------------|
| Empty | "No conversions for this product yet", plus the consequence: output in different units cannot be added |
| Loading | A table skeleton |
| Save error | The form keeps its values, with a save-failed message |
| Offline | Not available |
| Success | The new row appears with its readable example. No reverse row is asked for: one row covers both directions |
| No permission | As in [`US-FND-PRO-001`](#us-fnd-pro-001) |

Numeric limits: the conversion factor must be greater than 0, at most 6 decimal places.

**4. Calculation**

*Converting a quantity*
- Multiply the quantity by the conversion factor that matches this product and this unit pair, in force on
  the transaction date.
- Example: product `FG-1001`, "1 case = 24 pcs". Work unit A records 40 cases → 40 × 24 = 960 pieces. Combined
  with 960 pieces from work unit B, the total is 1,920 pieces. For product `FG-2002`, whose factor is 36,
  40 cases give 1,440 pieces rather than 960. This is exactly why the conversion is per product.

> [!note]- Exact formula (for developers)
> ```
> target_qty = source_qty × conversion_value
> ```

- **Source:** `conversion_value` from `PRODUCT_UOM_CONVERSION` where `product_id`, `from_uom_id`, and
  `to_uom_id` match and the row is effective on the transaction date: `valid_from <= date` and `valid_to`
  either null or later than the date.

*Reverse direction*
- **One row covers both directions** ([F-06 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#94-product_uom_conversion--fromto-uom-conversion-factor-scoped-to-one-product), PO 2026-09-28). Read backward, the row divides:
  "1 case = 24 pcs" turns 960 pieces into 960 ÷ 24 = **40 cases**. Nobody enters a pieces-to-cases row.
- If both directions exist for the same product, unit pair and date, the row read **forward** is used, so the
  result never depends on rounding in the other row.
- A backward result that doesn't divide evenly (1,000 pieces → 41.67 cases) is shown as-is with the
  "does not divide evenly" marker below, never rounded silently.

> [!note]- Exact formula (for developers)
> ```
> forward:  target_qty = source_qty × conversion_value   (source = from_uom_id)
> backward: target_qty = source_qty ÷ conversion_value   (source = to_uom_id)
> ```

*Edge cases*
- A factor of 0 or less is refused at save time.
- A conversion to the same unit is refused.
- A conversion between units of different dimensions, for example kg to pieces, is **allowed here as a
  product-scoped density/weight row** ([UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md);
  [F-05 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#94-measurement_group-and-unit_of_measurements)).
  `MEASUREMENT_GROUP` exists (Q-16 resolved) and tells same-dimension scale pairs apart from cross-dimension
  pairs. What is refused is a *generic* (non-product-scoped) cross-dimension factor. This screen already scopes
  by `product_id`, so a kg↔pcs row for this product is legal. The screen may still warn that the factor is
  plant-specific density, not a universal conversion.
- A non-integer result in a physically discrete unit, for example 41.67 cases, is shown as-is, with a marker
  that the target unit does not divide evenly, rather than rounded without a trace.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `PRODUCT_UOM_CONVERSION` (`product_uom_conversion_id`, `product_id`, `from_uom_id`, `to_uom_id`, `conversion_value`, `valid_from`, `valid_to`) per [F-06 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#94-product_uom_conversion--fromto-uom-conversion-factor-scoped-to-one-product) |
| **Reads only** | `PRODUCT.base_uom_id` · `UNIT_OF_MEASUREMENTS` for the unit pickers |
| **Not real fields: don't add** | No new fields. No direction marker and no packaging marker, because neither exists in F-06 |

**6. Rules & constraints**

1. One product + source unit + target unit may have only one row effective on a given date.
2. The conversion factor must be greater than 0.
3. The source unit must not equal the target unit.
4. The source unit would normally match the product's base unit. Converting from another unit is not forbidden
   but is unusual: show a soft warning and let the implementor continue.
5. One row covers both directions: forward × the factor, backward ÷ the factor. If both directions exist for the
   same pair and date, the forward-read row wins.
6. Changing a factor closes the old row with its end date and opens a new one. Overwriting a factor would
   change every output figure already converted.
7. Cross-dimension pairs (item vs mass, for example) are allowed only as product-scoped rows on this
   screen. Generic cross-dimension conversion is refused elsewhere, with the F-00/F-05 rule quoted
   ([UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md)). Same-dimension pairs remain ordinary conversions.
8. **Who may do it:** creating or updating a conversion needs **tenant access**.
   `TBD — foundation-domain: scope level undetermined (P-1)`: `PRODUCT_UOM_CONVERSION` FKs only to
   `product_id` and inherits `PRODUCT`'s own unresolved tenant-vs-enterprise scope
   question, with no direct Site Hierarchy FK of its own
   ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#work-master-wms--08-work-mastermd-9)).
   Until P-1 resolves, treat it as `tenant`-scoped per the ABAC "no FK → resolves upward" default.
9. Needs a connection.

**7. Acceptance criteria**

- **AC-1** **Given** product `FG-1001` with base unit `pcs`, **when** the implementor saves a conversion of
  1 case = 24 pcs, **then** the row is stored and its readable example shows "1 case = 24 pcs".
- **AC-2 (validation)** **Given** the conversion factor is entered as 0, **when** it is saved, **then** it is
  refused with "The conversion factor must be greater than 0".
- **AC-3 (validation)** **Given** source and target units are both `pcs`, **when** it is saved, **then** it is
  refused, because a conversion to the same unit is meaningless.
- **AC-4 (duplicate)** **Given** a case-to-piece conversion for this product already exists and is still
  effective, **when** the implementor saves the same pair, **then** it is refused with a link to the existing
  row.
- **AC-5 (per product)** **Given** `FG-1001` has factor 24 and `FG-2002` has factor 36, **when** the system
  converts 40 cases for each, **then** the results are 960 pieces and 1,440 pieces.
- **AC-6 (reverse direction)** **Given** only the row 1 case = 24 pcs exists, **when** the system converts
  960 pieces to cases, **then** the result is 40 cases (960 ÷ 24), and no reverse row is required.
- **AC-6b (both directions entered)** **Given** rows 1 case = 24 pcs and 1 pc = 0.0417 case both exist and are
  effective, **when** the system converts 960 pieces to cases, **then** it uses the pieces-to-cases row read
  forward (960 × 0.0417 = 40.032), not the case row read backward.
- **AC-7 (product-scoped density)** **Given** the plant admin saves a kg-to-piece conversion for this product,
  **when** they press save, **then** the row is stored (product-scoped cross-dimension is allowed); a soft note
  may say it is this product's density, not a generic factor.
- **AC-8 (versioning)** **Given** the factor is changed from 24 to 25 today, **when** last month's output report
  is opened, **then** its figures still use factor 24.
- **AC-9 (permission)** **Given** a user without tenant access (this entity's scope pending P-1), **when** they
  open the conversion table, **then** they can only read.

**8. Metrics & events**

- **Metric:** `conversions_available`, the share of unit pairs actually used by the pilot line that have a
  conversion row. Baseline not yet measured; measure it over the first 14 days of output recording. Target 100%
  by 30 days after release. Source: `PRODUCT_UOM_CONVERSION` compared against the unit pairs appearing in
  output records.
- **Counter-metric:** the count of output additions refused because a conversion is missing. Must trend
  towards 0.
- **Events:** `product_uom_conversion_created` (`product_id`, `from_uom_id`, `to_uom_id`).
  ~~`uom_conversion_reverse_missing`~~ dropped 2026-09-28: a reverse row is never needed.

**Dependencies:** `US-FND-PRO-001`, `US-FND-REF-004`

## Change notes (history — not needed to build)

- `US-FND-PRO-001` — Work Master used to call this entity `ITEM`; it is now only `PRODUCT`.
- `US-FND-PRO-004` — 2026-09-02 ([F-06 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#93-product_cycle--retired-2026-09-28)): item and time unit split into two FKs (`uom_id`, `time_conversion_id`). Earlier drafts of this story assumed a compound "seconds/pack" unit. The throughput entry mode was resolved the same day. P-2 (multiple active rows, one main) was resolved earlier in F-06.
- All stories (2026-09-23): detail blocks rewritten from one dense table into numbered sections, and
  "subject whose IDP-asserted scope claim covers …" replaced by *tenant access* (defined in the README
  glossary). Meaning unchanged; UI copy kept verbatim.
- All stories (2026-09-23, second pass): rewritten in plain language. Entity and field names now appear only in
  section 5, the collapsible "Exact formula (for developers)" boxes and schema-level `TBD` callouts for
  `foundation-domain`; everywhere else things are named in plain words (product, base unit, weight envelope,
  conversion factor, reference cycle time). Meaning unchanged; UI copy, story IDs and event names kept
  verbatim.
- 2026-09-23 (rule 1b, full UX cross-check): every story in this file now names the UX screen it is built on.
- `US-FND-PRO-004` (2026-09-28, PO, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) C; prd-sync docs-molcadx 7fb3a9a..1285317):
  **retired**. `PRODUCT_CYCLE` is retired and the Cycle time tab `SCR-FND-PRO-002` with it; cycle time lives only
  on `OPERATION` ([`US-FND-WMS-003`](08-work-master.md#us-fnd-wms-003)). Moved to
  [90-closed-stories.md](90-closed-stories.md#us-fnd-pro-004); ID kept, not reused. Its open items (P-1 for this
  entity, `cycle_time_value` precision) are dropped with it.
- `US-FND-PRO-001` (2026-09-28, same sync): product detail tabs are now Measurement / Conversions / History; the
  product list is pointed at by four things, not five (reference cycle times gone).
- `US-FND-PRO-003` (2026-09-28, same sync): context notes that the KPI Performance step now reads these rows to
  convert a counter to `OPERATION.uom_id`, and that `US-FND-WMS-003` blocks a non-base item unit without a
  conversion path. Rules, calculation and acceptance criteria of this story are unchanged.
- `US-FND-PRO-003` (2026-09-28, PO, prd-sync docs-molcadx 513c4f9..4cb81d6, [F-06 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#94-product_uom_conversion--fromto-uom-conversion-factor-scoped-to-one-product)): **one row
  covers both directions** — resolves the conflict with the F-08 conversion path used by `US-FND-WMS-003` and
  `US-FND-KPI-002`. Success state no longer offers a reverse row; "Reverse direction" calculation, rule 5 and
  AC-6 rewritten, AC-6b added; event `uom_conversion_reverse_missing` dropped.
- `US-FND-PRO-001` (2026-09-30, PO, decisions [product type / packaging / phantom](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-product-type-packaging-phantom-bom-tree.md)
  and [BOM / Routing as product tabs](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-bom-routing-as-product-tabs.md);
  prd-sync docs-molcadx 147e8d2..787e819): `product_type` has five values (`packaging`, `consumable` added) plus
  `packaging_level` (required for packaging only) — story text, context, Expectation (UX 06 `pro-001.field.type`,
  `pro-001.field.packaging-level`, `pro-001.error.packaging-level-missing`), rule 2, section 5, AC-10 … AC-12.
  Product detail tabs are now Measurement / Conversions / BOM / Routing / History; without a Production license
  the BOM and Routing tabs, status columns and filters are hidden (new state, AC-13). Readiness counts only made
  products; the `ready()` formula now joins `ROUTING` and covers class-based steps (`OPERATION_WORK_UNIT`); whether a
  draft-only routing counts is a new open item. Product codes are now mapped on the Routing tab (`SCR-FND-WMS-007`).
- `US-FND-PRO-002` (2026-10-01, PO, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-10-01-open-items-wrap-up.md); same sync):
  **Q-20 resolved** — `PRODUCT_DETAIL.weight_uom_id` (mass unit) added; status 🟡 → 🟢. Context, Expectation,
  edge case (reading in another mass unit), section 5, rule 2 and AC-9 updated.
- `US-FND-PRO-003` (2026-09-30, same sync): context notes two new readers (BOM recipe base unit, weight of one
  punch). Rules, calculation and acceptance criteria unchanged.
