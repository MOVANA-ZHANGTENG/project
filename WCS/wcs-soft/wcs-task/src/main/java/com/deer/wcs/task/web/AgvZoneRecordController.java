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
import com.deer.wcs.task.model.AgvZoneRecord;
import com.deer.wcs.task.model.AgvZoneRecordDto;
import com.deer.wcs.task.model.AgvZoneRecordCriteria;
import com.deer.wcs.task.service.AgvZoneRecordService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 交管日志Controller
 * 
 * @author deer
 * @date 2024-11-26
 */
@Api("交管日志")
@RestController
@RequestMapping("/wcs-task/AgvZoneRecord")
public class AgvZoneRecordController extends BaseController
{
    @Autowired
    private AgvZoneRecordService agvZoneRecordService;

    /**
     * 查询交管日志列表
     */
    @ApiOperation("查询交管日志列表")
    //@PreAuthorize("@ss.hasPermi('wcs-task:AgvZoneRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(AgvZoneRecordCriteria Criteria)
    {
        startPage();
        List<AgvZoneRecordDto> list = agvZoneRecordService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出交管日志列表
     */
    @ApiOperation("导出交管日志列表")
    //@PreAuthorize("@ss.hasPermi('wcs-task:AgvZoneRecord:export')")
    @Log(title = "交管日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AgvZoneRecordCriteria criteria)
    {
        List<AgvZoneRecordDto> list = agvZoneRecordService.findList(criteria);
        ExcelUtil<AgvZoneRecordDto> util = new ExcelUtil<AgvZoneRecordDto>(AgvZoneRecordDto.class);
        util.exportExcel(response, list, "交管日志数据");
    }

    /**
     * 获取交管日志详细信息
     */
    @ApiOperation("获取交管日志详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-task:AgvZoneRecord:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(agvZoneRecordService.findById(id));
    }

    /**
     * 新增交管日志
     */
    @ApiOperation("新增交管日志")
    //@PreAuthorize("@ss.hasPermi('wcs-task:AgvZoneRecord:add')")
    @Log(title = "交管日志", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody AgvZoneRecord agvZoneRecord)
    {
        agvZoneRecord.setCreateTime(DateUtil.getNowDateTimeString());

        agvZoneRecordService.save(agvZoneRecord);
        return toAjax(true);
    }

    /**
     * 修改交管日志
     */
    @ApiOperation("修改交管日志")
    //@PreAuthorize("@ss.hasPermi('wcs-task:AgvZoneRecord:edit')")
    @Log(title = "交管日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody AgvZoneRecord agvZoneRecord)
    {



        return toAjax(agvZoneRecordService.update(agvZoneRecord));
    }

    /**
     * 删除交管日志
     */
    @ApiOperation("删除交管日志")
    //@PreAuthorize("@ss.hasPermi('wcs-task:AgvZoneRecord:remove')")
    @Log(title = "交管日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(agvZoneRecordService.deleteAgvZoneRecordByIds(ids));
    }
}
