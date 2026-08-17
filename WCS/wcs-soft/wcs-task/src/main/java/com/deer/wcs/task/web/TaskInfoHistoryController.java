package com.deer.wcs.task.web;

import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.task.model.TaskInfoHistory;
import com.deer.wcs.task.model.TaskInfoHistoryCriteria;
import com.deer.wcs.task.model.TaskInfoHistoryDto;
import com.deer.wcs.task.service.TaskInfoHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 任务历史Controller
 * 
 * @author deer
 * @date 2024-06-04
 */
@Api("任务历史")
@RestController
@RequestMapping("/wcs-task/history")
public class TaskInfoHistoryController extends BaseController {
    @Autowired
    private TaskInfoHistoryService taskInfoHistoryService;

    /**
     * 查询任务历史列表
     */
    @ApiOperation("查询任务历史列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:history:list')")
    @GetMapping("/list")
    public TableDataInfo list(TaskInfoHistoryCriteria Criteria)
    {
        startPage();
        List<TaskInfoHistoryDto> list = taskInfoHistoryService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出任务历史列表
     */
    @ApiOperation("导出任务历史列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:history:export')")
    @Log(title = "任务历史", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TaskInfoHistoryCriteria criteria)
    {
        List<TaskInfoHistoryDto> list = taskInfoHistoryService.findList(criteria);
        ExcelUtil<TaskInfoHistoryDto> util = new ExcelUtil<TaskInfoHistoryDto>(TaskInfoHistoryDto.class);
        util.exportExcel(response, list, "任务历史数据");
    }

    /**
     * 获取任务历史详细信息
     */
    @ApiOperation("获取任务历史详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-task:history:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(taskInfoHistoryService.findById(id));
    }

    /**
     * 新增任务历史
     */
    @ApiOperation("新增任务历史")
    @PreAuthorize("@ss.hasPermi('wcs-task:history:add')")
    @Log(title = "任务历史", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody TaskInfoHistory taskInfoHistory)
    {
        taskInfoHistory.setCreateTime(DateUtil.getNowDateTimeString());

        taskInfoHistoryService.save(taskInfoHistory);
        return toAjax(true);
    }

    /**
     * 修改任务历史
     */
    @ApiOperation("修改任务历史")
    @PreAuthorize("@ss.hasPermi('wcs-task:history:edit')")
    @Log(title = "任务历史", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody TaskInfoHistory taskInfoHistory)
    {



        return toAjax(taskInfoHistoryService.update(taskInfoHistory));
    }

    /**
     * 删除任务历史
     */
    @ApiOperation("删除任务历史")
    @PreAuthorize("@ss.hasPermi('wcs-task:history:remove')")
    @Log(title = "任务历史", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(taskInfoHistoryService.deleteTaskInfoHistoryByIds(ids));
    }
}
