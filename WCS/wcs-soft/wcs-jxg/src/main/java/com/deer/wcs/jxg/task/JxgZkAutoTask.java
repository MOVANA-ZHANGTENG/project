package com.deer.wcs.jxg.task;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.FloorInfoService;
import com.deer.wcs.base.service.PalletInfoService;
import com.deer.wcs.base.service.TaskPriorityService;
import com.deer.wcs.common.enums.CellTypeEnum;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.model.RcsCarType;
import com.deer.wcs.rcs.service.RcsCarInfoService;
import com.deer.wcs.rcs.service.RcsCarTypeService;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component("JxgZkAutoTask")
public class JxgZkAutoTask {

    private static final Logger log = LoggerFactory.getLogger(JxgZkAutoTask.class);

    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private RcsCarInfoService rcsCarInfoService;

    @Autowired
    private PalletInfoService palletInfoService;

    @Autowired
    private TaskPriorityService taskPriorityService;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private RcsCarTypeService rcsCarTypeService;

    private static final String WARE_CODE = "P1";
    private static final String CHARGE_TASK_TYPE = "charge";  // 充电任务类型
    private static final String CAR_MOVE_TASK_TYPE = "car_move2";    // 小车移动任务（自动寻找存放库位）
    private static final String FINISH_CHARGE_TASK_TYPE = "finishCharge";    // 小车移动任务

    /**
     * 这个方法会被定时器调用
     * 查找wareCode =sxc 的所有有货、无任务、不被禁用的库位
     * 查找当前系统中taskInfo的任务数
     * 查找当前的小车在线数量
     * 如果任务数小于小车在线数量
     * 则遍历有货库位，自动生成移托盘搬运任务，直到任务数量达到小车在线数
     */
    public void run() {
        try {
            // 1. 查找wareCode=sxc的所有有货、无任务、不被禁用的库位
            Condition cellCondition = new Condition(CellInfo.class);
            cellCondition.createCriteria()
                    .andEqualTo("wareCode", WARE_CODE)
                    .andEqualTo("invenState", 1L)  // 有货
                    .andEqualTo("taskState", 0L)   // 无任务
                    .andEqualTo("disableState", 0L); // 不被禁用
            cellCondition.orderBy("inTime");
            List<CellInfo> availableCells = cellInfoService.findByCondition(cellCondition);

            if (availableCells == null || availableCells.isEmpty()) {
                log.info("仓库{}没有可用的有货库位", WARE_CODE);
                return;
            }

            // 2. 查找当前系统中taskInfo的任务数（未完成的任务）
            Condition taskCondition = new Condition(TaskInfo.class);
            taskCondition.createCriteria()
                    .andEqualTo("wareCode", WARE_CODE); // 3通常表示失败
            List<TaskInfo> activeTasks = taskInfoService.findByCondition(taskCondition);
            int taskCount = activeTasks != null ? activeTasks.size() : 0;

            // 3. 查找当前的小车在线数量
            Condition carCondition = new Condition(RcsCarInfo.class);
            carCondition.createCriteria()
                    .andEqualTo("wareCode", WARE_CODE)
                    .andEqualTo("isConnected", 1)    // 在线
                    .andEqualTo("disableState", 0L);  // 不被禁用
            List<RcsCarInfo> onlineCars = rcsCarInfoService.findByCondition(carCondition);
            int carCount = onlineCars != null ? onlineCars.size() : 0;

            log.info("仓库{}: 任务数={}, 小车在线数={}", WARE_CODE, taskCount, carCount);

            // 4. 如果任务数小于小车在线数量，则遍历有货库位生成任务，直到任务数达到小车在线数
            if (taskCount < carCount) {
                // 计算需要生成的任务数
                int needTaskCount = carCount - taskCount;
                log.info("仓库{}需要生成{}个任务", WARE_CODE, needTaskCount);
                // 打印符合条件的移出库位（有货、无任务、不被禁用）
                StringBuilder cellCodes = new StringBuilder();
                for (int i = 0; i < availableCells.size(); i++) {
                    if (i > 0) cellCodes.append(",");
                    cellCodes.append(availableCells.get(i).getCode());
                }
                log.info("仓库{}本次符合条件的有货库位(共{}个): {}", WARE_CODE, availableCells.size(), cellCodes.toString());

                // 预先查找所有空库位作为目标位置
                Condition emptyCellCondition = new Condition(CellInfo.class);
                emptyCellCondition.createCriteria()
                        .andEqualTo("wareCode", WARE_CODE)
                        .andEqualTo("type", 0)  // 类型是库位
                        .andEqualTo("invenState", 0L)  // 无货
                        .andEqualTo("taskState", 0L)   // 无任务
                        .andEqualTo("disableState", 0L); // 不被禁用

                emptyCellCondition.orderBy("inTime");
                List<CellInfo> emptyCells = cellInfoService.findByCondition(emptyCellCondition);


                if (emptyCells == null || emptyCells.isEmpty()) {
                    log.info("仓库{}没有可用的空库位", WARE_CODE);
                    return;
                }

                // 获取任务优先级配置
                TaskTypePriority taskTypePriority = taskPriorityService.findBy("code", "MOVE");
                int defaultPriority = taskTypePriority != null ? taskTypePriority.getPriority() : 10;

                int createdTaskCount = 0;
                int emptyCellIndex = 0;

                // 遍历有货库位，生成任务
                for (CellInfo fromCell : availableCells) {
                    // 如果已经生成足够的任务，停止
                    if (createdTaskCount >= needTaskCount) {
                        break;
                    }

//                    FloorInfo floorInfo = floorInfoService.findByZ(WARE_CODE,fromCell.getZ());
//                    if(floorInfo.getDisableState()>0.1){
//                        continue;
//                    }


                    // 检查该库位是否已经有任务（避免重复）
                    Condition cellTaskCondition = new Condition(TaskInfo.class);
                    cellTaskCondition.createCriteria()
                            .andEqualTo("wareCode", WARE_CODE)
                            .andEqualTo("fromCellCode", fromCell.getCode());
                    List<TaskInfo> cellTasks = taskInfoService.findByCondition(cellTaskCondition);
                    if (cellTasks != null && !cellTasks.isEmpty()) {
                        log.info("库位{}未生成任务原因: 该库位已有任务(任务数={})", fromCell.getCode(), cellTasks.size());
                        continue;
                    }

                    // 获取该库位的托盘信息
                    Condition palletCondition = new Condition(PalletInfo.class);
                    palletCondition.createCriteria()
                            .andEqualTo("cellCode", fromCell.getCode())
                            .andEqualTo("wareCode", WARE_CODE);
                    // List<PalletInfo> palletInfos = palletInfoService.findByCondition(palletCondition);

                    // if (palletInfos == null || palletInfos.isEmpty()) {
                    //     log.debug("库位{}没有托盘信息，跳过", fromCell.getCode());
                    //     continue;
                    // }

                    // String palletCode = palletInfos.get(0).getCode();
                    String palletCode = "";

                    // 查找一个可用的空库位作为目标位置（必须是跨层的）
                    CellInfo toCell = null;
                    Integer fromCellZ = fromCell.getZ(); // 起点库位的层

                    for (int i = 0; i < emptyCells.size(); i++) {
                        int index = (emptyCellIndex + i) % emptyCells.size();
                        CellInfo candidateCell = emptyCells.get(index);

                        // 必须跨层：目标库位的层必须与起点库位的层不同
                        Integer toCellZ = candidateCell.getZ();
//                        if (fromCellZ != null && toCellZ != null && fromCellZ.equals(toCellZ)) {
//                            log.debug("库位{}与起点库位{}在同一层（层{}），跳过",
//                                    candidateCell.getCode(), fromCell.getCode(), fromCellZ);
//                            continue;
//                        }

//                        floorInfo = floorInfoService.findByZ(WARE_CODE,toCellZ);
//                        if(floorInfo.getDisableState()>0.1){
//                            continue;
//                        }


                        // 检查该目标库位是否已经有任务
                        Condition toCellTaskCondition = new Condition(TaskInfo.class);
                        toCellTaskCondition.createCriteria()
                                .andEqualTo("wareCode", WARE_CODE)
                                .andEqualTo("toCellCode", candidateCell.getCode());
                        List<TaskInfo> toCellTasks = taskInfoService.findByCondition(toCellTaskCondition);
                        if (toCellTasks == null || toCellTasks.isEmpty()) {
                            toCell = candidateCell;
                            emptyCellIndex = (index + 1) % emptyCells.size();
                            log.info("找到跨层目标库位: {} (起点层: {}, 目标层: {})",
                                    toCell.getCode(), fromCellZ, toCellZ);
                            break;
                        }
                    }

                    if (toCell == null) {
                        log.info("库位{}未生成任务原因: 没有可用的目标空库位(所有空库位均已被占为任务目标)", fromCell.getCode());
                        log.info("仓库{}没有可用的空库位作为目标", WARE_CODE);
                        break;
                    }

                    // 检查是否已存在相同的任务
                    Condition existingTaskCondition = new Condition(TaskInfo.class);
                    existingTaskCondition.createCriteria()
                            .andEqualTo("wareCode", WARE_CODE)
                            .andEqualTo("fromCellCode", fromCell.getCode())
                            .andEqualTo("toCellCode", toCell.getCode())
                            .andEqualTo("palletCode", palletCode);
                    List<TaskInfo> existingTasks = taskInfoService.findByCondition(existingTaskCondition);

                    if (existingTasks != null && !existingTasks.isEmpty()) {
                        log.info("库位{}未生成任务原因: 已存在相同移托盘任务(->{})", fromCell.getCode(), toCell.getCode());
                        continue;
                    }

                    if (pc(fromCell, toCell)) {
                        log.info("库位{}未生成任务原因: y3/y4防撞校验不通过 ", fromCell.getCode());
                        continue;
                    }


                    // 创建移托盘搬运任务
                    TaskInfo taskInfo = new TaskInfo();
                    taskInfo.setWareCode(WARE_CODE);
                    taskInfo.setWareName(fromCell.getWareName());
                    taskInfo.setType("MOVE");  // 移托盘搬运任务类型
                    taskInfo.setPalletCode(palletCode);
                    taskInfo.setFromCellCode(fromCell.getCode());
                    taskInfo.setToCellCode(toCell.getCode());
                    taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
                    taskInfo.setState(0);  // 初始状态
                    taskInfo.setPriority(defaultPriority);

                    taskInfoService.save(taskInfo);
                    createdTaskCount++;
                    Integer toCellZ = toCell.getZ();
                    log.info("自动生成跨层移托盘搬运任务[{}/{}]: {} (层{}) -> {} (层{}), 托盘: {}, 任务ID: {}",
                            createdTaskCount, needTaskCount, fromCell.getCode(), fromCellZ,
                            toCell.getCode(), toCellZ, palletCode, taskInfo.getId());

                    fromCell.setTaskState(taskInfo.getId());
                    cellInfoService.update(fromCell);
                    toCell.setTaskState(taskInfo.getId());
                    toCell.setInTime(DateUtil.getNowDateTimeString());
                    cellInfoService.update(toCell);
                }

                log.info("仓库{}本次共生成{}个移托盘搬运任务", WARE_CODE, createdTaskCount);
                if (createdTaskCount == 0 && !availableCells.isEmpty()) {
                    log.info("仓库{}生成0个任务说明: 符合条件的有货库位共{}个，未生成原因见上方各库位的「未生成任务原因」日志", WARE_CODE, availableCells.size());
                }
            }
        } catch (Exception e) {
            log.error("自动生成移托盘搬运任务时发生异常", e);
        }
    }

    private Boolean pc(CellInfo fromCell, CellInfo toCell) {
        /**
         * 这个仓库 y3 y4挨着的库位 不能同时有小车 位置不够 不然会撞
         */

        String pcFromCellCode = null;
        if (fromCell.getY() != null && fromCell.getY() == 3) {
            pcFromCellCode = fromCell.getZ() + "-" + fromCell.getX() + "-4";
        }
        if (fromCell.getY() != null && fromCell.getY() == 4) {
            pcFromCellCode = fromCell.getZ() + "-" + fromCell.getX() + "-3";
        }
        String pcToCellCode = null;
        if (toCell.getY() != null && toCell.getY() == 3) {
            pcToCellCode = toCell.getZ() + "-" + toCell.getX() + "-4";
        }
        if (toCell.getY() != null && toCell.getY() == 4) {
            pcToCellCode = toCell.getZ() + "-" + toCell.getX() + "-3";
        }

        // 起点、终点都不在 y3/y4 时，不涉及邻位防撞，直接放行
        if (pcFromCellCode == null && pcToCellCode == null) {
            return false;
        }

        Condition condition;
        // 仅用非 null 的邻位码参与查询，避免 null 误匹配
        condition = new Condition(TaskInfo.class);
        if (pcFromCellCode != null && pcToCellCode != null) {
            condition.createCriteria().orEqualTo("fromCellCode", pcFromCellCode).orEqualTo("fromCellCode", pcToCellCode);
        } else if (pcFromCellCode != null) {
            condition.createCriteria().andEqualTo("fromCellCode", pcFromCellCode);
        } else {
            condition.createCriteria().andEqualTo("fromCellCode", pcToCellCode);
        }
        List<TaskInfo> taskInfos = taskInfoService.findByCondition(condition);
        if (!taskInfos.isEmpty()) {
            log.info("pc防撞返回true: {}->{}, 原因: 邻位( y3/y4 )已有任务从该邻位出发, 邻位码={}或{}", fromCell.getCode(), toCell.getCode(), pcFromCellCode, pcToCellCode);
            return true;
        }
        condition = new Condition(TaskInfo.class);
        if (pcFromCellCode != null && pcToCellCode != null) {
            condition.createCriteria().orEqualTo("toCellCode", pcFromCellCode).orEqualTo("toCellCode", pcToCellCode);
        } else if (pcFromCellCode != null) {
            condition.createCriteria().andEqualTo("toCellCode", pcFromCellCode);
        } else {
            condition.createCriteria().andEqualTo("toCellCode", pcToCellCode);
        }
        taskInfos = taskInfoService.findByCondition(condition);
        if (!taskInfos.isEmpty()) {
            log.info("pc防撞返回true: {}->{}, 原因: 邻位( y3/y4 )已有任务目标为该邻位, 邻位码={}或{}", fromCell.getCode(), toCell.getCode(), pcFromCellCode, pcToCellCode);
            return true;
        }
        condition = new Condition(RcsCarInfo.class);
        if (pcFromCellCode != null && pcToCellCode != null) {
            condition.createCriteria().orEqualTo("fromCellCode", pcFromCellCode).orEqualTo("fromCellCode", pcToCellCode);
        } else if (pcFromCellCode != null) {
            condition.createCriteria().andEqualTo("fromCellCode", pcFromCellCode);
        } else {
            condition.createCriteria().andEqualTo("fromCellCode", pcToCellCode);
        }
        List<RcsCarInfo> rcsCarInfos = rcsCarInfoService.findByCondition(condition);
        if (!rcsCarInfos.isEmpty()) {
            log.info("pc防撞返回true: {}->{}, 原因: 邻位( y3/y4 )已有小车当前位置在该邻位, 邻位码={}或{}", fromCell.getCode(), toCell.getCode(), pcFromCellCode, pcToCellCode);
            return true;
        }
        condition = new Condition(RcsCarInfo.class);
        if (pcFromCellCode != null && pcToCellCode != null) {
            condition.createCriteria().orEqualTo("toCellCode", pcFromCellCode).orEqualTo("toCellCode", pcToCellCode);
        } else if (pcFromCellCode != null) {
            condition.createCriteria().andEqualTo("toCellCode", pcFromCellCode);
        } else {
            condition.createCriteria().andEqualTo("toCellCode", pcToCellCode);
        }
        rcsCarInfos = rcsCarInfoService.findByCondition(condition);
        if (!rcsCarInfos.isEmpty()) {
            log.info("pc防撞返回true: {}->{}, 原因: 邻位( y3/y4 )已有小车目标为该邻位, 邻位码={}或{}", fromCell.getCode(), toCell.getCode(), pcFromCellCode, pcToCellCode);
            return true;
        }

        return false;
    }


    /**
     * 这个方法会被定时器调用
     * 遍历rcsCarInfo
     * 根据rcsCarType
     * /** 最小充电电量  minChargeLevel
     * "最小充电电量")
     * 空闲时充电电量 freeChargeLevel
     * 检测小车是否在线
     * 检测小车是否有任务   根据taskInfo和jobInfo
     * 自动生成小车的充电任务
     * 只能有一个充电任务，因为只有一个充电桩
     * 去充电时，需要检测是否有其他小车在1-8-1（充电位置），如果有，则不生成任务
     * 如果小车充电结束，还在1-8-1，则把这个小车在1层找一个空闲位置开过去
     */
    public void charge() {
        try {
            // 1. 查询所有小车
            List<RcsCarInfo> allCars = rcsCarInfoService.findByWareCode(WARE_CODE);
            if (allCars == null || allCars.isEmpty()) {
                log.debug("仓库{}没有可用的小车", WARE_CODE);
                return;
            }

            //2. 比较正在充电的小车数量是否超过充电桩数量
            List<RcsCarInfo> chargeAlreadyCars = allCars.stream().filter(car -> car.getIsCharge() == 1).collect(Collectors.toList());
            List<CellInfo> chargeCells = cellInfoService.findByType(WARE_CODE, CellTypeEnum.CHARGER_POS.getCode());
            if (chargeAlreadyCars.size() >= chargeCells.size()) {
                log.debug("仓库{}正在充电小车数量已经超过充电桩数量", WARE_CODE);
                return;
            }

            // 比较将要充电的数量是否超出
            Condition chargeTaskCondition = new Condition(TaskInfo.class);
            chargeTaskCondition.createCriteria()
                    .andEqualTo("wareCode", WARE_CODE)
                    .andEqualTo("type", CHARGE_TASK_TYPE);
            List<TaskInfo> existingChargeTasks = taskInfoService.findByCondition(chargeTaskCondition);
            if (chargeAlreadyCars.size() + existingChargeTasks.size() >= chargeCells.size()) {
                log.debug("仓库{}正在充电小车数量已经超过充电桩数量", WARE_CODE);
                return;
            }

            // 3. 检查充电桩位置是否存在小车
            List<CellInfo> freeChargeCells = new ArrayList<>();
            for (CellInfo chargeCell : chargeCells) {

                // 查询是否已经存在任务
                Condition checkExistTaskCon = new Condition(TaskInfo.class);
                checkExistTaskCon.createCriteria()
                        .andEqualTo("wareCode", WARE_CODE)
                        .andEqualTo("toCellCode", chargeCell.getCode());
                List<TaskInfo> existTasks = taskInfoService.findByCondition(checkExistTaskCon);

                // 没有任务存在
                boolean chargeCellOccupied = existTasks != null && !existTasks.isEmpty();
                if (!chargeCellOccupied) {

                    Condition checkExistCarCon = new Condition(RcsCarInfo.class);
                    checkExistCarCon.createCriteria()
                            .andEqualTo("wareCode", WARE_CODE)
                            .andEqualTo("fromCellCode", chargeCell.getCode());
                    List<RcsCarInfo> existCars = rcsCarInfoService.findByCondition(checkExistCarCon);

                    chargeCellOccupied = existCars != null && !existCars.isEmpty();
                    if (chargeCellOccupied) {
                        // 如果存在小车占用--直接判断该小车是否需要充电，需要就直接充电
                        if(existCars.size()>1){
                            for(RcsCarInfo car:existCars){
                                log.error("充电桩位置"+chargeCell.getCode()+"有多辆小车，请检查小车"+car.getCode());
                            }
                            continue;
                        }
                        checkIfNeedCharge(chargeCell,existCars.get(0),1);

                    } else {
                        // 如果不存在小车占用，标记为空闲库位
                        freeChargeCells.add(chargeCell);
                    }
                }
            }

            int maxChargeNum = freeChargeCells.size();

            // 4. 如果无空闲充电桩位置，则退出
            if (freeChargeCells.size() == 0) {
                log.debug("仓库{}无可用的空闲充电桩", WARE_CODE);
                return;
            }

            //5. 遍历充电桩，生成充电任务
            for (CellInfo chargeCell : freeChargeCells) {

                // 6. 遍历小车，查找需要充电的小车
                for (RcsCarInfo car : allCars) {
                    // 如果充电操作成功，则去下一个充电桩
                    if(checkIfNeedCharge(chargeCell,car,maxChargeNum)){
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("自动生成充电任务时发生异常", e);
        }
    }

    /**
     * 检查小车是否有活跃任务
     */
    private boolean hasActiveTask(Long rcsCarId) {
        // 检查TaskInfo中是否有该小车的未完成任务
        Condition taskCondition = new Condition(TaskInfo.class);
        taskCondition.createCriteria()
                .andEqualTo("wareCode", WARE_CODE)
                .andEqualTo("rcsCarId", rcsCarId);  // 未完成的任务
        List<TaskInfo> tasks = taskInfoService.findByCondition(taskCondition);
        if (tasks != null && !tasks.isEmpty()) {
            return true;
        }

        // 检查JobInfo中是否有该小车的未完成任务
        Condition jobCondition = new Condition(JobInfo.class);
        jobCondition.createCriteria()
                .andEqualTo("rcsCarId", rcsCarId);
        List<JobInfo> jobs = jobInfoService.findByCondition(jobCondition);
        if (jobs != null && !jobs.isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     *  判断是否需要充电并进行充电
     */
    private boolean checkIfNeedCharge(CellInfo chargeCell,RcsCarInfo car,int maxChargeNum){
        /**
         * 这里也是通过遥测数据获得小车是否在线  是否在充电
         */
        car = rcsCarInfoService.findById(car.getId());

        // 6.1 检查小车是否在线
        if (car.getIsConnected() == null || car.getIsConnected() != 1) {
            log.debug("小车" + car.getCode() + "不在线!");
            return false;
        }

        // 6.2 检查小车是否正在充电
        if (car.getIsCharge() != null && car.getIsCharge() == 1) {
            log.debug("小车" + car.getCode() + "正在充电!");
            return false;
        }

        // 6.3 检查小车是否有任务（TaskInfo和JobInfo）
        if (hasActiveTask(car.getId())) {
            log.debug("小车" + car.getCode() + "正在执行任务!");
            return false;
        }

        // 6.4 获取车型信息，检查充电阈值
        if (car.getRcsCarTypeId() == null) {
            log.warn("小车" + car.getCode() + "不属于任何类型，请检查小车类型rcsCarId!");
            return false;
        }
        RcsCarType carType = rcsCarTypeService.findById(car.getRcsCarTypeId());
        if (carType == null) {
            log.warn("小车{}的车型{}不存在", car.getCode(), car.getRcsCarTypeId());
            return false;
        }

        Integer minChargeLevel = carType.getMinChargeLevel();
        Integer freeChargeLevel = carType.getFreeChargeLevel();
        Integer batteryLevel = car.getBatteryLevel();

        if (batteryLevel == null) {
            log.warn("获取不到小车" + car.getCode() + "电池电量，请检查!");
            return false;
        }

        // 6.5 判断是否需要充电
        boolean needCharge = false;
        if (minChargeLevel != null && batteryLevel <= minChargeLevel) {
            // 电量低于最小充电电量，必须充电
            needCharge = true;
            log.info("小车{}电量{}低于最小充电电量{}，需要充电", car.getCode(), batteryLevel, minChargeLevel);
        } else if (freeChargeLevel != null && batteryLevel <= freeChargeLevel) {
            // 电量低于空闲时充电电量，且小车空闲，可以充电
            needCharge = true;
            log.info("小车{}电量{}低于空闲时充电电量{}，且小车空闲，需要充电", car.getCode(), batteryLevel, freeChargeLevel);
        }

        if (needCharge) {
            // 6.6 再次检查充电位置是否被占用（双重检查，排除当前小车本身）
            Condition checkChargeCellCondition = new Condition(RcsCarInfo.class);
            checkChargeCellCondition.createCriteria()
                    .andEqualTo("wareCode", WARE_CODE)
                    .andEqualTo("fromCellCode", chargeCell.getCode())
                    .andEqualTo("isConnected", 1);
            List<RcsCarInfo> checkCars = rcsCarInfoService.findByCondition(checkChargeCellCondition);
            if (checkCars != null && !checkCars.isEmpty()) {
                // 检查是否有其他小车（排除当前小车本身）
                boolean hasOtherCar = false;
                for (RcsCarInfo checkCar : checkCars) {
                    if (!checkCar.getId().equals(car.getId())) {
                        hasOtherCar = true;
                        break;
                    }
                }
                if (hasOtherCar) {
                    log.debug("充电位置{}被其他小车占用，小车{}无法充电", chargeCell.getCode(), car.getCode());
                    return false;
                }
            }

            // 6.7 生成充电任务
            if (maxChargeNum <= 0) {
                return false;
            }
            createChargeTask(car, chargeCell.getCode());
            maxChargeNum--;
            log.info("为小车{}生成充电任务", car.getCode());
            return true;
        }
        return false;
    }

    /**
     * 创建充电任务
     */
    private void createChargeTask(RcsCarInfo car, String chargeCellCode) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setWareCode(WARE_CODE);
        taskInfo.setType(CHARGE_TASK_TYPE);
        taskInfo.setRcsCarId(car.getId());
        taskInfo.setToCellCode(chargeCellCode);  // 充电位置
        taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
        taskInfo.setState(0);  // 初始状态

        // 获取充电任务优先级
        TaskTypePriority taskTypePriority = taskPriorityService.findBy("code", CHARGE_TASK_TYPE);
        int defaultPriority = taskTypePriority != null ? taskTypePriority.getPriority() : 1;  // 充电任务优先级较高
        taskInfo.setPriority(defaultPriority);

        taskInfoService.save(taskInfo);
        log.info("成功创建充电任务，小车：{}，任务ID：{}", car.getCode(), taskInfo.getId());
    }


    /**
     * 这个任务会被定时任务调用
     * 结束充电任务--查找所有在充电桩上的小车
     * 1. 如果小车不在充电--生成小车移动任务（随机寻找空位）
     * 2. 如果小车正在充电--生成小车停止充电任务（停止充电）
     *
     */
    public void finishCharge() {
        // 1. 查询所有充电桩
        List<CellInfo> chargeCells = cellInfoService.findByType(WARE_CODE, CellTypeEnum.CHARGER_POS.getCode());
        if (chargeCells == null || chargeCells.size() == 0) {
            log.warn("查不到充电桩");
            return;
        }

        for (CellInfo chargeCell : chargeCells) {
            Condition chargeCellCarCondition = new Condition(RcsCarInfo.class);
            chargeCellCarCondition.createCriteria()
                    .andEqualTo("wareCode", WARE_CODE)
                    .andEqualTo("fromCellCode", chargeCell.getCode());
            List<RcsCarInfo> carsAtChargeCell = rcsCarInfoService.findByCondition(chargeCellCarCondition);
            // 如果充电桩位置没有小车--直接跳过
            if (carsAtChargeCell == null || carsAtChargeCell.size() == 0) {
                continue;
            }
            // 充电桩位置存在小车
            if (carsAtChargeCell.size() > 1) {
                for (RcsCarInfo car : carsAtChargeCell) {
                    log.error("充电桩位置{}同时存在多辆小车，位置信息错误--请检查小车{}", chargeCell.getCode(), car.getCode());

                    // 需要添加异常处理流程
                    continue;
                }
            }
            RcsCarInfo car = carsAtChargeCell.get(0);
            car = rcsCarInfoService.findById(car.getId());

            // 充电桩位置存在离线小车
            if (car.getIsConnected() == 0) {
                log.error("充电桩位置{}存在离线小车{}，请手动处理", chargeCell.getCode(), car.getCode());

                // 需要添加异常处理流程
                continue;
            }

            if (car.getTaskState() > 0) {
                TaskInfo taskInfo = taskInfoService.findById(car.getTaskState());
                JobInfo jobInfo = jobInfoService.findById(car.getTaskState());
                if (taskInfo != null || jobInfo != null) {
                    log.info("小车{}存在任务");
                    // 需要添加异常处理流程
                    continue;
                }
            }

            // 小车没有充电-生成移动任务
            if (car.getIsCharge() == 0 && car.getBatteryLevel() >= 95) {
                createMoveTask(car);
            }

            // 小车正在充电-生成停止充电任务
            if (car.getIsCharge() == 1 && car.getBatteryLevel() >= 95) {
                createFinishChargeTask(car);
            }

        }

    }

    /**
     * 创建移动任务（将小车从当前位置移动到目标位置）
     */
    private void createMoveTask(RcsCarInfo car) {
        Condition condition = new Condition(TaskInfo.class);
        condition.createCriteria().andEqualTo("rcsCarId", car.getId());
        List<TaskInfo> taskInfos = taskInfoService.findByCondition(condition);
        if (taskInfos.size() > 0) {
            return;
        }

        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setWareCode(WARE_CODE);
        taskInfo.setType(CAR_MOVE_TASK_TYPE);
        taskInfo.setRcsCarId(car.getId());
        taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
        taskInfo.setState(0);  // 初始状态

        // 获取移动任务优先级
        TaskTypePriority taskTypePriority = taskPriorityService.findBy("code", CAR_MOVE_TASK_TYPE);
        int defaultPriority = taskTypePriority != null ? taskTypePriority.getPriority() : 10;
        taskInfo.setPriority(defaultPriority);

        taskInfoService.save(taskInfo);
        log.info("成功创建移动任务，小车：{}，从{}移动，任务ID：{}", car.getCode(), car.getFromCellCode(), taskInfo.getId());
    }

    /**
     * 创建停止充电任务（小车停止充电）
     */
    private void createFinishChargeTask(RcsCarInfo car) {
        Condition condition = new Condition(TaskInfo.class);
        condition.createCriteria().andEqualTo("rcsCarId", car.getId());
        List<TaskInfo> taskInfos = taskInfoService.findByCondition(condition);
        if (taskInfos.size() > 0) {
            return;
        }

        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setWareCode(WARE_CODE);
        taskInfo.setType(FINISH_CHARGE_TASK_TYPE);
        taskInfo.setRcsCarId(car.getId());
        taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
        taskInfo.setState(0);  // 初始状态

        // 获取任务优先级-最高
        taskInfo.setPriority(1);
        taskInfoService.save(taskInfo);
        log.info("生成小车停止充电任务，小车：{}，任务ID：{}", car.getCode(), taskInfo.getId());
    }
}
