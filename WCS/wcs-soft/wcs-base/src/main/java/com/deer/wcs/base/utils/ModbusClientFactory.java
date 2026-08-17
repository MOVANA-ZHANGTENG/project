package com.deer.wcs.base.utils;

import com.github.xingshuangs.iot.protocol.modbus.service.ModbusTcp;

import java.util.HashMap;
import java.util.Map;

public class ModbusClientFactory {
    private static Map clients = new HashMap();


    /**
     *
     * @param ip
     * @param port
     * @param unitId 从站ID
     * @return
     */
    public static ModbusTcp create(String ip, Integer port, Integer unitId){
        String key = ip+"-"+port+"-"+unitId;
        if(clients.containsKey(key)){
            return (ModbusTcp)clients.get(key);
        }
        ModbusTcp modbusTcp = new ModbusTcp(unitId, ip,port);
        return modbusTcp;
    }

}
