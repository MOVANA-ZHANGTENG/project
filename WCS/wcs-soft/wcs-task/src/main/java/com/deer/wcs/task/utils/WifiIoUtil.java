package com.deer.wcs.task.utils;

import com.serotonin.modbus4j.ModbusMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WifiIoUtil {

    private static Logger log = LoggerFactory.getLogger(WifiIoUtil.class);

    /***
     * wifi模块  DO  1-4    100  101  102  103
     * @param ip
     * @param offset
     * @return
     */

    public static Boolean read(String ip,int offset,String code ){
        ModbusMaster master = ModbusTcpMaster.getSlave(ip, 502);
        master.setTimeout(200);
        try {
            Boolean value = ModbusTcpUtil.readInputStatus(master,1, offset,code);
            master.destroy();
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        Boolean a = read("192.168.10.158", 10200, "a");
//        Boolean a1 = read("192.168.10.150", 10201, "a");
//        Boolean a2 = read("192.168.10.150", 10202, "a");
//        Boolean a3= read("192.168.10.150", 10203, "a");
        System.out.println(a);
    }


    public static Boolean write(String ip,int offset ,Boolean value){
        ModbusMaster master = ModbusTcpMaster.getSlave(ip, 502);
        try {
            Boolean returnValue = ModbusTcpUtil.writeCoil(master,1, offset,value);
            master.destroy();
            return returnValue;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
