package com.deer.wcs.task.handle.hik;

import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.model.AgvZoneRecord;
import com.deer.wcs.task.model.CallBoxRecord;
import com.deer.wcs.task.model.DeviceTaskResult;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfo;
import com.deer.wcs.task.service.AgvZoneRecordService;
import com.deer.wcs.task.service.CallBoxInfoService;
import com.deer.wcs.task.service.CallBoxRecordService;
import com.deer.wcs.task.service.DeviceTaskResultService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 海康回传
 * 
 * @author deer
 * @date 2024-07-30
 */
@Api("hik")
@RestController
@RequestMapping("/hik")
public class HikTaskResultController extends BaseController
{

    @Autowired
    private DeviceTaskResultService deviceTaskResultService;


    /**
     * 海康任务回传
     * @param rcsCallBack
     * @return
     */
    @PostMapping("/hik2000v33/agvCallback")
    public Map agvCallback(@RequestBody RobotTaskDetail rcsCallBack)
    {
        try {
            //设备任务回传表  agv 堆垛机  四向车
            DeviceTaskResult deviceTaskResult = new DeviceTaskResult();
            deviceTaskResult.setTaskCode(rcsCallBack.getRobotTaskCode());
            deviceTaskResult.setType(rcsCallBack.getExtra().getValues().getMethod());
            deviceTaskResult.setCreateTime(DateUtil.getNowDateTimeString());
            deviceTaskResult.setState(0);   //初始化  未处理
            deviceTaskResult.setNode(rcsCallBack.getExtra().getValues().getSlotCode());
            deviceTaskResult.setFromDevice("HIK-RCS");
            deviceTaskResultService.save(deviceTaskResult);
            Map<String,String> map = new HashMap<>();
            map.put("code","0");
            map.put("message","成功");
            //map.put("reqCode",rcsCallBack.getReqCode());
            return map;
        }catch (Exception ex){
            Map<String,String> map = new HashMap<>();
            map.put("code","1");
            map.put("message",ex.getMessage());
            //map.put("reqCode",rcsCallBack.getReqCode());
            return map;
        }
    }

    @Autowired
    private AgvZoneRecordService agvZoneRecordService;


    //海康交管区进出
    @PostMapping("/hik2000v33/NotifyTaskInfo")
    public Map<String,String> notifyTaskInfo(@RequestBody NotifyTaskInfo notifyTaskInfo)
    {
        try {
            AgvZoneRecord agvZoneRecord = new AgvZoneRecord();
            agvZoneRecord.setState(0);
            agvZoneRecord.setAgvType("HIK");
            agvZoneRecord.setCode(notifyTaskInfo.getDeviceIndex());
            agvZoneRecord.setUuid(notifyTaskInfo.getUuid());
            agvZoneRecord.setCreateTime(DateUtil.getNowDateTimeString());
            //申请进入
            if(notifyTaskInfo.getActionTask().equals("applyLock")){
                agvZoneRecord.setEventType(1);
            }
            if(notifyTaskInfo.getActionTask().equals("releaseDevice")){
                agvZoneRecord.setEventType(2);
            }
            agvZoneRecordService.save(agvZoneRecord);
            Map<String,String> map = new HashMap<>();
            map.put("code","0");
            map.put("message","成功");
            return map;
        }catch (Exception ex){
            Map<String,String> map = new HashMap<>();
            map.put("code","1");
            map.put("message",ex.getMessage());
            return map;
        }
    }

    @Autowired
    private CallBoxInfoService callBoxInfoService;

    @Autowired
    private CallBoxRecordService callBoxRecordService;


    /**
     *呼叫盒
     * @param callBoxData
     * @return
     */
    @PostMapping("/callBox")
    public Map<String,String> callBox(@RequestBody CallBoxData callBoxData)
    {
        // 呼叫和IP
        String  ip = callBoxData.getDeviceIp();
        CallBoxInfo callBoxInfo =callBoxInfoService.findBy("ip",ip);
        if(callBoxInfo ==null){
            Map<String,String> map = new HashMap<>();
            map.put("code","1");
            map.put("message",ip+"不存在");
            return map;
        }
        //第几个按钮  0-第一个按钮  1-第二个按钮
        String  btn  =callBoxData.getPinIndex();
        //呼叫和记录
        CallBoxRecord callBoxRecord = new CallBoxRecord();
        callBoxRecord.setCallBoxInfoId(callBoxInfo.getId());
        callBoxRecord.setCode(callBoxInfo.getCode());
        callBoxRecord.setIp(ip);
        callBoxRecord.setBtn(Integer.parseInt(btn)+1);
        callBoxRecord.setState(0);
        callBoxRecord.setType(0);  //0-按钮按下  1-控制灯
        callBoxRecord.setCreateTime(DateUtil.getNowDateTimeString());
        callBoxRecordService.save(callBoxRecord);
        Map<String,String> map = new HashMap<>();
        map.put("code","0");
        map.put("message","成功");
        return map;
    }
}