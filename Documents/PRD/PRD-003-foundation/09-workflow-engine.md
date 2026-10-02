# PRD-003 · Workflow Engine (`WFE`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-09. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.


> ⚠ **Not buildable yet.** The entities below (`WORKFLOW_DEFINITION`, `WORKFLOW_VERSION`,
> `WORKFLOW_INSTANCE`, `WORKFLOW_TASK`, `WORKFLOW_AUDIT_LOG`) are proposals in this PRD; `foundation-domain`
> has not modelled them — [F-09](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/09-workflow-engine.md) is still a concept document.
> Do not estimate or build these stories until it does ([F-Q-03](30-release-risks-questions.md#11-open-questions)).

**Second precondition.** These stories also must not enter a sprint before the PO answers
[Q3-3](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/goalsQ3.md#65-pertanyaan-terbuka): which flows implementors must be able to build.
Without that answer, the risk in [F-09 §7.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/09-workflow-engine.md)
(building an all-purpose engine before knowing the real flows) is close to certain.

**Proposed initial scope** — three flows from the existing documents
([F-09 §8](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/09-workflow-engine.md#implications-for-molcadx)):
① andon escalation, ② approval of data corrections after shift close, ③ approval of rescheduling a running job.
The engine covers these three first, with limited condition expressions and a closed action list.

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| Entity model | all `US-FND-WFE-*` | The proposed `WORKFLOW_*` entities are not modelled ([F-Q-03](30-release-risks-questions.md#11-open-questions)) | `foundation-domain`, PO |
| Which flows to support | all `US-FND-WFE-*` | Which flows implementors must be able to build ([Q3-3](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/goalsQ3.md#65-pertanyaan-terbuka)) | PO |
| Role claim per task | `US-FND-WFE-001` | Which IDP role claim a task's assignment maps to — outside this proposal | `production-domain` / IDP config |
| Workflow UX file | `US-FND-WFE-001`, `US-FND-WFE-002`, `US-FND-WFE-004` | No Workflow UX file exists yet; empty-state texts marked `[proposed]` | not stated |
| `[proposed]` numbers | `US-FND-WFE-001`, `US-FND-WFE-004` | Max 30 steps per flow; target ≥3 flows built without a developer; expired tasks ≤10% | PO |

## Stories


| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-WFE-001`](#us-fnd-wfe-001) | Design a workflow without coding | 🟠 Concept only | As an **implementor**, I want to assemble triggers, conditions, tasks and actions from a closed list, so plant SOPs can be executed by the system without code changes. → [detail](#us-fnd-wfe-001) | `—` |
| [`US-FND-WFE-002`](#us-fnd-wfe-002) | Dry-run a workflow before activating it | 🟠 Concept only | As an **implementor**, I want to simulate a flow with sample data, so configuration mistakes aren't discovered in production. → [detail](#us-fnd-wfe-002) | `US-FND-WFE-001` |
| [`US-FND-WFE-003`](#us-fnd-wfe-003) | Activate a version & pin running instances | 🟠 Concept only | As an **implementor**, I want to activate a new version without changing running instances, so an approval doesn't switch owners mid-flight. → [detail](#us-fnd-wfe-003) | `US-FND-WFE-002` |
| [`US-FND-WFE-004`](#us-fnd-wfe-004) | Work a task & trace instance history | 🟠 Concept only | As a **production supervisor**, I want to see my tasks plus the full instance trail, so a stuck flow can be diagnosed without asking a developer. → [detail](#us-fnd-wfe-004) | `US-FND-WFE-003`, `US-FND-NTF-001` |

## Detail blocks

---

#### US-FND-WFE-001

**Design a workflow without coding**

**Status:** 🟠 Concept only — foundation-domain hasn't modelled the data for this yet.

> **In short:** the implementor builds a plant SOP as a flow (trigger, conditions, tasks, actions) by picking
> from a closed list, with no code. Tasks go to a role + scope, never to a named person.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to assemble triggers, conditions, tasks and actions from
a closed list, so plant SOPs can be executed by the system without code changes.

**2. Context**

- **Why:** every plant has different SOPs. Without an engine, each implementation needs code changes and
  can't scale ([F-09 §2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/09-workflow-engine.md#why-it-matters--what-goes-wrong-without-it)).
- **Value:** faster implementations. Plants 2 and 3 don't have to wait in line for developers.
- **Who and where:** an internal implementor, at a desktop, in long sessions. What matters most is that the
  configuration is easy to read, not that it looks polished for a mass market.

**3. Expectation**

- A **flow designer with two panels**:
  - **On the left:** the list of building blocks (trigger, condition, task, action, escalation).
  - **On the right:** the flow itself, as a numbered list of steps. It's not a free-form canvas, so a flow can
    always be read as a list.
- Each step shows who it's assigned to: a **role + scope**, never a person's name.
- What the user can read within 3 seconds: the flow's name, its trigger, and how many steps it has.
- **Limits:** flow names up to 80 characters, and at most **`[proposed]` 30 steps** per flow so it stays
  readable.

| State | What the user sees |
|-------|--------------------|
| Empty | `"belum ada data"` ([UX 00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md) — no Workflow UX file yet), plus three templates from the initial scope (andon escalation, correction approval, reschedule approval) |
| Loading | A placeholder for the step list |
| Error | "Could not save — your flow layout has not been lost." |
| Offline | Not available |
| Success | The flow is saved as a **draft**. It stays inactive until it passes a dry run (`US-FND-WFE-002`) and is activated (`US-FND-WFE-003`) |
| No permission | "Your scope does not cover this flow — only a subject whose IDP-asserted scope claim covers the flow's own `scope_type`/`scope_id` (or a broader one) may design flows." |

**4. Calculation**

*When a flow is complete enough to be saved as ready to test*
- A flow is complete when all of these are true:
  - it has exactly one trigger;
  - it has at least one ending (a final state);
  - every task has a role and a scope;
  - every branch of every condition leads somewhere;
  - no step is cut off: every step can be reached from the trigger. The number of unreachable steps must be 0.
- Example: the andon escalation flow starts when an andon is raised (`andon_raised`) → task "acknowledge",
  for IDP role claim `<target_role>` with the andon's work center as scope → after an escalation timer of
  10 min → task "acknowledge", for IDP role claim `<escalation_role>` with the site as scope → ends as
  `resolved`.

> [!note]- Exact formula (for developers)
> ```
> complete = exactly 1 trigger
>        AND ≥1 terminal state
>        AND every task has (role, scope)
>        AND every condition branch has a destination
>        AND no unreachable steps
>
> unreachable_steps = steps with no inbound path from the trigger      → must be 0
> ```

*Edge cases*
- A condition that points to a field that doesn't exist → refused when saving, not when the flow runs.
- A flow with no ending is refused.
- Assigning a task to a person by name **isn't offered as an option at all**. It's prevented by the design,
  not checked afterwards.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Writes** | `WORKFLOW_DEFINITION` (proposed, not modelled) (name, description, `scope_type`, `scope_id`, draft/active status) · `WORKFLOW_VERSION` (proposed, not modelled) (`definition_id`, `version`, step layout, `valid_from`) |
| **Reads** | `SITE`/`AREA`/`WORK_CENTER` (for assignment scope) · the list of available actions & triggers (whitelist, managed by [Configuration Service](12-configuration-service.md#us-fnd-cfg-001)) |
| **Not a FOUNDATION entity** | `ROLE`. Role names in a task's "role + scope" assignment are an IDP-asserted subject attribute this module never stores or validates, per the [ABAC contract](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp) |

⚠ **Proposed entities, not yet in FOUNDATION.**

**6. Rules & constraints**

1. Tasks are assigned by scope, never to a person by name ([F-09 §7.2](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/09-workflow-engine.md)).
   Which IDP role claim a task's assignment maps to is for `production-domain` and the IDP configuration to
   decide, outside this proposal.
2. Actions can only be picked from a closed list. Conditions are limited: no free-form scripting, no loops,
   and no arbitrary expressions ([§4 out of scope](README.md)).
3. A new flow is always saved as a draft. It's never active straight away.
4. Every change to a flow's layout is recorded: who changed it, when, and from what to what.
5. **Who may do it:** creating or editing a flow needs **access to the flow's own scope** (or wider access).
   An implementor can't create a flow for a scope outside their own access.

**7. Acceptance criteria**

- **AC-1** **Given** the implementor picks the andon escalation template, **when** they change the first
  step's target to IDP role claim `<target_role>` + scope and save, **then** the flow is saved as a draft
  assigned to that role + scope.
- **AC-2 (validation)** **Given** the flow has no ending, **when** it's saved as ready to test, **then** it
  is refused with a message naming the step that has no ending.
- **AC-3 (validation)** **Given** a condition points to a field that doesn't exist, **when** saved, **then**
  it is refused at save time, naming that field, instead of failing when the flow runs.
- **AC-4 (low-code boundary)** **Given** the implementor looks for a way to assign a task to a specific
  employee, **when** they open the assignment options, **then** only role + scope options exist, with a short
  explanation of why.
- **AC-5 (draft)** **Given** a new flow has been saved, **when** its trigger event happens, **then** the flow
  doesn't run, because it's still a draft.
- **AC-6 (permission)** **Given** the implementor has site access to JKT1 only, **when** they create a flow
  for site BDG1, **then** it is refused, with an explanation of the limit on their access.

**8. Metrics & events**

- **Metric:** `alur_dibuat_tanpa_developer` (*flows built without a developer*): how many active flows an
  implementor built without asking a developer for help even once. **This is direct evidence for the "low
  code" KR.** Baseline 0 (2026-08-07), target **`[proposed]` ≥3 flows** by the end of the quarter. Measured
  from `workflow_definition_activated` plus the number of help tickets.
- **Counter-metric:** the share of flows that need changes within 7 days of going live. A high share means the
  designer isn't easy enough to read.
- **Events:** `workflow_definition_created` (`step_count`, `from_template`) ·
  `workflow_definition_validation_failed` (`reason`).

**Dependencies:** `—`

---

#### US-FND-WFE-002

**Dry-run a workflow before activating it**

**Status:** 🟠 Concept only — foundation-domain hasn't modelled the data for this yet.

> **In short:** before a flow goes live, the implementor runs it on sample data and sees the path it takes.
> Nothing is sent and nothing is written; activation is only possible after a passing run.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to simulate a flow with sample data, so configuration
mistakes aren't discovered in production.

**2. Context**

- **Why:** without a test run, every configuration change is a gamble made in production
  ([F-09 §7.4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/09-workflow-engine.md)). Configuration is still logic, and
  it can still be wrong ([F-09 §7.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/09-workflow-engine.md)).
- **Who and where:** the implementor, at a desktop, before every activation.

**3. Expectation**

- A **test-run panel** next to the designer:
  1. pick sample data (for example, an andon of category X on Line A, lasting 12 min);
  2. press "Run simulation";
  3. see **the path the flow took**, step by step, with the reason for each branch.
- What the user can read within 3 seconds: whether it passed or failed, and at which step.

| State | What the user sees |
|-------|--------------------|
| Empty | No simulation run yet → `"belum ada data"` ([UX 00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md)) plus `[proposed]` "Run a simulation to see the path this flow will take." |
| Loading | Steps appear one at a time as they are worked through |
| Error | If the simulation fails because of the configuration: the step, the values it checked, and why it failed |
| Offline | Not available |
| Success | A summary of the path, and the "Activate this version" button becomes available |
| No permission | Simulation results can only be read |

**4. Calculation**

*Nothing reaches the outside world*
- No action step actually runs during a simulation: no notification is sent and no other system is called.
- Instead of acting, the step shows "would send a notification to `<role>` on `<scope>`".

> [!note]- Exact formula (for developers)
> ```
> external_actions_executed = false        for every action step (notification, integration)
> ```

*Timers are skipped ahead*
- In a simulation, every timer counts as already run out, so escalation paths can be tested without waiting.
- Example: a 12-minute andon, with the condition "duration > 10 min" → true → the escalation path. The
  simulation shows "escalate to IDP role claim `<escalation_role>` on site JKT1 (not sent — test mode)".

> [!note]- Exact formula (for developers)
> ```
> simulation_time = real_time / ∞          (treated as immediately expired)
> ```

*Edge cases*
- An incomplete flow → the simulation can't run; the user is sent to the flow's validation errors.
- Sample data outside the implementor's access can't be picked.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORKFLOW_VERSION` (proposed, not modelled) · the location hierarchy · sample-data schemas from the real entities (`ASSET_STATE_LOG`, `WORK_ORDER_OPERATION`), **without** writing anything to them |
| **Writes** | `WORKFLOW_SIMULATION_RUN` (proposed, not modelled) (`version_id`, sample data, resulting path, time, runner) |
| **Not a FOUNDATION entity** | `ROLE`. Assignment targets are IDP role+scope claims ([ABAC](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp)) |

⚠ **Proposed entities, not yet in FOUNDATION.**

**6. Rules & constraints**

1. A simulation **never** acts on the outside world: no notifications are sent and no other system is called.
2. A simulation writes no operational data at all.
3. A version can **only** be activated (`US-FND-WFE-003`) after at least one passing simulation of that
   version.
4. Simulation results are kept as audit evidence.
5. **Who may do it:** running a simulation needs **access to the flow's own scope** (or wider access).

**7. Acceptance criteria**

- **AC-1** **Given** the andon escalation flow is complete, **when** the implementor runs a simulation with a
  12-minute andon, **then** the escalation path is shown step by step, with the reason for each branch.
- **AC-2 (no side effects)** **Given** the flow has a send-notification step, **when** the simulation runs,
  **then** no notification is sent to anyone and that step is marked "not sent — test mode".
- **AC-3 (clear failure)** **Given** a condition points to sample data that is empty, **when** the simulation
  runs, **then** the result shows the step that failed, the values it checked, and the cause.
- **AC-4 (activation needs a test)** **Given** there is no passing simulation for that version, **when** the
  implementor presses "Activate", **then** the action isn't available, with an explanation that a test run
  is needed first.
- **AC-5 (timer)** **Given** the flow escalates after 10 minutes, **when** the simulation runs, **then** the
  escalation path can be tested without waiting in real time.
- **AC-6 (permission)** **Given** a user without access to the flow's own scope (or wider access), **when**
  they open the test-run panel, **then** they can only read existing simulation results.

**8. Metrics & events**

- **Metric:** `alur_lulus_uji_sebelum_aktif` (*flows passing a test before activation*): the share of
  activations that came after a passing simulation. Target **100%** (rule 3 enforces it). Baseline not
  applicable (this is new). Measured from `workflow_simulation_run`.
- **Counter-metric:** the share of live flows that fail in production even though they passed a simulation.
  A high share means the sample data doesn't reflect reality.
- **Events:** `workflow_simulation_run` (`version_id`, `result`, `failed_step`) ·
  `workflow_simulation_blocked` (`reason`).

**Dependencies:** `US-FND-WFE-001`

---

#### US-FND-WFE-003

**Activate a version and pin it on running instances**

**Status:** 🟠 Concept only — foundation-domain hasn't modelled the data for this yet.

> **In short:** the implementor activates a new flow version. Instances already running stay on their old
> version until they finish, so an approval never changes owner mid-flight.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to activate a new version without changing running
instances, so an approval doesn't switch owners mid-flight.

**2. Context**

- **Why:** a running flow stays tied to the version it started on. Changing a flow must not change one that's
  already under way ([F-09 §3.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/09-workflow-engine.md#core-concepts)).
- **Out of scope:** moving running flows onto a new version. They finish on the version they started on
  ([F-09 §3.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/09-workflow-engine.md#core-concepts)).
- **Who and where:** the implementor, at a desktop. It happens rarely, but matters a lot when it does.

**3. Expectation**

- A **list of versions**, each with its status (draft / active / retired), activation date, and **how many
  running flows are on it**.
- Before activating, the impact is shown: "`<n>` instances are running on version `<v>` and will **remain** on
  it until they finish."
- What the user can read within 3 seconds: the active version and how many flows are running.

| State | What the user sees |
|-------|--------------------|
| Empty | No active version yet → `"belum ada data"` / the flow is not running at all ([UX 00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md)) |
| Loading | The running counts load. The activate button stays locked until the impact can be seen |
| Error | "Activation failed — the active version has not changed." |
| Offline | Not available |
| Success | The new version becomes active. The old version is retired, but keeps serving the flows still running on it |
| No permission | Read-only |

**4. Calculation**

*Flows that stay on the old version*
- The number of flows on the old version that haven't finished yet (haven't reached an ending).
- Example: 4 correction-approval flows are running on version 1. Version 2 is activated on 2026-08-10 → all
  four stay on version 1 until they finish. Anything triggered from 2026-08-10 onwards uses version 2.

> [!note]- Exact formula (for developers)
> ```
> instances_pinned_to_old_version =
>     COUNT(WORKFLOW_INSTANCE WHERE version_id = old_version AND status ∉ terminal_states)
> ```

*Edge cases*
- Activating a version that hasn't passed a simulation is refused.
- A flow has only one active version at a time.
- Switching off a whole flow (no active version) is allowed, and means new triggers start nothing. The screen
  must say so clearly, and not look as if the flow is still running.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads** | `WORKFLOW_INSTANCE` (proposed, not modelled) (counts per version) · `WORKFLOW_SIMULATION_RUN` (proposed, not modelled) (the passing-test precondition) |
| **Writes** | `WORKFLOW_VERSION.status` + `valid_from` (proposed, not modelled) · `WORKFLOW_DEFINITION.active_version_id` (proposed, not modelled) |

⚠ **Proposed entities.**

**6. Rules & constraints**

1. A flow has at most one active version.
2. A running flow stays on the version it started on. Flows are never moved to another version.
3. A version can only be activated if it has a passing simulation.
4. Every change of active version is recorded: who, when, from which version to which, and a reason (the
   reason is required).
5. **Who may do it:** activating a version needs **access to the flow's own scope** (or wider access).

**7. Acceptance criteria**

- **AC-1** **Given** 4 flows are running on version 1, **when** the implementor activates version 2, **then**
  all 4 still show version 1 and anything newly triggered runs on version 2.
- **AC-2 (impact)** **Given** some flows are running, **when** the activation screen is opened, **then** the
  number of flows that will stay on the old version is shown before the user can confirm.
- **AC-3 (validation)** **Given** the version hasn't passed a simulation, **when** someone tries to activate
  it, **then** it is refused, with an explanation that a test run is needed first.
- **AC-4 (reason required)** **Given** the reason for activating is left blank, **when** confirm is pressed,
  **then** it is refused with "A reason for the change is required".
- **AC-5 (no active version)** **Given** every version is retired, **when** a trigger event happens, **then**
  nothing new starts and the flow's status clearly reads "inactive".
- **AC-6 (permission)** **Given** a user without access to the flow's own scope (or wider access), **when**
  they open the list of versions, **then** they can only read it.

**8. Metrics & events**

- **Metric:** `instance_berubah_versi_di_tengah_jalan` (*flows that changed version while running*): **must
  be 0**. Measured by comparing `workflow_version_activated` with the version each running flow is on.
  Baseline not applicable.
- **Counter-metric:** activations per flow per month. More than 2 means the flow isn't stable, so the test
  run isn't catching enough.
- **Events:** `workflow_version_activated` (`definition_id`, `from_version`, `to_version`,
  `running_instance_count`, `reason`).

**Dependencies:** `US-FND-WFE-002`

---

#### US-FND-WFE-004

**Work a task and trace the instance history**

**Status:** 🟠 Concept only — foundation-domain hasn't modelled the data for this yet.

> **In short:** the supervisor sees the tasks waiting for them and works them. Every instance keeps a full,
> uneditable trail, so a stuck flow can be diagnosed without a developer.

**1. Story**

As a **production supervisor**, I want to see my tasks plus the full instance trail, so a stuck flow can be
diagnosed without asking a developer.

**2. Context**

- **Why:** every running flow must be able to answer: which step is it on, who did what and when, why did it
  take this branch, and how long did each step take? Without that, nobody can work out why a flow is stuck
  ([F-09 §3.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/09-workflow-engine.md#core-concepts)).
- **Risk:** if nobody plans for failure, flows get stuck and nobody notices
  ([F-09 §7.5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/09-workflow-engine.md)).
- **Who and where:** the supervisor works tasks on a small screen while walking. Working out why a flow is
  stuck happens at a desktop.

**3. Expectation**

- A **"My tasks" list**, with the nearest deadline first. Each row shows the flow, what it's about (for
  example, an andon on Line A), how long it has waited, and what the user can do.
- The **detail of a running flow** shows its full trail as a timeline: each step, who acted, what they
  decided, why that branch was taken, and how long it took.
- What the user can read within 3 seconds: how many tasks are waiting for me, and which has waited longest.

| State | What the user sees |
|-------|--------------------|
| Empty | `"belum ada data"` when the inbox has never loaded any task. Once loaded with zero pending: `[proposed]` "No tasks awaiting you", stated as a good state, not a data gap ([UX 00](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/UX/00-ux.md)) |
| Loading | A placeholder for the list |
| Error | "Could not load tasks — try again". Tasks already loaded stay visible |
| Offline | The last task list saved on the device can be read, marked as possibly out of date. **Working** a task needs a connection, because it changes a flow other people share |
| Success | The task leaves the list with a short confirmation, and the flow's trail shows the new step straight away |
| No permission | Flows outside the user's access don't appear. Opening one directly is refused, with an explanation |

**4. Calculation**

*Times*
- How long a task has waited = now minus the time it was assigned. The device's clock is used for display,
  the server's clock for the audit record.
- How long a step took = the time it finished minus the time it started.

> [!note]- Exact formula (for developers)
> ```
> task_wait_time = now − task.assigned_at        (client_ts for display, server_ts for audit)
> step_duration  = step.finished_at − step.started_at
> ```

*Escalation*
- A task has run out of time when it has waited longer than its step's timer.
- When that happens, the task moves to the escalation role. The old task is marked "expired" and **isn't
  deleted**.
- Example: an acknowledge-andon task goes to people holding IDP role claim `<target_role>` on Line A at 08:00,
  with a 10-minute timer. At 08:11 nobody has acknowledged it → the task moves to IDP role claim
  `<escalation_role>` at site scope, and the trail records "automatic escalation, 10-minute timer exceeded".

> [!note]- Exact formula (for developers)
> ```
> expired = task_wait_time > step.timer_minutes
> ```

*Edge cases*
- A flow stuck because an action on another system failed → it shows an error status that is **visible**,
  with the step that failed, the error message, and a retry button.
- Actions that already succeeded aren't run again on retry (idempotency,
  [F-09 §3.6](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/09-workflow-engine.md#core-concepts)).

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Reads and writes** | `WORKFLOW_INSTANCE` (proposed, not modelled) (status, `version_id`, related object) · `WORKFLOW_TASK` (proposed, not modelled) (`instance_id`, step, role, `scope_id`, `assigned_at`, `acknowledged_at`, `completed_at`, outcome) · `WORKFLOW_AUDIT_LOG` (proposed, not modelled) (step, actor, decision, branch reason, duration, `client_ts`, `server_ts`) |
| **Reads** | The routed object: `ASSET_STATE_LOG` for andon, correction entries, `WORK_ORDER_OPERATION` |
| **Not a FOUNDATION table** | **Whose task it is, is resolved against the IDP's (role, scope) claim, not a FOUNDATION `ROLE_ASSIGNMENT` table.** See the [ABAC contract](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/00-foundation.md#authorization-attribute-contract-abac--what-foundation-exposes-what-stays-in-the-idp) |

⚠ **Proposed entities.**

**6. Rules & constraints**

1. Tasks go to a **role + scope**. Anyone with that role may work the task, and the system records who did.
2. Tasks that ran out of time **aren't deleted**. They are marked and stay in the trail.
3. Actions on other systems must be safe to repeat: a retry must never cause the same effect twice.
4. Flows that have failed (stored status `error`) must show up in the monitoring list, not only in a log.
5. Nobody can edit a flow's trail.
6. **Who may do it:** seeing or working a task needs **access to the task's scope** and the task's role.
   Seeing a flow's trail needs only access to that scope, at any level.

**7. Acceptance criteria**

- **AC-1** **Given** a task is waiting for IDP role claim `<target_role>` on Line A, **when** someone holding
  that claim opens "My tasks", **then** the task appears with how long it has waited and what they can do.
- **AC-2 (escalation)** **Given** the step timer is 10 minutes and nobody has acknowledged the task after 11,
  **when** the list is opened, **then** the task has moved to the escalation role and the trail records why.
- **AC-3 (trail)** **Given** a flow has gone through 5 steps, **when** the supervisor opens its detail,
  **then** all 5 steps appear, each with who acted, what they decided, why that branch was taken, and how
  long it took.
- **AC-4 (stuck flow)** **Given** a send-notification action failed, **when** the flow is viewed, **then** its
  status shows error, with the step that failed, the error message, and a retry button.
- **AC-5 (idempotency)** **Given** an action succeeded before a later step failed, **when** the retry runs,
  **then** the action that already succeeded isn't run a second time.
- **AC-6 (offline)** **Given** the device is offline, **when** the supervisor opens "My tasks", **then** the
  last list appears, marked as possibly out of date, and working a task isn't available.
- **AC-7 (permission)** **Given** a flow on a line outside the user's access, **when** they open a direct link
  to it, **then** access is refused, with an explanation and who to contact.

**8. Metrics & events**

- **Metric:** `waktu_tanggap_task` (*task response time*): the median time from a task being assigned to it
  being acknowledged, per flow. Baseline `not yet measured`; the target is set per flow after 30 days of data.
  Measured from `workflow_task_acknowledged`.
- **Counter-metric:** `% expired tasks` (*the share of tasks that ran out of time*) must not go above
  **`[proposed]` 10%**. Above that, the assignment or the timer is wrong, not the people.
- **Events:** `workflow_task_assigned` (`instance_id`, `role`, `scope_type`) · `workflow_task_acknowledged`
  (`wait_sec`) · `workflow_task_completed` (`duration_sec`, `outcome`) · `workflow_task_escalated`
  (`from_role`, `to_role`, `timer_min`) · `workflow_instance_error` (`step`, `error_type`).

**Dependencies:** `US-FND-WFE-003`, `US-FND-NTF-001`

## Change notes (history — not needed to build)

- 2026-09-23 — module banner shortened to the standard "Not buildable yet" form; the Q3-3 precondition and the proposed initial scope are kept above as plain text. No other history wording was in this module.
- All stories (2026-09-23): detail blocks rewritten from one dense table into numbered sections, and
  "subject whose IDP-asserted scope claim covers …" replaced by glossary access terms (e.g. *site access*, defined in the README glossary).
  Meaning unchanged; UI copy kept verbatim.
- All stories (2026-09-23, second pass): rewritten in plain language. Entity and field names now appear only in
  section 5 and in the collapsible "Exact formula (for developers)" boxes; everywhere else items are named in
  plain words (flow, version, task, running flow, trail). Meaning unchanged; UI copy kept verbatim.
