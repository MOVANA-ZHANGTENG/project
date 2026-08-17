package com.deer.wcs.task.handle.han_tai;

import com.github.xingshuangs.iot.protocol.s7.enums.EPlcType;
import com.github.xingshuangs.iot.protocol.s7.model.DataItem;
import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;
import com.github.xingshuangs.iot.protocol.s7.utils.AddressUtil;
import com.github.xingshuangs.iot.utils.ByteUtil;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


@Component("HtSsxDevice")
public class HtSsxDevice {

    private static  String ip="192.168.200.10";
    private static  Integer port=102;

    private static S7PLC s7PLC=null;

    //巷道

    //入库口
    public  static final int device5 = 0;
    public  static final int device6 = 88;

    //入库口
    public  static final int device11 = 44;
    public  static final int device12 = 132;



    //入库接驳
    public  static final int device30 = 176;
    //出库接驳
    public  static final int device25 = 264;


    //入库接驳
    public  static final int device40 = 220;
    //出库接驳
    public  static final int device34 = 308;

    public static final  int palletCount = 352;

    public static final Map<Integer,Integer> deviceMap =new HashMap<>();
    static {
        deviceMap.put(5,device5);
        deviceMap.put(6,device6);
        deviceMap.put(11,device11);
        deviceMap.put(12,device12);
        deviceMap.put(30,device30);
        deviceMap.put(25,device25);
        deviceMap.put(40,device40);
        deviceMap.put(34,device34);
    }

    private static final String plcAddress = "DB100";
    private static final String wcsAddress = "DB101";

    private static String shortAddress(int device){
        return plcAddress+".DBW"+device;
    }

    private static String intAddress(int device){
        return plcAddress+".DBD"+device;
    }

    public static S7PLC getS7PLC() {
        if(s7PLC==null){
            s7PLC = new S7PLC(EPlcType.S1200, ip,port);
        }
        return s7PLC;
    }

    public Short hasPallet(int device){
         s7PLC = getS7PLC();
         short value = s7PLC.readInt16(shortAddress(device));
         return value;
    }

    /**
     * 申请信号
     * @param device
     * @return
     */
    public Short apply(int device){
        s7PLC = getS7PLC();
        String address = shortAddress(device+2);
        short value = s7PLC.readInt16(address);
        return value;
    }

    /**
     * 故障信息
     * @param device
     * @return
     */
    public Short error(int device){
        s7PLC = getS7PLC();
        short value = s7PLC.readInt16(shortAddress(device)+4+"");
        return value;
    }



    /**
     * 托盘码
     */
    public String palletCode(int device){
        s7PLC = getS7PLC();
        String address = "DB100.DBD"+(device+6);
        String value = readString(address,15);
        return value;
    }

    public static void main(String[] args) {
         s7PLC = getS7PLC();
        String address = "DB100.DBB138";
       String value = readString(address,15);

        System.out.println(value);
    }

    public static String readString(String address,int length){
        byte[] aaa = s7PLC.readByte(address,15);
        return new String(aaa);
    }



    /**
     * 重量
     */
    public float weight(int device){
        s7PLC = getS7PLC();
        float value = s7PLC.readFloat32(shortAddress(device+26));
        return value;
    }

    /**
     * 宽
     */
    public Short width(int device){
        s7PLC = getS7PLC();
        short value = s7PLC.readInt16(shortAddress(device+30));
        return value;
    }

    /**
     * 长
     */
    public Short length(int device){
        s7PLC = getS7PLC();
        short value = s7PLC.readInt16(shortAddress(device+32));
        return value;
    }

    /**
     * 高
     */
    public Short height(int device){
        s7PLC = getS7PLC();
        short value = s7PLC.readInt16(shortAddress(device+34));
        return value;
    }

    /**
     * 目的地
     */
    public Short dest(int device){
        s7PLC = getS7PLC();
        short value = s7PLC.readInt16(shortAddress(device+36));
        return value;
    }

    /**
     * 任务号
     */
    public Integer taskNo(int device){
        s7PLC = getS7PLC();
        Integer value = s7PLC.readInt32(shortAddress(device+38));
        return value;
    }

    /**
     * 备用
     */
    public Short backup(int device){
        s7PLC = getS7PLC();
        short value = s7PLC.readInt16(shortAddress(device+42));
        return value;
    }
    /**
     * 备用
     */
    public Short palletCount(){
        s7PLC = getS7PLC();
        short value = s7PLC.readInt16(shortAddress(palletCount));
        return value;
    }

    /**
     * 设备6	目的地	INT	0
     * 	任务号	DINT	2
     * 	预留1	INT	6
     * 	预留1	INT	8
     * 设备12	目的地	INT	10
     * 	任务号	DINT	12
     * 	预留1	INT	16
     * 	预留1	INT	18
     * 设备34(堆垛机出库口	目的地	INT	20
     * 	任务号	DINT	22
     * 	预留1	INT	26
     * 	预留1	INT	28
     * 设备25(堆垛机出库口	目的地	INT	30
     * 	任务号	DINT	32
     * 	预留1	INT	36
     * 	预留1	INT	38
     * @param device
     * @return
     */

    private String getWcsToPlcAddressInt(int device,Integer pyl){
        if(device==6){
            device=0;
        }
        if(device==12){
            device=10;
        }
        if(device==34){
            device=20;
        }
        if(device==25){
            device=30;
        }

        if(device==5){
            device=40;
        }
        if(device==11){
            device=48;
        }
        return "DB101.D"+(device+pyl);
    }

    public String sendTask(int device,int taskNo,short dest){
        try {
            s7PLC = getS7PLC();
            String destAddress = getWcsToPlcAddressInt(device,0);
            String taskNoAddress = getWcsToPlcAddressInt(device,2);
            //目的地
            s7PLC.writeInt16(destAddress, dest);
            //任务号
            s7PLC.writeInt32(taskNoAddress, taskNo);
            return null;
        }catch (Exception ex){
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    public String clearTaskData(int device){
        try {
            s7PLC = getS7PLC();
            //目的地
            s7PLC.writeInt16(getWcsToPlcAddressInt(device,0), (short) 0);
            //任务号
            s7PLC.writeInt32(getWcsToPlcAddressInt(device,2), 0);
            return null;
        }catch (Exception ex){
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    public String deng1(int device,int deng){
        try {
            s7PLC = getS7PLC();
            //目的地
            s7PLC.writeInt16(getWcsToPlcAddressInt(device,deng*2), (short) 1);
            return null;
        }catch (Exception ex){
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    public String deng0(int device,int deng){
        try {
            s7PLC = getS7PLC();
            //目的地
            s7PLC.writeInt16(getWcsToPlcAddressInt(device,deng*2), (short) 0);
            return null;
        }catch (Exception ex){
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    public String deng0All(int device ){
        try {
            s7PLC = getS7PLC();
            //目的地
            s7PLC.writeInt16(getWcsToPlcAddressInt(device,0), (short) 0);
            s7PLC.writeInt16(getWcsToPlcAddressInt(device,1), (short) 0);
            s7PLC.writeInt16(getWcsToPlcAddressInt(device,2), (short) 0);
            s7PLC.writeInt16(getWcsToPlcAddressInt(device,3), (short) 0);
            return null;
        }catch (Exception ex){
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

}
