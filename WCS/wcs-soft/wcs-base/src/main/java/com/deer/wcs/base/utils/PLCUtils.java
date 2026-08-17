package com.deer.wcs.base.utils;


import com.deer.wcs.base.model.DeviceValue;
import com.deer.wcs.base.model.ValueData;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import com.github.xingshuangs.iot.protocol.s7.enums.EPlcType;
import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;
import com.github.xingshuangs.iot.protocol.modbus.service.ModbusTcp;
import com.github.xingshuangs.iot.protocol.melsec.enums.EMcSeries;
import com.github.xingshuangs.iot.protocol.melsec.service.McPLC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * PLC工具类 - 优化版本
 * @description: 提供PLC读写功能，包含连接池管理、批量读写、异常处理等优化
 * @author:zfj
 * @date:2024/5/22 17:22
 */
@Service
public class PLCUtils {

    private static final Logger logger = LoggerFactory.getLogger(PLCUtils.class);
    
    @Autowired
    private RedisCache redisCache;
    
    // 连接池配置参数
    private static final int CONNECTION_TIMEOUT = 3000; // 连接超时时间（毫秒）
    private static final int IDLE_TIMEOUT = 60000; // 空闲连接超时时间（毫秒）
    private static final int MAX_RETRIES = 3; // 最大重试次数
    private static final long CLEANUP_INTERVAL = 30000; // 连接池清理间隔（毫秒）
    
    // 线程安全的连接池
    private static final ConcurrentHashMap<String, PLCConnection> S7_CONNECTION_POOL = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, PLCConnection> MODBUS_CONNECTION_POOL = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, PLCConnection> MC_CONNECTION_POOL = new ConcurrentHashMap<>();
    
    // 连接锁，防止并发创建连接
    private static final ConcurrentHashMap<String, ReentrantLock> CONNECTION_LOCKS = new ConcurrentHashMap<>();
    
    // 静态初始化块，启动连接池清理线程
    static {
        Thread cleanupThread = new Thread(PLCUtils::cleanupIdleConnections, "PLC-Connection-Cleanup");
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    private static String redisKey(DeviceValue value){
        return "device_value:" + value.getId() + value.getCode();
    }

    public DeviceValue plcReadFromRedis(DeviceValue value) {
        DeviceValue oldValue = redisCache.getCacheObject(redisKey(value));
        return oldValue;
    }
    
    /**
     * 批量读取PLC数据 - 优化版本
     * @param values 设备值列表
     * @return Map<Long, ValueData<Object>> - 设备ID到值数据的映射
     */
    public Map<Long, ValueData<Object>> batchReadPlc(List<DeviceValue> values) {
        // 参数校验
        if (values == null || values.isEmpty()) {
            logger.warn("Empty device values list for batch read");
            return new ConcurrentHashMap<>();
        }
        
        // 使用ConcurrentHashMap确保线程安全
        Map<Long, ValueData<Object>> result = new ConcurrentHashMap<>();
        
        try {
            // 按连接分组，减少连接创建次数
            Map<String, List<DeviceValue>> groupedValues = groupByConnection(values);
            
            // 使用并行流处理提高效率
            groupedValues.entrySet().parallelStream().forEach(entry -> {
                String key = entry.getKey();
                List<DeviceValue> deviceValues = entry.getValue();
                
                // 空指针检查
                if (deviceValues == null || deviceValues.isEmpty()) {
                    logger.warn("Empty device values group for connection: {}", key);
                    return;
                }
                
                DeviceValue firstValue = deviceValues.get(0);
                
                try {
                    for (DeviceValue value : deviceValues) {
                        if (value == null) {
                            logger.warn("Skipping null device value in batch read");
                            continue;
                        }
                        
                        try {
                            ValueData<Object> valueData = executeWithRetry(new PLCAction<ValueData<Object>>() {
                                @Override
                                public ValueData<Object> execute() throws Exception {
                                    return plcReadInternal(value);
                                }
                            }, MAX_RETRIES);
                            
                            if (valueData != null) {
                                result.put(value.getId(), valueData);
                                
                                // 更新Redis缓存（在plcReadInternal中已处理）
                            }
                        } catch (Exception e) {
                            logger.error("Failed to read device: {}, address: {}", value.getId(), value.getAddress(), e);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Batch read failed for connection group: {}", key, e);
                    // 清除可能有问题的连接
                    if (firstValue != null) {
                        try {
                            closeConnection(firstValue);
                        } catch (Exception ex) {
                            logger.warn("Failed to close problematic connection", ex);
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Batch read processing error", e);
        }
        
        return result;
    }

    public ValueData<Object> plcRead(DeviceValue value) {
        // 参数校验
        if (value == null || value.getIp() == null) {
            logger.error("Invalid DeviceValue: null or missing IP address");
            return null;
        }
        try {
            return executeWithRetry(new PLCAction<ValueData<Object>>() {
                @Override
                public ValueData<Object> execute() throws Exception {
                    return plcReadInternal(value);
                }
            }, MAX_RETRIES);
        } catch (Exception e) {
            logger.error("PLC read failed: {}, address: {}", value.getIp(), value.getAddress(), e);
            // 发生异常时关闭连接
            closeConnection(value);
            throw new ServiceException(e.getMessage());
        }
    }
    
    private ValueData<Object> plcReadInternal(DeviceValue value) throws Exception {
        ValueData<Object> valueData = null;
        switch (value.getComType()) {
            case 0://s7
                valueData = readS7(value);
                break;
            case 1://modbus tcp
                valueData = readModbus(value);
                break;
            case 2://mtqq
                // TODO: 实现MQTT协议
                break;
            case 3://opc da
                // TODO: 实现OPC DA协议
                break;
            case 4://opc ua
                // TODO: 实现OPC UA协议
                break;
            case 5://mc
                valueData = readMC(value);
                break;
        }
        
        if (valueData != null && redisCache != null) {
            // 更新Redis缓存
            DeviceValue oldValue = redisCache.getCacheObject(redisKey(value));
            if (oldValue != null) {
                value.setWriteValue(oldValue.getWriteValue());
                value.setWriteTime(oldValue.getWriteTime());
            }
            value.setReadValue(valueData.getData().toString());
            value.setReadTime(DateUtil.getNowDateTimeString());
            redisCache.setCacheObject(redisKey(value), value);
        }
        return valueData;
    }

    public void plcWrite(DeviceValue value, ValueData<Object> valueData) {
        // 参数校验
        if (value == null || value.getIp() == null || valueData == null || valueData.getData() == null) {
            logger.error("Invalid DeviceValue or ValueData: null or missing required fields");
            throw new IllegalArgumentException("Invalid DeviceValue or ValueData: null or missing required fields");
        }

        try {
            plcWriteInternal(value, valueData);
//            executeWithRetry(new PLCAction<Void>() {
//                @Override
//                public Void execute() throws Exception {
//                    plcWriteInternal(value, valueData);
//                    return null;
//                }
//            }, MAX_RETRIES);
        } catch (Exception e) {
            logger.error("PLC write failed: {}, address: {}", value.getIp(), value.getAddress(), e);

            // 发生异常时关闭连接
            closeConnection(value);
            throw new ServiceException(e.getMessage());
        }
    }
    
    private void plcWriteInternal(DeviceValue value, ValueData<Object> valueData) throws Exception {
        switch (value.getComType()) {
            case 0://s7
                writeS7(value, valueData);
                break;
            case 1://modbus tcp
                writeModbus(value, valueData);
                break;
            case 2://mtqq
                // TODO: 实现MQTT协议
                break;
            case 3://opc da
                // TODO: 实现OPC DA协议
                break;
            case 4://opc ua
                // TODO: 实现OPC UA协议
                break;
            case 5://mc
                writeMC(value, valueData);
                break;
        }
        
        // 更新Redis缓存，确保redisCache和valueData不为空
        if (valueData != null && valueData.getData() != null && redisCache != null) {
            try {
                DeviceValue oldValue = redisCache.getCacheObject(redisKey(value));
                if (oldValue != null) {
                    value.setReadValue(oldValue.getReadValue());
                    value.setReadTime(oldValue.getReadTime());
                }
                value.setWriteValue(valueData.getData().toString());
                value.setWriteTime(DateUtil.getNowDateTimeString());
                redisCache.setCacheObject(redisKey(value), value);
            } catch (Exception e) {
                logger.error("Failed to update Redis cache after PLC write", e);
            }
        }
    }
    
    // S7协议读写实现
    private ValueData<Object> readS7(DeviceValue value) throws Exception {
        S7PLC s7PLC = getS7Connection(value);
        ValueData<Object> valueData = null;
        try {
            switch (value.getPlcType()) {
                case 0: // boolean/BOOL
                    Boolean data1 = s7PLC.readBoolean(value.getAddress());
                    valueData = new ValueData<>(data1);
                    break;
                case 1: // int16/WORD/INT
                    Short data2 = s7PLC.readInt16(value.getAddress());
                    valueData = new ValueData<>(data2);
                    break;
                case 2: // unint16/WORD/UINT
                    Integer data3 = s7PLC.readUInt16(value.getAddress());
                    valueData = new ValueData<>(data3);
                    break;
                case 3: // int32/DWORD/DINT
                    Integer data4 = s7PLC.readInt32(value.getAddress());
                    valueData = new ValueData<>(data4);
                    break;
                case 4: // uint32/DWORD/UDINT
                    Long data5 = s7PLC.readUInt32(value.getAddress());
                    valueData = new ValueData<>(data5);
                    break;
                case 5: // float32/REAL
                    Float data6 = s7PLC.readFloat32(value.getAddress());
                    valueData = new ValueData<>(data6);
                    break;
                case 6: // float64/LREAL
                    Double data7 = s7PLC.readFloat64(value.getAddress());
                    valueData = new ValueData<>(data7);
                    break;
                case 7: // string
                    String data8 = s7PLC.readString(value.getAddress(), value.getLength());
                    valueData = new ValueData<>(data8);
                    break;
                case 8: // Byte
                    Byte data9 = s7PLC.readByte(value.getAddress());
                    valueData = new ValueData<>(data9);
                    break;
                // 其他类型...
            }
            // 更新连接最后使用时间
            updateConnectionLastUsed(value);
        } catch (Exception e) {
            // 连接可能已失效，需要关闭并移除
            closeConnection(value);
            throw e;
        }
        return valueData;
    }
    
    private void writeS7(DeviceValue value, ValueData<Object> valueData) throws Exception {
        S7PLC s7PLC = getS7Connection(value);
        try {
            switch (value.getPlcType()) {
                case 0: // boolean/BOOL
                    s7PLC.writeBoolean(value.getAddress(), (boolean) valueData.getData());
                    break;
                case 1: // int16/WORD/INT
                    s7PLC.writeInt16(value.getAddress(), (short) valueData.getData());
                    break;
                case 2: // unint16/WORD/UINT
                    s7PLC.writeUInt16(value.getAddress(), (short) valueData.getData());
                    break;
                case 3: // int32/DWORD/DINT
                    s7PLC.writeInt32(value.getAddress(), (int) valueData.getData());
                    break;
                case 4: // uint32/DWORD/UDINT
                    s7PLC.writeUInt32(value.getAddress(), (Long) valueData.getData());
                    break;
                case 5: // float32/REAL
                    s7PLC.writeFloat32(value.getAddress(), (Float) valueData.getData());
                    break;
                case 6: // float64/LREAL
                    s7PLC.writeFloat64(value.getAddress(), (Double) valueData.getData());
                    break;
                case 7: // string
                    s7PLC.writeString(value.getAddress(), valueData.getData().toString());
                    break;
                case 8: // byte
                    s7PLC.writeByte(value.getAddress(), (Byte) valueData.getData());
                    break;
                // 其他类型...
            }
            // 更新连接最后使用时间
            updateConnectionLastUsed(value);
        } catch (Exception e) {
            // 连接可能已失效，需要关闭并移除
            closeConnection(value);
            throw e;
        }
    }
    
    // Modbus协议读写实现
    private ValueData<Object> readModbus(DeviceValue value) throws Exception {
        ModbusTcp modbusTcp = getModbusConnection(value);
        ValueData<Object> valueData = null;
        try {
            switch (value.getModbusType()) {
                case 0: // 输出线圈
                    List<Boolean> coils = modbusTcp.readCoil(Integer.parseInt(value.getAddress()), value.getLength());
                    valueData = new ValueData<>(coils.get(0));
                    break;
                case 1: // 输入线圈
                    List<Boolean> discreteInputs = modbusTcp.readDiscreteInput(Integer.parseInt(value.getAddress()), value.getLength());
                    valueData = new ValueData<>(discreteInputs.get(0));
                    break;
                case 2: // 输入寄存器
                    // 根据PLC类型读取不同数据
                    valueData = readModbusRegister(modbusTcp, value);
                    break;
                case 3: // 保持寄存器
                    // 根据PLC类型读取不同数据
                    valueData = readModbusRegister(modbusTcp, value);
                    break;
            }
            // 更新连接最后使用时间
            updateConnectionLastUsed(value);
        } catch (Exception e) {
            // 连接可能已失效，需要关闭并移除
            closeConnection(value);
            throw e;
        }
        return valueData;
    }
    
    private ValueData<Object> readModbusRegister(ModbusTcp modbusTcp, DeviceValue value) throws Exception {
        ValueData<Object> valueData = null;
        switch (value.getPlcType()) {
            case 0: // boolean
                Boolean data1 = modbusTcp.readBoolean(Integer.parseInt(value.getAddress()), value.getBitIndex());
                valueData = new ValueData<>(data1);
                break;
            case 1: // int16
                Short data2 = modbusTcp.readInt16(Integer.parseInt(value.getAddress()));
                valueData = new ValueData<>(data2);
                break;
            case 2: // uint16
                Integer data3 = modbusTcp.readUInt16(Integer.parseInt(value.getAddress()));
                valueData = new ValueData<>(data3);
                break;
            case 3: // int32
                Integer data4 = modbusTcp.readInt32(Integer.parseInt(value.getAddress()));
                valueData = new ValueData<>(data4);
                break;
            case 4: // uint32
                Long data5 = modbusTcp.readUInt32(Integer.parseInt(value.getAddress()));
                valueData = new ValueData<>(data5);
                break;
            case 5: // float32
                Float data6 = modbusTcp.readFloat32(Integer.parseInt(value.getAddress()));
                valueData = new ValueData<>(data6);
                break;
            // 其他类型...
        }
        return valueData;
    }
    
    private void writeModbus(DeviceValue value, ValueData<Object> valueData) throws Exception {
        ModbusTcp modbusTcp = getModbusConnection(value);
        try {
            if (value.getModbusType() == 0) { // 输出线圈
                modbusTcp.writeCoil(Integer.parseInt(value.getAddress()), (boolean) valueData.getData());
            } else if (value.getModbusType() == 3) { // 保持寄存器
                switch (value.getPlcType()) {
                    case 1: // int16
                        modbusTcp.writeInt16(Integer.parseInt(value.getAddress()), (Short) valueData.getData());
                        break;
                    case 2: // uint16
                        modbusTcp.writeUInt16(Integer.parseInt(value.getAddress()), (Integer) valueData.getData());
                        break;
                    case 3: // int32
                        modbusTcp.writeInt32(Integer.parseInt(value.getAddress()), (Integer) valueData.getData());
                        break;
                    case 4: // uint32
                        modbusTcp.writeUInt32(Integer.parseInt(value.getAddress()), (Long) valueData.getData());
                        break;
                    case 5: // float32
                        modbusTcp.writeFloat32(Integer.parseInt(value.getAddress()), (Float) valueData.getData());
                        break;
                    // 其他类型...
                }
            }
            // 更新连接最后使用时间
            updateConnectionLastUsed(value);
        } catch (Exception e) {
            // 连接可能已失效，需要关闭并移除
            closeConnection(value);
            throw e;
        }
    }
    
    // MC协议读写实现
    private ValueData<Object> readMC(DeviceValue value) throws Exception {
        McPLC mcPLC = getMCConnection(value);
        ValueData<Object> valueData = null;
        try {
            switch (value.getPlcType()) {
                case 0: // boolean
                    Boolean data1 = mcPLC.readBoolean(value.getAddress());
                    valueData = new ValueData<>(data1);
                    break;
                case 1: // int16
                    Short data2 = mcPLC.readInt16(value.getAddress());
                    valueData = new ValueData<>(data2);
                    break;
                case 2: // uint16
                    Integer data3 = mcPLC.readUInt16(value.getAddress());
                    valueData = new ValueData<>(data3);
                    break;
                case 3: // int32
                    Integer data4 = mcPLC.readInt32(value.getAddress());
                    valueData = new ValueData<>(data4);
                    break;
                case 4: // uint32
                    Long data5 = mcPLC.readUInt32(value.getAddress());
                    valueData = new ValueData<>(data5);
                    break;
                case 5: // float32
                    Float data6 = mcPLC.readFloat32(value.getAddress());
                    valueData = new ValueData<>(data6);
                    break;
                case 6: // float64
                    Double data7 = mcPLC.readFloat64(value.getAddress());
                    valueData = new ValueData<>(data7);
                    break;
                case 7: // string
                    String data8 = mcPLC.readString(value.getAddress(), value.getLength());
                    valueData = new ValueData<>(data8);
                    break;
                // 其他类型...
            }
            // 更新连接最后使用时间
            updateConnectionLastUsed(value);
        } catch (Exception e) {
            // 连接可能已失效，需要关闭并移除
            closeConnection(value);
            throw e;
        }
        return valueData;
    }
    
    private void writeMC(DeviceValue value, ValueData<Object> valueData) throws Exception {
        McPLC mcPLC = getMCConnection(value);
        try {
            switch (value.getPlcType()) {
                case 0: // boolean
                    mcPLC.writeBoolean(value.getAddress(), (boolean) valueData.getData());
                    break;
                case 1: // int16
                    mcPLC.writeInt16(value.getAddress(), (Short) valueData.getData());
                    break;
                case 2: // uint16
                    mcPLC.writeUInt16(value.getAddress(), (Integer) valueData.getData());
                    break;
                case 3: // int32
                    mcPLC.writeInt32(value.getAddress(), (Integer) valueData.getData());
                    break;
                case 4: // uint32
                    mcPLC.writeUInt32(value.getAddress(), (Long) valueData.getData());
                    break;
                case 5: // float32
                    mcPLC.writeFloat32(value.getAddress(), (Float) valueData.getData());
                    break;
                case 6: // float64
                    mcPLC.writeFloat64(value.getAddress(), (Double) valueData.getData());
                    break;
                case 7: // string
                    mcPLC.writeString(value.getAddress(), valueData.getData().toString());
                    break;
                // 其他类型...
            }
            // 更新连接最后使用时间
            updateConnectionLastUsed(value);
        } catch (Exception e) {
            // 连接可能已失效，需要关闭并移除
          //  closeConnection(value);
            throw e;
        }
    }
    
    // 获取S7连接
    private S7PLC getS7Connection(DeviceValue value) throws Exception {
        String key = getConnectionKey(value);
        PLCConnection connection = S7_CONNECTION_POOL.get(key);
        
        // 检查连接是否存在、未过期且有效
        if (connection != null && !isConnectionExpired(connection) && isS7ConnectionValid((S7PLC) connection.getConnection())) {
            updateConnectionLastUsed(value);
            return (S7PLC) connection.getConnection();
        }
        
        // 获取连接锁
        ReentrantLock lock = CONNECTION_LOCKS.computeIfAbsent(key, k -> new ReentrantLock());
        lock.lock();
        
        try {
            // 双重检查
            connection = S7_CONNECTION_POOL.get(key);
            if (connection != null && !isConnectionExpired(connection) && isS7ConnectionValid((S7PLC) connection.getConnection())) {
                updateConnectionLastUsed(value);
                return (S7PLC) connection.getConnection();
            }
            
            // 关闭旧连接（如果存在）
            if (connection != null) {
                try {
                    ((S7PLC) connection.getConnection()).close();
                } catch (Exception e) {
                    logger.warn("Failed to close old S7 connection", e);
                }
            }
            
            // 创建新连接
            if (value.getPort() == null) {
                value.setPort(102);
            }
            
            EPlcType plcType;
            switch (value.getS7Type()) {
                case 0: plcType = EPlcType.S200; break;
                case 1: plcType = EPlcType.S200_SMART; break;
                case 2: plcType = EPlcType.S300; break;
                case 3: plcType = EPlcType.S400; break;
                case 4: plcType = EPlcType.S1200; break;
                case 5: plcType = EPlcType.S1500; break;
                case 6: plcType = EPlcType.SINUMERIK_828D; break;
                default: throw new RuntimeException("S7类型不正确！");
            }
            
            S7PLC s7PLC = new S7PLC(plcType, value.getIp(), value.getPort());
            connection = new PLCConnection(s7PLC);
            S7_CONNECTION_POOL.put(key, connection);
            logger.info("Created new S7 PLC connection: {}", key);
            return s7PLC;
        } catch (Exception e) {
            logger.error("Failed to create S7 connection to {}", key, e);
            throw e;
        } finally {
            lock.unlock();
            // 不再移除锁，避免频繁创建锁对象
            // 锁会随着连接池的清理而自动管理
        }
    }
    
    // 获取Modbus连接
    private ModbusTcp getModbusConnection(DeviceValue value) throws Exception {
        String key = getConnectionKey(value);
        PLCConnection connection = MODBUS_CONNECTION_POOL.get(key);
        
        // 检查连接是否存在且有效
        if (connection != null && !isConnectionExpired(connection)) {
            updateConnectionLastUsed(value);
            return (ModbusTcp) connection.getConnection();
        }
        
        // 获取连接锁
        ReentrantLock lock = CONNECTION_LOCKS.computeIfAbsent(key, k -> new ReentrantLock());
        lock.lock();
        
        try {
            // 双重检查
            connection = MODBUS_CONNECTION_POOL.get(key);
            if (connection != null && !isConnectionExpired(connection)) {
                updateConnectionLastUsed(value);
                return (ModbusTcp) connection.getConnection();
            }
            
            // 关闭旧连接（如果存在）
            if (connection != null) {
                try {
                    ((ModbusTcp) connection.getConnection()).close();
                } catch (Exception e) {
                    logger.warn("Failed to close old Modbus connection", e);
                }
            }
            
            // 创建新连接
            if (value.getPort() == null) {
                value.setPort(502);
            }
            ModbusTcp modbusTcp = new ModbusTcp(1, value.getIp(), value.getPort());
            connection = new PLCConnection(modbusTcp);
            MODBUS_CONNECTION_POOL.put(key, connection);
            logger.info("Created new Modbus TCP connection: {}", key);
            return modbusTcp;
        } catch (Exception e) {
            logger.error("Failed to create Modbus connection to {}", key, e);
            throw e;
        } finally {
            lock.unlock();
            // 不再移除锁，避免频繁创建锁对象
            // 锁会随着连接池的清理而自动管理
        }
    }
    
    // 获取MC连接
    private McPLC getMCConnection(DeviceValue value) throws Exception {
        String key = getConnectionKey(value);
        PLCConnection connection = MC_CONNECTION_POOL.get(key);
        
        // 检查连接是否存在且有效
        if (connection != null && !isConnectionExpired(connection)) {
            updateConnectionLastUsed(value);
            return (McPLC) connection.getConnection();
        }
        
        // 获取连接锁
        ReentrantLock lock = CONNECTION_LOCKS.computeIfAbsent(key, k -> new ReentrantLock());
        lock.lock();
        
        try {
            // 双重检查
            connection = MC_CONNECTION_POOL.get(key);
            if (connection != null && !isConnectionExpired(connection)) {
                updateConnectionLastUsed(value);
                return (McPLC) connection.getConnection();
            }
            
            // 关闭旧连接（如果存在）
            if (connection != null) {
                try {
                    ((McPLC) connection.getConnection()).close();
                } catch (Exception e) {
                    logger.warn("Failed to close old MC connection", e);
                }
            }
            
            // 创建新连接
            if (value.getPort() == null) {
                value.setPort(501);
            }
            McPLC mcPLC = new McPLC(EMcSeries.Q_L, value.getIp(), value.getPort());
            mcPLC.setConnectTimeout(CONNECTION_TIMEOUT);
            connection = new PLCConnection(mcPLC);
            MC_CONNECTION_POOL.put(key, connection);
            logger.info("Created new MC PLC connection: {}", key);
            return mcPLC;
        } catch (Exception e) {
            logger.error("Failed to create MC connection to {}", key, e);
            throw e;
        } finally {
            lock.unlock();
            // 不再移除锁，避免频繁创建锁对象
            // 锁会随着连接池的清理而自动管理
        }
    }
    
    // 关闭连接
    private void closeConnection(DeviceValue value) {
        String key = getConnectionKey(value);
        switch (value.getComType()) {
            case 0: // s7
//                PLCConnection s7Conn = S7_CONNECTION_POOL.remove(key);
//                if (s7Conn != null) {
//                    try {
//                        ((S7PLC) s7Conn.getConnection()).close();
//                        logger.info("Closed S7 PLC connection: {}", key);
//                    } catch (Exception e) {
//                        logger.warn("Failed to close S7 connection", e);
//                    }
//                }
//                break;
            case 1: // modbus
                PLCConnection modbusConn = MODBUS_CONNECTION_POOL.remove(key);
                if (modbusConn != null) {
                    try {
                        ((ModbusTcp) modbusConn.getConnection()).close();
                        logger.info("Closed Modbus connection: {}", key);
                    } catch (Exception e) {
                        logger.warn("Failed to close Modbus connection", e);
                    }
                }
                break;
            case 5: // mc
                PLCConnection mcConn = MC_CONNECTION_POOL.remove(key);
                if (mcConn != null) {
                    try {
                        ((McPLC) mcConn.getConnection()).close();
                        logger.info("Closed MC PLC connection: {}", key);
                    } catch (Exception e) {
                        logger.warn("Failed to close MC connection", e);
                    }
                }
                break;
        }
    }
    
    // 获取连接键
    private String getConnectionKey(DeviceValue value) {
        if (value.getPort() == null) {
            if (value.getComType() == 0) { // s7默认端口
                value.setPort(102);
            } else if (value.getComType() == 1) { // modbus默认端口
                value.setPort(502);
            }
        }
        return value.getComType() + ":" + value.getIp() + ":" + value.getPort();
    }
    
    // 检查连接是否超时
    private boolean isConnectionExpired(PLCConnection connection) {
        return System.currentTimeMillis() - connection.getLastUsedTime() > IDLE_TIMEOUT;
    }
    
    // 检查S7连接是否有效
    private boolean isS7ConnectionValid(S7PLC s7PLC) {
        if (s7PLC == null) {
            return false;
        }
        
        try {
            // 尝试执行一个轻量级操作来验证连接
            // 注意：这里不实际读取数据，只是检查连接是否可用
            // 如果连接已断开，任何操作都会抛出异常
            return true; // 由于S7PLC类似乎没有公开的isConnected方法，我们依赖于异常处理
            // 在实际环境中，可以尝试读取一个已知的状态位或进行更精确的检查
        } catch (Exception e) {
            logger.warn("S7 connection validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    // 更新连接最后使用时间
    private void updateConnectionLastUsed(DeviceValue value) {
        String key = getConnectionKey(value);
        PLCConnection connection = null;
        
        switch (value.getComType()) {
            case 0: connection = S7_CONNECTION_POOL.get(key); break;
            case 1: connection = MODBUS_CONNECTION_POOL.get(key); break;
            case 5: connection = MC_CONNECTION_POOL.get(key); break;
        }
        
        if (connection != null) {
            connection.setLastUsedTime(System.currentTimeMillis());
        }
    }
    
    // 按连接分组设备值
    private Map<String, List<DeviceValue>> groupByConnection(List<DeviceValue> values) {
        Map<String, List<DeviceValue>> groups = new HashMap<>();
        
        for (DeviceValue value : values) {
            String key = getConnectionKey(value);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        
        return groups;
    }
    
    // 带重试的执行方法
    private <T> T executeWithRetry(PLCAction<T> task, int maxRetries) throws Exception {
        Exception lastException = null;
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                return task.execute();
            } catch (Exception e) {
                lastException = e;
                logger.warn("Operation failed, retry {}/{}", i + 1, maxRetries, e);
                // 指数退避策略
                if (i < maxRetries - 1) {
                    long delay = (long) (100 * Math.pow(2, i));
                    TimeUnit.MILLISECONDS.sleep(delay);
                }
            }
        }
        
        throw lastException; // 抛出最后一次异常
    }
    
    // PLC连接包装类
    private static class PLCConnection {
        private final Object connection;
        private long lastUsedTime;
        
        public PLCConnection(Object connection) {
            this.connection = connection;
            this.lastUsedTime = System.currentTimeMillis();
        }
        
        public Object getConnection() {
            return connection;
        }
        
        public long getLastUsedTime() {
            return lastUsedTime;
        }
        
        public void setLastUsedTime(long lastUsedTime) {
            this.lastUsedTime = lastUsedTime;
        }
        
        // 检查连接是否有效（可根据具体协议扩展）
        public boolean isValid() {
            // 默认返回true，可根据不同协议实现具体检查逻辑
            return true;
        }
    }
    
    // 定期清理空闲连接
    private static void cleanupIdleConnections() {
        try {
            while (true) {
                Thread.sleep(CLEANUP_INTERVAL);
                
             //   cleanupPool(S7_CONNECTION_POOL);
                cleanupPool(MODBUS_CONNECTION_POOL);
                cleanupPool(MC_CONNECTION_POOL);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // 使用静态logger，安全
            LoggerFactory.getLogger(PLCUtils.class).warn("Connection cleanup thread interrupted", e);
        }
    }
    
    // 清理单个连接池
    private static void cleanupPool(ConcurrentHashMap<String, PLCConnection> pool) {
        Logger staticLogger = LoggerFactory.getLogger(PLCUtils.class);
        long now = System.currentTimeMillis();
        
        for (Map.Entry<String, PLCConnection> entry : new HashMap<>(pool).entrySet()) {
            String key = entry.getKey();
            PLCConnection connection = entry.getValue();
            
            if (now - connection.getLastUsedTime() > IDLE_TIMEOUT || !connection.isValid()) {
                try {
                    // 根据连接类型关闭连接
                    Object connObj = connection.getConnection();
                    if (connObj instanceof S7PLC) {
                        ((S7PLC) connObj).close();
                    } else if (connObj instanceof ModbusTcp) {
                        ((ModbusTcp) connObj).close();
                    } else if (connObj instanceof McPLC) {
                        ((McPLC) connObj).close();
                    }
                    
                    pool.remove(key);
                    staticLogger.info("Cleaned up idle connection: {}", key);
                } catch (Exception e) {
                    staticLogger.warn("Failed to close idle connection: {}", key, e);
                    pool.remove(key);
                }
            }
        }
    }
    
    // 内部接口，用于重试机制
    private interface PLCAction<T> {
        T execute() throws Exception;
    }

}
