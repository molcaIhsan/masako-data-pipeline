"""
Day / month / year rollups (gold_model.md, pipeline_contract.md DAG D). Pure SQL generated here, executed per period.
Rules: durations and counts are SUMMED; rates are RECOMPUTED from the sums (never averaged); MTTR/MTBF are recomputed
from the stops of the whole period (silver.fn_reliability); NULL stays NULL. Source of each grain = the gold grain
below it (day <- shift rows, month <- day rows, year <- month rows), so frozen (approved) shifts flow up unchanged.
"""
from gold.db import transaction
from gold.loader import stamp

SUM_COLS = ["total_out", "good_out", "effective_out", "runtime", "availability_time", "production_time", "pdt_time",
            "operating_time", "updt_time", "net_time", "reduce_speed_time", "ms_time", "value_added_time", "rework_time",
            "reject_time", "anomaly_time", "up_speed_time", "schedule_loss_time", "availability_loss_time",
            "performance_loss_time", "ms_loss_time", "reduce_speed_loss_time", "quality_loss_time", "reject", "rework"]
COUNT_COLS = {"total_out", "good_out", "effective_out", "reject", "rework"}   # NULL when the members' units differ

GRAIN = {   # target grain -> (source grain, period expression on the source's business_date, period length)
    "day":   ("shift", "s.business_date",                              "interval '1 day'"),
    "month": ("day",   "date_trunc('month', s.business_date)::date",   "interval '1 month'"),
    "year":  ("month", "date_trunc('year', s.business_date)::date",    "interval '1 year'"),
}
SUBJECT = {
    "wu": dict(prefix="work_unit", key="work_unit_id",
               dims=["site_id", "time_zone", "area_id", "work_center_id", "work_unit_name", "work_unit_code"],
               members="ARRAY[a.work_unit_id]"),
    "wc": dict(prefix="work_center", key="work_center_id",
               dims=["site_id", "time_zone", "area_id", "work_center_name", "work_center_code"],
               members="(SELECT array_agg(w.work_unit_id) FROM silver.work_unit w WHERE w.work_center_id = a.work_center_id)",
               extra_select="max(s.work_unit_count) AS work_unit_count", extra_cols=["work_unit_count"]),
}


def rollup_sql(subject, grain):
    sj, (src_grain, period, length) = SUBJECT[subject], GRAIN[grain]
    src, tgt = f"gold.{sj['prefix']}_{src_grain}_report", f"gold.{sj['prefix']}_{grain}_report"
    key = sj["key"]
    sums = ",\n           ".join(f"sum(s.{c}) AS {c}" for c in SUM_COLS)
    dims = ", ".join(f"max(s.{d}) AS {d}" for d in sj["dims"])
    extra = ("," + sj["extra_select"]) if "extra_select" in sj else ""
    extra_cols = sj.get("extra_cols", [])
    uom_ok = "count(DISTINCT s.uom_id) = 1 AND count(s.uom_id) = count(*) FILTER (WHERE s.total_out IS NOT NULL)"
    cols_out = []
    for c in SUM_COLS:
        cols_out.append(f"CASE WHEN a.uom_id IS NOT NULL THEN a.{c} END" if c in COUNT_COLS else f"a.{c}")
    target_cols = (["business_date"] + sj["dims"] + [key] + extra_cols +
                   ["uom_id", "uom_name", "uom_code", "teep", "oee", "availability", "performance", "quality"] +
                   SUM_COLS + ["mttr", "mtbf", "data_status", "binding_ids", "computed_at"])
    delete = f"DELETE FROM {tgt} WHERE business_date BETWEEN %(date_from)s AND %(date_to)s"
    insert = f"""
INSERT INTO {tgt} ({", ".join(target_cols)})
WITH src AS (
  SELECT {period} AS period, s.* FROM {src} s
  WHERE {period} BETWEEN %(date_from)s AND %(date_to)s
    AND s.data_status <> 'no_data'),   -- periods without readings do not dilute the sums (PRD: no data is never full availability)
a AS (
  SELECT s.period, s.{key}, {dims}{extra},
         CASE WHEN {uom_ok} THEN min(s.uom_id) END AS uom_id,
         {sums},
         sum(s.operating_time) FILTER (WHERE s.net_time IS NOT NULL) AS operating_time_with_std,
         CASE WHEN bool_and(s.data_status = 'ok') THEN 'ok'
              WHEN bool_and(s.data_status = 'no_binding') THEN 'no_binding' ELSE 'partial' END AS data_status,
         (SELECT array_agg(DISTINCT b ORDER BY b) FROM src s2, unnest(s2.binding_ids) b
           WHERE s2.period = s.period AND s2.{key} = s.{key}) AS binding_ids
  FROM src s GROUP BY s.period, s.{key}),
w AS (   -- the period's real clock window at the site (first shift start .. last shift end)
  SELECT a.period, a.{key}, min(si.start_datetime) AS w_from, max(si.end_datetime) AS w_to
  FROM a JOIN silver.v_shift si ON si.site_id = a.site_id
   AND si.business_date >= a.period AND si.business_date < (a.period + {length})::date
  GROUP BY 1, 2)
SELECT a.period, {", ".join("a." + d for d in sj["dims"])}, a.{key}{"".join(", a." + c for c in extra_cols)},
       a.uom_id, u.name, u.code,
       round(100.0 * x.av * x.pf * x.ql * a.production_time / nullif(a.availability_time, 0), 3),
       round(100.0 * x.av * x.pf * x.ql, 3), round(100.0 * x.av, 3), round(100.0 * x.pf, 3), round(100.0 * x.ql, 3),
       {", ".join(cols_out)},
       r.mttr_ms, r.mtbf_ms, a.data_status, a.binding_ids, now()
FROM a
JOIN w ON w.period = a.period AND w.{key} = a.{key}
LEFT JOIN silver.unit_of_measurements u ON u.uom_id = a.uom_id
CROSS JOIN LATERAL (SELECT a.operating_time::numeric / nullif(a.production_time, 0)   AS av,
                           a.net_time::numeric / nullif(a.operating_time_with_std, 0) AS pf,
                           a.value_added_time::numeric / nullif(a.net_time, 0)        AS ql) x
CROSS JOIN LATERAL silver.fn_reliability({sj["members"]}, w.w_from, w.w_to) r
"""
    return [delete, insert]   # psycopg 3 runs one statement per parameterised execute


PRODUCT_COUNT_DAY_SQL = ["DELETE FROM gold.product_count_day WHERE business_date BETWEEN %(date_from)s AND %(date_to)s", """
INSERT INTO gold.product_count_day
SELECT business_date, max(work_center_id), work_unit_id, max(work_unit_name), max(work_unit_code), product_id,
       max(product_name), max(product_code), max(base_uom_id), max(base_uom_name), max(base_uom_code),
       sum(total_out), sum(good_out), sum(reject), sum(rework), now()
FROM gold.product_count WHERE business_date BETWEEN %(date_from)s AND %(date_to)s
GROUP BY business_date, work_unit_id, product_id
"""]


def periods_for(business_dates):
    """Business dates -> the day / month / year period starts they touch."""
    import datetime as dt
    days = sorted({dt.date.fromisoformat(str(d)) for d in business_dates})
    months = sorted({d.replace(day=1) for d in days})
    years = sorted({d.replace(month=1, day=1) for d in days})
    return days, months, years


def run_rollups(business_dates, run_id="manual"):
    """Rebuild day, then month, then year rows touched by these business dates (order matters: each reads the one below)."""
    days, months, years = periods_for(business_dates)
    done = []
    with transaction() as cur:
        for d in days:
            p = {"date_from": d, "date_to": d}
            for subj in ("wu", "wc"):
                for stmt in rollup_sql(subj, "day"):
                    cur.execute(stmt, p)
            for stmt in PRODUCT_COUNT_DAY_SQL:
                cur.execute(stmt, p)
        for m in months:
            for subj in ("wu", "wc"):
                for stmt in rollup_sql(subj, "month"):
                    cur.execute(stmt, {"date_from": m, "date_to": m})
        for y in years:
            for subj in ("wu", "wc"):
                for stmt in rollup_sql(subj, "year"):
                    cur.execute(stmt, {"date_from": y, "date_to": y})
        if days:
            last = max(days)
            for t in ("work_unit_day_report", "work_center_day_report", "product_count_day"):
                stamp(cur, t, last, run_id)
            for t in ("work_unit_month_report", "work_center_month_report"):
                stamp(cur, t, max(months), run_id)       # month start, not month end (the row's business_date)
            for t in ("work_unit_year_report", "work_center_year_report"):
                stamp(cur, t, max(years), run_id)
        done = [str(d) for d in days]
    return {"days": done, "months": [str(m) for m in months], "years": [str(y) for y in years]}
