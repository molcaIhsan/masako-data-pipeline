# PRD-003 · Configuration Service (`CFG`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-12. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.


> ⚠ **Not buildable yet.** The entities below (`CONFIG_KEY`, `CONFIG_VALUE`, `CONFIG_CHANGE_LOG`, `FEATURE_FLAG`)
> are proposals in this PRD; `foundation-domain` has not modelled them — [F-12](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md)
> is still a concept. Do not estimate or build these stories until it does ([F-Q-03](30-release-risks-questions.md#11-open-questions)).

- **Decide first:** the list of what is configurable for the first release ([§11 Q-10](30-release-risks-questions.md)).
- **Boundary:** taxonomies used as analysis dimensions (reason code, defect code, asset class) are
  **master data, not configuration** ([F-12 core concept 2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#core-concepts)).

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| Entity model | all `US-FND-CFG-*` | `CONFIG_KEY`, `CONFIG_VALUE`, `CONFIG_CHANGE_LOG`, `FEATURE_FLAG` are not modelled ([F-Q-03](30-release-risks-questions.md#11-open-questions)) | `foundation-domain`, PO |
| What is configurable | `US-FND-CFG-001` | The first-release list of configurable keys; candidates are listed in `US-FND-CFG-001` ([§11 Q-10](30-release-risks-questions.md)). **One confirmed 2026-10-01:** the [stop thresholds](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#confirmed-first-release-item-stop-thresholds-po-2026-10-01) | PO |
| Stop threshold storage | `US-FND-CFG-001`, `US-FND-KPI-006` | Generic config-value table or a dedicated table for `min_stop_seconds` / `small_stop_max_seconds` ([§11 Q-32](30-release-risks-questions.md)) | engineering + PO |
| `[proposed]` numbers | `US-FND-CFG-001`, `US-FND-CFG-003` | Change takes effect ≤5 minutes on online devices | PO |

## Stories


| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-CFG-001`](#us-fnd-cfg-001) | Set tiered values and see where each comes from | 🟠 Concept only | As an **implementor**, I want to set thresholds per level and see which level the effective value came from, so tiered configuration is diagnosable. → [detail](#us-fnd-cfg-001) | `US-FND-SIT-001` |
| [`US-FND-CFG-002`](#us-fnd-cfg-002) | View history & roll back configuration | 🟠 Concept only | As an **implementor**, I want to see who changed what and roll back to a previous value, so "why did behaviour change since Tuesday" can be answered. → [detail](#us-fnd-cfg-002) | `US-FND-CFG-001` |
| [`US-FND-CFG-003`](#us-fnd-cfg-003) | Turn modules on/off per plant | 🟠 Concept only | As an **implementor**, I want to switch off modules a plant doesn't use, so the operator's screen isn't filled with irrelevant things. → [detail](#us-fnd-cfg-003) | `US-FND-CFG-001` |

## Detail blocks

---

#### US-FND-CFG-001

**Set tiered values and see where each comes from**

**Status:** 🟠 Concept only — foundation-domain hasn't modelled the data for this yet.

> **In short:** the implementor sets a value (such as a threshold) at the level where it belongs, from product
> default down to user. Every screen shows the value in force and which level it came from.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to set thresholds per level and see which level the
effective value came from, so tiered configuration is diagnosable.

**2. Context**

- **Why:** handling differences between plants with special cases in the code makes every change risky for
  every plant ([F-12](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#why-it-matters--what-goes-wrong-without-it)).
- **The key point:** the system must be able to answer *"which level did this value come from"*. Without that,
  nobody can work out what's going on when values are set at several levels
  ([F-12 core concept 1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#core-concepts)).
- **Who and where:** the implementor, at a desktop.

**3. Expectation**

- A **list of settings**, grouped by kind: operational parameter / choice list / feature flag / technical
  setting. The kinds are kept apart so operators can't touch what isn't theirs.
- Each row shows **the value in force**, **the level it came from**, and what type of value it is and its
  allowed range.
- Opening a setting shows its value at each level, from product default down to user, with the level that wins
  clearly marked.
- What the user can read within 3 seconds: the value in force and where it came from.

| State | What the user sees |
|-------|--------------------|
| Empty | No key configured at all → UX 00 `"belum ada data"` ([`00-ux.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md)). When the key exists but only the product default is defined (no overrides), show that product default with its label, not a blank inspector. F-12 wants a fallback when a key is in the catalog; that does **not** invent a fake row when no key has been registered yet |
| Loading | A placeholder |
| Error | Everything typed is still there, plus a message that it failed. The value that was in force stays in force |
| Offline | Devices use the **last settings saved on the device** ([F-12 core concept 6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#core-concepts)). The editing screen isn't available |
| Success | The value in force updates, with a "takes effect within ≤`<n>` minutes" marker matching how often devices refresh their settings |
| No permission | Operational parameters need **work-center access** to the line being changed. Choice lists and feature flags need **site access** (or wider). Technical settings need **enterprise access** (or wider). The message says which one is missing |

**4. Calculation**

*Which value is in force*
- The value set at the most specific level wins. The levels, from least to most specific, are: product default
  → tenant → enterprise → site → area → work center → user.
- Source: [F-12](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#core-concepts) (the tenant
  level stays in the chain).
- Example: the micro-stop threshold (`min_stop_seconds`). The product default is 60 s, site JKT1 changes it to 90 s, and Line C
  changes it to 120 s → Line C gets 120 s (from: work center), and Line A gets 90 s (from: site).

> [!note]- Exact formula (for developers)
> ```
> effective_value(key, context) = the value at the most specific level defined
>
> order: product default → tenant → enterprise → site → area → work center → user
> ```

*Checking a value*
- A value must be of the setting's type and between its lowest and highest allowed value.

> [!note]- Exact formula (for developers)
> ```
> value ∈ [min_bound, max_bound]        and typed per CONFIG_KEY.type
> ```

*Edge cases*
- Values outside the allowed range are refused when saving, not when they're used
  ([F-12 common pitfalls](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#common-pitfalls)).
- A setting that's in the catalog should fall back to the product default when no level has changed it.
- A setting that isn't in the catalog at all shows `"belum ada data"`; the system doesn't quietly make one up.
- Removing a level's own value means that level takes the value from the level above again, not that it
  becomes empty.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Writes** | `CONFIG_KEY` (proposed, not modelled) (key, kind, type, `min_bound`, `max_bound`, product default value, minimum scope tier to change) · `CONFIG_VALUE` (proposed, not modelled) (`config_key`, `scope_type`, `scope_id`, value, `valid_from`) |
| **Reads** | The location hierarchy |
| **Not a FOUNDATION entity** | `ROLE_ASSIGNMENT`. Which subject may write which kind is an IDP-asserted scope claim, per the [ABAC contract](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp), not a FOUNDATION role table |

⚠ **Proposed entities.**

**Confirmed first-release setting (PO, 2026-10-01):** the [stop thresholds](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#confirmed-first-release-item-stop-thresholds-po-2026-10-01) — the "micro-stop threshold"
below. Two whole-second values, `min_stop_seconds` (global default 60) and `small_stop_max_seconds` (global
default 300), read by [`US-FND-KPI-006`](07-kpi.md#us-fnd-kpi-006) and range detection. Unlike the general chain
above, this setting uses **only four levels**: work unit → work center → site → global default, first match wins,
each value on its own. On save, the pair that will apply at that level must satisfy
`0 < min_stop_seconds < small_stop_max_seconds`, otherwise the save is refused with: "Minimum stop must be more
than 0 and less than the small-stop limit." Each value has `valid_from`; a change never recalculates a closed
shift and is never hard-deleted; every change goes to the Audit Trail with before → after. Do **not** add
columns to `WORK_CENTER` or `WORK_UNIT` for it. Where it is stored is `TBD` for engineering (Q-32).

**Other candidate settings**, taken from the existing documents (still need PO confirmation): micro-stop threshold (confirmed, above), how far output may go
over plan, correction deadline after shift close, OEE target per line, data completeness threshold, planning
capacity factor, notification thresholds and windows, how far ahead shifts are generated, and how old each
source's data may get.

**6. Rules & constraints**

1. The value in force **must** show which level it came from.
2. The type and allowed range are checked when saving.
3. Lists used to analyse data (reason codes, defect codes, asset classes) are **not** settings. They belong in
   master data.
4. Passwords and API keys aren't stored here.
5. Devices that are offline use the last settings saved on the device.
6. How fast changes arrive: a change takes effect within **`[proposed]` 5 minutes** on devices that are
   online.
7. **Who may change what:**
   - An operational parameter set for a work center needs **work-center access** to that work center (or wider
     access).
   - A choice list or feature flag needs site access or wider.
   - A technical setting needs enterprise access or wider.
   - Seeing the value in force needs any access that covers it, at any level.

**7. Acceptance criteria**

- **AC-1** **Given** the product default micro-stop threshold is 60 s, **when** the implementor changes it to
  90 s at site JKT1, **then** Line A's value in force becomes 90 s, from "site".
- **AC-2 (more specific level)** **Given** the site is set to 90 s, **when** Line C is set to 120 s, **then**
  Line C's value in force is 120 s (from work center) and Line A stays at 90 s.
- **AC-3 (validation)** **Given** the setting's allowed range is 10–600 seconds, **when** the implementor saves
  5,000, **then** it is refused at save time, with the allowed range named.
- **AC-4 (remove a level's value)** **Given** Line C has its own value of 120 s, **when** that value is
  removed, **then** Line C takes 90 s from the site, and doesn't become empty.
- **AC-5 (offline)** **Given** the device is offline, **when** the threshold is used to decide whether a stop
  was a micro-stop, **then** the last value saved on the device is used and the offline marker is visible.
- **AC-6 (kinds kept apart)** **Given** a user with work-center access to their own line only (no site access
  or wider), **when** they open the list of settings, **then** they can change only operational parameters on
  their line, and don't see technical settings.
- **AC-7 (how fast changes arrive)** **Given** a value is changed, **when** 5 minutes pass on an online device,
  **then** the new value is already in force and the marker says so.

**8. Metrics & events**

- **Metric:** `pertanyaan_asal_nilai_ke_developer` (*"where does this value come from" questions sent to
  developers*): how many per month. Target **0**, because the screen shows where each value comes from.
  Baseline `not yet measured`, measured from help tickets.
- **Counter-metric:** how many settings have **never** been changed after 90 days. A high number means too much
  was made adjustable ([F-12 common pitfalls](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#common-pitfalls)).
- **Events:** `config_value_set` (`config_key`, `scope_type`, `old_value`, `new_value`) · `config_resolved`
  (`config_key`, `source_level`), sampled, not on every read.

**Dependencies:** `US-FND-SIT-001`

---

#### US-FND-CFG-002

**View history & roll back configuration**

**Status:** 🟠 Concept only — foundation-domain hasn't modelled the data for this yet.

> **In short:** the implementor sees who changed which value, when and why, and can roll back. A rollback is
> itself a new change; history is never edited or deleted.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to see who changed what and roll back to a previous
value, so "why did behaviour change since Tuesday" can be answered.

**2. Context**

- **Why:** settings are like code without a safety net. A wrongly set threshold can do as much harm as a bug,
  but there's no code review, no tests and no automated checks. What replaces them is checking values, keeping
  history, and being able to roll back
  ([F-12 core concept 3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#core-concepts)).
- **Rolling back is required**, not an extra
  ([F-12 core concept 4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#core-concepts)).
- **Who and where:** the implementor, at a desktop, while looking into why something started behaving
  differently.

**3. Expectation**

- **The history of changes**, newest first: when, who, which setting, which level, old value → new value, and
  the reason.
- Filters by setting, by level, and by date range. The question this screen answers is nearly always "what
  changed since date X".
- What the user can read within 3 seconds: the latest change and who made it.

| State | What the user sees |
|-------|--------------------|
| Empty | "No configuration changes yet", stated as a fact, not a failure |
| Loading | A placeholder for the list |
| Error | "Could not load history". Never show an empty list that could be misread as "nothing changed" |
| Offline | Not available |
| Success (rollback) | The value goes back to the chosen version and **is recorded as a new change**; history isn't deleted |
| No permission | Rolling back needs access that also allows changing that setting, as set out in [`US-FND-CFG-001`](#us-fnd-cfg-001). A user with only narrower or read-only access can read the history within their access but can't roll back |

**4. Calculation**

*Rolling back*
- The new value is the value of the chosen earlier version. It's recorded as a new entry in the history,
  marked as a rollback and linked to the change it undoes.
- History is never deleted or changed.
- Example: a threshold was changed from 60 to 300 on Tuesday by implementor A. On Thursday someone looks into
  it because notifications stopped appearing. The rollback to 60 is recorded as the third change (300→60,
  marked as a rollback), so the history shows all three steps.

> [!note]- Exact formula (for developers)
> ```
> new_value = value_at_selected_version
> recorded as a new history entry with is_rollback = true and rollback_of_change_id
> ```

*Edge cases*
- Rolling back to a version whose value is now outside the setting's allowed range (because the range changed
  later) → refused, with an explanation.
- Rolling back a setting whose level-specific value has since been removed → it's put back as a new value at
  the same level.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Writes** | `CONFIG_CHANGE_LOG` (proposed, not modelled) (`config_key`, `scope_type`, `scope_id`, `old_value`, `new_value`, `changed_by_subject_id`, `changed_at`, `reason`, `is_rollback`, `rollback_of_change_id`) |
| **Reads** | `CONFIG_KEY`, `CONFIG_VALUE`. Do **not** read `USER_ACCOUNT` |

⚠ **Proposed entities.** `changed_by_subject_id` is an **IDP subject identifier**, not a FOUNDATION
`USER_ACCOUNT` (FOUNDATION does not store users —
[F-00 ABAC](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp)).

**6. Rules & constraints**

1. Every change must record who, when, from what to what, and a **reason**. The reason is required.
2. Nobody can edit or delete the history.
3. A rollback is recorded as a new change.
4. Rolling back to a value outside the range allowed today is refused.
5. **Who may do it:** reading the history needs any access that covers the setting, at any level. Rolling back
   needs access that also allows changing that setting, as set out in [`US-FND-CFG-001`](#us-fnd-cfg-001).

**7. Acceptance criteria**

- **AC-1** **Given** the threshold was changed from 60 to 300 on Tuesday, **when** the implementor opens the
  history and filters from Tuesday, **then** that change appears with who made it, when, the old value, the new
  value and the reason.
- **AC-2 (reason required)** **Given** the reason is left blank, **when** a settings change is saved, **then**
  it is refused with "A reason for the change is required".
- **AC-3 (rollback)** **Given** the current value is 300, **when** the implementor rolls back to a version with
  the value 60, **then** the value in force becomes 60 and the history shows three entries, not two.
- **AC-4 (history can't be changed)** **Given** a history entry exists, **when** anyone tries to change or
  delete it, **then** the attempt is refused. There is no way to do it, either in the screens or through the
  API.
- **AC-5 (rollback refused)** **Given** the setting's allowed range is now 10–120 and the old version was 300,
  **when** a rollback is tried, **then** it is refused, naming the range allowed today.
- **AC-6 (permission)** **Given** a user whose access to their line doesn't allow changing that setting,
  **when** they open the history, **then** they can read changes on their line, but the rollback button isn't
  available.

**8. Metrics & events**

- **Metric:** `waktu_menjawab_kenapa_perilaku_berubah` (*time to answer "why did behaviour change"*): the median
  time from the question being asked to the cause being found using this screen. Baseline `not yet measured`;
  the target is set after 30 days of use. Measured from `config_history_opened` → `config_rolled_back` or the
  end of the session.
- **Counter-metric:** the share of changes with no real reason (a reason under 10 characters). A high share
  means the reason field has become a formality.
- **Events:** `config_history_opened` (`filter_type`) · `config_rolled_back` (`config_key`, `from_value`,
  `to_value`, `age_days`).

**Dependencies:** `US-FND-CFG-001`

---

#### US-FND-CFG-003

**Turn modules on & off per plant**

**Status:** 🟠 Concept only — foundation-domain hasn't modelled the data for this yet.

> **In short:** the implementor switches off modules a plant doesn't use, so operators see only what matters.
> The impact is shown first, and switching off never deletes data or changes past numbers.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to switch off modules a plant doesn't use, so the
operator's screen isn't filled with irrelevant things.

**2. Context**

- **Why:** it comes up at every implementation: "we don't use the rework module"
  ([F-12](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#why-it-matters--what-goes-wrong-without-it)).
- **Keep switches few:** every option is another path that has to be tested, so the number of switches is kept
  low on purpose ([F-12 common pitfalls](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/12-configuration-service.md#common-pitfalls)).
- **Who and where:** the implementor, at a desktop. It happens rarely.

**3. Expectation**

- A **list of feature flags** (module on/off switches) with the module name, whether it's on at each level, and
  **what switching it changes, said plainly**: which screens disappear and which data stops being recorded.
- Before switching off, show how much data the module already has.
- What the user can read within 3 seconds: which modules are on at this plant.

| State | What the user sees |
|-------|--------------------|
| Empty | not applicable (the list of switches always exists) |
| Loading | A placeholder. The switch stays locked until the impact can be seen |
| Error | "Could not change — the module status has not changed." |
| Offline | Devices use the last switch settings saved on the device |
| Success | The module disappears from (or appears in) the menu for every user in that scope within `[proposed]` 5 minutes. Data already recorded is **not** lost |
| No permission | Changing a switch needs site access or wider, the same level as choice lists and feature flags in [`US-FND-CFG-001`](#us-fnd-cfg-001). Narrower access can only read |

**4. Calculation**

- Which setting of the switch is in force follows the same level rules as `US-FND-CFG-001`.

*What data is affected*
- The number of records that belong to that module within that scope.

> [!note]- Exact formula (for developers)
> ```
> affected_data = COUNT(rows of entities owned by that module within that scope)
> ```

- Example: switching off the rework module at site JKT1 → operators can no longer enter a rework quantity, the
  column disappears from the defect screen, and for **new** entries total output is just good plus scrap. The
  1,240 rework records already recorded stay, and still count toward past periods.

> [!note]- Exact formula (for developers)
> ```
> total_output = good_qty + scrap_qty        for new entries once rework is off
>                (the 1,240 historical rework_qty rows still count toward past periods)
> ```

*Edge cases*
- Switching off a module while something is still in progress in it → still allowed, but the impact is stated
  (entries in progress are finished under the old rules).
- **Metric formulas must not change for past periods.** This is the most dangerous edge case in this story and
  must be tested.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Writes** | `FEATURE_FLAG` (proposed, not modelled) (module key, `scope_type`, `scope_id`, active flag, `valid_from`), stored as a `CONFIG_KEY` of the feature flag kind |
| **Reads** | The related module's entities, to compute impact |

⚠ **Proposed entities.**

**6. Rules & constraints**

1. Switching a module off **doesn't** delete its past data.
2. Metric calculations for past periods don't change because of today's switch.
3. The impact (screens removed, data no longer recorded) must be shown before the user confirms.
4. The number of switches is kept low on purpose. A new one needs the PO's approval.
5. Devices that are offline use the last setting saved on the device.
6. **Who may do it:** changing a switch needs **access to the switch's own scope** (or wider access), at the
   level for choice lists and feature flags in [`US-FND-CFG-001`](#us-fnd-cfg-001) (site or wider).

**7. Acceptance criteria**

- **AC-1** **Given** the rework module is on, with 1,240 past entries, **when** the implementor switches it off
  at site JKT1, **then** the impact (rework column removed, 1,240 entries kept) is shown before confirming.
- **AC-2 (history untouched)** **Given** the rework module is switched off today, **when** last month's Quality
  report is opened, **then** last month's rework quantities still count exactly as before.
- **AC-3 (operator screen)** **Given** the module is off, **when** the operator opens the record-defect screen,
  **then** the `rework` option for what happens to a defective item doesn't appear.
- **AC-4 (how fast it changes)** **Given** the switch is changed, **when** 5 minutes pass, **then** the menu
  has changed for every user in that scope.
- **AC-5 (offline)** **Given** the device is offline, **when** the operator opens the screen, **then** the last
  switch setting saved on the device applies.
- **AC-6 (permission)** **Given** a user without site access or wider, **when** they open the list of
  switches, **then** they can only read it.

**8. Metrics & events**

- **Metric:** `modul_dimatikan_per_pabrik` (*modules switched off per plant*): how many switches are off at each
  site. It shows how much the plants differ in ways that must be handled without code changes. Baseline 0
  (2026-08-07), measured from the feature-flag table.
- **Counter-metric:** 0 past reports whose numbers change because a switch was changed.
- **Events:** `feature_flag_changed` (`module_key`, `scope_type`, `enabled`, `affected_row_count`).

**Dependencies:** `US-FND-CFG-001`

## Change notes (history — not needed to build)

- `US-FND-CFG-001` · 2026-10-01 — prd-sync `docs-molcadx` 787e819..725362a: the stop thresholds are the first confirmed first-release setting (F-12, PO 2026-10-01) — four-level lookup, validation message, effective dating and audit copied from F-12; Q-10 partly answered; storage open as Q-32. Status unchanged (concept only).
- `US-FND-CFG-002` (2026-09-03, [§13.5](99-history.md#135-organization-removed-2026-09-03)) — the Organization module was removed from FOUNDATION, so there is no FOUNDATION `USER_ACCOUNT`; the actor is an IDP subject identifier.
- 2026-09-23 — module banner shortened to the standard "Not buildable yet" form; the Q-10 decision and the master-data boundary are kept above as a list.
- All stories (2026-09-23): detail blocks rewritten from one dense table into numbered sections, and
  "subject whose IDP-asserted scope claim covers …" replaced by glossary access terms (e.g. *site access*, defined in the README glossary).
  Meaning unchanged; UI copy kept verbatim.
- All stories (2026-09-23, second pass): rewritten in plain language. Entity and field names now appear only in
  section 5 and in the collapsible "Exact formula (for developers)" boxes; everywhere else items are named in
  plain words (setting, level, value in force, rollback, switch). Meaning unchanged; UI copy kept verbatim.
