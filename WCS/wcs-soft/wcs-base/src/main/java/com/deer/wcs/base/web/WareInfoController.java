package com.deer.wcs.base.web;

import com.deer.wcs.base.model.WareInfo;
import com.deer.wcs.base.model.WareInfoCriteria;
import com.deer.wcs.base.model.WareInfoDto;
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

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 仓库设置Controller
 *
 * @author deer
 * @date 2024-04-28
 */
@Api("仓库设置")
@RestController
@RequestMapping("/wcs-base/WareInfo")
public class WareInfoController extends BaseController {
    @Autowired
    private WareInfoService wareInfoService;
    @Autowired
    private ISysConfigService sysConfigService;

    @GetMapping("/findByCode")
    public Result findByCode(String code)
    {
        WareInfo wareInfo = wareInfoService.findBy("code",code);
        return success(wareInfo);
    }



    /**
     * 查询仓库设置列表
     */
    @ApiOperation("查询仓库设置列表")
    @GetMapping("/list")
    public TableDataInfo list(WareInfoCriteria Criteria) {
        startPage();
        List<WareInfoDto> list = wareInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 获取仓库选择框信息
     */
    @ApiOperation("获取仓库选择框信息")
    @GetMapping("/findAll")
    public Result findAll() {
        List<WareInfoDto> list = wareInfoService.findAllWareInfos();
        return success(list);
    }

    /**
     * 导出仓库设置列表
     */
    @ApiOperation("导出仓库设置列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:WareInfo:export')")
    @Log(title = "仓库设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WareInfoCriteria criteria) {
        List<WareInfoDto> list = wareInfoService.findList(criteria);
        ExcelUtil<WareInfoDto> util = new ExcelUtil<WareInfoDto>(WareInfoDto.class);
        util.exportExcel(response, list, "仓库设置数据");
    }

    /**
     * 获取仓库设置详细信息
     */
    @ApiOperation("获取仓库设置详细信息")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(wareInfoService.findById(id));
    }


    /**
     * 获取仓库设置详细信息
     */
    @ApiOperation("获取仓库设置详细信息")
    @GetMapping(value = "/getByCode")
    public Result getByCode( String code) {

        WareInfo wareInfo  = wareInfoService.findBy("code",code);
        return success( wareInfo);
    }

    /**
     * 新增仓库设置
     */
    @ApiOperation("新增仓库设置")
    @Log(title = "仓库设置", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody WareInfo wareInfo) {
        wareInfo.setCreateTime(DateUtil.getNowDateTimeString());
        wareInfo.setCreateUserId(getUserId());
        wareInfo.setCreateUserName(getUsername());

        wareInfoService.save(wareInfo);
        return toAjax(true);
    }

    /**
     * 修改仓库设置
     */
    @ApiOperation("修改仓库设置")
    @Log(title = "仓库设置", businessType = BusinessType.UPDATE)
    @PutMapping
    @Transactional
    public Result edit(@RequestBody WareInfo wareInfo) {

        wareInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        wareInfo.setUpdateUserId(getUserId());
        wareInfo.setUpdateUserName(getUsername());

        return toAjax(wareInfoService.update(wareInfo))                                          ;
    }

    /**
     * 修改仓库模型
     */
    @ApiOperation("修改仓库模型")
    @Log(title = "仓库设置", businessType = BusinessType.UPDATE)
    @PutMapping("editWareModel")
    public Result editWareModel(@RequestBody WareInfo wareInfo) {

        wareInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        wareInfo.setUpdateUserId(getUserId());
        wareInfo.setUpdateUserName(getUsername());

        return toAjax(wareInfoService.updateWareModel(wareInfo));
    }

    /**
     * 删除仓库设置
     */
    @ApiOperation("删除仓库设置")
    @Log(title = "仓库设置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        if(Integer.parseInt(sysConfigService.selectConfigByKey("soft_delete"))==0){
            for(Long id:ids){
                WareInfo wareInfo = wareInfoService.findById(id);
                if(wareInfo!=null){
                    if(wareInfo.getIsDelete()==1){
                        return toAjax(wareInfoService.deleteWareInfoByIds(ids));
                    }
                    wareInfo.setIsDelete(1);
                    wareInfoService.update(wareInfo);
                }
            }
            return Result.success();
        }else{
            return toAjax(wareInfoService.deleteWareInfoByIds(ids));
        }

    }


}
