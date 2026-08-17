package com.deer.wcs.base.utils;

import com.deer.wcs.base.model.DeviceValue;
import com.deer.wcs.base.model.ValueData;
import com.github.xingshuangs.iot.protocol.melsec.enums.EMcSeries;
import com.github.xingshuangs.iot.protocol.melsec.service.McPLC;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:三菱MELSEC(MC)通信协议
 * @author:zfj
 * @date:2024/5/20 15:19
 */
public class McPLCUtils {

    public static void main(String[] args) {
        ValueData<Object> valueData = read("192.168.10.50",3210,"D7002",1,30);
       /* ValueData valueData1 = read("192.168.10.50",3210,"D7011",1,30);
        ValueData valueData2 = read("192.168.10.50",3210,"D6100",7,50);
        ValueData valueData3 = read("192.168.10.50",3210,"D6200",7,50);*/
//        DeviceValue value = new DeviceValue();
//        value.setPort(3270);
//        value.setIp("192.168.10.50");
//        value.setPlcType(1);
//        value.setAddress("D7001");
//        ValueData<Object> data = new ValueData<>();
//        Short a = 1;
//        data.setData(a);
//        mcPLCWrite(value,data);
        System.out.println(valueData.getData());
    }
    static Map<String,McPLC> map =new HashMap<>();

    public static ValueData<Object> read(String ip,Integer port,String address,Integer type,Integer length){
        String key = ip+":"+port;
        McPLC mcPLC=map.get(key);
        if(mcPLC==null){
            mcPLC=new McPLC(EMcSeries.Q_L,ip,port);
            mcPLC.setConnectTimeout(1000);
            map.put(key,mcPLC);
        }
        ValueData<Object> valueData = null;
        try {
            switch (type){
                case 0:
                    //boolean/BOOL
                    Boolean data1 = mcPLC.readBoolean(address);
                    valueData = new ValueData<Object>(data1);
                    break;
                case 1:
                    //int16/WORD/INT
                    Short data2 = mcPLC.readInt16(address);
                    valueData = new ValueData<Object>(data2);
                    break;
                case 2:
                    //unint16/WORD/UINT
                    Integer data3 = mcPLC.readUInt16(address);
                    valueData = new ValueData<Object>(data3);
                    break;
                case 3:
                    //int32/DWORD/DINT
                    Integer data4 = mcPLC.readInt32(address);
                    valueData = new ValueData<Object>(data4);
                    break;
                case 4:
                    //uint32/DWORD/UDINT
                    Long data5 = mcPLC.readUInt32(address);
                    valueData = new ValueData<Object>(data5);
                    break;
                case 5:
                    //float32/REAL
                    Float data6 = mcPLC.readFloat32(address);
                    valueData = new ValueData<Object>(data6);
                    break;
                case 6:
                    //float64/LREAL
                    Double data7 = mcPLC.readFloat64(address);
                    valueData = new ValueData<Object>(data7);
                    break;
                case 7:
                    //string
                    String data8 = mcPLC.readString(address,length);
                    valueData = new ValueData<Object>(data8);
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            if (mcPLC != null){
                mcPLC.close();
                map.remove(key);
            }
            e.printStackTrace();
        }
        return valueData;
    }

    /**
     * 根据属性的plc类型来读取和写入plc数据
     * @param value
     * @return
     */
    public static ValueData<Object> mcPLCRead(DeviceValue value){
        return read(value.getIp(),value.getPort(),value.getAddress(),value.getPlcType(),value.getLength()) ;
    }
    public static void mcPLCWrite(DeviceValue value,ValueData<Object> valueData){
        String ip = value.getIp();
        Integer port=value.getPort();
        String key = ip+":"+port;
        McPLC mcPLC=map.get(key);
        if(mcPLC==null){
            mcPLC=new McPLC(EMcSeries.Q_L,ip,port);
            mcPLC.setConnectTimeout(1000);
            map.put(key,mcPLC);
        }
        try {
            switch (value.getPlcType()){
                case 0:
                    //boolean/BOOL
                    mcPLC.writeBoolean(value.getAddress(),(boolean) valueData.getData());
                    break;
                case 1:
                    //int16/WORD/INT
                    mcPLC.writeInt16(value.getAddress(),(Short) valueData.getData());
                    break;
                case 2:
                    //unint16/WORD/UINT
                    mcPLC.writeUInt16(value.getAddress(),(Integer) valueData.getData());
                    break;
                case 3:
                    //int32/DWORD/DINT
                    mcPLC.writeInt32(value.getAddress(),(Integer) valueData.getData());
                    break;
                case 4:
                    //uint32/DWORD/UDINT
                    mcPLC.writeUInt32(value.getAddress(),(Long) valueData.getData());
                    break;
                case 5:
                    //float32/REAL
                    mcPLC.writeFloat32(value.getAddress(),(Float) valueData.getData());
                    break;
                case 6:
                    //float64/LREAL
                    mcPLC.writeFloat64(value.getAddress(),(Double) valueData.getData());
                    break;
                case 7:
                    //string
                    mcPLC.writeString(value.getAddress(),valueData.getData().toString());
                    break;
                case 8:
                    //Byte
                    mcPLC.writeByte(value.getAddress(),(Byte) valueData.getData());
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            mcPLC.close();
            map.remove(key);
        }finally {

        }


    }


}
