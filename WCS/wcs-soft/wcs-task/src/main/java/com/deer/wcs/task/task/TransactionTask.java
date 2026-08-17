package com.deer.wcs.task.task;

import com.deer.wcs.base.model.Handle;
import com.deer.wcs.base.model.PathInfo;
import com.deer.wcs.base.model.TaskDefine;
import com.deer.wcs.base.model.TaskHandle;
import com.deer.wcs.base.model.TaskType;
import com.deer.wcs.base.model.WareInfo;
import com.deer.wcs.base.service.HandleService;
import com.deer.wcs.base.service.PathInfoService;
import com.deer.wcs.base.service.TaskDefineService;
import com.deer.wcs.base.service.TaskHandleService;
import com.deer.wcs.base.service.TaskTypeService;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.system.service.BillRecordService;
import com.deer.wcs.task.model.JobHandle;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.PathHandle;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobHandleService;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.PathHandleService;
import com.deer.wcs.task.service.TaskInfoService;
import com.deer.wcs.task.utils.HandelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 需要回滚的方法
 * @author:zfj
 * @date:2024/8/12 10:34
 */
@Component("TransactionTask")
public class TransactionTask {
    private static final Logger log = LoggerFactory.getLogger(TransactionTask.class);

    @Autowired
    private JobInfoService jobInfoService;
    @Autowired
    private PathInfoService pathInfoService;
    @Autowired
    private BillRecordService billRecordService;
    @Autowired
    private JobHandleService jobHandleService;
    @Autowired
    private PathHandleService pathHandleService;
    @Autowired
    private HandleService handleService;
    @Autowired
    private TaskInfoService taskInfoService;
    @Autowired
    private TaskDefineService taskDefineService;
    @Autowired
    private TaskHandleService taskHandleService;
    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private WcsTask wcsTask;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void preCmd(JobInfo jobInfo) {
        if (canCmd(jobInfo)) {
            if(cmd(jobInfo)){
                jobInfo.setState(1);
                jobInfo.setCmdTime(DateUtil.getNowDateTimeString());
                jobInfoService.update(jobInfo);
                billRecordService.createTaskRecord(jobInfo.getTaskId(), "【" + jobInfo.getName() + "】 满足执行条件");
            }
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void successPreCmd(JobInfo jobInfo) {
        if (canSuccess(jobInfo)) {
            success(jobInfo);
            jobInfo.setState(2);
            jobInfo.setFinishTime(DateUtil.getNowDateTimeString());
            jobInfoService.update(jobInfo);
            billRecordService.createTaskRecord(jobInfo.getTaskId(), "第" + jobInfo.getJobIndex() + "步骤执行结束");
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void prePathCmd(PathInfo pathInfo) {
        if (canPathCmd(pathInfo)) {
            cmdPath(pathInfo);
            pathInfo.setState(1);
            pathInfo.setCmdTime(DateUtil.getNowDateTimeString());
            pathInfoService.update(pathInfo);
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void successPathPreCmd(PathInfo pathInfo) {
        if (canPathSuccess(pathInfo)) {
            successPath(pathInfo);
            pathInfo.setState(2);
            pathInfo.setFinishTime(DateUtil.getNowDateTimeString());
            pathInfoService.update(pathInfo);
        }
    }


    public Boolean canCmd(JobInfo jobInfo) {
        List<JobHandle> list = findJobHandleByJobIdAndType(jobInfo, 1);
        for (JobHandle handle : list) {
            Boolean result = runHandle(jobInfo, handle);
            if (result == false) {
                return false;
            }
            log.info(handle.getClassName() + "." + handle.getMethodName() + "执行成功");
        }
        jobInfo.setState(1);
        jobInfo.setMemo("任务【" + jobInfo.getName() + "】 满足执行条件");
        jobInfoService.update(jobInfo);
        billRecordService.createTaskRecord(jobInfo.getTaskId(), "【" + jobInfo.getName() + "】 满足执行条件");
        return true;
    }
    public Boolean cmd(JobInfo jobInfo) {
        List<JobHandle> list = findJobHandleByJobIdAndType(jobInfo, 2);
        for (JobHandle handle : list) {
            Boolean result = runHandle(jobInfo, handle);
            if (result == false) {
                return false;
            }
        }
        jobInfo.setState(2);
        jobInfo.setCmdTime(DateUtil.getNowDateTimeString());
        jobInfo.setMemo("任务【" + jobInfo.getName() + "】 执行");
        jobInfoService.update(jobInfo);
        billRecordService.createTaskRecord(jobInfo.getTaskId(), "【" + jobInfo.getName() + "】 执行");
        return true;
    }
    public Boolean canSuccess(JobInfo jobInfo) {
        List<JobHandle> list = findJobHandleByJobIdAndType(jobInfo, 3);
        for (JobHandle handle : list) {
            Boolean result = runHandle(jobInfo, handle);
            if (result == false) {
                return false;
            }
            log.info(handle.getClassName() + "." + handle.getMethodName() + "执行成功");
        }
        jobInfo.setState(3);

        jobInfo.setMemo("任务【" + jobInfo.getName() + "】 满足成功条件");
        jobInfoService.update(jobInfo);
        billRecordService.createTaskRecord(jobInfo.getTaskId(), "【" + jobInfo.getName() + "】 满足成功条件");
        return true;
    }
    public Boolean success(JobInfo jobInfo) {
        List<JobHandle> list = findJobHandleByJobIdAndType(jobInfo, 4);
        for (JobHandle handle : list) {
            Boolean result = runHandle(jobInfo, handle);
            if (result == false) {
                return false;
            }
            log.info(handle.getClassName() + "." + handle.getMethodName() + "执行成功");
        }
        jobInfo.setState(4);
        jobInfo.setFinishTime(DateUtil.getNowDateTimeString());
        jobInfo.setMemo("任务【" + jobInfo.getName() + "】 完成成功回调");
        jobInfoService.update(jobInfo);
        billRecordService.createTaskRecord(jobInfo.getTaskId(), "【" + jobInfo.getName() + "】 完成成功回调");
        return true;
    }



    public Boolean canPathCmd(PathInfo pathInfo) {
        List<PathHandle> list = findPathHandleByPathIdAndType(pathInfo, 1);
        for (PathHandle handle : list) {
            Boolean result = runPathHandle(pathInfo, handle);
            if (!result) {
//                log.info(handle.getClassName()+"."+handle.getMethodName()+"执行失败");
                return false;
            }
            log.info(handle.getClassName() + "." + handle.getMethodName() + "执行成功");
        }
        return true;
    }
    public void cmdPath(PathInfo pathInfo) {
        List<PathHandle> list = findPathHandleByPathIdAndType(pathInfo, 2);
        for (PathHandle handle : list) {
            runPathHandle(pathInfo, handle);
        }

    }
    public Boolean canPathSuccess(PathInfo pathInfo) {
        List<PathHandle> list = findPathHandleByPathIdAndType(pathInfo, 3);
        for (PathHandle handle : list) {
            Boolean result = runPathHandle(pathInfo, handle);
            if (result == false) {
                return false;
            }
            log.info(handle.getClassName() + "." + handle.getMethodName() + "执行成功");
        }
        return true;
    }
    public void successPath(PathInfo pathInfo) {
        List<PathHandle> list = findPathHandleByPathIdAndType(pathInfo, 4);
        for (PathHandle handle : list) {
            runPathHandle(pathInfo, handle);
        }

    }


    /**
     * 根据jobId寻找对应的jobHandles
     *
     * @return
     */
    private List<JobHandle> findJobHandleByJobIdAndType(JobInfo jobInfo, Integer type) {
        Condition condition = new Condition(JobHandle.class);
        condition.createCriteria().andEqualTo("jobId", jobInfo.getId())
                .andEqualTo("type", type)
                .andEqualTo("state", 0);
        List<JobHandle> list = jobHandleService.findByCondition(condition);
        list = list.stream().sorted(Comparator.comparing(JobHandle::getCmdIndex)).collect(Collectors.toList());
        return list;
    }
    /**
     * 根据pathId寻找对应的pathHandles
     *
     * @return
     */
    private List<PathHandle> findPathHandleByPathIdAndType(PathInfo pathInfo, Integer type) {
        Condition condition = new Condition(PathHandle.class);
        condition.createCriteria().andEqualTo("pathId", pathInfo.getId())
                .andEqualTo("type", type)
                .andEqualTo("state", 0);
        List<PathHandle> list = pathHandleService.findByCondition(condition);
        list = list.stream().sorted(Comparator.comparing(PathHandle::getCmdIndex)).collect(Collectors.toList());
        return list;
    }

    public boolean runHandle(JobInfo jobInfo, JobHandle jobHandle) {
        try {
            Handle handle = handleService.findById(jobHandle.getHandleId());
            if(handle==null){
                throw new RuntimeException("找不到执行器"+jobHandle.getClassName()+"-"+jobHandle.getMethodName());
            }
            boolean result = HandelUtil.invokeJobHandleMethod(handle.getClassName(), handle.getMethodName(), jobInfo);
            if (result) {
                jobHandle.setState(1);
                jobHandleService.update(jobHandle);
            }
            return result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            jobInfoService.updateMemo(jobInfo,"找不到方法" + jobHandle.getClassName() + "." + jobHandle.getMethodName());
            log.warn("找不到方法" + jobHandle.getClassName() + "." + jobHandle.getMethodName());
            return false;
        } catch (IllegalAccessException e) {
            log.warn("当前代码没有足够的权限访问指定的类、字段、方法或构造器");
            e.printStackTrace();
            jobInfoService.updateMemo(jobInfo,"当前代码没有足够的权限访问指定的类、字段、方法或构造器");
            log.warn("当前代码没有足够的权限访问指定的类、字段、方法或构造器");
            return false;
        }catch (ServiceException e) {
            jobInfoService.updateMemo(jobInfo,jobHandle.getName()+" 执行方法" + jobHandle.getClassName() + "." + jobHandle.getMethodName() + "异常："+e.getMessage());
            log.warn("执行方法" + jobHandle.getClassName() + "." + jobHandle.getMethodName() + "异常");
            e.printStackTrace();
            return false;
            //throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            String msg  ="";
            // 获取InvocationTargetException的message
            String message = e.getMessage();
            if(message!=null){
                msg+=message;
            }

            // 获取被包装的异常
            Throwable cause = e.getCause();
            if (cause != null) {
                // 获取被包装异常的message
                String causeMessage = cause.getMessage();
                if(causeMessage!=null){
                    msg+=causeMessage;
                }
            }

                StringWriter sw = new StringWriter();
                // 创建 PrintWriter 对象，并将其输出流设置为 StringWriter
                PrintWriter pw = new PrintWriter(sw);
                // 将异常信息写入 PrintWriter
                e.printStackTrace(pw);
                // 从 StringWriter 获取字符串
                msg = sw.toString();

            jobInfoService.updateMemo(jobInfo,jobHandle.getName()+" 执行方法" + jobHandle.getClassName() + "." + jobHandle.getMethodName() + "异常："+msg);
            log.warn("执行方法" + jobHandle.getClassName() + "." + jobHandle.getMethodName() + "异常");
            e.printStackTrace();
            return false;
            //throw new RuntimeException(e);
        }
        catch (Exception e) {
            jobInfoService.updateMemo(jobInfo,jobHandle.getName()+" 执行方法" + jobHandle.getClassName() + "." + jobHandle.getMethodName() + "异常："+e.getMessage());
            log.warn("执行方法" + jobHandle.getClassName() + "." + jobHandle.getMethodName() + "异常");
            e.printStackTrace();
            return false;
            //throw new RuntimeException(e);
        }
    }
    private boolean runPathHandle(PathInfo pathInfo, PathHandle pathHandle) {
//        if (pathHandle.getState() == 1) {
//            return true;
//        }
        try {
            Handle handle = handleService.findById(pathHandle.getHandleId());
            if(handle==null){
                throw new RuntimeException("找不到执行器"+pathHandle.getClassName()+"-"+pathHandle.getMethodName());
            }
            boolean result = HandelUtil.invokePathHandleMethod(pathHandle.getClassName(), pathHandle.getMethodName(), pathInfo);
            if (result) {
                pathHandle.setState(1);
                pathHandleService.update(pathHandle);
            }
            return result;
        } catch (NoSuchMethodException e) {
            log.warn("找不到方法" + pathHandle.getClassName() + "." + pathHandle.getMethodName());
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            log.warn("当前代码没有足够的权限访问指定的类、字段、方法或构造器");
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            log.warn("执行方法" + pathHandle.getClassName() + "." + pathHandle.getMethodName() + "异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 为单个taskInfo创建jobInfo和jobHandle，带事务回滚
     * 如果任何步骤失败，整个事务将回滚
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void createJobInfoForTask(TaskInfo taskInfo, WareInfo wareInfo) throws Exception {
        List<TaskDefine> taskDefineList = findTaskDefine(wareInfo, taskInfo);
        
        if (taskDefineList.size() == 0) {
            //taskInfo切换状态为3 任务定义异常
            taskInfo.setState(3);
            taskInfo.setMemo("找不到定义的任务，请检查任务定义");
            taskInfoService.update(taskInfo);
            log.warn("任务号" + taskInfo.getId() + "找不到定义的任务，请检查任务定义");
            throw new ServiceException("找不到定义的任务，请检查任务定义");
        }
        
        // 创建所有jobInfo和jobHandle
        for (TaskDefine taskDefine : taskDefineList) {
            JobInfo jobInfo = copyPropertyToJobInfo(taskInfo, taskDefine);
            List<TaskHandle> taskHandleList = findTaskHandle(taskDefine);
            for (TaskHandle taskHandle : taskHandleList) {
                copyPropertyToJobHandle(jobInfo, taskHandle);
            }
        }

        taskInfo.setState(1);
        taskInfoService.update(taskInfo);
        taskInfoService.updateMemo(taskInfo, "步骤拆分完成");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void createJobInfoForTaskNew(TaskInfo taskInfo, WareInfo wareInfo) throws Exception {
        List<TaskDefine> taskDefineList = findTaskDefine(wareInfo, taskInfo);

        if (taskDefineList.size() == 0) {
            //taskInfo切换状态为3 任务定义异常
            taskInfo.setState(3);
            taskInfo.setMemo("找不到定义的任务，请检查任务定义");
            taskInfoService.update(taskInfo);
            log.warn("任务号" + taskInfo.getId() + "找不到定义的任务，请检查任务定义");
            throw new ServiceException("找不到定义的任务，请检查任务定义");
        }

        // 第一步：创建所有 jobInfo 和 jobHandle，并建立 TaskDefine.id -> JobInfo 的映射
        java.util.Map<Long, JobInfo> taskDefineIdToJobInfoMap = new java.util.HashMap<>();
        
        for (TaskDefine taskDefine : taskDefineList) {
            // 先创建 jobInfo（不设置 lastJobId）
            JobInfo jobInfo = copyPropertyToJobInfoNew(taskInfo, taskDefine);
            
            // 记录 TaskDefine.id -> JobInfo 的映射
            taskDefineIdToJobInfoMap.put(taskDefine.getId(), jobInfo);
            
            // 创建 jobHandle
            List<TaskHandle> taskHandleList = findTaskHandle(taskDefine);
            for (TaskHandle taskHandle : taskHandleList) {
                copyPropertyToJobHandle(jobInfo, taskHandle);
            }
        }

        // 第二步：根据 TaskDefine 的 lastId 设置 JobInfo 的 lastJobId
        for (TaskDefine taskDefine : taskDefineList) {
            if (taskDefine.getLastId() != null) {
                JobInfo currentJobInfo = taskDefineIdToJobInfoMap.get(taskDefine.getId());
                JobInfo lastJobInfo = taskDefineIdToJobInfoMap.get(taskDefine.getLastId());
                
                if (lastJobInfo != null) {
                    currentJobInfo.setLastJobId(lastJobInfo.getId());
                    jobInfoService.update(currentJobInfo);
                } else {
                    log.warn("任务号" + taskInfo.getId() + "的TaskDefine[" + taskDefine.getId() + "]引用了不存在的lastId: " + taskDefine.getLastId());
                }
            }
        }


        taskInfo.setState(1);
        taskInfoService.update(taskInfo);
        taskInfoService.updateMemo(taskInfo, "步骤拆分完成");
    }

    /**
     * taskDefine-->jobInfo
     */
    public JobInfo copyPropertyToJobInfo(TaskInfo taskInfo, TaskDefine taskDefine) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setTaskId(taskInfo.getId());
//        jobInfo.setTaskNo(taskInfo.getTaskNo());
        jobInfo.setName(taskDefine.getName());
        jobInfo.setIsJudgeStep(taskDefine.getIsJudgeStep());
        jobInfo.setJudgeBranchType(taskDefine.getJudgeBranchType());
        jobInfo.setJobIndex(taskDefine.getJobIndex());
        jobInfo.setType(taskInfo.getType());
        jobInfo.setPalletCode(taskInfo.getPalletCode());
        jobInfo.setCreateTime(DateUtil.getNowDateTimeString());
        jobInfo.setState(0);
//        if (taskDefine.getFromCellCode() == null || taskDefine.getFromCellCode().equals("")) {
//            jobInfo.setFromCellCode(taskInfo.getFromCellCode());
//        } else {
//            jobInfo.setFromCellCode(taskDefine.getFromCellCode());
//        }
//        if (taskDefine.getToCellCode() == null || taskDefine.getToCellCode().equals("")) {
//            jobInfo.setToCellCode(taskInfo.getToCellCode());
//        } else {
//            jobInfo.setToCellCode(taskDefine.getToCellCode());
//        }
        jobInfoService.save(jobInfo);
        return jobInfo;
    }

    @Autowired
    private AutoService autoService;

    /**
     * taskDefine-->jobInfo
     */
    public JobInfo copyPropertyToJobInfoNew(TaskInfo taskInfo, TaskDefine taskDefine) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setTaskId(taskInfo.getId());
        jobInfo.setId(autoService.getJobInfoId());
//        jobInfo.setTaskNo(taskInfo.getTaskNo());
        jobInfo.setName(taskDefine.getName());
        jobInfo.setIsJudgeStep(taskDefine.getIsJudgeStep());
        jobInfo.setJudgeBranchType(taskDefine.getJudgeBranchType());
        jobInfo.setJobIndex(taskDefine.getJobIndex());
        jobInfo.setType(taskInfo.getType());
        jobInfo.setPalletCode(taskInfo.getPalletCode());
        jobInfo.setCreateTime(DateUtil.getNowDateTimeString());
        jobInfo.setState(0);
//        if (taskDefine.getFromCellCode() == null || taskDefine.getFromCellCode().equals("")) {
//            jobInfo.setFromCellCode(taskInfo.getFromCellCode());
//        } else {
//            jobInfo.setFromCellCode(taskDefine.getFromCellCode());
//        }
//        if (taskDefine.getToCellCode() == null || taskDefine.getToCellCode().equals("")) {
//            jobInfo.setToCellCode(taskInfo.getToCellCode());
//        } else {
//            jobInfo.setToCellCode(taskDefine.getToCellCode());
//        }
        jobInfoService.save(jobInfo);
        return jobInfo;
    }


    /**
     * task_handle-->job_handle
     */
    public void copyPropertyToJobHandle(JobInfo jobInfo, TaskHandle taskHandle) {
        JobHandle jobHandle = new JobHandle();
        jobHandle.setTaskId(jobInfo.getTaskId());
//        jobHandle.setTaskNo(jobInfo.getTaskNo());
        jobHandle.setJobId(jobInfo.getId());
        jobHandle.setHandleId(taskHandle.getHandleId());
        jobHandle.setType(taskHandle.getType());
        jobHandle.setClassName(taskHandle.getClassName());
        jobHandle.setMethodName(taskHandle.getMethodName());
        jobHandle.setCode(taskHandle.getCode());
        jobHandle.setName(taskHandle.getName());
        jobHandle.setCmdIndex(taskHandle.getCmdIndex());
        jobHandle.setCreateUserId(jobHandle.getCreateUserId());
        jobHandle.setCreateUserName(jobHandle.getCreateUserName());
        jobHandleService.save(jobHandle);
    }

    /**
     * 寻找taskInfo对应类型的taskDefine
     */
    private List<TaskDefine> findTaskDefine(WareInfo wareInfo, TaskInfo taskInfo) {
        Condition condition = new Condition(TaskDefine.class);
        condition.createCriteria().andEqualTo("type", taskInfo.getType())
                .andEqualTo("wareCode", wareInfo.getCode());
        return taskDefineService.findByCondition(condition);
    }

    /**
     * 寻找taskDefine对应的taskHandle
     */
    private List<TaskHandle> findTaskHandle(TaskDefine taskDefine) {
        Condition condition = new Condition(TaskHandle.class);
        condition.createCriteria().andEqualTo("taskDefineId", taskDefine.getId());
        return taskHandleService.findByCondition(condition);
    }

}
