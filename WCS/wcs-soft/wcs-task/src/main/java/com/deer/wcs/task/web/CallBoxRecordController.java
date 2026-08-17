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
import com.deer.wcs.task.model.CallBoxRecord;
import com.deer.wcs.task.model.CallBoxRecordDto;
import com.deer.wcs.task.model.CallBoxRecordCriteria;
import com.deer.wcs.task.service.CallBoxRecordService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 呼叫盒记录Controller
 * 
 * @author deer
 * @date 2024-12-02
 */
@Api("呼叫盒记录")
@RestController
@RequestMapping("/wcs-task/CallBoxRecord")
public class CallBoxRecordController extends BaseController
{
    @Autowired
    private CallBoxRecordService callBoxRecordService;

    /**
     * 查询呼叫盒记录列表
     */
    @ApiOperation("查询呼叫盒记录列表")
    //@PreAuthorize("@ss.hasPermi('wcs-task:CallBoxRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(CallBoxRecordCriteria Criteria)
    {
        startPage();
        List<CallBoxRecordDto> list = callBoxRecordService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出呼叫盒记录列表
     */
    @ApiOperation("导出呼叫盒记录列表")
    //@PreAuthorize("@ss.hasPermi('wcs-task:CallBoxRecord:export')")
    @Log(title = "呼叫盒记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CallBoxRecordCriteria criteria)
    {
        List<CallBoxRecordDto> list = callBoxRecordService.findList(criteria);
        ExcelUtil<CallBoxRecordDto> util = new ExcelUtil<CallBoxRecordDto>(CallBoxRecordDto.class);
        util.exportExcel(response, list, "呼叫盒记录数据");
    }

    /**
     * 获取呼叫盒记录详细信息
     */
    @ApiOperation("获取呼叫盒记录详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-task:CallBoxRecord:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(callBoxRecordService.findById(id));
    }

    /**
     * 新增呼叫盒记录
     */
    @ApiOperation("新增呼叫盒记录")
    //@PreAuthorize("@ss.hasPermi('wcs-task:CallBoxRecord:add')")
    @Log(title = "呼叫盒记录", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody CallBoxRecord callBoxRecord)
    {

        callBoxRecordService.save(callBoxRecord);
        return toAjax(true);
    }

    /**
     * 修改呼叫盒记录
     */
    @ApiOperation("修改呼叫盒记录")
    //@PreAuthorize("@ss.hasPermi('wcs-task:CallBoxRecord:edit')")
    @Log(title = "呼叫盒记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody CallBoxRecord callBoxRecord)
    {



        return toAjax(callBoxRecordService.update(callBoxRecord));
    }

    /**
     * 删除呼叫盒记录
     */
    @ApiOperation("删除呼叫盒记录")
    //@PreAuthorize("@ss.hasPermi('wcs-task:CallBoxRecord:remove')")
    @Log(title = "呼叫盒记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(callBoxRecordService.deleteCallBoxRecordByIds(ids));
    }
}
