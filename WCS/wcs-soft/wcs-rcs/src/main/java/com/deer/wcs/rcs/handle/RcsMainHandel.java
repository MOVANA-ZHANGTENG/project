package com.deer.wcs.rcs.handle;


import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.*;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.rcs.model.*;
import com.deer.wcs.rcs.service.*;
import com.deer.wcs.rcs.util.CarPositionCalculator;
import com.deer.wcs.rcs.websocket.MonitorWebSocketHandler;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.PathHandle;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.PathHandleService;
import com.deer.wcs.task.service.TaskInfoService;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Component("RcsMainHandel")
public class RcsMainHandel {

    private static final Logger log = LoggerFactory.getLogger(RcsMainHandel.class);

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private CellLinkService cellLinkService;

    @Autowired
    private PathInfoService pathInfoService;

    @Autowired
    private RcsCarInfoService rcsCarInfoService;

    @Autowired
    private RcsCarTypeService rcsCarTypeService;

    @Autowired
    private RcsCarHandleService rcsCarHandleService;

    @Autowired
    private HandleService handleService;

    @Autowired
    private PathHandleService pathHandleService;

    @Autowired
    private RcsCarPathService rcsCarPathService;

    @Autowired
    private RcsTsjService rcsTsjService;

    private static final String CAR_MOVE_TASK_TYPE = "car_move2";    // 小车移动任务（自动寻找存放库位）

    /**
     * 分配提升机
     *
     * @return
     */
    public Boolean allotTsj(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        Condition condition = new Condition(RcsTsj.class);
        condition.createCriteria().andEqualTo("status", "idle");
        List<RcsTsj> rcsTsjList = rcsTsjService.findByCondition(condition);
        if (rcsTsjList.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "未找到可用提升机");
            return false;
        }

        for (RcsTsj rcsTsj : rcsTsjList) {
            if (rcsTsj.getCurrentTaskId() == 0||rcsTsj.getCurrentTaskId().equals(jobInfo.getTaskId())) {
                rcsTsj.setCurrentTaskId(jobInfo.getTaskId());
                rcsTsjService.update(rcsTsj);
                jobInfoService.updateMemo(jobInfo, "成功分配提升机-" + rcsTsj.getCode());
                return true;
            }

            TaskInfo checkTask = taskInfoService.findById(rcsTsj.getCurrentTaskId());
            if (checkTask == null) {
                rcsTsj.setCurrentTaskId(jobInfo.getTaskId());
                rcsTsjService.update(rcsTsj);
                jobInfoService.updateMemo(jobInfo, "成功分配提升机-" + rcsTsj.getCode());
                return true;
            }

        }

        jobInfoService.updateMemo(jobInfo, "未找到可用提升机");
        return false;
    }

    /**
     * 释放提升机占用
     *
     * @return
     */
    public Boolean unAllotTsj(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId", jobInfo.getTaskId());
        if (rcsTsj == null) {
            jobInfoService.updateMemo(jobInfo, "未找到当前任务占用的提升机 请检查");
            return false;
        }
        rcsTsj.setCurrentTaskId(0L);
        rcsTsjService.update(rcsTsj);
        jobInfoService.updateMemo(jobInfo, "成功释放提升机占用-" + rcsTsj.getCode());
        return true;

    }

    @Autowired
    private DeviceValueService deviceValueService;


    public Boolean getTsjDockCellCodeAsFromCell(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        CellInfo toCellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), jobInfo.getToCellCode());
        if (toCellInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务终点位置不存在 请检查");
            return false;
        }
        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId", jobInfo.getTaskId());
        if (rcsTsj == null) {
            jobInfoService.updateMemo(jobInfo, "未找到当前任务占用的提升机 请检查");
            return false;
        }
        String cellCode = rcsTsj.getDockCellCode();
        if (cellCode == null) {
            jobInfoService.updateMemo(jobInfo, "当前任务占用的提升机 未配置对接位置 请检查");
            return false;
        }
        CellInfo dockCellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), cellCode);
        if (dockCellInfo == null) {
            jobInfoService.updateMemo(jobInfo, "提升机对接位置不存在 请检查");
            return false;
        }

        Integer x = dockCellInfo.getX();
        Integer y = dockCellInfo.getY();
        Integer z = toCellInfo.getZ();

        String fromCellCode = z + "-" + x + "-" + y;
        jobInfo.setFromCellCode(fromCellCode);
        jobInfoService.update(jobInfo);
        jobInfoService.updateMemo(jobInfo, "成功获取提升机-" + rcsTsj.getCode() + "对接位置-" + fromCellCode);
        return true;
    }

    public Boolean getTsjDockCellCodeAsToCell(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId", jobInfo.getTaskId());
        if (rcsTsj == null) {
            jobInfoService.updateMemo(jobInfo, "未找到当前任务占用的提升机 请检查");
            return false;
        }
        String cellCode = rcsTsj.getDockCellCode();
        if (cellCode == null) {
            jobInfoService.updateMemo(jobInfo, "当前任务占用的提升机 未配置对接位置 请检查");
            return false;
        }
        CellInfo dockCellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), cellCode);
        if (dockCellInfo == null) {
            jobInfoService.updateMemo(jobInfo, "提升机对接位置不存在 请检查");
            return false;
        }

        short currentFloor = (short) deviceValueService.readValueByCode(rcsTsj.getCode(), "currentFloor");

        Integer x = dockCellInfo.getX();
        Integer y = dockCellInfo.getY();
        Integer z = (int) currentFloor;

        String toCellCode = z + "-" + x + "-" + y;

        jobInfo.setToCellCode(toCellCode);
        jobInfoService.update(jobInfo);
        jobInfoService.updateMemo(jobInfo, "成功获取提升机-" + rcsTsj.getCode() + "对接位置-" + toCellCode);
        return true;
    }


    /**
     * 提升机位置作为起点
     *
     * @return
     */
    public Boolean getTsjCellCodeAsFromCell(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId", jobInfo.getTaskId());
        if (rcsTsj == null) {
            jobInfoService.updateMemo(jobInfo, "未找到当前任务占用的提升机 请检查");
            return false;
        }
        String cellCode = rcsTsj.getCellCode();
        if (cellCode == null) {
            jobInfoService.updateMemo(jobInfo, "当前任务占用的提升机 未配置位置 请检查");
            return false;
        }
        CellInfo cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), cellCode);
        if (cellInfo == null) {
            jobInfoService.updateMemo(jobInfo, "提升机位置不存在 请检查");
            return false;
        }

        short currentFloor = (short) deviceValueService.readValueByCode(rcsTsj.getCode(), "currentFloor");

        Integer x = cellInfo.getX();
        Integer y = cellInfo.getY();
        Integer z = (int) currentFloor;

        String fromCellCode = z + "-" + x + "-" + y;
        jobInfo.setFromCellCode(fromCellCode);
        jobInfoService.update(jobInfo);
        jobInfoService.updateMemo(jobInfo, "成功获取提升机-" + rcsTsj.getCode() + "位置-" + fromCellCode);
        return true;

    }

    /**
     * 提升机位置作为终点
     *
     * @return
     */
    public Boolean getTsjCellCodeASToCell(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId", jobInfo.getTaskId());
        if (rcsTsj == null) {
            jobInfoService.updateMemo(jobInfo, "未找到当前任务占用的提升机 请检查");
            return false;
        }
        String cellCode = rcsTsj.getCellCode();
        if (cellCode == null) {
            jobInfoService.updateMemo(jobInfo, "当前任务占用的提升机 未配置位置 请检查");
            return false;
        }
        CellInfo cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), cellCode);
        if (cellInfo == null) {
            jobInfoService.updateMemo(jobInfo, "提升机位置不存在 请检查");
            return false;
        }

        short currentFloor = (short) deviceValueService.readValueByCode(rcsTsj.getCode(), "currentFloor");

        Integer x = cellInfo.getX();
        Integer y = cellInfo.getY();
        Integer z = (int) currentFloor;

        String toCellCode = z + "-" + x + "-" + y;
        jobInfo.setToCellCode(toCellCode);
        jobInfoService.update(jobInfo);
        jobInfoService.updateMemo(jobInfo, "成功获取提升机-" + rcsTsj.getCode() + "位置-" + toCellCode);
        return true;

    }


    /*
        分配小车(不论层数)
     */
    public Boolean allotCar(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        /* 查询是否有指定小车 */
        if (taskInfo.getRcsCarId() != null && taskInfo.getRcsCarId() > 0) {
            RcsCarInfo rcsCarInfo = rcsCarInfoService.findById(taskInfo.getRcsCarId());
            if (rcsCarInfo == null) {
                jobInfoService.updateMemo(jobInfo, "小车不存在：" + taskInfo.getRcsCarId());
                return false;
            }
            jobInfo.setRcsCarId(rcsCarInfo.getId());
            jobInfoService.update(jobInfo);
            jobInfoService.updateMemo(jobInfo, "该任务指定小车-" + rcsCarInfo.getCode());
            return true;
        }

        List<RcsCarInfo> rcsCarList = rcsCarInfoService.findAll();
        for (RcsCarInfo rcsCarInfo : rcsCarList) {
            if (rcsCarInfo.getIsConnected() == 0) {
                continue;
            }
            if (rcsCarInfo.getIsCharge() == null || rcsCarInfo.getIsCharge() == 1) {
                continue;
            }
            Condition condition1 = new Condition(JobInfo.class);
            condition1.createCriteria().andEqualTo("rcsCarId", rcsCarInfo.getId());
            List<JobInfo> jobInfos = jobInfoService.findByCondition(condition1);
            if (jobInfos.isEmpty()) {
                jobInfo.setRcsCarId(rcsCarInfo.getId());
                jobInfoService.update(jobInfo);
                jobInfoService.updateMemo(jobInfo, "成功分配小车-" + rcsCarInfo.getCode());
                return true;
            }
        }
        jobInfoService.updateMemo(jobInfo, "未成功分配小车");
        return false;
    }

    /*
       分配小车(根据层数)
    */
    public Boolean allotCarByFloor(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        /* 查询是否有指定小车 */
        if (taskInfo.getRcsCarId() != null && taskInfo.getRcsCarId() > 0) {
            RcsCarInfo rcsCarInfo = rcsCarInfoService.findById(taskInfo.getRcsCarId());
            if (rcsCarInfo == null) {
                jobInfoService.updateMemo(jobInfo, "小车不存在：" + taskInfo.getRcsCarId());
                return false;
            }
            jobInfo.setRcsCarId(rcsCarInfo.getId());
            jobInfoService.update(jobInfo);
            jobInfoService.updateMemo(jobInfo, "该任务指定小车-" + rcsCarInfo.getCode());
            return true;
        }

        /* 查询起始位置的层数 */
        CellInfo fromCellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), jobInfo.getFromCellCode());
        if (fromCellInfo == null) {
            jobInfoService.updateMemo(jobInfo, "起始位置不存在请检查");
            return false;
        }
        Integer currentZ = fromCellInfo.getZ();

        /* 根据起始位置的层数查询可用小车 */
        Condition condition = new Condition(RcsCarInfo.class);
        condition.createCriteria().andEqualTo("currentZ", currentZ);
        List<RcsCarInfo> rcsCarList = rcsCarInfoService.findByCondition(condition);
        for (RcsCarInfo rcsCarInfo : rcsCarList) {
            if (rcsCarInfo.getIsConnected() == 0) {
                continue;
            }
            if (rcsCarInfo.getIsCharge() == null || rcsCarInfo.getIsCharge() == 1) {
                continue;
            }
            Condition condition1 = new Condition(JobInfo.class);
            condition1.createCriteria().andEqualTo("rcsCarId", rcsCarInfo.getId());
            List<JobInfo> jobInfos = jobInfoService.findByCondition(condition1);
            if (jobInfos.isEmpty()) {
                jobInfo.setRcsCarId(rcsCarInfo.getId());
                jobInfoService.update(jobInfo);
                jobInfoService.updateMemo(jobInfo, "成功分配小车-" + rcsCarInfo.getCode());
                return true;
            }
        }
        jobInfoService.updateMemo(jobInfo, "未成功分配小车");
        return false;
    }

    public Boolean getLastJobCar(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        JobInfo lastJob = jobInfoService.findById(jobInfo.getLastJobId());
        if (lastJob == null) {
            jobInfoService.updateMemo(jobInfo, "上一个job不存在");
            return false;
        }
        jobInfo.setRcsCarId(lastJob.getRcsCarId());
        jobInfoService.update(jobInfo);
        jobInfoService.updateMemo(jobInfo, "成功获取上一个job的小车");
        return true;

    }


    /*
        生成起点层小车从接驳位--》该层空位
     */
    public Boolean createTaskToEmptyCell(JobInfo jobInfo) {

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务" + jobInfo.getTaskId() + "不存在");
            return false;
        }

        TaskInfo newTask = new TaskInfo();
        newTask.setWareCode(taskInfo.getWareCode());
        newTask.setWareName(taskInfo.getWareName());
        newTask.setType(CAR_MOVE_TASK_TYPE);
        // 起点位置为提升机接驳位
        newTask.setFromCellCode(jobInfo.getFromCellCode());
        // 指定已经分配的起点层小车
        newTask.setRcsCarId(jobInfo.getRcsCarId());
        taskInfoService.save(newTask);
        return true;
    }

    /*
        生成终点层小车从当前位置到达终点层接驳位
     */
    public Boolean createTaskToDockCell(JobInfo jobInfo) {

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务" + jobInfo.getTaskId() + "不存在");
            return false;
        }

        TaskInfo preTask = new TaskInfo();
        preTask.setWareCode(taskInfo.getWareCode());
        preTask.setWareName(taskInfo.getWareName());
        preTask.setType("car_move1");
        // 起点位置为小车位置
        preTask.setFromCellCode(jobInfo.getFromCellCode());
        // 终点位置为提升机接驳位
        preTask.setToCellCode(jobInfo.getToCellCode());
        // 指定已经分配的终点层小车
        preTask.setRcsCarId(jobInfo.getRcsCarId());
        taskInfoService.save(preTask);

        taskInfo.setSubTaskId(preTask.getSubTaskId());
        taskInfoService.update(taskInfo);
        return true;
    }

    /**
     * 状态常量
     */
    private static class StatusConstants {
        // 任务状态
        private static final int TASK_WAITING = 0;
        private static final int TASK_RUNNING = 1;
        private static final int TASK_COMPLETED = 2;
        private static final int TASK_FAILED = 3;
    }

    /*
        检测前置任务是否完成
     */
    public Boolean checkPreTaskFinish(JobInfo jobInfo) {
        // 参数校验
        if (jobInfo == null || jobInfo.getTaskId() == null) {
            log.error("jobInfo或taskId为空");
            return true;
        }

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            log.error("任务{}不存在", jobInfo.getTaskId());
            return true;
        }

        // 如果没有前置任务，直接返回完成
        if (taskInfo.getSubTaskId() == null) {
            log.debug("任务{}没有前置任务", jobInfo.getTaskId());
            return true;
        }

        TaskInfo preTask = taskInfoService.findById(taskInfo.getSubTaskId());
        if (preTask != null && preTask.getState() != StatusConstants.TASK_COMPLETED) {
            // 前置任务未完成
            jobInfo.setMemo("");
            jobInfoService.update(jobInfo);
            log.info("任务{}的前置任务{}未完成，当前状态{}", jobInfo.getTaskId(), preTask.getId(), preTask.getState());
            return false;
        }

        log.info("任务{}的前置任务{}已完成", jobInfo.getTaskId(), preTask.getId());
        jobInfo.setMemo("终点层小车到达目标位置：" + jobInfo.getToCellCode());
        jobInfoService.update(jobInfo);
        return true;

    }


    /**
     * 判断小车去是否需要换层
     * 如果需要换层，则在jobInfo中设置judgeResult为success
     * 如果不需要换层，则在jobInfo中设置judgeResult为fail
     * 均返回true 代表判断结束
     *
     * @param jobInfo
     */
    public Boolean needHc(JobInfo jobInfo) {
        // 1. 获取任务信息
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        // 2. 获取小车信息
        if (jobInfo.getRcsCarId() == null) {
            jobInfoService.updateMemo(jobInfo, "rcsCarId不能为空");
            return false;
        }
        RcsCarInfo rcsCarInfo = rcsCarInfoService.findById(jobInfo.getRcsCarId());
        if (rcsCarInfo == null) {
            jobInfoService.updateMemo(jobInfo, "小车不存在：" + jobInfo.getRcsCarId());
            return false;
        }

        // 3. 获取小车当前位置作为起点
        String carCurrentCellCode = rcsCarInfo.getFromCellCode();
        if (carCurrentCellCode == null || carCurrentCellCode.trim().isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "小车当前位置不能为空");
            return false;
        }

        // 4. 获取任务起点位置作为终点
        String toCellCode = jobInfo.getToCellCode();
        if (toCellCode == null || toCellCode.trim().isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "目标位置不能为空");
            return false;
        }

        // 5. 如果小车当前位置和任务起点相同，不需要换层
        if (carCurrentCellCode.equals(toCellCode)) {
            jobInfoService.updateMemo(jobInfo, "小车当前位置：" + carCurrentCellCode + "和目标：" + toCellCode + "相同，不需要换层");
            jobInfo.setJudgeResult("fail");
            jobInfoService.update(jobInfo);
            return true;
        }

        // 6. 查询小车当前库位信息
        CellInfo carCurrentCellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), carCurrentCellCode);
        if (carCurrentCellInfo == null) {
            jobInfoService.updateMemo(jobInfo, "小车当前位置：" + carCurrentCellCode + "不存在");
            return false;
        }

        // 7. 查询任务起点库位信息
        CellInfo toCellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), toCellCode);
        if (toCellInfo == null) {
            jobInfoService.updateMemo(jobInfo, "目标位置：" + toCellCode + "不存在");
            return false;
        }

        // 8. 判断是否在同一层
        if (!carCurrentCellInfo.getZ().equals(toCellInfo.getZ())) {
            // 不在同一层，需要换层
            jobInfoService.updateMemo(jobInfo, "小车当前位置：" + carCurrentCellCode + "(层" + carCurrentCellInfo.getZ() + ")和目标：" + toCellCode + "(层" + toCellInfo.getZ() + ")不在同一层，需要换层");
            jobInfo.setJudgeResult("yes");
            jobInfoService.update(jobInfo);
            return true;
        } else {
            // 在同一层，不需要换层
            jobInfoService.updateMemo(jobInfo, "小车当前位置：" + carCurrentCellCode + "(层" + carCurrentCellInfo.getZ() + ")和目标：" + toCellCode + "(层" + toCellInfo.getZ() + ")在同一层，不需要换层");
            jobInfo.setJudgeResult("no");
            jobInfoService.update(jobInfo);
            return true;
        }


    }


    /**
     * 获取小车当前位置，写入jobInfo的起点
     *
     * @param jobInfo
     * @return
     */
    public Boolean getCarPosition(JobInfo jobInfo) {
        RcsCarInfo rcsCarInfo = rcsCarInfoService.findById(jobInfo.getRcsCarId());
        if (rcsCarInfo == null) {
            jobInfoService.updateMemo(jobInfo, "小车不存在");
            return false;
        }
        String fromCellCode = rcsCarInfo.getFromCellCode();
        String toCellCode = rcsCarInfo.getToCellCode();
        jobInfo.setFromCellCode(fromCellCode);
        jobInfoService.update(jobInfo);
        jobInfoService.updateMemo(jobInfo, "成功获取小车当前位置-" + fromCellCode);
        return true;
    }


    /**
     * 同层job拆path
     */
    public Boolean pathPlanning(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());

        // 获取小车当前位置（如果小车当前位置和起点不一致，直接更新）
        RcsCarInfo carInfo = rcsCarInfoService.findById(jobInfo.getRcsCarId());
        if (carInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务小车不存在");
            return false;
        }
        if (carInfo.getFromCellCode() == null || carInfo.getFromCellCode().equals("")) {
            jobInfoService.updateMemo(jobInfo, "任务小车当前位置为空");
            return false;
        }

        // 以最新的小车位置为起点位置
        String fromCellCode = carInfo.getFromCellCode();

        String toCellCode = jobInfo.getToCellCode();
        if (fromCellCode == null || fromCellCode.trim().isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "fromCellCode不能为空-----------------------------------------------------------------------------------------------------");
            return false;
        }
        if (toCellCode == null || toCellCode.trim().isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "toCellCode不能为空");
            return false;
        }
        if (fromCellCode.equals(toCellCode)) {
            jobInfoService.updateMemo(jobInfo, "fromCellCode：" + fromCellCode + "和toCellCode：" + toCellCode + "相同");
            return true;
        }

        // 根据wareCode查询所有库位信息，建立id到CellInfo的映射
        Condition allCellCondition = new Condition(CellInfo.class);
        allCellCondition.createCriteria()
                .andEqualTo("wareCode", taskInfo.getWareCode())
                .andEqualTo("z", carInfo.getCurrentZ())
                .andEqualTo("isDelete", 0);
        List<CellInfo> allCellInfoList = cellInfoService.findByCondition(allCellCondition);
        Map<Long, CellInfo> cellInfoMap = new HashMap<>();
        for (CellInfo cellInfo : allCellInfoList) {
            cellInfoMap.put(cellInfo.getId(), cellInfo);
        }

        CellInfo fromCellInfo = cellInfoMap.values().stream()
                .filter(c -> c.getCode().equals(fromCellCode))
                .findFirst().orElse(null);
        if (fromCellInfo == null) {
            jobInfoService.updateMemo(jobInfo, "fromCellCode：" + fromCellCode + "不存在");
            return false;
        }
        CellInfo toCellInfo = cellInfoMap.values().stream()
                .filter(c -> c.getCode().equals(toCellCode))
                .findFirst().orElse(null);
        if (toCellInfo == null) {
            jobInfoService.updateMemo(jobInfo, "toCellCode：" + toCellCode + "不存在");
            return false;
        }
        if (!fromCellInfo.getZ().equals(toCellInfo.getZ())) {
            jobInfoService.updateMemo(jobInfo, "fromCellCode：" + fromCellCode + "和toCellCode：" + toCellCode + "不在同一层");
            return false;
        }


        /**
         * 先查出该仓库所有的rcsCarInfo，用来判断是否有小车在该路径上
         * 当然，如果某小车在起点，说明这个小车就是当前小车，不需要判断，进行排除
         * 然后在在后面的运算中，需要过滤掉有小车的库位
         */

        // 1. 查询该仓库该楼层所有已连接的小车
        Condition carCondition = new Condition(RcsCarInfo.class);
        carCondition.createCriteria()
                .andEqualTo("wareCode", taskInfo.getWareCode())
                .andEqualTo("z", fromCellInfo.getZ());
        List<RcsCarInfo> allCarsInFloor = rcsCarInfoService.findByCondition(carCondition);

        Condition pathCon = new Condition(RcsCarPath.class);
        pathCon.createCriteria()
                .andEqualTo("wareCode", taskInfo.getWareCode())
                .andEqualTo("z", fromCellInfo.getZ());
        List<RcsCarPath> rcsCarPathList = rcsCarPathService.findByCondition(pathCon);


        // 2. 构建有小车占用的库位编码集合（排除当前小车）
        Set<String> occupiedCellCodes = new HashSet<>();
        if (allCarsInFloor != null && !allCarsInFloor.isEmpty()) {
            for (RcsCarPath rcsCarPath : rcsCarPathList) {
                // 任务不存在，清空路径占用
                JobInfo jobInfo11 = jobInfoService.findById(rcsCarPath.getJobId());
                if (jobInfo11 == null) {
                    clearPath(jobInfo);
                    continue;
                }

                // 任务重发，清空路径占用
                if (jobInfo11.getId().equals(jobInfo.getId())) {
                    clearPath(jobInfo);
                    continue;
                }

                // 小车路径上所有节点标记被占用（起点）
                Long fromCellId = rcsCarPath.getFromCellId();
                CellInfo fromCellInfo1 = cellInfoMap.get(fromCellId);
                if (fromCellInfo1 != null) {
                    occupiedCellCodes.add(fromCellInfo1.getCode());
                }

                // 小车路径上所有节点标记被占用（终点）
                Long toCellId = rcsCarPath.getToCellId();
                CellInfo toCellInfo1 = cellInfoMap.get(toCellId);
                if (toCellInfo1 != null) {
                    occupiedCellCodes.add(toCellInfo1.getCode());
                }
            }

            for (RcsCarInfo car : allCarsInFloor) {
                // 排除当前小车（当前小车就在起点，不能算作障碍）
                if (car.getId().equals(jobInfo.getRcsCarId())) {
                    continue;
                }

                // 如果小车有明确的位置信息，将其所在库位标记为占用
                if (car.getFromCellCode() != null && !car.getFromCellCode().trim().isEmpty()) {
                    occupiedCellCodes.add(car.getFromCellCode());

                    // 如果小车正在移动（fromCellCode != toCellCode），目标库位也要标记为占用
                    if (car.getToCellCode() != null &&
                            !car.getToCellCode().trim().isEmpty() &&
                            !car.getFromCellCode().equals(car.getToCellCode())) {
                        occupiedCellCodes.add(car.getToCellCode());
                    }
                }
            }

            if (!occupiedCellCodes.isEmpty()) {
                log.info("路径规划-检测到其他小车占用的库位: {}", occupiedCellCodes);
            }
        }

        // 查询仓库所有路径
        Condition condition = new Condition(CellLink.class);
        condition.createCriteria().andEqualTo("wareCode", taskInfo.getWareCode());
        List<CellLink> allCellLinks = cellLinkService.findByCondition(condition);
        if (allCellLinks == null || allCellLinks.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "fromCellCode：" + fromCellCode + "和toCellCode：" + toCellCode + "不存在路径");
            return false;
        }

        // ========== 过滤有货、禁用和有小车占用的库位 ==========
        // 1. 查询所有同层库位
        Condition cellCondition = new Condition(CellInfo.class);
        cellCondition.createCriteria()
                .andEqualTo("wareCode", taskInfo.getWareCode())
                .andEqualTo("z", fromCellInfo.getZ())
                .andEqualTo("disableState", 0)
                .andEqualTo("isDelete", 0);
        List<CellInfo> allCells = cellInfoService.findByCondition(cellCondition);

        // 2. 构建可通行库位ID集合（过滤有货、禁用和有小车占用的库位）
        Set<Long> passableCellIds = allCells.stream()
                .filter(cell -> {
                    // 起点必须可通行（业务需要：支持取货和放货任务）--如果终点有车呢？--终点需要校验
                    if (cell.getId().equals(fromCellInfo.getId())) {
                        return true;
                    }

                    // 如果被其他车辆占用--排除路径
                    if (occupiedCellCodes.contains(cell.getCode())) {
                        return false;
                    }

                    // 如果为终点且没有小车占用可通行
                    if(cell.getId().equals(toCellInfo.getId())){
                        return true;
                    }

                    // 过滤有货的库位--只有在进行搬运任务时需要
                    if (("move".equals(taskInfo.getType())
                            || "move1".equals(taskInfo.getType())
                            || "move2".equals(taskInfo.getType())
                    ) && cell.getInvenState() != null && cell.getInvenState() > 0) {
                        return false;
                    }

                    // 过滤禁用的库位
                    if (cell.getDisableState() != null && cell.getDisableState() > 0) {
                        return false;
                    }

                    // 过滤类型为5的库位（5表示提升机）
                    if (cell.getType() != null && cell.getType().equals(5)) {
                        return false;
                    }

                    return true;
                })
                .map(CellInfo::getId)
                .collect(Collectors.toSet());

        // 3. 过滤CellLink：只保留起点和终点都可通行的路径连接
        List<CellLink> cellLinkList = allCellLinks.stream()
                .filter(link -> {
                    // 起点必须可通行
                    if (!passableCellIds.contains(link.getFromCellId())) {
                        return false;
                    }
                    // 终点必须可通行
                    if (!passableCellIds.contains(link.getToCellId())) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // 4. 检查过滤后是否还有可用路径
        if (cellLinkList.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "fromCellCode：" + fromCellCode + "和toCellCode：" + toCellCode + "之间无可用路径（被有货/禁用库位阻挡）");
            return false;
        }

        log.info("路径规划：仓库[{}]，起点[{}]，终点[{}]，可通行库位数[{}]，过滤库位数[{}]",
                taskInfo.getWareCode(), fromCellCode, toCellCode,
                passableCellIds.size(), allCells.size() - passableCellIds.size());
        // ========== 过滤结束 ==========


        // 最短算法寻找路径
        LinkedList<CellInfo> cellInfoList = buildGraph(fromCellInfo, toCellInfo, cellLinkList);
        if (cellInfoList == null || cellInfoList.isEmpty()) {
            // 根据排除小车占用找不到路径
            jobInfoService.updateMemo(jobInfo, "fromCellCode：" + fromCellCode + "和toCellCode：" + toCellCode + "不存在路径");

            // 不排除小车查找一次路径（如果能生成-找到占用小车-移开）
            Set<Long> passableCellIds2 = allCells.stream()
                    .filter(cell -> {
                        // 起点和终点必须可通行（业务需要：支持取货和放货任务）
                        if (cell.getId().equals(fromCellInfo.getId())) {
                            return true;
                        }

                        // 如果为终点且没有小车占用可通行
                        if(cell.getId().equals(toCellInfo.getId())){
                            return true;
                        }

                        // 过滤有货的库位--只有在进行搬运任务时需要
                        if (("move".equals(taskInfo.getType())
                                || "move1".equals(taskInfo.getType())
                                || "move2".equals(taskInfo.getType())
                        ) && cell.getInvenState() != null && cell.getInvenState() > 0) {
                            return false;
                        }

                        // 过滤禁用的库位
                        if (cell.getDisableState() != null && cell.getDisableState() > 0) {
                            return false;
                        }

                        // 过滤类型为5的库位（5表示提升机）
                        if (cell.getType() != null && cell.getType().equals(5)) {
                            return false;
                        }

                        return true;
                    })
                    .map(CellInfo::getId)
                    .collect(Collectors.toSet());

            cellLinkList = allCellLinks.stream()
                    .filter(link -> {
                        // 起点必须可通行
                        if (!passableCellIds2.contains(link.getFromCellId())) {
                            return false;
                        }
                        // 终点必须可通行
                        if (!passableCellIds2.contains(link.getToCellId())) {
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            cellInfoList = buildGraph(fromCellInfo, toCellInfo, cellLinkList);
            if(!cellInfoList.isEmpty()){
                List<String> needMoveCellCodes = cellInfoList.stream().filter(cell->
                    occupiedCellCodes.contains(cell.getCode())
                ).map(CellInfo::getCode).collect(Collectors.toList());
                List<RcsCarInfo> needMoveCars = allCarsInFloor.stream()
                        .filter(car->needMoveCellCodes.contains(car.getFromCellCode())).collect(Collectors.toList());
                for(RcsCarInfo car:needMoveCars){
                    // 生成阻挡小车搬运任务
                    Condition existTaskCon = new Condition(TaskInfo.class);
                    existTaskCon.createCriteria().andEqualTo("rcsCarId", car.getId());
                    List<TaskInfo> taskInfos = taskInfoService.findByCondition(existTaskCon);
                    if (taskInfos.size() > 0) {
                        break;
                    }

                    TaskInfo moveTask = new TaskInfo();
                    moveTask.setWareCode(taskInfo.getWareCode());
                    moveTask.setType(CAR_MOVE_TASK_TYPE);
                    moveTask.setRcsCarId(car.getId());
                    moveTask.setCreateTime(DateUtil.getNowDateTimeString());
                    moveTask.setState(0);  // 初始状态

                    // 获取移动任务优先级
                    moveTask.setPriority(8);

                    taskInfoService.save(moveTask);
                    log.info("成功创建移动任务，小车：{}，从{}移动，任务ID：{}", car.getCode(), car.getFromCellCode(), moveTask.getId());
                }
            }

            return false;
        }


        // 生成路径段：遍历相邻节点对，生成from->to的路径
        for (int i = 0; i < cellInfoList.size() - 1; i++) {
            CellInfo fromCell = cellInfoList.get(i);
            CellInfo toCell = cellInfoList.get(i + 1);

            // 根据库位id从映射中获取完整的CellInfo（确保code等字段有值）
            CellInfo fullFromCell = cellInfoMap.get(fromCell.getId());
            CellInfo fullToCell = cellInfoMap.get(toCell.getId());

            if (fullFromCell == null || fullToCell == null) {
                log.warn("路径规划-无法找到完整的库位信息，fromCellId: {}, toCellId: {}",
                        fromCell.getId(), toCell.getId());
                // 如果映射中没有，尝试使用原对象（可能buildGraph已经返回了完整对象）
                fullFromCell = fromCell;
                fullToCell = toCell;
            }

            RcsCarPath rcsCarPath = new RcsCarPath();
            rcsCarPath.setRcsCarId(jobInfo.getRcsCarId());
            rcsCarPath.setFromCellId(fullFromCell.getId());
            rcsCarPath.setFromCellCode(fullFromCell.getCode());
            rcsCarPath.setToCellId(fullToCell.getId());
            rcsCarPath.setToCellCode(fullToCell.getCode());
            rcsCarPath.setWareCode(fullFromCell.getWareCode());
            rcsCarPath.setZ(fullFromCell.getZ());
            rcsCarPath.setJobId(jobInfo.getId());
            rcsCarPath.setTaskId(jobInfo.getTaskId());
            rcsCarPath.setState(0);
            rcsCarPathService.save(rcsCarPath);
        }

        String pathStr = cellInfoList.stream().map(CellInfo::getCode).collect(Collectors.joining("->"));
        jobInfoService.updateMemo(jobInfo, "成功分配路径：" + pathStr);
        return true;
    }


    /**
     * 同层job运行 在successPre执行
     */
    public Boolean pathRun(JobInfo jobInfo) {
        if (jobInfo.getRcsCarId() == null) {
            jobInfoService.updateMemo(jobInfo, "rcsCarId不能为空");
            return false;
        }
        if (jobInfo.getFromCellCode() == null || jobInfo.getFromCellCode().trim().isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "fromCellCode不能为空");
            return false;
        }
        if (jobInfo.getToCellCode() == null || jobInfo.getToCellCode().trim().isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "toCellCode不能为空");
            return false;
        }
        if (jobInfo.getFromCellCode().equals(jobInfo.getToCellCode())) {
            jobInfoService.updateMemo(jobInfo, "fromCellCode：" + jobInfo.getFromCellCode() + "和toCellCode：" + jobInfo.getToCellCode() + "相同");
            return true;
        }
        // 1、查询车路径
        Condition condition = new Condition(RcsCarPath.class);
        condition.createCriteria().andEqualTo("jobId", jobInfo.getId())
                .andEqualTo("taskId", jobInfo.getTaskId())
                .andEqualTo("rcsCarId", jobInfo.getRcsCarId());
        List<RcsCarPath> rcsCarPathList = rcsCarPathService.findByCondition(condition);
        if (rcsCarPathList == null || rcsCarPathList.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "jobId：" + jobInfo.getId() + "不存在路径");
            return false;
        }
        // 2、查询小车
        RcsCarInfo rcsCarInfo = rcsCarInfoService.findById(jobInfo.getRcsCarId());
        if (rcsCarInfo == null) {
            jobInfoService.updateMemo(jobInfo, "rcsCarId：" + jobInfo.getRcsCarId() + "不存在");
            return false;
        }
        /**
         * 1、根据RcsCarPath的nextId(下一个cellId)进行排序rcsCarPathList为linkedList
         * 2、RcsCarPath 状态为 0-未执行 1-已经占用未执行 2-已下发执行中 3-完成的
         * 如果存在1，则意味着还在执行
         * 如果不存在1 则说明需要进行占用路径了
         * 占用路径的逻辑为，遍历linkedList  ，根据每一个的cellId，查询有没有被其他路径给占用，
         */

        // 根据nextId排序路径
        LinkedList<RcsCarPath> sortedPathList = sortRcsCarPathByNextId(rcsCarPathList);

        // 检查是否已经在分配
        boolean isExecuting1 = sortedPathList.stream().anyMatch(path -> path.getState() == 1);
        // 检查正在执行中的点
        boolean isExecuting2 = sortedPathList.stream().anyMatch(path -> path.getState() == 2);

        boolean isNotAllot = sortedPathList.stream().anyMatch(path -> path.getState() == 0);

        //都跑完了
        if (!isExecuting1 && !isExecuting2 && !isNotAllot) {
            rcsCarInfo.setTaskState(0L);
            rcsCarInfoService.update(rcsCarInfo);
            jobInfoService.updateMemo(jobInfo, "整段路径已走完");
            return true;
        }
        if (!isExecuting1 && !isExecuting2) {
            // 占用路径 如果路径中停着一辆小车，则把该小车移开
            LinkedList<RcsCarPath> list = occupyPath(sortedPathList, jobInfo);
            String pathStr = "";
            int index = 0;

            /**
             * 如果占用路径失败，需要检查，该层的所有的路径是否互斥
             * 如果互斥，需要有避让算法
             * 路径中 rcsCarPath是路径  根据nextId排序，n是下一个cellId 通过rcsCarId来区分不同小车的路径  state来区分路径节点状态
             * 请你实现避让，
             */
            if (list.isEmpty()) {
                // 占用路径失败，执行避让算法
                LinkedList<RcsCarPath> avoidedPath = avoidConflict(sortedPathList, jobInfo);
                if (!avoidedPath.isEmpty()) {
                    list = avoidedPath;
                    jobInfoService.updateMemo(jobInfo, "避让成功，重新占用路径");
                } else {
                    jobInfoService.updateMemo(jobInfo, "路径被占用，等待避让");
                    return false;
                }
            }


            for (RcsCarPath path : list) {
                path.setState(1);
                rcsCarPathService.update(path);
                if (index == 0) {
                    pathStr += path.getFromCellCode();
                }
                pathStr += "->" + path.getToCellCode();
                index++;
            }
            jobInfoService.updateMemo(jobInfo, "占用路径：" + pathStr);
            return false;
        } else {
            return false;
        }
    }


    /**
     * 同层job运行 在successPre执行
     * <p>
     * 功能：
     * 1. 找到车子的rcs_car_path并根据nextId排序为linkedList
     * 2. 状态管理：1-已占用 2-下发执行中 3-完成的
     * 3. 如果全部节点state==3，返回true（任务完成）
     * 4. 如果存在执行中的，根据cmdTime判断是否超时(>2秒)，超时则更新为完成并发送WebSocket
     * 5. 如果不存在执行中的，将最近的一个节点改为执行中
     *
     * @param jobInfo 作业信息
     * @return true-任务完成，false-任务进行中
     */
    public Boolean testCar(JobInfo jobInfo) {
        try {
            pathRun(jobInfo);
            // 1. 查找该作业的所有路径
            Condition condition = new Condition(RcsCarPath.class);
            condition.createCriteria()
                    .andEqualTo("jobId", jobInfo.getId())
                    .andNotEqualTo("state", 3);  // 排除已完成的

            List<RcsCarPath> rcsCarPathList = rcsCarPathService.findByCondition(condition);

            if (rcsCarPathList == null || rcsCarPathList.isEmpty()) {
                log.warn("未找到作业 {} 的路径", jobInfo.getId());
                return true;  // 没有路径，认为完成
            }

            // 2. 根据nextId排序
            LinkedList<RcsCarPath> sortedPathList = sortRcsCarPathByNextId(rcsCarPathList);

            // 3. 检查是否全部完成
            boolean allCompleted = sortedPathList.stream()
                    .allMatch(path -> path.getState() == 3);

            if (allCompleted) {
                log.info("作业 {} 的所有路径已完成", jobInfo.getId());
                return true;
            }

            // 4. 查找正在执行的路径
            RcsCarPath executingPath = sortedPathList.stream()
                    .filter(path -> path.getState() == 2)
                    .findFirst()
                    .orElse(null);

            if (executingPath != null) {
                // 存在执行中的路径，检查是否超时
                String cmdTimeStr = executingPath.getCmdTime();
                if (cmdTimeStr != null && !cmdTimeStr.isEmpty()) {
                    try {
                        // 将String时间转换为Date进行比较
                        Date cmdTime = DateUtil.strToDate(cmdTimeStr, "yyyy-MM-dd HH:mm:ss");
                        long elapsedSeconds = (System.currentTimeMillis() - cmdTime.getTime()) / 1000;

                        if (elapsedSeconds > 5) {
                            // 超时，删除已完成的路径段
                            rcsCarPathService.deleteById(executingPath.getId());
                            log.info("路径段超时完成: fromCellId={} -> toCellId={}, 耗时={}秒",
                                    executingPath.getFromCellId(), executingPath.getToCellId(), elapsedSeconds);

                            // 更新小车位置并推送WebSocket
                            updateCarPositionAndPush(jobInfo, executingPath);

                            return false;  // 还有其他节点未完成
                        }
                    } catch (Exception e) {
                        log.error("解析cmdTime失败: {}", cmdTimeStr, e);
                    }
                }

                return false;  // 正在执行中，未超时
            } else {
                // 5. 不存在执行中的，找到下一个待执行的节点
                RcsCarPath nextPath = sortedPathList.stream()
                        .filter(path -> path.getState() == 1)  // 已占用未下发
                        .findFirst()
                        .orElse(null);

                if (nextPath != null) {
                    // 更新为执行中
                    nextPath.setState(2);
                    nextPath.setCmdTime(DateUtil.getNowDateTimeString());  // String格式
                    rcsCarPathService.update(nextPath);
                    log.info("启动路径段: fromCellId={} → toCellId={}",
                            nextPath.getFromCellId(), nextPath.getToCellId());


                }

                return false;  // 还有节点待执行
            }

        } catch (Exception e) {
            log.error("testCar执行失败: jobId={}", jobInfo.getId(), e);
            return false;
        }
    }

    public Boolean clearPath(JobInfo jobInfo) {
        Condition condition = new Condition(RcsCarPath.class);
        condition.createCriteria()
                .andEqualTo("jobId", jobInfo.getId());
        List<RcsCarPath> rcsCarPathList = rcsCarPathService.findByCondition(condition);
        for (RcsCarPath path : rcsCarPathList) {
            rcsCarPathService.deleteById(path.getId());
        }
        return true;

    }

    /**
     * 更新小车位置并推送WebSocket
     *
     * @param jobInfo     作业信息
     * @param currentPath 当前路径节点
     * @param
     */
    private void updateCarPositionAndPush(JobInfo jobInfo, RcsCarPath currentPath) {
        try {
            // 1. 查找小车信息
            RcsCarInfo carInfo = rcsCarInfoService.findById(jobInfo.getRcsCarId());
            if (carInfo == null) {
                log.warn("未找到小车: id={}", jobInfo.getRcsCarId());
                return;
            }
            // 2. 查找路径段的终点库位（小车已到达的位置）
            CellInfo toCell = cellInfoService.findById(currentPath.getToCellId());
            // 3. 更新小车的位置信息
            carInfo.setFromCellCode(toCell.getCode());
            carInfo.setToCellCode(toCell.getCode());
            carInfo.setPositionRatio(BigDecimal.ZERO);
            // 5. 保存到数据库
            rcsCarInfoService.update(carInfo);

            log.info("更新小车位置: car={}, from={}, to={}, ratio={}",
                    carInfo.getCode(), carInfo.getFromCellCode(),
                    carInfo.getToCellCode(), carInfo.getPositionRatio());

            // 6. 通过WebSocket推送位置更新到前端（使用新的优化版本）
            MonitorWebSocketHandler.pushCarPosition(carInfo);

        } catch (Exception e) {
            log.error("更新小车位置失败", e);
        }
    }

    /**
     * 根据fromCellId->toCellId对RcsCarPath列表进行排序
     * 路径段按照连接顺序排列：第一条的toCellId = 第二条的fromCellId
     */
    private LinkedList<RcsCarPath> sortRcsCarPathByNextId(List<RcsCarPath> rcsCarPathList) {
        LinkedList<RcsCarPath> sortedList = new LinkedList<>();
        if (rcsCarPathList == null || rcsCarPathList.isEmpty()) {
            return sortedList;
        }

        // 创建以fromCellId为key的映射，便于快速查找
        Map<Long, RcsCarPath> fromCellMap = new HashMap<>();
        Set<Long> allToCellIds = new HashSet<>();

        for (RcsCarPath path : rcsCarPathList) {
            fromCellMap.put(path.getFromCellId(), path);
            allToCellIds.add(path.getToCellId());
        }

        // 找到起始路径：其fromCellId不是任何路径的toCellId
        RcsCarPath startPath = null;
        for (RcsCarPath path : rcsCarPathList) {
            boolean isStart = true;
            for (RcsCarPath otherPath : rcsCarPathList) {
                if (otherPath.getToCellId() != null &&
                        otherPath.getToCellId().equals(path.getFromCellId())) {
                    isStart = false;
                    break;
                }
            }
            if (isStart) {
                startPath = path;
                break;
            }
        }

        // 如果没找到起始路径，使用第一个
        if (startPath == null && !rcsCarPathList.isEmpty()) {
            startPath = rcsCarPathList.get(0);
        }

        // 按照from->to的连接关系构建排序链表
        RcsCarPath current = startPath;
        Set<Long> visited = new HashSet<>();

        while (current != null && !visited.contains(current.getId())) {
            sortedList.add(current);
            visited.add(current.getId());

            // 查找下一条路径：fromCellId = 当前路径的toCellId
            Long nextFromCellId = current.getToCellId();
            current = fromCellMap.get(nextFromCellId);
        }

        // 如果还有未排序的路径（可能是断开的），追加到末尾
        for (RcsCarPath path : rcsCarPathList) {
            if (!visited.contains(path.getId())) {
                sortedList.add(path);
            }
        }

        return sortedList;
    }

    /**
     * 占用路径
     */
    private LinkedList<RcsCarPath> occupyPath(LinkedList<RcsCarPath> sortedPathList, JobInfo jobInfo) {
        int deletedCount = rcsCarPathService.deleteOrphanedPaths();
        LinkedList<RcsCarPath> allotPath = new LinkedList<>();
        // 遍历路径，检查是否被其他路径占用
        for (RcsCarPath path : sortedPathList) {
            if (path.getState() == 3) {
                continue;
            }
            if (path.getState() == 1) {
                throw new ServiceException("已经存在占用的未执行路径");
            }
            if (path.getState() == 2) {
                throw new ServiceException("已经存在占用的执行中路径");
            }

            List<RcsCarPath> occupiedPaths = rcsCarPathService.hasAllot(path);

            CellInfo fromCell = cellInfoService.findById(path.getFromCellId());
            Condition conditionCar = new Condition(RcsCarInfo.class);
            conditionCar.createCriteria()
                    .andNotEqualTo("id", path.getRcsCarId())
                    .andEqualTo("wareCode", fromCell.getWareCode())
                    .andEqualTo("fromCellCode", fromCell.getCode());
            List<RcsCarInfo> fromCarList = rcsCarInfoService.findByCondition(conditionCar);

            CellInfo toCell = cellInfoService.findById(path.getToCellId());
            conditionCar = new Condition(RcsCarInfo.class);
            conditionCar.createCriteria()
                    .andNotEqualTo("id", path.getRcsCarId())
                    .andEqualTo("wareCode", toCell.getWareCode())
                    .andEqualTo("toCellCode", toCell.getCode());

            List<RcsCarInfo> toCarList = rcsCarInfoService.findByCondition(conditionCar);


            if (!toCarList.isEmpty() || !fromCarList.isEmpty()) {

                /**
                 * 有车在路径上
                 * 如果这个车当前没有任务，需要移走
                 * 需要给这个车寻一个路径，然后生成移动任务，移走
                 */

                // 合并两个列表并去重
                Set<Long> processedCarIds = new HashSet<>();
                List<RcsCarInfo> allBlockingCars = new ArrayList<>();

                for (RcsCarInfo car : fromCarList) {
                    if (!processedCarIds.contains(car.getId())) {
                        allBlockingCars.add(car);
                        processedCarIds.add(car.getId());
                    }
                }

                for (RcsCarInfo car : toCarList) {
                    if (!processedCarIds.contains(car.getId())) {
                        allBlockingCars.add(car);
                        processedCarIds.add(car.getId());
                    }
                }

                // 遍历每个阻塞的车辆
                for (RcsCarInfo blockingCar : allBlockingCars) {
                    // 检查车辆是否有任务
                    if (!hasActiveTask(blockingCar.getId())) {
                        // 没有任务，需要移走
                        String wareCode = fromCell.getWareCode();
                        String carCurrentCellCode = blockingCar.getFromCellCode();

                        if (carCurrentCellCode == null || carCurrentCellCode.trim().isEmpty()) {
                            log.warn("车辆{}当前位置为空，无法移走", blockingCar.getCode());
                            continue;
                        }

                        // 获取车辆当前库位信息
                        CellInfo carCurrentCell = cellInfoService.findByCode(wareCode, carCurrentCellCode);
                        if (carCurrentCell == null) {
                            log.warn("车辆{}当前位置库位{}不存在", blockingCar.getCode(), carCurrentCellCode);
                            continue;
                        }
                        /**
                         * 遍历所有库位，然后查找最短路径，直到找到一个位置，然后生成移动任务
                         */

                        // 检查是否有路径信息
                        Condition linkCondition = new Condition(CellLink.class);
                        linkCondition.createCriteria().andEqualTo("wareCode", wareCode);
                        List<CellLink> cellLinkList = cellLinkService.findByCondition(linkCondition);

                        if (cellLinkList == null || cellLinkList.isEmpty()) {
                            log.warn("仓库{}没有路径信息，无法为车辆{}寻路", wareCode, blockingCar.getCode());
                            continue;
                        }

                        // 查询所有符合条件的空闲库位（无货、无任务、不被禁用）
                        Condition cellCondition = new Condition(CellInfo.class);
                        cellCondition.createCriteria()
                                .andEqualTo("wareCode", wareCode)
                                .andEqualTo("invenState", 0L)  // 无货
                                .andEqualTo("taskState", 0L)   // 无任务
                                .andEqualTo("disableState", 0L)  // 不被禁用
                                .andEqualTo("isDelete", 0);
                        List<CellInfo> allIdleCells = cellInfoService.findByCondition(cellCondition);

                        if (allIdleCells == null || allIdleCells.isEmpty()) {
                            log.warn("未找到车辆{}可用的空闲库位，无法移走", blockingCar.getCode());
                            continue;
                        }

                        // 过滤掉被其他小车占用的库位
                        List<CellInfo> availableCells = new ArrayList<>();
                        for (CellInfo cell : allIdleCells) {
                            // 排除车辆当前位置
                            if (cell.getCode().equals(carCurrentCellCode)) {
                                continue;
                            }

                            // 检查是否有小车在该位置（排除当前车辆）
                            Condition carAtCellCondition = new Condition(RcsCarInfo.class);
                            carAtCellCondition.createCriteria()
                                    .andEqualTo("wareCode", wareCode)
                                    .andEqualTo("fromCellCode", cell.getCode())
                                    .andNotEqualTo("id", blockingCar.getId());
                            List<RcsCarInfo> carsAtCell = rcsCarInfoService.findByCondition(carAtCellCondition);

                            // 也检查toCellCode，因为车辆可能在移动中
                            Condition carToCellCondition = new Condition(RcsCarInfo.class);
                            carToCellCondition.createCriteria()
                                    .andEqualTo("wareCode", wareCode)
                                    .andEqualTo("toCellCode", cell.getCode())
                                    .andNotEqualTo("id", blockingCar.getId());
                            List<RcsCarInfo> carsToCell = rcsCarInfoService.findByCondition(carToCellCondition);


                            if (!carsAtCell.isEmpty() || !carsToCell.isEmpty()) {
                                continue;
                            }

                            List<RcsCarPath> carPaths = rcsCarPathService.findAll();
                            for (RcsCarPath carPath : carPaths) {
                                if (carPath.getFromCellId().equals(cell.getId()) || carPath.getToCellId().equals(cell.getId())) {
                                    break;
                                }
                            }

                            availableCells.add(cell);
                        }

                        if (availableCells.isEmpty()) {
                            log.warn("未找到车辆{}可用的空闲库位（所有库位都被占用），无法移走", blockingCar.getCode());
                            continue;
                        }

                        // 遍历所有可用库位，查找最短路径
                        CellInfo bestCell = null;
                        LinkedList<CellInfo> shortestPath = null;
                        int shortestPathLength = Integer.MAX_VALUE;

                        for (CellInfo targetCell : availableCells) {
                            // 使用buildGraph寻路
                            LinkedList<CellInfo> movePath = buildGraph(carCurrentCell, targetCell, cellLinkList);

                            if (movePath != null && !movePath.isEmpty()) {
                                // 路径长度等于路径中节点的数量
                                int pathLength = movePath.size();

                                // 如果找到更短的路径，更新最佳库位
                                if (pathLength < shortestPathLength) {
                                    shortestPathLength = pathLength;
                                    bestCell = targetCell;
                                    shortestPath = movePath;
                                }
                            }
                        }

                        // 如果找到了最短路径的库位，创建移动任务
                        if (bestCell != null && shortestPath != null) {
                            createMoveTaskForCar(blockingCar, carCurrentCellCode, bestCell.getCode(), wareCode);
                            log.info("为车辆{}创建移动任务，从{}移动到{}（路径长度：{}）",
                                    blockingCar.getCode(), carCurrentCellCode, bestCell.getCode(), shortestPathLength);
                        } else {
                            log.warn("车辆{}从{}无法找到任何可达的空闲库位", blockingCar.getCode(), carCurrentCellCode);
                        }
                    }
                }

                return allotPath;
            }


            if (!occupiedPaths.isEmpty()) {
                return allotPath;
            }

            allotPath.add(path);
        }
        return allotPath;
    }


    private LinkedList<CellInfo> buildGraph(CellInfo fromCellInfo, CellInfo toCellInfo, List<CellLink> cellLinkList) {
        LinkedList<CellInfo> path = new LinkedList<>();

        // 使用Dijkstra算法查找最短路径
        if (fromCellInfo == null || toCellInfo == null || cellLinkList == null || cellLinkList.isEmpty()) {
            return path;
        }

        // 过滤掉阻塞的路径
        List<CellLink> validLinks = cellLinkList.stream()
                .filter(link -> link.getIsBlocked() == null || link.getIsBlocked() == 0)
                .collect(Collectors.toList());

        // 构建图结构：节点ID -> 邻接关系列表
        Map<Long, List<CellLink>> graph = new HashMap<>();
        for (CellLink link : validLinks) {
            graph.computeIfAbsent(link.getFromCellId(), k -> new ArrayList<>()).add(link);
        }

        // Dijkstra算法数据结构
        Map<Long, BigDecimal> distances = new HashMap<>(); // 到每个节点的最短距离
        Map<Long, Long> previous = new HashMap<>(); // 前驱节点
        Set<Long> visited = new HashSet<>(); // 已访问节点

        // 使用优先队列（最小堆）来优化性能
        PriorityQueue<Long> queue = new PriorityQueue<>(
                Comparator.comparing(node -> distances.getOrDefault(node, BigDecimal.valueOf(Double.MAX_VALUE)))
        );

        // 初始化距离
        distances.put(fromCellInfo.getId(), BigDecimal.ZERO);
        queue.add(fromCellInfo.getId());

        // 执行Dijkstra算法
        while (!queue.isEmpty()) {
            Long current = queue.poll();

            // 如果已经访问过，跳过
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            // 如果到达目标节点，构建路径
            if (current.equals(toCellInfo.getId())) {
                break;
            }

            // 处理当前节点的所有邻接边
            List<CellLink> neighbors = graph.get(current);
            if (neighbors != null) {
                for (CellLink link : neighbors) {
                    Long neighborId = link.getToCellId();

                    // 如果邻居节点已经被访问过，跳过
                    if (visited.contains(neighborId)) {
                        continue;
                    }

                    BigDecimal currentDistance = distances.get(current);
                    BigDecimal edgeDistance = link.getDistance() != null ? link.getDistance() : BigDecimal.ONE;
                    BigDecimal newDistance = currentDistance.add(edgeDistance);

                    // 如果找到更短的路径，更新距离和前驱
                    if (!distances.containsKey(neighborId) || newDistance.compareTo(distances.get(neighborId)) < 0) {
                        distances.put(neighborId, newDistance);
                        previous.put(neighborId, current);
                        queue.add(neighborId);
                    }
                }
            }
        }

        // 构建路径（从目标节点回溯到起始节点）
        if (previous.containsKey(toCellInfo.getId())) {
            Stack<Long> reversePath = new Stack<>();
            Long current = toCellInfo.getId();

            // 回溯构建路径
            while (current != null) {
                reversePath.push(current);
                current = previous.get(current);
            }

            // 将路径转换为CellInfo对象列表
            while (!reversePath.isEmpty()) {
                Long cellId = reversePath.pop();

                // 根据cellId查找对应的CellInfo对象
                CellInfo cell = cellInfoService.findById(cellId);
                path.add(cell);
            }
        }

        return path;
    }

    /**
     * 避让算法：当路径占用失败时，检查冲突并实现避让
     *
     * @param sortedPathList 当前小车的排序路径列表
     * @param jobInfo        当前作业信息
     * @return 避让后可以占用的路径列表，如果无法避让则返回空列表
     */
    private LinkedList<RcsCarPath> avoidConflict(LinkedList<RcsCarPath> sortedPathList, JobInfo jobInfo) {
        LinkedList<RcsCarPath> result = new LinkedList<>();

        if (sortedPathList == null || sortedPathList.isEmpty()) {
            return result;
        }

        // 1. 获取当前任务信息
        TaskInfo currentTask = taskInfoService.findById(jobInfo.getTaskId());
        if (currentTask == null) {
            log.warn("避让算法-任务不存在: taskId={}", jobInfo.getTaskId());
            return result;
        }

        // 2. 获取当前小车信息
        RcsCarInfo currentCar = rcsCarInfoService.findById(jobInfo.getRcsCarId());
        if (currentCar == null) {
            log.warn("避让算法-小车不存在: rcsCarId={}", jobInfo.getRcsCarId());
            return result;
        }

        // 3. 获取当前路径的第一个库位信息（用于确定层）
        RcsCarPath firstPath = sortedPathList.getFirst();
        CellInfo firstCell = cellInfoService.findById(firstPath.getFromCellId());
        if (firstCell == null) {
            log.warn("避让算法-库位不存在: cellId={}", firstPath.getFromCellId());
            return result;
        }

        // 4. 查询该层所有小车的路径（排除当前小车）
        Condition condition = new Condition(RcsCarPath.class);
        condition.createCriteria()
                .andEqualTo("wareCode", currentTask.getWareCode())
                .andEqualTo("z", firstCell.getZ())
                .andNotEqualTo("rcsCarId", jobInfo.getRcsCarId())
                .andIn("state", Arrays.asList(0, 1, 2)); // 查询未执行、已占用或执行中的路径
        List<RcsCarPath> allPathsInFloor = rcsCarPathService.findByCondition(condition);

        if (allPathsInFloor == null || allPathsInFloor.isEmpty()) {
            // 没有其他小车的路径，理论上不应该出现这种情况（因为occupyPath已经检查过了）
            log.warn("避让算法-未找到其他小车的路径，但占用失败");
            return result;
        }

        // 5. 找出冲突的路径段和冲突的小车，并检测互锁场景
        Map<Long, ConflictInfo> conflictMap = new HashMap<>(); // rcsCarId -> 冲突信息

        for (RcsCarPath currentPath : sortedPathList) {
            if (currentPath.getState() == 3) {
                continue; // 已完成的路径段跳过
            }

            // 检查当前路径段是否与其他小车路径冲突
            // 注意：hasAllot只检查state=1,2的路径，我们需要同时检查state=0的路径
            List<RcsCarPath> occupiedPaths = rcsCarPathService.hasAllot(currentPath);

            // 额外检查state=0的路径冲突（未执行但已规划的路径）
            for (RcsCarPath otherPath : allPathsInFloor) {
                if (otherPath.getState() == 0) { // 只检查未执行的路径
                    // 检查路径冲突：起点或终点相同
                    boolean isConflict = false;
                    if (currentPath.getFromCellId().equals(otherPath.getFromCellId()) ||
                            currentPath.getFromCellId().equals(otherPath.getToCellId()) ||
                            currentPath.getToCellId().equals(otherPath.getFromCellId()) ||
                            currentPath.getToCellId().equals(otherPath.getToCellId())) {
                        isConflict = true;
                    }

                    if (isConflict) {
                        // 检查是否已经在occupiedPaths中
                        boolean alreadyInOccupied = occupiedPaths.stream()
                                .anyMatch(p -> p.getId().equals(otherPath.getId()));
                        if (!alreadyInOccupied) {
                            occupiedPaths.add(otherPath);
                        }
                    }
                }
            }

            if (!occupiedPaths.isEmpty()) {
                // 找到冲突路径，按小车ID分组
                for (RcsCarPath conflictPath : occupiedPaths) {
                    Long conflictCarId = conflictPath.getRcsCarId();
                    ConflictInfo conflictInfo = conflictMap.get(conflictCarId);
                    if (conflictInfo == null) {
                        conflictInfo = new ConflictInfo();
                        conflictInfo.setConflictCarId(conflictCarId);
                        conflictInfo.setConflictPaths(new ArrayList<>());
                        conflictInfo.setDeadlockPaths(new ArrayList<>());
                        conflictMap.put(conflictCarId, conflictInfo);
                    }
                    // 避免重复添加相同的冲突路径
                    boolean exists = conflictInfo.getConflictPaths().stream()
                            .anyMatch(p -> p.getId().equals(conflictPath.getId()));
                    if (!exists) {
                        conflictInfo.getConflictPaths().add(conflictPath);
                        conflictInfo.setMaxState(Math.max(conflictInfo.getMaxState(), conflictPath.getState()));

                        // 检测互锁：当前路径 A->B，冲突路径 B->A
                        if (currentPath.getFromCellId().equals(conflictPath.getToCellId()) &&
                                currentPath.getToCellId().equals(conflictPath.getFromCellId())) {
                            conflictInfo.getDeadlockPaths().add(conflictPath);
                            conflictInfo.setHasDeadlock(true);
                            log.warn("避让算法-检测到互锁：小车{}路径{}->{}与小车{}路径{}->{}相对而行",
                                    jobInfo.getRcsCarId(), currentPath.getFromCellCode(), currentPath.getToCellCode(),
                                    conflictCarId, conflictPath.getFromCellCode(), conflictPath.getToCellCode());
                        }
                    }
                }
            }
        }

        if (conflictMap.isEmpty()) {
            log.warn("避让算法-未找到冲突路径，但占用失败");
            return result;
        }

        // 6. 分析冲突并实现避让策略
        for (Map.Entry<Long, ConflictInfo> entry : conflictMap.entrySet()) {
            Long conflictCarId = entry.getKey();
            ConflictInfo conflictInfo = entry.getValue();

            // 获取冲突小车的任务信息
            RcsCarPath conflictPath = conflictInfo.getConflictPaths().get(0);
            JobInfo conflictJob = jobInfoService.findById(conflictPath.getJobId());
            if (conflictJob == null) {
                continue;
            }

            TaskInfo conflictTask = taskInfoService.findById(conflictJob.getTaskId());
            if (conflictTask == null) {
                continue;
            }

            // 获取冲突小车信息
            RcsCarInfo conflictCar = rcsCarInfoService.findById(conflictCarId);
            if (conflictCar == null) {
                continue;
            }

            // 比较优先级：priority越小优先级越高
            int currentPriority = currentTask.getPriority() != null ? currentTask.getPriority() : Integer.MAX_VALUE;
            int conflictPriority = conflictTask.getPriority() != null ? conflictTask.getPriority() : Integer.MAX_VALUE;

            // 7. 避让决策：综合考虑优先级、互锁情况、距离等因素
            boolean shouldYield = false; // 当前小车是否应该让路
            boolean shouldDeleteConflictPath = false; // 是否需要删除冲突小车的路径

            // 7.1 如果冲突小车正在执行中（state=2），必须让路
            if (conflictInfo.getMaxState() == 2) {
                shouldYield = true;
                log.info("避让算法-冲突小车{}正在执行中，当前小车{}必须让路",
                        conflictCarId, jobInfo.getRcsCarId());
            }
            // 7.2 如果当前任务优先级更低，需要让路
            else if (currentPriority > conflictPriority) {
                shouldYield = true;
                log.info("避让算法-当前任务优先级更低（{} > {}），当前小车{}需要让路",
                        currentPriority, conflictPriority, jobInfo.getRcsCarId());
            }
            // 7.3 如果当前任务优先级更高，删除冲突小车的路径
            else if (currentPriority < conflictPriority) {
                shouldDeleteConflictPath = true;
                log.info("避让算法-当前任务优先级更高（{} < {}），删除冲突小车{}的路径",
                        currentPriority, conflictPriority, conflictCarId);
            }
            // 7.4 如果优先级相同，进一步判断
            else if (currentPriority == conflictPriority) {
                // 7.4.1 如果存在互锁，比较距离冲突点的距离
                if (conflictInfo.isHasDeadlock()) {
                    // 计算当前小车到冲突点的距离
                    int currentDistance = calculateDistanceToConflict(sortedPathList, conflictInfo);
                    // 计算冲突小车到冲突点的距离
                    int conflictDistance = calculateConflictCarDistance(conflictCarId, conflictInfo, currentTask.getWareCode(), firstCell.getZ());

                    if (currentDistance > conflictDistance) {
                        // 当前小车距离冲突点更远，让路
                        shouldYield = true;
                        log.info("避让算法-互锁场景，当前小车{}距离冲突点{}步，冲突小车{}距离{}步，当前小车让路",
                                jobInfo.getRcsCarId(), currentDistance, conflictCarId, conflictDistance);
                    } else if (currentDistance < conflictDistance) {
                        // 当前小车距离冲突点更近，删除冲突小车的路径
                        shouldDeleteConflictPath = true;
                        log.info("避让算法-互锁场景，当前小车{}距离冲突点{}步，冲突小车{}距离{}步，删除冲突小车路径",
                                jobInfo.getRcsCarId(), currentDistance, conflictCarId, conflictDistance);
                    } else {
                        // 距离相同，比较创建时间（先创建的先走）
                        if (currentTask.getCreateTime() != null && conflictTask.getCreateTime() != null) {
                            try {
                                Date currentCreateTime = DateUtil.strToDate(currentTask.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
                                Date conflictCreateTime = DateUtil.strToDate(conflictTask.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
                                if (currentCreateTime.after(conflictCreateTime)) {
                                    shouldYield = true;
                                    log.info("避让算法-互锁场景，距离相同，当前任务创建时间更晚，当前小车{}让路",
                                            jobInfo.getRcsCarId());
                                } else {
                                    shouldDeleteConflictPath = true;
                                    log.info("避让算法-互锁场景，距离相同，当前任务创建时间更早，删除冲突小车{}路径",
                                            conflictCarId);
                                }
                            } catch (Exception e) {
                                log.warn("避让算法-解析创建时间失败", e);
                                // 解析失败时，默认让当前小车让路（保守策略）
                                shouldYield = true;
                            }
                        } else {
                            // 创建时间为空，默认让当前小车让路（保守策略）
                            shouldYield = true;
                        }
                    }
                } else {
                    // 非互锁场景
                    if (conflictInfo.getMaxState() == 1) {
                        // 冲突小车已占用（state=1），当前小车让路
                        shouldYield = true;
                        log.info("避让算法-优先级相同，冲突小车{}已占用路径，当前小车{}让路",
                                conflictCarId, jobInfo.getRcsCarId());
                    } else if (conflictInfo.getMaxState() == 0) {
                        // 冲突小车也是未执行（state=0），比较创建时间
                        if (currentTask.getCreateTime() != null && conflictTask.getCreateTime() != null) {
                            try {
                                Date currentCreateTime = DateUtil.strToDate(currentTask.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
                                Date conflictCreateTime = DateUtil.strToDate(conflictTask.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
                                if (currentCreateTime.after(conflictCreateTime)) {
                                    shouldYield = true;
                                    log.info("避让算法-优先级相同，当前任务创建时间更晚，当前小车{}让路",
                                            jobInfo.getRcsCarId());
                                } else {
                                    shouldDeleteConflictPath = true;
                                    log.info("避让算法-优先级相同，当前任务创建时间更早，删除冲突小车{}路径",
                                            conflictCarId);
                                }
                            } catch (Exception e) {
                                log.warn("避让算法-解析创建时间失败", e);
                                shouldYield = true;
                            }
                        } else {
                            // 创建时间为空，默认让当前小车让路
                            shouldYield = true;
                        }
                    }
                }
            }

            // 8. 执行避让决策
            if (shouldYield) {
                // 当前小车让路，返回空列表等待
                log.info("避让算法-当前小车{}需要让路给冲突小车{}", jobInfo.getRcsCarId(), conflictCarId);
                return result;
            } else if (shouldDeleteConflictPath) {
                // 当前小车优先级更高或距离更近，删除冲突小车的路径
                log.info("避让算法-当前小车{}优先级更高或距离更近，删除冲突小车{}的冲突路径",
                        jobInfo.getRcsCarId(), conflictCarId);

                // 删除冲突小车的冲突路径段
                for (RcsCarPath path : conflictInfo.getConflictPaths()) {
                    if (path.getState() == 0 || path.getState() == 1) {
                        // 删除未执行或已占用的冲突路径
                        rcsCarPathService.deleteById(path.getId());
                        log.info("避让算法-删除冲突路径：小车{}，路径{}->{}",
                                conflictCarId, path.getFromCellCode(), path.getToCellCode());
                    }
                }

                // 记录避让信息
                jobInfoService.updateMemo(conflictJob,
                        "路径被优先级更高或距离更近的任务占用，已删除冲突路径段，需要重新规划");

                // 注意：删除路径后，冲突小车需要重新规划路径，但这不在当前方法的职责范围内
            } else {
                // 当前小车优先级更高或距离更近，释放冲突小车的路径（state=1的情况）
                log.info("避让算法-当前小车{}优先级更高或距离更近，释放冲突小车{}的路径",
                        jobInfo.getRcsCarId(), conflictCarId);

                // 释放冲突小车的已占用路径（设置为未执行状态）
                for (RcsCarPath path : conflictInfo.getConflictPaths()) {
                    if (path.getState() == 1) { // 只释放已占用但未执行的路径
                        path.setState(0);
                        rcsCarPathService.update(path);
                        log.debug("避让算法-释放冲突路径：小车{}，路径{}->{}",
                                conflictCarId, path.getFromCellCode(), path.getToCellCode());
                    }
                }

                // 记录避让信息
                jobInfoService.updateMemo(conflictJob,
                        "路径被优先级更高或距离更近的任务占用，已释放路径等待避让");
            }
        }

        // 7. 避让成功后，重新尝试占用路径
        result = occupyPath(sortedPathList, jobInfo);

        return result;
    }

    /**
     * 计算当前小车到冲突点的距离（路径步数）
     */
    private int calculateDistanceToConflict(LinkedList<RcsCarPath> sortedPathList, ConflictInfo conflictInfo) {
        if (conflictInfo.getDeadlockPaths().isEmpty()) {
            return 0;
        }

        RcsCarPath conflictPath = conflictInfo.getDeadlockPaths().get(0);
        Long conflictCellId = conflictPath.getFromCellId(); // 冲突点

        // 查找当前路径中第一个到达冲突点的路径段索引
        int distance = 0;
        for (RcsCarPath path : sortedPathList) {
            if (path.getState() == 3) {
                continue; // 已完成的路径段不计入距离
            }
            if (path.getToCellId().equals(conflictCellId)) {
                break; // 找到冲突点
            }
            distance++;
        }

        return distance;
    }

    /**
     * 计算冲突小车到冲突点的距离（路径步数）
     */
    private int calculateConflictCarDistance(Long conflictCarId, ConflictInfo conflictInfo, String wareCode, Integer z) {
        if (conflictInfo.getDeadlockPaths().isEmpty()) {
            return 0;
        }

        // 查询冲突小车的所有路径
        Condition condition = new Condition(RcsCarPath.class);
        condition.createCriteria()
                .andEqualTo("rcsCarId", conflictCarId)
                .andEqualTo("wareCode", wareCode)
                .andEqualTo("z", z)
                .andNotEqualTo("state", 3); // 排除已完成的路径
        List<RcsCarPath> conflictCarPaths = rcsCarPathService.findByCondition(condition);

        if (conflictCarPaths == null || conflictCarPaths.isEmpty()) {
            return Integer.MAX_VALUE; // 如果找不到路径，返回最大值（让当前小车让路）
        }

        // 排序冲突小车的路径
        LinkedList<RcsCarPath> sortedConflictPaths = sortRcsCarPathByNextId(conflictCarPaths);

        RcsCarPath conflictPath = conflictInfo.getDeadlockPaths().get(0);
        Long conflictCellId = conflictPath.getToCellId(); // 冲突点（从冲突小车的角度看）

        // 查找冲突小车路径中第一个到达冲突点的路径段索引
        int distance = 0;
        for (RcsCarPath path : sortedConflictPaths) {
            if (path.getState() == 3) {
                continue; // 已完成的路径段不计入距离
            }
            if (path.getToCellId().equals(conflictCellId)) {
                break; // 找到冲突点
            }
            distance++;
        }

        return distance;
    }

    /**
     * 冲突信息内部类
     */
    private static class ConflictInfo {
        private Long conflictCarId;
        private List<RcsCarPath> conflictPaths; // 所有冲突路径
        private List<RcsCarPath> deadlockPaths; // 互锁路径（相对而行的路径）
        private Integer maxState = 0; // 冲突路径中的最大状态值
        private boolean hasDeadlock = false; // 是否存在互锁

        public Long getConflictCarId() {
            return conflictCarId;
        }

        public void setConflictCarId(Long conflictCarId) {
            this.conflictCarId = conflictCarId;
        }

        public List<RcsCarPath> getConflictPaths() {
            return conflictPaths;
        }

        public void setConflictPaths(List<RcsCarPath> conflictPaths) {
            this.conflictPaths = conflictPaths;
        }

        public List<RcsCarPath> getDeadlockPaths() {
            return deadlockPaths;
        }

        public void setDeadlockPaths(List<RcsCarPath> deadlockPaths) {
            this.deadlockPaths = deadlockPaths;
        }

        public Integer getMaxState() {
            return maxState;
        }

        public void setMaxState(Integer maxState) {
            this.maxState = maxState;
        }

        public boolean isHasDeadlock() {
            return hasDeadlock;
        }

        public void setHasDeadlock(boolean hasDeadlock) {
            this.hasDeadlock = hasDeadlock;
        }
    }

    /**
     * 检查车辆是否有活跃任务
     *
     * @param rcsCarId 车辆ID
     * @return true-有任务，false-无任务
     */
    private boolean hasActiveTask(Long rcsCarId) {
        // 检查TaskInfo中是否有该小车的未完成任务
        Condition taskCondition = new Condition(TaskInfo.class);
        taskCondition.createCriteria()
                .andEqualTo("rcsCarId", rcsCarId);
        List<TaskInfo> tasks = taskInfoService.findByCondition(taskCondition);
        if (tasks != null && !tasks.isEmpty()) {
            // 检查是否有未完成的任务（state != 3表示未完成）
            for (TaskInfo task : tasks) {
                if (task.getState() == null || task.getState() != 3) {
                    return true;
                }
            }
        }

        // 检查JobInfo中是否有该小车的未完成任务
        Condition jobCondition = new Condition(JobInfo.class);
        jobCondition.createCriteria()
                .andEqualTo("rcsCarId", rcsCarId);
        List<JobInfo> jobs = jobInfoService.findByCondition(jobCondition);
        if (jobs != null && !jobs.isEmpty()) {
            // 检查是否有未完成的作业
            for (JobInfo job : jobs) {
                if (job.getState() == null || job.getState() != 3) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 为车辆查找空闲库位
     *
     * @param wareCode     仓库代码
     * @param z            楼层
     * @param excludeCarId 排除的车辆ID（当前车辆）
     * @return 空闲库位，如果找不到返回null
     */
    private CellInfo findIdleCellForCar(String wareCode, Integer z, Long excludeCarId) {
        // 查询同层、无货、无任务、不被禁用的库位
        Condition cellCondition = new Condition(CellInfo.class);
        cellCondition.createCriteria()
                .andEqualTo("wareCode", wareCode)
                .andEqualTo("z", z)
                .andEqualTo("invenState", 0L)  // 无货
                .andEqualTo("taskState", 0L)   // 无任务
                .andEqualTo("disableState", 0L)  // 不被禁用
                .andEqualTo("isDelete", 0);
        cellCondition.orderBy("inTime");
        List<CellInfo> idleCells = cellInfoService.findByCondition(cellCondition);

        if (idleCells == null || idleCells.isEmpty()) {
            return null;
        }

        // 检查是否有小车在该位置（排除当前车辆）
        for (CellInfo cell : idleCells) {
            Condition carAtCellCondition = new Condition(RcsCarInfo.class);
            carAtCellCondition.createCriteria()
                    .andEqualTo("wareCode", wareCode)
                    .andEqualTo("fromCellCode", cell.getCode())
                    .andNotEqualTo("id", excludeCarId);
            List<RcsCarInfo> carsAtCell = rcsCarInfoService.findByCondition(carAtCellCondition);

            // 也检查toCellCode，因为车辆可能在移动中
            Condition carToCellCondition = new Condition(RcsCarInfo.class);
            carToCellCondition.createCriteria()
                    .andEqualTo("wareCode", wareCode)
                    .andEqualTo("toCellCode", cell.getCode())
                    .andNotEqualTo("id", excludeCarId);
            List<RcsCarInfo> carsToCell = rcsCarInfoService.findByCondition(carToCellCondition);

            if ((carsAtCell == null || carsAtCell.isEmpty()) &&
                    (carsToCell == null || carsToCell.isEmpty())) {
                return cell;  // 找到空闲位置
            }
        }

        return null;
    }

    @Value("${isTest}")
    private Boolean isTest;

    /**
     * 为车辆创建移动任务
     *
     * @param car          车辆信息
     * @param fromCellCode 起始库位编码
     * @param toCellCode   目标库位编码
     * @param wareCode     仓库代码
     */
    private void createMoveTaskForCar(RcsCarInfo car, String fromCellCode, String toCellCode, String wareCode) {
        try {
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setWareCode(wareCode);
            if (isTest) {
                taskInfo.setType("testPath");
            } else {
                taskInfo.setType("car_move");
            }

            taskInfo.setRcsCarId(car.getId());
            taskInfo.setFromCellCode(fromCellCode);
            taskInfo.setToCellCode(toCellCode);
            taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
            taskInfo.setState(0);  // 初始状态
            taskInfo.setPriority(10);  // 默认优先级

            taskInfoService.save(taskInfo);
            log.info("成功创建移动任务，车辆：{}，从{}移动到{}，任务ID：{}", car.getCode(), fromCellCode, toCellCode, taskInfo.getId());
        } catch (Exception e) {
            log.error("创建移动任务失败，车辆：{}，从{}移动到{}", car.getCode(), fromCellCode, toCellCode, e);
        }
    }


    /**
     * 分配小车可停放位置作为终点
     */
    public Boolean allotCarStopTaskTo(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        RcsCarInfo carInfo = rcsCarInfoService.findById(taskInfo.getRcsCarId());

        List<RcsCarInfo> allCars = rcsCarInfoService.findByWareCode(taskInfo.getWareCode());
        allCars.stream().filter(item -> item.getId() != taskInfo.getRcsCarId());

        int floor = 1;
        // 说明是避障任务
        if(taskInfo.getPriority()!=null && taskInfo.getPriority()==8){
            floor = carInfo.getCurrentZ();
        }else{
            // 1. 确定层数
            int floor1CarNum = allCars.stream().filter(item -> item.getCurrentZ() == 1).collect(Collectors.toList()).size();
            int floor3CarNum = allCars.stream().filter(item -> item.getCurrentZ() == 3).collect(Collectors.toList()).size();
            if (floor1CarNum < 2) {
                floor = 1;
            } else if (floor3CarNum < 1) {
                floor = 3;
            } else {
                floor = 2;
            }
        }

        // 1. 为移动任务分配位置
        CellInfo allotCell = cellInfoService.allotLocForSxc(taskInfo.getWareCode(), floor);
        if (allotCell == null) {
            jobInfoService.updateMemo(jobInfo, String.format("找不到小车[%s]可停放位置", carInfo.getCode()));
            return false;
        }

        taskInfo.setToCellCode(allotCell.getCode());
        taskInfoService.update(taskInfo);

        jobInfo.setToCellCode(allotCell.getCode());
        jobInfoService.updateMemo(jobInfo, String.format("小车[%s]成功分配临时停放点[%s]", carInfo.getCode(), allotCell.getCode()));
        jobInfoService.update(jobInfo);

        return true;
    }

}