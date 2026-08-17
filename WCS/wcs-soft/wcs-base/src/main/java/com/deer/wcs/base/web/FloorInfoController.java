package com.deer.wcs.base.web;

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
import com.deer.wcs.base.model.FloorInfo;
import com.deer.wcs.base.model.FloorInfoDto;
import com.deer.wcs.base.model.FloorInfoCriteria;
import com.deer.wcs.base.service.FloorInfoService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 层Controller
 * 
 * @author deer
 * @date 2025-09-18
 */
@Api("层")
@RestController
@RequestMapping("/wcs-base/FloorInfo")
public class FloorInfoController extends BaseController
{
    @Autowired
    private FloorInfoService floorInfoService;

    /**
     * 查询层列表
     */
    @ApiOperation("查询层列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:FloorInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(FloorInfoCriteria Criteria)
    {
        startPage();
        List<FloorInfoDto> list = floorInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出层列表
     */
    @ApiOperation("导出层列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:FloorInfo:export')")
    @Log(title = "层", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FloorInfoCriteria criteria)
    {
        List<FloorInfoDto> list = floorInfoService.findList(criteria);
        ExcelUtil<FloorInfoDto> util = new ExcelUtil<FloorInfoDto>(FloorInfoDto.class);
        util.exportExcel(response, list, "层数据");
    }

    /**
     * 获取层详细信息
     */
    @ApiOperation("获取层详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:FloorInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(floorInfoService.findById(id));
    }

    /**
     * 新增层
     */
    @ApiOperation("新增层")
    //@PreAuthorize("@ss.hasPermi('wcs-base:FloorInfo:add')")
    @Log(title = "层", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody FloorInfo floorInfo)
    {
        floorInfo.setCreateTime(DateUtil.getNowDateTimeString());
        floorInfo.setCreateUserId(getUserId());
        floorInfo.setCreateUserName(getUsername());

        floorInfoService.save(floorInfo);
        return toAjax(true);
    }

    /**
     * 修改层
     */
    @ApiOperation("修改层")
    //@PreAuthorize("@ss.hasPermi('wcs-base:FloorInfo:edit')")
    @Log(title = "层", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody FloorInfo floorInfo)
    {



        return toAjax(floorInfoService.update(floorInfo));
    }

    /**
     * 删除层
     */
    @ApiOperation("删除层")
    //@PreAuthorize("@ss.hasPermi('wcs-base:FloorInfo:remove')")
    @Log(title = "层", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(floorInfoService.deleteFloorInfoByIds(ids));
    }
}
