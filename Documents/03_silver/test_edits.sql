SET search_path = silver, public;
SET silver.now_override = '2026-09-30 09:00+00';
INSERT INTO downtime_edit (source_event_id, field, new_reason_ids, reason, created_by, created_by_name) VALUES ('wu7-t101-a','reasons','{3}','It was the second break, not a jam','sup-001','Supervisor A');
UPDATE downtime_edit SET voided_at = now(), voided_by = 'sup-001', void_reason = 'Wrong stop' WHERE edit_id = 1;
INSERT INTO quality_entry (kind, work_unit_id, shift_instance_id, product_id, reject_reason_id, entered_qty, entered_uom_id, override_tag_id, reason, created_by) VALUES
 ('reject',7,500,11,1,1.0,2,NULL,'Hand-sorted underweight','op-007'),('rework',7,500,12,NULL,20,1,NULL,'Re-sealed packs','op-007'),
 ('reject_override',7,500,11,2,1.0,2,103,'Checkweigher misclassified','qc-001');
INSERT INTO downtime_edit (source_event_id, field, new_category, reason, created_by) VALUES ('wu7-t101-b','category','unplanned','shopfloor says unplanned','sup-001');
SET silver.now_override = '2026-10-03 09:00+00';
INSERT INTO gold.downtime_edit (source_event_id, field, new_category, reason, created_by) VALUES ('wu7-t101-b','category','planned','report review: planned changeover','mgr-001');
\echo '== loaded state (silver + gold edits) =='
SELECT work_unit_id wu, availability "A%", performance "P%", quality "Q%", oee "OEE%", reject, rework FROM v_wu_shift_report WHERE shift_instance_id=500 ORDER BY 1;
\i /load_gold_v3.sql
\echo '== T7 approve -> edits refused, gold frozen on reload, reopen -> refreshed =='
SELECT gold.fn_set_report_status(7, 500, 'approved', 'mgr-001', 'Manager', 'Shift reviewed');
\set ON_ERROR_STOP 0
INSERT INTO gold.quality_entry (kind, work_unit_id, shift_instance_id, product_id, reject_reason_id, entered_qty, entered_uom_id, reason, created_by)
VALUES ('reject', 7, 500, 11, 1, 1.0, 2, 'after approval', 'mgr-001');
SELECT gold.fn_set_report_status(7, 500, 'approved', 'mgr-001', 'Manager', 'again');
\set ON_ERROR_STOP 1
INSERT INTO production_events(event_time,tag_id,asset_id,work_unit_id,shift_instance_id,business_date,product_id,role,uom_id,raw_value,cleaned_value,source_tag)
VALUES ('2026-09-30 07:45+00',101,1,7,500,'2026-09-30',12,'total',1,25000,1000,'t');
CALL public.refresh_continuous_aggregate('silver.production_events_1m', NULL, NULL);
SELECT 'view (live)' src, total_out FROM v_wu_shift_report WHERE shift_instance_id=500 AND work_unit_id=7;
\i /load_gold_v3.sql
SELECT 'gold (frozen)' src, total_out FROM gold.work_unit_shift_report WHERE shift_instance_id=500 AND work_unit_id=7;
SELECT gold.fn_set_report_status(7, 500, 'open', 'mgr-001', 'Manager', 'Late data, re-open');
\i /load_gold_v3.sql
SELECT 'gold (reopened)' src, total_out FROM gold.work_unit_shift_report WHERE shift_instance_id=500 AND work_unit_id=7;
SELECT from_status, to_status, reason, created_by FROM gold.report_approval_event ORDER BY event_id;
\set ON_ERROR_STOP 0
UPDATE gold.report_approval_event SET reason = 'x';
DELETE FROM gold.quality_entry;
\set ON_ERROR_STOP 1
\echo '== audit trail =='
SELECT module_code, action, entity_type, count(*) FROM audit_log GROUP BY 1,2,3 ORDER BY 3,2;
