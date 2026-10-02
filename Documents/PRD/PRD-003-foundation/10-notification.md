# PRD-003 · Notification (`NTF`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-10. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.


> ⚠ **Not buildable yet.** The entities below (`NOTIFICATION_RULE`, `NOTIFICATION`, `NOTIFICATION_DELIVERY`)
> are proposals in this PRD; `foundation-domain` has not modelled them — [F-10](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md)
> is still a concept. Do not estimate or build these stories until it does ([F-Q-03](30-release-risks-questions.md#11-open-questions)).

- **Build together with the Workflow Engine.** Both share trigger, role and escalation
  ([F-10 Implications](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md#implications-for-molcadx)).
- **Channels this quarter:** in-app + push only ([§4](README.md)).
- **Which channel operators can really see** depends on [SF-4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#boundaries).

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| Entity model | all `US-FND-NTF-*` | The proposed `NOTIFICATION_*` entities are not modelled ([F-Q-03](30-release-risks-questions.md#11-open-questions)) | `foundation-domain`, PO |
| IDP recipient query | `US-FND-NTF-001` | How the module asks the IDP "who holds role X at scope Y" before recipient counts can be shown (`TBD`) | PO / `foundation-domain` |
| "Acted on" field | `US-FND-NTF-001` | No entity models "acted on", so only the acknowledged half of the health ratio is measurable (`TBD`) | `foundation-domain` |
| Alarm-proportion proxy | `US-FND-NTF-001` | The rule-catalog proxy must be replaced by an alert-based denominator once alerts are modelled | `foundation-domain` |
| Operator-visible channel | all `US-FND-NTF-*` | Which channel operators can really see ([SF-4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#boundaries)) | `production-domain` |
| Notification UX file | `US-FND-NTF-001`, `US-FND-NTF-002` | No Notification UX file exists yet; empty-state texts marked `[proposed]` | not stated |
| `[proposed]` numbers | all `US-FND-NTF-*` | Alarm proxy warning 10%; ≥80% acknowledged; ≤10 notifications per recipient per shift; "past event" after 1 hour; ≤20% escalated; 5-minute window; 1 delivery per key per 5 minutes | PO |

## Stories


| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-NTF-001`](#us-fnd-ntf-001) | Configure notification rules per role | 🟠 Concept only | As an **implementor**, I want to define which event triggers which notification to which role, so information reaches someone who can act. → [detail](#us-fnd-ntf-001) | `US-FND-CFG-001` |
| [`US-FND-NTF-002`](#us-fnd-ntf-002) | Acknowledge notifications & auto-escalate | 🟠 Concept only | As a **production supervisor**, I want to acknowledge important notifications and have the system escalate if I miss one, so no warning dies quietly. → [detail](#us-fnd-ntf-002) | `US-FND-NTF-001` |
| [`US-FND-NTF-003`](#us-fnd-ntf-003) | Suppress repeating notifications | 🟠 Concept only | As a **production supervisor**, I want repeated similar events grouped, so one faulty sensor doesn't flood everyone. → [detail](#us-fnd-ntf-003) | `US-FND-NTF-001` |

## Detail blocks

---

#### US-FND-NTF-001

**Configure notification rules per role & scope**

**Status:** 🟠 Concept only — foundation-domain hasn't modelled the data for this yet.

> **In short:** the implementor decides which event sends which notification to which role + scope. Every
> alarm must name the action the recipient should take, and the screen warns when alarms get too common.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to define which event triggers which notification to
which role, so information reaches someone who can act.

**2. Context**

- **Why:** the most common failure isn't that information doesn't arrive. It's **too many notifications**. A
  bad notification system is worse than none, because it teaches people to ignore warnings
  ([F-10](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md)).
- **Principle:** every alarm must come with one clear action. If the person receiving it can do nothing, it's
  a log entry, not an alarm ([F-10 core concept 1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md#core-concepts)).
- **Who and where:** the implementor, at a desktop.

**3. Expectation**

- A **list of rules** with these columns: the event that triggers it, its type, the target role + scope, the
  channel, and **the action expected**. The type is one of alarm (someone must act), notification (for
  information) or log (just recorded), stored as `alarm` / `notification` / `log`. The action column is
  required: a rule with no action can't be saved as an alarm.
- The header shows **`[proposed]` what share of the active rules are alarms**. This is only a stand-in for
  now: F-10 limits how many top-priority **alerts** are raised, not how many rules there are. Replace it with a
  count based on alerts once the data model exists.
- What the user can read within 3 seconds: how many rules are active, and how many of them are alarms.
- **Limits:** notification titles up to 60 characters, and the body up to 160 characters (it must be readable
  on a lock screen).

| State | What the user sees |
|-------|--------------------|
| Empty | `[proposed]` "No notification rules yet" / prefer UX 00 `"belum ada data"` until a Notification UX file exists, plus a note that the highest priority must stay rare |
| Loading | A placeholder for the table |
| Error | Everything typed is still there, plus a message that it failed |
| Offline | Not available |
| Success | The rule is saved. If the share of alarms goes over the limit, a warning about alarm fatigue appears. It doesn't stop the save |
| No permission | Read-only |

**4. Calculation**

*Share of alarms (a `[proposed]` stand-in)*
- The number of alarm rules divided by the number of active rules. A warning appears when it goes over
  **`[proposed]` 10%**.
- F-10's priority rule is about the share of top-priority **alerts**, not the share of alarm rows in the list
  of rules ([F-10 core concept 2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md#core-concepts)).
  Counting rules is only a stand-in until alerts are modelled.

> [!note]- Exact formula (for developers)
> ```
> alarm_proportion = alarm_type_rule_count / active_rule_count
> warning when alarm_proportion > [proposed] 10%
> ```

*Who receives it*
- Everyone whose role and scope (as the login system, the IDP, says) match the rule's target role, at the
  event's own scope or wider.
- This is worked out by asking the IDP, not from a FOUNDATION table (see Data & entities).
- Example: a rule "downtime open >15 minutes with no cause" → IDP role claim `<target_role>` on the event's
  work center → 2 people hold that claim → 2 recipients.

> [!note]- Exact formula (for developers)
> ```
> recipients = {subject | subject's IDP-asserted (role, scope) claim satisfies target_role at a scope ⊇ event_scope}
> ```

*Edge cases*
- No recipients (nobody holds that role + scope) → the rule is still saved, but it's **marked "will not reach
  anyone"** in the list, because this is the silent failure that happens most often.
- Notification thresholds come from the [Configuration Service](12-configuration-service.md#us-fnd-cfg-001),
  and are **never** fixed in the code ([F-10 common pitfalls](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md#common-pitfalls)).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Writes** | `NOTIFICATION_RULE` (proposed, not modelled) (trigger event, type, target role, `scope_type`, channel, title, body, expected action, `escalation_role`, `escalation_timer_min`, active flag) |
| **Reads** | The location hierarchy · the list of available events (whitelist) · threshold values from Configuration Service |
| **Not a FOUNDATION read** | **Recipient resolution (who actually holds `target_role` on `scope_id`) is an IDP query, not a FOUNDATION read.** `ROLE`/`ROLE_ASSIGNMENT` are not FOUNDATION entities (FOUNDATION has no Organization module; [ABAC contract](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp)) |

⚠ **Proposed entities.** This module's proposed entities still need a way to ask the IDP "who holds role X at
scope Y" before recipient counts can be shown. `TBD — perlu konfirmasi PO`/`foundation-domain`: what that
integration looks like.

**6. Rules & constraints**

1. An alarm rule **must** say what action is expected.
2. Who receives a notification is decided by role + scope, not by name
   ([F-10 core concept 3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md#core-concepts)).
3. Thresholds must be adjustable per level, and never fixed in the code.
4. Going over the alarm-share limit gives a warning, not a refusal.
5. Rules that could reach nobody are marked in the list.
6. **Who may do it:** creating or editing a rule needs **access to the rule's own scope** (or wider access).

**7. Acceptance criteria**

- **AC-1** **Given** the list of rules is empty, **when** the implementor creates an alarm rule "downtime open
  >15 min with no cause" for IDP role claim `<target_role>` at work center scope, **then** the rule is saved
  and its row shows how many people it could reach.
- **AC-2 (validation)** **Given** the type alarm is chosen and the action is left blank, **when** saved,
  **then** it is refused with "An alarm must have a clear action — if the recipient can do nothing, change its
  type to log".
- **AC-3 (warning)** **Given** the new rule brings the share of alarms to 20%, **when** saved, **then** it
  **is** saved **and** a warning about alarm fatigue appears, showing the share.
- **AC-4 (no recipient)** **Given** nobody holds IDP role claim `<target_role>` on Line A, **when** a rule for
  that role + scope is saved, **then** the rule is marked "will not reach anyone" in the list.
- **AC-5 (adjustable threshold)** **Given** the 15-minute threshold is changed to 30 at work center level,
  **when** the event is checked for that line, **then** the 30-minute threshold is used, and the user can see
  where that value came from.
- **AC-6 (permission)** **Given** a user without access to the rule's own scope (or wider access), **when**
  they open the list of rules, **then** they can only read it.

**8. Metrics & events**

- **Metric:** `rasio_notifikasi_diakui_dan_ditindaklanjuti` (*the share of notifications acknowledged and acted
  on*). **This, not the number sent, shows whether the module is healthy**
  ([F-10 "Implications for MolcaDx"](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md#implications-for-molcadx)).
  F-10 counts both halves together: acknowledged **and** acted on, not just acknowledged.
  - `TBD — perlu konfirmasi foundation-domain`: F-10 is only a concept, and nothing in this module (or anywhere
    else in FOUNDATION) records "acted on". There is no field to measure that half from yet.
  - Until there is, only the acknowledged half can really be measured: the share of notifications a recipient
    acknowledged before their deadline.
  - Baseline `not yet measured`, target **`[proposed]` ≥80%** for alarms within 30 days. Measured from
    `notification_acknowledged` / `notification_delivered`.
- **Counter-metric:** notifications per recipient per shift must stay at or below **`[proposed]` 10**. Above
  that, the noise starts teaching people to ignore them.
- **Events:** `notification_rule_created` (`event_type`, `severity`, `target_role`, `recipient_count`) ·
  `notification_rule_no_recipient` (`rule_id`).

**Dependencies:** `US-FND-CFG-001`

---

#### US-FND-NTF-002

**Acknowledge notifications and auto-escalate when missed**

**Status:** 🟠 Concept only — foundation-domain hasn't modelled the data for this yet.

> **In short:** the supervisor acknowledges important notifications with one tap. If nobody does in time, the
> system escalates to the next role, so no warning dies quietly.

**1. Story**

As a **production supervisor**, I want to acknowledge important notifications and have the system escalate if
I miss one, so no warning dies quietly.

**2. Context**

- **Life of a notification:** in F-10 a notification goes **sent → delivered → acknowledged → resolved**
  ([F-10 core concept 4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md#core-concepts)). "Sent" is
  the system handing it over for delivery, and the user may never see it as a state. The gaps that matter to
  users are delivered → acknowledged → resolved.
- **Why:** without escalation, a notification nobody sees is the same as one never sent
  ([F-10 core concept 5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md#core-concepts)).
- **Who and where:** the supervisor, on a small screen, while walking, using one hand.

**3. Expectation**

- The **notification centre**, sorted by priority and then by time. Each row shows the title, the scope (the
  line), how old it is, and **the action expected**.
- Alarms look clearly different from ordinary notifications.
- What the user can read within 3 seconds: whether there's an alarm nobody has acknowledged.
- The "Acknowledge" touch area is at least 44×44 px.

| State | What the user sees |
|-------|--------------------|
| Empty | `[proposed]` "No notifications" / prefer UX 00 `"belum ada data"` until a Notification UX file exists. Stated as a good state when there truly are none |
| Loading | A placeholder for the list |
| Error | Notifications already loaded stay visible, with a marker that refreshing failed |
| Offline | Notifications created while the device is offline **wait in a queue** and are sent once it's back online, marked as an event that already happened, so nobody misjudges how urgent it is ([F-10 core concept 7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md#core-concepts)). Acknowledgements made offline also wait in the queue |
| Success | The row moves to "acknowledged" with a short confirmation |
| No permission | Notifications outside the user's access never appear |

**4. Calculation**

*Times*
- Response time = when it was acknowledged minus when it was delivered, using the device's clock.
- Time to resolve = when it was resolved minus when it was created.

> [!note]- Exact formula (for developers)
> ```
> response_time   = acknowledged_at − delivered_at        (using client_ts)
> resolution_time = resolved_at − created_at
> ```

*Escalation*
- A notification escalates when it has gone unacknowledged for longer than its escalation timer since it was
  delivered.
- When that happens → it's sent to the escalation role, on the same scope or one level above. The original
  notification is marked "escalated" and **stays**.
- Example: an alarm is **sent** at 08:00 to people holding IDP role claim `<target_role>` on Line A, with a
  10-minute timer. At 08:10:01 nobody has acknowledged it → it escalates to IDP role claim `<escalation_role>`
  at site scope. The first recipient's response time is left empty, with the reason "escalated".

> [!note]- Exact formula (for developers)
> ```
> unacknowledged_beyond_timer = (now − delivered_at) > escalation_timer_min
> ```

*Edge cases*
- A notification that arrives after the device comes back online, for an event more than **`[proposed]` 1
  hour** old, is marked "past event" and does **not** start a new escalation.
- Two people allowed to acknowledge press at the same moment → the first one is recorded, and the second is
  told who acknowledged it.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Writes** | `NOTIFICATION` (proposed, not modelled) (`rule_id`, related object, `scope_id`, `severity`, `created_at`, status including system-internal `sent` then `delivered`/`acknowledged`/`resolved`) · `NOTIFICATION_DELIVERY` (proposed, not modelled) (`notification_id`, channel, `delivered_at`, `acknowledged_at`, `resolved_at`, escalation flag) |
| **Reads** | `NOTIFICATION_RULE` |
| **Targeting** | Always role+scope via the IDP ([F-10 core concept 3](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md#core-concepts); FOUNDATION has no Organization/`ROLE_ASSIGNMENT` — [F-00 ABAC](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp)). A delivery row may carry an IDP **subject identifier** only as a **delivery address** (e.g. push-token binding), **not** as the targeting rule. Do not read a FOUNDATION `ROLE_ASSIGNMENT` table |

⚠ **Proposed entities.**

**6. Rules & constraints**

1. Each step in a notification's life must be traceable: **sent → delivered → acknowledged → resolved**
   ("sent" may be internal to the system only).
2. When the timer runs out, the notification escalates automatically. The original isn't deleted.
3. Notifications created offline wait in a queue and are marked "past event" when they arrive.
4. Acknowledging works offline and is sent later. The order is decided by the time on the device when the
   button was pressed.
5. Notifications only appear to people holding the matching IDP role + scope.
6. The record of who was notified, when, and how they responded can't be deleted (it's audit evidence).

**7. Acceptance criteria**

- **AC-1** **Given** an alarm is delivered to someone holding IDP role claim `<target_role>` on Line A,
  **when** they press "Acknowledge" within 3 minutes, **then** the response time is recorded as 3 minutes and
  the alarm moves to the acknowledged list.
- **AC-2 (escalation)** **Given** a 10-minute timer and the alarm still unacknowledged after 11, **when**
  someone holding IDP role claim `<escalation_role>` at site scope opens their list, **then** the alarm
  appears as an escalation with its cause, and the original stays marked "escalated".
- **AC-3 (offline queue)** **Given** the operator's device is offline when an andon is raised, **when** the
  connection comes back 40 minutes later, **then** the notification is delivered marked "past event, 40
  minutes ago".
- **AC-4 (offline acknowledge)** **Given** someone allowed to acknowledge does so while offline, **when** the
  connection comes back, **then** the acknowledgement is sent with the device time of when they pressed, not
  the time it was sent.
- **AC-5 (double acknowledgement)** **Given** two people allowed to acknowledge press "Acknowledge" at almost
  the same moment, **when** both are sent, **then** one acknowledgement is recorded and the second person is
  told who acknowledged first.
- **AC-6 (permission)** **Given** a notification for Line B, **when** a user with access to Line A only opens
  the notification centre, **then** that notification doesn't appear.

**8. Metrics & events**

- **Metric:** `waktu_tanggap_alarm` (*alarm response time*): the median time from an alarm being delivered to
  it being acknowledged. Baseline `not yet measured`; the target is set per type after 30 days. Measured from
  `notification_acknowledged`.
- **Counter-metric:** `% of alarms escalated` (*the share of alarms that had to escalate*) must stay at or
  below **`[proposed]` 20%**. Above that, the recipient or the timer is wrong.
- **Events:** `notification_delivered` (`severity`, `channel`, `is_offline_sync`, `event_age_sec`) ·
  `notification_acknowledged` (`response_sec`) · `notification_escalated` (`from_role`, `to_role`) ·
  `notification_resolved` (`resolution_sec`).

**Dependencies:** `US-FND-NTF-001`

---

#### US-FND-NTF-003

**Suppress repeating notifications**

**Status:** 🟠 Concept only — foundation-domain hasn't modelled the data for this yet.

> **In short:** repeats of the same event are grouped into one row with a count, so one faulty sensor can't
> flood everyone. Nothing is hidden: the count and every event stay visible.

**1. Story**

As a **production supervisor**, I want repeated similar events grouped, so one faulty sensor doesn't flood
everyone.

**2. Context**

- **Why:** a machine flickering between running and stopped can set off dozens of notifications in a minute
  ([F-10 core concept 8](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md#core-concepts)). If repeats
  aren't held back, one faulty sensor floods everybody
  ([F-10 common pitfalls](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/10-notification.md#common-pitfalls)).
- **Who and where:** the supervisor, on a small screen.

**3. Expectation**

- Similar notifications about the same thing appear as **one row**, with a count ("12 events in 5 minutes")
  and the times of the first and last one.
- Opening the row shows the events inside it.
- What the user can read within 3 seconds: one row per problem, not 12 rows for its symptoms.

| State | What the user sees |
|-------|--------------------|
| Empty | not applicable |
| Loading | A placeholder |
| Error | not applicable |
| Offline | Grouping still happens when the device comes back online: queued events are grouped before they're shown, not sent one by one |
| Success | Acknowledging the grouped row acknowledges every event inside it |
| No permission | As `US-FND-NTF-002` |

**4. Calculation**

*Grouping*
- Events are grouped when they come from the same rule, are about the same thing, and have the same severity.
- Such events within a **`[proposed]` 5-minute window** become one notification, and its count goes up by one
  for each.

> [!note]- Exact formula (for developers)
> ```
> dedup_key = (rule_id, related_object_id, severity)
> ```
> Events with the same `dedup_key` inside the window become one `NOTIFICATION` with `occurrence_count` incremented.

*Holding back deliveries*
- At most **`[proposed]` 1 delivery per group per 5 minutes**. Events in between only raise the count; they
  don't cause a new delivery.
- Example: 12 status changes on IM-003 within 5 minutes → 1 notification with a count of 12, and one delivery.

*Edge cases*
- If an event's severity **goes up** within the same window, it's delivered again (a problem that's getting
  worse must be seen).
- The grouping window comes from the Configuration Service, per level.
- Grouping **must not** hide events: the count must be visible and the list of events must open.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Writes** | `NOTIFICATION.dedup_key`, `NOTIFICATION.occurrence_count`, `NOTIFICATION.first_occurred_at`, `NOTIFICATION.last_occurred_at` (proposed, not modelled) · `NOTIFICATION_OCCURRENCE` (proposed, not modelled) (the events inside a group) |
| **Reads** | `NOTIFICATION_RULE` · the suppression window from Configuration Service |

⚠ **Proposed entities.**

**6. Rules & constraints**

1. Grouping collects events, it **doesn't throw them away**. The count and the list of events inside must be
   open to inspection.
2. If severity goes up within the window, it's delivered again.
3. The window and the delivery limit can be adjusted per level.
4. Acknowledging the grouped row acknowledges every event inside it.
5. Grouping also applies to notifications that arrive after the device comes back online.

**7. Acceptance criteria**

- **AC-1** **Given** 12 similar events on the same asset within 5 minutes, **when** the supervisor opens the
  notification centre, **then** one row appears with a count of 12 and its time range.
- **AC-2 (holding back)** **Given** similar events keep repeating, **when** 5 minutes pass, **then** only one
  delivery happens in that window.
- **AC-3 (severity rise)** **Given** an event in the window goes up from an ordinary notification to an alarm,
  **when** that happens, **then** it's delivered again even though the window is still open.
- **AC-4 (nothing hidden)** **Given** the grouped row is opened, **when** the supervisor looks at its detail,
  **then** all 12 events in it appear, each with its own time.
- **AC-5 (group acknowledge)** **Given** the grouped row holds 12 events, **when** the supervisor acknowledges
  it, **then** all 12 are recorded as acknowledged at once.
- **AC-6 (offline)** **Given** 12 events queue up while offline, **when** the connection comes back, **then**
  all 12 are grouped before they're shown, not sent one by one.

**8. Metrics & events**

- **Metric:** `notifikasi_per_penerima_per_shift` (*notifications per recipient per shift*): the median.
  Target at or below **`[proposed]` 10**, baseline `not yet measured`. Measured from `notification_delivered`.
- **Counter-metric:** `% of events whose delivery was delayed by suppression and later turned out to be alarms`
  must be 0 (grouping must never delay a real alarm).
- **Events:** `notification_suppressed` (`dedup_key`, `occurrence_count`, `window_sec`) ·
  `notification_group_acknowledged` (`occurrence_count`).

**Dependencies:** `US-FND-NTF-001`

## Change notes (history — not needed to build)

- `US-FND-NTF-001`, `US-FND-NTF-002` (2026-09-03, [§13.5](99-history.md#135-organization-removed-2026-09-03)) — the Organization module was removed from FOUNDATION; roles and role assignments now live only in the IDP. The spec text now states this in present tense.
- 2026-09-23 — module banner shortened to the standard "Not buildable yet" form; the build-together, channel and SF-4 notes are kept above as a list.
- All stories (2026-09-23): detail blocks rewritten from one dense table into numbered sections, and
  "subject whose IDP-asserted scope claim covers …" replaced by glossary access terms (e.g. *site access*, defined in the README glossary).
  Meaning unchanged; UI copy kept verbatim.
- All stories (2026-09-23, second pass): rewritten in plain language. Entity and field names now appear only in
  section 5 and in the collapsible "Exact formula (for developers)" boxes; everywhere else items are named in
  plain words (rule, alarm, recipient, escalation timer, grouped row). Meaning unchanged; UI copy kept verbatim.
