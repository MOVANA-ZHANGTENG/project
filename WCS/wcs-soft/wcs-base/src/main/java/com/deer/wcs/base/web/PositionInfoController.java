package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 站台Controller
 * 
 * @author deer
 * @date 2024-04-28
 */
@Api("站台")
@RestController
@RequestMapping("/wcs-base/PositionInfo")
public class PositionInfoController extends BaseController
{
    @Autowired
    private PositionInfoService positionInfoService;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private LineInfoService lineInfoService;
    @Autowired
    private WareInfoService wareInfoService;



    @GetMapping("/sf")
    public Result sf(Long id )
    {
        PositionInfo positionInfo = new PositionInfo();
        positionInfo.setInvenState(0L);
        positionInfo.setTaskState(0L);
        positionInfo.setId(id);
        positionInfoService.update(positionInfo);
        return success( );
    }

    /**
     * 查询站台列表
     */
    @ApiOperation("查询站台列表") 
    @GetMapping("/list")
    public TableDataInfo list(PositionInfoCriteria Criteria)
    {
        startPage();
        List<PositionInfoDto> list = positionInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 查询站台列表
     */
    @GetMapping("/getAllPositionByWareCode")
    public Result getAllPositionByWareCode( String wareCode)
    {
        Condition condition = new Condition(PositionInfo.class);
        condition.createCriteria().andEqualTo("wareCode",wareCode);
        List<PositionInfo> list = positionInfoService.findByCondition(condition );

        Condition condition2 = new Condition(LineInfo.class);
        condition2.createCriteria().andEqualTo("wareCode",wareCode);
        List<LineInfo> list2 = lineInfoService.findByCondition(condition2 );

        List<Map> mapList = new ArrayList<>();
        for (PositionInfo positionInfo:list) {
            Map<String,Object> map = new HashMap<>();
            map.put("code",positionInfo.getCode());
            map.put("code",positionInfo.getName());
            map.put("type",positionInfo.getType());
            mapList.add(map);
        }
        for (LineInfo line:list2) {
            Map<String,Object> map = new HashMap<>();
            map.put("code",line.getCode());
            map.put("code",line.getName());
            map.put("type",line.getType());
            mapList.add(map);
        }
        return success(mapList);
    }

    /**
     * 根据编码查询设备详情
     */
    @ApiOperation("根据编码查询站台详情")
    @GetMapping("/getPositionDetail")
    public Result getPositionDetail(PositionInfoCriteria Criteria)
    {
        Condition condition = new Condition(PositionInfo.class);
        condition.createCriteria().andEqualTo("code",Criteria.getCode());
        List<PositionInfo> positionInfos = positionInfoService.findByCondition(condition);
        if(positionInfos.size()==1){
            return success(positionInfos.get(0));
        }else{
            return Result.error();
        }
    }

    /**
     * 导出站台列表
     */
    @ApiOperation("导出站台列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionInfo:export')")
    @Log(title = "站台", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PositionInfoCriteria criteria)
    {
        List<PositionInfoDto> list = positionInfoService.findList(criteria);
        ExcelUtil<PositionInfoDto> util = new ExcelUtil<PositionInfoDto>(PositionInfoDto.class);
        util.exportExcel(response, list, "站台数据");
    }

    /**
     * 获取站台详细信息
     */
    @ApiOperation("获取站台详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(positionInfoService.findById(id));
    }

    @Autowired
    private ProPositionContentService proPositionContentService;
    /**
     * 新增站台
     */
    @ApiOperation("新增站台")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionInfo:add')")
    @Log(title = "站台", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody PositionInfo positionInfo)
    {

        Condition repeatCode = new Condition(PositionInfo.class);
        repeatCode.createCriteria().andEqualTo("wareCode",positionInfo.getWareCode())
                .andEqualTo("code",positionInfo.getCode());
        List<PositionInfo> repeatCodes = positionInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            return error("站台编码重复");
        }

//        Condition repeatName = new Condition(PositionInfo.class);
//        repeatName.createCriteria().andEqualTo("wareCode",positionInfo.getWareCode())
//                .andEqualTo("name",positionInfo.getName());
//        List<PositionInfo> repeatNames = positionInfoService.findByCondition(repeatName);
//        if(repeatNames.size()>0){
//            return error("站台名称重复");
//        }

        // 赋值仓库名称
        if(positionInfo.getWareCode()!=null&&!"".equals(positionInfo.getWareCode())){
            List<WareInfoDto> wareInfos = wareInfoService.findAllWareInfos();
            positionInfo.setWareName(wareInfos.stream().filter(s->s.getCode().equals(positionInfo.getWareCode())).collect(Collectors.toList()).get(0).getName());
        }

        positionInfo.setCreateTime(DateUtil.getNowDateTimeString());
        positionInfo.setCreateUserId(getUserId());
        positionInfo.setCreateUserName(getUsername());

        positionInfoService.save(positionInfo);

        try{
            ProPositionContent proPositionContent = new ProPositionContent();
            proPositionContent.setPositionId(positionInfo.getId());
            proPositionContent.setCode(positionInfo.getCode());
            proPositionContent.setWareCode(positionInfo.getWareCode());
            proPositionContentService.save(proPositionContent);
        }catch (Exception ex){

        }
        return toAjax(true);
    }

    @Autowired
    private PlcReadStationService plcReadStationService;

    /**
     * 修改站台
     */
    @ApiOperation("修改站台")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionInfo:edit')")
    @Log(title = "站台", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody PositionInfo positionInfo)
    {
        Condition repeatCode = new Condition(PositionInfo.class);
        repeatCode.createCriteria().andEqualTo("wareCode",positionInfo.getWareCode())
                .andEqualTo("code",positionInfo.getCode());
        List<PositionInfo> repeatCodes = positionInfoService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            if(repeatCodes.size()==1 && repeatCodes.get(0).getId().longValue() != positionInfo.getId().longValue()){
                return error("站台编码重复");
            }else if(repeatCodes.size()>1){
                return error("站台编码重复");
            }
        }

         positionInfo.setUpdateTime(DateUtil.getNowDateTimeString());
         positionInfo.setUpdateUserId(getUserId());
         positionInfo.setUpdateUserName(getUsername());

         // 赋值仓库名称
        if(positionInfo.getWareCode()!=null&&!"".equals(positionInfo.getWareCode())){
            List<WareInfoDto> wareInfos = wareInfoService.findAllWareInfos();
            positionInfo.setWareName(wareInfos.stream().filter(s->s.getCode().equals(positionInfo.getWareCode())).collect(Collectors.toList()).get(0).getName());
        }

//         PlcReadStation plcReadStation = plcReadStationService.findBy("code", positionInfo.getCode());
//         if (plcReadStation != null && plcReadStation.getDisableState() != positionInfo.getDisableState()){
//             plcReadStation.setDisableState(positionInfo.getDisableState());
//             plcReadStationService.update(plcReadStation);
//         }
//
//        try {
//             ProPositionContent proPositionContent = proPositionContentService.findBy("positionId",positionInfo.getId());
//             if(proPositionContent!=null){
//                 proPositionContent.setCode(positionInfo.getCode());
//                 proPositionContentService.update(proPositionContent);
//             }
//         }catch (Exception ex){
//            ex.printStackTrace();
//         }


        return toAjax(positionInfoService.update(positionInfo));
    }

    /**
     * 删除站台
     */
    @ApiOperation("删除站台")
    @PreAuthorize("@ss.hasPermi('wcs-base:PositionInfo:remove')")
    @Log(title = "站台", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        if(Integer.parseInt(sysConfigService.selectConfigByKey("soft_delete"))==0){
            for(Long id:ids){
                PositionInfo positionInfo = positionInfoService.findById(id);
                if(positionInfo!=null){
                    if(positionInfo.getIsDelete()==1){
                        return toAjax(positionInfoService.deletePositionInfoByIds(ids));
                    }
                    positionInfo.setIsDelete(1);
                    positionInfoService.update(positionInfo);
                }
            }
            return Result.success();
        }else{
            return toAjax(positionInfoService.deletePositionInfoByIds(ids));
        }
    }


}
