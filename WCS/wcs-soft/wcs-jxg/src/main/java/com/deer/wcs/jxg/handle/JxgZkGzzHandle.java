package com.deer.wcs.jxg.handle;

import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.service.JobInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 工作站(GZZ)控制器
 * 负责工作站的入库、出库、复位等操作
 */
@Component("JxgZkGzzHandle")
public class JxgZkGzzHandle {
    
    private static final Logger log = LoggerFactory.getLogger(JxgZkGzzHandle.class);
    
    @Autowired
    private DeviceValueService deviceValueService;
    
    @Autowired
    private JobInfoService jobInfoService;
    
    @Value("${isTest}")
    private Boolean isTest;
    
    // 设备编码
    private static final String DEVICE_CODE = "gzz";
    
    // 任务类型常量
    public static final int TASK_TYPE_INIT = 0;           // 初值
    public static final int TASK_TYPE_INBOUND = 1;        // 入库
    public static final int TASK_TYPE_OUTBOUND = 2;       // 出库
    public static final int TASK_TYPE_INBOUND_COMPLETE = 97;   // 货物入库完成
    public static final int TASK_TYPE_OUTBOUND_COMPLETE = 98;  // 货物出库完成
    public static final int TASK_TYPE_SIZE_ERROR = 100;   // 托盘尺寸不符退回
    public static final int TASK_TYPE_BARCODE_ERROR = 101; // 条码信息不符退回
    public static final int TASK_TYPE_SYSTEM_RETURN = 102; // 系统退回
    
    // 任务执行状态常量
    public static final int STATUS_IDLE = 0;              // 空闲无任务
    public static final int STATUS_INBOUND_PROCESSING = 1; // 入库任务执行中
    public static final int STATUS_OUTBOUND_PROCESSING = 2; // 出库任务执行中
    public static final int STATUS_COMPLETED = 3;         // 任务已完成
    public static final int STATUS_CRC_FAILED = 4;        // CRC校验失败
    
    // 故障状态常量
    public static final int FAULT_NONE = 0;               // 无故障
    public static final int FAULT_DETECTION = 1;          // 检测门故障
    public static final int FAULT_MACHINE = 2;            // 单机故障/CRC故障
    
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
    
    /**
     * 检查是否有入库申请
     * 
     * @param jobInfo 作业信息
     * @return true-有申请, false-无申请
     */
    public Boolean hasInboundApplication(JobInfo jobInfo) throws Exception {
        if (isTest) {
            return true;
        }
        
        // 检查申请信号
        Short applySignal = (Short) readPlc("apply_signal");
        if (applySignal == null || applySignal != 1) {
            jobInfoService.updateMemo(jobInfo, "【工作站】无入库申请信号");
            return false;
        }
        
        // 检查载货状态
        Short loadStatus = (Short) readPlc("load_status");
        if (loadStatus == null || loadStatus != 1) {
            jobInfoService.updateMemo(jobInfo, "【工作站】无货物");
            return false;
        }
        
        // 检查任务执行状态（必须是空闲）
        Short taskStatus = (Short) readPlc("task_status");
        if (taskStatus != null && taskStatus != STATUS_IDLE) {
            jobInfoService.updateMemo(jobInfo, "【工作站】正在执行任务，无法接收新任务");
            return false;
        }
        
        // 检查故障状态
        Short faultStatus = (Short) readPlc("fault_status");
        if (faultStatus != null && faultStatus != FAULT_NONE) {
            Short faultCode = (Short) readPlc("fault_code");
            jobInfoService.updateMemo(jobInfo, "【工作站】存在故障，状态:" + faultStatus + ", 代码:" + faultCode);
            return false;
        }
        
        log.info("工作站有入库申请");
        jobInfoService.updateMemo(jobInfo, "【工作站】检测到入库申请");
        return true;
    }
    
    /**
     * 检查托盘信息是否合格
     * 读取托盘尺寸、重量、条码等信息并进行WCS校验
     * 
     * @param jobInfo 作业信息
     * @return true-合格, false-不合格
     */
    public Boolean validatePalletInfo(JobInfo jobInfo) throws Exception {
        if (isTest) {
            return true;
        }
        
        // 读取托盘尺寸
        Short palletLength = (Short) readPlc("pallet_length");
        Short palletWidth = (Short) readPlc("pallet_width");
        Short palletHeight = (Short) readPlc("pallet_height");
        
        // 读取货物重量
        Short cargoWeight = (Short) readPlc("cargo_weight");
        
        // 读取条码信息
        String barcodeInfo = (String) readPlc("barcode_info");
        
        log.info("托盘信息 - 长:{}mm, 宽:{}mm, 高:{}mm, 重量:{}kg, 条码:{}", 
                 palletLength, palletWidth, palletHeight, cargoWeight, barcodeInfo);
        
        // TODO: 这里添加实际的WCS校验逻辑
        // 例如：检查尺寸是否在允许范围内、条码格式是否正确等
        
        // 示例校验：检查是否读取到有效数据
        if (palletLength == null || palletLength == 0) {
            jobInfoService.updateMemo(jobInfo, "【工作站】托盘长度数据无效");
            return false;
        }
        
        if (barcodeInfo == null || barcodeInfo.trim().isEmpty() || barcodeInfo.equals("0")) {
            jobInfoService.updateMemo(jobInfo, "【工作站】条码信息读取失败");
            return false;
        }
        
        jobInfoService.updateMemo(jobInfo, "【工作站】托盘信息校验通过 - 条码:" + barcodeInfo);
        return true;
    }
    
    /**
     * 下发入库任务
     * 
     * @param jobInfo 作业信息
     * @param taskId 任务号
     * @return true-下发成功, false-下发失败
     */
    public Boolean sendInboundTask(JobInfo jobInfo, Integer taskId) throws Exception {
        if (isTest) {
            return true;
        }
        
        // 写入任务参数
        writePlc("task_id_write", taskId.shortValue());
        writePlc("task_type_write", (short) TASK_TYPE_INBOUND); // 1-入库
        writePlc("reset_signal", (short) 0);
        
        Thread.sleep(100);
        
        // 计算并写入CRC校验码（对任务号和任务类型）
        Integer crc = calculateCRC(taskId, TASK_TYPE_INBOUND);
        writePlc("crc_checksum", crc);
        
        Thread.sleep(100);
        
        // 等待PLC反馈任务号
        Short feedbackTaskId = waitForTaskFeedback(taskId.shortValue(), 10);
        if (feedbackTaskId != null && feedbackTaskId.equals(taskId.shortValue())) {
            jobInfo.setTaskNo(taskId.toString());
            jobInfoService.update(jobInfo);
            jobInfoService.updateMemo(jobInfo, "【工作站】入库任务下发成功，任务号:" + taskId);
            return true;
        } else {
            jobInfoService.updateMemo(jobInfo, "【工作站】未收到任务确认");
            return false;
        }
    }
    
    /**
     * 下发出库任务
     * 
     * @param jobInfo 作业信息
     * @param taskId 任务号
     * @return true-下发成功, false-下发失败
     */
    public Boolean sendOutboundTask(JobInfo jobInfo, Integer taskId) throws Exception {
        if (isTest) {
            return true;
        }
        
        // 检查载货状态（出库时必须无货）
        Short loadStatus = (Short) readPlc("load_status");
        if (loadStatus != null && loadStatus != 0) {
            jobInfoService.updateMemo(jobInfo, "【工作站】有货物，无法执行出库任务");
            return false;
        }
        
        // 检查任务执行状态
        Short taskStatus = (Short) readPlc("task_status");
        if (taskStatus != null && taskStatus != STATUS_IDLE) {
            jobInfoService.updateMemo(jobInfo, "【工作站】正在执行任务，无法下发出库任务");
            return false;
        }
        
        // 写入出库任务参数
        writePlc("task_id_write", taskId.shortValue());
        writePlc("task_type_write", (short) TASK_TYPE_OUTBOUND); // 2-出库
        writePlc("reset_signal", (short) 0);
        
        Thread.sleep(100);
        
        // 计算并写入CRC校验码
        Integer crc = calculateCRC(taskId, TASK_TYPE_OUTBOUND);
        writePlc("crc_checksum", crc);
        
        Thread.sleep(100);
        
        // 等待PLC反馈
        Short feedbackTaskId = waitForTaskFeedback(taskId.shortValue(), 10);
        if (feedbackTaskId != null && feedbackTaskId.equals(taskId.shortValue())) {
            jobInfo.setTaskNo(taskId.toString());
            jobInfoService.update(jobInfo);
            jobInfoService.updateMemo(jobInfo, "【工作站】出库任务下发成功，任务号:" + taskId);
            return true;
        } else {
            jobInfoService.updateMemo(jobInfo, "【工作站】未收到出库任务确认");
            return false;
        }
    }
    
    /**
     * 入库完成确认
     * 
     * @param jobInfo 作业信息
     * @return true-确认成功, false-确认失败
     */
    public Boolean confirmInboundComplete(JobInfo jobInfo) throws Exception {
        if (isTest) {
            return true;
        }
        
        String taskNoStr = jobInfo.getTaskNo();
        if (taskNoStr == null || taskNoStr.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "【工作站】任务号为空，无法确认完成");
            return false;
        }
        
        Integer taskId = Integer.parseInt(taskNoStr);
        
        // 写入入库完成指令
        writePlc("task_id_write", taskId.shortValue());
        writePlc("task_type_write", (short) TASK_TYPE_INBOUND_COMPLETE); // 97-入库完成
        writePlc("reset_signal", (short) 0);
        
        Thread.sleep(100);
        
        // 计算并写入CRC校验码
        Integer crc = calculateCRC(taskId, TASK_TYPE_INBOUND_COMPLETE);
        writePlc("crc_checksum", crc);
        
        Thread.sleep(100);
        
        // 等待PLC反馈任务完成状态
        for (int i = 0; i < 10; i++) {
            Short feedbackTaskId = (Short) readPlc("feedback_task_id");
            Short taskStatus = (Short) readPlc("task_status");
            
            if (feedbackTaskId != null && feedbackTaskId.equals(taskId.shortValue())
                && taskStatus != null && taskStatus == STATUS_COMPLETED) {
                log.info("工作站入库任务已完成，任务号: {}", taskId);
                jobInfoService.updateMemo(jobInfo, "【工作站】入库完成确认成功");
                return true;
            }
            Thread.sleep(50);
        }
        
        jobInfoService.updateMemo(jobInfo, "【工作站】入库完成确认超时");
        return false;
    }
    
    /**
     * 出库完成确认
     * 
     * @param jobInfo 作业信息
     * @return true-确认成功, false-确认失败
     */
    public Boolean confirmOutboundComplete(JobInfo jobInfo) throws Exception {
        if (isTest) {
            return true;
        }
        
        String taskNoStr = jobInfo.getTaskNo();
        if (taskNoStr == null || taskNoStr.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "【工作站】任务号为空，无法确认完成");
            return false;
        }
        
        Integer taskId = Integer.parseInt(taskNoStr);
        
        // 写入出库完成指令
        writePlc("task_id_write", taskId.shortValue());
        writePlc("task_type_write", (short) TASK_TYPE_OUTBOUND_COMPLETE); // 98-出库完成
        writePlc("reset_signal", (short) 0);
        
        Thread.sleep(100);
        
        // 计算并写入CRC校验码
        Integer crc = calculateCRC(taskId, TASK_TYPE_OUTBOUND_COMPLETE);
        writePlc("crc_checksum", crc);
        
        Thread.sleep(100);
        
        // 等待PLC反馈任务完成状态
        for (int i = 0; i < 10; i++) {
            Short feedbackTaskId = (Short) readPlc("feedback_task_id");
            Short taskStatus = (Short) readPlc("task_status");
            
            if (feedbackTaskId != null && feedbackTaskId.equals(taskId.shortValue())
                && taskStatus != null && taskStatus == STATUS_COMPLETED) {
                log.info("工作站出库任务已完成，任务号: {}", taskId);
                jobInfoService.updateMemo(jobInfo, "【工作站】出库完成确认成功");
                return true;
            }
            Thread.sleep(50);
        }
        
        jobInfoService.updateMemo(jobInfo, "【工作站】出库完成确认超时");
        return false;
    }
    
    /**
     * 托盘尺寸不符退回
     * WCS校验失败时调用
     * 
     * @param jobInfo 作业信息
     * @return true-指令发送成功, false-发送失败
     */
    public Boolean rejectBySizeError(JobInfo jobInfo) throws Exception {
        if (isTest) {
            return true;
        }
        
        // 下发托盘尺寸不符退回指令（无需任务号）
        writePlc("task_id_write", (short) 0);
        writePlc("task_type_write", (short) TASK_TYPE_SIZE_ERROR); // 100
        writePlc("reset_signal", (short) 0);
        
        Thread.sleep(100);
        
        // CRC校验码
        Integer crc = calculateCRC(0, TASK_TYPE_SIZE_ERROR);
        writePlc("crc_checksum", crc);
        
        jobInfoService.updateMemo(jobInfo, "【工作站】已下发托盘尺寸不符退回指令");
        return true;
    }
    
    /**
     * 条码信息不符退回
     * WCS校验失败时调用
     * 
     * @param jobInfo 作业信息
     * @return true-指令发送成功, false-发送失败
     */
    public Boolean rejectByBarcodeError(JobInfo jobInfo) throws Exception {
        if (isTest) {
            return true;
        }
        
        // 下发条码信息不符退回指令（无需任务号）
        writePlc("task_id_write", (short) 0);
        writePlc("task_type_write", (short) TASK_TYPE_BARCODE_ERROR); // 101
        writePlc("reset_signal", (short) 0);
        
        Thread.sleep(100);
        
        // CRC校验码
        Integer crc = calculateCRC(0, TASK_TYPE_BARCODE_ERROR);
        writePlc("crc_checksum", crc);
        
        jobInfoService.updateMemo(jobInfo, "【工作站】已下发条码信息不符退回指令");
        return true;
    }
    
    /**
     * 系统退回
     * 
     * @param jobInfo 作业信息
     * @return true-指令发送成功, false-发送失败
     */
    public Boolean systemReturn(JobInfo jobInfo) throws Exception {
        if (isTest) {
            return true;
        }
        
        // 下发系统退回指令（无需任务号）
        writePlc("task_id_write", (short) 0);
        writePlc("task_type_write", (short) TASK_TYPE_SYSTEM_RETURN); // 102
        writePlc("reset_signal", (short) 0);
        
        Thread.sleep(100);
        
        // CRC校验码
        Integer crc = calculateCRC(0, TASK_TYPE_SYSTEM_RETURN);
        writePlc("crc_checksum", crc);
        
        jobInfoService.updateMemo(jobInfo, "【工作站】已下发系统退回指令");
        return true;
    }
    
    /**
     * 复位工作站
     * 清除故障状态
     * 
     * @param jobInfo 作业信息
     * @return true-复位成功, false-复位失败
     */
    public Boolean reset(JobInfo jobInfo) throws Exception {
        if (isTest) {
            return true;
        }
        
        String taskNoStr = jobInfo.getTaskNo();
        Integer taskId = (taskNoStr != null && !taskNoStr.isEmpty()) 
                         ? Integer.parseInt(taskNoStr) : 0;
        
        // 下发复位指令
        writePlc("task_id_write", taskId.shortValue());
        writePlc("task_type_write", (short) TASK_TYPE_INBOUND); // 保持原任务类型
        writePlc("reset_signal", (short) 1); // 复位信号=1
        
        Thread.sleep(100);
        
        // CRC校验码
        Integer crc = calculateCRC(taskId, TASK_TYPE_INBOUND);
        writePlc("crc_checksum", crc);
        
        Thread.sleep(100);
        
        // 等待故障状态清除
        for (int i = 0; i < 10; i++) {
            Short faultStatus = (Short) readPlc("fault_status");
            Short taskStatus = (Short) readPlc("task_status");
            Short faultCode = (Short) readPlc("fault_code");
            
            if (faultStatus != null && faultStatus == FAULT_NONE 
                && taskStatus != null && taskStatus == STATUS_IDLE
                && faultCode != null && faultCode == 0) {
                log.info("工作站复位成功");
                jobInfoService.updateMemo(jobInfo, "【工作站】复位成功");
                
                // 复位完成后将复位信号置0
                writePlc("reset_signal", (short) 0);
                return true;
            }
            Thread.sleep(50);
        }
        
        jobInfoService.updateMemo(jobInfo, "【工作站】复位超时");
        return false;
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
        
        String taskNoStr = jobInfo.getTaskNo();
        if (taskNoStr == null || taskNoStr.isEmpty()) {
            return false;
        }
        
        Short expectedTaskId = Short.parseShort(taskNoStr);
        
        // 读取反馈任务号
        Short feedbackTaskId = (Short) readPlc("feedback_task_id");
        
        if (feedbackTaskId != null && feedbackTaskId.equals(expectedTaskId)) {
            // 检查任务执行状态
            Short taskStatus = (Short) readPlc("task_status");
            
            if (taskStatus == null) {
                return false;
            }
            
            // 0-空闲, 1-入库中, 2-出库中, 3-已完成, 4-CRC失败
            switch (taskStatus) {
                case STATUS_IDLE:
                    jobInfoService.updateMemo(jobInfo, "【工作站】任务空闲");
                    return false;
                case STATUS_INBOUND_PROCESSING:
                    jobInfoService.updateMemo(jobInfo, "【工作站】入库任务执行中");
                    return false;
                case STATUS_OUTBOUND_PROCESSING:
                    jobInfoService.updateMemo(jobInfo, "【工作站】出库任务执行中");
                    return false;
                case STATUS_COMPLETED:
                    log.info("工作站任务已完成，任务号: {}", feedbackTaskId);
                    jobInfoService.updateMemo(jobInfo, "【工作站】任务已完成");
                    return true;
                case STATUS_CRC_FAILED:
                    jobInfoService.updateMemo(jobInfo, "【工作站】CRC校验失败");
                    return false;
                default:
                    jobInfoService.updateMemo(jobInfo, "【工作站】未知任务状态: " + taskStatus);
                    return false;
            }
        }
        
        return false;
    }
    
    /**
     * 检查是否存在故障
     * 
     * @param jobInfo 作业信息
     * @return 故障信息描述，无故障返回null
     */
    public String checkFault(JobInfo jobInfo) {
        if (isTest) {
            return null;
        }
        
        Short faultStatus = (Short) readPlc("fault_status");
        
        if (faultStatus == null || faultStatus == FAULT_NONE) {
            return null;
        }
        
        StringBuilder faultMsg = new StringBuilder();
        
        // 检测门故障
        if (faultStatus == FAULT_DETECTION) {
            Short gateFaultCode = (Short) readPlc("gate_fault_code");
            Short weighingFaultCode = (Short) readPlc("weighing_fault_code");
            Short barcodeFaultCode = (Short) readPlc("barcode_fault_code");
            
            faultMsg.append("【工作站】检测故障 - ");
            if (gateFaultCode != null && gateFaultCode != 0) {
                faultMsg.append("检测门故障代码:").append(gateFaultCode).append(" ");
            }
            if (weighingFaultCode != null && weighingFaultCode != 0) {
                faultMsg.append("称重异常代码:").append(weighingFaultCode).append(" ");
            }
            if (barcodeFaultCode != null && barcodeFaultCode != 0) {
                faultMsg.append("读码故障代码:").append(barcodeFaultCode).append(" ");
            }
        }
        
        // 单机故障
        if (faultStatus == FAULT_MACHINE) {
            Short faultCode = (Short) readPlc("fault_code");
            faultMsg.append("【工作站】单机故障 - 故障代码:").append(faultCode);
            
            // 故障代码说明
            if (faultCode != null) {
                switch (faultCode) {
                    case 1: faultMsg.append("(任务号校验错误)"); break;
                    case 2: faultMsg.append("(CRC校验错误)"); break;
                    case 3: faultMsg.append("(托盘尺寸不对)"); break;
                    case 4: faultMsg.append("(条码信息不对)"); break;
                    case 5: faultMsg.append("(系统退回)"); break;
                    case 6: faultMsg.append("(库口被占用)"); break;
                }
            }
        }
        
        String faultInfo = faultMsg.toString();
        log.warn(faultInfo);
        jobInfoService.updateMemo(jobInfo, faultInfo);
        return faultInfo;
    }
    
    /**
     * 获取工作站当前状态信息
     * 
     * @return 状态信息字符串
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("工作站状态: ");
        sb.append("\n心跳: ").append(readPlc("heartbeat"));
        sb.append("\n联机状态: ").append(readPlc("online_status"));
        sb.append("\n任务执行状态: ").append(readPlc("task_status"));
        sb.append("\n载货状态: ").append(readPlc("load_status"));
        sb.append("\n申请信号: ").append(readPlc("apply_signal"));
        sb.append("\n故障状态: ").append(readPlc("fault_status"));
        sb.append("\n故障代码: ").append(readPlc("fault_code"));
        sb.append("\n托盘尺寸: ").append(readPlc("pallet_length"))
          .append("x").append(readPlc("pallet_width"))
          .append("x").append(readPlc("pallet_height"));
        sb.append("\n货物重量: ").append(readPlc("cargo_weight"));
        sb.append("\n条码信息: ").append(readPlc("barcode_info"));
        return sb.toString();
    }
    
    /**
     * 等待PLC反馈任务号
     * 
     * @param taskId 发送的任务号
     * @param maxRetries 最大重试次数
     * @return 反馈的任务号，超时返回null
     */
    private Short waitForTaskFeedback(Short taskId, int maxRetries) throws Exception {
        for (int i = 0; i < maxRetries; i++) {
            Short feedbackTaskId = (Short) readPlc("feedback_task_id");
            if (feedbackTaskId != null && feedbackTaskId.equals(taskId)) {
                return feedbackTaskId;
            }
            Thread.sleep(50);
        }
        log.warn("等待PLC反馈任务号超时，任务号: {}", taskId);
        return null;
    }
    
    /**
     * 生成任务号
     * 简单递增方式
     */
    private static int taskIdCounter = 100;
    public synchronized Integer generateTaskId() {
        return taskIdCounter++;
    }
    
    /**
     * 计算CRC校验码 (CRC-16/MODBUS)
     * 对任务号和任务类型进行校验
     * 
     * 注意：按照PLC算法的字节提取方式
     * - 每个INT/WORD类型的高字节在前(SHR(value, 8) & 0xFF)
     * - 低字节在后(value & 0xFF)
     * 
     * @param taskId 任务号
     * @param taskType 任务类型
     * @return CRC校验码 (范围0-65535，对应PLC的WORD类型，使用Integer存储)
     */
    private Integer calculateCRC(Integer taskId, Integer taskType) {
        // 构建数据字节数组（任务号2字节 + 任务类型2字节 = 4字节）
        byte[] data = new byte[4];
        
        // 任务号 (Int, 2字节) - 高字节在前
        data[0] = (byte) ((taskId >> 8) & 0xFF);   // 高字节
        data[1] = (byte) (taskId & 0xFF);          // 低字节
        
        // 任务类型 (Int, 2字节) - 高字节在前
        data[2] = (byte) ((taskType >> 8) & 0xFF); // 高字节
        data[3] = (byte) (taskType & 0xFF);        // 低字节
        
        // 计算CRC-16/MODBUS
        return crc16Modbus(data);
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
    private int crc16Modbus(byte[] data) {
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
        
        // MODBUS标准要求：高低字节交换
        // 原始算法：return (CRC16 >> 8) | (CRC16 << 8)
        // 先右移8位得到低字节到高字节位置，再左移8位得到高字节到低字节位置，然后或运算合并
        return ((crc >> 8) & 0xFF) | ((crc << 8) & 0xFF00);
    }

}

