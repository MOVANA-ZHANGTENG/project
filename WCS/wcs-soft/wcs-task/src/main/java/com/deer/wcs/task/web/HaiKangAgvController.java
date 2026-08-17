package com.deer.wcs.task.web;

import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.model.DeviceTaskResult;
import com.deer.wcs.task.model.HostWcsInterface;
import com.deer.wcs.task.model.haikang.TaskStatusNotice;
import com.deer.wcs.task.model.result.HaiKangServiceResult;
import com.deer.wcs.task.service.DeviceTaskResultService;
import com.deer.wcs.task.service.HostWcsInterfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author:zfj
 * @date:2024/7/16 21:52
 */
@Api("海康AGV接口")
@RestController
@RequestMapping("/HKAGV")
public class HaiKangAgvController {
    @Autowired
    private HostWcsInterfaceService hostWcsInterfaceService;
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private DeviceTaskResultService deviceTaskResultService;

    /**
     * 海康AGV上报当前AGV状态
     */
    @ApiOperation("海康AGV上报当前AGV状态")
    @PostMapping("/taskNotice/agv/agvCallbackService/agvCallback")
    public HaiKangServiceResult taskNotice(@RequestBody TaskStatusNotice statusNotice) {
        HostWcsInterface wcsInterface = new HostWcsInterface();
        wcsInterface.setCode("hkAgv01");
        wcsInterface.setInterfaceName("HKAGV/taskNotice/agv/agvCallbackService");
        wcsInterface.setSendFrom("AGV");
        wcsInterface.setSendTo("WCS");
        wcsInterface.setContent(JSONObject.toJSONString(statusNotice));
        wcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        wcsInterface.setType(1);

        DeviceTaskResult deviceTaskResult = new DeviceTaskResult();
        deviceTaskResult.setTaskCode(statusNotice.getTaskCode());
        deviceTaskResult.setFromDevice(statusNotice.getEqpCode());
        deviceTaskResult.setState(0);
        deviceTaskResult.setData(JSONObject.toJSONString(statusNotice));
        deviceTaskResult.setType(statusNotice.getMethod());
        deviceTaskResult.setCreateTime(DateUtil.getNowDateTimeString());
        deviceTaskResultService.save(deviceTaskResult);

        if(!statusNotice.getCurrentPositionCode().equals("")){
            redisCache.setCacheObject("agvStatus",statusNotice);
        }

        //给AGV发送成功信号
        HaiKangServiceResult result = new HaiKangServiceResult();
        result.setReqCode(statusNotice.getReqCode());
        result.setCode(0);
        result.setMessage("成功");

        wcsInterface.setRecv(JSONObject.toJSONString(result));
        wcsInterface.setEndTime(DateUtil.getNowDateTimeString());
        hostWcsInterfaceService.save(wcsInterface);

        return result;
    }
    
}
