package com.deer.wcs.task.handle.lg7;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.model.ProPositionContent;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.base.service.ProPositionContentService;
import com.deer.wcs.common.core.domain.model.SysDictData;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.system.service.BillRecordService;
import com.deer.wcs.task.handle.hik.AgvResult;
import com.deer.wcs.task.model.*;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfo;
import com.deer.wcs.task.service.*;
import com.deer.wcs.task.utils.WifiIoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component("Lg7HikJobHandle")
public class Lg7HikJobHandle {

    static String rcsIp = "192.168.10.2";
    static Integer port=8181;


    //发送任务
    private static final  String sendTaskUrl="http://"+rcsIp+":"+port+"/rcms/services/rest/hikRpcService/genAgvSchedulingTask";
    //
    private static final  String notifyExcuteResultInfoUrl="http://"+rcsIp+":"+port+"/rcms/services/rest/liftCtlService/notifyExcuteResultInfo";
    //继续执行任务
    private static final  String continueTaskUrl="http://"+rcsIp+":"+port+"/rcms/services/rest/hikRpcService/continueTask";
    private static final  String cancelTask="http://192.168.111.20:80/rcs/rtas/api/robot/controller/task/cancel";
    private static final  String getTaskInfoUrl="http://"+rcsIp+":"+port+"/rcms/services/rest/hikRpcService/queryTaskStatus ";
    public static final  String getCallBoxUrl="http://"+rcsIp+":"+port+"/wcs/services/rest/beeperOutCtrl";

    //呼叫盒灯的颜色
    public static final String GREEN="green";
    //呼叫盒灭灯
    public static final String LEV_CTRL0="0";
    //呼叫盒亮灯
    public static final String LEV_CTRL1 ="1";

    private static final Logger log = LoggerFactory.getLogger(Lg7HikJobHandle.class);

    @Autowired
    private HostWcsInterfaceService hostWcsInterfaceService;

    @Autowired
    private DeviceTaskResultService deviceTaskResultService;

    @Autowired
    private JobInfoService jobInfoService;


    @Autowired
    private BillRecordService billRecordService;

    @Autowired
    private ProPositionContentService proPositionContentService;

    @Autowired
    private PositionInfoService positionInfoService;

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private CallBoxRecordService callBoxRecordService;

    @Autowired
    private CallBoxInfoService callBoxInfoService;

    @Value("${isTest}")
    private String isTest;

    //获取当前位置的状态
    private static Integer getPalletState(PositionInfo positionInfo,ProPositionContent proPositionContent){
        Integer fromType = proPositionContent.getType();
        Integer state=null;
        if(positionInfo.getInvenState()<0.1){
            state=0;
        }
        else if(proPositionContent.getPalletState()<0.1) {
            state=1;
        }
        else if(proPositionContent.getPalletState()>0.9) {
            state=2;
        }
        if(state==null){
            throw new RuntimeException("未获取到料架状态");
        }
        return state;
    }

    /**
     * 根据作业信息获取任务类型
     *
     * @param jobInfo 作业信息
     * @return 任务类型
     */
    private   TaskType getTaskType(JobInfo jobInfo){
        // 根据任务ID获取任务信息
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        // 根据仓库代码和起始单元格代码查找起始位置的产品位置内容
        ProPositionContent fromContent = proPositionContentService.findByCode(taskInfo.getWareCode(),jobInfo.getFromCellCode());
        // 根据仓库代码和起始单元格代码查找起始位置信息
        PositionInfo from =positionInfoService.findByCode(taskInfo.getWareCode(),jobInfo.getFromCellCode());
        // 根据仓库代码和目标单元格代码查找目标位置的产品位置内容
        ProPositionContent toContent = proPositionContentService.findByCode(taskInfo.getWareCode(),jobInfo.getToCellCode());
        // 根据仓库代码和目标单元格代码查找目标位置信息
        PositionInfo to =positionInfoService.findByCode(taskInfo.getWareCode(),jobInfo.getToCellCode());

        // 获取起始位置的类型
        Integer fromType = fromContent.getType();
        // 获取起始位置的状态
        Integer fromState=getPalletState(from,fromContent);


        // 获取目标位置的类型
        Integer toType = toContent.getType();
        // 获取目标位置的状态
        Integer toState=getPalletState(to,toContent);

        // 根据起始位置和目标位置的类型和状态确定任务类型
        return taskType(fromContent.getItemCode(),fromType,fromState,toType,toState,from.getCode(),to.getCode());
    }

    //用于返回给海康的
    static class TaskType{
        //海康任务模版名称
        String taskType;
        //包含起点、终点
        List<String> list ;//= new ArrayList<>();
        //产品名称
        Integer itemId ;

        public String getTaskType() {
            return taskType;
        }

        public void setTaskType(String taskType) {
            this.taskType = taskType;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public Integer getItemId() {
            return itemId;
        }

        public void setItemId(Integer itemId) {
            this.itemId = itemId;
        }
    }

    //阳极三个缓存站台的命名
    //缓存站台分为左右， 2023 - 2013
    //2023作为缓存站台的命名，2013作为相反面的命名
    private static String  quHuj(String code){
        if(code.equals("2023")){
            return "2013";
        }
        if(code.equals("2024")){
            return "2014";
        }
        if(code.equals("2025")){
            return "2015";
        }
        return null;
    }

    /**
     * 0-仓库 1-缓存架 2-CT 3-RP
     * 0-无料架 1-空料架 2-满料架
     * 返回给海康的
     * @param fromType
     * @param fromState
     * @param toType
     * @param toState
     * @return
     */
    private static TaskType taskType(String itemCode,Integer fromType,Integer fromState,Integer toType,Integer toState,String fromCode,String toCode){
        String taskType=null;
        Integer itemId = null;
        if(itemCode.equals("M50")){
            itemId=50;
        }
        if(itemCode.equals("M53")){
            itemId=53;
        }

        TaskType type = new TaskType();

        //CT
        //CT下满-缓存架上满(M50)
        if(fromType==2 && fromState==2 && toType==1 && toState==0 && itemCode.equals("M50")){
            type.taskType="CT1";
            List<String> list = new ArrayList<>();
            list.add(fromCode);
            list.add(toCode);
            type.list=list;
        }
        //CT下满-缓存架上满(M53)
        if(fromType==2 && fromState==2 && toType==1 && toState==0 && itemCode.equals("M53")){

            type.taskType="CT9";
            List<String> list = new ArrayList<>();
            list.add(fromCode);
            list.add(toCode);
            type.list=list;
        }
        //CT下-缓存架下满上空(M53)
        if(fromType==2 && fromState==2 && toType==1 && toState==1 && itemCode.equals("M53")){

            type.taskType="CT11";
            List<String> list = new ArrayList<>();
            list.add(fromCode);
            list.add(toCode);
            list.add(toCode);
            list.add(fromCode);
            type.list=list;
        }
        //CT下-缓存架下满上空(M50)
        if(fromType==2 && fromState==2 && toType==1 && toState==1 && itemCode.equals("M50")){

            type.taskType="CT5";
            List<String> list = new ArrayList<>();
            list.add(fromCode);
            list.add(toCode);
            list.add(toCode);
            list.add(fromCode);
            type.list=list;
        }
        //CT下满-RP上满(M50)
        if(fromType==2 && fromState==2 && toType==3 && toState==0 && itemCode.equals("M50")){
            taskType="CT3";
            type.taskType="CT3";
            List<String> list = new ArrayList<>();
            list.add(fromCode);
            list.add(toCode);
            type.list=list;
        }
        //CT下-RP上下满上空(M50)
        if(fromType==2 && fromState==2 && toType==3 && toState==1 && itemCode.equals("M50")){
            taskType="CT7";
            type.taskType="CT7";
            List<String> list = new ArrayList<>();
            list.add(fromCode);
            list.add(toCode);
            list.add(toCode);
            list.add(fromCode);
            type.list=list;
        }

        //RP
        //缓存架下满-RP上满(M53)
        if(fromType==1 && fromState==2 && toType==3 && toState==0 && itemCode.equals("M53")){

            type.taskType="CT10";
            List<String> list = new ArrayList<>();
            list.add(quHuj(fromCode));
            list.add(toCode);
            type.list=list;
        }
        //缓存架下满-RP上满(M50)
        if(fromType==1 && fromState==2 && toType==3 && toState==0 && itemCode.equals("M50")){

            type.taskType="CT2";
            List<String> list = new ArrayList<>();
            list.add(quHuj(fromCode));
            list.add(toCode);
            type.list=list;
        }
        //缓存架-RP下满上空(M53)
        if(fromType==1 && fromState==2 && toType==3 && toState==1 && itemCode.equals("M53")){

            type.taskType="CT12";
            List<String> list = new ArrayList<>();
            list.add(quHuj(fromCode));
            list.add(toCode);
            list.add(toCode);
            list.add(quHuj(fromCode));
            type.list=list;
        }
        //缓存架-RP下满上空(M50)
        if(fromType==1 && fromState==2 && toType==3 && toState==1 && itemCode.equals("M50")){
            taskType="CT6";
            type.taskType="CT6";
            List<String> list = new ArrayList<>();
            list.add(quHuj(fromCode));
            list.add(toCode);
            list.add(toCode);
            list.add(quHuj(fromCode));
            type.list=list;
        }

        //仓库
        //缓存架-仓库上满(M53）
        if(fromType==1 && fromState==2 && toType==0 && toState==0 && itemCode.equals("M53")){
            taskType="CT13";
            type.taskType="CT13";
            List<String> list = new ArrayList<>();
            list.add(quHuj(fromCode));
            list.add(toCode);
            type.list=list;
        }
        //缓存架-仓库上满(M50)
        if(fromType==1 && fromState==2 && toType==0 && toState==0 && itemCode.equals("M50")){
            taskType="CT8";
            type.taskType="CT8";
            List<String> list = new ArrayList<>();
            list.add(quHuj(fromCode));
            list.add(toCode);
            type.list=list;

        }
        //缓存架下满 有问题
//        if(fromType==1 && fromState==2 && toType==0 && toState==0 && itemCode.equals("M53")){
//            taskType="CT14";
//        }

        //缓存架上空
        if(fromType==1 && fromState==2 && toType==0 && toState==0 && itemCode.equals("M53")){
            taskType="CT15";
            type.taskType="CT15";
            List<String> list = new ArrayList<>();
            list.add(fromCode);
            list.add(toCode);
            type.list=list;
        }






        type.itemId=itemId;
        return type;
    }

    //给海康下达任务
    public Boolean sendTask(JobInfo jobInfo){

        String taskCode = jobInfo.getId().toString();
        TaskType taskType = getTaskType(jobInfo);
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        //CallBoxRecord callBoxRecord = callBoxRecordService.findById(taskInfo.getCallBoxRecordId());
        //CallBoxInfo callBoxInfo = callBoxInfoService.findById(callBoxRecord.getCallBoxInfoId());
        if(taskType.taskType==null){
            jobInfoService.updateMemo(jobInfo,"未匹配到任务模板");
            return false;
        }
        if(taskType.list==null || taskType.list.isEmpty()){
            jobInfoService.updateMemo(jobInfo,"未匹配路径LIST");
            return false;
        }
        if(isTest.equals("true")){
            return true;
        }
        AgvResult agvResult = sendTaskToRcs(taskCode, taskType.taskType  ,taskType.list,  taskType.itemId);
        if (agvResult == null) {
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务失败【agvResult == null】");

            return false;
        } else if (!agvResult.getCode().equals("0")) {//返回的编码不为0就是失败了
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务失败【"+agvResult.getMessage()+"】");

            return false;
        } else {
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务成功");
            //给呼叫盒发送亮绿灯
            //sendCallBox(callBoxRecord,GREEN,LEV_CTRL1,callBoxInfo.getDeviceId());
            return true;
        }
    }

    public Boolean isSuccess(JobInfo jobInfo) {
        String taskCode = jobInfo.getId().toString();
        DeviceTaskResult deviceTaskResult = deviceTaskResultService.getFirstState0ByTaskCode(taskCode);
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        //CallBoxRecord callBoxRecord = callBoxRecordService.findById(taskInfo.getCallBoxRecordId());
        //CallBoxInfo callBoxInfo = callBoxInfoService.findById(callBoxRecord.getCallBoxInfoId());
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
                //给呼叫盒发送灭灯
                //sendCallBox(callBoxRecord,GREEN,LEV_CTRL0,callBoxInfo.getDeviceId());
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


    private Boolean continueTask(JobInfo jobInfo,DeviceTaskResult deviceTaskResult){
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

    }


    public static void main(String[] args) {
        String aaa="123456";
        System.out.println(aaa.substring(0,aaa.length()-1));
    }

    /**
     *
     * @param taskCode 下发给agv的任务标s
     * @param type agv定义的任务类型
     * @param list 点集合
     * @return
     */
    public AgvResult sendTaskToRcs(String taskCode,String type,List<String> list,Integer itemId){
//        ProPositionContent from = proPositionContentService.findBy("code",start);
//        ProPositionContent to = proPositionContentService.findBy("code",end);
//        if(from.getCode2()==null || from.getCode2().trim().equals("")){
//            AgvResult agvResult = new AgvResult();
//            agvResult.setCode("2");
//            agvResult.setMessage(start+" 未设置前置编码");
//            return agvResult;
//        }
//        if(to.getCode2()==null || to.getCode2().trim().equals("")){
//            AgvResult agvResult = new AgvResult();
//            agvResult.setCode("2");
//            agvResult.setMessage(end+" 未设置前置编码");
//            return agvResult;
//        }

        String codes="";
        for (String code:list){
            codes+="        {\n" +
                    "            \"positionCode\": \""+  code+"\",\n" +
                    "            \"type\": \"00\"\n" +
                    "        },\n" ;
        }
        codes.substring(0,codes.length()-1);


        String json = "{\n" +
                "    \"reqCode\": \""+taskCode+"\",\n" +
                "    \"carrierWeight\": \"100\",\n" +
                "    \"materialType\": \""+itemId+"\",\n" +
              //  "    \"clientCode\": \"\",\n" +
                "    \"taskTyp\": \""+type+"\",\n" +
                "    \"taskCode\": \""+taskCode+"\",\n" +
               // "    \"podCode\": "+podCode+",\n" +
                "    \"positionCodePath\": [\n" +
                        codes +
                        "]"+
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

        try {

            //记录回传
            wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
            wcsInterface.setEndTime(DateUtil.getNowDateTimeString());
            hostWcsInterfaceService.save(wcsInterface);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        log.info("agv响应结果:"+agvResult);
        return agvResult;
    }




    public AgvResult cancelTask(String taskNo){
        String reqCode =autoService.getReqCode();
        String json = "{\n" +
                "    \"robotTaskCode\": \""+taskNo+"\",\n" +
                "    \"cancelType\": \"DROP\",\n" +
                "    \"reason\": \"AMR设备异常\",\n" +
                "        \"extra\": {}\n" +
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
        //发送
        String url = cancelTask;
        AgvResult agvResult = senToRcs(url, HttpMethod.POST, json);

        //记录回传
        wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
        wcsInterface.setEndTime(DateUtil.getNowDateTimeString());
        hostWcsInterfaceService.save(wcsInterface);

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


    public AgvResult continueTask(String reqCode,String taskCode){
        try {
            String json = "{\n" +
                    "\"reqCode\": \""+reqCode+"\",\n" +
                    "\"taskCode\": \""+taskCode+"\",\n" +
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
            wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
            wcsInterface.setEndTime(DateUtil.getNowDateTimeString());
            hostWcsInterfaceService.save(wcsInterface);

            return agvResult;
        }catch (Exception ex){
            AgvResult agvResult=new AgvResult();
            agvResult.setCode("-1");
            agvResult.setMessage(ex.getMessage());
            return agvResult;
        }
    }



    public AgvResult continueTaskByAgvCode(String reqCode,String taskCode){
        HikTask hikTask = getTaskInfo(autoService.getReqCode(),taskCode);
        if(hikTask==null){
            AgvResult agvResult=new AgvResult();
            agvResult.setCode("-1");
            agvResult.setMessage("未获取到AGV编码");
            return agvResult;
        }
        try {
            String json = "{\n" +
                    "\"reqCode\": \""+reqCode+"\",\n" +
                    "\"agvCode\": \""+hikTask.getAgvCode()+"\",\n" +
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
            wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
            wcsInterface.setEndTime(DateUtil.getNowDateTimeString());
            hostWcsInterfaceService.save(wcsInterface);

            return agvResult;
        }catch (Exception ex){
            AgvResult agvResult=new AgvResult();
            agvResult.setCode("-1");
            agvResult.setMessage(ex.getMessage());
            return agvResult;
        }
    }

    public HikTask getTaskInfo(String reqCode,String taskCode){
        try {

            String json ="{" +
                    "\"reqCode\": \"1541954B96B1110\",\n" +
                    "\"taskCodes\": [\""+taskCode+"\"]\n" +
                    "}";

            log.info("给RCS发的参数:"+json);

            //记录发送接口
            HostWcsInterface wcsInterface = new HostWcsInterface();
            wcsInterface.setCode("HIK");
            wcsInterface.setInterfaceName("查询任务状态");
            wcsInterface.setSendFrom("WCS");
            wcsInterface.setSendTo("RCS");
            wcsInterface.setContent(json);
            wcsInterface.setStartTime(DateUtil.getNowDateTimeString());
            wcsInterface.setType(0);
            wcsInterface.setSendStatus(1);

            //发送
            String url = getTaskInfoUrl;
            AgvResult agvResult = senToRcs(url, HttpMethod.POST, json);

            //记录回传
            wcsInterface.setRecv(JSONObject.toJSONString(agvResult));
            wcsInterface.setEndTime(DateUtil.getNowDateTimeString());
            hostWcsInterfaceService.save(wcsInterface);

//            if(agvResult.getCode().equals("0")){
//                JSONArray jsonArray = JSON.parseArray(agvResult.getData());
//                List<HikTask> list =jsonArray.toList(HikTask.class);
//                if(!list.isEmpty()){
//                    return list.get(0);
//                }
//            }
            return null;
        }catch (Exception ex){
            AgvResult agvResult=new AgvResult();
            agvResult.setCode("-1");
            agvResult.setMessage(ex.getMessage());
            return null;
        }
    }


    /**
     * 给呼叫盒发送亮灯指令
     * @param callBoxRecord
     * @param colour 灯的颜色 一共俩种 red、green
     * @param type 0-低电平（灭灯） 1-高点平（亮灯）
     * @return
     */
    public static boolean sendCallBox(CallBoxRecord callBoxRecord, String colour, String type,String deviceId){
        String json = "{\n" +
                "    \"msgType\": \"ioCtrl\",\n" +
                "    \"deviceId\": \""+deviceId+"\",\n" +
                "    \"deviceIp\": \""+callBoxRecord.getIp()+"\",\n" +
                "    \"ioCtrl\": [\n" +
                "        {\n" +
                "            \"pinIndex\": \""+callBoxRecord.getBtn()+"\",\n" +
                "            \"mode\": \"0\",\n" +
                "            \"lev_ctrl\": \""+type+"\",\n" +
                "            \"time\": \"1000\",\n" +
                "            \"col_ctl\": \""+colour+"\",\n" +
                "            \"interval\": \"0\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        CallBoxResult callBoxResult = senToCallBox(Lg7HikJobHandle.getCallBoxUrl, HttpMethod.POST, json);
        if (callBoxResult.getCode().equals("0")){
            return true;
        }
        return false;
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
//        try {
//            String resultStr = client(url,method,json);
//            agvResult = JSON.parseObject(resultStr, AgvResult.class);
//        }catch (Exception exception){
//            //code是agv返回的信息 只有1代表成功，只要不是1都是失败了
//            agvResult.setCode("2");
//            agvResult.setMessage(exception.getMessage());
//            exception.printStackTrace();
//        }

        return agvResult;
    }

    /**
     *
     * 所有给呼叫盒发任务的  统一调用这个方法
     * @param url
     * @param method
     * @param json
     * @return
     */
    public static CallBoxResult senToCallBox(String url, HttpMethod method, String json) {
        CallBoxResult Result = new CallBoxResult();
        Result.setCode("0");
//        try {
//            String resultStr = client(url,method,json);
//            Result = JSON.parseObject(resultStr, CallBoxResult.class);
//        }catch (Exception exception){
//            //code是agv返回的信息 只有1代表成功，只要不是1都是失败了
//            Result.setCode("2");
//            Result.setMessage(exception.getMessage());
//            exception.printStackTrace();
//        }

        return Result;
    }

    public static String client(String url, HttpMethod method, String json) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        RestTemplate client = new RestTemplate();
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK){
            return response.getBody();
        }
        log.error(url,response.getStatusCode());
        return null;
    }

    static class CallBoxResult{
        private String code;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
