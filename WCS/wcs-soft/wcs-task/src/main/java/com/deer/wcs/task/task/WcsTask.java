package com.deer.wcs.task.task;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.*;
import com.deer.wcs.base.utils.PLCUtils;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.exception.ExceptionHandle;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.BillRecordService;
import com.deer.wcs.task.handle.WcsHandle;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.PathHandle;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.*;
import com.deer.wcs.task.utils.FindShortPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * @description: 测试任务调度
 * @author:zfj
 * @date:2024/5/10 9:45
 */

@Component("WcsTask")
public class WcsTask {

    @Autowired
    private TaskInfoService taskInfoService;
    @Autowired
    private TaskDefineService taskDefineService;
    @Autowired
    private TaskHandleService taskHandleService;
    @Autowired
    private JobInfoService jobInfoService;
    @Autowired
    private JobHandleService jobHandleService;
    @Autowired
    private PositionConditionService positionConditionService;
    @Autowired
    private PositionHandleService positionHandleService;
    @Autowired
    private PathInfoService pathInfoService;
    @Autowired
    private PathHandleService pathHandleService;
    @Autowired
    private WareInfoService wareInfoService;
    @Autowired
    private DeviceValueService deviceValueService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private LineInfoService lineInfoService;
    @Autowired
    private BillRecordService billRecordService;
    @Autowired
    private WmsTaskInfoService wmsTaskInfoService;
    @Autowired
    private PLCUtils plcUtils;
    @Autowired
    private CallBoxInfoService callBoxInfoService;
    @Autowired
    private HandleService handleService;
    @Autowired
    private TransactionTask transactionTask;
    @Autowired
    private TaskPriorityService taskPriorityService;
    @Autowired
    private PositionInfoService positionInfoService;
    @Autowired
    private WcsHandle wcsHandle;

    private static final Logger log = LoggerFactory.getLogger(WcsTask.class);

    /**
     * 静态锁对象，用于确保 autoRun() 方法同一时间只有一个线程执行
     */
    private static final Object AUTO_RUN_LOCK = new Object();

    @Value("${isTest}")
    private Boolean isTest;

    private Object scReadPlc(String valueCode) {
        return deviceValueService.readValueByCode("sc", valueCode);
    }

    private Object scWrite(String valueCode, Object object) {
        return deviceValueService.writeValueByCode("sc", valueCode, object);
    }

    private void scHeart() {
        scWrite("Comm_Check_Confirm", (Short) scReadPlc("Comm_Check"));
    }


    /**
     * 应该定义一个静态锁 这个方法执行的时候  拿到锁资源才执行  在
     * 其他地方需要等待这个方法执行完才能执行
     * 获取当前可用的仓库
     * 根据选择的仓库运行相关的定时任务
     */
    //@Scheduled(cron = "0/1 * * * * ?")
    public void autoRun() {
        synchronized (AUTO_RUN_LOCK) {
            Condition condition = new Condition(WareInfo.class);
            condition.createCriteria().andEqualTo("isDelete", 0)
                    .andEqualTo("disableState", 0);
            List<WareInfo> wareInfoList = wareInfoService.findByCondition(condition);
            //找到所有正在使用的仓库
            for (WareInfo wareInfo : wareInfoList) {
                runWareInfo(wareInfo);
            }
        }
    }

    /**
     * 根据deviceValue定时获取设备值
     */
    public void readDeviceValue() {
        Condition condition = new Condition(DeviceValue.class);
        condition.createCriteria().andEqualTo("isDelete", 0);
        List<DeviceValue> deviceValueList = deviceValueService.findByCondition(condition);
        for (DeviceValue deviceValue : deviceValueList) {
            try {
                plcUtils.plcRead(deviceValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 定时根据wms任务生成wcs任务
     */
    public void saveWCSTask() {
        Condition condition = new Condition(WmsTaskInfo.class);
        condition.createCriteria().andEqualTo("state", 0);
        List<WmsTaskInfo> list = wmsTaskInfoService.findByCondition(condition);
        list.sort(Comparator.comparing(WmsTaskInfo::getPriority));
        for (WmsTaskInfo info : list) {
            TaskInfo taskInfo = new TaskInfo();
            BeanUtils.copyProperties(info, taskInfo);
            taskInfo.setWmsTaskNo(taskInfo.getTaskNo());
//            taskInfo.setTaskNo(UUID.randomUUID().toString());
            taskInfo.setId(null);
            taskInfoService.save(taskInfo);
            billRecordService.createTaskRecord(taskInfo.getId(), "根据WMS任务生成");
            info.setState(1);
            wmsTaskInfoService.update(info);
        }
    }


    @Autowired
    private DataSource dataSource;

    @Autowired
    private ExceptionHandle exceptionHandle;

    /**
     * 1.寻找taskInfo state=0 刚生成的
     * 2. taskInfo  根据task_define 生成jonInfo/jobHandle
     * 3. jobInfo  执行
     * <p>
     * wareInfo 正在使用的仓库
     */
    public void runWareInfo(WareInfo wareInfo) {
        try {
            //task_info    job_info
            createJobInfo(wareInfo);
            //执行
            runTask(wareInfo);
            runPathInfo(wareInfo);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("异常详细信息：", e); // 第二个参数传入异常对象，日志框架会自动打印堆栈
            exceptionHandle.handle(e);
        }

    }

    /**
     * 寻找所有 state=1 的 taskInfo
     * 寻找对应 taskInfo 的jobInfo
     * 寻找jobInfo 对应的jobHandle
     * 执行jobHandle中的方法
     */
    public void runTask(WareInfo wareInfo) {
        List<TaskInfo> taskInfoList = findTaskInfoState(wareInfo, 1);
        for (TaskInfo taskInfo : taskInfoList) {
            try {
                runJob(taskInfo);
            } catch (Exception ex) {
                ex.printStackTrace();
                exceptionHandle.handle(ex);
            }
        }
    }

    /**
     * 寻找所有 state=1 的 taskInfo
     * 寻找对应 taskInfo 的jobInfo
     * 寻找jobInfo 对应的jobHandle
     * 执行jobHandle中的方法
     */
    public void runPathInfo(WareInfo wareInfo) {
        List<PathInfo> pathInfoList = findPathInfos(wareInfo);
        for (PathInfo pathInfo : pathInfoList) {
            try {
                runPath(pathInfo);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    public void runJob(TaskInfo taskInfo) {
        List<JobInfo> jobInfoList = findJobInfoByTaskId(taskInfo);

        if (jobInfoList == null || jobInfoList.isEmpty()) {
//            if(!wcsHandle.reportWcsStatus2(taskInfo)){
//                return;
//            }

            taskInfo.setState(2);
            taskInfo.setFinishTime(DateUtil.getNowDateTimeString());
            taskInfoService.update(taskInfo);
            taskInfoService.recordHistory(taskInfo.getId());
            return;
        }

        for (JobInfo jobInfo : jobInfoList) {
            jobInfo = jobInfoService.findById(jobInfo.getId());
            //初始化
            if (jobInfo.getState().equals(0)) {
                Boolean result = transactionTask.canCmd(jobInfo);
                if (!result) {
                    break;
                }
            }
            //前置条件检测通过
            if (jobInfo.getState().equals(1)) {
                Boolean result = transactionTask.cmd(jobInfo);
                if (!result) {
                    break;
                }else{
                    // 向wms报告任务执行中
//                    wcsHandle.reportWcsStatus1(taskInfo);
                }
            }
            //已经执行 ，检测是否完成
            if (jobInfo.getState().equals(2)) {
                Boolean result = transactionTask.canSuccess(jobInfo);
                if (!result) {
                    break;
                }
            }
            //检测到完成，去执行完成回调
            if (jobInfo.getState().equals(3)) {
                Boolean result = transactionTask.success(jobInfo);
                if (!result) {
                    break;
                }
            }
        }

    }

    public void runPath(PathInfo pathInfo) {
        if (pathInfo.getState().equals(0)) {
            //判断 并 执行
            transactionTask.prePathCmd(pathInfo);
        }
        if (pathInfo.getState().equals(1)) {
            //判断成功 并 成功
            transactionTask.successPathPreCmd(pathInfo);
        }
    }


    /**
     * 寻找taskDefine对应的taskHandle
     *
     * @return
     */
    private List<TaskHandle> findTaskHandle(TaskDefine taskDefine) {
        Condition condition = new Condition(TaskHandle.class);
        condition.createCriteria().andEqualTo("taskDefineId", taskDefine.getId());
        return taskHandleService.findByCondition(condition);
    }

    /**
     * 寻找taskInfo对应类型的taskDefine
     *
     * @return
     */
    private List<TaskDefine> findTaskDefine(WareInfo wareInfo, TaskInfo taskInfo) {
        Condition condition = new Condition(TaskDefine.class);
        condition.createCriteria().andEqualTo("type", taskInfo.getType())
                .andEqualTo("wareCode", wareInfo.getCode());
        return taskDefineService.findByCondition(condition);
    }


    /**
     * 寻找此仓库对应的状态为0的任务
     *
     * @return
     */
    private List<TaskInfo> findTaskInfoState(WareInfo wareInfo, Integer state) {

        Condition condition = new Condition(TaskInfo.class);
        condition.createCriteria().andEqualTo("wareCode", wareInfo.getCode())
                .andEqualTo("state", state);
        List<TaskInfo> list = taskInfoService.findByCondition(condition);
        return list;
    }

    /**
     * 根据taskId寻找对应的jobInfos       jobInfo处于初始化和正在执行状态
     *
     * @return
     */
    private List<JobInfo> findJobInfoByTaskId(TaskInfo taskInfo) {

        List<Integer> states = new ArrayList<>();
        states.add(0);
        states.add(1);
        states.add(2);
        states.add(3);

        Condition condition = new Condition(JobInfo.class);
        condition.createCriteria().andEqualTo("taskId", taskInfo.getId())
                .andIn("state", states);
        return jobInfoService.findByCondition(condition);
    }

    /**
     * 寻找pathInfos       pathInfo处于初始化和正在执行状态
     *
     * @return
     */
    private List<PathInfo> findPathInfos(WareInfo wareInfo) {
        List<Integer> states = new ArrayList<>();
        states.add(0);
        states.add(1);

        Condition condition = new Condition(PathInfo.class);
        condition.createCriteria().andIn("state", states)
                .andEqualTo("wareCode", wareInfo.getCode());
        return pathInfoService.findByCondition(condition);
    }

    /**
     * 寻找pathInfo对应类型的positionHandle
     *
     * @return
     */
    private List<PositionCondition> findPositionConditionByWareCode(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo.getWareCode() == null) {
            log.error("任务" + taskInfo.getId() + "没有仓库编码");
            return new ArrayList<>();
        }
        List<PositionCondition> list = redisCache.getCacheList("position-condition:" + taskInfo.getWareCode());
        if (list == null || list.size() == 0) {
            list = splitGroupToNodes(taskInfo);
            redisCache.setCacheList("position-condition:" + taskInfo.getWareCode(), list);
        }
        return list;
    }

    /**
     * 将逻辑图中关于组的方法拆分为节点到节点的方法
     */
    List<PositionCondition> splitGroupToNodes(TaskInfo taskInfo) {
        Condition condition = new Condition(PositionCondition.class);
        condition.createCriteria().andEqualTo("wareCode", taskInfo.getWareCode());
        //from组--》to组  from节点--》to组 from组--》节点  节点--》节点
        List<PositionCondition> list = positionConditionService.findByCondition(condition);
        List<PositionCondition> addList = new ArrayList<>();
        for (PositionCondition path : list) {
            String fromCode = path.getFromCode();
            String toCode = path.getToCode();
            Condition fromCon = new Condition(PositionInfo.class);
            fromCon.createCriteria().andEqualTo("code", fromCode).andEqualTo("wareCode", path.getWareCode());
            PositionInfo fromPosition = positionInfoService.findByCondition(fromCon).get(0);
            if (fromPosition == null) {
                LineInfo lineInfo = lineInfoService.findBy("code", fromCode);
                fromPosition = new PositionInfo();
                fromPosition.setCode(lineInfo.getCode());
                fromPosition.setName(lineInfo.getName());
            }


            Condition toCon = new Condition(PositionInfo.class);
            toCon.createCriteria().andEqualTo("code", fromCode).andEqualTo("wareCode", path.getWareCode());
            PositionInfo toPosition = positionInfoService.findByCondition(toCon).get(0);
            if (toPosition == null) {
                LineInfo lineInfo = lineInfoService.findBy("code", toCode);
                toPosition = new PositionInfo();
                toPosition.setCode(lineInfo.getCode());
                toPosition.setName(lineInfo.getName());
            }

            if (
                    (fromPosition.getIsGroup() == null || fromPosition.getIsGroup() == 0)
                            && (toPosition.getIsGroup() == null || toPosition.getIsGroup() == 0)
            ) {
                continue;
            }

            List<PositionInfo> froms = new ArrayList<>();
            Condition conditionFrom = new Condition(PositionInfo.class);
            conditionFrom.createCriteria().andEqualTo("parentCode", fromCode)
                    .andEqualTo("wareCode", taskInfo.getWareCode());
            if (fromPosition.getIsGroup() != null && fromPosition.getIsGroup() == 1) {
                froms = positionInfoService.findByCondition(conditionFrom);
            } else {
                froms.add(fromPosition);
            }

            List<PositionInfo> tos = new ArrayList<>();
            Condition conditionTo = new Condition(PositionInfo.class);
            conditionTo.createCriteria().andEqualTo("parentCode", toCode)
                    .andEqualTo("wareCode", taskInfo.getWareCode());
            if (toPosition.getIsGroup() != null && toPosition.getIsGroup() == 1) {
                tos = positionInfoService.findByCondition(conditionTo);
            } else {
                tos.add(toPosition);
            }

            for (PositionInfo from : froms) {
                for (PositionInfo to : tos) {
                    PositionCondition pathDefine = new PositionCondition();
                    BeanUtils.copyProperties(path, pathDefine);
                    pathDefine.setFromCode(from.getCode());
                    pathDefine.setToCode(to.getCode());
                    pathDefine.setTemplateCode(path.getTemplateCode());
                    pathDefine.setTaskTime(path.getTaskTime());
                    pathDefine.setBlockingTime(path.getBlockingTime());
                    addList.add(pathDefine);
                }
            }
        }
        for (PositionCondition positionCondition : addList) {
            list.add(positionCondition);
        }
        return list;
    }

    /**
     * 寻找pathInfo对应类型的positionHandle
     *
     * @return
     */
    private List<PositionHandle> findPositionHandle(PositionCondition positionCondition) {
        Condition condition = new Condition(PositionHandle.class);
        condition.createCriteria().andEqualTo("stepCode", positionCondition.getTemplateCode());
        return positionHandleService.findByCondition(condition);
    }


    /**
     * positionCondition-->pathInfo
     */
    public PathInfo copyPropertyToPathInfo(JobInfo jobInfo, PositionCondition positionCondition) {
        PathInfo pathInfo = new PathInfo();
        pathInfo.setName(positionCondition.getStepName());
        pathInfo.setTaskId(jobInfo.getTaskId());
//        pathInfo.setTaskNo(jobInfo.getTaskNo());
        pathInfo.setJobId(jobInfo.getId());
        pathInfo.setPathIndex(1);
        pathInfo.setType(jobInfo.getType());
        pathInfo.setPalletCode(jobInfo.getPalletCode());
        pathInfo.setFromCellCode(positionCondition.getFromCode());
        pathInfo.setToCellCode(positionCondition.getToCode());
        pathInfo.setCreateTime(DateUtil.getNowDateTimeString());
        pathInfo.setState(0);
        pathInfo.setMemo("等待任务执行中");
        pathInfo.setWareCode(positionCondition.getWareCode());
        pathInfo.setWareName(positionCondition.getWareName());
        pathInfoService.save(pathInfo);

        return pathInfo;
    }


    /**
     * task_handle-->job_handle
     */
    public void copyPropertyToPathHandle(PathInfo pathInfo, PositionHandle positionHandle) {
        PathHandle pathHandle = new PathHandle();
        pathHandle.setTaskId(pathInfo.getTaskId());
//        pathHandle.setTaskNo(pathInfo.getTaskNo());
        pathHandle.setJobId(pathInfo.getJobId());
        pathHandle.setPathId(pathInfo.getId());
        pathHandle.setHandleId(positionHandle.getHandleId());
        pathHandle.setType(positionHandle.getType());
        pathHandle.setClassName(positionHandle.getClassName());
        pathHandle.setMethodName(positionHandle.getMethodName());
        pathHandle.setCode(positionHandle.getCode());
        pathHandle.setName(positionHandle.getName());
        pathHandle.setCmdIndex(positionHandle.getOrderNo());
        pathHandle.setCreateUserId(positionHandle.getCreateUserId());
        pathHandle.setCreateUserName(positionHandle.getCreateUserName());
        pathHandleService.save(pathHandle);
    }

    @Autowired
    private TaskTypeService taskTypeService;

    /**
     * 根据taskDefine生成jobInfo,同时生成jobHandle
     * 使用TransactionTask的事务方法确保失败时回滚
     */
    public void createJobInfo(WareInfo wareInfo) {
        List<TaskInfo> taskInfoList = findTaskInfoState(wareInfo, 0);

        for (TaskInfo taskInfo : taskInfoList) {
            try {
                // 使用TransactionTask的事务方法处理，确保失败时回滚所有已保存的jobInfo和jobHandle
                transactionTask.createJobInfoForTask(taskInfo, wareInfo);
//                wcsHandle.reportWcsStatus0(taskInfo);
            } catch (Exception ex) {
                ex.printStackTrace();
                // 这里只记录错误，jobInfo和jobHandle已经在事务方法中回滚
                taskInfoService.updateMemo(taskInfo, "步骤拆分失败：" + ex.getMessage());
                exceptionHandle.handle(ex);
            }
        }
    }

    /**
     * 1. 根据jobInfo的起点和终点位置，寻找最短路径positionCondition
     * 2. 根据positionCondition生成pathInfo
     * 3. 根据positionHandle生成pathHandle
     * <p>
     * 4.如果找不到路径，再找起点分组，终点分组
     * 分组也是一个站台  子站台parent_id  ==分组的ID
     */
    public boolean createPathInfo(JobInfo jobInfo) {
        List<PositionCondition> list = findPositionConditionByWareCode(jobInfo);
        //生成path
        List<PositionCondition> positionConditionList = FindShortPath.findShorPath(jobInfo, list);

        for (PositionCondition positionCondition : positionConditionList) {
            PathInfo pathInfo = copyPropertyToPathInfo(jobInfo, positionCondition);

            List<PositionHandle> positionHandleList = findPositionHandle(positionCondition);
            for (PositionHandle positionHandle : positionHandleList) {
                copyPropertyToPathHandle(pathInfo, positionHandle);
            }

        }
        return true;
    }

}
