# Documents — map

Open this folder as an Obsidian vault. `[[links]]` build the graph view.

```
00_overview ──► 01_architecture ──► 03_silver ──► 04_gold
     │                                  ▲            ▲
     └──► erd (master data) ────────────┴────────────┘
     └──► PRD (product requirements) ──► 02_kpi (formulas)
```

| Folder | Doc | What it is |
|---|---|---|
| 00_overview | [[objective]] | Goal of this repo: Flink + Airflow pipeline, tech stack |
| | [[molcadx_app_overview]] | **How MolcaDx works**, learned from the PRD: domains, tag → work unit → KPI, direct binding, formulas, time rules |
| 01_architecture | ![[data pipeline.png]] [[data pipeline]] | Big picture: IoT → Telegraf → Kafka/bronze → Flink → silver → Airflow → gold |
| | [[bronze]] | Raw layer, used for disaster replay |
| | [[flink_rules]] · [[airflow_rules]] | Engineering rules for the jobs |
| | [[timescale_cont_aggregates]] | Why and how silver uses continuous aggregates |
| 02_kpi | [[NEW_oee_or_kpi_calculation]] | **New rule:** find KPI inputs by binding, not by flow tracing |
| | [[oee_or_kpi_calculation]] | Legacy OEE formulas and views, built on the *old* master data (equipment = asset now) |
| | [[uom_conversion]] | **How counts are converted** between tag, standard and base units |
| | [[downtime]] | (empty) downtime notes |
| 03_silver | [[silver_model]] | **Silver v1 model + ERD** (decisions from Q&A) |
| | `ddl_silver.sql` → `../04_gold/ddl_gold.sql` → `ddl_views.sql` | **Tested DDL, run in this order**: silver (55 master tables + facts + edit tables), gold (report + edit + approval tables, no jsonb), views (apply edits, feed gold) |
| | [[editing_model]] | **Editing:** silver (shopfloor, instant) / gold (report, after rerun), approval freeze, audit trail |
| | [[silver tables]] | First silver notes (old model) |
| | [[downtime_detector]] | Downtime edit / override notes from `mes-oee-monitor` (companion to the contract §13) |
| 04_gold | [[gold-write-contract]] | Reference only: Gold write contract of another app's `oee` schema. We take its mechanics (keys, rollup and NULL rules, tests), **not** its tables |
| | [[gold_contract_digest]] | **Digest:** tables, keys, rules, conflicts with our design, open decisions |
| | [[gold_model]] | **Gold v1 model + ERD**: where each column now comes from (binding) |
| | [[gold_tables]] | Gold report columns and formulas (unchanged) |
| | `ddl_gold.sql` | **Our Gold DDL, schema `gold`** (plain tables, same DB as silver, no jsonb). Run between `ddl_silver.sql` and `ddl_views.sql` |
| 05_contracts | [[app_contract]] | **App ↔ DB contract:** where screens read, NULL/units/status rules, master-data CRUD rules, editing calls, error catalogue |
| | [[pipeline_contract]] | **Flink + Airflow obligations:** Flink F1–F16, DAGs A–F (shift close, rerun window, apply edits, rollups, backfill, checks) |
| 06_build | [[build_plan]] | **Flink + Airflow build plan:** versions, repo layout, phases 0–6 with to-do and done-criteria, open questions |
| erd | [[erd_spec_2026-10-01]] (+ `.json`) | Master data ERD exported from FigJam "Brainstorm DX" |
| PRD | [[PRD/AGENTS\|PRD index]] | Product requirements. PRD-003 FOUNDATION, PRD-004 PRODUCTION (PRD-001 frozen) |
