"""Quality checks (pipeline_contract.md §2.3). Each query returns the offending rows; empty = pass."""
ERROR_CHECKS = {
    "day_runtime_equals_sum_of_shifts": """
        SELECT d.work_unit_id, d.business_date FROM gold.work_unit_day_report d
        JOIN (SELECT work_unit_id, business_date, sum(runtime) rt, sum(availability_time) av
              FROM gold.work_unit_shift_report WHERE data_status <> 'no_data' GROUP BY 1, 2) s
          USING (work_unit_id, business_date)
        WHERE d.runtime IS DISTINCT FROM s.rt OR d.availability_time IS DISTINCT FROM s.av""",
    "month_runtime_equals_sum_of_days": """
        SELECT m.work_unit_id, m.business_date FROM gold.work_unit_month_report m
        JOIN (SELECT work_unit_id, date_trunc('month', business_date)::date AS business_date, sum(runtime) rt
              FROM gold.work_unit_day_report WHERE data_status <> 'no_data' GROUP BY 1, 2) d
          USING (work_unit_id, business_date)
        WHERE m.runtime IS DISTINCT FROM d.rt""",
    "hourly_runtime_at_most_one_hour": """
        SELECT work_unit_id, bucket_start FROM gold.product_perf_hourly
        GROUP BY 1, 2 HAVING sum(runtime) > 3600000""",
    "approved_rows_not_rewritten": """
        SELECT r.work_unit_id, r.shift_instance_id FROM gold.report_approval a
        JOIN gold.work_unit_shift_report r USING (work_unit_id, shift_instance_id)
        WHERE a.status = 'approved' AND r.computed_at > a.changed_at""",
    "closed_shifts_loaded": """
        SELECT si.shift_instance_id FROM silver.shift_instance si
        WHERE si.end_datetime < now() - interval '1 hour' AND si.end_datetime > now() - interval '7 days'
          AND EXISTS (SELECT 1 FROM silver.production_events_1m c WHERE c.shift_instance_id = si.shift_instance_id)
          AND NOT EXISTS (SELECT 1 FROM gold.work_unit_shift_report g WHERE g.shift_instance_id = si.shift_instance_id)""",
}
WARN_CHECKS = {
    "configuration_gaps": """
        SELECT data_status, count(*) FROM gold.work_unit_shift_report
        WHERE business_date > current_date - 7 AND data_status <> 'ok' GROUP BY 1""",
    "hourly_vs_product_count_gap": """
        SELECT p.work_unit_id, p.shift_instance_id, p.product_id, p.total_out AS shift_total, h.total AS hourly_total
        FROM gold.product_count p
        JOIN (SELECT work_unit_id, shift_instance_id, product_id, sum(total_out) total
              FROM gold.product_perf_hourly GROUP BY 1, 2, 3) h USING (work_unit_id, shift_instance_id, product_id)
        WHERE p.total_out IS DISTINCT FROM h.total""",
}
