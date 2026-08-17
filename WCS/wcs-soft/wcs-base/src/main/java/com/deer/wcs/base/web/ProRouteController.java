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
import com.deer.wcs.base.model.ProRoute;
import com.deer.wcs.base.model.ProRouteDto;
import com.deer.wcs.base.model.ProRouteCriteria;
import com.deer.wcs.base.service.ProRouteService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 工艺流程Controller
 * 
 * @author deer
 * @date 2024-11-21
 */
@Api("工艺流程")
@RestController
@RequestMapping("/wcs-base/ProRoute")
public class ProRouteController extends BaseController
{
    @Autowired
    private ProRouteService proRouteService;

    /**
     * 查询工艺流程列表
     */
    @ApiOperation("查询工艺流程列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProRoute:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProRouteCriteria Criteria)
    {
        startPage();
        List<ProRouteDto> list = proRouteService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出工艺流程列表
     */
    @ApiOperation("导出工艺流程列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProRoute:export')")
    @Log(title = "工艺流程", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProRouteCriteria criteria)
    {
        List<ProRouteDto> list = proRouteService.findList(criteria);
        ExcelUtil<ProRouteDto> util = new ExcelUtil<ProRouteDto>(ProRouteDto.class);
        util.exportExcel(response, list, "工艺流程数据");
    }

    /**
     * 获取工艺流程详细信息
     */
    @ApiOperation("获取工艺流程详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProRoute:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(proRouteService.findById(id));
    }

    /**
     * 新增工艺流程
     */
    @ApiOperation("新增工艺流程")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProRoute:add')")
    @Log(title = "工艺流程", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody ProRoute proRoute)
    {

        proRouteService.save(proRoute);
        return toAjax(true);
    }

    /**
     * 修改工艺流程
     */
    @ApiOperation("修改工艺流程")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProRoute:edit')")
    @Log(title = "工艺流程", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody ProRoute proRoute)
    {



        return toAjax(proRouteService.update(proRoute));
    }

    /**
     * 删除工艺流程
     */
    @ApiOperation("删除工艺流程")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProRoute:remove')")
    @Log(title = "工艺流程", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(proRouteService.deleteProRouteByIds(ids));
    }
}
