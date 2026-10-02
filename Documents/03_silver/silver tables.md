Create Silver Tables to Provide reports:
- Learn the ERD and then create tables for silver as bridge from raw source tag data  and gold(which is report ready). Silver is like a cleaned data that can calculate the report just by query filter sum etc to get the reports using master data references. Here delta value, or on off, etc is here
- Takes data from kafka
- Use Flink
- And store data to Silver Schema
- Create View in silver to generate all Reports/calculation, so view is for realtime from App backend.


# Silver
## production_events
|  No | Column Name    | Data Type                      | Calculation |
| --: | -------------- | ------------------------------ | ----------- |
|   1 | event_time     | timestamptz                    |             |
|   2 | equipment_id   | int4                           |             |
|   3 | equipment_code | varchar(50)                    |             |
|   4 | product_id     | product_id                     |             |
|   5 | product_code   | product_code                   |             |
|   6 | source_tag     | source_tag                     |             |
|   7 | role           | ms_events.production_role_enum |             |
|   8 | cleaned_value  | cleaned_value                  |             |
|   9 | raw_payload    | varchar(1000)                  |             |
|  10 | created_at     | created_at                     |             |
|  11 | work_unit_id   | int4                           |             |

## downtime_events
#### Option 1: work_unit based

|  No | Column Name  | Data Type          | Calculation                                                           |
| --: | ------------ | ------------------ | --------------------------------------------------------------------- |
|   1 | id           | serial4            | serial generated                                                      |
|   2 | work_unit_id | int4               | group for this work_unit_id                                           |
|   3 | category     | oee.operation_type | category of                                                           |
|   4 | reason       | jsonb              | for all downtime_reason for each equipment there is in this work_unit |
|   5 | start_at     | timestamptz        | start                                                                 |
|   6 | end_at       | timestamptz        | end                                                                   |
|   7 | duration     | int8               | duration from start and end                                           |
|   8 | actions      | Varchar(255)       | jsonb                                                                 |


### Downtime reason log for every downtime reason
|  No | Column Name  | Data Type   | Calculation                 |
| --: | ------------ | ----------- | --------------------------- |
|   1 | id           | serial4     | serial generated            |
|   2 | equipment_id | int4        | group for this work_unit_id |
|   3 | reason       | jsonb       | Name of node                |
|   4 | timestamp    | timestamptz | a reason occur              |



## reject_events

|  No | Column Name    | Data Type        | Calculation |
| --: | -------------- | ---------------- | ----------- |
|   1 | event_time     | timestamptz      |             |
|   2 | equipment_id   | int4             |             |
|   3 | equipment_code | varchar(50)      |             |
|   4 | product_id     | product_id       |             |
|   5 | product_code   | product_code     |             |
|   6 | source_tag     | source_tag       |             |
|   7 | reason         | ms_events.reject |             |
|   8 | cleaned_value  | cleaned_value    |             |
|   9 | raw_payload    | varchar(1000)    |             |
|  10 | created_at     | created_at       |             |
|  11 | work_unit_id   | int4             |             |
	

---
**Related:** Superseded by [[silver_model]] · [[timescale_cont_aggregates]] · [[erd_spec_2026-10-01]]
