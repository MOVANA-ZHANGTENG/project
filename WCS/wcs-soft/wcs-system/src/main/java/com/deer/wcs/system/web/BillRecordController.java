package com.deer.wcs.system.web;

import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.system.model.BillRecord;
import com.deer.wcs.system.model.BillRecordCriteria;
import com.deer.wcs.system.model.BillRecordDto;
import com.deer.wcs.system.service.BillRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 单据记录Controller
 * 
 * @author deer
 * @date 2023-10-13
 */
@Api("单据记录")
@RestController
@Transactional
@RequestMapping("/bill_record")
public class BillRecordController extends BaseController
{
    @Autowired
    private BillRecordService billRecordService;

    /**
     * 查询单据记录列表
     */
    @ApiOperation("查询单据记录列表")
    @PreAuthorize("@ss.hasPermi('wms-inventory:bill_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(BillRecordCriteria Criteria)
    {
        startPage();
        List<BillRecordDto> list = billRecordService.findList(Criteria);
        return getDataTable(list);
    }


    @GetMapping("/findByBillNo")
    public Result findByBillNo(String billNo)
    {
        List<BillRecord> list = billRecordService.findByBillNo(billNo);
        return success(list);
    }

    @ApiOperation("查询单据数量")
    @PreAuthorize("@ss.hasPermi('wms-inventory:bill_record:list')")
    @GetMapping("/count")
    public TableDataInfo findCount(BillRecordCriteria Criteria)
    {
        startPage();
        List<BillRecordDto> list = billRecordService.findCount(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出单据记录列表
     */
    @ApiOperation("导出单据记录列表")
    @PreAuthorize("@ss.hasPermi('wms-inventory:bill_record:export')")
    @Log(title = "单据记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BillRecordCriteria criteria)
    {
        List<BillRecordDto> list = billRecordService.findList(criteria);
        ExcelUtil<BillRecordDto> util = new ExcelUtil<BillRecordDto>(BillRecordDto.class);
        util.exportExcel(response, list, "单据记录数据");
    }

    /**
     * 获取单据记录详细信息
     */
    @ApiOperation("获取单据记录详细信息")
    @PreAuthorize("@ss.hasPermi('wms-inventory:bill_record:query')")
    @GetMapping(value = "/{billRecordId}")
    public Result getInfo(@PathVariable("billRecordId") Integer billRecordId)
    {
        return success(billRecordService.findById(billRecordId));
    }

    /**
     * 新增单据记录
     */
    @ApiOperation("新增单据记录")
    @PreAuthorize("@ss.hasPermi('wms-inventory:bill_record:add')")
    @Log(title = "单据记录", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody BillRecord billRecord)
    {
        billRecord.setCreateTime(DateUtil.getNowDateTimeString());
        billRecord.setCreateUserId(getUserId());
        billRecord.setCreateUserName(getUsername());

        billRecordService.save(billRecord);
        return toAjax(true);
    }

    /**
     * 修改单据记录
     */
    @ApiOperation("修改单据记录")
    @PreAuthorize("@ss.hasPermi('wms-inventory:bill_record:edit')")
    @Log(title = "单据记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody BillRecord billRecord)
    {



        return toAjax(billRecordService.update(billRecord));
    }

    /**
     * 删除单据记录
     */
    @ApiOperation("删除单据记录")
    @PreAuthorize("@ss.hasPermi('wms-inventory:bill_record:remove')")
    @Log(title = "单据记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{billRecordIds}")
    public Result remove(@PathVariable Integer[] billRecordIds)
    {
        return toAjax(billRecordService.deleteBillRecordByBillRecordIds(billRecordIds));
    }
}
