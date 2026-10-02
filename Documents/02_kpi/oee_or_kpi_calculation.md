Context Calculation of OEE because u will still use from ERD reference!
# Complete OEE Calculation Rules & Domain Specification
*Document for Context Transfer, Architecture Alignment & Implementation*

---

## 1. Core Principles & Time Boundaries

### A. Manufacturing Day & Shift Anchoring (7 AM Rule)
* **Manufacturing Day**: Starts at **07:00:00 AM** and ends at **06:59:59 AM the next day**.
* **Formula for Production Date (`business_date`)**:
  $$\text{business\_date} = \text{DATE}((\text{timestamp AT TIME ZONE 'Asia/Jakarta'}) - \text{INTERVAL '7 hours'})$$
* **Shift Definitions** or from tables of shift!
  * **Shift 1**: `07:00:00 - 14:59:59` (8 hours / 28,800,000 ms)
  * **Shift 2**: `15:00:00 - 22:59:59` (8 hours / 28,800,000 ms)
  * **Shift 3**: `23:00:00 - 06:59:59` (Crosses midnight! Belonging to the anchor date started at 7 AM).

### B. Time Unit Standard
* All internal time calculations, durations, and waterfall losses are computed in **milliseconds (`BIGINT`)**.
* Conversion to hours: $\text{hours} = \frac{\text{milliseconds}}{3,600,000.0}$.

### C. Base UOM Normalization (Packaging Multipliers)
* Counters come from sensors in different packaging levels (Sachets, Bundles, Cartons).
* All outputs (`total_out`, `good_out`, `reject`, `rework`) must be multiplied by **`factor_to_base`** (from `ms_core.product_packages`) so all calculations use the **Base UOM** (e.g. 1 Carton of 120 = 120 Sachets).

---

## 2. The Time Waterfall Architecture

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│ Total Time / Availability Time (100% Full Calendar Time, e.g., 24h = 86,400,000 ms)   │
├──────────────────────────────────────────┬─────────────────────────────────────────────┤
│ Production Time (Planned Operation)      │ Schedule Loss / Planned Downtime (PDT)      │
├────────────────────────────┬─────────────┴─────────────────────────────────────────────┤
│ Operating Time             │ Availability Loss / Unplanned Downtime (UPDT)             │
├──────────────┬─────────────┴───────────────────────────────────────────────────────────┤
│ Net Time     │ Performance Loss (Reduce Speed Loss + Small Stops / Microstoppages)     │
├──────────────┴─────────────┬───────────────────────────────────────────────────────────┤
│ Value Added Time (Good)    │ Quality Loss (Reject Loss Time + Rework Loss Time)        │
└────────────────────────────┴───────────────────────────────────────────────────────────┘
```

---

## 3. Step-by-Step Mathematical Formulas

### Step 1: Base Time Durations (in ms)
1. **`availability_time`**: Total duration of shift/day ($24\text{ hours} = 86,400,000\text{ ms}$, or $8\text{ hours} = 28,800,000\text{ ms}$).
2. **`pdt_time` (Schedule Loss Time)**:
   $$\text{pdt\_time} = \sum \text{Downtime duration where category} = \text{'PLANNED'}$$
3. **`production_time`**:
   $$\text{production\_time} = \max(0, \text{availability\_time} - \text{pdt\_time})$$
4. **`updt_time` (Availability Loss Time)**:
   $$\text{updt\_time} = \sum \text{Downtime duration where category} = \text{'UNPLANNED'}$$
5. **`operating_time`**:
   $$\text{operating\_time} = \max(0, \text{production\_time} - \text{updt\_time})$$
6. **`ms_time` (Small Stops / Microstoppages)**:
   $$\text{ms\_time} = \sum \text{Downtime duration where category} = \text{'SMALL\_STOP' (stops } < 3\text{ min)}$$
7. **`runtime`**:
   $$\text{runtime} = \max(0, \text{operating\_time} - \text{ms\_time}) = \max(0, \text{availability\_time} - (\text{pdt\_time} + \text{updt\_time} + \text{ms\_time}))$$

---

### Step 2: Cycle Time & Output Quantities
1. **`cycle_time_ms`** (Ideal milliseconds required to make 1 Base Unit):
   $$\text{cycle\_time\_ms} = \frac{3,600,000.0}{\text{ideal\_rate\_per\_hour}}$$
2. **`total_out`**: $\sum (\text{cleaned\_value} \times \text{factor\_to\_base})$ where `role = 'infeed'`.
3. **`good_out`**: $\sum (\text{cleaned\_value} \times \text{factor\_to\_base})$ where `role = 'good_output'`.
4. **`reject`**: $\sum (\text{cleaned\_value} \times \text{factor\_to\_base})$ where `role = 'reject'`.
5. **`rework`**: $\sum (\text{cleaned\_value} \times \text{factor\_to\_base})$ where `role = 'rework'`.
6. **`effective_out`** (Expected theoretical output during actual runtime):
   $$\text{effective\_out} = \frac{\text{runtime}}{\text{cycle\_time\_ms}} = \text{ideal\_rate\_per\_hour} \times \left(\frac{\text{runtime}}{3,600,000.0}\right)$$

---

### Step 3: Speed & Quality Loss Times (in ms)
1. **`reduce_speed_time` (Speed Loss)**:
   $$\text{reduce\_speed\_time} = \max\left(0, (\text{effective\_out} - \text{total\_out}) \times \text{cycle\_time\_ms}\right)$$
2. **`up_speed_time`** (When machine runs faster than master ideal speed):
   $$\text{up\_speed\_time} = \max\left(0, (\text{total\_out} - \text{effective\_out}) \times \text{cycle\_time\_ms}\right)$$
3. **`reject_time` (Quality Loss Time)**:
   $$\text{reject\_time} = \text{reject} \times \text{cycle\_time\_ms}$$
4. **`rework_time`**:
   $$\text{rework\_time} = \text{rework} \times \text{cycle\_time\_ms}$$
5. **`anomaly_time`** (Unaccounted scrap: Infeed - Good - Reject):
   $$\text{anomaly\_time} = \max\left(0, ((\text{total\_out} - \text{good\_out}) - \text{reject}) \times \text{cycle\_time\_ms}\right)$$
6. **`net_time`**:
   $$\text{net\_time} = \max\left(0, \text{operating\_time} - (\text{reduce\_speed\_time} + \text{ms\_time})\right) = \text{total\_out} \times \text{cycle\_time\_ms}$$
7. **`value_added_time`**:
   $$\text{value\_added\_time} = \max\left(0, \text{net\_time} - \text{reject\_time} - \text{rework\_time}\right) = \text{good\_out} \times \text{cycle\_time\_ms}$$

---

### Step 4: OEE, Availability, Performance, Quality & TEEP Ratios

All percentages are formatted as **`0.000` to `100.000`%** with `COALESCE` and `NULLIF` zero-division guards:

#### 1. Availability (%)
$$\text{Availability} = \frac{\text{operating\_time}}{\text{production\_time}} \times 100.0$$

#### 2. Performance (%)
$$\text{Performance} = \frac{\text{net\_time}}{\text{operating\_time}} \times 100.0 = \frac{\text{total\_out}}{\text{effective\_out}} \times 100.0$$

#### 3. Quality (%)
$$\text{Quality} = \frac{\text{value\_added\_time}}{\text{net\_time}} \times 100.0 = \frac{\text{good\_out}}{\text{total\_out}} \times 100.0$$

#### 4. OEE (Overall Equipment Effectiveness) (%)
$$\text{OEE} = \left(\frac{\text{Availability}}{100} \times \frac{\text{Performance}}{100} \times \frac{\text{Quality}}{100}\right) \times 100.0 = \frac{\text{value\_added\_time}}{\text{production\_time}} \times 100.0$$

#### 5. TEEP (Total Effective Equipment Performance) (%)
$$\text{Utility} = \frac{\text{production\_time}}{\text{availability\_time}}$$
$$\text{TEEP} = \text{OEE} \times \text{Utility} = \frac{\text{value\_added\_time}}{\text{availability\_time}} \times 100.0$$

---

## 4. Work Unit vs. Work Center (Line) Level Rules

| Metric | **Work Unit Level (Machine)** | **Work Center Level (Production Line)** |
| :--- | :--- | :--- |
| **`total_out`** | Total Infeed on this machine | **Sum of all Work Units** on the Line |
| **`good_out`** | Good output on this machine | **ONLY the Good Output of the LAST Machine** in the flow (`ms_core.work_unit_flows`) |
| **`reject`** | Rejects on this machine | **Sum of all Rejects** across all machines in the line |
| **`availability_time`** | $1 \times 24\text{ hours}$ (86.4M ms) | $\sum \text{availability\_time}$ across all machines ($N \times 86.4\text{M ms}$) |
| **`pdt_time` / `updt_time`** | Downtimes on this machine | **Sum of all downtime durations** across all machines in the line |
| **`runtime` / `net_time`** | Time metrics on this machine | **Sum of time metrics** across all machines in the line |
| **Line OEE Ratios** | Direct Work Unit ratio | Re-computed from aggregated line times: $\frac{\sum \text{value\_added\_time}}{\sum \text{production\_time}} \times 100$ |

---

## 5. Edge Cases & Safety Guards

1. **Zero Production (`total_out = 0`)**:
   * Performance $\rightarrow$ `0.000%`
   * Quality $\rightarrow$ `0.000%` (or `100.000%` if treated as 0 defects)
   * OEE $\rightarrow$ `0.000%`
2. **Full Scheduled Stop (`production_time = 0`)**:
   * Availability $\rightarrow$ `0.000%` (Guarded by `NULLIF(production_time, 0)`)
   * OEE $\rightarrow$ `0.000%`
3. **Unconfigured / Missing Cycle Time**:
   * Availability is **100% unaffected**.
   * Quality can be calculated directly from counts ($\frac{\text{good\_out}}{\text{total\_out}}$).
   * Fallback cycle time: `COALESCE(ideal_rate, 3600.0)` prevents query crashes.


This is how realtime from silver view to generate what the current report is, use it as reference because the master data is different! this is for work_unit

-- ms_events.vw_work_unit_day_report source

CREATE OR REPLACE VIEW ms_events.vw_work_unit_day_report
AS WITH day_boundary AS (
         SELECT date((CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Jakarta'::text) - '07:00:00'::interval) AS prod_date,
            ((date((CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Jakarta'::text) - '07:00:00'::interval) + '07:00:00'::time without time zone) AT TIME ZONE 'Asia/Jakarta'::text) AS day_start,
            ((date((CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Jakarta'::text) - '07:00:00'::interval) + '07:00:00'::time without time zone) AT TIME ZONE 'Asia/Jakarta'::text) + '1 day'::interval AS day_end,
            86400000::bigint AS total_availability_ms
        ), work_unit_master AS (
         SELECT wc.id AS work_center_id,
            wc.name AS work_center_name,
            wc.code AS work_center_code,
            wu.id AS work_unit_id,
            wu.name AS work_unit_name,
            wu.code AS work_unit_code,
            bu.id AS uom_id,
            bu.name AS uom_name,
            bu.code AS uom_code,
            COALESCE(avg(3600000.0 / NULLIF(COALESCE(pws.ideal_rate_per_hour, p.ideal_rate_per_hour), 0::numeric)), 1000.0) AS cycle_time_ms
           FROM ms_core.work_units wu
             LEFT JOIN ms_core.work_centers wc ON wc.id = wu.work_center_id
             LEFT JOIN ms_core.equipments eq ON eq.work_unit_id = wu.id
             LEFT JOIN ms_core.count_points cp ON cp.equipment_id = eq.id
             LEFT JOIN ms_core.products p ON p.id = 2
             LEFT JOIN ms_core.uom bu ON bu.id = p.base_uom_id
             LEFT JOIN ms_core.product_work_unit_specs pws ON pws.wor_unit_id = wu.id AND pws.product_id = p.id
          GROUP BY wc.id, wc.name, wc.code, wu.id, wu.name, wu.code, bu.id, bu.name, bu.code
        ), production_agg AS (
         SELECT pe.work_unit_id,
            COALESCE(sum(pe.cleaned_value * COALESCE(pkg.factor_to_base, 1.0)) FILTER (WHERE pe.role::text = 'infeed'::text), 0::numeric)::numeric(15,3) AS total_out,
            COALESCE(sum(pe.cleaned_value * COALESCE(pkg.factor_to_base, 1.0)) FILTER (WHERE pe.role::text = 'good_output'::text), 0::numeric)::numeric(15,3) AS good_out,
            COALESCE(sum(pe.cleaned_value * COALESCE(pkg.factor_to_base, 1.0)) FILTER (WHERE pe.role::text = 'reject'::text), 0::numeric)::numeric(15,3) AS reject,
            COALESCE(sum(pe.cleaned_value * COALESCE(pkg.factor_to_base, 1.0)) FILTER (WHERE pe.role::text = 'rework'::text), 0::numeric)::numeric(15,3) AS rework
           FROM ms_events.production_events pe
             CROSS JOIN day_boundary b
             LEFT JOIN ms_core.count_points cp ON cp.equipment_id = pe.equipment_id AND cp.source_tag::text = pe.source_tag::text
             LEFT JOIN ms_core.product_packages pkg ON pkg.product_id = pe.product_id AND pkg.uom_id = cp.uom_id
          WHERE pe.event_time >= b.day_start AND pe.event_time < b.day_end
          GROUP BY pe.work_unit_id
        ), downtime_agg AS (
         SELECT de.work_unit_id,
            COALESCE(sum(
                CASE
                    WHEN de.category::text = 'PLANNED'::text THEN EXTRACT(epoch FROM LEAST(COALESCE(de.end_at, CURRENT_TIMESTAMP), b.day_end) - GREATEST(de.start_at, b.day_start)) * 1000::numeric
                    ELSE 0::numeric
                END), 0::numeric)::bigint AS pdt_time_ms,
            COALESCE(sum(
                CASE
                    WHEN de.category::text = 'UNPLANNED'::text THEN EXTRACT(epoch FROM LEAST(COALESCE(de.end_at, CURRENT_TIMESTAMP), b.day_end) - GREATEST(de.start_at, b.day_start)) * 1000::numeric
                    ELSE 0::numeric
                END), 0::numeric)::bigint AS updt_time_ms,
            COALESCE(sum(
                CASE
                    WHEN de.category::text = 'SMALL_STOP'::text THEN EXTRACT(epoch FROM LEAST(COALESCE(de.end_at, CURRENT_TIMESTAMP), b.day_end) - GREATEST(de.start_at, b.day_start)) * 1000::numeric
                    ELSE 0::numeric
                END), 0::numeric)::bigint AS ms_time_ms
           FROM ms_events.work_unit_downtime_events de
             CROSS JOIN day_boundary b
          WHERE de.start_at < b.day_end AND COALESCE(de.end_at, CURRENT_TIMESTAMP) > b.day_start
          GROUP BY de.work_unit_id
        ), raw_metrics AS (
         SELECT b.prod_date AS date,
            w.work_center_id,
            w.work_center_name,
            w.work_center_code,
            w.work_unit_id,
            w.work_unit_name,
            w.work_unit_code,
            w.uom_id,
            w.uom_name,
            w.uom_code,
            w.cycle_time_ms,
            COALESCE(p.total_out, 0::numeric) AS total_out,
            COALESCE(p.good_out, 0::numeric) AS good_out,
            COALESCE(p.reject, 0::numeric) AS reject,
            COALESCE(p.rework, 0::numeric) AS rework,
            b.total_availability_ms AS availability_time,
            COALESCE(d.pdt_time_ms, 0::bigint) AS pdt_time,
            GREATEST(0::bigint, b.total_availability_ms - COALESCE(d.pdt_time_ms, 0::bigint)) AS production_time,
            COALESCE(d.updt_time_ms, 0::bigint) AS updt_time,
            GREATEST(0::bigint, b.total_availability_ms - COALESCE(d.pdt_time_ms, 0::bigint) - COALESCE(d.updt_time_ms, 0::bigint)) AS operating_time,
            COALESCE(d.ms_time_ms, 0::bigint) AS ms_time,
            GREATEST(0::bigint, b.total_availability_ms - COALESCE(d.pdt_time_ms, 0::bigint) - COALESCE(d.updt_time_ms, 0::bigint) - COALESCE(d.ms_time_ms, 0::bigint)) AS runtime
           FROM work_unit_master w
             CROSS JOIN day_boundary b
             LEFT JOIN production_agg p ON p.work_unit_id = w.work_unit_id
             LEFT JOIN downtime_agg d ON d.work_unit_id = w.work_unit_id
        ), derived_times AS (
         SELECT m.date,
            m.work_center_id,
            m.work_center_name,
            m.work_center_code,
            m.work_unit_id,
            m.work_unit_name,
            m.work_unit_code,
            m.uom_id,
            m.uom_name,
            m.uom_code,
            m.cycle_time_ms,
            m.total_out,
            m.good_out,
            m.reject,
            m.rework,
            m.availability_time,
            m.pdt_time,
            m.production_time,
            m.updt_time,
            m.operating_time,
            m.ms_time,
            m.runtime,
            (m.runtime::numeric / NULLIF(m.cycle_time_ms, 0::numeric))::numeric(15,3) AS effective_out,
            GREATEST(0::numeric, (m.runtime::numeric / NULLIF(m.cycle_time_ms, 0::numeric) - m.total_out) * m.cycle_time_ms)::bigint AS reduce_speed_time,
            GREATEST(0::numeric, (m.total_out - m.runtime::numeric / NULLIF(m.cycle_time_ms, 0::numeric)) * m.cycle_time_ms)::bigint AS up_speed_time,
            (m.reject * m.cycle_time_ms)::bigint AS reject_time,
            (m.rework * m.cycle_time_ms)::bigint AS rework_time,
            GREATEST(0::numeric, (m.total_out - m.good_out - m.reject) * m.cycle_time_ms)::bigint AS anomaly_time
           FROM raw_metrics m
        ), final_times AS (
         SELECT t.date,
            t.work_center_id,
            t.work_center_name,
            t.work_center_code,
            t.work_unit_id,
            t.work_unit_name,
            t.work_unit_code,
            t.uom_id,
            t.uom_name,
            t.uom_code,
            t.cycle_time_ms,
            t.total_out,
            t.good_out,
            t.reject,
            t.rework,
            t.availability_time,
            t.pdt_time,
            t.production_time,
            t.updt_time,
            t.operating_time,
            t.ms_time,
            t.runtime,
            t.effective_out,
            t.reduce_speed_time,
            t.up_speed_time,
            t.reject_time,
            t.rework_time,
            t.anomaly_time,
            GREATEST(0::bigint, t.operating_time - (t.reduce_speed_time + t.ms_time)) AS net_time,
            GREATEST(0::bigint, t.operating_time - (t.reduce_speed_time + t.ms_time) - t.reject_time - t.rework_time) AS value_added_time,
            t.reduce_speed_time + t.ms_time AS performance_loss_time
           FROM derived_times t
        )
 SELECT date,
    work_center_id,
    work_center_name,
    work_center_code,
    work_unit_id,
    work_unit_name,
    work_unit_code,
    uom_id,
    uom_name,
    uom_code,
    COALESCE(round(COALESCE(operating_time::numeric / NULLIF(production_time, 0)::numeric, 0::numeric) * COALESCE(net_time::numeric / NULLIF(operating_time, 0)::numeric, 0::numeric) * COALESCE(value_added_time::numeric / NULLIF(net_time, 0)::numeric, 0::numeric) * COALESCE(production_time::numeric / NULLIF(availability_time, 0)::numeric, 0::numeric) * 100.0, 3), 0.000) AS teep,
    COALESCE(round(COALESCE(operating_time::numeric / NULLIF(production_time, 0)::numeric, 0::numeric) * COALESCE(net_time::numeric / NULLIF(operating_time, 0)::numeric, 0::numeric) * COALESCE(value_added_time::numeric / NULLIF(net_time, 0)::numeric, 0::numeric) * 100.0, 3), 0.000) AS oee,
    COALESCE(round(operating_time::numeric / NULLIF(production_time, 0)::numeric * 100.0, 3), 0.000) AS availability,
    COALESCE(round(net_time::numeric / NULLIF(operating_time, 0)::numeric * 100.0, 3), 0.000) AS performance,
    COALESCE(round(value_added_time::numeric / NULLIF(net_time, 0)::numeric * 100.0, 3), 0.000) AS quality,
    total_out,
    good_out,
    effective_out,
    runtime,
    availability_time,
    production_time,
    pdt_time,
    operating_time,
    updt_time,
    net_time,
    reduce_speed_time,
    ms_time,
    value_added_time,
    rework_time,
    reject_time,
    anomaly_time,
    up_speed_time,
    pdt_time AS schedule_loss_time,
    updt_time AS availability_loss_time,
    performance_loss_time,
    ms_time AS ms_loss_time,
    reduce_speed_time AS reduce_speed_loss_time,
    reject_time AS quality_loss_time,
    reject,
    rework
   FROM final_times f;


and this is for work_center:
-- ms_events.vw_work_center_day_report source

CREATE OR REPLACE VIEW ms_events.vw_work_center_day_report
AS WITH last_work_units AS (
         SELECT DISTINCT ON (wc.id) wc.id AS work_center_id,
            COALESCE(fl.to_work_unit_id, wu.id) AS last_work_unit_id
           FROM ms_core.work_centers wc
             JOIN ms_core.work_units wu ON wu.work_center_id = wc.id
             LEFT JOIN ms_core.work_unit_flows fl ON fl.work_center_id = wc.id
          WHERE NOT (EXISTS ( SELECT 1
                   FROM ms_core.work_unit_flows f2
                  WHERE f2.work_center_id = wc.id AND f2.from_work_unit_id = COALESCE(fl.to_work_unit_id, wu.id)))
          ORDER BY wc.id, wu.id DESC
        ), line_summary AS (
         SELECT u.date,
            u.work_center_id,
            u.work_center_name,
            u.work_center_code,
            u.uom_id,
            u.uom_name,
            u.uom_code,
            sum(u.total_out) AS total_out,
            COALESCE(sum(
                CASE
                    WHEN u.work_unit_id = l.last_work_unit_id THEN u.good_out
                    ELSE 0::numeric
                END), 0::numeric) AS good_out,
            sum(u.reject) AS reject,
            sum(u.rework) AS rework,
            sum(u.effective_out) AS effective_out,
            sum(u.availability_time) AS availability_time,
            sum(u.pdt_time) AS pdt_time,
            sum(u.production_time) AS production_time,
            sum(u.updt_time) AS updt_time,
            sum(u.operating_time) AS operating_time,
            sum(u.ms_time) AS ms_time,
            sum(u.runtime) AS runtime,
            sum(u.reduce_speed_time) AS reduce_speed_time,
            sum(u.up_speed_time) AS up_speed_time,
            sum(u.net_time) AS net_time,
            sum(u.value_added_time) AS value_added_time,
            sum(u.reject_time) AS reject_time,
            sum(u.rework_time) AS rework_time,
            sum(u.anomaly_time) AS anomaly_time,
            sum(u.schedule_loss_time) AS schedule_loss_time,
            sum(u.availability_loss_time) AS availability_loss_time,
            sum(u.performance_loss_time) AS performance_loss_time,
            sum(u.ms_loss_time) AS ms_loss_time,
            sum(u.reduce_speed_loss_time) AS reduce_speed_loss_time,
            sum(u.quality_loss_time) AS quality_loss_time
           FROM ms_events.vw_work_unit_day_report u
             LEFT JOIN last_work_units l ON l.work_center_id = u.work_center_id
          GROUP BY u.date, u.work_center_id, u.work_center_name, u.work_center_code, u.uom_id, u.uom_name, u.uom_code
        )
 SELECT date,
    work_center_id,
    work_center_name,
    work_center_code,
    uom_id,
    uom_name,
    uom_code,
    COALESCE(round(COALESCE(operating_time / NULLIF(production_time, 0::numeric), 0::numeric) * COALESCE(net_time / NULLIF(operating_time, 0::numeric), 0::numeric) * COALESCE(value_added_time / NULLIF(net_time, 0::numeric), 0::numeric) * COALESCE(production_time / NULLIF(availability_time, 0::numeric), 0::numeric) * 100.0, 3), 0.000) AS teep,
    COALESCE(round(COALESCE(operating_time / NULLIF(production_time, 0::numeric), 0::numeric) * COALESCE(net_time / NULLIF(operating_time, 0::numeric), 0::numeric) * COALESCE(value_added_time / NULLIF(net_time, 0::numeric), 0::numeric) * 100.0, 3), 0.000) AS oee,
    COALESCE(round(operating_time / NULLIF(production_time, 0::numeric) * 100.0, 3), 0.000) AS availability,
    COALESCE(round(net_time / NULLIF(operating_time, 0::numeric) * 100.0, 3), 0.000) AS performance,
    COALESCE(round(value_added_time / NULLIF(net_time, 0::numeric) * 100.0, 3), 0.000) AS quality,
    total_out,
    good_out,
    effective_out,
    runtime,
    availability_time,
    production_time,
    pdt_time,
    operating_time,
    updt_time,
    net_time,
    reduce_speed_time,
    ms_time,
    value_added_time,
    rework_time,
    reject_time,
    anomaly_time,
    up_speed_time,
    schedule_loss_time,
    availability_loss_time,
    performance_loss_time,
    ms_loss_time,
    reduce_speed_loss_time,
    quality_loss_time,
    reject,
    rework
   FROM line_summary s;


Use this as context, also importany that equipment is now called asset not equipment anymore!

Next is for chart
![[performance_analytics.png]]
-- ms_events.product_perf_wu_hourly_today source

CREATE OR REPLACE VIEW ms_events.product_perf_wu_hourly_today
AS WITH day_boundary AS (
         SELECT date((CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Jakarta'::text) - '07:00:00'::interval) AS prod_date,
            ((date((CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Jakarta'::text) - '07:00:00'::interval) + '07:00:00'::time without time zone) AT TIME ZONE 'Asia/Jakarta'::text) AS day_start,
            ((date((CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Jakarta'::text) - '07:00:00'::interval) + '07:00:00'::time without time zone) AT TIME ZONE 'Asia/Jakarta'::text) + '1 day'::interval AS day_end
        ), hourly_buckets AS (
         SELECT b.prod_date AS business_date,
            gs.gs AS bucket_start,
            gs.gs + '01:00:00'::interval AS bucket_end,
            EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text))::smallint AS hour_of_day,
                CASE
                    WHEN EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text)) >= 7::numeric AND EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text)) <= 14::numeric THEN 1
                    WHEN EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text)) >= 15::numeric AND EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text)) <= 22::numeric THEN 2
                    ELSE 3
                END::smallint AS shift,
                CASE
                    WHEN EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text)) >= 7::numeric AND EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text)) <= 14::numeric THEN (EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text)) - 7::numeric)::smallint
                    WHEN EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text)) >= 15::numeric AND EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text)) <= 22::numeric THEN (EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text)) - 15::numeric)::smallint
                    WHEN EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text)) >= 23::numeric THEN 0::smallint
                    ELSE (EXTRACT(hour FROM (gs.gs AT TIME ZONE 'Asia/Jakarta'::text)) + 1::numeric)::smallint
                END AS shift_hour_seq
           FROM day_boundary b
             CROSS JOIN LATERAL generate_series(b.day_start, b.day_start + '23:00:00'::interval, '01:00:00'::interval) gs(gs)
        ), master_hierarchy AS (
         SELECT wu.id AS work_unit_id,
            wu.name AS work_unit_name,
            wu.code AS work_unit_code,
            wc.id AS work_center_id,
            wc.name AS work_center_name,
            wc.code AS work_center_code,
            a.id AS area_id,
            a.name AS area_name,
            s.id AS factory_id,
            s.name AS factory_name,
            p.id AS product_id,
            p.name AS product_name,
            p.code AS product_code,
            COALESCE(su.id, bu.id) AS uom_id,
            COALESCE(su.name, bu.name) AS uom_name,
            COALESCE(su.code, bu.code) AS uom_code,
            bu.id AS base_uom_id,
            bu.name AS base_uom_name,
            bu.code AS base_uom_code,
            COALESCE(pkg.factor_to_base, 1.0) AS factor_to_base,
            COALESCE(pws.ideal_rate_per_hour, p.ideal_rate_per_hour, 3600.0) AS ideal_rate_per_hour,
                CASE
                    WHEN pws.ideal_rate_per_hour IS NOT NULL OR p.ideal_rate_per_hour IS NOT NULL THEN 'master-data'::text
                    ELSE 'fallback'::text
                END AS ideal_rate_source,
            pws.created_at::date AS ideal_rate_valid_from
           FROM ms_core.work_units wu
             JOIN ms_core.work_centers wc ON wc.id = wu.work_center_id
             LEFT JOIN ms_core.areas a ON a.id = wc.area_id
             LEFT JOIN ms_core.sites s ON s.id = a.site_id
             CROSS JOIN ms_core.products p
             JOIN ms_core.uom bu ON bu.id = p.base_uom_id
             LEFT JOIN ms_core.equipments eq ON eq.work_unit_id = wu.id
             LEFT JOIN ms_core.count_points cp ON cp.equipment_id = eq.id
             LEFT JOIN ms_core.uom su ON su.id = cp.uom_id
             LEFT JOIN ms_core.product_packages pkg ON pkg.product_id = p.id AND pkg.uom_id = cp.uom_id
             LEFT JOIN ms_core.product_work_unit_specs pws ON pws.wor_unit_id = wu.id AND pws.product_id = p.id
          GROUP BY wu.id, wu.name, wu.code, wc.id, wc.name, wc.code, a.id, a.name, s.id, s.name, p.id, p.name, p.code, su.id, su.name, su.code, bu.id, bu.name, bu.code, pkg.factor_to_base, pws.ideal_rate_per_hour, p.ideal_rate_per_hour, pws.created_at
        ), hourly_production AS (
         SELECT h.bucket_start,
            pe.work_unit_id,
            pe.product_id,
            COALESCE(sum(pe.cleaned_value * COALESCE(pkg.factor_to_base, 1.0)) FILTER (WHERE pe.role::text = 'infeed'::text), 0::numeric)::numeric(15,3) AS total_out,
            COALESCE(sum(pe.cleaned_value * COALESCE(pkg.factor_to_base, 1.0)) FILTER (WHERE pe.role::text = 'good_output'::text), 0::numeric)::numeric(15,3) AS good_out,
            COALESCE(sum(pe.cleaned_value * COALESCE(pkg.factor_to_base, 1.0)) FILTER (WHERE pe.role::text = 'reject'::text), 0::numeric)::numeric(15,3) AS reject,
            COALESCE(sum(pe.cleaned_value * COALESCE(pkg.factor_to_base, 1.0)) FILTER (WHERE pe.role::text = 'rework'::text), 0::numeric)::numeric(15,3) AS rework
           FROM hourly_buckets h
             JOIN ms_events.production_events pe ON pe.event_time >= h.bucket_start AND pe.event_time < h.bucket_end
             LEFT JOIN ms_core.count_points cp ON cp.equipment_id = pe.equipment_id AND cp.source_tag::text = pe.source_tag::text
             LEFT JOIN ms_core.product_packages pkg ON pkg.product_id = pe.product_id AND pkg.uom_id = cp.uom_id
          GROUP BY h.bucket_start, pe.work_unit_id, pe.product_id
        ), hourly_downtime AS (
         SELECT h.bucket_start,
            de.work_unit_id,
            COALESCE(sum(EXTRACT(epoch FROM LEAST(COALESCE(de.end_at, CURRENT_TIMESTAMP), h.bucket_end) - GREATEST(de.start_at, h.bucket_start)) * 1000::numeric), 0::numeric)::bigint AS downtime_ms
           FROM hourly_buckets h
             JOIN ms_events.work_unit_downtime_events de ON de.start_at < h.bucket_end AND COALESCE(de.end_at, CURRENT_TIMESTAMP) > h.bucket_start
          GROUP BY h.bucket_start, de.work_unit_id
        ), combined_metrics AS (
         SELECT h.bucket_start,
            h.bucket_end,
            m.work_unit_id,
            m.product_id,
            h.business_date,
            h.shift,
            h.hour_of_day,
            h.shift_hour_seq,
            'Asia/Jakarta'::character varying(64) AS time_zone,
            m.work_unit_name,
            m.work_unit_code,
            m.work_center_id,
            m.work_center_name,
            m.work_center_code,
            m.area_id,
            m.area_name,
            m.factory_id,
            m.factory_name,
            m.product_name,
            m.product_code,
            m.uom_id,
            m.uom_name,
            m.uom_code,
            m.base_uom_id,
            m.base_uom_name,
            m.base_uom_code,
            m.factor_to_base,
            COALESCE(p.total_out, 0.000) AS total_out,
            COALESCE(p.good_out, 0.000) AS good_out,
            COALESCE(p.reject, 0.000) AS reject,
            COALESCE(p.rework, 0.000) AS rework,
            GREATEST(0::bigint, 3600000 - COALESCE(d.downtime_ms, 0::bigint)) AS runtime,
            m.ideal_rate_per_hour,
            m.ideal_rate_source,
            m.ideal_rate_valid_from
           FROM hourly_buckets h
             CROSS JOIN master_hierarchy m
             LEFT JOIN hourly_production p ON p.bucket_start = h.bucket_start AND p.work_unit_id = m.work_unit_id AND p.product_id = m.product_id
             LEFT JOIN hourly_downtime d ON d.bucket_start = h.bucket_start AND d.work_unit_id = m.work_unit_id
        )
 SELECT bucket_start,
    bucket_end,
    work_unit_id,
    product_id,
    business_date,
    shift,
    hour_of_day,
    shift_hour_seq,
    time_zone,
    work_unit_name,
    work_unit_code,
    work_center_id,
    work_center_name,
    work_center_code,
    area_id,
    area_name,
    factory_id,
    factory_name,
    product_name,
    product_code,
    uom_id,
    uom_name,
    uom_code,
    base_uom_id,
    base_uom_name,
    base_uom_code,
    factor_to_base,
    total_out,
    good_out,
    round(ideal_rate_per_hour * (runtime::numeric / 3600000.0), 3)::numeric(15,3) AS effective_out,
    reject,
    rework,
    runtime,
    ideal_rate_per_hour,
    ideal_rate_source,
    ideal_rate_valid_from,
        CASE
            WHEN runtime > 0 THEN round(total_out / (runtime::numeric / 3600000.0), 6)::numeric(15,6)
            ELSE 0.000000
        END AS actual_rate_per_hour,
    round(ideal_rate_per_hour * 1.0, 3)::numeric(15,3) AS ideal_out,
    CURRENT_TIMESTAMP AS loaded_at
   FROM combined_metrics c
  ORDER BY work_unit_id, bucket_start, product_id;

---
**Related:** Superseded for input lookup by [[NEW_oee_or_kpi_calculation]] · new formulas in [[molcadx_app_overview]] · output [[gold_tables]]
