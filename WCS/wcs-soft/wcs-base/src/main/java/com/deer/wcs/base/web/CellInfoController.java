package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.*;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.bean.BeanUtils;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.system.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库位Controller
 *
 * @author deer
 * @date 2024-04-28
 */
@Api("库位")
@RestController
@RequestMapping("/wcs-base/CellInfo")
public class CellInfoController extends BaseController {
    @Autowired
    private CellInfoService cellInfoService;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private WareInfoService wareInfoService;
    @Autowired
    private AreaInfoService areaInfoService;
    @Autowired
    private LineInfoService lineInfoService;
    @Autowired
    private FloorInfoService floorInfoService;

    /**
     * 查询库位列表
     */
    @ApiOperation("查询库位列表")
    @GetMapping("/list")
    public TableDataInfo list(CellInfoCriteria Criteria) {
        startPage();
        List<CellInfoDto> list = cellInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 为3D显示屏查询库位列表，返回库位信息和托盘信息
     * @param wareCode 仓库编码
     * @return  包含库位信息和托盘信息的列表
     */
    @GetMapping("find3dInventory")
    public Result find3dInventory(String wareCode) {
        List<CellInfoDto> list = cellInfoService.find3dInventory(wareCode);
        return success(list);
    }

    @GetMapping("/findByLineCode")
    public Result findByLineCode(String  lineCode)
    {
        CellInfoCriteria cellInfoCriteria = new CellInfoCriteria();
        cellInfoCriteria.setLineCode(lineCode);
        List<CellInfoDto> list = cellInfoService.findList(cellInfoCriteria);
        LineInfo lineInfo = lineInfoService.findBy("code", lineCode);
        Map<String,Object> map = new HashMap<>();
        map.put("list",list);
        map.put("lineInfo",lineInfo);
        return success(map);
    }

    /**
     * 根据仓库和巷道编码查询库位列表（关联托盘信息）
     */
    @ApiOperation("根据仓库和巷道编码查询库位列表（关联托盘信息）")
    @GetMapping("/findByLineCodeWithPallet")
    public Result findByLineCodeWithPallet(@RequestParam String wareCode, @RequestParam String lineCode)
    {
        Map<String, Object> result = cellInfoService.findByLineCodeWithPallet(wareCode, lineCode);
        return success(result);
    }


    /**
     * 获取库位详细信息
     */
    @ApiOperation("获取库位详细信息")
    @GetMapping("/getByCode")
    public Result getByCode(String code,String wareCode )
    {
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria().andEqualTo("code",code).andEqualTo("wareCode",wareCode);
        List<CellInfo> cellInfos = cellInfoService.findByCondition(condition);
        if(!cellInfos.isEmpty()){
            return success(cellInfos.get(0));
        }
        return success(null);
    }

    /**
     * 获取库位详细信息（包含托盘信息）
     */
    @ApiOperation("获取库位详细信息（包含托盘信息）")
    @GetMapping("/getByCodeWithPallet")
    public Result getByCodeWithPallet(String code, String wareCode)
    {
        // 查询库位信息
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria().andEqualTo("code", code).andEqualTo("wareCode", wareCode);
        List<CellInfo> cellInfos = cellInfoService.findByCondition(condition);
        
        if(cellInfos.isEmpty()){
            return success(null);
        }
        
        CellInfo cellInfo = cellInfos.get(0);
        
        // 转换为 CellInfoDto，使用 BeanUtils 复制属性
        CellInfoDto cellInfoDto = new CellInfoDto();
        BeanUtils.copyProperties(cellInfo, cellInfoDto);
        
        // 查询该库位下的托盘信息
        Condition palletCondition = new Condition(PalletInfo.class);
        palletCondition.createCriteria()
                .andEqualTo("wareCode", wareCode)
                .andEqualTo("cellCode", code);
        List<PalletInfo> palletInfos = palletInfoService.findByCondition(palletCondition);
        
        // 如果存在托盘，设置第一个托盘的信息（通常一个库位只有一个托盘）
        if (!palletInfos.isEmpty()) {
            PalletInfo palletInfo = palletInfos.get(0);
            cellInfoDto.setPalletCode(palletInfo.getCode());
            cellInfoDto.setIsEmpty(palletInfo.getIsEmpty());
        } else {
            cellInfoDto.setPalletCode(null);
            cellInfoDto.setIsEmpty(null);
        }
        
        return success(cellInfoDto);
    }

    @GetMapping("/deleteByCode")
    public Result deleteByCode(String code,String wareCode)
    {
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria().andEqualTo("code",code).andEqualTo("wareCode",wareCode);
        List<CellInfo> cellInfos = cellInfoService.findByCondition(condition);
        for (CellInfo cellInfo : cellInfos) {
            cellInfoService.deleteById(cellInfo.getId());
        }
        return success( );
    }


    @PostMapping("/batchAddSxcCell")
    public Result batchAddSxcCell(@RequestBody BatchAddCell addCell) {
        //排查是否已经创建过
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria().andEqualTo("wareCode",addCell.getWareCode())
//                .andEqualTo("areaCode",addCell.getAreaCode())
                .andEqualTo("x",addCell.getX())
                .andEqualTo("y",addCell.getY())
                .andEqualTo("z",addCell.getZ());
        List<CellInfo> cellInfos = cellInfoService.findByCondition(condition);
        if(!cellInfos.isEmpty()){
            return Result.error("重复添加货位");
        }
        String wareCode = addCell.getWareCode();
        WareInfo wareInfo = wareInfoService.findBy("code",wareCode);
        if(wareInfo == null)
        {
            return error("仓库不存在");
        }
        FloorInfo floorInfo = floorInfoService.findByZ(addCell.getWareCode(), addCell.getZ());
        if(floorInfo == null){
            floorInfo = new FloorInfo();
            floorInfo.setWareCode(addCell.getWareCode());
            floorInfo.setWareName(wareInfo.getName());
            floorInfo.setZ(addCell.getZ());
            floorInfo.setTotalX(addCell.getX());
            floorInfo.setTotalY(addCell.getY());
            floorInfo.setXy(addCell.getXy());
            floorInfoService.save(floorInfo);
        }


        Integer z = addCell.getZ();
        ;
        for (int y = 1; y <= addCell.getY(); y++
        ) {
            for (int x = 1; x <= addCell.getX(); x++
            ) {
                CellInfo cellInfo = new CellInfo();
                cellInfo.setX(x);
                cellInfo.setY(y);
                cellInfo.setZ(z);
                cellInfo.setCode(z + "-" + x + "-" + y);
                cellInfo.setHostCode(z + "-" + x + "-" + y);
                cellInfo.setSubCode(z + "-" + x + "-" + y);
                cellInfo.setName(cellInfo.getCode());
                cellInfo.setWareCode(wareCode);
                cellInfo.setWareName(wareInfo.getName());
                cellInfo.setCreateTime(DateUtil.getNowDateTimeString());
                cellInfo.setCreateUserId(getUserId());
                cellInfo.setCreateUserName(getUsername());
                cellInfo.setType(0);
//                cellInfo.setIsMove(0);
                cellInfoService.save(cellInfo);
            }
        }

        return toAjax(true);
    }


    /**
     * 导出库位列表
     */
    @ApiOperation("导出库位列表")
    @Log(title = "库位", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CellInfoCriteria criteria) {
        List<CellInfoDto> list = cellInfoService.findList(criteria);
        ExcelUtil<CellInfoDto> util = new ExcelUtil<CellInfoDto>(CellInfoDto.class);
        util.exportExcel(response, list, "库位数据");
    }

    /**
     * 获取库位详细信息
     */
    @ApiOperation("获取库位详细信息")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(cellInfoService.findById(id));
    }

    /**
     * 新增库位
     */
    @ApiOperation("新增库位")
    @Log(title = "库位", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody CellInfo cellInfo) {

        Condition repeatCode = new Condition(CellInfo.class);
        repeatCode.createCriteria().andEqualTo("code",cellInfo.getCode())
//                .andEqualTo("lineCode",cellInfo.getLineCode())
//                .andEqualTo("areaCode",cellInfo.getAreaCode())
                .andEqualTo("wareCode",cellInfo.getWareCode()) ;
        List<CellInfo> repeatCodes = cellInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            return error("库位编码重复");
        }



        cellInfo.setCreateTime(DateUtil.getNowDateTimeString());
        cellInfo.setCreateUserId(getUserId());
        cellInfo.setCreateUserName(getUsername());
        String lineCode = cellInfo.getLineCode();
        LineInfo lineInfo = lineInfoService.findBy("code", lineCode);
        cellInfo.setWareCode(lineInfo.getWareCode());
        cellInfo.setWareName(lineInfo.getWareName());
        cellInfo.setAreaCode(lineInfo.getAreaCode());
        cellInfo.setAreaName(lineInfo.getAreaName());
        cellInfo.setLineCode(lineInfo.getCode());
        cellInfo.setLineName(lineInfo.getName());
        cellInfoService.save(cellInfo);
        return toAjax(true);
    }


    /**
     * 新增库位
     */
    @ApiOperation("批量新增库位")
    @Log(title = "批量新增库位", businessType = BusinessType.INSERT)
    @PostMapping("/batchAddCell")
    public Result batchAddCell(@RequestBody BatchAddCell addCell) {
        //排查是否已经创建过
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria().andEqualTo("wareCode",addCell.getWareCode())
//                .andEqualTo("areaCode",addCell.getAreaCode())
                .andEqualTo("ab",addCell.getAb())
                .andEqualTo("lineCode",addCell.getLineCode())
                .andEqualTo("priority",addCell.getPriority());
        List<CellInfo> cellInfos = cellInfoService.findByCondition(condition);
        if(cellInfos.size()>0){
            return Result.error("重复添加货位");
        }

        String lineCode = addCell.getLineCode();
        LineInfo lineInfo = lineInfoService.findBy("code", lineCode);
        if (lineInfo.getMaxZ() == null || lineInfo.getMaxZ() < addCell.getZ()) {
            lineInfo.setMaxZ(addCell.getZ());
            lineInfoService.update(lineInfo);
        }
        String areaCode = lineInfo.getAreaCode();
        AreaInfo areaInfo = areaInfoService.findBy("code", areaCode);
        String wareCode = lineInfo.getWareCode();
        WareInfo wareInfo = wareInfoService.findBy("code", wareCode);

        Integer x = addCell.getX();
        Integer subCode=0;
        if(wareCode.equals("zlk") && addCell.getAb().equals("A") ){
            subCode=1;
        }
        if(wareCode.equals("zlk") && addCell.getAb().equals("B") ){
            subCode=161;
        }
        if(wareCode.equals("mlk") && addCell.getAb().equals("A") ){
            subCode=1;
        }
        if(wareCode.equals("mlk") && addCell.getAb().equals("B") ){
            subCode=291;
        }
        for (int z = 1; z <= addCell.getZ(); z++
        ) {
            for (int y = 1; y <= addCell.getY(); y++
            ) {
                CellInfo cellInfo = new CellInfo();
                cellInfo.setX(x);
                cellInfo.setY(y);
                cellInfo.setZ(z);
                cellInfo.setPriority(addCell.getPriority());
                cellInfo.setAb(addCell.getAb());
                cellInfo.setCode(x + "-" + y + "-" + z);
                cellInfo.setHostCode(x + "-" + y + "-" + z);
                cellInfo.setSubCode(subCode.toString());
                cellInfo.setName(cellInfo.getCode());
                cellInfo.setWareCode(wareCode);
                cellInfo.setWareName(lineInfo.getWareName());
                cellInfo.setAreaCode(lineInfo.getAreaCode());
                cellInfo.setAreaName(lineInfo.getAreaName());
                cellInfo.setLineCode(lineInfo.getCode());
                cellInfo.setLineName(lineInfo.getName());
                cellInfo.setCreateTime(DateUtil.getNowDateTimeString());
                cellInfo.setCreateUserId(getUserId());
                cellInfo.setCreateUserName(getUsername());
                cellInfoService.save(cellInfo);
                subCode++;
            }
        }

        return toAjax(true);
    }

    @Autowired
    private PalletInfoService palletInfoService;

    /**
     * 修改库位
     */
    @ApiOperation("修改库位")
    @Log(title = "库位", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody CellInfo cellInfo) {

        Condition repeatCode = new Condition(CellInfo.class);
        repeatCode.createCriteria().andEqualTo("code",cellInfo.getCode())
                .andEqualTo("wareCode",cellInfo.getWareCode())
                .andEqualTo("isDelete",0);
        List<CellInfo> repeatCodes = cellInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            if(repeatCodes.size()==1 && repeatCodes.get(0).getId().longValue() != cellInfo.getId().longValue()){
                return error("库位编码重复");
            }else if(repeatCodes.size()>1){
                return error("库位编码重复");
            }
        }

//        Condition repeatName = new Condition(CellInfo.class);
//        repeatName.createCriteria().andEqualTo("name",cellInfo.getName())
//                .andEqualTo("wareCode",cellInfo.getWareCode())
//                .andEqualTo("isDelete",0);
//        List<CellInfo> repeatNames = cellInfoService.findByCondition(repeatName);
//        if(repeatNames.size()>0){
//            if(repeatNames.size()==1 && repeatNames.get(0).getId().longValue() != cellInfo.getId().longValue()){
//                return error("库位名称重复");
//            }else if(repeatNames.size()>1){
//                return error("库位名称重复");
//            }
//        }

        cellInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        cellInfo.setUpdateUserId(getUserId());
        cellInfo.setUpdateUserName(getUsername());


        PalletInfo palletInfo = palletInfoService.findByCode(cellInfo.getWareCode(), cellInfo.getCode());
        if(cellInfo.getInvenState()>0.9 && palletInfo!=null){
            return error("数据库显示，该库位已被占用，托盘号："+palletInfo.getCode()+" 请先释放托盘，改一下托盘表的cell_code为空");
        }

        return toAjax(cellInfoService.update(cellInfo));
    }

    /**
     * 编辑模式：更新库位配置（type、preCode、subX、subY、subZ等）
     */
    @ApiOperation("编辑模式-更新库位配置")
    @Log(title = "库位配置", businessType = BusinessType.UPDATE)
    @PutMapping("/updateConfig")
    public Result updateConfig(@RequestBody CellInfo cellInfo) {
        // 检查库位是否存在
        CellInfo existCell = cellInfoService.findById(cellInfo.getId());
        if (existCell == null) {
            return error("库位不存在");
        }

        // 只更新配置相关字段
        existCell.setType(cellInfo.getType());
        existCell.setPreCode(cellInfo.getPreCode());
        existCell.setSubX(cellInfo.getSubX());
        existCell.setSubY(cellInfo.getSubY());
        existCell.setSubZ(cellInfo.getSubZ());
        
        // 设置更新信息
        existCell.setUpdateTime(DateUtil.getNowDateTimeString());
        existCell.setUpdateUserId(getUserId());
        existCell.setUpdateUserName(getUsername());

        return toAjax(cellInfoService.update(existCell));
    }

    /**
     * 更新货位状态
     */
    @ApiOperation("更新货位状态")
    @Log(title = "库位", businessType = BusinessType.UPDATE)
    @PostMapping("/updateCellInfoState")
    public Result updateCellInfoState(@RequestBody CellInfo cellInfo) {

        cellInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        cellInfo.setUpdateUserId(getUserId());
        cellInfo.setUpdateUserName(getUsername());
        LineInfo lineInfo = lineInfoService.findBy("code", cellInfo.getLineCode());
        if(lineInfo!=null){
            cellInfo.setWareCode(lineInfo.getWareCode());
            cellInfo.setWareName(lineInfo.getWareName());
            cellInfo.setAreaCode(lineInfo.getAreaCode());
            cellInfo.setAreaName(lineInfo.getAreaName());
            cellInfo.setLineCode(lineInfo.getCode());
            cellInfo.setLineName(lineInfo.getName());
        }

        // 如果库位的 invenState 为 0，则清空该库位所有托盘的 cellCode
        if(cellInfo.getInvenState() != null && cellInfo.getInvenState() == 0){
            Condition palletCondition = new Condition(PalletInfo.class);
            palletCondition.createCriteria()
                    .andEqualTo("wareCode", cellInfo.getWareCode())
                    .andEqualTo("cellCode", cellInfo.getCode());
            List<PalletInfo> palletInfos = palletInfoService.findByCondition(palletCondition);
            
            for (PalletInfo palletInfo : palletInfos) {
                palletInfo.setCellCode("");
                palletInfoService.update(palletInfo);
            }
        }

        return toAjax(cellInfoService.update(cellInfo));
    }

    /**
     * 更新货位状态和托盘信息
     */
    @ApiOperation("更新货位状态和托盘信息")
    @Log(title = "库位", businessType = BusinessType.UPDATE)
    @PostMapping("/updateCellInfoStateWithPallet")
    public Result updateCellInfoStateWithPallet(@RequestBody CellInfoDto cellInfoDto) {

        // 设置更新信息
        cellInfoDto.setUpdateTime(DateUtil.getNowDateTimeString());
        cellInfoDto.setUpdateUserId(getUserId());
        cellInfoDto.setUpdateUserName(getUsername());

        // 如果托盘号为空，清空该库位的托盘关联
        Condition palletCondition = new Condition(PalletInfo.class);
        palletCondition.createCriteria()
                .andEqualTo("wareCode", cellInfoDto.getWareCode())
                .andEqualTo("cellCode", cellInfoDto.getCode());
        List<PalletInfo> palletInfos = palletInfoService.findByCondition(palletCondition);

        for (PalletInfo palletInfo : palletInfos) {
            palletInfo.setCellCode("");
            palletInfoService.update(palletInfo);
        }
        
        // 获取巷道信息
        LineInfo lineInfo = lineInfoService.findBy("code", cellInfoDto.getLineCode());
        if(lineInfo != null){
            cellInfoDto.setWareCode(lineInfo.getWareCode());
            cellInfoDto.setWareName(lineInfo.getWareName());
            cellInfoDto.setAreaCode(lineInfo.getAreaCode());
            cellInfoDto.setAreaName(lineInfo.getAreaName());
            cellInfoDto.setLineCode(lineInfo.getCode());
            cellInfoDto.setLineName(lineInfo.getName());
        }

        String palletCode = cellInfoDto.getPalletCode();
        String isEmpty = cellInfoDto.getIsEmpty();

        if (palletCode != null && !palletCode.trim().isEmpty()) {
            // 如果提供了托盘号，更新或创建托盘信息
            PalletInfo palletInfo = palletInfoService.findByCode(cellInfoDto.getWareCode(), palletCode);

            if (palletInfo == null) {
                // 如果托盘不存在，创建新托盘
                palletInfo = new PalletInfo();
                palletInfo.setCode(palletCode);
                palletInfo.setWareCode(cellInfoDto.getWareCode());
                palletInfo.setCellCode(cellInfoDto.getCode());
                if (isEmpty != null && !isEmpty.trim().isEmpty()) {
                    palletInfo.setIsEmpty(isEmpty);
                } else {
                    palletInfo.setIsEmpty("0"); // 默认有货
                }
                palletInfoService.save(palletInfo);
            }
            palletInfo.setCellCode(cellInfoDto.getCode());
            if (isEmpty != null && !isEmpty.trim().isEmpty()) {
                palletInfo.setIsEmpty(isEmpty);
            }
            palletInfoService.update(palletInfo);
        }

        // 更新库位信息
        return toAjax(cellInfoService.update(cellInfoDto));
    }

    /**
     * 批量更新库位适用托盘类型
     */
    @ApiOperation("批量更新库位适用托盘类型")
    @Log(title = "库位", businessType = BusinessType.UPDATE)
    @PostMapping("/batchUpdatePalletType")
    public Result batchUpdatePalletType(@RequestBody BatchUpdateCellPalletType request) {
        if (request == null || request.getWareCode() == null || request.getWareCode().trim().isEmpty()) {
            return error("仓库编码不能为空");
        }
        if (request.getCellCodes() == null || request.getCellCodes().isEmpty()) {
            return error("请至少选择一个库位");
        }
        if (request.getPalletType() == null || request.getPalletType().trim().isEmpty()) {
            return error("适用托盘类型不能为空");
        }
        int count = cellInfoService.batchUpdatePalletType(
                request.getWareCode(),
                request.getCellCodes(),
                request.getPalletType(),
                getUserId(),
                getUsername());
        if (count <= 0) {
            return error("未更新任何库位，请检查库位编码是否正确");
        }
        return success("成功更新 " + count + " 个库位");
    }

    /**
     * 删除库位
     */
    @ApiOperation("删除库位")
    @PreAuthorize("@ss.hasPermi('wcs-base:CellInfo:remove')")
    @Log(title = "库位", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        return toAjax(cellInfoService.deleteCellInfoByIds(ids));
    }
}
