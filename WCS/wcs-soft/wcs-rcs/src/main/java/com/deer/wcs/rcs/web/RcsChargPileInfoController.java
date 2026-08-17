package com.deer.wcs.rcs.web;

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
import com.deer.wcs.rcs.model.RcsChargPileInfo;
import com.deer.wcs.rcs.model.RcsChargPileInfoDto;
import com.deer.wcs.rcs.model.RcsChargPileInfoCriteria;
import com.deer.wcs.rcs.service.RcsChargPileInfoService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 充电桩Controller
 * 
 * @author deer
 * @date 2025-10-14
 */
@Api("充电桩")
@RestController
@RequestMapping("/wcs-rcs/RcsChargPileInfo")
public class RcsChargPileInfoController extends BaseController
{
    @Autowired
    private RcsChargPileInfoService rcsChargPileInfoService;

    /**
     * 查询充电桩列表
     */
    @ApiOperation("查询充电桩列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsChargPileInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(RcsChargPileInfoCriteria Criteria)
    {
        startPage();
        List<RcsChargPileInfoDto> list = rcsChargPileInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出充电桩列表
     */
    @ApiOperation("导出充电桩列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsChargPileInfo:export')")
    @Log(title = "充电桩", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RcsChargPileInfoCriteria criteria)
    {
        List<RcsChargPileInfoDto> list = rcsChargPileInfoService.findList(criteria);
        ExcelUtil<RcsChargPileInfoDto> util = new ExcelUtil<RcsChargPileInfoDto>(RcsChargPileInfoDto.class);
        util.exportExcel(response, list, "充电桩数据");
    }

    /**
     * 获取充电桩详细信息
     */
    @ApiOperation("获取充电桩详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsChargPileInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(rcsChargPileInfoService.findById(id));
    }

    /**
     * 新增充电桩
     */
    @ApiOperation("新增充电桩")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsChargPileInfo:add')")
    @Log(title = "充电桩", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody RcsChargPileInfo rcsChargPileInfo)
    {

        rcsChargPileInfoService.save(rcsChargPileInfo);
        return toAjax(true);
    }

    /**
     * 修改充电桩
     */
    @ApiOperation("修改充电桩")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsChargPileInfo:edit')")
    @Log(title = "充电桩", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody RcsChargPileInfo rcsChargPileInfo)
    {



        return toAjax(rcsChargPileInfoService.update(rcsChargPileInfo));
    }

    /**
     * 删除充电桩
     */
    @ApiOperation("删除充电桩")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsChargPileInfo:remove')")
    @Log(title = "充电桩", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Integer[] ids)
    {
        return toAjax(rcsChargPileInfoService.deleteRcsChargPileInfoByIds(ids));
    }
}
