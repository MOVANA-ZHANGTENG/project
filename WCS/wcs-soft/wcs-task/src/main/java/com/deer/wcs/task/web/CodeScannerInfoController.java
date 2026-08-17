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
import com.deer.wcs.task.model.CodeScanner.CodeScannerInfo;
import com.deer.wcs.task.model.CodeScanner.CodeScannerInfoCriteria;
import com.deer.wcs.task.model.CodeScanner.CodeScannerInfoDto;
import com.deer.wcs.task.service.CodeScannerInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 扫码器Controller
 * 
 * @author deer
 * @date 2024-07-31
 */
@Api("扫码器")
@RestController
@RequestMapping("/wcs-task/smqInfo")
public class CodeScannerInfoController extends BaseController
{
    @Autowired
    private CodeScannerInfoService smqInfoService;

    @Resource
    private HandleService handleService;

    @Resource
    private WareInfoService wareInfoService;

    @Resource
    private PositionInfoService positionInfoService;

    /**
     * 查询扫码器列表
     */
    @ApiOperation("查询扫码器列表")
//    @PreAuthorize("@ss.hasPermi('wcs-task:smqInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(CodeScannerInfoCriteria Criteria)
    {
        startPage();
        List<CodeScannerInfoDto> list = smqInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出扫码器列表
     */
    @ApiOperation("导出扫码器列表")
//    @PreAuthorize("@ss.hasPermi('wcs-task:smqInfo:export')")
    @Log(title = "扫码器", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CodeScannerInfoCriteria criteria)
    {
        List<CodeScannerInfoDto> list = smqInfoService.findList(criteria);
        ExcelUtil<CodeScannerInfoDto> util = new ExcelUtil<CodeScannerInfoDto>(CodeScannerInfoDto.class);
        util.exportExcel(response, list, "扫码器数据");
    }

    /**
     * 获取扫码器详细信息
     */
    @ApiOperation("获取扫码器详细信息")
//    @PreAuthorize("@ss.hasPermi('wcs-task:smqInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(smqInfoService.findById(id));
    }

    /**
     * 新增扫码器
     */
    @ApiOperation("新增扫码器")
//    @PreAuthorize("@ss.hasPermi('wcs-task:codeScannerInfo:add')")
    @Log(title = "扫码器", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody CodeScannerInfo codeScannerInfo)
    {
        Condition condition = new Condition(CodeScannerInfo.class);
        condition.createCriteria().andEqualTo("code", codeScannerInfo.getCode()) ;
        List<CodeScannerInfo> codeScannerInfoList = smqInfoService.findByCondition(condition);
        if (codeScannerInfoList != null && !codeScannerInfoList.isEmpty()){
            return Result.error("不用添加重复的信息");
        }
        String wareCode = codeScannerInfo.getWareCode();
        WareInfo wareInfo =wareInfoService.findBy("code",wareCode);
        codeScannerInfo.setWareName(wareInfo.getName());
        String positionCode = codeScannerInfo.getPositionCode();
        PositionInfo positionInfo = positionInfoService.findBy("code", positionCode);
        if (positionInfo == null){
            return Result.error("添加的位置信息中不存在,请检查");
        }
        String wareCode1 = positionInfo.getWareCode();
        if (!wareCode1.equals(wareCode)){
            return Result.error("选择的仓库里面没有这个位置,请检查");
        }

        //检查编码和名称是否重复
        Condition repeatCode = new Condition(CodeScannerInfo.class);
        repeatCode.createCriteria()
                .andEqualTo("code",codeScannerInfo.getCode())
                .andEqualTo("delFlag",0);
        List<CodeScannerInfo> repeatCodes = smqInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            return error("扫码器编码重复");
        }

        Condition repeatName = new Condition(CodeScannerInfo.class);
        repeatName.createCriteria()
                .andEqualTo("name",codeScannerInfo.getName())
                .andEqualTo("delFlag",0);
        List<CodeScannerInfo> repeatNames = smqInfoService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            return error("扫码器名称重复");
        }

        smqInfoService.save(codeScannerInfo);
        return toAjax(true);
    }

    /**
     * 修改扫码器
     */
    @ApiOperation("修改扫码器")
    @PreAuthorize("@ss.hasPermi('wcs-task:codeScannerInfo:edit')")
    @Log(title = "扫码器", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody CodeScannerInfo codeScannerInfo)
    {
        Condition condition = new Condition(CodeScannerInfo.class);
        condition.createCriteria().andEqualTo("code", codeScannerInfo.getCode())
                .andEqualTo("name", codeScannerInfo.getName())  ;
        List<CodeScannerInfo> codeScannerInfoList = smqInfoService.findByCondition(condition);
        for (CodeScannerInfo codeScannerInfo1 : codeScannerInfoList) {
            if(!codeScannerInfo.getId().equals(codeScannerInfo1.getId())){
                return Result.error("不用修改成重复的信息");
            }
        }
        String wareCode = codeScannerInfo.getWareCode();
        WareInfo wareInfo =wareInfoService.findBy("code",wareCode);
        codeScannerInfo.setWareName(wareInfo.getName());
        String positionCode = codeScannerInfo.getPositionCode();
        PositionInfo positionInfo = positionInfoService.findBy("code", positionCode);
        if (positionInfo == null){
            return Result.error("添加的位置信息中不存在,请检查");
        }
        String wareCode1 = positionInfo.getWareCode();
        if (!wareCode1.equals(wareCode)){
            return Result.error("选择的仓库里面没有这个位置,请检查");
        }

        Condition repeatCode = new Condition(CodeScannerInfo.class);
        repeatCode.createCriteria()
                .andEqualTo("code",codeScannerInfo.getCode())
                .andEqualTo("delFlag",0);
        List<CodeScannerInfo> repeatCodes = smqInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            if(repeatCodes.size()==1 && repeatCodes.get(0).getId().longValue() != codeScannerInfo.getId().longValue()){
                return error("扫码器编码重复");
            }else if(repeatCodes.size()>1){
                return error("扫码器编码重复");
            }
        }

        Condition repeatName = new Condition(CodeScannerInfo.class);
        repeatName.createCriteria()
                .andEqualTo("name",codeScannerInfo.getName())
                .andEqualTo("delFlag",0);
        List<CodeScannerInfo> repeatNames = smqInfoService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            if(repeatNames.size()==1 && repeatNames.get(0).getId().longValue() != codeScannerInfo.getId().longValue()){
                return error("扫码器名称重复");
            }else if(repeatNames.size()>1){
                return error("扫码器名称重复");
            }
        }

        return toAjax(smqInfoService.update(codeScannerInfo));
    }

    @Autowired
    private ISysConfigService sysConfigService;
    /**
     * 删除扫码器
     */
    @ApiOperation("删除扫码器")
    @PreAuthorize("@ss.hasPermi('wcs-task:smqInfo:remove')")
    @Log(title = "扫码器", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        if (Integer.parseInt(sysConfigService.selectConfigByKey("soft_delete")) == 0) {
            for (Long id : ids) {
                CodeScannerInfo  codeScannerInfo = smqInfoService.findById(id);
                if (codeScannerInfo != null) {
                    codeScannerInfo.setDelFlag(1);
                    smqInfoService.update(codeScannerInfo);
                }
            }
            return Result.success();
        } else {
            return toAjax(smqInfoService.deleteSmqInfoByIds(ids));
        }
//        return toAjax(smqInfoService.deleteSmqInfoByIds(ids));
    }

    /**
     * 查询handle里面所有扫码器相关的
     */
    @GetMapping("/getSmqHandle")
    public Result getSmqHandle(){
        Condition condition = new Condition(Handle.class);
        condition.createCriteria().andEqualTo("type",3)
                .andEqualTo("handleType",1);
        List<Handle> handleList = handleService.findByCondition(condition);
        return Result.success(handleList);
    }
}
