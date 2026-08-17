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
import com.deer.wcs.base.model.CellRecord;
import com.deer.wcs.base.model.CellRecordDto;
import com.deer.wcs.base.model.CellRecordCriteria;
import com.deer.wcs.base.service.CellRecordService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 库位日志记录Controller
 * 
 * @author deer
 * @date 2025-11-04
 */
@Api("库位日志记录")
@RestController
@RequestMapping("/wcs-base/CellRecord")
public class CellRecordController extends BaseController
{
    @Autowired
    private CellRecordService cellRecordService;

    /**
     * 查询库位日志记录列表
     */
    @ApiOperation("查询库位日志记录列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:CellRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(CellRecordCriteria Criteria)
    {
        startPage();
        List<CellRecordDto> list = cellRecordService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出库位日志记录列表
     */
    @ApiOperation("导出库位日志记录列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:CellRecord:export')")
    @Log(title = "库位日志记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CellRecordCriteria criteria)
    {
        List<CellRecordDto> list = cellRecordService.findList(criteria);
        ExcelUtil<CellRecordDto> util = new ExcelUtil<CellRecordDto>(CellRecordDto.class);
        util.exportExcel(response, list, "库位日志记录数据");
    }

    /**
     * 获取库位日志记录详细信息
     */
    @ApiOperation("获取库位日志记录详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:CellRecord:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(cellRecordService.findById(id));
    }

    /**
     * 新增库位日志记录
     */
    @ApiOperation("新增库位日志记录")
    //@PreAuthorize("@ss.hasPermi('wcs-base:CellRecord:add')")
    @Log(title = "库位日志记录", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody CellRecord cellRecord)
    {
        cellRecord.setCreateTime(DateUtil.getNowDateTimeString());

        cellRecordService.save(cellRecord);
        return toAjax(true);
    }

    /**
     * 修改库位日志记录
     */
    @ApiOperation("修改库位日志记录")
    //@PreAuthorize("@ss.hasPermi('wcs-base:CellRecord:edit')")
    @Log(title = "库位日志记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody CellRecord cellRecord)
    {



        return toAjax(cellRecordService.update(cellRecord));
    }

    /**
     * 删除库位日志记录
     */
    @ApiOperation("删除库位日志记录")
    //@PreAuthorize("@ss.hasPermi('wcs-base:CellRecord:remove')")
    @Log(title = "库位日志记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(cellRecordService.deleteCellRecordByIds(ids));
    }
}
