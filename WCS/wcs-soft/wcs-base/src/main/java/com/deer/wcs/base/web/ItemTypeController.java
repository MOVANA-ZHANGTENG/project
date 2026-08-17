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
import com.deer.wcs.base.model.ItemType;
import com.deer.wcs.base.model.ItemTypeDto;
import com.deer.wcs.base.model.ItemTypeCriteria;
import com.deer.wcs.base.service.ItemTypeService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 物料类型Controller
 * 
 * @author deer
 * @date 2025-09-22
 */
@Api("物料类型")
@RestController
@RequestMapping("/wcs-base/ItemType")
public class ItemTypeController extends BaseController
{
    @Autowired
    private ItemTypeService itemTypeService;

    /**
     * 查询物料类型列表
     */
    @ApiOperation("查询物料类型列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ItemType:list')")
    @GetMapping("/list")
    public TableDataInfo list(ItemTypeCriteria Criteria)
    {
        startPage();
        List<ItemTypeDto> list = itemTypeService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出物料类型列表
     */
    @ApiOperation("导出物料类型列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ItemType:export')")
    @Log(title = "物料类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ItemTypeCriteria criteria)
    {
        List<ItemTypeDto> list = itemTypeService.findList(criteria);
        ExcelUtil<ItemTypeDto> util = new ExcelUtil<ItemTypeDto>(ItemTypeDto.class);
        util.exportExcel(response, list, "物料类型数据");
    }

    /**
     * 获取物料类型详细信息
     */
    @ApiOperation("获取物料类型详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ItemType:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(itemTypeService.findById(id));
    }

    /**
     * 新增物料类型
     */
    @ApiOperation("新增物料类型")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ItemType:add')")
    @Log(title = "物料类型", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody ItemType itemType)
    {
        itemType.setCreateTime(DateUtil.getNowDateTimeString());
        itemType.setCreateUserId(getUserId());
        itemType.setCreateUserName(getUsername());

        itemTypeService.save(itemType);
        return toAjax(true);
    }

    /**
     * 修改物料类型
     */
    @ApiOperation("修改物料类型")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ItemType:edit')")
    @Log(title = "物料类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody ItemType itemType)
    {



        return toAjax(itemTypeService.update(itemType));
    }

    /**
     * 删除物料类型
     */
    @ApiOperation("删除物料类型")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ItemType:remove')")
    @Log(title = "物料类型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(itemTypeService.deleteItemTypeByIds(ids));
    }
}
