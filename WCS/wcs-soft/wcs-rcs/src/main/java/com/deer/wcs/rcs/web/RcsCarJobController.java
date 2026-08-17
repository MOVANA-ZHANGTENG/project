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
import com.deer.wcs.rcs.model.RcsCarJob;
import com.deer.wcs.rcs.model.RcsCarJobDto;
import com.deer.wcs.rcs.model.RcsCarJobCriteria;
import com.deer.wcs.rcs.service.RcsCarJobService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 小车任务详情Controller
 * 
 * @author deer
 * @date 2025-07-07
 */
@Api("小车任务详情")
@RestController
@RequestMapping("/wcs-rcs/RcsCarJob")
public class RcsCarJobController extends BaseController
{
    @Autowired
    private RcsCarJobService rcsCarJobService;

    /**
     * 查询小车任务详情列表
     */
    @ApiOperation("查询小车任务详情列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarJob:list')")
    @GetMapping("/list")
    public TableDataInfo list(RcsCarJobCriteria Criteria)
    {
        startPage();
        List<RcsCarJobDto> list = rcsCarJobService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出小车任务详情列表
     */
    @ApiOperation("导出小车任务详情列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarJob:export')")
    @Log(title = "小车任务详情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RcsCarJobCriteria criteria)
    {
        List<RcsCarJobDto> list = rcsCarJobService.findList(criteria);
        ExcelUtil<RcsCarJobDto> util = new ExcelUtil<RcsCarJobDto>(RcsCarJobDto.class);
        util.exportExcel(response, list, "小车任务详情数据");
    }

    /**
     * 获取小车任务详情详细信息
     */
    @ApiOperation("获取小车任务详情详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarJob:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(rcsCarJobService.findById(id));
    }

    /**
     * 新增小车任务详情
     */
    @ApiOperation("新增小车任务详情")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarJob:add')")
    @Log(title = "小车任务详情", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody RcsCarJob rcsCarJob)
    {
        rcsCarJob.setCreateTime(DateUtil.getNowDateTimeString());
        rcsCarJob.setCreateUserName(getUsername());

        rcsCarJobService.save(rcsCarJob);
        return toAjax(true);
    }

    /**
     * 修改小车任务详情
     */
    @ApiOperation("修改小车任务详情")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarJob:edit')")
    @Log(title = "小车任务详情", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody RcsCarJob rcsCarJob)
    {



        return toAjax(rcsCarJobService.update(rcsCarJob));
    }

    /**
     * 删除小车任务详情
     */
    @ApiOperation("删除小车任务详情")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarJob:remove')")
    @Log(title = "小车任务详情", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(rcsCarJobService.deleteRcsCarJobByIds(ids));
    }
}
