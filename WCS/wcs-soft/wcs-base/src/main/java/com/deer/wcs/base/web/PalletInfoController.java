package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.LineInfoService;
import com.deer.wcs.base.service.PalletInfoService;
import com.deer.wcs.base.service.PalletTypeService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 托盘信息Controller
 * 
 * @author deer
 * @date 2024-05-29
 */
@Api("托盘信息")
@RestController
@RequestMapping("/wcs-base/palletInfo")
@Transactional
public class PalletInfoController extends BaseController
{
    @Autowired
    private PalletInfoService palletInfoService;
    @Autowired
    private PalletTypeService palletTypeService;

    @Autowired
    private LineInfoService lineInfoService;
    @Autowired
    private CellInfoService cellInfoService;

    /**
     * 更新货位状态
     */
    @ApiOperation("更新货位状态")
    @Log(title = "库位", businessType = BusinessType.UPDATE)
    @PostMapping("/updateCellInfoState")
    public Result updateCellInfoState(@RequestBody CellInfoDto cellInfo) {

        cellInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        cellInfo.setUpdateUserId(getUserId());
        cellInfo.setUpdateUserName(getUsername());
        if(cellInfo.getLineCode()!=null && !cellInfo.getLineCode().trim().isEmpty()){
            LineInfo lineInfo = lineInfoService.findByCode(cellInfo.getWareCode(), cellInfo.getLineCode());
            cellInfo.setWareCode(lineInfo.getWareCode());
            cellInfo.setWareName(lineInfo.getWareName());
            cellInfo.setAreaCode(lineInfo.getAreaCode());
            cellInfo.setAreaName(lineInfo.getAreaName());
            cellInfo.setLineCode(lineInfo.getCode());
            cellInfo.setLineName(lineInfo.getName());
        }
        String palletCode = cellInfo.getPalletCode();
        if(palletCode!=null && !palletCode.trim().isEmpty()){
            PalletInfo palletInfo = palletInfoService.findBy("code",palletCode);
            if(palletInfo!=null){
                palletInfo.setCellCode(cellInfo.getCode());
                palletInfo.setWareCode(cellInfo.getWareCode());
                palletInfoService.update(palletInfo);
            }else {
                palletInfo = new PalletInfo();
                palletInfo.setCode(palletCode);
                palletInfo.setCellCode(cellInfo.getCode());
                palletInfo.setWareCode(cellInfo.getWareCode());
                palletInfoService.save(palletInfo);
            }
        }
        cellInfoService.update(cellInfo);

        if(cellInfo.getInvenState()==0L){
            Condition condition = new Condition(PalletInfo.class);
            condition.createCriteria().andEqualTo("cellCode",cellInfo.getCode())
                    .andEqualTo("wareCode",cellInfo.getWareCode());
            List<PalletInfo> palletInfos = palletInfoService.findByCondition(condition);
            for (PalletInfo palletInfo : palletInfos) {
                palletInfo.setCellCode("");
                palletInfoService.update(palletInfo);
            }
        }
        return success();
    }

    /**
     * 查询托盘信息列表
     */
    @ApiOperation("查询托盘信息列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:palletInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(PalletInfoCriteria Criteria)
    {
        startPage();
        List<PalletInfoDto> list = palletInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出托盘信息列表
     */
    @ApiOperation("导出托盘信息列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:palletInfo:export')")
    @Log(title = "托盘信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PalletInfoCriteria criteria)
    {
        List<PalletInfoDto> list = palletInfoService.findList(criteria);
        ExcelUtil<PalletInfoDto> util = new ExcelUtil<PalletInfoDto>(PalletInfoDto.class);
        util.exportExcel(response, list, "托盘信息数据");
    }

    /**
     * 获取托盘信息详细信息
     */
    @ApiOperation("获取托盘信息详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:palletInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(palletInfoService.findById(id));
    }

    /**
     * 新增托盘信息
     */
    @ApiOperation("新增托盘信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:palletInfo:add')")
    @Log(title = "托盘信息", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody PalletInfo palletInfo)
    {
        Condition repeatCode = new Condition(PalletInfo.class);
        repeatCode.createCriteria().andEqualTo("code",palletInfo.getCode());
        List<PalletInfo> repeatCodes = palletInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            return error("托盘编码重复");
        }

        Condition repeatName = new Condition(PalletInfo.class);
        repeatName.createCriteria().andEqualTo("name",palletInfo.getName());
        List<PalletInfo> repeatNames = palletInfoService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            return error("托盘名称重复");
        }
        palletInfoService.save(palletInfo);
        return toAjax(true);
    }

    /**
     * 修改托盘信息
     */
    @ApiOperation("修改托盘信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:palletInfo:edit')")
    @Log(title = "托盘信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody PalletInfo palletInfo)
    {

        Condition repeatCode = new Condition(PalletInfo.class);
        repeatCode.createCriteria().andEqualTo("code",palletInfo.getCode());
        List<PalletInfo> repeatCodes = palletInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            if(repeatCodes.size()==1 && repeatCodes.get(0).getId().longValue() != palletInfo.getId().longValue()){
                return error("托盘编码重复");
            }else if(repeatCodes.size()>1){
                return error("托盘编码重复");
            }
        }

        Condition repeatName = new Condition(PalletInfo.class);
        repeatName.createCriteria().andEqualTo("name",palletInfo.getName());
        List<PalletInfo> repeatNames = palletInfoService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            if(repeatNames.size()==1 && repeatNames.get(0).getId().longValue() != palletInfo.getId().longValue()){
                return error("托盘名称重复");
            }else if(repeatNames.size()>1){
                return error("托盘名称重复");
            }
        }
        return toAjax(palletInfoService.update(palletInfo));
    }

    /**
     * 删除托盘信息
     */
    @ApiOperation("删除托盘信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:palletInfo:remove')")
    @Log(title = "托盘信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(palletInfoService.deletePalletInfoByIds(ids));
    }

    @ApiOperation("托盘信息导入")
    @Log(title = "托盘信息导入", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public Result importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<PalletInfo> util = new ExcelUtil<>(PalletInfo.class);
        List<PalletInfo> palletInfos = util.importExcel(file.getInputStream());
        PalletType palletType = palletTypeService.findAll().get(0);
        Integer taskCount = palletInfos.size();
        Integer insertCount = 0;
        Integer updateCount = 0;
        Integer failCount = 0;
        if(updateSupport){
            //更新存在的旧数据
            for(PalletInfo palletInfo :palletInfos){
                //非空校验，有字段为空则跳过
                if((palletInfo.getCode()==null||"".equals(palletInfo.getCode()))
                ||(palletInfo.getRfidCode()==null||"".equals(palletInfo.getRfidCode()))
                ||(palletInfo.getName()==null||"".equals(palletInfo.getName()))){
                    continue;
                }
                Condition condition = new Condition(PalletInfo.class);
                condition.createCriteria().andEqualTo("code",palletInfo.getCode());
                List<PalletInfo> oldInfos = palletInfoService.findByCondition(condition);
                if(oldInfos.size()==0){
                    palletInfo.setTypeCode(palletType.getCode());
                    palletInfo.setTypeName(palletType.getName());
                    Result result = add(palletInfo);
                    if((Integer)result.get("code")==200){
                        insertCount++;
                    }
                }else if(oldInfos.size()==1){
                    PalletInfo saveInfo = oldInfos.get(0);
                    saveInfo.setRfidCode(palletInfo.getRfidCode());
                    saveInfo.setName(palletInfo.getName());
                    saveInfo.setTypeCode(palletType.getCode());
                    saveInfo.setTypeName(palletType.getName());

                    Result result = edit(saveInfo);
                    if((Integer)result.get("code")==200){
                        updateCount++;
                    }
                }
            }
        }else{
            //不更新数据，如果重复直接跳过
            //更新存在的旧数据
            //更新存在的旧数据
            for(PalletInfo palletInfo :palletInfos){
                //非空校验，有字段为空则跳过
                if((palletInfo.getCode()==null||"".equals(palletInfo.getCode()))
                        ||(palletInfo.getRfidCode()==null||"".equals(palletInfo.getRfidCode()))
                        ||(palletInfo.getName()==null||"".equals(palletInfo.getName()))){
                    continue;
                }
                Condition condition = new Condition(PalletInfo.class);
                condition.createCriteria().andEqualTo("code",palletInfo.getCode());
                List<PalletInfo> oldInfos = palletInfoService.findByCondition(condition);
                if(oldInfos.size()==0){
                    palletInfo.setTypeCode(palletType.getCode());
                    palletInfo.setTypeName(palletType.getName());
                    Result result = add(palletInfo);
                    if((Integer)result.get("code")==200){
                        insertCount++;
                    }
                }
            }
        }
        failCount = taskCount-insertCount-updateCount;
        String msg = "任务总数："+taskCount+"，成功插入数据："+insertCount+"条，成功更新数据："+updateCount+"条，数据处理失败："+failCount+"条。";
        return success(msg);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<PalletInfo> util = new ExcelUtil<PalletInfo>(PalletInfo.class);
        util.importTemplateExcel(response,"托盘信息");
    }

}
