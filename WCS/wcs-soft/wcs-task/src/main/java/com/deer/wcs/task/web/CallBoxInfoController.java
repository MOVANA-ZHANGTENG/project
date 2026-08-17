package com.deer.wcs.task.web;

import com.deer.wcs.base.model.Handle;
import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.model.WareInfo;
import com.deer.wcs.base.service.HandleService;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.base.service.WareInfoService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.system.service.ISysConfigService;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfo;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfoCriteria;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfoDto;
import com.deer.wcs.task.service.CallBoxInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 呼叫盒Controller
 * 
 * @author deer
 * @date 2024-07-30
 */
@Api("呼叫盒")
@RestController
@RequestMapping("/wcs-task/CallBoxInfo")
public class CallBoxInfoController extends BaseController
{
    @Autowired
    private CallBoxInfoService callBoxInfoService;

    @Autowired
    private HandleService handleService;

    @Autowired
    private WareInfoService wareInfoService;

    @Autowired
    private PositionInfoService positionInfoService;

    /**
     * 查询呼叫盒列表
     */
    @ApiOperation("查询呼叫盒列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:CallBoxInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(CallBoxInfoCriteria Criteria)
    {
        startPage();
        List<CallBoxInfoDto> list = callBoxInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出呼叫盒列表
     */
    @ApiOperation("导出呼叫盒列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:CallBoxInfo:export')")
    @Log(title = "呼叫盒", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CallBoxInfoCriteria criteria)
    {
        List<CallBoxInfoDto> list = callBoxInfoService.findList(criteria);
        ExcelUtil<CallBoxInfoDto> util = new ExcelUtil<CallBoxInfoDto>(CallBoxInfoDto.class);
        util.exportExcel(response, list, "呼叫盒数据");
    }

    /**
     * 获取呼叫盒详细信息
     */
    @ApiOperation("获取呼叫盒详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-task:CallBoxInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(callBoxInfoService.findById(id));
    }

    /**
     * 新增呼叫盒
     */
    @ApiOperation("新增呼叫盒")
    @PreAuthorize("@ss.hasPermi('wcs-task:CallBoxInfo:add')")
    @Log(title = "呼叫盒", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody CallBoxInfo callBoxInfo)
    {
        Condition condition = new Condition(CallBoxInfo.class);
        condition.createCriteria().andEqualTo("code",callBoxInfo.getCode()) ;
        List<CallBoxInfo> callBoxInfoList = callBoxInfoService.findByCondition(condition);
        if (callBoxInfoList != null && !callBoxInfoList.isEmpty()){
            return Result.error("不用添加重复的信息");
        }
        String wareCode = callBoxInfo.getWareCode();
        WareInfo wareInfo =wareInfoService.findBy("code",wareCode);
        callBoxInfo.setWareName(wareInfo.getName());
        String positionCode = callBoxInfo.getPositionCode();
        PositionInfo positionInfo = positionInfoService.findBy("code", positionCode);
        if (positionInfo == null){
            return Result.error("添加的位置信息中不存在,请检查");
        }
        String wareCode1 = positionInfo.getWareCode();
        if (!wareCode1.equals(wareCode)){
            return Result.error("选择的仓库里面没有这个位置,请检查");
        }

        //检查编码和名称是否重复
        Condition repeatCode = new Condition(CallBoxInfo.class);
        repeatCode.createCriteria()
                .andEqualTo("code",callBoxInfo.getCode())
                .andEqualTo("delFlag",0);
        List<CallBoxInfo> repeatCodes = callBoxInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            return error("呼叫盒编码重复");
        }

        Condition repeatName = new Condition(CallBoxInfo.class);
        repeatName.createCriteria()
                .andEqualTo("name",callBoxInfo.getName())
                .andEqualTo("delFlag",0);
        List<CallBoxInfo> repeatNames = callBoxInfoService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            return error("呼叫盒名称重复");
        }

        callBoxInfoService.save(callBoxInfo);
        return toAjax(true);
    }

    /**
     * 修改呼叫盒
     */
    @ApiOperation("修改呼叫盒")
    @PreAuthorize("@ss.hasPermi('wcs-task:CallBoxInfo:edit')")
    @Log(title = "呼叫盒", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody CallBoxInfo callBoxInfo)
    {
        Condition condition = new Condition(CallBoxInfo.class);
        condition.createCriteria().andEqualTo("code",callBoxInfo.getCode())
                .andEqualTo("name",callBoxInfo.getName())  ;
        List<CallBoxInfo> callBoxInfoList = callBoxInfoService.findByCondition(condition);
        for (CallBoxInfo info:callBoxInfoList) {
            if(!callBoxInfo.getId().equals(info.getId())){
                return Result.error("不用修改成重复的信息");
            }
        }
        String wareCode = callBoxInfo.getWareCode();
        WareInfo wareInfo =wareInfoService.findBy("code",wareCode);
        callBoxInfo.setWareName(wareInfo.getName());
        String positionCode = callBoxInfo.getPositionCode();
        PositionInfo positionInfo = positionInfoService.findBy("code", positionCode);
        if (positionInfo == null){
            return Result.error("添加的位置信息中不存在,请检查");
        }
        String wareCode1 = positionInfo.getWareCode();
        if (!wareCode1.equals(wareCode)){
            return Result.error("选择的仓库里面没有这个位置,请检查");
        }

        //检查编码和名称是否重复
        Condition repeatCode = new Condition(CallBoxInfo.class);
        repeatCode.createCriteria()
                .andEqualTo("code",callBoxInfo.getCode())
                .andEqualTo("delFlag",0);
        List<CallBoxInfo> repeatCodes = callBoxInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            if(repeatCodes.size()==1 && repeatCodes.get(0).getId().longValue() != callBoxInfo.getId().longValue()){
                return error("呼叫盒编码重复");
            }else if(repeatCodes.size()>1){
                return error("呼叫盒编码重复");
            }
        }

        Condition repeatName = new Condition(CallBoxInfo.class);
        repeatName.createCriteria()
                .andEqualTo("name",callBoxInfo.getName())
                .andEqualTo("delFlag",0);
        List<CallBoxInfo> repeatNames = callBoxInfoService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            if(repeatNames.size()==1 && repeatNames.get(0).getId().longValue() != callBoxInfo.getId().longValue()){
                return error("呼叫盒名称重复");
            }else if(repeatNames.size()>1){
                return error("呼叫盒名称重复");
            }
        }

        return toAjax(callBoxInfoService.update(callBoxInfo));
    }

    @Autowired
    private ISysConfigService sysConfigService;

    /**
     * 删除呼叫盒
     */
    @ApiOperation("删除呼叫盒")
    @PreAuthorize("@ss.hasPermi('wcs-task:CallBoxInfo:remove')")
    @Log(title = "呼叫盒", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        if (Integer.parseInt(sysConfigService.selectConfigByKey("soft_delete")) == 0) {
            for (Long id : ids) {
                CallBoxInfo callBoxInfo = callBoxInfoService.findById(id);
                if (callBoxInfo != null) {
                    callBoxInfo.setDelFlag(1);
                    callBoxInfoService.update(callBoxInfo);
                }
            }
            return Result.success();
        } else {
            return toAjax(callBoxInfoService.deleteCallBoxInfoByIds(ids));
        }
//        return toAjax(callBoxInfoService.deleteCallBoxInfoByIds(ids));
    }


    /**
     * 查询handle里面所有呼叫盒相关的
     */
    @GetMapping("/getCallBoxHandle")
    public Result getCallBoxHandle(){
        Condition condition = new Condition(Handle.class);
        condition.createCriteria().andEqualTo("type",2)
                .andEqualTo("handleType",1);
        List<Handle> handleList = handleService.findByCondition(condition);
        return Result.success(handleList);
    }
}
