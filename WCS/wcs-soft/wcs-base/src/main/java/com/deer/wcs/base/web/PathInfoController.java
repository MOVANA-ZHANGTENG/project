package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.LineInfoService;
import com.deer.wcs.base.service.PalletInfoService;
import com.deer.wcs.base.service.PathInfoService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
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
 * 执行路径Controller
 * 
 * @author deer
 * @date 2024-05-10
 */
@Api("执行路径")
@RestController
@RequestMapping("/wcs-base/pathInfo")
public class PathInfoController extends BaseController
{
    @Autowired
    private PathInfoService pathInfoService;




    /**
     * 查询执行路径列表
     */
    @ApiOperation("查询执行路径列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:pathInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(PathInfoCriteria Criteria)
    {
        startPage();
        List<PathInfoDto> list = pathInfoService.findList(Criteria);
        return getDataTable(list);
    }
    /**
     * 根据当前jobId查询路径
     */
    @ApiOperation("根据当前jobId查询路径")
//    @PreAuthorize("@ss.hasPermi('wcs-task:job:list')")
    @GetMapping("/findPathListByJobId")
    public Result findPathListByJobId(PathInfoCriteria Criteria) {
        return Result.success(pathInfoService.findPathListByJobId(Criteria));
    }
    /**
     * 根据历史jobId查询路径
     */
    @ApiOperation("根据历史jobId查询路径")
//    @PreAuthorize("@ss.hasPermi('wcs-task:job:list')")
    @GetMapping("/findPathHisListByJobId")
    public Result findPathHisListByJobId(PathInfoCriteria Criteria) {
        return Result.success(pathInfoService.findPathHisListByJobId(Criteria));
    }

    /**
     * 导出执行路径列表
     */
    @ApiOperation("导出执行路径列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:pathInfo:export')")
    @Log(title = "执行路径", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PathInfoCriteria criteria)
    {
        List<PathInfoDto> list = pathInfoService.findList(criteria);
        ExcelUtil<PathInfoDto> util = new ExcelUtil<PathInfoDto>(PathInfoDto.class);
        util.exportExcel(response, list, "执行路径数据");
    }

    /**
     * 获取执行路径详细信息
     */
    @ApiOperation("获取执行路径详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:pathInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(pathInfoService.findById(id));
    }

    /**
     * 新增执行路径
     */
    @ApiOperation("新增执行路径")
    @PreAuthorize("@ss.hasPermi('wcs-base:pathInfo:add')")
    @Log(title = "执行路径", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody PathInfo pathInfo)
    {
        pathInfo.setCreateTime(DateUtil.getNowDateTimeString());

        pathInfoService.save(pathInfo);
        return toAjax(true);
    }

    /**
     * 修改执行路径
     */
    @ApiOperation("修改执行路径")
    @PreAuthorize("@ss.hasPermi('wcs-base:pathInfo:edit')")
    @Log(title = "执行路径", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody PathInfo pathInfo)
    {



        return toAjax(pathInfoService.update(pathInfo));
    }

    /**
     * 删除执行路径
     */
    @ApiOperation("删除执行路径")
    @PreAuthorize("@ss.hasPermi('wcs-base:pathInfo:remove')")
    @Log(title = "执行路径", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(pathInfoService.deletePathInfoByIds(ids));
    }
}
