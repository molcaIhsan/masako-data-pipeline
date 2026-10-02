# PRD-003 · Data Source (`DSR`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-13. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.


> `DATA_SOURCE` ([F-13 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md#91-data_source--one-connectionendpoint)) is this module's own entity.
> `ASSET_TAGS` ([F-04 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#97-asset_tags--per-asset-tag--signal-declaration)) stays formally owned by Equipment Taxonomy (`AST`) but is declared on this module's screen, alongside `DATA_SOURCE`, because a tag mapping has no meaning without the connection it reads through.
> Screen: `SCR-FND-DSR-001` "Data sources & tags", in its own sidebar group **Connections**, not Reference data
> ([UX 13](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/13-data-source.md), PO 2026-09-25).

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| Dependency on `US-FND-CFG-001` | `US-FND-DSR-001` | This story depends on `US-FND-CFG-001`, whose Configuration Service entities are only proposed, not modelled in FOUNDATION. | `foundation-domain`, PO |
| `TBD` (transformation gap) | `US-FND-DSR-001` | Neither `DATA_SOURCE` nor `ASSET_TAGS` has a `scale`/`offset`/`interpretation_rules` field. Does transformation get new fields on `ASSET_TAGS`, or is it resolved entirely by `kind` + `tag_role` (raw units always, no scale/offset needed) and the transformation language is obsolete? Blocks AC-1 (interpreted value), AC-3, AC-5. | `foundation-domain` |
| H-DS-1 | `US-FND-DSR-001` | `DATA_SOURCE.heartbeat_interval`/`last_seen_at` granularity: connection-level vs per-tag. Blocks `US-FND-INT-003`'s status classification, not this story's own ACs. | `foundation-domain` |
| H-DS-2 | `US-FND-DSR-001` | How credentials in `DATA_SOURCE.connection_config` are stored (not plaintext; mechanism `TBD`). | `foundation-domain` |
| [H-DS-5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md#whats-still-unknown) | `US-FND-DSR-001` | Namespace URI format: what URI Molca's hardware person sets on each OPC UA server (one per site, one per tenant)? Must it be unique per tenant? | PO / Operations |
| [H-DS-7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md#whats-still-unknown) | `US-FND-DSR-001` | MQTT topic root, when used (optional): one value per tenant, per environment (test/production), or per broker? Any naming rule? | PO / Operations |
| [A-16](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#98-open-questions) | `US-FND-DSR-001` | Default kind and default tag name each role pre-fills on the tag form (also: tag template per machine type — future improvement, not a blocker). | `foundation-domain` / PO |
| [H-DS-6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md#whats-still-unknown) | `US-FND-DSR-001` | Who may export the tag hand-off list: covered by `foundation:dsr:site:view`, or its own action in the permission catalog? | PO |
| [A-11](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#98-open-questions) | `US-FND-DSR-001` (AC-16) | Tag hand-off list: file format (CSV, OPC UA NodeSet XML, gateway import)? Is each export kept? Does a tag need a "configured on the server" status, so a newly generated address isn't treated as live (and read failures aren't raised as faults) until hardware confirms? | PO / Operations |
| [A-12](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#98-open-questions) | `US-FND-DSR-001` (AC-12) | Confirm: a machine with no active placement has no generated address and its tags aren't read. Does the hand-off list show such tags as "not placed" or leave them out? | PO / Operations |
| [A-13](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#98-open-questions) | `US-FND-DSR-001` (AC-10) | Keep the machine's floor tag as the address segment (refusing generated naming for tags like `IM-003`), or add a separate segment code on the asset? | PO / Operations |
| [A-14](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#98-open-questions) | `US-FND-DSR-001` (AC-15) | Changing address naming or namespace on a source with tags (and renaming any code in the path): warn with the count and confirm (current), or block? | PO |
| [A-15](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#98-open-questions) | `US-FND-DSR-001` (rule 12) | Multi-lane machine-wide signals: one address under one assigned work unit (default), or the gateway aliases the register under each lane? Does any real machine have a per-lane stop signal? | PO / Operations |
| [UX-DSR-1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/13-data-source.md#open-questions) | `US-FND-DSR-001` | ~~Is the tag list only a pane under the selected source, or also reachable from the machine's asset form (UX 04)?~~ **Resolved 2026-09-28 (PO):** its own page, Tags (`SCR-FND-DSR-002`), also opened from the asset form and the KPI side panel. | PO |
| [UX-DSR-2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/13-data-source.md#open-questions) | `US-FND-DSR-001` | ~~Where the hand-off list export lives: per data source only, or also per site / per work center?~~ **Resolved 2026-09-28 (PO):** on the Tags page, exporting the filtered rows (per source, site or work center). File format stays open under A-11. | PO / Operations |
| New tag roles and `count_side` on the tag form | `US-FND-DSR-001` | F-04 §9.7 added `count_side` and the roles `lot_marker` / `activity_signal` (2026-09-30 / 10-01). UX 13 has no field, label or screen text for them, and F-04 §9.7.1 rule 9 doesn't say whether the two roles are per-lane or machine-wide. `TBD — needs PO confirmation` | PO / `foundation-domain` |
| Screen text `(draft)` | `US-FND-DSR-001` | Most rows in the *Screen text* table are drafts waiting for PO review (UX T-3a). | PO |

## Stories


| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-DSR-001`](#us-fnd-dsr-001) | Declare a data source connection and map its tags | 🔴 Blocked | As a **Plant Admin/IT (internal implementor)**, I want to declare a machine's connection and map its tags to assets & signal types through configuration, so a new machine isn't a developer task. → [detail](#us-fnd-dsr-001) | `US-FND-AST-002`, `US-FND-AST-003`, `US-FND-CFG-001` |

## Detail blocks

---

#### US-FND-DSR-001

**Declare a data source connection and map its tags**

**Status:** 🔴 Blocked — depends on `US-FND-CFG-001`, whose CFG entities are not modelled, so it cannot start; AC-3 and AC-5 (and the interpreted part of AC-1) are also blocked on the transformation gap.

> **In short:** the implementor declares a machine's connection and maps its tags to assets and signal types,
> as data, not code. The implementor types only the **tag name**; on an OPC UA connection that builds addresses
> from location, the address is **generated** from where the machine is placed, and MolcaDx exports a tag list
> the hardware team configures the server to match. A "Test read" shows the raw value, so a new machine is not
> a developer task.

> **Two open gaps (`TBD — perlu konfirmasi foundation-domain`):** the **transformation gap** — no `scale`/`offset`/`interpretation_rules` field exists, and H-DS-1 heartbeat granularity ([Data Source §8](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md#whats-still-unknown)).
> Sections below are marked wherever they depend on the transformation gap; full questions are in the Calculation and Data sections.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to declare a machine's connection and map its tags to
assets & signal types through configuration, so a new machine isn't a developer task.

**2. Context**

- **Why:** machines speak in tags (`PLC1.DB10.DBW4`, `Total_Count`); the system speaks in assets and states
  (machine PCK01 is running). This bridge is split across two things, named exactly in section 5:
  - the **connection** ([F-13 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md#91-data_source--one-connectionendpoint))
    declares which PLC, OPC UA server or MQTT broker to talk to;
  - the **tag declaration** ([F-04 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#97-asset_tags--per-asset-tag--signal-declaration))
    says which tag on that connection means what, for which machine.
- **Who names the address (PO decision 2026-09-25,
  [F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address)):** on an OPC UA connection set to **generated**
  naming, Molca owns the OPC UA side. MolcaDx builds each tag's address from the machine's place in the
  hierarchy; the hardware person configures the gateway/server to expose exactly those addresses from a
  **tag hand-off list**. The generated address is the source of truth; the server matches it, never the
  reverse. **Since 2026-09-28 (PO) MQTT can be generated too:** the topic is built the same way, one value per
  topic (acquisition is Telegraf). Hand-typed addresses (**manual**) stay for a machine's or vendor's own OPC UA
  server whose node names are fixed, and for MQTT topics a device publishes in its own structure (or several
  signals in one message). The choice is per connection, never per tag.
- **Multi-lane machines:** one machine can be placed on several stations, one per lane, each with its own
  counters (`Lane1_Total_Count`, `Lane2_Total_Count`). Each per-lane tag must say which lane (station) it
  counts in ([F-04 §9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#96-asset-to-work-unit-placement-rules)).
- **Not F-11:** [F-11 Integration](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md) is ERP/system-to-system
  only and has no role in this story.
- **KR:** this is what "dynamic JSON metadata" in KR 1B.1 refers to: a new machine's tags are declared as data,
  not code.
- **Who and where:** the implementor, on desktop, in long technical sessions.

**3. Expectation**

*Layout* ([UX 13 `SCR-FND-DSR-001`, `SCR-FND-DSR-002`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/13-data-source.md#screens))
- Sidebar group **Connections**, items **Data sources** and **Tags** — not the Reference data group: a data source
  carries connection secrets and a live status.
- **Two pages (PO, 2026-09-28, [UX 13 § Tags page](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/13-data-source.md#tags-page-po-decision-2026-09-28)).** MolcaDx only
  **declares** the tags; the hardware person configures the OPC UA server elsewhere, from the hand-off list. So the
  declared tags have their own page. **Data sources** (`SCR-FND-DSR-001`) keeps the connections only; each source
  row shows its tag count, linking to Tags filtered to that source. **Tags** (`SCR-FND-DSR-002`) lists every
  declared tag in the site, across all data sources, and holds tag create / edit / Test read and the export.
- A **Data Source list**: the connection's name, protocol, **address naming** (generated or typed) and a short
  summary of its connection settings. A "last seen" time may appear as a **placeholder** diagnostic only,
  **not for alerting** while H-DS-1 is open.
- **Source form:** address naming — "Build from location" is offered for OPC UA **and MQTT** and is the
  **default** for a new source; "Type each address" is the exception. For OPC UA, the namespace URI (required
  when addresses are built from location). For MQTT built from location, an **optional** topic root:
  "Topic root (optional)" (draft) (`dsr-001.sources.topic-root`), hint "Leave empty if only Molca publishes to
  this broker. Fill it in if the broker is shared, for example with molca." (draft)
  (`dsr-001.sources.topic-root.hint`).
- On the **Tags** page, the **Asset Tags list** (all sources in the site): the tag name, the address, which machine the tag
  belongs to, what role the signal plays, how to read its value, and — where it applies — its unit or counting
  basis — plus its work unit and data source. Filters: data source, work center, machine, "No work unit",
  "Typed addresses" (draft) (`dsr-002.filter.typed`, tags on a manual source, so the exceptions can be reviewed); search by
  tag name or address. A "Last read" column and a "Never read" filter are `TBD` — they wait on H-DS-1 (whether
  `last_seen_at` exists per tag); until then they are not built. The Tags page is also opened, filtered to one
  machine, from the machine's asset form and from the KPI side panel's "No tag declared" link
  ([`US-FND-KPI-007`](07-kpi.md#us-fnd-kpi-007)).
- **Tag form** ([UX 13 § Declaring a tag](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/13-data-source.md#declaring-a-tag-po-decision-2026-09-28), 2026-09-28), fields in this order:
  1. **Data source** — decides whether the address is typed or generated;
  2. **Machine** — only machines in the source's site;
  3. **Role** (`tag_role`) — pre-fills the kind, and shows the unit / counting basis and reason fields only when
     the role needs them;
  4. **Tag name** — pre-filled from the role (e.g. role Total → `Total_Count`), editable;
  5. **Work unit** — only for a machine placed on more than one work unit (below).

  The pre-filled kind and tag name are suggestions the user can change. **Which kind and which tag name each role
  pre-fills: `TBD — foundation-domain`** ([F-04 A-16](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#98-open-questions)). On a **generated** source the address
  is a **live read-only preview** ("Address" (draft), `dsr-002.form.address-preview`) as the fields are filled; on
  a **manual** source it is a text field. A machine that is not placed shows no address (never a stale one) and
  its tag is not read.
- **"Missing for KPIs" panel** above the tag list: when a work unit in the filter has a bindable KPI slot with no
  binding **and** no tag on a machine placed there whose role is legal for that slot, one row per work unit × slot:
  "\<work unit\> · \<KPI\> needs a \<role\> tag" + button "Declare" (draft) (`dsr-002.missing.row`), under the heading
  "Missing for KPIs" (draft) (`dsr-002.missing.title`). Hidden when nothing is missing (`dsr-002.missing.none`,
  no text). **Declare** — and the KPI side panel's "Declare a tag" ([`US-FND-KPI-007`](07-kpi.md#us-fnd-kpi-007)) —
  open the tag form with the machine and role filled in; when the slot allows several roles, the role field lists
  only those.
- **Edit in one place:** tags are created and edited only on the Tags page. The asset detail shows a read-only
  Tags tab ([`US-FND-AST-002`](04-equipment-taxonomy.md#us-fnd-ast-002)).
- **Lane:** when the machine is placed on more than one station, the tag form shows a required **work unit**
  picker limited to the machine's active placements. A per-lane tag without a work unit is flagged and not read.
- A **"Test read"** button per tag shows **the raw value read from that tag's address**. See the transformation
  gap noted above: a transformed/interpreted value cannot be shown until that gap resolves.
- **Action — export tag hand-off list**, on the Tags page: exports **the rows the current filter shows** — per
  data source, per site or per work center. The file Molca's hardware person uses to configure the OPC UA server
  (format open, A-11).
- Readable within 3 seconds: how many data sources are declared and how many tags have never read a value (do
  not treat the "last seen" time as a committed health UI).

| State | What the user sees |
|-------|--------------------|
| Empty | `dsr-001.empty`: "No data sources yet." + "Automatic recording needs a data source to read machine signals." (draft). Tags page with no tag: `dsr-002.empty` "No tags yet." + "Declare a tag for each machine signal the KPIs need." (draft); filter matches nothing: `dsr-002.empty.filter` "No tags match these filters." (draft) |
| Loading | A skeleton (`dsr-001.loading`, no text). Test read shows a waiting state: `dsr-001.test-read.waiting` "Reading…" (draft) |
| Error | A failed test read shows the protocol message verbatim (a technical implementor needs detail, not a friendly message), plus likely causes: `dsr-001.error.test-read` |
| Offline | Unavailable: `dsr-001.offline` "You're offline. Data sources needs a connection to load and save changes." (draft) |
| Success | The tag declaration is saved and the test-read raw value appears (`dsr-001.success`, no text) |
| No permission | Read-only: `dsr-001.no-permission`. Connection credentials are **never** displayed to anyone (`dsr-001.credentials.hidden`) |

*Screen text* — copied word for word from [UX 13 `SCR-FND-DSR-001`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/13-data-source.md#scr-fnd-dsr-001--data-sources--tags) and [`SCR-FND-DSR-002`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/13-data-source.md#scr-fnd-dsr-002--tags). Since 2026-09-28 the `dsr-001.tags.*`, tag `dsr-001.error.*`, `dsr-001.action.test-read` and `dsr-001.action.export-handoff` rows render on the Tags page (keys unchanged).
The UX doc is the source; if this table and UX 13 differ, UX 13 wins. `(draft)` = proposed wording waiting for
PO review; builds may use it. `N/A — no text` = the state is visual only.

| Key | Where / state | Text |
|---|---|---|
| `dsr-sidebar.group` | Sidebar group label | "Connections" |
| `dsr-sidebar.item` | Sidebar item | "Data sources" |
| `dsr-001.title` | Page title | "Data sources" (draft) |
| `dsr-001.summary` | Counts: data sources declared, tags that never read a value | "\<n\> data sources · \<m\> tags have never read a value" (draft) |
| `dsr-001.sources.columns` | Source list columns: name, protocol, connection settings summary, last seen | "Name" · "Protocol" · "Connection" · "Last seen" (draft) |
| `dsr-001.last-seen.note` | Marker that "last seen" is diagnostic only | "For checking only. It doesn't raise alerts." (draft) |
| `dsr-001.sources.naming` | Source form: address naming field (generated / typed) and its two options | Field "Tag addresses" · options "Build from location" / "Type each address" (draft) |
| `dsr-001.sources.namespace` | Source form: namespace URI field (OPC UA only) | "Namespace URI" (draft) |
| `dsr-001.sources.topic-root` | Source form: topic root field (MQTT, "Build from location" only), optional | "Topic root (optional)" (draft) |
| `dsr-001.sources.topic-root.hint` | Under the topic root field | "Leave empty if only Molca publishes to this broker. Fill it in if the broker is shared, for example with molca." (draft) |
| `dsr-001.tags.columns` | Tag list columns: tag name, address, machine, signal role, how to read the value, unit / counting basis | "Tag name" · "Address" · "Machine" · "Role" · "Value type" · "Unit / counted per" (draft) |
| `dsr-001.tags.field.tag-name` | Tag form: tag name field label | "Tag name" (draft) |
| `dsr-001.tags.address.generated-hint` | Under the read-only address on a generated source | "Built from where the machine is placed. It changes if the machine moves." (draft) |
| `dsr-001.tags.address.not-placed` | Address cell when the machine is not placed | "No address — machine not placed" (draft) |
| `dsr-001.error.tag-name-chars` | Under Tag name: character outside letters, digits, `_` | "Use only letters, numbers and _ in the tag name." (draft) |
| `dsr-001.error.asset-tag-chars` | Save refused: the machine's tag has characters a generated address can't use | "Machine tag \<asset tag\> can't be used in an address because of \<character\>. Use only letters, numbers and _ in the machine tag." (draft) |
| `dsr-001.error.address-taken` | Save refused: another tag already has this address | "\<tag name\> on \<machine\> already uses this address." (draft) |
| `dsr-001.confirm.address-change` | Confirm when changing address naming or namespace on a source with tags | "This changes \<count\> tag addresses. The hardware team will need a new tag list." (draft) |
| `dsr-001.tags.field.lane` | Tag form: work unit picker, shown only for a machine placed on more than one work unit | "Work unit" (draft) |
| `dsr-001.tags.lane-missing` | Flag on a per-lane tag with no work unit on a multi-lane machine | "No work unit — this tag isn't counted." (draft) |
| `dsr-001.action.export-handoff` | Export tag hand-off list button — on the Tags page `SCR-FND-DSR-002` since 2026-09-28 | "Export tag list" (draft) |
| `dsr-001.action.test-read` | Test read button | "Test read" |
| `dsr-001.test-read.waiting` | Test read in progress | "Reading…" (draft) |
| `dsr-001.default` | Default state | N/A — no text: the lists above |
| `dsr-001.empty` | Empty state — no data source yet, plus a note that automatic recording mode needs one | "No data sources yet." + "Automatic recording needs a data source to read machine signals." (draft) |
| `dsr-001.loading` | Loading state | N/A — no text: skeleton |
| `dsr-001.error.test-read` | Test read failed — the protocol's own message word for word, plus likely causes | `<protocol message>` + "Test read failed" (heading) + "Check the address, the connection settings, and that the server can be reached." (draft) |
| `dsr-001.error.site-mismatch` | Save refused: the tag's machine is not in the connection's site | "\<machine\> is at \<machine site\>, but this data source is at \<source site\>. Choose a data source at \<machine site\>." (draft) |
| `dsr-001.offline` | Offline state — PRD: "Unavailable" | "You're offline. Data sources needs a connection to load and save changes." (draft) |
| `dsr-001.success` | Tag saved, test-read raw value appears | N/A — no text: the saved row and the raw value |
| `dsr-001.no-permission` | No permission — read-only | "You can view data sources and tags but not change them. Ask your Plant Admin/IT for access." (draft) |
| `dsr-001.credentials.hidden` | Where credentials would be | "Hidden" (draft) |
| `dsr-001.sources.tag-count` | `SCR-FND-DSR-001`, tag count link per source | "\<n\> tags" (draft) |
| `dsr-sidebar.item.tags` | Sidebar item | "Tags" (draft) |
| `dsr-002.title` | Page title | "Tags" (draft) |
| `dsr-002.columns` | Tag list columns | "Tag name" · "Address" · "Machine" · "Work unit" · "Role" · "Data source" · "Last read" (draft) |
| `dsr-002.never-read` | Last read cell, tag never read a value — only if H-DS-1 gives a per-tag `last_seen_at` | "Never read" (draft) |
| `dsr-002.filter.never-read` | Filter toggle — same H-DS-1 condition | "Never read" (draft) |
| `dsr-002.filter.no-work-unit` | Filter toggle | "No work unit" (draft) |
| `dsr-002.empty` | No tag declared in the site | "No tags yet." + "Declare a tag for each machine signal the KPIs need." (draft) |
| `dsr-002.empty.filter` | Filter matches no tag | "No tags match these filters." (draft) |
| `dsr-002.export.scope` | Next to Export tag list | "Exports the \<n\> tags shown." (draft) |
| `dsr-002.filter.typed` | Filter toggle | "Typed addresses" (draft) |
| `dsr-002.missing.title` | "Missing for KPIs" panel heading | "Missing for KPIs" (draft) |
| `dsr-002.missing.row` | One row in the panel | "\<work unit\> · \<KPI\> needs a \<role\> tag" + button "Declare" (draft) |
| `dsr-002.missing.none` | Panel hidden | N/A — no text: the panel is not shown |
| `dsr-002.form.address-preview` | Tag form, generated source, address preview label | "Address" (draft) |

**4. Calculation**

**Blocked on the transformation gap noted above.**

*How a raw reading becomes a usable value*
- The plan is: scale the raw reading, shift it, then map it to a named state.
- Example: a raw reading of 1,234 with a scale of 0.1 and no shift → 123.4, then mapped to its named state.

> [!note]- Exact formula (for developers)
> ```
> final_value = (raw_value × scale) + offset        then enum interpretation
> ```

- **`scale`/`offset`/`interpretation_rules` do not exist on `DATA_SOURCE` or `ASSET_TAGS`**, so this mechanism
  has no home in the real entities. Keep blocked TBD for `foundation-domain`; do not invent fields.
- **`TBD — perlu konfirmasi foundation-domain`:** does transformation get new fields on `ASSET_TAGS`, or is it
  resolved entirely by `kind` + `tag_role` (raw units always, no scale/offset needed) and the transformation
  language here is obsolete?

*What **can** be built without that decision: counter handling*
- A counter that only goes up (or adds per-interval deltas) is read as a difference: 12,450 now against a
  previous reading of 12,400 = 50 units of output.
- A backwards counter (12,450 → 0) is a reset event, not negative output.

> [!note]- Exact formula (for developers)
> ```
> output_delta = raw_value − previous_raw_value     for kind = cumulative_counter / delta_counter
> raw_value < previous_raw_value                    → counter reset event, never negative output
> ```

*How a generated address is built* ([F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address))
- Only for a connection with OPC UA and generated naming. The address is the namespace URI, then the codes of
  the machine's site, area, work center and station, the machine's floor tag, and the tag name, joined by dots.
- Example: packer PCK01 placed on `JKT1` › `AS` › `L01` › `S01`, tag `Total_Count` →
  `nsu=<namespace_uri>;s=JKT1.AS.L01.S01.PCK01.Total_Count`. The same tag on PCK02 at `S02` →
  `nsu=<namespace_uri>;s=JKT1.AS.L01.S02.PCK02.Total_Count`.
- Multi-lane: the station is the tag's own work unit. `Lane1_Total_Count` on `S01` →
  `…s=JKT1.AS.L01.S01.PCK01.Lane1_Total_Count`; `Lane2_Total_Count` on `S02` →
  `…s=JKT1.AS.L01.S02.PCK01.Lane2_Total_Count`; machine-wide `Machine_Run` assigned to `S01` →
  `…s=JKT1.AS.L01.S01.PCK01.Machine_Run`, which still applies to both lanes.
- Worked out when asked, from the placement active at that moment — never stored. A move gives the same tag a
  new address from the move date; readings stay keyed to the tag, so nothing already recorded changes.

> [!note]- Exact formula (for developers)
> ```
> generated_node = "nsu=" + DATA_SOURCE.namespace_uri + ";s=" +
>                  site_code + "." + area_code + "." + work_center_code + "." + work_unit_code + "." +
>                  ASSET.asset_tag + "." + ASSET_TAGS.tag_name
>   where the codes come from the ASSET_PLACEMENT row active at the time asked about
>         (WORK_UNIT → WORK_CENTER → AREA → SITE); for a multi-lane machine, WORK_UNIT = ASSET_TAGS.work_unit_id
>   only when DATA_SOURCE.protocol = opcua AND node_naming = generated
>
> generated_node (MQTT topic, since 2026-09-28) =
>                  [DATA_SOURCE.topic_root + "/"] +          -- only when topic_root is set (optional)
>                  site_code + "/" + area_code + "/" + work_center_code + "/" + work_unit_code + "/" +
>                  ASSET.asset_tag + "/" + ASSET_TAGS.tag_name
>   only when DATA_SOURCE.protocol = mqtt AND node_naming = generated; one value per topic
>
>   both: empty when node_naming = manual, or no active placement, or (multi-lane) work_unit_id has no active placement / is unset
> ```

- The namespace **URI** (`nsu=`) is used, never the numeric index (`ns=`): the index can change when the server
  restarts.
- The dotted path is a Molca convention; IEC 62541-3 fixes only the NodeId shape (namespace, identifier type,
  identifier value).

*What the tag hand-off list holds* ([F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address) rule 6)
- Per data source: every generated address with its tag name, signal role, value type, unit, counting basis,
  reason, machine floor tag, work unit, and the date the address takes effect. Never connection credentials.
- A new list is needed whenever addresses change (machine moved, code renamed, naming or namespace changed).

*Out of scope for this story*
- Writing readings into the machine stop log or the job execution records. F-13 does not store readings; those
  writes are downstream/PRODUCTION
  ([F-13](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md#what-this-module-is-for)). The exact entity
  names are in section 5.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Writes (connection)** | `DATA_SOURCE` ([F-13 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md#91-data_source--one-connectionendpoint)): **`site_id` (required)**, `name`, `protocol`, **`node_naming`** (`generated`/`manual`, required; `generated` allowed for `opcua` and, since 2026-09-28, `mqtt`; default `generated` for a new source), **`namespace_uri`** (OPC UA only; required when `protocol = opcua` and `node_naming = generated`, optional for a `manual` OPC UA source, empty for `mqtt`), **`topic_root`** (added 2026-09-28; MQTT only, **optional**; allowed only when `protocol = mqtt` and `node_naming = generated`, empty otherwise; same character rule), `connection_config` (the namespace is no longer in here), `heartbeat_interval`/`last_seen_at` as **placeholder only**, `valid_from`/`valid_to` |
| **Writes (tag declaration)** | `ASSET_TAGS` ([F-04 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#97-asset_tags--per-asset-tag--signal-declaration)): `asset_id`, `data_source_id`, **`work_unit_id`** (conditional: required when the machine has more than one active placement; otherwise optional and, if set, must equal the only placement's work unit), **`tag_name`** (required), `node` (**conditional**: required only when `DATA_SOURCE.node_naming = manual`, must be empty when `generated`), `kind`, `tag_role` (values since 2026-09-30/10-01 include `lot_marker` and `activity_signal`, below), `reject_reason_id`/`downtime_reason_id` when applicable, `uom_id`, `count_basis`, **`count_side`** (added 2026-09-30: `output` default / `input`; only for `total`/`good`/`reject`), `valid_from`/`valid_to` |
| **Derived, never written** | `ASSET_TAGS.generated_node` — not stored, not writable; computed on read (Calculation) |
| **Reads** | `ASSET` (`asset_tag`), `ASSET_PLACEMENT` (active rows), `WORK_UNIT`, `WORK_CENTER`, `AREA`, `SITE` (the `*_code` fields for the address), `DOWNTIME_REASON`, `REJECT_REASON` |
| **Not written by this story** (out of FOUNDATION DSR scope) | `ASSET_STATE_LOG`, `WORK_ORDER_OPERATION`. Readings land downstream/PRODUCTION; F-13 does not store them |

- **`heartbeat_interval`/`last_seen_at` granularity:** `TBD — perlu konfirmasi foundation-domain` per H-DS-1:
  connection-level vs per-tag. Not a committed health/alerting schema. It blocks `US-FND-INT-003`'s status
  classification, not this story's own ACs.
- **`ASSET_TAGS.work_unit_id` points at the work unit, not at an `ASSET_PLACEMENT` row:** placement rows close
  and reopen (swap, correction) while the lane itself does not change.
- **An asset move does not need a new `ASSET_TAGS` row:** the same `tag_id` gets a new `generated_node` from the
  move date. A new row is still needed when a hand-typed `node` changes, `tag_name` changes, or a role is
  reassigned.
- **Two new tag roles** ([F-04 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#97-asset_tags--per-asset-tag--signal-declaration)): **`lot_marker`** (2026-09-30) — kind `boolean`, a level
  signal: 1 while a batch / lot is open, 0 when closed (rising edge = start, falling edge = end). Optional; it only
  marks boundaries and never feeds a KPI value. **`activity_signal`** (2026-10-01) — kind `value`, unit on `uom_id`
  (motor current, voltage, vibration, pressure); shows whether the machine is working and feeds Availability only,
  through range detection.
- **`count_side`** (2026-09-30): `input` = the counter sits on the material going in (e.g. dough fed to the mold);
  KPI converts it to output with the step ratio and marks it derived. A measured output count always wins.
- **Gap, not invented here:** no field for `scale`/`offset`/`interpretation_rules` exists on either written
  entity. See the note above section 1. Do not add these fields without a `foundation-domain` decision.

**6. Rules & constraints**

1. Declaring a new tag row (a new physical signal on an already-legal signal role) must not require a code
   change; this is what F-04 actually supports. Adding a brand-new signal role **does** require a change,
   because signal roles are a closed, seeded list
   ([F-04 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#97-asset_tags--per-asset-tag--signal-declaration);
   [glossary "Tag role"](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/01-glossary.md)).
2. Credentials are **not** stored in plaintext inside the connection settings and are never displayed.
   Mechanism `TBD`, H-DS-2.
3. **No write access to PLCs** ([§4](README.md)).
4. Connections are initiated outward from the more secure zone.
5. Unknown values are never guessed. **Blocked on the transformation gap:** there is no interpretation table
   to check a value against yet.
6. **Access:** two different scope tiers for the two things this one story writes.
   - Creating, updating or deactivating a **connection** is scoped at `site` (the connection's required site
     foreign key — [authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#data-source-dsr--13-data-sourcemd-9)).
   - Creating, updating or deactivating a **tag declaration** is scoped at `work_unit` (inherited via the
     machine's current placement — same catalog, Equipment Taxonomy section).
     For a multi-lane machine the scope is the tag's own work unit
     ([authorization catalog](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#equipment-taxonomy-ast--04-asset-ontologymd-9), 2026-09-25).
   - A save-time validation: a tag declaration's machine must sit in the same site as the connection it points
     at ([13-data-source.md §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md#91-data_source--one-connectionendpoint)).
     Refused with `dsr-001.error.site-mismatch`.
   - Who may export the tag hand-off list: `TBD — needs PO confirmation` (H-DS-6) — site view access, or its
     own action?
7. **Address naming per connection** ([F-13 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md#91-data_source--one-connectionendpoint)): generated
   naming for OPC UA and MQTT, default for a new source; manual for a vendor/machine server with fixed node names
   or MQTT topics a device publishes itself. A generated OPC UA source needs a namespace URI; a generated MQTT
   source may have a topic root (optional). Namespace URI format: `TBD — needs PO/Operations confirmation`
   (H-DS-5). Topic root value when used (per tenant, per environment, per broker?): `TBD — needs PO/Operations
   confirmation` (H-DS-7). **MQTT uniqueness:** among active tags on sources sharing the same broker and the same
   `topic_root` (empty counts as one value), no two may produce the same topic — refused with
   `dsr-001.error.address-taken`.
8. **Tag name:** letters, digits and `_` only (no `.`, it is the last address segment); refused with
   `dsr-001.error.tag-name-chars`. Unique per machine among active tags.
9. **Address field:** typed only on a manual source; must stay empty on a generated source (the address is
   generated and shown read-only).
10. **Machine floor tag charset** ([F-04 §9.7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#971-generated-opc-ua-node-address)): checked when a tag is
    saved against a generated source. A floor tag with other characters (e.g. `IM-003`) blocks generated naming
    for that machine's tags; save refused with `dsr-001.error.asset-tag-chars`, naming the character. Open: A-13.
11. **Unique on the server:** among active tags whose sources share the same namespace URI, no two may produce
    the same address. Checked at tag save, placement save and code rename; refused with
    `dsr-001.error.address-taken`, naming the tag that already uses it.
12. **Multi-lane machine** ([F-04 §9.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#96-asset-to-work-unit-placement-rules), §9.7.1 rules 5 and 9):
    - The work unit picker offers only the machine's active placements; required when there are two or more.
    - Per-lane roles (product code, total, good, reject, reject reason, speed) count **only** in the tag's own
      work unit. Machine-wide roles (machine state, downtime reason, runtime) apply to **every** lane; for them
      the work unit only places the single generated address (open: A-15).
    - Whether the new roles `lot_marker` and `activity_signal` are per-lane or machine-wide: `TBD — needs PO
      confirmation` (F-04 §9.7.1 rule 9 lists only `machine_state`, `downtime_reason`, `runtime` as machine-wide).
    - A per-lane tag without a work unit, or whose work unit no longer has an active placement, has no address,
      is not read, and is flagged (`dsr-001.tags.lane-missing`) — never counted in every lane.
    - These rules apply to manual sources too: they decide where a tag counts, not only its address.
13. **No placement, no address:** a machine with no active placement has no generated address; its tags are
    declared but not read. Shown as `dsr-001.tags.address.not-placed`, never a stale address. Open for
    confirmation: A-12.
14. **Changing address naming or namespace** on a source with tags changes every tag's address: the save shows
    how many addresses change and asks for confirmation (`dsr-001.confirm.address-change`); afterwards a new
    hand-off list is needed. Warn-and-confirm vs block: `TBD — needs PO confirmation` (A-14).
15. **The generated address is the source of truth:** the hand-off list is how the hardware side is told what
    to expose; the server is configured to match it, never the reverse. Until hardware configures new addresses,
    reads on them fail. Whether a tag carries a "configured on the server" status, the file format and whether
    exports are kept: `TBD — needs PO/Operations confirmation` (A-11).

**7. Acceptance criteria**

- **AC-1 (test read) — partially blocked.** **Given** a connection is live and a tag is declared on machine
  PCK01, **when** the implementor presses "Test read", **then** the raw value appears.
  **Showing the interpreted state side by side cannot be tested until the transformation gap resolves.**
- **AC-2 (no code)** **Given** a new physical signal needs declaring on a machine that already uses a legal
  signal role, **when** the implementor adds a tag declaration for it, **then** the value is stored and
  readable with no code change and no schema migration. **Declaring an entirely new signal role is out of scope
  for "no code change" — see rule 1.**
- **AC-3 (unknown value) — blocked.** Cannot be tested until the transformation gap resolves; there is no
  interpretation table to check an unrecognized value against.
- **AC-4 (counter reset)** **Given** the machine counter drops from 12,450 to 0, **when** the next reading is
  processed, **then** no negative output is recorded and the reset event is recorded separately.
- **AC-5 (validation) — blocked.** Depends on a `scale` field that does not exist on either written entity.
- **AC-6 (security)** **Given** the connection settings hold credentials, **when** the implementor opens the
  connection's detail, **then** the credentials are not displayed in any form.
- **AC-7 (permission)** **Given** a user without site access to the connection's site or without work-unit
  access to the tag declaration's work unit, **when** they try to open the respective screen, **then** access
  is denied with an explanation.
- **AC-8 (generated address)** **Given** an OPC UA source with generated naming and PCK01 placed on
  `JKT1` › `AS` › `L01` › `S01`, **when** the implementor saves tag `Total_Count` on PCK01, **then** no address is
  typed and the address shows read-only as `nsu=<namespace_uri>;s=JKT1.AS.L01.S01.PCK01.Total_Count`, with the
  hint `dsr-001.tags.address.generated-hint`.
- **AC-9 (tag name)** **Given** the tag name `Total.Count`, **when** saved, **then** it is refused with
  "Use only letters, numbers and _ in the tag name." (`dsr-001.error.tag-name-chars`).
- **AC-10 (machine floor tag)** **Given** machine `IM-003` and a generated source, **when** a tag is saved on it,
  **then** it is refused with `dsr-001.error.asset-tag-chars` naming `-`. **Contingent on A-13.**
- **AC-11 (address taken)** **Given** two sources share one namespace URI and a tag on one already produces
  address X, **when** a tag that would produce X is saved, **then** it is refused with
  `dsr-001.error.address-taken` naming the tag and machine already using it.
- **AC-12 (not placed)** **Given** a machine with no active placement, **when** its tags are listed on a
  generated source, **then** the address cell reads "No address — machine not placed"
  (`dsr-001.tags.address.not-placed`) and the tag is not read. **Contingent on A-12.**
- **AC-13 (lane)** **Given** PCK01 is placed on `S01` and `S02`, **when** the implementor adds tag
  `Lane2_Total_Count`, **then** a required "Work unit" picker (`dsr-001.tags.field.lane`) offers only `S01` and
  `S02`; a per-lane tag saved earlier without a work unit shows "No work unit — this tag isn't counted."
  (`dsr-001.tags.lane-missing`) and is not read.
- **AC-14 (MQTT naming)** **Given** a new MQTT source, **when** the implementor opens its address naming,
  **then** both "Build from location" (selected by default) and "Type each address" are offered. *(Reworded
  2026-09-28: until then MQTT was manual only.)*
- **AC-15 (naming change)** **Given** a source with 12 active tags, **when** its address naming or namespace URI
  is changed, **then** the save first asks "This changes 12 tag addresses. The hardware team will need a new
  tag list." (`dsr-001.confirm.address-change`, draft). **Contingent on A-14.**
- **AC-16 (hand-off list)** **Given** a generated source with tags, **when** the implementor filters the Tags page
  to that source and presses "Export tag list" (`dsr-001.action.export-handoff`), **then** the text next to it reads
  "Exports the \<n\> tags shown." and the file lists every generated address among the rows shown with tag
  name, role, value type, unit, counting basis, reason, machine floor tag, work unit and effective date, and no
  credentials. **File format contingent on A-11.**
- **AC-17 (site)** **Given** machine PCK01 is placed in site `JKT1` and the source is in another site, **when**
  a tag for PCK01 is saved on that source, **then** it is refused with `dsr-001.error.site-mismatch`.
- **AC-18 (Tags page across sources)** **Given** site `JKT1` has two data sources with 5 and 7 tags, **when** the
  implementor opens **Tags** from the Connections group, **then** all 12 tags are listed with their data source;
  on **Data sources**, the two rows show "5 tags" and "7 tags", and clicking "5 tags" opens Tags filtered to that
  source. **Given** a filter that matches no tag, **then** "No tags match these filters." shows.
- **AC-19 (MQTT topic)** **Given** an MQTT source built from location with no topic root and machine PCK01 placed
  on `JKT1` › `AS` › `L01` › `S01`, **when** tag `Total_Count` is declared, **then** its address preview reads
  `JKT1/AS/L01/S01/PCK01/Total_Count`; **given** topic root `molca`, **then** it reads
  `molca/JKT1/AS/L01/S01/PCK01/Total_Count`.
- **AC-20 (Missing for KPIs)** **Given** work unit `WU_02` has a bindable Performance slot with no binding and no
  tag with a legal role on its machine, **when** the implementor opens Tags, **then** the "Missing for KPIs" panel
  shows "WU_02 · Performance needs a \<role\> tag" with "Declare"; pressing it opens the tag form with the machine
  and role filled in. **Given** nothing is missing, **then** the panel is not shown.
- **AC-21 (typed filter)** **Given** a site with one generated and one manual source, **when** the implementor
  turns on "Typed addresses", **then** only the manual source's tags are listed.

**8. Metrics & events**

- **Metric:** `waktu_tambah_tipe_aset_baru` (*time to add a new machine type*): how long it takes an implementor
  to add one machine + new tag until its **raw** value is readable (interpreted-value timing blocked on the
  transformation gap), **with no code change and no migration**. This is the measurable form of KR 1B.1
  ([Goals Q3 §6.2](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/goalsQ3.md#62-kr-belum-terukur)). Baseline `not yet measured`; target
  **`[proposed]` ≤30 minutes**. Source: `asset_tag_created` → `tag_first_value_received`.
- **Counter-metric:** % of tags that have ever sent an unknown value. **Not measurable until the transformation
  gap resolves.**
- **Events:** `data_source_created` (`protocol`) · `asset_tag_created` (`tag_role`, `asset_id`) ·
  `tag_first_value_received` (`setup_duration_sec`) · `tag_unknown_value` (`raw_value`). **The last event is
  blocked on the same gap.**

**Dependencies:** `US-FND-AST-002`, `US-FND-AST-003` (a generated address and the lane picker read the machine's active placements), `US-FND-CFG-001`

## Change notes (history — not needed to build)

- Module `DSR` — 2026-09-04 (GitHub issue #20): new module, split out of Integration & Service Monitoring. See [§6.8](11-integration.md)'s intro note and `US-FND-INT-001`'s closure note. The one-screen `DATA_SOURCE` + `ASSET_TAGS` workflow is the same one `US-FND-INT-001` described before the split.
- `US-FND-DSR-001` — 2026-09-04 (GitHub issue #20): moved from `US-FND-INT-001` verbatim; only the ID, module code, and cross-references changed. The transformation gap and H-DS-1 were already open in `US-FND-INT-001` and moved with it. The transformation formula was what the story originally assumed.
- `US-FND-DSR-001` — 2026-09-03 (decision B7): the machine-to-entity bridge split across `DATA_SOURCE` and `ASSET_TAGS`.
- All stories (2026-09-23): detail blocks rewritten from one dense table into numbered sections, and
  "subject whose IDP-asserted scope claim covers …" replaced by glossary access terms (e.g. *site access*, defined in the README glossary).
  Meaning unchanged; UI copy kept verbatim.
- All stories (2026-09-23, second pass): rewritten in plain language. Entity and field names now appear only in
  section 5 and in the collapsible "Exact formula (for developers)" boxes (plus schema-level `TBD` callouts for
  `foundation-domain`); everywhere else things are named in plain words (connection, tag declaration, machine,
  raw reading). Meaning unchanged; UI copy, story IDs and event names kept verbatim. The counter-reset rule
  gained its own formula box (it was an unwritten rule before).
- `US-FND-DSR-001` — 2026-09-27 (prd-sync docs-molcadx 3cb17fa..7ad3ed8; PO decision 2026-09-25 generated OPC UA tag addresses, F-04 §9.6/§9.7/§9.7.1, F-13 §9.1, new UX 13): new fields `DATA_SOURCE.node_naming`/`namespace_uri` and `ASSET_TAGS.tag_name`/`work_unit_id`; `node` now conditional (manual sources only); `generated_node` derived read-only; address formula with worked example; tag hand-off list export; multi-lane lane picker and per-lane vs machine-wide rules; address uniqueness and charset refusals; rules 7–15, AC-8…AC-17. Expectation now cites `SCR-FND-DSR-001` in the **Connections** sidebar group and carries the UX 13 *Screen text* table word for word; the old Empty text `"belum ada data"` (cited from UX 00 while no DSR UX file existed) is replaced by `dsr-001.empty`. Dependency `US-FND-AST-003` added. Open items A-11…A-15, H-DS-5, H-DS-6, UX-DSR-1/2 added. Status unchanged (🔴, `US-FND-CFG-001` and transformation gap).
- `US-FND-DSR-001` — 2026-09-28 (prd-sync docs-molcadx dd0a64e..0dea5c3; PO decision, UX 13 § Tags page): tags
  get their own page **Tags** (`SCR-FND-DSR-002`) in the Connections group; Data sources keeps connections + tag
  count. Export moves to Tags and exports the filtered rows. Layout, states and screen text updated (new
  `dsr-002.*`, `dsr-sidebar.item.tags`, `dsr-001.sources.tag-count`); AC-16 updated, AC-18 added. UX-DSR-1 and
  UX-DSR-2 closed. "Last read" column `TBD` on H-DS-1.
- `US-FND-DSR-001` — 2026-09-28 (prd-sync docs-molcadx 0dea5c3..147e8d2; PO decisions, UX 13 § Declaring a tag,
  F-13 §9.1, F-04 §9.7.1): MQTT topics can be generated (optional `topic_root`); generated is the default for new
  sources, typed kept for fixed vendor servers. Tag form reordered with role pre-fill (defaults `TBD`, A-16),
  live address preview, "Typed addresses" filter, "Missing for KPIs" panel, edit only on Tags. AC-14 reworded,
  AC-19–21 added; H-DS-7 and A-16 added to open items.
- `US-FND-DSR-001` — 2026-10-01 (prd-sync docs-molcadx 147e8d2..787e819; F-04 §9.7, issue #58, 2026-10-01 wrap-up):
  `ASSET_TAGS.count_side` added to the tag writes; new roles `lot_marker` and `activity_signal` described. New `TBD`
  and open item: per-lane vs machine-wide for the new roles, and no UX 13 field/text for them yet. Status unchanged (🔴).
