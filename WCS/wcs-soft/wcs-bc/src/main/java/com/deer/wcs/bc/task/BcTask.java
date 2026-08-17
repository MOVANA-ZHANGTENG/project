package com.deer.wcs.bc.task;

import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.TaskInfoService;
import com.deer.wcs.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

@Component("BcTask")
public class BcTask {

    @Autowired
    private DeviceValueService deviceValueService;

    @Autowired
    private TaskInfoService taskInfoService;


    // 站台设备编码
    private static final String STATION_1_CODE = "1";
    private static final String STATION_2_CODE = "2";

    /**
     * 两个站台
     * 检测每个站台的任务申请信号
     * 拿到托盘号
     * 创建入库任务
     */
    public void auto() {
        // 检测站台1
        checkAndCreateTask(STATION_1_CODE);
        // 检测站台2
        checkAndCreateTask(STATION_2_CODE);
    }

    /**
     * 检查站台并创建入库任务
     * @param stationCode 站台设备编码
     */
    private void checkAndCreateTask(String stationCode) {
        try {
            // 读取任务申请信号 (0-无, 1-有申请)
            Object taskRequestObj = deviceValueService.readValueByCode(stationCode, "TASK_REQUEST");
            if (taskRequestObj == null) {
                return;
            }

            Short taskRequest = Short.valueOf(taskRequestObj.toString());
            if (taskRequest == 1) {
                // 读取托盘号
                String palletCode = deviceValueService.readValueByCode(stationCode, "PALLET_CODE").toString();
                if (palletCode == null || palletCode.isEmpty()) {
                    return;
                }

                // 检查是否已存在相同托盘号的入库任务
                Condition condition = new Condition(TaskInfo.class);
                condition.createCriteria()
                        .andEqualTo("palletCode", palletCode); // 状态为0表示刚生成的任务
                List<TaskInfo> existingTasks = taskInfoService.findByCondition(condition);
                if (!existingTasks.isEmpty()) {
                    return; // 任务已存在，不重复创建
                }

                // 创建入库任务
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setWareCode("bc"); // 假设仓库编码为bc
                taskInfo.setWareName("BC仓库");
                taskInfo.setType("IN"); // 1表示入库任务（根据实际情况调整）
                taskInfo.setPalletCode(palletCode);
                taskInfo.setFromCellCode(stationCode); // 从当前站台取货
                taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
                taskInfo.setState(0); // 初始状态

                // 保存任务
                taskInfoService.save(taskInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
