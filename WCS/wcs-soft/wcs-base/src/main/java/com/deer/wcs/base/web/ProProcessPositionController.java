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
import com.deer.wcs.base.model.ProProcessPosition;
import com.deer.wcs.base.model.ProProcessPositionDto;
import com.deer.wcs.base.model.ProProcessPositionCriteria;
import com.deer.wcs.base.service.ProProcessPositionService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 工序站台Controller
 * 
 * @author deer
 * @date 2024-12-25
 */
@Api("工序站台")
@RestController
@RequestMapping("/wcs-base/ProProcessPosition")
public class ProProcessPositionController extends BaseController
{
    @Autowired
    private ProProcessPositionService proProcessPositionService;

    /**
     * 查询工序站台列表
     */
    @ApiOperation("查询工序站台列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProProcessPosition:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProProcessPositionCriteria Criteria)
    {
        startPage();
        List<ProProcessPositionDto> list = proProcessPositionService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出工序站台列表
     */
    @ApiOperation("导出工序站台列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProProcessPosition:export')")
    @Log(title = "工序站台", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProProcessPositionCriteria criteria)
    {
        List<ProProcessPositionDto> list = proProcessPositionService.findList(criteria);
        ExcelUtil<ProProcessPositionDto> util = new ExcelUtil<ProProcessPositionDto>(ProProcessPositionDto.class);
        util.exportExcel(response, list, "工序站台数据");
    }

    /**
     * 获取工序站台详细信息
     */
    @ApiOperation("获取工序站台详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProProcessPosition:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(proProcessPositionService.findById(id));
    }

    /**
     * 新增工序站台
     */
    @ApiOperation("新增工序站台")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProProcessPosition:add')")
    @Log(title = "工序站台", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody ProProcessPosition proProcessPosition)
    {

        proProcessPositionService.save(proProcessPosition);
        return toAjax(true);
    }

    /**
     * 修改工序站台
     */
    @ApiOperation("修改工序站台")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProProcessPosition:edit')")
    @Log(title = "工序站台", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody ProProcessPosition proProcessPosition)
    {



        return toAjax(proProcessPositionService.update(proProcessPosition));
    }

    /**
     * 删除工序站台
     */
    @ApiOperation("删除工序站台")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProProcessPosition:remove')")
    @Log(title = "工序站台", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(proProcessPositionService.deleteProProcessPositionByIds(ids));
    }
}
