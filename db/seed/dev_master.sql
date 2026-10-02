-- Dev / simulation master data (site JKT1, line L01 = packer PCK01 -> checkweigher CHK01).
-- Matches tools/telegraf_sim.py. Facts are NOT seeded: Flink produces them from Kafka.
SET search_path = silver, public;
INSERT INTO tenant(tenant_id,tenant_code,valid_from) VALUES (1,'MOLCA','2026-01-01');
INSERT INTO enterprise(enterprise_id,tenant_id,enterprise_code,valid_from) VALUES (1,1,'ENT','2026-01-01');
INSERT INTO site(site_id,enterprise_id,site_code,site_name,timezone,valid_from) VALUES (1,1,'JKT1','Jakarta 1','Asia/Jakarta','2026-01-01');
INSERT INTO area(area_id,site_id,area_code,area_name,valid_from) VALUES (1,1,'AS','Assembly','2026-01-01');
INSERT INTO work_center(work_center_id,area_id,work_center_code,work_center_name,type,valid_from) VALUES (1,1,'L01','Line 1','production_line','2026-01-01');
INSERT INTO work_unit(work_unit_id,work_center_id,work_unit_code,work_unit_name,valid_from) VALUES (7,1,'PCK01','Packer 1','2026-01-01'),(8,1,'CHK01','Checkweigher','2026-01-01');
INSERT INTO equipment_flow(work_center_id,from_work_unit_id,to_work_unit_id,valid_from) VALUES (1,7,8,'2026-01-01');
INSERT INTO data_source(data_source_id,site_id,name,protocol,node_naming,valid_from) VALUES (1,1,'PLC L1','mqtt','manual','2026-01-01');
INSERT INTO measurement_group(measurement_group_id,code,name,valid_from) VALUES (1,'count','Count','2026-01-01'),(2,'mass','Mass','2026-01-01');
INSERT INTO unit_of_measurements(uom_id,code,name,unit,measurement_group_id) VALUES (1,'PACK','Pack','pk',1),(2,'KG','Kilogram','kg',2);
INSERT INTO time_conversions(time_conversions_id,code,name,seconds_per_unit) VALUES (1,'SEC','Second',1);
INSERT INTO asset(asset_id,asset_tag,asset_name,valid_from) VALUES (1,'PCK01','Packer 1','2026-01-01'),(2,'CHK01','Checkweigher','2026-01-01');
INSERT INTO asset_placement(asset_id,work_unit_id,placement_role,valid_from) VALUES (1,7,'primary','2026-01-01'),(2,8,'primary','2026-01-01');
INSERT INTO reject_reason(reject_reason_id,code,name) VALUES (1,'UNDER','Underweight'),(2,'OVER','Overweight');
INSERT INTO downtime_reason(downtime_reason_id,downtime_category,code,name) VALUES
  (1,'unplanned','FILM','Film out'),(2,'unplanned','JAM','Material jam'),(3,'planned','BREAK','Break');
INSERT INTO asset_tags(tag_id,asset_id,data_source_id,work_unit_id,tag_name,node,kind,tag_role,uom_id,count_basis,reject_reason_id,downtime_reason_id,valid_from) VALUES
 (101,1,1,7,'total_count','PLC/L1/PCK01/total_count','cumulative_counter','total',1,'unit',NULL,NULL,'2026-01-01'),
 (102,1,1,7,'good_count','PLC/L1/PCK01/good_count','cumulative_counter','good',1,'unit',NULL,NULL,'2026-01-01'),
 (103,1,1,7,'ng_under_weight','PLC/L1/PCK01/ng_under_weight','cumulative_counter','reject',2,'unit',1,NULL,'2026-01-01'),
 (104,1,1,7,'ng_over_weight','PLC/L1/PCK01/ng_over_weight','cumulative_counter','reject',2,'unit',2,NULL,'2026-01-01'),
 (105,1,1,NULL,'jam','PLC/L1/PCK01/jam','boolean','downtime_reason',NULL,NULL,NULL,2,'2026-01-01'),
 (106,1,1,NULL,'film_out','PLC/L1/PCK01/film_out','boolean','downtime_reason',NULL,NULL,NULL,1,'2026-01-01'),
 (107,1,1,7,'product_code','PLC/L1/PCK01/product_code','value','product_code',NULL,NULL,NULL,NULL,'2026-01-01'),
 (201,2,1,8,'total_count','PLC/L1/CHK01/total_count','cumulative_counter','total',1,'unit',NULL,NULL,'2026-01-01'),
 (202,2,1,8,'good_count','PLC/L1/CHK01/good_count','cumulative_counter','good',1,'unit',NULL,NULL,'2026-01-01');
INSERT INTO work_unit_kpi_binding(work_unit_id,slot_id,asset_tag_id,transform,valid_from) VALUES
 (7,'availability.downtime_reason',101,'stall_bucket','2026-01-01'),(7,'performance.output',101,'none','2026-01-01'),
 (7,'quality.total',101,'none','2026-01-01'),(7,'quality.good',102,'none','2026-01-01'),
 (7,'quality.reject',103,'none','2026-01-01'),(7,'quality.reject',104,'none','2026-01-01'),
 (8,'quality.total',201,'none','2026-01-01'),(8,'quality.good',202,'none','2026-01-01');
INSERT INTO enterprise_shift(enterprise_shift_id,enterprise_id,shift_label,valid_from) VALUES
 (1,1,'Shift 1','2026-01-01'),(2,1,'Shift 2','2026-01-01'),(3,1,'Shift 3','2026-01-01');
INSERT INTO site_shift(site_shift_id,site_id,enterprise_shift_id,start_time,end_time,valid_from) VALUES
 (11,1,1,'07:00','15:00','2026-01-01'),(12,1,2,'15:00','23:00','2026-01-01'),(13,1,3,'23:00','07:00','2026-01-01');
-- shift instances 2026-09-28 .. 2026-10-05 (generated ahead, as production-domain will do). 2026-09-30 = ids 500/501/502.
INSERT INTO shift_instance(shift_instance_id, site_shift_id, business_date, start_datetime, end_datetime, status)
SELECT 500 + (d - DATE '2026-09-30') * 10 + n - 1, 10 + n, d,
       (d + ss.start_time) AT TIME ZONE 'Asia/Jakarta',
       (d + ss.start_time) AT TIME ZONE 'Asia/Jakarta' + interval '8 hours',
       'open'
FROM generate_series(DATE '2026-09-28', DATE '2026-10-05', interval '1 day') g(dd)
CROSS JOIN LATERAL (SELECT dd::date AS d) x
CROSS JOIN generate_series(1, 3) n
JOIN site_shift ss ON ss.site_shift_id = 10 + n;
INSERT INTO product(product_id,code,name,product_type,base_uom_id,valid_from) VALUES
 (11,'BISC-A','Biscuit A','finished_good',1,'2026-01-01'),(12,'BISC-B','Biscuit B','finished_good',1,'2026-01-01');
INSERT INTO product_detail(product_id,standard_weight,weight_uom_id,valid_from) VALUES (11,0.02,2,'2026-01-01'),(12,0.02,2,'2026-01-01');
INSERT INTO asset_product_codes(tag_id,raw_value,product_id,valid_from) VALUES
 (107,'A01',11,'2026-01-01 00:00+00'),(107,'B01',12,'2026-01-01 00:00+00');
INSERT INTO product_flow(product_flow_id,product_id,flow_code,is_primary,status,valid_from) VALUES (1,11,'STD',true,'released','2026-01-01'),(2,12,'STD',true,'released','2026-01-01');
INSERT INTO routing(routing_id,product_id,flow_id,work_center_id,version,status,is_primary,valid_from) VALUES (1,11,1,1,1,'released',true,'2026-01-01'),(2,12,2,1,1,'released',true,'2026-01-01');
INSERT INTO operation(operation_id,routing_id,step_number,work_unit_id,cycle_time_value,uom_id,time_conversion_id,batch_size,valid_from) VALUES
 (1,1,1,7,0.9,1,1,1,'2026-01-01'),(2,2,1,7,1.2,1,1,1,'2026-01-01');
