package com.deer.wcs.xj.task;


import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.PalletInfo;
import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.service.*;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.TaskInfoService;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

@Slf4j
@Component("XjP4WlCsTask")
public class XjP4WlCsTask {



    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private DeviceValueService deviceValueService;


    @Value("${isTest}")
    private Boolean isTest;

    private String[] units = {"P4-LINE1-WL-IN" };
    private String[] outInits = {"P4-LINE1-WL-OUT" };
    private String[] crnDevices = {"crn1" };

    @Autowired
    private PalletInfoService palletInfoService;

    public void heart(){
        if(isTest){
            return;
        }
        for (String crnDevice:crnDevices){
            Boolean ComCheckPlc = (Boolean) deviceValueService.readValueByCode(crnDevice, "ComCheckPlc");
            deviceValueService.writeValueByCode(crnDevice, "ComCheckWcs",ComCheckPlc);
        }

    }


    private PalletInfo getPallet(String palletCode,String type){
        PalletInfo palletInfo = palletInfoService.findByCode("P4",palletCode);
        if(palletInfo==null){
            palletInfo = new PalletInfo();
            palletInfo.setWareCode("P4");
            palletInfo.setCode(palletCode);
            palletInfo.setTypeCode(type);
            palletInfoService.save(palletInfo);
            return palletInfo;
        }
        if(!palletInfo.getTypeCode().equals(type)){
            throw new ServiceException("大小有问题");
        }


        palletInfo.setWareCode("P4");
        palletInfo.setCode(palletCode);
        palletInfo.setTypeCode(type);
        palletInfoService.update(palletInfo);


        return palletInfo;

    }

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private PositionInfoService positionInfoService;

    private PositionInfo getPosition(String unit){
        PositionInfo positionInfo = positionInfoService.findByCode("P4",unit);
        if(positionInfo==null){
            positionInfo = new PositionInfo();
            positionInfo.setWareCode("P4");
            positionInfo.setCode(unit);
            positionInfoService.save(positionInfo);
        }
        return positionInfo;
    }

    @Autowired
    private CellInfoService cellInfoService;

    public  void autoOutTask(){
        if(isTest){
            return;
        }
        for(String unit:outInits){
            Condition condition  = new Condition(TaskInfo.class);
            condition.createCriteria().andEqualTo("wareCode","P4")
                    .andEqualTo("type","OUT")
                    .andEqualTo("toCellCode",unit);
            List<TaskInfo> list = taskInfoService.findByCondition(condition);
            if(!list.isEmpty()){
                return;
            }
            Boolean bStaOutReady = (Boolean) deviceValueService.readValueByCode(unit, "bStaOutReady");
            if(!bStaOutReady ){
                return;
            }

            PositionInfo positionInfo = getPosition(unit);
            String lineCode = positionInfo.getLineCode();
            if(lineCode==null){
                return;
            }
            Condition cellCondition = new Condition(CellInfo.class);
            cellCondition.createCriteria()
                    .andEqualTo("disableState",0L)
                    .andEqualTo("taskState",0L)
                    .andEqualTo("invenState",1L)
                    .andEqualTo("lineCode",lineCode);

            List<CellInfo> cellInfos = cellInfoService.findByCondition(cellCondition);
            if(cellInfos.isEmpty()){
               return;
            }

            CellInfo from = cellInfos.get(0);



            PalletInfo palletInfo = palletInfoService.findByCellCode("P4",from.getCode());
            if(palletInfo==null){
                return;
            }


            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setWareCode("P4");
            taskInfo.setWareName("P4仓库");
            taskInfo.setFromCellCode(from.getCode());
            taskInfo.setToCellCode(unit);
            taskInfo.setPalletCode(palletInfo.getCode());
            taskInfo.setType("OUT");
            taskInfoService.save(taskInfo);

        }


    }


    /**
     * NO
     * AlarmCode
     * bAutoMode
     * bAlarmMode
     * bTypeBigRequeset
     * bTypeSmallRequeset
     * bTrayDet
     * bRunInBusy
     * bRunOutBusy
     * bFull
     * bBcrReadOk
     * bBcrReadNg
     * bStaInReady
     * bStaOutReady
     * bResetACK
     * bReReadACK
     * bStopAcK
     * bRunAck
     */
    public void autoInTask(){
        if(isTest){
            return;
        }
        for(String unit:units){
            Boolean bAutoMode = (Boolean) deviceValueService.readValueByCode(unit, "bAutoMode");
            Boolean bBcrReadOk = (Boolean) deviceValueService.readValueByCode(unit, "bBcrReadOk");
            if(!bBcrReadOk){
                continue;
            }
            if(!bAutoMode){
                continue;
            }
            Boolean bAlarmMode = (Boolean) deviceValueService.readValueByCode(unit, "bAlarmMode");
            Boolean bTrayDet = (Boolean) deviceValueService.readValueByCode(unit, "bTrayDet");
            Boolean bTypeBigRequeset = (Boolean) deviceValueService.readValueByCode(unit, "bTypeBigRequeset");
            Boolean bTypeSmallRequeset = (Boolean) deviceValueService.readValueByCode(unit, "bTypeSmallRequeset");
            String palletCode = (String) deviceValueService.readValueByCode(unit, "palletCode");

            if(bTypeBigRequeset && bTypeSmallRequeset){
                log.error("大===小");
                continue;
            }

            /**
             * 自动模式  报警模式0  托盘检测  大请求
             */
            if(bAutoMode && !bAlarmMode  && bTrayDet && bTypeBigRequeset  ){
                if(palletCode.isEmpty()){
                    log.error("da请求  托盘码为空");
                    continue;
                    // 小请求  托盘码为空
                }
                if(palletCode.contains("CT")){
                    log.error("大小给错了");
                    continue;
                }
                PalletInfo palletInfo = getPallet(palletCode,"p4_big");

                Condition condition = new Condition(TaskInfo.class);

                condition.createCriteria().andEqualTo("fromCellCode", unit);
                List<TaskInfo> taskInfos = taskInfoService.findByCondition(condition);
                if(!taskInfos.isEmpty()){
                //    log.error("小请求  托盘已经存在任务");
                    continue;
                    // 小请求  任务不存在
                }
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setWareCode("P4");
                taskInfo.setWareName("P4仓库");
                taskInfo.setFromCellCode(unit);
                taskInfo.setPalletCode(palletCode);
                taskInfo.setType("IN");
                taskInfoService.save(taskInfo);

            }

            /**
             * 自动模式  报警模式0  托盘检测  小请求
             */
            if(bAutoMode && !bAlarmMode  && bTrayDet && bTypeSmallRequeset ){
                if(palletCode.isEmpty()){
                    log.error("小请求  托盘码为空");
                    continue;
                    // 小请求  托盘码为空
                }
                if(palletCode.contains("K8")){
                    log.error("大小给错了");
                    continue;
                }
                PalletInfo palletInfo = getPallet(palletCode,"p4_small");

                Condition condition = new Condition(TaskInfo.class);

                condition.createCriteria().andEqualTo("fromCellCode", unit);
                List<TaskInfo> taskInfos = taskInfoService.findByCondition(condition);
                if(!taskInfos.isEmpty()){
                 //   log.error("小请求  托盘已经存在任务");
                    continue;
                    // 小请求  任务不存在
                }
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setWareCode("P4");
                taskInfo.setWareName("P4仓库");
                taskInfo.setFromCellCode(unit);
                taskInfo.setPalletCode(palletCode);
                taskInfo.setType("IN");
                taskInfoService.save(taskInfo);

            }


        }






    }
}
