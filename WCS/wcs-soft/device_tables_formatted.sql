-- -----------------------------
-- DeviceInfo 示例数据
-- -----------------------------
INSERT INTO `device_info` (`id`, `code`, `name`, `type`, `com_type`, `s7_type`, `ip`, `port`, `is_online`, `state`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (111, 'CRANE_001', '堆垛机001', 1, 1, 1, '192.168.100.60', 102, 1, 2, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- -----------------------------
-- DeviceValue 示例数据（基于堆垛机PLC接口点表）
-- -----------------------------
-- 写字区域(WW)数据项
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (1, 'RK_Hang', '入库行', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W0', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (2, 'RK_Line', '入库列', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W2', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (6, 'RK_Ceng', '入库层', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W4', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (7, 'CK_Hang', '出库行', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W6', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (8, 'CK_Line', '出库列', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W8', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (9, 'CK_Ceng', '出库层', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W10', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (10, 'PK_From_Hang', '盘库取行', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W12', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (11, 'PK_From_Line', '盘库取列', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W14', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (12, 'PK_From_Ceng', '盘库取层', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W16', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (13, 'PK_To_Hang', '盘库放行', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W18', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (14, 'PK_To_Line', '盘库放列', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W20', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (15, 'PK_To_Ceng', '盘库放层', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W22', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (16, 'Get_Station', '取货站台号', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W24', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (17, 'Put_Station', '放货站台号', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W26', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (18, 'RepairPwd', '维修密码', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W28', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (19, 'Task_ID', '任务号', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W30', NULL, NULL, 1, 2, 4, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (20, 'Task_Reset', '任务复位', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W34', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (21, 'Move_Line1', '空移位列', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W36', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (22, 'Move_Line2', '空移位层', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.W38', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- 写位区域(WB)数据项
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (23, 'Stop', '急停', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X40', 0, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (24, 'Reset', '复位', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X40', 1, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (25, 'Online', '上位机控制联机', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X40', 2, NULL, 2, 1, 1, 'true', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (26, 'Offline', '上位机控制脱机', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X40', 3, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (27, 'BaseStation', '回站台', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X40', 4, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (28, 'StationMove', '站台搬运', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X40', 5, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (29, 'Begin_RK', '开始入库', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X40', 6, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (30, 'Begin_CK', '开始出库', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X40', 7, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (31, 'Begin_PK', '开始盘库', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X41', 0, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (32, 'M_U', '手动上升', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X41', 1, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (33, 'M_D', '手动下降', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X41', 2, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (34, 'M_F', '手动向前', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X41', 3, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (35, 'M_B', '手动后退', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X41', 4, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (36, 'M_HC_Left', '手动货叉向左', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X41', 5, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (37, 'M_HC_Right', '手动货叉向右', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X41', 6, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (38, 'M_Speed', '手动中速选择', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X41', 7, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (39, 'M_Pass', '手动直通选择', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X42', 0, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (40, 'M_mnzh', '模拟有货', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X42', 1, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (41, 'Per_Cont', '继续执行', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X42', 2, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (42, 'Clear_Task', '清除任务', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X42', 3, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (43, 'Move_Start', '开始移动', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X42', 4, NULL, 2, 1, 1, 'false', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- 读字区域(RD)数据项
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (3, 'HC_UP', '货叉在上点', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X90', 0, NULL, 2, 1, 1, 'true', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (4, 'HC_Mid', '货叉在中点', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X94', 2, NULL, 2, 1, 1, 'true', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (5, 'SHWJ_LJ', '上位机联机', 0, 111, 'CRANE_001', '堆垛机001', 1, 1, '192.168.100.60', 102, 'DB5.X98', 0, NULL, 2, 1, 1, 'true', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- -----------------------------
-- 输送线站台设备信息
-- -----------------------------
INSERT INTO `device_info` (`id`, `code`, `name`, `type`, `com_type`, `s7_type`, `ip`, `port`, `is_online`, `state`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (112, 'SSX_STATION_01', '输送线站台1', 1, 1, 1, '192.168.100.61', 102, 1, 2, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

INSERT INTO `device_info` (`id`, `code`, `name`, `type`, `com_type`, `s7_type`, `ip`, `port`, `is_online`, `state`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (113, 'SSX_STATION_02', '输送线站台2', 1, 1, 1, '192.168.100.62', 102, 1, 2, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- -----------------------------
-- 输送线站台1点位数据
-- -----------------------------
-- 是否有托盘 (0-无, 1-有)
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (22, 'HAS_PALLET', '是否有托盘', 0, 112, 'SSX_STATION_01', '输送线站台1', 1, 1, '192.168.100.61', 102, 'DB10.W0', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- 托盘号
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (23, 'PALLET_CODE', '托盘号', 0, 112, 'SSX_STATION_01', '输送线站台1', 1, 1, '192.168.100.61', 102, 'DB10.S20', NULL, NULL, 3, 1, 1, '', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- BCR数据
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (24, 'BCR_DATA', 'BCR数据', 0, 112, 'SSX_STATION_01', '输送线站台1', 1, 1, '192.168.100.61', 102, 'DB10.S50', NULL, NULL, 3, 1, 1, '', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- 任务申请信号 (0-无, 1-有申请)
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (25, 'TASK_REQUEST', '任务申请信号', 0, 112, 'SSX_STATION_01', '输送线站台1', 1, 1, '192.168.100.61', 102, 'DB10.W2', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- 是否允许入库 (0-不允许, 1-允许)
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (26, 'ALLOW_IN', '是否允许入库', 0, 112, 'SSX_STATION_01', '输送线站台1', 1, 1, '192.168.100.61', 102, 'DB10.W4', NULL, NULL, 1, 1, 1, '1', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- 是否允许出库 (0-不允许, 1-允许)
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (27, 'ALLOW_OUT', '是否允许出库', 0, 112, 'SSX_STATION_01', '输送线站台1', 1, 1, '192.168.100.61', 102, 'DB10.W6', NULL, NULL, 1, 1, 1, '1', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- -----------------------------
-- 输送线站台2点位数据
-- -----------------------------
-- 是否有托盘 (0-无, 1-有)
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (28, 'HAS_PALLET', '是否有托盘', 0, 113, 'SSX_STATION_02', '输送线站台2', 1, 1, '192.168.100.62', 102, 'DB11.W0', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- 托盘号
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (29, 'PALLET_CODE', '托盘号', 0, 113, 'SSX_STATION_02', '输送线站台2', 1, 1, '192.168.100.62', 102, 'DB11.S20', NULL, NULL, 3, 1, 1, '', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- BCR数据
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (30, 'BCR_DATA', 'BCR数据', 0, 113, 'SSX_STATION_02', '输送线站台2', 1, 1, '192.168.100.62', 102, 'DB11.S50', NULL, NULL, 3, 1, 1, '', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- 任务申请信号 (0-无, 1-有申请)
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (31, 'TASK_REQUEST', '任务申请信号', 0, 113, 'SSX_STATION_02', '输送线站台2', 1, 1, '192.168.100.62', 102, 'DB11.W2', NULL, NULL, 1, 1, 1, '0', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- 是否允许入库 (0-不允许, 1-允许)
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (32, 'ALLOW_IN', '是否允许入库', 0, 113, 'SSX_STATION_02', '输送线站台2', 1, 1, '192.168.100.62', 102, 'DB11.W4', NULL, NULL, 1, 1, 1, '1', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);

-- 是否允许出库 (0-不允许, 1-允许)
INSERT INTO `device_value` (`id`, `code`, `name`, `type`, `device_id`, `device_code`, `device_name`, `com_type`, `s7_type`, `ip`, `port`, `address`, `bit_index`, `modbus_type`, `plc_type`, `java_type`, `length`, `read_value`, `read_time`, `write_value`, `write_time`, `create_time`, `create_user_id`, `create_user_name`, `update_time`, `update_user_id`, `update_user_name`, `version`, `is_delete`)
VALUES (33, 'ALLOW_OUT', '是否允许出库', 0, 113, 'SSX_STATION_02', '输送线站台2', 1, 1, '192.168.100.62', 102, 'DB11.W6', NULL, NULL, 1, 1, 1, '1', NULL, NULL, NULL, '2024-05-16 10:00:00', 1, 'admin', '2024-05-16 10:00:00', 1, 'admin', 1, 0);
