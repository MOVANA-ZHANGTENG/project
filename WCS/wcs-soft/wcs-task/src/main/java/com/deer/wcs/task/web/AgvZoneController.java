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
import com.deer.wcs.task.model.AgvZone;
import com.deer.wcs.task.model.AgvZoneDto;
import com.deer.wcs.task.model.AgvZoneCriteria;
import com.deer.wcs.task.service.AgvZoneService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * AGV交管Controller
 * 
 * @author deer
 * @date 2024-11-26
 */
@Api("AGV交管")
@RestController
@RequestMapping("/wcs-task/AgvZone")
public class AgvZoneController extends BaseController
{
    @Autowired
    private AgvZoneService agvZoneService;

    /**
     * 查询AGV交管列表
     */
    @ApiOperation("查询AGV交管列表")
    //@PreAuthorize("@ss.hasPermi('wcs-task:AgvZone:list')")
    @GetMapping("/list")
    public TableDataInfo list(AgvZoneCriteria Criteria)
    {
        startPage();
        List<AgvZoneDto> list = agvZoneService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出AGV交管列表
     */
    @ApiOperation("导出AGV交管列表")
    //@PreAuthorize("@ss.hasPermi('wcs-task:AgvZone:export')")
    @Log(title = "AGV交管", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AgvZoneCriteria criteria)
    {
        List<AgvZoneDto> list = agvZoneService.findList(criteria);
        ExcelUtil<AgvZoneDto> util = new ExcelUtil<AgvZoneDto>(AgvZoneDto.class);
        util.exportExcel(response, list, "AGV交管数据");
    }

    /**
     * 获取AGV交管详细信息
     */
    @ApiOperation("获取AGV交管详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-task:AgvZone:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(agvZoneService.findById(id));
    }

    /**
     * 新增AGV交管
     */
    @ApiOperation("新增AGV交管")
    //@PreAuthorize("@ss.hasPermi('wcs-task:AgvZone:add')")
    @Log(title = "AGV交管", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody AgvZone agvZone)
    {

        agvZoneService.save(agvZone);
        return toAjax(true);
    }

    /**
     * 修改AGV交管
     */
    @ApiOperation("修改AGV交管")
    //@PreAuthorize("@ss.hasPermi('wcs-task:AgvZone:edit')")
    @Log(title = "AGV交管", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody AgvZone agvZone)
    {

         agvZone.setUpdateTime(DateUtil.getNowDateTimeString());


        return toAjax(agvZoneService.update(agvZone));
    }

    /**
     * 删除AGV交管
     */
    @ApiOperation("删除AGV交管")
    //@PreAuthorize("@ss.hasPermi('wcs-task:AgvZone:remove')")
    @Log(title = "AGV交管", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(agvZoneService.deleteAgvZoneByIds(ids));
    }
}
