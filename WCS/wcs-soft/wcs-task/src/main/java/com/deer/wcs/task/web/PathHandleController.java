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
import com.deer.wcs.task.model.PathHandle;
import com.deer.wcs.task.model.PathHandleDto;
import com.deer.wcs.task.model.PathHandleCriteria;
import com.deer.wcs.task.service.PathHandleService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 路径方法Controller
 * 
 * @author deer
 * @date 2024-05-10
 */
@Api("路径方法")
@RestController
@RequestMapping("/wcs-task/pathHandle")
public class PathHandleController extends BaseController
{
    @Autowired
    private PathHandleService pathHandleService;

    /**
     * 查询路径方法列表
     */
    @ApiOperation("查询路径方法列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:pathHandle:list')")
    @GetMapping("/list")
    public TableDataInfo list(PathHandleCriteria Criteria)
    {
        startPage();
        List<PathHandleDto> list = pathHandleService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出路径方法列表
     */
    @ApiOperation("导出路径方法列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:pathHandle:export')")
    @Log(title = "路径方法", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PathHandleCriteria criteria)
    {
        List<PathHandleDto> list = pathHandleService.findList(criteria);
        ExcelUtil<PathHandleDto> util = new ExcelUtil<PathHandleDto>(PathHandleDto.class);
        util.exportExcel(response, list, "路径方法数据");
    }

    /**
     * 获取路径方法详细信息
     */
    @ApiOperation("获取路径方法详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-task:pathHandle:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(pathHandleService.findById(id));
    }

    /**
     * 新增路径方法
     */
    @ApiOperation("新增路径方法")
    @PreAuthorize("@ss.hasPermi('wcs-task:pathHandle:add')")
    @Log(title = "路径方法", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody PathHandle pathHandle)
    {
        pathHandle.setCreateTime(DateUtil.getNowDateTimeString());
        pathHandle.setCreateUserId(getUserId());
        pathHandle.setCreateUserName(getUsername());

        pathHandleService.save(pathHandle);
        return toAjax(true);
    }

    /**
     * 修改路径方法
     */
    @ApiOperation("修改路径方法")
    @PreAuthorize("@ss.hasPermi('wcs-task:pathHandle:edit')")
    @Log(title = "路径方法", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody PathHandle pathHandle)
    {

         pathHandle.setUpdateTime(DateUtil.getNowDateTimeString());
         pathHandle.setUpdateUserId(getUserId());
         pathHandle.setUpdateUserName(getUsername());


        return toAjax(pathHandleService.update(pathHandle));
    }

    /**
     * 删除路径方法
     */
    @ApiOperation("删除路径方法")
    @PreAuthorize("@ss.hasPermi('wcs-task:pathHandle:remove')")
    @Log(title = "路径方法", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(pathHandleService.deletePathHandleByIds(ids));
    }
}
