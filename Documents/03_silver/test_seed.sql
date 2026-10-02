SET search_path = silver, public;
INSERT INTO tenant(tenant_id,tenant_code,valid_from) VALUES (1,'M','2026-01-01');
INSERT INTO enterprise(enterprise_id,tenant_id,enterprise_code,valid_from) VALUES (1,1,'E','2026-01-01');
INSERT INTO site(site_id,enterprise_id,site_code,timezone,valid_from) VALUES (1,1,'JKT1','Asia/Jakarta','2026-01-01');
INSERT INTO area(area_id,site_id,area_code,area_name,valid_from) VALUES (1,1,'AS','Assembly','2026-01-01');
INSERT INTO work_center(work_center_id,area_id,work_center_code,work_center_name,type,valid_from) VALUES (1,1,'L01','Line 1','production_line','2026-01-01');
INSERT INTO work_unit(work_unit_id,work_center_id,work_unit_code,work_unit_name,valid_from) VALUES (7,1,'PCK01','Packer 1','2026-01-01'),(8,1,'CHK01','Checkweigher','2026-01-01');
INSERT INTO equipment_flow(work_center_id,from_work_unit_id,to_work_unit_id,valid_from) VALUES (1,7,8,'2026-01-01');
INSERT INTO data_source(data_source_id,site_id,name,protocol,node_naming,valid_from) VALUES (1,1,'PLC','mqtt','manual','2026-01-01');
INSERT INTO measurement_group(measurement_group_id,code,valid_from) VALUES (1,'count','2026-01-01'),(2,'mass','2026-01-01');
INSERT INTO unit_of_measurements(uom_id,code,name,unit,measurement_group_id) VALUES (1,'PACK','Pack','pk',1),(2,'KG','Kilogram','kg',2);
INSERT INTO time_conversions(time_conversions_id,code,seconds_per_unit) VALUES (1,'SEC',1);
INSERT INTO asset(asset_id,asset_tag,asset_name,valid_from) VALUES (1,'PCK01','Packer 1','2026-01-01'),(2,'CHK01','Checkweigher','2026-01-01');
INSERT INTO asset_placement(asset_id,work_unit_id,placement_role,valid_from) VALUES (1,7,'primary','2026-01-01'),(2,8,'primary','2026-01-01');
INSERT INTO reject_reason(reject_reason_id,code,name) VALUES (1,'UNDER','Underweight'),(2,'OVER','Overweight');
INSERT INTO downtime_reason(downtime_reason_id,downtime_category,code,name) VALUES (1,'unplanned','FILM','Film out'),(2,'unplanned','JAM','Material jam'),(3,'planned','BREAK','Break');
INSERT INTO asset_tags(tag_id,asset_id,data_source_id,work_unit_id,tag_name,node,kind,tag_role,uom_id,count_basis,reject_reason_id,valid_from) VALUES
 (101,1,1,7,'total_count','PLC/L1/PCK01/total_count','cumulative_counter','total',1,'unit',NULL,'2026-01-01'),
 (102,1,1,7,'good_count','PLC/L1/PCK01/good_count','cumulative_counter','good',1,'unit',NULL,'2026-01-01'),
 (103,1,1,7,'ng_under_weight','PLC/L1/PCK01/ng_under_weight','cumulative_counter','reject',2,'unit',1,'2026-01-01'),
 (104,1,1,7,'ng_over_weight','PLC/L1/PCK01/ng_over_weight','cumulative_counter','reject',2,'unit',2,'2026-01-01'),
 (201,2,1,8,'total_count','PLC/L1/CHK01/total_count','cumulative_counter','total',1,'unit',NULL,'2026-01-01'),
 (202,2,1,8,'good_count','PLC/L1/CHK01/good_count','cumulative_counter','good',1,'unit',NULL,'2026-01-01');
INSERT INTO kpi_formula_slot(slot_id,metric,bindable) VALUES ('availability.downtime_reason','availability',true),('performance.output','performance',true),('quality.total','quality',true),('quality.good','quality',true),('quality.reject','quality',true);
INSERT INTO work_unit_kpi_binding(work_unit_id,slot_id,asset_tag_id,transform,valid_from) VALUES
 (7,'availability.downtime_reason',101,'stall_bucket','2026-01-01'),(7,'performance.output',101,'none','2026-01-01'),
 (7,'quality.total',101,'none','2026-01-01'),(7,'quality.good',102,'none','2026-01-01'),
 (7,'quality.reject',103,'none','2026-01-01'),(7,'quality.reject',104,'none','2026-01-01'),
 (8,'quality.total',201,'none','2026-01-01'),(8,'quality.good',202,'none','2026-01-01');
INSERT INTO enterprise_shift(enterprise_shift_id,enterprise_id,shift_label,valid_from) VALUES (1,1,'Shift 1','2026-01-01'),(2,1,'Shift 2','2026-01-01');
INSERT INTO site_shift(site_shift_id,site_id,enterprise_shift_id,start_time,end_time,valid_from) VALUES (11,1,1,'07:00','15:00','2026-01-01'),(12,1,2,'15:00','23:00','2026-01-01');
INSERT INTO shift_instance(shift_instance_id,site_shift_id,business_date,start_datetime,end_datetime,status) VALUES
 (500,11,'2026-09-30','2026-09-30 00:00+00','2026-09-30 08:00+00','closed'),(501,12,'2026-09-30','2026-09-30 08:00+00','2026-09-30 16:00+00','closed');
INSERT INTO product(product_id,code,name,product_type,base_uom_id,valid_from) VALUES (11,'BISC-A','Biscuit A','finished_good',1,'2026-01-01'),(12,'BISC-B','Biscuit B','finished_good',1,'2026-01-01');
INSERT INTO product_detail(product_id,standard_weight,weight_uom_id,valid_from) VALUES (11,0.02,2,'2026-01-01'),(12,0.02,2,'2026-01-01');
INSERT INTO product_flow(product_flow_id,product_id,flow_code,is_primary,status,valid_from) VALUES (1,11,'STD',true,'released','2026-01-01'),(2,12,'STD',true,'released','2026-01-01');
INSERT INTO routing(routing_id,product_id,flow_id,work_center_id,version,status,is_primary,valid_from) VALUES (1,11,1,1,1,'released',true,'2026-01-01'),(2,12,2,1,1,'released',true,'2026-01-01');
INSERT INTO operation(operation_id,routing_id,step_number,work_unit_id,cycle_time_value,uom_id,time_conversion_id,batch_size,valid_from) VALUES
 (1,1,1,7,0.9,1,1,1,'2026-01-01'),(2,2,1,7,1.2,1,1,1,'2026-01-01');
INSERT INTO production_events(event_time,tag_id,asset_id,work_unit_id,shift_instance_id,business_date,product_id,role,uom_id,raw_value,cleaned_value,source_tag) VALUES
 ('2026-09-30 00:30+00',101,1,7,500,'2026-09-30',11,'total',1,9000,9000,'t'),('2026-09-30 03:30+00',101,1,7,500,'2026-09-30',11,'total',1,18000,9000,'t'),
 ('2026-09-30 05:30+00',101,1,7,500,'2026-09-30',12,'total',1,21000,3000,'t'),('2026-09-30 07:30+00',101,1,7,500,'2026-09-30',12,'total',1,24000,3000,'t'),
 ('2026-09-30 03:30+00',102,1,7,500,'2026-09-30',11,'good',1,17700,17700,'t'),('2026-09-30 07:30+00',102,1,7,500,'2026-09-30',12,'good',1,23520,5820,'t'),
 ('2026-09-30 07:31+00',201,2,8,500,'2026-09-30',12,'total',1,23520,23520,'t'),('2026-09-30 07:31+00',202,2,8,500,'2026-09-30',12,'good',1,23400,23400,'t');
INSERT INTO reject_events(event_time,tag_id,asset_id,work_unit_id,shift_instance_id,business_date,product_id,reject_reason_id,uom_id,raw_value,cleaned_value,source_tag) VALUES
 ('2026-09-30 01:00+00',103,1,7,500,'2026-09-30',11,1,2,3.0,3.0,'t'),('2026-09-30 02:00+00',103,1,7,500,'2026-09-30',11,1,2,6.0,3.0,'t'),
 ('2026-09-30 06:00+00',104,1,7,500,'2026-09-30',12,2,2,3.6,3.6,'t');
-- manual stop (S4): a break entered by the supervisor during the shift
SET silver.now_override = '2026-09-30 07:00+00';
INSERT INTO downtime_entry(work_unit_id,asset_id,shift_instance_id,category,reason_ids,product_id,start_at,end_at,reason,created_by,created_by_name) VALUES
 (7,1,500,'planned','{3}',12,'2026-09-30 05:00+00','2026-09-30 05:30+00','Lunch break','sup-001','Supervisor A');
RESET silver.now_override;
INSERT INTO downtime_events(source_event_id,asset_id,tag_id,work_unit_id,shift_instance_id,business_date,category,downtime_reason_id,downtime_reason_ids,product_id,source,start_at,end_at,stop_group_id) VALUES
 ('wu7-t101-a',1,101,7,500,'2026-09-30','unplanned',2,'{2,1}',12,'derived','2026-09-30 06:40+00','2026-09-30 07:02+00',2),
 ('wu7-t101-b',1,101,7,500,'2026-09-30','small_stop',NULL,'{}',11,'derived','2026-09-30 03:00+00','2026-09-30 03:04:30+00',3);
CALL public.refresh_continuous_aggregate('silver.production_events_1m', NULL, NULL);
CALL public.refresh_continuous_aggregate('silver.reject_events_1m', NULL, NULL);
