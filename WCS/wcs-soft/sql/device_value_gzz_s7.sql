-- device_value 表插入脚本
-- 设备编码: gzz (工作站)
-- 通讯协议: S7
-- 
-- 字段说明:
--   com_type: 0 = S7
--   s7_type: 需要根据实际PLC型号配置 (0=S200, 1=S200_SMART, 2=S300, 3=S400, 4=S1200, 5=S1500)
--   type: 0=读取(PLC->WCS), 1=写入(WCS->PLC)
--   plc_type: 1=int16/INT, 2=uint16/WORD, 3=int32/DINT, 7=string
--   java_type: 2=Integer, 3=Short, 7=String
--   length: Int类型=1, DInt类型=2, String根据实际长度
--   address: S7地址格式，如 DB100.DBW0, DB101.DBW0
--
-- 通信方向:
--   PLC -> WCS (type=0): PLC反馈状态给WCS (DB101, 约20个字段)
--   WCS -> PLC (type=1): WCS控制指令发送给工作站 (DB100, 约4个字段)

-- ================================================================================
-- PLC -> WCS 方向 (读取数据) - DB101
-- type = 0 表示从PLC读取状态数据
-- ================================================================================

-- 1. 反馈设备号 (当前线体设备编号)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('feedback_device_id', '反馈设备号', 0, 'gzz', 0, 4, 'DB101.DBW0', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 2. 反馈任务号
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('feedback_task_id', '反馈任务号', 0, 'gzz', 0, 4, 'DB101.DBW2', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 3. 心跳
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('heartbeat', '心跳', 0, 'gzz', 0, 4, 'DB101.DBW4', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 4. 任务执行状态 (0-空闲无任务, 1-入库任务执行中, 2-出库任务执行中, 3-任务已完成, 4-任务执行失败)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('task_status', '任务执行状态', 0, 'gzz', 0, 4, 'DB101.DBW6', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 5. 联机状态
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('online_status', '联机状态', 0, 'gzz', 0, 4, 'DB101.DBW8', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 6. 载货状态 (0-无货, 1-有货)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('load_status', '载货状态', 0, 'gzz', 0, 4, 'DB101.DBW10', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 7. 申请信号 (工作站申请入库/出库)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('apply_signal', '申请信号', 0, 'gzz', 0, 4, 'DB101.DBW12', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 8. 单机故障状态 (0-正常, 1-故障)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('fault_status', '单机故障状态', 0, 'gzz', 0, 4, 'DB101.DBW14', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 9. 单机故障代码
-- (1-任务号校验错误, 2-CRC校验错误, 3-托盘尺寸不对, 4-条码信息不对, 5-系统退回, 6-库口被占用)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('fault_code', '单机故障代码', 0, 'gzz', 0, 4, 'DB101.DBW16', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 10. 检测门故障状态
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('gate_fault_status', '检测门故障状态', 0, 'gzz', 0, 4, 'DB101.DBW18', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 11. 检测门故障代码
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('gate_fault_code', '检测门故障代码', 0, 'gzz', 0, 4, 'DB101.DBW20', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 12. 称重故障状态
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('weighing_fault_status', '称重故障状态', 0, 'gzz', 0, 4, 'DB101.DBW22', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 13. 称重故障代码
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('weighing_fault_code', '称重故障代码', 0, 'gzz', 0, 4, 'DB101.DBW24', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 14. 读码故障状态
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('barcode_fault_status', '读码故障状态', 0, 'gzz', 0, 4, 'DB101.DBW26', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 15. 读码故障代码
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('barcode_fault_code', '读码故障代码', 0, 'gzz', 0, 4, 'DB101.DBW28', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 16. 当前托盘长 (单位:mm)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('pallet_length', '当前托盘长', 0, 'gzz', 0, 4, 'DB101.DBW30', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 17. 当前托盘宽 (单位:mm)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('pallet_width', '当前托盘宽', 0, 'gzz', 0, 4, 'DB101.DBW32', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 18. 当前托盘高 (单位:mm)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('pallet_height', '当前托盘高', 0, 'gzz', 0, 4, 'DB101.DBW34', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 19. 当前货物重量 (单位:kg)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('cargo_weight', '当前货物重量', 0, 'gzz', 0, 4, 'DB101.DBW36', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 20. 托盘条码信息 (字符串, 假设30字符)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('barcode_info', '托盘条码信息', 0, 'gzz', 0, 4, 'DB101.DBW38', 7, 7, 30, 
        NOW(), 1, 'admin', 0, 0);

-- ================================================================================
-- WCS -> PLC 方向 (写入数据) - DB100
-- type = 1 表示WCS向PLC写入控制指令
-- ================================================================================

-- 21. 任务号 (下发任务号)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('task_id_write', '任务号(写)', 1, 'gzz', 0, 4, 'DB100.DBW0', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 22. 任务类型
-- (0-初值, 1-入库, 2-出库, 97-货物入库完成, 98-货物出库完成, 
--  100-托盘尺寸不符退回, 101-条码信息不符退回, 102-系统退回)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('task_type_write', '任务类型(写)', 1, 'gzz', 0, 4, 'DB100.DBW2', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 23. 复位信号 (0-正常, 1-复位)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('reset_signal', '复位信号', 1, 'gzz', 0, 4, 'DB100.DBW4', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 24. CRC校验码 (对任务号和任务类型进行CRC-16校验)
INSERT INTO device_value (code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('crc_checksum', 'CRC校验码', 1, 'gzz', 0, 4, 'DB100.DBW6', 2, 2, 1, 
        NOW(), 1, 'admin', 0, 0);

-- ================================================================================
-- 查询验证
-- ================================================================================
-- 查看所有gzz设备的配置 (按类型和地址排序)
SELECT id, code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length 
FROM device_value 
WHERE device_code = 'gzz' 
ORDER BY type, id;

-- 统计信息
SELECT 
    type,
    CASE type 
        WHEN 0 THEN 'PLC->WCS(读取)'
        WHEN 1 THEN 'WCS->PLC(写入)'
        ELSE '未知'
    END AS direction,
    COUNT(*) AS count
FROM device_value 
WHERE device_code = 'gzz' 
GROUP BY type;

-- 任务类型枚举说明
-- 0  - 初值
-- 1  - 入库
-- 2  - 出库
-- 97 - 货物入库完成
-- 98 - 货物出库完成
-- 100 - 托盘尺寸不符退回
-- 101 - 条码信息不符退回
-- 102 - 系统退回

-- 任务执行状态枚举说明
-- 0 - 空闲无任务
-- 1 - 入库任务执行中
-- 2 - 出库任务执行中
-- 3 - 任务已完成
-- 4 - 任务执行失败

-- 单机故障代码枚举说明
-- 0 - 无故障
-- 1 - 上位指令任务号校验错误
-- 2 - 上位指令CRC校验错误
-- 3 - 托盘尺寸不对退回
-- 4 - 条码信息复核不对退回
-- 5 - 系统退回
-- 6 - 出库执行中库口被占用

