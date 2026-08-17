package com.deer.wcs.base.utils;

import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.base.model.DeviceValue;
import com.deer.wcs.base.model.ValueData;
import com.deer.wcs.common.utils.ByteUtils;
import com.github.xingshuangs.iot.protocol.modbus.service.ModbusTcp;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用iot-communication兼容s7协议和modbus协议
 * 默认为大端模式
 * @author: zfj
 * @date: 2024/5/16 17:10
 */
public class ModbusPLCUtils {

    public static void main(String[] args) {
        ModbusTcp modbusTcp = ModbusClientFactory.create("127.0.0.1", 502, 1);
        // List<Boolean> obj = modbusTcp.readCoil(0, 4);
        byte[] obj = modbusTcp.readHoldRegister(0, 4);
        List<Integer> aaa = new ArrayList<>();
        for (int i = 0; i < obj.length; i++) {
            if (i % 2 == 0) {
                byte[] bytes = {obj[i], obj[i + 1]}; // 示例byte数组，表示-32768（16位有符号整数的最小值）
                int int16 = ByteBuffer.wrap(bytes).getShort();
                aaa.add(int16);
            }

        }
        String bbb = JSONObject.toJSONString(aaa);
        System.out.println(bbb);
    }

    private DeviceValue addDeviceValue() {
        DeviceValue value = new DeviceValue();
        value.setComType(1);
        value.setIp("127.0.0.1");
//        value.setPort(502);
        value.setAddress("100");
        value.setBitIndex(0);
        value.setModbusType(1);
        value.setPlcType(0);
        value.setJavaType(0);
        value.setLength(1);
        return value;
    }

    @Test
    public void test() {
        DeviceValue value = addDeviceValue();
        value.setModbusType(3);
        value.setAddress("200");
        value.setPlcType(7);
        value.setJavaType(7);
        ValueData<Object> valueDataRead = modbusRead(value);
        System.out.println(valueDataRead.getData());

        ValueData<String> valueDataWrite = new ValueData<>("happyNewYear");
        modbusWrite(value, valueDataWrite);
        System.out.println(valueDataWrite.getData());

        ValueData<Object> valueDataRead2 = modbusRead(value);
        System.out.println(valueDataRead2.getData());
    }


    /**
     * 根据属性的java类型来读取和写入plc数据
     *
     * @param value 设备值
     * @return
     */
    public static ValueData<Object> modbusRead(DeviceValue value) {
        if (value.getPort() == null) {
            value.setPort(502);
        }
        ModbusTcp modbusTcp =   ModbusClientFactory.create(value.getIp(),value.getPort(),1);
       // ModbusTcp modbusTcp = new ModbusTcp(1, value.getIp(), value.getPort());
        ValueData<Object> obj = null;
        /*
            valueStyle 代表modbus对应的
            0---输出线圈
            1---输入线圈
            2---输入寄存器
            3---保持寄存器
         */
        switch (value.getModbusType()) {
            case 0:
                obj = modbusReadOutCoil(modbusTcp, value);
                break;
            case 1:
                obj = modbusReadInCoil(modbusTcp, value);
                break;
            case 2:
                obj = modbusReadInRegister(modbusTcp, value);
                break;
            case 3:
                obj = modbusReadHoldRegister(modbusTcp, value);
                break;
            default:
                break;
        }
        modbusTcp.close();
        return obj;
    }

    public static void modbusWrite(DeviceValue value, ValueData data) {
        if (value.getPort() == null) {
            value.setPort(502);
        }
        ModbusTcp modbusTcp = ModbusClientFactory.create(value.getIp(), value.getPort(), 1);
        // ModbusTcp modbusTcp = new ModbusTcp(1, value.getIp(), value.getPort());
        Object obj = null;
        /*
            valueStyle 代表modbus对应的
            0---输出线圈
            1---输入线圈        只读
            2---输入寄存器      只读
            3---保持寄存器
         */
        if (value.getModbusType() == 0) {
            // 确保data是Boolean类型并进行正确的类型转换
            if (data.getData() instanceof Boolean) {
                ValueData<Boolean> booleanData = new ValueData<>((Boolean) data.getData());
                modbusWriteCoil(modbusTcp, value, booleanData);
            } else {
                throw new IllegalArgumentException("Modbus输出线圈写入需要Boolean类型数据");
            }
        } else if (value.getModbusType() == 3) {
            modbusWriteHoldRegister(modbusTcp, value, data);
        }
        modbusTcp.close();
    }

    /**
     * 功能码  01H
     * 读取输出线圈
     *
     * @param value
     * @return
     */
    public static ValueData<Object> modbusReadOutCoil(ModbusTcp modbusTcp, DeviceValue value) {
        List<Boolean> obj = modbusTcp.readCoil(Integer.parseInt(value.getAddress()), value.getLength());
        ValueData<Object> valueData = new ValueData<>(obj.get(0));
        return valueData;
    }

    /**
     * 功能码  02H
     * 读取输入线圈
     *
     * @param value
     * @return
     */
    public static ValueData<Object> modbusReadInCoil(ModbusTcp modbusTcp, DeviceValue value) {
        List<Boolean> obj = modbusTcp.readDiscreteInput(Integer.parseInt(value.getAddress()), value.getLength());
        ValueData<Object> valueData = new ValueData<>(obj.get(0));
        return valueData;
    }

    /**
     * 功能码  03H
     * 读取保存寄存器
     *
     * @param value
     * @return
     */
    public static ValueData<Object> modbusReadHoldRegister(ModbusTcp modbusTcp, DeviceValue value) {
        Integer plcType = value.getPlcType();
        ValueData<Object> valueData = null;
        switch (plcType) {
            case 0:
                //boolean/BOOL
                Boolean data1 = modbusTcp.readBoolean(Integer.parseInt(value.getAddress()), value.getBitIndex());
                valueData = new ValueData<>(data1);
                break;
            case 1:
                //int16/WORD/INT
                Short data2 = modbusTcp.readInt16(Integer.parseInt(value.getAddress()));
                valueData = new ValueData<>(data2);
                break;
            case 2:
                //unint16/WORD/UINT
                Integer data3 = modbusTcp.readUInt16(Integer.parseInt(value.getAddress()));
                valueData = new ValueData<>(data3);
                break;
            case 3:
                //int32/DWORD/DINT
                Integer data4 = modbusTcp.readInt32(Integer.parseInt(value.getAddress()));
                valueData = new ValueData<Object>(data4);
                break;
            case 4:
                //uint32/DWORD/UDINT
                Long data5 = modbusTcp.readUInt32(Integer.parseInt(value.getAddress()));
                valueData = new ValueData<Object>(data5);
                break;
            case 5:
                //float32/REAL
                Float data6 = modbusTcp.readFloat32(Integer.parseInt(value.getAddress()));
                valueData = new ValueData<Object>(data6);
                break;
            case 6:
                //float64/LREAL
                Double data7 = modbusTcp.readFloat64(Integer.parseInt(value.getAddress()));
                valueData = new ValueData<Object>(data7);
                break;
            case 7:
                //string
                String data8 = modbusTcp.readString(Integer.parseInt(value.getAddress()), value.getLength() * 2);
                valueData = new ValueData<Object>(data8);
                break;
            default:
                break;
        }
        return valueData;
    }

    /**
     * 功能码  04H
     * 读取输入寄存器
     *
     * @param value
     * @return
     */
    public static ValueData<Object> modbusReadInRegister(ModbusTcp modbusTcp, DeviceValue value) {
        byte[] obj = modbusTcp.readInputRegister(Integer.parseInt(value.getAddress()), value.getLength());
        ValueData<Object> valueData;
        switch (value.getJavaType()) {
            case 0:
                Boolean data = (ByteUtils.byteArrToShort(obj) == 1);
                valueData = new ValueData<Object>(data);
                break;
            case 1:
                valueData = new ValueData<Object>(obj);
                break;
            case 2:
                valueData = new ValueData<Object>(ByteUtils.byteArrToInt(obj));
                break;
            case 3:
                valueData = new ValueData<Object>(ByteUtils.byteArrToShort(obj));
                break;
            case 4:
                valueData = new ValueData<Object>(ByteUtils.byteArrToLong(obj));
                break;
            case 5:
                valueData = new ValueData<Object>(ByteUtils.byteArrToFloat(obj));
                break;
            case 6:
                valueData = new ValueData<Object>(ByteUtils.byteArrToDouble(obj));
                break;
            case 7:
                valueData = new ValueData<Object>(ByteUtils.bytesToString(obj));
                break;
            default:
                valueData = new ValueData<Object>(ByteUtils.bytesToBinStr(obj));
                break;
        }
        return valueData;
    }

    /**
     * 功能码  05H
     * 写入单线圈
     *
     * @param value
     * @return
     */
    public static void modbusWriteCoil(ModbusTcp modbusTcp, DeviceValue value, ValueData<Boolean> valueData) {
        modbusTcp.writeCoil(Integer.parseInt(value.getAddress()), valueData.getData());
    }

    /**
     * 功能码  06H
     * 写入单寄存器
     *
     * @param value
     * @return
     */
    public static void modbusWriteHoldRegister(ModbusTcp modbusTcp, DeviceValue value, ValueData valueData) {
        switch (value.getPlcType()) {
            case 1:
                Short data1 = (Short) valueData.getData();
                modbusTcp.writeInt16(Integer.parseInt(value.getAddress()), data1);
                break;
            case 2:
                Integer data2 = (Integer) valueData.getData();
                modbusTcp.writeUInt16(Integer.parseInt(value.getAddress()), data2);
                break;
            case 3:
                Integer data3 = (Integer) valueData.getData();
                modbusTcp.writeInt32(Integer.parseInt(value.getAddress()), data3);
                break;
            case 4:
                Long data4 = (Long) valueData.getData();
                modbusTcp.writeUInt32(Integer.parseInt(value.getAddress()), data4);
                break;
            case 5:
                Float data5 = (Float) valueData.getData();
                modbusTcp.writeFloat32(Integer.parseInt(value.getAddress()), data5);
                break;
            case 6:
                Double data6 = (Double) valueData.getData();
                modbusTcp.writeFloat64(Integer.parseInt(value.getAddress()), data6);
                break;
            case 7:
                String data7 = (String) valueData.getData();
                int length = value.getLength() * 2 - data7.length();
                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        data7 += " ";
                    }
                }
                modbusTcp.writeString(Integer.parseInt(value.getAddress()), data7);
                break;
            default:
                break;
        }
    }

}
