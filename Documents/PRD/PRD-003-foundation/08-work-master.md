# PRD-003 · Work Master (`WMS`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-08. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Terms like *tenant access*, *work-unit access*, *implementor* and *APT* are defined in the [README glossary](README.md#glossary).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.

> **Part-4 minimum spine (2026-09-24):** next FE / PRODUCTION handoff = Work Master (this module) → Work Directive → Job Order (+ Job Response when close-in-scope). Defer Work Schedule / Job List / Work Performance / Workflow Spec unless multi-job dispatch is required. See [F-08](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#part-4-execution-spine-minimum-for-next-fe--production-handoff).

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| ~~[W-8](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown)~~ | `US-FND-WMS-002`, `US-FND-WMS-003` | ~~How a `BOM` / `ROUTING` is locked onto a released work order.~~ **Resolved 2026-09-30 (PO, issue #58):** `version` + `status` on `ROUTING` and `BOM`, one row per version; the work order pins by `routing_id` / `bom_id` ([versions and status](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#versions-and-status-po-2026-09-30)). | — |
| ~~W-7~~ | `US-FND-WMS-003` | ~~Are setup time and crew size required?~~ **Resolved 2026-09-30 (PO):** both optional; setup time has its own time unit (`setup_time_conversion_id`). | — |
| Phantom in stock / material requirement | `US-FND-WMS-002` | What a phantom BOM, and a `consumable` line, mean for stock and material requirement (`TBD` in F-08 §9.3) — the future Inventory domain's call. The tree only displays them. | Inventory domain (not created) |
| Same-tab features without a story | — | The Routing tab also holds the flow bar (`PRODUCT_FLOW`), the parameter standards section (`OPERATION_PARAMETER`); the product codes section (`SCR-FND-WMS-007`) is now `US-FND-WMS-006`. UX 08 marks their text `story TBD`; no PRD story exists (rule 1). | PO |
| UX-WMS-4 | `US-FND-WMS-006` | "Seen but not mapped" needs a record of unmapped codes (raw value, tag, first and last seen): who owns it, FOUNDATION or PRODUCTION, and how long is it kept? Blocks AC-11 | PO → `foundation-domain` / `production-domain` |
| UX-WMS-5 | `US-FND-WMS-006` | Two flows of a product with routings at the same work center: which routing's step does a code resolve to? Interim: the primary flow | PO → `foundation-domain` |
| [W-10](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown) | `US-FND-WMS-003` | Hold time **across routings** (dough at Mixing → molding at Baking): needed, and where does it live? | PO + Operations |
| [W-11](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown) | `US-FND-WMS-003` | A different hold time per successor, and a **minimum** wait (resting, cooling)? | Operations |
| [W-12](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown) | `US-FND-WMS-002` | Asymmetric dispensing tolerance (−0 / +2%, never under-dose)? | Operations / QA |
| [W-13](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown) | `US-FND-WMS-003` | Check frequency by quantity ("every 500 packs")? Is `once` per run or per lot? | Operations + `production-domain` |
| [W-14](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown) | `US-FND-WMS-003` | Instruction text length, attachments (picture, SOP link), more than one language? | PO |
| [W-15](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown) | `US-FND-WMS-003` | A step counted in pieces whose loss is mass: is a yield in pieces enough, or a mass yield too? | PO + QA |
| [W-16](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown) | `US-FND-WMS-003` | Lines dispensed out of recipe ratio: theoretical output from the limiting component (proposed), or another rule? | PO + QA |
| [W-17](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#whats-still-unknown) | `US-FND-WMS-002` | Tolerance change on a released BOM: new BOM version (built this way for now), or effective-dated? | PO |
| ~~Step detail screen text~~ | `US-FND-WMS-002`, `US-FND-WMS-003` | ~~UX 08 had no screen text for the step detail~~ **Resolved 2026-10-01:** UX 08 keys `wms-003.instr.*`, `.checks.*`, `.yield.*`, `.hold.*`, `wms-002.line.tolerance*`, `wms-002.error.tolerance-*` | — |
| P-1 | `US-FND-WMS-002` | Scope level of `BOM`/`BOM_LINE` (tenant vs enterprise). Until resolved, treat as `tenant`-scoped. | `foundation-domain` |
| `cycle_time_value` precision | `US-FND-WMS-003` | ~~A typed rate is saved as typed (PO 2026-09-28), e.g. "700 per hour" → 0.0014285…; how many decimal places must be stored so it round-trips exactly? (`TBD` in [F-08 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#92-operation--one-step-of-a-routing-at-one-work-unit-with-its-standard))~~ **Resolved 2026-09-28 (PO):** up to 6 decimal places, derived rates rounded half-up (F-08 §9.2). | PO / Engineering |
| Item picker text | `US-FND-WMS-002`, `US-FND-WMS-003` | ~~The BOM and Routing pages each have an item picker; its text is not declared in UX 08 yet (`TBD`)~~ **Resolved 2026-09-28 (PO):** "Product" / "Pick a product" / "No products yet — declare one first." (UX 08 `wms-picker.*`). **Superseded later on 2026-09-28:** the picker is retired — both pages open on a product list (UX 08 `wms-list.*`). | PO |
| W-18 | `US-FND-WMS-002`, `US-FND-WMS-003` | Keys (2026-10-01): is `step_number` unique per routing version, can a dependency pair be listed twice, can one BOM list the same component twice? | `foundation-domain` / PO |
| W-19 | `US-FND-WMS-002` | Keys (2026-10-01): F-08 says BOM ranges must not overlap per **product**, but also one released version per **flow** per date. The spec's Keys line uses the per-flow rule; confirm | `foundation-domain` / PO |

## Stories


| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-WMS-002`](#us-fnd-wms-002) | Build versioned BOMs | 🟢 Ready | As an **implementor**, I want versioned component structures with scrap factors, so material consumption can be compared against plan. → [detail](#us-fnd-wms-002) | `US-FND-PRO-001` |
| [`US-FND-WMS-003`](#us-fnd-wms-003) | Manage routings & time-versioned cycle time | 🟢 Ready | As an **implementor**, I want to maintain operation sequences with time-versioned standard cycle times, so last month's OEE doesn't change every time a standard is updated. → [detail](#us-fnd-wms-003) | `US-FND-PRO-001`, `US-FND-PRO-003`, `US-FND-SIT-001` |
| [`US-FND-WMS-006`](#us-fnd-wms-006) | Map machine product codes to a product | 🟡 Partly blocked | As a **Plant Admin/IT (internal implementor)**, I want to record which raw codes each machine sends for a product, so Performance uses the right standard when a line runs from the PLC alone. → [detail](#us-fnd-wms-006) | `US-FND-WMS-003`, `US-FND-DSR-001`, `US-FND-PRO-001` |

## Detail blocks

---

#### US-FND-WMS-002

**Build versioned BOMs**

**Status:** 🟢 Ready — W-8 resolved 2026-09-30; dispensing tolerance added 2026-10-01 with its UX 08 screen text.

> **In short:** the implementor records which components (and how much scrap) go into each product, as
> numbered versions on the product's **BOM** tab, shown as a tree. Quantities are the net recipe per a recipe
> base quantity. This lets material use be compared against plan.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want versioned component structures with scrap factors, so
material consumption can be compared against plan.

**2. Context**

- **Component lists are hierarchical:** a component can have its own component list
  ([F-08 §3.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#core-concepts)).
  **Multi-level is prepared, not required (PO, 2026-09-30):** a single-level BOM (finished good → its materials)
  is valid. When a semi-finished product gets its own BOM, the tree shows the extra level with no other change.
- **Phantom (2026-09-30):** a semi-finished product that is **never stocked** and has **no work center of its
  own** (e.g. a premix poured by hand into the mixer) has a phantom BOM. It is still `semi_finished`; phantom is
  a flag on its BOM, not a product type. A never-stocked stage that runs on its **own** work center (a dough on
  the mixers) is **not** phantom: it needs its own jobs for its own OEE
  ([F-08 line across work centers](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#a-line-across-several-work-centers--biscuit-example-po-2026-09-30),
  [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-product-type-packaging-phantom-bom-tree.md)).
- **Net recipe plus losses (PO, 2026-09-30, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-bom-net-recipe-shrinkage-base-qty.md)):**
  a BOM line is what the recipe needs, **without** expected losses. Losses are added on top when planning:
  component scrap (per material, on the line) and shrinkage (per process step and work unit, on the routing,
  [`US-FND-WMS-003`](#us-fnd-wms-003)). Rejects are separate and counted in PRODUCTION. Lines are written per a
  **recipe base quantity** ("per 1,000 kg", "per 40 packs"), default 1 in the product's base unit. It is not a
  batch size: a real batch can be any size, the recipe scales.
- **Versions (W-8 closed 2026-09-30, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-work-master-wave-1.md)):**
  each BOM version is its own row, numbered 1, 2, 3… with a status: draft (editable, no job can use it), released
  (usable, read-only), retired. A work order pins the version it was released with by its `bom_id`
  ([F-08 versions and status](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#versions-and-status-po-2026-09-30)).
- **Material per step (2026-09-30):** a line can name the routing step where it enters (consumed) or comes out
  (produced). Without a step it enters at the first step.
- **Flows (2026-09-30, wave 2):** a BOM belongs to one flow of the product (one way of making it). Every product
  has at least one flow; a flow "Standard" is created automatically and existing BOMs move into it, so a product
  made only one way never sees the word "flow". The flow bar and adding flows are not part of this story (no PRD
  story yet).
- **Dispensing tolerance (PO, 2026-10-01, EBR, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-10-01-work-master-ebr-step-detail.md)):** a consumed line can say how far
  the weighed quantity may be from its target, as ± a percent or ± a quantity in the line's unit. This is the
  **standard** part of GitHub issue #60 (weight deviation). Recording the weighed quantity, comparing it and
  handling a deviation are execution, parked with #60 (PRODUCTION / future Quality and Inventory)
  ([F-08 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#94-bom_line--one-component-on-a-bom)).
- **Who and where:** the implementor, on desktop.
- **Where it sits (PO, 2026-09-30, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-bom-routing-as-product-tabs.md)):**
  the BOM editor is the **BOM tab on the Product detail page** (Measurement · Conversions · BOM · Routing ·
  History). It is no longer an item in the Production sidebar group, and the separate BOM list page (2026-09-28)
  is gone: the "which products still lack a BOM" view is now a **BOM status column on the product list**. The
  data is FOUNDATION's and other domains read it; a recipe belongs to one product. There is no "Work Master" menu
  item or tab ([UX 08](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#work-master-pages--bom-and-routing-on-product-detail-po-decision-2026-09-30),
  [UX 00 § App shell](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md#app-shell--sidebar-grouped-by-domain-po-decision-2026-09-28)).
- **Screen:** `SCR-FND-WMS-002` — the BOM tab of `SCR-FND-PRO-001`, shown and edited as a tree; a version list
  with valid-from/to dates; starting a new version is an explicit step that closes the old one
  ([UX 08 § BOM tab](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#bom-tab--a-tree-on-product-detail-only-po-decision-2026-09-30)).

**3. Expectation**

Screen text is copied word for word from [UX 08](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#work-master-pages--bom-and-routing-on-product-detail-po-decision-2026-09-30)
and [UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md#screens), with its key. If they differ, UX is right.

*Product list — BOM column (on `SCR-FND-PRO-001`)*
- Column "BOM" (draft) (`pro-001.list.col.bom`): the current BOM version (`valid_from` → `valid_to` or "open")
  and its component count.
- Badges: "No BOM yet" (draft) (`wms-002.badge.none`) · "Locked by \<work order\>" (draft) (`wms-list.badge.locked`).
- **Badges only for made products** (`semi_finished`, `finished_good`). A bought product (`raw_material`,
  `packaging`, `consumable`) shows "—" (draft) (`pro-001.list.not-made`).
- Filter toggle: "Only products without a BOM" (draft) (`wms-list.filter.without`). Rows needing action (no BOM)
  can be sorted to the top.

*BOM tab (product detail)*
- Tab label: "BOM" (draft) (`pro-001.tab.bom`). The product is part of the page address, so back/forward and
  shared links to a product's BOM tab work.
- **Version** (BOM header): its version, the dates it is valid, "Recipe base quantity" (draft)
  (`wms-002.base-qty.label`) with its unit, and, for a `semi_finished` product only, a **Phantom** checkbox.
  The header shows the total components and a "used by active WO" marker.
- **The tree:** the product at the top, its components below, their own components below those. One view, no
  view-mode switch.
  - Top node: the product and "Quantities per \<base qty\> \<unit\>" (draft) (`wms-002.base-qty.summary`);
    default "per 1 \<unit\>".
  - Each node: code — name · type badge (and "Primary" / "Secondary" for packaging) · quantity **as typed** (per
    the parent's recipe base quantity) · "\<n\>% of batch" (draft) (`wms-002.tree.percent`) when the line's unit
    is in the same measurement group as the base unit · "\<qty\> \<unit\> per \<product unit\>" (draft)
    (`wms-002.tree.qty-per-top`), the quantity per 1 unit of this product. A missing unit conversion shows
    "No unit conversion" on the node instead of a number. All numbers are **net**: scrap and shrinkage are
    planning, not shown here.
  - Each first-level line shows "Enters at: \<step\>" (draft) (`wms-002.line.enters-at`); a produced line shows
    "Produced at \<step\>" · "Primary output" (draft) (`wms-002.line.produced-at`).
  - Phantom node: drawn dashed with the badge "Phantom" (draft) (`wms-002.tree.phantom`); its lines still show
    beneath it.
  - A `semi_finished` line with no BOM of its own is a leaf with the "No BOM yet" badge and a link to that
    product's BOM tab.
  - The same component under two parents is shown under both.
  - Folded below the first level by default when the tree has more than 20 nodes.
- **Editing happens in the tree, first level only.** "Add component" (draft) (`wms-002.tree.add`) under the top
  node; selecting a first-level node opens its line (component, quantity, unit, scrap factor, entry step, and
  since 2026-10-01 the **dispensing tolerance**: type ± % / ± quantity, and its value) for edit or removal.
- **Dispensing tolerance** ([UX 08](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#ebr-step-detail--instructions-checks-yield-hold-time-dispensing-tolerance-po-decision-2026-10-01)): in the line editor, "Dispensing tolerance" · "± %" · "± quantity"
  (draft) (`wms-002.line.tolerance`) — the type, then the value. Hidden on a produced line and on a line of a
  `consumable` product. When set, the tree node shows it after the quantity: "± \<value\>%" / "± \<value\>
  \<unit\>" (draft) (`wms-002.line.tolerance.summary`). Deeper levels belong to other products' BOMs and are read-only here: "Open \<product code\>
  BOM" (draft) (`wms-002.tree.open-child`); on hover / long-press: "Part of \<product code\>'s BOM — edit it
  there." (draft) (`wms-002.tree.read-only`).
- **As of:** a date picker "As of" (draft) (`wms-002.tree.as-of`), default today, picks which BOM version of each
  product is used; each node shows its version's `valid_from`. Editing is allowed only when "As of" is today.
- **Readable within 3 seconds:** parent product, active version, and line count.

Retired text, not used: `wms-002.list.columns`, `wms-list.empty`, `wms-list.link.other` (UX 08, 2026-09-30) and
`nav-sidebar.item.bom` (UX 00, 2026-09-30). Keys not reused.

| State | What the user sees |
|-------|--------------------|
| Empty | The product has no BOM on the chosen date: "No BOM on \<date\>." + "Add component" (draft) (`wms-002.tree.empty`) |
| Past date | "As of" set to a past date: "Showing the BOM as of \<date\>. Switch to today to edit." (draft) (`wms-002.tree.past-read-only`); nothing is editable |
| Loading | A tree skeleton |
| Error | Input intact, plus a failure message |
| Save blocked — dispensing tolerance | Input kept. Type set without a value: "Enter the tolerance, or clear the type." (draft) (`wms-002.error.tolerance-value`). Percent not above 0 or not below 100: "Tolerance must be more than 0% and less than 100%." (draft) (`wms-002.error.tolerance-percent`). Quantity not above 0: "Tolerance must be more than 0." (draft) (`wms-002.error.tolerance-absolute`). On a produced line or a `consumable` product's line the field is not shown, so it can't be set |
| Save blocked — phantom on a non-semi-finished product | "Only a semi-finished product can have a phantom BOM." (draft) (`wms-002.error.phantom-not-semi`) |
| Edit on a released version | The version is read-only; a structural edit (add or remove a line, change a line's component or entry step) starts the next version as a draft copy |
| Offline | Unavailable |
| Success | The new version is saved as a draft. It only takes effect once it is released and its validity period starts |
| No entitlement (license gate) | Tenant without a **Production** license: the BOM tab, the BOM column and the "without a BOM" filter are **hidden** on the Product screens. A direct link to a product's BOM tab opens its Measurement tab. The upsell stays on the locked Production group heading in the sidebar ([UX 08](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#work-master-pages--bom-and-routing-on-product-detail-po-decision-2026-09-30), option A, 2026-09-30). This is separate from "No permission": entitlement is about what the *tenant* bought; permission is about the *subject's* ABAC scope ([`PLATFORM/DOCS-EN/01-license-manager.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PLATFORM/DOCS-EN/01-license-manager.md#module-license-gate--data-ownership)) |
| No permission | Read-only |

**4. Calculation**

*Planned input: how much of a component one unit of the product needs*
([F-08 planned input](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#planned-input--net-recipe-plus-losses-po-2026-09-30))
- The net quantity per one unit of the product (line quantity ÷ recipe base quantity), grown by the line's scrap
  factor, then divided by (1 − shrinkage) of the line's entry step and each later step on the routing. This
  number is used for planning; the tree itself shows only net quantities.
- Example: mix recipe, base 1,000 kg: A 700 kg, B 300 kg; A scrap 0.02. 100 kg of mix on Mixer 1 (shrinkage
  0.05): A = 100 × 0.7 × 1.02 ÷ 0.95 = **75.16 kg**, B = 100 × 0.3 ÷ 0.95 = **31.58 kg**. Same BOM on Mixer 2
  (shrinkage 0.03): A = 73.61 kg, B = 30.93 kg.

> [!note]- Exact formula (for developers)
> ```
> planned qty of a component per 1 unit of the product
>   = (BOM_LINE.qty_per_unit ÷ BOM.base_qty) × (1 + BOM_LINE.scrap_factor)
>     ÷ Π (1 − OPERATION.planned_shrinkage of the line's step and each later step)
> line step = BOM_LINE.operation_id; empty → the first step (every step's shrinkage applies)
> ```

*Shown on each tree node*
- **Quantity per 1 unit of this product:** the line quantity divided by its parent's recipe base quantity,
  multiplied down the branch. Example: Biscuit X pack BOM, base 1 pack: 40 baked biscuits; Baked biscuit BOM,
  base 1 pc: 0.96 g dough → dough per pack = 40 × 0.96 = 38.4 g.
- **% of batch:** the line quantity as a share of its parent's recipe base quantity, only when both are in the
  same measurement group. Example: base 1,000 kg, A 700 kg → 70% of batch.

> [!note]- Exact formula (for developers)
> ```
> qty_per_top(node) = Π over the branch (BOM_LINE.qty_per_unit ÷ BOM.base_qty), converted to each parent's unit
> percent_of_batch  = BOM_LINE.qty_per_unit ÷ BOM.base_qty × 100   (only if same MEASUREMENT_GROUP, after conversion)
> step ratio (one unit of a step's primary output) — calculated from the quantities, never stored, same measurement group only
> ```

*Dispensing tolerance: the allowed range of a weighing* ([F-08 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#94-bom_line--one-component-on-a-bom))
- The **target** a weighing is compared to is the line's planned quantity for the job: the planned input above
  × the job quantity, so it already includes scrap and shrinkage. Allowed range = target ± tolerance.
- Example: component A, planned 75.16 kg for the job. Percent 1 → **74.41–75.91 kg**. Absolute 0.2 kg →
  **74.96–75.36 kg**.
- The limits are symmetric ±. Asymmetric limits (−0 / +2%) are not supported (`TBD`, W-12).

> [!note]- Exact formula (for developers)
> ```
> target = planned qty of the component per 1 unit of the product × job quantity      (planned input, above)
> percent:  allowed = target × (1 − dispense_tolerance_value ÷ 100) … target × (1 + dispense_tolerance_value ÷ 100)
> absolute: allowed = target − dispense_tolerance_value … target + dispense_tolerance_value   (in BOM_LINE.uom_id)
> dispense_tolerance_type empty → no dispensing check for this line
> ```

*Edge cases*
- Scrap factor empty → 0. A negative scrap factor is rejected.
- A scrap factor of 1 (100%) or more asks for confirmation, because it is almost always a typo.
- Shrinkage empty → 0. The product has no routing → no shrinkage is applied (scrap only), flagged "No routing
  yet".
- Levels stay apart: a mix's shrinkage sits on the mix's routing, not on the pack's.
- A missing unit conversion on a node → "No unit conversion" instead of a number, never a guess.
- A circular BOM (item A needs B, B needs A) is rejected, with the chain shown.
- BOM depth beyond 5 levels triggers a warning.
- A line of a `consumable` product is left out of the material requirement. What a phantom means for stock and
  material requirement is the future Inventory domain's call (`TBD` in F-08 §9.3); the tree counts a phantom's
  lines as if they sat directly under each parent that uses it.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `PRODUCT` (`product_type`, `packaging_level`, `base_uom_id`), `PRODUCT_UOM_CONVERSION` (for `base_uom_id` and node conversions), `PRODUCT_FLOW`, `ROUTING` / `OPERATION` (`operation_id`, `step_name`, `planned_shrinkage`, for entry steps and planned input), `UNIT_OF_MEASUREMENTS` (with its measurement group) |
| **Writes** | `BOM` (`product_id`, `flow_id`, `version`, `status`, `base_qty`, `base_uom_id`, `is_phantom`, `valid_from`, `valid_to`) per [F-08 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#93-bom--from-what-for-one-product) · `BOM_LINE` (`bom_id`, `component_product_id`, `operation_id`, `line_use`, `is_primary_output`, `qty_per_unit`, `uom_id`, `scrap_factor`) per [F-08 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#94-bom_line--one-component-on-a-bom). `scrap_factor` is a fraction (`0.05` = 5%). Since 2026-10-01 also `dispense_tolerance_type` (`percent` / `absolute`, empty = no check) and `dispense_tolerance_value` |
| **Not real fields: don't add** | No stored step ratio and no stored planned quantity (both calculated). No `consumable` value in `line_use` (consumable is a `product_type`). No material class on a line: a produced line names a `semi_finished` product. No separate `routing_version_locked` / `bom_version_locked`: the work order pins by `bom_id`. No weighed quantity on `BOM_LINE`: recording a weighing is PRODUCTION's (#60). No asymmetric tolerance fields (W-12) |

**6. Rules & constraints**

1. **Structural vs standard edits** ([F-08 versions and status](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#versions-and-status-po-2026-09-30)):
   adding or removing a line, or changing a line's component or entry step, is structural — allowed only on a
   `draft`. On a `released` version it opens the next version as a draft copy; the old one stays released until
   the new one is released, then is `retired`.
2. At most one `released` version of a flow's BOM is active on a given date. Validity ranges must not overlap.
3. Circular BOMs are rejected.
4. A BOM version locked onto a released work order is never edited in place; the work order keeps pinning its
   `bom_id`, and an edit starts the next version. Only `released` versions can be picked by a work order.
5. The quantity per unit must be greater than 0, and is per the version's recipe base quantity.
6. The recipe base quantity must be greater than 0 (default 1). Its unit must be the product's base unit or have
   a conversion path to it on `valid_from` (same rule as a routing step's item unit).
7. Phantom is allowed only on the BOM of a `semi_finished` product; otherwise the save is blocked with
   `wms-002.error.phantom-not-semi`. The checkbox is shown only for a `semi_finished` product.
8. A line's entry step must be a step of a routing of the same product. A `produced` line names a
   `semi_finished` product. A step with produced lines has **exactly one** primary output.
9. Only first-level lines are editable on this tab, and only when "As of" is today.
10. **Dispensing tolerance** (PO 2026-10-01, [F-08 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#94-bom_line--one-component-on-a-bom)): optional. Only on
    `consumed` lines, never on a line of a `consumable` product (it is left out of the material requirement).
    `dispense_tolerance_value` is required when `dispense_tolerance_type` is set and empty otherwise. `percent`:
    above 0 and below 100 (`1` = ±1%). `absolute`: above 0, in the line's own unit (`0.2` = ±0.2 kg). Changing it
    on a released BOM is a **structural** edit (BOM lines are not effective-dated) and opens the next version
    ([F-08 versions and status](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#versions-and-status-po-2026-09-30)). F-08 W-17 still asks whether it should
    instead be effective-dated like a step parameter: until the PO answers, build it as structural.
11. **Who may do it:** creating or updating a BOM or its component lines needs **tenant access** for now.
    `TBD — foundation-domain: scope level undetermined (P-1)`: neither `BOM` nor `BOM_LINE` has a direct Site
    Hierarchy foreign key, and both inherit `PRODUCT`'s own unresolved tenant-vs-enterprise scope question
    ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#work-master-wms--08-work-mastermd-9)).
    Until P-1 resolves, treat them as `tenant`-scoped per the ABAC "no FK → resolves upward" default.

**7. Acceptance criteria**

- **AC-1** **Given** a finished-goods product with no BOM, **when** the implementor adds 3 components on its BOM
  tab and saves version 1 valid from tomorrow, **then** the BOM is saved as a draft and shown as the definition
  about to take effect once released.
- **AC-2 (validation)** **Given** the quantity per unit is entered as 0, **when** saved, **then** it is rejected
  with "Quantity per unit must be greater than 0".
- **AC-3 (validation)** **Given** the chosen components form a circular chain, **when** saved, **then** it is
  rejected and the circular chain is shown.
- **AC-4 (released version)** **Given** BOM version 2 of `FG-1001` is `released` and pinned by a released work
  order, **when** the implementor adds a line, **then** version 2 is not changed, version 3 opens as a draft copy
  with the new line, and the work order still points at version 2's `bom_id`. When version 3 is released, version
  2 becomes `retired`. *(Was "not yet testable" until W-8 closed 2026-09-30.)*
- **AC-5 (overlap)** **Given** a released BOM version valid 2026-01-01–open-ended, **when** another released
  version of the same flow is set valid from 2025-12-01, **then** it is rejected for range overlap, naming the
  conflicting range.
- **AC-6 (permission)** **Given** a user without tenant access (BOM's scope pending P-1), **when** they open the
  BOM tab, **then** they can only read.
- **AC-7 (entitlement)** **Given** a tenant without a Production license, **when** any user opens a product,
  **then** there is no BOM tab, no BOM column and no "Only products without a BOM" filter, and a direct link to the
  product's BOM tab opens its Measurement tab. This holds regardless of the user's own ABAC scope (AC-6 is a
  separate gate).
- **AC-8 (tab, no sidebar item)** **Given** a tenant with a Production license, **when** a user opens a product,
  **then** the tabs read Measurement · Conversions · BOM · Routing · History, and the Production sidebar group has
  no "BOM" item. *(Replaces the 2026-09-28 sidebar AC.)*
- **AC-9 (list, gaps shown)** **Given** finished goods `FG-1001` (has a BOM), `FG-1002` (no BOM) and packaging
  product `PK-01`, **when** the user opens the product list, **then** `FG-1001` shows its current BOM version and
  component count, `FG-1002` shows "No BOM yet", and `PK-01` shows "—". Turning on "Only products without a BOM"
  leaves only `FG-1002`. *(Replaces the 2026-09-28 BOM list page AC.)*
- **AC-10 (no products)** *(Retired 2026-09-30 with `wms-list.empty`: there is no separate BOM list; the product
  list's own empty state applies, see [`US-FND-PRO-001`](06-product.md#us-fnd-pro-001).)*
- **AC-11 (recipe base quantity)** **Given** a mix BOM with recipe base quantity 1,000 kg and line A 700 kg, **when**
  the tree is shown, **then** the top node reads "Quantities per 1000 kg" and node A shows "70% of batch" and
  0.7 kg per kg of mix.
- **AC-12 (planned input)** **Given** that BOM with A's scrap factor 0.02 and Mixer 1's step shrinkage 0.05,
  **when** planned input for 100 kg of mix is calculated, **then** A = 75.16 kg and B = 31.58 kg.
- **AC-13 (phantom)** **Given** a `finished_good` product, **when** the implementor saves its BOM as phantom,
  **then** it is refused with "Only a semi-finished product can have a phantom BOM.". For a `semi_finished`
  product it is saved and the node is drawn dashed with "Phantom".
- **AC-14 (deeper level read-only)** **Given** a finished good whose line is a semi-finished product with its own
  BOM, **when** the user selects one of that product's lines in the tree, **then** it cannot be edited and shows
  "Open \<product code\> BOM", which opens that product's BOM tab.
- **AC-15 (as of a past date)** **Given** "As of" is set to 2026-06-01, **when** the tree is shown, **then** each
  product uses the version valid on that date and the tab reads "Showing the BOM as of 2026-06-01. Switch to
  today to edit." with nothing editable.
- **AC-16 (dispensing tolerance, percent)** **Given** component A's line has tolerance `percent` 1 and the job's
  planned quantity of A is 75.16 kg, **when** the allowed range is worked out, **then** it is 74.41–75.91 kg.
- **AC-17 (dispensing tolerance, absolute)** **Given** the same line with tolerance `absolute` 0.2 (kg), **when** the
  allowed range is worked out, **then** it is 74.96–75.36 kg.
- **AC-18 (dispensing tolerance, validation)** **Given** a line of a `consumable` product, **when** the implementor
  opens it, **then** no "Dispensing tolerance" field is shown. **Given** type ± % with value 100, **when** saved,
  **then** it is refused with "Tolerance must be more than 0% and less than 100%." and the input is kept. **Given**
  a type with no value, **then** it is refused with "Enter the tolerance, or clear the type."
- **AC-19 (dispensing tolerance, released BOM)** **Given** BOM version 2 is `released`, **when** the implementor
  changes a line's tolerance, **then** version 2 is not changed and version 3 opens as a draft copy with the new
  tolerance.

**8. Metrics & events**

- **Metric:** `bom_aktif_per_item_produksi` (*active BOMs per produced item*): the % of items produced on the
  pilot line that have a BOM in effect. Target 100% before the first WO is released. Baseline 0, source: the
  `BOM` table.
- **Counter-metric:** the number of BOM revisions created per item per month (>2 means the initial data was
  guessed, not known).
- **Events:** `bom_version_created` (`product_id`, `line_count`).

**Dependencies:** `US-FND-PRO-001`

---

#### US-FND-WMS-003

**Manage routings & time-versioned standard cycle time**

**Status:** 🟢 Ready — W-8 resolved 2026-09-30; step detail (instructions, checks, yield range, hold time) added 2026-10-01 with its UX 08 screen text.

> **In short:** the implementor keeps each product's operation steps and their standard cycle times on the
> product's **Routing** tab, as numbered versions with dated standards. Old reports always use the standard that
> was in force on that day, so last month's OEE never shifts.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to maintain operation sequences with time-versioned
standard cycle times, so last month's OEE doesn't change every time a standard is updated.

**2. Context**

- **The most consequential number:** the standard cycle time is the numerator of Performance **and** the basis
  of capacity calculation ([F-08 §3.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#core-concepts)).
- **Why versioning:** if historical calculation uses the latest version, last month's OEE changes every time a
  standard is updated, and nobody can trust a report that changes on its own.
- **Why realistic values:** cycle times taken from ideal laboratory conditions make Performance always look bad,
  and operators stop trusting the numbers
  ([F-08 §7.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#common-pitfalls)).
  For a punching step the cycle time is the normal full speed for that product, not the speed the operator
  happens to set, otherwise Performance always reads 100%.
- **The standard is per an item unit (PO, 2026-09-28):** a cycle time is three things — a number, a time unit
  and the **item** it is per ("20 seconds / pack"). Machines on one routing count in different units: a filler
  counts packs, a cartoner counts cartons. So each operation step names its own item unit (pack, carton, kg,
  punch), which may differ from the product's base unit and from the unit the machine counter reports. The
  Performance calculation converts the counter to the step's unit through the product's own conversions
  ([`US-FND-PRO-003`](06-product.md#us-fnd-pro-003), [F-08 §9.2.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#921-item-unit-and-conversion-po-2026-09-28), [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) B).
- **Only cycle-time source:** Product's reference cycle times were retired on 2026-09-28
  ([`US-FND-PRO-004`](90-closed-stories.md#us-fnd-pro-004)); this editor is the one place a cycle time is entered.
- **A line across several work centers (PO, 2026-09-30, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-line-across-work-centers-reject-weight.md)):**
  a routing is one product at one work center. A line through several work centers is modelled as one product
  per work center, linked by the BOM (Dough @ Mixing → Baked biscuit @ Baking → Pack @ Packing), so OEE and cycle
  time sit on the product that really runs there
  ([F-08 line across work centers](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#a-line-across-several-work-centers--biscuit-example-po-2026-09-30)).
- **Steps pre-filled from the work center's flow (2026-09-30):** the order of machines inside a work center is
  declared once in the site hierarchy (`EQUIPMENT_FLOW`). Adding the product to a work center pre-fills one step
  per work unit, in flow order. Per step the user sets the output unit, cycle time or throughput, and planned
  shrinkage, and can untick a machine the product skips. Parallel machines (Mixer 1 and Mixer 2) are **one** step
  with a machine class; each station can override its cycle time. Steps that run side by side list what they
  wait for.
- **Versions (W-8 closed 2026-09-30, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-work-master-wave-1.md)):**
  each routing version is its own row, numbered per flow and work center, with a status (draft / released /
  retired). Structural edits (steps, stations, classes) open a new version; standard edits (cycle time, speed,
  setup time, shrinkage, a station override) stay effective-dated inside the version. A work order pins the
  version by its `routing_id`, but its **standard** is read as of the shift's business date, not as of release
  ([F-08 versions and status](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#versions-and-status-po-2026-09-30)).
- **Planned shrinkage (2026-09-30):** mass the process itself loses at a step on a work unit (evaporation,
  moisture). It is not a reject and not component scrap. It differs per work unit for the same product (Mixer 1
  5%, Mixer 2 3%) and is used to plan input ([`US-FND-WMS-002`](#us-fnd-wms-002)). It is measured with a weigh
  test: 100 g before the step → 96 g after gives 0.04. Unknown → leave empty (0).
- **Flows (2026-09-30, wave 2):** a routing belongs to one flow of the product. A product made only one way has
  the automatic flow "Standard" and never sees the word "flow". The flow bar, the parameter standards section and
  the product codes section (`SCR-FND-WMS-007`, now [`US-FND-WMS-006`](#us-fnd-wms-006)) sit on the same tab but
  are **not** part of this story.
- **Step detail like an EBR master recipe (PO, 2026-10-01, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-10-01-work-master-ebr-step-detail.md)):** each step can carry
  - **instructions:** ordered sub-steps the operator follows, some marked "must be confirmed" ([F-08 §9.2.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#925-operation_instruction--ordered-sub-steps-of-a-step-po-2026-10-01-ebr));
  - **in-process checks (IPC):** what the operator measures or judges during the step, with range, sample count and
    frequency ([F-08 §9.2.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#926-operation_check--in-process-checks-of-a-step-po-2026-10-01-ebr));
  - a **yield range** and a **hold time** ([F-08 §9.2.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#927-yield-limits-and-hold-time-po-2026-10-01-ebr)).

  FOUNDATION holds only these **standards**. Recording that an instruction was confirmed, recording check results,
  measuring actual yield, timing the wait and acting on a breach are execution: PRODUCTION / future Quality domain.
  Per-step sign-off, second-person verification and line clearance are **out of scope** until the PO confirms
  pharma as a target market.
- **Where it sits (PO, 2026-09-30, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-bom-routing-as-product-tabs.md)):**
  the **Routing tab on the Product detail page**, next to BOM. No longer an item in the Production sidebar group;
  the separate Routing list page (2026-09-28) is gone and its gap view is a **Routing status column on the product
  list** ([UX 08](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#work-master-pages--bom-and-routing-on-product-detail-po-decision-2026-09-30),
  [UX 00 § App shell](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md#app-shell--sidebar-grouped-by-domain-po-decision-2026-09-28)).
- **Screen:** `SCR-FND-WMS-003` — the Routing tab of `SCR-FND-PRO-001`, the routing & operation editor — a
  version bar and a version timeline per operation. Changing a standard cycle time asks for confirmation ("this
  closes the current record"), then closes the old row and opens a new one
  ([UX 08 § Routing tab](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#routing-tab--versions-steps-and-product-codes-po-decision-2026-09-30-issue-58),
  [UX 08 § Screens](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#screens)).

**3. Expectation**

Screen text is copied word for word from [UX 08](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#work-master-pages--bom-and-routing-on-product-detail-po-decision-2026-09-30)
and [UX 06](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/06-product.md#screens), with its key. If they differ, UX is right.

*Product list — Routing column (on `SCR-FND-PRO-001`)*
- Column "Routing" (draft) (`pro-001.list.col.routing`): the current routing version and its operation count.
- Badges: "No routing yet" (draft) (`wms-003.badge.none`) · "Cycle time missing" (draft)
  (`wms-003.badge.cycle-time-missing`, an operation without a standard cycle time) · "No unit conversion" (draft)
  (`wms-003.badge.no-conversion`, no [F-08 §9.2.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#921-item-unit-and-conversion-po-2026-09-28) path on the current version) · "Locked by \<work order\>" (draft) (`wms-list.badge.locked`) · "Flow changed — review routing" (draft) (`wms-003.badge.flow-changed`, the work center's flow changed since this routing version).
- Badges only for made products (`semi_finished`, `finished_good`); a bought product shows "—" (draft)
  (`pro-001.list.not-made`).
- Filter toggle: "Only products without a routing" (draft) (`wms-list.filter.without`). Rows needing action (no
  routing, cycle time missing, no conversion) can be sorted to the top.

*Routing tab (product detail)*
- Tab label: "Routing" (draft) (`pro-001.tab.routing`). The product is part of the page address.
- **Version bar** at the top: "Version \<n\>" (draft) (`wms-003.version.label`) + status badge "Draft" / "Released"
  / "Retired" (draft) (`wms-003.version.status`) + toggle "Primary routing at \<work center\>" (draft)
  (`wms-003.version.primary`). On a draft, the button "Release" (draft) (`wms-003.version.release`) makes it usable
  by work orders and retires the previous released version from the release date. A structural edit on a released
  version asks "Start version \<n+1\> as a draft?" (draft) (`wms-003.version.new-draft`) and opens the copy.
- Toggle "Steps must be done in order" (draft) (`wms-003.enforce-sequence`).
- An operation table in step order. **Each step:** step name; "Step type" · placeholder "e.g. Mixing, Quality
  check" (draft) (`wms-003.step.type`), free text that suggests step types already used in the tenant; the work
  unit **or** "Machine class" (draft) (`wms-003.step.class`); by work-center type, either
  - **discrete/batch:** the standard cycle time (value, time unit and item unit — see below) or throughput, and the
    batch size; or
  - **continuous:** the standard speed (with its unit);
  - then the standard setup time with its time unit, the crew size, "Planned shrinkage (%)" · hint "Mass lost by
    the process at this work unit (evaporation, moisture). Not rejects." (draft) (`wms-003.shrinkage.label`),
    "Used by this product" (draft) (`wms-003.step.skip`), "Waits for" (draft) (`wms-003.step.waits-for`, a
    multi-select of other steps; all empty = a straight line in step order), and the dates the row is valid.
- **Machine class step:** choosing a class lists the work center's stations of that class, each with "Cycle time
  on \<station\> (empty = default)" (draft) (`wms-003.step.override`).
- **Step detail (2026-10-01, [UX 08](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#ebr-step-detail--instructions-checks-yield-hold-time-dispensing-tolerance-po-decision-2026-10-01)).** Under each step, beside Parameters:
  - **Instructions** (draft) (`wms-003.instr.title`): a numbered list, drag to re-order, one text per row, and a
    "Must be confirmed" (draft) checkbox per row (`wms-003.instr.confirm`). Button "Add instruction" (draft)
    (`wms-003.instr.add`). No rows: "No instructions for this step." (draft) (`wms-003.instr.empty`).
  - **Checks** (draft) (`wms-003.checks.title`): a table with columns "Check" · "Type" · "Target" · "Min" · "Max" ·
    "Unit" · "Samples" · "How often" · "Critical" (draft) (`wms-003.checks.columns`). Type: "Number" · "Pass / fail" ·
    "Note" (draft) (`wms-003.checks.type.options`); *Number* shows target / min / max / unit, the others hide them.
    How often: "Once per run" · "Start, middle and end" · "Every \<n\> \<time unit\>" (draft)
    (`wms-003.checks.frequency.options`); the interval fields show only for "Every …". Button "Add check" (draft)
    (`wms-003.checks.add`). No rows: "No checks for this step." (draft) (`wms-003.checks.empty`).
  - In the step's standards, next to planned shrinkage: "Yield range (%)" · "Min" · "Max" (draft)
    (`wms-003.yield.label`) with the hint "Expected at standard: \<expected\>%" (draft) (`wms-003.yield.hint`), and
    "Max hold time" (draft) (`wms-003.hold.label`), a value plus a time unit, with the hint "Longest wait from the end
    of this step to the start of the next." (draft) (`wms-003.hold.hint`).
- **Derived weight of one unit** per step, read-only: "\<weight\> per \<unit\> (wet)" / "(dry)" (draft)
  (`wms-003.step.unit-weight`) — "wet" before the shrinkage step, "dry" after.
- Every standard shows the **row in effect today**, plus a "view history" link.
- **Readable within 3 seconds:** the version and its status, the number of operations, and which work units are
  used.

*Entering a cycle time (discrete/batch, 2026-09-28, [UX 08](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#screens))*
- Three fields: the value, the **time unit**, and the **item unit**. They are shown as one phrase, for example
  "20 seconds / pack" or "1 s / punch".
- The item-unit picker offers only the product's base unit plus units that have a conversion path to it on the
  row's valid-from date. When only the base unit is listed, the picker shows a link to the product's
  Conversions tab (`SCR-FND-PRO-003`) to add others.
- Throughput mode ("500 per hour") reads as per item: "500 packs per hour", and is **saved as typed** — stored in
  the time unit picked (hour), not converted to seconds (PO 2026-09-28, [F-08 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#92-operation--one-step-of-a-routing-at-one-work-unit-with-its-standard)).

Retired text, not used: `wms-003.list.columns`, `wms-list.empty`, `wms-list.link.other` (UX 08, 2026-09-30) and
`nav-sidebar.item.routing` (UX 00, 2026-09-30). Keys not reused.

| State | What the user sees |
|-------|--------------------|
| Empty | "No routing for this item yet", plus a warning: "Without a standard cycle time, Performance and capacity cannot be computed." |
| Empty item-unit picker | Only the base unit is listed, with a link to the product's Conversions tab (`SCR-FND-PRO-003`) to add others |
| Loading | A table skeleton |
| Error | Input intact, plus a failure message |
| Save blocked — no conversion path | "Cannot save: there is no unit conversion between \<uom_id unit\> and \<base UOM unit\> for product \<product code\> on \<valid_from\>. Add it on the product's Conversions tab, then save again." ([F-08 §9.2.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#921-item-unit-and-conversion-po-2026-09-28), UX 08 `SCR-FND-WMS-003`). Input stays intact |
| Save blocked — shrinkage out of range | "Planned shrinkage must be from 0 to less than 100%." (draft) (`wms-003.error.shrinkage-range`) |
| Save blocked — steps in a loop | "These steps wait for each other in a loop: \<steps\>." (draft) (`wms-003.error.loop`) |
| Yield range does not contain the expected yield | A **warning, not a block**, on save: "Expected yield \<expected\>% is outside \<min\>–\<max\>%" (`wms-003.yield.warn`), e.g. "Expected yield 95% is outside 96–99%". The range is saved if the user goes on |
| Save blocked — instruction | Row without text: "Write the instruction, or remove the row." (draft) (`wms-003.instr.error.empty`). Input kept |
| Save blocked — check | Number check with neither min nor max: "A number check needs a min, a max, or both." (draft) (`wms-003.checks.error.no-limit`). Min above target or target above max: "Min must not be above target, and target not above max." (draft) (`wms-003.checks.error.range`). Number check without unit: "Pick a unit for this check." (draft) (`wms-003.checks.error.unit`). "Every …" without a value above 0 or a time unit: "Enter how often, more than 0, with its time unit." (draft) (`wms-003.checks.error.interval`). Critical on a Note: "A note can't be critical." (draft) (`wms-003.checks.error.critical-note`). Name already used in the step: "\<check name\> is already a check on this step." (draft) (`wms-003.checks.error.duplicate`). Input kept |
| Save blocked — yield or hold time | Yield min above max or below 0: "Min yield must be 0 or more and not above max." (draft) (`wms-003.yield.error.range`). Hold time not above 0 or without a time unit: "Hold time must be more than 0, with a time unit." (draft) (`wms-003.hold.error`). Input kept |
| Back-dated standard | Before save: "This changes KPIs of \<n\> past shifts." (draft) (`wms-003.warn.backdated`) |
| Released version | Read-only; a structural edit asks `wms-003.version.new-draft` first |
| Offline | Not editable. The value in effect must be available on the operator's device for local calculation |
| Success | The new row is saved with its validity. The old row stays readable in history |
| No entitlement (license gate) | Tenant without a **Production** license: the Routing tab, the Routing column and the "without a routing" filter are **hidden** on the Product screens; a direct link to the Routing tab opens the Measurement tab. The upsell stays on the locked Production group heading ([UX 08](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#work-master-pages--bom-and-routing-on-product-detail-po-decision-2026-09-30), option A, 2026-09-30). This is separate from "No permission" (tenant-level license vs. subject-level ABAC scope). See [`PLATFORM/FLOW/01-module-entitlement-check.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PLATFORM/FLOW/01-module-entitlement-check.md) |
| No permission | Read-only |

**4. Calculation**

*Which time standard applies on a given day*
- The row whose validity period covers the shift's business date — not the value at work-order release. Every
  historical figure uses the standard that was in force on its date, never the latest one. On a class-based step,
  the station the job actually ran on uses its own override if set, else the step's default. Continuous work units
  use the standard speed instead of the standard cycle time, resolved to seconds through the row's time unit
  ([F-08 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#92-operation--one-step-of-a-routing-at-one-work-unit-with-its-standard),
  [§9.2.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#922-operation_work_unit--which-stations-can-run-a-class-based-step-po-2026-09-30)).
- Example: a cycle time of 20 s in effect January–June, improved to 18 s from July → every figure for
  2026-05-12 uses 20 s.
- Example (class step): step "Mix", class Mixer, default 600 s / batch; Mixer 2 override 540 s. A batch on
  Mixer 1 is measured against 600 s, on Mixer 2 against 540 s.
- A **back-dated** standard fix is allowed and recalculates the affected past shifts, with the warning above;
  the change is recorded in the audit log.

> [!note]- Exact formula (for developers)
> ```
> active on date: valid_from ≤ business_date AND (valid_to IS NULL OR valid_to > business_date)
> cycle_time_ideal(operation, station, business_date) =
>     OPERATION_WORK_UNIT.cycle_time_value (operation, station, active on date) if set
>     else OPERATION.cycle_time_value (active on date)
> ```

*Saving a typed rate* (PO 2026-09-28, [F-08 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#92-operation--one-step-of-a-routing-at-one-work-unit-with-its-standard), [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) addendum)
- A rate is saved in the time unit the user picked, never normalised to seconds. The stored value is the batch
  size divided by the rate, and the row's time unit is the one picked. Seconds appear only at calculation time,
  through the time unit's seconds.
- Example: batch size 1, "500 per hour" → stored `0.002` with time unit hour; at calculation time
  0.002 × 3,600 = 7.2 seconds per item.
- **Precision (PO, 2026-09-28, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md)):** the stored value keeps up to **6 decimal places**, the
  same limit as a conversion factor. A derived rate is rounded half-up: "700 per hour" → 0.001429 hour;
  "500 per hour" → 0.002 hour (exact).

> [!note]- Exact formula (for developers)
> ```
> cycle_time_value   = batch_size ÷ rate            (in the time unit picked)
> time_conversion_id = the time unit picked          (e.g. hour — not seconds)
> seconds (calc time) = cycle_time_value × TIME_CONVERSIONS.seconds_per_unit
> ```

*How the standard feeds Performance* ([04 §5](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/04-metrics-framework.md#5-metrik-operasional-oee-family))
- Performance is the ideal time the output should have taken, divided by the actual production time (APT).
- Example: a shift on 2026-05-12 with 1,000 units of output, the 20-second standard in force and `APT` of
  378 minutes → `Performance = (20 × 1,000) / 60 / 378 = 88.2%`. Using the July row (18 s) gives 79.4%: **the
  historical number would change**, which is exactly what this rule prevents.
- A step counts for Performance when it has a standard (cycle time or standard speed). The step type plays no
  part.

> [!note]- Exact formula (for developers)
> ```
> Performance = (cycle_time_ideal × total_output) / APT
> ```

*Converting the counter to the step's item unit* ([F-08 §9.2.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#921-item-unit-and-conversion-po-2026-09-28))
- When the machine counter counts **units** (`count_basis = 'unit'`) in another unit than the step's item unit,
  the count is first converted to the step's unit through the product's conversions in force on the business
  date, then divided by the batch size to get cycles, then multiplied by the cycle time. A counter that counts
  **machine cycles** (`count_basis = 'cycle'`) skips the conversion: a cycle has no item unit. A punching step
  uses `punch` as its item unit with a cycle counter and batch size 1, so its Performance is fully in punches.
- Example: product conversion 1 carton = 12 pack (base unit pack). Step 10 at WU_01 (filler) is 20 s / pack;
  step 20 at WU_02 (cartoner) is 216 s / carton. The counter at WU_02 reports **1,200 pack** (counts units,
  batch size 1) → 1,200 ÷ 12 = **100 carton** → 100 × 216 s = **21,600 s** ideal operating time.
- A conversion path is: the same unit (no conversion); one conversion row between the two units, read forward
  (× factor) or backward (÷ factor); or two rows joined through the product's base unit. Only rows in force on
  the date asked about count.

> [!note]- Exact formula (for developers)
> ```
> count_basis = 'unit':   cycles = convert(sensor_count, ASSET_TAGS.uom_id → OPERATION.uom_id, business_date) / batch_size
> count_basis = 'cycle':  cycles = sensor_count
> ideal_seconds = cycles × OPERATION.cycle_time_value × TIME_CONVERSIONS.seconds_per_unit
>
> conversion path (a → b, product, date):
>     a = b                                                  → × 1
>     PRODUCT_UOM_CONVERSION row a → b active on date        → × conversion_value
>     PRODUCT_UOM_CONVERSION row b → a active on date        → ÷ conversion_value
>     two such rows joined through PRODUCT.base_uom_id       → both steps applied
> active on date: valid_from ≤ date AND (valid_to IS NULL OR valid_to > date)
> ```

*Weight of one unit at each step* (shown read-only on each step, [F-08 line across work centers](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#a-line-across-several-work-centers--biscuit-example-po-2026-09-30))
- The product's standard weight (its finished weight at the end of this routing, [`US-FND-PRO-002`](06-product.md#us-fnd-pro-002))
  divided by (1 − shrinkage) of each later step. "Wet" and "dry" are simply before and after the step that has
  shrinkage; nobody marks a work unit wet or dry.
- Example (Baked biscuit, standard weight 0.96 g): 1 Mold, shrinkage 0 → 0.96 ÷ (1 − 0.04) = **1.00 g per piece
  (wet)**; 2 Oven, shrinkage 0.04 → **0.96 g (dry)**; 3 Cooling, shrinkage 0 → **0.96 g (dry)**. For a count unit
  produced at one step only (like punch), the weight comes from the product conversion (`1 punch = 20 g`).

> [!note]- Exact formula (for developers)
> ```
> weight of one unit leaving step k = PRODUCT_DETAIL.standard_weight ÷ Π (1 − OPERATION.planned_shrinkage of each later step)
> unit: PRODUCT_DETAIL.weight_uom_id (mass)
> planned_shrinkage stored as a fraction; entered and shown as % (5 % → 0.05)
> ```

*Yield of a step* ([F-08 §9.2.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#927-yield-limits-and-hold-time-po-2026-10-01-ebr))
- Yield is how much a step actually put out, as a share of what its actual input should have given at the net
  recipe — before any loss. FOUNDATION stores only the allowed range; PRODUCTION measures the actual figure.
- **Actual input** = every consumed BOM line entering at this step + the output of the step(s) it follows
  (the previous step, or every step it waits for). Each is converted to the step's output unit (`OPERATION.uom_id`):
  BOM lines through the net ratio (line quantity ÷ recipe base quantity), the previous output through the
  product's conversions.
- **Component scrap is outside yield:** scrap is lost before material enters the step; only what entered counts.
- **Expected yield at standard** = 100 × (1 − planned shrinkage of this step). The yield range is the allowed spread
  around it.
- Example (example data): mix recipe base 1,000 kg, A 700 kg, B 300 kg; Mixer 1 shrinkage 0.05; yield range 93–97%.
  Dispensed: A 73.5 kg, B 31.5 kg → theoretical output 73.5 ÷ 0.7 = **105 kg** (B gives the same, 31.5 ÷ 0.3). Actual
  mix out 100 kg → yield = 100 × 100 ÷ 105 = **95.2%**, inside 93–97%. Expected yield = 100 × (1 − 0.05) = **95%**.
- **Output counted in pieces:** shrinkage is a mass loss. An oven counted in pieces has expected yield 100% in
  pieces even with shrinkage 0.04, and its range should be written that way. Whether such a step also needs a
  mass yield is `TBD` (W-15).

> [!note]- Exact formula (for developers)
> ```
> theoretical output of step k = actual input of step k, converted to OPERATION.uom_id at the net recipe ratio
>                                (BOM_LINE.qty_per_unit ÷ BOM.base_qty; no scrap_factor, no planned_shrinkage)
> actual input of step k       = Σ actual qty of consumed BOM_LINE with operation_id = k (no step → first step)
>                                + actual output of the previous step (step_number order) or of every
>                                  OPERATION_DEPENDENCY predecessor
> actual yield of step k (%)   = 100 × actual primary output of step k ÷ theoretical output of step k
> expected yield (%)           = 100 × (1 − OPERATION.planned_shrinkage)
> on save: warn if expected yield ∉ [yield_min_pct, yield_max_pct] (the bounds that are set)
> ```

*Yield edge cases* (copied from F-08)

| Case | Answer |
|---|---|
| Actual input is 0 or not recorded | "Cannot be calculated", never 0% or 100% |
| Lines dispensed out of recipe ratio (A gives 105 kg, B gives 100 kg) | `TBD — needs PO confirmation` (W-16). F-08 proposes, not decided: the **smallest** of the per-line figures (the limiting component), here 100 kg; a line of a `consumable` product is left out |
| No conversion path from an input to the output unit | "Cannot be calculated", naming the two units — never an unconverted figure |
| Yield above 100% | Possible (mass gain, weighing error). Inside `yield_max_pct` → fine; above → out of range |
| Only `yield_min_pct` set | Only low yield is out of range |
| A class-based step | One range for all stations |
| Out-of-range result | What happens (warning, deviation, block) is PRODUCTION's / Quality's call |

*Hold time* ([F-08 §9.2.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#927-yield-limits-and-hold-time-po-2026-10-01-ebr))
- The longest allowed wait from the **end of this step** to the **start of the step that follows it**, for the same
  job. Example: "Granulate" → "Dry", hold time 2 hours: granules finished 08:00 must enter the dryer by 10:00.

| Routing shape | Which step the limit applies to |
|---|---|
| Straight line (no "Waits for") | The next step in step order that the product uses |
| With "Waits for" | **Every** step that waits for this one, each timed separately with the same limit (Bake → Glaze and Bake → Print, both within 1 hour). A different limit per successor is not supported (`TBD`, W-11) |
| Last step of the routing | No successor → the limit has no effect. A hold across routings (dough at Mixing → molding at Baking) is `TBD` (W-10) |

- A minimum wait (resting, cooling) is not modelled (W-11).

> [!note]- Exact formula (for developers)
> ```
> hold limit (seconds)  = OPERATION.max_hold_time × TIME_CONVERSIONS.seconds_per_unit (hold_time_conversion_id)
> limit applies to each successor of step k:
>   OPERATION_DEPENDENCY rows with predecessor_operation_id = k, or, if the routing has none,
>   the next used step in step_number order
> breach = start of successor − end of step k > hold limit      (measured and acted on by PRODUCTION)
> ```

*Instructions and checks: what is stored* ([F-08 §9.2.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#925-operation_instruction--ordered-sub-steps-of-a-step-po-2026-10-01-ebr), [§9.2.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#926-operation_check--in-process-checks-of-a-step-po-2026-10-01-ebr))
- An **instruction** is text the operator follows inside a step, in order. It has no station, cycle time or yield of
  its own; a sub-step that needs those is a separate step. Measured values do not go in the text: they are checks.
  Example (food, example data): Mix — 1 "Add water and sugar, mix 2 min at low speed"; 2 "Add flour and fat, mix
  8 min at high speed"; 3 "Stop the mixer, scrape the bowl, mix 1 more minute" (must be confirmed).
- A **check** is a result the operator measures or judges (tablet weight, core temperature, seal test), unlike a
  parameter, which is a machine setting the step runs within. Each time it is due, the operator takes the set number
  of samples and each sample is compared with the range on its own (averages are PRODUCTION's / Quality's).
  Frequency: once per run, start-middle-end (three times per run), or every N time units while the step runs.
  Example (example data): Compress — "Tablet weight", numeric, 245–255 mg, target 250, 10 samples, every 15 min,
  critical.
- A class-based step has one instruction list and one set of checks for all its stations.

*Step order*
- A routing version with no "Waits for" anywhere is a straight line in step order. With any, the order comes from
  them only. Example: Glaze and Print both wait for Bake; Pack waits for both.

*Edge cases*
- No row in effect on that date → Performance is "cannot be computed" with the reason "no time
  standard available for that date", not 0%.
- No conversion path between the counter's unit and the step's item unit on the business date (row missing, or
  closed by then) → Performance for that segment shows "cannot be calculated", with the reason naming the two
  units and the product. Never 0%, never the unconverted count.
- Continuous work units (standard speed) are not affected by the item-unit rule.
- A standard cycle time of 0 or less (or a standard speed of 0 or less on continuous) is rejected.
- Planned shrinkage empty → 0; 1 (100%) or more, or below 0, is rejected (nothing would come out).
- No standard weight on the product → no derived weight is shown for the steps (never a guessed number).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `PRODUCT` (`code`, `base_uom_id`, `product_type`), `PRODUCT_DETAIL` (`standard_weight`, `weight_uom_id`) for the derived weight, `PRODUCT_UOM_CONVERSION` (`product_id`, `from_uom_id`, `to_uom_id`, `conversion_value`, `valid_from`, `valid_to`) for the item-unit picker and the save rule, `PRODUCT_FLOW`, `WORK_CENTER`, `WORK_UNIT`, `WORK_UNIT_CLASS`, `EQUIPMENT_FLOW` (to pre-fill steps), `UNIT_OF_MEASUREMENTS`, `TIME_CONVERSIONS` |
| **Writes** | `ROUTING` (`product_id`, `work_center_id`, `flow_id`, `version`, `status`, `is_primary`, `enforce_sequence`, `valid_from`, `valid_to`) per [F-08 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#91-routing--a-product-can-run-at-this-work-center) · `OPERATION` (`routing_id`, `step_number`, `step_name`, `step_type`, `work_unit_id`, `required_work_unit_class_id`, `cycle_time_value`, `uom_id`, `time_conversion_id`, `batch_size`, `standard_speed_value`, `speed_uom_id`, `standard_setup_time`, `setup_time_conversion_id`, `crew_size`, `planned_shrinkage`, `yield_min_pct`, `yield_max_pct`, `max_hold_time`, `hold_time_conversion_id`, `valid_from`, `valid_to`) per [F-08 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#92-operation--one-step-of-a-routing-at-one-work-unit-with-its-standard) · `OPERATION_WORK_UNIT` (`operation_id`, `work_unit_id`, `cycle_time_value`, `valid_from`, `valid_to`) per [§9.2.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#922-operation_work_unit--which-stations-can-run-a-class-based-step-po-2026-09-30) · `OPERATION_INSTRUCTION` (`operation_id`, `sequence`, `instruction_text`, `requires_confirmation`, `valid_from`, `valid_to`) per [§9.2.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#925-operation_instruction--ordered-sub-steps-of-a-step-po-2026-10-01-ebr) · `OPERATION_CHECK` (`operation_id`, `sequence`, `check_name`, `value_type`, `target_value`, `min_value`, `max_value`, `uom_id`, `sample_count`, `frequency_type`, `frequency_interval_value`, `frequency_interval_conversion_id`, `is_critical`, `valid_from`, `valid_to`) per [§9.2.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#926-operation_check--in-process-checks-of-a-step-po-2026-10-01-ebr) · `OPERATION_DEPENDENCY` (`operation_id`, `predecessor_operation_id`) per [§9.2.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#923-operation_dependency--steps-that-run-side-by-side-po-2026-09-30). Discrete/batch use the cycle-time fields; continuous use the speed fields. **`uom_id` is the item unit** the standard is per (pack, carton, kg, punch), never a time unit; `time_conversion_id` is the only time unit for the cycle time (changed 2026-09-28, [F-08 §9.2.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#921-item-unit-and-conversion-po-2026-09-28), resolves W-6); `setup_time_conversion_id` is the setup time's unit |
| **Not real fields: don't add** | Not `sequence_no` / `standard_crew_size` (retired names). No `PROCESS` table (dropped 2026-10-01: `step_type` is free text, no system logic reads it). No wet/dry marker on a work unit (derived). No stored weight per step (derived). No knife / tooling (not modelled, PO 2026-10-01). No `routing_version_locked`: the work order pins by `routing_id`. `OPERATION_PARAMETER` (parameter standards) is not written by this story. Nothing about execution is stored here: no instruction confirmation, no check result, no actual yield, no hold timer. No per-step sign-off, second-person verification or line-clearance field (out of scope). No frequency by quantity (W-13), no minimum wait or per-successor hold time (W-11), no cross-routing hold (W-10) |

**6. Rules & constraints**

1. The standard cycle time is **required** on a discrete/batch step and **time-variant**: a change closes the old
   row and creates a new one.
2. Historical calculation must use the row in effect on that business date, not the latest one and not the value
   at work-order release.
3. Step numbers are unique within a routing version, pre-filled from the work center's flow order.
4. Every step names one active work unit **or** a machine class, never both. A class step's stations must be of
   that class and in the routing's work center; the user can remove one.
5. **Versions** ([F-08 versions and status](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#versions-and-status-po-2026-09-30)):
   adding or removing a step, or changing a step's station or class, is structural — allowed only on a `draft`; on
   a `released` version it opens the next version as a draft copy. Since 2026-10-01 adding, removing or re-ordering
   an instruction, and adding or removing a check, are structural too. Standard edits (cycle time, speed, setup time,
   shrinkage, a station override; yield range, hold time, a check's values, an instruction's text or confirmation
   flag) are effective-dated rows inside the same version, and a job reads them as of the shift's business date. "Release" retires the
   previous released version from the release date. A routing locked onto a released work order is never edited
   in place; the work order keeps its `routing_id`.
6. The dated rows behind one operation step must neither overlap nor leave gaps. A gap means dates with no
   standard.
7. At most one `released` version of a flow's routing per work center is active on a date, and at most one
   primary routing per flow and work center on a date.
8. **Who may do it:** changing a routing needs **work-center access** to the work center it runs on; changing
   one step needs **work-unit access** to that step's work unit
   ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#work-master-wms--08-work-mastermd-9)).
   Area, site, enterprise or tenant access also qualifies.
9. **Item unit (2026-09-28):** on a discrete/batch step the item unit is required and is never a time unit; the
   time unit is required separately.
10. **Save rule (2026-09-28):** if the step's item unit is not the product's base unit, a conversion path between
    the two must exist, in force on the step's valid-from date. Otherwise the save is blocked with the message in
    the *Save blocked* state above ([F-08 §9.2.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#921-item-unit-and-conversion-po-2026-09-28)).
11. **Typed rate saved as typed (2026-09-28):** a rate keeps the time unit the user picked; no stored value is
    normalised to seconds. Stored value keeps up to 6 decimal places, rounded half-up.
12. **Step name** is required (pre-filled from the work unit's name). **Step type** is optional free text.
    **Setup time** and **crew size** are optional (W-7 closed 2026-09-30); when a setup time is set, its time unit
    is required.
13. **Planned shrinkage** is optional, from 0 to less than 1 (entered as %), per step and work unit.
14. **Waits for:** a step cannot wait for itself, only for steps of the same routing version, and loops are
    refused on save (`wms-003.error.loop`).
15. **Flow changed later** (a machine added to the work center's flow): existing routings do not change by
    themselves; the product list shows `wms-003.badge.flow-changed`, and saving the new step opens a new routing
    version. Unticking a step for this product leaves the flow as it is for other products.
16. **Steps must be done in order** is only declared here; blocking out-of-order steps at execution is
    PRODUCTION's rule.
17. **Instructions** ([F-08 §9.2.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#925-operation_instruction--ordered-sub-steps-of-a-step-po-2026-10-01-ebr)): `sequence` 1, 2, 3… unique per step on any date; `instruction_text`
    required (maximum length, attachments and translation `TBD`, W-14); `requires_confirmation` defaults to false.
    What "confirm" records, and whether it blocks the next instruction, is PRODUCTION's call.
18. **Checks** ([F-08 §9.2.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#926-operation_check--in-process-checks-of-a-step-po-2026-10-01-ebr)): `check_name` required and unique per step on any date. `value_type` is
    `numeric` / `pass_fail` / `text`. **Numeric:** at least one of min / max required; min ≤ target ≤ max where set;
    unit required. **Pass/fail:** Fail is out of range. **Text:** a free observation, never out of range, and never
    critical. Range and unit stay empty for pass/fail and text. `sample_count` defaults to 1. `frequency_type`
    defaults to `once`; `interval` needs an interval value above 0 and its time unit. Whether `once` means per run
    or per lot when a run holds several lots is `TBD` (W-13, PRODUCTION). `is_critical` = an out-of-range result is
    a deviation, not just a warning.
19. **Yield range** ([F-08 §9.2.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/08-work-master.md#927-yield-limits-and-hold-time-po-2026-10-01-ebr)): optional; both empty = no yield check; either may be set alone.
    `0 ≤ yield_min_pct ≤ yield_max_pct`; `yield_max_pct` may be above 100 (a step can gain mass). A range that does
    not contain the expected yield gives a warning on save, never a block.
20. **Hold time:** optional; `max_hold_time` above 0; its time unit (`hold_time_conversion_id`) is required when it
    is set.

**7. Acceptance criteria**

- **AC-1** **Given** an item has a routing with 2 operations, **when** the plant admin changes operation 1's
  cycle time from 20 to 18 seconds effective 2026-07-01, **then** the old row closes on 2026-06-30 and the new
  row takes effect from 2026-07-01, both visible in history, in the same routing version.
- **AC-2 (history)** **Given** the change in AC-1 is saved, **when** OEE for 2026-05-12 is
  recomputed, **then** Performance still uses 20 seconds and the result is identical to before the change.
- **AC-3 (validation)** **Given** the standard cycle time is entered as 0, **when** saved, **then** it is rejected
  with "Standard cycle time must be greater than 0" (in the chosen time unit from the row).
- **AC-4 (validity gap)** **Given** the old row ends 2026-06-30 and the new one starts 2026-07-05, **when**
  saved, **then** it is rejected, naming the range with no standard (2026-07-01–2026-07-04).
- **AC-5 (cannot compute)** **Given** a job on a date with no standard row in effect, **when** OEE is opened,
  **then** Performance shows "cannot be computed" with the reason that no time standard is available, not 0%.
- **AC-6 (permission)** **Given** a user without access to the routing's work center (and without
  area/site/enterprise/tenant access), **when** they open the routing, **then** they can only read.
- **AC-7 (entitlement)** **Given** a tenant without a Production license, **when** any user opens a product,
  **then** there is no Routing tab, no Routing column and no "Only products without a routing" filter, and a
  direct link to the Routing tab opens the Measurement tab, independent of that user's own ABAC scope (AC-6).
- **AC-8 (item unit, save blocked)** **Given** product `FG-1001` with base unit pack and no carton conversion,
  **when** the plant admin saves a step of 216 seconds / carton valid from 2026-07-01, **then** the save is
  blocked with "Cannot save: there is no unit conversion between carton and pack for product FG-1001 on
  2026-07-01. Add it on the product's Conversions tab, then save again." and the input stays intact.
- **AC-9 (item unit, picker)** **Given** base unit pack and a conversion 1 carton = 12 pack in force, **when** the
  plant admin opens the item-unit picker, **then** it lists pack and carton only, and the saved step reads
  "216 seconds / carton".
- **AC-10 (conversion in Performance)** **Given** step 20 at WU_02 is 216 s / carton, the conversion 1 carton =
  12 pack is in force, and the WU_02 counter (counts units, batch size 1) reports 1,200 pack, **when**
  Performance is computed, **then** the ideal operating time is 21,600 s.
- **AC-11 (no conversion at run time)** **Given** the conversion row was closed before the business date,
  **when** Performance is computed for that segment, **then** it shows "cannot be calculated" naming carton,
  pack and the product, and not 0%.
- **AC-12 (tab, no sidebar item)** **Given** a tenant with a Production license, **when** a user opens a product,
  **then** the Routing tab sits next to BOM, and the Production sidebar group has no "Routing" item. *(Replaces
  the 2026-09-28 sidebar AC.)*
- **AC-13 (rate saved as typed)** **Given** a discrete step with batch size 1, **when** the plant admin enters
  "500 per hour" and saves, **then** the row stores `cycle_time_value` 0.002 with time unit hour (not 7.2 with
  time unit second), and the KPI uses 7.2 seconds per item.
- **AC-14 (list, gaps shown)** **Given** `FG-1001` whose routing has an operation with no standard cycle time,
  and `FG-1002` with no routing, **when** the user opens the product list, **then** `FG-1001` shows "Cycle time
  missing" and `FG-1002` shows "No routing yet" in the Routing column. Clicking `FG-1001` and its Routing tab opens
  its routing; its BOM is the neighbouring tab. *(Replaces the 2026-09-28 Routing list page AC.)*
- **AC-15 (precision)** **Given** a discrete step with batch size 1, **when** the planner types "700 per hour",
  **then** the stored value is 0.001429 with time unit hour (6 decimals, rounded half-up).
- **AC-16 (structural edit on a released version)** **Given** routing version 1 of `FG-1001` at Packing is
  `released`, **when** the plant admin adds a step, **then** the screen asks "Start version 2 as a draft?" and, on
  yes, version 2 opens as a draft copy with the new step while version 1 stays released. Pressing "Release" on
  version 2 retires version 1 from the release date.
- **AC-17 (back-dated standard)** **Given** a cycle-time fix back-dated to a date covered by 12 closed shifts,
  **when** the plant admin saves, **then** "This changes KPIs of 12 past shifts." is shown first, and after saving
  those shifts are recalculated and the change appears in the audit log.
- **AC-18 (machine class, override)** **Given** step "Mix" with class Mixer and default 600 s / batch, and Mixer 2's
  override 540 s, **when** Performance is computed, **then** a batch on Mixer 1 uses 600 s and a batch on Mixer 2
  uses 540 s.
- **AC-19 (pre-filled from flow)** **Given** work center Baking with flow Mold → Oven → Cooling, **when** the plant
  admin adds Baked biscuit to Baking, **then** three steps are listed in that order with "Used by this product"
  ticked; unticking Cooling removes it from this product's routing only.
- **AC-20 (shrinkage range)** **Given** planned shrinkage entered as 100%, **when** saved, **then** it is refused
  with "Planned shrinkage must be from 0 to less than 100%."; 4% is stored as 0.04.
- **AC-21 (loop)** **Given** step B waits for A, **when** the plant admin sets A to wait for B, **then** the save is
  refused with "These steps wait for each other in a loop: A, B."
- **AC-22 (derived weight)** **Given** Baked biscuit with standard weight 0.96 g and Oven shrinkage 0.04, **when**
  the Baking routing is shown, **then** Mold reads "1.00 g per piece (wet)" and Oven and Cooling read
  "0.96 g per piece (dry)".
- **AC-23 (flow changed)** **Given** a machine is added to Baking's flow after the routing was released, **when**
  the product list is opened, **then** the routing shows "Flow changed — review routing" and its steps are
  unchanged until a new version is saved.
- **AC-24 (yield example)** **Given** the mix step on Mixer 1 with shrinkage 0.05 and yield range 93–97%, A dispensed
  73.5 kg and B 31.5 kg at a 700/300 recipe per 1,000 kg, and 100 kg of mix out, **when** yield is worked out,
  **then** theoretical output is 105 kg and yield is 95.2%, inside the range.
- **AC-25 (yield warning)** **Given** a step with shrinkage 0.05, **when** the implementor saves a yield range of
  96–99%, **then** the warning "Expected yield 95% is outside 96–99%" is shown and the range can still be saved.
- **AC-26 (yield validation)** **Given** a yield min of 98 and max of 95, **when** saved, **then** it is refused with
  "Min yield must be 0 or more and not above max." and the input is kept.
- **AC-27 (hold time, parallel steps)** **Given** Glaze and Print both wait for Bake and Bake has a hold time of 1 hour,
  **when** the routing is read, **then** the 1-hour limit applies to Bake → Glaze and to Bake → Print separately.
  **Given** the last step of a routing has a hold time, **then** it applies to no step.
- **AC-28 (hold time, unit required)** **Given** a hold time of 2 with no time unit, **when** saved, **then** it is
  refused with "Hold time must be more than 0, with a time unit."; 2 hours is saved.
- **AC-29 (instructions)** **Given** step Mix with 3 instructions, the third marked "must be confirmed", **when** the
  routing version is released, **then** the instructions are stored in order 1–3 with that flag. **When** the text of
  instruction 2 is corrected, **then** it is a dated change in the same version; **when** an instruction is added or
  the order changed on a released version, **then** the next version opens as a draft copy.
- **AC-30 (checks)** **Given** a Number check "Tablet weight" with no min and no max, **when** saved, **then** it is
  refused with "A number check needs a min, a max, or both.". **Given** a Note check marked critical, **then** it is
  refused with "A note can't be critical." **Given** an `interval` check every 15
  min with 10 samples and range 245–255 mg, **then** it is saved.

**8. Metrics & events**

- **Metric:** `akurasi_standar_waktu` (*time standard accuracy*): the median difference between planned
  `beban_job` and actual time ([P-03 §9](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#job-order)).
  A wide gap means the standard is stale. Baseline `not yet measured`; the target is set after 30 days of
  pilot-line data. Source: `WORK_ORDER_OPERATION.actual_start/actual_end` vs `beban_job`.
- **Counter-metric:** % of shifts with `Performance > 100%` (a sign of stale standards). Must not increase.
- **Events:** `cycle_time_version_created` (`operation_id`, `old_value`, `new_value`, `valid_from`).
  Step detail (2026-10-01): `operation_step_detail_saved` (`operation_id`, `part` = instruction / check / yield /
  hold, `change` = structural / standard). Execution events (confirmations, check results, yield, hold breaches)
  are PRODUCTION's.

**Dependencies:** `US-FND-PRO-001`, `US-FND-PRO-003`, `US-FND-SIT-001`

---

#### US-FND-WMS-006

**Map machine product codes to a product**

**Status:** 🟡 Partly blocked — mapping, ending and flags are ready. The "Seen but not mapped" list waits on UX-WMS-4 (no record of unmapped codes exists yet); the two-flow case waits on UX-WMS-5.

> **In short:** on the product's **Routing** tab, the implementor lists the raw codes each machine's PLC sends when
> this product runs. Performance then knows which product, and so which cycle-time standard, is running when no job
> is dispatched. Each machine has its own codes; one product may have several.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to record which raw codes each machine sends for a product, so
Performance uses the right standard when a line runs from the PLC alone.

**2. Context**

- **Why:** when no job is dispatched, a work unit's Performance is split by the `product_code` tag's value. A raw
  value like `12` means nothing until it is mapped to a product; an unmapped run has no standard and is excluded
  from Performance
  ([F-07.1 segment boundaries](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/07-1-kpi-tag-mapping.md#more-than-one-sku-in-the-same-period)).
- **Per machine, several per product (PO, 2026-10-01):** codes are local to each asset. The same `12` can mean
  `FG-1001` on `PCK01` and `FG-2002` on `PCK02`. On one machine a code means one product at a time, but one product
  may have several codes (`12` and `112`, two HMI recipes) and different codes on other machines
  ([F-04 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#97-asset_tags--per-asset-tag--signal-declaration)).
- **Where it sits (2026-09-30, issue #58 part B, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-work-master-wave-1.md)):**
  a section of the product's Routing tab (`SCR-FND-WMS-007`), grouped by work unit. The data stays owned by Equipment
  Taxonomy (`ASSET_PRODUCT_CODES`); the old asset-side screen `SCR-FND-AST-002` is retired.
- **Two screens, two steps:** the tag itself (role `product_code`) is declared on Data Source › Tags
  ([`US-FND-DSR-001`](13-data-source.md#us-fnd-dsr-001)); this story maps its values.
- **Timestamps, not dates:** a code can change mid-shift (changeover), so start and end are date and time.
- **Who and where:** the implementor, on desktop, during line setup and whenever a PLC program changes.

**3. Expectation**

Screen text is copied word for word from [UX 08 § Product codes section](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/08-work-master.md#product-codes-section--full-screen-spec-scr-fnd-wms-007-2026-10-01),
with its key. If they differ, UX 08 is right. `(draft)` = proposed wording, usable in a build, may still change.

*Layout*
- A section "Product codes" (draft) (`wms-007.title`) at the bottom of the Routing tab, below the shown routing's steps.
- **One group per work unit** used by the routing's steps: a step's own work unit, or every station listed under a
  machine-class step. Group header: work unit name and code.
- **Inside a group, one block per `product_code` tag** on the machines placed there (primary and secondary; on a
  multi-lane machine only the tag whose work unit is this one). Block header "\<asset tag\> · \<tag name\>" (draft)
  (`wms-007.block.header`).
- A work unit with **no** `product_code` tag shows "No product code tag on this work unit — the job decides the
  product here. Declare a tag" (draft) (`wms-007.no-tag`), the last words linking to the Tags page (`SCR-FND-DSR-002`).
- **Rows:** "Code" · "From" · "To" · "Status" (draft) (`wms-007.columns`); status "Active" · "Scheduled" · "Ended"
  (draft) (`wms-007.status`). Times in the site's timezone, with the zone. Ended rows are hidden until "Show ended
  codes" (draft) (`wms-007.show-ended`) is on. A block may hold several active rows for this product.
- **Add:** "Add code" (draft) (`wms-007.add`) per block opens "Add product code for \<product code\>" (draft)
  (`wms-007.dialog.title`) with "Code sent by the machine" · hint "Exactly as the PLC sends it. 12 and 012 are
  different codes." (draft) (`wms-007.field.raw`) and "From" · "To (optional)" (draft) (`wms-007.field.from` ·
  `.to`). From defaults to now.
- **End:** per active or scheduled row, "End" · "End this code at" (draft) (`wms-007.end`), default now. "Delete"
  (draft) (`wms-007.delete-scheduled`) only on a row whose start is still in the future. No edit in place: a change of
  product or code is an end plus a new row.
- **Flags:** "Code with no step at this work unit" (draft) (`wms-007.flag.no-step`) and "Step with no code" (draft)
  (`wms-007.flag.no-code`); rules in Calculation.
- **"Seen but not mapped"** (draft) (`wms-007.unmapped.title`) under each group: "Code" · "Tag" · "First seen" ·
  "Last seen" (draft) (`wms-007.unmapped.columns`); per row "Map to this product" (draft) (`wms-007.unmapped.map`),
  which opens Add with the code and tag filled in and From = first seen. **Waits on UX-WMS-4.**
- **Readable within 3 seconds:** which work units still have a flag.

| State | What the user sees |
|-------|--------------------|
| Default | Groups, blocks and rows as above |
| Empty block | "No product codes yet for this work unit" + "Add code" |
| No code tag | `wms-007.no-tag` with the Tags page link; never flagged |
| Unmapped list empty | Nothing shown |
| Loading | Placeholder rows, no text |
| Save blocked | Dialog stays open, input kept. Overlap: "Code \<code\> on \<asset tag\> already means \<other product\> from \<from\>. End it there first." (draft) (`wms-007.error.overlap`). Missing code or start: "Enter the code and the start time." (draft) (`wms-007.error.required`). End not after start: "The end must be after the start." (draft) (`wms-007.error.order`) |
| Past mapping | Before save: "This changes KPIs of \<n\> past shifts." (draft) (`wms-007.warn.backdated`) |
| Offline | Not editable; the last loaded codes stay readable |
| Success | The row appears with its status; the flags update |
| No permission | Read-only: no "Add code", "End", "Delete" or "Map to this product" |
| No entitlement | Hidden with the Routing tab (tenant without Production), as `US-FND-WMS-003` AC-7 |

**4. Calculation**

*Status of a row* (shown in the site's timezone, stored in UTC)
- Active: started and not ended. Scheduled: starts later. Ended: its end has passed.
- Example: now 2026-10-01 10:00 WIB. `12` from 07:00 → Active. `0012` from 2026-11-01 00:00 → Scheduled. `7` ended
  2026-09-30 22:00 → Ended.

> [!note]- Exact formula (for developers)
> ```
> Active    = valid_from ≤ now AND (valid_to IS NULL OR valid_to > now)
> Scheduled = valid_from > now
> Ended     = valid_to ≤ now
> ```

*Which product a reading means* (used by Performance, F-07.1)
- A reading of a `product_code` tag with value v at time t means the product of the row on that tag's **asset** with
  raw value v active at t. No row → unmapped: the segment has no standard, Performance excludes it and flags the
  total partial.
- Example: `PCK01` sends `112` at 08:15; row `112 → FG-1001` is active since 07:00 → `FG-1001`.

> [!note]- Exact formula (for developers)
> ```
> product(tag T, value v, time t) = ASSET_PRODUCT_CODES.product_id
>   WHERE (tag_id → ASSET_TAGS.asset_id) = asset of T AND raw_value = v
>     AND valid_from ≤ t AND (valid_to IS NULL OR valid_to > t)
> at most one row matches (rule 1)
> ```

*Flags*
- **"Code with no step at this work unit":** an active code maps this product on a work unit where none of the
  product's **released** routings has a step (directly, or as a station of a class step) on today's date.
- **"Step with no code":** a step of a released routing sits on a work unit that **has** a `product_code` tag, and
  that tag's asset has no active code for this product. Never shown for a work unit without a code tag.
- Example: the Packing routing has steps on WU_01 (`PCK01` has a code tag, `12` active) and WU_02 (`CRT01` has a code
  tag, no code for this product) → WU_02 shows "Step with no code". WU_03 has no code tag → no flag.

*Mapping into the past*
- A code whose start is in the past re-attributes that period's readings to this product and recalculates the
  affected shifts, closed ones included — the same rule as a back-dated standard ([`US-FND-WMS-003`](#us-fnd-wms-003)
  AC-17). The warning counts those shifts first.
- Example: map `77` on `PCK01` from 2026-09-30 22:10; the 2026-09-30 night shift and the 2026-10-01 morning shift hold
  `77` readings → "This changes KPIs of 2 past shifts."

> [!note]- Exact formula (for developers)
> ```
> n = count of SHIFT_INSTANCE at the asset's work unit(s) overlapping [valid_from, min(valid_to, now))
>     that hold at least one reading of this tag with raw_value = v
> ```

*Edge cases*
- A raw value is compared exactly as stored: `12` ≠ `012` ≠ `12 ` (no trimming, no number parsing).
- A machine moved to another work unit keeps its codes: codes follow the asset, not the work unit.
- Two flows of this product with routings at the same work center: which routing's step a code resolves to is
  `TBD — needs PO confirmation` (UX-WMS-5). Until answered, use the primary flow.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `ASSET_TAGS` (`tag_id`, `asset_id`, `tag_name`, `tag_role = product_code`, `work_unit_id`), `ASSET` (`asset_tag`), `ASSET_PLACEMENT` (active, primary and secondary), `ROUTING` (`status`, `flow_id`, `work_center_id`), `OPERATION` (`work_unit_id`, `required_work_unit_class_id`), `OPERATION_WORK_UNIT`, `WORK_UNIT`, `SITE.timezone`, `PRODUCT` (`code`), `SHIFT_INSTANCE` (for the warning count) |
| **Writes** | `ASSET_PRODUCT_CODES` (`tag_id`, `raw_value`, `product_id`, `valid_from`, `valid_to` — timestamps with time zone) per [F-04 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#97-asset_tags--per-asset-tag--signal-declaration); `AUDIT_LOG` per change (F-14) |
| **Not decided** | The record behind "Seen but not mapped" (raw value, tag, first seen, last seen) has no entity and no owner yet (UX-WMS-4). Do not create one in this story |
| **Not real fields: don't add** | No product-code field on `PRODUCT` or `OPERATION`. No format, length or number type on `raw_value`. No uniqueness on `tag_id` alone |

**6. Rules & constraints**

1. **Overlap (A-6b):** on one **asset**, across all its `product_code` tags, a raw value maps to at most one product
   at any time. Refused with `wms-007.error.overlap`, naming the other product. The check is on `(asset_id,
   raw_value)`, not on `tag_id`.
2. One product may have several active codes on the same asset, and different codes on different assets.
3. Only tags with `tag_role = product_code` can carry codes.
4. Raw value and start are required; the end, when set, must be after the start.
5. Rows are never overwritten: a change is an end plus a new row. A row whose start is still in the future may be
   deleted.
6. A start in the past recalculates the affected shifts after the warning; every change is written to `AUDIT_LOG`.
7. **Who may do it:** creating, ending or deleting a code needs **work-unit access** to the work unit the tag's asset
   is placed on (for a multi-lane machine, the tag's own work unit) — `foundation:ast:work_unit:update`
   ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md));
   work-center, area, site, enterprise or tenant access also qualifies. Viewing needs any access that covers it.
8. Offline: not editable.

**7. Acceptance criteria**

- **AC-1 (add)** **Given** `PCK01` on WU_01 has tag `Product_Code`, **when** the implementor adds code `12` for
  `FG-1001` from now, **then** the row shows Active and WU_01's "Step with no code" flag disappears.
- **AC-2 (several codes per product)** **Given** `12 → FG-1001` is active on `PCK01`, **when** `112 → FG-1001` is
  added, **then** both are active and a reading of either resolves to `FG-1001`.
- **AC-3 (same code on another machine)** **Given** `12 → FG-1001` on `PCK01`, **when** `12 → FG-2002` is added on
  `PCK02`, **then** it is saved.
- **AC-4 (overlap)** **Given** `12 → FG-2002` is active on `PCK01`, **when** `12 → FG-1001` is added on `PCK01` from
  now, **then** it is refused with "Code 12 on PCK01 already means FG-2002 from \<from\>. End it there first." This
  also holds when the two rows sit on two different `product_code` tags of `PCK01`.
- **AC-5 (exact value)** **Given** `12` is mapped, **when** the PLC sends `012`, **then** it does not resolve to that
  product.
- **AC-6 (end, no edit)** **Given** `12` is active, **when** the implementor ends it at 14:00 and adds `0012` from
  14:00, **then** readings before 14:00 resolve through `12`, readings after through `0012`, and both rows stay in
  history.
- **AC-7 (past mapping)** **Given** 2 shifts hold `77` readings since 2026-09-30 22:10, **when** `77` is mapped from that
  time, **then** "This changes KPIs of 2 past shifts." is shown first, and after saving both shifts are recalculated
  and the change is in the audit log.
- **AC-8 (no code tag)** **Given** WU_03 has no `product_code` tag, **when** the section is shown, **then** WU_03 reads
  `wms-007.no-tag` with a link to the Tags page and shows no flag.
- **AC-9 (code with no step)** **Given** `12 → FG-1001` is active on a work unit where no released routing of `FG-1001`
  has a step, **when** the section is shown, **then** that row shows "Code with no step at this work unit".
- **AC-10 (permission)** **Given** a user without access to WU_01, **when** they open the section, **then** WU_01's
  codes are read-only with no actions.
- **AC-11 (seen but not mapped — waits on UX-WMS-4)** **Given** `PCK01` sent `77` with no active mapping, **when** the
  section is shown, **then** `77` is listed with first and last seen, and "Map to this product" opens Add prefilled.
  **Not testable until UX-WMS-4 is answered.**

**8. Metrics & events**

- **Metric:** `kode_produk_lengkap` (*product codes complete*): the share of pilot-line steps on work units with a
  `product_code` tag that have an active code for their product. Target 100% before PLC-only Performance goes live.
  Baseline 0 (no codes mapped). Source: `ASSET_PRODUCT_CODES` against released `OPERATION` rows.
- **Counter-metric:** minutes of Performance excluded as "unmapped" per shift on the pilot line; must trend to 0.
- **Events:** `product_code_added` (`asset_id`, `tag_id`, `product_id`, `backdated`, `shifts_recalculated`) ·
  `product_code_ended` (`asset_id`, `tag_id`, `product_id`) · `unmapped_code_mapped` (`asset_id`, `raw_value`).

**Dependencies:** `US-FND-WMS-003`, `US-FND-DSR-001`, `US-FND-PRO-001`

---

> **`US-FND-WMS-004` is retired** — it is now [`US-PROD-JOB-005`](../PRD-001-molcadx-core-q3-en.md#us-prod-job-005--create--release-a-work-order-with-locked-versions) in PRD-001, because `WORK_ORDER` and `WORK_ORDER_OPERATION` are PRODUCTION entities.
> The ID `US-FND-WMS-004` is never reused.

## Change notes (history — not needed to build)

- `US-FND-WMS-006` · 2026-10-01 — **new story, PO request.** Product codes section (`SCR-FND-WMS-007`) from the UX 08 full screen spec and F-04 §9.7 (codes per asset; several per product; overlap on `(asset_id, raw_value)`). 🟡: "Seen but not mapped" waits on UX-WMS-4, two flows on UX-WMS-5.
- `US-FND-WMS-002`, `US-FND-WMS-003` · 2026-10-01 — prd-sync `docs-molcadx` c932974..e3ba785 (UX 08 step detail text): screen text and keys copied word for word into Expectation, states and ACs; both 🟡 → 🟢.
- `US-FND-WMS-002`, `US-FND-WMS-003` · 2026-10-01 — F-08 EBR step detail synced (docs-molcadx changelog backlog of 2026-10-01): `WMS-002` dispensing tolerance (context, formula with example, rule 10, AC-16–AC-19); `WMS-003` instructions, in-process checks, yield range and hold time (context, formulas with examples, edge cases, rules 17–20, AC-24–AC-30, event). Screen text `TBD` until UX 08 is designed. Both 🟢 → 🟡. Open items W-10–W-17 added; the "EBR step detail without a story" row replaced.
- Module open items · 2026-10-01 — prd-sync `docs-molcadx` 787e819..725362a: F-08 EBR step detail (instructions, in-process checks, yield range, hold time, dispensing tolerance) recorded as an open item; no story changed (rule 1, no UX yet).
- `US-FND-WMS-002`, `US-FND-WMS-003`, `US-FND-WMS-005` — 2026-09-12: the "No entitlement" (license gate) state and the entitlement ACs (`WMS-002` AC-7, `WMS-003` AC-7, `WMS-005` AC-6) were added.
- `US-FND-WMS-004` — 2026-09-03 (PO decision): renamed to `US-PROD-JOB-005` and moved out of this FOUNDATION PRD. Retired per `AGENTS.md` §4 dependency rule 3. The full story now lives only in PRD-001 §6.14 Job Schedule and is not duplicated here. See [`LOGS/decisions/2026-09-03-foundation-contest-blockers.md`](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-03-foundation-contest-blockers.md).
- `US-FND-WMS-005` — 2026-09-03: an earlier draft assumed `QUALITY_DEFECT_CODE` carries a disposition, a parent code, and a product restriction. None exist in F-08 §9.7; the story was rewritten to what can be built today, with that behaviour marked `TBD`.
- All stories (2026-09-23): detail blocks rewritten from one dense table into numbered sections, and
  "subject whose IDP-asserted scope claim covers …" replaced by *tenant access* / *work-center access* /
  *work-unit access* (defined in the README glossary). Meaning unchanged; UI copy kept verbatim.
- All stories (2026-09-23, second pass): rewritten in plain language. Entity and field names now appear only in
  section 5, the collapsible "Exact formula (for developers)" boxes and schema-level `TBD` callouts for
  `foundation-domain`; everywhere else things are named in plain words (component list, standard cycle time,
  scrap factor, defect code). Meaning unchanged; UI copy, story IDs and event names kept verbatim.
- 2026-09-23 (rule 1b, full UX cross-check): every story in this file now names the UX screen it is built on ; WMS-002/003 gain UX 08's version behaviour (explicit new version closes the old; confirm before closing a cycle-time row).
- `US-FND-WMS-005` (2026-09-23, PO decision): the defect code list is now reached from the Reference data sidebar; data and license gate unchanged.
- `US-FND-WMS-005` (2026-09-23, PO decision): **closed** — defect codes merged into `REJECT_REASON`; the story moved to [90-closed-stories.md](90-closed-stories.md) and its live content lives in `US-FND-REF-002`.
- `US-FND-WMS-003` (2026-09-28, PO, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) B; prd-sync docs-molcadx 7fb3a9a..1285317): `OPERATION.uom_id` is
  now the **item unit** the standard is per, not a time unit (resolves F-08 W-6). Added: three-field cycle-time
  entry shown as "20 seconds / pack", item-unit picker limited to convertible units, the save rule with its exact
  error text (F-08 §9.2.1 / UX 08), the counter → item-unit conversion step with the 1,200 pack → 21,600 s worked
  example, the run-time "cannot be calculated" edge case, AC-8 … AC-12. New dependency `US-FND-PRO-003`
  (conversion rows). Sidebar placement under the Production group and its `nav-sidebar.*` text from UX 00.
- `US-FND-WMS-002` (2026-09-28, PO, decision A; same sync): reached from the Work Master item inside the
  Production sidebar group; the license gate shows in the sidebar as one locked group heading (PLT-01 concept
  3a); `nav-sidebar.*` text from UX 00; AC-8 added.
- `US-FND-WMS-002`, `US-FND-WMS-003` (2026-09-28, PO, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md) addenda; prd-sync docs-molcadx 1285317..513c4f9):
  Work Master flattened in the sidebar — BOM and Routing are separate pages (items "BOM" / "Routing" in the
  Production group, keys `nav-sidebar.item.bom` / `.routing`; `nav-sidebar.item.work-master` retired), no tabs,
  each with its own item picker, the last picked item carried over (picker text `TBD`). No "Master data" label
  over shared items. UX 08 link re-pointed to the renamed section. `WMS-002` AC-8 reworded, AC-9 added;
  `WMS-003` AC-12 reworded, AC-14 added.
- `US-FND-WMS-003` (2026-09-28, PO answer; same sync): a typed rate is **saved as typed** in the time unit picked
  (no seconds normalisation) — calculation block, rule 10, AC-13. `cycle_time_value` precision stays `TBD`
  (F-08's own `TBD`), added as an open item.
- `US-FND-WMS-002`, `US-FND-WMS-003` (2026-09-28, PO, prd-sync docs-molcadx 4cb81d6..dd0a64e): picker text
  declared (UX 08 `wms-picker.*`, "Product"), WMS-002 AC-10 added; `cycle_time_value` keeps up to 6 decimals,
  rounded half-up — WMS-003 precision `TBD` closed, rule 10 updated, AC-15 added. Open items closed.
- `US-FND-WMS-002`, `US-FND-WMS-003` (2026-09-28, PO, prd-sync docs-molcadx dd0a64e..0dea5c3): product picker
  retired — each page opens on a product list with gap badges (UX 08 `wms-list.*`, `wms-002.*`, `wms-003.*`),
  default filter semi-finished + finished goods, detail header links BOM ↔ Routing. Context + Expectation
  rewritten; WMS-002 AC-9/AC-10 and WMS-003 AC-14 replaced.
- `US-FND-WMS-002`, `US-FND-WMS-003` (2026-09-30 / 2026-10-01, PO, decisions [BOM / Routing as product tabs](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-bom-routing-as-product-tabs.md),
  [product type / phantom / tree](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-product-type-packaging-phantom-bom-tree.md),
  [net recipe / shrinkage / base qty](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-bom-net-recipe-shrinkage-base-qty.md),
  [line across work centers](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-line-across-work-centers-reject-weight.md),
  [wave 1](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-work-master-wave-1.md),
  [wave 2](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-30-work-master-wave-2-flows-parameters.md),
  [wrap-up](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-10-01-open-items-wrap-up.md);
  prd-sync docs-molcadx 147e8d2..787e819): BOM and Routing are **tabs on the Product detail page**, not Production
  sidebar pages; the list pages are gone and their gap badges are status columns on the product list (bought
  products show "—"); without a Production license the tabs, columns and filters are **hidden** (was visible but
  locked). Retired text dropped: `wms-002/003.list.columns`, `wms-list.empty`, `wms-list.link.other`,
  `nav-sidebar.item.bom/.routing`. **W-8 resolved** (version + status, pin by `bom_id` / `routing_id`): both
  stories 🟡 → 🟢. **W-7 resolved** (setup time / crew optional, setup time unit).
  WMS-002: BOM tab is a tree (UX 08 `wms-002.tree.*`, `.base-qty.*`, `.line.*`, phantom error); recipe base quantity,
  net recipe and the planned-input formula with the 75.16 kg example; `is_phantom`, `flow_id`, `version`, `status`;
  `BOM_LINE.operation_id` / `line_use` / `is_primary_output`; rules 1–9 rewritten; AC-4 now testable, AC-7/8/9
  replaced, AC-10 retired, AC-11 … AC-15 added.
  WMS-003: version bar, step name / type / machine class / station override / waits for / shrinkage / "Used by
  this product" / derived unit weight (UX 08 `wms-003.*`); standard read as of the business date, back-dated
  warning; steps pre-filled from `EQUIPMENT_FLOW`; `ROUTING.flow_id` / `version` / `status` / `is_primary` /
  `enforce_sequence`, `OPERATION_WORK_UNIT`, `OPERATION_DEPENDENCY`; rules 3–5, 7, 12–16; AC-7/12/14 replaced,
  AC-16 … AC-23 added. Flow bar, parameter standards and product codes (`SCR-FND-WMS-007`) have no story — open item.
