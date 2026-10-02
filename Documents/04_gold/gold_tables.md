
# Gold

![[Mana yg sum manaa yg recalc 2.png]]

*For Work Center
<mark style="background: #ADCCFFA6;">Biru</mark> =  Recalculate
<mark style="background: #FF5582A6;">Merah</mark> = Sum from all work_unit
<mark style="background: #D2B3FFA6;">Ungu </mark>= Unique retrieve ( Might Sum Up)
## Work Unit
### work unit shift reports

|     No | Column Name                | Data Type        | Calculation / Rules                                                                                                            |
| -----: | -------------------------- | ---------------- | ------------------------------------------------------------------------------------------------------------------------------ |
|      1 | date                       | date             | a day start at 7 AM to tomorrow 7 am, so date 4 Agustus 2026 can have shift 3 that takes data from 23-7AM(tomorrow)            |
|      2 | shift                      | varchar(100)     | We group the calculation (like sum, etc) based on shift for 7-15-23 (1,2,3)                                                    |
|      3 | work_unit_id               | int4             | We group the calculation based on work_unit                                                                                    |
|      4 | work_unit_name             | varchar(255)     | join to find its work_unit_name                                                                                                |
|      5 | work_unit_code             | varchar(255)     | join to find its work_unit_code                                                                                                |
|  ==6== | ==uom_id==                 | ==int4==         | ==uom_id for the base uom==                                                                                                    |
|  ==7== | ==uom_name==               | ==varchar(255)== | ==uom_name==                                                                                                                   |
|  ==8== | ==uom_code==               | ==varchar(255)== | ==uom_code==                                                                                                                   |
|      9 | teep                       | numeric(10,3)    | OEE x Utility                                                                                                                  |
|     10 | oee                        | numeric(10,3)    | availability(11) x performance(12) x quality(13)                                                                               |
|     11 | availability               | numeric(10,3)    | Operating Time(21) / production_time(19)                                                                                       |
|     12 | performance                | numeric(10,3)    | net_time(23) / operating_time(21)                                                                                              |
|     13 | quality                    | numeric(10,3)    | value_added_time(26) / net_time(23)                                                                                            |
|     14 | total_out                  | numeric(15,3)    | total_out using base uom                                                                                                       |
|     15 | good_out                   | numeric(15,3)    | good_out using base uom                                                                                                        |
|     16 | effective_out              | numeric(15,3)    | cycle_time x (operating_time (21) -small_stop(ms_time))                                                                        |
|     17 | runtime                    | int8             | SUM of duration when its not small stop or unplanned or planned or availability_time(18) - (small_stop + unplanned  + planned) |
|     18 | availability_time          | int8             | Full hour of shift                                                                                                             |
|     19 | production_time            | int8             | availability_time(18) -pdt_time(20)                                                                                            |
|     20 | pdt_time                   | int8             | Sum of planned downtime duration                                                                                               |
|     21 | operating_time             | int8             | production_time(19) - updt_time(22)                                                                                            |
|     22 | updt_time                  | int8             | Sum of unplanned downtime duration                                                                                             |
|     23 | net_time                   | int8             | operating_time(21) - (reduce_speed_time(24) + ms_time(25))                                                                     |
|     24 | reduce_speed_time          | int8             | (effective_out(16) - Sum of Actual Production) / cycle_time                                                                    |
|     25 | ms_time                    | int8             | Sum of Small Stop duration                                                                                                     |
|     26 | value_added_time           | int8             | net_time(23) - (reject_time(27)/ cycle_time)                                                                                   |
|     27 | rework_time                | int8             | rework(38) / cycle_time                                                                                                        |
|     28 | reject_time                | int8             | Reject(37) / cycle_time                                                                                                        |
|     29 | anomaly_time               | int8             | Sum of Anomaly / cycle_time. Anomaly = Actual Prod - Finish Good) - Sum of Reject                                              |
|     30 | up_speed_time              | int8             | reverse reduce_speed                                                                                                           |
|     31 | schedule_loss_time         | int8             | SAME as pdt_time(20)                                                                                                           |
|     32 | availability_loss_time     | int8             | SAME as updt_time(22)                                                                                                          |
|     33 | performance_loss_time      | int8             | reduce_speed_time(24) + ms_loss_time(34)                                                                                       |
| ==34== | ==ms_loss_time==           | ==int8==         | ==SAME as ms_time==                                                                                                            |
| ==35== | ==reduce_speed_loss_time== | ==int8==         | ==SAME as reduce_speed_time(24)==                                                                                              |
| ==36== | ==quality_loss_time==      | ==int8==         | ==SAME as reject_time(28)==                                                                                                    |
|     37 | reject                     | numeric(15,3)    | Sum of Reject                                                                                                                  |
|     38 | rework                     | numeric(15,3)    | Sum of Rework -- Empty for now                                                                                                 |
### work unit day reports

|     No | Column Name                | Data Type        | Calculation / Rules                                                                                                            |
| -----: | -------------------------- | ---------------- | ------------------------------------------------------------------------------------------------------------------------------ |
|      1 | id                         | serial4          |                                                                                                                                |
|      2 | date                       | date             | a day start at 7 AM to tomorrow 7 am, so date 4 Agustus 2026 can have shift 3 that takes data from 23-7AM(tomorrow)            |
|      3 | work_unit_id               | int4             | We group the calculation based on work_unit                                                                                    |
|      4 | work_unit_name             | varchar(255)     | join to find its work_unit_name                                                                                                |
|      5 | work_unit_code             | varchar(255)     | join to find its work_unit_code                                                                                                |
|  ==6== | ==uom_id==                 | ==int4==         | ==uom_id for the base uom==                                                                                                    |
|  ==7== | ==uom_name==               | ==varchar(255)== | ==uom_name==                                                                                                                   |
|  ==8== | ==uom_code==               | ==varchar(255)== | ==uom_code==                                                                                                                   |
|      9 | teep                       | numeric(10,3)    | OEE x Utility                                                                                                                  |
|     10 | oee                        | numeric(10,3)    | availability(11) x performance(12) x quality(13)                                                                               |
|     11 | availability               | numeric(10,3)    | Operating Time(21) / production_time(19)                                                                                       |
|     12 | performance                | numeric(10,3)    | net_time(23) / operating_time(21)                                                                                              |
|     13 | quality                    | numeric(10,3)    | value_added_time(26) / net_time(23)                                                                                            |
|     14 | total_out                  | numeric(15,3)    | total_out using base uom                                                                                                       |
|     15 | good_out                   | numeric(15,3)    | good_out using base uom                                                                                                        |
|     16 | effective_out              | numeric(15,3)    | cycle_time x (operating_time (21) -small_stop(ms_time))                                                                        |
|     17 | runtime                    | int8             | SUM of duration when its not small stop or unplanned or planned or availability_time(18) - (small_stop + unplanned  + planned) |
|     18 | availability_time          | int8             | Full hour of shift                                                                                                             |
|     19 | production_time            | int8             | availability_time(18) -pdt_time(20)                                                                                            |
|     20 | pdt_time                   | int8             | Sum of planned downtime duration                                                                                               |
|     21 | operating_time             | int8             | production_time(19) - updt_time(22)                                                                                            |
|     22 | updt_time                  | int8             | Sum of unplanned downtime duration                                                                                             |
|     23 | net_time                   | int8             | operating_time(21) - (reduce_speed_time(24) + ms_time(25))                                                                     |
|     24 | reduce_speed_time          | int8             | (effective_out(16) - Sum of Actual Production) / cycle_time                                                                    |
|     25 | ms_time                    | int8             | Sum of Small Stop duration                                                                                                     |
|     26 | value_added_time           | int8             | net_time(23) - (reject_time(27)/ cycle_time)                                                                                   |
|     27 | rework_time                | int8             | rework(38) / cycle_time                                                                                                        |
|     28 | reject_time                | int8             | Reject(37) / cycle_time                                                                                                        |
|     29 | anomaly_time               | int8             | Sum of Anomaly / cycle_time. Anomaly = Actual Prod - Finish Good) - Sum of Reject                                              |
|     30 | up_speed_time              | int8             | reverse reduce_speed                                                                                                           |
|     31 | schedule_loss_time         | int8             | SAME as pdt_time(20)                                                                                                           |
|     32 | availability_loss_time     | int8             | SAME as updt_time(22)                                                                                                          |
|     33 | performance_loss_time      | int8             | reduce_speed_time(24) + ms_loss_time(34)                                                                                       |
| ==34== | ==ms_loss_time==           | ==int8==         | ==SAME as ms_time==                                                                                                            |
| ==35== | ==reduce_speed_loss_time== | ==int8==         | ==SAME as reduce_speed_time(24)==                                                                                              |
| ==36== | ==quality_loss_time==      | ==int8==         | ==SAME as reject_time(28)==                                                                                                    |
|     37 | reject                     | numeric(15,3)    | Sum of Reject                                                                                                                  |
|     38 | rework                     | numeric(15,3)    | Sum of Rework -- Empty for now                                                                                                 |

### Work Unit Perf Analytics
![[Screenshot 2026-08-04 at 10.53.30.png]]




| **No** | **Column Name**         | **Data Type** | **Calculation / Description**                                                                                       |
| ------ | ----------------------- | ------------- | ------------------------------------------------------------------------------------------------------------------- |
| 1      | `id`                    | serial        | Surrogate Primary Key                                                                                               |
| 2      | `report_date`           | date          | Report date (aggregation date)                                                                                      |
| 3      | `work_unit_id`          | int4          | FK → Work Unit                                                                                                      |
| 4      | `work_unit_code`        | varchar(100)  | inherit work_unit_code                                                                                              |
| 5      | `work_unit_name`        | varchar(255)  | inherit work_unit_name                                                                                              |
| 6      | `uom_id`                | int4          | FK → Unit of Measure                                                                                                |
| 7      | `uom_code`              | varchar(50)   | inherited                                                                                                           |
| 8      | `uom_name`              | varchar(100)  | inherited                                                                                                           |
| 9      | `avg_cycle_time`        | numeric(12,3) | `AVG(actual_rate_per_hour)` of all hourly windows. Must consider the averaging because of multiple product          |
| 10     | `avg_production_output` | numeric(12,3) | `AVG(total_out)` of all hourly windows                                                                              |
| 11     | `chart`                 | jsonb         | All Hourly visualizations payload grouped by 1-hour windows                                                         |
| 12     | `created_at`            | timestamptz   | `CURRENT_TIMESTAMP`                                                                                                 |
| 13     | `day_date`              | date bucket   | a day start at 7 AM to tomorrow 7 am, so date 4 Agustus 2026 can have shift 3 that takes data from 23-7AM(tomorrow) |
| 14     | `shift`                 | int4          | We group the calculation (like sum, etc) based on shift for 7-15-23 (1,2,3)                                         |

#### JSON Structure (`chart`)

The `chart` column stores all data required by the frontend to render the hourly line chart.

```
Work Unit
    ↓
Hourly Windows
        ↓
Products
        ↓
Metrics
```
#### Root Object

|Depth|Field|Data Type|Description|
|---|---|---|---|
|0|windows|json array|Collection of 1-hour reporting windows|

---
#### Window Object

Each object represents **one hourly aggregation bucket**.

|Depth|Field|Data Type|Description|
|---|---|---|---|
|1|start_time|timestamptz|Start of 1-hour window|
|1|end_time|timestamptz|End of 1-hour window|
|1|products|json array|Every product produced during the hour|
#### Product Object

Each object represents **one product within a specific hourly window**.

|Depth|Field|Data Type|Description|
|---|---|---|---|
|2|product_id|int4|Product Identifier|
|2|product_name|varchar|Product Name|
|2|metrics|json object|Collection of measurable values|
#### Metrics Object

All business measurements are stored together.

| Depth | Field              | Data Type | Calculation                                                   |
| ----- | ------------------ | --------- | ------------------------------------------------------------- |
| 3     | total_out          | numeric   | Total production within the hour                              |
| 3     | effective_out      | numeric   | Ideal. Cycle time x runtime                                   |
| 3     | runtime            | bigint    | Runtime in seconds                                            |
| 3     | actual_cycle_time  | numeric   | `total_out / (runtime / 3600)`                                |
| 3     | master_cycle_time  | numeric   | Master production rate                                        |
| 3     | _(future metrics)_ | numeric   | Reject, Rework, OEE, Availability, Performance, Quality, etc. |
### downtime reports
|  No | Column Name  | Data Type          | Calculation                                                           |
| --: | ------------ | ------------------ | --------------------------------------------------------------------- |
|   1 | id           | serial4            | serial generated                                                      |
|   2 | reason       | jsonb              | for all downtime_reason for each equipment there is in this work_unit |
|   3 | category     | oee.operation_type | category of                                                           |
|   4 | work_unit_id | int4               | group for this work_unit_id                                           |
|   5 | shift        | varchar(100)       | grouped via this shift                                                |
|   6 | date         | timestamptz        | date                                                                  |
|   7 | actions      |                    |                                                                       |
|   8 | start_at     | timestamptz        | start                                                                 |
|   9 | end_at       | timestamptz        | end                                                                   |
|  10 | duration     | int8               | duration from start and end                                           |
|     |              |                    |                                                                       |

### product_counts

|  No | Column Name    | Data Type     | Calculation                                                                                 |
| --: | -------------- | ------------- | ------------------------------------------------------------------------------------------- |
|   1 | id             | serial4       |                                                                                             |
|   2 | product_id     | int4          | grouped for this product_id, and it might be multiple product_id in spread  in work_unit_id |
|   3 | work_center_id | int4          | just knowing for this work_center_id, so it might multiple work_unit                        |
|   4 | work_unit_id   | int4          | grouped for this work_unit                                                                  |
|   5 | product_name   | varchar(255)  | derived                                                                                     |
|   6 | product_code   | varchar(255)  | derived                                                                                     |
|   7 | total_out      | numeric(15,3) | Sum of all total_out for this product_id, work_unit_id                                      |
|   8 | good_out       | numeric(15,3) | Sum of all good_out for this product_id, work_unit_id                                       |
|   9 | reject         | numeric(15,3) | Sum of all reject for this product_id, work_unit_id                                         |
|  10 | rework         | numeric(15,3) | Sum of all rework for this product_id, work_unit_id                                         |
|  11 | date           | timestamptz   | date                                                                                        |
|  12 | shift          | varchar(100)  | grouped via this shift                                                                      |
|  13 | base_uom_id    |               |                                                                                             |
|  14 | base_uom_name  |               |                                                                                             |
|  15 | base_uom_code  |               |                                                                                             |
|  16 | work_unit_name |               |                                                                                             |
|  17 | work_unit_code |               |                                                                                             |
|  18 | product_id     |               |                                                                                             |
|     | factor_to_base |               |                                                                                             |

alternative -> 1 row per work_unit_id

|  No | Column Name    | Data Type    | Calculation                                                      |
| --: | -------------- | ------------ | ---------------------------------------------------------------- |
|   1 | id             | serial4      |                                                                  |
|   2 | work_center_id | int4         | Parent work center of this work unit                             |
|   3 | work_unit_id   | int4         | **Primary grouping key (1 row per work_unit_id)**                |
|   4 | products       | jsonb        | JSON array containing aggregated metrics grouped by `product_id` |
|   5 | date           | timestamptz  | Report date                                                      |
|   6 | shift          | varchar(100) | Grouped by shift                                                 |
[
  {
    "product_id": 101,
    "product_name": "Product A",
    "product_code": "PA001",
    "total_out": 1200.000,
    "good_out": 1180.000,
    "reject": 15.000,
    "rework": 5.000
  },
  {
    "product_id": 102,
    "product_name": "Product B",
    "product_code": "PB002",
    "total_out": 850.000,
    "good_out": 830.000,
    "reject": 18.000,
    "rework": 2.000
  }
]


### Table Aggregated  Schedule Loss,  Availability Loss,  Performance Loss, Quality Loss
#### View Schedule Loss

|  No | Column Name    | Data Type    | Calculation                                                                                                 |
| --: | -------------- | ------------ | ----------------------------------------------------------------------------------------------------------- |
|   1 | id             | serial4      |                                                                                                             |
|   2 | work_unit_id   | int4         | **Primary grouping key (1 row per work_unit_id)**                                                           |
|   3 | date           | timestamptz  | a day start at 7 AM to tomorrow 7 am, so date today can have shift 3 that takes data from 23-7AM(tomorrow). |
|   4 | shift          | varchar(100) | Grouped by shift                                                                                            |
|   5 | pdt_reason     | VARCHAR(100) |                                                                                                             |
|   6 | equipment_name | Varchar(100) |                                                                                                             |
|   7 | freq           | int4         |                                                                                                             |
|  8. | Total duration | BIGINT       | milis                                                                                                       |

#### View Availability Loss

|  No | Column Name    | Data Type    | Calculation                                                                                                 |
| --: | -------------- | ------------ | ----------------------------------------------------------------------------------------------------------- |
|   1 | id             | serial4      |                                                                                                             |
|   2 | work_unit_id   | int4         | **Primary grouping key (1 row per work_unit_id)**                                                           |
|   3 | date           | timestamptz  | a day start at 7 AM to tomorrow 7 am, so date today can have shift 3 that takes data from 23-7AM(tomorrow). |
|   4 | shift          | varchar(100) | Grouped by shift                                                                                            |
|   5 | dt_reason      | VARCHAR(100) |                                                                                                             |
|   6 | equipment_name | Varchar(100) |                                                                                                             |
|   7 | freq           | int4         |                                                                                                             |
|  8. | Total duration | BIGINT       | milis                                                                                                       |
#### View Performance Loss

|  No | Column Name    | Data Type    | Calculation                                                                                                 |
| --: | -------------- | ------------ | ----------------------------------------------------------------------------------------------------------- |
|   1 | id             | serial4      |                                                                                                             |
|   2 | work_unit_id   | int4         | **Primary grouping key (1 row per work_unit_id)**                                                           |
|   3 | date           | timestamptz  | a day start at 7 AM to tomorrow 7 am, so date today can have shift 3 that takes data from 23-7AM(tomorrow). |
|   4 | shift          | varchar(100) | Grouped by shift                                                                                            |
|   5 | ms_reason      | VARCHAR(100) |                                                                                                             |
|   6 | equipment_name | Varchar(100) |                                                                                                             |
|   7 | freq           | int4         |                                                                                                             |
|  8. | Total duration | BIGINT       | milis                                                                                                       |

#### View Reject
|  No | Column Name    | Data Type     | Calculation                                                                                                 |
| --: | -------------- | ------------- | ----------------------------------------------------------------------------------------------------------- |
|   1 | id             | serial4       |                                                                                                             |
|   2 | work_unit_id   | int4          | **Primary grouping key (1 row per work_unit_id)**                                                           |
|   3 | date           | timestamptz   | a day start at 7 AM to tomorrow 7 am, so date today can have shift 3 that takes data from 23-7AM(tomorrow). |
|   4 | shift          | varchar(100)  | Grouped by shift                                                                                            |
|   5 | reject_reason  | VARCHAR(100)  | for each reason                                                                                             |
|   6 | equipment_name | Varchar(100)  | for each equipment                                                                                          |
|   7 | quantity       | int4          | sum of quantity based on uom_id for each reject_reaason                                                     |
|  8. | freq           | BIGINT        | milis                                                                                                       |
|   9 | uom_id         | int4          | the uom_id                                                                                                  |
|  10 | weight         | numeric(10,3) | sum of weight for that reject_reason                                                                        |


## Work Center
### work center shift reports
|    No | Column Name            | Data Type        | Calculation / Rules                                                                                                                                 |
| ----: | ---------------------- | ---------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
|     1 | date                   | timestamptz      | a day start at 7 AM to tomorrow 7 am, so date 4 Agustus 2026 can have shift 3 that takes data from 23-7AM(tomorrow). using day start date as anchor |
|     2 | shift                  | varchar(100)     | We group the calculation (like sum, etc) based on shift for 7-15-23 (1,2,3)                                                                         |
|     3 | work_center_id         | int4             | We group/sum the calculation based on work_center                                                                                                   |
|     4 | work_center_name       | varchar(255)     | join to find its work_center_name                                                                                                                   |
|     5 | work_center_code       | varchar(255)     | join to find its work_center_code                                                                                                                   |
| ==6== | ==uom_id==             | ==int4==         | ==uom_id for the base uom==                                                                                                                         |
| ==7== | ==uom_name==           | ==varchar(255)== | ==uom_name==                                                                                                                                        |
| ==8== | ==uom_code==           | ==varchar(255)== | ==uom_code==                                                                                                                                        |
|     9 | teep                   | numeric(10,3)    | OEE x Utility                                                                                                                                       |
|    10 | oee                    | numeric(10,3)    | availability(11) x performance(12) x quality(13). percentage                                                                                        |
|    11 | availability           | numeric(10,3)    | Operating Time(21) / production_time(19). percentage                                                                                                |
|    12 | performance            | numeric(10,3)    | net_time(23) / operating_time(21). percentage                                                                                                       |
|    13 | quality                | numeric(10,3)    | value_added_time(26) / net_time(23). percentage                                                                                                     |
|    14 | total_out              | numeric(15,3)    | total_out of ALL work_unit                                                                                                                          |
|    15 | good_out               | numeric(15,3)    | good_out using only last equipment in work_center                                                                                                   |
|    16 | effective_out          | numeric(15,3)    | cycle_time x (operating_time (21) -small_stop(ms_time))                                                                                             |
|    17 | runtime                | int8             | sum runtime of ALL work_unit                                                                                                                        |
|    18 | availability_time      | int8             | sum of all Full hour of shift of all work_unit                                                                                                      |
|    19 | production_time        | int8             | availability_time(18) -pdt_time(20) of all work_unit of all work_unit                                                                               |
|    20 | pdt_time               | int8             | Sum of planned downtime duration of all work_unit                                                                                                   |
|    21 | operating_time         | int8             | production_time(19) - updt_time(22) of ALL work_unit                                                                                                |
|    22 | updt_time              | int8             | Sum of unplanned downtime duration of ALL work_unit                                                                                                 |
|    23 | net_time               | int8             | operating_time(21) - (reduce_speed_time(24) + ms_time(25))                                                                                          |
|    24 | reduce_speed_time      | int8             | sum reduce_speed_time of ALL work_unit                                                                                                              |
|    25 | ms_time                | int8             | Sum of Small Stop duration of ALL work_unit                                                                                                         |
|    26 | value_added_time       | int8             | sum value_added_time of all of ALL work_unit                                                                                                        |
|    27 | rework_time            | int8             | rework(38) / cycle_time                                                                                                                             |
|    28 | reject_time            | int8             | Reject(37) / cycle_time                                                                                                                             |
|    29 | anomaly_time           | int8             | Sum of Anomaly / cycle_time. Anomaly = Actual Prod - Finish Good) - Sum of Reject                                                                   |
|    30 | up_speed_time          | int8             | reverse reduce_speed                                                                                                                                |
|    31 | schedule_loss_time     | int8             | Sum of Planned Downtime duration of ALL work_unit                                                                                                   |
|    32 | availability_loss_time | int8             | Sum of Unlanned Downtime duration of ALL work_unit                                                                                                  |
|    33 | performance_loss_time  | int8             | reduce_speed_time(24) + ms_loss_time(34)                                                                                                            |
|    34 | ms_loss_time           | int8             | SAME as ms_time                                                                                                                                     |
|    35 | reduce_speed_loss_time | int8             | SAME as reduce_speed_time                                                                                                                           |
|    36 | quality_loss_time      | int8             | SAME as reject_time                                                                                                                                 |
|    37 | reject                 | numeric(15,3)    | Sum of Reject accross all of Equipment work_center. The main diff                                                                                   |
|    38 | rework                 | numeric(15,3)    | Sum of Rework accross work_center -- Empty for now                                                                                                  |
## work center day reports
|    No | Column Name            | Data Type        | Calculation / Rules                                                               |
| ----: | ---------------------- | ---------------- | --------------------------------------------------------------------------------- |
|     1 | date                   | date             | a day start at 7 AM to tomorrow 7 am using day start date as anchor               |
|     3 | work_center_id         | int4             | We group/sum the calculation based on work_center                                 |
|     4 | work_center_name       | varchar(255)     | join to find its work_center_name                                                 |
|     5 | work_center_code       | varchar(255)     | join to find its work_center_code                                                 |
| ==6== | ==uom_id==             | ==int4==         | ==uom_id for the base uom==                                                       |
| ==7== | ==uom_name==           | ==varchar(255)== | ==uom_name==                                                                      |
| ==8== | ==uom_code==           | ==varchar(255)== | ==uom_code==                                                                      |
|     9 | teep                   | numeric(10,3)    | OEE x Utility                                                                     |
|    10 | oee                    | numeric(10,3)    | availability(11) x performance(12) x quality(13). percentage                      |
|    11 | availability           | numeric(10,3)    | Operating Time(21) / production_time(19). percentage                              |
|    12 | performance            | numeric(10,3)    | net_time(23) / operating_time(21). percentage                                     |
|    13 | quality                | numeric(10,3)    | value_added_time(26) / net_time(23). percentage                                   |
|    14 | total_out              | numeric(15,3)    | total_out of ALL work_unit                                                        |
|    15 | good_out               | numeric(15,3)    | good_out using only last equipment in work_center                                 |
|    16 | effective_out          | numeric(15,3)    | cycle_time x (operating_time (21) -small_stop(ms_time))                           |
|    17 | runtime                | int8             | sum runtime of ALL work_unit                                                      |
|    18 | availability_time      | int8             | sum of all Full hour day of all work_unit                                         |
|    19 | production_time        | int8             | availability_time(18) -pdt_time(20) of all work_unit of all work_unit             |
|    20 | pdt_time               | int8             | Sum of planned downtime duration of all work_unit                                 |
|    21 | operating_time         | int8             | production_time(19) - updt_time(22) of ALL work_unit                              |
|    22 | updt_time              | int8             | Sum of unplanned downtime duration of ALL work_unit                               |
|    23 | net_time               | int8             | operating_time(21) - (reduce_speed_time(24) + ms_time(25))                        |
|    24 | reduce_speed_time      | int8             | sum reduce_speed_time of ALL work_unit                                            |
|    25 | ms_time                | int8             | Sum of Small Stop duration of ALL work_unit                                       |
|    26 | value_added_time       | int8             | sum value_added_time of all of ALL work_unit                                      |
|    27 | rework_time            | int8             | rework(38) / cycle_time                                                           |
|    28 | reject_time            | int8             | Reject(37) / cycle_time                                                           |
|    29 | anomaly_time           | int8             | Sum of Anomaly / cycle_time. Anomaly = Actual Prod - Finish Good) - Sum of Reject |
|    30 | up_speed_time          | int8             | reverse reduce_speed                                                              |
|    31 | schedule_loss_time     | int8             | Sum of Planned Downtime duration of ALL work_unit                                 |
|    32 | availability_loss_time | int8             | Sum of Unlanned Downtime duration of ALL work_unit                                |
|    33 | performance_loss_time  | int8             | reduce_speed_time(24) + ms_loss_time(34)                                          |
|    34 | ms_loss_time           | int8             | SAME as ms_time                                                                   |
|    35 | reduce_speed_loss_time | int8             | SAME as reduce_speed_time                                                         |
|    36 | quality_loss_time      | int8             | SAME as reject_time                                                               |
|    37 | reject                 | numeric(15,3)    | Sum of Reject accross all of Equipment work_center.                               |
|    38 | rework                 | numeric(15,3)    | Sum of Rework accross work_center -- Empty for now                                |

## Area
### Area day reports

|    No | Column Name            | Data Type        | Calculation / Rules                                                               |     |
| ----: | ---------------------- | ---------------- | --------------------------------------------------------------------------------- | --- |
|     1 | date                   | timestamptz      | a day start at 7 AM to tomorrow 7 am. using day start as anchor                   |     |
|     3 | area_id                | int4             | We group/sum the calculation based on area                                        |     |
|     4 | area_name              | varchar(255)     | join to find its area_name                                                        |     |
|     5 | area_code              | varchar(255)     | join to find its area_code                                                        |     |
| ==6== | ==uom_id==             | ==int4==         | ==uom_id for the base uom==                                                       |     |
| ==7== | ==uom_name==           | ==varchar(255)== | ==uom_name==                                                                      |     |
| ==8== | ==uom_code==           | ==varchar(255)== | ==uom_code==                                                                      |     |
|     9 | teep                   | numeric(10,3)    | OEE x Utility                                                                     |     |
|    10 | oee                    | numeric(10,3)    | availability(11) x performance(12) x quality(13). percentage                      |     |
|    11 | availability           | numeric(10,3)    | Operating Time(21) / production_time(19). percentage                              |     |
|    12 | performance            | numeric(10,3)    | net_time(23) / operating_time(21). percentage                                     |     |
|    13 | quality                | numeric(10,3)    | value_added_time(26) / net_time(23). percentage                                   |     |
|    14 | total_out              | numeric(15,3)    | total_out of ALL work_unit                                                        |     |
|    15 | good_out               | numeric(15,3)    | good_out using only last equipment in work_center                                 |     |
|    16 | effective_out          | numeric(15,3)    | cycle_time x (operating_time (21) -small_stop(ms_time))                           |     |
|    17 | runtime                | int8             | sum runtime of ALL work_unit                                                      |     |
|    18 | availability_time      | int8             | sum of all Full hour of shift of all work_unit                                    |     |
|    19 | production_time        | int8             | availability_time(18) -pdt_time(20) of all work_unit of all work_unit             |     |
|    20 | pdt_time               | int8             | Sum of planned downtime duration of all work_unit                                 |     |
|    21 | operating_time         | int8             | production_time(19) - updt_time(22) of ALL work_unit                              |     |
|    22 | updt_time              | int8             | Sum of unplanned downtime duration of ALL work_unit                               |     |
|    23 | net_time               | int8             | operating_time(21) - (reduce_speed_time(24) + ms_time(25))                        |     |
|    24 | reduce_speed_time      | int8             | sum reduce_speed_time of ALL work_unit                                            |     |
|    25 | ms_time                | int8             | Sum of Small Stop duration of ALL work_unit                                       |     |
|    26 | value_added_time       | int8             | sum value_added_time of all of ALL work_unit                                      |     |
|    27 | rework_time            | int8             | rework(38) / cycle_time                                                           |     |
|    28 | reject_time            | int8             | Reject(37) / cycle_time                                                           |     |
|    29 | anomaly_time           | int8             | Sum of Anomaly / cycle_time. Anomaly = Actual Prod - Finish Good) - Sum of Reject |     |
|    30 | up_speed_time          | int8             | reverse reduce_speed                                                              |     |
|    31 | schedule_loss_time     | int8             | Sum of Planned Downtime duration of ALL work_unit                                 |     |
|    32 | availability_loss_time | int8             | Sum of Unplanned Downtime duration of ALL work_unit                               |     |
|    33 | performance_loss_time  | int8             | reduce_speed_time(24) + ms_loss_time(34)                                          |     |
|    34 | ms_loss_time           | int8             | SAME as ms_time                                                                   |     |
|    35 | reduce_speed_loss_time | int8             | SAME as reduce_speed_time                                                         |     |
|    36 | quality_loss_time      | int8             | SAME as reject_time                                                               |     |
|    37 | reject                 | numeric(15,3)    | Sum of Reject accross all of Equipment work_center. The main diff                 |     |
|    38 | rework                 | numeric(15,3)    | Sum of Rework accross work_center -- Empty for now                                |     |


---
**Related:** Formulas [[oee_or_kpi_calculation]] · inputs [[NEW_oee_or_kpi_calculation]] · source [[silver_model]] · job [[airflow_rules]]
