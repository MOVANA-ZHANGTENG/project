package com.deer.wcs.base.web;

import com.deer.wcs.base.model.PositionCondition;
import com.deer.wcs.base.model.PositionConditionCriteria;
import com.deer.wcs.base.model.PositionConditionDto;
import com.deer.wcs.base.model.WareInfo;
import com.deer.wcs.base.service.PositionConditionService;
import com.deer.wcs.base.service.WareInfoService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 路径Controller
 * 
 * @author deer
 * @date 2024-04-28
 */
@Api("路径")
@RestController
@RequestMapping("/wcs-base/PositionCondition")
public class PositionConditionController extends BaseController
{
    @Autowired
    private PositionConditionService positionConditionService;
    @Autowired
    private WareInfoService wareInfoService;

    /**
     * 查询路径列表
     */
    @ApiOperation("查询路径列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionCondition:list')")
    @GetMapping("/list")
    public TableDataInfo list(PositionConditionCriteria Criteria)
    {

        Condition condition = new Condition(WareInfo.class);
        condition.createCriteria().andEqualTo("isDelete",0);
        List<WareInfo> wareInfoList = wareInfoService.findByCondition(condition);
        List<String> wareCodes = new ArrayList<>();
        for(WareInfo wareInfo:wareInfoList){
            wareCodes.add(wareInfo.getCode());
        }
        startPage();
        List<PositionConditionDto> list = positionConditionService.findList(Criteria);
        list = list.stream().filter(info->wareCodes.contains(info.getWareCode())).collect(Collectors.toList());
        return getDataTable(list);
    }

    /**
     * 导出路径列表
     */
    @ApiOperation("导出路径列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionCondition:export')")
    @Log(title = "路径", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PositionConditionCriteria criteria)
    {
        List<PositionConditionDto> list = positionConditionService.findList(criteria);
        ExcelUtil<PositionConditionDto> util = new ExcelUtil<PositionConditionDto>(PositionConditionDto.class);
        util.exportExcel(response, list, "路径数据");
    }

    /**
     * 获取路径详细信息
     */
    @ApiOperation("获取路径详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionCondition:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(positionConditionService.findById(id));
    }

    /**
     * 新增路径
     */
    @ApiOperation("新增路径")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionCondition:add')")
    @Log(title = "路径", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody PositionCondition positionCondition)
    {

        positionConditionService.save(positionCondition);
        return toAjax(true);
    }

    /**
     * 修改路径
     */
    @ApiOperation("修改路径")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionCondition:edit')")
    @Log(title = "路径", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody PositionCondition positionCondition)
    {



        return toAjax(positionConditionService.update(positionCondition));
    }

    /**
     * 删除路径
     */
    @ApiOperation("删除路径")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionCondition:remove')")
    @Log(title = "路径", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(positionConditionService.deletePositionConditionByIds(ids));
    }
}
