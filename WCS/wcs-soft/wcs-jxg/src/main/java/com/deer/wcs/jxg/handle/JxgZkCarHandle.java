package com.deer.wcs.jxg.handle;

import com.deer.wcs.common.enums.CellTypeEnum;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.DateUtils;
import com.deer.wcs.rcs.model.RcsCarType;
import com.deer.wcs.rcs.model.RcsTsj;
import com.deer.wcs.rcs.service.RcsCarTypeService;
import com.deer.wcs.rcs.service.RcsTsjService;
import com.fasterxml.jackson.core.type.TypeReference;


import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.CellLink;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.CellLinkService;
import com.deer.wcs.common.constant.WebSocketCacheConstants;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.jxg.car_brand_model.ZkCarUtil;
import com.deer.wcs.jxg.car_brand_model.ZkCarRedisUtil;
import com.deer.wcs.jxg.car_brand_model.ZkDeviceMessage;
import com.deer.wcs.rcs.handle.RcsMainHandel;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.model.RcsCarPath;
import com.deer.wcs.rcs.service.RcsCarInfoService;
import com.deer.wcs.rcs.service.RcsCarPathService;
import com.deer.wcs.rcs.websocket.MonitorWebSocketHandler;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.task.model.DeviceTaskResult;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.DeviceTaskResultService;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.*;

@Component("JxgZkCarHandle")
public class JxgZkCarHandle {

    private static final Logger log = LoggerFactory.getLogger(JxgZkCarHandle.class);

    @Autowired
    private RcsCarInfoService rcsCarInfoService;

    @Autowired
    private RcsCarPathService rcsCarPathService;

    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private ZkCarUtil zkCarUtil;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private RcsMainHandel rcsMainHandel;

    @Autowired
    private DeviceTaskResultService deviceTaskResultService;

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ZkCarRedisUtil zkCarRedisUtil;

    @Autowired
    private com.deer.wcs.base.service.DeviceValueService deviceValueService;

    @Autowired
    private CellLinkService cellLinkService;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RcsCarTypeService rcsCarTypeService;
    @Autowired
    private RcsTsjService rcsTsjService;

    /**
     * 选取小车  应该根据小车离起点的距离  小车的redis中的状态  小车的任务队列  来判断是否分配给该任务
     *
     * @param jobInfo
     * @return
     */
    public Boolean allotCar(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        // 1、查询任务起点库位信息
        if (taskInfo.getFromCellCode() == null || taskInfo.getFromCellCode().isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "任务起点库位编码为空，无法分配小车");
            return false;
        }

        CellInfo startCell = cellInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getFromCellCode());
        CellInfo endCell = cellInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getToCellCode());
        if (startCell == null) {
            jobInfoService.updateMemo(jobInfo, "未找到任务起点库位信息：" + taskInfo.getFromCellCode());
            return false;
        }
        if (endCell == null) {
            jobInfoService.updateMemo(jobInfo, "未找到任务终点库位信息：" + taskInfo.getToCellCode());
            return false;
        }

        // 2、查询所有可用小车（未禁用、已连接）
        Condition condition = new Condition(RcsCarInfo.class);
        condition.createCriteria()
                .andEqualTo("disableState", 0L)  // 未禁用
                .andEqualTo("isConnected", 1)    // 已连接
                .andEqualTo("isCharge",0);      // 未充电
        List<RcsCarInfo> rcsCarList = rcsCarInfoService.findByCondition(condition);

        if (rcsCarList == null || rcsCarList.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "没有可用小车");
            return false;
        }
        if (taskInfo.getRcsCarId() != null) {
            Condition conditionJob = new Condition(JobInfo.class);
            conditionJob.createCriteria().andEqualTo("rcsCarId", taskInfo.getRcsCarId());
            List<JobInfo> list = jobInfoService.findByCondition(conditionJob);
            if (!list.isEmpty()) {
                return false;
            } else {
                RcsCarInfo rcsCarInfo = rcsCarInfoService.findById(taskInfo.getRcsCarId());
                jobInfo.setRcsCarId(rcsCarInfo.getId());
                jobInfoService.update(jobInfo);
                jobInfoService.updateMemo(jobInfo, "任务指定小车" + rcsCarInfo.getCode());
                return true;
            }
        }

        // 3、为每辆小车评分，选择最佳小车
        RcsCarInfo bestCar = null;
        double bestScore = Double.MAX_VALUE; // 分数越低越好
        String bestCarInfo = "";

        for (RcsCarInfo car : rcsCarList) {
            Condition conditionJob = new Condition(JobInfo.class);
            conditionJob.createCriteria().andEqualTo("rcsCarId", car.getId());
            List<JobInfo> list = jobInfoService.findByCondition(conditionJob);
            if (!list.isEmpty()) {
                continue;
            }


            // 3.1 计算该小车的综合评分
            CarScore carScore = calculateCarScore(car, startCell, endCell, jobInfo);

            if (!carScore.isAvailable) {
                log.info("小车 {} 不可用：{}", car.getCode(), carScore.reason);
                continue;
            }

            // 3.2 比较分数，选择最优小车
            if (carScore.totalScore < bestScore) {
                bestScore = carScore.totalScore;
                bestCar = car;
                bestCarInfo = carScore.toString();
            }

            log.info("小车 {} 评分：{}", car.getCode(), carScore.toString());
        }

        // 4、分配最佳小车
        if (bestCar != null) {
            taskInfo.setRcsCarId(bestCar.getId());
            taskInfoService.update(taskInfo);
            jobInfo.setRcsCarId(bestCar.getId());
            jobInfoService.update(jobInfo);
            jobInfoService.updateMemo(jobInfo,
                    String.format("成功分配小车 %s (评分: %.2f) - %s",
                            bestCar.getCode(), bestScore, bestCarInfo));
            return true;
        } else {
            jobInfoService.updateMemo(jobInfo, "所有小车均不可用，无法分配");
            return false;
        }
    }

    public Boolean carNoPalletCheck(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo.getRcsCarId() == null) {
            jobInfoService.updateMemo(jobInfo, "任务未指定小车，无法判断是否载货");
            return false;
        }
        RcsCarInfo rcsCarInfo = rcsCarInfoService.findById(taskInfo.getRcsCarId());
        Map<String, Object> redisState = zkCarUtil.getCarStateFromRedis(Integer.getInteger(rcsCarInfo.getCode()));

        if (redisState == null) {
            jobInfoService.updateMemo(jobInfo, "未找到小车" + rcsCarInfo.getCode() + "的实时状态");
            return false;
        }
        Integer loadState = (Integer) redisState.get("loadState");
        if (loadState == null) {
            jobInfoService.updateMemo(jobInfo, "未找到小车" + rcsCarInfo.getCode() + "的载货状态");
            return false;
        }
        if (loadState == 1) {
            jobInfoService.updateMemo(jobInfo, "小车" + rcsCarInfo.getCode() + "已载货，无法开始搬运任务");
            return false;
        }
        return true;

    }

    /**
     * 分配充电桩位置
     *
     * @param jobInfo
     * @return
     */
    public Boolean startChargeCell(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo.getRcsCarId() == null) {
            jobInfoService.updateMemo(jobInfo, "任务未指定小车，无法开始充电");
            return false;
        }
        RcsCarInfo rcsCarInfo = rcsCarInfoService.findById(taskInfo.getRcsCarId());
        if (rcsCarInfo.getIsCharge() == 1) {
            jobInfoService.updateMemo(jobInfo, "小车" + rcsCarInfo.getCode() + "已在充电");
            return false;
        }
        List<CellInfo> chargeCells = cellInfoService.findByType(taskInfo.getWareCode(), CellTypeEnum.CHARGER_POS.getCode());
        CellInfo allotChargeCell = null;
        for (CellInfo chargeCell : chargeCells) {

            /*
                查看该充电位置是否有小车
             */
            Condition condition = new Condition(RcsCarInfo.class);
            condition.createCriteria().andEqualTo("fromCellCode", chargeCell.getCode());
            List<RcsCarInfo> list = rcsCarInfoService.findByCondition(condition);
            boolean hasCar = false;
            for (RcsCarInfo car : list) {
                if (!car.getId().equals(rcsCarInfo.getId())) {
                    hasCar = true;
                    break;
                }
                if(car.getId().equals(rcsCarInfo.getId())){
                    allotChargeCell = chargeCell;
                    break;
                }
            }
            // 该充电桩有小车，更换充电桩
            if (hasCar) {
                continue;
            }

            /*
                查看是否有其它任务终点在充电位置
             */
            Condition conditionTask = new Condition(TaskInfo.class);
            conditionTask.createCriteria()
                    .andEqualTo("wareCode", taskInfo.getWareCode())
                    .andEqualTo("toCellCode", chargeCell.getCode());
            List<TaskInfo> taskInfoList = taskInfoService.findByCondition(conditionTask);
            boolean hasTask = false;
            for (TaskInfo taskInfo1 : taskInfoList) {
                if (!taskInfo1.getId().equals(taskInfo.getId())) {
                    hasTask = true;
                    break;
                }
            }
            if (hasTask) {
                continue;
            }
            allotChargeCell = chargeCell;
        }

        if (allotChargeCell == null) {
            jobInfoService.updateMemo(jobInfo, "小车" + rcsCarInfo.getCode() + " 无可分配的充电桩，请等待！");
            return false;
        }

        Condition conditionTask = new Condition(TaskInfo.class);
        conditionTask.createCriteria()
                .andEqualTo("wareCode", taskInfo.getWareCode())
                .andEqualTo("toCellCode", allotChargeCell.getCode());
        List<TaskInfo> taskInfoList = taskInfoService.findByCondition(conditionTask);
        for (TaskInfo taskInfo1 : taskInfoList) {
            if (!taskInfo1.getId().equals(taskInfo.getId())) {
                jobInfoService.updateMemo(jobInfo, "其他任务" + taskInfo1.getId() + "目标点在" + taskInfo1.getToCellCode() + "已经在充电位置：" + allotChargeCell.getCode());
                return false;
            }
        }

        allotChargeCell.setTaskState(rcsCarInfo.getId());
        cellInfoService.update(allotChargeCell);
        cellInfoService.addRecord(allotChargeCell.getCode(),allotChargeCell.getWareCode(),
                "[充电]小车"+rcsCarInfo.getCode()+"充电占用库位"+allotChargeCell.getCode());

        taskInfo.setToCellCode(allotChargeCell.getCode());
        taskInfoService.update(taskInfo);
        jobInfoService.updateMemo(jobInfo, "小车" + rcsCarInfo.getCode() + " 已分配充电位置：" + allotChargeCell.getCode());
        return true;
    }


    /**
     * 开始充电
     * 获取小车
     * 判断是否在充电位置
     * 如果是 则调用充电接口
     */
    public Boolean startCharge(JobInfo jobInfo) {
        try {
            // 1、验证参数
            if (jobInfo == null || jobInfo.getRcsCarId() == null) {
                jobInfoService.updateMemo(jobInfo, "充电失败：jobInfo或小车ID为空");
                return false;
            }

            // 2、查询小车信息
            RcsCarInfo carInfo = rcsCarInfoService.selectRcsCarInfoById(jobInfo.getRcsCarId());
            if (carInfo == null) {
                jobInfoService.updateMemo(jobInfo, "充电失败：未找到小车信息，小车ID: " + jobInfo.getRcsCarId());
                return false;
            }

            // 3、获取小车当前位置
            String currentCellCode = carInfo.getFromCellCode();
            if (currentCellCode == null || currentCellCode.isEmpty()) {
                jobInfoService.updateMemo(jobInfo, "充电失败：小车当前位置为空");
                return false;
            }

            // 4、查询当前位置的库位信息
            TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
            if (taskInfo == null) {
                jobInfoService.updateMemo(jobInfo, "充电失败：任务不存在");
                return false;
            }

            CellInfo currentCell = cellInfoService.findByCode(taskInfo.getWareCode(), currentCellCode);
            if (currentCell == null) {
                jobInfoService.updateMemo(jobInfo, "充电失败：未找到当前位置库位信息：" + currentCellCode);
                return false;
            }

            // 5、判断是否在充电位置（type=6表示充电位置）
            if (currentCell.getType() == null || currentCell.getType() != 6) {
                jobInfoService.updateMemo(jobInfo,
                        String.format("充电失败：当前位置不是充电位，位置:%s, type:%s",
                                currentCellCode, currentCell.getType()));
                return false;
            }

            // 6、检查小车是否已在充电
//            if ("1".equals(carInfo.getIsCharge())) {
//                jobInfoService.updateMemo(jobInfo, "小车已在充电中，小车: " + carInfo.getCode());
//                return true; // 已经在充电，返回成功
//            }

            // 7、检查小车IP地址
            if (carInfo.getIp() == null || carInfo.getIp().isEmpty()) {
                jobInfoService.updateMemo(jobInfo, "充电失败：小车IP地址为空，无法发送充电指令");
                return false;
            }

            // 8、生成任务编号
            Integer taskNo = autoService.getTodayTaskNo();
            jobInfo.setTaskNo(taskNo.toString());
            jobInfoService.update(jobInfo);

            // 9、构建开始充电指令消息（START_CHARGE）
            Long requestId = System.currentTimeMillis();
            Map<String, Object> bodyData = new HashMap<>();
            bodyData.put("operationType", "COMMAND");
            bodyData.put("operationCode", "START_CHARGE");
            bodyData.put("taskId", taskNo);

            ZkDeviceMessage zkMessage = ZkDeviceMessage.ZkMessageUtil.createRequest(
                    "InstructionRequestMsg",
                    Integer.parseInt(carInfo.getCode()),
                    requestId,
                    "2.0.0",
                    bodyData
            );

            // 10、发送充电指令
            String messageJson = ZkDeviceMessage.ZkMessageUtil.toJson(zkMessage);
            jobInfoService.updateMemo(jobInfo, "发送开始充电指令给小车 " + carInfo.getCode() + ": " + messageJson);

            log.info("发送开始充电指令，小车: {}, 位置: {}, jobId: {}",
                    carInfo.getCode(), currentCellCode, jobInfo.getId());

            // 11、发送指令并获取返回值
            String responseJson = zkCarUtil.sendMessageSync(carInfo.getIp(), zkMessage);

            // 12、使用通用方法处理小车响应
            boolean result = handleCarResponse(responseJson, carInfo, jobInfo, "开始充电");

            if (result) {
                // 13、更新小车充电状态
                carInfo.setIsCharge(1);
                rcsCarInfoService.update(carInfo);
                log.info("小车开始充电成功，小车: {}, 位置: {}", carInfo.getCode(), currentCellCode);
            } else {
                jobInfoService.updateMemo(jobInfo, "发送开始充电指令给小车失败  " + carInfo.getCode() + ": " + responseJson);
            }

            return result;

        } catch (Exception e) {
            log.error("小车充电失败，jobId: {}", jobInfo.getId(), e);
            jobInfoService.updateMemo(jobInfo, "充电失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 结束充电
     * 获取小车
     * 判断是否在充电位置
     * 如果是 则调用充电接口
     */
    public Boolean endCharge(JobInfo jobInfo) {
        try {
            // 1、验证参数
            if (jobInfo == null || jobInfo.getRcsCarId() == null) {
                jobInfoService.updateMemo(jobInfo, "充电失败：jobInfo或小车ID为空");
                return false;
            }

            // 2、查询小车信息
            RcsCarInfo carInfo = rcsCarInfoService.selectRcsCarInfoById(jobInfo.getRcsCarId());
            if (carInfo == null) {
                jobInfoService.updateMemo(jobInfo, "充电失败：未找到小车信息，小车ID: " + jobInfo.getRcsCarId());
                return false;
            }

            // 3、获取小车当前位置
            String currentCellCode = carInfo.getFromCellCode();
            if (currentCellCode == null || currentCellCode.isEmpty()) {
                jobInfoService.updateMemo(jobInfo, "充电失败：小车当前位置为空");
                return false;
            }

            // 4、查询当前位置的库位信息
            TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
            if (taskInfo == null) {
                jobInfoService.updateMemo(jobInfo, "充电失败：任务不存在");
                return false;
            }

            CellInfo currentCell = cellInfoService.findByCode(taskInfo.getWareCode(), currentCellCode);
            if (currentCell == null) {
                jobInfoService.updateMemo(jobInfo, "充电失败：未找到当前位置库位信息：" + currentCellCode);
                return false;
            }

            // 5、判断是否在充电位置（type=6表示充电位置）
            if (currentCell.getType() == null || currentCell.getType() != 6) {
                jobInfoService.updateMemo(jobInfo,
                        String.format("充电失败：当前位置不是充电位，位置:%s, type:%s",
                                currentCellCode, currentCell.getType()));
                return false;
            }

            // 6、检查小车是否已在充电
//            if ("1".equals(carInfo.getIsCharge())) {
//                jobInfoService.updateMemo(jobInfo, "小车已在充电中，小车: " + carInfo.getCode());
//                return true; // 已经在充电，返回成功
//            }

            // 7、检查小车IP地址
            if (carInfo.getIp() == null || carInfo.getIp().isEmpty()) {
                jobInfoService.updateMemo(jobInfo, "充电失败：小车IP地址为空，无法发送充电指令");
                return false;
            }

            // 8、生成任务编号
            Integer taskNo = autoService.getTodayTaskNo();
            jobInfo.setTaskNo(taskNo.toString());
            jobInfoService.update(jobInfo);

            // 9、构建开始充电指令消息（START_CHARGE）
            Long requestId = System.currentTimeMillis();
            Map<String, Object> bodyData = new HashMap<>();
            bodyData.put("operationType", "COMMAND");
            bodyData.put("operationCode", "STOP_CHARGE");
            bodyData.put("taskId", taskNo);

            ZkDeviceMessage zkMessage = ZkDeviceMessage.ZkMessageUtil.createRequest(
                    "InstructionRequestMsg",
                    Integer.parseInt(carInfo.getCode()),
                    requestId,
                    "2.0.0",
                    bodyData
            );

            // 10、发送充电指令
            String messageJson = ZkDeviceMessage.ZkMessageUtil.toJson(zkMessage);
            jobInfoService.updateMemo(jobInfo, "发送结束充电指令给小车 " + carInfo.getCode() + ": " + messageJson);

            log.info("发送结束充电指令，小车: {}, 位置: {}, jobId: {}",
                    carInfo.getCode(), currentCellCode, jobInfo.getId());

            // 11、发送指令并获取返回值
            String responseJson = zkCarUtil.sendMessageSync(carInfo.getIp(), zkMessage);

            // 12、使用通用方法处理小车响应
            boolean result = handleCarResponse(responseJson, carInfo, jobInfo, "开始充电");

            if (result) {
                currentCell.setTaskState(0L);
                cellInfoService.update(currentCell);
                cellInfoService.addRecord(currentCell.getCode(),currentCell.getWareCode(),
                        "[充电]小车"+carInfo.getCode()+"结束充电释放库位"+currentCell.getCode());
                // 13、更新小车充电状态
                carInfo.setIsCharge(0);
                rcsCarInfoService.update(carInfo);
                log.info("小车结束充电成功，小车: {}, 位置: {}", carInfo.getCode(), currentCellCode);
            } else {
                jobInfoService.updateMemo(jobInfo, "发送结束充电指令给小车失败  " + carInfo.getCode() + ": " + responseJson);
            }

            return result;

        } catch (Exception e) {
            log.error("小车充电失败，jobId: {}", jobInfo.getId(), e);
            jobInfoService.updateMemo(jobInfo, "充电失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 在顶升之前检测是否有托盘
     * 通过小车的遥测数据
     *
     * @param jobInfo
     * @return
     */
    public Boolean hasPallet(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        return true;
    }

    /**
     * 根据小车当前位置 获取去往提升机的位置
     *
     * @param jobInfo
     * @return
     */
    public Boolean getToTsjPosition(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        // 1、验证jobInfo基本信息
        if (jobInfo.getRcsCarId() == null) {
            jobInfoService.updateMemo(jobInfo, "小车ID为空，无法获取提升机位置");
            return false;
        }

        // 2、获取小车信息
        RcsCarInfo carInfo = rcsCarInfoService.selectRcsCarInfoById(jobInfo.getRcsCarId());
        if (carInfo == null) {
            jobInfoService.updateMemo(jobInfo, "未找到小车信息，小车ID: " + jobInfo.getRcsCarId());
            return false;
        }

        // 3、获取小车当前位置
        String currentCellCode = carInfo.getFromCellCode();
        if (currentCellCode == null || currentCellCode.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "小车当前位置为空，无法计算去往提升机的路径");
            return false;
        }

        CellInfo currentCell = cellInfoService.findByCode(taskInfo.getWareCode(), currentCellCode);
        if (currentCell == null) {
            jobInfoService.updateMemo(jobInfo, "未找到小车当前位置库位信息：" + currentCellCode);
            return false;
        }

        // 3.1、验证遥测数据，确保小车实际位置与系统记录一致
        ZkCarRedisUtil.CarPositionVerifyResult verifyResult =
                zkCarRedisUtil.verifyAndUpdateCarPosition(carInfo, taskInfo.getWareCode(), 3000);

        if (!verifyResult.isSuccess()) {
            // 验证失败（遥测数据超时、坐标不完整、未匹配到库位等）
            jobInfoService.updateMemo(jobInfo, verifyResult.getErrorMessage());
            log.warn("小车位置验证失败，jobId: {}, {}", jobInfo.getId(), verifyResult);
            return false;
        }

        // 如果位置不一致，使用遥测位置更新当前位置
        if (!verifyResult.isConsistent() && verifyResult.getTelemetryCell() != null) {
            currentCellCode = verifyResult.getTelemetryCellCode();
            currentCell = verifyResult.getTelemetryCell();

            jobInfoService.updateMemo(jobInfo,
                    String.format("已更新小车[%s]位置：系统记录[%s] -> 遥测位置[%s]",
                            verifyResult.getCarCode(), verifyResult.getSystemCellCode(), currentCellCode));

            log.info("小车位置已更新，jobId: {}, {}", jobInfo.getId(), verifyResult);
        } else {
            // 位置一致，验证通过
            // log.info("✓ 小车位置验证通过，jobId: {}, {}", jobInfo.getId(), verifyResult);
        }

        // 4、获取小车当前楼层
        Integer currentFloor = carInfo.getZ();
        if (currentFloor == null) {
            // 如果小车Z坐标为空，尝试从当前库位获取
            currentFloor = currentCell.getZ();
        }

        if (currentFloor == null) {
            jobInfoService.updateMemo(jobInfo, "无法获取小车当前楼层信息");
            return false;
        }

        RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
        if(rcsTsj==null){
            jobInfoService.updateMemo(jobInfo, "【换层】未找到提升机信息");
            return false;
        }

        // 5、查找该楼层的提升机入口库位
        // 提升机入口库位命名规则：TSJ-入口-楼层 或者 TSJ入口 或者 包含"提升机"、"入口"关键字的库位
        CellInfo tsjEntranceCell = findTsjEntranceCellByFloor(taskInfo.getWareCode(),rcsTsj, currentFloor);

        if (tsjEntranceCell == null) {
            jobInfoService.updateMemo(jobInfo, String.format("未找到楼层%d的提升机入口库位，请检查库位配置", currentFloor));
            return false;
        }

        // 6、设置起点和终点
        jobInfo.setFromCellCode(currentCellCode);  // 起点：小车当前位置
        jobInfo.setToCellCode(tsjEntranceCell.getCode());  // 终点：提升机入口
        jobInfoService.update(jobInfo);

        jobInfoService.updateMemo(jobInfo,
                String.format("成功获取去往提升机路径：起点[%s] -> 终点[%s]（楼层%d）",
                        currentCellCode, tsjEntranceCell.getCode(), currentFloor));

        log.info("成功获取去往提升机路径，jobId: {}, 小车: {}, 起点: {}, 终点: {}, 楼层: {}",
                jobInfo.getId(), carInfo.getCode(), currentCellCode, tsjEntranceCell.getCode(), currentFloor);

        return true;
    }

    /**
     * 根据楼层查找提升机入口库位
     * 查找规则：
     * 1. 优先查找名称包含"提升机"和"入口"的库位
     * 2. 查找编码以"TSJ"开头的库位
     * 3. 查找type字段为特定值的库位（如果有配置）
     *
     * @param wareCode 仓库编码
     * @param floor    楼层
     * @return 提升机入口库位，如果未找到返回null
     */
    private CellInfo findTsjEntranceCellByFloor(String wareCode,RcsTsj rcsTsj, Integer floor) {
        try {
            String cellCode = rcsTsj.getCellCode();
            if(cellCode.isEmpty()){
                log.error("查找提升机入口库位未配置，wareCode: {}, tsj: {}", wareCode, rcsTsj.getCode());
                return null;
            }
            String[] codes = cellCode.split("-");
            if(codes.length!=3){
                log.error("提升机入口库位配置有误，wareCode: {}, tsj: {},cellCode:{}", wareCode, rcsTsj.getCode(),rcsTsj.getCellCode());
                return null;
            }
            String code = floor+"-"+codes[1]+"-"+codes[2];
            CellInfo cellInfo = cellInfoService.findByCode(wareCode,code);
            if(cellInfo==null){
                log.error("提升机入口库位配置有误，wareCode: {}, tsj: {},cellCode:{}", wareCode, rcsTsj.getCode(),rcsTsj.getCellCode());
                return null;
            }
            return cellInfo;
        } catch (Exception e) {
            log.error("查找提升机入口库位失败，wareCode: {}, floor: {}", wareCode, floor, e);
            return null;
        }
    }

    /**
     * 计算小车的综合评分
     *
     * @param car       小车信息
     * @param startCell 任务起点库位
     * @param jobInfo   任务信息
     * @return 评分对象
     */
    private CarScore calculateCarScore(RcsCarInfo car, CellInfo startCell, CellInfo endCell, JobInfo jobInfo) {
        CarScore score = new CarScore();

        try {
            // 0、优先判断：如果小车当前位置就是任务起点，给予最高优先级
            if (car.getFromCellCode() != null && car.getFromCellCode().equals(startCell.getCode())) {
                // 小车就在起点位置，必须优先选择该小车
                score.isAvailable = true;
                score.distanceScore = 0.0;  // 距离为0
                score.taskQueueScore = 0.0; // 忽略任务队列
                score.batteryScore = 0.0;    // 忽略电量
                score.totalScore = 0.0;      // 总分为0（最优）
                score.reason = "小车位置与起点一致，优先选择";

                log.info("小车 {} 位置与任务起点一致，给予最高优先级，位置: {}", car.getCode(), startCell.getCode());
                return score;
            }

            // 1、检查小车基本状态
            // 1.1 检查电量（低于20%不分配）
            RcsCarType carType = rcsCarTypeService.findById(car.getRcsCarTypeId());
            if (car.getBatteryLevel() != null && car.getBatteryLevel() < carType.getMinChargeLevel()) {
                score.isAvailable = false;
                score.reason = "电量不足：" + car.getBatteryLevel() + "%";
                return score;
            }
            score.batteryScore = car.getBatteryLevel() != null ? (100 - car.getBatteryLevel()) : 0;

            // 1.2 检查充电状态（充电中不分配）
            if (car.getIsCharge() == 1) {
                score.isAvailable = false;
                score.reason = "小车充电中";
                return score;
            }

            // 1.3 检查数据库中的任务状态
            // taskState: 0-空闲, 1-执行中, -1-充电, -2-故障
            if (car.getTaskState() != null && car.getTaskState() == -2L) {
                score.isAvailable = false;
                score.reason = "小车故障";
                return score;
            }

            Map<String, Object> redisState = zkCarUtil.getCarStateFromRedis(Integer.parseInt(car.getCode()));
            if (redisState == null) {
                score.isAvailable = false;
                score.reason = "小车未获取到遥测数据";
                return score;
            }
            // 检查Redis中的状态
            String status = (String) redisState.get("status");
            if ("ERROR".equals(status)) {
                score.isAvailable = false;
                score.reason = "小车Redis状态异常";
                return score;
            }

            // 如果Redis中有更新的电量信息，使用Redis的
            Object redisBattery = redisState.get("battery");
            if (redisBattery != null) {
                Integer battery = redisBattery instanceof Integer ?
                        (Integer) redisBattery :
                        Integer.parseInt(redisBattery.toString());
                if (battery < 20) {
                    score.isAvailable = false;
                    score.reason = "Redis电量不足：" + battery + "%";
                    return score;
                }
                score.batteryScore = 100 - battery;
            }
            // 3、计算距离评分
            score.distanceScore = calculateDistance(car, startCell);
            if (score.distanceScore < 0) {
                // 无法计算距离，给一个中等分数
                score.distanceScore = 5000.0;
                score.reason = "无法计算距离（小车位置未知）";
            }

            // 4、计算任务队列评分
            Condition jobCondition = new Condition(JobInfo.class);
            jobCondition.createCriteria()
                    .andEqualTo("rcsCarId", car.getId())
                    .andNotEqualTo("state", 3);  // 排除已完成的任务
            List<JobInfo> carJobs = jobInfoService.findByCondition(jobCondition);
            score.taskQueueCount = carJobs != null ? carJobs.size() : 0;
            score.taskQueueScore = score.taskQueueCount * 1000.0; // 每个任务增加1000分

            // 5、计算总分（距离权重60%，任务队列权重30%，电量权重10%）
            score.totalScore = score.distanceScore * 0.8 +
                    score.taskQueueScore * 0.1 +
                    score.batteryScore * 0.1;

            score.isAvailable = true;

        } catch (Exception e) {
            log.error("计算小车评分失败，小车ID: {}", car.getId(), e);
            score.isAvailable = false;
            score.reason = "评分计算异常：" + e.getMessage();
        }

        return score;
    }

    /**
     * 计算小车到目标点的距离（毫米）
     *
     * @param car        小车信息
     * @param targetCell 目标库位
     * @return 距离值（毫米），如果无法计算返回-1
     */
    private double calculateDistance(RcsCarInfo car, CellInfo targetCell) {
        try {
            // 优先使用小车当前位置库位
            CellInfo carCell = null;
            if (car.getFromCellCode() != null && !car.getFromCellCode().isEmpty()) {
                carCell = cellInfoService.findBy("code", car.getFromCellCode());
            }

            int score = 0;
            if(car.getCurrentZ()!=targetCell.getZ()){
                score = 10000;
            }

            // 如果没有库位信息，尝试使用坐标
            if (carCell == null || carCell.getX() == null || carCell.getY() == null) {
                if (car.getCurrentX() != null && car.getCurrentY() != null) {
                    // 使用小车当前坐标
                    if (targetCell.getX() != null && targetCell.getY() != null) {
                        int dx = targetCell.getX() - car.getCurrentX();
                        int dy = targetCell.getY() - car.getCurrentY();
                        return Math.sqrt(dx * dx + dy * dy)+score;
                    }
                }
                return -1; // 无法计算距离
            }

            // 使用库位坐标计算距离
            if (targetCell.getX() != null && targetCell.getY() != null) {
                int dx = targetCell.getX() - carCell.getX();
                int dy = targetCell.getY() - carCell.getY();
                return Math.sqrt(dx * dx + dy * dy)+score;
            }

            return -1;
        } catch (Exception e) {
            log.error("计算距离失败", e);
            return -1;
        }
    }


    @Autowired
    private AutoService autoService;

    /**
     * 小车评分内部类
     */
    private static class CarScore {
        boolean isAvailable = false;        // 是否可用
        String reason = "";                  // 不可用原因
        double distanceScore = 0.0;          // 距离评分（毫米）
        double taskQueueScore = 0.0;         // 任务队列评分
        double batteryScore = 0.0;           // 电量评分（100-电量值）
        int taskQueueCount = 0;              // 任务队列数量
        double totalScore = 0.0;             // 总评分

        @Override
        public String toString() {
            if (!isAvailable) {
                return "不可用 - " + reason;
            }
            return String.format("距离:%.1fmm, 任务数:%d, 电量评分:%.1f, 总分:%.2f",
                    distanceScore, taskQueueCount, batteryScore, totalScore);
        }
    }


    /**
     * 提升机结束后 给小车换层
     * 查询当前提升机所在层
     * 提升机所在层为当前层
     * 查询当前层的提升机位置 赋值给小车
     * 调用小车接口 换层
     *
     * @param jobInfo
     * @return
     */
    public Boolean setCarFloor(JobInfo jobInfo) {
        try {
            // 1、查询任务信息
            TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
            if (taskInfo == null) {
                jobInfoService.updateMemo(jobInfo, "【换层】任务不存在");
                return false;
            }

            // 2、查询小车信息
            if (jobInfo.getRcsCarId() == null) {
                jobInfoService.updateMemo(jobInfo, "【换层】小车ID为空");
                return false;
            }
            RcsCarInfo carInfo = rcsCarInfoService.selectRcsCarInfoById(jobInfo.getRcsCarId());
            if (carInfo == null) {
                jobInfoService.updateMemo(jobInfo, "【换层】未找到小车信息");
                return false;
            }



            // 3、查询提升机当前所在层
            RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
            if(rcsTsj==null){
                jobInfoService.updateMemo(jobInfo, "【换层】未找到提升机信息");
                return false;
            }
            Short currentFloor = (Short) deviceValueService.readValueByCode(rcsTsj.getDeviceCode(), "targetFloor");

            if (currentFloor == null) {
                jobInfoService.updateMemo(jobInfo, "【换层】无法读取提升机当前楼层");
                return false;
            }

            log.info("提升机当前所在层: {}, 准备给小车 {} 换层", currentFloor, carInfo.getCode());

            // 4、查询当前层的提升机库位
            CellInfo tsjCell = findTsjEntranceCellByFloor(taskInfo.getWareCode(), rcsTsj, currentFloor.intValue());
            if (tsjCell == null) {
                jobInfoService.updateMemo(jobInfo, String.format("【换层】未找到楼层%d的提升机库位", currentFloor));
                return false;
            }

            // 5、更新小车的位置和楼层信息
            carInfo.setFromCellCode(tsjCell.getCode());
            carInfo.setToCellCode(tsjCell.getCode());
            carInfo.setZ(currentFloor.intValue());
            rcsCarInfoService.update(carInfo);

            log.info("小车 {} 位置信息已更新：库位={}, 楼层={}", carInfo.getCode(), tsjCell.getCode(), currentFloor);

            // 6、调用小车接口，发送换层指令（LOCATION）
            if (carInfo.getIp() == null || carInfo.getIp().isEmpty()) {
                jobInfoService.updateMemo(jobInfo, "【换层】小车IP地址为空，无法发送换层指令");
                return false;
            }

            // 构建 LOCATION 指令数据
            Map<String, Object> location = new HashMap<>();
            location.put("x", tsjCell.getSubX());
            location.put("y", tsjCell.getSubY());
            location.put("z", tsjCell.getSubZ());

            Map<String, Object> bodyData = new HashMap<>();
            bodyData.put("operationType", "DATA");
            bodyData.put("operationCode", "UPDATE_FLOOR");
            bodyData.put("location", location);

            // 创建设备消息
            Long requestId = System.currentTimeMillis();
            ZkDeviceMessage zkMessage = ZkDeviceMessage.ZkMessageUtil.createRequest(
                    "InstructionRequestMsg",
                    Integer.parseInt(carInfo.getCode()),
                    requestId,
                    "2.0.0",
                    bodyData
            );

            // 同步发送换层指令并等待响应
            log.info("发送换层指令给小车 {}, 目标楼层: {}, 位置: ({}, {}, {})",
                    carInfo.getCode(), currentFloor, tsjCell.getSubX(), tsjCell.getSubY(), tsjCell.getSubZ());


            zkCarUtil.sendMessageAsync(carInfo.getIp(), zkMessage);
            jobInfoService.updateMemo(jobInfo,
                    String.format("【换层】指令已发送，小车切换至楼层%d，位置：%s", currentFloor, tsjCell.getCode()));

            /**
             * 这个地方，应该再检测一下遥测数据的位置
             * 判断是否换层成功
             */
            // 等待一段时间让小车完成换层操作（2秒）
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("等待换层完成时被中断，jobId: {}", jobInfo.getId());
            }

            // 检查遥测数据是否及时（3秒内）
            String carCode = carInfo.getCode();
            boolean hasRecentData = zkCarRedisUtil.hasRecentData(carCode, 3000);

            if (!hasRecentData) {
                long timeSinceUpdate = zkCarRedisUtil.getTimeSinceUpdate(carCode);

                jobInfoService.updateMemo(jobInfo,
                        String.format("【换层】遥测数据超时，无法验证换层结果，小车: %s", carCode));

                return false;
            }

            // 获取遥测数据中的z坐标
            Integer telemetryZ = zkCarRedisUtil.getIntValue(carCode, "z");
            Integer telemetryX = zkCarRedisUtil.getIntValue(carCode, "x");
            Integer telemetryY = zkCarRedisUtil.getIntValue(carCode, "y");

            if (telemetryZ == null) {
                log.warn("【换层】无法获取小车遥测z坐标，无法验证换层结果，jobId: {}, carCode: {}",
                        jobInfo.getId(), carCode);
                jobInfoService.updateMemo(jobInfo,
                        String.format("【换层】无法获取遥测z坐标，小车: %s", carCode));
                return false;
            }

            // 获取目标楼层的提升机库位的subZ坐标
            Integer targetSubZ = tsjCell.getSubZ();
            if (telemetryZ == targetSubZ) {
                jobInfoService.updateMemo(jobInfo,
                        String.format("【换层】验证成功，小车已切换至楼层%d，遥测坐标: (%d, %d, %d)",
                                currentFloor, telemetryX != null ? telemetryX : 0,
                                telemetryY != null ? telemetryY : 0, telemetryZ));
                return true;
            } else {

                jobInfoService.updateMemo(jobInfo,
                        String.format("【换层】验证失败，遥测z坐标(%d)与目标楼层(%d)不一致，小车: %s",
                                telemetryZ, currentFloor, carCode));
                return false;
            }
        } catch (Exception e) {
            log.error("小车换层失败，jobId: {}", jobInfo.getId(), e);
            jobInfoService.updateMemo(jobInfo, "【换层】失败：" + e.getMessage());
            return false;
        }
    }


    public Boolean setCarFloorBak(JobInfo jobInfo) {
        try {
            // 1、查询任务信息
            TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
            if (taskInfo == null) {
                jobInfoService.updateMemo(jobInfo, "【换层】任务不存在");
                return false;
            }

            // 2、查询小车信息
            if (jobInfo.getRcsCarId() == null) {
                jobInfoService.updateMemo(jobInfo, "【换层】小车ID为空");
                return false;
            }

            RcsCarInfo carInfo = rcsCarInfoService.selectRcsCarInfoById(jobInfo.getRcsCarId());
            if (carInfo == null) {
                jobInfoService.updateMemo(jobInfo, "【换层】未找到小车信息");
                return false;
            }

            RcsTsj rcsTsj = rcsTsjService.findBy("currentTaskId",jobInfo.getTaskId());
            if(rcsTsj==null){
                jobInfoService.updateMemo(jobInfo, "【换层】未找到提升机信息");
                return false;
            }

            // 3、查询提升机当前所在层
            Short currentFloor = (Short) deviceValueService.readValueByCode(rcsTsj.getDeviceCode(), "current_floor");
            if (currentFloor == null) {
                jobInfoService.updateMemo(jobInfo, "【换层】无法读取提升机当前楼层");
                return false;
            }

            log.info("提升机当前所在层: {}, 准备给小车 {} 换层", currentFloor, carInfo.getCode());

            // 4、查询当前层的提升机库位
            CellInfo tsjCell = findTsjEntranceCellByFloor(taskInfo.getWareCode(),rcsTsj, currentFloor.intValue());
            if (tsjCell == null) {
                jobInfoService.updateMemo(jobInfo, String.format("【换层】未找到楼层%d的提升机库位", currentFloor));
                return false;
            }

            // 5、更新小车的位置和楼层信息
            carInfo.setFromCellCode(tsjCell.getCode());
            carInfo.setToCellCode(tsjCell.getCode());
            carInfo.setZ(currentFloor.intValue());
            rcsCarInfoService.update(carInfo);

            log.info("小车 {} 位置信息已更新：库位={}, 楼层={}", carInfo.getCode(), tsjCell.getCode(), currentFloor);

            // 6、调用小车接口，发送换层指令（LOCATION）
            if (carInfo.getIp() == null || carInfo.getIp().isEmpty()) {
                jobInfoService.updateMemo(jobInfo, "【换层】小车IP地址为空，无法发送换层指令");
                return false;
            }

            // 构建 LOCATION 指令数据
            Map<String, Object> location = new HashMap<>();
            location.put("x", tsjCell.getSubX());
            location.put("y", tsjCell.getSubY());
            location.put("z", tsjCell.getSubZ());

            Map<String, Object> bodyData = new HashMap<>();
            bodyData.put("operationType", "DATA");
            bodyData.put("operationCode", "UPDATE_FLOOR");
            bodyData.put("location", location);

            // 创建设备消息
            Long requestId = System.currentTimeMillis();
            ZkDeviceMessage zkMessage = ZkDeviceMessage.ZkMessageUtil.createRequest(
                    "InstructionRequestMsg",
                    Integer.parseInt(carInfo.getCode()),
                    requestId,
                    "2.0.0",
                    bodyData
            );

            // 同步发送换层指令并等待响应
            log.info("发送换层指令给小车 {}, 目标楼层: {}, 位置: ({}, {}, {})",
                    carInfo.getCode(), currentFloor, tsjCell.getSubX(), tsjCell.getSubY(), tsjCell.getSubZ());

            String responseJson = zkCarUtil.sendMessageSync(carInfo.getIp(), zkMessage);
            zkCarUtil.sendMessageSync(carInfo.getIp(), zkMessage);

            // 判断返回值是否为空
            if (responseJson == null || responseJson.trim().isEmpty()) {
                jobInfoService.updateMemo(jobInfo, "【换层】小车无响应或响应超时，小车: " + carInfo.getCode());
                log.error("【换层】小车无响应，carCode: {}, jobId: {}", carInfo.getCode(), jobInfo.getId());
                return false;
            }

            // 解析返回值，验证响应状态
            try {
                ZkDeviceMessage responseMessage = ZkDeviceMessage.ZkMessageUtil.fromJson(responseJson);

                // 检查响应是否成功
                if (responseMessage.getResponse() != null &&
                        responseMessage.getResponse().getHeader() != null) {

                    Integer resultCode = responseMessage.getResponse().getHeader().getCode();
                    String resultMsg = responseMessage.getResponse().getHeader().getMsg();

                    if (resultCode != null && resultCode == 0) {
                        // 成功
                        log.info("【换层】小车 {} 成功接收换层指令，楼层: {}, 位置: {}",
                                carInfo.getCode(), currentFloor, tsjCell.getCode());
                        jobInfoService.updateMemo(jobInfo,
                                String.format("【换层】成功，小车已切换至楼层%d，位置：%s", currentFloor, tsjCell.getCode()));
                        return true;
                    } else {
                        // 失败
                        String errorMsg = String.format("【换层】小车拒绝指令，错误码: %d, 错误信息: %s",
                                resultCode, resultMsg != null ? resultMsg : "未知错误");
                        log.error(errorMsg + ", carCode: {}, jobId: {}", carInfo.getCode(), jobInfo.getId());
                        jobInfoService.updateMemo(jobInfo, errorMsg);
                        return false;
                    }
                } else {
                    log.warn("【换层】小车响应格式异常，响应: {}", responseJson);
                    jobInfoService.updateMemo(jobInfo, "【换层】小车响应格式异常");
                    return false;
                }

            } catch (Exception parseEx) {
                log.error("【换层】解析小车响应失败，响应内容: {}", responseJson, parseEx);
                jobInfoService.updateMemo(jobInfo, "【换层】解析响应失败: " + parseEx.getMessage());
                return false;
            }

        } catch (Exception e) {
            log.error("小车换层失败，jobId: {}", jobInfo.getId(), e);
            jobInfoService.updateMemo(jobInfo, "【换层】失败：" + e.getMessage());
            return false;
        }
    }


    @Autowired
    private JxgZkTsjHandle jxgZkTsjHandle;

    /**
     * 同层job运行 在successPre执行
     * 前置条件：rcsMainHandel.pathRun(jobInfo) 已经完成路径占用（state=1）
     * 本方法职责：
     * 1. 查询并处理该任务的未处理上报数据
     * 2. 查询已占用的路径（state=1）
     * 3. 构建路径指令发送给车子
     * 4. 更新路径状态为执行中（state=2）
     */
    public Boolean pathRun(JobInfo jobInfo) {
        // 0、查询并处理该任务的未处理上报数据，判断是否可以继续执行
        processTaskResultByTaskCode(jobInfo);


        if (rcsMainHandel.pathRun(jobInfo)) {
            return true;
        }

        /**
         * 发送指令参考示例：
         * {"msgType": "InstructionRequestMsg",
         *  "robotId": 500001,
         *  "request": {
         *  "header": {
         *  "requestId": 112233,
         *  "version": "2.0.0",
         *  "extParam": "1556"
         *  },
         *  "body": {
         *  "operationType": "COMMAND",
         *  "operationCode": "GO",
         *  "taskId": 1122223,
         *  "path": [{
         *  "x": 11002,
         *  "y": 12567,
         *  "z": 1
         *  },
         *  {
         *  "x": 11002,
         *  "y": 13567,
         *  "z": 1
         *  },
         *  {
         *  "x": 11002,
         *  "y": 14567,
         *  "z": 1
         *  }
         *  ]
         *  }
         *  }}
         */

        try {
            // 1、验证参数和查询小车信息
            if (jobInfo == null || jobInfo.getRcsCarId() == null) {
                jobInfoService.updateMemo(jobInfo, "jobInfo或小车ID为空");
                return false;
            }

            RcsCarInfo carInfo = rcsCarInfoService.selectRcsCarInfoById(jobInfo.getRcsCarId());
            if (carInfo == null) {
                jobInfoService.updateMemo(jobInfo, "未找到小车信息，小车ID: " + jobInfo.getRcsCarId());
                return false;
            }

            // 判断小车是否空闲（taskState为0表示空闲）
            if (carInfo.getTaskState() != null && carInfo.getTaskState() != 0 && !Objects.equals(carInfo.getTaskState(), jobInfo.getId())) {

                JobInfo runningJob = jobInfoService.findById(carInfo.getTaskState());
                if (runningJob != null && !runningJob.getId().equals(jobInfo.getId()) && !runningJob.getTaskId().equals(jobInfo.getTaskId())) {
                    jobInfoService.updateMemo(jobInfo, String.format("小车 %s 正在执行任务 %s，无法执行新任务", carInfo.getCode(), runningJob.getId()));
                    return false;
                }

//                TaskInfo runningTask = taskInfoService.findById(carInfo.getTaskState());
//                if(runningTask != null){
//                    jobInfoService.updateMemo(jobInfo, String.format("小车 %s 正在执行任务 %s，任务类型：%s", carInfo.getCode(), runningTask.getId(), runningTask.getType()));
//                }

//                if(runningJob==null && runningTask==null){
//                    carInfo.setTaskState(0L);
//                    rcsCarInfoService.update(carInfo);
//                }else {
//                    jobInfoService.updateMemo(jobInfo,"小车不是空闲状态，小车: "+ carInfo.getCode());
//                    return false;
//                }
            }

            // 2、查询已占用的路径（state=1表示已占用）
            Condition pathCondition = new Condition(RcsCarPath.class);
            pathCondition.createCriteria()
                    .andEqualTo("jobId", jobInfo.getId())
                    .andEqualTo("rcsCarId", carInfo.getId())
                    .andEqualTo("state", 1); // state=1：已占用待执行 

            List<RcsCarPath> carPathList = rcsCarPathService.findByCondition(pathCondition);
            if (carPathList == null || carPathList.isEmpty()) {
                return false;
            }

            // 3、根据fromCellId->toCellId构建有序的路径链表
            LinkedList<RcsCarPath> orderedPathList = buildOrderedPathList(carPathList);
            if (orderedPathList.isEmpty()) {
                jobInfoService.updateMemo(jobInfo, "无法构建有序路径链表，可能存在循环或断链，jobId: " + jobInfo.getId());
                return false;
            }

            // 获取第一个路径段的起点
            RcsCarPath firstPath = orderedPathList.getFirst();
            CellInfo startCell = cellInfoService.findById(firstPath.getFromCellId());
            if (startCell == null) {
                jobInfoService.updateMemo(jobInfo, "未找到起点库位信息，fromCellId: " + firstPath.getFromCellId());
                return false;
            }


            jobInfoService.updateMemo(jobInfo, "小车 " + carInfo.getCode() + " 准备执行路径，起点: " + startCell.getCode());

            // 4、构建路径指令数据：提取所有节点（起点 + 每个路径段的终点）
            List<Map<String, Object>> pathDataList = new ArrayList<>();

            // 添加起点
            if (startCell.getX() != null && startCell.getY() != null) {
                Map<String, Object> pathPoint = new HashMap<>();
                pathPoint.put("x", startCell.getSubX());
                pathPoint.put("y", startCell.getSubY());
                pathPoint.put("z", startCell.getSubZ());
                pathDataList.add(pathPoint);
            }

            // 添加每个路径段的终点
            for (RcsCarPath carPath : orderedPathList) {
                CellInfo toCell = cellInfoService.findById(carPath.getToCellId());

                /**
                 * 提升机
                 */
                if (toCell.getType().equals(5)) {
//                    String redisKey = "tsj:task_info_id";
//                    Object cachedTaskIdObj = redisCache.getCacheObject(redisKey);
//                    if(cachedTaskIdObj==null){
//                        jobInfoService.updateMemo(jobInfo,"当前路径经过提升机，但是提升机未被占用");
//                        return false;
//                    }
//                    Long taskId = Long.parseLong(cachedTaskIdObj.toString());
//                    if(!jobInfo.getTaskId().equals(taskId)){
//                        jobInfoService.updateMemo(jobInfo,"当前路径经过提升机，但是当前占用提升机非本任务（"+jobInfo.getTaskId()+"），当前占用提升机任务为："+taskId+"");
//                        return false;
//                    }
//
//                    Boolean tsjCan =  jxgZkTsjHandle.checkAtTargetFloor(jobInfo);
//                    if(!tsjCan){
//                        return false;
//                    }
                }

                if (toCell != null && toCell.getX() != null && toCell.getY() != null) {
                    Map<String, Object> pathPoint = new HashMap<>();
                    pathPoint.put("x", toCell.getSubX());
                    pathPoint.put("y", toCell.getSubY());
                    pathPoint.put("z", toCell.getSubZ());
                    pathDataList.add(pathPoint);
                }
            }

            if (pathDataList.isEmpty()) {
                jobInfoService.updateMemo(jobInfo, "路径数据为空，无法发送指令");
                return false;
            }

            /**
             * 在这个地方 应该检测遥测数据的及时性（3秒内），而且校验小车现在是否可以接收任务
             */
            // 4.1、检查小车遥测数据的及时性（3秒内）
            String carCode = carInfo.getCode();
            boolean hasRecentData = zkCarRedisUtil.hasRecentData(carCode, 3000);

            if (!hasRecentData) {
                long timeSinceUpdate = zkCarRedisUtil.getTimeSinceUpdate(carCode);
                if (timeSinceUpdate == -1) {
                    jobInfoService.updateMemo(jobInfo, "等待小车遥测数据上报，小车: " + carCode);
                } else {
                    jobInfoService.updateMemo(jobInfo,
                            String.format("小车遥测数据超时，小车:%s, 已过期%dms", carCode, timeSinceUpdate));
                }
                log.warn("小车遥测数据不及时，jobId: {}, carCode: {}, timeSinceUpdate: {}ms",
                        jobInfo.getId(), carCode, timeSinceUpdate);
                return false;
            }

            // 4.2、检查小车是否正在执行其他任务
            Long taskId = (Long) zkCarRedisUtil.getFieldValue(carCode, "taskId");
            Integer taskState = zkCarRedisUtil.getIntValue(carCode, "taskState");

            if (taskId != null && taskState != null && taskState == 0) {
                // taskState=0表示执行中，检查是否是其他任务
                String taskNoStr = jobInfo.getTaskNo();
                if (taskNoStr != null && !taskNoStr.isEmpty()) {
                    try {
                        long currentTaskNo = Long.parseLong(taskNoStr);
                        if (!taskId.equals(currentTaskNo)) {
                            jobInfoService.updateMemo(jobInfo,
                                    String.format("小车正在执行其他任务，小车:%s, 当前任务:%d", carCode, taskId));
                            log.warn("小车正在执行其他任务，jobId: {}, carCode: {}, 当前任务: {}",
                                    jobInfo.getId(), carCode, taskId);
                            return false;
                        }
                    } catch (NumberFormatException e) {
                        log.debug("解析taskNo失败: {}", taskNoStr);
                    }
                }
            }

            // 4.3、检查小车工作模式（0手动/1自动）
            Integer workingMode = zkCarRedisUtil.getIntValue(carCode, "workingMode");
            if (workingMode != null && workingMode == 0) {
                jobInfoService.updateMemo(jobInfo, "小车当前为手动模式，无法接收任务，小车: " + carCode);
                log.warn("小车为手动模式，jobId: {}, carCode: {}", jobInfo.getId(), carCode);
                return false;
            }

            // 4.4、验证小车当前位置是否与路径起点一致
            Integer telemetryX = zkCarRedisUtil.getIntValue(carCode, "x");
            Integer telemetryY = zkCarRedisUtil.getIntValue(carCode, "y");
            Integer telemetryZ = zkCarRedisUtil.getIntValue(carCode, "z");

            if (telemetryX != null && telemetryY != null && telemetryZ != null) {
                TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
                if (taskInfo != null) {
                    CellInfo telemetryCell = findCellByCoordinates(taskInfo.getWareCode(),
                            telemetryX, telemetryY, telemetryZ, 50);

                    if (telemetryCell != null) {
                        String currentFromCell = carInfo.getFromCellCode();

                        if (currentFromCell != null && !currentFromCell.equals(telemetryCell.getCode())) {
                            // 位置不一致，记录警告并更新小车位置
                            log.warn("小车实际位置与系统记录不符，jobId: {}, carCode: {}, 系统记录: {}, 遥测位置: {}",
                                    jobInfo.getId(), carCode, currentFromCell, telemetryCell.getCode());

                            jobInfoService.updateMemo(jobInfo,
                                    String.format("小车位置已更新，系统记录:%s -> 遥测位置:%s",
                                            currentFromCell, telemetryCell.getCode()));

                            // 更新小车位置
                            carInfo.setFromCellCode(telemetryCell.getCode());
                            carInfo.setToCellCode(telemetryCell.getCode());
                            carInfo.setZ(telemetryCell.getZ());
                            carInfo.setCurrentX(telemetryX);
                            carInfo.setCurrentY(telemetryY);
                            carInfo.setCurrentZ(telemetryZ);
                            rcsCarInfoService.update(carInfo);

                            // 推送位置更新到前端
                            MonitorWebSocketHandler.pushCarPosition(carInfo);
                        }

                        // 检查遥测位置是否与路径起点一致
                        if (!startCell.getCode().equals(telemetryCell.getCode())) {
                            jobInfoService.updateMemo(jobInfo,
                                    String.format("小车位置[%s]与路径起点[%s]不一致，无法执行",
                                            telemetryCell.getCode(), startCell.getCode()));
                            log.warn("小车位置与路径起点不一致，jobId: {}, carCode: {}, 小车位置: {}, 路径起点: {}",
                                    jobInfo.getId(), carCode, telemetryCell.getCode(), startCell.getCode());
                            return false;
                        }

                        log.info("✓ 小车位置验证通过，jobId: {}, carCode: {}, 当前位置: {}, 遥测坐标:({},{},{})",
                                jobInfo.getId(), carCode, telemetryCell.getCode(), telemetryX, telemetryY, telemetryZ);
                    } else {
                        log.warn("未找到遥测坐标对应的库位，jobId: {}, carCode: {}, 坐标:({},{},{})",
                                jobInfo.getId(), carCode, telemetryX, telemetryY, telemetryZ);
                        jobInfoService.updateMemo(jobInfo,
                                String.format("未找到小车遥测坐标对应的库位，坐标:(%d,%d,%d)",
                                        telemetryX, telemetryY, telemetryZ));
                        return false;
                    }
                }
            } else {
                log.warn("小车遥测坐标不完整，jobId: {}, carCode: {}, x: {}, y: {}, z: {}",
                        jobInfo.getId(), carCode, telemetryX, telemetryY, telemetryZ);
                jobInfoService.updateMemo(jobInfo, "小车遥测坐标不完整，小车: " + carCode);
                return false;
            }

            // 4.5、检查小车电量
            Integer powerPercent = zkCarRedisUtil.getIntValue(carCode, "powerPercent");
            if (!jobInfo.getType().equals("charge") && powerPercent != null && powerPercent < 20) {
                jobInfoService.updateMemo(jobInfo,
                        String.format("小车电量不足，小车:%s, 电量:%d%%", carCode, powerPercent));
                log.warn("小车电量不足，jobId: {}, carCode: {}, 电量: {}%",
                        jobInfo.getId(), carCode, powerPercent);
                return false;
            }

            log.info("✓ 小车遥测数据及状态校验通过，jobId: {}, carCode: {}, 电量:{}%, 工作模式:{}",
                    jobInfo.getId(), carCode, powerPercent, workingMode == 1 ? "自动" : "手动");

            Integer tasNo = autoService.getTodayTaskNo();
            jobInfo.setTaskNo((tasNo + ""));
            jobInfoService.update(jobInfo);


            // 5、构建GO指令消息
            Long requestId = System.currentTimeMillis();
            Map<String, Object> bodyData = new HashMap<>();
            bodyData.put("operationType", "COMMAND");
            bodyData.put("operationCode", "GO");
            bodyData.put("taskId", tasNo);
            bodyData.put("path", pathDataList);
            bodyData.put("isAllowEnterElevator", true);

            ZkDeviceMessage zkMessage = ZkDeviceMessage.ZkMessageUtil.createRequest(
                    "InstructionRequestMsg",
                    Integer.parseInt(carInfo.getCode()),
                    requestId,
                    "2.0.0",
                    bodyData
            );

            // 6、发送路径指令给小车

            if (carInfo.getIp() == null || carInfo.getIp().equals("")) {
                jobInfoService.updateMemo(jobInfo, "小车未获取到IP");
                return false;
            }

            String messageJson = ZkDeviceMessage.ZkMessageUtil.toJson(zkMessage);
            jobInfoService.updateMemo(jobInfo, "发送路径指令给小车 " + carInfo.getCode() + ": " + messageJson);

            // 发送指令并获取返回值
            String responseJson = zkCarUtil.sendMessageSync(carInfo.getIp(), zkMessage);

            // 判断返回值是否为空
            if (responseJson == null || responseJson.trim().isEmpty()) {
                jobInfoService.updateMemo(jobInfo, "小车无响应或响应超时，小车: " + carInfo.getCode());
                log.error("小车无响应，carCode: {}, jobId: {}", carInfo.getCode(), jobInfo.getId());
                return false;
            }

            // 解析返回值
            try {
                ZkDeviceMessage responseMessage = ZkDeviceMessage.ZkMessageUtil.fromJson(responseJson);

                // 检查是否为响应消息
                if (responseMessage == null || !responseMessage.isResponse()) {
                    jobInfoService.updateMemo(jobInfo, "小车返回数据格式错误，小车: " + carInfo.getCode());
                    log.error("小车返回数据格式错误，carCode: {}, response: {}", carInfo.getCode(), responseJson);
                    return false;
                }

                // 获取响应头
                ZkDeviceMessage.ZkResponseHeader header = responseMessage.getResponse().getHeader();
                if (header == null) {
                    jobInfoService.updateMemo(jobInfo, "小车返回数据缺少响应头，小车: " + carInfo.getCode());
                    log.error("小车返回数据缺少响应头，carCode: {}, response: {}", carInfo.getCode(), responseJson);
                    return false;
                }

                // 判断返回码：0表示成功，非0表示失败
                Integer code = header.getCode();
                String msg = header.getMsg();

                if (code == null || code != 0) {
                    // 小车返回失败
                    String errorMsg = "小车返回失败，code: " + code + ", msg: " + msg;
                    jobInfoService.updateMemo(jobInfo, errorMsg);
                    log.error("小车返回失败，carCode: {}, jobId: {}, code: {}, msg: {}",
                            carInfo.getCode(), jobInfo.getId(), code, msg);
                    return false;
                }

                // 小车返回成功
                jobInfoService.updateMemo(jobInfo, "小车确认接收指令成功，小车: " + carInfo.getCode() + ", msg: " + msg);
                // 7、更新小车状态为执行中
                carInfo.setTaskState(jobInfo.getId()); // taskState记录正在执行的jobId
                rcsCarInfoService.update(carInfo);

                // 8、更新路径状态为执行中（state: 1已占用 -> 2执行中）
                for (RcsCarPath carPath : orderedPathList) {
                    carPath.setState(2); // state=2：执行中
                    rcsCarPathService.update(carPath);
                }

            } catch (Exception e) {
                jobInfoService.updateMemo(jobInfo, "解析小车返回数据失败，小车: " + carInfo.getCode() + ", 错误: " + e.getMessage());
                log.error("解析小车返回数据失败，carCode: {}, response: {}", carInfo.getCode(), responseJson, e);
                return false;
            }
            return false;

        } catch (Exception e) {
            jobInfoService.updateMemo(jobInfo, "执行路径指令失败，jobId: " + jobInfo.getId() + ", 错误: " + e.getMessage());
            log.error("执行路径指令失败", e);
            return false;
        }
    }

    /**
     * 托盘顶升
     *
     * @param jobInfo 任务信息
     * @return 是否成功
     */
    public Boolean upPallet(JobInfo jobInfo) {

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());

        // 1、验证参数
        if (jobInfo == null || jobInfo.getRcsCarId() == null) {
            jobInfoService.updateMemo(jobInfo, "顶升失败：jobInfo或小车ID为空");
            return false;
        }

        // 2、查询小车信息
        RcsCarInfo carInfo = rcsCarInfoService.selectRcsCarInfoById(jobInfo.getRcsCarId());
        if (carInfo == null) {
            jobInfoService.updateMemo(jobInfo, "顶升失败：未找到小车信息，小车ID: " + jobInfo.getRcsCarId());
            return false;
        }

        Integer taskNo = autoService.getTodayTaskNo();
        jobInfo.setTaskNo(taskNo.toString());
        jobInfoService.update(jobInfo);

        CellInfo cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), carInfo.getFromCellCode());

        // 4、构建路径指令数据：提取所有节点（起点 + 每个路径段的终点）
        List<Map<String, Object>> pathDataList = new ArrayList<>();

        // 添加起点
        if (cellInfo != null) {
            Map<String, Object> pathPoint = new HashMap<>();
            pathPoint.put("x", cellInfo.getSubX());
            pathPoint.put("y", cellInfo.getSubY());
            pathPoint.put("z", cellInfo.getSubZ());
            pathDataList.add(pathPoint);
        } else {
            jobInfoService.updateMemo(jobInfo, "顶升失败：未找到小车位置信息: " + jobInfo.getRcsCarId());
            return false;
        }

        // 3、构建顶升指令消息
        Long requestId = System.currentTimeMillis();
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("operationType", "COMMAND");
        bodyData.put("operationCode", "GO_LIFT_UP");
        bodyData.put("taskId", taskNo);
        bodyData.put("path", pathDataList);


        ZkDeviceMessage zkMessage = ZkDeviceMessage.ZkMessageUtil.createRequest(
                "InstructionRequestMsg",
                Integer.parseInt(carInfo.getCode()),
                requestId,
                "2.0.0",
                bodyData
        );

        // 4、发送指令
        String messageJson = ZkDeviceMessage.ZkMessageUtil.toJson(zkMessage);
        jobInfoService.updateMemo(jobInfo, "发送顶升指令给小车 " + carInfo.getCode() + ": " + messageJson);

        // 发送指令并获取返回值
        String responseJson = zkCarUtil.sendMessageSync(carInfo.getIp(), zkMessage);

        // 使用通用方法处理小车响应
        return handleCarResponse(responseJson, carInfo, jobInfo, "托盘顶升");


    }

    /**
     * 托盘下降
     *
     * @param jobInfo 任务信息
     * @return 是否成功
     */
    public Boolean downPallet(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        // 1、验证参数
        if (jobInfo == null || jobInfo.getRcsCarId() == null) {
            jobInfoService.updateMemo(jobInfo, "下降失败：jobInfo或小车ID为空");
            return false;
        }

        // 2、查询小车信息
        RcsCarInfo carInfo = rcsCarInfoService.selectRcsCarInfoById(jobInfo.getRcsCarId());
        if (carInfo == null) {
            jobInfoService.updateMemo(jobInfo, "下降失败：未找到小车信息，小车ID: " + jobInfo.getRcsCarId());
            return false;
        }

        Integer taskNo = autoService.getTodayTaskNo();
        jobInfo.setTaskNo(taskNo.toString());
        jobInfoService.update(jobInfo);

        CellInfo cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), carInfo.getFromCellCode());

        // 4、构建路径指令数据：提取所有节点（起点 + 每个路径段的终点）
        List<Map<String, Object>> pathDataList = new ArrayList<>();

        // 添加起点
        if (cellInfo != null) {
            Map<String, Object> pathPoint = new HashMap<>();
            pathPoint.put("x", cellInfo.getSubX());
            pathPoint.put("y", cellInfo.getSubY());
            pathPoint.put("z", cellInfo.getSubZ());
            pathDataList.add(pathPoint);
        } else {
            jobInfoService.updateMemo(jobInfo, "顶升失败：未找到小车位置信息: " + jobInfo.getRcsCarId());
            return false;
        }

        // 3、构建下降指令消息
        Long requestId = System.currentTimeMillis();
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("operationType", "COMMAND");
        bodyData.put("operationCode", "GO_LIFT_DOWN");
        bodyData.put("taskId", taskNo);
        bodyData.put("path", pathDataList);

        ZkDeviceMessage zkMessage = ZkDeviceMessage.ZkMessageUtil.createRequest(
                "InstructionRequestMsg",
                Integer.parseInt(carInfo.getCode()),
                requestId,
                "2.0.0",
                bodyData
        );

        // 4、发送指令
        String messageJson = ZkDeviceMessage.ZkMessageUtil.toJson(zkMessage);
        jobInfoService.updateMemo(jobInfo, "发送下降指令给小车 " + carInfo.getCode() + ": " + messageJson);

        // 发送指令并获取返回值
        String responseJson = zkCarUtil.sendMessageSync(carInfo.getIp(), zkMessage);

        // 使用通用方法处理小车响应
        return handleCarResponse(responseJson, carInfo, jobInfo, "托盘下降");
    }

    /**
     * 根据 DeviceTaskResult的数据 判断顶升或者下降托盘的任务是否完成
     *
     * @param jobInfo
     * @return true-任务完成，false-任务未完成或执行中
     */
    public Boolean upOrDownSuccess(JobInfo jobInfo) {
        if (jobInfo == null || jobInfo.getId() == null) {
            log.warn("jobInfo 或 jobInfo.getId() 为空");
            return false;
        }

        String taskCode = jobInfo.getTaskNo();
        if (taskCode == null || taskCode.isEmpty()) {
            log.warn("jobInfo.taskNo 为空，jobId: {}", jobInfo.getId());
            return false;
        }

        // 1、根据 taskCode 查询未处理的任务上报记录
        DeviceTaskResult taskResult = deviceTaskResultService.getFirstState0ByTaskCode(taskCode);

        if (taskResult == null) {
            log.info("暂无任务上报数据，等待小车响应，taskCode: {}", taskCode);
            return false; // 没有上报数据，任务未完成
        }

        // 2、解析 data 字段（JSON 格式）
        Map<String, Object> dataMap = null;
        if (taskResult.getData() != null && !taskResult.getData().isEmpty()) {
            try {
                dataMap = objectMapper.readValue(taskResult.getData(), new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception e) {
                log.error("解析任务上报数据失败，data: {}", taskResult.getData(), e);
                throw new ServiceException("解析任务上报数据失败，data: " + taskResult.getData());
            }
        }

        // 3、根据任务状态判断是否完成
        String type = taskResult.getType();
        boolean isCompleted = false;

        if (dataMap != null) {
            Integer taskState = (Integer) dataMap.get("taskState");
            Integer pathState = (Integer) dataMap.get("pathState");
            Integer statusCode = (Integer) dataMap.get("statusCode");

            log.info("判断顶升/下降任务状态，jobId: {}, taskCode: {}, taskState: {}, pathState: {}, statusCode: {}",
                    jobInfo.getId(), taskCode, taskState, pathState, statusCode);

            // 判断任务是否完成
            if ("COMPLETED".equals(type) || (taskState != null && taskState == 1)) {
                // 任务已完成（taskState=1）
                isCompleted = true;
                jobInfoService.updateMemo(jobInfo, "顶升/下降任务已完成，statusCode: " + statusCode);
                log.info("顶升/下降任务完成，jobId: {}, taskCode: {}, statusCode: {}",
                        jobInfo.getId(), taskCode, statusCode);

            } else if ("EXECUTING".equals(type) || (taskState != null && taskState == 0)) {
                // 任务执行中（taskState=0）
                jobInfoService.updateMemo(jobInfo, "顶升/下降任务执行中");
                log.info("顶升/下降任务执行中，jobId: {}, taskCode: {}", jobInfo.getId(), taskCode);

            } else if (taskState != null && taskState == 2) {
                // 任务异常（taskState=2）
                // 根据 statusCode 判断是否为严重错误
                if (statusCode != null && statusCode != 0) {
                    // statusCode 非零，判断是否为可重试的错误
                    if (isRetryableStatusCode(statusCode)) {
                        // 可重试的错误，不抛异常
                        jobInfoService.updateMemo(jobInfo, String.format("顶升/下降任务异常（可重试），statusCode: %d，详情: %s", statusCode, dataMap));
                    } else {
                        // 不可重试的严重错误，抛出异常
                        String errorMsg = "顶升/下降任务异常 ，statusCode: " + statusCode + "，详情: " + dataMap;
                        jobInfoService.updateMemo(jobInfo, errorMsg);

                        isCompleted = false; // 未完成，等待重试
                    }
                } else {
                    // statusCode=0 或 null，但 taskState=2，记录警告，不抛异常
                    jobInfoService.updateMemo(jobInfo,
                            String.format("顶升/下降任务状态异常但可重试，taskState: 2, statusCode: %d", statusCode));
                    log.warn("顶升/下降任务状态异常但可重试，jobId: {}, taskCode: {}, taskState: 2, statusCode: {}",
                            jobInfo.getId(), taskCode, statusCode);
                    isCompleted = false; // 未完成，等待重试
                }
            }
        } else {
            log.warn("任务上报数据为空，taskCode: {}", taskCode);
        }

        // 4、标记该记录为已处理（state: 0 -> 1）
        taskResult.setState(1);
        deviceTaskResultService.update(taskResult);

        return isCompleted;
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

        String taskToCellCode = taskInfo.getToCellCode();
        CellInfo taskToCellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), taskToCellCode);
        if (taskToCellInfo == null) {
            jobInfoService.updateMemo(jobInfo, "目标位置：" + taskToCellCode + "不存在");
            return false;
        }
        // 1. 判断目标位置是否为提升机
        if (taskToCellInfo.getType().equals(5)) {
            jobInfoService.updateMemo(jobInfo, "目标位置：" + taskToCellCode + "不允许移动到提升机");
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
            jobInfo.setJudgeResult("no");
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
            if (carCurrentCellInfo.getType().equals(5)) {
                jobInfoService.updateMemo(jobInfo, "小车当前位置：" + carCurrentCellCode + "在提升机里面，需要提升机");
                jobInfo.setJudgeResult("yes");
                jobInfoService.update(jobInfo);
            }
            /**
             * 在同一层，但是小车当前位置到达目标位置，如果正常通道分配不了路径的话，但是算上提升机就能到达的话 就需要换层
             */
            // 1. 先尝试正常通道路径规划（不包含提升机库位）
            boolean normalPathExists = checkPathExists(carCurrentCellInfo, toCellInfo, taskInfo.getWareCode(), false);

            if (normalPathExists) {
                // 正常通道可以到达，不需要换层
                jobInfoService.updateMemo(jobInfo, "小车当前位置：" + carCurrentCellCode + "(层" + carCurrentCellInfo.getZ() + ")和目标：" + toCellCode + "(层" + toCellInfo.getZ() + ")在同一层，正常通道可达，不需要换层");
                jobInfo.setJudgeResult("no");
                jobInfoService.update(jobInfo);
                return true;
            }

            // 2. 正常通道分配不了路径，尝试算上提升机是否能到达
            boolean pathWithTsjExists = checkPathExists(carCurrentCellInfo, toCellInfo, taskInfo.getWareCode(), true);

            if (pathWithTsjExists) {
                // 算上提升机就能到达，需要换层
                jobInfoService.updateMemo(jobInfo, "小车当前位置：" + carCurrentCellCode + "(层" + carCurrentCellInfo.getZ() + ")和目标：" + toCellCode + "(层" + toCellInfo.getZ() + ")在同一层，正常通道不可达，但通过提升机可达，需要换层");
                jobInfo.setJudgeResult("yes");
                jobInfoService.update(jobInfo);
                return true;
            } else {
                // 即使算上提升机也无法到达，不需要换层（可能是路径配置问题）
                jobInfoService.updateMemo(jobInfo, "小车当前位置：" + carCurrentCellCode + "(层" + carCurrentCellInfo.getZ() + ")和目标：" + toCellCode + "(层" + toCellInfo.getZ() + ")在同一层，正常通道和提升机通道均不可达，不需要换层");
                jobInfo.setJudgeResult("no");
                jobInfoService.update(jobInfo);
                return true;
            }
        }


    }

    /**
     * 检查两个库位之间是否存在路径
     *
     * @param fromCellInfo 起点库位信息
     * @param toCellInfo   终点库位信息
     * @param wareCode     仓库编码
     * @param allowTsj     是否允许提升机库位参与路径规划（true-允许，false-不允许）
     * @return true-存在路径，false-不存在路径
     */
    private boolean checkPathExists(CellInfo fromCellInfo, CellInfo toCellInfo, String wareCode, boolean allowTsj) {
        try {
            // 1. 查询所有路径连接
            Condition condition = new Condition(CellLink.class);
            condition.createCriteria().andEqualTo("wareCode", wareCode);
            List<CellLink> cellLinkList = cellLinkService.findByCondition(condition);
            if (cellLinkList == null || cellLinkList.isEmpty()) {
                return false;
            }

            // 2. 查询所有同层库位
            Condition cellCondition = new Condition(CellInfo.class);
            cellCondition.createCriteria()
                    .andEqualTo("wareCode", wareCode)
                    .andEqualTo("z", fromCellInfo.getZ())
                    .andEqualTo("isDelete", 0);
            List<CellInfo> allCells = cellInfoService.findByCondition(cellCondition);
            if (allCells == null || allCells.isEmpty()) {
                return false;
            }

            // 3. 构建可通行库位ID集合
            Set<Long> passableCellIds = allCells.stream()
                    .filter(cell -> {
                        // 起点和终点必须可通行
                        if (cell.getId().equals(fromCellInfo.getId()) ||
                                cell.getId().equals(toCellInfo.getId())) {
                            return true;
                        }

                        // 过滤有货的库位
                        if (cell.getInvenState() != null && cell.getInvenState() > 0) {
                            return false;
                        }
                        // 过滤禁用的库位
                        if (cell.getDisableState() != null && cell.getDisableState() > 0) {
                            return false;
                        }

                        // 根据allowTsj参数决定是否过滤提升机库位
                        if (!allowTsj && cell.getType() != null && cell.getType().equals(5)) {
                            return false;
                        }
                        return true;
                    })
                    .map(CellInfo::getId)
                    .collect(java.util.stream.Collectors.toSet());

            // 4. 过滤CellLink：只保留起点和终点都可通行的路径连接
            List<CellLink> validLinks = cellLinkList.stream()
                    .filter(link -> {
                        // 过滤阻塞的路径
                        if (link.getIsBlocked() != null && link.getIsBlocked() == 1) {
                            return false;
                        }
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
                    .collect(java.util.stream.Collectors.toList());

            // 5. 检查过滤后是否还有可用路径
            if (validLinks.isEmpty()) {
                return false;
            }

            // 6. 使用BFS算法检查是否存在路径
            return bfsCheckPath(fromCellInfo.getId(), toCellInfo.getId(), validLinks);

        } catch (Exception e) {
            log.error("检查路径是否存在时发生异常，fromCell: {}, toCell: {}, allowTsj: {}",
                    fromCellInfo.getCode(), toCellInfo.getCode(), allowTsj, e);
            return false;
        }
    }

    /**
     * 使用BFS算法检查两个节点之间是否存在路径
     *
     * @param fromCellId 起点库位ID
     * @param toCellId   终点库位ID
     * @param validLinks 有效的路径连接列表
     * @return true-存在路径，false-不存在路径
     */
    private boolean bfsCheckPath(Long fromCellId, Long toCellId, List<CellLink> validLinks) {
        if (fromCellId.equals(toCellId)) {
            return true;
        }

        // 构建邻接表
        Map<Long, List<Long>> graph = new HashMap<>();
        for (CellLink link : validLinks) {
            graph.computeIfAbsent(link.getFromCellId(), k -> new ArrayList<>()).add(link.getToCellId());
        }

        // BFS遍历
        Queue<Long> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();
        queue.offer(fromCellId);
        visited.add(fromCellId);

        while (!queue.isEmpty()) {
            Long current = queue.poll();
            List<Long> neighbors = graph.get(current);
            if (neighbors != null) {
                for (Long neighbor : neighbors) {
                    if (neighbor.equals(toCellId)) {
                        return true; // 找到路径
                    }
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.offer(neighbor);
                    }
                }
            }
        }

        return false; // 未找到路径
    }

    /**
     * 从Redis里面获取小车的遥测数据，判断是否在5秒内有更新
     * 使用 ZkCarRedisUtil 工具类统一管理 Redis 操作
     * 作为任务执行判断条件，会更新任务备注信息
     *
     * @param jobInfo 任务信息（包含小车ID）
     * @return true-有5秒内的数据可继续执行，false-没有最近数据需等待
     */
    public Boolean redisHasCarData(JobInfo jobInfo) {
        if (jobInfo == null || jobInfo.getRcsCarId() == null) {
            log.warn("jobInfo 或 rcsCarId 为空，无法检查 Redis 数据");
            if (jobInfo != null) {
                jobInfoService.updateMemo(jobInfo, "参数错误：小车ID为空");
            }
            return false;
        }

        try {
            // 1、根据 jobInfo 中的小车ID查询小车信息
            RcsCarInfo carInfo = rcsCarInfoService.findById(jobInfo.getRcsCarId());

            if (carInfo == null) {
                log.warn("未找到小车信息，carId: {}", jobInfo.getRcsCarId());
                jobInfoService.updateMemo(jobInfo, "未找到小车信息，小车ID: " + jobInfo.getRcsCarId());
                return false;
            }

            // 2、检查小车是否已连接
//            if (carInfo.getIsConnected() == null || carInfo.getIsConnected() != 1) {
//                log.debug("小车未连接，carCode: {}, isConnected: {}",
//                         carInfo.getCode(), carInfo.getIsConnected());
//                jobInfoService.updateMemo(jobInfo, "小车未连接，小车: " + carInfo.getCode());
//                return false;
//            }
//
            // 3、使用 ZkCarRedisUtil 检查小车是否有最近5秒内的遥测数据
            String carCode = carInfo.getCode();
            boolean hasRecentData = zkCarRedisUtil.hasRecentData(carCode, 3000);

            if (!hasRecentData) {
                long timeSinceUpdate = zkCarRedisUtil.getTimeSinceUpdate(carCode);

                if (timeSinceUpdate == -1) {
                    // 没有数据
                    log.debug("Redis 中无小车遥测数据，carCode: {}", carCode);
                    jobInfoService.updateMemo(jobInfo, "等待小车遥测数据上报，小车: " + carCode);
                } else {
                    // 数据超时
                    log.debug("小车遥测数据已过期，jobId: {}, carCode: {}",
                            jobInfo.getId(), carCode);
                    jobInfoService.updateMemo(jobInfo,
                            String.format("小车遥测数据超时，小车:%s", carCode));
                }

                return false;
            }

            // 4、有最近的数据，提取关键信息用于日志和备注
            long timeSinceUpdate = zkCarRedisUtil.getTimeSinceUpdate(carCode);
            Integer x = zkCarRedisUtil.getIntValue(carCode, "x");
            Integer y = zkCarRedisUtil.getIntValue(carCode, "y");
            Integer z = zkCarRedisUtil.getIntValue(carCode, "z");
            Integer powerPercent = zkCarRedisUtil.getIntValue(carCode, "powerPercent");


            // 5、根据遥测坐标查找对应的库位
            if (x != null && y != null && z != null) {
                CellInfo actualCell = findCellByCoordinates(carInfo.getWareCode(), x, y, z, 50);

                if (actualCell != null) {
                    // 判断遥测坐标对应的库位是否与系统记录的fromCellCode一致
                    String fromCellCode = carInfo.getFromCellCode();

                    if (fromCellCode != null && !fromCellCode.equals(actualCell.getCode())) {
                        // 位置不匹配
                        log.warn("小车实际位置与系统记录不符，jobId: {}, carCode: {}, 系统记录: {}, 遥测位置: {}, 坐标:({},{},{})",
                                jobInfo.getId(), carCode, fromCellCode, actualCell.getCode(), x, y, z);
                        jobInfoService.updateMemo(jobInfo,
                                String.format("小车位置异常，系统记录:%s，实际位置:%s，小车:%s",
                                        fromCellCode, actualCell.getCode(), carCode));
                        return false;
                    }

                    log.debug("✓ 小车位置验证通过，jobId: {}, carCode: {}, 当前位置: {}, 坐标:({},{},{})",
                            jobInfo.getId(), carCode, actualCell.getCode(), x, y, z);
                } else {
                    log.warn("未找到遥测坐标对应的库位，jobId: {}, carCode: {}, 坐标:({},{},{})",
                            jobInfo.getId(), carCode, x, y, z);
                    jobInfoService.updateMemo(jobInfo,
                            String.format("未找到小车遥测坐标对应的库位，坐标:(%d,%d,%d)，小车:%s", x, y, z, carCode));
                    return false;
                }
            } else {
                log.warn("小车遥测坐标为空，jobId: {}, carCode: {}", jobInfo.getId(), carCode);
                jobInfoService.updateMemo(jobInfo, "小车遥测坐标为空，小车: " + carCode);
                return false;
            }


            /**
             * 需要查看遥测数据中  有没有正在执行的任务
             *
             */
            Long taskId = (Long) zkCarRedisUtil.getFieldValue(carCode, "taskId");
            Integer taskState = zkCarRedisUtil.getIntValue(carCode, "taskState");

            Long jobId = jobInfo.getId();
            String taskNo = jobInfo.getTaskNo();
            if (taskId != null && taskState == 0) {
                jobInfoService.updateMemo(jobInfo, "小车遥测数据显示当前小车正在执行任务 " + carCode + " 任务号：" + taskId);
                return false;
            }


            // 18、提取手自动状态（0手动/1自动）
            Integer workingMode = zkCarRedisUtil.getIntValue(carCode, "workingMode");
            if (workingMode == 0) {
                jobInfoService.updateMemo(jobInfo, "小车遥测数据显示当前小车为手动模式 " + carCode);
                return false;
            }


            log.info("✓ 小车有3秒内的遥测数据，jobId: {}, carCode: {}, 更新于{}ms前, 位置:({},{},{}), 电量:{}%",
                    jobInfo.getId(), carCode, timeSinceUpdate, x, y, z, powerPercent);

            jobInfoService.updateMemo(jobInfo,
                    String.format("小车遥测数据正常，更新于%dms前，电量:%s%%，小车:%s",
                            timeSinceUpdate, powerPercent != null ? powerPercent : "N/A", carCode));
            return true;

        } catch (Exception e) {
            log.error("检查 Redis 小车遥测数据失败，jobId: {}, carId: {}",
                    jobInfo.getId(), jobInfo.getRcsCarId(), e);
            jobInfoService.updateMemo(jobInfo, "检查小车数据异常: " + e.getMessage());
            return false;
        }
    }

    /**
     * 判断 statusCode 是否为可重试的错误
     * <p>
     * 可重试的错误码（示例，根据实际情况调整）：
     * - 1: 路径失败/位置偏差（可重试）
     * - 2-10: 临时性错误（设备忙、传感器读取失败等）
     * <p>
     * 不可重试的严重错误码：
     * - 100+: 硬件故障、严重异常
     *
     * @param statusCode 状态码
     * @return true-可重试，false-不可重试
     */
    private boolean isRetryableStatusCode(Integer statusCode) {
        if (statusCode == null) {
            return false;
        }

        // 定义可重试的错误码范围
        // 1-50: 轻微错误，可重试
        // 51-99: 中等错误，暂不抛异常，记录日志
        // 100+: 严重错误，需要抛异常
        if (statusCode >= 1 && statusCode <= 99) {
            return true; // 可重试
        }

        return false; // 不可重试
    }

    /**
     * 根据遥测坐标查找对应的库位
     * 坐标误差范围：正负 tolerance
     *
     * @param wareCode  仓库编码
     * @param x         遥测X坐标
     * @param y         遥测Y坐标
     * @param z         遥测Z坐标
     * @param tolerance 坐标误差范围
     * @return 匹配的库位，如果未找到则返回 null
     */
    private CellInfo findCellByCoordinates(String wareCode, Integer x, Integer y, Integer z, int tolerance) {
        if (wareCode == null || x == null || y == null || z == null) {
            return null;
        }

        try {
            // 使用 Condition 构建查询条件
            Condition condition = new Condition(CellInfo.class);
            Condition.Criteria criteria = condition.createCriteria();

            // 仓库编码
            criteria.andEqualTo("wareCode", wareCode);

            // Z坐标精确匹配（楼层）
            criteria.andEqualTo("subZ", z);

            // X坐标范围查询（x ± tolerance）
            criteria.andBetween("subX", x - tolerance, x + tolerance);

            // Y坐标范围查询（y ± tolerance）
            criteria.andBetween("subY", y - tolerance, y + tolerance);

            // 查询符合条件的库位列表
            List<CellInfo> cellList = cellInfoService.findByCondition(condition);

            if (cellList == null || cellList.isEmpty()) {
                log.debug("未找到匹配坐标的库位，wareCode: {}, 坐标:({},{},{}), 误差范围: ±{}",
                        wareCode, x, y, z, tolerance);
                return null;
            }

            // 如果有多个匹配结果，抛出异常
            if (cellList.size() > 1) {
                StringBuilder cellCodes = new StringBuilder();
                for (CellInfo cell : cellList) {
                    if (cellCodes.length() > 0) {
                        cellCodes.append(", ");
                    }
                    cellCodes.append(cell.getCode()).append("(")
                            .append(cell.getSubX()).append(",")
                            .append(cell.getSubY()).append(",")
                            .append(cell.getSubZ()).append(")");
                }

                String errorMsg = String.format(
                        "根据遥测坐标找到多个匹配的库位，数据异常！wareCode: %s, 遥测坐标:(%d,%d,%d), 误差范围: ±%d, 匹配库位: [%s]",
                        wareCode, x, y, z, tolerance, cellCodes.toString()
                );

                log.error(errorMsg);
                throw new ServiceException(errorMsg);
            }

            // 只有一个匹配结果
            CellInfo matchedCell = cellList.get(0);
            log.debug("找到匹配库位，wareCode: {}, 库位: {}, 坐标:({},{},{})",
                    wareCode, matchedCell.getCode(), matchedCell.getSubX(), matchedCell.getSubY(), matchedCell.getSubZ());
            return matchedCell;

        } catch (ServiceException e) {
            // ServiceException 直接向上抛出，不捕获
            throw e;
        } catch (Exception e) {
            log.error("根据坐标查找库位失败，wareCode: {}, 坐标:({},{},{})", wareCode, x, y, z, e);
            return null;
        }
    }

    /**
     * 根据fromCellId->toCellId构建有序的路径链表
     *
     * @param carPathList 未排序的路径列表
     * @return 有序的路径链表
     */
    private LinkedList<RcsCarPath> buildOrderedPathList(List<RcsCarPath> carPathList) {
        LinkedList<RcsCarPath> orderedList = new LinkedList<>();

        if (carPathList == null || carPathList.isEmpty()) {
            return orderedList;
        }

        // 创建以fromCellId为key的映射，便于快速查找
        Map<Long, RcsCarPath> fromCellMap = new HashMap<>();
        Set<Long> allToCellIds = new HashSet<>();

        for (RcsCarPath path : carPathList) {
            fromCellMap.put(path.getFromCellId(), path);
            allToCellIds.add(path.getToCellId());
        }

        // 找到起始路径段：其fromCellId不是任何路径段的toCellId
        RcsCarPath startPath = null;
        for (RcsCarPath path : carPathList) {
            boolean isStart = true;
            for (RcsCarPath otherPath : carPathList) {
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
        if (startPath == null) {
            startPath = carPathList.get(0);
        }

        // 按照from->to的连接关系构建排序链表
        RcsCarPath current = startPath;
        Set<Long> visited = new HashSet<>();

        while (current != null && !visited.contains(current.getId())) {
            orderedList.add(current);
            visited.add(current.getId());

            // 查找下一条路径：fromCellId = 当前路径的toCellId
            Long nextFromCellId = current.getToCellId();
            current = fromCellMap.get(nextFromCellId);
        }

        // 如果还有未排序的路径（可能是断开的），追加到末尾
        for (RcsCarPath path : carPathList) {
            if (!visited.contains(path.getId())) {
                orderedList.add(path);
                log.warn("发现断开的路径段: fromCellId={}, toCellId={}",
                        path.getFromCellId(), path.getToCellId());
            }
        }

        // 检查构建的链表长度
        if (orderedList.size() != carPathList.size()) {
            log.warn("路径链表构建不完整，原始路径数: {}, 构建后路径数: {}",
                    carPathList.size(), orderedList.size());
        }

        return orderedList;
    }

    /**
     * 根据 JobInfo 处理该任务的未处理上报数据
     * 查询 device_task_result 表中指定 taskCode 且 state=0 的最早记录并处理
     *
     * @param jobInfo 任务信息
     * @return true-可以继续执行路径指令，false-不能继续（任务已完成或异常）
     */
    private Boolean processTaskResultByTaskCode(JobInfo jobInfo) {
        if (jobInfo == null || jobInfo.getId() == null) {
            log.warn("jobInfo 或 jobInfo.getId() 为空");
            return true; // 没有上报数据，允许继续执行
        }

        String taskCode = jobInfo.getTaskNo();


        // 1、根据 taskCode 查询未处理记录
        DeviceTaskResult taskResult = deviceTaskResultService.getFirstState0ByTaskCode(taskCode);

        if (taskResult == null) {
            return false; // 没有上报数据，允许继续执行
        }


        // 2、解析 data 字段（JSON 格式）
        Map<String, Object> dataMap = null;
        if (taskResult.getData() != null && !taskResult.getData().isEmpty()) {
            try {
                dataMap = objectMapper.readValue(taskResult.getData(), new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception e) {
                throw new ServiceException("解析任务上报数据失败，data: " + taskResult.getData());
            }
        }

        // 3、根据 type 和 data 内容进行业务处理，并判断是否继续执行
        String type = taskResult.getType();


        if (dataMap != null) {
            Integer taskState = (Integer) dataMap.get("taskState");
            Integer pathState = (Integer) dataMap.get("pathState");
            Integer statusCode = (Integer) dataMap.get("statusCode");

            // 根据任务状态进行处理并决定是否继续
            if ("COMPLETED".equals(type) || (taskState != null && taskState == 1)) {
                // 任务已完成，释放路径
                handleTaskCompleted(jobInfo);

            } else if ("EXECUTING".equals(type) || (taskState != null && taskState == 0)) {
                // 任务执行中，记录日志
                handleTaskExecuting(jobInfo, taskResult, dataMap);

            } else if (type != null && type.startsWith("EXCEPTION_")) {
                // 任务异常，记录错误
                handleTaskException(jobInfo, taskResult, dataMap, statusCode);
                return false;
            }

            // 路径状态处理
            if (pathState != null && pathState == 1) {
                // 路径已完成
                handleTaskCompleted(jobInfo);

            }
        }
        // 4、标记该记录为已处理（state: 0 -> 1）
        taskResult.setState(1);
        deviceTaskResultService.update(taskResult);

        return true;
    }

    /**
     * 处理任务完成的情况
     */
    private void handleTaskCompleted(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        // 1、释放该 job 占用的路径（state: 2执行中 -> 3已完成）
        Condition pathCondition = new Condition(RcsCarPath.class);
        pathCondition.createCriteria()
                .andEqualTo("jobId", jobInfo.getId())
                .andEqualTo("state", 2); // state=2：执行中

        List<RcsCarPath> carPathList = rcsCarPathService.findByCondition(pathCondition);
        for (RcsCarPath carPath : carPathList) {
            carPath.setState(3); // state=3：已完成
            rcsCarPathService.update(carPath);
        }

        // 2、从路径链表中找到终点并更新小车位置
        if (carPathList != null && !carPathList.isEmpty() && jobInfo.getRcsCarId() != null) {
            // 建立链路：找到路径的终点（没有其他路径以它的 toCellCode 作为 fromCellCode）
            String finalDestination = findFinalDestination(carPathList);

            if (finalDestination != null) {
                RcsCarInfo carInfo = rcsCarInfoService.findById(jobInfo.getRcsCarId());
                if (carInfo != null) {
                    // 更新小车当前位置为链路终点
                    carInfo.setFromCellCode(finalDestination);
                    carInfo.setToCellCode(finalDestination);

                    // 同时更新小车的Z坐标（如果目标位置有库位信息）
                    CellInfo toCellInfo = cellInfoService.findByCode(taskInfo.getWareCode(), finalDestination);
                    if (toCellInfo != null) {
                        carInfo.setZ(toCellInfo.getZ());
                    }

                    rcsCarInfoService.update(carInfo);
                    log.info("路径完成后更新小车位置成功，carId: {}, 链路终点: {}", carInfo.getId(), finalDestination);

                    // 通过 WebSocket 推送小车位置更新到前端
                    MonitorWebSocketHandler.pushCarPosition(carInfo);
                    log.info("WebSocket推送小车位置更新，carCode: {}, 位置: {}", carInfo.getCode(), finalDestination);
                } else {
                    log.warn("未找到小车信息，carId: {}", jobInfo.getRcsCarId());
                }
            } else {
                log.warn("无法确定路径终点，jobId: {}", jobInfo.getId());
            }
        }

        jobInfoService.updateMemo(jobInfo, "路径已完成 ");
    }

    /**
     * 从路径列表中找到链路的终点
     * 终点的定义：某个路径的 toCellCode 不是任何其他路径的 fromCellCode
     *
     * @param carPathList 路径列表
     * @return 终点的 cellCode，如果无法确定则返回 null
     */
    private String findFinalDestination(List<RcsCarPath> carPathList) {
        if (carPathList == null || carPathList.isEmpty()) {
            return null;
        }

        // 如果只有一个路径点，直接返回它的 toCellCode
        if (carPathList.size() == 1) {
            return carPathList.get(0).getToCellCode();
        }

        // 收集所有的 fromCellCode（这些都是中间点或起点）
        Set<String> fromCellSet = new HashSet<>();
        for (RcsCarPath path : carPathList) {
            if (path.getFromCellCode() != null) {
                fromCellSet.add(path.getFromCellCode());
            }
        }

        // 找到不在 fromCellSet 中的 toCellCode，那就是终点
        for (RcsCarPath path : carPathList) {
            String toCell = path.getToCellCode();
            if (toCell != null && !fromCellSet.contains(toCell)) {
                // 找到终点
                return toCell;
            }
        }

        // 如果上述逻辑没找到（可能是环路或数据异常），返回最后一个路径的 toCellCode
        log.warn("无法通过链路逻辑找到终点，使用最后一个路径的 toCellCode");
        return carPathList.get(carPathList.size() - 1).getToCellCode();
    }

    /**
     * 处理任务执行中的情况
     */
    private void handleTaskExecuting(JobInfo jobInfo, DeviceTaskResult taskResult, Map<String, Object> dataMap) {
        try {
            log.info("处理任务执行中，jobId: {}", jobInfo.getId());
            // 可以在这里更新任务进度、记录日志等
        } catch (Exception e) {
            log.error("处理任务执行中失败，jobId: {}", jobInfo.getId(), e);
        }
    }

    /**
     * 处理任务异常的情况
     * 记录异常之后 应该从遥测数据中 （redis）获取小车的当前位置，然后更新到小车
     * 然后根据小车的位置  管理一下路径 哪一段已经走过了 ，没走过的应该重置为已经占用 未下发
     */
    private void handleTaskException(JobInfo jobInfo, DeviceTaskResult taskResult, Map<String, Object> dataMap, Integer statusCode) {
        try {
            // 1、记录异常信息
            String errorMsg = "任务执行异常，statusCode: " + statusCode + "，原因: " + dataMap;
            jobInfoService.updateMemo(jobInfo, errorMsg);
            log.error("任务执行异常，jobId: {}, statusCode: {}, 详情: {}", jobInfo.getId(), statusCode, dataMap);


//            Condition pathCondition = new Condition(RcsCarPath.class);
//            pathCondition.createCriteria()
//                    .andEqualTo("jobId", jobInfo.getId())
//                    .andEqualTo("rcsCarId", jobInfo.getRcsCarId())
//                    .andEqualTo("state", 2); // state=2：已经下发的路径  需要重新下发
//
//            List<RcsCarPath> carPathList = rcsCarPathService.findByCondition(pathCondition);
//            if (carPathList == null || carPathList.isEmpty()) {
//                return false;
//            }

//            // 2、获取小车信息
//            if (jobInfo.getRcsCarId() == null) {
//                log.warn("任务异常处理失败：小车ID为空，jobId: {}", jobInfo.getId());
//                return;
//            }
//
//            RcsCarInfo carInfo = rcsCarInfoService.selectRcsCarInfoById(jobInfo.getRcsCarId());
//            if (carInfo == null) {
//                log.warn("任务异常处理失败：未找到小车信息，jobId: {}, carId: {}", jobInfo.getId(), jobInfo.getRcsCarId());
//                return;
//            }
//
//            // 3、从Redis获取小车当前遥测位置
//            String carCode = carInfo.getCode();
//            Integer x = zkCarRedisUtil.getIntValue(carCode, "x");
//            Integer y = zkCarRedisUtil.getIntValue(carCode, "y");
//            Integer z = zkCarRedisUtil.getIntValue(carCode, "z");
//
//            if (x == null || y == null || z == null) {
//                log.warn("任务异常处理：无法从Redis获取小车遥测坐标，jobId: {}, carCode: {}", jobInfo.getId(), carCode);
//                jobInfoService.updateMemo(jobInfo, "异常处理失败：无法获取小车遥测坐标");
//                return;
//            }
//
//            log.info("从Redis获取小车遥测位置，jobId: {}, carCode: {}, 坐标:({},{},{})",
//                    jobInfo.getId(), carCode, x, y, z);
//
//            // 4、根据遥测坐标查找对应的库位
//            TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
//            if (taskInfo == null) {
//                log.warn("任务异常处理失败：任务不存在，jobId: {}", jobInfo.getId());
//                return;
//            }
//
//            CellInfo currentCell = findCellByCoordinates(taskInfo.getWareCode(), x, y, z, 50);
//            if (currentCell == null) {
//                log.warn("任务异常处理：未找到遥测坐标对应的库位，jobId: {}, 坐标:({},{},{})",
//                        jobInfo.getId(), x, y, z);
//                jobInfoService.updateMemo(jobInfo,
//                    String.format("异常处理：未找到小车当前位置对应的库位，坐标:(%d,%d,%d)", x, y, z));
//                return;
//            }
//
//            // 5、更新小车位置到数据库
//            String oldPosition = carInfo.getFromCellCode();
//            carInfo.setFromCellCode(currentCell.getCode());
//            carInfo.setToCellCode(currentCell.getCode());
//            carInfo.setZ(currentCell.getZ());
//            carInfo.setCurrentX(x);
//            carInfo.setCurrentY(y);
//            carInfo.setCurrentZ(z);
//            carInfo.setTaskState(0L); // 重置小车状态为空闲
//            rcsCarInfoService.update(carInfo);
//
//            log.info("任务异常处理：已更新小车位置，jobId: {}, carCode: {}, 旧位置: {}, 新位置: {}",
//                    jobInfo.getId(), carCode, oldPosition, currentCell.getCode());
//            jobInfoService.updateMemo(jobInfo,
//                String.format("异常处理：已更新小车位置从[%s]到[%s]", oldPosition, currentCell.getCode()));
//
//            // 通过 WebSocket 推送小车位置更新到前端
//            MonitorWebSocketHandler.pushCarPosition(carInfo);
//
//            // 6、查询该任务的所有执行中的路径（state=2）
//            Condition pathCondition = new Condition(RcsCarPath.class);
//            pathCondition.createCriteria()
//                    .andEqualTo("jobId", jobInfo.getId())
//                    .andEqualTo("rcsCarId", carInfo.getId())
//                    .andEqualTo("state", 2); // state=2：执行中
//
//            List<RcsCarPath> executingPathList = rcsCarPathService.findByCondition(pathCondition);
//            if (executingPathList == null || executingPathList.isEmpty()) {
//                log.info("任务异常处理：没有执行中的路径需要处理，jobId: {}", jobInfo.getId());
//                return;
//            }
//
//            // 7、构建有序路径链表
//            LinkedList<RcsCarPath> orderedPathList = buildOrderedPathList(executingPathList);
//
//            // 8、根据小车当前位置，判断哪些路径已经走过了
//            boolean foundCurrentPosition = false;
//            int completedCount = 0;
//            int resetCount = 0;
//
//            for (RcsCarPath path : orderedPathList) {
//                CellInfo fromCell = cellInfoService.findById(path.getFromCellId());
//                CellInfo toCell = cellInfoService.findById(path.getToCellId());
//
//                if (fromCell == null || toCell == null) {
//                    log.warn("路径库位信息不完整，pathId: {}, fromCellId: {}, toCellId: {}",
//                            path.getId(), path.getFromCellId(), path.getToCellId());
//                    continue;
//                }
//
//                // 判断小车是否已经走过这段路径
//                // 如果小车当前位置等于该路径段的终点，说明这段路径已经走完
//                if (currentCell.getCode().equals(toCell.getCode())) {
//                    foundCurrentPosition = true;
//                    // 这段路径已完成
//                    path.setState(3); // state=3：已完成
//                    rcsCarPathService.update(path);
//                    completedCount++;
//                    log.info("路径段已完成，pathId: {}, 从[{}]到[{}]",
//                            path.getId(), fromCell.getCode(), toCell.getCode());
//                } else if (!foundCurrentPosition) {
//                    // 还没找到当前位置，说明这段路径已经走过（小车已经走到更远的位置）
//                    path.setState(3); // state=3：已完成
//                    rcsCarPathService.update(path);
//                    completedCount++;
//                    log.info("路径段已完成（小车已走过），pathId: {}, 从[{}]到[{}]",
//                            path.getId(), fromCell.getCode(), toCell.getCode());
//                } else {
//                    // 找到当前位置之后的路径段，说明还没走过，重置为待执行
//                    path.setState(1); // state=1：已占用待执行
//                    rcsCarPathService.update(path);
//                    resetCount++;
//                    log.info("路径段重置为待执行，pathId: {}, 从[{}]到[{}]",
//                            path.getId(), fromCell.getCode(), toCell.getCode());
//                }
//            }
//
//            // 9、记录路径处理结果
//            String pathHandleMsg = String.format("异常处理完成：已完成路径%d段，重置待执行路径%d段", completedCount, resetCount);
//            jobInfoService.updateMemo(jobInfo, pathHandleMsg);
//            log.info("任务异常处理完成，jobId: {}, carCode: {}, 当前位置: {}, {}",
//                    jobInfo.getId(), carCode, currentCell.getCode(), pathHandleMsg);

        } catch (Exception e) {
            log.error("任务异常处理失败，jobId: {}", jobInfo.getId(), e);
            jobInfoService.updateMemo(jobInfo, "异常处理失败: " + e.getMessage());
        }
    }

    /**
     * 处理小车响应消息的通用方法
     * 根据MD文档协议规范，响应消息格式：
     * {
     * "msgType": "InstructionResponseMsg",
     * "robotId": 500001,
     * "response": {
     * "header": {
     * "responseId": 112233,
     * "code": 0,          // 0正常，非0异常
     * "msg": "success"
     * },
     * "body": {}
     * }
     * }
     *
     * @param responseJson  小车返回的JSON字符串
     * @param carInfo       小车信息
     * @param jobInfo       任务信息
     * @param operationName 操作名称（用于日志）
     * @return true-成功，false-失败
     */
    private boolean handleCarResponse(String responseJson, RcsCarInfo carInfo, JobInfo jobInfo, String operationName) {
        try {
            // 1、判断返回值是否为空
            if (responseJson == null || responseJson.trim().isEmpty()) {
                String errorMsg = operationName + "失败：小车无响应或响应超时";
                jobInfoService.updateMemo(jobInfo, errorMsg + "，小车: " + carInfo.getCode());
                log.error("{}，carCode: {}, jobId: {}", errorMsg, carInfo.getCode(), jobInfo.getId());
                return false;
            }

            // 2、解析返回值
            ZkDeviceMessage responseMessage = ZkDeviceMessage.ZkMessageUtil.fromJson(responseJson);

            // 3、检查是否为响应消息
            if (responseMessage == null || !responseMessage.isResponse()) {
                String errorMsg = operationName + "失败：小车返回数据格式错误";
                jobInfoService.updateMemo(jobInfo, errorMsg + "，小车: " + carInfo.getCode());
                log.error("{}，carCode: {}, response: {}", errorMsg, carInfo.getCode(), responseJson);
                return false;
            }

            // 4、获取响应头
            ZkDeviceMessage.ZkResponseHeader header = responseMessage.getResponse().getHeader();
            if (header == null) {
                String errorMsg = operationName + "失败：小车返回数据缺少响应头";
                jobInfoService.updateMemo(jobInfo, errorMsg + "，小车: " + carInfo.getCode());
                log.error("{}，carCode: {}, response: {}", errorMsg, carInfo.getCode(), responseJson);
                return false;
            }

            // 5、判断返回码：0表示成功，非0表示失败
            Integer code = header.getCode();
            String msg = header.getMsg();

            if (code == null || code != 0) {
                // 小车返回失败
                String errorMsg = operationName + "失败：小车返回失败，code: " + code + ", msg: " + msg;
                jobInfoService.updateMemo(jobInfo, errorMsg);
                log.error("{}，carCode: {}, jobId: {}, code: {}, msg: {}",
                        operationName + "失败", carInfo.getCode(), jobInfo.getId(), code, msg);
                return false;
            }

            // 6、小车返回成功
            String successMsg = operationName + "成功：小车确认接收指令";
            jobInfoService.updateMemo(jobInfo, successMsg + "，小车: " + carInfo.getCode() + ", msg: " + msg);
            log.info("{}，carCode: {}, jobId: {}, response: {}",
                    successMsg, carInfo.getCode(), jobInfo.getId(), responseJson);
            return true;

        } catch (Exception e) {
            String errorMsg = operationName + "失败：解析小车返回数据异常";
            jobInfoService.updateMemo(jobInfo, errorMsg + "，小车: " + carInfo.getCode() + ", 错误: " + e.getMessage());
            log.error("{}，carCode: {}, response: {}", errorMsg, carInfo.getCode(), responseJson, e);
            return false;
        }
    }

}
