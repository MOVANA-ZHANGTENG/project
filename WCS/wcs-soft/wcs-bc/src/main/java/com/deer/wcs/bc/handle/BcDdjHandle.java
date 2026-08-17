package com.deer.wcs.bc.handle;


import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.base.service.LineInfoService;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;


@Component("BcDdjHandle")
public class BcDdjHandle {
    
    
    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private DeviceValueService deviceValueService;

    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private LineInfoService lineInfoService;

    @Autowired
    private JobInfoService jobInfoService;

    @Value("${isTest}")
    private Boolean isTest;


    public Boolean allotInCell(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if(taskInfo.getToCellCode() != null){

            jobInfo.setToCellCode(taskInfo.getToCellCode());
            jobInfoService.update(jobInfo);
            return true;
        }
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria().andEqualTo("wareCode",taskInfo.getWareCode())
                .andEqualTo("invenState",0L)
                .andEqualTo("taskState",0L)
                .andEqualTo("disableState",0L);


        List<CellInfo> list = cellInfoService.findByCondition(condition);

        if(list.isEmpty()){
            jobInfoService.updateMemo(jobInfo,"无空库位");
            return false;
        }
        taskInfo.setToCellCode(list.get(0).getCode());
        taskInfoService.update(taskInfo);
        jobInfoService.updateMemo(jobInfo,"分配入库库位"+taskInfo.getToCellCode());
        return true;
    }


    /**
     * 检测堆垛机当前是否可以接收任务
     * 根据deviceValue表  去读取堆垛机
     * @param jobInfo
     * @return
     */
    public Boolean canRun(JobInfo jobInfo){

        if(isTest){
            jobInfoService.updateMemo(jobInfo, "测试模式，直接通过【检测堆垛机当前是否可以接收任务】");
            return true;
        }

        jobInfo.setDeviceCode("CRANE_001");
        // 读取堆垛机当前状态，C_State表示当前状态：0-离线，1-空闲，2-运行，4-故障，5-手动，-1-未知
        Object stateObj = deviceValueService.readValueByCode(jobInfo.getDeviceCode(), "C_State");
        if(stateObj == null){
            jobInfoService.updateMemo(jobInfo, "无法获取堆垛机状态");
            return false;
        }

        Short state = Short.valueOf(stateObj.toString());

        if(state == 1){
            // 空闲状态，可以接收任务
            return true;
        } else if(state == 0){
            jobInfoService.updateMemo(jobInfo, "当前堆垛机离线");
        } else if(state == 2){
            jobInfoService.updateMemo(jobInfo, "当前堆垛机正在运行中");
        } else if(state == 4){
            jobInfoService.updateMemo(jobInfo, "当前堆垛机发生故障");
        } else if(state == 5){
            jobInfoService.updateMemo(jobInfo, "当前堆垛机处于手动模式");
        } else if(state == -1){
            jobInfoService.updateMemo(jobInfo, "当前堆垛机状态未知");
        } else {
            jobInfoService.updateMemo(jobInfo, "当前堆垛机处于未定义状态");
        }
        return false;
    }

    @Autowired
    private TaskInfoService taskInfoService;

    /**
     * 发送入库任务给堆垛机
     *
     * @param jobInfo
     * @return
     */
    public Boolean sendInTask(JobInfo jobInfo){
        if(isTest){
            jobInfoService.updateMemo(jobInfo, "测试模式，直接通过【发送入库任务给堆垛机】");
            return true;
        }
        jobInfo.setDeviceCode("CRANE_001");

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String fromCellCode = taskInfo.getFromCellCode();  //入库站台号

        //入库库位
        CellInfo to = cellInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getToCellCode());
        if (to == null) {
            jobInfoService.updateMemo(jobInfo, "找不到目标入库库位");
            return false;
        }

        // 逐个写入入库任务参数
        // 入库行
        boolean rkHangResult = deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "RK_Hang", to.getX().shortValue());
        if (!rkHangResult) {
            jobInfoService.updateMemo(jobInfo, "发送入库行参数失败");
            return false;
        }
        // 入库列
        boolean rkLineResult = deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "RK_Line", to.getY().shortValue());
        if (!rkLineResult) {
            jobInfoService.updateMemo(jobInfo, "发送入库列参数失败");
            return false;
        }
        // 入库层
        boolean rkCengResult = deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "RK_Ceng", to.getZ().shortValue());
        if (!rkCengResult) {
            jobInfoService.updateMemo(jobInfo, "发送入库层参数失败");
            return false;
        }
        // 取货站台号
        boolean getStationResult = deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "Get_Station", Short.parseShort(fromCellCode));
        if (!getStationResult) {
            jobInfoService.updateMemo(jobInfo, "发送取货站台号参数失败");
            return false;
        }
        // 设置任务号
//        boolean taskIdResult = deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "Task_ID", jobInfo.getTaskId());
//        if (!taskIdResult) {
//            jobInfoService.updateMemo(jobInfo, "发送任务号参数失败");
//            return false;
//        }

        // 发送开始入库指令
        boolean startResult = deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "Begin_RK", true);
        if (startResult) {
            jobInfoService.updateMemo(jobInfo, "成功发送入库任务");
            return true;
        } else {
            jobInfoService.updateMemo(jobInfo, "发送开始入库指令失败");
            return false;
        }
    }

    /**
     * 发送出库任务给堆垛机
     *
     * @param jobInfo
     * @return
     */
    public Boolean sendOutTask(JobInfo jobInfo){
        if(isTest){
            jobInfoService.updateMemo(jobInfo, "测试模式，直接通过【发送出库任务给堆垛机】");
            return true;
        }
        jobInfo.setDeviceCode("CRANE_001");

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String toCellCode = taskInfo.getToCellCode();  //出库站台号

        //出库库位
        CellInfo from = cellInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getFromCellCode());
        if (from == null) {
            jobInfoService.updateMemo(jobInfo, "找不到目标出库库位");
            return false;
        }

        // 逐个写入出库任务参数
        // 出库行
        boolean ckHangResult = deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "CK_Hang", from.getX().shortValue());
        if (!ckHangResult) {
            jobInfoService.updateMemo(jobInfo, "发送出库行参数失败");
            return false;
        }
        // 出库列
        boolean ckLineResult = deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "CK_Line", from.getY().shortValue());
        if (!ckLineResult) {
            jobInfoService.updateMemo(jobInfo, "发送出库列参数失败");
            return false;
        }
        // 出库层
        boolean ckCengResult = deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "CK_Ceng", from.getZ().shortValue());
        if (!ckCengResult) {
            jobInfoService.updateMemo(jobInfo, "发送出库层参数失败");
            return false;
        }
        // 放货站台号
        boolean putStationResult = deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "Put_Station", Short.parseShort(toCellCode));
        if (!putStationResult) {
            jobInfoService.updateMemo(jobInfo, "发送放货站台号参数失败");
            return false;
        }
        // 设置任务号
//        boolean taskIdResult = deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "Task_ID", jobInfo.getTaskId());
//        if (!taskIdResult) {
//            jobInfoService.updateMemo(jobInfo, "发送任务号参数失败");
//            return false;
//        }

        // 发送开始出库指令
        boolean startResult = deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "Begin_CK", true);
        if (startResult) {
            jobInfoService.updateMemo(jobInfo, "成功发送出库任务");
            return true;
        } else {
            jobInfoService.updateMemo(jobInfo, "发送开始出库指令失败");
            return false;
        }
    }

    /**
     * 检测入库任务是否完成
     *
     * @param jobInfo
     * @return true表示任务已完成，false表示任务未完成或检测失败
     */
    public Boolean checkInTaskFinish(JobInfo jobInfo){
        if(isTest){
            jobInfoService.updateMemo(jobInfo, "测试模式，直接通过【检测入库任务是否完成】");
            return true;
        }
        jobInfo.setDeviceCode("CRANE_001");

        // 读取任务完成状态
        Object taskFinishObj = deviceValueService.readValueByCode(jobInfo.getDeviceCode(), "Task_Finish");
        if(taskFinishObj == null){
            jobInfoService.updateMemo(jobInfo, "无法获取任务完成状态");
            return false;
        }

        Short taskFinish = Short.valueOf(taskFinishObj.toString());
        if(taskFinish == 1){
            // 读取任务类型，确认是入库任务
            Object taskTypeObj = deviceValueService.readValueByCode(jobInfo.getDeviceCode(), "Task_Type");
            if(taskTypeObj != null){
                Short taskType = Short.valueOf(taskTypeObj.toString());
                if(taskType == 1){
                    // 是入库任务且已完成
                    jobInfoService.updateMemo(jobInfo, "入库任务已完成");
                    // 重置任务完成标志
                    deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "Task_Finish", (short)0);
                    return true;
                }
            }
        }

        // 任务未完成
        return false;
    }

    /**
     * 检测出库任务是否完成
     *
     * @param jobInfo
     * @return true表示任务已完成，false表示任务未完成或检测失败
     */
    public Boolean checkOutTaskFinish(JobInfo jobInfo){
        if(isTest){
            jobInfoService.updateMemo(jobInfo, "测试模式，直接通过【检测出库任务是否完成】");
            return true;
        }
        jobInfo.setDeviceCode("CRANE_001");

        // 读取任务完成状态
        Object taskFinishObj = deviceValueService.readValueByCode(jobInfo.getDeviceCode(), "Task_Finish");
        if(taskFinishObj == null){
            jobInfoService.updateMemo(jobInfo, "无法获取任务完成状态");
            return false;
        }

        Short taskFinish = Short.valueOf(taskFinishObj.toString());
        if(taskFinish == 1){
            // 读取任务类型，确认是出库任务
            Object taskTypeObj = deviceValueService.readValueByCode(jobInfo.getDeviceCode(), "Task_Type");
            if(taskTypeObj != null){
                Short taskType = Short.valueOf(taskTypeObj.toString());
                if(taskType == 2){
                    // 是出库任务且已完成
                    jobInfoService.updateMemo(jobInfo, "出库任务已完成");
                    // 重置任务完成标志
                    deviceValueService.writeValueByCode(jobInfo.getDeviceCode(), "Task_Finish", (short)0);
                    return true;
                }
            }
        }

        // 任务未完成
        return false;
    }


    
}
