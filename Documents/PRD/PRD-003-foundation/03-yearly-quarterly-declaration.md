# PRD-003 · Yearly & Quarterly Declaration (`FYR`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-03. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Terms like *enterprise access*, *effective dating* and *implementor* are defined in the [README glossary](README.md#glossary).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.

> Entity spec: [F-03](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/03-yearly-quarterly-declaration.md) (**🟢 ACTIVE, ready to implement**).
>
> This module answers one question and nothing else: **from when to when does a business year, and its
> quarters, run?** It is a pure declaration of a reporting window. It does not decide which days are
> worked, and it calculates no production figure. Which dates production is planned on belongs to
> `production-domain`; shift structure belongs to [Shift](02-shift.md).
>
> **Screens:** declaration lives in the **Fiscal Year panel inside `SCR-FND-SIT-002`**.
> - On the `ENTERPRISE` node: declare.
> - On a `SITE` node: **fully read-only** — shows which enterprise-wide `FISCAL_YEAR` applies. **No site override**, no button, no dialog, no `FISCAL_YEAR_SITE_OVERRIDE` (Y-2 / [`UX/03-fiscal.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/03-fiscal.md)).
>
> `SCR-FND-FSC-001` is **not a standalone screen**; the ID is kept and points at that panel ([`UX/01-site-hierarchy.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md)).

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| Y-3 | `US-FND-FYR-001`, `US-FND-FYR-002` | Keys (2026-10-01): is `fiscal_year_label` unique per enterprise, and may two active fiscal years of one enterprise overlap? Not a blocker: the stories stay ready | `foundation-domain` / PO |

## Stories


| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-FYR-001`](#us-fnd-fyr-001) | Declare a fiscal year and how its quarters split | 🟢 Ready | As a **plant admin**, I want to declare when the business year starts and ends and how its quarters divide, so that "Q1 2026" means one date range everywhere instead of a different one per report. → [detail](#us-fnd-fyr-001) | `US-FND-SIT-001` |
| [`US-FND-FYR-002`](#us-fnd-fyr-002) | Correct a fiscal year without rewriting closed periods | 🟢 Ready | As a **plant admin**, I want a correction to a fiscal year to apply going forward only, so that totals already shared with management do not silently shift. → [detail](#us-fnd-fyr-002) | `US-FND-FYR-001` |
| [`US-FND-FYR-003`](#us-fnd-fyr-003) | Resolve any business date to its fiscal year and quarter | 🟢 Ready | As a **plant manager**, I want every production date to resolve to exactly one fiscal year and quarter, so that period comparisons add up instead of double-counting or dropping days. → [detail](#us-fnd-fyr-003) | `US-FND-FYR-001` |

## Detail blocks

---

#### US-FND-FYR-001

**Declare a fiscal year and how its quarters split**

**Status:** 🟢 Ready

> **In short:** the implementor declares, once for the whole enterprise, when the business year starts
> and ends and how its quarters split. After that, "Q1 2026" means one date range in every report.

> **Not a standalone live screen.** UI for this story is the Fiscal Year panel of `SCR-FND-SIT-002`
> (`ENTERPRISE` node). `SCR-FND-FSC-001` is retired into that panel ([`UX/03-fiscal.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/03-fiscal.md)).

**1. Story**

As a **plant admin**, I want to declare when the business year starts and ends and how its quarters divide,
so that "Q1 2026" means one date range everywhere instead of a different one per report.

**2. Context**

- **Why:** without a fixed fiscal window, "Q1 2026" means a different date range in every report and the
  totals never match ([F-03 §3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/03-yearly-quarterly-declaration.md#why-it-matters--what-goes-wrong-without-it)).
  If nobody fixes how quarters split, week-based reporting drifts apart between spreadsheets and the system.
- **When:** declared once per enterprise while setting up, then rarely touched.
- **Who and where:** the implementor, at a desktop, in a short session.
- **For the whole enterprise only.** A site can't set its own year (decided as Y-2 in F-03), so a company
  with several sites declares this once.

**3. Expectation**

*Layout*
- On the Fiscal Year panel of `SCR-FND-SIT-002` when the Enterprise is selected (not a separate
  `SCR-FND-FSC-001` screen), headed "Fiscal Year" (`sit-002.fiscal.heading`): a list of the declared fiscal
  years, newest first, each with its label, mode, date range and status. Row: "\<label\> · \<mode\> · \<start date\>
  to \<end date\>" (`sit-002.fiscal-ent.row`); row status: TBD — PO (`sit-002.fiscal-ent.row.status`).
- A main button "Declare fiscal year" (`sit-002.fiscal-ent.button.declare`).
- When a site is selected, the same panel is **read-only** (it shows which company-wide year applies there).
  There is no way to override it. Its text is in the *Site panel states* table below.
- What the user can read within 3 seconds: whether any fiscal year covers today.

*The form depends on the mode*
- **Monthly mode** → the user picks a start month and an end month. The start and end dates are worked out
  from them and shown read-only.
- **Weekly (ISO) mode** → the user types a start date and an end date, **and the quarter pattern choice
  appears**.
- In monthly mode the quarter pattern choice is **not shown at all**, not just greyed out. Showing it would
  invite someone to fill in a value that means nothing in that mode
  ([F-03 pitfall 2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/03-yearly-quarterly-declaration.md#common-pitfalls)).

*States (Enterprise panel)*

Screen text is copied word for word from [UX 01 § Screen text](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md#screen-text)
(`SCR-FND-SIT-002` Fiscal Year rows marked for this story), with its key. If this story and UX 01 differ,
UX 01 is right. `(draft)` = proposed wording, usable in a build, may still change. `TBD — PO` = the build
shows `[TBD copy: <key>]`.

| State | What the user sees |
|-------|--------------------|
| Default | `sit-002.fiscal-ent.default`: N/A — no text: list of declared years |
| Empty (enterprise) | This year isn't declared yet → "declare your first fiscal year" (`sit-002.fiscal-ent.empty`), with what that means: "Until a fiscal year is declared, reports can't group figures by year or quarter." (draft) (`sit-002.fiscal-ent.empty.meaning`) |
| Loading | `sit-002.fiscal-ent.loading`: N/A — no text: PRD shows "a list placeholder" only |
| Save error | The form keeps what was typed: "Could not save. Your entries are not lost." (`sit-002.fiscal-ent.error.save`) |
| Offline | Not available; master data needs a connection: "You're offline. Site Hierarchy needs a connection to load and save changes." (draft) (`sit-002.fiscal-ent.offline`) |
| Success | The new year appears at the top with its date range written out in full: "\<label\> · \<mode\> · \<start date\> to \<end date\>" (e.g. "FY2026 · monthly · 2026-01-01 to 2026-12-31") (`sit-002.fiscal-ent.success`) |
| No permission | "Only subjects whose IDP-asserted scope claim covers the enterprise (or a broader `tenant` claim) can declare a fiscal year. Contact your plant admin." (`sit-002.fiscal-ent.no-permission`) |

*States (Site panel, read-only)*

| State | What the user sees |
|-------|--------------------|
| Default | Which company-wide year applies here: "\<label\> · \<start date\> to \<end date\> · set for the whole company" (draft) (`sit-002.fiscal-site.default`) |
| Empty | No year applies: "fiscal context not set" (`sit-002.fiscal-site.empty`) |
| Loading | "Loading fiscal year…" (draft) (`sit-002.fiscal-site.loading`) |
| Error | "Couldn't load the fiscal year. Try again." (draft) (`sit-002.fiscal-site.error`) |
| Offline | "You're offline. Site Hierarchy needs a connection to load and save changes." (draft) (`sit-002.fiscal-site.offline`) |
| Success | `sit-002.fiscal-site.success`: N/A — read-only section, no write action (Y-2) |
| No permission | "You don't have access to this site's fiscal year. Ask your Plant Admin/IT for access." (draft) (`sit-002.fiscal-site.no-permission`) |

*Labels and messages*

| Key | Where | Text |
|-----|-------|------|
| `sit-002.fiscal-ent.field.labels` | Form field labels (label, mode, start/end month, start/end date, quarter pattern) | "Name" · "Mode" · "Start month" · "End month" · "Start date" · "End date" · "Quarter pattern" (draft) |
| `sit-002.fiscal-ent.field.mode.options` | Mode option labels (monthly, weekly ISO) | "Monthly" · "Weekly (ISO)" (draft) |
| `sit-002.fiscal-ent.week-count` | Year length in weeks shown on the form (weekly mode) | "\<count\> weeks" (draft) |
| `sit-002.fiscal-ent.warning.not-monday` | Weekly start date not a Monday (saved with warning) | "This date isn't a Monday. ISO weeks start on Monday, so every week in this year will start mid-week." (draft) |
| `sit-002.fiscal-ent.error.overlap` | Overlaps an existing year, names it and its date range | "These dates overlap \<label\> (\<start date\> to \<end date\>). Choose dates outside it." (draft) |
| `sit-002.fiscal-ent.error.duplicate-label` | Label already used in the enterprise | "\<label\> is already used. Use another name." (draft) |
| `sit-002.fiscal-ent.error.label-too-long` | Label over 20 characters | "Name can be up to 20 characters." (draft) |

Text limits: the fiscal year label is at most 20 characters.

**4. Calculation**

*Monthly mode*

The start and end dates are worked out, never typed: the year starts on the first day of the start month
and ends on the last day of the end month. Each quarter is three calendar months, counted from the start
month.
- Example: FY2026 with start month 1 (January) and end month 12 → it runs 2026-01-01 to 2026-12-31. March is
  2 months after the start, so it's in **Q1**. October is 9 months after the start, so it's in **Q4**.
- Example, a year that doesn't start in January: start month 4 → April is the first month, so it's in **Q1**,
  and March is 11 months after the start, so it's in **Q4**. This is the case the simple "month divided by 3,
  rounded up" rule gets wrong.

> [!note]- Exact formula (for developers)
> ```
> start_date     = first day of start_month
> end_date       = last day of end_month
> quarter(month) = floor(((month − start_month + 12) mod 12) / 3) + 1
> ```
>
> - `start_month = 1`, `end_month = 12` → `start_date = 2026-01-01`, `end_date = 2026-12-31`.
>   March: `floor(((3 − 1 + 12) mod 12)/3) + 1 = floor(2/3) + 1 = ` **Q1**. October: `floor(9/3) + 1 = ` **Q4**.
> - `start_month = 4` → April: `floor(0/3)+1 =` **Q1**. March: `floor(((3 − 4 + 12) mod 12)/3) + 1 = floor(11/3) + 1 =` **Q4**.
>   A naive `ceil(month/3)` gets this wrong.

*Weekly (ISO) mode*

The start and end dates are typed in directly; they are what actually set the year's range. Every quarter is
**13 weeks, whatever the pattern**. The pattern only decides how the three periods inside a quarter split:
`4-5-4` → 4, 5, 4 weeks · `4-4-5` → 4, 4, 5 weeks · `5-4-4` → 5, 4, 4 weeks. Weeks are counted from the
start date; weeks 1–13 are Q1, 14–26 are Q2, and so on, and anything after the 52nd week stays in Q4.
- Example: the year starts on 2026-01-05 (a Monday), with pattern `4-5-4`. Q1 is the first 13 weeks and
  ends on 2026-04-05. Inside it: period 1 is 2026-01-05 to 2026-02-01 (4 weeks), period 2 is 2026-02-02 to
  2026-03-08 (5 weeks), and period 3 is 2026-03-09 to 2026-04-05 (4 weeks).

> [!note]- Exact formula (for developers)
> ```
> week_index(date) = floor((date − start_date) / 7 days)
> quarter          = min(4, floor(week_index / 13) + 1)
> ```
>
> - `start_date = 2026-01-05`, pattern `4-5-4`: Q1 is `week_index` 0 to 12, ending `2026-04-05`. Period 1
>   `2026-01-05` to `2026-02-01`, period 2 `2026-02-02` to `2026-03-08`, period 3 `2026-03-09` to `2026-04-05`.

*Edge cases*
- A **53-week year** has one week left over after four 13-week quarters. The "stays in Q4" rule above
  (the `min(4, …)` in the formula) puts it in Q4 instead of inventing a Q5. The form must show the year's
  length in weeks, so the implementor sees 53 rather than finding out later.
- Start month and end month **don't apply** in weekly mode and must not be saved for such a year. The quarter
  pattern **doesn't apply** in monthly mode and must not be saved either.
- A weekly-mode start date that isn't a Monday is accepted but flagged: ISO weeks start on Monday, and
  starting mid-week makes every week boundary confusing.
- Two fiscal years of the same enterprise whose date ranges overlap are refused. See
  [`US-FND-FYR-003`](#us-fnd-fyr-003), where an overlap would put one date in two years.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `FISCAL_YEAR` (`fiscal_year_id`, `enterprise_id`, `fiscal_year_label`, `calendar_basis`, `start_month`, `end_month`, `start_date`, `end_date`, `period_pattern`, `valid_from`, `valid_to`) per [F-03 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/03-yearly-quarterly-declaration.md#91-fiscal_year--the-reporting-window-declaration) |
| **Reads only** | `ENTERPRISE` ([Site Hierarchy §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/01-site-hierarchy.md)) |
| **Not real fields: don't add** | No new fields. **No `site_id`**: enterprise level only, resolved as Y-2 |

This entity connects to nothing else: not to Shift, not to any working-day concept. It only tags a date
range with a year and quarter label.

**6. Rules & constraints**

1. Every fiscal year must have its enterprise, a label, a mode, a start date, an end date, and a start and
   end of validity.
2. Start month and end month are required in monthly mode and **must not be saved** in weekly mode.
3. The quarter pattern is required in weekly mode and **must not be saved, nor shown, in monthly mode**.
   Whether it's hidden depends on each year's own mode.
4. Both modes ship as real options the user can pick: monthly or weekly (ISO), stored as `monthly` /
   `weekly_iso`. Molca's own year happens to be monthly (Y-1). That's a fact about Molca's calendar, not a
   reason to hold back weekly mode.
5. A fiscal year label can't repeat within an enterprise.
6. Two fiscal years of one enterprise can't have overlapping date ranges.
7. A site can't set its own year. No site field may be added without a `foundation-domain` decision.
8. **Who may do it:** declaring a fiscal year needs **enterprise access** (or tenant access) only. Site access
   alone isn't enough, because a fiscal year doesn't belong to any site.
9. Needs a connection.

**7. Acceptance criteria**

- **AC-1** **Given** no fiscal year exists, **when** the implementor declares FY2026 in monthly mode with
  start month 1 and end month 12, **then** it is saved and its date range shows as 2026-01-01 to
  2026-12-31, worked out rather than typed.
- **AC-2 (mode switch)** **Given** the form is open in monthly mode, **when** the implementor switches to
  weekly mode, **then** start date and end date become fields to type in and the quarter pattern choice
  appears.
- **AC-3 (pattern hidden)** **Given** the form is in monthly mode, **when** the implementor looks for the
  quarter pattern, **then** it is **not on the form at all**, not merely greyed out, and no quarter pattern
  is saved for that year.
- **AC-4 (non-January year)** **Given** FY2026 declared with start month 4, **when** March 2027 is looked up,
  **then** it falls in **Q4**, not Q1.
- **AC-5 (weekly quarters)** **Given** weekly mode with start date 2026-01-05 and pattern `4-5-4`, **when**
  Q1 is shown, **then** it runs 2026-01-05 to 2026-04-05 and its three periods are 4, 5, and 4 weeks.
- **AC-6 (53-week year)** **Given** a weekly-mode year that spans 53 weeks, **when** its quarters are worked
  out, **then** the extra week falls in Q4, no Q5 is created, and the form shows the year's length as 53
  weeks ("\<count\> weeks" (draft), `sit-002.fiscal-ent.week-count`).
- **AC-7 (validation)** **Given** a fiscal year whose date range overlaps an existing one for the same
  enterprise, **when** it is saved, **then** it is refused, naming the overlapping year and its date range:
  "These dates overlap \<label\> (\<start date\> to \<end date\>). Choose dates outside it." (draft)
  (`sit-002.fiscal-ent.error.overlap`).
- **AC-8 (validation)** **Given** weekly mode with a start date that isn't a Monday, **when** it is saved,
  **then** it is saved with a visible warning explaining that ISO weeks start on Monday: "This date isn't a
  Monday. ISO weeks start on Monday, so every week in this year will start mid-week." (draft)
  (`sit-002.fiscal-ent.warning.not-monday`).
- **AC-9 (permission)** **Given** a user with site access only, not enterprise access (and no tenant
  access), **when** they open this screen, **then** they can only read, and the message says enterprise
  access is required (`sit-002.fiscal-ent.no-permission`).
- **AC-10 (error)** **Given** the connection fails during save, **when** the save is tried, **then** the
  form keeps what was typed and the message says the data was not saved: "Could not save. Your entries are
  not lost." (`sit-002.fiscal-ent.error.save`).

**8. Metrics & events**

- **Metric:** `fiscal_year_declared_before_use`: whether a fiscal year covering the pilot line's first
  production date exists **before** that date. Baseline 0 (no fiscal year is declared on 2026-08-24).
  Target: declared before the pilot line records its first shift. Measured by comparing the declared fiscal
  years with the business date of the earliest real shift.
- **Counter-metric:** how many fiscal years are edited within 30 days of being declared. Any at all means the
  date range was guessed rather than taken from Finance.
- **Events:** `fiscal_year_declared` (`calendar_basis`, `period_pattern`, `week_count`, `enterprise_id`) ·
  `fiscal_year_overlap_rejected` (`enterprise_id`, `conflicting_label`).

**Dependencies:** `US-FND-SIT-001`

---

#### US-FND-FYR-002

**Correct a fiscal year without rewriting closed periods**

**Status:** 🟢 Ready

> **In short:** a fiscal year is never edited in place. A correction ends the old version and starts a new
> one from a future date, and it's refused if it would move any period that has already closed.

**1. Story**

As a **plant admin**, I want a correction to a fiscal year to apply going forward only, so that totals
already shared with management do not silently shift.

**2. Context**

- **Why:** editing a fiscal year that has already closed would rewrite reports people have already seen.
  F-03 calls this its pitfall number 1 and its third failure mode: a fiscal year edited after the fact
  quietly changes the totals of periods that have already closed
  ([F-03 §3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/03-yearly-quarterly-declaration.md#why-it-matters--what-goes-wrong-without-it)).
- **The danger:** nothing looks wrong afterwards. The numbers are simply different from the ones people
  remember.
- **Who and where:** it rarely happens; the implementor, at a desktop.

**3. Expectation**

- The selected fiscal year has an **"Amend"** action (`sit-002.fiscal-ent.button.amend`), not a form that
  edits it in place.
- Before confirming, an **impact list** shows:
  - how many closed periods fall inside the current date range;
  - how many production dates would move to a different year or quarter.

  Text: "\<count\> closed periods in this range" · "\<count\> production dates move to another year or quarter"
  (draft) (`sit-002.fiscal-ent.amend.impact`).
- What the user can read within 3 seconds: whether any closed period is affected.

Screen text is copied word for word from [UX 01 § Screen text](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/01-site-hierarchy.md#screen-text)
(`SCR-FND-SIT-002` Fiscal Year rows marked for this story), with its key. If this story and UX 01 differ,
UX 01 is right. `(draft)` = proposed wording, usable in a build, may still change.

| State | What the user sees |
|-------|--------------------|
| Blocked | If the date range has already closed, amending is refused straight away, with the reason. It is not offered and then refused on save: "Can't amend \<label\>. Its dates have already closed." (draft) (`sit-002.fiscal-ent.amend.blocked`) |
| Loading | The impact list shows a placeholder. The confirm button stays locked until the impact is worked out: "Working out the impact…" (draft) (`sit-002.fiscal-ent.amend.loading`) |
| Error | "Could not compute impact. No change was made." (`sit-002.fiscal-ent.amend.error.impact`) |
| Offline | Not available |
| Success | The old version gets an end date and a new version starts. Both show in a history list, headed "History" (draft) (`sit-002.fiscal-ent.amend.history`), so the change is easy to follow later |
| No permission | As in [`US-FND-FYR-001`](#us-fnd-fyr-001) |

*Messages*

| Key | Where | Text |
|-----|-------|------|
| `sit-002.fiscal-ent.amend.error.past-date` | Effective date in the past | "The effective date cannot be in the past. Closed periods must not change." |
| `sit-002.fiscal-ent.amend.error.closed-period` | Would move closed periods — names count and date range | "Can't save. This would move \<count\> closed periods (\<start date\> to \<end date\>). Closed periods must not change." (draft) |
| `sit-002.fiscal-ent.amend.error.gap` | Would leave dates in no fiscal year | "Can't save. \<start date\> to \<end date\> would belong to no fiscal year." (draft) |
| `sit-002.fiscal-ent.amend.mode-change` | Mode change treated as a new declaration — form says so | "Changing the mode declares a new fiscal year instead of amending this one." (draft) |

**4. Calculation**

*Versions*

A correction never overwrites. It ends the old version the day before the effective date and starts the new
version on the effective date, following FOUNDATION's shared rule for versions
([F-00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md)).

> [!note]- Exact formula (for developers)
> ```
> old.valid_to   = effective_date − 1 day
> new.valid_from = effective_date
> ```

*Closed periods affected* (the number the impact list shows)
- The number of closed real shifts, inside the new date range, whose quarter would be different under the
  new version than under the old one.
- Example: FY2026 runs 2026-01-01 to 2026-12-31. On 2026-09-01 the implementor changes the end month from
  12 to 11. Dates from 2026-12-01 on haven't happened yet, so nothing is affected (0) and the change goes
  ahead: the old version ends on 2026-08-31 and the new one starts on 2026-09-01.
- Counter-example: the same change tried on 2027-02-01, after December 2026 has closed. Every closed shift
  in December would move from FY2026 to no declared year at all, so more than 0 are affected and the change
  is **refused**, naming how many and the date range.

> [!note]- Exact formula (for developers)
> ```
> affected = COUNT(SHIFT_INSTANCE WHERE status = 'closed'
>                    AND business_date BETWEEN new_window_start AND new_window_end
>                    AND resolved_quarter(business_date, old_row) ≠ resolved_quarter(business_date, new_row))
> ```
>
> - First example → `affected = 0`. Counter-example → `affected > 0`.

*Edge cases*
- An effective date in the past is refused: that's exactly the rewrite this story exists to prevent.
- Changing the mode from monthly to weekly or back counts as a new declaration, not a correction, because
  every worked-out value changes meaning. The form must say so rather than quietly carrying values across.
- A correction that leaves a gap between the old and new date ranges is refused, because dates in the gap
  would belong to no fiscal year.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `FISCAL_YEAR` (`valid_from`, `valid_to` on the old row; a full new row for the amendment) |
| **Reads only** | `SHIFT_INSTANCE` (`business_date`, `status`), to compute the impact list |
| **Not real fields: don't add** | There is no `status` field on `FISCAL_YEAR`: `valid_from`/`valid_to` carry the lifecycle, as on every other FOUNDATION master |

No physical deletion, and no in-place edit of a closed window.

**6. Rules & constraints**

1. A fiscal year is never edited in place. A correction ends the old version and starts a new one.
2. The effective date can't be in the past.
3. A correction that would move any **closed** period into a different year or quarter is refused.
4. A correction that leaves a gap between date ranges is refused.
5. Changing the mode is a new declaration, not a correction.
6. **Who may do it:** correcting a fiscal year needs **enterprise access** (or tenant access).
7. Needs a connection.

**7. Acceptance criteria**

- **AC-1** **Given** FY2026 runs to 2026-12-31 and no December shift has closed, **when** the implementor
  changes the end month to 11 effective 2026-09-01, **then** the old version ends on 2026-08-31, a new
  version starts on 2026-09-01, and both appear in the history list.
- **AC-2 (closed period protection)** **Given** December 2026 shifts have closed, **when** the same change
  is tried, **then** it is refused, naming how many closed periods and which date range would move: "Can't
  save. This would move \<count\> closed periods (\<start date\> to \<end date\>). Closed periods must not
  change." (draft) (`sit-002.fiscal-ent.amend.error.closed-period`).
- **AC-3 (validation)** **Given** an effective date in the past, **when** the correction is saved, **then**
  it is refused with "The effective date cannot be in the past. Closed periods must not change."
  (`sit-002.fiscal-ent.amend.error.past-date`).
- **AC-4 (gap)** **Given** a correction that would leave 2026-12-01 to 2026-12-31 in no fiscal year,
  **when** it is confirmed, **then** it is refused because dates in the gap would belong to no year: "Can't
  save. \<start date\> to \<end date\> would belong to no fiscal year." (draft) (`sit-002.fiscal-ent.amend.error.gap`).
- **AC-5 (mode change)** **Given** the implementor changes the mode from monthly to weekly, **when** they
  confirm, **then** the system treats it as a new declaration and says so ("Changing the mode declares a new
  fiscal year instead of amending this one." (draft), `sit-002.fiscal-ent.amend.mode-change`), rather than
  carrying the start month across.
- **AC-6 (loading)** **Given** the impact list is still loading, **when** confirm is pressed, **then** the
  button isn't active yet and the system says the impact is still being worked out: "Working out the
  impact…" (draft) (`sit-002.fiscal-ent.amend.loading`).
- **AC-7 (history)** **Given** a correction has been made, **when** a report for a date before the effective
  date is opened, **then** it still uses the old version.
- **AC-8 (permission)** **Given** a user without enterprise access (and without tenant access), **when**
  they open the fiscal year, **then** there is no "Amend" action (`sit-002.fiscal-ent.button.amend`), and the
  access needed is explained (`sit-002.fiscal-ent.no-permission`).

**8. Metrics & events**

- **Metric:** `closed_period_totals_changed`: how many already-closed periods got a different year or quarter
  label after a correction. **Must be 0.** Measured by comparing the labels before and after each
  `fiscal_year_amended` event.
- **Counter-metric:** the share of correction attempts that are refused. A high share means the declaration
  form isn't capturing the right date range when the year is first declared.
- **Events:** `fiscal_year_amended` (`enterprise_id`, `effective_date`, `field_changed`) ·
  `fiscal_year_amend_blocked` (`enterprise_id`, `blocker_type`, `affected_closed_periods`).

**Dependencies:** `US-FND-FYR-001`

---

#### US-FND-FYR-003

**Resolve any business date to its fiscal year and quarter**

**Status:** 🟢 Ready

> **In short:** every production date maps to exactly one fiscal year and quarter, so period totals add
> up. A date with no fiscal year says "not declared" instead of being guessed.

**1. Story**

As a **plant manager**, I want every production date to resolve to exactly one fiscal year and quarter, so
that period comparisons add up instead of double-counting or dropping days.

**2. Context**

- **What it is:** the **lookup every other module uses**. Nothing in FOUNDATION uses the fiscal year
  itself; its only purpose is to be looked up when a number needs a period label
  ([F-03](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/03-yearly-quarterly-declaration.md#how-the-entities-connect)).
- **Why:** if a date can fall in two fiscal years, quarter totals count it twice. If it falls in none, the
  date quietly disappears from every period report. Neither problem shows in a single number; they only
  show when totals don't match.
- **Where:** used by every reporting screen, so it must be quick to call and must never guess.

**3. Expectation**

- Wherever a period label is shown, it reads as the year label plus the quarter, for example "FY2026 · Q3".
- A screen that compares periods states the exact date range each label covers, because that's the whole
  reason the module exists.

| State | What the user sees |
|-------|--------------------|
| Not declared | A date outside every declared fiscal year comes back as **"not declared"**, shown as such rather than guessed into the nearest year. The message names the date and points to the fiscal-year screen |
| Loading | The label area shows a placeholder. A number is never shown with a guessed label |
| Error | The number is shown with the label area marked unavailable, rather than a wrong label |
| Offline | The last labels looked up stay usable from storage on the device, because the fiscal year changes very rarely |
| No permission | not applicable — anyone who may see a figure may see its period label |

**4. Calculation**

*Looking up a date*
- Take the business date. Find the enterprise's fiscal year version that was **in effect on that date**
  (it had started by then and hadn't ended). Give back its label and the quarter the date falls in, using
  the same quarter rules as [`US-FND-FYR-001`](#us-fnd-fyr-001): months counted from the start month in
  monthly mode, 13-week blocks from the start date in weekly mode.

> [!note]- Exact formula (for developers)
> ```
> resolve(business_date) → (fiscal_year_label, quarter)
>
> row in effect:  valid_from ≤ business_date  AND  (valid_to IS NULL OR valid_to ≥ business_date)
>
> Monthly mode:  quarter    = floor(((month(business_date) − start_month + 12) mod 12) / 3) + 1
> Weekly mode:   week_index = floor((business_date − start_date) / 7 days)
>                quarter    = min(4, floor(week_index / 13) + 1)
> ```

*Examples*
- Monthly: FY2026 starting in month 1. The date 2026-08-14 is in month 8, 7 months after the start →
  **FY2026 · Q3**.
- Not starting in January: FY2026 starting in month 4. The date 2027-02-10 is in month 2, 10 months after
  the start → **FY2026 · Q4**.
- Weekly: the year starts on 2026-01-05. The date 2026-05-20 is 135 days later, which is in week 19 (counting
  from 0) → **FY2026 · Q2**.

> [!note]- Exact formula (for developers)
> - `start_month = 1`, `business_date = 2026-08-14` → month 8 →
>   `floor(((8 − 1 + 12) mod 12)/3) + 1 = floor(7/3) + 1 = 3` → **FY2026 · Q3**.
> - `start_month = 4`, `business_date = 2027-02-10` → month 2 →
>   `floor(((2 − 4 + 12) mod 12)/3) + 1 = floor(10/3) + 1 = 4` → **FY2026 · Q4**.
> - `start_date = 2026-01-05`, `business_date = 2026-05-20` → 135 days elapsed →
>   `week_index = floor(135/7) = 19` → `quarter = floor(19/13) + 1 = 2` → **FY2026 · Q2**.

*Edge cases*
- A date that matches **no** declared fiscal year comes back as "not declared". It's never rounded into the
  nearest year. The number it belongs to is left out of period totals, and the screen says so instead of
  quietly dropping it.
- A date that matches **more than one** fiscal year is a data fault, not a choice to make. It's reported as
  an anomaly; the overlap rule in [`US-FND-FYR-001`](#us-fnd-fyr-001) exists to stop it ever happening.
- The lookup always uses the business date, never the clock time, so a night shift that crosses midnight
  gets the same label as the rest of its shift ([Calendar & Shift](02-shift.md), assumption A3).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `FISCAL_YEAR` (all fields), `SHIFT_INSTANCE.business_date` as the input date |
| **Writes** | None. This story is read-only by definition |
| **Not real fields: don't add** | No new fields. No derived quarter is stored on any row: the quarter is computed at read time from the declaration, so amending a fiscal year can't leave stale quarter labels scattered across other tables |

**6. Rules & constraints**

1. The lookup always uses the business date, never the clock time.
2. A date that falls in no fiscal year comes back as "not declared" and is **never** guessed into a
   neighbouring year.
3. A date that falls in more than one fiscal year is an anomaly and is reported as one.
4. The quarter is worked out when it's looked up and is never saved anywhere else.
5. Totals that leave out "not declared" dates must say so on screen, with how many dates were left out.
6. No access limit beyond that of the number being labelled.

**7. Acceptance criteria**

- **AC-1** **Given** FY2026 in monthly mode starting month 1, **when** 2026-08-14 is looked up, **then** the
  result is FY2026 · Q3.
- **AC-2 (non-January)** **Given** FY2026 starting month 4, **when** 2027-02-10 is looked up, **then** the
  result is FY2026 · Q4.
- **AC-3 (weekly)** **Given** weekly mode starting 2026-01-05, **when** 2026-05-20 is looked up, **then** the
  result is FY2026 · Q2.
- **AC-4 (not declared)** **Given** no fiscal year covers 2025-11-02, **when** it is looked up, **then** the
  result is "not declared", the date is not folded into FY2026, and the screen names the date and links to
  the fiscal-year screen.
- **AC-5 (exclusion visible)** **Given** a quarter total that leaves out 3 not-declared dates, **when** the
  total is shown, **then** the screen says dates were left out and that there were 3.
- **AC-6 (overlap anomaly)** **Given** two fiscal years wrongly overlap on 2026-06-01, **when** that date is
  looked up, **then** an anomaly is reported rather than one of the two being picked.
- **AC-7 (night shift)** **Given** a shift starting 2026-08-14 at 22:00 and ending 2026-08-15 at 06:00,
  **when** its output is labelled, **then** all of it gets the label for business date 2026-08-14.
- **AC-8 (amendment respected)** **Given** a fiscal year was corrected effective 2026-09-01, **when**
  2026-08-14 is looked up, **then** the version in effect on 2026-08-14 is used, not the current one.

**8. Metrics & events**

- **Metric:** `dates_resolving_to_a_fiscal_year`: the share of the pilot line's shift business dates that
  fall in a declared fiscal year. Baseline 0% (no fiscal year is declared on 2026-08-24). Target 100% from
  the pilot line's first production date onward. Measured by matching the real shifts against the declared
  fiscal years.
- **Counter-metric:** how many dates fall in more than one fiscal year. **Must be 0**; anything above 0 means
  the overlap rule failed when the year was declared.
- **Events:** `fiscal_period_resolved` (`business_date`, `fiscal_year_label`, `quarter`) ·
  `fiscal_period_not_declared` (`business_date`, `enterprise_id`) · `fiscal_period_overlap_detected`
  (`business_date`, `matching_row_count`).

**Dependencies:** `US-FND-FYR-001`

## Change notes (history — not needed to build)

- Module intro: F-03 carried an ACTIVE entity spec for some time without appearing in any PRD; `FISCAL_YEAR` appeared zero times across PRD-001 and PRD-002. PRD-003 is the first PRD to include it.
- Module intro (2026-09-03): screen composition corrected — declaration moved into the Fiscal Year panel of `SCR-FND-SIT-002`, and `SCR-FND-FSC-001` retired as a standalone screen. Entity content was not affected; this was composition only.
- All stories (2026-09-23): detail blocks rewritten from one dense table into numbered sections, and
  "subject whose IDP-asserted scope claim covers …" replaced by *enterprise access* / *site access*
  (defined in the README glossary). Meaning unchanged; UI copy kept verbatim.
- All stories (2026-09-23, second pass): rewritten in plain language. Entity and field names now appear only in
  section 5 and in the collapsible "Exact formula (for developers)" boxes; everywhere else items are named in
  plain words (fiscal year, monthly / weekly mode, start month, business date, real shift). Meaning
  unchanged; UI copy kept verbatim.
- `US-FND-FYR-001`, `US-FND-FYR-002` — 2026-09-27: prd-sync docs-molcadx 3cb17fa..7ad3ed8: screen text for the Fiscal Year panels (UX 01) copied with keys into FYR-001 / FYR-002. FYR-001 gains the Site panel states table and a labels/messages table; ACs that name a message now quote it. Behaviour, rules and data unchanged.
