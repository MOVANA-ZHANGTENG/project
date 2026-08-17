package com.deer.wcs.task.web;

import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.model.TaskTypePriority;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.base.service.TaskPriorityService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.task.handle.hik.AgvResult;
import com.deer.wcs.task.handle.hik.Hik2000V33JobHandle;
import com.deer.wcs.task.handle.lg7.Lg7HikJobHandle;
import com.deer.wcs.task.model.*;
import com.deer.wcs.task.service.JobHandleService;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import com.deer.wcs.task.task.TransactionTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 任务Controller
 * 
 * @author deer
 * @date 2024-04-30
 */
@Api("任务")
@RestController
@Transactional
@RequestMapping("/wcs-task/TaskInfo")
public class TaskInfoController extends BaseController
{

    private static final Logger log = LoggerFactory.getLogger(TaskInfoController.class);
    @Autowired
    private TaskInfoService taskInfoService;
    @Autowired
    private TaskPriorityService taskPriorityService;

    /**
     * 查询任务列表
     */
    @ApiOperation("查询任务列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:TaskInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(TaskInfoCriteria Criteria) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        startPage();
        List<TaskInfoDto> list = taskInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 查询所有任务列表
     */
    @ApiOperation("查询所有任务列表")
    @GetMapping("/getAll")
    public Result getAll(){
        return Result.success(taskInfoService.findAll());
    }

    /**
     * 导出任务列表
     */
    @ApiOperation("导出任务列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:TaskInfo:export')")
    @Log(title = "任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TaskInfoCriteria criteria)
    {
        List<TaskInfoDto> list = taskInfoService.findList(criteria);
        ExcelUtil<TaskInfoDto> util = new ExcelUtil<TaskInfoDto>(TaskInfoDto.class);
        util.exportExcel(response, list, "任务数据");
    }

    /**
     * 获取任务详细信息
     */
    @ApiOperation("获取任务详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-task:TaskInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(taskInfoService.findById(id));
    }

    /**
     * 新增任务
     */
    @ApiOperation("新增任务")
    @PreAuthorize("@ss.hasPermi('wcs-task:TaskInfo:add')")
    @Log(title = "任务", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody TaskInfo taskInfo)
    {
        taskInfo.setCreateTime(DateUtil.getNowDateTimeString());

        TaskTypePriority taskTypePriority = taskPriorityService.findBy("code",taskInfo.getType().toString());
        if(taskTypePriority!=null){
            taskInfo.setPriority(taskTypePriority.getPriority());
        }else{
            taskInfo.setPriority(10);
        }
        if(taskInfo.getFromCellCode()!=null && taskInfo.getToCellCode()!=null && taskInfo.getFromCellCode().equals(taskInfo.getToCellCode())){
            return error("任务起始库位和目标库位不得一致");
        }
        taskInfoService.save(taskInfo);
        return toAjax(true);
    }

    /**
     * 修改任务
     */
    @ApiOperation("修改任务")
    @PreAuthorize("@ss.hasPermi('wcs-task:TaskInfo:edit')")
    @Log(title = "任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody TaskInfo taskInfo)
    {



        return toAjax(taskInfoService.update(taskInfo));
    }

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private Hik2000V33JobHandle lg7HikJobHandle;

    @Autowired
    private JobHandleService jobHandleService;

    @Autowired
    private TransactionTask transactionTask;

    /**
     * 删除任务
     */
    @ApiOperation("删除任务")
    @PreAuthorize("@ss.hasPermi('wcs-task:TaskInfo:remove')")
    @Log(title = "任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        try {
            for (Long id:ids
            ) {
                TaskInfo taskInfo = taskInfoService.findById(id);

                List<JobInfo> jobInfos = jobInfoService.findByTaskId(id);
                for (JobInfo jobInfo:jobInfos){
                    List<JobHandle> jobHandles = jobHandleService.findByJobId(jobInfo.getId());
                    for (JobHandle jobHandle:jobHandles){
                        if(jobHandle.getType()==5 && jobHandle.getState()==0){
                            Boolean result = transactionTask.runHandle(jobInfo,jobHandle);
                            if(!result){
                                return error("任务执行失败："+jobHandle.getClassName()+"-"+jobHandle.getMethodName());
                            }
                        }
                    }
                }
                taskInfoService.recordHistory(id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return success();
        //return toAjax(taskInfoService.deleteTaskInfoByIds(ids));
    }

    /**
     * 获取7天任务类型统计
     */
    @ApiOperation("获取7天任务类型统计")
    @GetMapping("/statistics/sevenDays")
    public Result getSevenDaysStatistics(String wareCode)
    {
        if (wareCode == null || wareCode.trim().isEmpty()) {
            return error("仓库编码不能为空");
        }
        TaskStatisticsDto statistics = taskInfoService.getSevenDaysStatistics(wareCode);
        return success(statistics);
    }

    /**
     * 获取任务基础统计（总数、今日新增等）
     */
    @ApiOperation("获取任务基础统计")
    @GetMapping("/statistics/basic")
    public Result getBasicStatistics(String wareCode)
    {
        if (wareCode == null || wareCode.trim().isEmpty()) {
            return error("仓库编码不能为空");
        }
        TaskBasicStatisticsDto statistics = taskInfoService.getBasicStatistics(wareCode);
        return success(statistics);
    }





}
