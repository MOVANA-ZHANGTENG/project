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
import com.deer.wcs.base.model.ProProcess;
import com.deer.wcs.base.model.ProProcessDto;
import com.deer.wcs.base.model.ProProcessCriteria;
import com.deer.wcs.base.service.ProProcessService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 工序Controller
 * 
 * @author deer
 * @date 2024-11-21
 */
@Api("工序")
@RestController
@RequestMapping("/wcs-base/ProProcess")
public class ProProcessController extends BaseController
{
    @Autowired
    private ProProcessService proProcessService;

    /**
     * 查询工序列表
     */
    @ApiOperation("查询工序列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProProcess:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProProcessCriteria Criteria)
    {
        startPage();
        List<ProProcessDto> list = proProcessService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出工序列表
     */
    @ApiOperation("导出工序列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProProcess:export')")
    @Log(title = "工序", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProProcessCriteria criteria)
    {
        List<ProProcessDto> list = proProcessService.findList(criteria);
        ExcelUtil<ProProcessDto> util = new ExcelUtil<ProProcessDto>(ProProcessDto.class);
        util.exportExcel(response, list, "工序数据");
    }

    /**
     * 获取工序详细信息
     */
    @ApiOperation("获取工序详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProProcess:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(proProcessService.findById(id));
    }

    /**
     * 新增工序
     */
    @ApiOperation("新增工序")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProProcess:add')")
    @Log(title = "工序", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody ProProcess proProcess)
    {

        proProcessService.save(proProcess);
        return toAjax(true);
    }

    /**
     * 修改工序
     */
    @ApiOperation("修改工序")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProProcess:edit')")
    @Log(title = "工序", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody ProProcess proProcess)
    {



        return toAjax(proProcessService.update(proProcess));
    }

    /**
     * 删除工序
     */
    @ApiOperation("删除工序")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProProcess:remove')")
    @Log(title = "工序", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(proProcessService.deleteProProcessByIds(ids));
    }
}
