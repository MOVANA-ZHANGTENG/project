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
import com.deer.wcs.rcs.model.RcsCarPath;
import com.deer.wcs.rcs.model.RcsCarPathDto;
import com.deer.wcs.rcs.model.RcsCarPathCriteria;
import com.deer.wcs.rcs.service.RcsCarPathService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 车路径Controller
 * 
 * @author deer
 * @date 2025-10-15
 */
@Api("车路径")
@RestController
@RequestMapping("/wcs-rcs/RcsCarPath")
public class RcsCarPathController extends BaseController
{
    @Autowired
    private RcsCarPathService rcsCarPathService;

    /**
     * 查询车路径列表
     */
    @ApiOperation("查询车路径列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarPath:list')")
    @GetMapping("/list")
    public TableDataInfo list(RcsCarPathCriteria Criteria)
    {
        startPage();
        List<RcsCarPathDto> list = rcsCarPathService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出车路径列表
     */
    @ApiOperation("导出车路径列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarPath:export')")
    @Log(title = "车路径", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RcsCarPathCriteria criteria)
    {
        List<RcsCarPathDto> list = rcsCarPathService.findList(criteria);
        ExcelUtil<RcsCarPathDto> util = new ExcelUtil<RcsCarPathDto>(RcsCarPathDto.class);
        util.exportExcel(response, list, "车路径数据");
    }

    /**
     * 获取车路径详细信息
     */
    @ApiOperation("获取车路径详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarPath:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(rcsCarPathService.findById(id));
    }

    /**
     * 新增车路径
     */
    @ApiOperation("新增车路径")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarPath:add')")
    @Log(title = "车路径", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody RcsCarPath rcsCarPath)
    {

        rcsCarPathService.save(rcsCarPath);
        return toAjax(true);
    }

    /**
     * 修改车路径
     */
    @ApiOperation("修改车路径")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarPath:edit')")
    @Log(title = "车路径", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody RcsCarPath rcsCarPath)
    {



        return toAjax(rcsCarPathService.update(rcsCarPath));
    }

    /**
     * 删除车路径
     */
    @ApiOperation("删除车路径")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarPath:remove')")
    @Log(title = "车路径", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(rcsCarPathService.deleteRcsCarPathByIds(ids));
    }
}
