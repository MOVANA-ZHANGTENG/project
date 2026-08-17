package com.deer.wcs.rcs.web;

import com.deer.wcs.common.utils.DateUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.deer.wcs.rcs.model.RcsCarPath;
import com.deer.wcs.rcs.service.RcsCarPathService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.model.RcsCarInfoDto;
import com.deer.wcs.rcs.model.RcsCarInfoCriteria;
import com.deer.wcs.rcs.service.RcsCarInfoService;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import tk.mybatis.mapper.entity.Condition;

/**
 * 四向车/AGVController
 * 
 * @author deer
 * @date 2025-10-14
 */
@Api("四向车/AGV")
@RestController
@RequestMapping("/wcs-rcs/RcsCarInfo")
public class RcsCarInfoController extends BaseController
{
    @Autowired
    private RcsCarInfoService rcsCarInfoService;

    /**
     * 查询四向车/AGV列表
     */
    @ApiOperation("查询四向车/AGV列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(RcsCarInfoCriteria Criteria)
    {
        startPage();
        List<RcsCarInfoDto> list = rcsCarInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出四向车/AGV列表
     */
    @ApiOperation("导出四向车/AGV列表")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarInfo:export')")
    @Log(title = "四向车/AGV", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RcsCarInfoCriteria criteria)
    {
        List<RcsCarInfoDto> list = rcsCarInfoService.findList(criteria);
        ExcelUtil<RcsCarInfoDto> util = new ExcelUtil<RcsCarInfoDto>(RcsCarInfoDto.class);
        util.exportExcel(response, list, "四向车/AGV数据");
    }

    /**
     * 获取四向车/AGV详细信息
     */
    @ApiOperation("获取四向车/AGV详细信息")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return success(rcsCarInfoService.findById(id));
    }

    /**
     * 新增四向车/AGV
     */
    @ApiOperation("新增四向车/AGV")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarInfo:add')")
    @Log(title = "四向车/AGV", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody RcsCarInfo rcsCarInfo)
    {
        rcsCarInfo.setCreateTime(DateUtil.getNowDateTimeString());
        rcsCarInfo.setCreateUserId(getUserId());
        rcsCarInfo.setCreateUserName(getUsername());

        rcsCarInfoService.save(rcsCarInfo);
        return toAjax(true);
    }

    /**
     * 修改四向车/AGV
     */
    @ApiOperation("修改四向车/AGV")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarInfo:edit')")
    @Log(title = "四向车/AGV", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody RcsCarInfo rcsCarInfo)
    {

         rcsCarInfo.setUpdateUserId(getUserId());
         rcsCarInfo.setUpdateUserName(getUsername());
         rcsCarInfo.setUpdateTime(DateUtil.getNowDateTimeString());


        return toAjax(rcsCarInfoService.update(rcsCarInfo));
    }

    /**
     * 查询所有小车的实时位置
     */
    @ApiOperation("查询所有小车实时位置")
    @GetMapping("/positions")
    public Result getCarPositions(RcsCarInfoCriteria criteria)
    {
        // 查询小车列表，可以根据条件过滤
        List<RcsCarInfoDto> list = rcsCarInfoService.findList(criteria);
        
        // 只返回有位置信息的小车
        List<RcsCarInfoDto> carsWithPosition = new ArrayList<>();
        for (RcsCarInfoDto car : list) {
            if (car.getCurrentX() != null && car.getCurrentY() != null) {
                carsWithPosition.add(car);
            }
        }
        
        return success(carsWithPosition);
    }
    
    /**
     * 查询WebSocket在线用户
     */
    @ApiOperation("查询在线监控用户")
    @GetMapping("/websocket/onlineUsers")
    public Result getOnlineUsers()
    {
        List<Map<String, Object>> users = com.deer.wcs.rcs.websocket.MonitorWebSocketHandler.getOnlineUsers();
        Map<String, Object> result = new HashMap<>();
        result.put("users", users);
        result.put("count", users.size());
        return success(result);
    }

    @Autowired
    private RcsCarPathService rcsCarPathService;

    /**
     * 获取路径状态（监控模式实时刷新）
     * 
     * @return 路径状态列表（前端自己过滤楼层）
     */
    @ApiOperation("获取路径状态")
    @GetMapping("/paths/status")
    public Result getPathsStatus()
    {
        List<RcsCarPath> paths = rcsCarPathService.findAll( );
        return success(paths);
    }
    
    /**
     * 修改小车位置（静止状态）
     * 同时更新fromCellCode和toCellCode为相同值，表示小车静止在该库位
     */
    @ApiOperation("修改小车位置（静止状态）")
    @Log(title = "四向车/AGV-修改位置", businessType = BusinessType.UPDATE)
    @PostMapping("/updateFromCellCode")
    public Result updateFromCellCode(@RequestBody Map<String, Object> params)
    {
        try {
            Long carId = params.get("carId") != null ? Long.valueOf(params.get("carId").toString()) : null;
            String carCode = params.get("carCode") != null ? params.get("carCode").toString() : null;
            String fromCellCode = params.get("fromCellCode") != null ? params.get("fromCellCode").toString() : null;

            if (carId == null && carCode == null) {
                return error("小车ID或编码不能为空");
            }

            if (fromCellCode == null || fromCellCode.trim().isEmpty()) {
                return error("起点库位编码不能为空");
            }

            // 调用服务层方法更新
            boolean result = rcsCarInfoService.updateFromCellCode(carId, carCode, fromCellCode);

            if (result) {
                return success("小车位置修改成功");
            } else {
                return error("小车位置修改失败");
            }
        } catch (Exception e) {
            logger.error("修改小车位置失败", e);
            return error("修改失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除四向车/AGV
     */
    @ApiOperation("删除四向车/AGV")
    //@PreAuthorize("@ss.hasPermi('wcs-rcs:RcsCarInfo:remove')")
    @Log(title = "四向车/AGV", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        return toAjax(rcsCarInfoService.deleteRcsCarInfoByIds(ids));
    }
}
