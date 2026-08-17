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
import com.deer.wcs.base.model.LineItem;
import com.deer.wcs.base.model.LineItemDto;
import com.deer.wcs.base.model.LineItemCriteria;
import com.deer.wcs.base.service.LineItemService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 产线物料Controller
 * 
 * @author deer
 * @date 2024-12-21
 */
@Api("产线物料")
@RestController
@RequestMapping("/wcs-base/LineItem")
public class LineItemController extends BaseController
{
    @Autowired
    private LineItemService lineItemService;

    /**
     * 查询产线物料列表
     */
    @ApiOperation("查询产线物料列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:LineItem:list')")
    @GetMapping("/list")
    public TableDataInfo list(LineItemCriteria Criteria)
    {
        startPage();
        List<LineItemDto> list = lineItemService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出产线物料列表
     */
    @ApiOperation("导出产线物料列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:LineItem:export')")
    @Log(title = "产线物料", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LineItemCriteria criteria)
    {
        List<LineItemDto> list = lineItemService.findList(criteria);
        ExcelUtil<LineItemDto> util = new ExcelUtil<LineItemDto>(LineItemDto.class);
        util.exportExcel(response, list, "产线物料数据");
    }

    /**
     * 获取产线物料详细信息
     */
    @ApiOperation("获取产线物料详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:LineItem:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(lineItemService.findById(id));
    }

    /**
     * 新增产线物料
     */
    @ApiOperation("新增产线物料")
    //@PreAuthorize("@ss.hasPermi('wcs-base:LineItem:add')")
    @Log(title = "产线物料", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody LineItem lineItem)
    {

        lineItemService.save(lineItem);
        return toAjax(true);
    }

    /**
     * 修改产线物料
     */
    @ApiOperation("修改产线物料")
    //@PreAuthorize("@ss.hasPermi('wcs-base:LineItem:edit')")
    @Log(title = "产线物料", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody LineItem lineItem)
    {



        return toAjax(lineItemService.update(lineItem));
    }

    /**
     * 删除产线物料
     */
    @ApiOperation("删除产线物料")
    //@PreAuthorize("@ss.hasPermi('wcs-base:LineItem:remove')")
    @Log(title = "产线物料", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Integer[] ids)
    {
        return toAjax(lineItemService.deleteLineItemByIds(ids));
    }
}
