package com.deer.wcs.task.web;

import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.task.model.JobHandle;
import com.deer.wcs.task.model.JobHandleCriteria;
import com.deer.wcs.task.model.JobHandleDto;
import com.deer.wcs.task.service.JobHandleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 步骤执行器Controller
 * 
 * @author deer
 * @date 2024-05-10
 */
@Api("步骤执行器")
@RestController
@RequestMapping("/wcs-task/jobHandle")
public class JobHandleController extends BaseController
{
    @Autowired
    private JobHandleService jobHandleService;

    /**
     * 查询步骤执行器列表
     */
    @ApiOperation("查询步骤执行器列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:jobHandle:list')")
    @GetMapping("/list")
    public TableDataInfo list(JobHandleCriteria Criteria)
    {
        startPage();
        List<JobHandleDto> list = jobHandleService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出步骤执行器列表
     */
    @ApiOperation("导出步骤执行器列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:jobHandle:export')")
    @Log(title = "步骤执行器", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JobHandleCriteria criteria)
    {
        List<JobHandleDto> list = jobHandleService.findList(criteria);
        ExcelUtil<JobHandleDto> util = new ExcelUtil<JobHandleDto>(JobHandleDto.class);
        util.exportExcel(response, list, "步骤执行器数据");
    }

    /**
     * 获取步骤执行器详细信息
     */
    @ApiOperation("获取步骤执行器详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-task:jobHandle:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(jobHandleService.findById(id));
    }

    /**
     * 新增步骤执行器
     */
    @ApiOperation("新增步骤执行器")
    @PreAuthorize("@ss.hasPermi('wcs-task:jobHandle:add')")
    @Log(title = "步骤执行器", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody JobHandle jobHandle)
    {
        jobHandle.setCreateTime(DateUtil.getNowDateTimeString());
        jobHandle.setCreateUserId(getUserId());
        jobHandle.setCreateUserName(getUsername());

        jobHandleService.save(jobHandle);
        return toAjax(true);
    }

    /**
     * 修改步骤执行器
     */
    @ApiOperation("修改步骤执行器")
    @PreAuthorize("@ss.hasPermi('wcs-task:jobHandle:edit')")
    @Log(title = "步骤执行器", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody JobHandle jobHandle)
    {

         jobHandle.setUpdateTime(DateUtil.getNowDateTimeString());
         jobHandle.setUpdateUserId(getUserId());
         jobHandle.setUpdateUserName(getUsername());


        return toAjax(jobHandleService.update(jobHandle));
    }

    /**
     * 删除步骤执行器
     */
    @ApiOperation("删除步骤执行器")
    @PreAuthorize("@ss.hasPermi('wcs-task:jobHandle:remove')")
    @Log(title = "步骤执行器", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(jobHandleService.deleteJobHandleByIds(ids));
    }
}
