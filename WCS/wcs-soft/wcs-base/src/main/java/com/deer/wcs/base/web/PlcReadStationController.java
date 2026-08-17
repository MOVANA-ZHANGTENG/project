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
import com.deer.wcs.base.model.PlcReadStation;
import com.deer.wcs.base.model.PlcReadStationDto;
import com.deer.wcs.base.model.PlcReadStationCriteria;
import com.deer.wcs.base.service.PlcReadStationService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * plc读取站台信号Controller
 * 
 * @author deer
 * @date 2025-06-04
 */
@Api("plc读取站台信号")
@RestController
@RequestMapping("/wcs-base/PlcReadStation")
public class PlcReadStationController extends BaseController
{
    @Autowired
    private PlcReadStationService plcReadStationService;

    /**
     * 查询plc读取站台信号列表
     */
    @ApiOperation("查询plc读取站台信号列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:PlcReadStation:list')")
    @GetMapping("/list")
    public TableDataInfo list(PlcReadStationCriteria Criteria)
    {
        startPage();
        List<PlcReadStationDto> list = plcReadStationService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出plc读取站台信号列表
     */
    @ApiOperation("导出plc读取站台信号列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:PlcReadStation:export')")
    @Log(title = "plc读取站台信号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PlcReadStationCriteria criteria)
    {
        List<PlcReadStationDto> list = plcReadStationService.findList(criteria);
        ExcelUtil<PlcReadStationDto> util = new ExcelUtil<PlcReadStationDto>(PlcReadStationDto.class);
        util.exportExcel(response, list, "plc读取站台信号数据");
    }

    /**
     * 获取plc读取站台信号详细信息
     */
    @ApiOperation("获取plc读取站台信号详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:PlcReadStation:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(plcReadStationService.findById(id));
    }

    /**
     * 新增plc读取站台信号
     */
    @ApiOperation("新增plc读取站台信号")
    //@PreAuthorize("@ss.hasPermi('wcs-base:PlcReadStation:add')")
    @Log(title = "plc读取站台信号", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody PlcReadStation plcReadStation)
    {

        plcReadStationService.save(plcReadStation);
        return toAjax(true);
    }

    /**
     * 修改plc读取站台信号
     */
    @ApiOperation("修改plc读取站台信号")
    //@PreAuthorize("@ss.hasPermi('wcs-base:PlcReadStation:edit')")
    @Log(title = "plc读取站台信号", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody PlcReadStation plcReadStation)
    {



        return toAjax(plcReadStationService.update(plcReadStation));
    }

    /**
     * 删除plc读取站台信号
     */
    @ApiOperation("删除plc读取站台信号")
    //@PreAuthorize("@ss.hasPermi('wcs-base:PlcReadStation:remove')")
    @Log(title = "plc读取站台信号", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(plcReadStationService.deletePlcReadStationByIds(ids));
    }
}
