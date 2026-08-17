package com.deer.wcs.base.web;

import com.deer.wcs.base.model.TaskHandleCriteria;
import com.deer.wcs.base.model.TaskHandleDto;
import com.deer.wcs.base.service.TaskHandleService;
import com.deer.wcs.common.utils.DateUtil;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.base.model.TaskHandle;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 任务执行器Controller
 * 
 * @author deer
 * @date 2024-04-28
 */
@Api("任务执行器")
@RestController
@RequestMapping("/wcs-base/TaskHandle")
public class TaskHandleController extends BaseController
{
    @Autowired
    private TaskHandleService taskHandleService;

    /**
     * 查询任务执行器列表
     */
    @ApiOperation("查询任务执行器列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskHandle:list')")
    @GetMapping("/list")
    public TableDataInfo list(TaskHandleCriteria Criteria)
    {
        startPage();
        List<TaskHandleDto> list = taskHandleService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出任务执行器列表
     */
    @ApiOperation("导出任务执行器列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskHandle:export')")
    @Log(title = "任务执行器", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TaskHandleCriteria criteria)
    {
        List<TaskHandleDto> list = taskHandleService.findList(criteria);
        ExcelUtil<TaskHandleDto> util = new ExcelUtil<TaskHandleDto>(TaskHandleDto.class);
        util.exportExcel(response, list, "任务执行器数据");
    }

    /**
     * 获取任务执行器详细信息
     */
    @ApiOperation("获取任务执行器详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskHandle:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(taskHandleService.findById(id));
    }

    /**
     * 新增任务执行器
     */
    @ApiOperation("新增任务执行器")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskHandle:add')")
    @Log(title = "任务执行器", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody TaskHandle taskHandle)
    {
        taskHandle.setCreateTime(DateUtil.getNowDateTimeString());
        taskHandle.setCreateUserId(getUserId());
        taskHandle.setCreateUserName(getUsername());

        taskHandleService.save(taskHandle);
        return toAjax(true);
    }

    /**
     * 修改任务执行器
     */
    @ApiOperation("修改任务执行器")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskHandle:edit')")
    @Log(title = "任务执行器", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody TaskHandle taskHandle)
    {

         taskHandle.setUpdateTime(DateUtil.getNowDateTimeString());
         taskHandle.setUpdateUserId(getUserId());
         taskHandle.setUpdateUserName(getUsername());


        return toAjax(taskHandleService.update(taskHandle));
    }

    /**
     * 删除任务执行器
     */
    @ApiOperation("删除任务执行器")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskHandle:remove')")
    @Log(title = "任务执行器", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(taskHandleService.deleteTaskHandleByIds(ids));
    }
}
