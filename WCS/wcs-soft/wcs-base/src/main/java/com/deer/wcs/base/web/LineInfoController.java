package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.AreaInfoService;
import com.deer.wcs.base.service.LineInfoService;
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
import java.util.List;

/**
 * 巷道Controller
 *
 * @author deer
 * @date 2024-04-28
 */
@Api("巷道")
@RestController
@RequestMapping("/wcs-base/LineInfo")
public class LineInfoController extends BaseController {
    @Autowired
    private LineInfoService lineInfoService;
    @Autowired
    private ISysConfigService sysConfigService;

    /**
     * 查询巷道列表
     */
    @ApiOperation("查询巷道列表")
    @GetMapping("/list")
    public TableDataInfo list(LineInfoCriteria Criteria) {
        startPage();
        List<LineInfoDto> list = lineInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出巷道列表
     */
    @ApiOperation("导出巷道列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:LineInfo:export')")
    @Log(title = "巷道", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LineInfoCriteria criteria) {
        List<LineInfoDto> list = lineInfoService.findList(criteria);
        ExcelUtil<LineInfoDto> util = new ExcelUtil<LineInfoDto>(LineInfoDto.class);
        util.exportExcel(response, list, "巷道数据");
    }

    /**
     * 获取巷道详细信息
     */
    @ApiOperation("获取巷道详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:LineInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(lineInfoService.findById(id));
    }

    @Autowired
    WareInfoService wareInfoService;
    @Autowired
    AreaInfoService areaInfoService;

    /**
     * 新增巷道
     */
    @ApiOperation("新增巷道")
    @Log(title = "巷道", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody LineInfo lineInfo) {
        lineInfo.setType("line");
        Condition repeatCode = new Condition(LineInfo.class);
        repeatCode.createCriteria().andEqualTo("code",lineInfo.getCode())
                .andEqualTo("isDelete",0);
        List<LineInfo> repeatCodes = lineInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            return error("巷道编码重复");
        }

        Condition repeatName = new Condition(LineInfo.class);
        repeatName.createCriteria().andEqualTo("name",lineInfo.getName())
                .andEqualTo("isDelete",0);
        List<LineInfo> repeatNames = lineInfoService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            return error("巷道名称重复");
        }

        lineInfo.setCreateTime(DateUtil.getNowDateTimeString());
        lineInfo.setCreateUserId(getUserId());
        lineInfo.setCreateUserName(getUsername());
        String wareCode = lineInfo.getWareCode();
        WareInfo wareInfo = wareInfoService.findBy("code", wareCode);
        String areaCode = lineInfo.getAreaCode();
        AreaInfo areaInfo = areaInfoService.findBy("code", areaCode);

        if (!areaInfo.getWareCode().equals(wareInfo.getCode())) {
            return error("所选仓库和库区冲突！");
        }

        lineInfo.setWareName(wareInfo.getName());
        lineInfo.setAreaName(areaInfo.getName());
        lineInfoService.save(lineInfo);
        return toAjax(true);
    }

    /**
     * 修改巷道
     */
    @ApiOperation("修改巷道")
    @Log(title = "巷道", businessType = BusinessType.UPDATE)
    @PutMapping
    @Transactional
    public Result edit(@RequestBody LineInfo lineInfo) {

        Condition repeatCode = new Condition(LineInfo.class);
        repeatCode.createCriteria().andEqualTo("code",lineInfo.getCode())
                .andEqualTo("isDelete",0);
        List<LineInfo> repeatCodes = lineInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            if(repeatCodes.size()==1 && repeatCodes.get(0).getId().longValue() != lineInfo.getId().longValue()){
                return error("巷道编码重复");
            }else if(repeatCodes.size()>1){
                return error("巷道编码重复");
            }
        }

        Condition repeatName = new Condition(LineInfo.class);
        repeatName.createCriteria().andEqualTo("name",lineInfo.getName())
                .andEqualTo("isDelete",0);
        List<LineInfo> repeatNames = lineInfoService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            if(repeatNames.size()==1 && repeatNames.get(0).getId().longValue() != lineInfo.getId().longValue()){
                return error("巷道名称重复");
            }else if(repeatNames.size()>1){
                return error("巷道名称重复");
            }
        }

        lineInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        lineInfo.setUpdateUserId(getUserId());
        lineInfo.setUpdateUserName(getUsername());
        String wareCode = lineInfo.getWareCode();
        String areaCode = lineInfo.getAreaCode();

        WareInfo wareInfo = null;
        AreaInfo areaInfo = null;

        if (wareCode!=null&&!"".equals(wareCode)) {
            wareInfo = wareInfoService.findBy("code", wareCode);
        }
        if (areaCode!=null&&!"".equals(areaCode)) {
            areaInfo = areaInfoService.findBy("code", areaCode);
        }
        if (wareInfo != null && areaInfo != null) {
            if (!areaInfo.getWareCode().equals(wareInfo.getCode())) {
                return error("所选仓库和库区冲突！");
            }
        }
        if (wareInfo != null) {
            lineInfo.setWareName(wareInfo.getName());
        }
        if (areaInfo != null) {
            lineInfo.setAreaName(areaInfo.getName());
        }

        return toAjax(lineInfoService.update(lineInfo));
    }


    /**
     * 删除巷道
     */
    @ApiOperation("删除巷道")
    @PreAuthorize("@ss.hasPermi('wcs-base:LineInfo:remove')")
    @Log(title = "巷道", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        if (Integer.parseInt(sysConfigService.selectConfigByKey("soft_delete")) == 0) {
            for (Long id : ids) {
                LineInfo lineInfo = lineInfoService.findById(id);
                if (lineInfo != null) {
                    if(lineInfo.getIsDelete()==1){
                        return toAjax(lineInfoService.deleteLineInfoByIds(ids));
                    }
                    lineInfo.setIsDelete(1);
                    lineInfoService.update(lineInfo);
                }
            }
            return Result.success();
        } else {
            return toAjax(lineInfoService.deleteLineInfoByIds(ids));
        }
    }
}
