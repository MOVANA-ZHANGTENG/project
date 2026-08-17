package com.deer.wcs.base.web;

import com.deer.wcs.base.model.PalletType;
import com.deer.wcs.base.model.PalletTypeCriteria;
import com.deer.wcs.base.model.PalletTypeDto;
import com.deer.wcs.base.service.PalletTypeService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 托盘类型Controller
 * 
 * @author deer
 * @date 2024-05-29
 */
@Api("托盘类型")
@RestController
@RequestMapping("/system/palletType")
public class PalletTypeController extends BaseController
{
    @Autowired
    private PalletTypeService palletTypeService;

    /**
     * 查询托盘类型列表
     */
    @ApiOperation("查询托盘类型列表")
    @PreAuthorize("@ss.hasPermi('system:palletType:list')")
    @GetMapping("/list")
    public TableDataInfo list(PalletTypeCriteria Criteria)
    {
        startPage();
        List<PalletTypeDto> list = palletTypeService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出托盘类型列表
     */
    @ApiOperation("导出托盘类型列表")
    @PreAuthorize("@ss.hasPermi('system:palletType:export')")
    @Log(title = "托盘类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PalletTypeCriteria criteria)
    {
        List<PalletTypeDto> list = palletTypeService.findList(criteria);
        ExcelUtil<PalletTypeDto> util = new ExcelUtil<PalletTypeDto>(PalletTypeDto.class);
        util.exportExcel(response, list, "托盘类型数据");
    }

    /**
     * 获取托盘类型详细信息
     */
    @ApiOperation("获取托盘类型详细信息")
    @PreAuthorize("@ss.hasPermi('system:palletType:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(palletTypeService.findById(id));
    }

    /**
     * 新增托盘类型
     */
    @ApiOperation("新增托盘类型")
    @PreAuthorize("@ss.hasPermi('system:palletType:add')")
    @Log(title = "托盘类型", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody PalletType palletType)
    {

        Condition repeatCode = new Condition(PalletType.class);
        repeatCode.createCriteria().andEqualTo("code",palletType.getCode());
        List<PalletType> repeatCodes = palletTypeService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            return error("托盘类型编码重复");
        }

        Condition repeatName = new Condition(PalletType.class);
        repeatName.createCriteria().andEqualTo("name",palletType.getName());
        List<PalletType> repeatNames = palletTypeService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            return error("托盘类型名称重复");
        }
        palletTypeService.save(palletType);
        return toAjax(true);
    }

    /**
     * 修改托盘类型
     */
    @ApiOperation("修改托盘类型")
    @PreAuthorize("@ss.hasPermi('system:palletType:edit')")
    @Log(title = "托盘类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody PalletType palletType)
    {

        Condition repeatCode = new Condition(PalletType.class);
        repeatCode.createCriteria().andEqualTo("code",palletType.getCode());
        List<PalletType> repeatCodes = palletTypeService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            if(repeatCodes.size()==1 && repeatCodes.get(0).getId().longValue() != palletType.getId().longValue()){
                return error("托盘类型编码重复");
            }else if(repeatCodes.size()>1){
                return error("托盘类型编码重复");
            }
        }

        Condition repeatName = new Condition(PalletType.class);
        repeatName.createCriteria().andEqualTo("name",palletType.getName());
        List<PalletType> repeatNames = palletTypeService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            if(repeatNames.size()==1 && repeatNames.get(0).getId().longValue() != palletType.getId().longValue()){
                return error("托盘类型名称重复");
            }else if(repeatNames.size()>1){
                return error("托盘类型名称重复");
            }
        }
        return toAjax(palletTypeService.update(palletType));
    }

    /**
     * 删除托盘类型
     */
    @ApiOperation("删除托盘类型")
    @PreAuthorize("@ss.hasPermi('system:palletType:remove')")
    @Log(title = "托盘类型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(palletTypeService.deletePalletTypeByIds(ids));
    }
}
