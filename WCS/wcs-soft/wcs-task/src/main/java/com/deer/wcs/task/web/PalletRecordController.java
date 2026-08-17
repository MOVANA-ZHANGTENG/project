package com.deer.wcs.task.web;

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
import com.deer.wcs.task.model.PalletRecord;
import com.deer.wcs.task.model.PalletRecordDto;
import com.deer.wcs.task.model.PalletRecordCriteria;
import com.deer.wcs.task.service.PalletRecordService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 托盘记录Controller
 * 
 * @author deer
 * @date 2025-07-23
 */
@Api("托盘记录")
@RestController
@RequestMapping("/wcs-task/PalletRecord")
public class PalletRecordController extends BaseController
{
    @Autowired
    private PalletRecordService palletRecordService;

    /**
     * 查询托盘记录列表
     */
    @ApiOperation("查询托盘记录列表")
    //@PreAuthorize("@ss.hasPermi('wcs-task:PalletRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(PalletRecordCriteria Criteria)
    {
        startPage();
        List<PalletRecordDto> list = palletRecordService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出托盘记录列表
     */
    @ApiOperation("导出托盘记录列表")
    //@PreAuthorize("@ss.hasPermi('wcs-task:PalletRecord:export')")
    @Log(title = "托盘记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PalletRecordCriteria criteria)
    {
        List<PalletRecordDto> list = palletRecordService.findList(criteria);
        ExcelUtil<PalletRecordDto> util = new ExcelUtil<PalletRecordDto>(PalletRecordDto.class);
        util.exportExcel(response, list, "托盘记录数据");
    }

    /**
     * 获取托盘记录详细信息
     */
    @ApiOperation("获取托盘记录详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-task:PalletRecord:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(palletRecordService.findById(id));
    }

    /**
     * 新增托盘记录
     */
    @ApiOperation("新增托盘记录")
    //@PreAuthorize("@ss.hasPermi('wcs-task:PalletRecord:add')")
    @Log(title = "托盘记录", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody PalletRecord palletRecord)
    {
        palletRecord.setCreateTime(DateUtil.getNowDateTimeString());

        palletRecordService.save(palletRecord);
        return toAjax(true);
    }

    /**
     * 修改托盘记录
     */
    @ApiOperation("修改托盘记录")
    //@PreAuthorize("@ss.hasPermi('wcs-task:PalletRecord:edit')")
    @Log(title = "托盘记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody PalletRecord palletRecord)
    {



        return toAjax(palletRecordService.update(palletRecord));
    }

    /**
     * 删除托盘记录
     */
    @ApiOperation("删除托盘记录")
    //@PreAuthorize("@ss.hasPermi('wcs-task:PalletRecord:remove')")
    @Log(title = "托盘记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(palletRecordService.deletePalletRecordByIds(ids));
    }
}
