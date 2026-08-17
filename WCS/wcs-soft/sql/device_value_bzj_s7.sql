-- device_info 和 device_value 表插入脚本
-- 设备编码: BZ_ROBOT1 (包装机1号站台) 和 BZ_ROBOT2 (包装机2号站台)
-- 通讯协议: S7
-- 
-- 字段说明:
--   com_type: 0 = S7
--   s7_type: 需要根据实际PLC型号配置 (0=S200, 1=S200_SMART, 2=S300, 3=S400, 4=S1200, 5=S1500)
--   type: 0=读取(PLC->WCS), 1=写入(WCS->PLC)
--   plc_type: 1=int16/INT, 2=uint16/WORD, 3=int32/DINT, 5=float32/REAL
--   java_type: 2=Integer, 3=Short, 5=Float
--   length: Int类型=1, DInt类型=2, Real类型=2, String根据实际长度
--   address: S7地址格式，如 DB100.DBW0, DB100.DBD0, DB100.DBR0
--
-- 注意: 图片中的D区地址需要转换为S7的DB块地址格式
-- D5000-D5099 对应 DB100 (包装机1号站台)
-- D6000-D6099 对应 DB200 (包装机2号站台)
-- D5100-D5199 对应 DB101 (包装机1号站台 WMS->PLC)
-- D6100-D6199 对应 DB201 (包装机2号站台 WMS->PLC)

-- ================================================================================
-- 第一部分: 插入设备信息 (device_info)
-- ================================================================================

-- 包装机1号站台
INSERT INTO device_info (code, name, type, com_type, s7_type, ip, port, is_online, state, 
                         create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('BZ_ROBOT1', '包装机1号站台', 0, 0, 4, '192.168.1.100', 102, 0, 0,
        NOW(), 1, 'admin', 0, 0);

-- 包装机2号站台
INSERT INTO device_info (code, name, type, com_type, s7_type, ip, port, is_online, state,
                         create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('BZ_ROBOT2', '包装机2号站台', 0, 0, 4, '192.168.1.101', 102, 0, 0,
        NOW(), 1, 'admin', 0, 0);

-- ================================================================================
-- 第二部分: 包装机1号站台 (BZ_ROBOT1) - PLC->WCS 方向 (读取数据)
-- type = 0 表示从PLC读取状态数据
-- 地址范围: D5000-D5099 (对应 DB100)
-- ================================================================================

-- 1. 框子二维码号 (25个short, D5000-D5024)
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_1', '框子二维码号1', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW0', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_2', '框子二维码号2', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW2', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_3', '框子二维码号3', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW4', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_4', '框子二维码号4', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW6', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_5', '框子二维码号5', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW8', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_6', '框子二维码号6', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW10', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_7', '框子二维码号7', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW12', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_8', '框子二维码号8', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW14', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_9', '框子二维码号9', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW16', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_10', '框子二维码号10', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW18', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_11', '框子二维码号11', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW20', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_12', '框子二维码号12', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW22', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_13', '框子二维码号13', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW24', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_14', '框子二维码号14', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW26', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_15', '框子二维码号15', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW28', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_16', '框子二维码号16', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW30', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_17', '框子二维码号17', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW32', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_18', '框子二维码号18', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW34', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_19', '框子二维码号19', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW36', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_20', '框子二维码号20', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW38', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_21', '框子二维码号21', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW40', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_22', '框子二维码号22', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW42', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_23', '框子二维码号23', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW44', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_24', '框子二维码号24', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW46', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_25', '框子二维码号25', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW48', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 2. 物料布卷条码号 (25个short, D5025-D5049)
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_1', '物料布卷条码号1', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW50', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_2', '物料布卷条码号2', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW52', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_3', '物料布卷条码号3', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW54', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_4', '物料布卷条码号4', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW56', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_5', '物料布卷条码号5', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW58', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_6', '物料布卷条码号6', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW60', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_7', '物料布卷条码号7', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW62', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_8', '物料布卷条码号8', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW64', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_9', '物料布卷条码号9', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW66', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_10', '物料布卷条码号10', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW68', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_11', '物料布卷条码号11', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW70', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_12', '物料布卷条码号12', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW72', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_13', '物料布卷条码号13', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW74', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_14', '物料布卷条码号14', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW76', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_15', '物料布卷条码号15', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW78', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_16', '物料布卷条码号16', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW80', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_17', '物料布卷条码号17', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW82', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_18', '物料布卷条码号18', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW84', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_19', '物料布卷条码号19', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW86', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_20', '物料布卷条码号20', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW88', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_21', '物料布卷条码号21', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW90', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_22', '物料布卷条码号22', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW92', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_23', '物料布卷条码号23', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW94', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_24', '物料布卷条码号24', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW96', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_25', '物料布卷条码号25', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW98', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 3. 是否有料箱 (1没箱子 2有箱子) - D5050
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('has_material_box', '是否有料箱', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW100', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 4. 请求WMS保存此卷信息 (1不保存 2保存) - D5051
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('request_save_roll_info', '请求WMS保存此卷信息', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW102', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 5. 布卷重量 (real类型, D5052-D5053)
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('fabric_roll_weight', '布卷重量', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBR104', 5, 5, 2,
        NOW(), 1, 'admin', 0, 0);

-- 6. 请求WMS AGV任务 (1要空框 2送满框) - D5054
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('request_agv_task', '请求WMS AGV任务', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW108', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 7. 是否允许取货 (1不允许 2允许) - D5055
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('allow_picking', '是否允许取货', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW110', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 8. 是否允许放货 (1不允许 2允许) - D5056
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('allow_putting', '是否允许放货', 0, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB100.DBW112', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- ================================================================================
-- 第三部分: 包装机1号站台 (BZ_ROBOT1) - WMS->PLC 方向 (写入数据)
-- type = 1 表示WCS向PLC写入控制指令
-- 地址范围: D5100-D5199 (对应 DB101)
-- ================================================================================

-- 1. 物料布卷保存 (1未保存 2保存) - D5100
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_saved', '物料布卷保存', 1, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB101.DBW0', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 2. AGV任务已呼叫 (1空框任务 2满框任务) - D5101
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('agv_task_called', 'AGV任务已呼叫', 1, 'BZ_ROBOT1', '包装机1号站台', 0, 4, 'DB101.DBW2', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- ================================================================================
-- 第四部分: 包装机2号站台 (BZ_ROBOT2) - PLC->WCS 方向 (读取数据)
-- type = 0 表示从PLC读取状态数据
-- 地址范围: D6000-D6099 (对应 DB200)
-- ================================================================================

-- 1. 框子二维码号 (25个short, D6000-D6024)
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_1', '框子二维码号1', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW0', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_2', '框子二维码号2', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW2', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_3', '框子二维码号3', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW4', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_4', '框子二维码号4', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW6', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_5', '框子二维码号5', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW8', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_6', '框子二维码号6', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW10', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_7', '框子二维码号7', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW12', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_8', '框子二维码号8', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW14', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_9', '框子二维码号9', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW16', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_10', '框子二维码号10', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW18', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_11', '框子二维码号11', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW20', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_12', '框子二维码号12', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW22', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_13', '框子二维码号13', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW24', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_14', '框子二维码号14', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW26', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_15', '框子二维码号15', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW28', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_16', '框子二维码号16', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW30', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_17', '框子二维码号17', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW32', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_18', '框子二维码号18', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW34', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_19', '框子二维码号19', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW36', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_20', '框子二维码号20', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW38', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_21', '框子二维码号21', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW40', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_22', '框子二维码号22', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW42', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_23', '框子二维码号23', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW44', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_24', '框子二维码号24', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW46', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('box_qr_code_25', '框子二维码号25', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW48', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 2. 物料布卷条码号 (25个short, D6025-D6049)
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_1', '物料布卷条码号1', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW50', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_2', '物料布卷条码号2', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW52', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_3', '物料布卷条码号3', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW54', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_4', '物料布卷条码号4', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW56', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_5', '物料布卷条码号5', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW58', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_6', '物料布卷条码号6', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW60', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_7', '物料布卷条码号7', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW62', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_8', '物料布卷条码号8', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW64', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_9', '物料布卷条码号9', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW66', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_10', '物料布卷条码号10', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW68', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_11', '物料布卷条码号11', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW70', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_12', '物料布卷条码号12', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW72', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_13', '物料布卷条码号13', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW74', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_14', '物料布卷条码号14', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW76', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_15', '物料布卷条码号15', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW78', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_16', '物料布卷条码号16', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW80', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_17', '物料布卷条码号17', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW82', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_18', '物料布卷条码号18', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW84', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_19', '物料布卷条码号19', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW86', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_20', '物料布卷条码号20', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW88', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_21', '物料布卷条码号21', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW90', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_22', '物料布卷条码号22', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW92', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_23', '物料布卷条码号23', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW94', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_24', '物料布卷条码号24', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW96', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_barcode_25', '物料布卷条码号25', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW98', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 3. 是否有料箱 (1没箱子 2有箱子) - D6050
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('has_material_box', '是否有料箱', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW100', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 4. 请求WMS保存此卷信息 (1不保存 2保存) - D6051
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('request_save_roll_info', '请求WMS保存此卷信息', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW102', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 5. 布卷重量 (real类型, D6052-D6053)
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('fabric_roll_weight', '布卷重量', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBR104', 5, 5, 2,
        NOW(), 1, 'admin', 0, 0);

-- 6. 请求WMS AGV任务 (1要空框 2送满框) - D6054
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('request_agv_task', '请求WMS AGV任务', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW108', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 7. 是否允许取货 (1不允许 2允许) - D6055
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('allow_picking', '是否允许取货', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW110', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 8. 是否允许放货 (1不允许 2允许) - D6056
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('allow_putting', '是否允许放货', 0, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB200.DBW112', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- ================================================================================
-- 第五部分: 包装机2号站台 (BZ_ROBOT2) - WMS->PLC 方向 (写入数据)
-- type = 1 表示WCS向PLC写入控制指令
-- 地址范围: D6100-D6199 (对应 DB201)
-- ================================================================================

-- 1. 物料布卷保存 (1未保存 2保存) - D6100
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('material_roll_saved', '物料布卷保存', 1, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB201.DBW0', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- 2. AGV任务已呼叫 (1空框任务 2满框任务) - D6101
INSERT INTO device_value (code, name, type, device_code, device_name, com_type, s7_type, address, plc_type, java_type, length,
                          create_time, create_user_id, create_user_name, version, is_delete)
VALUES ('agv_task_called', 'AGV任务已呼叫', 1, 'BZ_ROBOT2', '包装机2号站台', 0, 4, 'DB201.DBW2', 1, 3, 1,
        NOW(), 1, 'admin', 0, 0);

-- ================================================================================
-- 查询验证
-- ================================================================================
-- 查看所有包装机设备的配置 (按设备编码和类型排序)
SELECT id, code, name, type, device_code, com_type, s7_type, address, plc_type, java_type, length 
FROM device_value 
WHERE device_code IN ('BZ_ROBOT1', 'BZ_ROBOT2')
ORDER BY device_code, type, id;

-- 统计信息
SELECT 
    device_code,
    type,
    CASE type 
        WHEN 0 THEN 'PLC->WCS(读取)'
        WHEN 1 THEN 'WCS->PLC(写入)'
        ELSE '未知'
    END AS direction,
    COUNT(*) AS count
FROM device_value 
WHERE device_code IN ('BZ_ROBOT1', 'BZ_ROBOT2')
GROUP BY device_code, type
ORDER BY device_code, type;

-- 数据点说明:
-- 框子二维码号: 25个short类型数据点，存储框子的二维码信息
-- 物料布卷条码号: 25个short类型数据点，存储物料布卷的条码信息
-- 是否有料箱: 1=没箱子, 2=有箱子
-- 请求WMS保存此卷信息: 1=不保存, 2=保存
-- 布卷重量: real类型，存储布卷的重量
-- 请求WMS AGV任务: 1=要空框, 2=送满框
-- 是否允许取货: 1=不允许, 2=允许
-- 是否允许放货: 1=不允许, 2=允许
-- 物料布卷保存: 1=未保存, 2=保存
-- AGV任务已呼叫: 1=空框任务, 2=满框任务

