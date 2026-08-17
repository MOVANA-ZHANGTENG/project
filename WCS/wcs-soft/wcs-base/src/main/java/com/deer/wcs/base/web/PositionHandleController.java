package com.deer.wcs.base.web;

import com.deer.wcs.base.model.PositionHandle;
import com.deer.wcs.base.model.PositionHandleCriteria;
import com.deer.wcs.base.model.PositionHandleDto;
import com.deer.wcs.base.service.PositionHandleService;
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

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 路径执行器Controller
 * 
 * @author deer
 * @date 2024-04-28
 */
@Api("路径执行器")
@RestController
@RequestMapping("/wcs-base/PositionHandle")
public class PositionHandleController extends BaseController
{
    @Autowired
    private PositionHandleService pisitionHandleService;

    /**
     * 查询路径执行器列表
     */
    @ApiOperation("查询路径执行器列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionHandle:list')")
    @GetMapping("/list")
    public TableDataInfo list(PositionHandleCriteria Criteria)
    {
        startPage();
        List<PositionHandleDto> list = pisitionHandleService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出路径执行器列表
     */
    @ApiOperation("导出路径执行器列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionHandle:export')")
    @Log(title = "路径执行器", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PositionHandleCriteria criteria)
    {
        List<PositionHandleDto> list = pisitionHandleService.findList(criteria);
        ExcelUtil<PositionHandleDto> util = new ExcelUtil<PositionHandleDto>(PositionHandleDto.class);
        util.exportExcel(response, list, "路径执行器数据");
    }

    /**
     * 获取路径执行器详细信息
     */
    @ApiOperation("获取路径执行器详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionHandle:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(pisitionHandleService.findById(id));
    }

    /**
     * 新增路径执行器
     */
    @ApiOperation("新增路径执行器")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionHandle:add')")
    @Log(title = "路径执行器", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody PositionHandle pisitionHandle)
    {
        pisitionHandle.setCreateTime(DateUtil.getNowDateTimeString());
        pisitionHandle.setCreateUserId(getUserId());
        pisitionHandle.setCreateUserName(getUsername());

        pisitionHandleService.save(pisitionHandle);
        return toAjax(true);
    }

    /**
     * 修改路径执行器
     */
    @ApiOperation("修改路径执行器")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionHandle:edit')")
    @Log(title = "路径执行器", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody PositionHandle pisitionHandle)
    {

         pisitionHandle.setUpdateTime(DateUtil.getNowDateTimeString());
         pisitionHandle.setUpdateUserId(getUserId());
         pisitionHandle.setUpdateUserName(getUsername());


        return toAjax(pisitionHandleService.update(pisitionHandle));
    }

    /**
     * 删除路径执行器
     */
    @ApiOperation("删除路径执行器")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionHandle:remove')")
    @Log(title = "路径执行器", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(pisitionHandleService.deletePositionHandleByIds(ids));
    }
}
