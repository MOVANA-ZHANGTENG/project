package com.deer.wcs.system.web;

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
import com.deer.wcs.system.model.Auto;
import com.deer.wcs.system.model.AutoDto;
import com.deer.wcs.system.model.AutoCriteria;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 当日自增长Controller
 * 
 * @author deer
 * @date 2023-10-23
 */
@Api("当日自增长")
@RestController
@RequestMapping("/wcs-base/auto")
public class AutoController extends BaseController
{
    @Autowired
    private AutoService autoService;

    /**
     * 查询当日自增长列表
     */
    @ApiOperation("查询当日自增长列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:auto:list')")
    @GetMapping("/list")
    public TableDataInfo list(AutoCriteria Criteria)
    {
        startPage();
        List<AutoDto> list = autoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出当日自增长列表
     */
    @ApiOperation("导出当日自增长列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:auto:export')")
    @Log(title = "当日自增长", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AutoCriteria criteria)
    {
        List<AutoDto> list = autoService.findList(criteria);
        ExcelUtil<AutoDto> util = new ExcelUtil<AutoDto>(AutoDto.class);
        util.exportExcel(response, list, "当日自增长数据");
    }

    /**
     * 获取当日自增长详细信息
     */
    @ApiOperation("获取当日自增长详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:auto:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Integer id)
    {
        return success(autoService.findById(id));
    }

    /**
     * 新增当日自增长
     */
    @ApiOperation("新增当日自增长")
    @PreAuthorize("@ss.hasPermi('wcs-base:auto:add')")
    @Log(title = "当日自增长", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody Auto auto)
    {

        autoService.save(auto);
        return toAjax(true);
    }

    /**
     * 修改当日自增长
     */
    @ApiOperation("修改当日自增长")
    @PreAuthorize("@ss.hasPermi('wcs-base:auto:edit')")
    @Log(title = "当日自增长", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody Auto auto)
    {



        return toAjax(autoService.update(auto));
    }

    /**
     * 删除当日自增长
     */
    @ApiOperation("删除当日自增长")
    @PreAuthorize("@ss.hasPermi('wcs-base:auto:remove')")
    @Log(title = "当日自增长", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Integer[] ids)
    {
        return toAjax(autoService.deleteAutoByIds(ids));
    }
}
