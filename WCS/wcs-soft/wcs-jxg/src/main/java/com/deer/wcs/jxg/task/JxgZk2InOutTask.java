package com.deer.wcs.jxg.task;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.jxg.handle.JxgZkGzzHandle;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.TaskInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

@Component("JxgZk2InOutTask")
public class JxgZk2InOutTask {
    
    private static final Logger log = LoggerFactory.getLogger(JxgZk2InOutTask.class);
    
    @Autowired
    private DeviceValueService deviceValueService;
    
    @Autowired
    private CellInfoService cellInfoService;
    
    @Autowired
    private TaskInfoService taskInfoService;
    
    @Autowired
    private AutoService autoService;
    
    @Autowired
    private JxgZkGzzHandle jxgZkGzzHandle;
    
    @Value("${isTest:false}")
    private Boolean isTest;
    
    // 设备编码 - 工作站
    private static final String DEVICE_CODE_GZZ = "gzz";
    
    // 默认仓库编码（根据实际情况配置）
    private static final String DEFAULT_WARE_CODE = "sxc";
    
    // 工作站库位编码
    private static final String GZZ_INBOUND_CELL = "1-8-6";

    /**
     * 检测入库口是否有入库申请
     * 以及扫描的托盘号
     * 如果有，则分配一个入库位置
     * 下发一个入库任务（taskInfo）
     * 简化处理 不用管工作站的任务状态
     * 工作站的点位  可以去JxgZkGzzHandle看一下
     */
    public void autoIn() {
        String recordContent = null; // 用于记录本次循环的最终状态，确保只插入一条记录
        
        try {
            // 1. 检查是否有入库申请信号
            if (!checkInboundApplication()) {
                recordContent = "等待入库申请";
                return;
            }

            // 2. 检测是否存在起点为1-8-6的任务，如果已经存在则返回
            if (checkExistingInboundTask(GZZ_INBOUND_CELL)) {
                log.debug("【自动入库】已存在起点为{}的任务，等待任务完成", GZZ_INBOUND_CELL);
                recordContent = "已存在起点为" + GZZ_INBOUND_CELL + "的任务，等待任务完成";
                return;
            }
            
            // 3. 读取扫描的托盘号
//            String palletCode = readPalletCode();
//            if (palletCode == null || palletCode.trim().isEmpty() || "0".equals(palletCode)) {
//                log.debug("【自动入库】未读取到有效托盘号");
//                recordContent = "检测到入库申请，等待扫描托盘条码";
//                return;
//            }

            String palletCode = "JXG-TEST-001";
            
            log.info("【自动入库】检测到入库申请，托盘号: {}", palletCode);
            
            // 3. 分配一个空闲入库位置
            CellInfo targetCell = allocateInboundCell();
            if (targetCell == null) {
                log.warn("【自动入库】未找到空闲库位，托盘号: {}", palletCode);
                recordContent = "读取到托盘号: " + palletCode + "，但未找到空闲库位";
                // 可以选择下发系统退回指令
                // jxgZkGzzHandle.sendSystemReturn(null);
                return;
            }
            
            log.info("【自动入库】分配库位成功，托盘号: {}, 目标库位: {}", palletCode, targetCell.getCode());
            
            // 4. 获取工作站入库点位（作为任务起点）
            String gzzInboundCell = GZZ_INBOUND_CELL;
            
            // 5. 创建入库任务
            TaskInfo taskInfo = createInboundTask(palletCode, gzzInboundCell, targetCell.getCode());
            if (taskInfo == null) {
                log.error("【自动入库】任务创建失败，托盘号: {}", palletCode);
                recordContent = "分配库位成功: " + targetCell.getCode() + "，但任务创建失败，托盘号: " + palletCode;
                return;
            }
            targetCell.setTaskState(taskInfo.getId());
            cellInfoService.update(targetCell);
            
            log.info("【自动入库】任务创建成功，任务ID: {}, 托盘号: {}, 起点: {}, 终点: {}", 
                    taskInfo.getId(), palletCode, gzzInboundCell, targetCell.getCode());
            recordContent = "任务创建成功，任务ID: " + taskInfo.getId() + "，托盘号: " + palletCode + "，终点: " + targetCell.getCode();
            
            // 6. 下发入库任务到工作站（可选，根据实际需求）
            // 如果需要直接控制工作站，可以调用：
            // Short taskNo = autoService.getTodayTaskNo();
            // jxgZkGzzHandle.sendInboundTask(null, taskNo.intValue());
            
        } catch (Exception e) {
            log.error("【自动入库】处理异常", e);
            recordContent = "处理异常: " + e.getMessage();
        } finally {
            // 在finally块中统一插入一条记录，确保一个autoIn()循环只插入一次
            if (recordContent != null) {
                cellInfoService.addRecord(GZZ_INBOUND_CELL, DEFAULT_WARE_CODE, recordContent);
            }
        }
    }
    
    /**
     * 检查是否存在指定起点的未完成任务
     * @param fromCellCode 起点库位编码
     * @return true-存在, false-不存在
     */
    private boolean checkExistingInboundTask(String fromCellCode) {
        try {
            // 查询起点为指定库位且未完成的任务
            Condition condition = new Condition(TaskInfo.class);
            condition.createCriteria()
                    .andEqualTo("fromCellCode", fromCellCode)
                    .andEqualTo("wareCode", DEFAULT_WARE_CODE);
            
            List<TaskInfo> taskList = taskInfoService.findByCondition(condition);
            
            if (taskList != null && !taskList.isEmpty()) {
                log.debug("【自动入库】检测到起点为{}的未完成任务，数量: {}", fromCellCode, taskList.size());
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("【自动入库】检查已存在任务失败", e);
            return true; // 异常时返回true，防止重复创建任务
        }
    }
    
    /**
     * 检查是否有入库申请
     * @return true-有申请, false-无申请
     */
    private boolean checkInboundApplication() {
        try {
            if (isTest) {
                return true;
            }
            
            // 检查申请信号（apply_signal = 1）
            Short applySignal = (Short) deviceValueService.readValueByCode(DEVICE_CODE_GZZ, "apply_signal");
            if (applySignal == null || applySignal != 1) {
                return false;
            }
            
            // 检查载货状态（load_status = 1）
            Short loadStatus = (Short) deviceValueService.readValueByCode(DEVICE_CODE_GZZ, "load_status");
            if (loadStatus == null || loadStatus != 1) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("【自动入库】检查入库申请失败", e);
            return false;
        }
    }
    
    /**
     * 读取扫描的托盘号
     * @return 托盘号
     */
    private String readPalletCode() {
        try {
            if (isTest) {
                return "PALLET-TEST-" + System.currentTimeMillis();
            }
            
            // 从工作站读取条码信息
            String barcodeInfo = (String) deviceValueService.readValueByCode(DEVICE_CODE_GZZ, "barcode_info");
            return barcodeInfo;
        } catch (Exception e) {
            log.error("【自动入库】读取托盘号失败", e);
            return null;
        }
    }
    
    /**
     * 分配空闲入库位置
     * 查询条件：
     * 1. inven_state = 0 (空闲)
     * 2. task_state = 0 (无任务)
     * 3. disable_state = 0 (未禁用)
     * 4. is_delete = 0 (未删除)
     * 5. type = 1 (普通货位) 或根据实际需求调整
     * 
     * @return 空闲库位，未找到返回null
     */
    private CellInfo allocateInboundCell() {
        try {

            
            // 如果没有找到，尝试查询特定条件的库位
            Condition condition = new Condition(CellInfo.class);
            condition.createCriteria()
                    .andEqualTo("wareCode", DEFAULT_WARE_CODE)
                    .andEqualTo("type", 0)  // 空闲
                    .andEqualTo("invenState", 0)  // 空闲
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
            log.error("【自动入库】分配库位失败", e);
            return null;
        }
    }
    
    /**
     * 获取工作站入库点位编码
     * 根据实际配置，工作站的入库点位可能命名为：
     * - GZZ-IN（工作站入库口）
     * - GZZ-IN-01
     * 或者通过type字段标识（type=7表示工作站）
     * 
     * @return 工作站入库点位编码
     */
    private String getGzzInboundCell() {
        try {
            // 查询工作站类型的库位（type=7）
            Condition condition = new Condition(CellInfo.class);
            condition.createCriteria()
                    .andEqualTo("wareCode", DEFAULT_WARE_CODE)
                    .andEqualTo("type", 7)  // 工作站类型
                    .andEqualTo("isDelete", 0)
                    .andLike("code", "%IN%"); // 包含IN关键字的入库口
            
            List<CellInfo> cellList = cellInfoService.findByCondition(condition);
            
            if (cellList != null && !cellList.isEmpty()) {
                return cellList.get(0).getCode();
            }
            
            // 如果没找到，返回默认值
            log.warn("【自动入库】未找到工作站入库点位，使用默认值");
            return "GZZ-IN";
        } catch (Exception e) {
            log.error("【自动入库】获取工作站点位失败", e);
            return "GZZ-IN";
        }
    }
    
    /**
     * 创建入库任务
     * 
     * @param palletCode 托盘号
     * @param fromCellCode 起点（工作站入库口）
     * @param toCellCode 终点（分配的库位）
     * @return 创建的任务信息
     */
    private TaskInfo createInboundTask(String palletCode, String fromCellCode, String toCellCode) {
        try {
            TaskInfo taskInfo = new TaskInfo();
            
            // 设置仓库信息
            taskInfo.setWareCode(DEFAULT_WARE_CODE);
            
            // 设置任务类型
            taskInfo.setType("move"); // 入库类型
            
            // 设置托盘信息
            taskInfo.setPalletCode(palletCode);
            
            // 设置起点和终点
            taskInfo.setFromCellCode(fromCellCode);
            taskInfo.setToCellCode(toCellCode);
            
            // 设置优先级（普通优先级）
            taskInfo.setPriority(0);
            
            // 保存任务（TaskInfoService.save方法会自动设置ID、状态、创建时间等）
            taskInfoService.save(taskInfo);
            
            return taskInfo;
        } catch (Exception e) {
            log.error("【自动入库】创建任务失败，托盘号: {}", palletCode, e);
            return null;
        }
    }

}
