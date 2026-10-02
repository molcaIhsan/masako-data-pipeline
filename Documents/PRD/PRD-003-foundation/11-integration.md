# PRD-003 · Integration & Service Monitoring (`INT`)

> Part of [PRD-003 FOUNDATION](README.md). Spec: F-11. Closed stories for this module are in [90-closed-stories.md](90-closed-stories.md).
> Stories are written in plain language. Exact field names are in section 5, and each exact formula is in a
> collapsible "for developers" box under its plain explanation.


> ⚠ **Not buildable yet.** The entities below (`SYNC_QUEUE_ENTRY`, `CONNECTOR_HEALTH`) are proposals in this
> PRD; `foundation-domain` has not modelled them — [F-11](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md) is still a concept.
> Do not estimate or build these stories until it does ([F-Q-03](30-release-risks-questions.md#11-open-questions)).
> Both stories are also **blocked on ownership** — see each story's status.

- **Scope.** INT covers **ERP/peer-system integration only**. Machine/sensor connections (`DATA_SOURCE`/`ASSET_TAGS`)
  belong to the [`DSR` module](13-data-source.md).
- **KRs held here.** [KR 1B.2](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/goalsQ3.md#track-1b--platform-and-edge-engineering-backend-data-engine)
  (offline sync, `US-FND-INT-002`) and [KR 1A.1](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/goalsQ3.md#track-1a--monitoring-and-controlling)
  (service monitoring, `US-FND-INT-003`). KR 1B.1 (machine tag declaration) belongs to `DSR`.
- **Offline sync is mandatory.** `US-FND-INT-002` (offline sync for the operator app) stays mandatory whatever
  the answer to the PLC/SCADA readability `TBD` ([00 §6](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/00-product-overview.md#6-batasan-awal)).
  It is the most important data path in the first release.
- **No ERP-connector story yet.** [F-11](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md) has no concrete
  ERP fields, screens, or entities to write one from. One will be added, with a new ID, once an ERP path is scoped.

## Open items in this module

| Item | Story | What is undecided | Owner |
|------|-------|-------------------|-------|
| Entity model | `US-FND-INT-002`, `US-FND-INT-003` | `SYNC_QUEUE_ENTRY`, `SYNC_RECEIPT`, `CONNECTOR_HEALTH` are not modelled ([F-Q-03](30-release-risks-questions.md#11-open-questions)) | `foundation-domain`, PO |
| Ownership of offline sync | `US-FND-INT-002` | Whether it moves to a PRODUCTION story (new ID) or stays in INT (`TBD`) | PO routes; `production-domain` vs INT |
| Ownership of Service Monitoring | `US-FND-INT-003` | Should move to DSR, or stay blocked on H-DS-1 under DSR | PO |
| H-DS-1 heartbeat granularity | `US-FND-INT-003` | Connection-level vs per-tag `data_age_seconds`; `DATA_SOURCE.heartbeat_interval`/`last_seen_at` is a placeholder (`TBD`) | `foundation-domain` (F-13) |
| PLC/SCADA readability | module scope | `TBD` in [00 §6](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/00-product-overview.md#6-batasan-awal); does not block `US-FND-INT-002` | not stated |
| KR 1B.2 test definition | `US-FND-INT-002` | Outage length, scenarios, recovery time ([§11 Q-09](30-release-risks-questions.md)) | PO + `engineering-domain` |
| ERP connector story | — | No story until an ERP path is scoped | not stated |
| `[proposed]` numbers | `US-FND-INT-002`, `US-FND-INT-003` | 8-hour offline endurance; 100% synced ≤15 min, ≤1% conflicts; thresholds 300 s / 1×/day + 4 h / 8 h; detection ≤5 min, 100% coverage; false alarms ≤5% | PO |

## Stories


| ID | User story title | Status | Description | Dependencies |
|----|------------------|--------|-------------|--------------|
| [`US-FND-INT-002`](#us-fnd-int-002) | Guarantee zero lost entries when offline | 🔴 Blocked — ownership | As a **line operator**, I want my entries to persist and sync once the connection returns, so I never record anything twice. → [detail](#us-fnd-int-002) | `US-FND-DSR-001` |
| [`US-FND-INT-003`](#us-fnd-int-003) | Monitor data acquisition health | 🔴 Blocked — ownership / H-DS-1 | As a **Plant Admin/IT**, I want to know within minutes when a source stops sending data, so gaps aren't discovered in the monthly report. → [detail](#us-fnd-int-003) | `US-FND-DSR-001`, `US-FND-NTF-001` |

## Detail blocks

---

#### US-FND-INT-002

**Guarantee zero lost entries when the connection drops**

**Status:** 🔴 Blocked — it isn't decided yet whether this belongs to PRODUCTION or INT. It's also concept only: foundation-domain hasn't modelled the data for it yet.

> **In short:** the operator keeps recording when the network drops. Every entry is saved on the device and
> synced later, exactly once, so nothing is lost and nothing is counted twice.

> **Why blocked (do not delete this ID).** According to [F-11](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md)
> (B7), INT only covers **one system talking to another (ERP or peer systems)**. This story is about **the
> operator's device on the shopfloor working offline** — it belongs with PRODUCTION's shopfloor, not INT.
> `TBD — ownership: production-domain vs INT` — move it under a PRODUCTION story (with a new ID there) when the
> PO routes it. Until then, don't build it as part of INT. The text is kept so nothing is lost.
> The capability itself is still required for the product ([KR 1B.2](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/goalsQ3.md#track-1b--platform-and-edge-engineering-backend-data-engine));
> only the module it sits in is wrong.

**1. Story**

As a **line operator**, I want my entries to persist and sync once the connection returns, so I never record
anything twice.

**2. Context**

- **Why:** nobody can count on the plant network being stable, and the rule is simple: **never throw away
  anything an operator entered** ([P-01 §5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#log-monitoring)).
- **What "0 data loss" means:** in practice, the target of
  [KR 1B.2](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/goalsQ3.md#track-1b--platform-and-edge-engineering-backend-data-engine) means **every entry
  is sent at least once, and the receiving side ignores repeats (idempotency)**, not that each entry is sent
  exactly once ([F-11 core concept 4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md#core-concepts)).
- **Risk:** without a key that marks each entry as unique, sending again doubles the production output, and
  that's hard to spot ([F-11 common pitfalls](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md#common-pitfalls)).
- **Note:** F-11 no longer owns this (B7). The F-11 references are background only.
- **Who and where:** the operator, on a small screen, on a network that comes and goes. They must not have to
  think about any of this.

**3. Expectation**

- When online with nothing waiting to be sent, there is **no** sync indicator at all. The operator doesn't need
  to be told that things are normal.
- Every new entry straight away gets a "saved on device" confirmation.

| State | What the user sees |
|-------|--------------------|
| Default | Online, nothing waiting → no sync indicator |
| Offline | The offline marker stays visible, with the **number of entries waiting to be sent** |
| Loading (syncing) | The waiting count can be seen going down. The input screen keeps working |
| Sync error | Entries that failed **aren't lost**. They appear in a "not yet synced" list with the cause and a retry button. The message must say the data is safely stored on the device, not lost ([P-01 §4](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#capabilities-figma)) |
| Success | The waiting count reaches 0 → the indicator quietly disappears |
| Empty | not applicable |
| No permission | not applicable |

**4. Calculation**

*A unique key for each entry*
- Each entry gets a key made from what kind of entry it is, the station, the shift, the device time, the device,
  and the entry's content.
- The key is made **on the device** when the entry is created, not on the server.
- If the receiver gets an entry with a key it has already seen, it doesn't create a new row, but it still
  answers "success" (so the sender stops retrying).

> [!note]- Exact formula (for developers)
> ```
> idempotency_key = hash(entity_type, work_unit_id, shift_instance_id, client_ts, device_id, payload_hash)
> ```

*Order and time*
- Entries are sent oldest first, by the time on the device when they were entered.
- Durations in metrics use the device time. The server's receive time is only for the audit record
  ([04 §4](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/04-metrics-framework.md#4-konvensi-event)).
- Example: an operator records 3 output entries offline at 10:05, 10:20 and 10:41. The connection comes back
  at 12:00 → all three are sent, in that order. Sending the 10:20 entry again after a timeout gives **one** row,
  not two.

> [!note]- Exact rule (for developers)
> ```
> send order        = ascending client_ts
> metric durations  use client_ts;  server_ts is for audit only
> ```

*Checking the totals match*
- The number sent, minus the number received, minus the number refused as repeats, should be 0. If it isn't,
  the difference is reported, not ignored
  ([F-11 core concept 5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md#core-concepts)).

> [!note]- Exact formula (for developers)
> ```
> delta = sent_count − received_count − duplicate_rejected_count
> ```

*Edge cases*
- An entry that arrives for a shift that's already closed is **accepted** as a correction, and the shift is
  marked for review ([P-01 §5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#log-monitoring)).
- Two downtimes from two devices that overlap → both are kept and marked as a conflict. The supervisor sorts it
  out before closing the shift.
- Device storage full → entries already sent are removed first. Entries not yet sent are **never** removed,
  and the operator is told.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Writes** | `SYNC_QUEUE_ENTRY` (proposed, not modelled) (local on the device: `idempotency_key`, `entity_type`, payload, `client_ts`, status, attempt count, last failure reason) · `SYNC_RECEIPT` (proposed, not modelled) (server: `idempotency_key`, `received_at`, outcome) · sync results into `WORK_ORDER_OPERATION`, `ASSET_STATE_LOG`, andon entries, correction entries |
| **Reads** | `SHIFT_INSTANCE.status` (to handle the closed-shift case) |

⚠ **Proposed entities.**

**6. Rules & constraints**

1. What **must** work offline: recording output, recording and closing downtime, recording defects, and raising
   an andon ([P-01 §5](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/02-monitoring.md#log-monitoring)).
2. What may need a connection: closing a shift, dashboards across lines, and changing the schedule.
3. The device must be able to work offline for at least one full shift: **`[proposed]` 8 hours**.
4. Every entry must carry a unique key made on the device.
5. Entries not yet sent are never removed, whatever happens.
6. Output entries **add up**; they don't overwrite each other. Two entries for the same job and period are
   both kept.
7. Entries are sent in order of their device time.
8. Any difference found when checking totals must be reported.

**7. Acceptance criteria**

- **AC-1 (0 data loss)** **Given** the device is offline for 8 hours and 40 entries are made, **when** the
  connection comes back, **then** all 40 entries are sent and the server's count equals the device's count.
- **AC-2 (idempotency)** **Given** one entry is sent twice because of a timeout, **when** the server receives
  both, **then** only one row is created and the sender gets "success" for both.
- **AC-3 (event time)** **Given** an entry is made offline at 10:20 and sent at 12:00, **when** a duration is
  worked out, **then** it uses the device time, 10:20, and the server time, 12:00, is kept for the audit
  record only.
- **AC-4 (closed shift)** **Given** an offline entry arrives for a shift that's already closed, **when** it's
  sent, **then** the entry is accepted as a correction and the shift is marked for review, not refused.
- **AC-5 (conflict)** **Given** two devices record overlapping downtime on the same asset, **when** both are
  sent, **then** both are kept, marked as a conflict, and closing the shift asks the supervisor to sort it out.
- **AC-6 (storage full)** **Given** the device storage is nearly full, **when** a new entry is made, **then**
  entries already sent are removed first, entries not yet sent stay untouched, and the operator is told.
- **AC-7 (visible error)** **Given** one entry fails to send because its data is invalid, **when** the operator
  opens the not-yet-synced list, **then** that entry appears with its cause and a retry button, along with a
  statement that the data is safely stored on the device.
- **AC-8 (checking totals)** **Given** 40 were sent and 39 received, **when** the totals are checked, **then**
  the difference of 1 is reported to service monitoring, together with which entry wasn't recorded.

**8. Metrics & events**

- **Metric:** `entri_offline_tersinkron_tanpa_konflik` (*offline entries synced without conflict*): the share
  of entries made offline that are sent with no conflict. Target **`[proposed]` 100% synced, ≤1% conflicts**.
  Baseline not applicable (this is new).
- **KR 1B.2 test definition (the PO must decide):** **`[proposed]` an 8-hour outage × 3 scenarios (total
  connection loss, intermittent connection, device powered off), 100% synced ≤15 minutes after recovery**
  ([Goals Q3 §6.2](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/goalsQ3.md#62-kr-belum-terukur), [§11 Q-09](30-release-risks-questions.md)).
- **Counter-metric:** the number of entries doubled by sending again: **must be 0**.
- **Events:** `sync_queue_enqueued` (`entity_type`, `queue_depth`) · `sync_batch_completed` (`entry_count`,
  `duration_sec`, `conflict_count`) · `sync_duplicate_rejected` (`idempotency_key`) ·
  `sync_reconciliation_mismatch` (`sent`, `received`, `delta`).

**Dependencies:** `US-FND-DSR-001` — only for order: connections are declared before the sync path that may
carry related readings. This does **not** mean DSR writes `ASSET_STATE_LOG` (F-13 does not store readings).
Who owns this story is itself TBD (PRODUCTION vs INT).

---

#### US-FND-INT-003

**Monitor data acquisition health (Service Monitoring)**

**Status:** 🔴 Blocked — it should move to DSR, and H-DS-1 (whether "how old is the data" is tracked per connection or per tag) is undecided. It's also concept only: foundation-domain hasn't modelled the data for it yet.

> **In short:** IT sees every data source in one list and learns within minutes when one stops sending, instead
> of finding the gap in the monthly report. Only parts that don't depend on H-DS-1 can be built today.

> **Why blocked (do not delete this ID).**
> - **Who owns it.** According to [F-11](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md) /
>   [F-13](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md), watching whether machine connections are
>   still sending (their heartbeat, and how stale their data is) **belongs to DSR**, not INT (ERP/peer). It
>   should move to DSR (or stay blocked on H-DS-1 under DSR).
> - **H-DS-1.** The logic that decides a source's status is blocked on
>   [Data Source H-DS-1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md#whats-still-unknown) —
>   "Do not build staleness alerting against either answer until this resolves" (the same pattern as
>   `US-FND-DSR-001`'s transformation gap). The connection's heartbeat fields are only placeholders, and it
>   isn't decided whether they're tracked per connection or per tag. The healthy / late / stopped limits below
>   assume one "age of data" figure per connection — exactly what H-DS-1 says not to build on yet.
> - The limits are kept as a `[proposed]` design: useful once H-DS-1 is settled, not buildable before.

**1. Story**

As a **Plant Admin/IT (internal implementor)**, I want to know within minutes when a source stops sending data,
so gaps aren't discovered in the monthly report.

**2. Context**

- **Why:** if nobody watches the connections, data can stop arriving and nobody notices until a monthly report
  looks odd ([F-11 common pitfalls](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/11-integration.md#common-pitfalls); the
  heartbeat question is covered in [F-13](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md)).
- **KR:** this is **KR 1A.1**, watching the **system**. It's different from
  [P-02 OEE Monitoring](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/PRODUCTION/DOCS/03-performance.md), which watches **production**. The
  two have similar names but are different things, which is why this module is called **Service Monitoring**
  ([Goals Q3 §6.3](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/goalsQ3.md#63-celah-cakupan)).
- **Who and where:** the implementor or IT, at a desktop. Also followed through notifications.

**3. Expectation**

- A **list of data sources** (machine connections, the ERP link, the queues of entries waiting on devices) with
  these columns: status, **how old the latest data is**, messages per minute, failures in the last 24 h, and
  the latest difference found when checking totals.
- The sources with the most problems come first.
- What the user can read within 3 seconds: whether any source has stopped.

| State | What the user sees |
|-------|--------------------|
| Empty | No sources registered → point the user to `US-FND-DSR-001`. For a plant with no PLC, show only the operator devices' waiting queues, **not** a blank screen |
| Loading | A placeholder for the table |
| Error | If the monitoring service itself can't be reached, say plainly "status unknown". **Don't** show everything as green |
| Offline | not applicable (admin screen, desktop) |
| Success | not applicable |
| No permission | Needs **site access** to the site being watched (or enterprise/tenant access) |

**4. Calculation**

**Blocked on H-DS-1.**

*Status (as proposed)*
- How old a source's data is = now minus the time of its latest reading.
- The source is healthy if that age is within its limit, late if it's within twice the limit, and stopped if
  it's more than twice the limit.
- Each source's limit comes from the Configuration Service. First proposals: a machine **`[proposed]` 300
  seconds**, the ERP **`[proposed]` 1×/day + 4-hour tolerance**, a device's waiting queue **`[proposed]` 8
  hours** (the same as the offline limit).
- Example (for illustration only, can't be built yet): machine connection IM-003's last reading was at 08:00,
  it's now 08:12, and the limit is 300 s → the data is 720 seconds old → more than 2×300 → status stopped → an
  alarm goes out through the notification rules.
- **This can't be built today.** H-DS-1 hasn't decided whether the age of data is one figure per connection or
  would need to be tracked per tag, and the connection's heartbeat fields are only placeholders, not a settled
  design, until then.

> [!note]- Exact formula (for developers)
> ```
> data_age_seconds = now − MAX(last_reading.client_ts)        per source
>
> healthy   if data_age_seconds ≤ source_threshold
> late      if data_age_seconds ≤ 2× threshold
> stopped   if data_age_seconds > 2× threshold
> ```

*What **can** be built without that decision*
- Messages per minute = the number of messages in the last 60 seconds. It doesn't depend on deciding whether
  data is stale.

> [!note]- Exact formula (for developers)
> ```
> message_rate_per_minute = message count in the last 60 seconds
> ```

*Edge cases (how it should work once H-DS-1 is settled; not buildable now)*
- A source that rightly sends nothing outside working hours wouldn't be marked stopped: limits are only
  checked while a production shift is running.
- A new source that has never sent anything would show the status never active (stored as `never active`),
  not stopped.

**5. Data & entities**

| | Entities and fields |
|---|---|
| **Writes** | `CONNECTOR_HEALTH` (proposed, not modelled) (`connector_id` / source, `last_value_at`, `status`, `message_rate`, `failure_count_24h`, `last_reconciliation_delta`). Name kept `⚠ proposed`, not yet a real F-09/F-13 entity |
| **Reads** | `DATA_SOURCE` ([F-13 §9.1](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/13-data-source.md#91-data_source--one-connectionendpoint)) and `ASSET_TAGS` ([F-04 §9.7](https://github.com/molca-id/docs-molcadx/blob/main/DOMAINS/FOUNDATION/DOCS-EN/04-asset-ontology.md#97-asset_tags--per-asset-tag--signal-declaration)), both declared by [`US-FND-DSR-001`](13-data-source.md#us-fnd-dsr-001) · `SYNC_RECEIPT` (still `⚠ proposed`, `US-FND-INT-002`) · `SHIFT_INSTANCE` (for operating hours) · thresholds from Configuration Service · notification rules from `US-FND-NTF-001` |

⚠ **Proposed entities.** `DATA_SOURCE.heartbeat_interval`/`last_seen_at` granularity is `TBD` (H-DS-1). This
story's `data_age_seconds` calculation (section 4) assumes connection-level heartbeat, which is exactly the
assumption H-DS-1 says is not yet decided.

**6. Rules & constraints**

1. The module is called **Service Monitoring**. A plain "Monitoring" label is not allowed anywhere in the
   screens, so it can't be mixed up with OEE Monitoring.
2. **Blocked on H-DS-1.** A stop must be detected within **`[proposed]` 5 minutes** of happening, for
   **`[proposed]` 100%** of registered sources ([Goals Q3 §6.2](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/goalsQ3.md#62-kr-belum-terukur)). This
   can't be built until it's decided how the age of data is tracked.
3. An "unknown" status must never be shown as healthy. Not blocked: this applies whatever H-DS-1 decides.
4. **Blocked on H-DS-1.** Limits are only checked during working hours (this relies on the same limits as
   rule 2).
5. Failures send notifications through `US-FND-NTF-001`, not through a separate mechanism.
6. **Who may see it:** viewing needs **site access** to that site (or enterprise/tenant access).

**7. Acceptance criteria**

- **AC-1 — blocked on H-DS-1.** Can't be tested until it's decided how heartbeat and staleness are tracked.
  Today there's no settled "age of data" figure to decide status from. As proposed: **given** a machine
  connection stops sending at 08:00 with a 300-second limit, **when** it's 08:11, **then** that source's
  status is stopped and an alarm goes to the configured role, within 5 minutes of passing twice the limit.
- **AC-2 (outside working hours) — blocked on H-DS-1.** Relies on the same "stopped" status as AC-1.
- **AC-3 (never active) — blocked on H-DS-1.** Relies on the same status logic as AC-1.
- **AC-4 (unknown status)** **Given** the monitoring service can't be reached, **when** the screen is opened,
  **then** every status shows "unknown" with an explanation, not green. **Not blocked:** this doesn't depend on
  the age-of-data status, only on whether the monitoring service itself can be reached.
- **AC-5 (no PLC)** **Given** the plant has no machine connection, **when** the screen is opened, **then** the
  operator devices' waiting queues still appear as watched sources. **Not blocked.**
- **AC-6 (checking totals)** **Given** checking the totals finds a difference of 3 entries, **when** the screen
  is opened, **then** that difference appears on its source's row, together with when it was last checked.
  **Not blocked:** this difference doesn't depend on H-DS-1.
- **AC-7 (permission)** **Given** a user without site access to this site (and without enterprise/tenant
  access), **when** they open Service Monitoring, **then** access is refused, with an explanation. **Not
  blocked.**

**8. Metrics & events**

- **Metric:** `waktu_deteksi_kegagalan_akuisisi` (*how long it takes to notice a source has stopped*): the
  median time between a stop happening and the alarm going out. Target **`[proposed]` ≤5 minutes**, covering
  **`[proposed]` 100%** of sources. This is how KR 1A.1 is measured. **Can't be measured until H-DS-1 is
  settled:** there's no "stopped" status to detect yet. Baseline not applicable (this is new).
- **Counter-metric:** `% false alarms` (a source marked stopped when it was rightly outside working hours) must
  stay at or below **`[proposed]` 5%**. Above that, the limit or the working hours are wrong. **The same H-DS-1
  block applies.**
- **Events:**
  - `source_health_changed` (`source_type`, `from_status`, `to_status`, `data_age_sec`) — **blocked on H-DS-1**
  - `source_stopped_alerted` (`detection_sec`) — **blocked on H-DS-1**
  - `reconciliation_delta_reported` (`delta`) — not blocked

**Dependencies:** `US-FND-DSR-001`, `US-FND-NTF-001`

## Change notes (history — not needed to build)

- `US-FND-INT-001` (2026-09-04, GitHub issue #20) — `DATA_SOURCE`/`ASSET_TAGS` (machine/sensor connections) moved to the new `DSR` module; `US-FND-INT-001` was closed and F-11 became ERP/peer-system integration only.
- `US-FND-INT-002` (2026-09-04) — dependency changed from `US-FND-INT-001` (closed) to `US-FND-DSR-001`.
- `US-FND-INT-002` — the blocked body is kept following the same pattern as the `US-FND-SIT-002` blockers. After B7, the story's F-11 citations are historical.
- `US-FND-INT-003` (2026-09-04) — `Dependencies`, `Data & entities` and `Expectation` now point at `US-FND-DSR-001` (`DATA_SOURCE`/`ASSET_TAGS` declaration) instead of the closed `US-FND-INT-001`. No content changed, only which story owns the entities it reads (they moved out of `US-FND-INT-001`).
- `US-FND-INT-003` — "As originally conceived" in the Calculation row now reads "As proposed".
- All stories (2026-09-23): detail blocks rewritten from one dense table into numbered sections, and
  "subject whose IDP-asserted scope claim covers …" replaced by glossary access terms (e.g. *site access*, defined in the README glossary).
  Meaning unchanged; UI copy kept verbatim.
- All stories (2026-09-23, second pass): rewritten in plain language. Entity and field names now appear only in
  section 5 and in the collapsible "Exact formula (for developers)" boxes; everywhere else items are named in
  plain words (entry, unique key, device time, age of data, machine connection). Meaning unchanged; UI copy
  kept verbatim.
