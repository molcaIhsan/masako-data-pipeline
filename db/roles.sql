-- Least-privilege roles for the pipeline (run after the 3 DDL files). Passwords: ALTER ROLE ... PASSWORD from secrets.
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'flink_silver') THEN CREATE ROLE flink_silver LOGIN; END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'airflow_gold') THEN CREATE ROLE airflow_gold LOGIN; END IF;
END $$;
-- Flink: write the 3 fact tables only; read master data
GRANT USAGE ON SCHEMA silver TO flink_silver;
GRANT SELECT ON ALL TABLES IN SCHEMA silver TO flink_silver;
GRANT INSERT, UPDATE ON silver.production_events, silver.reject_events, silver.downtime_events TO flink_silver;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA silver TO flink_silver;
-- Airflow: read silver (views + functions), write gold report tables + watermarks; read gold edit/approval tables
GRANT USAGE ON SCHEMA silver, gold TO airflow_gold;
GRANT SELECT ON ALL TABLES IN SCHEMA silver TO airflow_gold;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA silver TO airflow_gold;
GRANT SELECT ON ALL TABLES IN SCHEMA gold TO airflow_gold;
GRANT INSERT, UPDATE, DELETE ON gold.work_unit_shift_report, gold.work_unit_day_report, gold.work_unit_month_report,
  gold.work_unit_year_report, gold.work_center_shift_report, gold.work_center_day_report, gold.work_center_month_report,
  gold.work_center_year_report, gold.product_count, gold.product_count_day, gold.reject_count, gold.downtime_report,
  gold.loss_by_reason, gold.product_perf_hourly, gold.etl_watermarks TO airflow_gold;
