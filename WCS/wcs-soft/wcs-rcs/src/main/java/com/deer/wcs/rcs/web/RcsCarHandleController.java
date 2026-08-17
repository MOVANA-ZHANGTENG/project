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
import com.deer.wcs.rcs.model.RcsCarHandle;
import com.deer.wcs.rcs.model.RcsCarHandleDto;
import com.deer.wcs.rcs.model.RcsCarHandleCriteria;
import com.deer.wcs.rcs.service.RcsCarHandleService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * RcsCarHandleController
 * 
 * @author deer
 * @date 2025-10-14
 */
@Api("RcsCarHandle")
@RestController
@RequestMapping("/wcs-rcs/RcsCarHandle")
public class RcsCarHandleController extends BaseController
{
    @Autowired
    private RcsCarHandleService rcsCarHandleService;

    /**
     * 查询RcsCarHandle列表
     */
    @ApiOperation("查询RcsCarHandle列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarHandle:list')")
    @GetMapping("/list")
    public TableDataInfo list(RcsCarHandleCriteria Criteria)
    {
        startPage();
        List<RcsCarHandleDto> list = rcsCarHandleService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出RcsCarHandle列表
     */
    @ApiOperation("导出RcsCarHandle列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarHandle:export')")
    @Log(title = "RcsCarHandle", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RcsCarHandleCriteria criteria)
    {
        List<RcsCarHandleDto> list = rcsCarHandleService.findList(criteria);
        ExcelUtil<RcsCarHandleDto> util = new ExcelUtil<RcsCarHandleDto>(RcsCarHandleDto.class);
        util.exportExcel(response, list, "RcsCarHandle数据");
    }

    /**
     * 获取RcsCarHandle详细信息
     */
    @ApiOperation("获取RcsCarHandle详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarHandle:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(rcsCarHandleService.findById(id));
    }

    /**
     * 新增RcsCarHandle
     */
    @ApiOperation("新增RcsCarHandle")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarHandle:add')")
    @Log(title = "RcsCarHandle", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody RcsCarHandle rcsCarHandle)
    {
        rcsCarHandle.setCreateTime(DateUtil.getNowDateTimeString());

        rcsCarHandleService.save(rcsCarHandle);
        return toAjax(true);
    }

    /**
     * 修改RcsCarHandle
     */
    @ApiOperation("修改RcsCarHandle")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarHandle:edit')")
    @Log(title = "RcsCarHandle", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody RcsCarHandle rcsCarHandle)
    {



        return toAjax(rcsCarHandleService.update(rcsCarHandle));
    }

    /**
     * 删除RcsCarHandle
     */
    @ApiOperation("删除RcsCarHandle")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarHandle:remove')")
    @Log(title = "RcsCarHandle", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(rcsCarHandleService.deleteRcsCarHandleByIds(ids));
    }
}
