package com.deer.wcs.task.task;

import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.model.ProPositionContent;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.base.service.ProPositionContentService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.handle.hik.Hik2000V33JobHandle;
import com.deer.wcs.task.model.AgvZone;
import com.deer.wcs.task.model.AgvZoneRecord;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.AgvZoneRecordService;
import com.deer.wcs.task.service.AgvZoneService;
import com.deer.wcs.task.service.TaskInfoService;
import com.deer.wcs.task.utils.WifiIoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;


@Component("LgNaYangJiTask")
public class LgNaYangJiTask {
    @Autowired
    private ProPositionContentService proPositionContentService;

    @Autowired
    private PositionInfoService positionInfoService;

    @Autowired
    private DeviceValueService deviceValueService;


    @Value("${isTest}")
    private String isTest;

    private String wareCode = "LG-NA-YANG";


    private static final Logger log = LoggerFactory.getLogger(LgNaYangJiTask.class);

    //查询接驳位的状态
    private Object readPlc(String valueCode) {
        return deviceValueService.readValueByCode("PLC2", valueCode);
    }

    //@Scheduled(cron = "0/5 * * * * ?")
    public void taskState( ){
        List<PositionInfo> list = positionInfoService.findAll();
        for (PositionInfo positionInfo : list) {
            if(!hasTask(positionInfo.getCode(),positionInfo.getWareCode())){
                if(positionInfo.getTaskState()>0.1){
                    positionInfo.setTaskState(0L);
                    positionInfoService.update(positionInfo);
                }
            }
        }
    }

    /**
     * 室外PLC检测
     */
    //@Scheduled(cron = "0/1 * * * * ?")
    public void autoRun1() {

        Condition condition = new Condition(ProPositionContent.class);
        condition.createCriteria().andEqualTo("type",1)
                .andEqualTo("wareCode",wareCode);
        List<ProPositionContent> list=proPositionContentService.findByCondition(condition);

        for(ProPositionContent content:list){
            content=proPositionContentService.findById(content.getId());


            String hasPalletAddress=content.getDevceGuangdianAddress();
          //  String qrCodeAddress = content.getDeviceScanCodeAddress();
            try {
                Short hasPallet = null;
                String palletCode = null;
                if(isTest.equals("true")){
                      hasPallet =1;
                      palletCode = content.getItemCode()+";"+content.getItemCode();
                }else {
                      hasPallet = (Short) readPlc(hasPalletAddress);
                    //  palletCode = (String) readPlc(qrCodeAddress);
                }

                content=proPositionContentService.findById(content.getId());
                if(palletCode==null){
                    palletCode="";
                }
                content.setPalletCode(palletCode);
                content.setWifiModeIsConnect(1);
                if(hasPallet==0){
                    content.setMemo("无货");
                    content.setIsOk(0);
                }

                if(hasPallet==1)
                {
                    if(content.getIsOk()==0){
                        content.setInTime(DateUtil.getNowDateTimeString());
                    }
                    content.setIsOk(1);
                    content.setMemo("OK");
                }

                proPositionContentService.update(content);

                PositionInfo positionInfo = positionInfoService.findById(content.getPositionId());
                positionInfo.setInvenState(Long.valueOf(hasPallet));
                positionInfoService.update(positionInfo);

            }catch(Exception ex){
                content.setWifiModeIsConnect(0);
                proPositionContentService.update(content);
                ex.printStackTrace();

            }
        }
    }

    private void checkType2(){
        //检测余料缓存站台
        Condition condition2 = new Condition(ProPositionContent.class);
        condition2.createCriteria().andEqualTo("type",2)
                .andEqualTo("wareCode",wareCode);
        List<ProPositionContent> list2=proPositionContentService.findByCondition(condition2);
        for (ProPositionContent content:list2){
            try {
                content=proPositionContentService.findById(content.getId());
                PositionInfo to = positionInfoService.findById(content.getPositionId());
                if(to.getDisableState()>0.9){
                    continue;
                }

                if(isTest.equals("true")){
                    continue;
                }
                String ip=content.getWifiModeIp();
                Boolean hasPallet = WifiIoUtil.read(ip,content.getWifiModeOffset(),content.getCode());
                if(hasPallet==null){
                    content.setWifiModeIsConnect(0);
                    proPositionContentService.update(content);
                    continue;
                }else {
                    content.setWifiModeIsConnect(1);
                    proPositionContentService.update(content);
                }
                //光电   取反
                if(hasPallet !=null && (!hasPallet)) {
                    // PositionInfo to = positionInfoService.findById(content.getPositionId());
                    to.setInvenState(1L);
                    positionInfoService.update(to);
                    continue;
                }
                if(hasPallet !=null && (hasPallet)){
                    // PositionInfo to = positionInfoService.findById(content.getPositionId());
                    to.setInvenState(0L);
                    positionInfoService.update(to);
                }

            }catch (Exception ex){
                content.setWifiModeIsConnect(0);
                proPositionContentService.update(content);
                ex.printStackTrace();
            }

        }
    }

    private void checkType0(){

    }

    @Autowired
    private TaskInfoService taskInfoService;
    /**
     * 工位自动生成补料任务
     */
    //@Scheduled(cron = "0/1 * * * * ?")
    public void autoRun0() {


        //检测余料缓存站台的状态
        checkType2();

        //检测工位状态
        Condition condition = new Condition(ProPositionContent.class);
        condition.createCriteria().andEqualTo("type",0)
                .andEqualTo("wareCode",wareCode);
        List<ProPositionContent> list=proPositionContentService.findByCondition(condition);

        for(ProPositionContent content:list){
            content=proPositionContentService.findById(content.getId());
            //System.out.println("1:"+content.getCode());
            PositionInfo positionInfo = positionInfoService.findByCode(wareCode,content.getCode());
            String ip=content.getWifiModeIp();
            //非测试模式才会执行这个
            if(isTest.equals("false")){
                try {
                    Boolean hasPallet=null;
                    if(ip==null || ip.trim().equals("")){
                        content.setWifiModeIsConnect(0);
                        content.setMemo("未设置IP");
                        proPositionContentService.update(content);
                        continue;
                    }
                    hasPallet = WifiIoUtil.read(ip,content.getWifiModeOffset(),content.getCode());
                    //通信异常
                    if(hasPallet==null){
                        content.setWifiModeIsConnect(0);
                        content.setMemo("hasPallet==null");
                        proPositionContentService.update(content);
                        continue;
                    }
                    //有托盘 光电取反
                    else if(!hasPallet) {
                        content.setWifiModeIsConnect(1);
                        proPositionContentService.update(content);
                        positionInfo.setInvenState(1L);
                        positionInfoService.update(positionInfo);
                    }
                    //无托盘
                    else {
                        content.setWifiModeIsConnect(1);
                        proPositionContentService.update(content);
                        positionInfo.setInvenState(0L);
                        positionInfoService.update(positionInfo);
                    }

                }catch (Exception ex){
                    content.setMemo(ex.getMessage());
                    content.setWifiModeIsConnect(0);
                    proPositionContentService.update(content);
                    ex.printStackTrace();

                    continue;
                }
            }

            //工位有托盘  检测是否已经料号，如果换料号，则把托盘发往余料缓存位
            if(content.getWifiModeIsConnect()>0.9  && positionInfo.getInvenState()>0.9) {
                if(positionInfo.getDisableState()>0.1){
                    content.setMemo("positionInfo.getDisableState()>0.1");
                    proPositionContentService.update(content);
                    continue;
                }
                create0To2(content);
            }
            //工位无托盘  从室外缓存位发到工位
            if(content.getWifiModeIsConnect()>0.9  && positionInfo.getInvenState()<0.1){
                if(positionInfo.getDisableState()>0.1){
                    content.setMemo("positionInfo.getDisableState()>0.1");
                    proPositionContentService.update(content);
                    continue;
                }
                //System.out.println("2:"+content.getCode());
                //补料
                create1To0(content);
            }
        }

    }

    /**
     * 缓存位搬运到工位
     * @param content
     */
    private void create1To0(ProPositionContent content){
        try {
            PositionInfo positionInfo = positionInfoService.findById(content.getPositionId());

            if(hasTask(positionInfo.getCode(),positionInfo.getWareCode())){
                content.setMemo("hasTask(positionInfo.getCode())");
                proPositionContentService.update(content);
                return;
            }
            if(positionInfo.getDisableState()>0.9){
                content.setMemo("positionInfo.getDisableState()>0.9");
                proPositionContentService.update(content);
                return;
            }

            if(positionInfo.getInvenState()>0.9){
                content.setMemo("positionInfo.getInvenState()>0.9");
                proPositionContentService.update(content);
                return;
            }
            if(positionInfo.getTaskState()>0.9){
                content.setMemo("positionInfo.getTaskState()>0.9");
                proPositionContentService.update(content);
                return;
            }

            //获取一个缓存区的位置  type==1  室外缓存位
            PositionInfo from =getType1(content);
            if(from==null){
                content.setMemo("from==null");
                proPositionContentService.update(content);
                return;
            }
            ProPositionContent fromContent = proPositionContentService.findByCode(wareCode,from.getCode());

            if(from.getDisableState().equals(1L)){
                content.setMemo("from.getDisableState().equals(1L)");
                proPositionContentService.update(content);
                return;
            }
            if(from.getTaskState()>0.1){
                content.setMemo("from.getTaskState()>0.1");
                proPositionContentService.update(content);
                return;
            }
            if(positionInfo.getTaskState()>0.1){
                content.setMemo("positionInfo.getTaskState()>0.1");
                proPositionContentService.update(content);
                return;
            }

            if(hasTask(from.getCode(),from.getWareCode())){
                content.setMemo("hasTask(from.getCode())");
                proPositionContentService.update(content);
                return;
            }
            if(hasTask(positionInfo.getCode(),positionInfo.getWareCode())){
                content.setMemo("hasTask(positionInfo.getCode())");
                proPositionContentService.update(content);
                return;
            }
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setWareCode(wareCode);
            taskInfo.setWareName(wareCode);
            taskInfo.setFromCellCode(from.getCode());
            taskInfo.setToCellCode(positionInfo.getCode());
            taskInfo.setType("9");
            taskInfo.setPalletCode(fromContent.getPalletCode());
            taskInfoService.save(taskInfo);

        }catch (Exception ex){
            content.setWifiModeIsConnect(0);
            content.setMemo(ex.getMessage());
            proPositionContentService.update(content);
            ex.printStackTrace();
        }

    }


    /**
     * 工位搬运到余料缓存位
     * @param content
     */
    private void create0To2(ProPositionContent content){
        try {
            PositionInfo from = positionInfoService.findById(content.getPositionId());
            if(from.getDisableState()>0.9){
                return;

            }

            if(from.getInvenState()<0.9){
                return;
            }
            if(from.getTaskState()>0.9){
                return;
            }

            String palletCode =content.getPalletCode();
            if(palletCode!=null && !palletCode.trim().equals("")) {
                String aaa[] = palletCode.split(";");

                if (aaa.length == 2 && (content.getItemCode().equals(aaa[0]))) {
                    return;
                }
                //物料不匹配，需要到余料缓存，或者室外缓存
                if (aaa.length == 2 && (!content.getItemCode().equals(aaa[0]))) {
                    PositionInfo to = getType2(content);
                    if (to == null) {
                        return;
                    }
                   // ProPositionContent toContent = proPositionContentService.findBy("code", to.getCode());

                    if (to.getDisableState()>0.1) {
                        return;
                    }
                    if (to.getTaskState() > 0.1) {
                        return;
                    }
                    if (to.getInvenState() > 0.1) {
                        return;
                    }

                    if (hasTask(from.getCode(),from.getWareCode())) {
                        return;
                    }
                    if (hasTask(to.getCode(),to.getWareCode())) {
                        return;
                    }
                    TaskInfo taskInfo = new TaskInfo();
                    taskInfo.setWareCode("LG-NA");
                    taskInfo.setWareName("LG-NA");
                    taskInfo.setFromCellCode(from.getCode());
                    taskInfo.setToCellCode(to.getCode());
                    taskInfo.setType("8");
                    taskInfo.setPalletCode(content.getPalletCode());
                    taskInfoService.save(taskInfo);
                }
            }


        }catch (Exception ex){
            content.setWifiModeIsConnect(0);
            proPositionContentService.update(content);
            ex.printStackTrace();
        }

    }


    /**
     * 获取余料缓存为空位置
     * @param content
     * @return
     */
    private PositionInfo getType2(ProPositionContent content){
        try{

            Condition condition2 = new Condition(ProPositionContent.class);
            condition2.createCriteria().andEqualTo("type",2)
                    .andEqualTo("wifiModeIsConnect",1)
                    .andEqualTo("wareCode",wareCode);
            List<ProPositionContent> list2 = proPositionContentService.findByCondition(condition2);
            for (ProPositionContent ToContent:list2){
                PositionInfo positionInfo = positionInfoService.findById(ToContent.getPositionId());
                if(positionInfo.getTaskState()<0.1 && positionInfo.getInvenState()<0.9 && positionInfo.getDisableState()<0.9 ){
                    log.info("检测余料缓存位空位置----------------------------------------");
                    log.info("工位："+content.getItemCode());
                    log.info("itemCode："+content.getItemCode());
                    log.info("proLineCode："+content.getProLineCode());
                    log.info("type："+1);
                    log.info("isOk："+1);
                    log.info("positionInfo："+positionInfo.getCode());
                    log.info("----------------------------------------");
                    return positionInfo;
                }
            }


            Condition condition1 = new Condition(ProPositionContent.class);
            condition1.createCriteria().andEqualTo("itemCode",content.getItemCode())
                    .andEqualTo("wifiModeIsConnect",1)
                    .andEqualTo("wareCode",wareCode);
            List<ProPositionContent> list1 = proPositionContentService.findByCondition(condition1);
            for (ProPositionContent ToContent:list1){
                PositionInfo positionInfo = positionInfoService.findById(ToContent.getPositionId());
                if(positionInfo.getTaskState()<0.1 && positionInfo.getInvenState()<0.9 && positionInfo.getDisableState()<0.9 ){
                    log.info("检测余料缓存位空位置----------------------------------------");
                    log.info("工位："+content.getItemCode());
                    log.info("itemCode："+content.getItemCode());
                    log.info("proLineCode："+content.getProLineCode());
                    log.info("type："+1);
                    log.info("isOk："+1);
                    log.info("positionInfo："+positionInfo.getCode());
                    log.info("----------------------------------------");
                    return positionInfo;
                }
            }
            return null;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    //获取缓存位置
    private PositionInfo getType1(ProPositionContent toContent){

        try {
            //优先找备用的
            String backCode = toContent.getBackCode();
            if(backCode!=null && !backCode.trim().equals("")){
                backCode =backCode.trim();
                String[] backCodes = backCode.split(";");
                //log.info("当前站台："+toContent.getCode()+"的备用站台是："+backCode);
                for (int i= 0;i<backCodes.length;i++){
                    ProPositionContent fromContent = proPositionContentService.findByCode(wareCode,backCodes[i]);
                    //log.info("当前备用站台是："+backCodes[i]);
                    if(fromContent!=null && fromContent.getItemCode().equals(toContent.getItemCode())){
                        PositionInfo positionInfo = positionInfoService.findByCode(wareCode,backCodes[i]);
                        if(positionInfo.getInvenState()>0.9 && positionInfo.getTaskState()<0.1 && positionInfo.getDisableState()<0.1){
                            log.info("======================================");
                            log.info("叫料点位为："+toContent.getCode());
                            log.info("backCode："+toContent.getBackCode());
                            log.info("fromContent："+fromContent.getCode());
                            log.info("positionInfo："+positionInfo.getCode());
                            log.info("======================================");
                            return positionInfo;
                        }
                    }
                }

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }


        String itemCode = toContent.getItemCode();
        String lineCode = toContent.getProLineCode();

        if(itemCode==null || itemCode.trim().isEmpty()){
            return null;
        }
        if(lineCode==null || lineCode.trim().isEmpty()){
            return null;
        }

//        //区分站台的
//        Condition condition1= new Condition(ProPositionContent.class);
//        condition1.createCriteria().andEqualTo("itemCode",itemCode)
//                .andEqualTo("proLineCode",lineCode)
//                .andEqualTo("type",1)
//                .andEqualTo("isOk",1);
//
//        //不区分站台的
//        Condition condition11= new Condition(ProPositionContent.class);
//        condition11.createCriteria().andEqualTo("itemCode",itemCode)
//                .andEqualTo("proLineCode","000")
//                .andEqualTo("type",1)
//                .andEqualTo("isOk",1);



        ProPositionContent content = proPositionContentService.selectType1(itemCode,lineCode);

        if(content!=null){
            PositionInfo positionInfo = positionInfoService.findById(content.getPositionId());
            return positionInfo;
        }
        return null;
//        for (ProPositionContent content:list1){
//            PositionInfo positionInfo = positionInfoService.findById(content.getPositionId());
//            if(positionInfo.getTaskState()<0.9 && positionInfo.getInvenState()>0.9 && positionInfo.getDisableState()<0.9 ){
//                log.info("----------------------------------------");
//                log.info("itemCode："+toContent.getItemCode());
//                log.info("proLineCode："+toContent.getProLineCode());
//                log.info("type："+1);
//                log.info("isOk："+1);
//                log.info("positionInfo："+positionInfo.getCode());
//                log.info("----------------------------------------");
//                return positionInfo;
//            }
//        }
//        return null;
    }

    @Autowired
    private AgvZoneService agvZoneService;

   private  void agvZone(String code){
       if(isTest.equals("true")){
            return;
       }
       Short hasChaChe = (Short) readPlc("agvZone"+code);
       if(hasChaChe==1){
           AgvZone agvZone = agvZoneService.findBy("code",code);
           if(agvZone==null){
               agvZone=new AgvZone();
           }
           agvZone.setAgvType("叉车");
           agvZone.setAgvCount(999);
           agvZone.setCode(code);
           updateAgvZone(agvZone);
       }else if(hasChaChe==0){
           AgvZone agvZone = agvZoneService.findBy("code",code);
           if(agvZone==null){
               agvZone=new AgvZone();
           }
           agvZone.setAgvType("HIK");
           agvZone.setAgvCount(0);
           agvZone.setCode(code);
           updateAgvZone(agvZone);
       }
       try {
           AgvZoneRecord agvZoneRecord  =agvZoneRecordService.findFirstState0();
           if(agvZoneRecord==null){
               return;
           }
           agvZoneRecord.setState(1);
           AgvZone agvZone = agvZoneService.findBy("code",code);
           //海康申请进入
           if(agvZoneRecord.getEventType()==1){

               if(agvZone==null || agvZone.getAgvType().equals(agvZoneRecord.getAgvType())){
                   agvZoneRecord.setMemo("允许进入，已经通知海康RCS");
                   if(agvZone.getAgvCount()==null || agvZone.getAgvCount()<0){
                       agvZone.setAgvCount(0);
                   }
                   agvZone.setAgvCount(agvZone.getAgvCount()+1);
                   hik2000V33JobHandle.notifyExcuteResultInfo(code,agvZoneRecord.getUuid(),"1");
                   agvZoneRecordService.update(agvZoneRecord);
               }else {
                   //如果是拒绝，则需要一直检测这个记录，直到可以通过
                   agvZoneRecord.setState(0);
                   if(agvZoneRecord.getMemo()==null || agvZoneRecord.getMemo().equals("")){
                       hik2000V33JobHandle.notifyExcuteResultInfo(code,agvZoneRecord.getUuid(),"0");
                       agvZoneRecord.setMemo("不允许进入,已经通知海康RCS");
                       agvZoneRecordService.update(agvZoneRecord);
                   }

               }
           }else {
               agvZone.setAgvCount(agvZone.getAgvCount()-1);
               if(agvZone.getAgvCount()<0){
                   agvZone.setAgvCount(0);
               }
               agvZoneRecord.setMemo("AGV已经离开交管区，数量减一");
               agvZoneRecordService.update(agvZoneRecord);
           }
           agvZoneService.update(agvZone);

       }catch (Exception ex){
           ex.printStackTrace();
       }
   }


    //@Scheduled(cron = "0/1 * * * * ?")
    public void chachejiaoguan(){
        agvZone("333");
        agvZone("444");

    }

    @Autowired
    private AgvZoneRecordService agvZoneRecordService;
    @Autowired
    private Hik2000V33JobHandle hik2000V33JobHandle;

    private void updateAgvZone(AgvZone agvZone){
        agvZone.setUpdateTime(DateUtil.getNowDateTimeString());

        if(agvZone.getId()==null){
            agvZoneService.save(agvZone);
        }else {
            agvZoneService.update(agvZone);
        }
    }

    private Boolean hasTask(String code,String wareCode){
        Condition condition = new Condition(TaskInfo.class);
        condition.createCriteria().andEqualTo("fromCellCode",code)
                .andEqualTo("wareCode",wareCode);
        List<TaskInfo> list = taskInfoService.findByCondition(condition);

        Condition condition2 = new Condition(TaskInfo.class);
        condition2.createCriteria().andEqualTo("toCellCode",code)
                .andEqualTo("wareCode",wareCode);
        List<TaskInfo> list2 = taskInfoService.findByCondition(condition2);

        if(list.size()>0 || list2.size()>0){
            return true;
        }else {
            return false;
        }
    }
}
