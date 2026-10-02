# Editing model (approved 2026-10-02)

> Related: [[silver_model]] · [[gold_model]] · PRD-004 MON-005/006 · PRD-003 F-14 audit · [[gold-write-contract]] §12, §13, §18
> DDL: `ddl_silver.sql` (silver edit tables + triggers), `../04_gold/ddl_gold.sql` (gold edit + approval), `ddl_views.sql` (applies the edits). Tests: `test_edits.sql`.

## Two domains

| Domain | When | Tables | Becomes visible |
|---|---|---|---|
| **Silver (shopfloor)** | Shift open → 48 h after shift end | `silver.downtime_edit`, `silver.downtime_entry`, `silver.quality_entry` | **Instantly** (the views apply them) |
| **Gold (report)** | After 48 h | `gold.downtime_edit`, `gold.quality_entry` | After the next Airflow run of that shift (`gold_apply_edits`) |
| **Approval** | Any time | `gold.report_approval` + `gold.report_approval_event`, via `gold.fn_set_report_status()` | Approved = **frozen**: edits are refused in both domains, and Airflow does not rewrite that work unit's rows until it is reopened |

The window rule is enforced by trigger (`silver.fn_assert_edit_window`). **Precedence: gold edit > silver edit > machine value**, latest live edit per field.

## What can be edited

| # | Edit | Table | Effect |
|---|---|---|---|
| S1 / G1 | Stop reasons (ordered, element 1 = primary) | `*.downtime_edit` field `reasons` | The category follows the primary reason's category (a small stop stays a small stop) |
| S2 / G1 | Stop category | field `category` | Moves time between schedule / availability / performance loss |
| S3 / G1 | Detail reason, actions, maintained by | fields `detail_reason`, `actions`, `maintained_by` | Report text only |
| S4 | Manual stop (time no machine row covers) | `silver.downtime_entry` | Added stop (`source = manual`) |
| S5 / G2 | Manual reject (any unit, with reason) | `*.quality_entry` kind `reject` | Added to reject; a manual row in `reject_count` |
| S6 / G2 | Rework | kind `rework` | Fills `rework` / `rework_time` |
| S7 / G2 | Reject reason correction | kind `reject_override` + `override_tag_id` | Moves quantity of one sensor reject node to another reason; total unchanged |

Sensor counts (total / good) are never editable. Machine rows (`silver.downtime_events`, `production_events`, `reject_events`) are never changed by people.

## Audit trail

- **The edit row is the trail.** It records who (`created_by` = IDP subject, `created_by_name`), when, **old → new** (`old_*` filled by trigger), and a **required** `reason`.
- **Append-only.** A trigger refuses UPDATE and DELETE. The only change allowed is a **void** (`voided_at`, `voided_by`, `void_reason`), which stops the edit applying.
- **Every create and void writes one `silver.audit_log` row** in the same transaction (PRD F-14). The approval events are fully immutable.
