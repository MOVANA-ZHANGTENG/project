package com.deer.wcs.xj.handle;

import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * P1仓库--------------------
 * 希捷提升机的运动任务逻辑
 */
@Component("TsjHandle")
public class TsjHandle {

    private static final Logger log = LoggerFactory.getLogger(TsjHandle.class);

    @Autowired
    private DeviceValueService deviceValueService;
    @Autowired
    private JobInfoService jobInfoService;
    @Autowired
    private TaskInfoService taskInfoService;
    @Autowired
    private DeviceInfoService deviceInfoService;

    private Object read(String valueCode) {
        return deviceValueService.readValueByCode("XJ_TSJ1", valueCode);
    }

    private Object write(String valueCode, Object object) {
        return deviceValueService.writeValueByCode("XJ_TSJ1", valueCode, object);
    }

    // 写入类型常量
    private final String W_HEART_BEAT = "w_heartBeat";
    private final String W_TASK_NO = "w_taskNo";
    private final String W_CAR_NO = "w_carNo";
    private final String W_TARGET_FLOOR = "w_targetFloor";
    private final String W_TASK_TYPE = "w_taskType";
    private final String W_RESET = "w_reset";
    private final String W_ORDER = "w_order";
    private final String W_FINISH_CONFIRM = "w_finishConfirm";
    private final String W_STOP = "w_stop";
    private final String W_INIT_TASK = "w_initTask";

    // 读取类型常量
    private final String HEART_BEAT = "heartBeat";
    private final String TASK_NO = "taskNo";
    private final String CAR_NO = "carNo";
    private final String TARGET_FLOOR = "targetFloor";
    private final String TASK_TYPE = "taskType";
    private final String GET_CAR_READY = "getCarReady";
    private final String PUT_CAR_READY = "putCarReady";
    private final String CURRENT_FLOOR = "currentFloor";
    private final String HAS_CAR = "hasCar";
    private final String TASK_BUSY = "taskBusy";
    private final String TASK_LOCK = "taskLock";
    private final String TASK_BLOCK = "taskBlock";
    private final String TASK_FINISH = "taskFinish";
    private final String ERR_CODE = "errCode";
    private final String INIT_TASK = "initTask";
    private final String ALLOW_CAR_IN = "allowCarIn";
    private final String ALLOW_CAR_OUT = "allowCarOut";

    // 任务类型常量
    private final String TSJ_TASK_GET = "TSJ_TASK_GET";
    private final String TSJ_TASK_PUT = "TSJ_TASK_PUT";


    /*
        心跳逻辑
     */
//    @Scheduled(fixedRate = 1000)
    public void heartBeat() {
        short wHeartBeat = (short)read(W_HEART_BEAT);
        write(W_HEART_BEAT,(short)(wHeartBeat+ 1));
        short heartBeat = (short) read(HEART_BEAT);
        DeviceInfo deviceInfo = deviceInfoService.findByCode("XJ_TSJ1");
        if(deviceInfo==null){
            log.error("设备不存在");
            return;
        }
        if(heartBeat-wHeartBeat>100||wHeartBeat-heartBeat>100){
            if(deviceInfo.getIsOnline()==1){
                deviceInfo.setIsOnline(0);
                deviceInfoService.update(deviceInfo);
                log.error("设备掉线");
                log.error("设备掉线");
                log.error("设备掉线");
            }
        }else{
            if(deviceInfo.getIsOnline()==0){
                deviceInfo.setIsOnline(1);
                deviceInfoService.update(deviceInfo);
                log.error("设备上线");
                log.error("设备上线");
                log.error("设备上线");
            }
        }
    }

    public Boolean setParams(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if(taskInfo==null){
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        jobInfo.setFromCellCode(taskInfo.getFromCellCode());
        jobInfo.setToCellCode(taskInfo.getToCellCode());
        jobInfoService.update(jobInfo);
        return true;
    }

    /*
        发送任务信息
     */
    public Boolean sendGetCarOrder(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if(taskInfo==null){
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        String taskNo = jobInfo.getTaskId().toString();
        String carNo = taskInfo.getRcsCarId().toString();
        String targetFloor = jobInfo.getToCellCode();
        short taskType;
        String taskInfoType = taskInfo.getType();

        // 根据taskInfo的type确定任务类型和目标楼层
        if (TSJ_TASK_GET.equals(taskInfoType)) {
            taskType = 101;
            targetFloor = jobInfo.getFromCellCode();
        } else if (TSJ_TASK_PUT.equals(taskInfoType)) {
            taskType = 102;
        } else {
            jobInfoService.updateMemo(jobInfo, "任务类型无效: " + taskInfoType);
            return false;
        }

        // 非空校验
        if (taskNo == null || taskNo.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "任务编号为空");
            return false;
        }
        if (carNo == null || carNo.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "小车编号为空");
            return false;
        }
        if (targetFloor == null || targetFloor.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "目标楼层为空");
            return false;
        }

        // 发送任务到PLC
        if (taskType == 101) {
            short getCarReady = (short) read(GET_CAR_READY);
            if (getCarReady == 1) {
                write(W_TASK_NO, taskNo);
                write(W_CAR_NO,Short.parseShort(carNo));
                write(W_TARGET_FLOOR, Short.parseShort(targetFloor));
                write(W_TASK_TYPE, taskType);
            } else {
                jobInfoService.updateMemo(jobInfo, "提升机不允许取小车");
                return false;
            }
        } else if (taskType == 102) {
            short putCarReady = (short) read(PUT_CAR_READY);
            if (putCarReady == 1) {
                write(W_TASK_NO, taskNo);
                write(W_CAR_NO, Short.parseShort(carNo));
                write(W_TARGET_FLOOR, Short.parseShort(targetFloor));
                write(W_TASK_TYPE, taskType);
            } else {
                jobInfoService.updateMemo(jobInfo, "提升机不允许放小车");
                return false;
            }
        }

        jobInfoService.updateMemo(jobInfo, "提升机接小车任务发送成功");
        return true;
    }

    /*
        校验任务，发送开始指令
     */
    public Boolean checkTask(JobInfo jobInfo) throws InterruptedException {
        if (!validateAndCompareTask(jobInfo)) {
            return false;
        }

        write(W_ORDER, (short)1);

        Thread.sleep(100);

        short getCarReady = (short) read(GET_CAR_READY);
        short putCarReady = (short) read(PUT_CAR_READY);
        short taskBusy = (short) read(TASK_BUSY);
        if (getCarReady == 0 && putCarReady == 0 && taskBusy == 1) {
            write(W_ORDER,(short)0);
            jobInfoService.updateMemo(jobInfo, "提升机任务接收成功");
            return true;
        }

        jobInfoService.updateMemo(jobInfo, "提升机未成功接收任务");
        return false;
    }


    /*
        检测任务完成
     */
    public Boolean checkFinish(JobInfo jobInfo) throws InterruptedException {
        if (!validateAndCompareTask(jobInfo)) {
            return false;
        }

        short taskFinish = (short) read(TASK_FINISH);
        if (taskFinish == 1) {
            TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
            if (taskInfo == null) {
                jobInfoService.updateMemo(jobInfo, "任务不存在");
                return false;
            }

            String targetFloor = jobInfo.getToCellCode();
            String taskInfoType = taskInfo.getType();

            // 根据taskInfo的type确定目标楼层
            if (TSJ_TASK_GET.equals(taskInfoType)) {
                targetFloor = jobInfo.getFromCellCode();
            } else if (TSJ_TASK_PUT.equals(taskInfoType)) {
                // 保持原目标楼层
            } else {
                jobInfoService.updateMemo(jobInfo, "任务类型无效: " + taskInfoType);
                return false;
            }
            short currentFloor = (short) read(CURRENT_FLOOR);

            if (currentFloor != Short.parseShort(targetFloor)) {
                jobInfoService.updateMemo(jobInfo, "提升机当前楼层与目标楼层不一致");
                return false;
            }

            write(W_FINISH_CONFIRM, (short)1);

            Thread.sleep(100);

            taskFinish = (short) read(TASK_FINISH);
            if (taskFinish == 0) {
                write(W_FINISH_CONFIRM, (short)0);
                jobInfoService.updateMemo(jobInfo, "提升机任务完成");
                return true;
            }
        }
        jobInfoService.updateMemo(jobInfo, "提升机任务未完成");
        return false;
    }


    /*
        任务完成回调
     */
    public Boolean finishCallback(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        // 校验任务参数并比对PLC数据
        write(W_TASK_NO, " ");
        write(W_CAR_NO, (short) 0);
        write(W_TARGET_FLOOR, (short) 0);
        write(W_TASK_TYPE, (short) 0);
        write(W_ORDER, (short) 0);

        short taskType;
        String taskInfoType = taskInfo.getType();

        // 根据taskInfo的type确定任务类型
        if (TSJ_TASK_GET.equals(taskInfoType)) {
            taskType = 101;
        } else if (TSJ_TASK_PUT.equals(taskInfoType)) {
            taskType = 102;
        } else {
            jobInfoService.updateMemo(jobInfo, "任务类型无效: " + taskInfoType);
            return false;
        }

        if (taskType == 101) {
            short allowCarIn = (short) read(ALLOW_CAR_IN);
            if (allowCarIn == 0) {
                jobInfoService.updateMemo(jobInfo, "提升机不允许取小车");
                return false;
            }

            log.info("发送小车进入提升机任务");
        } else if (taskType == 102) {
            short allowCarOut = (short) read(ALLOW_CAR_OUT);
            if (allowCarOut == 0) {
                jobInfoService.updateMemo(jobInfo, "提升机不允许放小车");
                return false;
            }
            log.info("发送小车退出提升机任务");
        } else {
            jobInfoService.updateMemo(jobInfo, "任务类型无效: " + taskType);
            return false;
        }

        return true;
    }

    /**
     * 校验任务参数并比对PLC数据
     *
     * @param jobInfo 任务信息
     * @return true表示校验通过，false表示校验失败
     */
    private Boolean validateAndCompareTask(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        String taskNo = jobInfo.getTaskId().toString();
        String targetFloor = jobInfo.getToCellCode();
        short taskType;
        String taskInfoType = taskInfo.getType();

        // 根据taskInfo的type确定任务类型和目标楼层
        if (TSJ_TASK_GET.equals(taskInfoType)) {
            taskType = 101;
            targetFloor = jobInfo.getFromCellCode();
        } else if (TSJ_TASK_PUT.equals(taskInfoType)) {
            taskType = 102;
        } else {
            jobInfoService.updateMemo(jobInfo, "任务类型无效: " + taskInfoType);
            return false;
        }

        // 非空校验
        if (taskNo == null || taskNo.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "任务编号为空");
            return false;
        }
        if (targetFloor == null || targetFloor.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "目标楼层为空");
            return false;
        }

        // 比对PLC数据
        String r_taskNo = (String) read(TASK_NO);
        if (!r_taskNo.equals(taskNo)) {
            jobInfoService.updateMemo(jobInfo, "提升机任务编号与任务编号不一致");
            return false;
        }

        short r_targetFloor = (short) read(TARGET_FLOOR);
        if (r_targetFloor != Short.parseShort(targetFloor)) {
            jobInfoService.updateMemo(jobInfo, "提升机目标楼层与目标楼层不一致");
            return false;
        }

        short r_taskType = (short) read(TASK_TYPE);
        if (r_taskType != taskType) {
            jobInfoService.updateMemo(jobInfo, "提升机任务类型与任务类型不一致");
            return false;
        }

        return true;
    }

}
