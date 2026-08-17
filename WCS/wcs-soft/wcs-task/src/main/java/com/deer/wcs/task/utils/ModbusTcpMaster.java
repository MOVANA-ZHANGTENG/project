package com.deer.wcs.task.utils;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.IpParameters;
import org.springframework.stereotype.Service;

@Service(value = "ModbusTcpMaster")
public class ModbusTcpMaster {
    private static final ModbusFactory modbusFactory = new ModbusFactory();

    //static ModbusMaster master = null;
    /**
     * 获取slave
     * @return
     * @throws ModbusInitException
     */
    public static ModbusMaster getSlave(String ip, int port) {
//        if(master==null){
//
//        }else if(master.isConnected()){
//            return master;
//        }
        ModbusMaster master = null;
        try {
            IpParameters params = new IpParameters();
            params.setHost(ip);
            params.setPort(port);
            //这个属性确定了协议帧是否是通过tcp封装的RTU结构，采用modbus tcp/ip时，要设为false, 采用modbus rtu over tcp/ip时，要设为true
            params.setEncapsulated(false);
            // modbusFactory.createRtuMaster(wapper); //RTU 协议
            // modbusFactory.createUdpMaster(params);//UDP 协议
            // modbusFactory.createAsciiMaster(wrapper);//ASCII 协议
            master = modbusFactory.createTcpMaster(params, false);
            //最大等待时间
            master.setTimeout(500);
            //最大连接次数
            master.setRetries(5);
            master.init();
            // master.setConnected(true);
        } catch (ModbusInitException e) {
            e.printStackTrace();
        }
        return master;
    }
}
