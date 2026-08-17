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
import com.deer.wcs.base.model.ProductInfo;
import com.deer.wcs.base.model.ProductInfoDto;
import com.deer.wcs.base.model.ProductInfoCriteria;
import com.deer.wcs.base.service.ProductInfoService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 产品Controller
 * 
 * @author deer
 * @date 2024-12-25
 */
@Api("产品")
@RestController
@RequestMapping("/wcs-base/ProductInfo")
public class ProductInfoController extends BaseController
{
    @Autowired
    private ProductInfoService productInfoService;

    /**
     * 查询产品列表
     */
    @ApiOperation("查询产品列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProductInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProductInfoCriteria Criteria)
    {
        startPage();
        List<ProductInfoDto> list = productInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出产品列表
     */
    @ApiOperation("导出产品列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProductInfo:export')")
    @Log(title = "产品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProductInfoCriteria criteria)
    {
        List<ProductInfoDto> list = productInfoService.findList(criteria);
        ExcelUtil<ProductInfoDto> util = new ExcelUtil<ProductInfoDto>(ProductInfoDto.class);
        util.exportExcel(response, list, "产品数据");
    }

    /**
     * 获取产品详细信息
     */
    @ApiOperation("获取产品详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProductInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(productInfoService.findById(id));
    }

    /**
     * 新增产品
     */
    @ApiOperation("新增产品")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProductInfo:add')")
    @Log(title = "产品", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody ProductInfo productInfo)
    {

        productInfoService.save(productInfo);
        return toAjax(true);
    }

    /**
     * 修改产品
     */
    @ApiOperation("修改产品")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProductInfo:edit')")
    @Log(title = "产品", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody ProductInfo productInfo)
    {



        return toAjax(productInfoService.update(productInfo));
    }

    /**
     * 删除产品
     */
    @ApiOperation("删除产品")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProductInfo:remove')")
    @Log(title = "产品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(productInfoService.deleteProductInfoByIds(ids));
    }
}
