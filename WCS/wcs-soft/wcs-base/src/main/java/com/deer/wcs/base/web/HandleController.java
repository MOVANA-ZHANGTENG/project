package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.HandleGroupService;
import com.deer.wcs.base.service.HandleService;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 执行器Controller
 *
 * @author deer
 * @date 2024-04-28
 */
@Api("执行器")
@RestController
@RequestMapping("/wcs-base/Handle")
public class HandleController extends BaseController {
    @Autowired
    private HandleService handleService;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private HandleGroupService handleGroupService;

    /**
     * 查询执行器列表
     */
    @ApiOperation("查询执行器列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:Handle:list')")
    @GetMapping("/list")
    public TableDataInfo list(HandleCriteria Criteria) {
        startPage();
        List<HandleDto> list = handleService.findList(Criteria);
        return getDataTable(list);
    }

    @ApiOperation("根据类型获取没有被禁用的handle")
    @GetMapping("/findAll")
    public TableDataInfo findAll(HandleCriteria Criteria) {
        List<HandleDto> list = handleService.findHandleByType(Criteria);
        return getDataTable(list);
    }
    @ApiOperation("获取选中的方法")
//    @PreAuthorize("@ss.hasPermi('wcs-base:Handle:list')")
    @PostMapping("/findCheckedMethods")
    public Result findCheckedMethods(@RequestBody String[] codes) {
        List<Handle> list = new ArrayList<>();
        for(String code:codes){
            list.add(handleService.findBy("code",code));
        }
        return success(list);
    }

    /**
     * 导出执行器列表
     */
    @ApiOperation("导出执行器列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:Handle:export')")
    @Log(title = "执行器", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HandleCriteria criteria) {
        List<HandleDto> list = handleService.findList(criteria);
        ExcelUtil<HandleDto> util = new ExcelUtil<HandleDto>(HandleDto.class);
        util.exportExcel(response, list, "执行器数据");
    }

    /**
     * 获取执行器详细信息
     */
    @ApiOperation("获取执行器详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:Handle:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(handleService.findById(id));
    }

    /**
     * 新增执行器
     */
    @ApiOperation("新增执行器")
    @PreAuthorize("@ss.hasPermi('wcs-base:Handle:add')")
    @Log(title = "执行器", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody Handle handle) {

        Condition repeatCode = new Condition(Handle.class);
        repeatCode.createCriteria().andEqualTo("code",handle.getCode())
                .andEqualTo("isDelete",0);
        List<Handle> repeatCodes = handleService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            return error("执行器编码重复");
        }

//        Condition repeatName = new Condition(Handle.class);
//        repeatName.createCriteria().andEqualTo("name",handle.getName())
//                .andEqualTo("isDelete",0);
//        List<Handle> repeatNames = handleService.findByCondition(repeatName);
//        if(repeatNames.size()>0){
//            return error("执行器名称重复");
//        }

        HandleGroup group = handleGroupService.findById(handle.getGroupId());
        if(group!=null){
            handle.setType(group.getType());
        }
        handle.setCreateTime(DateUtil.getNowDateTimeString());
        handle.setCreateUserId(getUserId());
        handle.setCreateUserName(getUsername());

        handleService.save(handle);
        return toAjax(true);
    }

    /**
     * 修改执行器
     */
    @ApiOperation("修改执行器")
    @PreAuthorize("@ss.hasPermi('wcs-base:Handle:edit')")
    @Log(title = "执行器", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody Handle handle) {

        Condition repeatCode = new Condition(Handle.class);
        repeatCode.createCriteria().andEqualTo("code",handle.getCode())
                .andEqualTo("isDelete",0);
        List<Handle> repeatCodes = handleService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            if(repeatCodes.size()==1 && repeatCodes.get(0).getId().longValue() != handle.getId().longValue()){
                return error("执行器编码重复");
            }else if(repeatCodes.size()>1){
                return error("执行器编码重复");
            }
        }

//        Condition repeatName = new Condition(Handle.class);
//        repeatName.createCriteria().andEqualTo("name",handle.getName())
//                .andEqualTo("isDelete",0);
//        List<Handle> repeatNames = handleService.findByCondition(repeatName);
//        if(repeatNames.size()>0){
//            if(repeatNames.size()==1 && repeatNames.get(0).getId().longValue() != handle.getId().longValue()){
//                return error("执行器名称重复");
//            }else if(repeatNames.size()>1){
//                return error("执行器名称重复");
//            }
//        }

        handle.setUpdateTime(DateUtil.getNowDateTimeString());
        handle.setUpdateUserId(getUserId());
        handle.setUpdateUserName(getUsername());

        return toAjax(handleService.update(handle));
    }

    /**
     * 删除执行器
     */
    @ApiOperation("删除执行器")
    @PreAuthorize("@ss.hasPermi('wcs-base:Handle:remove')")
    @Log(title = "执行器", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        if(Integer.parseInt(sysConfigService.selectConfigByKey("soft_delete"))==0){
            for(Long id:ids){
                Handle handle = handleService.findById(id);
                if(handle!=null){
                    handle.setIsDelete(1);
                    handleService.update(handle);
                }
            }
            return Result.success();
        }else{
            return toAjax(handleService.deleteHandleByIds(ids));
        }
    }
}
