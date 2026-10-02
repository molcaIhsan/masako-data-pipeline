# PRD-003 · Audit Trail (`AUD`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: [F-14](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md).
> Terms like *site access*, *implementor* and *effective dating* are defined in the [README glossary](README.md#glossary).
> Stories are written in plain language. Exact field names are in section 5.

> **What this module is.** Every write to any data in the system leaves one permanent row: who did it, when, what changed
> from what to what, and **from which date the change applies**. Two ways to read it (PO's Figma):
> - **History**: a capability on **every** master-data screen, showing one record's changes.
> - **Audit log**: one feature (the Figma's *Log service* under Library Configuration), searching and downloading
>   everything.
>
> **Whole system** (PO, 2026-09-25, widened from "FOUNDATION only" the same day): FOUNDATION owns the one
> `AUDIT_LOG`, and **every domain writes to it** — PRODUCTION Job Orders and losses, MAINTENANCE component history,
> as well as FOUNDATION master data. PRODUCTION's log stories (PRD-004 `PLN-011`, `MON-007`, `PRF-007`) read it.
> **Reading is per module:** `<domain>:<module>:<scope>:audit_view` (and `:audit_download`), e.g.
> `foundation:sit:site:audit_view`, `production:pln:site:audit_view`.
> The full list of covered entities is in [F-14 § What is covered](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md#what-is-covered).

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| **AU-1b** After 3 years | all | Rows are kept **3 years** (AU-1, resolved 2026-09-25). Purge or archive after that is open | PO + engineering |
| **AU-5** System-job volume | `AUD-001` | One audit row per `KPI_RESULT` row, or one summary row per job run? | PO + `production-domain` + engineering |
| **AU-6** Secret fields | `AUD-001`, `002` | Only `DATA_SOURCE.connection_config` is known to be masked | `foundation-domain` + engineering |
| **AU-9** Download | `AUD-004` | Format (CSV proposed), row limit, immediate or background | PO + engineering |
| **AU-11** PLATFORM | `AUD-001` | Is vendor-side PLATFORM data (e.g. tenant entitlements) covered? Its rows have no tenant scope | PO + `platform-domain` |
| **AU-12** Business actions | `AUD-001` | Which entity each PRODUCTION business action (`release`, `hold`, …, `report_*`) applies to | `production-domain` |
| **AU-13** Reading download rows | `AUD-003` | Who may read a `download` row (the Audit log has no permission of its own). Proposed: whoever holds `audit_view` for every module in that download's filter | PO |
| **UX** | `AUD-002`, `003`, `004` | No `SCR-FND-AUD-*` screens declared yet (AU-8 is now resolved, so they can be) | `foundation-domain` |
| AU-14 | `US-FND-AUD-001` | Keys (2026-10-01): `changes` is a list of before → after fields, which a stored column may not be: child table (one row per changed field) or other storage? | `foundation-domain` |

## Stories

| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-AUD-001`](#us-fnd-aud-001) | Record every change, in every domain | 🟢 Ready | As a **Plant Admin/IT (internal implementor)**, I want every create, edit, deactivation and business action on any record recorded automatically, with who, when, before and after, and the date it takes effect, so that any number can be traced back to the person or job that set it. → [detail](#us-fnd-aud-001) | `US-FND-SIT-001` |
| [`US-FND-AUD-002`](#us-fnd-aud-002) | See one record's history | 🟡 Partly blocked | As a **Plant Admin/IT (internal implementor)**, I want a History view on every master-data record, so that I can see who changed it and from when each version applies without asking anyone. → [detail](#us-fnd-aud-002) | `US-FND-AUD-001` |
| [`US-FND-AUD-003`](#us-fnd-aud-003) | Search the audit log across the system | 🟡 Partly blocked | As a **plant manager**, I want to search all changes by period, person, module, data type and site, so that I can answer "who changed this, and what else did they change" in one place. → [detail](#us-fnd-aud-003) | `US-FND-AUD-001` |
| [`US-FND-AUD-004`](#us-fnd-aud-004) | Download the audit log | 🟡 Partly blocked | As a **plant manager**, I want to download filtered audit rows, with the download itself recorded, so that I can hand an auditor a file and still know who took a copy. → [detail](#us-fnd-aud-004) | `US-FND-AUD-003` |

**Totals: 1 🟢 · 3 🟡 = 4.**

## Detail blocks

---

#### US-FND-AUD-001

**Record every change, in every domain**

**Status:** 🟢 Ready. Retention is 3 years and a reason is required (PO, 2026-09-25). The remaining open items (after 3 years, volume, secrets) don't change what is written.

> **In short:** every successful write to any domain's data writes one audit row in the same transaction. An
> effective-dated edit (old version closed, new version opened) is **one** row carrying the date it takes effect.
> A failed write leaves no row. Nobody can edit or delete a row.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want every create, edit, deactivation and business action on any record
recorded automatically, with who, when, before and after, and the date it takes effect, so that any number can be
traced back to the person or job that set it.

**2. Context**

- **Why:** FOUNDATION data is effective-dated, so you can see *what* was valid when. You can't see *who* wrote it.
  When an OEE figure looks wrong because a cycle time changed, nobody can say who changed it or why
  ([F-14 § Why it matters](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md#why-it-matters--what-goes-wrong-without-it)).
- **Figma:** every Master Foundation box **and every Production module** has "Log: who made the dataset, who
  edited it (before → after), who deleted it, downloadable log". One shared trail serves them all.
- **"Delete" in the Figma = deactivate.** FOUNDATION never hard-deletes.
- **Every domain and every source counts:** FOUNDATION, PRODUCTION and MAINTENANCE data, written from the UI, bulk
  import, API or a system job (`SHIFT_INSTANCE` generator, `KPI_RESULT` calculation). Domains add their own business
  actions (PRODUCTION: `release`, `hold`, `resume`, `cancel`, `upload`, `reason_corrected`, `rework_added`, …).
- **Who and where:** nobody triggers it. It happens on every save.

**3. Expectation**

- **Every edit, deactivate and reactivate form in FOUNDATION gets a required *Reason* field** (free text), shown
  next to the Save button. Create forms show it as optional. A bulk import asks for one reason for the whole file.
- After any save, the change is already in History (`AUD-002`).
- A bulk import of 500 rows produces up to 500 audit rows sharing one batch number; rejected rows produce none.

| State | What the user sees |
|-------|--------------------|
| Empty | not applicable (no screen) |
| Loading | not applicable |
| Error | If the audit row can't be written, **the change isn't saved either**: "Not saved — try again". Never a saved change without its row. **No reason given** on an edit, deactivate, reactivate or business action → the save is refused with "Say why you're making this change" under the Reason field |
| Offline | not applicable (FOUNDATION edits need a connection) |
| Success | The normal save message of that screen |
| No permission | not applicable (the system writes the row) |

**4. Calculation**

*What one audit row holds for an effective-dated edit*

The old version is closed and the new one opened. One row links both, lists the business fields that changed, and
carries both dates.

*Example (from F-14):* on 2026-09-25 09:14 UTC, implementor Rina changes an operation's cycle time from 20 s to
18 s, effective 2026-10-01.
- Master rows: old row `op-77` gets `valid_to = 2026-09-30`; new row `op-78` opens with 18 s, `valid_from = 2026-10-01`.
- Audit row: `action = update`, `previous_version_entity_id = op-77`, `entity_id = op-78`,
  `changes = [{cycle_time_value, 20, 18}]`, `effective_to = 2026-09-30`, `effective_from = 2026-10-01`,
  `actor_display_name = Rina`, `occurred_at = 2026-09-25 09:14Z`, `source = ui`.
- A KPI for 2026-09-28 still uses 20 s. The audit row doesn't change any number.

*Edge cases*
- A save that touches three records → three audit rows with one `transaction_id`.
- A validation error or a blocked deactivation → no audit row.
- A secret field (`DATA_SOURCE.connection_config`) → before and after are masked (AU-6).
- A job writes → `source = system`, `actor_type = service`, actor = the job's service account and name.
- Another domain's record whose site scope that domain hasn't set yet (e.g. `WORK_ORDER`) → the row records `tenant`
  scope until it does ([F-14 core concept 6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md#core-concepts)).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Writes** | `AUDIT_LOG` ([F-14 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md#91-audit_log--one-append-only-row-per-change)): `audit_log_id`, `occurred_at`, `action` (`create` / `update` / `deactivate` / `reactivate`, plus any business action the owning domain declares, e.g. `release`), `entity_type`, `entity_id`, `entity_code`, `previous_version_entity_id`, `effective_from`, `effective_to`, `changes`, `reason`, `source`, `domain_code`, `module_code`, `import_batch_id`, `transaction_id`, `actor_type`, `actor_subject_id`, `actor_display_name`, `tenant_id` … `work_unit_id`, `scope_level` |
| **Reads** | The IDP token (`sub`, display name) of whoever saves; the scope of the record being written |
| **Covered entities** | Every entity of every domain — FOUNDATION, PRODUCTION (`WORK_ORDER`, `WORK_ORDER_OPERATION`, `WORK_ORDER_OPERATION_DEFECT`, `WORK_ORDER_OPERATION_LOT`), MAINTENANCE (`ASSET_COMPONENT_LINK`, `PM_PROCEDURE`, `PM_PROCEDURE_STEP`) ([list](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md#what-is-covered)); new entities of any domain are covered by default. `ASSET_STATE_LOG` is covered as PRODUCTION's: `domain_code` = `PROD`, `module_code` = `MON` (owner settled 2026-09-28, [decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)). PLATFORM is open (AU-11) |

**6. Rules & constraints**

1. The audit row commits in the same transaction as the change ([F-14 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md#92-rules)).
2. One close-and-open is one row, never two.
3. Rows are never updated or deleted, through any UI, API, import or admin path. Only a retention rule may remove
   them (AU-1).
4. A write path that skips the audit row is a defect — in every domain and every job.
5. **A reason is required** (AU-2, resolved 2026-09-25) on every `update`, `deactivate`, `reactivate` and business action
   a person makes, and once per bulk import (copied to every row of the batch). It is optional on `create` and empty for
   system jobs. Without it the save is refused and nothing is written.
6. Nobody writes audit rows by hand; there is no create, edit or delete permission on them.
7. Rows are kept **3 years** from `occurred_at` (AU-1). What happens after that is AU-1b.
8. The person's display name is stored on the row and **never blanked**, not even on a deletion request (AU-3,
   resolved 2026-09-25). The customer records the legal basis (UU PDP) at onboarding.
9. `domain_code` + `module_code` always name the **owner of the written entity**, never the domain of the screen or
   job that made the write.

**7. Acceptance criteria**

- **AC-1 (effective-dated edit)** **Given** operation `op-77` has cycle time 20 s, **when** Rina saves 18 s effective
  2026-10-01, **then** exactly one audit row exists with `previous_version_entity_id = op-77`, the new row's id,
  `changes` 20 → 18, `effective_from = 2026-10-01`, `effective_to = 2026-09-30`, and Rina as actor.
- **AC-2 (create)** **Given** a new downtime reason is saved, **when** the save succeeds, **then** one `create` row
  lists every field with an empty before.
- **AC-3 (deactivate)** **Given** a work unit is deactivated on 2026-10-15, **when** it succeeds, **then** one
  `deactivate` row has `effective_to = 2026-10-15`.
- **AC-4 (failed write)** **Given** a save fails validation, **when** the user sees the error, **then** no audit row exists.
- **AC-5 (import)** **Given** an import of 500 rows where 3 are rejected, **when** it finishes, **then** 497 audit rows
  share one `import_batch_id`.
- **AC-6 (system job)** **Given** the shift-instance generator creates 270 instances, **when** it finishes, **then**
  the audit rows have `source = system`, `actor_type = service` and the job's name.
- **AC-7 (append-only)** **Given** any audit row, **when** anyone tries to change or delete it through the UI or API,
  **then** it is refused.
- **AC-9 (reason required)** **Given** an implementor edits a downtime reason's name, **when** they press Save with
  the Reason field empty, **then** the save is refused with "Say why you're making this change" and no audit row
  and no change exist.
- **AC-10 (import reason)** **Given** an import is started, **when** the implementor gives the reason "Initial
  load, pilot site", **then** every audit row of that batch carries that reason.
- **AC-11 (other domain)** **Given** a supervisor releases a PRODUCTION Job Order with the reason "material
  arrived", **when** the release succeeds, **then** one audit row exists with `domain_code = PROD`,
  `module_code = PLN`, `action = release`, the reason, and the supervisor as actor — in the same `AUDIT_LOG`.
- **AC-8 (secret)** **Given** `DATA_SOURCE.connection_config` changes, **when** the row is written, **then** before and
  after are masked.

**8. Metrics & events**

- **Metric:** `writes_without_audit_row`: writes in any domain with no matching audit row. **Must be 0**, checked by a
  daily reconciliation job.
- **Counter-metric:** save time p95 must not grow by more than `[proposed]` 100 ms because of the audit write.
- **Events:** none extra (the audit row is the record).

**Dependencies:** `US-FND-SIT-001` (scope to snapshot). It must ship **with** the first FOUNDATION screen, not
after: rows can't be written retroactively.

---

#### US-FND-AUD-002

**See one record's history**

**Status:** 🟡 Partly blocked: no screen is declared yet (`SCR-FND-AUD-*`). Who may read it (AU-8) and the name
shown (AU-3: always the person's name) are decided. The view itself can be built.

> **In short:** a History tab on every master-data record. Newest first, each line says who, when, what changed,
> and from when it applies. Versions of the same record are joined, so a close-and-open reads as one change.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want a History view on every master-data record, so that I can
see who changed it and from when each version applies without asking anyone.

**2. Context**

- **Figma:** the "Log" note on each Master Foundation box.
- It is a **capability** of every FOUNDATION feature (Site Hierarchy, Equipment, Product, Reference data, Work
  Master, Shift, Fiscal year, KPI binding, Data Source), not a screen of its own. Other domains' records (Job Order,
  PM procedure, …) get the same capability; their screens belong to their own PRDs.
- **Who and where:** the implementor or admin at a desktop, usually when a number looks wrong.

**3. Expectation**

- A **History** tab or panel on the record's screen.
- Each line: date and time (site timezone), person or job name, action, the fields that changed with before → after,
  **effective from** (and to), reason if given, source (screen / import / API / system).
- All versions of the record are shown as one history (followed through `previous_version_entity_id`).
- Future-dated changes are marked "applies from `<date>`".

| State | What the user sees |
|-------|--------------------|
| Empty | "No changes recorded since `<date the audit trail started>`" |
| Loading | Row placeholders |
| Error | "History couldn't load — try again" |
| Offline | "History needs a connection" |
| Success | not applicable (read-only) |
| No permission | The tab is hidden without that module's `audit_view` (e.g. `foundation:sit:<scope>:audit_view` on a site), even when the user can view the record (AU-8) |

**4. Calculation** — not applicable (no numbers).

*Example line:* "25 Sep 2026 16:14 · Rina · edited · cycle time 20 s → 18 s · applies from 1 Oct 2026 · screen".

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `AUDIT_LOG` rows where `entity_type` is the record's type and `entity_id` or `previous_version_entity_id` is any version of it |
| **Writes** | None |

**6. Rules & constraints**

1. Newest first; `[proposed]` 50 lines, then "show more".
2. Masked fields stay masked (AU-6).
3. **Who may see it:** the owning module's `<domain>:<module>:<scope>:audit_view` on the record's scope ([permissions](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-1-authorization-permissions.md#audit-trail-aud--14-audit-trailmd-9)).

**7. Acceptance criteria**

- **AC-1** **Given** the cycle-time edit of `AUD-001` AC-1, **when** History opens on that operation, **then** one line
  shows Rina, 20 s → 18 s and "applies from 1 Oct 2026".
- **AC-2 (versions joined)** **Given** a record with three versions, **when** History opens on the newest, **then**
  the changes of all three versions show.
- **AC-3 (system job)** **Given** a shift instance created by the generator, **when** its History opens, **then** the
  line names the job, not a person.

**8. Metrics & events**

- **Events:** `record_history_opened` (`entity_type`, `row_count`).

**Dependencies:** `US-FND-AUD-001`

---

#### US-FND-AUD-003

**Search the audit log across the system**

**Status:** 🟡 Partly blocked: same as `AUD-002` (no screen yet).

> **In short:** one Audit log page for every change in the system that the user may read. Filter by period, person,
> domain and module, data type, action and site, then open any line to see the record.

**1. Story**

As a **plant manager**, I want to search all changes by period, person, module, data type and site, so that I can
answer "who changed this, and what else did they change" in one place.

**2. Context**

- **Figma:** the *Log service* box under Library Configuration.
- **Who and where:** plant manager or admin, at a desktop, during an investigation or audit.

**3. Expectation**

- Filters: period (default last 7 days), person or job, domain and module (`domain_code`, `module_code`), data type (`entity_type`), action, source, site / area.
- Table: when, **domain · module** (by name, e.g. "PRODUCTION · Production Planning", "FOUNDATION · Site Hierarchy"),
  who, action, data type, record code, what changed (short), applies from, source.
- A line opens the record **in its owning module** (a Job Order opens in Production Planning), not a FOUNDATION page.
- The page stays **central**: one page for the whole system (PO, 2026-09-25).
- Import batches can be grouped into one line ("Import of 497 rows").

| State | What the user sees |
|-------|--------------------|
| Empty | "No changes match these filters" |
| Loading | Row placeholders |
| Error | "The audit log couldn't load — try again" |
| Offline | "The audit log needs a connection" |
| Success | not applicable |
| No permission | Only rows of modules the user holds `audit_view` for, within their scope. A user who holds none sees the page with no rows, not an error ([F-14 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md#92-rules)) |

**4. Calculation** — not applicable.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `AUDIT_LOG` (filtered on `occurred_at`, `actor_subject_id`, `domain_code`, `module_code`, `entity_type`, `action`, `source`, scope fields) |
| **Writes** | None |

**6. Rules & constraints**

1. Period at most `[proposed]` 366 days per search.
2. Scope filtering uses the scope snapshot on the row (what the record belonged to at write time).
3. **Who may see it:** per row, the owning module's `<domain>:<module>:<scope>:audit_view` (row field `module_code`). The page itself has no permission of its own.
4. **Every row shows where it came from:** the domain and module names come from `domain_code` + `module_code`,
   using the names in the PRD module code table (`AGENTS.md` §4) ([F-14 §9.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/14-audit-trail.md#92-rules)).

**7. Acceptance criteria**

- **AC-1** **Given** Rina changed 12 records last week, **when** the manager filters on Rina and last week, **then** 12
  lines show.
- **AC-2 (scope)** **Given** a manager with access to site JKT1 only, **when** they search, **then** no row from another
  site shows.
- **AC-3 (origin)** **Given** a Job Order release and a Site edit in the same week, **when** the manager searches that
  week, **then** the rows show "PRODUCTION · Production Planning" and "FOUNDATION · Site Hierarchy", and opening the
  release row goes to the Job Order in Production Planning.

**8. Metrics & events**

- **Events:** `audit_log_searched` (`filters`, `row_count`).

**Dependencies:** `US-FND-AUD-001`

---

#### US-FND-AUD-004

**Download the audit log**

**Status:** 🟡 Partly blocked: format, row limit and immediate vs background (AU-9) are open.

> **In short:** download what the search shows. The download itself becomes an audit row, so you always know who
> took a copy.

**1. Story**

As a **plant manager**, I want to download filtered audit rows, with the download itself recorded, so that I can
hand an auditor a file and still know who took a copy.

**2. Context** — Figma: "Downloadable log". F-14 rule: every download writes one `download` audit row.

**3. Expectation**

- A **Download** button on the Audit log page, using the current filters.

| State | What the user sees |
|-------|--------------------|
| Empty | The button is disabled when there are no rows |
| Loading | "Preparing file…" |
| Error | "Download failed — try again"; no `download` row is written |
| Offline | "Downloading needs a connection" |
| Success | The file downloads |
| No permission | The file only holds rows of modules the user holds `audit_download` for; the button is hidden if they hold none |

**4. Calculation** — not applicable.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `AUDIT_LOG` with the current filters |
| **Writes** | One `AUDIT_LOG` row: `action = download`, `entity_type = AUDIT_LOG`, `download_filter`, `download_row_count`, actor, `occurred_at` |

**6. Rules & constraints**

1. The `download` row is written in the same transaction that produces the file.
2. File format `[proposed]` CSV; limit and immediate vs background `TBD` (AU-9). Every file has `domain_code` and
   `module_code` columns, so the source of each row survives outside the app.
3. **Who may do it:** per row, `<domain>:<module>:<scope>:audit_download`.

**7. Acceptance criteria**

- **AC-1** **Given** a filter showing 120 rows, **when** the manager downloads, **then** the file has 120 rows and one
  `download` audit row records the filter and 120.
- **AC-2 (failed)** **Given** the download fails, **when** the error shows, **then** no `download` row exists.

**8. Metrics & events**

- **Events:** none extra (the `download` row is the record).

**Dependencies:** `US-FND-AUD-003`

---

## Change notes

| Date | Change |
|------|--------|
| 2026-09-28 | prd-sync to `docs-molcadx@513c4f9` ([asset status decision](https://github.com/molca-id/docs-molcadx/blob/main/LOGS/decisions/2026-09-28-asset-status-ownership.md)): `AUD-001` — `ASSET_STATE_LOG` is PRODUCTION's, so its rows carry `domain_code` = `PROD`, `module_code` = `MON` (F-14, was waiting on B5) |
| 2026-09-25 | prd-sync to `docs-molcadx@327bb64` (PO: audit log stays central, each row shows its source): `AUD-003` — Domain · Module column, a row opens in its owning module, rule 4, AC-3. `AUD-004` — file has `domain_code` / `module_code` columns. P-10 closed: downloads of a domain's own data are its `*_downloaded` business actions |
| 2026-09-25 | prd-sync to `docs-molcadx@171a333`: `AUD-001` — business actions in `action` and the reason rule, covered entities for every domain, `tenant` scope edge case, owner rule (9), metric for every domain. `AUD-002` notes other domains' History. `AUD-003` no-permission state follows F-14 §9.2 (empty page, not hidden). New open item P-10 (`download` inconsistency) |
| 2026-09-25 | **Widened to the whole system** (PO): every domain writes to `AUDIT_LOG`; reading is per module (`<domain>:<module>:<scope>:audit_view` / `audit_download`); rows carry `domain_code` / `module_code`. `AUD-002`–`004` permissions and `AUD-003` filters updated |
| 2026-09-25 | PO answers AU-3: keep the display name, never blanked |
| 2026-09-25 | PO answers: AU-1 keep 3 years, AU-2 reason required, AU-8 own read permission. `AUD-001` gains AC-9/AC-10 and the Reason field; AU-3, AU-9 still open |
| 2026-09-25 | Module created (PO decision): F-14 Audit Trail, FOUNDATION only. 4 stories. Effective-dated edits are one audit row carrying the effective dates; every write source is covered, including other domains' jobs that write FOUNDATION data |
