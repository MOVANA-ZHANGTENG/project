package com.deer.wcs.task.task;

import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("WcsHeartTask")
public class WcsHeartTask {

    @Autowired
    private DeviceValueService deviceValueService;

    private Object scReadPlc( String valueCode){
        return deviceValueService.readValueByCode("sc",valueCode);
    };

    private Object scWrite( String valueCode,Object object){
        return deviceValueService.writeValueByCode("sc",valueCode,object);
    };

    Boolean isOnline =false;
    Boolean lastIsOnline =false;
    @Autowired
    private DeviceInfoService deviceInfoService;

    public void scHeart(){

        try {
            scWrite("Comm_Check_Confirm",(Short)scReadPlc("Comm_Check"));
            isOnline=true;
        }catch (Exception ex){
            isOnline=false;
        }
        if(isOnline!=lastIsOnline){
            DeviceInfo deviceInfo = deviceInfoService.findByCode("sc");

            deviceInfo.setIsOnline(isOnline?1:0);
            deviceInfoService.update(deviceInfo);
            lastIsOnline =isOnline;
        }
    }
}
