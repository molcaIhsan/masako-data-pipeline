-- =====================================================================
-- Open the Postgres WAL for Flink CDC on the master-data tables (logical replication, pgoutput).
-- Run as a superuser on the silver database AFTER ddl_silver.sql. Idempotent.
-- Prerequisite (server level, needs a restart): wal_level = logical — see enable_cdc.sh.
-- Flink creates and owns the replication SLOT itself (slot.name in .env: FLINK_CDC_SLOT).
-- =====================================================================
DO $$ BEGIN
  IF current_setting('wal_level') <> 'logical' THEN
    RAISE EXCEPTION 'wal_level is %, must be logical: run enable_cdc.sh (ALTER SYSTEM + restart) first', current_setting('wal_level');
  END IF;
END $$;

-- 1. Replication user for Flink CDC (password set by enable_cdc.sh from FLINK_CDC_PASSWORD)
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'flink_cdc') THEN
    CREATE ROLE flink_cdc WITH LOGIN REPLICATION;
  END IF;
END $$;
GRANT USAGE ON SCHEMA silver TO flink_cdc;

-- 2. The master tables Flink needs live (resolve tags, placement, shifts, bindings, product codes, reasons)
DO $$
DECLARE t text;
  tables text[] := ARRAY['asset_tags','asset_placement','asset','work_unit','work_center','area','site','site_shift',
                         'shift_instance','work_unit_kpi_binding','asset_product_codes','downtime_reason',
                         'reject_reason','product'];
BEGIN
  FOREACH t IN ARRAY tables LOOP
    EXECUTE format('GRANT SELECT ON silver.%I TO flink_cdc', t);
    -- FULL: deletes and updates carry the whole old row (Flink keeps the master snapshot in state)
    EXECUTE format('ALTER TABLE silver.%I REPLICA IDENTITY FULL', t);
  END LOOP;
  IF NOT EXISTS (SELECT 1 FROM pg_publication WHERE pubname = 'flink_master_pub') THEN
    EXECUTE 'CREATE PUBLICATION flink_master_pub FOR TABLE '
         || (SELECT string_agg(format('silver.%I', x), ', ') FROM unnest(tables) x);
  ELSE
    EXECUTE 'ALTER PUBLICATION flink_master_pub SET TABLE '
         || (SELECT string_agg(format('silver.%I', x), ', ') FROM unnest(tables) x);
  END IF;
END $$;

-- 3. Check
SELECT pubname, schemaname || '.' || tablename AS published_table
FROM pg_publication_tables WHERE pubname = 'flink_master_pub' ORDER BY 2;
