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
import com.deer.wcs.base.model.CellLink;
import com.deer.wcs.base.model.CellLinkDto;
import com.deer.wcs.base.model.CellLinkCriteria;
import com.deer.wcs.base.service.CellLinkService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import tk.mybatis.mapper.entity.Condition;

/**
 * 库位邻接关系，存储四向车调度系统的节点联通关系Controller
 * 
 * @author deer
 * @date 2025-10-14
 */
@Api("库位邻接关系，存储四向车调度系统的节点联通关系")
@RestController
@RequestMapping("/wcs-base/CellLink")
public class CellLinkController extends BaseController
{
    @Autowired
    private CellLinkService cellLinkService;

    /**
     * 查询库位邻接关系，存储四向车调度系统的节点联通关系列表
     */
    @ApiOperation("查询库位邻接关系，存储四向车调度系统的节点联通关系列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:CellLink:list')")
    @GetMapping("/list")
    public TableDataInfo list(CellLinkCriteria Criteria)
    {
        startPage();
        List<CellLinkDto> list = cellLinkService.findList(Criteria);
        return getDataTable(list);
    }
    @GetMapping("/deleteByFromCellIdAndToCellIdAndWareCode")
    public Result deleteByFromCellIdAndToCellIdAndWareCode(CellLink cellLink)
    {
        Condition condition = new Condition(CellLink.class);
        condition.createCriteria().andEqualTo("fromCellId", cellLink.getFromCellId())
                .andEqualTo("toCellId", cellLink.getToCellId())
                .andEqualTo("wareCode", cellLink.getWareCode());

        List<CellLink> list = cellLinkService.findByCondition(condition);
       for (CellLink cellLink1 : list) {
            cellLinkService.deleteById(cellLink1.getId());
        }

        return success();
    }

    /**
     * 导出库位邻接关系，存储四向车调度系统的节点联通关系列表
     */
    @ApiOperation("导出库位邻接关系，存储四向车调度系统的节点联通关系列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:CellLink:export')")
    @Log(title = "库位邻接关系，存储四向车调度系统的节点联通关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CellLinkCriteria criteria)
    {
        List<CellLinkDto> list = cellLinkService.findList(criteria);
        ExcelUtil<CellLinkDto> util = new ExcelUtil<CellLinkDto>(CellLinkDto.class);
        util.exportExcel(response, list, "库位邻接关系，存储四向车调度系统的节点联通关系数据");
    }

    /**
     * 获取库位邻接关系，存储四向车调度系统的节点联通关系详细信息
     */
    @ApiOperation("获取库位邻接关系，存储四向车调度系统的节点联通关系详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:CellLink:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(cellLinkService.findById(id));
    }

    /**
     * 新增库位邻接关系，存储四向车调度系统的节点联通关系
     */
    @ApiOperation("新增库位邻接关系，存储四向车调度系统的节点联通关系")
    //@PreAuthorize("@ss.hasPermi('wcs-base:CellLink:add')")
    @Log(title = "库位邻接关系，存储四向车调度系统的节点联通关系", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody CellLink cellLink)
    {
        cellLink.setCreateTime(DateUtil.getNowDateTimeString());

        cellLinkService.save(cellLink);
        return toAjax(true);
    }

    /**
     * 修改库位邻接关系，存储四向车调度系统的节点联通关系
     */
    @ApiOperation("修改库位邻接关系，存储四向车调度系统的节点联通关系")
    //@PreAuthorize("@ss.hasPermi('wcs-base:CellLink:edit')")
    @Log(title = "库位邻接关系，存储四向车调度系统的节点联通关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody CellLink cellLink)
    {



        return toAjax(cellLinkService.update(cellLink));
    }




    /**
     * 删除库位邻接关系，存储四向车调度系统的节点联通关系
     */
    @ApiOperation("删除库位邻接关系，存储四向车调度系统的节点联通关系")
    //@PreAuthorize("@ss.hasPermi('wcs-base:CellLink:remove')")
    @Log(title = "库位邻接关系，存储四向车调度系统的节点联通关系", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(cellLinkService.deleteCellLinkByIds(ids));
    }
}
