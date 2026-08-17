package com.deer.wcs.base.web;

import com.deer.wcs.base.model.PositionSource;
import com.deer.wcs.base.model.PositionSourceCriteria;
import com.deer.wcs.base.model.PositionSourceDto;
import com.deer.wcs.base.service.PositionSourceService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.system.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 站台资源Controller
 * 
 * @author deer
 * @date 2024-07-09
 */
@Api("站台资源")
@RestController
@RequestMapping("/wcs-base/source")
public class PositionSourceController extends BaseController
{
    @Autowired
    private PositionSourceService positionSourceService;
    @Autowired
    private ISysConfigService sysConfigService;

    /**
     * 查询站台资源列表
     */
    @ApiOperation("查询站台资源列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:source:list')")
    @GetMapping("/list")
    public TableDataInfo list(PositionSourceCriteria Criteria)
    {
        startPage();
        List<PositionSourceDto> list = positionSourceService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出站台资源列表
     */
    @ApiOperation("导出站台资源列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:source:export')")
    @Log(title = "站台资源", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PositionSourceCriteria criteria)
    {
        List<PositionSourceDto> list = positionSourceService.findList(criteria);
        ExcelUtil<PositionSourceDto> util = new ExcelUtil<PositionSourceDto>(PositionSourceDto.class);
        util.exportExcel(response, list, "站台资源数据");
    }

    /**
     * 获取站台资源详细信息
     */
    @ApiOperation("获取站台资源详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:source:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(positionSourceService.findById(id));
    }

    /**
     * 新增站台资源
     */
    @ApiOperation("新增站台资源")
    @PreAuthorize("@ss.hasPermi('wcs-base:source:add')")
    @Log(title = "站台资源", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody PositionSource positionSource)
    {
        positionSource.setDelFlag(0);
        positionSourceService.save(positionSource);
        return toAjax(true);
    }

    /**
     * 修改站台资源
     */
    @ApiOperation("修改站台资源")
    @PreAuthorize("@ss.hasPermi('wcs-base:source:edit')")
    @Log(title = "站台资源", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody PositionSource positionSource)
    {



        return toAjax(positionSourceService.update(positionSource));
    }

    /**
     * 删除站台资源
     */
    @ApiOperation("删除站台资源")
    @PreAuthorize("@ss.hasPermi('wcs-base:source:remove')")
    @Log(title = "站台资源", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        if(Integer.parseInt(sysConfigService.selectConfigByKey("soft_delete"))==0){
            for(Long id:ids){
                PositionSource positionSource = positionSourceService.findById(id);
                if(positionSource!=null){
                    positionSource.setDelFlag(1);
                    positionSourceService.update(positionSource);
                }
            }
            return Result.success();
        }else{
            return toAjax(positionSourceService.deletePositionSourceByIds(ids));
        }
    }
}
