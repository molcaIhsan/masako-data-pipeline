-- =====================================================================
-- MolcaDx SILVER VIEWS (2026-10-02). Run order: ddl_silver.sql -> ../04_gold/ddl_gold.sql -> this file.
-- Realtime screens read these views; Airflow writes the same rows into gold (INSERT ... SELECT * ... ON CONFLICT).
-- * Every master-data lookup is AS-OF (business_date / event time).
-- * Edits are applied here: GOLD edit > SILVER edit > machine value (latest non-voided edit per field wins);
--   manual entries (stops, rejects, rework) are added. So a silver edit is visible instantly, a gold edit after the
--   next Airflow rerun of the gold table.
-- * No view returns jsonb.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Shift helpers
-- ---------------------------------------------------------------------
CREATE VIEW silver.v_shift AS
SELECT si.shift_instance_id, si.business_date, si.start_datetime, si.end_datetime, si.status,
       ss.site_id, st.timezone AS time_zone, es.shift_label,
       row_number() OVER (PARTITION BY ss.site_id, si.business_date ORDER BY si.start_datetime)::smallint AS shift_no,
       (extract(epoch FROM si.end_datetime - si.start_datetime) * 1000)::bigint AS availability_time
FROM silver.shift_instance si
JOIN silver.site_shift ss       ON ss.site_shift_id = si.site_shift_id
JOIN silver.site st             ON st.site_id = ss.site_id
JOIN silver.enterprise_shift es ON es.enterprise_shift_id = ss.enterprise_shift_id;

CREATE VIEW silver.v_shift_work_unit AS
SELECT s.*, wu.work_unit_id, wu.work_unit_code, wu.work_unit_name, wu.work_center_id, wc.area_id
FROM silver.v_shift s
JOIN silver.area a         ON a.site_id = s.site_id
JOIN silver.work_center wc ON wc.area_id = a.area_id
JOIN silver.work_unit wu   ON wu.work_center_id = wc.work_center_id
WHERE wu.valid_from <= s.business_date AND (wu.valid_to IS NULL OR wu.valid_to >= s.business_date);

-- ---------------------------------------------------------------------
-- Stops after edits
-- ---------------------------------------------------------------------
-- Latest live edit per stop and field; gold beats silver.
CREATE VIEW silver.v_downtime_edit_latest AS
SELECT DISTINCT ON (x.source_event_id, x.field)
       x.source_event_id, x.field, x.domain, x.new_category, x.new_reason_ids, x.new_detail_reason,
       x.new_actions, x.new_maintained_by, x.created_by, x.created_at
FROM (SELECT 'gold'::text AS domain, 2 AS prio, e.* FROM gold.downtime_edit e WHERE e.voided_at IS NULL
      UNION ALL
      SELECT 'silver', 1, e.* FROM silver.downtime_edit e WHERE e.voided_at IS NULL) x
ORDER BY x.source_event_id, x.field, x.prio DESC, x.created_at DESC, x.edit_id DESC;

-- Machine stops + manual stops, with every edit applied.
-- Reasons edited but category not: the category follows the primary reason's category, except a small stop stays one.
CREATE VIEW silver.v_downtime_effective AS
WITH base AS (
  SELECT source_event_id, asset_id, tag_id, work_unit_id, shift_instance_id, business_date, category,
         downtime_reason_ids AS reason_ids, NULL::text AS detail_reason, NULL::text[] AS actions,
         NULL::varchar(255) AS maintained_by, product_id, source::text AS source, start_at, end_at, stop_group_id
  FROM silver.downtime_events
  UNION ALL
  SELECT source_event_id, asset_id, NULL, work_unit_id, shift_instance_id, business_date, category,
         reason_ids, detail_reason, actions, maintained_by, product_id, 'manual', start_at, end_at, -entry_id
  FROM silver.downtime_entry WHERE voided_at IS NULL)
SELECT b.source_event_id, b.asset_id, b.tag_id, b.work_unit_id, b.shift_instance_id, b.business_date,
       CASE WHEN ec.field IS NOT NULL THEN ec.new_category
            WHEN er.field IS NOT NULL AND b.category <> 'small_stop' AND dr.downtime_category IS NOT NULL
                 THEN dr.downtime_category
            WHEN er.field IS NOT NULL AND b.category <> 'small_stop' AND er.new_reason_ids = '{}' THEN 'unplanned'
            ELSE b.category END::varchar(20)                                        AS category,
       CASE WHEN er.field IS NOT NULL THEN er.new_reason_ids ELSE b.reason_ids END AS reason_ids,
       CASE WHEN ed.field IS NOT NULL THEN ed.new_detail_reason ELSE b.detail_reason END AS detail_reason,
       CASE WHEN ea.field IS NOT NULL THEN ea.new_actions ELSE b.actions END       AS actions,
       CASE WHEN em.field IS NOT NULL THEN em.new_maintained_by ELSE b.maintained_by END AS maintained_by,
       b.product_id, b.source, b.start_at, b.end_at,
       (extract(epoch FROM b.end_at - b.start_at) * 1000)::bigint                  AS duration,
       b.stop_group_id,
       (ec.field IS NOT NULL OR er.field IS NOT NULL OR ed.field IS NOT NULL OR ea.field IS NOT NULL
        OR em.field IS NOT NULL)                                                   AS is_edited,
       (SELECT l.created_by FROM silver.v_downtime_edit_latest l
         WHERE l.source_event_id = b.source_event_id ORDER BY l.created_at DESC LIMIT 1) AS last_edited_by
FROM base b
LEFT JOIN silver.v_downtime_edit_latest ec ON ec.source_event_id = b.source_event_id AND ec.field = 'category'
LEFT JOIN silver.v_downtime_edit_latest er ON er.source_event_id = b.source_event_id AND er.field = 'reasons'
LEFT JOIN silver.v_downtime_edit_latest ed ON ed.source_event_id = b.source_event_id AND ed.field = 'detail_reason'
LEFT JOIN silver.v_downtime_edit_latest ea ON ea.source_event_id = b.source_event_id AND ea.field = 'actions'
LEFT JOIN silver.v_downtime_edit_latest em ON em.source_event_id = b.source_event_id AND em.field = 'maintained_by'
LEFT JOIN silver.downtime_reason dr ON dr.downtime_reason_id = er.new_reason_ids[1];

-- MTTR / MTBF (ms) over closed UNPLANNED stops (after edits) whose first piece starts in [p_from, p_to)
-- (PRD KPI-005: pieces rejoined by stop_group_id; MTBF gaps per work unit, pooled). NULL when none / fewer than 2.
CREATE FUNCTION silver.fn_reliability(p_work_unit_ids bigint[], p_from timestamptz, p_to timestamptz)
RETURNS TABLE (mttr_ms bigint, mtbf_ms bigint, failures integer)
LANGUAGE sql STABLE AS $$
  WITH f AS (
    SELECT work_unit_id, min(start_at) AS s, max(end_at) AS e, bool_or(end_at IS NULL) AS still_open
    FROM silver.v_downtime_effective
    WHERE category = 'unplanned' AND work_unit_id = ANY (p_work_unit_ids)
    GROUP BY work_unit_id, stop_group_id),
  c AS (SELECT * FROM f WHERE NOT still_open AND s >= p_from AND s < p_to),
  g AS (SELECT lead(s) OVER (PARTITION BY work_unit_id ORDER BY s) - e AS gap FROM c)
  SELECT (avg(extract(epoch FROM e - s)) * 1000)::bigint,
         (SELECT (avg(extract(epoch FROM gap)) * 1000)::bigint FROM g WHERE gap IS NOT NULL),
         count(*)::integer
  FROM c
$$;

CREATE VIEW silver.v_wu_shift_downtime AS
SELECT d.work_unit_id, d.shift_instance_id,
       sum(x.ms) FILTER (WHERE d.category = 'planned')    AS pdt_time,
       sum(x.ms) FILTER (WHERE d.category = 'unplanned')  AS updt_time,
       sum(x.ms) FILTER (WHERE d.category = 'small_stop') AS ms_time
FROM silver.v_downtime_effective d
JOIN silver.shift_instance s ON s.shift_instance_id = d.shift_instance_id
CROSS JOIN LATERAL (SELECT greatest(0, (extract(epoch FROM
          least(coalesce(d.end_at, now()), s.end_datetime) - greatest(d.start_at, s.start_datetime)) * 1000))::bigint AS ms) x
GROUP BY 1, 2;

-- ---------------------------------------------------------------------
-- Quantities
-- ---------------------------------------------------------------------
-- Σ counts per work unit × shift × product × KPI slot, found THROUGH THE BINDING (as of business_date)
CREATE VIEW silver.v_wu_slot_qty AS
SELECT b.work_unit_id, b.binding_id, b.slot_id, b.transform, b.asset_tag_id AS tag_id,
       c.shift_instance_id, c.business_date, c.product_id, c.uom_id AS tag_uom_id, t.count_basis,
       sum(c.qty) AS qty, sum(c.positive_count) AS positive_count
FROM silver.work_unit_kpi_binding b
JOIN silver.asset_tags t ON t.tag_id = b.asset_tag_id
JOIN silver.production_events_1m c
  ON c.tag_id = b.asset_tag_id
 AND c.business_date >= b.valid_from AND (b.valid_to IS NULL OR c.business_date < b.valid_to)
WHERE b.slot_id IN ('quality.total', 'quality.good', 'performance.output')
GROUP BY 1,2,3,4,5,6,7,8,9,10
UNION ALL
SELECT b.work_unit_id, b.binding_id, b.slot_id, b.transform, b.asset_tag_id,
       c.shift_instance_id, c.business_date, c.product_id, c.uom_id, t.count_basis,
       sum(c.qty), sum(c.positive_count)
FROM silver.work_unit_kpi_binding b
JOIN silver.asset_tags t ON t.tag_id = b.asset_tag_id
JOIN silver.reject_events_1m c
  ON c.tag_id = b.asset_tag_id
 AND c.business_date >= b.valid_from AND (b.valid_to IS NULL OR c.business_date < b.valid_to)
WHERE b.slot_id = 'quality.reject'
GROUP BY 1,2,3,4,5,6,7,8,9,10;

-- Live manual quality entries (silver + gold), quantity converted to the product's base unit
CREATE VIEW silver.v_quality_entry_effective AS
SELECT x.domain, x.entry_id, x.kind, x.work_unit_id, x.shift_instance_id, x.business_date, x.product_id,
       x.reject_reason_id, x.entered_qty, x.entered_uom_id, x.override_tag_id, p.base_uom_id,
       round(x.entered_qty * silver.fn_uom_factor(x.product_id, x.entered_uom_id, p.base_uom_id, x.business_date), 3)
         AS qty_base
FROM (SELECT 'silver'::text AS domain, e.* FROM silver.quality_entry e WHERE e.voided_at IS NULL
      UNION ALL
      SELECT 'gold', e.* FROM gold.quality_entry e WHERE e.voided_at IS NULL) x
JOIN silver.product p ON p.product_id = x.product_id;

-- ---------------------------------------------------------------------
-- v_wu_shift_report -> gold.work_unit_shift_report
-- ---------------------------------------------------------------------
CREATE VIEW silver.v_wu_shift_report AS
WITH q AS (   -- sensor quality counts in base uom
  SELECT v.work_unit_id, v.shift_instance_id, v.slot_id, v.product_id, p.base_uom_id,
         round(v.qty * silver.fn_uom_factor(v.product_id, v.tag_uom_id, p.base_uom_id, v.business_date), 3) AS qty_base
  FROM silver.v_wu_slot_qty v
  LEFT JOIN silver.product p ON p.product_id = v.product_id
  WHERE v.slot_id LIKE 'quality.%'),
qa AS (
  SELECT work_unit_id, shift_instance_id,
         sum(qty_base) FILTER (WHERE slot_id = 'quality.total')  AS total_out,
         sum(qty_base) FILTER (WHERE slot_id = 'quality.good')   AS good_tag,
         sum(qty_base) FILTER (WHERE slot_id = 'quality.reject') AS reject_tag,
         count(DISTINCT base_uom_id) AS n_uom, count(*) FILTER (WHERE base_uom_id IS NULL) AS n_no_uom,
         min(base_uom_id) AS base_uom_id, bool_or(qty_base IS NULL) AS unconverted
  FROM q GROUP BY 1, 2),
me AS (       -- manual entries: reject added, rework (overrides move reject between reasons, total unchanged)
  SELECT work_unit_id, shift_instance_id,
         sum(qty_base) FILTER (WHERE kind = 'reject') AS reject_manual,
         sum(qty_base) FILTER (WHERE kind = 'rework') AS rework,
         bool_or(qty_base IS NULL AND kind <> 'reject_override') AS unconverted
  FROM silver.v_quality_entry_effective GROUP BY 1, 2),
perf AS (     -- ideal (net) time: Σ cycles × cycle time per product; output = performance.output, else quality.total
  SELECT v.work_unit_id, v.shift_instance_id,
         sum(CASE WHEN v.count_basis = 'cycle' THEN v.qty
                  ELSE v.qty * silver.fn_uom_factor(v.product_id, v.tag_uom_id, ct.op_uom_id, v.business_date) / ct.batch_size
             END * ct.ct_s) * 1000 AS ideal_ms,
         bool_or(ct.ct_s IS NULL OR (v.count_basis IS DISTINCT FROM 'cycle'
                 AND silver.fn_uom_factor(v.product_id, v.tag_uom_id, ct.op_uom_id, v.business_date) IS NULL)) AS ct_missing
  FROM silver.v_wu_slot_qty v
  LEFT JOIN LATERAL silver.fn_cycle_time(v.work_unit_id, v.product_id, v.business_date) ct ON true
  WHERE v.slot_id = CASE WHEN EXISTS (SELECT 1 FROM silver.work_unit_kpi_binding b2
                                       WHERE b2.work_unit_id = v.work_unit_id AND b2.slot_id = 'performance.output'
                                         AND b2.valid_from <= v.business_date
                                         AND (b2.valid_to IS NULL OR b2.valid_to > v.business_date))
                         THEN 'performance.output' ELSE 'quality.total' END
  GROUP BY 1, 2),
bind AS (     -- bindings in force for the shift
  SELECT s.work_unit_id, s.shift_instance_id,
         bool_or(b.slot_id = 'quality.total') AS has_total,
         bool_or(b.slot_id = 'quality.good' AND b.transform = 'complement_derive') AS good_by_complement,
         array_agg(b.binding_id ORDER BY b.binding_id) AS binding_ids
  FROM silver.v_shift_work_unit s
  JOIN silver.work_unit_kpi_binding b
    ON b.work_unit_id = s.work_unit_id
   AND b.valid_from <= s.business_date AND (b.valid_to IS NULL OR b.valid_to > s.business_date)
  GROUP BY 1, 2),
base AS (
  SELECT s.shift_instance_id, s.business_date, s.shift_no, s.shift_label, s.site_id, s.time_zone, s.area_id,
         s.work_center_id, s.work_unit_id, s.work_unit_name, s.work_unit_code, s.start_datetime, s.end_datetime,
         (qa.n_uom = 1 AND qa.n_no_uom = 0 AND NOT qa.unconverted AND NOT coalesce(me.unconverted, false)) AS uom_ok,
         qa.base_uom_id, qa.total_out,
         coalesce(qa.reject_tag, 0) + coalesce(me.reject_manual, 0) AS reject_all,
         (qa.reject_tag IS NOT NULL OR me.reject_manual IS NOT NULL) AS has_reject,
         CASE WHEN bd.good_by_complement THEN qa.total_out - coalesce(qa.reject_tag, 0) - coalesce(me.reject_manual, 0)
              ELSE qa.good_tag END AS good_raw,
         me.rework, s.availability_time,
         coalesce(dt.pdt_time, 0) AS pdt_time, coalesce(dt.updt_time, 0) AS updt_time, coalesce(dt.ms_time, 0) AS ms_time,
         perf.ideal_ms, perf.ct_missing, coalesce(bd.has_total, false) AS has_total, bd.binding_ids
  FROM silver.v_shift_work_unit s
  LEFT JOIN qa   ON qa.work_unit_id = s.work_unit_id AND qa.shift_instance_id = s.shift_instance_id
  LEFT JOIN me   ON me.work_unit_id = s.work_unit_id AND me.shift_instance_id = s.shift_instance_id
  LEFT JOIN perf ON perf.work_unit_id = s.work_unit_id AND perf.shift_instance_id = s.shift_instance_id
  LEFT JOIN bind bd ON bd.work_unit_id = s.work_unit_id AND bd.shift_instance_id = s.shift_instance_id
  LEFT JOIN silver.v_wu_shift_downtime dt ON dt.work_unit_id = s.work_unit_id AND dt.shift_instance_id = s.shift_instance_id),
t AS (
  SELECT b.*,
         CASE WHEN b.uom_ok THEN b.base_uom_id END  AS uom_id,
         CASE WHEN b.uom_ok THEN b.total_out END    AS total_out_v,
         CASE WHEN b.uom_ok THEN b.good_raw END     AS good_out,
         CASE WHEN b.uom_ok AND b.has_reject THEN b.reject_all END AS reject,
         greatest(0, b.availability_time - b.pdt_time)                           AS production_time,
         greatest(0, b.availability_time - b.pdt_time - b.updt_time)             AS operating_time,
         greatest(0, b.availability_time - b.pdt_time - b.updt_time - b.ms_time) AS runtime,
         round(b.ideal_ms)::bigint                                               AS net_time
  FROM base b),
m AS (
  SELECT t.*,
         greatest(0, t.runtime - t.net_time) AS reduce_speed_time,
         greatest(0, t.net_time - t.runtime) AS up_speed_time,
         CASE WHEN t.total_out_v > 0 THEN round(t.net_time * t.good_out / t.total_out_v)::bigint END             AS value_added_time,
         CASE WHEN t.total_out_v > 0 THEN round(t.net_time * coalesce(t.reject, 0) / t.total_out_v)::bigint END AS reject_time,
         CASE WHEN t.total_out_v > 0 THEN round(t.net_time * coalesce(t.rework, 0) / t.total_out_v)::bigint END AS rework_time,
         CASE WHEN t.total_out_v > 0 THEN greatest(0, round(t.net_time * (t.total_out_v - t.good_out - coalesce(t.reject, 0))
                                                            / t.total_out_v))::bigint END                       AS anomaly_time,
         CASE WHEN t.net_time > 0 THEN round(t.total_out_v * t.runtime / t.net_time, 3) END                    AS effective_out
  FROM t)
SELECT m.shift_instance_id, m.business_date, m.shift_no, m.shift_label, m.site_id, m.time_zone, m.area_id,
       m.work_center_id, m.work_unit_id, m.work_unit_name, m.work_unit_code,
       m.uom_id, u.name AS uom_name, u.code AS uom_code,
       round(100.0 * m.value_added_time / nullif(m.availability_time, 0), 3)                         AS teep,
       round(100.0 * m.value_added_time / nullif(m.production_time, 0), 3)                           AS oee,
       round(100.0 * m.operating_time / nullif(m.production_time, 0), 3)                             AS availability,
       CASE WHEN m.net_time > 0 THEN round(100.0 * m.net_time / nullif(m.operating_time, 0), 3) END  AS performance,
       round(100.0 * m.good_out / nullif(m.total_out_v, 0), 3)                                       AS quality,
       m.total_out_v AS total_out, m.good_out, m.effective_out,
       m.runtime, m.availability_time, m.production_time, m.pdt_time, m.operating_time, m.updt_time,
       m.net_time, m.reduce_speed_time, m.ms_time, m.value_added_time, m.rework_time, m.reject_time,
       m.anomaly_time, m.up_speed_time,
       m.pdt_time AS schedule_loss_time, m.updt_time AS availability_loss_time,
       m.reduce_speed_time + m.ms_time AS performance_loss_time, m.ms_time AS ms_loss_time,
       m.reduce_speed_time AS reduce_speed_loss_time, m.reject_time AS quality_loss_time,
       m.reject, m.rework,
       r.mttr_ms AS mttr, r.mtbf_ms AS mtbf,
       CASE WHEN NOT m.has_total                        THEN 'no_binding'
            WHEN m.total_out_v > 0 AND m.net_time IS NULL THEN 'no_cycle_time'
            WHEN NOT coalesce(m.uom_ok, true) OR m.ct_missing THEN 'partial'
            ELSE 'ok' END::varchar(20) AS data_status,
       m.binding_ids,
       now() AS computed_at
FROM m
LEFT JOIN silver.unit_of_measurements u ON u.uom_id = m.uom_id
CROSS JOIN LATERAL silver.fn_reliability(ARRAY[m.work_unit_id], m.start_datetime, m.end_datetime) r;

-- ---------------------------------------------------------------------
-- v_wc_shift_report -> gold.work_center_shift_report: Σ work units; good_out from the FINAL work units of
-- equipment_flow; P and Q only over members that have a standard; OEE = A × P × Q
-- ---------------------------------------------------------------------
CREATE VIEW silver.v_wc_shift_report AS
WITH s AS (
  SELECT r.*, sh.start_datetime AS start_dt, sh.end_datetime AS end_dt, f.work_unit_id IS NOT NULL AS is_final
  FROM silver.v_wu_shift_report r
  JOIN silver.shift_instance sh ON sh.shift_instance_id = r.shift_instance_id
  LEFT JOIN LATERAL silver.fn_work_center_final_work_units(r.business_date) f
         ON f.work_unit_id = r.work_unit_id AND f.work_center_id = r.work_center_id),
bids AS (
  SELECT s.shift_instance_id, s.work_center_id, array_agg(DISTINCT b ORDER BY b) AS binding_ids
  FROM s, unnest(s.binding_ids) b GROUP BY 1, 2),
a AS (
  SELECT s.shift_instance_id, s.business_date, s.shift_no, s.shift_label, s.site_id, s.time_zone, s.area_id,
         s.work_center_id, min(s.start_dt) AS start_dt, max(s.end_dt) AS end_dt,
         count(*)::integer AS work_unit_count,
         array_agg(s.work_unit_id ORDER BY s.work_unit_id) FILTER (WHERE s.is_final) AS final_work_unit_ids,
         array_agg(s.work_unit_id) AS all_work_unit_ids,
         CASE WHEN count(DISTINCT s.uom_id) = 1 AND count(s.uom_id) = count(*) FILTER (WHERE s.total_out IS NOT NULL)
              THEN min(s.uom_id) END AS uom_id,
         sum(s.total_out) AS total_out, sum(s.good_out) FILTER (WHERE s.is_final) AS good_out,
         sum(s.effective_out) AS effective_out, sum(s.runtime) AS runtime,
         sum(s.availability_time) AS availability_time, sum(s.production_time) AS production_time,
         sum(s.pdt_time) AS pdt_time, sum(s.operating_time) AS operating_time, sum(s.updt_time) AS updt_time,
         sum(s.net_time) AS net_time, sum(s.reduce_speed_time) AS reduce_speed_time, sum(s.ms_time) AS ms_time,
         sum(s.operating_time) FILTER (WHERE s.net_time IS NOT NULL) AS operating_time_with_std,
         sum(s.value_added_time) AS value_added_time, sum(s.rework_time) AS rework_time,
         sum(s.reject_time) AS reject_time, sum(s.anomaly_time) AS anomaly_time,
         sum(s.up_speed_time) AS up_speed_time, sum(s.reject) AS reject, sum(s.rework) AS rework,
         bool_or(s.data_status <> 'ok') AS any_not_ok, bool_and(s.data_status = 'no_binding') AS all_no_binding
  FROM s GROUP BY 1,2,3,4,5,6,7,8)
SELECT a.shift_instance_id, a.business_date, a.shift_no, a.shift_label, a.site_id, a.time_zone, a.area_id,
       a.work_center_id, wc.work_center_name, wc.work_center_code, a.work_unit_count, a.final_work_unit_ids,
       a.uom_id, u.name AS uom_name, u.code AS uom_code,
       round(100.0 * x.av * x.pf * x.ql * a.production_time / nullif(a.availability_time, 0), 3) AS teep,
       round(100.0 * x.av * x.pf * x.ql, 3) AS oee,
       round(100.0 * x.av, 3) AS availability,
       round(100.0 * x.pf, 3) AS performance,
       round(100.0 * x.ql, 3) AS quality,
       CASE WHEN a.uom_id IS NOT NULL THEN a.total_out END     AS total_out,
       CASE WHEN a.uom_id IS NOT NULL THEN a.good_out END      AS good_out,
       CASE WHEN a.uom_id IS NOT NULL THEN a.effective_out END AS effective_out,
       a.runtime, a.availability_time, a.production_time, a.pdt_time, a.operating_time, a.updt_time,
       a.net_time, a.reduce_speed_time, a.ms_time, a.value_added_time, a.rework_time, a.reject_time,
       a.anomaly_time, a.up_speed_time,
       a.pdt_time AS schedule_loss_time, a.updt_time AS availability_loss_time,
       a.reduce_speed_time + a.ms_time AS performance_loss_time, a.ms_time AS ms_loss_time,
       a.reduce_speed_time AS reduce_speed_loss_time, a.reject_time AS quality_loss_time,
       CASE WHEN a.uom_id IS NOT NULL THEN a.reject END AS reject,
       CASE WHEN a.uom_id IS NOT NULL THEN a.rework END AS rework,
       r.mttr_ms AS mttr, r.mtbf_ms AS mtbf,
       CASE WHEN a.all_no_binding THEN 'no_binding' WHEN a.any_not_ok OR a.uom_id IS NULL THEN 'partial'
            ELSE 'ok' END::varchar(20) AS data_status,
       bids.binding_ids,
       now() AS computed_at
FROM a
JOIN silver.work_center wc ON wc.work_center_id = a.work_center_id
LEFT JOIN bids ON bids.shift_instance_id = a.shift_instance_id AND bids.work_center_id = a.work_center_id
LEFT JOIN silver.unit_of_measurements u ON u.uom_id = a.uom_id
CROSS JOIN LATERAL (SELECT a.operating_time::numeric / nullif(a.production_time, 0)   AS av,
                           a.net_time::numeric / nullif(a.operating_time_with_std, 0) AS pf,
                           a.value_added_time::numeric / nullif(a.net_time, 0)        AS ql) x
CROSS JOIN LATERAL silver.fn_reliability(a.all_work_unit_ids, a.start_dt, a.end_dt) r;

-- ---------------------------------------------------------------------
-- v_product_count -> gold.product_count (per work unit × shift × product, base uom; manual entries added)
-- ---------------------------------------------------------------------
CREATE VIEW silver.v_product_count AS
WITH rows_ AS (
  SELECT v.work_unit_id, v.shift_instance_id, v.product_id, v.slot_id AS kind,
         round(v.qty * silver.fn_uom_factor(v.product_id, v.tag_uom_id, p.base_uom_id, v.business_date), 3) AS qty_base
  FROM silver.v_wu_slot_qty v
  JOIN silver.product p ON p.product_id = v.product_id
  WHERE v.slot_id LIKE 'quality.%'
  UNION ALL
  SELECT work_unit_id, shift_instance_id, product_id, 'manual.' || kind, qty_base
  FROM silver.v_quality_entry_effective WHERE kind IN ('reject', 'rework')),
a AS (
  SELECT work_unit_id, shift_instance_id, product_id,
         sum(qty_base) FILTER (WHERE kind = 'quality.total')  AS total_out,
         sum(qty_base) FILTER (WHERE kind = 'quality.good')   AS good_tag,
         sum(qty_base) FILTER (WHERE kind IN ('quality.reject', 'manual.reject')) AS reject,
         sum(qty_base) FILTER (WHERE kind = 'manual.rework')  AS rework
  FROM rows_ GROUP BY 1, 2, 3)
SELECT s.shift_instance_id, s.business_date, s.shift_no, s.shift_label, s.work_center_id, s.work_unit_id,
       s.work_unit_name, s.work_unit_code, a.product_id, p.name AS product_name, p.code AS product_code,
       p.base_uom_id, u.name AS base_uom_name, u.code AS base_uom_code,
       a.total_out,
       CASE WHEN EXISTS (SELECT 1 FROM silver.work_unit_kpi_binding b
                          WHERE b.work_unit_id = a.work_unit_id AND b.slot_id = 'quality.good'
                            AND b.transform = 'complement_derive'
                            AND b.valid_from <= s.business_date AND (b.valid_to IS NULL OR b.valid_to > s.business_date))
            THEN a.total_out - coalesce(a.reject, 0) ELSE a.good_tag END AS good_out,
       a.reject, a.rework, now() AS computed_at
FROM a
JOIN silver.v_shift_work_unit s ON s.work_unit_id = a.work_unit_id AND s.shift_instance_id = a.shift_instance_id
JOIN silver.product p ON p.product_id = a.product_id
LEFT JOIN silver.unit_of_measurements u ON u.uom_id = p.base_uom_id;

-- ---------------------------------------------------------------------
-- v_reject_count -> gold.reject_count. origin: sensor (one node = one reason, minus quantity moved away by
-- overrides) | override (quantity moved to another reason) | manual (entered reject).
-- ---------------------------------------------------------------------
CREATE VIEW silver.v_reject_count AS
WITH ov AS (
  SELECT work_unit_id, shift_instance_id, product_id, override_tag_id AS tag_id, reject_reason_id,
         sum(entered_qty) AS qty, count(*) AS n
  FROM silver.v_quality_entry_effective WHERE kind = 'reject_override' GROUP BY 1, 2, 3, 4, 5),
r AS (
  SELECT 'sensor'::varchar(20) AS origin, v.work_unit_id, v.shift_instance_id, v.business_date, v.product_id,
         v.tag_id, t.reject_reason_id, v.tag_uom_id AS qty_uom_id,
         v.qty - coalesce((SELECT sum(o.qty) FROM ov o WHERE o.work_unit_id = v.work_unit_id
                             AND o.shift_instance_id = v.shift_instance_id AND o.product_id = v.product_id
                             AND o.tag_id = v.tag_id), 0) AS qty,
         v.positive_count::integer AS freq
  FROM silver.v_wu_slot_qty v
  JOIN silver.asset_tags t ON t.tag_id = v.tag_id
  WHERE v.slot_id = 'quality.reject'
  UNION ALL
  SELECT 'override', o.work_unit_id, o.shift_instance_id, si.business_date, o.product_id, o.tag_id,
         o.reject_reason_id, t.uom_id, o.qty, o.n::integer
  FROM ov o JOIN silver.asset_tags t ON t.tag_id = o.tag_id
  JOIN silver.shift_instance si ON si.shift_instance_id = o.shift_instance_id
  UNION ALL
  SELECT 'manual', e.work_unit_id, e.shift_instance_id, e.business_date, e.product_id, NULL::bigint,
         e.reject_reason_id, e.entered_uom_id, sum(e.entered_qty), count(*)::integer
  FROM silver.v_quality_entry_effective e WHERE e.kind = 'reject'
  GROUP BY 1, 2, 3, 4, 5, 6, 7, 8)
SELECT s.shift_instance_id, s.business_date, s.shift_no, s.shift_label, s.work_center_id, s.work_unit_id,
       s.work_unit_name, t.asset_id, a.asset_name, r.product_id, p.name AS product_name,
       r.reject_reason_id, rr.name AS reject_reason_name, r.origin, r.tag_id, t.node AS source_tag,
       CASE lower(qu.unit) WHEN 'kg' THEN round(r.qty * 1000, 3) WHEN 'g' THEN round(r.qty, 3) END AS weight_gram,
       round(r.qty * silver.fn_uom_factor(r.product_id, r.qty_uom_id, p.base_uom_id, r.business_date), 3) AS quantity,
       p.base_uom_id AS uom_id, r.freq,
       round(r.qty * silver.fn_uom_factor(r.product_id, r.qty_uom_id, ct.op_uom_id, r.business_date)
             / ct.batch_size * ct.ct_s * 1000)::bigint AS reject_time,
       now() AS computed_at
FROM r
JOIN silver.v_shift_work_unit s ON s.work_unit_id = r.work_unit_id AND s.shift_instance_id = r.shift_instance_id
LEFT JOIN silver.asset_tags t ON t.tag_id = r.tag_id
LEFT JOIN silver.asset a ON a.asset_id = t.asset_id
LEFT JOIN silver.reject_reason rr ON rr.reject_reason_id = r.reject_reason_id
LEFT JOIN silver.product p ON p.product_id = r.product_id
LEFT JOIN silver.unit_of_measurements qu ON qu.uom_id = r.qty_uom_id
LEFT JOIN LATERAL silver.fn_cycle_time(r.work_unit_id, r.product_id, r.business_date) ct ON true
WHERE r.qty <> 0;

-- ---------------------------------------------------------------------
-- v_downtime_report -> gold.downtime_report (one row per stop piece, after edits; no jsonb)
-- ---------------------------------------------------------------------
CREATE VIEW silver.v_downtime_report AS
SELECT d.source_event_id, d.shift_instance_id, d.business_date, s.shift_no, s.shift_label, wc.area_id,
       wu.work_center_id, d.work_unit_id, wu.work_unit_name, d.asset_id, a.asset_tag AS asset_code, a.asset_name,
       d.category,
       d.reason_ids[1] AS primary_reason_id,
       (SELECT btrim(r.name) FROM silver.downtime_reason r WHERE r.downtime_reason_id = d.reason_ids[1])::varchar(80)
         AS primary_reason_name,
       coalesce((SELECT array_agg(btrim(r.name) ORDER BY u.ord)
                   FROM unnest(d.reason_ids) WITH ORDINALITY u(rid, ord)
                   JOIN silver.downtime_reason r ON r.downtime_reason_id = u.rid), '{}') AS reason_names,
       d.detail_reason, d.actions, d.maintained_by,
       d.product_id, p.name AS product_name, d.source, d.start_at, d.end_at, d.duration, d.stop_group_id,
       d.is_edited, d.last_edited_by, now() AS computed_at
FROM silver.v_downtime_effective d
JOIN silver.v_shift s ON s.shift_instance_id = d.shift_instance_id
JOIN silver.work_unit wu ON wu.work_unit_id = d.work_unit_id
JOIN silver.work_center wc ON wc.work_center_id = wu.work_center_id
JOIN silver.asset a ON a.asset_id = d.asset_id
LEFT JOIN silver.product p ON p.product_id = d.product_id;

-- v_loss_by_reason -> gold.loss_by_reason (schedule = planned, availability = unplanned, performance = small stop)
CREATE VIEW silver.v_loss_by_reason AS
SELECT r.shift_instance_id, r.business_date, r.shift_no, r.shift_label, r.work_unit_id,
       CASE r.category WHEN 'planned' THEN 'schedule' WHEN 'unplanned' THEN 'availability' ELSE 'performance' END
         ::varchar(20) AS loss_type,
       r.primary_reason_id AS reason_id, r.primary_reason_name AS reason_name, r.asset_id, r.asset_name,
       count(DISTINCT r.stop_group_id)::integer AS freq,
       sum(coalesce(r.duration, (extract(epoch FROM now() - r.start_at) * 1000)::bigint)) AS total_duration,
       now() AS computed_at
FROM silver.v_downtime_report r
GROUP BY 1,2,3,4,5,6,7,8,9,10;

-- ---------------------------------------------------------------------
-- v_product_perf_hourly -> gold.product_perf_hourly: one row per work unit × local hour × product.
-- A minute belongs to the product with output in it (last product carried forward); stop time inside the minute
-- is removed (silver_model.md §10).
-- ---------------------------------------------------------------------
CREATE VIEW silver.v_product_perf_hourly AS
WITH out_tag AS (   -- output tag: performance.output, else quality.total
  SELECT DISTINCT ON (s.work_unit_id, s.shift_instance_id) s.work_unit_id, s.shift_instance_id, b.asset_tag_id AS tag_id
  FROM silver.v_shift_work_unit s
  JOIN silver.work_unit_kpi_binding b
    ON b.work_unit_id = s.work_unit_id AND b.slot_id IN ('performance.output', 'quality.total')
   AND b.valid_from <= s.business_date AND (b.valid_to IS NULL OR b.valid_to > s.business_date)
  ORDER BY s.work_unit_id, s.shift_instance_id, (b.slot_id = 'performance.output') DESC),
minute AS (
  SELECT s.shift_instance_id, s.business_date, s.shift_no, s.shift_label, s.time_zone, s.work_center_id,
         s.work_unit_id, s.work_unit_code, s.work_unit_name, o.tag_id, m AS minute_start, s.start_datetime
  FROM silver.v_shift_work_unit s
  JOIN out_tag o ON o.work_unit_id = s.work_unit_id AND o.shift_instance_id = s.shift_instance_id
  CROSS JOIN LATERAL generate_series(s.start_datetime, least(s.end_datetime, now()) - interval '1 minute',
                                     interval '1 minute') m),
attributed AS (
  SELECT mi.*,
         (SELECT c.product_id FROM silver.production_events_1m c
           WHERE c.tag_id = mi.tag_id AND c.bucket_time <= mi.minute_start AND c.bucket_time >= mi.start_datetime
             AND c.product_id IS NOT NULL AND c.qty > 0
           ORDER BY c.bucket_time DESC, c.qty DESC LIMIT 1) AS product_id,
         greatest(0, 60000 - coalesce((
           SELECT sum(extract(epoch FROM least(coalesce(d.end_at, now()), mi.minute_start + interval '1 minute')
                                         - greatest(d.start_at, mi.minute_start)) * 1000)
           FROM silver.v_downtime_effective d
           WHERE d.work_unit_id = mi.work_unit_id
             AND d.start_at < mi.minute_start + interval '1 minute'
             AND coalesce(d.end_at, now()) > mi.minute_start), 0))::bigint AS runtime_ms
  FROM minute mi),
hourly AS (
  SELECT a.work_unit_id, a.product_id,
         (date_trunc('hour', a.minute_start AT TIME ZONE a.time_zone) AT TIME ZONE a.time_zone) AS bucket_start,
         a.shift_instance_id, a.business_date, a.shift_no, a.shift_label, a.time_zone, a.work_center_id,
         a.work_unit_name, a.work_unit_code, a.tag_id, sum(a.runtime_ms) AS runtime
  FROM attributed a WHERE a.product_id IS NOT NULL
  GROUP BY 1,2,3,4,5,6,7,8,9,10,11,12),
m AS (
  SELECT h.*, p.name AS product_name, p.code AS product_code, p.base_uom_id,
         round((SELECT sum(c.qty) FROM silver.production_events_1m c
                 WHERE c.tag_id = h.tag_id AND c.product_id = h.product_id AND c.shift_instance_id = h.shift_instance_id
                   AND c.bucket_time >= h.bucket_start AND c.bucket_time < h.bucket_start + interval '1 hour')
               * silver.fn_uom_factor(h.product_id, t.uom_id, p.base_uom_id, h.business_date), 3) AS total_out,
         ct.ct_s * silver.fn_uom_factor(h.product_id, p.base_uom_id, ct.op_uom_id, h.business_date)
           / ct.batch_size * 1000 AS ct_base_ms
  FROM hourly h
  JOIN silver.asset_tags t ON t.tag_id = h.tag_id
  JOIN silver.product p ON p.product_id = h.product_id
  LEFT JOIN LATERAL silver.fn_cycle_time(h.work_unit_id, h.product_id, h.business_date) ct ON true)
SELECT m.work_unit_id, m.bucket_start, m.product_id, m.shift_instance_id, m.business_date, m.shift_no, m.shift_label,
       extract(hour FROM m.bucket_start AT TIME ZONE m.time_zone)::smallint AS hour_of_day,
       m.time_zone, m.work_center_id, m.work_unit_name, m.work_unit_code, m.product_name, m.product_code,
       m.base_uom_id AS uom_id, u.code AS uom_code,
       coalesce(m.total_out, 0) AS total_out, m.runtime,
       round(m.runtime / nullif(m.ct_base_ms, 0), 3)                              AS effective_out,
       round(3600000.0 / nullif(m.ct_base_ms, 0), 3)                              AS ideal_rate_per_hour,
       round(coalesce(m.total_out, 0) / nullif(m.runtime / 3600000.0, 0), 3)      AS actual_rate_per_hour,
       now() AS computed_at
FROM m
LEFT JOIN silver.unit_of_measurements u ON u.uom_id = m.base_uom_id
WHERE m.runtime > 0 OR coalesce(m.total_out, 0) > 0;
