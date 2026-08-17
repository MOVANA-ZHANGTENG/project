package com.deer.wcs.base.utils;

import com.deer.wcs.base.model.DeviceValue;
import com.deer.wcs.base.model.ValueData;
import com.github.xingshuangs.iot.protocol.s7.enums.EPlcType;
import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @description:    使用iot-communication兼容s7协议和modbus协议
 * @author:zfj
 * @date:2024/5/16 17:10
 */
public class S7PLCUtils {

    @Test
    public void test(){

    }


    private static S7PLC getS7PLC(DeviceValue value){
        if(value.getPort()==null){
            value.setPort(102);
        }
        S7PLC s7PLC ;
        switch (value.getS7Type()){
            case 0:
                s7PLC = new S7PLC(EPlcType.S200, value.getIp(),value.getPort());
                break;
            case 1:
                s7PLC = new S7PLC(EPlcType.S200_SMART, value.getIp(),value.getPort());
                break;
            case 2:
                s7PLC = new S7PLC(EPlcType.S300, value.getIp(),value.getPort());
                break;
            case 3:
                s7PLC = new S7PLC(EPlcType.S400, value.getIp(),value.getPort());
                break;
            case 4:
                s7PLC = new S7PLC(EPlcType.S1200, value.getIp(),value.getPort());
                break;
            case 5:
                s7PLC = new S7PLC(EPlcType.S1500, value.getIp(),value.getPort());
                break;
            case 6:
                s7PLC = new S7PLC(EPlcType.SINUMERIK_828D, value.getIp(),value.getPort());
                break;
            default:
                throw new RuntimeException("S7类型不正确！");
        }
        return s7PLC;
    }

    /**
     * 根据属性的plc类型来读取和写入plc数据
     * @param value
     * @return
     */
    public static ValueData<Object> plcS7Read(DeviceValue value){
        S7PLC s7PLC = getS7PLC(value);
        ValueData<Object> valueData = null;
        switch (value.getPlcType()){
            case 0:
                //boolean/BOOL
                Boolean data1 = s7PLC.readBoolean(value.getAddress());
                valueData = new ValueData<Object>(data1);
                break;
            case 1:
                //int16/WORD/INT
                Short data2 = s7PLC.readInt16(value.getAddress());
                valueData = new ValueData<Object>(data2);
                break;
            case 2:
                //unint16/WORD/UINT
                Integer data3 = s7PLC.readUInt16(value.getAddress());
                valueData = new ValueData<Object>(data3);
                break;
            case 3:
                //int32/DWORD/DINT
                Integer data4 = s7PLC.readInt32(value.getAddress());
                valueData = new ValueData<Object>(data4);
                break;
            case 4:
                //uint32/DWORD/UDINT
                Long data5 = s7PLC.readUInt32(value.getAddress());
                valueData = new ValueData<Object>(data5);
                break;
            case 5:
                //float32/REAL
                Float data6 = s7PLC.readFloat32(value.getAddress());
                valueData = new ValueData<Object>(data6);
                break;
            case 6:
                //float64/LREAL
                Double data7 = s7PLC.readFloat64(value.getAddress());
                valueData = new ValueData<Object>(data7);
                break;
            case 7:
                //string
                String data8 = s7PLC.readString(value.getAddress(),value.getLength());
                valueData = new ValueData<Object>(data8);
                break;
            case 8:
                //Byte
                Byte data9 = s7PLC.readByte(value.getAddress());
                valueData = new ValueData<Object>(data9);
                break;
            case 9:
                //Time
                Long data10 = s7PLC.readTime(value.getAddress());
                valueData = new ValueData<Object>(data10);
                break;
            case 10:
                //date
                LocalDate data11 = s7PLC.readDate(value.getAddress());
                valueData = new ValueData<Object>(data11);
                break;
            case 11:
                //timeOfDay
                LocalTime data12 = s7PLC.readTimeOfDay(value.getAddress());
                valueData = new ValueData<Object>(data12);
                break;
            case 12:
                //DTL
                LocalDateTime data13 = s7PLC.readDTL(value.getAddress());
                valueData = new ValueData<Object>(data13);
                break;
            default:
                break;
        }
        s7PLC.close();
        return valueData;
    }
    public static void plcS7Write(DeviceValue value,ValueData valueData){
        S7PLC s7PLC = getS7PLC(value);
        switch (value.getPlcType()){
            case 0:
                //boolean/BOOL
                s7PLC.writeBoolean(value.getAddress(),(boolean) valueData.getData());
                break;
            case 1:
                //int16/WORD/INT
                s7PLC.writeInt16(value.getAddress(),(Short) valueData.getData());
                break;
            case 2:
                //unint16/WORD/UINT
                s7PLC.writeUInt16(value.getAddress(),(Integer) valueData.getData());
                break;
            case 3:
                //int32/DWORD/DINT
                s7PLC.writeInt32(value.getAddress(),(Integer) valueData.getData());
                break;
            case 4:
                //uint32/DWORD/UDINT
                s7PLC.writeUInt32(value.getAddress(),(Long) valueData.getData());
                break;
            case 5:
                //float32/REAL
                s7PLC.writeFloat32(value.getAddress(),(Float) valueData.getData());
                break;
            case 6:
                //float64/LREAL
                s7PLC.writeFloat64(value.getAddress(),(Double) valueData.getData());
                break;
            case 7:
                //string
                s7PLC.writeString(value.getAddress(),valueData.getData().toString());
                break;
            case 8:
                //Byte
                s7PLC.writeByte(value.getAddress(),(Byte) valueData.getData());
                break;
            case 9:
                //Time
                s7PLC.writeTime(value.getAddress(),(Long) valueData.getData());
                break;
            case 10:
                //date
                s7PLC.writeDate(value.getAddress(),(LocalDate) valueData.getData());
                break;
            case 11:
                //timeOfDay
                s7PLC.writeTimeOfDay(value.getAddress(),(LocalTime) valueData.getData());
                break;
            case 12:
                //DTL
                s7PLC.writeDTL(value.getAddress(),(LocalDateTime) valueData.getData());
                break;
            default:
                break;
        }
        s7PLC.close();
    }

}
