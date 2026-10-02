# PRD-003 · Site Hierarchy (`SIT`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-01. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Terms like *site access*, *node* and *implementor* are defined in the [README glossary](README.md#glossary).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| Panel collapse (UX-SIT) | `US-FND-SIT-001` | [UX 01](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md) contradicts itself: the hierarchy panel "never collapses to a rail" vs. both panels "collapse independently to icon rails". | Not named in PRD (UX-SIT) |
| `is_oee_tracked` | `US-FND-SIT-001` (also `US-FND-KPI-001`, `US-FND-KPI-004`) | Should F-01 gain an `is_oee_tracked` field, or should "in the OEE family" be derived from `WORK_CENTER_CLASS`? | `foundation-domain` |
| S-5 — real class values | `US-FND-SIT-001`, `US-FND-SIT-004` | Which site / area / work center / work unit classes the floor and PO actually need ([F-01 §9.13](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#913-open-questions)). The lists can be built; their contents can't be seeded yet. | Product Owner + Operations |
| Deactivating a class in use | `US-FND-SIT-004` | F-01 doesn't say whether a class can be deactivated while active nodes still use it. | `foundation-domain` |
| A-14 — renaming a code in live addresses | `US-FND-SIT-001` | Site / area / work center / work unit codes are segments of generated OPC UA tag addresses. Renaming one: warn with the count of changed addresses and let the user confirm (current spec, AC-12), or block the rename while generated tags depend on it ([F-04 §9.8](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#98-open-questions))? | PO |
| Code-clash message wording | `US-FND-SIT-001` | A code rename that would give two active tags the same address is refused naming the tag (F-04 §9.7.1 rule 3), but UX 01 has no key or text for that message. | PO (UX 01 Screen text) |
| UX-0-1 — Indonesian screen text | all stories | Screen text in UX is English for now; the screens are Indonesian. Second column per table, or a separate translation file keyed by the same keys? ([UX 00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md#screen-text--this-folder-is-the-source-po-decision-2026-09-25)) | PO |
| ~~SL-1 (was A-5 / B5) — `ASSET_STATE_LOG`~~ | `US-FND-SIT-002` | ~~Whether `work_unit_id` is required on a state-log row.~~ **Resolved 2026-09-29 (PO):** recording is per asset, and `asset_id` and `work_unit_id` are **both required** ([PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)). The open-downtime count by work unit can be built. | — |
| [S-8](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#913-open-questions) | `US-FND-SIT-001`, `US-FND-SIT-004` | Keys (2026-10-01): is `enterprise_code`, and `code` on the four class lists, unique — per tenant or per enterprise? | `foundation-domain` / PO |
| [S-9](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#913-open-questions) | `US-FND-SIT-001`, `US-FND-SIT-002` | Keys (2026-10-01): do the "unique within …" code rules and the flow-pair rule hold over all rows (a retired code can't come back) or only active rows? | `foundation-domain` / PO |

## Stories

| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-SIT-001`](#us-fnd-sit-001) | Build the location hierarchy for one site | 🟡 Partly blocked | As an **implementor**, I want to create the Enterprise→Site→Area→Work Center→Work Unit tiers, so that every number has a home and every role has a scope. → [detail](#us-fnd-sit-001) | `—` |
| [`US-FND-SIT-002`](#us-fnd-sit-002) | Deactivate a node without breaking history | 🟢 Ready | As an **implementor**, I want to deactivate lines/stations no longer in use, so they disappear from new input choices while their old data stays readable. → [detail](#us-fnd-sit-002) | `US-FND-SIT-001` |
| [`US-FND-SIT-004`](#us-fnd-sit-004) | Manage the location class lists | 🟡 Partly blocked | As an **implementor**, I want to keep a list of classes for each location level, so lines and stations can be grouped across sites for reporting, for example "OEE of every Packaging Line". → [detail](#us-fnd-sit-004) | `US-FND-SIT-001` |

## Detail blocks

---

#### US-FND-SIT-001

**Build the location hierarchy for one site**

**Status:** 🟡 Partly blocked — how the panels collapse is undecided (UX 01 contradicts itself), and whether renaming a code that is part of live machine addresses is warned or blocked is open (A-14; the spec's current answer, warn and confirm, is what AC-12 tests). All acceptance criteria can be built.

> **In short:** the implementor builds the plant's location tree, from Enterprise down to Site, Area, Work
> Center and Work Unit, using a ⋮ menu and dialogs. It's the first task in every implementation; every other
> module waits for it.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to create the Enterprise → Site → Area → Work Center →
Work Unit tiers, so that every number has a home to roll up into and every role has a scope.

**2. Context**

- **Why:** without a fixed hierarchy, "plant OEE" has no clear meaning, because nobody knows which lines it
  includes ([F-01 §2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#why-it-matters--what-goes-wrong-without-it)).
- **Who and where:** the implementor, at a desktop, in long sessions (hours), on a stable network, while
  setting up a new plant.
- **Order:** this is the **first** task in every implementation. Every other module waits for it.
- **Screens:** `SCR-FND-SIT-001` and `002`, behaving as described in [UX 01](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md).

**3. Expectation**

*Layout*
- **On the left, the hierarchy.** The user can switch between three views:
  - **Tree:** an indented list that folds open and closed with chevrons.
  - **Diagram:** boxes joined by lines, each level can be opened or closed.
  - **Table:** a flat list with filter chips: All / Site / Area / Work Center / Work Unit.
- **On the right, a read-only detail pane** showing the selected item's title, code and type badge. All three
  views share the same selection.
  - When a site is selected, the pane also shows its Shift and Fiscal Year panels
    ([`US-FND-SHF-001`](02-shift.md#us-fnd-shf-001), [`US-FND-FYR-001`](03-yearly-quarterly-declaration.md#us-fnd-fyr-001)).
  - When a work unit is selected, the pane shows which asset is placed there: a read-only Asset Placement
    section (current primary and secondary, then a timeline). Placing or swapping a machine is done on
    `SCR-FND-AST-001`, not here.
  - When an area is selected, the pane shows a short note that shift and fiscal year settings are on the
    site (`sit-002.area-note`). This is information, not an empty or error state.
- By default the tree is folded closed below the Area level.

*Search and keyboard* (PO, 2026-10-01, [UX 01 state notes](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md#state-notes))
- A **search box** above the hierarchy ("Search code or name", `sit-001.search`) matches an item's **code or
  name**, and works the same in all three views. It keeps the matches **plus their parents**, opened, so each
  match is shown in place. A clear button ("Clear search", `sit-001.search.clear`) empties it, and the view goes
  back to the default fold (below Area). No match → "No match for \<search text\>" (`sit-001.empty.no-match`).
- The **Tree** view works from the keyboard, following the WAI-ARIA treeview pattern: ↑/↓ move, → opens an item
  or moves into it, ← closes it or moves to its parent, Home/End go to the first/last visible row, Enter/Space
  select.

*Editing*
- Every item, in all three views, has a "more actions" **⋮ menu** with:
  - **Edit**, which opens a dialog with that item's own fields. A site has code, name and timezone. An area
    has code and name. A work center has code, name and type. A work unit has code and name only: **it has
    no type** ([F-01 §9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#96-work_unit--station-or-machine-position-the-functional-location)).
    The Enterprise has no Edit.
  - **Add** (a child), which opens a dialog to create the next level down. A work unit has no Add, because
    nothing sits below it.
- Every Edit and Add dialog, **including the work unit's**, also has an optional **Class** picker. It lists
  the active classes for that level only (site classes for a site, and so on), from
  [`US-FND-SIT-004`](#us-fnd-sit-004). An item has at most one class, and leaving it empty is fine
  ([F-01 §9.3–§9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#9-entity-specification)). It comes last, after the item's own fields
  ([UX 01](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md)).
- Editing happens **only in dialogs**. There are no inline or floating forms, and no main "Add" button that
  follows the selection.
- Codes are typed freely (never picked from a list), using **letters, digits and `_` only**. Each code is
  **only its own level's segment**, and the Code field's hint shows that segment for the item's kind:
  `JKT1` for a site, `AS` for an area, `L01` for a work center, `S03` for a work unit (`sit-001.field.code.hint`).
  The full dashed form (`JKT1-AS-L01-S03`) is never typed or stored; a screen that shows it joins the
  segments ([F-00 ID and code rules](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#id-code-and-versioning-rules),
  [F-01 S-4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#913-open-questions)).
- **Why codes are strict now:** each code is one segment of the machine tag addresses MolcaDx generates for
  OPC UA sources, e.g. `JKT1.AS.L01.S03.PCK01.Total_Count`
  ([F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address)).
  A code can still be changed, but if it is already part of live addresses, saving the new code first shows
  how many machine tag addresses change and asks the user to confirm (`sit-001.confirm.code-in-address`).
- **How the panels collapse is not decided yet.** [UX 01](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md)
  says both "the hierarchy panel never collapses to a rail" and "both panels collapse independently to icon
  rails". Don't ship either one as final until UX-SIT decides.

*What the user can read within 3 seconds*
- The site name, and how many areas, lines and stations it has.
- On each site (in all three views), a **solid alert badge** showing how many of the company's shifts this
  site hasn't set hours for yet ([`US-FND-SHF-001`](02-shift.md#us-fnd-shf-001)). When the site has set
  hours for all of them, the badge disappears: it never shows "0". Areas, work centers and work units never
  show this badge.

*States*

Screen text is copied word for word from [UX 01 § Screen text](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md#screen-text)
(`SCR-FND-SIT-001`, and the `SCR-FND-SIT-002` rows marked for this story), with its key. If this story and UX 01
differ, UX 01 is right. `(draft)` = proposed wording, usable in a build, may still change. `TBD — PO` = the
build shows `[TBD copy: <key>]`.

| State | What the user sees |
|-------|--------------------|
| Default | `sit-001.default`: N/A — no text: tree folded below Area, focused node highlighted. Pane: `sit-002.default`: N/A — no text: read-only title, code, type badge |
| Empty | No hierarchy yet → one starting item "start with Enterprise" (`sit-001.empty`) whose ⋮ menu offers Add Site, plus the note "No sites yet. Shifts, machines and production data all need a place here first." (draft) (`sit-001.empty.note`) |
| No search match | "No match for \<search text\>" (`sit-001.empty.no-match`) in the current view; clearing the search restores the hierarchy |
| Loading | `sit-001.loading`: N/A — no text: PRD shows "a tree placeholder" only. The ⋮ menus of items already loaded keep working |
| Save error | The dialog stays open with everything typed still there, plus "Could not save — your input has not been lost. Try saving again." (`sit-001.error.save`). If the save was refused because of another item, the pane shows "Couldn't save because of \<item name\>. Open it to see why." (draft) (`sit-001.error.blocking-link`), with that item as a link |
| Offline | Master data can't be changed offline: "A connection is required to change master data" (`sit-001.offline`). Data already loaded can still be read |
| Success | The dialog closes and the new item appears, selected, in the current view, confirmed within 2 seconds with "\<node name\> saved." (draft) (`sit-001.success`). The next Add is one ⋮ away, because implementors create many items in a row |
| No permission | There is no ⋮ menu. The pane says: "Only subjects whose IDP-asserted scope claim covers this site (or a broader `enterprise`/`tenant` claim) may change the location structure. Contact your Plant Admin/IT." (`sit-001.no-permission`) |

*Labels, buttons and messages*

| Key | Where | Text |
|-----|-------|------|
| `sit-001.title` | Page title | "Site Hierarchy" |
| `sit-001.nav.site-hierarchy` | Sidebar entry | "Site Hierarchy" |
| `sit-001.nav.sites` · `.areas` · `.work-centers` · `.work-units` | Sidebar sub-links (open the Table view, filtered) | "Sites" · "Areas" · "Work Centers" · "Work Units" |
| `sit-001.view.tree` · `.diagram` · `.table` | View-mode buttons | "Tree" · "Diagram" · "Table" |
| `sit-001.search` | Search box above the hierarchy, shared by Tree, Diagram and Table | "Search code or name" |
| `sit-001.search.clear` | Clear button inside the search box | "Clear search" |
| `sit-001.chip.all` · `.site` · `.area` · `.work-center` · `.work-unit` | Table view filter chips | "All" · "Site" · "Area" · "Work Center" · "Work Unit" |
| `sit-001.table.columns` | Table view column headers | TBD — PO |
| `sit-001.badge.missing-shifts` | Alert badge on a site, all three views; hidden at 0 (never "0") | "\<missing shift count\>" |
| `sit-001.menu.edit` | ⋮ menu item (not on Enterprise) | "Edit" |
| `sit-001.menu.add` | ⋮ menu item (not on Work Unit), e.g. "Add Site", "Add Work Center" | "Add \<child kind\>" |
| `sit-001.dialog.title-edit` | Edit dialog title | "Edit \<kind\>" (draft) |
| `sit-001.dialog.title-add` | Add dialog title | "Add \<kind\>" (draft) |
| `sit-001.field.code` | Dialog field label (all kinds) | "Code" |
| `sit-001.field.code.hint` | Code field hint, per kind (own segment only) | Site "JKT1" · Area "AS" · Work Center "L01" · Work Unit "S03" |
| `sit-001.field.name` | Dialog field label (all kinds) | "Name" |
| `sit-001.field.timezone` | Dialog field label (Site only) | "Timezone" |
| `sit-001.field.type` | Dialog field label (Work Center only) | "Type" |
| `sit-001.field.type.options` | Work Center type options (four values) | "Production line (discrete)" · "Process cell (batch)" · "Production unit (continuous)" · "Storage zone (warehousing)" (draft) |
| `sit-001.field.class` | Dialog field label, last field, optional (all four kinds) | "Class" |
| `sit-001.button.save` | Dialog primary button | "Save" |
| `sit-001.error.duplicate-code` | Under Code field, code already used | "Code \<code\> is already used by \<node name\>. Use another code." |
| `sit-001.error.timezone-required` | Under Timezone field, none chosen | "Timezone is required" |
| `sit-001.error.type-required` | Under Type field, none chosen | "Type is required" (draft) |
| `sit-001.error.name-too-long` | Name over 80 characters | "Name can be up to 80 characters." (draft) |
| `sit-001.error.code-too-long` | Code over 20 characters | "Code can be up to 20 characters." (draft) |
| `sit-001.error.code-chars` | Under Code field: a character other than letters, digits, `_` | "Use only letters, numbers and _ in the code." (draft) |
| `sit-001.confirm.code-in-address` | Edit dialog: saving a new code that is part of live machine tag addresses | "Changing this code changes \<count\> machine tag addresses. The hardware team will need a new tag list. Save anyway?" (draft) |
| `sit-002.pane.title` | Detail pane title | "\<node name\>" |
| `sit-002.pane.code` | Detail pane code | "\<node code\>" |
| `sit-002.pane.type-badge` | Detail pane type badge labels | "Enterprise" · "Site" · "Area" · "Work Center" · "Work Unit" (draft) |
| `sit-002.area-note` | Area selected: note that shift/fiscal settings live on the site | "Shift and fiscal year settings are on the site. Select \<site name\> to see them." (draft) |

*Asset Placement section (work unit selected, read-only)*

| Key | Where / state | Text |
|-----|---------------|------|
| `sit-002.placement.heading` | Section heading | "Asset Placement" |
| `sit-002.placement.summary` | Current primary / secondary summary | "Primary: \<asset name\>" · "Secondary: \<count\>" (draft) |
| `sit-002.placement.col.role` · `.asset` · `.from` · `.to` · `.status` | Timeline columns | "Role" · "Asset" · "From" · "To" · "Status" |
| `sit-002.placement.default` | Default | N/A — no text: summary + timeline |
| `sit-002.placement.empty` | Empty — no placement rows | "no placements recorded" |
| `sit-002.placement.loading` | Loading | "Loading placements…" (draft) |
| `sit-002.placement.error` | Error | "Couldn't load placements. Try again." (draft) |
| `sit-002.placement.offline` | Offline | "You're offline. Site Hierarchy needs a connection to load and save changes." (draft) |
| `sit-002.placement.success` | Success | N/A — read-only section; place/swap lives on `SCR-FND-AST-001` |
| `sit-002.placement.no-permission` | No permission | "You don't have access to the machines at this position. Ask your Plant Admin/IT for access." (draft) |

The site's Shift panels are in [`US-FND-SHF-001`](02-shift.md#us-fnd-shf-001); its Fiscal Year panels are in
[`US-FND-FYR-001`](03-yearly-quarterly-declaration.md#us-fnd-fyr-001).

Text limits: names up to 80 characters, codes up to 20 characters.

**4. Calculation**

*How deep the tree can go*
- The Enterprise is level 1, and each item is one level deeper than its parent. The deepest allowed is
  level 5.
- Example: work unit `S03` under `JKT1` › `AS` › `L01` (shown joined as `JKT1-AS-L01-S03`) is at level 5, so
  it's valid. Adding anything below it would make level 6, so it's refused.

> [!note]- Exact formula (for developers)
> ```
> depth = 1 + depth(parent)        ENTERPRISE = 1        valid while depth ≤ 5
> ```

*The missing-shift badge on a site*
- The number of the company's active shifts, minus the number of those this site has set hours for.
- Example: the company has 3 shifts and the site has set hours for 2 → the badge shows "1". If it has set
  all 3 → no badge (not "0").
- It's the same number as the "N missing" count in the site's Shift panel.

> [!note]- Exact formula (for developers)
> ```
> missing_shifts(site) = COUNT(ENTERPRISE_SHIFT active) − COUNT(SITE_SHIFT active WHERE site_id = site)
> ```

*How many machine tag addresses a code change affects*
- A generated address is built from the codes of the site, area, work center and work unit a machine is
  placed on, plus the machine's `asset_tag` and the tag's name. So changing an item's code changes the address
  of every active tag, on a connection that uses generated naming, whose machine is actively placed on that
  item or anywhere below it ([F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address)).
- That number is the `<count>` in `sit-001.confirm.code-in-address`. If it is 0, the code is saved with no
  confirmation.
- Example: line `L01` has stations `S01` and `S02`. Packer `PCK01` on `S01` has 5 tags and packer `PCK02` on
  `S02` has 4, all on an OPC UA connection with generated naming → renaming `L01` to `L02` changes **9**
  addresses (`JKT1.AS.L01.S01.PCK01.Total_Count` becomes `JKT1.AS.L02.S01.PCK01.Total_Count`, and so on).
  Renaming station `S02` alone changes 4.

> [!note]- Exact formula (for developers)
> ```
> affected_addresses(node) = COUNT(active generated_node
>                                  WHERE its WORK_UNIT (from the active ASSET_PLACEMENT, or ASSET_TAGS.work_unit_id
>                                        on a multi-lane machine) ∈ subtree(node)
>                                    AND DATA_SOURCE.node_naming = 'generated')
> ```

*Edge cases*
- Items that have been deactivated (their end date has passed) are left out of every count.
- A machine with no active placement has no generated address, so its tags are not counted
  ([F-04 §9.7.1 rule 5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address)).
- A code rename that would give two active tags the same address on one server is refused, naming the tag that
  already uses the address (F-04 §9.7.1 rule 3). This can only happen when two connections share one namespace
  URI. The message wording is not in UX 01 yet.
- An item with nothing below it is still valid. A line may have no stations yet while it's being set up.
- [S-3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#913-open-questions) is **resolved: work units
  are used.** The tree doesn't stop at line level.

*No "tracked lines" count*
- Lines have no field saying whether they're tracked for OEE
  ([F-01 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#95-work_center--production-line--process-cell--production-unit--storage-zone)),
  so this screen shows no "tracked lines" count. Don't add that field, and don't compute
  `COUNT(WORK_CENTER WHERE is_oee_tracked = true AND valid_to IS NULL)`.
- `TBD — perlu konfirmasi foundation-domain`: should F-01 gain an `is_oee_tracked` field, or should "in the
  OEE family" come from `WORK_CENTER_CLASS`? Until that's decided, neither this screen nor
  [`US-FND-KPI-001`](07-kpi.md#us-fnd-kpi-001) / [`US-FND-KPI-004`](07-kpi.md#us-fnd-kpi-004) shows the count.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `ENTERPRISE`, `SITE`, `AREA`, `WORK_CENTER`, `WORK_UNIT`. `ENTERPRISE_SHIFT` and `SITE_SHIFT` are read only, for the missing-shift badge. `SITE_CLASS`, `AREA_CLASS`, `WORK_CENTER_CLASS`, `WORK_UNIT_CLASS` (active rows only) for the Class pickers. `ASSET_PLACEMENT` (`placement_role`, `valid_from`, `valid_to`) and `ASSET` for the work unit's Asset Placement section. For the code-change warning: `ASSET_PLACEMENT`, `ASSET.asset_tag`, `ASSET_TAGS` (`tag_name`, `work_unit_id`, `data_source_id`), `DATA_SOURCE` (`node_naming`, `namespace_uri`) — the generated address itself is derived, never stored ([F-04 §9.7.1 rule 2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address)) |
| **Writes** | `SITE` (`site_code`, `site_name`, `timezone`, `site_class_id`) · `AREA` (`area_code`, `area_name`, `area_class_id`) · `WORK_CENTER` (`work_center_code`, `work_center_name`, `type`, `work_center_class_id`) · `WORK_UNIT` (`work_unit_code`, `work_unit_name`, `work_unit_class_id`). The four class fields are optional |
| **Not real fields: don't add** | `SITE.default_shift_calendar_id` (the calendar concept is retired; not in F-01 §9.3) · `WORK_CENTER.is_oee_tracked`, `.design_capacity_uom`, `.sequence_no` · `WORK_UNIT.position_no`, `.is_bottleneck` (none are in [F-01 §9](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#9-entity-specification)). No new fields |

**6. Rules & constraints**

1. Every item has exactly one parent. Nothing floats loose ([F-01 §9](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#9-entity-specification)).
2. Codes can't repeat: a site code must be unique in the enterprise, an area code and a work center code in
   their site (so line numbers run across the whole site, not per area), and a work unit code in its work
   center ([F-01 §9.3–§9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#9-entity-specification)).
   The message is: "Code \<code\> is already used by \<node name\>. Use another code." (`sit-001.error.duplicate-code`)
3. Every site must have a timezone, picked from the standard (IANA) list, never a fixed offset like +07:00.
4. Every work center must have one of **four** types
   ([F-01 §9.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#95-work_center--production-line--process-cell--production-unit--storage-zone)):
   production line (discrete), process cell (batch), production unit (continuous) or storage zone (warehousing),
   stored as `production_line` / `process_cell` / `production_unit` / `storage_zone`.
5. The number of levels **can't** be increased without a decision by FOUNDATION and the PO.
6. **Who may change it:** creating, editing or deactivating an item needs **site access** to that site, or
   enterprise or tenant access.
7. Codes the plant already uses **are kept** rather than replaced with the suggested pattern, but they are
   entered one segment per level and any `-` or `.` in them is rewritten
   ([F-00 ID and code rules](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#id-code-and-versioning-rules),
   [FLOW-01](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/FLOW/01-site-skeleton.md)).
   Floor naming goes in the name, which stays free text (F-01 S-4).
8. An item's class is optional, and there is at most one per item. Only classes of the same level can be
   picked (a site can't get a work-center class). A class is for grouping and reports only: it never changes
   how the system treats the item ([F-01 §9.7–§9.10](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#97-site_class--cross-cutting-classification-for-site)).
9. **Code characters and shape** (since 2026-09-25, [F-01 S-4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#913-open-questions)):
   a code may contain only letters, digits and `_` (no `.`, no `-`, no spaces), checked on every save; it is
   that level's own segment only, never the parent's code plus its own. A refused character shows
   "Use only letters, numbers and _ in the code." (draft) (`sit-001.error.code-chars`).
10. **Codes in live addresses are effectively frozen.** Renaming a site, area, work center or work unit code
    that is part of active generated tag addresses shows how many addresses change and asks for confirmation
    (`sit-001.confirm.code-in-address`); after it, a new tag hand-off list is needed for the hardware team
    ([F-04 §9.7.1 rule 7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address)).
    Stored readings and history are not affected: they are keyed by the tag, not its address (rule 1 there).
    `TBD — needs PO confirmation` (A-14): keep warn-and-confirm, or block the rename while generated tags
    depend on the code?

**7. Acceptance criteria**

- **AC-1** **Given** an implementor on an empty site, **when** they create a site, an area, a work center and
  a work unit in that order, **then** all four are saved under the right parent and appear in the tree at
  their level.
- **AC-2 (validation)** **Given** the site already has a work center with code `L01`, **when** the implementor
  saves another work center with code `L01`, **then** the save is refused and a message under the code field
  names the item already using it.
- **AC-3 (validation)** **Given** the site form is open with no timezone chosen, **when** "Save" is pressed,
  **then** the form is not sent and "Timezone is required" appears under the timezone field.
- **AC-4 (limit)** **Given** a work unit, **when** the implementor opens its ⋮ menu, **then** there is no
  "Add" option, because nothing can sit below a work unit (the 5-level maximum).
- **AC-5 (error)** **Given** the connection drops during a save, **when** the save is tried, **then** the
  dialog stays open with everything typed still there, and the message says the data isn't saved yet, not
  that it's lost.
- **AC-6 (permission)** **Given** a user without access to that site (and without enterprise or tenant
  access), **when** they open the location structure screen, **then** they can only read it, no item has a ⋮
  menu, and the pane tells them who to contact.
- **AC-7 (missing-shift badge)** **Given** the company has 3 shifts and site JKT1 has set hours for 2 of them,
  **when** the hierarchy is viewed in Tree, then Diagram, then Table, **then** JKT1 shows a solid alert badge
  reading "1" in every view, no area, work center or work unit shows a badge, and once the third shift gets
  its hours the badge disappears instead of reading "0".
- **AC-8 (dialog editing)** **Given** any of the three views, **when** the implementor opens ⋮ on an area,
  **then** Edit and Add Work Center are offered and each opens a dialog. The Enterprise offers no Edit. No
  inline or floating form appears anywhere.
- **AC-9 (class)** **Given** the work center classes "Packaging Line" and "Utility Line" exist, **when** the
  implementor edits line L01 and picks "Packaging Line", **then** L01 is saved with that class. Leaving the
  Class picker empty also saves.
- **AC-10 (class by level)** **Given** site, area, work center and work unit classes all exist, **when** the
  implementor opens the Class picker on a work unit, **then** only work unit classes are offered, and a
  deactivated class is not offered at all.
- **AC-11 (code characters)** **Given** the Add Area dialog is open, **when** the implementor saves the code
  `JKT1-AS`, **then** the save is refused and "Use only letters, numbers and _ in the code." appears under the
  Code field; saving `AS` succeeds, and the Code hint for an area reads "AS".
- **AC-12 (code in live addresses)** **Given** line `L01` has 9 active tags with generated addresses on its
  stations, **when** the implementor changes its code to `L02` and saves, **then** the dialog asks
  "Changing this code changes 9 machine tag addresses. The hardware team will need a new tag list. Save
  anyway?" and nothing is saved until they confirm. **Given** a line with no generated addresses, **when** its
  code is changed, **then** it saves with no confirmation. (A-14 may turn the first case into a refusal.)
- **AC-13 (search)** **Given** JKT1 has work center `L01` under area `AS`, folded closed, **when** the implementor
  types "L01" in any of the three views, **then** `L01` is shown with JKT1 and `AS` above it, opened, and
  unrelated items are hidden. **When** they press "Clear search", **then** the default fold returns.
- **AC-14 (no match)** **Given** no item's code or name contains "XYZ", **when** it is searched, **then** the view
  reads "No match for XYZ".
- **AC-15 (keyboard)** **Given** the Tree view has focus on a closed area, **when** the implementor presses →,
  then ↓, then Enter, **then** the area opens, focus moves to its first child, and that child is selected in the
  detail pane.

**8. Metrics & events**

- **Metric:** `waktu_setup_hierarki_site` (*how long it takes to set up a site's hierarchy*). Baseline
  `not yet measured`: time it by observation at the first pilot site. The target is set once the baseline
  exists. Measured from the first to the last `hierarchy_node_created` event for the site (`site_id`).
- **Counter-metric:** how many items are deactivated within 7 days of being created. A high number means the
  structure was guessed, not known.
- **Events:** `hierarchy_node_created` (`level`, `parent_level`, `site_id`, `view_mode`) ·
  `hierarchy_node_updated` (`level`, `field_changed`, `view_mode`) · `hierarchy_searched` (`view_mode`,
  `match_count`).

**Dependencies:** `—`

---

#### US-FND-SIT-002

**Deactivate a hierarchy node without breaking history**

**Status:** 🟢 Ready — SL-1 was resolved by the PO on 2026-09-29 (`work_unit_id` is required on every `ASSET_STATE_LOG` row), so the open-downtime blocker count can be built too.

> **In short:** retiring a line or station hides it from new input but keeps all its history. The system
> refuses if anything is still running on it, and always shows the impact before the user confirms.

> `UX-SIT-3` is resolved: `valid_from`/`valid_to` on `AREA`/`WORK_CENTER`/`WORK_UNIT` are required, exactly
> as spec'd in [F-01 §9.4–§9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#9-entity-specification),
> and are separate from `ASSET_PLACEMENT`'s own effective dating (F-04 §9.6). Add the columns to the live
> schema if they are not there yet.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to deactivate a line or station that is no longer in
use, so it disappears from new input choices while all of its historical data stays readable.

**2. Context**

- **Why:** deleting old items leaves their history with nowhere to belong
  ([F-01, common pitfalls](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#common-pitfalls)).
- **When:** a line is moved or retired. It's rare, but each time it risks spoiling last year's reports.
- **Who and where:** the implementor, at a desktop.
- **Screen:** the node detail `SCR-FND-SIT-002`; Deactivate sits in the footer of the node's Edit dialog ([UX 01](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md)).

**3. Expectation**

- **Deactivate** is in the footer of the item's **Edit dialog** (opened from its ⋮ menu), with an effective date.
- Before confirming, the system shows an **impact list**:
  - how many items below it will be deactivated too;
  - how many things still running on it are blocking it;
  - which modules use this item.
- A "show deactivated" toggle above the hierarchy (`sit-001.toggle.show-deactivated`) brings inactive items
  into view.

Screen text is copied word for word from [UX 01 § Screen text](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md#screen-text)
(`SCR-FND-SIT-002`, node pane and Deactivate), with its key. If they differ, UX 01 is right.

| State | What the user sees |
|-------|--------------------|
| Empty | not applicable (`sit-002.empty`: N/A — PRD: "not applicable") |
| Loading | The impact list shows a placeholder while it loads, with "Working out the impact…" (draft) (`sit-002.loading`). The confirm button stays locked until the impact is worked out: the user can never confirm without seeing the impact |
| Error | "Could not compute impact — deactivation was not performed." (`sit-002.error.impact`) |
| Offline | Not available (master data needs a connection): "You're offline. Site Hierarchy needs a connection to load and save changes." (draft) (`sit-002.offline`) |
| Success | The item shows "inactive since \<date\>" (`sit-002.success`), stays in the tree greyed out, and disappears from every picker used for new input |
| No permission | Deactivate is unavailable, and the access is explained: "Only subjects whose IDP-asserted scope claim covers this site (or a broader `enterprise`/`tenant` claim) may change the location structure. Contact your Plant Admin/IT." (`sit-002.no-permission`) |

*Labels, buttons and messages*

| Key | Where | Text |
|-----|-------|------|
| `sit-001.toggle.show-deactivated` | Toggle above the hierarchy | "show deactivated" |
| `sit-002.button.deactivate` | Edit dialog footer button | "Deactivate" |
| `sit-002.field.effective-date` | Deactivate — effective date label | "Inactive from" (draft) |
| `sit-002.impact.descendants` | Impact list — items below that will be deactivated too | "\<count\> items below this will be deactivated too." (draft) |
| `sit-002.impact.blockers` | Impact list — things still running that block it | "\<count\> things still in use here are blocking this." (draft) |
| `sit-002.impact.modules` | Impact list — modules that use this item | "Used in: \<module list\>" (draft) |
| `sit-002.button.confirm` | Deactivate confirm button | "Deactivate \<kind\>" (draft) |
| `sit-002.error.blocked` | Deactivation refused, names each blocker (e.g. work order number) | "Can't deactivate \<node name\>. Still in use: \<blocker list\>." (draft) |
| `sit-002.error.past-date` | Effective date in the past | "The effective date cannot be in the past — corrections to the past are handled as flagged adjustments." |

**4. Calculation**

*What blocks a deactivation*
- The item can't be deactivated while anything below it is still in use. The system counts three things:
  - jobs on it that are waiting, scheduled, running or on hold;
  - machine stops on it that haven't ended yet;
  - shifts on it that are still open.
- Example: line L01 has 2 jobs running and 1 stop that hasn't ended → 3 blockers → the deactivation is
  refused, and all three are named.
- If nothing is blocking → the item and everything below it get an end date equal to the effective date.

> [!note]- Exact formula (for developers)
> ```
> blocker_count =
>     COUNT(WORK_ORDER_OPERATION WHERE work_unit_id ∈ subtree(node)
>                                  AND status ∈ {pending, scheduled, in_progress, on_hold})
>   + COUNT(ASSET_STATE_LOG      WHERE work_unit_id ∈ subtree(node) AND ended_at IS NULL)
>   + COUNT(SHIFT_INSTANCE       WHERE work_center_id ∈ subtree(node) AND status = 'open')
>
> if blocker_count = 0:  valid_to = effective_date   on the node and all its descendants
> ```

*Edge cases*
- An effective date in the past is refused, because it would change history.
- An effective date of today means the item stops at the end of today.

*Two of the three counts depend on other teams*
- **Jobs** belong to PRODUCTION
  ([01-planning.md § WORK_ORDER_OPERATION](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#work_order_operation--execution-and-output-per-shift)).
  This story only reads them; it doesn't redefine them.
- **Machine stops** come from the downtime log, `ASSET_STATE_LOG`, owned by PRODUCTION since 2026-09-28
  ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md), [PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time)). An open stop is a row with `ended_at` null. Every row
  carries `work_unit_id`: PRODUCTION's SL-1 (was A-5) was resolved on 2026-09-29 — recording is per asset, and
  `asset_id` and `work_unit_id` are both required, the work unit resolved from the placement at the event time.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORK_ORDER_OPERATION` (owned by PRODUCTION, [01-planning.md § WORK_ORDER_OPERATION](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/01-planning.md#work_order_operation--execution-and-output-per-shift)) · `ASSET_STATE_LOG` (owned by PRODUCTION, [PRODUCTION Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#asset_state_log--operational-state-per-asset-over-time); `work_unit_id` (required since SL-1, 2026-09-29), `ended_at`) · `SHIFT_INSTANCE` · `ASSET_PLACEMENT` |
| **Writes** | `valid_to` on the selected `AREA` / `WORK_CENTER` / `WORK_UNIT` and everything below it. Nothing is physically deleted ([F-00 §4.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md)) |

**6. Rules & constraints**

1. Master data is never physically deleted.
2. If anything is still running on the item, deactivation is **refused**, and the message names each thing
   that blocks it (`sit-002.error.blocked`).
3. Deactivating an item also deactivates everything below it.
4. Inactive items no longer appear when entering new data, but stay visible in history.
5. A machine placed on a station that's being deactivated must be removed from it first
   (`US-FND-AST-003`). If it hasn't been, it shows up as a blocker.
6. **Who may do it:** deactivating needs **site access**, or enterprise or tenant access.

**7. Acceptance criteria**

- **AC-1** **Given** a line with nothing running on it, **when** the implementor deactivates it as of today,
  **then** the line and all its stations are marked inactive and disappear from the line picker on shopfloor
  screens.
- **AC-2 (validation)** **Given** the line has 1 job running, **when** deactivation is confirmed, **then** it
  is refused with "Can't deactivate \<node name\>. Still in use: \<blocker list\>." (`sit-002.error.blocked`), the
  list naming the blocking work order number.
- **AC-3 (validation)** **Given** the effective date is in the past, **when** saved, **then** it is refused
  with "The effective date cannot be in the past — corrections to the past are handled as flagged adjustments."
- **AC-4 (history)** **Given** the line is already inactive, **when** a manager opens an OEE report for a
  period before it was deactivated, **then** that line's numbers still appear in full, with its name.
- **AC-5 (loading)** **Given** the impact list is still loading, **when** the implementor presses confirm,
  **then** the button isn't active yet and the system says "Working out the impact…" (`sit-002.loading`).
- **AC-6 (permission)** **Given** a user without access to that site (and without enterprise or tenant
  access), **when** they open the item, **then** "Deactivate" is unavailable, with an explanation of the
  access needed.

**8. Metrics & events**

- **Metric:** `penonaktifan_ditolak_karena_penghalang` (*deactivations refused because something was still
  running*): the share of deactivation attempts that are refused. A high share means the impact list isn't
  visible early enough. Baseline `not yet measured`; measured from the `hierarchy_node_deactivate_blocked` event.
- **Counter-metric:** no historical report's numbers ever change after a deactivation (must stay 0).
- **Events:** `hierarchy_node_deactivated` (`level`, `descendant_count`, `effective_date`) ·
  `hierarchy_node_deactivate_blocked` (`blocker_type`, `blocker_count`).

**Dependencies:** `US-FND-SIT-001`

---

#### US-FND-SIT-004

**Manage the location class lists**

**Status:** 🟡 Partly blocked — the real class values are undecided (S-5), and so is whether a class still in use can be deactivated. The lists themselves can be built.

> **In short:** the implementor keeps four short lists of classes, one for each location level: site, area,
> work center and work unit. A class groups items across sites for reports, for example "every Packaging
> Line". It never changes how the system behaves.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to keep a list of classes for each location level, so
lines and stations can be grouped across sites for reporting, for example "OEE of every Packaging Line".

**2. Context**

- **Why:** the location tree says *where* something is. A class says *what kind* it is, across every site.
  Without it, "all packaging lines in the company" can only be found by reading names
  ([F-01 §9.7–§9.10](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#97-site_class--cross-cutting-classification-for-site)).
- **Deliberately simple:** each list is flat. There is no class inside a class, and a class has no attributes
  of its own. This is unlike asset classes, which nest ([F-01 §9.10 note](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#910-work_unit_class--cross-cutting-classification-for-work_unit)).
- **Values not known yet:** the spec's examples ("Region: East", "Contract Manufacturer", "Packaging Line",
  "Manual Station") are placeholders. The real lists wait on S-5 (PO + Operations).
- **Who and where:** the implementor, at a desktop, during plant setup, on a stable network.
- **Screens:** four Reference screens, each with its own nav entry: the site, area, work center and work
  unit class managers (`SCR-FND-REF-005` to `008`,
  [UX 05](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md)).

**3. Expectation**

- Four lists, one per level, each on its own Reference screen: site classes (`SCR-FND-REF-005`), area
  classes (`SCR-FND-REF-006`), work center classes (`SCR-FND-REF-007`), work unit classes (`SCR-FND-REF-008`).
- Each row shows the class code, name, and how many active items use it.
- A class is added and edited in a dialog with two fields: code and name.
- Once saved, a class appears in the Class picker of **its own level only** ([`US-FND-SIT-001`](#us-fnd-sit-001)).
- Readable within 3 seconds: how many classes each level has.

Screen text comes from [UX 05 § Screen text](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/05-reference.md#screen-text)
(`SCR-FND-REF-005 … 008`, one set of keys for all four lists; only the level noun in the title and sidebar
label changes). Nearly all of it is still `TBD — PO`: a build shows `[TBD copy: <key>]` for those.

| State | What the user sees |
|-------|--------------------|
| Default | The list (`ref-class.default`: N/A — no text) |
| Empty | A level with no classes yet shows an empty list and an Add action; classes are optional (UX 05: the list starts empty, real values wait on S-5). Text: TBD — PO (`ref-class.empty`) |
| Loading | A list placeholder (`ref-class.loading`: N/A — no text) |
| Save error | The dialog stays open with everything typed still there, and says the class was not saved. Text: TBD — PO (`ref-class.error.save`) |
| Offline | Not available (master data needs a connection). Text: TBD — PO (`ref-class.offline`) |
| Success | The dialog closes and the new class appears in its list, ready to be picked (`ref-class.success`: N/A — no text) |
| No permission | The lists can be read but not changed, with an explanation that tenant access is needed. Text: TBD — PO (`ref-class.no-permission`) |

*Labels, buttons and messages*

| Key | Where | Text |
|-----|-------|------|
| `ref-sidebar.item.site-classes` · `.area-classes` · `.work-center-classes` · `.work-unit-classes` | Reference sidebar items 5–8 | "Site classes" · "Area classes" · "Work center classes" · "Work unit classes" |
| `ref-class.title` | Page title (level noun changes) | TBD — PO (see UX-REF-2) |
| `ref-class.summary` | Number of classes on this level | TBD — PO |
| `ref-class.table.columns` | Columns: code, name, number of active items using it | TBD — PO |
| `ref-class.action.add` | Add action | TBD — PO |
| `ref-class.dialog.fields` | Add / Edit dialog labels: code, name (only these two) | TBD — PO |
| `ref-class.dialog.save` | Dialog save button | TBD — PO |
| `ref-class.action.deactivate` | Deactivate a class (sets its end date) | TBD — PO |
| `ref-class.error.required` | Save, code or name empty | TBD — PO |
| `ref-class.error.in-use` | Deactivating a class active items still use — whether this is refused is undecided | TBD — PO |

**4. Calculation**

*How many items use a class*
- The number of active items of that level whose class is this one.
- Example: the work center class "Packaging Line" is set on lines L01 and L03 at JKT1 and on line L02 at a
  second site; L03 has been deactivated → the list shows **2**.

> [!note]- Exact formula (for developers)
> ```
> items_using(class) = COUNT(<level entity> WHERE <level>_class_id = class AND valid_to IS NULL)
>     e.g. COUNT(WORK_CENTER WHERE work_center_class_id = X AND valid_to IS NULL)
> ```

*Edge cases*
- A class with no items is still valid (it can be prepared in advance).
- A deactivated class isn't offered in pickers any more, but items that already had it keep showing it in
  history.
- Whether a class can be deactivated while active items still use it isn't decided
  (`TBD — perlu konfirmasi foundation-domain`).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `SITE_CLASS`, `AREA_CLASS`, `WORK_CENTER_CLASS`, `WORK_UNIT_CLASS` — each with its own ID (`site_class_id`, `area_class_id`, `work_center_class_id`, `work_unit_class_id`), `code`, `name`, `valid_from`, `valid_to` ([F-01 §9.7–§9.10](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md#97-site_class--cross-cutting-classification-for-site)) |
| **Reads only** | `SITE.site_class_id`, `AREA.area_class_id`, `WORK_CENTER.work_center_class_id`, `WORK_UNIT.work_unit_class_id`, for the "used by" count |
| **Not real fields: don't add** | No `parent_class_id` (the lists are flat), no attribute fields, no join table for several classes per item (one class per item, per F-01) |

**6. Rules & constraints**

1. Code and name are required. Codes follow the F-00 ID and code rules
   ([F-00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md)).
2. Each list belongs to exactly one level. A class can't be moved to another level.
3. Classes don't nest and carry no attributes.
4. A class only groups items. Nothing in the system behaves differently because of an item's class; any such
   rule would have to be added explicitly by the module that needs it.
5. Classes are never physically deleted. Retiring one sets its end date.
6. Classes are created one at a time. There is no bulk import
   ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#site-hierarchy-sit--01-site-hierarchymd-9)).
7. **Who may change them:** creating, editing or deactivating a class needs **tenant access**
   (`foundation:sit:tenant:update`), because the lists belong to no single site.

**7. Acceptance criteria**

- **AC-1** **Given** no work center classes exist, **when** the implementor adds "Packaging Line" with code
  `PKG`, **then** it appears in the work center class list with 0 items using it.
- **AC-2 (own level only)** **Given** "Packaging Line" is a work center class, **when** the implementor opens
  the Class picker on a site, an area or a work unit, **then** "Packaging Line" is not offered.
- **AC-3 (count)** **Given** "Packaging Line" is set on two active lines and one deactivated line, **when** the
  list is shown, **then** it says 2 items use it.
- **AC-4 (deactivated class)** **Given** "Utility Line" is deactivated, **when** the implementor opens a work
  center's Class picker, **then** it is not offered, and a line that already had it still shows it in history.
- **AC-5 (flat)** **Given** the class dialog is open, **when** the implementor looks at its fields, **then**
  there are only code and name: no parent class and no attributes.
- **AC-6 (permission)** **Given** a user without tenant access, **when** they open the class lists, **then**
  they can read them but can't add, edit or deactivate.

**8. Metrics & events**

- **Metric:** `lini_berkelas` (*share of active lines with a class*): active work centers with a work center
  class, divided by all active work centers. Baseline `not yet measured`; the target is set once S-5 settles
  what the classes are. Source: the `WORK_CENTER` table.
- **Counter-metric:** classes created and then unused for 30 days (a sign the list was guessed, not needed).
- **Events:** `location_class_created` (`level`) · `location_class_deactivated` (`level`, `items_using`).

**Dependencies:** `US-FND-SIT-001`

## Change notes (history — not needed to build)

- `US-FND-SIT-001` (2026-10-01, prd-sync `docs-molcadx` 725362a..bf12721, PO decision): search box (`sit-001.search`, `.search.clear`, `.empty.no-match`) across all three views, Tree keyboard navigation, state "No search match", AC-13–AC-15, event `hierarchy_searched`.
- `US-FND-SIT-001` (2026-09-02): screen behaviour synced to UX 01 (`SCR-FND-SIT-001`/`002`).
- `US-FND-SIT-001` (2026-09-03): an earlier draft counted a "tracked lines" figure from `WORK_CENTER.is_oee_tracked`, a field that was invented. The count was removed; `US-FND-KPI-001`/`US-FND-KPI-004` had the same assumption.
- `US-FND-SIT-001`: `SITE.default_shift_calendar_id` removed (calendar concept retired).
- `US-FND-SIT-001`: an earlier draft invented five fields (`WORK_CENTER.is_oee_tracked`, `.design_capacity_uom`, `.sequence_no`, `WORK_UNIT.position_no`, `.is_bottleneck`); they were removed.
- `US-FND-SIT-002` (2026-09-22): unblocked by rule 1b sync, PO decision (`UX-SIT-3` resolved). Not a spec change — the fields were always listed in F-01; the gap was implementation catching up to spec. `ASSET_PLACEMENT` effective dating was untouched by this decision.
- `US-FND-SIT-002` (2026-09-03): `WORK_ORDER_OPERATION` moved to PRODUCTION; A-5 reopened (B5).
- All stories (2026-09-23): detail blocks rewritten from one dense table into numbered sections, and
  "subject whose IDP-asserted scope claim covers …" replaced by *site access* (defined in the README glossary).
  Meaning unchanged; UI copy kept verbatim. Stale reference labels "F-04 §7.4" / "F-04 §4" corrected to F-01
  (the links already pointed at F-01).
- All stories (2026-09-23, second pass): rewritten in plain language. Entity and field names now appear only in
  section 5 and in the collapsible "Exact formula (for developers)" boxes; everywhere else items are named in
  plain words (site, line, station, job, machine stop). Meaning unchanged; UI copy kept verbatim.
- `US-FND-SIT-001` / new `US-FND-SIT-004` (2026-09-23, rule 1b sync): F-01 has defined `SITE_CLASS`/`AREA_CLASS`/`WORK_CENTER_CLASS`/`WORK_UNIT_CLASS` (§9.7–§9.10) and the four optional class fields (§9.3–§9.6) since 2026-08-26, but no story used them. SIT-001 gains the Class picker, the class fields, rule 8, AC-9 and AC-10. SIT-004 is new, for managing the four lists. Open items gain S-5, the missing UX screen, and the undecided deactivate-while-in-use rule.
- `US-FND-SIT-004` (2026-09-23, rule 1b sync to the PO's UX 05 edit): the class lists now have screens, `SCR-FND-REF-005`–`008` on Reference. The "screen not declared" TBD is closed; the Class picker in node dialogs is still missing from UX 01.
- `US-FND-SIT-001` (2026-09-23): UX 01 now declares the Class picker (last field in every node dialog); the placement TBD and its open item are closed.
- 2026-09-23 (rule 1b, full UX cross-check): every story in this file now names the UX screen it is built on ; SIT-002's Deactivate moved into the Edit dialog footer, as UX 01 declares.
- `US-FND-SIT-001` (2026-09-27, prd-sync docs-molcadx 3cb17fa..7ad3ed8): codes are now one segment per level
  (`JKT1` / `AS` / `L01` / `S03`), letters, digits and `_` only, because they are segments of generated OPC UA tag
  addresses (F-01 S-4 partly reversed, F-00 pattern table, FLOW-01, PO decision 2026-09-25). The `JKT1-AS-L01-S03`
  hint is replaced by a per-kind hint; rule 2 gains area-code uniqueness; rule 7 rewritten; new rules 9–10, the
  affected-address count, AC-11, AC-12; open items A-14, code-clash wording and UX-0-1 added. Screen text for
  `SCR-FND-SIT-001`, the node pane, the Area note and the Asset Placement section copied word for word from UX 01
  with keys.
- `US-FND-SIT-002` (2026-09-27, prd-sync docs-molcadx 3cb17fa..7ad3ed8): Deactivate screen text copied from UX 01
  with keys (impact list lines, confirm button, blocked message, offline text, "show deactivated" toggle); AC-2 and
  AC-5 now quote the text.
- `US-FND-SIT-003` (2026-09-27, prd-sync docs-molcadx 3cb17fa..7ad3ed8): `SCR-FND-SIT-003` screen text copied from
  UX 01 with keys (picker label, offline marker).
- `US-FND-SIT-003` (2026-09-27, PO, [docs-molcadx#46](https://github.com/molca-id/docs-molcadx/issues/46)): **retired**. The
  location context picker is not a FOUNDATION story and no FOUNDATION app shows it. Moved to
  [90-closed-stories.md](90-closed-stories.md#us-fnd-sit-003); ID kept, not reused.
- `US-FND-SIT-004` (2026-09-27, prd-sync docs-molcadx 3cb17fa..7ad3ed8): keys for `SCR-FND-REF-005 … 008` from
  UX 05 added; all but the four sidebar labels are still `TBD — PO`.
- `US-FND-SIT-002` (2026-09-28, prd-sync docs-molcadx 1285317..513c4f9, [asset status decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)):
  `ASSET_STATE_LOG` owner = PRODUCTION (B5 ownership resolved). The open-downtime count now waits on PRODUCTION's
  SL-1 (recording level, was A-5), not on ownership. Status unchanged (partly blocked).
- `US-FND-SIT-002` (2026-10-01, prd-sync docs-molcadx 147e8d2..787e819): PRODUCTION SL-1 resolved by the PO on
  2026-09-29 — `ASSET_STATE_LOG` is recorded per asset with `asset_id` and `work_unit_id` both required. The
  open-downtime blocker count can now be built. Status 🟡 → 🟢; SL-1 open item closed.
