-- Gold load for ONE shift (Airflow passes :shift). Frozen (approved) work-unit rows are never rewritten.
-- delete + insert per shift in one transaction = idempotent and refreshes every column.
BEGIN;
CREATE TEMP TABLE frozen ON COMMIT DROP AS
  SELECT work_unit_id FROM gold.report_approval WHERE shift_instance_id = :shift AND status = 'approved';

DELETE FROM gold.work_unit_shift_report WHERE shift_instance_id = :shift AND work_unit_id NOT IN (SELECT work_unit_id FROM frozen);
INSERT INTO gold.work_unit_shift_report SELECT * FROM silver.v_wu_shift_report
 WHERE shift_instance_id = :shift AND work_unit_id NOT IN (SELECT work_unit_id FROM frozen);

DELETE FROM gold.product_count WHERE shift_instance_id = :shift AND work_unit_id NOT IN (SELECT work_unit_id FROM frozen);
INSERT INTO gold.product_count SELECT * FROM silver.v_product_count
 WHERE shift_instance_id = :shift AND work_unit_id NOT IN (SELECT work_unit_id FROM frozen);

DELETE FROM gold.reject_count WHERE shift_instance_id = :shift AND work_unit_id NOT IN (SELECT work_unit_id FROM frozen);
INSERT INTO gold.reject_count SELECT * FROM silver.v_reject_count
 WHERE shift_instance_id = :shift AND work_unit_id NOT IN (SELECT work_unit_id FROM frozen);

DELETE FROM gold.downtime_report WHERE shift_instance_id = :shift AND work_unit_id NOT IN (SELECT work_unit_id FROM frozen);
INSERT INTO gold.downtime_report SELECT * FROM silver.v_downtime_report
 WHERE shift_instance_id = :shift AND work_unit_id NOT IN (SELECT work_unit_id FROM frozen);

DELETE FROM gold.loss_by_reason WHERE shift_instance_id = :shift AND work_unit_id NOT IN (SELECT work_unit_id FROM frozen);
INSERT INTO gold.loss_by_reason SELECT * FROM silver.v_loss_by_reason
 WHERE shift_instance_id = :shift AND work_unit_id NOT IN (SELECT work_unit_id FROM frozen);

DELETE FROM gold.product_perf_hourly WHERE shift_instance_id = :shift AND work_unit_id NOT IN (SELECT work_unit_id FROM frozen);
INSERT INTO gold.product_perf_hourly SELECT * FROM silver.v_product_perf_hourly
 WHERE shift_instance_id = :shift AND work_unit_id NOT IN (SELECT work_unit_id FROM frozen);

-- work center: always recomputed (it has no approval of its own)
DELETE FROM gold.work_center_shift_report WHERE shift_instance_id = :shift;
INSERT INTO gold.work_center_shift_report SELECT * FROM silver.v_wc_shift_report WHERE shift_instance_id = :shift;

INSERT INTO gold.etl_watermarks (table_name, last_run_at, last_business_date, rows_written, run_id, writer)
SELECT t, now(), (SELECT business_date FROM silver.shift_instance WHERE shift_instance_id = :shift), NULL, 'test', 'airflow'
FROM unnest(ARRAY['work_unit_shift_report','work_center_shift_report','product_count','reject_count',
                  'downtime_report','loss_by_reason','product_perf_hourly']) t
ON CONFLICT (table_name) DO UPDATE SET last_run_at = EXCLUDED.last_run_at,
   last_business_date = greatest(gold.etl_watermarks.last_business_date, EXCLUDED.last_business_date);
COMMIT;
