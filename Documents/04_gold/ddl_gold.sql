-- =====================================================================
-- MolcaDx gold DDL — first delivery (2026-10-02). Plain Postgres tables (no Timescale) in the same DB as silver.
-- Written by Airflow: INSERT ... SELECT FROM silver.v_* ... ON CONFLICT (<key>) DO UPDATE (idempotent per run window).
-- Run order: ../03_silver/ddl_silver.sql -> this file -> ../03_silver/ddl_views.sql. Spec: gold_model.md, gold_tables.md.
-- No jsonb in gold (decision 2026-10-02): lists are typed arrays.
-- Rules: durations in ms; counts in the product's BASE uom; rates recomputed from summed terms (never averaged);
--        NULL = not known / not computable (never 0). Work center = Σ work units, except good_out = Σ good of the
--        FINAL work units of equipment_flow (silver.fn_work_center_final_work_units).
-- =====================================================================
CREATE SCHEMA IF NOT EXISTS gold;

-- Template: the report measure columns of gold_tables.md (+ mttr/mtbf + lineage). Never written to directly.
CREATE TABLE gold.report_metrics_template (
    uom_id                 bigint,          -- base uom of the counts; NULL when the members' units disagree
    uom_name               varchar(255),
    uom_code               varchar(255),
    teep                   numeric(10,3),
    oee                    numeric(10,3),
    availability           numeric(10,3),
    performance            numeric(10,3),
    quality                numeric(10,3),
    total_out              numeric(15,3),
    good_out               numeric(15,3),   -- work center: Σ good of the FINAL work units only
    effective_out          numeric(15,3),
    runtime                bigint,          -- all times in ms
    availability_time      bigint,
    production_time        bigint,
    pdt_time               bigint,
    operating_time         bigint,
    updt_time              bigint,
    net_time               bigint,
    reduce_speed_time      bigint,
    ms_time                bigint,
    value_added_time       bigint,
    rework_time            bigint,
    reject_time            bigint,
    anomaly_time           bigint,
    up_speed_time          bigint,
    schedule_loss_time     bigint,
    availability_loss_time bigint,
    performance_loss_time  bigint,
    ms_loss_time           bigint,
    reduce_speed_loss_time bigint,
    quality_loss_time      bigint,
    reject                 numeric(15,3),
    rework                 numeric(15,3),
    mttr                   bigint,          -- ms; NULL when no closed unplanned stop
    mtbf                   bigint,          -- ms; NULL when fewer than 2 failures
    data_status            varchar(20) NOT NULL DEFAULT 'ok'
                           CHECK (data_status IN ('ok','no_data','no_binding','no_cycle_time','partial')),   -- no_data: no readings and no stops (PRD: never 100%)
    binding_ids            bigint[],        -- work_unit_kpi_binding rows used (work center: all members')
    computed_at            timestamptz NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------
-- WORK UNIT reports: shift -> day -> month -> year
-- ---------------------------------------------------------------------
CREATE TABLE gold.work_unit_shift_report (
    shift_instance_id bigint      NOT NULL,
    business_date     date        NOT NULL,
    shift_no          smallint    NOT NULL,     -- 1..n by site_shift.start_time from the business day's first shift
    shift_label       varchar(100),             -- enterprise_shift.shift_label
    site_id           bigint,
    time_zone         varchar(64),
    area_id           bigint,
    work_center_id    bigint,
    work_unit_id      bigint      NOT NULL,
    work_unit_name    varchar(255),
    work_unit_code    varchar(255),
    LIKE gold.report_metrics_template INCLUDING DEFAULTS INCLUDING CONSTRAINTS,
    UNIQUE NULLS NOT DISTINCT (shift_instance_id, work_unit_id, uom_id)
);
CREATE INDEX ON gold.work_unit_shift_report (business_date, work_unit_id);

CREATE TABLE gold.work_unit_day_report (
    business_date  date NOT NULL,               -- 07:00 -> 07:00 business day = all shift instances of that date
    site_id        bigint,
    time_zone      varchar(64),
    area_id        bigint,
    work_center_id bigint,
    work_unit_id   bigint NOT NULL,
    work_unit_name varchar(255),
    work_unit_code varchar(255),
    LIKE gold.report_metrics_template INCLUDING DEFAULTS INCLUDING CONSTRAINTS,
    UNIQUE NULLS NOT DISTINCT (business_date, work_unit_id, uom_id)
);

CREATE TABLE gold.work_unit_month_report (
    business_date  date NOT NULL CHECK (business_date = date_trunc('month', business_date)::date),  -- 1st of month
    site_id        bigint,
    time_zone      varchar(64),
    area_id        bigint,
    work_center_id bigint,
    work_unit_id   bigint NOT NULL,
    work_unit_name varchar(255),
    work_unit_code varchar(255),
    LIKE gold.report_metrics_template INCLUDING DEFAULTS INCLUDING CONSTRAINTS,
    UNIQUE NULLS NOT DISTINCT (business_date, work_unit_id, uom_id)
);

CREATE TABLE gold.work_unit_year_report (
    business_date  date NOT NULL CHECK (business_date = date_trunc('year', business_date)::date),   -- Jan 1
    site_id        bigint,
    time_zone      varchar(64),
    area_id        bigint,
    work_center_id bigint,
    work_unit_id   bigint NOT NULL,
    work_unit_name varchar(255),
    work_unit_code varchar(255),
    LIKE gold.report_metrics_template INCLUDING DEFAULTS INCLUDING CONSTRAINTS,
    UNIQUE NULLS NOT DISTINCT (business_date, work_unit_id, uom_id)
);

-- ---------------------------------------------------------------------
-- WORK CENTER reports: Σ of its work units; good_out from the FINAL work units of equipment_flow
-- ---------------------------------------------------------------------
CREATE TABLE gold.work_center_shift_report (
    shift_instance_id   bigint NOT NULL,
    business_date       date   NOT NULL,
    shift_no            smallint NOT NULL,
    shift_label         varchar(100),
    site_id             bigint,
    time_zone           varchar(64),
    area_id             bigint,
    work_center_id      bigint NOT NULL,
    work_center_name    varchar(255),
    work_center_code    varchar(255),
    work_unit_count     integer,                -- work units that reported into this row
    final_work_unit_ids bigint[],               -- final work units whose good_out was used
    LIKE gold.report_metrics_template INCLUDING DEFAULTS INCLUDING CONSTRAINTS,
    UNIQUE NULLS NOT DISTINCT (shift_instance_id, work_center_id, uom_id)
);
CREATE INDEX ON gold.work_center_shift_report (business_date, work_center_id);

CREATE TABLE gold.work_center_day_report (
    business_date    date   NOT NULL,
    site_id          bigint,
    time_zone        varchar(64),
    area_id          bigint,
    work_center_id   bigint NOT NULL,
    work_center_name varchar(255),
    work_center_code varchar(255),
    work_unit_count  integer,                   -- max over the day's shifts
    LIKE gold.report_metrics_template INCLUDING DEFAULTS INCLUDING CONSTRAINTS,
    UNIQUE NULLS NOT DISTINCT (business_date, work_center_id, uom_id)
);

CREATE TABLE gold.work_center_month_report (
    business_date    date   NOT NULL CHECK (business_date = date_trunc('month', business_date)::date),
    site_id          bigint,
    time_zone        varchar(64),
    area_id          bigint,
    work_center_id   bigint NOT NULL,
    work_center_name varchar(255),
    work_center_code varchar(255),
    work_unit_count  integer,
    LIKE gold.report_metrics_template INCLUDING DEFAULTS INCLUDING CONSTRAINTS,
    UNIQUE NULLS NOT DISTINCT (business_date, work_center_id, uom_id)
);

CREATE TABLE gold.work_center_year_report (
    business_date    date   NOT NULL CHECK (business_date = date_trunc('year', business_date)::date),
    site_id          bigint,
    time_zone        varchar(64),
    area_id          bigint,
    work_center_id   bigint NOT NULL,
    work_center_name varchar(255),
    work_center_code varchar(255),
    work_unit_count  integer,
    LIKE gold.report_metrics_template INCLUDING DEFAULTS INCLUDING CONSTRAINTS,
    UNIQUE NULLS NOT DISTINCT (business_date, work_center_id, uom_id)
);

-- ---------------------------------------------------------------------
-- Hourly performance (gold_tables.md "Work Unit Perf Analytics"): one ROW per work unit × local hour × product.
-- The chart is drawn from these rows (no jsonb). Runtime per product from per-minute counts (silver_model.md §10).
-- Additive: total_out, runtime, effective_out (= ideal output for the runtime). Rates are recomputed from sums.
-- ---------------------------------------------------------------------
CREATE TABLE gold.product_perf_hourly (
    work_unit_id         bigint      NOT NULL,
    bucket_start         timestamptz NOT NULL,     -- start of the plant-LOCAL hour, as an instant
    product_id           bigint      NOT NULL,
    shift_instance_id    bigint      NOT NULL,
    business_date        date        NOT NULL,
    shift_no             smallint    NOT NULL,
    shift_label          varchar(100),
    hour_of_day          smallint    NOT NULL CHECK (hour_of_day BETWEEN 0 AND 23),   -- local clock hour
    time_zone            varchar(64),
    work_center_id       bigint,
    work_unit_name       varchar(255),
    work_unit_code       varchar(255),
    product_name         varchar(255),
    product_code         varchar(255),
    uom_id               bigint,                   -- product base uom
    uom_code             varchar(50),
    total_out            numeric(15,3),
    runtime              bigint      NOT NULL CHECK (runtime BETWEEN 0 AND 3600000),   -- ms
    effective_out        numeric(15,3),            -- ideal output for that runtime; NULL = no standard
    ideal_rate_per_hour  numeric(15,3),            -- master rate in uom per hour; NULL = no standard
    actual_rate_per_hour numeric(15,3),            -- total_out per hour of runtime
    computed_at          timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (work_unit_id, bucket_start, product_id)
);
CREATE INDEX ON gold.product_perf_hourly (shift_instance_id, work_unit_id);
CREATE INDEX ON gold.product_perf_hourly (business_date, work_unit_id);

-- ---------------------------------------------------------------------
-- Product counts per work unit (shift, day). total_out / reject / rework in base uom; good_out in base uom too.
-- ---------------------------------------------------------------------
CREATE TABLE gold.product_count (
    shift_instance_id bigint NOT NULL,
    business_date     date   NOT NULL,
    shift_no          smallint NOT NULL,
    shift_label       varchar(100),
    work_center_id    bigint,
    work_unit_id      bigint NOT NULL,
    work_unit_name    varchar(255),
    work_unit_code    varchar(255),
    product_id        bigint NOT NULL,
    product_name      varchar(255),
    product_code      varchar(255),
    base_uom_id       bigint,
    base_uom_name     varchar(255),
    base_uom_code     varchar(255),
    total_out         numeric(15,3),
    good_out          numeric(15,3),
    reject            numeric(15,3),
    rework            numeric(15,3),
    computed_at       timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (shift_instance_id, work_unit_id, product_id)
);
CREATE INDEX ON gold.product_count (business_date, product_id);

CREATE TABLE gold.product_count_day (
    business_date  date   NOT NULL,
    work_center_id bigint,
    work_unit_id   bigint NOT NULL,
    work_unit_name varchar(255),
    work_unit_code varchar(255),
    product_id     bigint NOT NULL,
    product_name   varchar(255),
    product_code   varchar(255),
    base_uom_id    bigint,
    base_uom_name  varchar(255),
    base_uom_code  varchar(255),
    total_out      numeric(15,3),
    good_out       numeric(15,3),
    reject         numeric(15,3),
    rework         numeric(15,3),
    computed_at    timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (business_date, work_unit_id, product_id)
);

-- ---------------------------------------------------------------------
-- Reject count per reason (option B): each reject node = one reason (asset_tags.reject_reason_id) and gives a WEIGHT.
-- weight = Σ cleaned_value of the node converted to grams; quantity = weight ÷ weight of one unit (product_detail
-- standard_weight, PRD KPI-003), NULL when no standard weight; freq = readings with a positive delta.
-- ---------------------------------------------------------------------
CREATE TABLE gold.reject_count (
    shift_instance_id  bigint NOT NULL,
    business_date      date   NOT NULL,
    shift_no           smallint NOT NULL,
    shift_label        varchar(100),
    work_center_id     bigint,
    work_unit_id       bigint NOT NULL,
    work_unit_name     varchar(255) NOT NULL,
    asset_id           bigint,
    asset_name         varchar(80),
    product_id         bigint,                 -- NULL = product not mapped
    product_name       varchar(255),
    reject_reason_id   bigint,                 -- NULL = node has no reason
    reject_reason_name varchar(80),
    origin             varchar(20) NOT NULL CHECK (origin IN ('sensor','override','manual')),
    tag_id             bigint,                 -- the reject node; NULL for a manual entry
    source_tag         varchar(255),
    weight_gram        numeric(15,3),          -- NULL if the node does not weigh
    quantity           numeric(15,3),          -- in the product's base uom; NULL if no standard weight
    uom_id             bigint,                 -- uom of quantity
    freq               integer,
    reject_time        bigint,                 -- ms = quantity × cycle time; NULL if not computable
    computed_at        timestamptz NOT NULL DEFAULT now(),
    UNIQUE NULLS NOT DISTINCT (shift_instance_id, work_unit_id, product_id, origin, tag_id, reject_reason_id)
);
CREATE INDEX ON gold.reject_count (business_date, work_unit_id);

-- ---------------------------------------------------------------------
-- Downtime list: one row per stop piece, AFTER edits (silver.v_downtime_effective). Primary reason as scalars (the
-- Pareto groups on them) + every reason in order as text[].
-- ---------------------------------------------------------------------
CREATE TABLE gold.downtime_report (
    source_event_id    text PRIMARY KEY,         -- = silver.downtime_events.source_event_id
    shift_instance_id  bigint NOT NULL,
    business_date      date   NOT NULL,
    shift_no           smallint NOT NULL,
    shift_label        varchar(100),
    area_id            bigint,
    work_center_id     bigint,
    work_unit_id       bigint NOT NULL,
    work_unit_name     varchar(255),
    asset_id           bigint NOT NULL,
    asset_code         varchar(30),
    asset_name         varchar(80),
    category           varchar(20) NOT NULL CHECK (category IN ('planned','unplanned','small_stop')),
    primary_reason_id   bigint,                       -- NULL = unlabelled (counts as unplanned)
    primary_reason_name varchar(80),
    reason_names        text[] NOT NULL DEFAULT '{}', -- {"Material jam","Film out"}, element 1 = primary
    detail_reason       text,
    actions             text[],
    maintained_by       varchar(255),
    product_id         bigint,                        -- SKU running when the stop started
    product_name       varchar(255),
    source             varchar(10),
    start_at           timestamptz NOT NULL,
    end_at             timestamptz,
    duration           bigint,                        -- ms
    stop_group_id      bigint,
    is_edited          boolean NOT NULL DEFAULT false,
    last_edited_by     text,
    computed_at        timestamptz NOT NULL DEFAULT now(),
    CHECK (end_at IS NULL OR end_at >= start_at)
);
CREATE INDEX ON gold.downtime_report (business_date, work_unit_id);

-- Loss tables of gold_tables.md (schedule / availability / performance) — one table, by loss_type.
-- Reject losses are gold.reject_count.
CREATE TABLE gold.loss_by_reason (
    shift_instance_id bigint      NOT NULL,
    business_date     date        NOT NULL,
    shift_no          smallint    NOT NULL,
    shift_label       varchar(100),
    work_unit_id      bigint      NOT NULL,
    loss_type         varchar(20) NOT NULL CHECK (loss_type IN ('schedule','availability','performance')),
    reason_id         bigint,                  -- primary downtime_reason_id; NULL = no reason given
    reason_name       varchar(80),
    asset_id          bigint      NOT NULL,
    asset_name        varchar(80),
    freq              integer,
    total_duration    bigint,                  -- ms
    computed_at       timestamptz NOT NULL DEFAULT now(),
    UNIQUE NULLS NOT DISTINCT (shift_instance_id, work_unit_id, loss_type, reason_id, asset_id)
);
CREATE INDEX ON gold.loss_by_reason (business_date, work_unit_id, loss_type);

-- Freshness: one row per gold table, stamped by every Airflow load (lets screens tell "not loaded" from "zero").
CREATE TABLE gold.etl_watermarks (
    table_name         text PRIMARY KEY,
    last_run_at        timestamptz NOT NULL,
    last_business_date date,
    rows_written       bigint,
    run_id             text,
    writer             text NOT NULL DEFAULT 'airflow'
);

-- =====================================================================
-- GOLD EDITING (report domain, after the 48 h shopfloor window). Same shape and rules as the silver tables;
-- gold edit > silver edit. Applied by Airflow (gold_apply_edits) on the next run.
-- =====================================================================
-- G1
CREATE TABLE gold.downtime_edit (LIKE silver.downtime_edit INCLUDING ALL);
CREATE TRIGGER downtime_edit_before BEFORE INSERT ON gold.downtime_edit
  FOR EACH ROW EXECUTE FUNCTION silver.trg_downtime_edit_before('gold');
CREATE TRIGGER downtime_edit_append_only BEFORE UPDATE OR DELETE ON gold.downtime_edit
  FOR EACH ROW EXECUTE FUNCTION silver.trg_append_only();
CREATE TRIGGER downtime_edit_audit AFTER INSERT OR UPDATE ON gold.downtime_edit
  FOR EACH ROW EXECUTE FUNCTION silver.trg_edit_audit('RPT', 'edit_id');

-- G2
CREATE TABLE gold.quality_entry (LIKE silver.quality_entry INCLUDING ALL);
CREATE TRIGGER quality_entry_before BEFORE INSERT ON gold.quality_entry
  FOR EACH ROW EXECUTE FUNCTION silver.trg_quality_entry_before('gold');
CREATE TRIGGER quality_entry_append_only BEFORE UPDATE OR DELETE ON gold.quality_entry
  FOR EACH ROW EXECUTE FUNCTION silver.trg_append_only();
CREATE TRIGGER quality_entry_audit AFTER INSERT OR UPDATE ON gold.quality_entry
  FOR EACH ROW EXECUTE FUNCTION silver.trg_edit_audit('RPT', 'entry_id');

-- G3: approval of a work unit's shift report. approved = frozen (no edits anywhere; Airflow does not rewrite it).
CREATE TABLE gold.report_approval (
    work_unit_id      bigint      NOT NULL,
    shift_instance_id bigint      NOT NULL,
    status            varchar(10) NOT NULL CHECK (status IN ('open','approved')),
    changed_by        text        NOT NULL,
    changed_by_name   varchar(255),
    changed_at        timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (work_unit_id, shift_instance_id)
);
CREATE TABLE gold.report_approval_event (                 -- the trail, append-only
    event_id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    work_unit_id      bigint      NOT NULL,
    shift_instance_id bigint      NOT NULL,
    from_status       varchar(10) NOT NULL,
    to_status         varchar(10) NOT NULL CHECK (to_status IN ('open','approved')),
    reason            text,
    created_by        text        NOT NULL,
    created_by_name   varchar(255),
    created_at        timestamptz NOT NULL DEFAULT now()
);
CREATE TRIGGER report_approval_event_immutable BEFORE UPDATE OR DELETE ON gold.report_approval_event
  FOR EACH ROW EXECUTE FUNCTION silver.trg_immutable();
CREATE TRIGGER report_approval_event_audit AFTER INSERT ON gold.report_approval_event
  FOR EACH ROW EXECUTE FUNCTION silver.trg_edit_audit('RPT', 'event_id');

-- The one way to approve / reopen: updates the current state and appends the event, in one transaction.
CREATE FUNCTION gold.fn_set_report_status(p_work_unit_id bigint, p_shift_instance_id bigint, p_to_status text,
                                          p_by text, p_by_name text, p_reason text)
RETURNS void LANGUAGE plpgsql AS $$
DECLARE v_from text;
BEGIN
  SELECT status INTO v_from FROM gold.report_approval
   WHERE work_unit_id = p_work_unit_id AND shift_instance_id = p_shift_instance_id FOR UPDATE;
  v_from := coalesce(v_from, 'open');
  IF v_from = p_to_status THEN
    RAISE EXCEPTION 'report (work unit %, shift %) is already %', p_work_unit_id, p_shift_instance_id, p_to_status;
  END IF;
  INSERT INTO gold.report_approval (work_unit_id, shift_instance_id, status, changed_by, changed_by_name, changed_at)
  VALUES (p_work_unit_id, p_shift_instance_id, p_to_status, p_by, p_by_name, now())
  ON CONFLICT (work_unit_id, shift_instance_id) DO UPDATE
     SET status = EXCLUDED.status, changed_by = EXCLUDED.changed_by,
         changed_by_name = EXCLUDED.changed_by_name, changed_at = EXCLUDED.changed_at;
  INSERT INTO gold.report_approval_event (work_unit_id, shift_instance_id, from_status, to_status, reason,
                                         created_by, created_by_name)
  VALUES (p_work_unit_id, p_shift_instance_id, v_from, p_to_status, p_reason, p_by, p_by_name);
END $$;

