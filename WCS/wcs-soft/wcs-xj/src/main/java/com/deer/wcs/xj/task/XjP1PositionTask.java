package com.deer.wcs.xj.task;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.PalletInfo;
import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.service.*;
import com.deer.wcs.base.utils.PLCUtils;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.TaskInfoService;
import com.github.xingshuangs.iot.protocol.s7.enums.EPlcType;
import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component("XjP1PositionTask")
public class XjP1PositionTask {


    @Autowired
    private PositionInfoService positionInfoService;

    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private PalletInfoService palletInfoService;

    @Autowired
    private TaskInfoService taskInfoService;

    private static final ConcurrentHashMap<String, S7PLC> S7_CONNECTION_POOL = new ConcurrentHashMap<>();

    private static S7PLC getS7PLC(String ip, Integer port, Integer s7Type){

        String key = ip+":"+port+":"+s7Type;
        S7PLC s7PLC = S7_CONNECTION_POOL.get(key);
        if(s7PLC!=null && s7PLC.checkConnected()){
            return s7PLC;
        }
        switch (s7Type){
            case 0:
                s7PLC = new S7PLC(EPlcType.S200, ip,port);
                break;
            case 1:
                s7PLC = new S7PLC(EPlcType.S200_SMART, ip,port);
                break;
            case 2:
                s7PLC = new S7PLC(EPlcType.S300, ip,port);
                break;
            case 3:
                s7PLC = new S7PLC(EPlcType.S400, ip,port);
                break;
            case 4:
                s7PLC = new S7PLC(EPlcType.S1200, ip,port);
                break;
            case 5:
                s7PLC = new S7PLC(EPlcType.S1500, ip,port);
                break;
            case 6:
                s7PLC = new S7PLC(EPlcType.SINUMERIK_828D, ip,port);
                break;
            default:
                throw new RuntimeException("S7类型不正确！");
        }
        S7_CONNECTION_POOL.put(key,s7PLC);
        return s7PLC;
    }

    @Value("${isTest}")
    private Boolean isTest;

    public void auto(){
        Condition condition = new Condition(PositionInfo.class);
        condition.createCriteria()
                .andEqualTo("wareCode","P1") ;
        List<PositionInfo> list = positionInfoService.findByCondition(condition);
        for (PositionInfo positionInfo:list){
           handle(positionInfo);
        }
    }


    private void handle(PositionInfo positionInfo){
        try {

            //入库口
            if(positionInfo.getType().equals("IN")){
                handleIn(positionInfo);
            }

            //出库口
            if(positionInfo.getType().equals("OUT")){
                handleOut(positionInfo);
            }

            //MD 码垛
            if(positionInfo.getType().equals("MD")){

            }

            //CD 拆垛
             if(positionInfo.getType().equals("CD")){

             }


        }catch (Exception ex){
            ex.printStackTrace();
            positionInfoService.updateMemo(positionInfo,ex.getMessage(),1);
        }
    }

    private void handleIn(PositionInfo positionInfo){
        String plcAddress = "DB101.";
        String pcAddress = "DB100.";
        String plcIp = positionInfo.getPlcIp();
        double plcToPcAddress = positionInfo.getAddress1();
        double pcToPlcAddress = positionInfo.getAddress2();
       // String plcPalletAddress = positionInfo.getAddress3();

        S7PLC s7PLC = getS7PLC(plcIp,102,5);

        Short no = s7PLC.readInt16(plcAddress+plcToPcAddress   );
        short alarmCode = s7PLC.readInt16(plcAddress+(plcToPcAddress+2) );
        boolean autoMode = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4) );
        boolean alarmMode = s7PLC.readBoolean(String.format("%s%s", plcAddress, plcToPcAddress + 4.1));
        boolean bigRequest = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4.2 ) );
        boolean smallRequest = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4.3 ) );
        boolean hasBox = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4.4 ) );
        boolean inBusy = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4.5  ));
        boolean outBusy = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4.6 ) );
        boolean full = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4.7  ));
        Boolean bcrOk = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5  ));
        Boolean bcrNg = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.1 ) );
        Boolean inReady = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.2  ));
        Boolean outReady = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.3 ) );
        Boolean resetAck = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.4 ) );
        Boolean reReadAck = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.5 ) );
        Boolean stopAck = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.6  ));
        Boolean runAck = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.7 ) );

        ////////////////////////////////////////////
        //  String palletCode = s7PLC.readString(address+plcPalletAddress,50);

        ////////////////////////////////////////////////
        Boolean reset = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.0 ) );
        Boolean stop = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.1 ) );
        Boolean run = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.2 ) );
        Boolean reRead = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.3  ));
        Boolean bcrOkAck = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.4  ));
        Boolean bcrNgAck = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.5  ));
        Boolean big = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.6 ) );
        Boolean small = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.7 ) );
        Boolean pcTasking = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+1.0 ) );
        Boolean idResult = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+2.0 ) );

        Condition condition = new Condition(TaskInfo.class);

        condition.createCriteria()
                .orEqualTo("fromCellCode", positionInfo.getCode())
                .orEqualTo("toCellCode", positionInfo.getCode());
        List<TaskInfo> taskInfos = taskInfoService.findByCondition(condition);
        if(taskInfos.isEmpty()){
            s7PLC.writeBoolean(pcAddress+(pcToPlcAddress+1.0),true);
        }else {
            s7PLC.writeBoolean(pcAddress+(pcToPlcAddress+1.0),false);
        }




        if(!autoMode){
            positionInfoService.updateMemo(positionInfo,"手动模式" ,1) ;
            return;
        }
        if(alarmMode){
            positionInfoService.updateMemo(positionInfo,"报警"+alarmCode,1) ;
            return;
        }
        if(!hasBox){
            positionInfoService.updateMemo(positionInfo,"无托盘" ,1) ;

            return;
        }

        if(!inReady){
            positionInfoService.updateMemo(positionInfo,"不允许入" ,1) ;
            return;
        }
        String msg = "自动模式，无报警，有托盘，允许入； ";
//        if(!bcrOk){
//            positionInfoService.updateMemo(positionInfo,"BCR未就绪" ,1) ;
//            return;
//        }

//        if(palletCode==null || palletCode.trim().equals("")){
//            positionInfoService.updateMemo(positionInfo,"托盘码为空" ,1) ;
//            return;
//        }


        // PalletInfo palletInfo = getPallet(palletCode,"P1");

        Condition condition1 = new Condition(TaskInfo.class);

        condition.createCriteria().andEqualTo("fromCellCode", positionInfo.getCode());
         taskInfos = taskInfoService.findByCondition(condition1);
        if(!taskInfos.isEmpty()){
           return;
        }
        CellInfo cellInfo  = allocateInboundCell(positionInfo);
        if(cellInfo==null){
            positionInfoService.updateMemo(positionInfo,msg+"未分配到入库库位" ,1) ;
            return;
        }
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setWareCode("P1");
        taskInfo.setWareName("P1仓库");
        taskInfo.setFromCellCode("1-13-4");
        taskInfo.setToCellCode(cellInfo.getCode());
       // taskInfo.setPalletCode(palletCode);
        taskInfo.setType("move");
        taskInfoService.save(taskInfo);

        positionInfoService.updateMemo(positionInfo,"生成入库任务，分配到入库库位"+cellInfo.getCode(),0) ;
        return;


    }

    private void handleOut(PositionInfo positionInfo){
        String plcAddress = "DB101.";
        String pcAddress = "DB100.";
        String plcIp = positionInfo.getPlcIp();
        double plcToPcAddress = positionInfo.getAddress1();
        double pcToPlcAddress = positionInfo.getAddress2();
        double plcPalletAddress = positionInfo.getAddress3();

        S7PLC s7PLC = getS7PLC(plcIp,102,5);

        Short no = s7PLC.readInt16(plcAddress+plcToPcAddress   );
        short alarmCode = s7PLC.readInt16(plcAddress+(plcToPcAddress+2) );
        boolean autoMode = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4) );
        boolean alarmMode = s7PLC.readBoolean(String.format("%s%s", plcAddress, plcToPcAddress + 4.1));
        boolean bigRequest = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4.2 ) );
        boolean smallRequest = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4.3 ) );
        boolean hasBox = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4.4 ) );
        boolean inBusy = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4.5  ));
        boolean outBusy = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4.6 ) );
        boolean full = s7PLC.readBoolean(plcAddress+(plcToPcAddress+4.7  ));
        Boolean bcrOk = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5  ));
        Boolean bcrNg = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.1 ) );
        Boolean inReady = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.2  ));
        Boolean outReady = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.3 ) );
        Boolean resetAck = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.4 ) );
        Boolean reReadAck = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.5 ) );
        Boolean stopAck = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.6  ));
        Boolean runAck = s7PLC.readBoolean(plcAddress+(plcToPcAddress+5.7 ) );

        ////////////////////////////////////////////
        //  String palletCode = s7PLC.readString(address+plcPalletAddress,50);

        ////////////////////////////////////////////////
        Boolean reset = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.0 ) );
        Boolean stop = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.1 ) );
        Boolean run = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.2 ) );
        Boolean reRead = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.3  ));
        Boolean bcrOkAck = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.4  ));
        Boolean bcrNgAck = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.5  ));
        Boolean big = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.6 ) );
        Boolean small = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+0.7 ) );
        Boolean pcTasking = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+1.0 ) );
        Boolean idResult = s7PLC.readBoolean(pcAddress+(pcToPlcAddress+2.0 ) );

        Condition condition = new Condition(TaskInfo.class);

        condition.createCriteria()
                .orEqualTo("fromCellCode", positionInfo.getCode())
                .orEqualTo("toCellCode", positionInfo.getCode());
        List<TaskInfo> taskInfos = taskInfoService.findByCondition(condition);
        if(taskInfos.isEmpty()){
            s7PLC.writeBoolean(pcAddress+(pcToPlcAddress+1.0),true);
        }else {
            s7PLC.writeBoolean(pcAddress+(pcToPlcAddress+1.0),false);
        }






        if(!autoMode){
            positionInfoService.updateMemo(positionInfo,"手动模式" ,1) ;
            return;
        }
        if(alarmMode){
            positionInfoService.updateMemo(positionInfo,"报警"+alarmCode,1) ;
            return;
        }
        if(hasBox){
            positionInfoService.updateMemo(positionInfo,"有托盘" ,0) ;

            return;
        }

        if(!outReady){
            positionInfoService.updateMemo(positionInfo,"不允许出库" ,1) ;
            return;
        }

        String msg = "自动模式，无报警，无托盘，允许出库 ；";
//        if(!bcrOk){
//            positionInfoService.updateMemo(positionInfo,"BCR未就绪" ,1) ;
//            return;
//        }

//        if(palletCode==null || palletCode.trim().equals("")){
//            positionInfoService.updateMemo(positionInfo,"托盘码为空" ,1) ;
//            return;
//        }


        // PalletInfo palletInfo = getPallet(palletCode,"P1");

        Condition condition1 = new Condition(TaskInfo.class);

        condition.createCriteria().andEqualTo("toCellCode", positionInfo.getCode());
        taskInfos = taskInfoService.findByCondition(condition1);
        if(!taskInfos.isEmpty()){
            return;
        }
        CellInfo cellInfo  = allocateOutboundCell(positionInfo);
        if(cellInfo==null){
            positionInfoService.updateMemo(positionInfo,msg+" 未分配到chu 库库位" ,0) ;
            return;
        }
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setWareCode("P1");
        taskInfo.setWareName("P1仓库");
        taskInfo.setFromCellCode(cellInfo.getCode());
        taskInfo.setToCellCode(positionInfo.getCode());
        // taskInfo.setPalletCode(palletCode);
        taskInfo.setType("move");
        taskInfoService.save(taskInfo);

        positionInfoService.updateMemo(positionInfo,"生成出库任务，分配到出库库位"+cellInfo.getCode(),0) ;
        return;


    }

    private PalletInfo getPallet(String palletCode,String type){
        PalletInfo palletInfo = palletInfoService.findByCode("P1",palletCode);
        if(palletInfo==null){
            palletInfo = new PalletInfo();
            palletInfo.setWareCode("P4");
            palletInfo.setCode(palletCode);
            palletInfo.setTypeCode(type);
            palletInfoService.save(palletInfo);
            return palletInfo;
        }
        if(!palletInfo.getTypeCode().equals(type)){
            throw new ServiceException("托盘类型有问题");
        }


        palletInfo.setWareCode("P1");
        palletInfo.setCode(palletCode);
        palletInfo.setTypeCode(type);
        palletInfoService.update(palletInfo);


        return palletInfo;

    }

    private CellInfo allocateInboundCell(PositionInfo positionInfo) {
        try {


            // 如果没有找到，尝试查询特定条件的库位
            Condition condition = new Condition(CellInfo.class);
            condition.createCriteria()
                    .andEqualTo("wareCode", positionInfo.getWareCode())
                    .andEqualTo("type", 0)  // 空闲
                    .andEqualTo("invenState", 0)  // 空闲
                    .andEqualTo("taskState", 0)    // 无任务
                    .andBetween("z", 1, 2)
                    .andEqualTo("disableState", 0); // 未禁用 ;        // 普通货位

            condition.orderBy("z").asc();  // 按优先级排序

            List<CellInfo> cellList = cellInfoService.findByCondition(condition);

            if (cellList != null && !cellList.isEmpty()) {
                CellInfo cell = cellList.get(0);
                return cell;
            }
            return null;
        } catch (Exception e) {

            return null;
        }
    }

    private CellInfo allocateOutboundCell(PositionInfo positionInfo) {
        try {


            // 如果没有找到，尝试查询特定条件的库位
            Condition condition = new Condition(CellInfo.class);
            condition.createCriteria()
                    .andEqualTo("wareCode", positionInfo.getWareCode())
                    .andEqualTo("type", 0)  // 空闲
                    .andEqualTo("invenState", 1L)  // 空闲
                    .andEqualTo("taskState", 0)    // 无任务
                    .andEqualTo("disableState", 0); // 未禁用 ;        // 普通货位

            condition.orderBy("z").asc();  // 按优先级排序

            List<CellInfo> cellList = cellInfoService.findByCondition(condition);

            if (cellList != null && !cellList.isEmpty()) {
                CellInfo cell = cellList.get(0);
                return cell;
            }
            return null;
        } catch (Exception e) {

            return null;
        }
    }
}
