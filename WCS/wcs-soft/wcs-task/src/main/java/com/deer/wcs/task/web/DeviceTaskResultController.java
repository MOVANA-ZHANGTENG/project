package com.deer.wcs.task.web;

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
import com.deer.wcs.task.model.DeviceTaskResult;
import com.deer.wcs.task.model.DeviceTaskResultDto;
import com.deer.wcs.task.model.DeviceTaskResultCriteria;
import com.deer.wcs.task.service.DeviceTaskResultService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 设备任务回传Controller
 * 
 * @author deer
 * @date 2024-11-22
 */
@Api("设备任务回传")
@RestController
@RequestMapping("/wcs-task/DeviceTaskResult")
public class DeviceTaskResultController extends BaseController
{
    @Autowired
    private DeviceTaskResultService deviceTaskResultService;

    /**
     * 查询设备任务回传列表
     */
    @ApiOperation("查询设备任务回传列表")
    //@PreAuthorize("@ss.hasPermi('wcs-task:DeviceTaskResult:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceTaskResultCriteria Criteria)
    {
        startPage();
        List<DeviceTaskResultDto> list = deviceTaskResultService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出设备任务回传列表
     */
    @ApiOperation("导出设备任务回传列表")
    //@PreAuthorize("@ss.hasPermi('wcs-task:DeviceTaskResult:export')")
    @Log(title = "设备任务回传", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeviceTaskResultCriteria criteria)
    {
        List<DeviceTaskResultDto> list = deviceTaskResultService.findList(criteria);
        ExcelUtil<DeviceTaskResultDto> util = new ExcelUtil<DeviceTaskResultDto>(DeviceTaskResultDto.class);
        util.exportExcel(response, list, "设备任务回传数据");
    }

    /**
     * 获取设备任务回传详细信息
     */
    @ApiOperation("获取设备任务回传详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-task:DeviceTaskResult:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(deviceTaskResultService.findById(id));
    }

    /**
     * 新增设备任务回传
     */
    @ApiOperation("新增设备任务回传")
    //@PreAuthorize("@ss.hasPermi('wcs-task:DeviceTaskResult:add')")
    @Log(title = "设备任务回传", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody DeviceTaskResult deviceTaskResult)
    {
        deviceTaskResult.setCreateTime(DateUtil.getNowDateTimeString());

        deviceTaskResultService.save(deviceTaskResult);
        return toAjax(true);
    }

    /**
     * 修改设备任务回传
     */
    @ApiOperation("修改设备任务回传")
    //@PreAuthorize("@ss.hasPermi('wcs-task:DeviceTaskResult:edit')")
    @Log(title = "设备任务回传", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody DeviceTaskResult deviceTaskResult)
    {



        return toAjax(deviceTaskResultService.update(deviceTaskResult));
    }

    /**
     * 删除设备任务回传
     */
    @ApiOperation("删除设备任务回传")
    //@PreAuthorize("@ss.hasPermi('wcs-task:DeviceTaskResult:remove')")
    @Log(title = "设备任务回传", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(deviceTaskResultService.deleteDeviceTaskResultByIds(ids));
    }
}
