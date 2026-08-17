package com.deer.wcs.task.web;

import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.model.PathInfo;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.base.service.PathInfoService;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.system.service.BillRecordService;
import com.deer.wcs.task.model.HostWcsInterface;
import com.deer.wcs.task.model.ThreeData;
import com.deer.wcs.task.model.haikang.ContinueAGVTask;
import com.deer.wcs.task.model.haikang.GenAGVTask;
import com.deer.wcs.task.model.haikang.PositionPath;
import com.deer.wcs.task.model.haikang.TaskStatusNotice;
import com.deer.wcs.task.service.HostWcsInterfaceService;
import com.deer.wcs.task.websocket.WebSocketUsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:AGV调试流程 1. 创建一个AGV任务接口发送给小车
 * 2. 小车上报AGV状态，到达起点
 * 3. 写入上货指令
 * 4. 接口通知小车继续任务
 * 5. 小车到达终点
 * 6. 任务结束
 * @author:zfj
 * @date:2024/7/16 21:52
 */
@Component("HaiKangAgvService")
public class HaiKangAgvService {

    private static final Logger log = LoggerFactory.getLogger(HaiKangAgvService.class);

    @Autowired
    private HostWcsInterfaceService hostWcsInterfaceService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private DeviceValueService deviceValueService;
    @Autowired
    private AutoService autoService;
    @Autowired
    private PathInfoService pathInfoService;


    private Object readAGV(String valueCode) {
        return deviceValueService.readValueByCode("JXG_AGV", valueCode);
    }

    private Object writeAGV(String valueCode, Object object) {
        return deviceValueService.writeValueByCode("JXG_AGV", valueCode, object);
    }

    private Object readSsxPlc(String valueCode) {
        return deviceValueService.readValueByCode("JXG_SSX", valueCode);
    }

    private Object writeSsx(String valueCode, Object object) {
        return deviceValueService.writeValueByCode("JXG_SSX", valueCode, object);
    }
    Boolean isOnline =false;
    Boolean lastIsOnline =false;
    @Autowired
    private DeviceInfoService deviceInfoService;
    public void scHeart(){

        try {
            Integer frontUpReady = (Integer) readAGV("frontUpReady");
            isOnline=true;
        }catch (Exception ex){
            isOnline=false;
        }
        if(isOnline!=lastIsOnline){
            DeviceInfo deviceInfo = deviceInfoService.findByCode("JXG_AGV");

            deviceInfo.setIsOnline(isOnline?1:0);
            deviceInfoService.update(deviceInfo);
            lastIsOnline =isOnline;
        }
    }

    /**
     * 1. 执行条件
     * 起点是否有货
     * 2. 执行函数
     * 创建agv任务
     * 3. 成功条件
     * agv在入口处是否就绪
     * agv上货是否完成
     * agv是否到达终点
     * agv下货是否完成
     * 4. 成功回调
     */

    //起点是否有货
    public boolean ssxIsNotEmpty(PathInfo pathInfo) {

        //输送线是否请求排除
        Integer outRequest = (Integer) readSsxPlc("outRequest");
        if (outRequest != 1) {
            pathInfoService.updateMemo(pathInfo,"输送线是否请求排出信号为0");
            return false;
        }
        return true;
    }

    //创建agv任务
    public boolean createAgvTask(PathInfo pathInfo) {
        if(!isOnline){
            billRecordService.createTaskRecord(pathInfo.getTaskId(),"AGV上装连不上，请检查网络，上装是否开机");
        }
        //给webSocket发信号
        ThreeData data = new ThreeData();
        data.setType("agv");
        data.setFromNode("00");
        data.setToNode(pathInfo.getFromCellCode());
        sendMsgToWebSocket(data);

        PositionPath start = new PositionPath();
        start.setPositionCode(pathInfo.getFromCellCode());
        start.setType("00");
        PositionPath end = new PositionPath();
        end.setPositionCode(pathInfo.getToCellCode());
        end.setType("00");
        PositionPath position = new PositionPath();
        position.setPositionCode("A01");
        position.setType("00");
        PositionPath[] paths = {start,position,end};

        GenAGVTask task = new GenAGVTask();
        task.setReqCode(autoService.getReqCode());
        task.setTaskTyp("GtTest");
        task.setTaskCode(pathInfo.getId().toString());
        task.setPositionCodePath(paths);
        task.setReqTime(DateUtil.getNowDateTimeString());
        redisCache.deleteObject("agvStatus");

        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode("hkAgv02");
        hostWcsInterface.setInterfaceName("rcms/services/rest/hikRpcService/genAgvSchedulingTask");
        hostWcsInterface.setSendFrom("WCS");
        hostWcsInterface.setSendTo("AGV");
        hostWcsInterface.setContent(JSONObject.toJSONString(task));
        hostWcsInterface.setType(0);
        hostWcsInterfaceService.save(hostWcsInterface);

        log.info("AGV任务已创建");
        pathInfo.setMemo("AGV任务已创建");

        billRecordService.createTaskRecord(pathInfo.getTaskId(),"AGV任务已创建");
        pathInfoService.update(pathInfo);
        return true;
    }

    //agv在入口处是否就绪
    public boolean inAgvReady(PathInfo pathInfo) {
        if(!isOnline){
            billRecordService.createTaskRecord(pathInfo.getTaskId(),"AGV上装连不上，请检查网络，上装是否开机");
        }
        TaskStatusNotice notice = redisCache.getCacheObject("agvStatus");
        if (notice != null) {
            if (pathInfo.getFromCellCode().equals(notice.getCurrentPositionCode())) {

                //给webSocket发信号
                ThreeData data = new ThreeData();
                data.setType("agv_shangliao");
                sendMsgToWebSocket(data);

                //通知输送线agv已经到达起点
                Integer agvReachOut = 1;
                writeSsx("agvReachOut", agvReachOut);
                billRecordService.createTaskRecord(pathInfo.getTaskId(),"给输送线发送AGV到达上料点信号");
                //agv开始滚动 前上货
                Integer frontInventoryType = 1;
                writeAGV("frontInventoryType", frontInventoryType);
                billRecordService.createTaskRecord(pathInfo.getTaskId(),"给上装发送前上料信号");
                //通知输送线滚动
                Integer outReady = 1;
                writeSsx("outReady", outReady);
                billRecordService.createTaskRecord(pathInfo.getTaskId(),"给输送线出口出库信号");
                log.info("任务" + pathInfo.getId() + "AGV上货中");
                pathInfo.setMemo("AGV上货中");
                pathInfoService.update(pathInfo);

                billRecordService.createTaskRecord(pathInfo.getTaskId(),"AGV上货中");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //agv上货是否完成
    public boolean intoAgvOK(PathInfo pathInfo) {
        //agv前上货完毕信号
//        Integer frontUpReady = redisCache.getCacheObject("frontUpReady");
//        if(frontUpReady==null){
//            frontUpReady = (Integer) readPlc("frontUpReady");
//        }
//        if (frontUpReady != 2) {
//            return false;
//        }
//        redisCache.setCacheObject("frontUpReady",frontUpReady);

        Integer frontUpReady = (Integer) readAGV("frontUpReady");
        if (frontUpReady != 2) {
            pathInfoService.updateMemo(pathInfo,"上装正在上料中。。。");
            return false;
        }


        pathInfoService.updateMemo(pathInfo,"检测到上装已经上料完成");
        //.createTaskRecord(pathInfo.getTaskId(),"检测到上装已经上料完成");
        //通知输送线滚动停止
        Integer outSSXOk = 1;
        writeSsx("outSSXOk", outSSXOk);


        pathInfoService.updateMemo(pathInfo,"通知输送线出库OK");
        //billRecordService.createTaskRecord(pathInfo.getTaskId(),"通知输送线出库OK");
        //agv移动到终点位置

        //输送线信号复位
        Integer agvReachOut = 0;
        Integer outReady = 0;
        writeSsx("agvReachOut", agvReachOut);
        writeSsx("outReady", outReady);



        PositionPath end = new PositionPath();
        end.setPositionCode(pathInfo.getToCellCode());
        end.setType("00");
        PositionPath position = new PositionPath();
        position.setPositionCode("A01");
        position.setType("00");

        ContinueAGVTask task = new ContinueAGVTask();
        task.setReqCode(autoService.getReqCode());
        task.setReqTime(DateUtil.getNowDateTimeString());
        task.setTaskCode(pathInfo.getId().toString());
        task.setNextPositionCode(position);


        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode("hkAgv03");
        hostWcsInterface.setInterfaceName("rcms/services/rest/hikRpcService/continueTask");
        hostWcsInterface.setSendFrom("WCS");
        hostWcsInterface.setSendTo("AGV");
        hostWcsInterface.setContent(JSONObject.toJSONString(task));
        hostWcsInterface.setType(0);
        hostWcsInterfaceService.save(hostWcsInterface);

//        ContinueAGVTask task1 = new ContinueAGVTask();
//        task1.setReqCode(autoService.getReqCode());
//        task1.setReqTime(DateUtil.getNowDateTimeString());
//        task1.setTaskCode(pathInfo.getId().toString());
//        task1.setNextPositionCode(position);
//
//
//        hostWcsInterface.setId(null);
//        hostWcsInterface.setCode("hkAgv03");
//        hostWcsInterface.setInterfaceName("rcms/services/rest/hikRpcService/continueTask");
//        hostWcsInterface.setSendFrom("WCS");
//        hostWcsInterface.setSendTo("AGV");
//        hostWcsInterface.setContent(JSONObject.toJSONString(task1));
//        hostWcsInterface.setType(0);
//        hostWcsInterfaceService.save(hostWcsInterface);


        //给webSocket发信号
        ThreeData data = new ThreeData();
        data.setType("agv");
        data.setFromNode(pathInfo.getFromCellCode());
        data.setToNode(pathInfo.getToCellCode());
        sendMsgToWebSocket(data);


        pathInfoService.updateMemo(pathInfo,"给RCS发送继续执行信号");

        pathInfoService.updateMemo(pathInfo,"AGV上货完成");


        //redisCache.deleteObject("frontUpReady");

        return true;
    }

    @Autowired
    private BillRecordService billRecordService;

    //检测是否到达终点
    public boolean reachGoal(PathInfo pathInfo) {


        //billRecordService.createTaskRecord(pathInfo.getTaskId(),"输送线入口无托盘");


        TaskStatusNotice notice = redisCache.getCacheObject("agvStatus");
        if (notice != null) {
            if (pathInfo.getToCellCode().equals(notice.getCurrentPositionCode())) {
                //检查输送线是否空料
                Integer isSSXEmpty = (Integer) readSsxPlc("isSSXEmpty");
                if (isSSXEmpty != 1) {
                    pathInfoService.updateMemo(pathInfo,"AGV到达终点，但是输送线入口有托盘");
                    return false;
                }

                //给webSocket发信号
                ThreeData data = new ThreeData();
                data.setType("agv_xialiao");
                sendMsgToWebSocket(data);

                //告知输送线agv到达
                Integer agvReachIn = 1;
                writeSsx("agvReachIn", agvReachIn);
                //WMS告知PLC请求进入
                Integer inRequest = 1;
                writeSsx("inRequest", inRequest);
                //输送线是否准备好上货
                Integer inReady = (Integer) readSsxPlc("inReady");
                if (inReady != 1) {
                    pathInfoService.updateMemo(pathInfo,"AGV到达终点，但是输送线未准备好上料");
                    //   return false;
                    return false;
                }
                pathInfoService.updateMemo(pathInfo,"输送线已准备好上料");
                //    billRecordService.createTaskRecord(pathInfo.getTaskId(),"输送线已准备好上料");
                //agv前下货
                Integer frontInventoryType = 2;
                writeAGV("frontInventoryType", frontInventoryType);
//                log.info("AGV开始下货");
//                pathInfo.setMemo("AGV开始下货");
//                pathInfoService.update(pathInfo);

                pathInfoService.updateMemo(pathInfo,"AGV开始下货");
                //billRecordService.createTaskRecord(pathInfo.getTaskId(),"AGV开始下货");
                return true;
            } else {
                pathInfoService.updateMemo(pathInfo,"等待AGV到达终点");

                return false;
            }
        } else {

            pathInfoService.updateMemo(pathInfo,"等待AGV到达终点");
            return false;
        }
    }

    //下货完成
    public boolean outAgvOK(PathInfo pathInfo) {
        //通知输送线进料完成
        Integer inSSXOk = redisCache.getCacheObject("inSSXOk");
        if(inSSXOk==null){
            inSSXOk = (Integer) readSsxPlc("inSSXOk");
        }

        if (inSSXOk != 1) {
            return false;
        }

        redisCache.setCacheObject("inSSXOk",inSSXOk);


        //滚筒停止
        Integer frontRollStop = 1;
        writeAGV("frontRollStop", frontRollStop);
        billRecordService.createTaskRecord(pathInfo.getTaskId(),"发送上装滚筒停止信号");
        //通知agv下货结束
        Integer frontDownReady = 2;
        writeAGV("frontDownReady", frontDownReady);
        billRecordService.createTaskRecord(pathInfo.getTaskId(),"发送上装滚前下货结束信号");

        Integer agvReachIn = 0;
        Integer inRequest = 0;
        writeSsx("agvReachIn", agvReachIn);
        writeSsx("inRequest", inRequest);
        billRecordService.createTaskRecord(pathInfo.getTaskId(),"发送输送线复位信号");


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Integer AGVConfirmFinish = 1;
        writeAGV("AGVConfirmFinish", AGVConfirmFinish);

        ContinueAGVTask task = new ContinueAGVTask();
        task.setReqCode(autoService.getReqCode());
        task.setReqTime(DateUtil.getNowDateTimeString());
        task.setTaskCode(pathInfo.getId().toString());
//        PositionPath path = new PositionPath();
//        path.setPositionCode("X01");
//        path.setType("00");
//        task.setNextPositionCode(path);

        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode("hkAgv03");
        hostWcsInterface.setInterfaceName("rcms/services/rest/hikRpcService/continueTask");
        hostWcsInterface.setSendFrom("WCS");
        hostWcsInterface.setSendTo("AGV");
        hostWcsInterface.setContent(JSONObject.toJSONString(task));
        hostWcsInterface.setType(0);
        hostWcsInterfaceService.save(hostWcsInterface);

        //给webSocket发信号
        ThreeData data = new ThreeData();
        data.setType("agv");
        data.setFromNode(pathInfo.getToCellCode());
        data.setToNode("00");
        sendMsgToWebSocket(data);

        log.info("AGV下货完成");
        pathInfo.setMemo("AGV下货完成");
        pathInfoService.update(pathInfo);


        redisCache.deleteObject("inSSXOk");

        return true;
    }

    private void sendMsgToWebSocket(ThreeData data){
        WebSocketUsers.sendMessageToUsersByText(JSONObject.toJSONString(data));
    }

}
