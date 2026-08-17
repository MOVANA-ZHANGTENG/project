package com.deer.wcs.base.web;

import com.deer.wcs.base.model.WmsTaskInfo;
import com.deer.wcs.base.model.WmsTaskInfoCriteria;
import com.deer.wcs.base.model.WmsTaskInfoDto;
import com.deer.wcs.base.service.WmsTaskInfoService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * wms任务Controller
 * 
 * @author deer
 * @date 2024-05-10
 */
@Api("wms任务")
@RestController
@RequestMapping("/wcs-base/WmsTaskInfo")
public class WmsTaskInfoController extends BaseController
{
    @Autowired
    private WmsTaskInfoService wmsTaskInfoService;

    /**
     * 查询wms任务列表
     */
    @ApiOperation("查询wms任务列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:WmsTaskInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsTaskInfoCriteria Criteria)
    {
        startPage();
        List<WmsTaskInfoDto> list = wmsTaskInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出wms任务列表
     */
    @ApiOperation("导出wms任务列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:WmsTaskInfo:export')")
    @Log(title = "wms任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WmsTaskInfoCriteria criteria)
    {
        List<WmsTaskInfoDto> list = wmsTaskInfoService.findList(criteria);
        ExcelUtil<WmsTaskInfoDto> util = new ExcelUtil<WmsTaskInfoDto>(WmsTaskInfoDto.class);
        util.exportExcel(response, list, "wms任务数据");
    }

    /**
     * 获取wms任务详细信息
     */
    @ApiOperation("获取wms任务详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:WmsTaskInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(wmsTaskInfoService.findById(id));
    }

    /**
     * 新增wms任务
     */
    @ApiOperation("新增wms任务")
    @PreAuthorize("@ss.hasPermi('wcs-base:WmsTaskInfo:add')")
    @Log(title = "wms任务", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody WmsTaskInfo wmsTaskInfo)
    {
        wmsTaskInfo.setCreateTime(DateUtil.getNowDateTimeString());

        wmsTaskInfoService.save(wmsTaskInfo);
        return toAjax(true);
    }

    /**
     * 修改wms任务
     */
    @ApiOperation("修改wms任务")
    @PreAuthorize("@ss.hasPermi('wcs-base:WmsTaskInfo:edit')")
    @Log(title = "wms任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody WmsTaskInfo wmsTaskInfo)
    {



        return toAjax(wmsTaskInfoService.update(wmsTaskInfo));
    }

    /**
     * 删除wms任务
     */
    @ApiOperation("删除wms任务")
    @PreAuthorize("@ss.hasPermi('wcs-base:WmsTaskInfo:remove')")
    @Log(title = "wms任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(wmsTaskInfoService.deleteWmsTaskInfoByIds(ids));
    }
}
