# Downtime detector (Flink)

(empty — to fill)

Related: [[silver_model]] · [[downtime]] · [[flink_rules]]

# Editing a downtime: what changes for the pipeline

**Audience:** whoever writes the `work_unit_*` Airflow DAGs and dbt models — currently

**Status:** §1–§3 are **normative** and describe behaviour that is already deployed.
§4 is a **proposal**: nothing in it is built, and it is here to be argued with before anyone builds
it. The line between them is marked.

**Scope:** `oee.downtime_reports` and `oee.downtime_report_history`, and the aggregates in
`oee.work_unit_shift_reports` that a downtime's `category` feeds. Companion to
[`gold-write-contract.md`](gold-write-contract.md), which stays the normative reference for the
schema — [§13](gold-write-contract.md#13-editing-a-downtime--the-one-change-you-must-make-to-your-model)
there is the same instruction as §3 here, stated as a contract clause. This document is the
narrative: what the feature does, why only one of its four fields concerns you, and what happens
next.

---

## 1. What an operator can now change

`mes-oee-monitor` ships `PATCH /api/v1/downtimes/:sourceEventId`. It changes exactly four columns on
one row of `oee.downtime_reports`:

| Column          | Type                 | What it is                                     |
| --------------- | -------------------- | ---------------------------------------------- |
| `category`      | `oee.operation_type` | PLANNED / UNPLANNED / SMALL_STOP               |
| `reason`        | `jsonb` array        | the causes cited; element 0 is the primary one |
| `detail_reason` | `text`               | the operator's free-text elaboration           |
| `actions`       | `jsonb` array        | what was done about it                         |

Nothing else. Not `start_at`, not `end_at`, not `duration`, not the denormalised names, not any
identifier. It never inserts a stop and it never deletes one — the rule in
[§1 rule 2](gold-write-contract.md#1-who-writes-what) is unchanged.

Every change also writes one row per changed field into `oee.downtime_report_history`, in the same
transaction as the `UPDATE`, carrying the old value, the new value, the Keycloak subject of the
person who made it, and the instant. A trail that could disagree with the row it describes would be
worse than no trail, so the two commit together or neither does.

### Addressed by `source_event_id`, which you have not written yet

The endpoint takes a stop's `source_event_id` — the column
[§3.3.2](gold-write-contract.md#332-the-six-columns-migration-0007-added-and-what-must-fill-them)
asks you for — and never the `id`. That surrogate `serial` is re-minted on every run under
`DELETE`-then-`INSERT`, so a URL or a history row keyed on it would come to address a different stop.

**The practical consequence today: the feature is deployed and almost entirely unreachable.** The
pipeline does not populate `source_event_id`, so a stop that has none 404s and the browser disables
its Edit item with a tooltip saying the stop has no stable id yet.

**A handful of rows on the dev database do carry one** — hand-seeded UUIDs, not pipeline output; at
the time of writing, three of the seventeen rows in `oee.downtime_reports`, and one of them already
has two hand-written history rows against it. That is enough to exercise the feature end to end, and
it is why the screens can be checked at all before you populate the column. It is not a substitute
for populating it: the other fourteen stops are uneditable, and which stops are editable is currently
an accident of who hand-seeded what.

**The day you populate that column properly, the feature turns on for every stop** — and §3 below
stops being inert in the same run.

---

## 2. Why only `category` concerns you

This is the part worth reading before anything else, because it bounds the whole problem.

```
  operator edits a stop
          │
          ├── reason ─────────┐
          ├── detail_reason ──┤   read LIVE from oee.downtime_reports by
          └── actions ────────┘   the Pareto, the drill-down, the drawer
          │                        ──▶ already correct. Nothing to recompute.
          │
          └── category ──────▶ moves time between SCHEDULE loss and
                               AVAILABILITY loss
                               ──▶ oee.work_unit_shift_reports holds those
                                   totals PRECOMPUTED, so OEE / A / P / Q,
                                   the timeline strip and the waterfall are
                                   stale until you recompute them.
```

Three of the four fields need nothing from you, ever. `mes-oee-monitor` computes the Availability
and Schedule Loss Paretos and the per-reason drill-down by querying `oee.downtime_reports` directly
at read time, so an edit to a reason is reflected the moment it commits.

`category` is the exception, and only because the loss split is precomputed. Until the aggregates
are rebuilt, the report's tiles show the pre-edit split. Today the browser states that plainly — a
banner naming which cards are current and which are not — and waits for your next scheduled run.
**Nothing is broken by that delay; the screen just does not lie about it.**

Two things that deliberately do **not** move when an operator edits a stop:

- **`oee.downtime_log_events`**, and therefore the Others tab.
  [§3.9](gold-write-contract.md#39-downtime_log_events--what-must-fill-it) says never `UPDATE` and
  never `DELETE` there. That table records what was OBSERVED; a count that changed when somebody
  corrected a label would not be a count of anything.
- **`work_unit_day_reports` and `work_unit_month_reports`.** They roll up the same time, so a
  category edit makes them stale too — and nothing today asks you to rebuild them either. Worth
  knowing rather than discovering.

---

## 3. NORMATIVE: apply the override when you rebuild the table

**Your next ordinary run destroys every operator edit unless you do this.** Not a retry, not a
backfill — the scheduled run.

Silver does not know about the correction. §2.1 has you loading `downtime_reports`
`DELETE`-then-`INSERT`, so the corrected row is deleted and the machine's original values are
re-inserted. **Switching to an upsert once `source_event_id` lands does not help**:
`excluded.category` overwrites it just as thoroughly. The load strategy is not the problem — deriving
those four columns from Silver on every run is.

The fix is to treat `oee.downtime_report_history` as the source of truth for those four columns and
Silver as the source of truth for everything else on the row. The exact SQL, the three ways it is
easy to get wrong, the precedence rule and a reconciliation query are in
[§13](gold-write-contract.md#13-editing-a-downtime--the-one-change-you-must-make-to-your-model). In
one line:

```sql
CASE WHEN o.has_category THEN (o.category #>> '{}')::oee.operation_type ELSE s.category END
```

`CASE WHEN`, not `COALESCE`, because an operator can deliberately clear a field. `#>> '{}'`, not
`::text`, because the scalars are stored as jsonb strings and `::text` keeps the quotes.

### What you need to do

1. Add the `latest` / `override` CTEs from §13 to the model that builds `downtime_reports`.
2. `LEFT JOIN` them on `source_event_id` and wrap the four columns in `CASE WHEN has_*`.
3. Ship the singular test in §13 at `error` severity. It is the only mechanical guard that any of
   this happened — the failure mode is a chart that quietly reverts.
4. Request `SELECT` on `oee.downtime_report_history` for the `oee_etl` role (§6). Without it the
   join fails outright, which is the good outcome.
5. **Land 1–4 in the same change as populating `source_event_id`.** Either alone is inert; the
   combination is what works. Populating the key WITHOUT the join is the one ordering that loses
   data.
6. Tell us if any of this is not workable in your model's structure. §4 below is negotiable; this
   section is not, but its SHAPE is.

---

## 4. PROPOSAL — not built, not agreed: asking for one report sooner

Everything below this line is a sketch. **Do not implement it from this document.** It is written
down so the shape can be argued about while it is still cheap to change, and because the alternative
— designing it in a hurry the first time a supervisor complains about a stale tile — is how the
wrong thing gets built.

### The problem it would solve, and how big it actually is

After a `category` edit, the report's tiles are stale until the next scheduled run. Per
[§7](gold-write-contract.md#7-porting-poc-flink-v2) that is `0 0,8,16 * * *`, so the wait is up to
eight hours. The banner is honest about it, and for most edits that is fine.

It stops being fine when somebody corrects a stop specifically in order to look at the corrected
numbers — which is the normal reason to correct one.

### The shape we would propose

A **transactional outbox**, not a message broker.

```
  PATCH /downtimes/:id
        │
        └─ one transaction ─┬─ UPDATE oee.downtime_reports
                            ├─ INSERT oee.downtime_report_history
                            └─ INSERT oee.downtime_recalc_requests  ◀ only if
                                        (work_unit_id, date, shift)   category
                                        status = 'pending'            changed
                                             │
   Airflow, every N minutes ─────────────────┘
        │  UPDATE ... SET status='claimed' WHERE id IN (
        │    SELECT id ... WHERE status='pending'
        │     ORDER BY requested_at FOR UPDATE SKIP LOCKED LIMIT k)
        │  RETURNING *
        │
        ├─ recompute those (work_unit_id, date, shift) keys
        │
        └─ UPDATE ... SET status='succeeded' | 'failed', finished_at, error
```

- **A partial unique index on `(work_unit_id, date, shift) WHERE status = 'pending'`** does the
  coalescing: five edits to one shift queue ONE recalculation, enforced by the database rather than
  by application logic.
- **`status` + `attempts` is the dead-letter queue.** A row stuck in `claimed` past a threshold goes
  back to `pending` until `attempts` runs out, then `failed`. No second topic, no separate DLQ.
- **The status write-back is the completion signal.** The browser polls the request row; nothing has
  to publish anything.

### Why an outbox rather than Kafka, given that a broker already exists

A broker does exist on the Bronze/Silver side, so producing to it would not be free but would not be
exotic either. The argument against is that **the only consumer is Airflow, which already holds a
direct connection to this database.** Routing through a topic would add a consumer group, offset
management, and a second topic to carry completion — to deliver one fact that a `status` column
delivers for free, at a rate of a handful of operator edits a day.

It is also not a dead end. If a second consumer ever appears, the outbox table is exactly the seam a
publisher reads from, and nothing about the API side changes.

The honest counter-argument, which is yours to make: **polling is a scheduled DAG someone has to own
and monitor**, and if you would rather consume a topic you already operate than run another sensor,
that is a legitimate reason to prefer Kafka. We do not have a view on which is less work for you —
which is the whole reason this section is a proposal.

### What we would need from you before building it

1. **Is polling acceptable at all?** A deferrable sensor or a short-interval DAG, every 5–15 minutes.
2. **Can a recalculation be scoped to one `(work_unit_id, date, shift)`**, or does the model only
   rebuild whole days? If only whole days, the queue's grain should be the day and the table's shape
   changes.
3. **How long does that recomputation take?** It sets the polling interval and what the banner
   should promise.
4. **Would you rather consume a Kafka topic?** If so, say which broker and topic conventions apply,
   and we would produce from the outbox row instead of having you read it.

---

## Open risks

- **§3 is unenforced, and it is the one that loses data.** No constraint can express "this column
  should hold what a different table's latest row says". The dbt test is the only guard, and it lives
  in a repository nobody in this repo can review
  ([§18](../TODOS.md), the DAG has no repository). A chart that silently reverts to the machine's
  values is the failure, with the operator's correction still visible in the trail.
- **The ordering in §3 step 5 is a real trap.** Populating `source_event_id` without the override
  join turns the feature on and starts losing edits in the same run.
- **Day and month rollups are stale after a category edit and nothing addresses it.** No screen reads
  them for this yet, so nothing is visibly wrong — which is exactly why it is easy to forget.
- **§4 is unbuilt, so the banner cannot clear itself.** It reports that an edit happened and does not
  claim the pipeline has caught up, because per-report confirmation is not knowable without §4. Until
  then it stays on screen. A banner that lingers is a nuisance; one that cleared early would be a
  lie, and `etl_watermarks.last_run_at` is table-grained, so comparing against it would clear early.

# Downtime Handoff
# Hand-off: what to change before populating `source_event_id`

**Audience:** whoever writes the `work_unit_*` Airflow DAGs and dbt models — currently
**Wira Hutomo** (`pwawiwa`, wira.hutomo@gmail.com).

**Status:** a **cover note**, not a specification. Everything here points at
[`gold-write-contract.md`](gold-write-contract.md), which stays normative; nothing in this file
overrides it. It exists because the contract is long and two of its sections became urgent at a
specific moment, and "read §13 before you do §3.3.2" is not a thing a document can say about itself.

**Scope:** the four operator-editable columns of `oee.downtime_reports`, and the ordering constraint
between the override join and populating `source_event_id`.

---

**TL;DR:** re-deriving four columns of `oee.downtime_reports` from Silver on every run silently
discards operator corrections. The fix is a `LEFT JOIN` documented in
[§13](gold-write-contract.md#13-editing-a-downtime--the-one-change-you-must-make-to-your-model). It
must land in the **same change** as populating `source_event_id`, not after. Separately,
[§14](gold-write-contract.md#14-what-0011-adds--a-shape-guard-that-can-fail-your-load) can abort your
load outright, which is a different and louder problem.

---

## 1. The problem in four lines

1. An operator corrects a stop: `category` UNPLANNED → PLANNED. Saved.
2. Your next run loads `downtime_reports` from Silver, which knows nothing about that correction.
3. §2.1 has you loading that table `DELETE`-then-`INSERT`, so the corrected row is deleted and the
   machine's original values are re-inserted.
4. The chart reverts. **Nothing errors.** The correction is gone, and the only trace is a row in
   `oee.downtime_report_history`.

This is your **next ordinary scheduled run** — not a retry, not a backfill.

**Switching to an upsert does not fix it.** Once `source_event_id` is populated and the model becomes
`ON CONFLICT (source_event_id) DO UPDATE`, `excluded.category` overwrites the operator's value just
as thoroughly. The load strategy was never the problem; **re-deriving those four columns from Silver
on every run is.**

## 2. What to change

`oee.downtime_report_history` becomes the source of truth for four columns — `category`, `reason`,
`detail_reason`, `actions`. Silver stays the source of truth for everything else on the row.

The full SQL is in **§13**. In outline: a `DISTINCT ON (source_event_id, field)` CTE for the latest
override per field, `LEFT JOIN`ed on `source_event_id`, with each of the four columns wrapped in
`CASE WHEN has_x`.

Three specifics worth reading twice, because each one type-checks while being wrong:

- **`CASE WHEN has_x`, never `COALESCE`.** An operator can deliberately CLEAR `detail_reason`, stored
  as a jsonb `null`. `COALESCE` would read that as "said nothing" and put the machine's text back.
- **`#>> '{}'` for the two scalars, never `::text`.** They are stored as jsonb strings, so `::text`
  yields `"PLANNED"` _with the quotes_, and the enum cast then fails pointing at the enum rather than
  at the quoting.
- **`array_agg(...)[1]`, not `max(...)`.** There is no `max(jsonb)` in Postgres. §13 carried the
  `max()` version briefly and it does not run — found by executing it rather than reading it.

Every statement in §13 has been executed against **Postgres 18** and is asserted in
`apps/api/src/module/downtime/downtime-repository.integration.test.ts`, including the round trip:
restate the row, re-apply the override, confirm it heals.

> **On the version:** these were originally verified against 16, because CI, `docker-compose.yml` and
> both integration-test headers pinned `postgres:16-alpine` while the deployed Gold database is
> **18.4** — a two-major gap that had gone unnoticed for months. Everything was re-run against 18 and
> passed unchanged, and the pins now match. Flagged in case anything on your side is also pinned to
> 16 on the same stale assumption.

## 3. Also read §14 — it can fail your load rather than corrupt it

`0011_oee_downtime_reason_shape.sql` added CHECK constraints on the shape of `reason`, `actions` and
`detail_reason`. **A row that violates one aborts the statement**, so a load carrying a malformed
value fails loudly instead of landing.

That is the _good_ failure mode and it needs no join to defend against — but it is a new way for your
DAG to go red, and the message will point at a constraint name rather than at the row. §14 lists what
each constraint requires and the shapes that trip it.

## 4. The ordering, which is the actual trap

`source_event_id` is what the edit endpoint addresses a stop by, so **populating it is what turns the
feature on.** That makes it the natural thing to do first — and populating it _without_ the override
join is the one sequence that loses data.

Please land these together:

1. The `latest` / `override` CTEs and the four `CASE WHEN` columns (§13).
2. The singular dbt test in §13, at `error` severity. It is the only mechanical guard that any of
   this happened — no constraint can express "this column should hold what a different table's latest
   row says".
3. `SELECT` on `oee.downtime_report_history` for the `oee_etl` role (§6). Without it the join fails
   outright, which is the good outcome.
4. Populating `source_event_id` itself (§3.3.2 / TODOS §19). Please write the same string into
   `downtime_log_events.event_id` — §2.1 asks for one identifier in both, which is what makes the two
   tables reconcilable.

**Precedence, so you do not have to infer it:** the operator wins, including over a later upstream
correction. Both values stay in the history table, so nothing is lost, and a genuine upstream
correction can be applied by editing the stop again.

## 5. What is NOT asked of you

- **Never write `oee.downtime_report_history`.** Read it in the projection; no insert, update or
  delete. Same as the two approval tables.
- **No `etl_watermarks` row for it** — `ck_etl_watermarks_table` will reject one, deliberately.
- **`downtime_log_events` is untouched by all of this**, and that is the point of it being a separate
  table: an operator correcting a stop does not change what was _observed_, so the Others tab
  deliberately does not move when the Availability Pareto does.
- **`oee.write_audit` is now partitioned by month** (`0012`) and pruned to twelve months. Nothing
  changes for you — the triggers still fire and you still insert nothing — but §5.1 is worth two
  minutes because a partitioned table _rejects_ an insert with no matching partition, and that would
  surface as your loads failing rather than as a gap in an audit log. There is a DEFAULT partition
  precisely so that cannot happen.
- **Nothing about recalculation, yet** — see below.

## 6. The four questions, and where they stand

Changing a stop's `category` moves time between schedule loss and availability loss, and those totals
are precomputed in `work_unit_shift_reports`. So a report's OEE / A / P / Q tiles, its timeline strip
and its waterfall are stale until your next run. Today the screen says so plainly and waits — nothing
is broken by the delay.

We sketched a way to ask you to recompute _one_ report sooner. It is written up in
[`downtime-edit-trd.md`](downtime-edit-trd.md) §4 and is explicitly **a proposal, not built, not
agreed** — please argue with it before anyone implements it.

**Three of the four now have a provisional answer from the OEE side. They are recorded here as
answers we are working from, NOT as decisions made for you** — questions 2 and 3 are about what your
model can actually do, and only you can settle them.

| Question                                                                   | Where it stands                                                                                                                                                                                                                                                                                                                                                    |
| -------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **1. Is polling acceptable at all?**                                       | Provisionally yes, **every 5 minutes**. Please confirm a deferrable sensor or short-interval DAG is something you are willing to own.                                                                                                                                                                                                                              |
| **2. Can a recalculation be scoped to one `(work_unit_id, date, shift)`?** | Provisionally yes, with the DAG fanning out to the day and month aggregates. **This determines the request table's primary key**, so it is the answer we most need. Note there is no year-grain table in Gold, so year is out of scope.                                                                                                                            |
| **3. How long does that recomputation take?**                              | **Still open, and nobody on our side can answer it.** It sets the reaper's "this run is stuck" threshold and what the screen is allowed to promise. We are assuming ≤5 minutes and will set the threshold to 15; tell us if that is wrong by an order of magnitude.                                                                                                |
| **4. Would you rather consume a Kafka topic?**                             | Provisionally no — a table for now, revisited later. A broker exists on the Bronze/Silver side, and we defaulted to a table because the only consumer is Airflow, which already holds a direct Gold connection. But **polling is a DAG you would have to own and monitor**, and if that is the worse trade for you, say so and we will produce to a topic instead. |

One more we should have asked: **what should happen to the request table's own rows?** We are
planning to delete terminal (`succeeded` / `failed`) rows after 30 days. It is the same problem
`write_audit` had and it needs a scheduler, which brings us to the next section.

## 7. Two things we need from your side of the fence

Both are infrastructure rather than code, and both are blocking something already written:

1. **`pg_cron` is not installed** on the Gold cluster — nor is `pg_partman`, and `webadmin` is a
   superuser, so it is a genuine absence rather than a permissions problem. Two jobs want it: pruning
   `write_audit` (§5.1) and auto-closing shift reports older than 14 days. If Airflow is the better
   home for both, that is a perfectly good answer — it is a scheduler either way, and you already
   operate one.
2. **The roles in [§6](gold-write-contract.md#6-proposed-database-roles)**, including the amended
   `SELECT` on `oee.downtime_report_history`. Today every connection is `webadmin` with a blank
   `application_name`, and no table in the `oee` schema carries any grant at all.

## 8. Where things stand

The feature is merged and live. On the dev database **all 17 `downtime_reports` rows now carry a
`source_event_id`** and are editable, with 15 rows in `downtime_report_history` — so the write path
has been exercised end to end rather than merely shipped. What is still true is that **nothing the
pipeline has loaded carries one**, which is what §13 plus TODOS §19 turns on properly.

Happy to walk through §13 together if that is easier than reading it.