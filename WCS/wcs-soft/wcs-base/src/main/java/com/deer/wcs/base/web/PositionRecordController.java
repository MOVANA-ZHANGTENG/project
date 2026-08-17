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
import com.deer.wcs.base.model.PositionRecord;
import com.deer.wcs.base.model.PositionRecordDto;
import com.deer.wcs.base.model.PositionRecordCriteria;
import com.deer.wcs.base.service.PositionRecordService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 站台日志Controller
 * 
 * @author deer
 * @date 2025-04-02
 */
@Api("站台日志")
@RestController
@RequestMapping("/wcs-base/PositionRecord")
public class PositionRecordController extends BaseController
{
    @Autowired
    private PositionRecordService positionRecordService;

    /**
     * 查询站台日志列表
     */
    @ApiOperation("查询站台日志列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:PositionRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(PositionRecordCriteria Criteria)
    {
        startPage();
        List<PositionRecordDto> list = positionRecordService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出站台日志列表
     */
    @ApiOperation("导出站台日志列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:PositionRecord:export')")
    @Log(title = "站台日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PositionRecordCriteria criteria)
    {
        List<PositionRecordDto> list = positionRecordService.findList(criteria);
        ExcelUtil<PositionRecordDto> util = new ExcelUtil<PositionRecordDto>(PositionRecordDto.class);
        util.exportExcel(response, list, "站台日志数据");
    }

    /**
     * 获取站台日志详细信息
     */
    @ApiOperation("获取站台日志详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:PositionRecord:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(positionRecordService.findById(id));
    }

    /**
     * 新增站台日志
     */
    @ApiOperation("新增站台日志")
    //@PreAuthorize("@ss.hasPermi('wcs-base:PositionRecord:add')")
    @Log(title = "站台日志", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody PositionRecord positionRecord)
    {
        positionRecord.setCreateTime(DateUtil.getNowDateTimeString());

        positionRecordService.save(positionRecord);
        return toAjax(true);
    }

    /**
     * 修改站台日志
     */
    @ApiOperation("修改站台日志")
    //@PreAuthorize("@ss.hasPermi('wcs-base:PositionRecord:edit')")
    @Log(title = "站台日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody PositionRecord positionRecord)
    {



        return toAjax(positionRecordService.update(positionRecord));
    }

    /**
     * 删除站台日志
     */
    @ApiOperation("删除站台日志")
    //@PreAuthorize("@ss.hasPermi('wcs-base:PositionRecord:remove')")
    @Log(title = "站台日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(positionRecordService.deletePositionRecordByIds(ids));
    }
}
