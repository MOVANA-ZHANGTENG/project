package com.deer.wcs.jxg.task;

import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("JxkZk2Heartbeat")
public class JxkZk2Heartbeat {

    @Autowired
    private DeviceValueService deviceValueService;

    private Object scReadPlc(String deviceCode, String valueCode){
        return deviceValueService.readValueByCode(deviceCode,valueCode);
    };



    Boolean tsjIsOnline =null;
    Boolean tsjLastIsOnline =null;

    Boolean gzzIsOnline =null;
    Boolean gzzLastIsOnline =null;

    @Autowired
    private DeviceInfoService deviceInfoService;

    public void auto(){
        try {
            scReadPlc("tsj","heartbeat");
            tsjIsOnline=true;
        }catch (Exception ex){
            tsjIsOnline=false;
        }
        if(tsjIsOnline!=tsjLastIsOnline){
            DeviceInfo deviceInfo = deviceInfoService.findByCode("tsj");
            deviceInfo.setIsOnline(tsjIsOnline?1:0);
            deviceInfoService.update(deviceInfo);
            tsjLastIsOnline =tsjIsOnline;
        }

        try {
            scReadPlc("gzz","heartbeat");
            gzzIsOnline=true;
        }catch (Exception ex){
            gzzIsOnline=false;
        }
        if(gzzIsOnline!=gzzLastIsOnline){
            DeviceInfo deviceInfo = deviceInfoService.findByCode("gzz");
            deviceInfo.setIsOnline(gzzIsOnline?1:0);
            deviceInfoService.update(deviceInfo);
            gzzLastIsOnline =gzzIsOnline;
        }
    }
}
