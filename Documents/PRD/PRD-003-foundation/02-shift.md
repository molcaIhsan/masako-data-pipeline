# PRD-003 · Shift (`SHF`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-02. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Terms like *site access*, *implementor* and *POT / PBT* are defined in the [README glossary](README.md#glossary).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.

> This module covers Shift only. The `SHIFT_CALENDAR`/`NON_WORKING_DAY` concept is retired (`US-FND-SHF-003`, closed). The fiscal/calendar part has its own section: [§6.13 Yearly & Quarterly Declaration](03-yearly-quarterly-declaration.md).

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| Zero-length / 24-hour window | `US-FND-SHF-001` | `start_time = end_time` is not addressed by F-02. | PO (question for `foundation-domain`) |
| `end_time` boundary convention | `US-FND-SHF-001` | Is `end_time` the last occupied minute (inclusive) or the boundary instant? Decides whether 14:59 and 15:00 touch or leave a one-minute gap; the overlap check must use the same convention. | PO (`foundation-domain`) |
| `SHIFT_INSTANCE.status` values | `US-FND-SHF-002` | Values beyond `open`/`closed` are `TBD` in F-02 §9.3. | Not named in PRD (F-02 §9.3) |
| Horizon and warning threshold | `US-FND-SHF-002` | Horizon length `[proposed]` 90 days; horizon warning threshold `[proposed]` 14 days. | Not named in PRD |
| ~~Where crew grouping lives~~ | ~~`US-FND-SHF-004`~~ | **Parked 2026-09-25:** the story is retired (not needed for now), so this no longer blocks anything in this module. It returns only if crew assignment comes back. Was: back in FOUNDATION as a narrower entity, fully in `production-domain`, or a `production-domain` per-site roster concept? | `foundation-domain` / PO |
| ~~Default crew proposal~~ | ~~`US-FND-SHF-004`~~ | **Parked 2026-09-25** with the retired story. Was: whether a default crew should be proposed at all, and from what. | PO |
| UX-SHF-2 — gap alert text | `US-FND-SHF-002` | Is "no instances generated for this date" (UX 02 Screens table) the approved alert text? UX 02 Screen text has the draft "No \<shift label\> generated for this date." (`shf-003.alert.gap`); this story gave no wording before. | PO |
| ~~[UX-SHF-3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/02-shift.md#open-questions) — gap count~~ | `US-FND-SHF-002` | ~~Keep a gap count next to the horizon marker?~~ **Resolved 2026-10-01 (PO): keep it** — `shf-003.gap-count`. The per-date alerts stay; their wording is UX-SHF-2 | — |
| UX-0-1 — Indonesian screen text | all stories | Screen text in UX is English for now; the screens are Indonesian. Second column per table, or a separate translation file keyed by the same keys? | PO |
| H-8 | `US-FND-SHF-001` | Keys (2026-10-01): is `ENTERPRISE_SHIFT.shift_label` unique per enterprise among active rows? | `foundation-domain` / PO |

## Stories


| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-SHF-001`](#us-fnd-shf-001) | Define enterprise shifts & each site's shift hours | 🟡 Partly blocked | As an **implementor**, I want to declare the enterprise's shift labels once and give each site real start/end times for every one of them, so that every site has a complete, non-overlapping shift clock. → [detail](#us-fnd-shf-001) | `US-FND-SIT-001` |
| [`US-FND-SHF-002`](#us-fnd-shf-002) | Generate shift instances in advance | 🟢 Ready | As an **implementor**, I want real shifts generated per date per site in advance, so every production transaction always has a valid parent. → [detail](#us-fnd-shf-002) | `US-FND-SHF-001`, `US-FND-SIT-001` |

## Detail blocks

---

#### US-FND-SHF-001

**Define enterprise shifts & each site's shift hours**

**Status:** 🟡 Partly blocked — the overlap check (rule 2) needs two things decided first (both `TBD`): what a
shift's end time means exactly, and what to do when start and end are the same. AC-1–AC-9 can be built with
the H-1 hours.

> **In short:** the implementor declares the shift labels once for the whole enterprise, then gives each
> site real start and end times for every label. Every site ends up with a complete shift clock with no
> overlaps, which the real shifts are generated from.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to declare the enterprise's shift labels once and then
give each site its real start and end time for every one of them, so that every site has a complete,
non-overlapping shift clock from which instances can be generated.

**2. Context**

- **Why:** a shift is the smallest legitimate unit of analysis in manufacturing. Nearly every production
  figure is reported per shift ([F-02](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#what-this-module-is-for)).
- **Two levels** ([core concept 1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#core-concepts)):
  - **the company's shift:** just the label, declared once for the whole enterprise;
  - **a site's shift hours:** that label's real start and end time at one site.
  - **Every site must take up every shift:** each site must set hours for every one of the company's shifts.
- **What is not modelled:**
  - **No rotation or pattern** (core concept 2). Every declared shift happens on every working day, so there
    is no shift pattern at all.
  - **No break field** (H-2, resolved). The clock is start and end only. A real planned stop is recorded as
    a machine stop in the downtime log.
  - **No scheduled minutes** (core concept 5). The shift only gives ISO POT. PBT is worked out in the KPI layer.
- **Real hours are decided (H-1):** 3 company shifts, with the same local hours at both sites (they differ
  only in timezone):
  - Shift 1 07:00–14:59
  - Shift 2 15:00–22:59
  - Shift 3 23:00–06:59
- **Screens ([UX 02](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/02-shift.md)):** `SCR-FND-SHF-001` (the company's shift
  labels) and `SCR-FND-SHF-002` (a site's shift hours) are **not screens of their own**. They are the Shift
  panels on `SCR-FND-SIT-002` when the Enterprise or a site is selected.
- **Who and where:** the implementor, at a desktop, once while setting up the plant, then rarely.

**3. Expectation**

*The company's shift labels (Shift panel on `SCR-FND-SIT-002` when the Enterprise is selected = `SCR-FND-SHF-001`)*
- A list of the company's shifts. Each row shows the label and how many of the company's sites have set hours
  for it.
- A pencil icon on each row opens an **edit dialog** for the label. It is an
  ordinary field and can be edited in place.
- A main button "Add enterprise shift" (`sit-002.shift-ent.button.add`).
- **Delete** is refused if any site has already set hours for the label, and the message names those sites.
- Empty: "declare your first shift" (`sit-002.shift-ent.empty`) plus a warning: "Without shifts, no site can
  declare hours and no production transaction can be recorded." (`sit-002.shift-ent.empty.warning`)

*A site's hours (Shift panel on `SCR-FND-SIT-002` when a site is selected = `SCR-FND-SHF-002`)*
- A checklist with one line for each of the company's shifts: the label, its start time and its end time,
  in the site's local time.
- An "\<n\> missing" badge (`sit-002.shift-site.badge.missing`) for lines that have no hours yet. It's the same
  number as the badge on the site in the hierarchy views (`sit-001.badge.missing-shifts`,
  [`US-FND-SIT-001`](01-site-hierarchy.md#us-fnd-sit-001)).
- Once both times are set, a hint "crosses midnight" (`sit-002.shift-site.hint.crosses-midnight`) appears if the
  end time is earlier than the start time. The hint is worked out from the two times. **There is no checkbox**,
  no break field and no duration field.
- The panel only offers hours for labels that already exist. It never creates a label.
- Empty (the company has no shifts yet): "declare enterprise shifts first" (`sit-002.shift-site.empty`) with a
  link "Go to enterprise shifts" (draft) (`sit-002.shift-site.empty.link`) to that step, not a blank form.

*What the user can read within 3 seconds*
- How many company shifts exist, and for a site, how many still have no hours.

*States*

Screen text is copied word for word from [UX 01 § Screen text](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md#screen-text)
(the Shift section of `SCR-FND-SIT-002`; `SCR-FND-SHF-001`/`002` are retired and have no text of their own,
[UX 02](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/02-shift.md#screen-text)), with its
key. If they differ, UX 01 is right. `(draft)` = proposed wording, usable in a build, may still change.

| State | Company shift labels (Enterprise selected) | A site's hours (site selected) |
|-------|--------------------------------------------|--------------------------------|
| Default | List of labels (`sit-002.shift-ent.default`: N/A — no text) | One checklist line per company shift (`sit-002.shift-site.default`: N/A — no text) |
| Empty | See the Empty line above (`sit-002.shift-ent.empty`, `.empty.warning`) | See the Empty line above (`sit-002.shift-site.empty`, `.empty.link`) |
| Loading | A placeholder layout (`sit-002.shift-ent.loading`: N/A — no text) | A placeholder layout (`sit-002.shift-site.loading`: N/A — no text) |
| Error | A refused delete names the sites using the label: "Can't delete \<shift name\>. These sites have hours for it: \<site list\>." (draft) (`sit-002.shift-ent.error.delete-blocked`) | Hours that overlap another shift are refused, naming that shift: "These hours overlap \<shift name\>. Change the start or end time." (draft) (`sit-002.shift-site.error.overlap`). Everything typed stays in place |
| Offline | Not available: "You're offline. Site Hierarchy needs a connection to load and save changes." (draft) (`sit-002.shift-ent.offline`) | Not available: "You're offline. Site Hierarchy needs a connection to load and save changes." (draft) (`sit-002.shift-site.offline`) |
| Success | "\<shift name\> saved." (draft) (`sit-002.shift-ent.success`) | The checklist line is marked complete, the missing count goes down by one, and the site's badge in the hierarchy updates (`sit-002.shift-site.success`: N/A — no text) |
| No permission | Read-only, no pencil, no Add: "You can view shifts but not change them. Ask your Plant Admin/IT for access." (draft) (`sit-002.shift-ent.no-permission`) | Read-only, times not editable: "You can view this site's shift hours but not change them. Ask your Plant Admin/IT for access." (draft) (`sit-002.shift-site.no-permission`) |

*Labels, buttons and messages*

| Key | Where | Text |
|-----|-------|------|
| `sit-002.shift.heading` | Section heading (Enterprise and Site) | "Shift" |
| `sit-002.shift-ent.row.adopted` | Label row — how many sites set hours for it | "Hours set at \<count\> of \<total\> sites" (draft) |
| `sit-002.shift-ent.button.add` | Main button | "Add enterprise shift" |
| `sit-002.shift-ent.dialog.title-edit` | Pencil → edit dialog title | "Edit shift" (draft) |
| `sit-002.shift-ent.field.label` | Shift label field label | "Shift name" (draft) |
| `sit-002.shift-ent.action.delete` | Delete action label | "Delete" (draft) |
| `sit-002.shift-site.columns` | Checklist column headers (label, start, end) | "Shift" · "Start" · "End" (draft) |
| `sit-002.shift-site.badge.missing` | Badge — lines with no hours yet | "\<n\> missing" |
| `sit-002.shift-site.hint.crosses-midnight` | Hint once both times are set and end < start (no checkbox) | "crosses midnight" |
| `sit-001.badge.missing-shifts` | Alert badge on the site in the hierarchy, all three views; hidden at 0 (never "0") | "\<missing shift count\>" |

**4. Calculation**

*Crosses midnight*
- A shift crosses midnight when its end time is earlier than its start time.
- This is worked out every time from the two saved times. It is never saved as a field of its own
  ([F-02 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#92-site_shift--a-sites-real-startend-time-for-one-enterprise-shift)).

> [!note]- Exact formula (for developers)
> ```
> crosses_midnight = end_time < start_time
> ```

*Which minutes a shift takes up in the site's day*
- A shift that doesn't cross midnight takes up the minutes from its start time to its end time.
- A shift that crosses midnight takes up the minutes from its start time to midnight, plus the minutes from
  midnight to its end time on the next calendar day.

> [!note]- Exact formula (for developers)
> ```
> occupied = [start_time, end_time]                              when it does not cross midnight
> occupied = [start_time, 24:00) ∪ [00:00, end_time]             otherwise ([00:00, end_time] is on the next calendar day)
> ```

*No overlap within one site*
- No two active shifts at the same site may share any minute, including the part that runs past midnight.
- Worked example (H-1 hours): Shift 1 07:00–14:59, Shift 2 15:00–22:59, Shift 3 23:00–06:59. Shift 3
  crosses midnight and takes up 23:00–24:00 plus 00:00–06:59 of the next day. Nothing collides, because
  Shift 1 starts at 07:00.
- Change Shift 1 to 06:30–14:59 → 06:30–06:59 collides with the end of Shift 3 → refused, naming "Shift 3".

> [!note]- Exact formula (for developers)
> ```
> for any two active SITE_SHIFT rows with the same site_id:
>     occupied(a) ∩ occupied(b) = ∅          (including the part past midnight)
> ```

*How many shifts a site is missing*
- The number of the company's active shifts, minus the number this site has set hours for.
- Example: 3 − 2 = 1.

> [!note]- Exact formula (for developers)
> ```
> missing(site) = COUNT(ENTERPRISE_SHIFT active) − COUNT(SITE_SHIFT active WHERE site_id = site)
> ```

*How many sites use a label*
- The number of sites that have set active hours for that company shift.
- A label can be deleted only when this number is 0.

> [!note]- Exact formula (for developers)
> ```
> adopted(label) = COUNT(SITE_SHIFT active WHERE enterprise_shift_id = label)
> ```

*Every shift counts*
- There are no non-production shifts: every real shift fills Availability's POT (core concept 6).

*Not computed here*
- Duration, scheduled minutes and PBT are not worked out here. POT is the real shift's end minus its start,
  worked out where it's used ([`US-FND-SHF-002`](#us-fnd-shf-002), KPI layer).
- A real planned stop, including istirahat (breaks), is subtracted from POT to give PBT in the KPI layer,
  never here.

> [!note]- Exact formula (for developers)
> ```
> POT = end_datetime − start_datetime                (of the generated SHIFT_INSTANCE)
> PBT = POT − planned stops                          planned stop = ASSET_STATE_LOG row with downtime_category = 'planned'
>                                                    (KPI layer only, never in this module)
> ```

*Edge cases*
- A shift whose start time and end time are the same (a zero-length or 24-hour window) is not covered by
  F-02. `TBD — perlu konfirmasi PO` (question for `foundation-domain`).
- F-02 writes the windows as 07:00–14:59 / 15:00–22:59. It doesn't say whether the end time is the last
  minute the shift takes up (inclusive) or the exact moment it ends. `TBD — perlu konfirmasi PO`
  (`foundation-domain`). This decides whether 14:59 and 15:00 touch or leave a one-minute gap. The overlap
  check must use the same meaning.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `ENTERPRISE`, `SITE.timezone` |
| **Writes** | `ENTERPRISE_SHIFT` (`enterprise_shift_id`, `enterprise_id`, `shift_label`, `valid_from`, `valid_to`) per [F-02 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#91-enterprise_shift--the-shift-label-declared-once-at-enterprise-level) · `SITE_SHIFT` (`site_shift_id`, `site_id`, `enterprise_shift_id`, `start_time`, `end_time`, `valid_from`, `valid_to`) per [F-02 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#92-site_shift--a-sites-real-startend-time-for-one-enterprise-shift) |
| **Not stored, not offered** | `crosses_midnight`, `break_minutes`, `duration_minutes`, `scheduled_minutes` |
| **Don't exist: don't add** | `SHIFT_DEFINITION`, `SHIFT_PATTERN`, any crew rotation. No new fields |
| **Referred to, not read or written here** | `SHIFT_INSTANCE` (generated in [`US-FND-SHF-002`](#us-fnd-shf-002)) · `ASSET_STATE_LOG` (`downtime_category = 'planned'`, used by the KPI layer for PBT) |

**6. Rules & constraints**

1. Each site has exactly one active set of hours for each company shift. Every site must take up every
   shift: a site can't skip one.
2. Two shifts at the same site never share any minute, including across midnight. This is checked when hours
   are entered and when they're edited. Hours that overlap are refused, and the message names the other shift.
3. Start and end times are in the **site's local time** (its timezone). The real shift times worked out from
   them are saved in UTC ([`US-FND-SHF-002`](#us-fnd-shf-002)).
4. The label can be edited in place. A company shift can't be deleted while any
   site has hours for it, and the refusal names those sites.
5. Changing a site's hours ends the old record (it gets an end date) and starts a new one. The old record is
   never edited in place. Real shifts that are already closed are never affected (core concept 7).
6. There is no production-shift flag. Every declared shift counts toward OEE; don't add
   `is_production_shift` (removed from F-02 §9.1, 2026-09-23).
7. No breaks and no rotation. A real planned stop, including istirahat, is recorded as a planned machine stop
   in the downtime log, and is subtracted from POT to give PBT in the KPI layer, never here.
8. The Shift panel never creates a company shift. Declaring the company's shifts is a separate step that
   comes first.
9. **Who may change it:**
   - creating, editing or deactivating a company shift needs **enterprise access** (or tenant access);
   - creating or editing a site's hours needs **site access** (or broader);
   - viewing either one only needs access to that item, at any level.

**7. Acceptance criteria**

- **AC-1** **Given** no shifts exist, **when** the implementor declares "Shift 1", "Shift 2", "Shift 3",
  **then** three company shifts exist and every site's Shift panel and hierarchy badge
  show 3 missing.
- **AC-2 (site hours)** **Given** site JKT1 (`Asia/Jakarta`), **when** the implementor enters 07:00–14:59,
  15:00–22:59, and 23:00–06:59, **then** three sets of site hours are saved, the checklist is complete, the
  badge disappears, and the Shift 3 line shows the hint "crosses midnight" with no checkbox anywhere.
- **AC-3 (overlap)** **Given** Shift 3 is saved as 23:00–06:59 at JKT1, **when** Shift 1 is entered as
  06:30–14:59, **then** it is refused with "These hours overlap Shift 3. Change the start or end time."
  (`sit-002.shift-site.error.overlap`) and everything typed stays in place.
- **AC-4 (no break, no duration)** **Given** the Shift panel is open, **when** the implementor looks for a
  break, duration, or scheduled-minutes field, **then** none exists.
- **AC-5 (edit label)** **Given** "Shift 1" is used by two sites, **when** the implementor renames it
  "Morning" through the pencil dialog, **then** the label is saved in place and both sites' hours are
  unchanged.
- **AC-6 (delete blocked)** **Given** "Shift 2" is used by JKT1 and TKY1, **when** the implementor tries
  to delete it, **then** the delete is refused with "Can't delete Shift 2. These sites have hours for it:
  \<site list\>." (`sit-002.shift-ent.error.delete-blocked`), the list naming both sites.
- ~~**AC-7 (non-production)**~~ **Removed 2026-09-23:** non-production shifts no longer exist (F-02 core
  concept 6). The ID is not reused.
- **AC-8 (prerequisite)** **Given** the company has no shifts yet, **when** a site's Shift panel is opened,
  **then** it shows "declare enterprise shifts first" with the link "Go to enterprise shifts" to that step and
  offers no time inputs.
- **AC-9 (permission)** **Given** a user without access to the relevant enterprise or site (and without
  tenant access), **when** they open either the labels screen or a site's Shift panel, **then** they can
  only read.

**8. Metrics & events**

- **Metric:** `site_shift_adoption_complete`: the share of active sites that have hours for every company
  shift (none missing). Baseline **0** (on 2026-09-03 no company shift and no site hours exist yet); target
  **100%** before the pilot line records its first shift. Measured from the company shifts and the site
  hours saved.
- **Counter-metric:** how many times site hours are changed in the first 30 days. A high number means the
  hours were guessed rather than confirmed with Operations.
- **Events:** `enterprise_shift_created` / `enterprise_shift_updated` (`shift_label`) ·
  `enterprise_shift_delete_blocked` (`adopting_site_count`) · `site_shift_declared` (`site_id`,
  `crosses_midnight`) · `site_shift_overlap_rejected` (`site_id`, `conflicting_enterprise_shift_id`).

**Dependencies:** `US-FND-SIT-001`

---

#### US-FND-SHF-002

**Generate shift instances in advance**

**Status:** 🟢 Ready

> **In short:** real shifts are generated ahead of time, one per site, per shift, per date, so every
> production transaction always has a shift to belong to. The implementor watches how far ahead they go and
> whether any are missing.

> This story **is** the `SHIFT_INSTANCE` generator. **`production-domain` builds and owns the generation mechanism** ([decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-10-b1-shift-instance-generator-owner.md); [F-02 §H-6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#94-open-questions)).

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want real shifts generated per date per site in advance,
so every production transaction always has a valid parent.

**2. Context**

- **Why:** if the calendar is worked out on the fly, a record that arrives on a day with no generated shift
  loses its context. The real shift is the item the whole product refers to most
  ([F-02 core concept 3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#core-concepts)).
- **Source:** generated from each site's shift hours, the start and end time a site has set for every
  company shift ([`US-FND-SHF-001`](#us-fnd-shf-001)). One real shift per site, per shift, per date
  ([F-02 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#93-shift_instance--one-real-shift-on-one-date-at-one-site)).
- **Timing is decided:** every day, before the business day begins (H-6).
- **Owner:** the mechanism and the list of production dates belong to `production-domain`, **confirmed**
  ([B1 decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-10-b1-shift-instance-generator-owner.md)).
- **Who and where:** the implementor, at a desktop. It runs by itself once set up.

**3. Expectation**

- A calendar for each site (`SCR-FND-SHF-003`, [UX 02](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/02-shift.md)) shows
  the real shifts generated for each date, each with its start, end and status (open or closed).
- A **horizon** marker shows the last date that has shifts generated. Next to it, a **gap count** shows how many
  gap alerts the shown date range has ("\<n\> gap(s) in this range", `shf-003.gap-count`); it is hidden at 0, never
  "0". Selecting it opens the generate control (the date-range picker of `shf-003.empty.generate`).
- What the user can read within 3 seconds: whether the horizon is still far enough ahead, and whether any
  date is missing a shift or has one twice. A production date with no shift is an **alert**, not a blank
  page (H-6: no gaps allowed).

Screen text is copied word for word from [UX 02 § Screen text](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/02-shift.md#screen-text)
(`SCR-FND-SHF-003`), with its key. If they differ, UX 02 is right. `(draft)` = proposed wording, usable in a
build, may still change.

| State | What the user sees |
|-------|--------------------|
| Default | A calendar per site; each real shift reads "\<shift label\> · \<start\>–\<end\> · \<status\>" (draft) (`shf-003.default`) |
| Empty | No real shifts yet → "No shifts generated yet" (`shf-003.empty`) plus a "Generate shifts" (draft) button (`shf-003.empty.generate`) with a date-range picker |
| No shift hours | The site has no declared shift hours (no `SITE_SHIFT` rows), so there is nothing to generate. No "Generate shifts" button; instead a link "Declare shift hours for \<site\>" (`shf-003.empty.no-hours`) that opens the site's Shift panel in Site Hierarchy (`SCR-FND-SIT-002`) |
| Loading | A calendar placeholder (`shf-003.loading`: N/A — no text) |
| Error | "Generation failed on date `<x>` — earlier dates remain saved" (`shf-003.error.generation-failed`). One bad date never cancels the whole run. A second shift for the same site, shift and date is refused with its own message: "\<site\> already has \<shift label\> on \<date\>. It was not added again." (draft) (`shf-003.error.duplicate`) |
| Offline | Not available: "You're offline. Shift calendar needs a connection to load and generate shifts." (draft) (`shf-003.offline`) |
| Success | A summary "`<n>` shift instances created for `<range>`" (`shf-003.success`), plus the new horizon |
| No permission | Read-only: "You can view shifts but not generate them. Ask your Plant Admin/IT for access." (draft) (`shf-003.no-permission`) |

*Labels and markers*

| Key | Where | Text |
|-----|-------|------|
| `shf-003.title` | Page title | "Shift calendar" (draft) |
| `shf-003.status.open` · `shf-003.status.closed` | Status label of a real shift (stored `open` / `closed`) | "Open" · "Closed" (draft) |
| `shf-003.horizon` | Horizon marker — last date with generated shifts | "Shifts generated up to \<date\>" (draft) |
| `shf-003.gap-count` | Next to the horizon marker — number of gap alerts (`shf-003.alert.gap`) in the shown date range; hidden at 0. Selecting it opens the generate control | "\<n\> gap(s) in this range" |
| `shf-003.alert.gap` | A production date missing a real shift for one of the site's shifts — an alert, not an empty cell (AC-7) | "No \<shift label\> generated for this date." (draft; see UX-SHF-2) |
| `shf-003.not-scheduled` | A date not in the production-date list — not scheduled, not a gap (AC-2) | "Not scheduled" (draft) |

**4. Calculation**

*When each real shift starts and ends*

For every production date that `production-domain` declares for a site, and every active set of hours at
that site:
- the shift starts on that date at its start time, in the site's timezone;
- it ends on that date at its end time, or on the next day if it crosses midnight (end time earlier than
  start time). That "next day" is worked out, never saved;
- its business date is the date the shift **starts**
  ([F-02 core concept 4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#core-concepts), confirmed by PO).

> [!note]- Exact formula (for developers)
> ```
> start_datetime = d + SITE_SHIFT.start_time          (in SITE.timezone)
> end_datetime   = d + SITE_SHIFT.end_time            plus one calendar day when end_time < start_time
> business_date  = d                                  (the date the shift starts)
> ```

*What is not computed here*
- **No scheduled minutes are worked out or saved** (F-02 core concept 5). The real shift is only the clock.
- It gives ISO **POT**: the shift's end minus its start.
- PBT (POT minus planned stops) is worked out in the KPI layer from the planned machine stops in the
  downtime log ([`US-PROD-OEE-001`](../PRD-001-molcadx-core-q3-en.md#us-prod-oee-001--see-line-oee-for-the-running-shift)).
  Subtracting it here would put PBT into the shift clock and then subtract it a second time.
- There is no break deduction either (H-2).
- FOUNDATION does **not** decide which dates are worked. The retired working-day calendar is not read.

> [!note]- Exact formula (for developers)
> ```
> POT = end_datetime − start_datetime
> PBT = POT − planned downtime          (KPI layer, from ASSET_STATE_LOG rows with downtime_category = 'planned')
> ```

*Example (H-1 hours)*
- Shift 1 07:00–14:59 on 2026-08-07 at JKT1 (`Asia/Jakarta`) → it starts at 2026-08-07 07:00+07:00, ends at
  2026-08-07 14:59+07:00, and its business date is 2026-08-07.
- Shift 3 23:00–06:59 on the same date → it ends at 2026-08-08 06:59+07:00. Output recorded at 03:00 on the
  8th belongs to business date 2026-08-07.
- Three sets of site hours × 1 date → 3 real shifts. 20 sites × 3 shifts × 365 days ≈ 22,000 rows a year:
  the volume isn't the risk, getting the generation right is.

*Edge cases*
- A date that isn't in the production-date list gets no shift.
- Real shifts that are already closed are **not** generated again or changed.
- Running generation again on a range that already exists is safe: a site's shift can have only one real
  shift per business date, so a second one is refused.
- A site with **no** shift hours at all is never offered generation (PO 2026-10-01, [UX 02 state notes](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/02-shift.md#state-notes)):
  a run would create nothing and still report success, a dead end. The calendar shows `shf-003.empty.no-hours`
  instead. This is not AC-4: there the site has hours and the range already exists.
- A site that hasn't set hours for every company shift yet (the missing badge in
  [`US-FND-SHF-001`](#us-fnd-shf-001)) only gets real shifts for the shifts it has hours for, and the gap is
  shown as an alert.

> [!note]- Exact formula (for developers)
> ```
> UNIQUE (site_shift_id, business_date)          re-running a range is idempotent; a duplicate is refused
> ```

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `SITE_SHIFT` (`start_time`, `end_time`, `valid_from`, `valid_to`) · `SITE.timezone` · the production-date list owned by `production-domain` |
| **Not read (don't exist in F-02)** | `SHIFT_DEFINITION`, `SHIFT_PATTERN`, `SHIFT_CALENDAR`, `NON_WORKING_DAY` |
| **Writes** | `SHIFT_INSTANCE` (`shift_instance_id`, `site_shift_id`, `business_date`, `start_datetime`, `end_datetime`, `status`) per [F-02 §9.3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/02-shift.md#93-shift_instance--one-real-shift-on-one-date-at-one-site) |
| **Not real fields: don't add** | `scheduled_minutes`, `work_center_id`, `is_overtime` (H-5) |
| **Referred to by other modules** | Every operational transaction carries `shift_instance_id`, never an `ENTERPRISE_SHIFT` or `SITE_SHIFT` id · `ASSET_STATE_LOG` (`downtime_category = 'planned'`) is read by the KPI layer for PBT, not here |

**6. Rules & constraints**

1. Real shifts are generated in advance: every day, before the business day begins (H-6 timing). How far
   ahead beyond that is **`[proposed]` 90 days**.
2. All times are saved in UTC and shown in the site's timezone.
3. The business date is the date the shift starts (F-02 core concept 4, confirmed).
4. Changing a site's hours ends the old record and starts a new one. It **doesn't apply backwards**, and real
   shifts that are already closed don't change.
5. Every production record must point to a real shift, never to a company shift label or to a site's shift
   hours.
6. Running generation again is safe: each site's shift can have only one real shift per business date.
7. Statuses other than open and closed are `TBD` in F-02 §9.3. Screens show only those two, stored as
   `open` / `closed`.
8. Generation is offered only for a site with at least one active `SITE_SHIFT` row; otherwise the calendar
   links to where hours are declared (`shf-003.empty.no-hours`).
9. **Who may do it:** running generation needs **site access** (or enterprise or tenant access). Viewing only
   needs access to that item.

**7. Acceptance criteria**

- **AC-1** **Given** JKT1 has hours for three shifts and `production-domain` lists 90 production dates,
  **when** generation runs, **then** 270 real shifts exist, each open, each with its start and end worked out
  from its site hours on its business date, and no scheduled-minutes field is written.
- **AC-2 (non-production date)** **Given** 2026-08-17 is not in the production-date list, **when** generation
  runs, **then** there is no shift for that date and the calendar marks it "Not scheduled"
  (`shf-003.not-scheduled`) rather than as a gap.
- **AC-3 (crosses midnight)** **Given** Shift 3 is 23:00–06:59, **when** output is recorded at 03:00 on
  2026-08-08, **then** that entry counts toward business date 2026-08-07 and the shift's end falls on
  2026-08-08.
- **AC-4 (run again)** **Given** the range has been generated before, **when** generation is run again for
  the same range, **then** no shift exists twice and the summary reports 0 new shifts.
- **AC-5 (closed is final)** **Given** the 2026-08-01 shift is already closed, **when** JKT1's Shift 1
  hours are changed and generation runs again, **then** that shift's start and end do not change.
- **AC-6 (partial error)** **Given** generation fails on the 40th date, **when** the process stops, **then**
  the first 39 dates stay saved and the message names the failing date.
- **AC-7 (gap alert)** **Given** a production date has no real shift for one of the site's shifts, **when**
  the calendar is opened, **then** that date is shown as an alert reading "No \<shift label\> generated for this
  date." (`shf-003.alert.gap`, draft, UX-SHF-2 open), not as an empty cell.
- **AC-8 (no shift hours)** **Given** site TKY1 has no declared shift hours, **when** the implementor opens its
  shift calendar, **then** no "Generate shifts" button is shown, the link "Declare shift hours for TKY1"
  (`shf-003.empty.no-hours`) is shown, and following it opens TKY1's Shift panel in Site Hierarchy.
- **AC-9 (gap count)** **Given** the shown range of JKT1's calendar has 2 production dates each missing one shift,
  **when** the calendar is opened, **then** "2 gap(s) in this range" (`shf-003.gap-count`) shows next to the horizon
  marker, and selecting it opens the generate control. **Given** no gaps in the shown range, **then** no count is
  shown.

**8. Metrics & events**

- **Metric:** `hari_horizon_tersisa` (*horizon days remaining*): the number of days between today and the
  last generated shift. It **must not fall below 14 days** ([proposed] warning threshold). Measured from the
  real shifts saved.
- **Counter-metric:** how many records are refused because no real shift existed for them. Must be 0.
- **Events:** `shift_instances_generated` (`site_id`, `date_from`, `date_to`, `instance_count`) ·
  `shift_instance_missing` (`site_id`, `attempted_date`).

**Dependencies:** `US-FND-SHF-001`, `US-FND-SIT-001`

## Change notes (history — not needed to build)

- `US-FND-SHF-002` (2026-10-01, prd-sync `docs-molcadx` bf12721..c932974, PO decision): gap count `shf-003.gap-count` next to the horizon marker, AC-9; UX-SHF-3 closed.
- `US-FND-SHF-002` (2026-10-01, prd-sync `docs-molcadx` 725362a..bf12721, PO decision): new state "No shift hours" (`shf-003.empty.no-hours`), edge case, rule 8 and AC-8 — a site without `SITE_SHIFT` rows is not offered generation. Open item UX-SHF-3 (gap count indicator) added.
- Module intro (2026-08-28): section renamed from "Calendar & Shift" to "Shift". "Calendar" referred to the retired `SHIFT_CALENDAR`/`NON_WORKING_DAY` concept (`US-FND-SHF-003`, closed).
- `US-FND-SHF-001` (2026-09-02): no-overlap-within-one-site rule resolved by PO.
- `US-FND-SHF-001`: `SCR-FND-SHF-001` and `SCR-FND-SHF-002` retired as standalone screens and folded into the Shift panels of `SCR-FND-SIT-002`.
- `US-FND-SHF-002` (2026-09-03): B1 raised in the contest review — generator ownership was unassigned and F-02 was downgraded to `DRAFT`; implementation was on hold until PO confirmed ownership.
- `US-FND-SHF-002` (2026-09-10): B1 closed — `production-domain` owns the generator; F-02 §H-6 updated and F-02 restored to `ACTIVE` the same day; the "do not start" blocker lifted.
- `US-FND-SHF-002` (2026-08-12): `business_date` = shift start date confirmed by PO.
- `US-FND-SHF-004` (2026-09-03): BLOCKED. `CREW` was one of three entities the removed `US-FND-ORG-003` would have built. The story text below the banner is kept exactly as it stood before the block.
- `US-FND-SHF-004`: pattern-proposed crew retired together with `SHIFT_PATTERN` and the rotation concept.
- All stories (2026-09-23): detail blocks rewritten from one dense table into numbered sections, and
  "subject whose IDP-asserted scope claim covers …" replaced by *site access* / *enterprise access* /
  *work-center access* (defined in the README glossary). Meaning unchanged; UI copy kept verbatim.
- All stories (2026-09-23, second pass): rewritten in plain language. Entity and field names now appear only in
  section 5 and in the collapsible "Exact formula (for developers)" boxes; everywhere else items are named in
  plain words (company shift, site hours, real shift, machine stop, crew). Meaning unchanged; UI copy kept
  verbatim.
- `US-FND-SHF-001`/`SHF-002` (2026-09-23, rule 1b sync, PO decision): `ENTERPRISE_SHIFT.is_production_shift` removed from F-02 §9.1 and the non-production-shift concept dropped — every declared shift counts toward OEE. Removed: the flag column, rule 6's flag requirement and its per-site-override TBD, AC-7, the event property, the SHF-002 exclusion edge case and its formula box, and the SHF-002 read. AC-7's ID is retired, not reused.
- 2026-09-23 (rule 1b, full UX cross-check): every story in this file now names the UX screen it is built on.
- `US-FND-SHF-004` (2026-09-27, prd-sync docs-molcadx 3cb17fa..7ad3ed8): **closed (retired)** by PO decision
  2026-09-25 — crew assignment is not needed for now; `SCR-FND-SHF-004` retired in UX 02 and its `shf-004.*`
  keys removed, not reused (glossary *Crew (regu)* updated the same day). Status, heading and Stories row
  changed; the detail text is kept for history; the two crew open items are parked. No live story in this module
  depends on it. The ID is kept and not reused.
- `US-FND-SHF-001` (2026-09-27, prd-sync docs-molcadx 3cb17fa..7ad3ed8): screen text for the Shift section of
  `SCR-FND-SIT-002` (Enterprise labels and site hours) copied word for word from UX 01 with keys; the States table
  now has one column per panel; AC-3, AC-6 and AC-8 quote the text.
- `US-FND-SHF-002` (2026-09-27, prd-sync docs-molcadx 3cb17fa..7ad3ed8): `SCR-FND-SHF-003` screen text copied from
  UX 02 with keys (title, statuses, horizon, gap alert, not-scheduled marker, duplicate, offline, no permission);
  AC-2 and AC-7 quote the text; open items UX-SHF-2 and UX-0-1 added.
