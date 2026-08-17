package com.deer.wcs.rcs.web;

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
import com.deer.wcs.rcs.model.RcsCarTask;
import com.deer.wcs.rcs.model.RcsCarTaskDto;
import com.deer.wcs.rcs.model.RcsCarTaskCriteria;
import com.deer.wcs.rcs.service.RcsCarTaskService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 小车任务主体Controller
 * 
 * @author deer
 * @date 2025-07-07
 */
@Api("小车任务主体")
@RestController
@RequestMapping("/wcs-rcs/RcsCarTask")
public class RcsCarTaskController extends BaseController
{
    @Autowired
    private RcsCarTaskService rcsCarTaskService;

    /**
     * 查询小车任务主体列表
     */
    @ApiOperation("查询小车任务主体列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(RcsCarTaskCriteria Criteria)
    {
        startPage();
        List<RcsCarTaskDto> list = rcsCarTaskService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出小车任务主体列表
     */
    @ApiOperation("导出小车任务主体列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarTask:export')")
    @Log(title = "小车任务主体", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RcsCarTaskCriteria criteria)
    {
        List<RcsCarTaskDto> list = rcsCarTaskService.findList(criteria);
        ExcelUtil<RcsCarTaskDto> util = new ExcelUtil<RcsCarTaskDto>(RcsCarTaskDto.class);
        util.exportExcel(response, list, "小车任务主体数据");
    }

    /**
     * 获取小车任务主体详细信息
     */
    @ApiOperation("获取小车任务主体详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarTask:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(rcsCarTaskService.findById(id));
    }

    /**
     * 新增小车任务主体
     */
    @ApiOperation("新增小车任务主体")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarTask:add')")
    @Log(title = "小车任务主体", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody RcsCarTask rcsCarTask)
    {
        rcsCarTask.setCreateTime(DateUtil.getNowDateTimeString());
        rcsCarTask.setCreateUserName(getUsername());

        rcsCarTaskService.save(rcsCarTask);
        return toAjax(true);
    }

    /**
     * 修改小车任务主体
     */
    @ApiOperation("修改小车任务主体")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarTask:edit')")
    @Log(title = "小车任务主体", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody RcsCarTask rcsCarTask)
    {



        return toAjax(rcsCarTaskService.update(rcsCarTask));
    }

    /**
     * 删除小车任务主体
     */
    @ApiOperation("删除小车任务主体")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarTask:remove')")
    @Log(title = "小车任务主体", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(rcsCarTaskService.deleteRcsCarTaskByIds(ids));
    }
}
