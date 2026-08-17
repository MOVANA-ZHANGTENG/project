package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.HandleGroupService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.system.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 分组管理Controller
 * 
 * @author deer
 * @date 2024-05-15
 */
@Api("分组管理")
@RestController
@RequestMapping("/wcs-base/handleGroup")
public class HandleGroupController extends BaseController
{
    @Autowired
    private HandleGroupService handleGroupService;
    @Autowired
    private ISysConfigService sysConfigService;

    /**
     * 查询分组管理列表
     */
    @ApiOperation("查询分组管理列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:handleGroup:list')")
    @GetMapping("/list")
    public TableDataInfo list(HandleGroupCriteria Criteria)
    {
        startPage();
        List<HandleGroupDto> list = handleGroupService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 查询分组管理列表
     */
    @ApiOperation("查询分组管理列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:handleGroup:list')")
    @GetMapping("/findCanAllotGroup")
    public Result findCanAllotGroup()
    {
        Condition condition = new Condition(HandleGroup.class);
        condition.createCriteria().andEqualTo("disableState","0")
                .andEqualTo("delFlag",0);
        return Result.success(handleGroupService.findByCondition(condition));
    }

    /**
     * 导出分组管理列表
     */
    @ApiOperation("导出分组管理列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:handleGroup:export')")
    @Log(title = "分组管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HandleGroupCriteria criteria)
    {
        List<HandleGroupDto> list = handleGroupService.findList(criteria);
        ExcelUtil<HandleGroupDto> util = new ExcelUtil<HandleGroupDto>(HandleGroupDto.class);
        util.exportExcel(response, list, "分组管理数据");
    }

    /**
     * 获取分组管理详细信息
     */
    @ApiOperation("获取分组管理详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:handleGroup:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(handleGroupService.findById(id));
    }

    /**
     * 新增分组管理
     */
    @ApiOperation("新增分组管理")
    @PreAuthorize("@ss.hasPermi('wcs-base:handleGroup:add')")
    @Log(title = "分组管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody HandleGroup handleGroup)
    {
        Condition repeatName = new Condition(HandleGroup.class);
        repeatName.createCriteria().andEqualTo("name",handleGroup.getName())
                .andEqualTo("delFlag",0);
        List<HandleGroup> repeatNames = handleGroupService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            return error("执行器分组名称重复");
        }

        handleGroup.setCreateTime(DateUtil.getNowDateTimeString());
        handleGroup.setCreateUserId(getUserId());
        handleGroup.setCreateUserName(getUsername());

        handleGroupService.save(handleGroup);
        return toAjax(true);
    }

    /**
     * 修改分组管理
     */
    @ApiOperation("修改分组管理")
    @PreAuthorize("@ss.hasPermi('wcs-base:handleGroup:edit')")
    @Log(title = "分组管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody HandleGroup handleGroup)
    {


        Condition repeatName = new Condition(HandleGroup.class);
        repeatName.createCriteria().andEqualTo("name",handleGroup.getName())
                .andEqualTo("delFlag",0);
        List<HandleGroup> repeatNames = handleGroupService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            if(repeatNames.size()==1 && repeatNames.get(0).getId().longValue() != handleGroup.getId().longValue()){
                return error("执行器分组名称重复");
            }else if(repeatNames.size()>1){
                return error("执行器分组名称重复");
            }
        }

         handleGroup.setUpdateTime(DateUtil.getNowDateTimeString());
         handleGroup.setUpdateUserId(getUserId());
         handleGroup.setUpdateUserName(getUsername());


        return toAjax(handleGroupService.update(handleGroup));
    }

    /**
     * 删除分组管理
     */
    @ApiOperation("删除分组管理")
    @PreAuthorize("@ss.hasPermi('wcs-base:handleGroup:remove')")
    @Log(title = "分组管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        if(Integer.parseInt(sysConfigService.selectConfigByKey("soft_delete"))==0){
            for(Long id:ids){
                HandleGroup handleGroup = handleGroupService.findById(id);
                if(handleGroup!=null){
                    handleGroup.setDelFlag(1);
                    handleGroupService.update(handleGroup);
                }
            }
            return Result.success();
        }else{
            return toAjax(handleGroupService.deleteHandleGroupByIds(ids));
        }
    }
}
