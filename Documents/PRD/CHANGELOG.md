# CHANGELOG — MolcaDx PRD

Newest entry first. Log every PRD change here. Log the spec decision that caused it in
[`docs-molcadx/LOGS/CHANGELOG.md`](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/CHANGELOG.md).

---

## 2026-10-01 — prd-sync docs-molcadx c1f1848..30c7087 (PRD-003, PRD-004)

Sync range `docs-molcadx@c1f1848` → `@30c7087` (`origin/main`): docs-molcadx #73 (UX 08 keys linked to `WMS-006`) and #74 (keys on
every table, all domains). Added to the open PR #23.

- **Engineering summaries** (PRD-003 §7.2, PRD-004 §2): note on the F-00 keys rule — build from each entity's Keys line; E-1 open.
- **New PKs in Writes:** `US-FND-REF-003` (`downtime_reason_asset_id`, `reject_reason_asset_id`), `US-FND-AST-003` (`asset_placement_id`,
  validity type `TBD` A-19), `KPI_RESULT` stories (`kpi_result_id`; `inputs_used` / `waterfall` storage `TBD` K-12).
- **`ASSET_STATE_LOG` row ID** now `asset_state_log_id`: B5 wording fixed in PRD-003 (KPI open items, KPI-001/005, §7.2, release, README) and
  PRD-004 (MON open items, MON-003, cross-cutting, release, README, PRF).
- **Open items added** (spec keys questions): SIT S-8, S-9 · SHF H-8 · FYR Y-3 · AST A-17–A-19 · REF T-7, T-8 · PRO P-8, P-9 · KPI K-12–K-15 ·
  WMS W-18, W-19 · AUD AU-14 · PLN PLN-Q10–Q12 · MON SL-5; E-1 in PRD-003 open questions. No status changed.
- **Unmatched:** MAINTENANCE changes (`asset_component_link_id`, `pm_procedure_step_id`, AH-1, WM-5, WM-6) — no MAINTENANCE PRD (rule 1).

---

## 2026-10-01 — New story US-FND-WMS-006 + prd-sync docs-molcadx e3ba785..c1f1848 (PRD-003)

Sync range `docs-molcadx@e3ba785` → `@c1f1848` (`origin/main`): docs-molcadx #71 (UX 08 full spec for the Product codes
section) and #72 (product codes per asset; several per product). PRD-004: no impact, sync point moved.

- **New story `US-FND-WMS-006` — Map machine product codes to a product** (PO request): the Product codes section of the
  Routing tab (`SCR-FND-WMS-007`). Layout per work unit and tag, rows and status, add / end / delete-future, exact flag rules,
  "Seen but not mapped", mapping into the past (recalculation + warning), overlap on `(asset_id, raw_value)`, 11 ACs.
  🟡 Partly blocked: AC-11 waits on Q-33 (UX-WMS-4); two flows on Q-34 (UX-WMS-5).
- Work Master open items, `WMS-003` context, README (53 stories; 24 🟢 · 13 🟡 · 6 🔴 · 10 🟠), release F4, Q-33/Q-34, decision log.
- **Unmatched:** none.

---

## 2026-10-01 — prd-sync docs-molcadx c932974..e3ba785 (PRD-003)

Sync range `docs-molcadx@c932974` → `@e3ba785` (`origin/main`): docs-molcadx #70 (UX 08 screen text for the step detail).
PRD-004: no impact, sync point moved.

- **`US-FND-WMS-002`** 🟡 → 🟢 — dispensing tolerance text and keys (`wms-002.line.tolerance*`, `wms-002.error.tolerance-*`) in
  Expectation, the save-blocked state and AC-18.
- **`US-FND-WMS-003`** 🟡 → 🟢 — instructions, checks, yield and hold time text and keys (`wms-003.instr.*`, `.checks.*`,
  `.yield.*`, `.hold.*`) in Expectation, states and AC-26/28/30. README totals back to 24 🟢 · 12 🟡 · 6 🔴 · 10 🟠.
- **Unmatched:** none.

## 2026-10-01 — Work Master step detail into WMS-002 / WMS-003 (PRD-003)

Catches up F-08's EBR step detail (docs-molcadx #66, already inside the `787e819..725362a` range). The earlier sync wrongly
listed it as unmatched; the docs-molcadx changelog had already put these two stories on the sync backlog. Sync point unchanged.

- **`US-FND-WMS-002`** 🟢 → 🟡 — dispensing tolerance on a BOM line: context, allowed-range formula with example
  (75.16 kg → 74.41–75.91 / 74.96–75.36 kg), rule 10, AC-16–AC-19. Change on a released BOM = new version (W-17 open).
- **`US-FND-WMS-003`** 🟢 → 🟡 — instructions, in-process checks, yield range (formula, 95.2% example, edge cases, save
  warning) and hold time (successor rules, example); rules 17–20, AC-24–AC-30, event `operation_step_detail_saved`.
- Screen text for all of it `TBD` until UX 08 is designed. Open items W-10–W-17 added. README totals 22 🟢 · 14 🟡 · 6 🔴 · 10 🟠.

## 2026-10-01 — prd-sync docs-molcadx bf12721..c932974 (PRD-003)

Sync range `docs-molcadx@bf12721` → `@c932974` (`origin/main`): docs-molcadx #69 (PO: keep the gap count, UX-SHF-3 closed).
PRD-004: no impact, sync point moved.

- **`US-FND-SHF-002`** — gap count "\<n\> gap(s) in this range" (`shf-003.gap-count`) next to the horizon marker, hidden at 0,
  opens the generate control; AC-9; open item UX-SHF-3 closed.
- **Unmatched:** none.

## 2026-10-01 — prd-sync docs-molcadx 725362a..bf12721 (PRD-003)

Sync range `docs-molcadx@725362a` → `@bf12721` (`origin/main`): docs-molcadx #67 (UX 01/02, PO decision on the usability
audit from PR #22). #68 was skills/tooling only, no spec change. PRD-004: no impact, sync point moved.

- **`US-FND-SHF-002`** — new state "No shift hours" (`shf-003.empty.no-hours`): a site without `SITE_SHIFT` rows is not
  offered generation, it links to its Shift panel; edge case, rule 8, AC-8. Open item UX-SHF-3 (gap count) added.
- **`US-FND-SIT-001`** — search box (`sit-001.search`, `.search.clear`, `.empty.no-match`) in all three views, Tree keyboard
  navigation, state "No search match", AC-13–AC-15, event `hierarchy_searched`.
- Replaces prd-molcadx PR #22 (closed): its findings now come from UX, not from the frontend.
- **Unmatched:** none.

## 2026-10-01 — prd-sync docs-molcadx 787e819..725362a (PRD-003, PRD-004)

Sync range `docs-molcadx@787e819` → `@725362a` (both 2026-10-01, `origin/main`): docs-molcadx #64 (Q-28 manual reject
in any unit; K-6/K-8 stop thresholds) and #66 (Work Master EBR step detail). Stacked on the still-open sync PR #20.

- **`US-FND-KPI-003`** 🟡 → 🟢 — Q-28 resolved: manual reject in any unit with a conversion path, Quality calculates in
  `OPERATION.uom_id`; machine total + manual reject is an allowed pair (`source_tier = manual`); new "No conversion for
  this unit" case; AC-18 testable, AC-19/AC-20 added.
- **`US-FND-KPI-009`** — reads the converted manual reject, as `KPI-003`.
- **`US-FND-KPI-006`** 🔴 → 🟡 — K-6 resolved: `min_stop_seconds` 60 s / `small_stop_max_seconds` 300 s, per work center
  with work unit override, effective-dated. Three-way bucketing, AC-7/AC-8 testable, AC-10–AC-12 added. Still open: K-11
  (Q-31) and threshold storage (Q-32).
- **`US-FND-KPI-008`** — threshold part of K-8 resolved (one shared pair); AC-3 now waits on K-10 (band ownership, Q-26).
- **`US-FND-CFG-001`** — stop thresholds recorded as the first confirmed setting; Q-10 partly answered.
- PRD-003 open items, README counts (24 🟢 · 12 🟡 · 6 🔴 · 10 🟠), release F5/F7, Q-21/Q-26 reworded, Q-28 moved to
  resolved, Q-31/Q-32 added, event `downtime_detected_from_stall` fields, decision log row.
- **PRD-004** `MON-006`, `PRF-001`, M-04, R-07: renamed F-07 anchor, manual reject in any unit, new display case.
- **Unmatched:** F-08 EBR step detail (`OPERATION_INSTRUCTION`, `OPERATION_CHECK`, yield range, hold time, dispensing
  tolerance; W-10–W-17) — no story, no UX yet; logged as a WMS open item for the PO.
- **Ignored (no PRD impact):** F-00 entity map row, F-05 `downtime_category` note, FLOW-04 diagram wording.

---

## 2026-10-01 — prd-sync docs-molcadx 147e8d2..787e819 (PRD-003, PRD-004)

Sync range `docs-molcadx@147e8d2` (2026-09-28) → `@787e819` (2026-10-01): docs-molcadx #55 (rendering only, ignored),
#57 (state log SL-1 / SL-4), #59 (Work Master decisions, issue #58 waves 1–2, open-items wrap-up).

**PRD-003 FOUNDATION** — totals now **23 🟢 · 12 🟡 · 7 🔴 · 10 🟠 = 52** (was 20 · 15 · 7 · 10).
- `US-FND-PRO-001`: five product types + packaging level, BOM / Routing tabs, hidden without Production license, `ready()`
  joins `ROUTING` and `OPERATION_WORK_UNIT`. New open question Q-27 (draft routings and readiness).
- `US-FND-PRO-002` 🟡 → 🟢: `PRODUCT_DETAIL.weight_uom_id` (Q-20 resolved). `US-FND-PRO-003`: context only.
- `US-FND-WMS-002` 🟡 → 🟢: BOM tab as a tree, net recipe, `base_qty`, phantom, material per step, version + status (W-8).
- `US-FND-WMS-003` 🟡 → 🟢: Routing tab, version bar, step fields, class steps + station overrides, dependencies,
  shrinkage, prefill from `EQUIPMENT_FLOW`, derived unit weight, `enforce_sequence`, back-dated warning (W-7, W-8).
- `US-FND-SIT-002` 🟡 → 🟢: SL-1 resolved, open-downtime count buildable.
- `US-FND-KPI-002`: standard as of business date, station override, batch rules (pending until close, split by running
  time), input-side counters. `KPI-003` 🟢 → 🟡: reject by weight → units → time added, but it conflicts with F-07.1's
  one-source / no-conversion rule (new Q-28). `KPI-004`: rejects add up in time. `KPI-001`, `KPI-008`: `activity_signal`
  in range detection (Q-30 wording). `KPI-005`: SL-1 noted. `KPI-007`: Production sidebar is KPI only.
- `US-FND-DSR-001`: `count_side`, `lot_marker`, `activity_signal` (new Q-29: per-lane or machine-wide; no UX 13 fields yet).
- README, 20-cross-cutting (screen map, entity table), 30-release-risks (Q-20 resolved; Q-27…Q-30), 99-history updated.

**PRD-004 PRODUCTION** — totals unchanged (6 · 12 · 7 = 25).
- B3 closed (PLN-Q6): the lock is the released `routing_id` / `bom_id`; lock fields removed — `PLN-001`, `003`, `006`.
- `WORK_ORDER.product_flow_id` (PLN-Q7) — `PLN-001`, `002`; stage-order drafts on release (PLN-Q3) — `PLN-003`, new P-11.
- Station override and setup-time unit in the workload formula — `PLN-001`.
- SL-1 / SL-4, two write paths (#57) — `MON-003` (still 🔴 on the state field name), `MON-005`, `PLN-010`, `PRF-006`.
- Reject `uom_id`, `origin = manual` (PLN-Q1 / Q2) — `MON-004`, `005`, `006`, `PLN-004`; new M-04, M-05.
- F-07 batch / weight display cases — `PRF-001`, `PRF-002`, `MON-001`; new R-07.
- README glossary and blockers, 20-cross-cutting, 30-release-risks (B3 struck, B5 → SL-2…SL-4), 99-history updated.

**Unmatched (no story — PO to decide):** product codes section `SCR-FND-WMS-007` (#58 part B); `PRODUCT_FLOW` flow bar;
`OPERATION_PARAMETER`; `SCR-PROD-MON-001` state log browser (#57 asked for one); starting / executing a step
(`enforce_sequence`); lot recording (PLN-Q4); operator reject entry as the main source; actual shrinkage; stage-order
drafts as their own story (now inside `PLN-003`); new F-07 texts without UX keys ("pending, batch open", "Weight per unit
missing", "Cycle time missing", "Reject is more than this shift's output", estimated Quality); UX 06 has no text for the
`weight_uom_id` picker.

## 2026-09-28 — Readability audit: Obsidian and GitHub rendering fixes (no content change)

- **Screen-text placeholders escaped.** Bare placeholders (`<count>`, `<label>`, `<machine>` …) were read as
  unclosed HTML tags. GitHub hid them; Obsidian also stopped rendering Markdown after them (tables, bold and
  italic showed raw). 162 placeholders in 10 files are now written `\<count\>`, which renders as `<count>`.
- **Formula boxes are Obsidian callouts.** The 106 `<details><summary>Exact formula (for developers)</summary>`
  blocks became `> [!note]- Exact formula (for developers)` callouts (collapsed in Obsidian). Obsidian did not
  render the code inside `<details>` and left the text after it raw. On GitHub they now show as an open quote
  block with `[!note]-` on the first line.
- **Broken tables repaired.** `99-history.md` Version notes (no separator row, one cell wrapped over 11 lines) is
  now two labelled paragraphs; the "One product conversion row" decision-log row is split into its 4 columns.
  PRD-001 (frozen): 4 "Device context" rows that had slipped under the "First 3 seconds" table are moved back
  under their own header.
- No story, field, rule or text changed. No spec decision behind it, so no `docs-molcadx` log entry.

## 2026-09-28 — prd-sync docs-molcadx `0dea5c3..147e8d2` (PRD-003): tag declaration, generated MQTT topics

Spec change: docs-molcadx #54 (PO, 2026-09-28).

- **`US-FND-DSR-001`**: MQTT topics can be generated (`[topic_root/]SITE/AREA/WC/WU/ASSET/TAG`, one value per topic,
  optional `topic_root`); generated is the default for new sources, typed kept for fixed vendor servers. Tag form
  reordered with role pre-fill (defaults `TBD`, A-16), live address preview, "Typed addresses" filter, "Missing for
  KPIs" panel, edit only on Tags. Screen text word for word from UX 13. AC-14 reworded; AC-19–21 added; H-DS-7 and
  A-16 open items added.
- **`US-FND-AST-002`**: read-only Tags tab on the asset detail with "Manage tags"; AC-9 added.
- **`US-FND-KPI-007`**: "Declare a tag" opens the tag form pre-filled (`kpi-panel.slot.no-tag` text updated).
- **Also:** 30-release open-questions row (+H-DS-7), decision log row.
- **Task breakdown**: added `AST-002-43/-44`, `KPI-007-56` (`DSR-001` still not broken down — blocked). Totals
  1,349 live · 1,468 IDs issued.
- Sync point: PRD-003 and PRD-004 README → `docs-molcadx@147e8d2`. PRD-004: no match. Unmatched: none.

## 2026-09-28 — Task breakdown: duplicate task ID fixed

- `T-US-FND-WMS-002-30` was issued twice. The picker-text row (referenced by `-29` and earlier changelog entries)
  keeps `-30`; the "Test AC-7: tenant without Production" row is renumbered to **`T-US-FND-WMS-002-38`**. No row
  depended on it. IDs issued 1,464 → 1,465; live count unchanged.

## 2026-09-28 — prd-sync docs-molcadx `dd0a64e..0dea5c3` (PRD-003): list pages, KPI side panel, Tags page

Spec change: docs-molcadx #53 (PO, 2026-09-28).

- **`US-FND-WMS-002` / `-003`**: product picker retired — each page opens on a product list with gap badges
  (UX 08 `wms-list.*`, `wms-002.*`, `wms-003.*`), default type filter semi-finished + finished goods, header link
  BOM ↔ Routing. WMS-002 AC-9/AC-10 and WMS-003 AC-14 replaced; picker open item marked superseded.
- **`US-FND-KPI-007`**: KPI page drops "KPI Formula Slots" / "KPI results"; a KPI card opens a per-KPI side panel
  (Formula · Inputs · Work units · Latest result, UX 07 `kpi-panel.*`). AC-8 updated, AC-14 added.
- **`US-FND-KPI-008`**: now lives in panel section 3 "Work units"; behavior, states and ACs unchanged.
- **`US-FND-DSR-001`**: tags get their own page **Tags** (`SCR-FND-DSR-002`); Data sources keeps connections + tag
  count; export moves to Tags and exports the filtered rows. Layout, states, screen text (`dsr-002.*`) updated;
  AC-16 updated, AC-18 added; UX-DSR-1 / UX-DSR-2 closed. "Last read" column `TBD` on H-DS-1.
- **Also:** `US-FND-AST-003` screen refs → `SCR-FND-DSR-002`; cross-cutting screen map rows; decision log row.
- **Task breakdown**: superseded `WMS-002-27/-30/-32`, `WMS-003-38/-49`, `KPI-007-17/-33`; added `WMS-002-33..37`,
  `WMS-003-50..52`, `KPI-007-51..55`. Totals 1,346 live · 118 withdrawn/superseded/resolved · 1,464 IDs issued.
- Sync point: PRD-003 and PRD-004 README → `docs-molcadx@0dea5c3`. PRD-004: no match. Unmatched: none.

## 2026-09-28 — prd-sync docs-molcadx `4cb81d6..dd0a64e` (PRD-003): last three `TBD`s closed

Spec change: docs-molcadx #52 (PO, 2026-09-28).

- **`US-FND-KPI-008`**: conversion rule shown for the product running now on the work unit, else the last product
  run (`kpi-004.conversion.product`, word for word); `TBD` + open item closed; AC-11 added.
- **`US-FND-WMS-003`**: `cycle_time_value` keeps up to 6 decimals, rounded half-up ("700 per hour" → 0.001429 h);
  precision `TBD` + open item closed; rule 10 updated; AC-15 added.
- **`US-FND-WMS-002` / `-003`**: picker text from UX 08 `wms-picker.*` ("Product", "Pick a product", "No products
  yet — declare one first."); open item closed; WMS-002 AC-10 added.
- **Task breakdown**: `T-US-FND-WMS-002-29`, `T-US-FND-WMS-003-40/-41` resolved; `T-US-FND-WMS-002-30`,
  `T-US-FND-WMS-003-42` added. Totals 1,340 live · 111 withdrawn/superseded/resolved · 1,451 IDs issued.
- Sync point: PRD-003 and PRD-004 README → `docs-molcadx@dd0a64e`. PRD-004: no match. Unmatched: none.

## 2026-09-28 — prd-sync docs-molcadx `513c4f9..4cb81d6` (PRD-003): one conversion row covers both directions

Spec change: docs-molcadx #51, [F-06 §9.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/06-product.md#94-product_uom_conversion--fromto-uom-conversion-factor-scoped-to-one-product)
(PO, 2026-09-28) — a `PRODUCT_UOM_CONVERSION` row read forward multiplies, read backward divides; if both directions
exist for the same pair and date, the forward-read row wins. Found as a conflict in the task breakdown (#13).

- **`US-FND-PRO-003`**: Success state no longer offers a reverse row; "Reverse direction" calculation rewritten with
  forward/backward formula; rule 5 and AC-6 rewritten, AC-6b added; event `uom_conversion_reverse_missing` dropped
  (also struck in `20-cross-cutting.md`). Change note + `99-history.md` row.
- **Task breakdown**: `T-US-FND-PRO-003-02/-11/-20/-27` superseded, `-31`…`-34` added (dependencies repointed to
  `-31`; tests use the shared resolver `T-US-FND-WMS-003-29`); inconsistency note 4 resolved. Totals 1,341 live ·
  108 withdrawn/superseded · 1,449 IDs issued.
- Sync point: PRD-003 and PRD-004 README → `docs-molcadx@4cb81d6`. PRD-004: no match in this range.
- Unmatched: none.

## 2026-09-28 — PRD-003 task breakdown matched to today's two syncs (`6d9a679`, `961b6f2`)

`PRD-003-task-breakdown.md` only; no story edited. Source: `git diff 86aa54c..961b6f2 -- PRD-003-foundation/` plus
the current story blocks (52 live stories, specs synced to `docs-molcadx@513c4f9`). Task IDs are not renumbered or
reused: new tasks take the next free number, old ones are struck through and marked superseded/withdrawn.

- **`US-FND-PRO-004`** retired: task table replaced by a note (same style as `SIT-003`); `T-US-FND-PRO-004-01`…`-40`
  withdrawn. No other table depended on them.
- **`US-FND-WMS-003`**: `OPERATION` schema superseded (`uom_id` = item unit, F-08 §9.2 field list); +23 tasks —
  item-unit picker, shared conversion-path resolver, typed rate saved as typed, rules 8/9 with the exact error,
  three-field entry, save-blocked and empty-picker states, Production › Routing page, item carry-over, direct-link
  entitlement state, precision and picker-text `docs` tasks, tests AC-7…AC-14.
- **`US-FND-WMS-002`**: +7 — Production › BOM page, item carry-over, entitlement state, picker-text `docs`, tests AC-7…AC-9.
- **`US-FND-KPI-002`**: +6 (counter unit/basis from `ASSET_TAGS`, ideal-time step with conversion, rule 3, cannot-be-
  calculated state, AC-5, AC-5b); 2 superseded (`PRODUCT_CYCLE` rule and old AC-5).
- **`US-FND-KPI-003`**: conversion task and old AC-11 test withdrawn; +1 (new AC-11).
- **`US-FND-KPI-007`**: 4 superseded (KPI group, hide-when-unlicensed, old AC-10/AC-11); +10 (domain-grouped sidebar,
  locked heading, KPI item, direct link, Quality one-unit rule and state, AC-10…AC-13).
- **`US-FND-KPI-006`**: origin-marker `docs` task withdrawn (answered by `source = derived`); `ASSET_STATE_LOG` write
  superseded by a hand-off to PRODUCTION's write path (+1). **`US-FND-INT-001-07`** (FOUNDATION writing
  `ASSET_STATE_LOG`) withdrawn: owned by PRODUCTION.
- **`US-FND-SIT-002`**: A-5 `docs` task superseded by SL-1 (`production-domain`); `ASSET_STATE_LOG` readers in
  `SIT-002`, `REF-001/003/006`, `KPI-001/005` depend on the PRODUCTION entity.
- **`US-FND-REF-005`**: usage-count task superseded (operations only, +1). **`US-FND-PRO-001`**: +1 (tabs Measurement / Conversions / History).
- Header, batch summary and totals recounted: **1,341 live tasks**, 104 withdrawn/superseded/dropped, 1,445 IDs issued.
  New ambiguity flag: `PRO-003` AC-6 (no automatic inversion) vs the F-08 conversion path read backward (÷).

---

## 2026-09-28 — prd-sync `docs-molcadx@1285317..513c4f9` (PRD-003, PRD-004)

Target `origin/main` (`513c4f9`). Sync point `1285317` was an ancestor of main. Sync points of PRD-003 and PRD-004 moved
to `docs-molcadx@513c4f9`. Decisions: [work master addenda](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md)
(no "Master data" label, Work Master flattened, four PO answers) and
[asset status ownership](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md).
**No story changed status** and no story was created, closed or retired; README totals unchanged (52 live).

**PRD-003**
- **`US-FND-KPI-003`**: Quality never converts units — all Quality tags of a work unit share one unit, blocked at
  binding (F-07.1 §9.4). Conversion edge case and its `TBD` removed, AC-11 rewritten, `PRODUCT_UOM_CONVERSION` no
  longer read. Open item "Quality unit conversion" closed.
- **`US-FND-KPI-007`**: binding refuses mixed Quality units — validation rule 4, rule 7, new state and AC-12 with
  `kpi-002.error.quality-uom-mismatch` copied word for word from UX 07. Sidebar: shared items at the top with no
  "Master data" heading; Production BOM · Routing · KPI, Maintenance PM checklist · KPI · Asset history; AC-13.
- **`US-FND-KPI-008`**: breakdown shows the conversion **rule**, not live numbers — `kpi-004.conversion.rule` /
  `.missing` word for word, two states, edge case, reads, AC-9/AC-10. Old `TBD` closed. **New `TBD`:** which
  product's operation the rule shows when several products run on the work unit.
- **`US-FND-KPI-009`**: `PRODUCT_UOM_CONVERSION` dropped from reads.
- **`US-FND-WMS-002`, `WMS-003`**: BOM and Routing are separate pages (`nav-sidebar.item.bom` / `.routing`;
  `.work-master` retired), no tabs, own item picker, last picked item carried over (picker text `TBD`). AC-8/AC-12
  reworded, AC-9/AC-14 added. `WMS-003`: a typed rate is saved as typed (no seconds normalisation) — calculation
  block, rule 10, AC-13; `cycle_time_value` precision stays `TBD` (F-08's own), new open item. UX 08 anchor fixed.
- **`US-FND-REF-005`**: 1-second row summary retired; removed from Expectation and example, key shown retired.
  `TBD` and open item closed.
- **`US-FND-KPI-001`, `004`, `005`, `012`**: B5 ownership resolved (PRODUCTION owns `ASSET_STATE_LOG`). Kept 🔴/🟡:
  PRODUCTION's entity spec is a draft (row ID / state field names, recording level SL-1, shift-split rule `TBD`).
- **`US-FND-KPI-006`**: origin marker = PRODUCTION's `source = derived` (Q-21 origin-marker part closed); the detected
  row is written through PRODUCTION, the only writer. Still blocked on the stall threshold (K-6).
- **`US-FND-SIT-002`**: open-downtime count now waits on PRODUCTION SL-1, not on ownership. Still 🟡.
- **`US-FND-AST-002`**: `lifecycle_status` = current value only; history is MAINTENANCE's (entity `TBD`).
- **`US-FND-REF-001`, `003`, `006`**: `ASSET_STATE_LOG` reads name PRODUCTION; `REF-003`'s ordering by `asset_id`
  depends on SL-1. **`US-FND-AUD-001`**: `ASSET_STATE_LOG` rows are `PROD` / `MON`.
- Closed `US-FND-WMS-001`: note that its screen `SCR-FND-WMS-001` is retired too (already closed 2026-08-28, no
  retirement needed; no live story depends on it).
- README (item 1, WMS row), `00-problem-goals-scope` A2, `20-cross-cutting` (sidebar row, BOM/Routing entry points,
  "Master data menu" entry points, `SCR-FND-WMS-001` / `SCR-FND-AST-005` struck, `ASSET_STATE_LOG` and
  `PRODUCT_UOM_CONVERSION` rows, stale `PRODUCT` writer), `30-release-risks-questions` (F5, F-R-07, Q-21, B5
  resolved row, next step 1), `99-history` decision log.

**PRD-004**
- **`US-PROD-MON-003`**: owner PRODUCTION, draft spec, `SCR-PROD-MON-001` noted; unblock condition = SL-1 answered and
  state field named. Still 🔴. **`MON-004`**, **`MON-005`**: `ASSET_STATE_LOG` reads/corrections point to the
  PRODUCTION spec (`reason_corrected`, `PROD`/`MON`, closed shift = signed adjustment). B5 open item lists SL-1…SL-4.
- **`US-PROD-PRF-006`**, PRF B5 open item; **`US-PROD-PLN-008`** (P-06 = SL-3 in PRODUCTION's spec) and
  **`PLN-010`** (equipment readiness); `20-cross-cutting` owner row; `30-release-risks-questions` P-R-01, B5 row,
  next step 4; README item 3; `99-history`.

**Backlog (not done here, repo scope):** task breakdown for `KPI-003` AC-11, `KPI-007` AC-12/13, `KPI-008` AC-9/10,
`WMS-002` AC-9, `WMS-003` AC-13/14; withdraw tasks for the retired 1-second summary (`REF-005`).

**Unmatched (no story covers them — PO decides):** `SCR-PROD-MON-001` state log browser and PRODUCTION operator
downtime entry (reject rules, offline SL-4) — no PRD-004 story; `ASSET_STATE_LOG` view permission
(`production:mon:<scope>:…` `TBD`); SL-2 / MNT-Q1 (Maintenance-only tenant has no state log); MAINTENANCE lifecycle
history (LH-1…LH-5), PM checklist and Asset history sidebar items (`nav-sidebar.item.pm-checklist` /
`.asset-history`) — no Maintenance PRD; PLATFORM PLT-01 concept 3a sidebar wording — no PLATFORM PRD.
Ignored (wording only): F-13, FLOW-00/04/05 owner notes, UX 04 coverage line, UX 09 journey node.

---

## 2026-09-28 — prd-sync `docs-molcadx@7fb3a9a..1285317` (PRD-003, PRD-004)

Target `origin/main` (Work Master per domain, `OPERATION.uom_id` = item unit, `PRODUCT_CYCLE` retired —
[decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-work-master-per-domain-operation-uom.md)).
Sync point was an ancestor of main. Sync points of PRD-003 and PRD-004 moved to `docs-molcadx@1285317`.

- **PRD-003 `US-FND-PRO-004`** (reference cycle times): **retired** (decision C). Moved to `90-closed-stories.md` with a
  retirement note; ID kept, not reused. No story listed it as a dependency; body mentions in `REF-005` and `KPI-002`
  re-pointed. Live stories 53 → 52, closed/retired 12 → 13; `PRO` 4 → 3 stories; README totals 20 🟢 · 15 🟡 · 7 🔴 ·
  10 🟠 = 52 (the SIT-003 retire had left the totals line at 54).
- **`US-FND-WMS-003`**: `OPERATION.uom_id` is the item unit, not a time unit (W-6 resolved). Three-field entry
  ("20 seconds / pack"), item-unit picker, save rule with the exact F-08 §9.2.1 / UX 08 error text, conversion step
  with the 1,200 pack → 21,600 s example, run-time "cannot be calculated", AC-8…AC-12, dependency `US-FND-PRO-003` added.
- **`US-FND-WMS-002`**: reached from Production › Work Master; license gate shows as a locked group heading; AC-8.
- **`US-FND-KPI-002`**: counter → `OPERATION.uom_id` conversion through `PRODUCT_UOM_CONVERSION` before `batch_size`
  (only `count_basis = 'unit'`); new state/edge case; F-07 example (85.7%); rule 3 and AC-5 replaced, AC-5b added;
  `count_basis` cited on `ASSET_TAGS` (was wrongly on `OPERATION`); dependency `US-FND-PRO-003` added.
- **`US-FND-KPI-007`**: KPI item inside each domain group; K-9 revised (unlicensed domain = one locked heading);
  retired `kpi-sidebar.*` keys replaced by UX 00 `nav-sidebar.*` keys word for word; AC-10/AC-11 reworded.
- **`US-FND-KPI-008`**: UX 07 anchor fixed; `TBD` on FLOW-05's "breakdown shows the conversion used".
- **`US-FND-KPI-003`**: `TBD` + open item — F-07 now says Quality needs no conversion, the story converts differing units.
- **`US-FND-PRO-001`, `PRO-003`**: tabs Measurement / Conversions / History; conversions now read by the KPI.
- **`US-FND-REF-004`, `005`, `006`**: `PRODUCT_CYCLE` dropped from usage counts, blockers and metric; `ref-002.*` rows
  updated from UX 05; renamed F-06 §9.3 anchor; `TBD` on whether the 1-second row summary is still needed.
- `20-cross-cutting.md` (screen map, entity summary, events), `30-release-risks-questions.md` (F1/F3 counts),
  `99-history.md` (decision log + §13.2) updated.
- **PRD-004:** no matches in this range; only the sync point moved.
- **Backlog (not done here, repo scope):** task breakdown — `T-US-FND-PRO-004-*` (40 tasks) to withdraw, new tasks for
  `WMS-003` AC-8…12 and `KPI-002` AC-5/5b.
- **Unmatched (no story covers them — PO decides):** `nav-sidebar.group.master-data` "Master data" (UX 00, story TBD);
  PLATFORM PLT-01 concept 3a and FLOW-01 sidebar render, FL-3 (licensed domain, every item hidden) and the deep-link
  `TBD` (no PLATFORM PRD); Maintenance / Quality / Inventory Work Master items (M-02 screens TBD, no PRD); UX 05
  `ref-002.summary.one-second-row` still cites retired `US-FND-PRO-004`.
- Ignored (wording / no PRD effect): F-00, F-00.1 (P-1 list, PRO row), F-12, F-14 entity list, FLOW-02, PLATFORM
  AGENTS, PLT-01 anchor fixes.

## 2026-09-27 — `US-FND-SIT-003` retired (PO, docs-molcadx#46) (PRD-003)

prd-sync `docs-molcadx@7a59537..7fb3a9a` (docs-molcadx #48 merged: UX 01 `SCR-FND-SIT-003` retired). Target
`origin/main`; sync point was an ancestor of main. Sync points of PRD-003 and PRD-004 moved to `docs-molcadx@7fb3a9a`.

- **PRD-003 `US-FND-SIT-003`** (location context picker): retired, not a FOUNDATION story. Moved to
  `90-closed-stories.md` with a retirement note; ID kept, not reused. Live stories 54 → 53, closed/retired 11 → 12;
  `SIT` module 4 → 3 stories.
- `20-cross-cutting.md`: §7.1 "Header across the whole app" row and §8 `context_changed` event struck out.
- `99-history.md`: decision log row and §13.2 row added.
- PRD-004: no reference to the picker; no change beyond the sync point.
- Task breakdown: 18 `T-US-FND-SIT-003-*` tasks withdrawn, replaced by a "no tasks — retired" note.
- **Parked (no story written, rule 1):** a Production picker (`US-PROD-MON-*`?), sharing with Planning/Performance,
  and storage before `CFG-001` — open on docs-molcadx#46.

## 2026-09-27 — prd-sync `docs-molcadx@7ad3ed8..7a59537` (PRD-003, PRD-004)

Target `origin/main` (docs-molcadx #45 merged). Sync point was an ancestor of main.

- **PRD-003 `US-FND-KPI-007`** — K-9 (PO): only domains the tenant is entitled to appear in the KPI sidebar, hidden not
  locked (PLT-01 core concept 3); access rule adds the entitlement check; new AC-11. Task breakdown: 2 more tasks.
- **PRD-004 `03-performance`** — spec "Report pages" (PO): reporting is four pages (Overview / Availability / Performance /
  Quality), each with Live and Period views. `PRF-001` becomes the Live view (AC-6 drill-in, AC-7 no editing), `PRF-002`
  the Period view with download per tab. KPI table gains a Report page column; module layout note, screen map, README
  glossary ("Report page") and modules row updated. Figma redraw `TBD — design`.
- Already covered earlier (prd-molcadx #8): `KPI_FORMULA_SLOT.domain`, `KPI_RESULT.metric` values, FLOW-05 → `KPI-008`.
- Sync points moved to `docs-molcadx@7a59537`.
- **Unmatched:** none. Spec clean-ups with no PRD effect: UX index rows, UX 09 journey label, 11 anchor fixes in F-07 / F-07.1.

## 2026-09-27 — KPI per domain + adopted Production KPIs in Performance (PRD-003, PRD-004)

Spec: docs-molcadx branch `docs/kpi-slot-domain` (F-07.1 §9.1 `KPI_FORMULA_SLOT.domain`, F-07 "every adopted KPI is a
Production KPI", UX 07 domain groups). Sync point not moved — the spec change isn't on `main` yet.

- PRD-003 `US-FND-KPI-007`: one KPI sidebar item per domain (Production / Quality / Maintenance / Inventory, all shown,
  empty domains open an empty state); AC-10; screen text copied (draft).
- PRD-003 **new `US-FND-KPI-013`** (PO request): Fall-off ratio per Job Order. 🟢 Ready; live stories 53 → 54.
- PRD-004 `03-performance`: KPI table adds Scrap ratio, Rework ratio, Fall-off ratio; statement that all are Production
  KPIs; new "Where each KPI exists" scope table. `PRF-001` (AC-4/5), `PRF-002` (AC-4/5), `PRF-003` (AC-3) show them.
- Sync check against docs-molcadx#45 (`7ad3ed8..e2a1d7b`): also `US-FND-KPI-008` rule 7 (keeps the domain it was opened from) and a `20-cross-cutting` note. Nothing else matched.
- Task breakdown: 12 tasks for `US-FND-KPI-013`, 5 for `US-FND-KPI-007`'s domain sidebar (AC-10). `KPI-008..012` still have none.
- R-06 (no Fall-off story) resolved by `US-FND-KPI-013`; `PRF-002` shows the column as "Not available yet" until it is built.

## 2026-09-27 — prd-sync `docs-molcadx@3cb17fa..7ad3ed8` (PRD-003, PRD-004)

Target `origin/main` (merges #41, #44, #43). Sync point was an ancestor of main.

- **Screen text from UX, word for word with keys** (PO 2026-09-25): `SIT-001…004`, `SHF-001/002`, `FYR-001/002`
  (Fiscal panels of UX 01), `REF-001/002/004/005/006`, `AST-001`, `DSR-001` (new UX 13, sidebar group "Connections").
- **Generated OPC UA addresses + multi-lane machine** (PO 2026-09-25, F-04 §9.6/§9.7.1, F-13, F-07.1, F-00.1):
  `SIT-001` (one-segment codes, charset, rename warning A-14), `AST-002` (asset_tag rename warning, multi-lane access),
  `AST-003` (move vs add lane), `DSR-001` (`node_naming`, `namespace_uri`, `tag_name`, `work_unit_id`, `generated_node`,
  hand-off list; new dependency `US-FND-AST-003`), `KPI-001/002/003/006/007` (per-lane tag filter; machine-wide signals on every lane).
- **Crew assignment retired** (PO 2026-09-25): `US-FND-SHF-004` moved to `90-closed-stories.md`; counts 54 → 53 live;
  cross-cutting, scope, release stage F2 and task breakdown updated; PRD-004 `PLN-010` note updated.
- **PRD-004 `PLN-003`** (PRODUCTION 05-work-master, issue #42): release lock freezes routing/BOM identity only; OEE reads
  the `business_date` standard (rule 3a). P-03 carries the parked WM-P-2 question. Glossary "Work Directive" updated.
- **Open questions added** (PRD-003 §11): A-11, A-13, A-14, H-DS-5/6.
- **Unmatched (PO to decide):** Line flow (`sit-002.line-flow.*`, `EQUIPMENT_FLOW` editor) has no story; the tag hand-off list
  as its own feature has no story; UX 04 draft texts have no keys yet; code-clash refusal message has no UX key; PRD texts with
  no UX row in AST-002/003 and REF-003.

## 2026-09-25 — prd-sync `docs-molcadx@327bb64..3cb17fa` (PRD-003, PRD-004)

Target `origin/main`. One spec change: F-14 core concept 9 — PO confirms no reason and filter scope on `*_downloaded` rows.

- PRD-004 `US-PROD-PLN-007`, `US-PROD-PRF-002`: section 5 states both rules (`PLN-011` already did).
- PRD-003: no story change; sync point moved.
- Unmatched: none.

---

## 2026-09-25 — prd-sync `docs-molcadx@171a333..327bb64` (PRD-003, PRD-004)

Target: branch `docs/audit-source-download-actions` (docs-molcadx#38), not yet on `main`. PO decisions: the Audit log
stays central and every row shows its domain and module; downloads of a domain's own data are business actions.

- PRD-003 `US-FND-AUD-003`: Domain · Module column, a row opens in its owning module, rule 4, AC-3.
- PRD-003 `US-FND-AUD-004`: the file has `domain_code` / `module_code` columns.
- PRD-004 `US-PROD-PLN-007` / `PLN-011`: schedule download = `schedule_downloaded`; the log's own download is an `FND` · `AUD` row.
- PRD-004 `US-PROD-MON-007`: generic `download` removed from the module's actions.
- PRD-004 `US-PROD-PRF-002` / `PRF-007`: report download = `report_downloaded`.
- **P-10 closed** (PRD-004, and the mirror row in PRD-003 `AUD`).
- Unmatched: none.

---

## 2026-09-25 — prd-sync `docs-molcadx@5c7d303..171a333` (PRD-003, PRD-004)

Spec changes in range: F-14 Audit Trail widened to the whole system, F-00 / F-00.1 per-module `audit_view` /
`audit_download`, PRODUCTION 01–03 § Log rewritten as views of the shared `AUDIT_LOG`, glossary. Most of it was already
in the PRDs (entries below); this closes what was left.

- PRD-003 `US-FND-AUD-001`: business actions in `action` and in the reason rule; covered entities for every domain;
  `tenant` scope edge case for other domains' records; rule 9 (owner in `domain_code` / `module_code`); metric for every domain.
- PRD-003 `US-FND-AUD-002`: other domains' records get the History capability in their own PRDs.
- PRD-003 `US-FND-AUD-003`: no-permission state is an empty page, not a hidden one (F-14 §9.2).
- PRD-003 `20-cross-cutting.md`: action vocabulary — `audit_view` / `audit_download` on every module, not `download` for `AUD`.
- PRD-004 `US-PROD-PLN-011`: Figma "delete" = `deactivate`; `cancel` is a business action; permission on the row's scope.
- PRD-004 `US-PROD-MON-007`, `US-PROD-PRF-007`: generic actions added; `report_downloaded` (not in spec) → `download`.
- New open item **P-10** (PRD-004, mirrored in PRD-003 `AUD`): F-14 allows `download` only on `AUDIT_LOG`, PRODUCTION
  specs log schedule/report downloads. Real inconsistency, left for the PO.
- Unmatched: none.

---

## 2026-09-25 — Audit trail widened to the whole system (PRD-003 `AUD`)

- PO decision: one `AUDIT_LOG` (FOUNDATION F-14) for **every domain**; reading is **per module**
  (`<domain>:<module>:<scope>:audit_view` / `:audit_download`); rows carry `domain_code` / `module_code`.
- `14-audit-trail.md`: scope note, `AUD-001` widened (title "Record every change, in every domain", AC-11 for a
  PRODUCTION release), `AUD-002`–`004` permissions per module, `AUD-003` filters by domain/module. README row, history.
- PRD-004 (PR #2): P-08 closes — `PLN-011`, `MON-007`, `PRF-007` read the shared `AUDIT_LOG`.

---

## 2026-09-25 — Sync points for the `prd-sync` skill

- PRD-003 README (and PRD-004 README, in PR #2): new header row **Specs synced to** `docs-molcadx@5c7d303`. The
  `prd-sync` skill in `docs-molcadx` diffs the specs from this commit to find what a PRD must catch up on.
- `AGENTS.md` §3: rule 1b syncs go through `prd-sync`.

---

## 2026-09-25 — PRD-003 Audit Trail: PO answers AU-1, AU-2, AU-8

- `14-audit-trail.md`: 3-year retention (AU-1b: after that, open); **reason required** on edit / deactivate /
  reactivate and once per import (AC-9, AC-10, Reason field); reading the log is its own permission (AU-8).
- `20-cross-cutting.md` §7.3: one rule for every FOUNDATION screen — edit / deactivate / reactivate forms get a
  required Reason field. It covers the existing edit stories without rewriting each one (rule 1b).
- AU-3 answered: the display name is kept and never blanked (customer records the UU PDP basis at onboarding).
- Still open: AU-1b (after 3 years), AU-9 (download).

---

## 2026-09-25 — PRD-003: new module Audit Trail (`AUD`)

- New `PRD-003-foundation/14-audit-trail.md`: 4 stories — `US-FND-AUD-001` record every change (🟢),
  `AUD-002` history per record, `AUD-003` search the audit log, `AUD-004` download it (🟡, open items AU-3 / AU-8 / AU-9).
  Spec: F-14 in `docs-molcadx` (PO decision 2026-09-25, FOUNDATION only).
- README: 14 modules, **54 live stories** (21 🟢 · 15 🟡 · 8 🔴 · 10 🟠); `AUD-001` joins build stage F1.
- `00-problem-goals-scope.md`, `30-release-risks-questions.md` (F1), `20-cross-cutting.md` (the `download` action), `99-history.md`.
- **Backlog:** `PRD-003-task-breakdown.md` has no `AUD` tasks yet.

---

## 2026-09-25 — PRD-004: logs use the shared audit trail (P-08 closed)

- PO decision: the audit trail is system-wide (FOUNDATION F-14 `AUDIT_LOG`), read per module.
- `PLN-011` and `MON-007` → 🟢 Ready: views of `AUDIT_LOG` for `module_code = PLN` / `MON`, permissions
  `production:pln|mon:<scope>:audit_view` / `:audit_download`, F-14 rules (append-only, reason required, 3 years).
  Both now depend on `US-FND-AUD-001`.
- `PRF-007` stays 🟡 (report lifecycle R-03); new **R-05**: should it show Monitoring's reason-edit / rework rows?
- Entity summary gains `AUDIT_LOG`; P-08 closed in all open-question tables. Totals now 6 🟢 · 12 🟡 · 7 🔴 = 25.

---

## 2026-09-24 — PRD-004 PRODUCTION started: Production Planning (`PLN`)

- New folder `PRD-004-production/`: `README.md` + `01-planning.md`, 11 stories
  (3 🟢 · 5 🟡 · 3 🔴). Modules and features follow the PO's Figma: Planning = Job Order · Breaktime ·
  Execution (Product Tracing deferred); log is a shared capability.
- Same day: **Monitoring** (`02-monitoring.md`, 7 stories: Dashboard · Live Monitoring · Losses List + log) and
  **Performance Analysis** (`03-performance.md`, 7 stories: OEE live · OEE report · OEE KPI for Management ·
  OEE Performance + log; every number read from FOUNDATION `KPI_RESULT`). PRD-004 = 25 stories.
- Open items added: M-01 how output arrives, M-02 ranking basis, M-03 speed losses, R-01 KPI Selection entity,
  R-02 KPI targets, R-03 report lifecycle, R-04 failure redefinition. B5 (`ASSET_STATE_LOG`) blocks the
  machine-state parts, as in PRD-003.
- Domain-level files added, same shape as PRD-003: `00-problem-goals-scope.md` (3 goals, counter-metrics,
  scope), `20-cross-cutting.md` (screen map, entities, access, offline, NFRs, instrumentation),
  `30-release-risks-questions.md` (stages P1–P5, risks P-R-01..07, all open questions), `90-closed-stories.md`
  (18 retired PRD-001 PRODUCTION IDs mapped to new stories or "not carried"), `99-history.md`.
- **Implementation repo per module:** PRD-004 README module table has an *Implementation repo* column (`TBD`
  until the PO names them). `AGENTS.md` §2 rewritten for any domain PRD: build only in your module's repo.
- Content from frozen PRD-001 `US-PROD-JOB-001/002/004/005` carried over where the spec still supports it;
  those IDs stay retired.
- Open items raised: P-01 queue field, P-02 row = Job Order, P-03 upload template, P-04 approval = release,
  P-05 achievement basis, **P-06 breaktime conflict with F-02 H-2**, P-07 readiness data, P-08 log storage,
  P-09 planner persona.
- PRD-003 README: the PRODUCTION dependencies table now notes the old `US-PROD-*` IDs are retired.
- `AGENTS.md`: PRD-004 listed; PRD-001 no longer described as the PRODUCTION PRD.

---

## 2026-09-24 — Sync PRD edits from docs-molcadx #31 + #32

- Ported `DOCS/prd/` changes from docs-molcadx `f0c4eb1..8f18446` (PRs #31 standards-gaps, #32 production Figma EN rewrite).
- Highlights: T-6 disposition closed on `WORK_ORDER_OPERATION_DEFECT`; OEE POT/PBT Availability; F-08 Part-4 min spine; PRODUCTION link/anchor retargets into `01-planning.md` / `02-monitoring.md` / `03-performance.md`.
- Same link rule as the move: links into docs-molcadx → GitHub URLs; PRD-internal links stay relative. 12 files, +116 / −113 lines.
- Source: docs-molcadx `LOGS/CHANGELOG.md` entries for those PRs.

---

## 2026-09-24 — Repo created, PRDs moved from `docs-molcadx/DOCS/prd/`

- Moved: `PRD-001-molcadx-core-q3-en.md` (frozen), `PRD-003-foundation/` (20 files), `PRD-003-task-breakdown.md`.
  No content changed.
- 929 relative links into `docs-molcadx` were rewritten to GitHub URLs. Links between PRD files stay relative.
- Added `AGENTS.md` (reading order for implementers), `CLAUDE.md`, `GEMINI.md`, `README.md`, this file.
- Earlier history: `docs-molcadx` git log and `LOGS/CHANGELOG.md`.
