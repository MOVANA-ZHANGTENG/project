package com.deer.wcs.task.handle.hik;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.*;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.system.service.BillRecordService;
import com.deer.wcs.task.model.*;
import com.deer.wcs.task.service.*;
import com.deer.wcs.task.utils.WifiIoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Condition;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.deer.wcs.base.web.ProPositionContentController.WrongPlacement;


//NA项目用
@Component("Hik_LG_NA_YANG_JobHandle")
public class Hik_LG_NA_YANG_JobHandle {

    private static final  String sendTaskUrl="http://192.168.10.30:8181/rcms/services/rest/hikRpcService/genAgvSchedulingTask";
    private static final  String notifyExcuteResultInfoUrl="http://192.168.10.30:8181/rcms/services/rest/liftCtlService/notifyExcuteResultInfo";
    private static final  String continueTaskUrl="http://192.168.10.30:8181/rcms/services/rest/hikRpcService//continueTask";

    private static final Logger log = LoggerFactory.getLogger(Hik_LG_NA_YANG_JobHandle.class);

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

    @Autowired
    private ItemInfoService itemInfoService;

    @Autowired
    private PlcReadStationService plcReadStationService;

    @Value("${isTest}")
    private String isTest;


    //定时读取站台PLC的信号
    //分为Name/id/余量
    //根据余量进行排名，修改站台的物料类型
    //查找所有有备用站台的站台
    public void updateItemType(){
        //查找所有需要读取PLC信号的站台
        List<ProPositionContent> proPositionContents = proPositionContentService.findByYangJIDeviceCode();
        for (ProPositionContent positionContent:proPositionContents){
            String deviceCode = positionContent.getDeviceCode();
            String[] split = deviceCode.split(";");
            List<String> list = Arrays.asList(split);
            //list集合中存了当前站台的所有罐桶的信号，比较余量最小的哪个
            String Id = compareLast(list);
            if (Id != null){
                //修改当前站台的物料类型
                Condition condition = new Condition(ItemInfo.class);
                condition.createCriteria().andEqualTo("itemCode",Id);
                List<ItemInfo> itemInfos = itemInfoService.findByCondition(condition);
                if(itemInfos != null && !itemInfos.isEmpty()){
                    //修改
                    positionContent.setItemCode(itemInfos.get(0).getItemCode());
                    proPositionContentService.update(positionContent);
                }else {
                    log.info("查到的最小余量的物料编码找到："+Id);
                }
            }
        }
    }



    //list是当前站台的所有余量信号
    private String compareLast(List<String> list) {
        //找到最小的余量的
        PlcReadStation byLastSmall = plcReadStationService.findByLastSmall(list);
        if (byLastSmall == null){
            return null;
        }
        return byLastSmall.getMaterialId();
    }


    //读取所有的站台信号
    public void readAllStation(){
        Condition condition = new Condition(PlcReadStation.class);
        condition.createCriteria().andEqualTo("disableState",0);
        List<PlcReadStation> list = plcReadStationService.findByCondition(condition);
        for (PlcReadStation plcReadStation:list){
            String name = (String) deviceValueService.readValueByCode("PLC2", plcReadStation.getDeviceCodeName());
            String id  = (String) deviceValueService.readValueByCode("PLC2", plcReadStation.getDeviceCodeId());
            ProPositionContent proPositionContent = proPositionContentService.findByCode(null, plcReadStation.getCode());
            if (plcReadStation.getDeviceCodeLast() != null && !plcReadStation.getDeviceCodeLast().equals("")){
                Integer last = (Integer) deviceValueService.readValueByCode("PLC2", plcReadStation.getDeviceCodeLast());
                plcReadStation.setLast(last+"");
                proPositionContent.setLast(last+"");
            }
            plcReadStation.setName(name);
            plcReadStation.setMaterialId(id);
            plcReadStationService.update(plcReadStation);
            proPositionContent.setMaterialName(name);
            proPositionContent.setMaterialId(id);
            proPositionContentService.update(proPositionContent);
        }
    }


    //plc定时任务清除报警信息
    public void removeWrongPlacement(){
        Short remove =(Short) deviceValueService.readValueByCode("PLC2", "removeWrongPlacement");
        if (remove == 1){
            del();
        }
    }

    //plc心跳
    public void heat(){
        try {
            Short heatRead =(Short) deviceValueService.readValueByCode("PLC2", "heatWrite");
            if (heatRead == 0){
                deviceValueService.writeValueByCode("PLC2", "heatWrite",(short)1);
            }else if (heatRead ==1){
                deviceValueService.writeValueByCode("PLC2", "heatWrite",(short)0);
            }
        }catch (Exception e){
            log.error("心跳异常{}",e.getMessage());
        }
    }

    //检测阴/阳级所有站台是否低于阈值
    public void detectionNumber(){
        //找到对应仓库的所有产品信息
        List<ContentReport> report = proPositionContentService.report();
        //比较那些是低于阈值的
        for (ContentReport contentReport:report){
            //当前持有数量
            Integer hasNum = contentReport.getHasNum();
            //最低阈值
            Integer limitNum = contentReport.getLimitNum();
            if (hasNum != null && limitNum != null && hasNum<limitNum){
                Short Read =(Short) deviceValueService.readValueByCode("PLC2", "Light");
                if (Read==0){
                    //通知plc报警
                    deviceValueService.writeValueByCode("PLC2", "Light",(short)1);
                }
            }
        }
        if (report == null || report.isEmpty()){
            Short Read =(Short) deviceValueService.readValueByCode("PLC2", "Light");
            if (Read==1){
                //通知plc报警
                deviceValueService.writeValueByCode("PLC2", "Light",(short)0);
            }
        }

    }



    //给海康下达任务
    public Boolean sendTask(JobInfo jobInfo){
        if(isTest.equals("true")){
            return true;
        }
        String taskCode = jobInfo.getId().toString();
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        AgvResult agvResult = null;
        if (taskInfo.getRemark2() == null){
            agvResult = sendTaskToRcs(taskCode, "NA001", "", jobInfo.getFromCellCode(), jobInfo.getToCellCode());
        }else {
            agvResult = sendTaskToRcs(taskCode, "NA", "", jobInfo.getFromCellCode(), jobInfo.getToCellCode());
        }

        if (agvResult == null) {
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务失败【agvResult == null】");

            return false;
        } else if (!agvResult.getCode().equals("0")) {//返回的编码不为0就是失败了
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务失败【"+agvResult.getMessage()+"】");

            return false;
        } else {
            jobInfoService.updateMemo(jobInfo,"向RCS发送任务成功");
            return true;
        }
    }

    public Boolean isSuccess(JobInfo jobInfo) {
        String taskCode = jobInfo.getId().toString();
        DeviceTaskResult deviceTaskResult = deviceTaskResultService.getFirstState0ByTaskCode(taskCode);
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

            //到达某个点
            case "end" :{
                //是否满足继续执行任务的条件
                Boolean canContinue = continueTask(jobInfo,deviceTaskResult);
                if(canContinue){
                    break;
                }else {
                    deviceTaskResult.setState(0);
                    deviceTaskResultService.update(deviceTaskResult);
                    break;
                }

            }

            //任务彻底结束
            case "Aend" :{
                jobInfo.setMemo("任务结束");
                jobInfoService.update(jobInfo);
                billRecordService.createTaskRecord(jobInfo.getTaskId(),"RCS任务结束");
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
        return deviceValueService.readValueByCode("PLC2", valueCode);
    }

    @Autowired
    private AutoService autoService;

    @Autowired
    private PositionInfoService positionInfoService;


    @Autowired
    private RedisCache redisCache;


    @Autowired
    private TaskInfoHistoryService taskInfoHistoryService;


    private Map<Long,Integer> waitMap = new ConcurrentHashMap<>();

    public boolean processId(Long id) {
        // 如果是第一次处理这个ID，初始化计数为1
        Integer count = waitMap.putIfAbsent(id, 1);

        // 如果不是第一次处理，获取当前计数并加1
        if (count != null) {
            try {
                Thread.sleep(200);
                count++;
                waitMap.put(id, count);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //第一次处理
            if (count == 1){
                System.out.println("第一次处理");
                return true;
            }

            // 检查是否达到10次
            if (count == 10) {
                System.out.println("ID " + id + " 已达到最大处理次数: " + count);
                // 重新循环
                waitMap.put(id, 2);
                return true;
            }
        }

        return false;
    }

    // 删除
    public void removeMap(Long id) {
        waitMap.remove(id);
    }



    private void add(String cellCode){
        if (!WrongPlacement.contains(cellCode)){
            WrongPlacement.add(cellCode);
        }
    }

    private void del(){
        WrongPlacement.clear();
    }

    public static void main(String[] args) {
        if ("ERCA00066AA".equals("ERCA00066AA")){
            System.out.println("11");
        }
    }

    private void saveSaoMa(String classTime){
        //新建当天的3个类型
        SaoMaSuccess saoMaSuccess1 = new SaoMaSuccess();
        SaoMaSuccess saoMaSuccess2 = new SaoMaSuccess();
        SaoMaSuccess saoMaSuccess3 = new SaoMaSuccess();
        saoMaSuccess1.setClassTime(classTime);
        saoMaSuccess1.setClassNumber(0);
        saoMaSuccess1.setType(1);
        saoMaSuccess1.setCreateTime(DateUtil.getNowDateTimeString());
        saoMaSuccess2.setClassTime(classTime);
        saoMaSuccess2.setClassNumber(0);
        saoMaSuccess2.setType(2);
        saoMaSuccess2.setCreateTime(DateUtil.getNowDateTimeString());
        saoMaSuccess3.setClassTime(classTime);
        saoMaSuccess3.setClassNumber(0);
        saoMaSuccess3.setTaskNumber(0);
        saoMaSuccess3.setType(3);
        saoMaSuccess3.setCreateTime(DateUtil.getNowDateTimeString());
        saoMaSuccessService.save(saoMaSuccess1);
        saoMaSuccessService.save(saoMaSuccess2);
        saoMaSuccessService.save(saoMaSuccess3);
    }


    private void saveTaskFail(JobInfo jobInfo,String classTime,String beginTime,String endTime,String nowDateString,Boolean endTask){
        log.info(jobInfo.getId()+"扫码失败,开始记录PLC扫码失败的类型数量");
        LocalTime now = LocalTime.now();
        int currentHour = now.getHour(); // 获取当前小时（0-23）
        //找到总扫码次数
        SaoMaSuccess saoMaSuccess2 = saoMaSuccessService.findByTypeAndTime(classTime, 2, beginTime, endTime,currentHour);
        SaoMaSuccess saoMaSuccess1 = saoMaSuccessService.findByTypeAndTime(classTime, 1, beginTime, endTime,currentHour);
        Integer number1 = 0;
        Integer number2 = 0;
        if (saoMaSuccess1 !=null){
            number1 = saoMaSuccess1.getClassNumber();
        }
        if (saoMaSuccess2 != null){
            number2 = saoMaSuccess2.getClassNumber();
        }
        //扫码失败记录数量和失败率
        SaoMaSuccess saoMaSuccess3 = saoMaSuccessService.findByTypeAndTime(classTime,3, beginTime,endTime,currentHour);
        if (saoMaSuccess3 == null){
            saoMaSuccess3 = new SaoMaSuccess();
            saoMaSuccess3.setType(3);
            saoMaSuccess3.setClassNumber(1);
            saoMaSuccess3.setClassTime(classTime);
            saoMaSuccess3.setTaskNumber(0);
            double percentage = ((double)1 / (number1+number2+1)) * 100;
            if (percentage >100){
                percentage = 100;
            }
            // 格式化保留两位小数
            String result = String.format("%.2f%%", percentage);
            //失败率和这个班次的总任务数对比
            saoMaSuccess3.setClassSuccess(result);
            saoMaSuccess3.setCreateTime(DateUtil.getNowDateTimeString());
            saoMaSuccessService.save(saoMaSuccess3);
            log.info(jobInfo.getId()+"当天第一次扫码失败,开始记录PLC扫码失败的类型数量："+1+",失败率："+result+",总任务数量"+(number1+number2+1));
        }else {
            //以及有这个数据了
            Integer classNumber = saoMaSuccess3.getClassNumber();
            classNumber=classNumber+1;
            double percentage = ((double)classNumber / (number1+number2+classNumber)) * 100;
            if (percentage >100){
                percentage = 100;
            }
            // 格式化保留两位小数
            String result = String.format("%.2f%%", percentage);
            saoMaSuccess3.setClassNumber(classNumber);
            saoMaSuccess3.setClassSuccess(result);
            if (endTask){
                int taskNumber =0;
                //计算失败的任务数
                if (saoMaSuccess3.getTaskNumber() == null){
                    taskNumber = 1;
                }else {
                    taskNumber=saoMaSuccess3.getTaskNumber()+1;
                }
                Integer byTimeAllTask = taskInfoHistoryService.findByTimeAllTask(beginTime, endTime,classTime,currentHour);
                Integer taskAll = byTimeAllTask+1;
                double percentage2 = ((double) taskNumber / taskAll) * 100;
                if (percentage2 >100){
                    percentage2 = 100;
                }
                // 格式化保留两位小数
                String result2 = String.format("%.2f%%", percentage2);
                saoMaSuccess3.setTaskNumber(taskNumber);
                saoMaSuccess3.setTaskSuccess(result2);
                log.info(jobInfo.getId()+"最后一次扫码，开始记录扫码失败的任务数量："+taskNumber+",总任务数："+taskAll+",比例是："+result2);
            }
            saoMaSuccessService.update(saoMaSuccess3);
            log.info(jobInfo.getId()+"扫码失败,开始记录PLC扫码失败的类型数量："+classNumber+",失败率："+result+",总扫码数量"+(number1+number2+classNumber)+",classTime="+classTime+",beginTime="+beginTime+",currentHour="+currentHour);
        }
    }

    //记录plc传递的码值不对
    private void saveTaskCompareFail(JobInfo jobInfo,String classTime,String beginTime,String endTime,String nowDateString){
        log.info(jobInfo.getId()+"PLC传递的码值不对,开始记录比对失败的类型数量");
        LocalTime now = LocalTime.now();
        int currentHour = now.getHour(); // 获取当前小时（0-23）
        //扫码对比记录数量
        SaoMaSuccess saoMaSuccess2 = saoMaSuccessService.findByTypeAndTime(classTime,2, beginTime,endTime,currentHour);
        if (saoMaSuccess2 == null){
            saoMaSuccess2 = new SaoMaSuccess();
            saoMaSuccess2.setType(3);
            saoMaSuccess2.setClassNumber(1);
            saoMaSuccess2.setClassTime(classTime);
            saoMaSuccess2.setCreateTime(DateUtil.getNowDateTimeString());
            saoMaSuccessService.save(saoMaSuccess2);
            log.info(jobInfo.getId()+"PLC传递的码值不对,开始记录比对失败的类型数量："+1);
        }else {
            //有这个数据了
            Integer classNumber = saoMaSuccess2.getClassNumber();
            classNumber=classNumber+1;
            saoMaSuccess2.setClassNumber(classNumber);
            saoMaSuccessService.update(saoMaSuccess2);
            log.info(jobInfo.getId()+"PLC传递的码值不对,开始记录比对失败的类型数量："+classNumber+",classTime="+classTime+",beginTime="+beginTime+",currentHour="+currentHour);
        }
    }


    //记录plc传递的码值正确
    private void saveTaskCompareSuccess(JobInfo jobInfo,String classTime,String beginTime,String endTime,String nowDateString){
        log.info(jobInfo.getId()+"PLC传递的码正确,开始记录比对成功的类型数量");
        LocalTime now = LocalTime.now();
        int currentHour = now.getHour(); // 获取当前小时（0-23）
        //扫码失败记录数量和失败率
        SaoMaSuccess saoMaSuccess1 = saoMaSuccessService.findByTypeAndTime(classTime,1, beginTime,endTime,currentHour);
        if (saoMaSuccess1 == null){
            saoMaSuccess1 = new SaoMaSuccess();
            saoMaSuccess1.setType(1);
            saoMaSuccess1.setClassNumber(1);
            saoMaSuccess1.setClassTime(classTime);
            saoMaSuccess1.setCreateTime(DateUtil.getNowDateTimeString());
            saoMaSuccessService.save(saoMaSuccess1);
            log.info(jobInfo.getId()+"PLC传递的码值正确,开始记录比对成功的类型数量："+1);
        }else {
            //以及有这个数据了
            Integer classNumber = saoMaSuccess1.getClassNumber();
            classNumber=classNumber+1;
            saoMaSuccess1.setClassNumber(classNumber);
            saoMaSuccessService.update(saoMaSuccess1);
            log.info(jobInfo.getId()+"PLC传递的码值正确,开始记录比对成功的类型数量："+classNumber+",classTime="+classTime+",beginTime="+beginTime+",currentHour="+currentHour);
        }
    }



    @Autowired
    private SaoMaSuccessService saoMaSuccessService;

    private Boolean continueTask(JobInfo jobInfo,DeviceTaskResult deviceTaskResult){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String node  =deviceTaskResult.getNode();
        String taskCode = jobInfo.getId().toString();
        //非前置
        if(node.equals(jobInfo.getFromCellCode()) || node.equals(jobInfo.getToCellCode())){
            return true;
        }

        String continueType=null;

        //风淋门外扫码处
        if(node.equals("SM001")){
            //进入开始扫码的动作,查看当前时间是多少？是否有扫码失败的这些数据，没有的话新建
            String nowDateString = DateUtil.getNowDateString();
            String beginTime=null;
            String endTime = null;
            LocalTime now = LocalTime.now();
            int hour = now.getHour();
            //班次
            String classTime = null;
            if (hour >=8 && hour<20){
                classTime = "1";
                //白班
                beginTime = "08:00:00";
                endTime = "20:00:00";
            }else {
                classTime="2";
                //夜班
                beginTime = "20:00:00";
                endTime = "08:00:00";
            }

            // 获取当前小时（0-23）
            SaoMaSuccess saoMaSuccess = saoMaSuccessService.findByTypeAndTime(classTime,null, beginTime,endTime,hour);
            if (saoMaSuccess == null){
                //新建当天的三个类型
                saveSaoMa(classTime);
                log.info(jobInfo.getId()+"当前班次新建3个类型的扫码记录："+",classTime="+classTime+",beginTime="+beginTime+",currentHour="+hour);
            }else {
                log.info(jobInfo.getId()+"当前班次已经3个类型的扫码记录："+",classTime="+classTime+",beginTime="+beginTime+",currentHour="+hour+",扫码记录id："+saoMaSuccess.getId());
            }


            //通知PLC AGV到达扫码处
            deviceValueService.writeValueByCode("PLC2", "PLC02-TO",(short)1);

            String key ="NA-YANG-SM-XOUNT:"+taskCode;
            if(redisCache.getCacheObject(key)==null){
                redisCache.setCacheObject(key,0,1, TimeUnit.HOURS);
            }
            Integer count  = redisCache.getCacheObject(key);

            String  palletCode=null;

            if (!processId(jobInfo.getId())) {
                //不是第一次扫码 做延时操作
                System.out.println("延时扫码");
                return false;
            }
            //断开旋转信号
            deviceValueService.writeValueByCode("PLC2", "PLC02-Revolve",(short)0);
            //扫码成功
            Short  scanSuccess =(Short) deviceValueService.readValueByCode("PLC2", "PLC02-Success");
            //扫描失败
            Short  scanFail =(Short) deviceValueService.readValueByCode("PLC2", "PLC02-Fail");

            //等待
            if (scanSuccess ==0 && scanFail == 0){
                log.info("等待plc扫码结果");
                return false;
            }

            count++;
            redisCache.setCacheObject(key,count,1, TimeUnit.HOURS);

            //记录当前任务是扫码的
            taskInfo.setRemark1("1");
            taskInfoService.update(taskInfo);

            if(scanSuccess==1){
                palletCode =(String) deviceValueService.readValueByCode("PLC2", "PLC02-SAO1");
                if(palletCode == null || palletCode.trim().length() < 2){
                    palletCode=(String) deviceValueService.readValueByCode("PLC2", "PLC02-SAO2");
                }

                if(palletCode == null || palletCode.trim().length() < 2){
                    palletCode=null;
                }
                //deviceValueService.writeValueByCode("PLC2", "PLC02-TO",(short)0);
                //log.info("plc告诉扫码成功，清除到位信号");
            }



            if(scanFail==1 && count<=3){
                deviceValueService.writeValueByCode("PLC2", "PLC02-Revolve",(short)1);
                //deviceValueService.writeValueByCode("PLC2", "PLC02-TO",(short)0);
                continueType = "2";    // null  继续执行  1-回原点  2-旋转
                jobInfoService.updateMemo(jobInfo,"扫码失败，旋转次数"+count+"");
                //记录PLC扫码失败的次数和扫码总次数
                saveTaskFail(jobInfo,classTime,beginTime,endTime,nowDateString,false);
                AgvResult agvResult= continueTask(autoService.getReqCode(),taskCode,continueType,null);
                return true;
            }

           if(palletCode==null && count<3){
                deviceValueService.writeValueByCode("PLC2", "PLC02-Revolve",(short)1);
                deviceValueService.writeValueByCode("PLC2", "PLC02-TO",(short)0);
                continueType = "2";    // null  继续执行  1-回原点  2-旋转
                jobInfoService.updateMemo(jobInfo,"未扫到码，旋转次数"+count+"清除到位信号");
               //记录PLC扫码失败的数量和失败率
               saveTaskFail(jobInfo,classTime,beginTime,endTime,nowDateString,false);
               AgvResult agvResult= continueTask(autoService.getReqCode(),taskCode,continueType,null);
               return true;

           }
           if(palletCode==null && count>=3){
                deviceValueService.writeValueByCode("PLC2", "PLC02-Revolve",(short)0);
                deviceValueService.writeValueByCode("PLC2", "PLC02-TO",(short)0);
                continueType = "1";
                jobInfoService.updateMemo(jobInfo,"未扫到码"+ "回原点"+",清除到位信号");
                //记录PLC扫码失败的数量和失败率
                saveTaskFail(jobInfo,classTime,beginTime,endTime,nowDateString,true);
                AgvResult agvResult= continueTask(autoService.getReqCode(),taskCode,continueType,null);
                //删除
                removeMap(jobInfo.getId());
                return true;
           }
           if(palletCode!=null){
                ProPositionContent content = proPositionContentService.findByCode(taskInfo.getWareCode(),jobInfo.getToCellCode());
               //解析托盘码  和料号是否能对的上
                String aaa[]=palletCode.split(";");
                int i = 0;
                if (aaa.length <=1){
                    //解析的是条形码
                    aaa =palletCode.split(" ");
                    i = 1;
                }
                if(aaa.length>=1 && (!content.getItemCode().equals(aaa[i].trim()))){
                    deviceValueService.writeValueByCode("PLC2", "PLC02-Revolve",(short)0);
                    deviceValueService.writeValueByCode("PLC2", "PLC02-TO",(short)0);
                    continueType = "1";
                    //匹配失败，优先指定空闲的对应的站台
                    ProPositionContent koStation = findKoStation(aaa[i]);
                    if (koStation == null){
                        //没找到，返回原点
                        jobInfoService.updateMemo(jobInfo,"plc托盘号"+aaa[i]+"匹配物料失败"+content.getItemCode()+",次数"+count+" 回原点,清除到位信号");
                        continueTask(autoService.getReqCode(),taskCode,continueType,null);
                        //新增报警站台,回到了起点
                        add(jobInfo.getFromCellCode());
                    }else {
                        //找到了
                        String code = koStation.getCode();
                        jobInfoService.updateMemo(jobInfo,"plc托盘号"+aaa[i]+"匹配物料失败"+content.getItemCode()+",次数"+count+" 回指定点位:"+code+",清除到位信号");
                        continueTask(autoService.getReqCode(),taskCode,continueType,code);
                    }

                    //记录扫码比对失败次数
                    saveTaskCompareFail(jobInfo,classTime,beginTime,endTime,nowDateString);
                    //报警
                    Short Read =(Short) deviceValueService.readValueByCode("PLC2", "Light");
                    if (Read==1){
                        //通知plc报警
                        deviceValueService.writeValueByCode("PLC2", "Light",(short)0);
                    }

                    return true;
                }
                if(aaa.length>=1 && (content.getItemCode().equals(aaa[i].trim()))){
                    deviceValueService.writeValueByCode("PLC2", "PLC02-Revolve",(short)0);
                    deviceValueService.writeValueByCode("PLC2", "PLC02-TO",(short)0);
                    taskInfo.setPalletCode(palletCode);
                    taskInfoService.update(taskInfo);
                    jobInfoService.updateMemo(jobInfo,"托盘号"+palletCode+"匹配物料成功"+content.getItemCode()+count+" 继续执行,清除到位信号");
                    saveTaskCompareSuccess(jobInfo,classTime,beginTime,endTime,nowDateString);
                    AgvResult agvResult= continueTask(autoService.getReqCode(),taskCode,null,null);
                    //删除
                    removeMap(jobInfo.getId());
                    return true;
                }
                //删除
                removeMap(jobInfo.getId());
           }



           // AgvResult agvResult= continueTask(autoService.getReqCode(),taskCode,continueType);
            return false;

        }else {
            try {
                deviceValueService.writeValueByCode("PLC2", "PLC02-TO",(short)0);
                deviceValueService.writeValueByCode("PLC2", "PLC02-Revolve",(short)0);
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
        ProPositionContent content = proPositionContentService.findBy("code2",node);
        PositionInfo positionInfo = positionInfoService.findBy("code",content.getCode());
        // types:[
        //        {value:0,name:"工位",color:"#67C23A"}
        //        ,  {value:1,name:"缓存区",color:"#409EFF"}
        //        ,  {value:2,name:"余料缓存区",color:"#E6A23C"}
        //      ]
        Boolean hasPallet=null;
        //工位和余料是用的wifi模块
        if(content.getType()==0 || content.getType()==2){
            hasPallet= WifiIoUtil.read(content.getWifiModeIp(),content.getWifiModeOffset(),content.getCode());
            if(hasPallet==null){
                jobInfoService.updateMemo(jobInfo,content.getCode()+" 位置光电模块断联");
                return false;
            }
            hasPallet=!hasPallet;
        }
        //用的PLC
        if(content.getType()==1 ){
            try {
                Short has =(Short) deviceValueService.readValueByCode("PLC2", content.getDevceGuangdianAddress());
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

        //起点有货
        if(code.equals(jobInfo.getFromCellCode()) ){
            if(hasPallet){
                AgvResult agvResult= continueTask(autoService.getReqCode(),taskCode,null,null);

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
               AgvResult agvResult= continueTask(autoService.getReqCode(),taskCode,null,null);

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

    //根据托盘编号去站台查找空闲的
    private ProPositionContent findKoStation(String code) {
        ProPositionContent positionContent = proPositionContentService.findByType(code, 1, 0L, null);
        if (positionContent == null){
            return null;
        }
        return positionContent;
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
    public AgvResult sendTaskToRcs(String taskCode,String type,String podCode,String start,String end){
        ProPositionContent from = proPositionContentService.findBy("code",start);
        ProPositionContent to = proPositionContentService.findBy("code",end);
        if(from.getCode2()==null || from.getCode2().trim().equals("")){
            AgvResult agvResult = new AgvResult();
            agvResult.setCode("2");
            agvResult.setMessage(start+" 未设置前置编码");
            return agvResult;
        }
        if(to.getCode2()==null || to.getCode2().trim().equals("")){
            AgvResult agvResult = new AgvResult();
            agvResult.setCode("2");
            agvResult.setMessage(end+" 未设置前置编码");
            return agvResult;
        }
        String json = null;
        //都是站台
        if (type.equals("NA")){
            type= "NA";
            json = "{\n" +
                    "    \"reqCode\": \""+taskCode+"\",\n" +
                    //  "    \"clientCode\": \"\",\n" +
                    "    \"taskTyp\": \""+type+"\",\n" +
                    "    \"taskCode\": \""+taskCode+"\",\n" +
                    // "    \"podCode\": "+podCode+",\n" +
                    "    \"positionCodePath\": [\n" +
                "        {\n" +
                "            \"positionCode\": \""+ from.getCode2() +"\",\n" +
                "            \"type\": \"00\"\n" +
                "        },\n" +
                    "        {\n" +
                    "            \"positionCode\": \""+  start+"\",\n" +
                    "            \"type\": \"00\"\n" +
                    "        },\n" +
                   /* "        {\n" +
                    "            \"positionCode\": \""+  "SM001"+"\",\n" +
                    "            \"type\": \"00\"\n" +
                    "        },\n" +*/
                    "        {\n" +
                    "            \"positionCode\": \""+ to.getCode2() +"\",\n" +
                    "            \"type\": \"00\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"positionCode\": \""+end+"\",\n" +
                    "            \"type\": \"00\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"positionCode\": \""+to.getCode2()+"\",\n" +
                    "            \"type\": \"00\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
        }else {
             json = "{\n" +
                    "    \"reqCode\": \""+taskCode+"\",\n" +
                    //  "    \"clientCode\": \"\",\n" +
                    "    \"taskTyp\": \""+type+"\",\n" +
                    "    \"taskCode\": \""+taskCode+"\",\n" +
                    // "    \"podCode\": "+podCode+",\n" +
                    "    \"positionCodePath\": [\n" +
               /* "        {\n" +
                "            \"positionCode\": \""+ from.getCode2() +"\",\n" +
                "            \"type\": \"00\"\n" +
                "        },\n" +*/
                    "        {\n" +
                    "            \"positionCode\": \""+  start+"\",\n" +
                    "            \"type\": \"00\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"positionCode\": \""+  "SM001"+"\",\n" +
                    "            \"type\": \"00\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"positionCode\": \""+ to.getCode2() +"\",\n" +
                    "            \"type\": \"00\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"positionCode\": \""+end+"\",\n" +
                    "            \"type\": \"00\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"positionCode\": \""+to.getCode2()+"\",\n" +
                    "            \"type\": \"00\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
        }

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


    public AgvResult continueTask(String reqCode,String taskCode,String continueType,String returnTarget){
        String json;
        if(continueType==null){
              json = "{\n" +
                    "\"reqCode\": \""+reqCode+"\",\n" +
                    "\"taskCode\": \""+taskCode+"\",\n" +
                    "}";
        }else {
            if (returnTarget == null){
                json = "{\n" +
                        "\"reqCode\": \""+reqCode+"\",\n" +
                        "\"taskCode\": \""+taskCode+"\",\n" +
                        "\"continueType\": \""+continueType+"\",\n" +
                        "}";
            }else {
                json = "{\n" +
                        "\"reqCode\": \""+reqCode+"\",\n" +
                        "\"taskCode\": \""+taskCode+"\",\n" +
                        "\"continueType\": \""+continueType+"\",\n" +
                        "\"returnTarget\": \""+returnTarget+"\",\n" +
                        "}";
            }

        }

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
            //code是agv返回的信息 只有0代表成功，只要不是0都是失败了
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
        log.error(url,response.getStatusCode());
        return null;
    }

}
