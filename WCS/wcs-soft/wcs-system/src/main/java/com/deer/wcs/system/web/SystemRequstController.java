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
import com.deer.wcs.system.model.SystemRequst;
import com.deer.wcs.system.model.SystemRequstDto;
import com.deer.wcs.system.model.SystemRequstCriteria;
import com.deer.wcs.system.service.SystemRequstService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 接口调用Controller
 * 
 * @author deer
 * @date 2023-11-16
 */
@Api("接口调用")
@RestController
@RequestMapping("/wcs-system/SystemRequst")
public class
SystemRequstController extends BaseController
{
    @Autowired
    private SystemRequstService systemRequstService;

    /**
     * 查询接口调用列表
     */
    @ApiOperation("查询接口调用列表")
    @PreAuthorize("@ss.hasPermi('wcs-system:SystemRequst:list')")
    @GetMapping("/list")
    public TableDataInfo list(SystemRequstCriteria Criteria)
    {
        startPage();
        List<SystemRequstDto> list = systemRequstService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出接口调用列表
     */
    @ApiOperation("导出接口调用列表")
    @PreAuthorize("@ss.hasPermi('wcs-system:SystemRequst:export')")
    @Log(title = "接口调用", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SystemRequstCriteria criteria)
    {
        List<SystemRequstDto> list = systemRequstService.findList(criteria);
        ExcelUtil<SystemRequstDto> util = new ExcelUtil<SystemRequstDto>(SystemRequstDto.class);
        util.exportExcel(response, list, "接口调用数据");
    }

    /**
     * 获取接口调用详细信息
     */
    @ApiOperation("获取接口调用详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-system:SystemRequst:query')")
    @GetMapping(value = "/{requestId}")
    public Result getInfo(@PathVariable("requestId") Integer requestId)
    {
        return success(systemRequstService.findById(requestId));
    }

    /**
     * 新增接口调用
     */
    @ApiOperation("新增接口调用")
    @PreAuthorize("@ss.hasPermi('wcs-system:SystemRequst:add')")
    @Log(title = "接口调用", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody SystemRequst systemRequst)
    {
        systemRequst.setCreateTime(DateUtil.getNowDateTimeString());

        systemRequstService.save(systemRequst);
        return toAjax(true);
    }

    /**
     * 修改接口调用
     */
    @ApiOperation("修改接口调用")
    @PreAuthorize("@ss.hasPermi('wcs-system:SystemRequst:edit')")
    @Log(title = "接口调用", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody SystemRequst systemRequst)
    {



        return toAjax(systemRequstService.update(systemRequst));
    }

    /**
     * 删除接口调用
     */
    @ApiOperation("删除接口调用")
    @PreAuthorize("@ss.hasPermi('wcs-system:SystemRequst:remove')")
    @Log(title = "接口调用", businessType = BusinessType.DELETE)
	@DeleteMapping("/{requestIds}")
    public Result remove(@PathVariable Integer[] requestIds)
    {
        return toAjax(systemRequstService.deleteSystemRequstByRequestIds(requestIds));
    }
}
