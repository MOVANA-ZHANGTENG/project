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
import com.deer.wcs.rcs.model.RcsCarType;
import com.deer.wcs.rcs.model.RcsCarTypeDto;
import com.deer.wcs.rcs.model.RcsCarTypeCriteria;
import com.deer.wcs.rcs.service.RcsCarTypeService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 车型号Controller
 * 
 * @author deer
 * @date 2025-10-14
 */
@Api("车型号")
@RestController
@RequestMapping("/wcs-rcs/RcsCarType")
public class RcsCarTypeController extends BaseController
{
    @Autowired
    private RcsCarTypeService rcsCarTypeService;

    /**
     * 查询车型号列表
     */
    @ApiOperation("查询车型号列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarType:list')")
    @GetMapping("/list")
    public TableDataInfo list(RcsCarTypeCriteria Criteria)
    {
        startPage();
        List<RcsCarTypeDto> list = rcsCarTypeService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出车型号列表
     */
    @ApiOperation("导出车型号列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarType:export')")
    @Log(title = "车型号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RcsCarTypeCriteria criteria)
    {
        List<RcsCarTypeDto> list = rcsCarTypeService.findList(criteria);
        ExcelUtil<RcsCarTypeDto> util = new ExcelUtil<RcsCarTypeDto>(RcsCarTypeDto.class);
        util.exportExcel(response, list, "车型号数据");
    }

    /**
     * 获取车型号详细信息
     */
    @ApiOperation("获取车型号详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarType:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(rcsCarTypeService.findById(id));
    }

    /**
     * 新增车型号
     */
    @ApiOperation("新增车型号")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarType:add')")
    @Log(title = "车型号", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody RcsCarType rcsCarType)
    {
        rcsCarType.setCreateTime(DateUtil.getNowDateTimeString());
        rcsCarType.setCreateUserId(getUserId());
        rcsCarType.setCreateUserName(getUsername());

        rcsCarTypeService.save(rcsCarType);
        return toAjax(true);
    }

    /**
     * 修改车型号
     */
    @ApiOperation("修改车型号")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarType:edit')")
    @Log(title = "车型号", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody RcsCarType rcsCarType)
    {

         rcsCarType.setUpdateUserId(getUserId());
         rcsCarType.setUpdateUserName(getUsername());
         rcsCarType.setUpdateTime(DateUtil.getNowDateTimeString());


        return toAjax(rcsCarTypeService.update(rcsCarType));
    }

    /**
     * 删除车型号
     */
    @ApiOperation("删除车型号")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarType:remove')")
    @Log(title = "车型号", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(rcsCarTypeService.deleteRcsCarTypeByIds(ids));
    }
}
