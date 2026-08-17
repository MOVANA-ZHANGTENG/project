package com.deer.wcs.base.web;

import com.deer.wcs.base.model.AreaInfo;
import com.deer.wcs.base.model.AreaInfoCriteria;
import com.deer.wcs.base.model.AreaInfoDto;
import com.deer.wcs.base.model.WareInfo;
import com.deer.wcs.base.service.AreaInfoService;
import com.deer.wcs.base.service.WareInfoService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 库区Controller
 *
 * @author deer
 * @date 2024-04-28
 */
@Api("库区")
@RestController
@RequestMapping("/wcs-base/AreaInfo")
public class AreaInfoController extends BaseController {
    @Autowired
    private AreaInfoService areaInfoService;
    @Autowired
    private ISysConfigService sysConfigService;

    private static void invokeMethod(Object bean, String methodName, Object parem)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Method method = bean.getClass().getMethod(methodName, Integer.class);
        method.invoke(bean, parem);
    }

    /**
     * 查询库区列表
     */
    @ApiOperation("查询库区列表")
    @GetMapping("/list")
    public TableDataInfo list(AreaInfoCriteria Criteria) {
        startPage();
        List<AreaInfoDto> list = areaInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出库区列表
     */
    @ApiOperation("导出库区列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:AreaInfo:export')")
    @Log(title = "库区", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AreaInfoCriteria criteria) {
        List<AreaInfoDto> list = areaInfoService.findList(criteria);
        ExcelUtil<AreaInfoDto> util = new ExcelUtil<AreaInfoDto>(AreaInfoDto.class);
        util.exportExcel(response, list, "库区数据");
    }

    /**
     * 获取库区详细信息
     */
    @ApiOperation("获取库区详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:AreaInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(areaInfoService.findById(id));
    }

    @Autowired
    WareInfoService wareInfoService;

    /**
     * 新增库区
     */
    @ApiOperation("新增库区")
    @PreAuthorize("@ss.hasPermi('wcs-base:AreaInfo:add')")
    @Log(title = "库区", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody AreaInfo areaInfo) {
        Condition repeatCode = new Condition(AreaInfo.class);
        repeatCode.createCriteria().andEqualTo("code",areaInfo.getCode())
                         .andEqualTo("isDelete",0);
        List<AreaInfo> repeatCodes = areaInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            return error("库区编码重复");
        }

        Condition repeatName = new Condition(AreaInfo.class);
        repeatName.createCriteria().andEqualTo("name",areaInfo.getName())
                .andEqualTo("isDelete",0);
        List<AreaInfo> repeatNames = areaInfoService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            return error("库区名称重复");
        }

        areaInfo.setCreateTime(DateUtil.getNowDateTimeString());
        areaInfo.setCreateUserId(getUserId());
        areaInfo.setCreateUserName(getUsername());
        String wareCode = areaInfo.getWareCode();
        WareInfo wareInfo = wareInfoService.findBy("code", wareCode);
        areaInfo.setWareName(wareInfo.getName());
        areaInfoService.save(areaInfo);
        return toAjax(true);
    }

    /**
     * 修改库区
     */
    @ApiOperation("修改库区")
    @PreAuthorize("@ss.hasPermi('wcs-base:AreaInfo:edit')")
    @Log(title = "库区", businessType = BusinessType.UPDATE)
    @PutMapping
    @Transactional
    public Result edit(@RequestBody AreaInfo areaInfo) {
        Condition repeatCode = new Condition(AreaInfo.class);
        repeatCode.createCriteria().andEqualTo("code",areaInfo.getCode())
                .andEqualTo("isDelete",0);
        List<AreaInfo> repeatCodes = areaInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            if(repeatCodes.size()==1 && repeatCodes.get(0).getId().longValue() != areaInfo.getId().longValue()){
                return error("库区编码重复");
            }else if(repeatCodes.size()>1){
                return error("库区编码重复");
            }
        }

        Condition repeatName = new Condition(AreaInfo.class);
        repeatName.createCriteria().andEqualTo("name",areaInfo.getName())
                .andEqualTo("isDelete",0);
        List<AreaInfo> repeatNames = areaInfoService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            if(repeatNames.size()==1 && repeatNames.get(0).getId().longValue() != areaInfo.getId().longValue()){
                return error("库区名称重复");
            }else if(repeatNames.size()>1){
                return error("库区名称重复");
            }
        }

        areaInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        areaInfo.setUpdateUserId(getUserId());
        areaInfo.setUpdateUserName(getUsername());
        String wareCode = areaInfo.getWareCode();
        WareInfo wareInfo = wareInfoService.findBy("code", wareCode);
        if(wareInfo!=null){
            areaInfo.setWareName(wareInfo.getName());
        }

        return toAjax(areaInfoService.update(areaInfo));
    }

    /**
     * 删除库区
     */
    @ApiOperation("删除库区")
    @PreAuthorize("@ss.hasPermi('wcs-base:AreaInfo:remove')")
    @Log(title = "库区", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        if (Integer.parseInt(sysConfigService.selectConfigByKey("soft_delete")) == 0) {
            for (Long id : ids) {
                AreaInfo areaInfo = areaInfoService.findById(id);
                if (areaInfo != null) {
                    areaInfo.setIsDelete(1);
                    areaInfoService.update(areaInfo);
                }
            }
            return Result.success();
        } else {
            return toAjax(areaInfoService.deleteAreaInfoByIds(ids));
        }
    }
}
