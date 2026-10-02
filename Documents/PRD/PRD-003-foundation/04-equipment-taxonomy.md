# PRD-003 · Equipment Taxonomy (Asset Ontology) (`AST`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-04. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Terms like *site access*, *tenant access*, *work-unit access* and *implementor* are defined in the [README glossary](README.md#glossary).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| `A-10` — never-placed asset | `US-FND-AST-002` | How a never-placed asset is recorded as "physically at Site X". | Not named in PRD (A-10) |
| `asset_tag` uniqueness scope | `US-FND-AST-002` (rule 1, AC-2) | Which site the "unique within site" check runs against for a not-yet-placed asset. | PO / `foundation-domain` |
| `criticality` | `US-FND-AST-002` (rule 2, AC-2/AC-3) | Add `criticality` to `ASSET` §9.4 first, or drop rule 2 and AC-2/AC-3 until it is. | PO / `foundation-domain` |
| `owner_org_unit_id` target | `US-FND-AST-002` | What `owner_org_unit_id` points to now that `ORG_UNIT` no longer exists. | PO / `foundation-domain` |
| [`A-13`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#98-open-questions) — floor tag in machine addresses | `US-FND-AST-002` | The floor tag is a segment of generated OPC UA tag addresses and may only use letters, digits and `_`. Keep the floor tag as the segment (and refuse generated naming for a non-conforming tag such as `IM-003`), or add a separate segment code on the asset? | PO / Operations |
| [`A-14`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#98-open-questions) — renaming a code in a live address | `US-FND-AST-002` | Renaming a floor tag that is in a live generated address: warn with the count and let the user confirm (current spec), or block the rename? | PO |
| Place/swap and Edit asset address texts | `US-FND-AST-002`, `US-FND-AST-003` | The move-or-add-lane question, the other-site refusal and the two "addresses change" messages exist only as `(draft)` proposals in [UX 04 state notes](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/04-asset.md#state-notes); they get keys when UX 04's Screen text block reaches `SCR-FND-AST-001`/`007`. | PO |
| `UX-AST-2` — unplaced alert | `US-FND-AST-003` | Should an unplaced work unit alert like the Shift Instance Monitor's "no gaps" rule? | Not named in PRD ([`UX/04-asset.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/04-asset.md)) |
| A-17 | `US-FND-AST-001` | Keys (2026-10-01): is `code` unique on `ASSET_CLASS` and `ASSET_TYPE`, and in what scope? | `foundation-domain` / PO |
| A-18 | `US-FND-AST-002` | Keys (2026-10-01): `asset_tag` is unique within site, but the site comes from the placement. Which scope applies to an unplaced asset, and is it checked again on a move? | `foundation-domain` / PO |
| A-19 | `US-FND-AST-003` | Keys (2026-10-01): is `ASSET_PLACEMENT.valid_from`/`valid_to` a date or a timestamp? Readers resolve placement at the event timestamp | `foundation-domain` / PO |

## Stories


| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-AST-001`](#us-fnd-ast-001) | Manage the class & type taxonomy | 🟢 Ready | As an **implementor**, I want to build the asset class and type/model tree, so analysis can span similar assets rather than only individual units. → [detail](#us-fnd-ast-001) | `—` |
| [`US-FND-AST-002`](#us-fnd-ast-002) | Register physical assets with their tag | 🟡 Partly blocked | As an **implementor**, I want to register machines together with the tag stuck on their body, so operators refer to assets by the same code as on the floor. → [detail](#us-fnd-ast-002) | `US-FND-AST-001`, `US-FND-SIT-001` |
| [`US-FND-AST-003`](#us-fnd-ast-003) | Record asset moves as placement history | 🟢 Ready | As an **implementor**, I want to move assets between stations without overwriting the old placement, so last month's line performance doesn't change when a machine is swapped. → [detail](#us-fnd-ast-003) | `US-FND-AST-002` |

## Detail blocks

---

#### US-FND-AST-001

**Manage the asset class & asset type taxonomy**

**Status:** 🟢 Ready

> **In short:** the implementor builds the asset class and type tree (for example Production Machine →
> Injection Molding → IM 250T brand X). Downtime can then be summed per machine type, not just per machine.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to build the asset class and type/model tree, so
analysis can span similar assets rather than only individual units.

**2. Context**

- **Why:** without a taxonomy, downtime is recorded against free-text names and can't be summed per machine
  type, so Pareto analysis becomes impossible
  ([F-04](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#why-it-matters-what-goes-wrong-without-it)).
- **Shape:** three levels, **Class / Type / Instance**
  ([F-04 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#91-three-layers-class--type--instance)).
  A class may sit under another class, but F-04 does **not** define a hard depth-warning rule
  (there is no F-01 §7.1 depth warning).
- **Who and where:** the implementor, on desktop, during onboarding.

**3. Expectation**

*Layout*
- Classes and types are created and edited on two **Reference** screens, each with its own nav entry:
  the **Asset class manager** (`SCR-FND-REF-009`) and the **Asset type manager** (`SCR-FND-REF-010`)
  ([`UX/05-reference.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md)). These are the **only** places a
  class or type is created or edited.
- The class manager shows the class tree (classes can sit inside classes) in a Tree and a Table view. The
  type manager lists the types under each class, with their maker, model and shared attributes.
- Each class and type row shows how many machines use it.
- The asset screens only **show** classes and types. The Asset Registry's (`SCR-FND-AST-001`) class/type
  breadcrumb opens the read-only taxonomy view (`SCR-FND-AST-007`), and each class or type row there links on
  to its Reference manager for editing ([`UX/04-asset.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/04-asset.md)).
- Readable within 3 seconds: how many classes and how many types are defined.

*States*

| State | What the user sees |
|-------|--------------------|
| Empty | "No asset classes yet" plus a short note that Class → Type → Instance is the modelled shape, so the implementor doesn't invent extra entity layers |
| Loading | Table skeleton |
| Error | Input intact plus a save failure message. A class with machines can't be deleted: the message names them |
| Offline | Unavailable |
| Success | The new node is selected, and the form is ready for the next entry |
| No permission | "Only subjects whose IDP-asserted scope claim covers the tenant may change the asset taxonomy." |

*Screen text* — copied word for word from UX 05 ([`SCR-FND-REF-009`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#scr-fnd-ref-009--asset-class-manager),
[`SCR-FND-REF-010`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#scr-fnd-ref-010--asset-type-manager)). The UX doc is the source; if these tables and UX 05 differ, UX 05 wins.
`TBD — PO` = wording not decided yet (the build shows `[TBD copy: <key>]`, never its own words). The state
table above says what each state does; these tables say what it reads.

Asset class manager (`SCR-FND-REF-009`):

| Key | Where / state | Text |
|---|---|---|
| `ref-sidebar.item.asset-classes` | Sidebar item 9 → `SCR-FND-REF-009` | "Asset classes" |
| `ref-009.title` | Page title | TBD — PO (see UX-REF-2) |
| `ref-009.summary` | How many classes and how many types are defined | TBD — PO |
| `ref-009.view-switch` | Tree / Table view switch | TBD — PO (PRD names a Tree and a Table view, gives no labels) |
| `ref-009.table.columns` | Class row: code, name, parent class, number of assets using it | TBD — PO |
| `ref-009.action.add` | Add a class | TBD — PO |
| `ref-009.form.fields` | Form labels: code, name, parent class | TBD — PO |
| `ref-009.default` | Default state | N/A — no text: tree / table (rows above) |
| `ref-009.empty` | Empty state | "No asset classes yet" |
| `ref-009.empty.note` | Empty state, short note — must say Class → Type → Instance is the modelled shape | TBD — PO |
| `ref-009.loading` | Loading state | N/A — no text: "Table skeleton" |
| `ref-009.error.save` | Save error — input intact plus a save failure message (see UX-REF-3) | TBD — PO |
| `ref-009.offline` | Offline state — PRD: "Unavailable" (see UX-REF-3) | TBD — PO |
| `ref-009.success` | Success | N/A — no text: the new node is selected, and the form is ready for the next entry |
| `ref-009.no-permission` | No permission — read only | "Only subjects whose IDP-asserted scope claim covers the tenant may change the asset taxonomy." |
| `ref-009.error.duplicate-code` | Save, code already used — inline, names the existing holder | TBD — PO |
| `ref-009.error.has-active-types` | Deactivate a class that still has active types — refused, names the types | TBD — PO |
| `ref-009.error.has-assets` | A class with machines can't be deleted — names them | TBD — PO |
| `ref-009.hint.deep-nesting` | Optional soft hint on deep nesting; never blocks save (AC-3: "optional soft UX") | "trees deeper than 3 class levels — consider simplifying" |

Asset type manager (`SCR-FND-REF-010`) — the state texts above are one table for both managers; only the rows
that apply to types:

| Key | Where / state | Text |
|---|---|---|
| `ref-sidebar.item.asset-types` | Sidebar item 10 → `SCR-FND-REF-010` | "Asset types" |
| `ref-010.title` | Page title | TBD — PO (see UX-REF-2) |
| `ref-010.table.columns` | Type row, listed under its class: code, name, manufacturer, model, shared attributes, number of assets using it | TBD — PO |
| `ref-010.action.add` | Add a type | TBD — PO |
| `ref-010.form.fields` | Form labels: code, name, class, manufacturer, model, attributes (key-value) | TBD — PO |
| `ref-010.default` | Default state | N/A — no text: type list (rows above) |
| `ref-010.empty` | Empty state — no types yet (the PRD's "No asset classes yet" is the class list's) | TBD — PO |
| `ref-010.loading` | Loading state | N/A — no text: "Table skeleton" |
| `ref-010.error.save` | Save error — input intact plus a save failure message (see UX-REF-3) | TBD — PO |
| `ref-010.offline` | Offline state — PRD: "Unavailable" (see UX-REF-3) | TBD — PO |
| `ref-010.success` | Success | N/A — no text: the new node is selected, and the form is ready for the next entry |
| `ref-010.no-permission` | No permission — read only | "Only subjects whose IDP-asserted scope claim covers the tenant may change the asset taxonomy." |
| `ref-010.error.duplicate-code` | Save, code already used (e.g. `IM250X`) — inline, names the existing holder | TBD — PO |

**4. Calculation**

*How deep a class sits in the tree*
- Each class sits one level below its parent class.
- Example: Production Machine at level 2 (under the root class), Injection Molding under it at level 3.

> [!note]- Exact formula (for developers)
> ```
> class_depth = 1 + depth(parent_class)
> ```

- F-04 allows class nesting and sets **no** depth rejection or required warning threshold.
- Any "trees deeper than 3 class levels — consider simplifying" copy is a **soft UX hint only** (not a
  product rejection; not cited from F-04).

*How many machines use a type*
- The number of machines of that type whose record hasn't ended yet.
- Example: class `Production Machine` → sub-class `Injection Molding` → type `IM 250T brand X` → 4 machines.

> [!note]- Exact formula (for developers)
> ```
> assets_per_type = COUNT(ASSET WHERE asset_type_id = X AND valid_to IS NULL)
> ```

*Edge cases*
- A type with no machines is still valid (prepared ahead of time).
- A class with children can't be deactivated before its children are.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `ASSET_CLASS` (`code`, `name`, `parent_class_id`): **organizational only**, no attribute-value field per [F-04 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#92-asset_class--top-level-taxonomy) · `ASSET_TYPE` (`code`, `name`, `asset_class_id`, `manufacturer`, `model`, `attributes`): **shared technical attributes live here**, [F-04 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#93-asset_type--modelspec-shared-technical-attributes) |
| **Reads** | `ASSET` (usage counts; instance-level `attributes` are exceptions only, [F-04 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#94-asset--core-attributes)) · `DOWNTIME_REASON_ASSET`/`REJECT_REASON_ASSET` (per-**asset** join tables in F-05: scoping is per asset instance, not per `ASSET_CLASS`, see [F-05 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/05-reference-master-data.md#92-downtime_reason_asset-and-reject_reason_asset--asset-scoped-join-tables)) |

**6. Rules & constraints**

1. Class and type codes are unique within the enterprise.
2. A class may sit under another class
   ([F-04 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#91-three-layers-class--type--instance)).
   F-04 defines no depth rejection. Soft UX depth hints (if shown) must not block saving.
3. Classes/types are never deleted, only deactivated. Deactivating a class that still has active types is
   rejected, naming the blocking types.
4. Shared technical attributes are stored as key-value on the type; a machine carries only its own
   exceptions. **Never** as attribute definitions on the class itself
   ([F-04 §9.2–§9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#92-asset_class--top-level-taxonomy)).
   Adding a new attribute key **must not** need a code change
   ([KR 1B.1](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/goalsQ3.md#track-1b--platform-and-edge-engineering-backend-data-engine)).
5. **Who may do it:** creating, updating or deactivating a class or type needs **tenant access** (or
   broader). `ASSET_CLASS` / `ASSET_TYPE` carry no Site Hierarchy FK
   ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#equipment-taxonomy-ast--04-asset-ontologymd-9)),
   so site or enterprise access alone is not enough.

**7. Acceptance criteria**

- **AC-1** **Given** an empty taxonomy, **when** the implementor creates class → sub-class → type, **then**
  all three are saved with correct parent relations and the type appears under its class.
- **AC-2 (validation)** **Given** type code `IM250X` already exists, **when** the same code is saved again,
  **then** it is rejected with an inline message naming the existing holder.
- **AC-3 (soft UX only)** **Given** the implementor creates a deeply nested class, **when** saved, **then** it
  **is** saved: F-04 does not reject on class depth. Any "consider simplifying" message is optional soft
  UX, not a pass/fail product rule.
- **AC-4 (validation)** **Given** a class still has 2 active types, **when** the class is deactivated,
  **then** it is rejected, naming both types.
- **AC-5 (flexible attributes)** **Given** the implementor adds a new shared attribute "tonnage" on the
  Injection Molding **type**, **when** a machine of that type is viewed, **then** the value is
  inherited from the type without a schema change (a machine may override it for itself if
  needed, never stored as a class-level definition).
- **AC-6 (permission)** **Given** a user without tenant access, **when** they open the taxonomy screen,
  **then** they can only read.

**8. Metrics & events**

- **Metric:** `kedalaman_taksonomi_terpakai` (*taxonomy depth actually used*): the average depth of classes
  that actually have machines. Far shallower than what was created means the taxonomy is over-complex.
  Baseline `not yet measured`, source: `ASSET_CLASS` + `ASSET` tables.
- **Counter-metric:** % of `ASSET_TYPE` rows with no machines after 30 days (speculative taxonomy).
- **Events:** `asset_class_created` (`depth`) · `asset_type_created` (`asset_class_id`) ·
  `asset_attribute_defined` (`asset_type_id`, `attribute_key`).

**Dependencies:** `—`

---

#### US-FND-AST-002

**Register physical assets with the tag operators actually read**

**Status:** 🟡 Partly blocked — AC-2 waits on the `asset_tag` uniqueness scope (rule 1); `criticality` and the `owner_org_unit_id` target are `TBD`.

> **In short:** the implementor registers each machine with the tag stuck on its body, so operators and
> reports use the same code. The tag isn't the key and can change; the asset's site comes from its
> placement, not from this screen.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to register machines together with the tag stuck on
their body, so operators refer to assets by the same code as on the floor.

**2. Context**

- **Why:** "Machine 3", "IM-03" and "Injection 3" get treated as three different machines during analysis
  ([F-04, why it matters](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#why-it-matters-what-goes-wrong-without-it)).
- **The tag is not the primary key:** plants do renumber
  ([F-04](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md)).
- **Who and where:** the implementor, on desktop. Operators read the result on the shop floor.

**3. Expectation**

*Layout* ([`UX/04-asset.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/04-asset.md))
- **Registering and editing a machine** happens on the asset form of `SCR-FND-AST-007`: floor tag, name,
  type, serial number, component level, lifecycle status and the rest. The **Edit asset** action on a
  machine's detail page opens this same form, so there is only one asset form.
- **Finding a machine** happens on the **Asset Registry** (`SCR-FND-AST-001`), which lists top-level
  (Equipment-unit) machines only; components appear inside their parent's detail, never in the list.
  - Each row shows the floor tag, the name, class/type badges, **where the machine is placed now** (the
    station, with a primary/secondary badge, or "unplaced"), and its lifecycle status. A **multi-lane
    machine** (one machine placed on several stations, one per lane — see `US-FND-AST-003`) shows **every**
    active placement, with a position count when there is more than one; its detail page does the same.
  - The name shown is the machine's own name when set; otherwise its type's name; otherwise its floor tag.
  - A **search box** matches floor tag, name or serial number. A **Filter** button opens a small window
    (class, lifecycle status, placed / unplaced); filters apply only when the user presses Apply, and the
    button shows a count while any filter is on.
  - Two views: **Table** (a flat, sortable list: "find me this tag") and **Diagram** (one card per asset
    class, unclassified machines together: "what does the fleet look like").
- Selecting a machine opens its **detail page**, not a pop-up: floor tag, badges and name at the top, the
  **Edit asset** and **Place/swap** actions, key facts (serial, component level, class, type, site,
  lifecycle), and three tabs: components, placement history, and **Tags** (2026-09-28, read-only: the machine's
  tags — tag name, role, address (generated or typed), data source, work unit — plus a "Manage tags" link to the
  Tags page filtered to this machine; tags are never created or edited here,
  [`US-FND-DSR-001`](13-data-source.md#us-fnd-dsr-001)). Placing itself is `US-FND-AST-003`.
- Readable within 3 seconds: how many machines are registered, and how many are not yet installed at a
  station (counted from the placement records, see Calculation).
- **Renaming the floor tag changes machine addresses** (2026-09-25). The floor tag is part of the generated
  address of each of the machine's tags on a data source that builds addresses from location
  (`US-FND-DSR-001`). On **Edit asset**, renaming the floor tag of a machine that has such tags asks for
  confirmation with the number of addresses that change. Proposed text, no key yet
  ([UX 04 state notes](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/04-asset.md#state-notes)):
  "This changes \<count\> tag addresses. The hardware team will need a new tag list." (draft). Warn-and-confirm
  vs. block is open (A-14).

*States*

| State | What the user sees |
|-------|--------------------|
| Empty | "No assets registered yet" plus an add button. A search or filter that matches nothing shows a **different** message from this |
| Loading | Placeholder rows |
| Not found | Opening a machine whose ID doesn't exist says "not found", never a blank page |
| Error | Input intact plus a save failure message |
| Offline | Unavailable |
| Success | The new machine appears in the Asset Registry marked "new", and the form is ready for the next entry |
| No permission | Read-only |

Text limits: the floor tag up to 30 characters, the name up to 80. The name is optional and doesn't have to be
unique ([F-04 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#94-asset--core-attributes)).

**4. Calculation**

*How old a machine is*
- The number of months between the day it was commissioned and today.
- Example: commissioned on 2021-03-15, today 2026-08-07 → 64 months.

> [!note]- Exact formula (for developers)
> ```
> asset_age_months = month_diff(commissioning_date, today)
> ```

- Shown blank when there is no commissioning date, **not** 0 months.

*How many registered machines are not yet installed*
- The number of top-level machines that aren't decommissioned and have no station placement in force.
- Example: 12 registered machines, 1 decommissioned, 8 placed → 3 not yet installed.

> [!note]- Exact formula (for developers)
> ```
> uninstalled_assets = COUNT(Equipment-unit ASSET WHERE lifecycle_status ≠ 'decommissioned' AND no active ASSET_PLACEMENT)
> ```

- "No active placement" means no `ASSET_PLACEMENT` row with `valid_to IS NULL` for that `asset_id`
  ([F-04 §9.4 B6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#94-asset--core-attributes) /
  [§9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#96-asset-to-work-unit-placement-rules)).
- **`ASSET.work_unit_id` does not exist.** Don't read or write it.

*Edge case*
- Components (parts inside a machine) are not counted as "not installed", because a component isn't
  meant to occupy a station of its own
  ([F-04 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#95-component-structure)).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `ASSET_TYPE`, `ASSET_CLASS`, `WORK_UNIT`, `ASSET_PLACEMENT` (to derive unplaced / current site — never `ASSET.work_unit_id`) · `ASSET_TAGS` + `DATA_SOURCE.node_naming` (to count the `generated_node` addresses a floor-tag rename changes, [F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address) rule 7) |
| **Writes** | `ASSET`: `asset_tag`, `asset_name` (optional free text, no uniqueness; shown with the fallback `ASSET_TYPE.type_name` → `asset_tag` when empty), `asset_type_id` (nullable: an asset can be registered before it's classified), `serial_number`, `parent_asset_id`, `component_level` (`equipment_unit`/`subunit`/`maintainable_item`/`part`), `lifecycle_status`, `commissioning_date`, `owner_org_unit_id`, `attributes` (instance-level exceptions only), `valid_from`, `valid_to` ([`04-asset-ontology.md` §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#94-asset--core-attributes)) |
| **Not writable / not real fields: don't add** | `ASSET.site_id` (derived, B6) · `manufacturer`/`model` on `ASSET` (A-7) · `criticality` (not in F-04). Details below |

- **`lifecycle_status` is the current value only** ([F-04 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#94-asset--core-attributes), [decision 2026-09-28](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)). It stays in
  FOUNDATION because the placement rule reads it, so it works without a Maintenance licence. Its history is
  MAINTENANCE's (entity `TBD`, LH-1…LH-5). The minute-by-minute operational state (running / idle / down /
  setup) is a different thing: PRODUCTION's `ASSET_STATE_LOG` ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)), never written here.
- **`ASSET.site_id` is not a writable field here or anywhere** (B6). It is derived read-only from the
  machine's current `ASSET_PLACEMENT` row (§9.4). This screen never sets it directly, and a machine with no
  active placement shows as "unplaced" rather than carrying a stale site. A multi-lane machine has several
  active rows, all in one site by rule ([§9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#96-asset-to-work-unit-placement-rules)), so its site stays a single value.
- **`asset_tag` is an address segment** ([F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address)):
  `generated_node` = `nsu=<namespace_uri>;s=<site_code>.<area_code>.<work_center_code>.<work_unit_code>.<asset_tag>.<tag_name>`.
  The suggested pattern is now `<2-letter class><nnn>`, e.g. `IM003` (F-00, 2026-09-25). The charset
  (letters, digits, `_`) is checked when a tag is saved against a `generated` data source, **not** on this
  form — an `asset_tag` such as `IM-003` is still valid here, but blocks generated naming for that machine's
  tags (`US-FND-DSR-001`). Open: A-13.
- Until `A-10` (how a never-placed machine is recorded as "physically at Site X") resolves, this screen
  registers a machine without a site. Placement, and with it a derived site, is handled by `US-FND-AST-003`.
- **`manufacturer`/`model` are not fields on `ASSET`** (A-7). Every machine reads them from its `ASSET_TYPE`
  once classified; a machine registered before classification simply has none yet.
- **`criticality` is not in the current F-04 entity spec at all.** `TBD — perlu konfirmasi PO`/`foundation-domain`:
  either add it to `ASSET` §9.4 first, or drop rule 2 and AC-2/AC-3 below until it is.
- `owner_org_unit_id` is a real field per F-04 §9.4, but its FK target `ORG_UNIT` no longer exists in
  FOUNDATION since Organization was removed ([§13.5](99-history.md#135-organization-removed-2026-09-03)).
  This screen must leave the field blank/unused until `foundation-domain` decides what `owner_org_unit_id`
  points to now. `TBD — perlu konfirmasi PO`/`foundation-domain`.

**6. Rules & constraints**

1. The floor tag is unique within the site
   ([`04-asset-ontology.md` §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#94-asset--core-attributes)).
   It may change without changing the asset's identity.
   - **How this is checked at registration time is `TBD — perlu konfirmasi PO`/`foundation-domain`.**
     `site_id` is derived read-only from `ASSET_PLACEMENT` (see Data & entities), and this screen
     registers a machine with no site yet. §9.4's own "unique within site" wording doesn't say which site the
     check runs against for a not-yet-placed machine: the same open gap as `A-10`.
   - Until resolved, this screen can't enforce site-scoped uniqueness at save time. It may need to fall
     back to a global `asset_tag` uniqueness check, or defer the check to `US-FND-AST-003` when a site is
     first derived via placement.
2. `criticality` is **not** a rule yet: **`TBD`, see Data & entities.** Don't enforce it as required.
3. Lifecycle status is required. Only `operational` machines may occupy an active station. This screen keeps
   the **current** value only; lifecycle history and maintenance-driven transitions (e.g. `under_maintenance`)
   belong to MAINTENANCE, entity `TBD` ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md), [M-01 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/MAINTENANCE/DOCS-EN/01-asset-history.md#92-lifecycle-status-history--declared-entity-tbd)).
4. **Component depth is locked at 4 layers** (Equipment unit → Subunit → Maintainable item → Part). This is
   a requirement, not a recommendation ([§9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#95-component-structure)).
   - A Part-level machine (`component_level = part`) can't be the parent of another asset: ISO 14224
     defines nothing past it.
   - Components inherit the site from the parent and must not have their own station. Only the
     top-level Equipment unit can ever be placed (§9.6).
5. Assets are never deleted: `decommissioned` + `valid_to`.
6. **Who may do it:** creating or updating a machine needs **site access** (or enterprise/tenant access).
   Reading only needs access to that machine, at any tier. For a **multi-lane machine** (several active
   placements), reading needs access to **any** of its work units; updating or deactivating needs access to
   **all** of them, since the change reaches every lane
   ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#equipment-taxonomy-ast--04-asset-ontologymd-9), 2026-09-25).
7. **A floor-tag rename that changes live addresses is confirmed, not silent**
   ([F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address) rule 7):
   the save shows how many active generated addresses will change and asks for confirmation; afterwards a new
   tag hand-off list is needed. Stored readings and history are keyed to the tag, not the address, so nothing
   already recorded changes. `TBD — needs PO confirmation` (A-14): warn-and-confirm, or block the rename while
   generated tags depend on it?

**7. Acceptance criteria**

- **AC-1** **Given** the type already exists, **when** the implementor saves a machine with floor tag and
  (optionally) type (no site is entered here; placement/site comes later via `US-FND-AST-003`), **then**
  the machine is saved and appears in the list with a searchable tag, shown as "unplaced" until placed.
- **AC-2 (validation)** **Given** tag `IM-003` is already used on that site, **when** the same tag is saved,
  **then** it is rejected with "Tag `IM-003` is already used by asset `<name>` on this site."
  **Contingent on rule 1 above being resolved**; until then this AC can't be built as written.
- **AC-3** *(not built: depends on `criticality`, which is `TBD` above — do not build a criticality
  field/validation until F-04 §9.4 defines one).*
- **AC-4 (component)** **Given** a Part-level machine is selected, **when** the
  implementor tries to set it as another asset's parent, **then** it is rejected with an
  explanation that Part is the last ISO 14224 layer: nothing can nest under it.
- **AC-5 (tag change)** **Given** a machine has 200 historical downtime entries, **when** the implementor
  changes its floor tag, **then** every historical entry still refers to the same machine and no data is
  detached.
- **AC-6 (permission)** **Given** a user without site access to that site (and without enterprise/tenant
  access), **when** they open the asset list, **then** they can only read machines within their access.
- **AC-7 (address change)** **Given** a machine has 6 active tags on a data source that builds addresses from
  location, **when** the implementor renames its floor tag, **then** the save first asks for confirmation
  saying 6 tag addresses change, and only saves after confirming. **Contingent on A-14** (confirm vs block).
- **AC-8 (multi-lane permission)** **Given** a multi-lane machine is placed on `S01` and `S02` and a user has
  work-unit access to `S01` only, **when** they open the machine, **then** they can read it but **Edit asset**
  is unavailable.
- **AC-9 (Tags tab)** **Given** machine PCK01 has 3 tags, **when** the user opens its detail and the **Tags** tab,
  **then** the 3 tags are listed read-only with their address and data source, no create/edit control is shown,
  and "Manage tags" opens the Tags page filtered to PCK01.

**8. Metrics & events**

- **Metric:** `aset_terdaftar_per_lini_pilot` (*registered machines per pilot line*): the number of machines
  with type filled on the pilot line. A precondition for OEE being computable at all. Baseline 0
  (2026-08-07); target: 100% of pilot-line machines registered before that line starts recording production.
  Source: `ASSET` table.
- **Counter-metric:** % of machines with empty `attributes` despite their `ASSET_TYPE` defining shared
  attributes.
- **Events:** `asset_created` (`asset_class_id`, `has_serial`) · `asset_tag_changed` (`asset_id`).

**Dependencies:** `US-FND-AST-001`, `US-FND-SIT-001`

---

#### US-FND-AST-003

**Record asset moves as placement history**

**Status:** 🟢 Ready

> **In short:** moving a machine to another station adds a new placement row and closes the old one; it
> never overwrites. Past downtime stays with the station the machine was at back then.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to move assets between stations without overwriting
the old placement, so last month's line performance doesn't change when a machine is swapped.

**2. Context**

- **Why:** moving machines between lines without a trail puts historical data in the wrong place, and last
  month's line performance changes with it
  ([F-04, why it matters](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#why-it-matters-what-goes-wrong-without-it)).
- **Rule of history:** historical analysis must use the placement in effect **at the time of the event**
  ([F-04 §9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#96-asset-to-work-unit-placement-rules)).
- **When:** rare, but every occurrence touches all historical reporting.

**3. Expectation**

*List* (`SCR-FND-AST-003`)
- Every work unit (taken from `SCR-FND-SIT-001`/`002`, not re-created here). Each row shows its current
  active `primary` machine (or "— unplaced —") and its `secondary` count.
- Filterable by Site / Area / Work Center.

*Detail (on select)*
- A timeline per work unit: the active `primary` (at most one) with its full history of replaced rows below
  it, plus active `secondary` rows (0..N), each with its own history.
- Nothing is ever hidden or overwritten (F-00 place ≠ thing).

*Action: place / swap*
- Search an existing, already-registered machine — top-level (Equipment-unit) machines only; a
  Subunit / Maintainable item / Part is filtered out of the picker, not just rejected afterwards.
- Choose the role (`primary`/`secondary`), locked to whichever control opened the action.
- Set an effective date.
- **Machine already placed somewhere else** (multi-lane rule, 2026-09-25): the action must ask whether this is
  a **move** (close the old placement) or an **added lane** (keep both). Both are never kept silently.
  "Add a lane" is refused when the new station is in another site. After adding a lane, the machine's per-lane
  tags without a lane are flagged on the Tags page `SCR-FND-DSR-002` (`US-FND-DSR-001`).
- **Addresses change** (2026-09-25): when the machine has tags on a data source that builds addresses from
  location, saving tells the user the machine's tag addresses change from the effective date and a new tag
  hand-off list is needed (exported from the Tags page `SCR-FND-DSR-002`).
- Proposed text, no keys yet ([UX 04 state notes](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/04-asset.md#state-notes)):
  - question — "\<machine\> is already placed on \<work unit\>. Move it here, or add this as another lane?" (draft)
  - buttons — "Move machine" / "Add lane" (draft)
  - refused — "\<new work unit\> is in \<other site\>. All lanes of a machine must be in one site." (draft)
  - after save — "From \<effective date\>, this machine's tag addresses change. Export a new tag list for the hardware team." (draft)

*Also reachable* from the read-only summary section on the work unit node detail of `SCR-FND-SIT-002`.

| State | What the user sees |
|-------|--------------------|
| Empty (list) | An unplaced work unit shows "— unplaced —", not blank or hidden |
| Empty (detail) | "no placements recorded" instead of an empty timeline |
| Loading | Timeline skeleton |
| Error | Input intact plus a failure message |
| Offline | Unavailable |
| Success | Saving creates a new row. If it replaces an active `primary`, the prior row's end date is closed; it is never deleted |
| No permission | Read-only |

**4. Calculation**

*Which machine(s) were on a station at a point in time*
- Whose placement row covers that moment: it started at or before it and hasn't ended. One `primary` row
  plus any number of `secondary` rows can be in force at the same moment.
- Example: IM-003 is `primary` at `S03` from 2026-01-01 to 2026-06-30, then moves to `S07` from 2026-07-01,
  as `primary` there too. Downtime on 2026-05-12 is attributed to `S03` (via its `primary` at that time),
  **not** `S07`, even though IM-003 sits in `S07` today.

> [!note]- Exact formula (for developers)
> ```
> placement(work_unit, t) = ASSET_PLACEMENT WHERE work_unit_id = work_unit
>                                             AND valid_from ≤ t
>                                             AND (valid_to IS NULL OR valid_to > t)
> ```

*Edge cases*
- A work unit may have **zero or one** active `primary` at a time, plus any number of active `secondary`
  rows ([A-9](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#98-open-questions), resolved). This is
  not "one asset per work unit," it's "one `primary` per work unit."
- A second active `primary` on the same work unit is rejected, naming the incumbent.
- No two active placements (primary or secondary) on the **same** work unit may reference the same machine.
- A past effective date colliding with another placement of the same machine is rejected.
- Unplacing (closing with no new row) is valid.
- **One machine, one active placement — except a multi-lane machine**
  ([F-04 §9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#96-asset-to-work-unit-placement-rules), PO 2026-09-25).
  A machine normally has exactly one active placement. It may be active on several stations at once only as a
  multi-lane machine: one machine body with a separate counter set per lane, each lane its own station. There
  is no multi-lane flag on the machine; "multi-lane" simply means more than one active placement, created only
  by choosing "add a lane".
  - Example: packer PCK01 is `primary` at `S01`. The implementor places it at `S02` and chooses "add a lane":
    PCK01 is now `primary` at `S01` **and** `S02`, one lane each. Had they chosen "move", the `S01` row would
    close instead.
  - All of a multi-lane machine's active placements are in **one site**; they may be in different work centers.
  - The machine may be `primary` on each of its stations (each lane has its own Availability). `secondary` on
    one lane and `primary` on another is allowed too; each role is judged on its own station, with the
    existing rules (one active `primary` per station, the same machine at most once per station).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `ASSET`, `WORK_UNIT`, `ASSET_PLACEMENT` (the machine's other active rows, for the move-or-add-lane question and the one-site check) · `ASSET_TAGS` + `DATA_SOURCE.node_naming` (does the machine have tags whose `generated_node` changes with this placement, [F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address) rule 4) |
| **Writes** | `ASSET_PLACEMENT`: `asset_placement_id` (PK), `asset_id`, `work_unit_id`, `placement_role` (`primary`/`secondary`), `valid_from`, `valid_to` — date or timestamp `TBD` (A-19) ([`04-asset-ontology.md` §9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#96-asset-to-work-unit-placement-rules)). Never `UPDATE` an old placement in place ([F-00 effective-dating rule](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md)) |
| **Not real fields: don't add** | **No multi-lane flag on `ASSET`** (multi-lane = more than one active `ASSET_PLACEMENT` row). **`WORK_UNIT` has no `current_asset_id` and there is no denormalized `ASSET.work_unit_id` to sync.** Do not add them. `ASSET_PLACEMENT` is the record the calculation above reads directly; there is no separate cached "current asset" column to keep in sync |

**6. Rules & constraints**

1. Only a top-level machine (Equipment unit, `parent_asset_id IS NULL`) may be placed, never a Subunit /
   Maintainable item / Part.
2. At most **one active `primary`** placement per work unit: the machine that drives that station's
   Availability and is the downtime-detection reference.
   - Any number of active **`secondary`** placements are allowed: dedicated auxiliary equipment with **no
     Availability of its own**, adding only Quality/downtime-reason detail.
   - Auxiliary equipment that needs its own tracked Availability doesn't belong here as `secondary`. It
     gets its own work unit instead, connected via `WORK_UNIT_FLOW`.
3. A move = close the old row + create a new row. Never overwrite.
4. Historical analysis must use whichever `primary`/`secondary` placement(s) were active at the time of the
   event.
5. A decommissioned machine can't be placed.
6. A component (a part inside a machine) can't be placed directly onto a work unit.
7. **Who may do it:** placing or swapping needs **work-unit access** to that work unit (or
   site/area/enterprise/tenant access). `ASSET_PLACEMENT` resolves to `work_unit_id`, its own level
   ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#equipment-taxonomy-ast--04-asset-ontologymd-9)).
8. **Move or add a lane — never silent** ([F-04 §9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#96-asset-to-work-unit-placement-rules)).
   Placing a machine that already has an active placement must ask: move (close the old row) or add a lane
   (keep both). A forgotten move must not turn a machine into a multi-lane machine by accident. All lanes of a
   machine must be in one site; "add a lane" to a station in another site is refused.
9. **A placement decides the machine's generated tag addresses**
   ([F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address) rules 4–5).
   A new placement gives the same tags new addresses from its effective date; the OPC UA server does not
   follow on its own, so a new tag hand-off list is needed, and reads on the new address fail until the
   hardware side configures it. Closing the last placement leaves the tags with no address (not acquired).
   Stored readings stay keyed to the tag, so history doesn't move. Whether MolcaDx tracks the "configured on
   the server" step is open (A-11, `US-FND-DSR-001`).

**7. Acceptance criteria**

- **AC-1** **Given** IM-003 is `primary` at `S03`, **when** the implementor moves it to `S07` effective
  2026-07-01, **then** the `S03` row closes on 2026-06-30 and a new `primary` row opens at `S07` from
  2026-07-01.
- **AC-2 (history)** **Given** IM-003 had downtime on 2026-05-12, **when** the May loss report is opened,
  **then** that downtime is still counted against `S03` via the `primary` placement active on that date.
- **AC-3 (validation)** **Given** `S07` already has an active `primary`, **when** another machine is placed as
  `primary` at `S07` on an overlapping date, **then** it is rejected with a message naming the incumbent's tag.
- **AC-3b (secondary, no conflict)** **Given** `S07` already has an active `primary`, **when** a different
  machine is placed as `secondary` at `S07`, **then** it succeeds: a `secondary` never conflicts with the
  active `primary`.
- **AC-4 (validation)** **Given** the effective date collides with another placement **of the same machine**,
  **when** saved, **then** it is rejected, naming the colliding range.
- **AC-5 (unplacement)** **Given** the `primary` is unplaced with no destination, **when** saved, **then** the
  `primary` row closes and the work unit shows "— unplaced —" without losing history or affecting any
  `secondary` rows.
- **AC-6 (permission)** **Given** a user without work-unit access to that work unit (and without
  site/area/enterprise/tenant access), **when** they open the work unit's placement detail, **then** the
  timeline is readable but the place/swap action is unavailable.
- **AC-7 (move or add a lane)** **Given** PCK01 is `primary` at `S01`, **when** the implementor places it at
  `S02`, **then** the save is not done until they choose "move" or "add a lane"; "move" closes the `S01` row,
  "add a lane" keeps it and opens a `primary` row at `S02`, so PCK01 has two active placements.
- **AC-8 (one site)** **Given** PCK01 is placed at `S01` in site `JKT1`, **when** the implementor adds a lane
  on a station in another site, **then** it is refused, naming the other site.
- **AC-9 (addresses)** **Given** PCK01 has tags on a data source that builds addresses from location, **when**
  it is moved to `S02` effective 2026-10-01, **then** after saving the user is told its tag addresses change
  from 2026-10-01 and a new tag hand-off list is needed; its stored readings are unchanged.

**8. Metrics & events**

- **Metric:** `perpindahan_tercatat_lengkap` (*moves recorded completely*): the % of asset moves that have an
  `ASSET_PLACEMENT` row (vs being discovered through a direct change). Target 100%. Baseline
  `not yet measured`, source `asset_relocated`.
- **Counter-metric:** 0 historical reports whose numbers change after a move.
- **Events:** `asset_relocated` (`asset_id`, `work_unit_id`, `placement_role`, `effective_date`).

**Dependencies:** `US-FND-AST-002`

**Open questions carried in:** `UX-AST-2` (should an unplaced work unit alert like the Shift Instance
Monitor's "no gaps" rule?), from `UX/04-asset.md`, unresolved. (`UX-AST-3`, the reverse lookup, was resolved
2026-09-17: the Asset Registry shows where each machine is placed.)

## Change notes (history — not needed to build)

- `US-FND-AST-003` · 2026-10-01 — prd-sync c1f1848..30c7087 (keys rule): `asset_placement_id` in Writes, validity type `TBD` (A-19); open items A-17–A-19.
- `US-FND-AST-001`, `US-FND-AST-002` (2026-09-04): taxonomy editing and asset registration merged into the single screen `SCR-FND-AST-001` (screen-merge decision).
- `US-FND-AST-002` (2026-09-03): B6 resolved — `ASSET.site_id` became derived read-only from `ASSET_PLACEMENT`; `ASSET.work_unit_id` removed as a read/write source.
- `US-FND-AST-002`: A-7 resolved — `manufacturer`/`model` moved off `ASSET` to `ASSET_TYPE`.
- `US-FND-AST-002`: rule ② "`criticality` is mandatory" struck, and the criticality-dependent ACs (AC-3) removed, pending the `criticality` TBD.
- `US-FND-AST-003` (2026-08-20): A-9 resolved — one active `primary` plus any number of `secondary` placements per work unit.
- All stories (2026-09-23): detail blocks rewritten from one dense table into numbered sections, and
  "subject whose IDP-asserted scope claim covers …" replaced by *tenant access* / *site access* /
  *work-unit access* (defined in the README glossary). Meaning unchanged; UI copy kept verbatim.
- All stories (2026-09-23, second pass): rewritten in plain language. Entity and field names now appear only
  in section 5, the collapsible "Exact formula (for developers)" boxes and schema-level `TBD` callouts for
  `foundation-domain`; everywhere else things are named in plain words (machine, class/type, floor tag,
  station, placement). Meaning unchanged; UI copy, story IDs and event names kept verbatim.
- `US-FND-AST-001` (2026-09-23, rule 1b sync, PO decision): class/type editing moved to the Reference screens `SCR-FND-REF-009` (Asset class manager) and `SCR-FND-REF-010` (Asset type manager). The old "Table view of `SCR-FND-AST-001` is the only editing place" text was also already stale: UX 04 had split editing out to `SCR-FND-AST-007` on 2026-09-17. The Table-layout TBD is dropped with it.
- `US-FND-AST-002` (2026-09-23, rule 1b sync to UX 04 and F-04): registration moved to `SCR-FND-AST-007`'s asset form (UX 04 split, 2026-09-17), browsing is on the Asset Registry with Table/Diagram, search and Filter; UX-AST-3 resolved (per-machine placement is shown); `asset_name` added (F-04 §9.4, 2026-09-21). Open items UX-AST-3 and "Tree/Diagram search & filter" closed. `US-FND-AST-003`'s carried-in note updated.
- `US-FND-AST-001` — 2026-09-27 (prd-sync docs-molcadx 3cb17fa..7ad3ed8, UX 05 Screen text, PO decision 2026-09-25): Expectation gained *Screen text* tables for `SCR-FND-REF-009`/`010`, copied word for word from UX 05 with keys (`ref-009.*`, `ref-010.*`, `ref-sidebar.item.asset-*`). Most new rows are `TBD — PO` (UX-REF-2/3 in the REF module).
- `US-FND-AST-002` — 2026-09-27 (same sync; PO decision 2026-09-25 generated OPC UA tag addresses, F-04 §9.6/§9.7.1, authorization catalog): the floor tag is now a segment of generated tag addresses — a rename is warned and confirmed (rule 7, AC-7, A-14 open); charset checked at tag save, not here (A-13 open); multi-lane machines show every active placement; multi-lane permission rule (view: any work unit, update: all) and AC-8. Draft text from UX 04 state notes, no key yet.
- `US-FND-AST-003` — 2026-09-27 (same sync; PO decision 2026-09-25, F-04 §9.6/§9.7.1): one active placement per machine except a multi-lane machine; place/swap asks move vs add a lane, never silent; lanes in one site; per-lane `primary` allowed; a placement changes the machine's generated tag addresses (rules 8–9, AC-7…AC-9). Draft text from UX 04 state notes, no key yet.
- `US-FND-AST-002` — 2026-09-28 (prd-sync docs-molcadx 1285317..513c4f9; [asset status decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)): `lifecycle_status` is the current value only (placement rule reads it); history belongs to MAINTENANCE (entity `TBD`); operational state is PRODUCTION's `ASSET_STATE_LOG`. Rule 3 and a Data & entities note. No behaviour or status change.
- `US-FND-AST-002` — 2026-09-28 (prd-sync docs-molcadx 0dea5c3..147e8d2; UX 04): the asset detail gets a read-only
  **Tags** tab with a "Manage tags" link; tags are edited only on the Tags page. AC-9 added.
