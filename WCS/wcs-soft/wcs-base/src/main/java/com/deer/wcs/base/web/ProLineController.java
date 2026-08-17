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
import com.deer.wcs.base.model.ProLine;
import com.deer.wcs.base.model.ProLineDto;
import com.deer.wcs.base.model.ProLineCriteria;
import com.deer.wcs.base.service.ProLineService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 产线Controller
 * 
 * @author deer
 * @date 2024-11-21
 */
@Api("产线")
@RestController
@RequestMapping("/wcs-base/ProLine")
public class ProLineController extends BaseController
{
    @Autowired
    private ProLineService proLineService;

    /**
     * 查询产线列表
     */
    @ApiOperation("查询产线列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProLine:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProLineCriteria Criteria)
    {
        startPage();
        List<ProLineDto> list = proLineService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出产线列表
     */
    @ApiOperation("导出产线列表")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProLine:export')")
    @Log(title = "产线", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProLineCriteria criteria)
    {
        List<ProLineDto> list = proLineService.findList(criteria);
        ExcelUtil<ProLineDto> util = new ExcelUtil<ProLineDto>(ProLineDto.class);
        util.exportExcel(response, list, "产线数据");
    }

    /**
     * 获取产线详细信息
     */
    @ApiOperation("获取产线详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProLine:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(proLineService.findById(id));
    }

    /**
     * 新增产线
     */
    @ApiOperation("新增产线")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProLine:add')")
    @Log(title = "产线", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody ProLine proLine)
    {
        proLine.setCreateTime(DateUtil.getNowDateTimeString());
        proLine.setCreateUserId(getUserId());
        proLine.setCreateUserName(getUsername());

        proLineService.save(proLine);
        return toAjax(true);
    }

    /**
     * 修改产线
     */
    @ApiOperation("修改产线")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProLine:edit')")
    @Log(title = "产线", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody ProLine proLine)
    {

         proLine.setUpdateTime(DateUtil.getNowDateTimeString());
         proLine.setUpdateUserName(getUsername());


        return toAjax(proLineService.update(proLine));
    }

    /**
     * 删除产线
     */
    @ApiOperation("删除产线")
    //@PreAuthorize("@ss.hasPermi('wcs-base:ProLine:remove')")
    @Log(title = "产线", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(proLineService.deleteProLineByIds(ids));
    }
}
