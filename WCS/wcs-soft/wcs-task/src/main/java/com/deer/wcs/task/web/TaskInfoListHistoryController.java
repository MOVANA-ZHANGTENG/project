package com.deer.wcs.task.web;

import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.task.model.TaskInfoListHistory;
import com.deer.wcs.task.model.TaskInfoListHistoryCriteria;
import com.deer.wcs.task.model.TaskInfoListHistoryDto;
import com.deer.wcs.task.service.TaskInfoListHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用于记录任务执行的每一步历史Controller
 * 
 * @author deer
 * @date 2024-06-06
 */
@Api("用于记录任务执行的每一步历史")
@RestController
@RequestMapping("/wcs-task/historyTaskList")
public class TaskInfoListHistoryController extends BaseController
{
    @Autowired
    private TaskInfoListHistoryService taskInfoListHistoryService;

    /**
     * 查询用于记录任务执行的每一步历史列表
     */
    @ApiOperation("查询用于记录任务执行的每一步历史列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:historyTaskList:list')")
    @GetMapping("/list")
    public TableDataInfo list(TaskInfoListHistoryCriteria Criteria)
    {
        startPage();
        List<TaskInfoListHistoryDto> list = taskInfoListHistoryService.findList(Criteria);
        return getDataTable(list);
    }

    @ApiOperation("查询用于记录任务执行的每一步历史列表")
    @GetMapping("/findAll")
    public Result findAll(TaskInfoListHistoryCriteria Criteria)
    {
        List<TaskInfoListHistoryDto> list = taskInfoListHistoryService.findList(Criteria);
        return Result.success(list);
    }
    @GetMapping("/findByTaskId")
    public Result findByTaskId(Long taskId)
    {
        Condition condition =new Condition(TaskInfoListHistory.class);
        condition.createCriteria().andEqualTo("taskId",taskId);
        condition.orderBy("id");
        List<TaskInfoListHistory> list = taskInfoListHistoryService.findByCondition(condition);
        return Result.success(list);
    }

    /**
     * 导出用于记录任务执行的每一步历史列表
     */
    @ApiOperation("导出用于记录任务执行的每一步历史列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:historyTaskList:export')")
    @Log(title = "用于记录任务执行的每一步历史", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TaskInfoListHistoryCriteria criteria)
    {
        List<TaskInfoListHistoryDto> list = taskInfoListHistoryService.findList(criteria);
        ExcelUtil<TaskInfoListHistoryDto> util = new ExcelUtil<TaskInfoListHistoryDto>(TaskInfoListHistoryDto.class);
        util.exportExcel(response, list, "用于记录任务执行的每一步历史数据");
    }

    /**
     * 获取用于记录任务执行的每一步历史详细信息
     */
    @ApiOperation("获取用于记录任务执行的每一步历史详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-task:historyTaskList:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(taskInfoListHistoryService.findById(id));
    }

    /**
     * 新增用于记录任务执行的每一步历史
     */
    @ApiOperation("新增用于记录任务执行的每一步历史")
    @PreAuthorize("@ss.hasPermi('wcs-task:historyTaskList:add')")
    @Log(title = "用于记录任务执行的每一步历史", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody TaskInfoListHistory taskInfoListHistory)
    {
        taskInfoListHistory.setCreateTime(DateUtil.getNowDateTimeString());

        taskInfoListHistoryService.save(taskInfoListHistory);
        return toAjax(true);
    }

    /**
     * 修改用于记录任务执行的每一步历史
     */
    @ApiOperation("修改用于记录任务执行的每一步历史")
    @PreAuthorize("@ss.hasPermi('wcs-task:historyTaskList:edit')")
    @Log(title = "用于记录任务执行的每一步历史", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody TaskInfoListHistory taskInfoListHistory)
    {



        return toAjax(taskInfoListHistoryService.update(taskInfoListHistory));
    }

    /**
     * 删除用于记录任务执行的每一步历史
     */
    @ApiOperation("删除用于记录任务执行的每一步历史")
    @PreAuthorize("@ss.hasPermi('wcs-task:historyTaskList:remove')")
    @Log(title = "用于记录任务执行的每一步历史", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(taskInfoListHistoryService.deleteTaskInfoListHistoryByIds(ids));
    }
}
