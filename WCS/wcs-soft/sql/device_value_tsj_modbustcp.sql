-- device_value 表插入脚本
-- 设备编码: tsj (提升机)
-- 通讯协议: ModbusTCP
-- 
-- 重要说明 - Modbus地址:
--   图片中提供的地址(如41138)已经是协议地址，直接使用即可
--   无需进行 -40001 的转换
--
-- 字段说明:
--   com_type: 1 = ModbusTCP
--   modbus_type: 3 = 保持寄存器
--   type: 0=读取(PLC->WCS), 1=写入(WCS->PLC)
--   plc_type: 1=int16/INT, 2=uint16/WORD, 3=int32/DINT
--   java_type: 2=Integer, 3=Short
--   length: Int/Word类型=1, DInt类型=2
--
-- 通信方向:
--   PLC -> WCS (type=0): 提升机状态反馈给WCS (19个字段, 地址41138-41182)
--   WCS -> PLC (type=1): WCS控制指令发送给提升机 (10个字段, 地址41088-41108)

-- ================================================================================
-- PLC -> WCS 方向 (读取数据)
-- type = 0 表示从PLC读取状态数据
-- ================================================================================

-- 1. 心跳 (0~127, 1000ms切换)
-- Modbus地址: 41138
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('heartbeat', '心跳', 0, 'tsj', 1, 3, '41138', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 2. 联机状态 (0=未联机, 1=联机)
-- Modbus地址: 41140
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('online_status', '联机状态', 0, 'tsj', 1, 3, '41140', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 3. 空闲状态 (0=初值, 1=空闲)
-- Modbus地址: 41142
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('idle_status', '空闲状态', 0, 'tsj', 1, 3, '41142', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 4. 接收任务反馈 (已接收任务号, 不做清除, CRC校验正确, 覆盖上一个任务号)
-- Modbus地址: 41144
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('receive_task_feedback', '接收任务反馈', 0, 'tsj', 1, 3, '41144', 3, 2, 2, 
        NOW(), 1, 'admin', 0, 0);

-- 5. 任务号 (当前任务号, 发99需要做清除)
-- Modbus地址: 41148
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('task_number', '任务号', 0, 'tsj', 1, 3, '41148', 3, 2, 2, 
        NOW(), 1, 'admin', 0, 0);

-- 6. 任务状态 (0-未完成, 1-执行中, 2-已完成, 需要和任务号保持一致, 发99需要做清除)
-- Modbus地址: 41152
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('task_status', '任务状态', 0, 'tsj', 1, 3, '41152', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 7. 动作类型 (1-移动(默认); 2-左取货; 3-左卸货; 4-右取货; 5-右卸货; 99-释放; 101-小车进入; 102-小车离开)
-- Modbus地址: 41154
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('action_type', '动作类型', 0, 'tsj', 1, 3, '41154', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 8. 当前楼层 (提升机当前所在楼层)
-- Modbus地址: 41156
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('current_floor', '当前楼层', 0, 'tsj', 1, 3, '41156', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 9. 到位信号 (0=未到位, 1=到位)
-- Modbus地址: 41158
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('arrival_signal', '到位信号', 0, 'tsj', 1, 3, '41158', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 10. 小车信号 (0=无车, 1=有车)
-- Modbus地址: 41160
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('cart_signal', '小车信号', 0, 'tsj', 1, 3, '41160', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 11. 货物信号 (0=无货, 1=有货)
-- Modbus地址: 41162
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('cargo_signal', '货物信号', 0, 'tsj', 1, 3, '41162', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 12. 运行状态 (0=停止, 1-链条正转, 2-链条反转, 3-上升, 4-下降)
-- Modbus地址: 41164
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('running_status', '运行状态', 0, 'tsj', 1, 3, '41164', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 13. 电阻挡信号 (0=初值, 1=升起状态, 2=落下状态)
-- Modbus地址: 41166
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('resistance_block_signal', '电阻挡信号', 0, 'tsj', 1, 3, '41166', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 14. 传感器状态 (见传感器状态明细, 参考L-M-1)
-- Modbus地址: 41168
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('sensor_status', '传感器状态', 0, 'tsj', 1, 3, '41168', 3, 2, 2, 
        NOW(), 1, 'admin', 0, 0);

-- 15. 报警代码 (0-无报警; 1~255, PLC方提供, 参考L-M-2)
-- Modbus地址: 41172
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('alarm_code', '报警代码', 0, 'tsj', 1, 3, '41172', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 16. 层高数据 (单位mm)
-- Modbus地址: 41174
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('floor_height_data', '层高数据', 0, 'tsj', 1, 3, '41174', 3, 2, 2, 
        NOW(), 1, 'admin', 0, 0);

-- 17. 托盘类型 (1-正常托盘(默认); 2-特殊托盘)
-- Modbus地址: 41178
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('pallet_type', '托盘类型', 0, 'tsj', 1, 3, '41178', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 18. 目标楼层 (任务目标楼层, 无任务的时候为0)
-- Modbus地址: 41180
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('target_floor', '目标楼层', 0, 'tsj', 1, 3, '41180', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 19. 备用
-- Modbus地址: 41182
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('reserved', '备用', 0, 'tsj', 1, 3, '41182', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- ================================================================================
-- WCS -> PLC 方向 (写入数据)
-- type = 1 表示WCS向PLC写入数据
-- ================================================================================

-- 20. 任务号 (WCS下发的任务号, taskID, 唯一)
-- Modbus地址: 41088
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('task_id_write', '任务号(写)', 1, 'tsj', 1, 3, '41088', 3, 2, 2, 
        NOW(), 1, 'admin', 0, 0);

-- 21. 小车信号 (当前任务是否有小车: 0=无车, 1=有车)
-- Modbus地址: 41092
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('cart_signal_write', '小车信号(写)', 1, 'tsj', 1, 3, '41092', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 22. 货物信号 (当前任务是否有货物: 0=无货, 1=有货)
-- Modbus地址: 41094
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('cargo_signal_write', '货物信号(写)', 1, 'tsj', 1, 3, '41094', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 23. 动作类型 (1-移动(默认); 2-左取货; 3-左卸货; 4-右取货; 5-右卸货; 99-释放; 101-小车进入; 102-小车离开)
-- Modbus地址: 41096
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('action_type_write', '动作类型(写)', 1, 'tsj', 1, 3, '41096', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 24. 目标楼层 (目标楼层)
-- Modbus地址: 41098
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('target_floor_write', '目标楼层(写)', 1, 'tsj', 1, 3, '41098', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 25. 托盘类型 (1-正常托盘(默认); 2-特殊托盘)
-- Modbus地址: 41100
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('pallet_type_write', '托盘类型(写)', 1, 'tsj', 1, 3, '41100', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 26. CRC校验码 (对设备编号前面的数据CRC-16/MODBUS校验)
-- Modbus地址: 41102
-- 数据类型: Word (无符号16位)
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('crc_checksum', 'CRC校验码', 1, 'tsj', 1, 3, '41102', 2, 2, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 27. 复位信号 (0=初值, 1=复位)
-- Modbus地址: 41104
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('reset_signal', '复位信号', 1, 'tsj', 1, 3, '41104', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 28. 上位急停 (0=初值, 1=上位机急停)
-- Modbus地址: 41106
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('emergency_stop', '上位急停', 1, 'tsj', 1, 3, '41106', 1, 3, 1, 
        NOW(), 1, 'admin', 0, 0);

-- 29. 备用 (保留字段)
-- Modbus地址: 41108
INSERT INTO device_value (code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length, 
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('reserved_write', '备用(写)', 1, 'tsj', 1, 3, '41108', 3, 2, 2, 
        NOW(), 1, 'admin', 0, 0);

-- ================================================================================
-- 查询验证
-- ================================================================================
-- 查看所有tsj设备的配置 (按类型和地址排序)
SELECT id, code, name, type, device_code, com_type, modbus_type, address, plc_type, java_type, length 
FROM device_value 
WHERE device_code = 'tsj' 
ORDER BY type, CAST(address AS UNSIGNED);

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
WHERE device_code = 'tsj' 
GROUP BY type;

