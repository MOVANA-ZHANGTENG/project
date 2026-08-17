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
import com.deer.wcs.rcs.model.RcsTsj;
import com.deer.wcs.rcs.model.RcsTsjDto;
import com.deer.wcs.rcs.model.RcsTsjCriteria;
import com.deer.wcs.rcs.service.RcsTsjService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 提升机Controller
 * 
 * @author deer
 * @date 2026-05-10
 */
@Api("提升机")
@RestController
@RequestMapping("/wcs-rcs/RcsTsj")
public class RcsTsjController extends BaseController
{
    @Autowired
    private RcsTsjService rcsTsjService;

    /**
     * 查询提升机列表
     */
    @ApiOperation("查询提升机列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsTsj:list')")
    @GetMapping("/list")
    public TableDataInfo list(RcsTsjCriteria Criteria)
    {
        startPage();
        List<RcsTsjDto> list = rcsTsjService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出提升机列表
     */
    @ApiOperation("导出提升机列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsTsj:export')")
    @Log(title = "提升机", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RcsTsjCriteria criteria)
    {
        List<RcsTsjDto> list = rcsTsjService.findList(criteria);
        ExcelUtil<RcsTsjDto> util = new ExcelUtil<RcsTsjDto>(RcsTsjDto.class);
        util.exportExcel(response, list, "提升机数据");
    }

    /**
     * 获取提升机详细信息
     */
    @ApiOperation("获取提升机详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsTsj:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(rcsTsjService.findById(id));
    }

    /**
     * 新增提升机
     */
    @ApiOperation("新增提升机")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsTsj:add')")
    @Log(title = "提升机", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody RcsTsj rcsTsj)
    {
        rcsTsj.setCreateTime(DateUtil.getNowDateTimeString());

        rcsTsjService.save(rcsTsj);
        return toAjax(true);
    }

    /**
     * 修改提升机
     */
    @ApiOperation("修改提升机")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsTsj:edit')")
    @Log(title = "提升机", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody RcsTsj rcsTsj)
    {



        return toAjax(rcsTsjService.update(rcsTsj));
    }

    /**
     * 删除提升机
     */
    @ApiOperation("删除提升机")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsTsj:remove')")
    @Log(title = "提升机", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(rcsTsjService.deleteRcsTsjByIds(ids));
    }
}
