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
import com.deer.wcs.base.model.ProRouteProcess;
import com.deer.wcs.base.model.ProRouteProcessDto;
import com.deer.wcs.base.model.ProRouteProcessCriteria;
import com.deer.wcs.base.service.ProRouteProcessService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 工艺流程工序关联Controller
 * 
 * @author deer
 * @date 2024-11-21
 */
@Api("工艺流程工序关联")
@RestController
@RequestMapping("/wcs-base/ProRouteProcess")
public class ProRouteProcessController extends BaseController
{
    @Autowired
    private ProRouteProcessService proRouteProcessService;

    /**
     * 查询工艺流程工序关联列表
     */
    @ApiOperation("查询工艺流程工序关联列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProRouteProcess:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProRouteProcessCriteria Criteria)
    {
        startPage();
        List<ProRouteProcessDto> list = proRouteProcessService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出工艺流程工序关联列表
     */
    @ApiOperation("导出工艺流程工序关联列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProRouteProcess:export')")
    @Log(title = "工艺流程工序关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProRouteProcessCriteria criteria)
    {
        List<ProRouteProcessDto> list = proRouteProcessService.findList(criteria);
        ExcelUtil<ProRouteProcessDto> util = new ExcelUtil<ProRouteProcessDto>(ProRouteProcessDto.class);
        util.exportExcel(response, list, "工艺流程工序关联数据");
    }

    /**
     * 获取工艺流程工序关联详细信息
     */
    @ApiOperation("获取工艺流程工序关联详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProRouteProcess:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(proRouteProcessService.findById(id));
    }

    /**
     * 新增工艺流程工序关联
     */
    @ApiOperation("新增工艺流程工序关联")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProRouteProcess:add')")
    @Log(title = "工艺流程工序关联", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody ProRouteProcess proRouteProcess)
    {

        proRouteProcessService.save(proRouteProcess);
        return toAjax(true);
    }

    /**
     * 修改工艺流程工序关联
     */
    @ApiOperation("修改工艺流程工序关联")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProRouteProcess:edit')")
    @Log(title = "工艺流程工序关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody ProRouteProcess proRouteProcess)
    {



        return toAjax(proRouteProcessService.update(proRouteProcess));
    }

    /**
     * 删除工艺流程工序关联
     */
    @ApiOperation("删除工艺流程工序关联")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProRouteProcess:remove')")
    @Log(title = "工艺流程工序关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(proRouteProcessService.deleteProRouteProcessByIds(ids));
    }
}
