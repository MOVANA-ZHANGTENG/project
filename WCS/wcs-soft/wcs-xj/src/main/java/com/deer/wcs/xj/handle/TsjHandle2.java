package com.deer.wcs.xj.handle;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.model.RcsTsj;
import com.deer.wcs.rcs.service.RcsCarInfoService;
import com.deer.wcs.rcs.service.RcsTsjService;
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
 * P1仓库-------------------------------------
 * 希捷提升机的运动任务逻辑
 */
@Component("TsjHandle2")
public class TsjHandle2 {

    private static final Logger log = LoggerFactory.getLogger(TsjHandle2.class);

    @Autowired
    private DeviceValueService deviceValueService;
    @Autowired
    private JobInfoService jobInfoService;
    @Autowired
    private TaskInfoService taskInfoService;
    @Autowired
    private DeviceInfoService deviceInfoService;
    @Autowired
    private RcsTsjService rcsTsjService;

    private Object read(String deviceCode,String valueCode) {
        return deviceValueService.readValueByCode(deviceCode, valueCode);
    }

    private Object write(String deviceCode,String valueCode, Object object) {
        return deviceValueService.writeValueByCode(deviceCode, valueCode, object);
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
    @Scheduled(fixedRate = 1000)
    public void heartBeat() {

//        String[] deviceCodes = {"XJ_TSJ1","XJ_TSJ2"};
        String[] deviceCodes = {"XJ_TSJ1"};
        DeviceInfo deviceInfo = null;
        for(String deviceCode:deviceCodes){
            try{
                short wHeartBeat = (short)read(deviceCode,W_HEART_BEAT);
                write(deviceCode,W_HEART_BEAT,(short)(wHeartBeat+ 1));
                short heartBeat = (short) read(deviceCode,HEART_BEAT);
                deviceInfo = deviceInfoService.findByCode(deviceCode);
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
            }catch (Exception e){
                if(deviceInfo.getIsOnline()==1){
                    deviceInfo.setIsOnline(0);
                    deviceInfoService.update(deviceInfo);
                    log.error("设备掉线");
                    log.error("设备掉线");
                    log.error("设备掉线");
                }
            }
        }
    }

    public Boolean checkAtTargetFloor(JobInfo jobInfo) {
        try {
            // 1. 获取小车信息
            if (jobInfo.getRcsCarId() == null) {
                jobInfoService.updateMemo(jobInfo, "【提升机】小车ID不存在");
                return false;
            }

            RcsCarInfo carInfo = rcsCarInfoService.findById(jobInfo.getRcsCarId());
            if (carInfo == null) {
                jobInfoService.updateMemo(jobInfo, "【提升机】未找到小车信息");
                return false;
            }

            // 2. 获取小车当前位置
            String cellCode = carInfo.getFromCellCode();
            if (cellCode == null || cellCode.isEmpty()) {
                jobInfoService.updateMemo(jobInfo, "【提升机】小车当前位置为空");
                return false;
            }

            // 3. 查询库位信息
            TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
            CellInfo cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), cellCode);
            if (cellInfo == null) {
                jobInfoService.updateMemo(jobInfo, "【提升机】小车当前库位信息不存在: " + cellCode);
                return false;
            }

            // 4. 获取小车当前所在楼层
            Short carCurrentFloor = Short.parseShort(cellInfo.getZ().toString());

            RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
            if(rcsTsj==null){
                jobInfoService.updateMemo(jobInfo, "【提升机】找不到当前任务锁定的提升机");
                return false;
            }
            String deviceCode = rcsTsj.getDeviceCode();

            // 5. 读取提升机当前楼层
            Short tsjCurrentFloor = (Short) read(deviceCode,CURRENT_FLOOR);
            if (tsjCurrentFloor == null) {
                jobInfoService.updateMemo(jobInfo, "【提升机】无法读取提升机当前楼层信息");
                return false;
            }

            // 6. 对比楼层
            if (!tsjCurrentFloor.equals(carCurrentFloor)) {
                jobInfoService.updateMemo(jobInfo,
                        String.format("【提升机】不在小车当前楼层，提升机在:%d层，小车在:%d层(位置:%s)",
                                tsjCurrentFloor, carCurrentFloor, cellCode));
                return false;
            }

            // 9. 检查提升机内是否有小车（如果有小车则不能进入）
            Short cartSignal = (Short) read(deviceCode,HAS_CAR);
            if (cartSignal != null && cartSignal == 1) {
                jobInfoService.updateMemo(jobInfo, "【提升机】内部已有小车，无法进入");
                return false;
            }

            // 9. 检查提升机内是否有小车（如果有小车则不能进入）
            Short putCarReady = (Short) read(deviceCode,PUT_CAR_READY);
            if (putCarReady != null && putCarReady == 1) {
                jobInfoService.updateMemo(jobInfo, "【提升机】送车ready未准备好，无法进入");
                return false;
            }

            jobInfoService.updateMemo(jobInfo,
                    String.format("【提升机】满足进入条件(楼层:%d，位置:%s)", carCurrentFloor, cellCode));
            return true;

        } catch (Exception e) {
            log.error("检测提升机进入条件时发生异常", e);
            jobInfoService.updateMemo(jobInfo, "【提升机】进入条件检测异常: " + e.getMessage());
            return false;
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

    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private RcsCarInfoService rcsCarInfoService;

    /*
        发送任务信息 - 到达接货楼层
     */
    public Boolean sendGetCarOrderGet(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if(taskInfo==null){
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        String taskNo = jobInfo.getId().toString();


        // 2. 获取小车信息
        if (jobInfo.getRcsCarId() == null) {
            jobInfoService.updateMemo(jobInfo, "【提升机】请求进入调用 小车ID不存在");
            return false;
        }
        String carNo = jobInfo.getRcsCarId().toString();
        RcsCarInfo carInfo = rcsCarInfoService.findById(jobInfo.getRcsCarId());
        if (carInfo == null) {
            jobInfoService.updateMemo(jobInfo, "【提升机】请求进入调用 未找到小车信息");
            return false;
        }

        // 3. 获取小车当前位置（小车呼叫提升机，提升机需要到达小车所在楼层）
        String currentCellCode = carInfo.getFromCellCode();
        if(currentCellCode == null || currentCellCode.isEmpty()){
            jobInfoService.updateMemo(jobInfo, "【提升机】请求进入调用 小车当前位置为空");
            return false;
        }

        CellInfo currentCellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), currentCellCode);
        if(currentCellInfo == null){
            jobInfoService.updateMemo(jobInfo, "【提升机】请求进入调用 小车当前库位不存在: " + currentCellCode);
            return false;
        }

        // 4. 解析小车当前楼层，即提升机应该到达的目标楼层
        if (currentCellInfo.getZ() == null) {
            jobInfoService.updateMemo(jobInfo, "【提升机】请求进入调用 小车当前楼层信息为空");
            return false;
        }
        String targetFloor = currentCellInfo.getZ().toString();
        jobInfo.setFromCellCode(currentCellCode);
        jobInfoService.update(jobInfo);

        short taskType = 101;

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

        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
        if(rcsTsj==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】找不到当前任务锁定的提升机");
            return false;
        }
        String deviceCode = rcsTsj.getDeviceCode();

        // 发送任务到PLC - 到达接货楼层
        short getCarReady = (short) read(deviceCode,GET_CAR_READY);
        if (getCarReady == 1) {
            write(deviceCode,W_TASK_NO, taskNo);
            write(deviceCode,W_CAR_NO,Short.parseShort(carNo));
            write(deviceCode,W_TARGET_FLOOR, Short.parseShort(targetFloor));
            write(deviceCode,W_TASK_TYPE, taskType);
        } else {
            jobInfoService.updateMemo(jobInfo, "提升机不允许小车进入");
            return false;
        }

        try {
            Thread.sleep(200);
        }catch (Exception ex){

        }

        if (!validateAndCompareTaskGet(jobInfo)) {
            return false;
        }

        write(deviceCode,W_ORDER, (short)1);
        try {
            Thread.sleep(500);
        }catch (Exception ex){

        }

        getCarReady = (short) read(deviceCode,GET_CAR_READY);
        short taskBusy = (short) read(deviceCode,TASK_BUSY);
        if (getCarReady == 0 && taskBusy == 1) {
            write(deviceCode,W_ORDER,(short)0);
            jobInfoService.updateMemo(jobInfo, "提升机到达接货楼层任务接收成功");
            return true;
        }

        jobInfoService.updateMemo(jobInfo, "提升机到达接货楼层任务未成功接收");
        return false;

    }


    public Boolean sendGetCarOrderGet2(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if(taskInfo==null){
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        String taskNo = jobInfo.getId().toString();


        CellInfo currentCellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getToCellCode());
        if(currentCellInfo == null){
            jobInfoService.updateMemo(jobInfo, "【提升机】请求进入调用 目标库位不存在: " + jobInfo.getToCellCode());
            return false;
        }


        String targetFloor = currentCellInfo.getZ().toString();
        jobInfo.setFromCellCode(taskInfo.getToCellCode());
        jobInfoService.update(jobInfo);

        short taskType = 101;

        // 非空校验
        if (taskNo == null || taskNo.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "任务编号为空");
            return false;
        }
//        if (carNo == null || carNo.isEmpty()) {
//            jobInfoService.updateMemo(jobInfo, "小车编号为空");
//            return false;
//        }
        if (targetFloor == null || targetFloor.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "目标楼层为空");
            return false;
        }

        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
        if(rcsTsj==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】找不到当前任务锁定的提升机");
            return false;
        }
        String deviceCode = rcsTsj.getDeviceCode();

        // 发送任务到PLC - 接小车
        short getCarReady = (short) read(deviceCode,GET_CAR_READY);
        if (getCarReady == 1) {
            write(deviceCode,W_TASK_NO, taskNo);
         //   write(W_CAR_NO,Short.parseShort(carNo));
            write(deviceCode,W_TARGET_FLOOR, Short.parseShort(targetFloor));
            write(deviceCode,W_TASK_TYPE, taskType);
        } else {
            jobInfoService.updateMemo(jobInfo, "提升机不允许取小车");
            return false;
        }

        try {
            Thread.sleep(200);
        }catch (Exception ex){

        }

        if (!validateAndCompareTaskGet(jobInfo)) {
            return false;
        }

        write(deviceCode,W_ORDER, (short)1);
        try {
            Thread.sleep(500);
        }catch (Exception ex){

        }

        getCarReady = (short) read(deviceCode,GET_CAR_READY);
        short taskBusy = (short) read(deviceCode,TASK_BUSY);
        if (getCarReady == 0 && taskBusy == 1) {
            write(deviceCode,W_ORDER,(short)0);
            jobInfoService.updateMemo(jobInfo, "提升机接小车任务接收成功");
            return true;
        }

        jobInfoService.updateMemo(jobInfo, "提升机接小车任务未成功接收");
        return false;

    }

//    public Boolean canGetCar(JobInfo jobInfo){
//        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
//        if(taskInfo==null){
//            jobInfoService.updateMemo(jobInfo, "任务不存在");
//            return false;
//        }
//
//        // 发送任务到PLC - 到达送货楼层
//        short putCarReady = (short) read(PUT_CAR_READY);
//        if (putCarReady != 1) {
//            jobInfoService.updateMemo(jobInfo, "提升机不允许放小车");
//            return false;
//        }
//    }

    /*
        发送任务信息 - 到达送货楼层
     */
    public Boolean sendGetCarOrderPut(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if(taskInfo==null){
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        String taskNo = jobInfo.getId().toString();
        String carNo = jobInfo.getRcsCarId().toString();
        CellInfo from = cellInfoService.findByCode(taskInfo.getWareCode(),jobInfo.getToCellCode());
        String targetFloor = from.getZ().toString();
        short taskType = 102;

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

        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
        if(rcsTsj==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】找不到当前任务锁定的提升机");
            return false;
        }
        String deviceCode = rcsTsj.getDeviceCode();

        // 发送任务到PLC - 到达送货楼层
        short putCarReady = (short) read(deviceCode,PUT_CAR_READY);
        if (putCarReady == 1) {
            write(deviceCode,W_TASK_NO, taskNo);
            write(deviceCode,W_CAR_NO, Short.parseShort(carNo));
            write(deviceCode,W_TARGET_FLOOR, Short.parseShort(targetFloor));
            write(deviceCode,W_TASK_TYPE, taskType);
        } else {
            jobInfoService.updateMemo(jobInfo, "提升机不允许小车排出");
            return false;
        }

        try {
            Thread.sleep(500);
        }catch (Exception ex){

        }
        if (!validateAndCompareTaskPut(jobInfo)) {
            return false;
        }

        write(deviceCode,W_ORDER, (short)1);
        try {
            Thread.sleep(500);
        }catch (Exception ex){

        }



        short getCarReady = (short) read(deviceCode,GET_CAR_READY);
        short taskBusy = (short) read(deviceCode,TASK_BUSY);
        if (getCarReady == 0 && taskBusy == 1) {
            write(deviceCode,W_ORDER,(short)0);
            jobInfoService.updateMemo(jobInfo, "提升机到达接货楼层任务接收成功");
            return true;
        }

        jobInfoService.updateMemo(jobInfo, "提升机到达接货楼层任务未成功接收");
        return false;


    }


    public Boolean sendGetCarOrderPut2(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if(taskInfo==null){
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        String taskNo = jobInfo.getId().toString();
      //  String carNo = taskInfo.getRcsCarId().toString();
        CellInfo from = cellInfoService.findByCode(taskInfo.getWareCode(),jobInfo.getToCellCode());
        String targetFloor = from.getZ().toString();
        short taskType = 102;

        // 非空校验
        if (taskNo == null || taskNo.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "任务编号为空");
            return false;
        }
//        if (carNo == null || carNo.isEmpty()) {
//            jobInfoService.updateMemo(jobInfo, "小车编号为空");
//            return false;
//        }
        if (targetFloor == null || targetFloor.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "目标楼层为空");
            return false;
        }

        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
        if(rcsTsj==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】找不到当前任务锁定的提升机");
            return false;
        }
        String deviceCode = rcsTsj.getDeviceCode();

        // 发送任务到PLC - 送小车
        short putCarReady = (short) read(deviceCode,PUT_CAR_READY);
        if (putCarReady == 1) {
            write(deviceCode,W_TASK_NO, taskNo);
        //    write(W_CAR_NO, Short.parseShort(carNo));
            write(deviceCode,W_TARGET_FLOOR, Short.parseShort(targetFloor));
            write(deviceCode,W_TASK_TYPE, taskType);
        } else {
            jobInfoService.updateMemo(jobInfo, "提升机不允许放小车");
            return false;
        }

        try {
            Thread.sleep(500);
        }catch (Exception ex){

        }
        if (!validateAndCompareTaskPut(jobInfo)) {
            return false;
        }

        write(deviceCode,W_ORDER, (short)1);
        try {
            Thread.sleep(500);
        }catch (Exception ex){

        }



        short getCarReady = (short) read(deviceCode,GET_CAR_READY);
        short taskBusy = (short) read(deviceCode,TASK_BUSY);
        if (getCarReady == 0 && taskBusy == 1) {
            write(deviceCode,W_ORDER,(short)0);
            jobInfoService.updateMemo(jobInfo, "提升机接小车任务接收成功");
            return true;
        }

        jobInfoService.updateMemo(jobInfo, "提升机接小车任务未成功接收");
        return false;


    }








    /*
        检测任务完成 - 到达接货楼层
     */
    public Boolean checkFinishGet(JobInfo jobInfo) throws InterruptedException {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());



        if (!validateAndCompareTaskGet(jobInfo)) {
            return false;
        }

        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
        if(rcsTsj==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】找不到当前任务锁定的提升机");
            return false;
        }
        String deviceCode = rcsTsj.getDeviceCode();

        short taskFinish = (short) read(deviceCode,TASK_FINISH);
        if (taskFinish == 1) {
            CellInfo cell =cellInfoService.findByCode(taskInfo.getWareCode(),jobInfo.getFromCellCode());
            String targetFloor =cell.getZ().toString();
            short currentFloor = (short) read(deviceCode,CURRENT_FLOOR);

            if (currentFloor != Short.parseShort(targetFloor)) {
                jobInfoService.updateMemo(jobInfo, "提升机当前楼层与目标楼层不一致");
                return false;
            }

            write(deviceCode,W_FINISH_CONFIRM, (short)1);

            Thread.sleep(100);

            taskFinish = (short) read(deviceCode,TASK_FINISH);
            if (taskFinish == 0) {
                write(deviceCode,W_FINISH_CONFIRM, (short)0);
                jobInfoService.updateMemo(jobInfo, "提升机到达接货楼层任务完成");
                return true;
            }
        }
        jobInfoService.updateMemo(jobInfo, "提升机到达接货楼层任务未完成");
        return false;
    }

    /*
        检测任务完成 - 到达送货楼层
     */
    public Boolean checkFinishPut(JobInfo jobInfo) throws InterruptedException {
        if (!validateAndCompareTaskPut(jobInfo)) {
            return false;
        }
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());

        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
        if(rcsTsj==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】找不到当前任务锁定的提升机");
            return false;
        }
        String deviceCode = rcsTsj.getDeviceCode();

        short taskFinish = (short) read(deviceCode,TASK_FINISH);
        if (taskFinish == 1) {
            CellInfo cell =cellInfoService.findByCode(taskInfo.getWareCode(),jobInfo.getToCellCode());
            String targetFloor =cell.getZ().toString();
            short currentFloor = (short) read(deviceCode,CURRENT_FLOOR);

            if (currentFloor != Short.parseShort(targetFloor)) {
                jobInfoService.updateMemo(jobInfo, "提升机当前楼层与目标楼层不一致");
                return false;
            }

            write(deviceCode,W_FINISH_CONFIRM, (short)1);

            Thread.sleep(100);

            taskFinish = (short) read(deviceCode,TASK_FINISH);
            if (taskFinish == 0) {
                write(deviceCode,W_FINISH_CONFIRM, (short)0);
                jobInfoService.updateMemo(jobInfo, "提升机到达送货楼层任务完成");
                return true;
            }
        }
        jobInfoService.updateMemo(jobInfo, "提升机到达送货楼层任务未完成");
        return false;
    }


    /*
        任务完成回调 - 到达接货楼层
     */
    public Boolean finishCallbackGet(JobInfo jobInfo) {

        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
        if(rcsTsj==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】找不到当前任务锁定的提升机");
            return false;
        }
        String deviceCode = rcsTsj.getDeviceCode();

        // 清空PLC任务数据
        write(deviceCode,W_TASK_NO, " ");
        write(deviceCode,W_CAR_NO, (short) 0);
        write(deviceCode,W_TARGET_FLOOR, (short) 0);
        write(deviceCode,W_TASK_TYPE, (short) 0);
        write(deviceCode,W_ORDER, (short) 0);

        short allowCarIn = (short) read(deviceCode,ALLOW_CAR_IN);
        if (allowCarIn == 0) {
            jobInfoService.updateMemo(jobInfo, "提升机不允许取小车");
            return false;
        }

        log.info("发到达送货楼层进入提升机任务");
        return true;
    }

    /*
        任务完成回调 - 到达送货楼层
     */
    public Boolean finishCallbackPut(JobInfo jobInfo) {

        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
        if(rcsTsj==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】找不到当前任务锁定的提升机");
            return false;
        }
        String deviceCode = rcsTsj.getDeviceCode();

        // 清空PLC任务数据
        write(deviceCode,W_TASK_NO, " ");
        write(deviceCode,W_CAR_NO, (short) 0);
        write(deviceCode,W_TARGET_FLOOR, (short) 0);
        write(deviceCode,W_TASK_TYPE, (short) 0);
        write(deviceCode,W_ORDER, (short) 0);

        short allowCarOut = (short) read(deviceCode,ALLOW_CAR_OUT);
        if (allowCarOut == 0) {
            jobInfoService.updateMemo(jobInfo, "提升机不允许放小车");
            return false;
        }

        log.info("发到达送货楼层退出提升机任务");
        return true;
    }

    /**
     * 校验任务参数并比对PLC数据 - 到达接货楼层
     *
     * @param jobInfo 任务信息
     * @return true表示校验通过，false表示校验失败
     */
    private Boolean validateAndCompareTaskGet(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        String taskNo = jobInfo.getId().toString();
        CellInfo from = cellInfoService.findByCode(taskInfo.getWareCode(),jobInfo.getFromCellCode());
        String targetFloor = from.getZ().toString();
        short taskType = 101;

        // 非空校验
        if (taskNo == null || taskNo.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "任务编号为空");
            return false;
        }
        if (targetFloor == null || targetFloor.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "目标楼层为空");
            return false;
        }

        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
        if(rcsTsj==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】找不到当前任务锁定的提升机");
            return false;
        }
        String deviceCode = rcsTsj.getDeviceCode();

        // 比对PLC数据
        String r_taskNo = (String) read(deviceCode,TASK_NO);
        if (!r_taskNo.equals(taskNo)) {
            jobInfoService.updateMemo(jobInfo, "提升机任务编号与任务编号不一致");
            return false;
        }

        short r_targetFloor = (short) read(deviceCode,TARGET_FLOOR);
        if (r_targetFloor != Short.parseShort(targetFloor)) {
            jobInfoService.updateMemo(jobInfo, "提升机目标楼层与目标楼层不一致");
            return false;
        }

        short r_taskType = (short) read(deviceCode,TASK_TYPE);
        if (r_taskType != taskType) {
            jobInfoService.updateMemo(jobInfo, "提升机任务类型与任务类型不一致");
            return false;
        }

        return true;
    }

    /**
     * 校验任务参数并比对PLC数据 - 到达送货楼层
     *
     * @param jobInfo 任务信息
     * @return true表示校验通过，false表示校验失败
     */
    private Boolean validateAndCompareTaskPut(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        String taskNo = jobInfo.getId().toString();
        CellInfo to = cellInfoService.findByCode(taskInfo.getWareCode(),jobInfo.getToCellCode());
        String targetFloor = to.getZ().toString();
        short taskType = 102;

        // 非空校验
        if (taskNo == null || taskNo.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "任务编号为空");
            return false;
        }
        if (targetFloor == null || targetFloor.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "目标楼层为空");
            return false;
        }

        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
        if(rcsTsj==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】找不到当前任务锁定的提升机");
            return false;
        }
        String deviceCode = rcsTsj.getDeviceCode();

        // 比对PLC数据
        String r_taskNo = (String) read(deviceCode,TASK_NO);
        if (!r_taskNo.equals(taskNo)) {
            jobInfoService.updateMemo(jobInfo, "提升机任务编号与任务编号不一致");
            return false;
        }

        short r_targetFloor = (short) read(deviceCode,TARGET_FLOOR);
        if (r_targetFloor != Short.parseShort(targetFloor)) {
            jobInfoService.updateMemo(jobInfo, "提升机目标楼层与目标楼层不一致");
            return false;
        }

        short r_taskType = (short) read(deviceCode,TASK_TYPE);
        if (r_taskType != taskType) {
            jobInfoService.updateMemo(jobInfo, "提升机任务类型与任务类型不一致");
            return false;
        }

        return true;
    }

}
