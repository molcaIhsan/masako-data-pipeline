IMPORTANT BIG CHANGES of how we calcualte reports:
Old: Trace thru work_unit flow and find first, last node so we can know  the total the finish good
New: go to KPI and then directly find thru silver what this KPI needs data.

e.g Total from work-unitL
Old: Trace thru the work_unit find the primary or hardcoded to find which is counted
New: Direct binding from KPI, lets say target is find find Total work_unit then just search the node or data needed by tracing maybe the tag, role or whatever from ERD that connected to silver and then calculate it!

Therefore the pipeline or query or sql we build must have dictionary of formula

IMPORTANT: The GOLD Remain the same, it just how we search it from binding!!!

---
**Related:** [[molcadx_app_overview]] (direct binding) · [[erd_spec_2026-10-01]] · [[silver_model]] · [[gold_tables]] · legacy [[oee_or_kpi_calculation]]
