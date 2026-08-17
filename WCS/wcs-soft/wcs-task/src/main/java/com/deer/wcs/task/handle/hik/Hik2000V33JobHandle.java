package com.deer.wcs.task.handle.hik;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.model.ProPositionContent;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.base.service.ProPositionContentService;
import com.deer.wcs.base.utils.PLCUtils;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.system.service.BillRecordService;
import com.deer.wcs.task.model.DeviceTaskResult;
import com.deer.wcs.task.model.HostWcsInterface;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.DeviceTaskResultService;
import com.deer.wcs.task.service.HostWcsInterfaceService;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import com.deer.wcs.task.utils.WifiIoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;


//NA项目用
@Component("Hik2000V33JobHandle")
public class Hik2000V33JobHandle {

    //绑定货架URL
    private static final  String sendTaskUrlBind="http://192.168.111.80/rcs/rtas/api/robot/controller/carrier/bind";
    //下发任务url
    private static final  String sendTaskUrl="http://192.168.111.20:80/rcs/rtas/api/robot/controller/task/submit";
    private static final  String notifyExcuteResultInfoUrl="http://192.168.10.30:8181/rcms/services/rest/liftCtlService/notifyExcuteResultInfo";

    //继续执行任务url
    private static final  String continueTaskUrl="http://192.168.111.20:80/rcs/rtas/api/robot/controller/task/extend/continue";
    private static final  String cancelTaskUrl="http://192.168.111.20:80/rcs/rtas/api/robot/controller/task/cancel";

    private static final Logger log = LoggerFactory.getLogger(Hik2000V33JobHandle.class);

    @Autowired
    private HostWcsInterfaceService hostWcsInterfaceService;

    @Autowired
    private DeviceTaskResultService deviceTaskResultService;

    @Autowired
    private JobInfoService jobInfoService;


    @Autowired
    private BillRecordService billRecordService;

    @Autowired
    private TaskInfoService taskInfoService;


    // 注入配置好的RestTemplate（单例复用，解决SSL问题）
    @Autowired
    @Qualifier("sslIgnoreRestTemplate")
    private RestTemplate sslIgnoreRestTemplate;


    @Value("${isTest}")
    private String isTest;

    //海康需要绑定货架
    public Boolean sendTaskBind(JobInfo jobInfo){
        if(isTest.equals("true")){
            return true;
        }
        Long taskId = jobInfo.getTaskId();
        TaskInfo taskInfo = taskInfoService.findById(taskId);
        jobInfo.setFromCellCode(taskInfo.getFromCellCode());
        jobInfo.setToCellCode(taskInfo.getToCellCode());
        jobInfoService.update(jobInfo);
        String type = taskInfo.getType();
        String fromCellCode = taskInfo.getFromCellCode();
        String palletCode = null;
        if (fromCellCode.equals("H21") || fromCellCode.equals("H22") || fromCellCode.equals("S21") || fromCellCode.equals("S22")){
            palletCode="200001";
        }else if (fromCellCode.equals("H31") || fromCellCode.equals("H32") || fromCellCode.equals("S31") || fromCellCode.equals("S32")){
            palletCode="300001";
        }else if (fromCellCode.equals("H41") || fromCellCode.equals("H42") || fromCellCode.equals("S41") || fromCellCode.equals("S42")){
            palletCode="400001";
        }else {
            palletCode="100001";
        }
        AgvResult agvResult = sendTaskToRcsBind(palletCode,taskInfo.getFromCellCode());
        if (agvResult == null) {
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务失败【agvResult == null】");
            return false;
        } else if (!agvResult.getCode().equals("0")) {//返回的编码不为0就是失败了
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务失败【"+agvResult.getMessage()+"】");
            return false;
        } else {
            jobInfoService.updateMemo(jobInfo,"向RCS发送绑定货架成功");
            return true;
        }
    }

    //给海康下达任务
    public Boolean sendTask(JobInfo jobInfo){
        if(isTest.equals("true")){
            return true;
        }
        Long taskId = jobInfo.getTaskId();
        TaskInfo taskInfo = taskInfoService.findById(taskId);
        jobInfo.setFromCellCode(taskInfo.getFromCellCode());
        jobInfo.setToCellCode(taskInfo.getToCellCode());
        jobInfoService.update(jobInfo);
        String taskCode = jobInfo.getId().toString();
        String type = jobInfo.getType();
        String code = null;
        String fromCellCode = taskInfo.getFromCellCode();
        if (fromCellCode.equals("H21") || fromCellCode.equals("H22") || fromCellCode.equals("S21") || fromCellCode.equals("S22")){
            code="200001";
        }else if (fromCellCode.equals("H31") || fromCellCode.equals("H32") || fromCellCode.equals("S31") || fromCellCode.equals("S32")){
            code="300001";
        }else if (fromCellCode.equals("H41") || fromCellCode.equals("H42") || fromCellCode.equals("S41") || fromCellCode.equals("S42")){
            code="400001";
        }else {
            code="100001";
        }
        AgvResult agvResult = sendTaskToRcs(jobInfo,taskCode, type, "", jobInfo.getFromCellCode(), jobInfo.getToCellCode(),code);
        if (agvResult == null) {
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务失败【agvResult == null】");

            return false;
        } else if (!agvResult.getCode().equals("SUCCESS")) {//返回的编码不为0就是失败了
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务失败【"+agvResult.getMessage()+"】");

            return false;
        } else {
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务成功");
            return true;
        }
    }

    public Boolean isSuccess(JobInfo jobInfo) {
        String taskCode = jobInfo.getId().toString();
        Long taskId = jobInfo.getTaskId();
        TaskInfo taskInfo = taskInfoService.findById(taskId);
        String taskNo = taskInfo.getTaskNo();
        DeviceTaskResult deviceTaskResult = deviceTaskResultService.getFirstState0ByTaskCode(taskNo);
        if(deviceTaskResult==null){
            return false;
        }
        deviceTaskResult.setState(1);
        deviceTaskResultService.update(deviceTaskResult);
        String type = deviceTaskResult.getType();
        //start : 任务开始
        //outbin : 走出储位
        //end : 任务结束
        //cancel : 任务单取消
        //apply：CTU 料箱取放申请
        switch (type){
            case "start" :{
                jobInfo.setMemo("任务开始");
                jobInfoService.update(jobInfo);
                billRecordService.createTaskRecord(jobInfo.getTaskId(),"RCS任务开始");
                break;
            }
            case "outbin" :{
                jobInfo.setMemo("走出储位");
                jobInfoService.update(jobInfo);
                billRecordService.createTaskRecord(jobInfo.getTaskId(),"RCS走出储位");
                break;
            }
            case "end" :{
                jobInfo.setMemo("任务结束");
                jobInfoService.update(jobInfo);
                billRecordService.createTaskRecord(jobInfo.getTaskId(),"RCS任务结束");
                return true;
            }
            case "leave" :{
                jobInfo.setMemo("取料结束");
                jobInfoService.update(jobInfo);
                billRecordService.createTaskRecord(jobInfo.getTaskId(),"RCS取料结束结束");
                return true;
            }

            case "cancel" :{
                jobInfo.setMemo("任务单取消");
                jobInfoService.update(jobInfo);
                billRecordService.createTaskRecord(jobInfo.getTaskId(),"RCS任务单取消");
                break;
            }
            case "apply" :{
                jobInfo.setMemo("料箱取放申请");
                jobInfoService.update(jobInfo);
                billRecordService.createTaskRecord(jobInfo.getTaskId(),"RCS料箱取放申请");
                break;
            }
        }
        return false;
    }

    @Autowired
    private DeviceValueService deviceValueService;

    //查询接驳位的状态
    private Object readPlc(String valueCode) {
        return deviceValueService.readValueByCode("PLC", valueCode);
    }

    @Autowired
    private AutoService autoService;

    @Autowired
    private PositionInfoService positionInfoService;



    /*private Boolean continueTask(JobInfo jobInfo,DeviceTaskResult deviceTaskResult){
        String node  =deviceTaskResult.getNode();
        //非前置
        if(node.equals(jobInfo.getFromCellCode()) || node.equals(jobInfo.getToCellCode())){
            return true;
        }
        ProPositionContent content = proPositionContentService.findBy("code2",node);
        PositionInfo positionInfo = positionInfoService.findBy("code",content.getCode());
        // types:[
        //        {value:0,name:"工位",color:"#67C23A"}
        //        ,  {value:1,name:"缓存区",color:"#409EFF"}
        //        ,  {value:2,name:"余料缓存区",color:"#E6A23C"}
        //      ]
        Boolean hasPallet=null;
        if(content.getType()==0 || content.getType()==2){
            hasPallet= WifiIoUtil.read(content.getWifiModeIp(),content.getWifiModeOffset(),content.getCode());
            //hasPallet=true;
            if(hasPallet==null){
                jobInfoService.updateMemo(jobInfo,content.getCode()+" 位置光电模块断联");
                return false;
            }
            hasPallet=!hasPallet;
        }
        if(content.getType()==1 ){
            try {
                Short has =(Short) deviceValueService.readValueByCode("PLC", content.getDevceGuangdianAddress());
                hasPallet = has==1;
            }catch (Exception ex){
                deviceTaskResult.setState(0);
                deviceTaskResultService.update(deviceTaskResult);
                jobInfoService.updateMemo(jobInfo,content.getCode()+" 位置读取PLC异常");

                ex.printStackTrace();
                return false;
            }

        }

        String code  =content.getCode();
        String taskCode = jobInfo.getId().toString();
        //起点有货
        if(code.equals(jobInfo.getFromCellCode()) ){
            if(hasPallet){
                AgvResult agvResult= continueTask(autoService.getReqCode(),taskCode);

                if (agvResult == null) {
                    jobInfoService.updateMemo(jobInfo,node+"向RCS继续执行任务失败【agvResult == null】");

                    return false;
                } else if (!agvResult.getCode().equals("0")) {//返回的编码不为0就是失败了
                    jobInfoService.updateMemo(jobInfo,node+"向RCS发送继续执行任务失败【"+agvResult.getMessage()+"】");

                    return false;
                } else {
                    jobInfoService.updateMemo(jobInfo,node+"向RCS发送继续执行任务成功");
                    return true;
                }
            }else {
                jobInfoService.updateMemo(jobInfo,content.getCode()+" 起点位置无托盘");
                return false;
            }
        }

        //终点
        if(code.equals(jobInfo.getToCellCode())){
            if(hasPallet){
                jobInfoService.updateMemo(jobInfo,content.getCode()+" 终点位置有托盘");
                return false;
            }else {
               AgvResult agvResult= continueTask(autoService.getReqCode(),taskCode);

                if (agvResult == null) {
                    jobInfoService.updateMemo(jobInfo,node+"向RCS继续执行任务失败【agvResult == null】");

                    return false;
                } else if (!agvResult.getCode().equals("0")) {//返回的编码不为0就是失败了
                    jobInfoService.updateMemo(jobInfo,node+"向RCS发送继续执行任务失败【"+agvResult.getMessage()+"】");

                    return false;
                } else {
                    jobInfoService.updateMemo(jobInfo,node+"向RCS发送继续执行任务成功");
                    return true;
                }

            }
        }

        return false;

    }*/
    //继续执行
    public Boolean continueTaskAGV(JobInfo jobInfo){
        Long taskId = jobInfo.getTaskId();
        TaskInfo taskInfo = taskInfoService.findById(taskId);
        String taskNo = taskInfo.getTaskNo();
        if (taskNo == null){
            jobInfoService.updateMemo(jobInfo,"没有找到上一步下发任务agv回传的信息");
            return false;
        }
        String type = taskInfo.getType();
        String palletCode = null;
        if (type.equals("CQF03")){
            palletCode ="100001";
        }else {
            palletCode = "100002";
        }
        AgvResult agvResult = continueTask(taskInfo.getToCellCode(),taskNo,palletCode);
        if (agvResult == null) {
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务失败【agvResult == null】");
            return false;
        } else if (!agvResult.getCode().equals("SUCCESS")) {//返回的编码不为0就是失败了
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务失败【"+agvResult.getMessage()+"】");
            return false;
        } else {
            jobInfoService.updateMemo(jobInfo,"向RCS发送继续执行任务成功");
            return true;
        }
    }


    @Autowired
    private ProPositionContentService proPositionContentService;

    /**
     *
     * @param taskCode 下发给agv的任务标s
     * @param type agv定义的任务类型
     * @param podCode agv的料架编码 可为null
     * @param start 起点
     * @param end 终点
     * @return
     */
    public AgvResult sendTaskToRcs(JobInfo jobInfo,String taskCode,String type,String podCode,String start,String end,String code){

        String json = "{\n" +
                "    \"taskType\": \""+type+"\",\n" +
                "    \"targetRoute\": [{\n" +
                "        \"type\": \"SITE\",\n" +
                "        \"code\": \""+start+"\"\n" +
                "    }, {\n" +
                "        \"type\": \"SITE\",\n" +
                "        \"code\": \""+end+"\"\n" +
                "    }],\n" +
                "    \"extra\": {\n" +
                "        \"carrierInfo\": [{\n" +
                "            \"carrierType\": \"1\",\n" +
                "            \"carrierCode\": \""+code+"\"\n" +
                "        }]\n" +
                "    },\n" +
                "    \"initPriority\": 1\n" +
                "}";
        log.info("给agv发的参数:"+json);

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

        //记录回传
        wcsInterface.setUrl(url);
        wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
        wcsInterface.setEndTime(DateUtil.getNowDateTimeString());
        hostWcsInterfaceService.save(wcsInterface);
        AgvResult.AGVRes data = agvResult.getData();
        if (data != null){
            String robotTaskCode = agvResult.getData().getRobotTaskCode();
            Long taskId = jobInfo.getTaskId();
            TaskInfo taskInfo = taskInfoService.findById(taskId);
            taskInfo.setTaskNo(robotTaskCode);
            taskInfoService.update(taskInfo);
        }

        log.info("agv响应结果:"+agvResult);
        return agvResult;
    }


    public AgvResult sendTaskToRcsBind(String palletCode,String code){

        String json = "{\n" +
                "    \"carrierCode\": \""+palletCode+"\",\n" +
                "    \"siteCode\": \""+code+"\"\n" +
                "}";
        log.info("给agv发的参数:"+json);

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
        String url = sendTaskUrlBind;
        AgvResult agvResult = senToRcs(url, HttpMethod.POST, json);

        //记录回传
        wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
        wcsInterface.setEndTime(DateUtil.getNowDateTimeString());
        hostWcsInterfaceService.save(wcsInterface);
        log.info("agv响应结果:"+agvResult);
        return agvResult;
    }


    public void notifyExcuteResultInfo(String code,String uuId,String actionStatus){
        String json = "{\n" +
                "    \"deviceIndex\": \""+code+"\",\n" +
                "    \"uuId\": \""+uuId+"\",\n" +
                "    \"actionStatus\": \""+actionStatus+"\",\n" +
                "    \"deviceType\": \""+"door"+"\"\n" +
                "}";
        log.info("给RCS发的参数:"+json);

        //记录发送接口
        HostWcsInterface wcsInterface = new HostWcsInterface();
        wcsInterface.setCode("HIK");
        wcsInterface.setInterfaceName("通知交管");
        wcsInterface.setSendFrom("WCS");
        wcsInterface.setSendTo("RCS");
        wcsInterface.setContent(json);
        wcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        wcsInterface.setType(0);

        //发送
        String url = notifyExcuteResultInfoUrl;
        AgvResult agvResult = senToRcs(url, HttpMethod.POST, json);

        //记录回传
        wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
        wcsInterface.setEndTime(DateUtil.getNowDateTimeString());
        hostWcsInterfaceService.save(wcsInterface);

    }


    public AgvResult continueTask(String code,String taskCode,String palletCode){
        String json = "{\n" +
                "    \"triggerType\": \"TASK\",\n" +
                "    \"triggerCode\": \""+taskCode+"\",\n" +
                "    \"targetRoute\": {\n" +
                "        \"seq\": 1,\n" +
                "        \"type\": \"SITE\",\n" +
                "        \"code\": \""+code+"\"\n" +
                "    },\n" +
                "    \"extra\": {\n" +
                "        \"carrierInfo\": [{\n" +
                "            \"carrierType\": \"1\",\n" +
                "            \"carrierCode\": \""+palletCode+"\"\n" +
                "        }]\n" +
                "    }\n" +
                "}";
        log.info("给RCS发的参数:"+json);

        //记录发送接口
        HostWcsInterface wcsInterface = new HostWcsInterface();
        wcsInterface.setCode("HIK");
        wcsInterface.setInterfaceName("继续执行任务");
        wcsInterface.setSendFrom("WCS");
        wcsInterface.setSendTo("RCS");
        wcsInterface.setContent(json);
        wcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        wcsInterface.setType(0);
        wcsInterface.setSendStatus(1);

        //发送
        String url = continueTaskUrl;
        AgvResult agvResult = senToRcs(url, HttpMethod.POST, json);

        //记录回传
        wcsInterface.setUrl(url);
        wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
        wcsInterface.setEndTime(DateUtil.getNowDateTimeString());
        hostWcsInterfaceService.save(wcsInterface);

        return agvResult;

    }


    public AgvResult cancelTask(String taskCode){
        String json = "{\n" +
                "    \"robotTaskCode\": \""+taskCode+"\",\n" +
                "    \"cancelType\": \"DROP\",\n" +
                "    \"reason\": \"AMR设备异常\"\n" +
                "}";
        log.info("给RCS发的参数:"+json);

        //记录发送接口
        HostWcsInterface wcsInterface = new HostWcsInterface();
        wcsInterface.setCode("HIK");
        wcsInterface.setInterfaceName("取消任务");
        wcsInterface.setSendFrom("WCS");
        wcsInterface.setSendTo("RCS");
        wcsInterface.setContent(json);
        wcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        wcsInterface.setType(0);
        wcsInterface.setSendStatus(1);

        //发送
        String url = cancelTaskUrl;
        AgvResult agvResult = senToRcs(url, HttpMethod.POST, json);

        //记录回传
        wcsInterface.setUrl(url);
        wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
        wcsInterface.setEndTime(DateUtil.getNowDateTimeString());
        hostWcsInterfaceService.save(wcsInterface);

        return agvResult;

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
        agvResult.setCode("SUCCESS");
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
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 1. 随机16位数字的 X-Ir-request-id
        headers.add("x-lr-request-id", generateRequestId());
//        headers.add("X-lr-appkey", "V4");
//        // 2. 固定值4.3的 X-Ir-version
//        headers.add("X-Ir-version", "4.3");
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        RestTemplate client = new RestTemplate();
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK){
            return response.getBody();
        }
        log.error(url,response.getStatusCode());
        return null;
    }

    // 生成16位随机数字的requestId
    private static String generateRequestId() {
        String prefix = "LR";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        Random random = new Random();
        String randomStr = String.format("%04d", random.nextInt(10000)); // 4位随机数
        return String.format("%s-%s-%s", prefix, timestamp, randomStr);
    }
}
