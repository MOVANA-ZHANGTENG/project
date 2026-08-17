package com.deer.wcs.jxg.handle;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.model.PathInfo;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.task.handle.hik.AgvResult;
import com.deer.wcs.task.model.HostWcsInterface;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.model.ThreeData;
import com.deer.wcs.task.model.haikang.ContinueAGVTask;
import com.deer.wcs.task.model.haikang.GenAGVTask;
import com.deer.wcs.task.model.haikang.PositionPath;
import com.deer.wcs.task.model.haikang.TaskStatusNotice;
import com.deer.wcs.task.service.HostWcsInterfaceService;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import com.deer.wcs.task.websocket.WebSocketUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("Jxg2HikHandle")
public class Jxg2HikHandle {

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private DeviceValueService deviceValueService;
    @Autowired
    private AutoService autoService;

    @Autowired
    private JobInfoService jobInfoService;


    @Autowired
    private HostWcsInterfaceService hostWcsInterfaceService;


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

    //起点是否有货
    public boolean ssxIsNotEmpty(JobInfo jobInfo) {

        //输送线是否请求排除
        Integer outRequest = (Integer) readSsxPlc("outRequest");
        if (outRequest != 1) {
            jobInfoService.updateMemo(jobInfo,"输送线是否请求排出信号为0");
            return false;
        }
        return true;
    }

    private static final  String sendTaskUrl="http://192.168.3.110:8182/rcms/services/rest/hikRpcService/genAgvSchedulingTask";
    private static final  String notifyExcuteResultInfoUrl="http://192.168.3.110:8182/rcms/services/rest/liftCtlService/notifyExcuteResultInfo";
    private static final  String continueTaskUrl="http://192.168.3.110:8182/rcms/services/rest/hikRpcService//continueTask";


    //创建agv任务
    public boolean createAgvTask(JobInfo jobInfo) {
        if(!isOnline){
            jobInfoService.updateMemo(jobInfo,"AGV上装连不上，请检查网络，上装是否开机");
            return false;
        }


        redisCache.deleteObject("agvStatus");

        TaskInfo taskInfo  = taskInfoService.findById(jobInfo.getTaskId());
        String json = "{\n" +
                "    \"reqCode\": \""+autoService.getReqCode()+"\",\n" +
                "    \"taskTyp\": \"GtTest\",\n" +
                "    \"taskCode\": \""+jobInfo.getTaskId().toString()+"\",\n" +
                "    \"reqTime\": \""+DateUtil.getNowDateTimeString()+"\",\n" +
                "    \"positionCodePath\": [\n" +
                "        {\n" +
                "            \"positionCode\": \""+taskInfo.getFromCellCode()+"\",\n" +
                "            \"type\": \"00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"positionCode\": \""+  "A01"+"\",\n" +
                "            \"type\": \"00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"positionCode\": \""+taskInfo.getToCellCode()+"\",\n" +
                "            \"type\": \"00\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        //给webSocket发信号
        ThreeData data = new ThreeData();
        data.setType("agv");
        data.setFromNode("00");
        data.setToNode("C01");
        sendMsgToWebSocket(data);

        //记录发送接口
        HostWcsInterface wcsInterface = new HostWcsInterface();
        wcsInterface.setCode("hkAgv01");
        wcsInterface.setInterfaceName("genAgvSchedulingTask");
        wcsInterface.setSendFrom("WCS");
        wcsInterface.setSendTo("AGV");
        wcsInterface.setContent(json);
        wcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        wcsInterface.setType(0);

        //发送
        String url = sendTaskUrl;
        AgvResult agvResult = senToRcs(url, HttpMethod.POST, json);
        wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
        wcsInterface.setEndTime(DateUtil.getNowDateTimeString());

        //记录回传
        if(agvResult.getCode().equals("0")){
            jobInfoService.updateMemo(jobInfo,"AGV任务给RCS发送成功");
            wcsInterface.setSendStatus(1);
            wcsInterface.setSendResult(1);
            hostWcsInterfaceService.save(wcsInterface);
            return true;
        }else {
            jobInfoService.updateMemo(jobInfo,"AGV任务给RCS发送失败，"+agvResult.getMessage());
            wcsInterface.setSendStatus(1);
            wcsInterface.setSendResult(0);
            hostWcsInterfaceService.save(wcsInterface);
            return false;
        }
    }

    //创建agv任务
    public boolean createAgvTask2(JobInfo jobInfo) {
        if(!isOnline){
            jobInfoService.updateMemo(jobInfo,"AGV上装连不上，请检查网络，上装是否开机");
            return false;
        }


        redisCache.deleteObject("agvStatus");

        TaskInfo taskInfo  = taskInfoService.findById(jobInfo.getTaskId());
        String json = "{\n" +
                "    \"reqCode\": \""+autoService.getReqCode()+"\",\n" +
                "    \"taskTyp\": \"LIAOCHE\",\n" +
                "    \"taskCode\": \""+jobInfo.getTaskId().toString()+"\",\n" +
                "    \"reqTime\": \""+DateUtil.getNowDateTimeString()+"\",\n" +
                "    \"positionCodePath\": [\n" +
                "        {\n" +
                "            \"positionCode\": \""+taskInfo.getFromCellCode()+"\",\n" +
                "            \"type\": \"00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"positionCode\": \""+  "A01"+"\",\n" +
                "            \"type\": \"00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"positionCode\": \""+taskInfo.getToCellCode()+"\",\n" +
                "            \"type\": \"00\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        //给webSocket发信号
//        ThreeData data = new ThreeData();
//        data.setType("agv");
//        data.setFromNode("00");
//        data.setToNode("C01");
//        sendMsgToWebSocket(data);

        //记录发送接口
        HostWcsInterface wcsInterface = new HostWcsInterface();
        wcsInterface.setCode("hkAgv01");
        wcsInterface.setInterfaceName("genAgvSchedulingTask");
        wcsInterface.setSendFrom("WCS");
        wcsInterface.setSendTo("AGV");
        wcsInterface.setContent(json);
        wcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        wcsInterface.setType(0);

        //发送
        String url = sendTaskUrl;
        AgvResult agvResult = senToRcs(url, HttpMethod.POST, json);
        wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
        wcsInterface.setEndTime(DateUtil.getNowDateTimeString());

        //记录回传
        if(agvResult.getCode().equals("0")){
            jobInfoService.updateMemo(jobInfo,"AGV任务给RCS发送成功");
            wcsInterface.setSendStatus(1);
            wcsInterface.setSendResult(1);
            hostWcsInterfaceService.save(wcsInterface);
            return true;
        }else {
            jobInfoService.updateMemo(jobInfo,"AGV任务给RCS发送失败，"+agvResult.getMessage());
            wcsInterface.setSendStatus(1);
            wcsInterface.setSendResult(0);
            hostWcsInterfaceService.save(wcsInterface);
            return false;
        }
    }

    /**
     * 检测AGV是否到达起点
     * @param jobInfo
     * @return
     */
    public boolean agvArriveFrom(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        TaskStatusNotice notice = redisCache.getCacheObject("agvStatus");
        if(notice==null){
            jobInfoService.updateMemo(jobInfo,"等待AGV到达起点");
            return false;
        }
        redisCache.deleteObject("agvStatus");
        if (taskInfo.getFromCellCode().equals(notice.getCurrentPositionCode())) {
            //给webSocket发信号
            ThreeData data = new ThreeData();
            data.setType("agv_xialiao");
            sendMsgToWebSocket(data);
            jobInfoService.updateMemo(jobInfo,"AGV到达起点");
            return true;
        } else {
            jobInfoService.updateMemo(jobInfo,"AGV未到达起点，当前位置："+notice.getCurrentPositionCode());
            return false;
        }
    }

    //通知输送线agv已经到达起点
    public boolean writeSsxAgvReachOut(JobInfo jobInfo) {
        if(!isOnline){
            jobInfoService.updateMemo(jobInfo,"AGV上装连不上，请检查网络，上装是否开机");
            return false;
        }

        Integer agvReachOut = 1;
        writeSsx("agvReachOut", agvReachOut);
        jobInfoService.updateMemo(jobInfo,"给输送线发送AGV到达上料点信号");
        return true;
    }

    //给上装发送前上料信号
    public boolean writeAGVFrontInventoryType(JobInfo jobInfo) {
        if(!isOnline){
            jobInfoService.updateMemo(jobInfo,"AGV上装连不上，请检查网络，上装是否开机");
            return false;
        }

        Integer frontInventoryType = 1;
        writeAGV("frontInventoryType", frontInventoryType);
        jobInfoService.updateMemo(jobInfo,"给上装发送前上料信号");
        return true;
    }
    //通知输送线滚动
    public boolean writeSsxOutReady(JobInfo jobInfo) {
        if(!isOnline){
            jobInfoService.updateMemo(jobInfo,"AGV上装连不上，请检查网络，上装是否开机");
            return false;
        }

        Integer outReady = 1;
        writeSsx("outReady", outReady);
        jobInfoService.updateMemo(jobInfo,"通知输送线滚动");
        return true;
    }



    /**
     * 检测上料完成
     * @param jobInfo
     * @return
     */
    public boolean readAGVFrontUpReady(JobInfo jobInfo) {
        if(!isOnline){
            jobInfoService.updateMemo(jobInfo,"AGV上装连不上，请检查网络，上装是否开机");
            return false;
        }

        Integer frontUpReady = (Integer) readAGV("frontUpReady");
        if (frontUpReady != 2) {
            jobInfoService.updateMemo(jobInfo,"上装正在上料中。。。");
            return false;
        }else {
            writeAGV("AGVConfirmFinish", 1);
            jobInfoService.updateMemo(jobInfo,"上装已经上料完成");
            return true;
        }
    }

    /**
     * 通知输送线上料完成
     * @param jobInfo
     * @return
     */
    public boolean writeSsxOutSSXOk(JobInfo jobInfo) {
        if(!isOnline){
            jobInfoService.updateMemo(jobInfo,"AGV上装连不上，请检查网络，上装是否开机");
            return false;
        }
        Integer outSSXOk = 1;
        writeSsx("outSSXOk", outSSXOk);

        writeSsx("agvReachOut", 0);
        writeSsx("outReady", 0);
        jobInfoService.updateMemo(jobInfo,"通知输送线上料完成，给输送线信号复位");
        return true;
    }

    @Autowired
    private TaskInfoService taskInfoService;
    /**
     * 起点上料完成通知AGV继续执行任务
     * @param jobInfo
     * @return
     */
    public boolean continueTask1(JobInfo jobInfo) {
        TaskInfo taskInfo  = taskInfoService.findById(jobInfo.getTaskId());
        PositionPath end = new PositionPath();
        end.setPositionCode(taskInfo.getToCellCode());
        end.setType("00");
        PositionPath position = new PositionPath();
        position.setPositionCode("A01");
        position.setType("00");

        ContinueAGVTask task = new ContinueAGVTask();
        task.setReqCode(autoService.getReqCode());
        task.setReqTime(DateUtil.getNowDateTimeString());
        task.setTaskCode(jobInfo.getTaskId().toString());
        task.setNextPositionCode(position);

        String json = JSONObject.toJSONString(task);

        //记录发送接口
        HostWcsInterface wcsInterface = new HostWcsInterface();
        wcsInterface.setCode("继续执行任务");
        wcsInterface.setInterfaceName("继续执行任务");
        wcsInterface.setSendFrom("WCS");
        wcsInterface.setSendTo("AGV");
        wcsInterface.setContent(json);
        wcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        wcsInterface.setType(0);


        AgvResult agvResult = senToRcs(continueTaskUrl, HttpMethod.POST, json);
        wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
        wcsInterface.setEndTime(DateUtil.getNowDateTimeString());
        //记录回传
        if(agvResult.getCode().equals("0")){
            jobInfoService.updateMemo(jobInfo,"AGV任务继续执行成功");

            wcsInterface.setSendStatus(1);
            wcsInterface.setSendResult(1);
            hostWcsInterfaceService.save(wcsInterface);


            //给webSocket发信号
            ThreeData data = new ThreeData();
            data.setType("agv");
            data.setFromNode("C01");
            data.setToNode("R01");
            sendMsgToWebSocket(data);
            return true;
        }else {
            jobInfoService.updateMemo(jobInfo,"AGV任务继续执行失败，"+agvResult.getMessage());
            wcsInterface.setSendStatus(1);
            wcsInterface.setSendResult(0);
            hostWcsInterfaceService.save(wcsInterface);
            return false;
        }
    }

    /**
     * 检测是否到达终点
     * @param jobInfo
     * @return
     */
    public boolean agvArriveTo(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        TaskStatusNotice notice = redisCache.getCacheObject("agvStatus");
        if(notice==null){
            jobInfoService.updateMemo(jobInfo,"等待AGV到达终点");
            return false;
        }
        redisCache.deleteObject("agvStatus");
        if (taskInfo.getToCellCode().equals(notice.getCurrentPositionCode())) {
            //给webSocket发信号
            ThreeData data = new ThreeData();
            data.setType("agv_xialiao");
            sendMsgToWebSocket(data);
            jobInfoService.updateMemo(jobInfo,"AGV到达终点");
            return true;
        } else {
            jobInfoService.updateMemo(jobInfo,"AGV未到达终点，当前位置："+notice.getCurrentPositionCode());
            return false;
        }
    }


    /**
     * 检测是否可以在终点上料
     * @param jobInfo
     * @return
     */
    public Boolean readSsxCanIn(JobInfo jobInfo){
        Integer isSSXEmpty = (Integer) readSsxPlc("isSSXEmpty");
        if (isSSXEmpty != 1) {
            jobInfoService.updateMemo(jobInfo,"AGV到达终点，但是输送线入口有托盘");
            return false;
        }

        //告知输送线agv到达
        writeSsx("agvReachIn", 1);
        //WMS输送线请求进入
        writeSsx("inRequest", 1);
        //输送线是否准备好上货
        Integer inReady = (Integer) readSsxPlc("inReady");
        if (inReady != 1) {
            jobInfoService.updateMemo(jobInfo,"AGV到达终点，但是输送线未准备好上料");
            return false;
        }
        jobInfoService.updateMemo(jobInfo,"输送线终点允许");
        return true;
    }

    /**
     * 给AGV上装发送终点上输送线信号
     * @param jobInfo
     * @return
     */
    public Boolean frontInventoryType2(JobInfo jobInfo){
        writeAGV("frontInventoryType", 2);
        jobInfoService.updateMemo(jobInfo,"给AGV上装发送终点上输送线信号");
        return true;
    }

    /**
     * 检测上货完成
     * @param jobInfo
     * @return
     */
    public Boolean agvFinishIn(JobInfo jobInfo){
        //输送线下货完成
        Integer inSSXOk = (Integer) readSsxPlc("inSSXOk");
        if(inSSXOk != 1){
            jobInfoService.updateMemo(jobInfo,"等待输送线下货完成信号");
            return false;

        }
        //滚筒停止
        Integer frontRollStop = 1;
        writeAGV("frontRollStop", 1);
        jobInfoService.updateMemo(jobInfo,"发送上装滚筒停止信号");
        //通知agv下货结束
        Integer frontDownReady = 2;
        writeAGV("frontDownReady", 2);
        jobInfoService.updateMemo(jobInfo,"发送上装滚前下货结束信号");

        writeSsx("agvReachIn", 0);
        writeSsx("inRequest", 0);
        jobInfoService.updateMemo(jobInfo,"发送输送线复位信号");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Integer AGVConfirmFinish = 1;
        writeAGV("AGVConfirmFinish", AGVConfirmFinish);
        jobInfoService.updateMemo(jobInfo,"给上装发送确认完成信号");
        return true;
    }

    /**
     * 在终点完成AGV-输送线上料 继续执行AGV任务
     * @param jobInfo
     * @return
     */
    public boolean continueTask2(JobInfo jobInfo) {

        TaskInfo taskInfo  = taskInfoService.findById(jobInfo.getTaskId());
        ContinueAGVTask task = new ContinueAGVTask();
        task.setReqCode(autoService.getReqCode());
        task.setReqTime(DateUtil.getNowDateTimeString());
        task.setTaskCode(jobInfo.getTaskId().toString());

        String json = JSONObject.toJSONString(task);

        //记录发送接口
        HostWcsInterface wcsInterface = new HostWcsInterface();
        wcsInterface.setCode("继续执行任务");
        wcsInterface.setInterfaceName("继续执行任务");
        wcsInterface.setSendFrom("WCS");
        wcsInterface.setSendTo("AGV");
        wcsInterface.setContent(json);
        wcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        wcsInterface.setType(0);


        AgvResult agvResult = senToRcs(continueTaskUrl, HttpMethod.POST, json);
        wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
        wcsInterface.setEndTime(DateUtil.getNowDateTimeString());

        //记录回传
        if(agvResult.getCode().equals("0")){
            jobInfoService.updateMemo(jobInfo,"在终点完成AGV-输送线上料 继续执行AGV任务 成功");
            wcsInterface.setSendStatus(1);
            wcsInterface.setSendResult(1);
            hostWcsInterfaceService.save(wcsInterface);
            return true;
        }else {
            jobInfoService.updateMemo(jobInfo,"在终点完成AGV-输送线上料 继续执行AGV任务 失败，"+agvResult.getMessage());
            wcsInterface.setSendStatus(1);
            wcsInterface.setSendResult(0);
            hostWcsInterfaceService.save(wcsInterface);
            return false;
        }
    }





    /**
     *
     * 所有给AGV发任务的  统一调用这个方法
     * @param url
     * @param method
     * @param json
     * @return
     */
    public static AgvResult senToRcs(String url, HttpMethod method, String json) {
        AgvResult agvResult = new AgvResult();
        agvResult.setCode("0");
        try {
            String resultStr = client(url,method,json);
            agvResult = JSON.parseObject(resultStr, AgvResult.class);
        }catch (Exception exception){
            //code是agv返回的信息 只有1代表成功，只要不是1都是失败了
            agvResult.setCode("2");
            agvResult.setMessage(exception.getMessage());
            exception.printStackTrace();
        }

        return agvResult;
    }

    public static String client(String url, HttpMethod method, String json) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        RestTemplate client = new RestTemplate();
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK){
            return response.getBody();
        }
        return null;
    }

    private void sendMsgToWebSocket(ThreeData data){
        WebSocketUsers.sendMessageToUsersByText(JSONObject.toJSONString(data));
    }


}
