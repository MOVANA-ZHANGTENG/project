package com.deer.wcs.base.web;

import com.deer.wcs.base.model.Inventory;
import com.deer.wcs.base.model.InventoryCriteria;
import com.deer.wcs.base.model.InventoryDto;
import com.deer.wcs.base.service.InventoryService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 库存信息Controller
 * 
 * @author deer
 * @date 2024-08-22
 */
@Api("库存信息")
@RestController
@RequestMapping("/wcs-base/inventory")
public class InventoryController extends BaseController
{
    @Autowired
    private InventoryService inventoryService;

    /**
     * 查询库存信息列表
     */
    @ApiOperation("查询库存信息列表")
//    @PreAuthorize("@ss.hasPermi('wcs-base:inventory:list')")
    @GetMapping("/list")
    public TableDataInfo list(InventoryCriteria Criteria)
    {
        startPage();
        List<InventoryDto> list = inventoryService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出库存信息列表
     */
    @ApiOperation("导出库存信息列表")
//    @PreAuthorize("@ss.hasPermi('wcs-base:inventory:export')")
    @Log(title = "库存信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InventoryCriteria criteria)
    {
        List<InventoryDto> list = inventoryService.findList(criteria);
        ExcelUtil<InventoryDto> util = new ExcelUtil<InventoryDto>(InventoryDto.class);
        util.exportExcel(response, list, "库存信息数据");
    }

    /**
     * 获取库存信息详细信息
     */
    @ApiOperation("获取库存信息详细信息")
//    @PreAuthorize("@ss.hasPermi('wcs-base:inventory:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(inventoryService.findById(id));
    }

    /**
     * 新增库存信息
     */
    @ApiOperation("新增库存信息")
//    @PreAuthorize("@ss.hasPermi('wcs-base:inventory:add')")
    @Log(title = "库存信息", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody Inventory inventory)
    {

        inventoryService.save(inventory);
        return toAjax(true);
    }

    /**
     * 修改库存信息
     */
    @ApiOperation("修改库存信息")
//    @PreAuthorize("@ss.hasPermi('wcs-base:inventory:edit')")
    @Log(title = "库存信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody Inventory inventory)
    {



        return toAjax(inventoryService.update(inventory));
    }

    /**
     * 删除库存信息
     */
    @ApiOperation("删除库存信息")
//    @PreAuthorize("@ss.hasPermi('wcs-base:inventory:remove')")
    @Log(title = "库存信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(inventoryService.deleteInventoryByIds(ids));
    }
}
