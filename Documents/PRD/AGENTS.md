# AGENTS.md — MolcaDx PRD

PRDs for **MolcaDx**, Molca's digital-transformation platform for manufacturing operations.
**Read this file first.** It tells you what to read, and what to skip.

- **Owner:** Product Owner.
- **Specs live elsewhere:** the domain specs (entities, fields, formulas, screens) live in
  [`docs-molcadx`](https://github.com/molca-id/docs-molcadx). PRD links into that repo use GitHub URLs.
- **Moved here:** 2026-09-24, from `docs-molcadx/DOCS/prd/`. Older history is in that repo's git log.

## 1. What is here

| File | What it is | Status |
|------|------------|--------|
| [`PRD-003-foundation/`](PRD-003-foundation/README.md) | **Active PRD for the FOUNDATION domain.** One file per module (`01-`…`13-`), plus cross-cutting rules, risks, closed stories and history | 🟡 DRAFT v0.2 |
| [`PRD-004-production/`](PRD-004-production/README.md) | **Active PRD for the PRODUCTION domain.** One file per module: Planning (`PLN`), Monitoring (`MON`), Performance Analysis (`PRF`) | 🟡 DRAFT v0.1 |
| [`PRD-003-task-breakdown.md`](PRD-003-task-breakdown.md) | Each PRD-003 story split into implementation tasks (schema/api/ui/validation/abac/event/test/docs), ready to paste into ClickUp | 🟡 DRAFT |
| [`PRD-001-molcadx-core-q3-en.md`](PRD-001-molcadx-core-q3-en.md) | **Frozen 2026-09-03 — not updated any more.** For FOUNDATION use PRD-003; for PRODUCTION use PRD-004. Its `US-PROD-*` IDs are retired | 🔴 FROZEN v0.2 |

## 2. Implementing a story — read only this

One PRD per domain (PRD-003 FOUNDATION, PRD-004 PRODUCTION). Inside it, one file per module. When you are
assigned a story (e.g. `US-PROD-PLN-003`), the story ID tells you both: `FND` → PRD-003, `PROD` → PRD-004; the
next part is the module code.

1. **Your domain PRD's `README.md`** — the one-page summary, module table, build order and blockers. The module
   table has an **Implementation repo** column: different modules can be built in different repos. Build only in
   your module's repo, and treat stories of other modules as dependencies another repo owns.
2. **The one module file** that holds your story. Find the story by its ID heading.
3. Cross-cutting sections your story links to (PRD-003: [`20-cross-cutting.md`](PRD-003-foundation/20-cross-cutting.md)).
4. Your story's task rows, if a task breakdown exists for that PRD (today only
   [`PRD-003-task-breakdown.md`](PRD-003-task-breakdown.md); search for the story ID).

**Follow a link into `docs-molcadx` only when your story's *Calculation* or *Data & entities* section points
there.** Where the PRD and a spec disagree, the spec wins (for example, F-07 for KPI formulas). Tell the Product
Owner about the conflict — do not guess which one is right.

**Skip:** closed-story and history files (`90-*`, `99-*`), risk/question files unless your story links to a
question, frozen PRD-001, other modules' files except the stories yours depends on, and the rest of `docs-molcadx`.

Stuck on something the PRD does not answer? Write it down as `TBD — needs PO confirmation` with the exact
question. Never invent a field, rule or number.

## 3. Writing or changing a PRD

Only the Product Owner (or an agent working for them in `docs-molcadx`) writes here.

- New PRDs are made **only when the PO asks for one** (`docs-molcadx` `AGENTS.md` §5 rule 1).
- When a spec decision in `docs-molcadx` changes a story, sync that story here **in the same session** (rule 1b), with
  the `prd-sync` skill in `docs-molcadx`. It diffs the specs from each PRD's **Specs synced to** row (README header) —
  keep that row current; never edit it by hand except to record a sync.
- Format and checklist: `docs-molcadx` [`AGENTS.md` §4](https://github.com/molca-id/docs-molcadx/blob/main/AGENTS.md),
  [`DOCS/03-prd-standard.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/03-prd-standard.md),
  [`DOCS/templates/prd-template.md`](https://github.com/molca-id/docs-molcadx/blob/main/DOCS/templates/prd-template.md).
- Links to specs use `https://github.com/molca-id/docs-molcadx/blob/main/...`. Links between PRD files stay relative.
- Log every change in [`CHANGELOG.md`](CHANGELOG.md), and log the decision that caused it in
  `docs-molcadx/LOGS/CHANGELOG.md`.
