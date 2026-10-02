How TimescaleDB Continuous Aggregates Achieve 100x Faster Queries
1. The Problem with Standard SQL Views on Raw Streaming Data
In our Silver hypertable (ms_events.production_events), Flink inserts thousands of counter ticks every minute.

When a user opens a dashboard or Airflow runs a shift report across 8 hours:

Standard SQL Query: Must scan 500,000 to 2,000,000 raw rows, recalculate math, evaluate CASE statements, and run SUM() across disk every single time!
Result: High CPU usage, 3–10 second query times, and database bottlenecks during peak hours.


[ Traditional View ]
 User Query ──► Scans 2,000,000 Raw Rows on Disk ──► Computes Math from scratch ──► Returns in 5s (Slow 🐢)
2. The Solution: TimescaleDB Continuous Aggregates (Real-Time Materialized Views)
A Continuous Aggregate pre-calculates and pre-materializes time-bucketed summaries (e.g. 1-minute or 1-hour buckets) incrementally in the background.



[ TimescaleDB Continuous Aggregate ]
 Flink Stream ──► Raw Hypertables (Raw Events)
                        │
                        ▼ (Background Worker incrementally pre-aggregates 1-minute buckets)
                 Continuous Aggregate Table (Pre-calculated SUMs & Counts)
                        │
 User Query ────────────┴────────► Scans ONLY 480 Pre-computed Rows ──► Returns in 5ms (Instant ⚡)
Instead of scanning 2,000,000 raw rows, the database scans 480 pre-aggregated 1-minute rows!

3. The 3 Superpowers of Continuous Aggregates
Superpower A: Real-Time Aggregate (Hybrid Querying)
TimescaleDB combines two data sources automatically without you needing to write extra SQL:

Materialized Historical Data: Scans pre-calculated buckets up to the last refresh.
Live Unmaterialized Tail: Scans only the few raw records written in the last few seconds!
Query Result
=
Pre-Calculated Materialized Buckets
⏟
99.9% of data (Ultra-Fast)
+
Live Raw Stream Data
⏟
Last few seconds (Few rows)
Query Result= 
99.9% of data (Ultra-Fast)
Pre-Calculated Materialized Buckets
​
 
​
 + 
Last few seconds (Few rows)
Live Raw Stream Data
​
 
​
 
Superpower B: Incremental Background Refresh
TimescaleDB NEVER recalculates the entire table from scratch (unlike PostgreSQL's REFRESH MATERIALIZED VIEW which locks the table).
It tracks watermark modifications and refreshes only newly arrived chunks using a lightweight background worker policy (add_continuous_aggregate_policy).
Superpower C: Native Columnar Compression on Aggregates
You can compress the continuous aggregate table itself!
Shrinks 1-minute pre-aggregated historical data by 90%, keeping months of analytics in RAM cache.
4. Real-World DDL Example for Production OEE
Here is how we define a 1-minute pre-aggregated Continuous Aggregate for ms_events.production_events:

sql


-- 1. Create 1-Minute Continuous Aggregate Hypertable
CREATE MATERIALIZED VIEW ms_events.production_events_1m_cagg
WITH (timescaledb.continuous) AS
SELECT 
    time_bucket('1 minute', event_time) AS bucket_1m,
    work_unit_id,
    equipment_id,
    product_id,
    role,
    SUM(cleaned_value) AS total_quantity,
    COUNT(*)           AS event_count
FROM ms_events.production_events
GROUP BY 
    time_bucket('1 minute', event_time),
    work_unit_id,
    equipment_id,
    product_id,
    role
WITH NO DATA;
-- 2. Add Background Refresh Policy (Runs automatically every 1 minute)
SELECT add_continuous_aggregate_policy('ms_events.production_events_1m_cagg',
    start_offset => INTERVAL '2 hours', -- Refresh window back to 2 hours (catches late arrivals)
    end_offset   => INTERVAL '1 minute',-- Don't lock current second
    schedule_interval => INTERVAL '1 minute'
);
5. Performance Comparison
Metric	Raw Table Query (production_events)	Continuous Aggregate (production_events_1m_cagg)
Rows Scanned (8h Shift)	~2,000,000 rows	480 rows
Query Latency	2,500 ms – 6,000 ms	3 ms – 15 ms
CPU & Disk I/O	100% Core Spike	< 1% I/O load
Cold-Start Dashboard Load	Noticeable lag / spinning loader	Instant instantaneous render
Summary
Continuous Aggregates give you the speed of pre-computed summary tables with the freshness of a live real-time stream!

example queries done:
-- ms_events.production_1m_cont_agg source

CREATE OR REPLACE VIEW ms_events.production_1m_cont_agg
AS SELECT bucket_time,
    equipment_id,
    equipment_code,
    good_output_qty,
    infeed_qty,
    reject_qty
   FROM _timescaledb_internal._materialized_hypertable_19;



-- ms_events.downtime_1m_cont_agg source

CREATE OR REPLACE VIEW ms_events.downtime_1m_cont_agg
AS SELECT bucket_time,
    equipment_id,
    equipment_code,
    planned_sec,
    unplanned_sec,
    small_stop_sec
   FROM _timescaledb_internal._materialized_hypertable_20;

---
**Related:** [[silver_model]] · [[silver tables]] · [[gold_tables]]
