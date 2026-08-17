package com.deer.wcs.base.web;

import com.deer.wcs.base.model.InCellTacics;
import com.deer.wcs.base.model.InCellTacicsCriteria;
import com.deer.wcs.base.model.InCellTacicsDto;
import com.deer.wcs.base.service.InCellTacicsService;
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
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 策略配置Controller
 * 
 * @author deer
 * @date 2024-09-09
 */
@Api("策略配置")
@RestController
@RequestMapping("/wcs-base/tacics")
public class InCellTacicsController extends BaseController
{
    @Autowired
    private InCellTacicsService inCellTacicsService;

    /**
     * 查询策略配置列表
     */
    @ApiOperation("查询策略配置列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:tacics:list')")
    @GetMapping("/list")
    public TableDataInfo list(InCellTacicsCriteria Criteria)
    {
        startPage();
        List<InCellTacicsDto> list = inCellTacicsService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出策略配置列表
     */
    @ApiOperation("导出策略配置列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:tacics:export')")
    @Log(title = "策略配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InCellTacicsCriteria criteria)
    {
        List<InCellTacicsDto> list = inCellTacicsService.findList(criteria);
        ExcelUtil<InCellTacicsDto> util = new ExcelUtil<InCellTacicsDto>(InCellTacicsDto.class);
        util.exportExcel(response, list, "策略配置数据");
    }

    /**
     * 获取策略配置详细信息
     */
    @ApiOperation("获取策略配置详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:tacics:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(inCellTacicsService.findById(id));
    }

    /**
     * 新增策略配置
     */
    @ApiOperation("新增策略配置")
    //@PreAuthorize("@ss.hasPermi('wcs-base:tacics:add')")
    @Log(title = "策略配置", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody InCellTacics inCellTacics)
    {
        if(inCellTacics.getCode()==null||inCellTacics.getCode().equals("")){
            return error("策略配置编码不能为空");
        }
        if(inCellTacics.getName()==null||inCellTacics.getName().equals("")){
            return error("策略配置名称不能为空");
        }
        Condition repeatCode = new Condition(InCellTacics.class);
        repeatCode.createCriteria().andEqualTo("code",inCellTacics.getCode());
        List<InCellTacics> repeatCodes = inCellTacicsService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            return error("策略配置编码重复");
        }

        Condition repeatName = new Condition(InCellTacics.class);
        repeatName.createCriteria().andEqualTo("name",inCellTacics.getName());
        List<InCellTacics> repeatNames = inCellTacicsService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            return error("策略配置名称重复");
        }
        inCellTacics.setIsSys(1);

        inCellTacicsService.save(inCellTacics);
        return toAjax(true);
    }

    /**
     * 修改策略配置
     */
    @ApiOperation("修改策略配置")
    //@PreAuthorize("@ss.hasPermi('wcs-base:tacics:edit')")
    @Log(title = "策略配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody InCellTacics inCellTacics)
    {
        if(inCellTacics.getCode()==null||inCellTacics.getCode().equals("")){
            return error("策略配置编码不能为空");
        }
        if(inCellTacics.getName()==null||inCellTacics.getName().equals("")){
            return error("策略配置名称不能为空");
        }
        Condition repeatCode = new Condition(InCellTacics.class);
        repeatCode.createCriteria().andEqualTo("code",inCellTacics.getCode());
        List<InCellTacics> repeatCodes = inCellTacicsService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            if(repeatCodes.size()==1 && repeatCodes.get(0).getId().longValue() != inCellTacics.getId().longValue()){
                return error("策略配置编码重复");
            }else if(repeatCodes.size()>1){
                return error("策略配置编码重复");
            }
        }

        Condition repeatName = new Condition(InCellTacics.class);
        repeatName.createCriteria().andEqualTo("name",inCellTacics.getName());
        List<InCellTacics> repeatNames = inCellTacicsService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            if(repeatNames.size()==1 && repeatNames.get(0).getId().longValue() != inCellTacics.getId().longValue()){
                return error("策略配置名称重复");
            }else if(repeatNames.size()>1){
                return error("策略配置名称重复");
            }
        }

        return toAjax(inCellTacicsService.update(inCellTacics));
    }

    /**
     * 删除策略配置
     */
    @ApiOperation("删除策略配置")
    //@PreAuthorize("@ss.hasPermi('wcs-base:tacics:remove')")
    @Log(title = "策略配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        boolean flag = false;
        List<InCellTacics> list = new ArrayList<>();
        for(Long id:ids){
            InCellTacics inCellTacics = inCellTacicsService.findById(id);
            if(inCellTacics.getIsSys()!=0){
                list.add(inCellTacics);
            }else{
                flag = true;
            }
        }
        for(InCellTacics tacics : list){
            inCellTacicsService.deleteInCellTacicsById(tacics.getId());
        }
        if(flag){
            return error("系统内置配置无法删除，只能修改");
        }
        return success();
    }
}
