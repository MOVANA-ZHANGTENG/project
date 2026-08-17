package com.deer.wcs.jxg.handle;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.service.RcsCarInfoService;
import com.deer.wcs.system.model.Auto;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 提升机(TSJ)控制器
 * 负责提升机的调用、进入、离开、释放等操作
 */
@Component("JxgZkTsjHandle")
public class JxgZkTsjHandle {
    
    private static final Logger log = LoggerFactory.getLogger(JxgZkTsjHandle.class);
    
    @Autowired
    private DeviceValueService deviceValueService;
    
    @Autowired
    private JobInfoService jobInfoService;
    
    @Value("${isTest}")
    private Boolean isTest;
    
    // 设备编码
    private static final String DEVICE_CODE = "tsj";
    
    /**
     * 读取PLC数据
     */
    private Object readPlc(String valueCode) {
        return deviceValueService.readValueByCode(DEVICE_CODE, valueCode);
    }
    
    /**
     * 写入PLC数据
     */
    private Boolean writePlc(String valueCode, Object value) {
        return deviceValueService.writeValueByCode(DEVICE_CODE, valueCode, value);
    }

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private RcsCarInfoService rcsCarInfoService;

    @Autowired
    private RedisCache redisCache;

    Boolean isOnline=false;
    Boolean lastIsOnline=false;

    public void heart(){

        try {
            readPlc("heartbeat" );
            isOnline=true;
        }catch (Exception ex){
            isOnline=false;
        }
        if(isOnline!=lastIsOnline){
            DeviceInfo deviceInfo = deviceInfoService.findByCode(DEVICE_CODE);
            deviceInfo.setIsOnline(isOnline?1:0);
            deviceInfoService.update(deviceInfo);
            lastIsOnline =isOnline;
        }
    }

    public Boolean unAllotTsj(JobInfo jobInfo){
        String redisKey = "tsj:task_info_id";
        Object cachedTaskIdObj = redisCache.getCacheObject(redisKey);
        if(cachedTaskIdObj==null){
            jobInfoService.updateMemo(jobInfo,"提升机未被占用，但是现在在释放，逻辑错误请检查！");
            return false;
        }
        Long taskId = Long.parseLong(cachedTaskIdObj.toString());
        if(!jobInfo.getTaskId().equals(taskId)){
            jobInfoService.updateMemo(jobInfo,"当前占用提升机非本任务（"+jobInfo.getTaskId()+"），当前占用提升机任务为："+taskId+"但是现在在释放，逻辑错误请检查！");
            return false;
        }


        redisCache.deleteObject(redisKey);
        jobInfoService.updateMemo(jobInfo,"解除占用提升机："+cachedTaskIdObj);
        return true;
    }

    /**
     * 占用提升机
     * 在redis里面保存提升机的 taskInfo的Id;
     * 在占用之前，发现有值，则检测taskInfoId，是否能查到taskInfo
     * 如果查不到，则说明没被占用，可以占用
     * 如果查到了，而且id和当前任务id一致，则也可以占用。
     * 如果是其他任务，则返回false；等待；
     * 如果可以占用，把当前taskInfo的id写进去。
     * 这个项目只有一台提升机；
     */
    public Boolean allotTsjRedis(JobInfo jobInfo) {
        try {
            // Redis key：提升机占用的任务ID
            String redisKey = "tsj:task_info_id";
            
            // 获取当前任务的taskInfoId
            Long currentTaskId = jobInfo.getTaskId();
            if (currentTaskId == null) {
                log.warn("【提升机】占用失败：JobInfo的taskId为空");
                jobInfoService.updateMemo(jobInfo, "【提升机】占用失败：任务ID为空");
                return false;
            }
            
            // 从Redis读取已占用的taskInfoId
            Object cachedTaskIdObj = redisCache.getCacheObject(redisKey);
            
            // 如果Redis中没有值，说明提升机未被占用，可以直接占用
            if (cachedTaskIdObj == null) {
                redisCache.setCacheObject(redisKey, currentTaskId.toString());
                log.info("【提升机】成功占用，当前任务ID: {}", currentTaskId);
                jobInfoService.updateMemo(jobInfo, "【提升机】成功占用，任务ID:" + currentTaskId);
                return true;
            }
            
            // Redis中有值，需要验证该任务是否还存在
            String cachedTaskIdStr = cachedTaskIdObj.toString();
            Long cachedTaskId;
            try {
                cachedTaskId = Long.parseLong(cachedTaskIdStr);
            } catch (NumberFormatException e) {
                // 如果Redis中的值不是有效的数字，清除它并允许占用
                log.warn("【提升机】Redis中的taskInfoId格式错误: {}，清除后重新占用", cachedTaskIdStr);
                redisCache.deleteObject(redisKey);
                redisCache.setCacheObject(redisKey, currentTaskId.toString());
                jobInfoService.updateMemo(jobInfo, "【提升机】清除无效占用后成功占用，任务ID:" + currentTaskId);
                return true;
            }
            
            // 如果Redis中的任务ID和当前任务ID一致，说明是同一个任务，可以占用
            if (cachedTaskId.equals(currentTaskId)) {
                log.info("【提升机】当前任务已占用，任务ID: {}", currentTaskId);
                jobInfoService.updateMemo(jobInfo, "【提升机】当前任务已占用，任务ID:" + currentTaskId);
                return true;
            }
            
            // Redis中的任务ID和当前任务ID不一致，需要验证该任务是否还存在
            TaskInfo cachedTaskInfo = taskInfoService.findById(cachedTaskId);
            
            // 如果查不到任务，说明任务已不存在（可能已完成或已删除），可以占用
            if (cachedTaskInfo == null) {
                log.info("【提升机】Redis中的任务已不存在(ID: {})，清除后重新占用，当前任务ID: {}", 
                    cachedTaskId, currentTaskId);
                redisCache.setCacheObject(redisKey, currentTaskId.toString());
                jobInfoService.updateMemo(jobInfo, 
                    "【提升机】清除过期占用后成功占用，任务ID:" + currentTaskId);
                return true;
            }
            
            // 任务存在且不是当前任务，说明提升机被其他任务占用，不能占用
            log.info("【提升机】被其他任务占用，占用任务ID: {}，当前任务ID: {}，等待中", 
                cachedTaskId, currentTaskId);
            jobInfoService.updateMemo(jobInfo, 
                String.format("【提升机】被其他任务占用(任务ID:%d)，等待中", cachedTaskId));
            return false;
            
        } catch (Exception e) {
            log.error("【提升机】占用提升机时发生异常", e);
            jobInfoService.updateMemo(jobInfo, "【提升机】占用异常: " + e.getMessage());
            return false;
        }
    }


    /**
     * 检测提升机是否满足小车进入条件
     * 包括：楼层匹配、到位信号、空闲状态、小车/货物信号等
     * 
     * @param jobInfo 作业信息
     * @return true-满足进入条件, false-不满足进入条件
     */
    public Boolean checkAtTargetFloor(JobInfo jobInfo) {
        if (isTest) {
            return true;
        }
        
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
            
            // 5. 读取提升机当前楼层
            Short tsjCurrentFloor = (Short) readPlc("current_floor");
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
            
            // 7. 检查到位信号（必须到位才能进入）
            Short arrivalSignal = (Short) readPlc("arrival_signal");
            if (arrivalSignal == null || arrivalSignal != 1) {
                jobInfoService.updateMemo(jobInfo, 
                    String.format("【提升机】未到位，无法进入(到位信号:%s)", arrivalSignal));
                return false;
            }
            
            // 8. 检查空闲状态
            Short idleStatus = (Short) readPlc("idle_status");
            if (idleStatus == null || idleStatus != 1) {
                jobInfoService.updateMemo(jobInfo, 
                    String.format("【提升机】不在空闲状态，无法进入(空闲状态:%s)", idleStatus));
                return false;
            }
            
            // 9. 检查提升机内是否有小车（如果有小车则不能进入）
            Short cartSignal = (Short) readPlc("cart_signal");
            if (cartSignal != null && cartSignal == 1) {
                jobInfoService.updateMemo(jobInfo, "【提升机】内部已有小车，无法进入");
                return false;
            }
            
            // 10. 检查提升机内是否有货物（根据实际需求判断是否允许有货）
            Short cargoSignal = (Short) readPlc("cargo_signal");
//            if (cargoSignal != null && cargoSignal == 1) {
//                jobInfoService.updateMemo(jobInfo, "【提升机】内部已有货物，无法进入");
//                return false;
//            }
            
            // 11. 检查任务状态（不能有任务在执行）
            Short taskStatus = (Short) readPlc("task_status");
            if (taskStatus != null && taskStatus == 1) {
                jobInfoService.updateMemo(jobInfo, "【提升机】正在执行任务，无法进入");
                return false;
            }
            
            // 12. 检查运行状态
            Short runningStatus = (Short) readPlc("running_status");
            if (runningStatus != null && runningStatus == 1) {
                jobInfoService.updateMemo(jobInfo, "【提升机】正在运行中，无法进入");
                return false;
            }
            
            log.info("提升机进入条件检测全部通过 - 楼层: {}, 位置: {}, 到位信号: {}, 空闲: {}, 小车信号: {}, 货物信号: {}", 
                tsjCurrentFloor, cellCode, arrivalSignal, idleStatus, cartSignal, cargoSignal);
            jobInfoService.updateMemo(jobInfo, 
                String.format("【提升机】满足进入条件(楼层:%d，位置:%s)", carCurrentFloor, cellCode));
            return true;
            
        } catch (Exception e) {
            log.error("检测提升机进入条件时发生异常", e);
            jobInfoService.updateMemo(jobInfo, "【提升机】进入条件检测异常: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 是否可以调用提升机
     * 检查提升机状态是否满足调用条件
     * 
     * @param jobInfo 作业信息
     * @return true-可以调用, false-不可调用
     */
    public Boolean canCall(JobInfo jobInfo)   {
        if (isTest) {
            return true;
        }
        if(!allotTsjRedis(jobInfo)){
            return false;
        }

        DeviceInfo deviceInfo = deviceInfoService.findBy("code","TSJ");
        if (deviceInfo == null) {
            jobInfoService.updateMemo(jobInfo, "【提升机】设备信息不存在");
            return false;
        }
        if(deviceInfo.getIsOnline() == null || deviceInfo.getIsOnline() != 1){
            jobInfoService.updateMemo(jobInfo, "【提升机】不在线，无法调用");
            return false;
        }

        
        // 1. 检查心跳状态
        Short heartbeat = (Short) readPlc("heartbeat");
        if (heartbeat == null) {
            jobInfoService.updateMemo(jobInfo, "【提升机】通信异常，无法读取心跳");
            return false;
        }
        
        // 2. 检查联机状态
        Short onlineStatus = (Short) readPlc("online_status");
        if (onlineStatus == null || onlineStatus != 1) {
            jobInfoService.updateMemo(jobInfo, "【提升机】内部不是联机状态，无法调用");
            return false;
        }
        
        // 3. 检查空闲状态
        Short idleStatus = (Short) readPlc("idle_status");
        if (idleStatus == null || idleStatus != 1) {
            jobInfoService.updateMemo(jobInfo, "【提升机】不在空闲状态，无法调用");
            return false;
        }
        
        // 4. 检查任务状态
        Short taskStatus = (Short) readPlc("task_status");
        if (taskStatus != null && taskStatus == 1) {
            jobInfoService.updateMemo(jobInfo, "【提升机】正在执行任务中，无法调用");
            return false;
        }
        
        // 5. 检查报警代码
        Short alarmCode = (Short) readPlc("alarm_code");
        if (alarmCode != null && alarmCode != 0) {
            String alarmDesc = getAlarmDescription(alarmCode);
            jobInfoService.updateMemo(jobInfo, "【提升机】存在报警(代码:" + alarmCode + " - " + alarmDesc + ")，无法调用");
            return false;
        }

//        Short resistanceBlock = (Short) readPlc("resistance_block_signal");
//        if (resistanceBlock == null || resistanceBlock != 1) {
//            jobInfoService.updateMemo(jobInfo, "【提升机】电阻挡未升起，无法执行任务");
//            return false;
//        }
        
        log.info("提升机状态检查通过，可以调用");
       // jobInfoService.updateMemo(jobInfo, "【提升机】状态检查通过，可以调用");
        return true;
    }

    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private AutoService autoService;
    
    /**
     * 请求进入调用
     * 小车请求进入提升机
     *
     *
     * 货物信号	Int
     * 动作类型	Int
     * 目标楼层	Int
     * 托盘类型	Int
     *
     * @param jobInfo 作业信息
     * @return true-请求成功, false-请求失败
     */
    public Boolean requestEnter(JobInfo jobInfo)   {
        short hasCart=0;
        short hasCargo=0;
        short palletType=0;
        short targetFloor;

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if(!allotTsjRedis(jobInfo)){
            return false;
        }

        String cellCode = jobInfo.getToCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】请求进入调用 目标层不存在");
            return false;
        }

        CellInfo cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode);
        if(cellInfo==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】请求进入调用 层不存在");
            return false;
        }
        targetFloor =Short.parseShort(cellInfo.getZ().toString());

        if (isTest) {
            return true;
        }
        
        // 先检查是否可以调用
        if (!canCall(jobInfo)) {
            return false;
        }
        

        
        // 检查电阻挡信号是否已升起（执行升降任务前必须校验）
//        Short resistanceBlock = (Short) readPlc("resistance_block_signal");
//        if (resistanceBlock == null || resistanceBlock != 1) {
//            jobInfoService.updateMemo(jobInfo, "【提升机】电阻挡未升起，无法执行任务");
//            return false;
//        }
//
        // 生成任务号


        Integer taskId = generateTaskId(jobInfo);
        
        // 写入任务参数
        writePlc("task_id_write", taskId);
        writePlc("cart_signal_write", hasCart);
        writePlc("cargo_signal_write", hasCargo);
        writePlc("action_type_write", (short) 101); // 101-小车进入
        writePlc("target_floor_write", targetFloor);
        writePlc("pallet_type_write", palletType); // 1-正常托盘
        
        // 等待PLC接收
        try {
            Thread.sleep(50); // 每500ms检查一次
        }catch (Exception ex){

        }

        // 计算并写入CRC校验码
        Integer crc = calculateCRC(taskId, hasCart, hasCargo, (short) 101, targetFloor, palletType);
        writePlc("crc_checksum", crc);

        try {
            Thread.sleep(50); // 每500ms检查一次
        }catch (Exception ex){

        }

        // 等待PLC接收任务反馈（循环等待）
        Integer receivedTaskId = (Integer) readPlc("receive_task_feedback");

        if (receivedTaskId != null && receivedTaskId.equals(taskId)) {
            log.info("提升机已接收进入请求，任务号: {}", taskId);
            jobInfo.setTaskNo(taskId.toString());
            jobInfoService.update(jobInfo);
            jobInfoService.updateMemo(jobInfo, "【提升机】小车请求进入成功，任务号:" + taskId);
            return true;
        } else {
            jobInfoService.updateMemo(jobInfo, "【提升机】未接收到任务确认");
            return false;
        }
    }


    /**
     * 请求进入调用
     * 小车请求进入提升机
     *
     *
     * 货物信号	Int
     * 动作类型	Int
     * 目标楼层	Int
     * 托盘类型	Int
     *
     * @param jobInfo 作业信息
     * @return true-请求成功, false-请求失败
     */
    public Boolean carRequestEnter(JobInfo jobInfo)   {
        short hasCart=0;
        short hasCargo=0;
        short palletType=0;
        short targetFloor;

        // 1. 获取任务信息
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "【提升机】请求进入调用 任务信息不存在");
            return false;
        }

        if(!allotTsjRedis(jobInfo)){
            return false;
        }

        // 2. 获取小车信息
        if (jobInfo.getRcsCarId() == null) {
            jobInfoService.updateMemo(jobInfo, "【提升机】请求进入调用 小车ID不存在");
            return false;
        }
        
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
        targetFloor = Short.parseShort(currentCellInfo.getZ().toString());
        
        log.info("【提升机】小车[{}]呼叫提升机，小车当前位置: {}，楼层: {}，目标位置: {}", 
                carInfo.getCode(), currentCellCode, targetFloor, jobInfo.getToCellCode());

        if (isTest) {
            return true;
        }

        // 先检查是否可以调用
        if (!canCall(jobInfo)) {
            return false;
        }


        // 生成任务号


        Integer taskId = generateTaskId(jobInfo);

        // 写入任务参数
        writePlc("task_id_write", taskId);
        writePlc("cart_signal_write", hasCart);
        writePlc("cargo_signal_write", hasCargo);
        writePlc("action_type_write", (short) 101); // 101-小车进入
        writePlc("target_floor_write", targetFloor);
        writePlc("pallet_type_write", palletType); // 1-正常托盘

        // 等待PLC接收
        try {
            Thread.sleep(50); // 每500ms检查一次
        }catch (Exception ex){

        }

        // 计算并写入CRC校验码
        Integer crc = calculateCRC(taskId, hasCart, hasCargo, (short) 101, targetFloor, palletType);
        writePlc("crc_checksum", crc);

        try {
            Thread.sleep(50); // 每500ms检查一次
        }catch (Exception ex){

        }

        // 等待PLC接收任务反馈（循环等待）
        Integer receivedTaskId = (Integer) readPlc("receive_task_feedback");

        if (receivedTaskId != null && receivedTaskId.equals(taskId)) {
            log.info("提升机已接收进入请求，任务号: {}", taskId);
            jobInfo.setTaskNo(taskId.toString());
            jobInfoService.update(jobInfo);
            jobInfoService.updateMemo(jobInfo, "【提升机】小车请求进入成功，任务号:" + taskId);
            return true;
        } else {
            jobInfoService.updateMemo(jobInfo, "【提升机】未接收到任务确认");
            return false;
        }
    }
    
    /**
     * 走出调用
     * 小车从提升机走出
     * 
     * @param jobInfo 作业信息
     * @return true-走出成功, false-走出失败
     */
    public Boolean requestLeave(JobInfo jobInfo  )  {
        short hasCart=1;
        short hasCargo=0;
        short palletType=0;


        short targetFloor;

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());

        if(!allotTsjRedis(jobInfo)){
            return false;
        }

        String cellCode = jobInfo.getToCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】走出时 目标层不存在");
            return false;
        }
        CellInfo cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode);
        if(cellInfo==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】走出时 目标层不存在");
            return false;
        }
        targetFloor =Short.parseShort(cellInfo.getZ().toString());

        if (isTest) {
            return true;
        }

        // 检查是否到位
        Short arrivalSignal = (Short) readPlc("arrival_signal");
        if (arrivalSignal == null || arrivalSignal != 1) {
            jobInfoService.updateMemo(jobInfo, "【提升机】未到达目标楼层，无法走出");
            return false;
        }
        
        // 检查当前楼层
        Short currentFloor = (Short) readPlc("current_floor");


        Short cart_signal = (Short) readPlc("cart_signal");

        if(cart_signal<0.1){
            jobInfoService.updateMemo(jobInfo, "【提升机】内部无小车，无法下发提升机离开任务，请检查");
            return false;
        }

        
        // 检查电阻挡信号是否已升起（执行升降任务前必须校验）
//        Short resistanceBlock = (Short) readPlc("resistance_block_signal");
//        if (resistanceBlock == null || resistanceBlock != 1) {
//            jobInfoService.updateMemo(jobInfo, "【提升机】电阻挡未升起，无法执行任务");
//            return false;
//        }


        
        // 生成新的任务号
        Integer taskId = generateTaskId(jobInfo);
        
        // 写入完整的任务参数（参考图片示例：任务2）
        writePlc("task_id_write", taskId);
        writePlc("cart_signal_write", hasCart );
        writePlc("cargo_signal_write", hasCargo );
        writePlc("action_type_write", (short) 102); // 102-小车离开
        writePlc("target_floor_write", targetFloor);
        writePlc("pallet_type_write", palletType); // 1-正常托盘

        try {
            Thread.sleep(50); // 每500ms检查一次
        }catch (Exception ex){

        }

        // 计算并写入CRC校验码
        Integer crc = calculateCRC(taskId, hasCart, hasCargo, (short) 102, targetFloor, palletType);
        writePlc("crc_checksum", crc);

        try {
            Thread.sleep(50); // 每500ms检查一次
        }catch (Exception ex){

        }



        // 等待PLC接收任务反馈（循环等待）
        Integer receivedTaskId = (Integer) readPlc("receive_task_feedback");
        if (receivedTaskId != null && receivedTaskId.equals(taskId)) {
            log.info("提升机已接收走出请求，任务号: {}", taskId);
            jobInfo.setTaskNo(taskId.toString());
            jobInfoService.update(jobInfo);
            jobInfoService.updateMemo(jobInfo, "【提升机】已接收走出请求，任务号:" + taskId);
            return true;
        } else {
            jobInfoService.updateMemo(jobInfo, "【提升机】未接收到任务确认");
            return false;
        }
    }
    
    /**
     * 释放调用
     * 释放提升机，任务完成后调用
     * 
     * @param jobInfo 作业信息
     * @return true-释放成功, false-释放失败
     */
    public Boolean release(JobInfo jobInfo )   {
        short hasCart=0;
        short hasCargo=0;
        short palletType=0;
        short targetFloor;

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());

        String cellCode = jobInfo.getToCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】当前层不存在");
            return false;
        }
        CellInfo cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode);
        if(cellInfo==null){
            jobInfoService.updateMemo(jobInfo, "【提升机】当前层不存在");
            return false;
        }
        targetFloor =Short.parseShort(cellInfo.getZ().toString());

        if (isTest) {
            return true;
        }

        // 生成新的任务号用于释放（参考图片示例：任务3）
        Integer taskId = generateTaskId(jobInfo);
        
        // 写入释放指令（参考图片：需要完整的任务参数）
        writePlc("task_id_write", taskId);
        writePlc("cart_signal_write", hasCart);
        writePlc("cargo_signal_write", hasCargo);
        writePlc("action_type_write", (short) 99); // 99-释放
        writePlc("target_floor_write", targetFloor );
        writePlc("pallet_type_write", palletType);

        try {
            Thread.sleep(50); // 每500ms检查一次
        }catch (Exception ex){

        }

        // 计算并写入CRC校验码
        Integer crc = calculateCRC(taskId, hasCart, hasCargo, (short) 99, targetFloor, palletType);
        writePlc("crc_checksum", crc);

        try {
            Thread.sleep(50); // 每500ms检查一次
        }catch (Exception ex){

        }

        // 等待PLC接收释放指令
        Integer receivedTaskId = (Integer) readPlc("receive_task_feedback");
        if (receivedTaskId != null && receivedTaskId.equals(taskId)) {
            log.info("提升机已接收释放指令，任务号: {}", taskId);
            jobInfoService.updateMemo(jobInfo, "【提升机】释放成功，任务号:" + taskId);
            
            // 等待任务状态变为已完成（2）
            for (int i = 0; i < 10; i++) {
                Short taskStatus = (Short) readPlc("task_status");
                if (taskStatus != null && taskStatus == 2) {
                    log.info("提升机释放任务已完成");
                    break;
                }
                try {
                    Thread.sleep(50); // 每500ms检查一次
                }catch (Exception ex){

                }
            }
            
            return true;
        } else {
            jobInfoService.updateMemo(jobInfo, "【提升机】未接收到释放确认");
            return false;
        }
    }
    
    /**
     * 检查任务是否完成
     * 
     * @param jobInfo 作业信息
     * @return true-已完成, false-未完成
     */
    public Boolean isTaskComplete(JobInfo jobInfo) {
        if (isTest) {
            return true;
        }

        Short alarmCode = (Short) readPlc("alarm_code");
        if (alarmCode != null && alarmCode != 0) {
            String alarmDesc = getAlarmDescription(alarmCode);
            jobInfoService.updateMemo(jobInfo, "【提升机】存在报警(代码:" + alarmCode + " - " + alarmDesc + ")");
            return false;
        }
        
        // 读取当前任务号
        Integer currentTaskNo = (Integer) readPlc("task_number");
        String expectedTaskNo = jobInfo.getTaskNo();
        
        if (currentTaskNo != null && expectedTaskNo != null 
            && currentTaskNo.toString().equals(expectedTaskNo)) {
            
            // 检查任务状态
            Short taskStatus = (Short) readPlc("task_status");
            
            if (taskStatus == null) {
                return false;
            }
            
            // 0-未完成, 1-执行中, 2-已完成
            switch (taskStatus) {
                case 0:
                    jobInfoService.updateMemo(jobInfo, "【提升机】任务未开始");
                    return false;
                case 1:
                    jobInfoService.updateMemo(jobInfo, "【提升机】任务执行中");
                    return false;
                case 2:
                    log.info("提升机任务完成，任务号: {}", currentTaskNo);
                    jobInfoService.updateMemo(jobInfo, "【提升机】任务完成");
                    return true;
                default:
                    jobInfoService.updateMemo(jobInfo, "【提升机】未知任务状态: " + taskStatus);
                    return false;
            }
        }
        
        return false;
    }
    
    /**
     * 获取报警代码对应的文字描述
     * 
     * @param alarmCode 报警代码
     * @return 报警描述
     */
    public String getAlarmDescription(Short alarmCode) {
        if (alarmCode == null || alarmCode == 0) {
            return "无故障";
        }
        
        switch (alarmCode) {
            case 1: return "链条机过流";
            case 2: return "输送超时";
            case 3: return "上位数据错误";
            case 4: return "提升超时";
            case 5: return "位置偏差过大";
            case 6: return "扭矩偏差过大";
            case 7: return "轴1超扭矩报警";
            case 8: return "轴2超扭矩报警";
            case 9: return "轴3超扭矩报警";
            case 10: return "轴4超扭矩报警";
            case 11: return "层数据错误";
            case 12: return "1角原点异常";
            case 13: return "2角原点异常";
            case 14: return "3角原点异常";
            case 15: return "4角原点异常";
            case 16: return "自动寻原点异常";
            case 17: return "提升上硬限位";
            case 18: return "提升下硬限位";
            case 19: return "提升上软限位";
            case 20: return "提升下软限位";
            case 21: return "提升车前超限";
            case 22: return "提升车后超限";
            case 23: return "提升货前超限";
            case 24: return "提升货后超限";
            case 25: return "输送线超限";
            case 26: return "与输送线心跳超时";
            case 27: return "轴1报警,驱动器报警";
            case 28: return "轴2报警,驱动器报警";
            case 29: return "轴3报警,驱动器报警";
            case 30: return "轴4报警,驱动器报警";
            case 31: return "送货-平台无货";
            case 32: return "接货-平台有货,或者有车";
            case 33: return "前电动阻挡运行超时";
            case 34: return "后电动阻挡运行超时";
            case 35: case 36: case 37: case 38: case 39: case 40: case 41: case 42: case 43: case 44:
                return (alarmCode - 34) + "层货架前阻挡打开超时或前阻挡状态不对";
            case 45: case 46: case 47: case 48: case 49: case 50: case 51: case 52: case 53: case 54:
                return (alarmCode - 44) + "层货架后阻挡打开超时或后阻挡状态不对";
            case 55: return "提升机关闭货架电动阻挡超时";
            case 56: return "前电动阻挡状态不对";
            case 57: return "后电动阻挡状态不对";
            case 58: return "上位急停";
            case 59: return "取车时平台有车";
            case 60: return "放车时平台无车";
            case 61: return "上位小车信号错误";
            case 62: return "输送线任务错误";
            default: return "未知故障代码: " + alarmCode;
        }
    }
    
    /**
     * 获取提升机当前状态信息
     * 
     * @return 状态信息字符串
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("提升机状态: ");
        sb.append("\n心跳: ").append(readPlc("heartbeat"));
        sb.append("\n联机状态: ").append(readPlc("online_status"));
        sb.append("\n空闲状态: ").append(readPlc("idle_status"));
        sb.append("\n当前楼层: ").append(readPlc("current_floor"));
        sb.append("\n任务状态: ").append(readPlc("task_status"));
        sb.append("\n小车信号: ").append(readPlc("cart_signal"));
        sb.append("\n货物信号: ").append(readPlc("cargo_signal"));
        sb.append("\n运行状态: ").append(readPlc("running_status"));
        
        Short alarmCode = (Short) readPlc("alarm_code");
        if (alarmCode != null && alarmCode != 0) {
            sb.append("\n报警代码: ").append(alarmCode).append(" - ").append(getAlarmDescription(alarmCode));
        } else {
            sb.append("\n报警代码: ").append(alarmCode);
        }
        
        return sb.toString();
    }
    
    /**
     * 等待PLC接收任务反馈
     * 循环等待PLC的receive_task_feedback与发送的taskId匹配
     * 
     * @param taskId 发送的任务号
     * @param maxRetries 最大重试次数
     * @return 接收到的任务号，如果超时则返回null
     */
    private Integer waitForTaskFeedback(Integer taskId, int maxRetries)  {
        for (int i = 0; i < maxRetries; i++) {
            Integer receivedTaskId = (Integer) readPlc("receive_task_feedback");

            try {
                Thread.sleep(50); // 每500ms检查一次
            }catch (Exception ex){

            }
        }
        log.warn("等待PLC接收任务反馈超时，任务号: {}", taskId);
        return null;
    }
    
    /**
     * 生成任务号
     * 简单递增方式，实际项目中可以使用更复杂的生成策略
     */
    private synchronized Integer generateTaskId(JobInfo jobInfo) {
//        if(jobInfo.getTaskNo()!=null){
//            return Integer.valueOf(jobInfo.getTaskNo());
//        }
        Integer taskId = Integer.valueOf(autoService.getTodayTaskNo());
        jobInfo.setTaskNo(taskId.toString());
        jobInfoService.update(jobInfo);
        return taskId;
    }
    
    /**
     * 计算CRC校验码 (CRC-16/MODBUS)
     * 标准CRC-16/MODBUS算法实现
     * 多项式: 0x8005 (反向表示为 0xA001)
     * 初始值: 0xFFFF
     * 
     * 注意字节顺序：
     * - DInt (任务号): 低字节在前（小端序）
     * - INT (其他字段): 高字节在前（大端序）
     * 
     * @param taskId 任务号
     * @param cart 小车信号
     * @param cargo 货物信号
     * @param action 动作类型
     * @param floor 目标楼层
     * @param palletType 托盘类型
     * @return CRC校验码 (范围0-65535，对应PLC的WORD类型，使用Integer存储)
     */
    private static Integer calculateCRC(Integer taskId, short cart, short cargo,
                                  short action, short floor, short palletType) {
        // 构建数据字节数组
        // 任务号(4) + 小车信号(2) + 货物信号(2) + 动作类型(2) + 目标楼层(2) + 托盘类型(2) = 14字节
        byte[] data = new byte[14];
        
        // 任务号 (DInt, 4字节) - 低字节在前（小端序）
        data[0] = (byte) ((taskId >> 8) & 0xFF);    // 第二字节
        data[1] = (byte) (taskId & 0xFF);           // 最低字节
        data[2] = (byte) ((taskId >> 24) & 0xFF);   // 最高字节
        data[3] = (byte) ((taskId >> 16) & 0xFF);   // 第三字节
        
        // 小车信号 (Int, 2字节) - 高字节在前
        data[4] = (byte) ((cart >> 8) & 0xFF);      // 高字节
        data[5] = (byte) (cart & 0xFF);             // 低字节
        
        // 货物信号 (Int, 2字节) - 高字节在前
        data[6] = (byte) ((cargo >> 8) & 0xFF);     // 高字节
        data[7] = (byte) (cargo & 0xFF);            // 低字节
        
        // 动作类型 (Int, 2字节) - 高字节在前
        data[8] = (byte) ((action >> 8) & 0xFF);    // 高字节
        data[9] = (byte) (action & 0xFF);           // 低字节
        
        // 目标楼层 (Int, 2字节) - 高字节在前
        data[10] = (byte) ((floor >> 8) & 0xFF);    // 高字节
        data[11] = (byte) (floor & 0xFF);           // 低字节
        
        // 托盘类型 (Int, 2字节) - 高字节在前
        data[12] = (byte) ((palletType >> 8) & 0xFF); // 高字节
        data[13] = (byte) (palletType & 0xFF);        // 低字节
        
        // 计算CRC-16/MODBUS
        return crc16Modbus(data);
    }

    public static void main(String[] args) {
        // 测试数据
        Integer taskId = 2;
        short cart = 0;
        short cargo = 0;
        short action = 101;
        short floor = 2;
        short palletType = 0;

        System.out.println("测试数据:");
        System.out.println("任务号: " + taskId);
        System.out.println("小车信号: " + cart);
        System.out.println("货物信号: " + cargo);
        System.out.println("动作类型: " + action);
        System.out.println("目标楼层: " + floor);
        System.out.println("托盘类型: " + palletType);
        System.out.println();

        byte[] data = new byte[14];

        // 任务号 (DInt, 4字节) - 低字节在前（小端序）

        data[0] = (byte) ((taskId >> 8) & 0xFF);    // 第二字节
        data[1] = (byte) (taskId & 0xFF);           // 最低字节
        data[2] = (byte) ((taskId >> 24) & 0xFF);   // 最高字节
        data[3] = (byte) ((taskId >> 16) & 0xFF);   // 第三字节

        // 小车信号 (Int, 2字节) - 高字节在前
        data[4] = (byte) ((cart >> 8) & 0xFF);      // 高字节
        data[5] = (byte) (cart & 0xFF);             // 低字节

        // 货物信号 (Int, 2字节) - 高字节在前
        data[6] = (byte) ((cargo >> 8) & 0xFF);     // 高字节
        data[7] = (byte) (cargo & 0xFF);            // 低字节

        // 动作类型 (Int, 2字节) - 高字节在前
        data[8] = (byte) ((action >> 8) & 0xFF);    // 高字节
        data[9] = (byte) (action & 0xFF);           // 低字节

        // 目标楼层 (Int, 2字节) - 高字节在前
        data[10] = (byte) ((floor >> 8) & 0xFF);    // 高字节
        data[11] = (byte) (floor & 0xFF);           // 低字节

        // 托盘类型 (Int, 2字节) - 高字节在前
        data[12] = (byte) ((palletType >> 8) & 0xFF); // 高字节
        data[13] = (byte) (palletType & 0xFF);        // 低字节
        
        System.out.println("大端序14字节数组:");
        System.out.print("十六进制: ");
        for (int i = 0; i < data.length; i++) {
            System.out.printf("%02X ", data[i] & 0xFF);
        }
        System.out.println();
        
        // 计算CRC（大端序）
        Integer crc1 = crc16Modbus(data);
        System.out.println("大端序CRC: " + String.format("%04X", crc1) + " - " + (crc1 == 0x807E ? "✓" : "✗"));
        System.out.println();
        
        // 测试小端序（低字节在前）
        byte[] data2 = new byte[14];
        data2[0] = (byte) (taskId & 0xFF);
        data2[1] = (byte) ((taskId >> 8) & 0xFF);
        data2[2] = (byte) ((taskId >> 16) & 0xFF);
        data2[3] = (byte) ((taskId >> 24) & 0xFF);
        data2[4] = (byte) (cart & 0xFF);
        data2[5] = (byte) ((cart >> 8) & 0xFF);
        data2[6] = (byte) (cargo & 0xFF);
        data2[7] = (byte) ((cargo >> 8) & 0xFF);
        data2[8] = (byte) (action & 0xFF);
        data2[9] = (byte) ((action >> 8) & 0xFF);
        data2[10] = (byte) (floor & 0xFF);
        data2[11] = (byte) ((floor >> 8) & 0xFF);
        data2[12] = (byte) (palletType & 0xFF);
        data2[13] = (byte) ((palletType >> 8) & 0xFF);
        
        System.out.println("小端序14字节数组:");
        System.out.print("十六进制: ");
        for (int i = 0; i < data2.length; i++) {
            System.out.printf("%02X ", data2[i] & 0xFF);
        }
        System.out.println();
        
        Integer crc2 = crc16Modbus(data2);
        System.out.println("小端序CRC: " + String.format("%04X", crc2) + " - " + (crc2 == 0x807E ? "✓" : "✗"));
        System.out.println();
        
        System.out.println("期望CRC: 807E");
    }
    
    /**
     * CRC-16/MODBUS 标准算法实现
     * 参考标准C实现，与Modbus协议完全一致
     * 多项式: 0x8005 (反向表示为 0xA001)
     * 初始值: 0xFFFF
     * 
     * 注意：MODBUS标准要求对结果进行高低字节交换
     * 
     * @param data 待校验的字节数组
     * @return CRC-16校验码 (范围: 0-65535)
     * 
     * 注意：返回值为int类型，但实际只有低16位有效(0-65535)，对应PLC的WORD类型
     * Java没有无符号类型，使用int来存储0-65535范围内的值是标准的做法
     */
    private static int crc16Modbus(byte[] data) {
        int crc = 0xFFFF; // 初始值
        
        // 外层循环：处理每个字节
        for (byte b : data) {
            crc ^= (b & 0xFF); // 将字节与CRC的低8位异或
            
            // 内层循环：处理每个字节的8位
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0) {
                    // 最低位为1：右移一位后与0xA001异或
                    crc = (crc >> 1) ^ 0xA001;
                } else {
                    // 最低位为0：仅右移一位
                    crc = crc >> 1;
                }
            }
        }


        return crc;
        // MODBUS标准要求：高低字节交换
        // 原始算法：return (CRC16 >> 8) | (CRC16 << 8)
        // 先右移8位得到低字节到高字节位置，再左移8位得到高字节到低字节位置，然后或运算合并
        //return ((crc >> 8) & 0xFF) | ((crc << 8) & 0xFF00);
    }

}
