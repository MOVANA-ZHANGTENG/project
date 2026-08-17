package com.deer.wcs.web.controller.dashboard;

import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.web.service.IDashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 仪表板统计Controller
 * 
 * @author deer
 * @date 2024-01-15
 */
@Api("仪表板统计")
@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController
{
    @Autowired
    private IDashboardService dashboardService;

    /**
     * 获取任务统计信息
     */
    @ApiOperation("获取任务统计信息")
    @GetMapping("/taskStats")
    public Result getTaskStats(@ApiParam("仓库编码") @RequestParam(required = false) String wareCode)
    {
        Map<String, Object> taskStats = dashboardService.getTaskStats(wareCode);
        return Result.success(taskStats);
    }

    /**
     * 获取设备统计信息
     */
    @ApiOperation("获取设备统计信息")
    @GetMapping("/deviceStats")
    public Result getDeviceStats(@ApiParam("仓库编码") @RequestParam(required = false) String wareCode)
    {
        Map<String, Object> deviceStats = dashboardService.getDeviceStats(wareCode);
        return Result.success(deviceStats);
    }

    /**
     * 获取库位统计信息
     */
    @ApiOperation("获取库位统计信息")
    @GetMapping("/cellStats")
    public Result getCellStats(@ApiParam("仓库编码") @RequestParam(required = false) String wareCode)
    {
        Map<String, Object> cellStats = dashboardService.getCellStats(wareCode);
        return Result.success(cellStats);
    }

    /**
     * 获取最近任务列表
     */
    @ApiOperation("获取最近任务列表")
    @GetMapping("/recentTasks")
    public Result getRecentTasks(@ApiParam("仓库编码") @RequestParam(required = false) String wareCode,
                                @ApiParam("数量限制") @RequestParam(defaultValue = "10") Integer limit)
    {
        List<Map<String, Object>> recentTasks = dashboardService.getRecentTasks(wareCode, limit);
        return Result.success(recentTasks);
    }

    /**
     * 获取设备状态列表
     */
    @ApiOperation("获取设备状态列表")
    @GetMapping("/deviceStatus")
    public Result getDeviceStatus(@ApiParam("仓库编码") @RequestParam(required = false) String wareCode,
                                 @ApiParam("数量限制") @RequestParam(defaultValue = "20") Integer limit)
    {
        List<Map<String, Object>> deviceStatus = dashboardService.getDeviceStatus(wareCode, limit);
        return Result.success(deviceStatus);
    }

    /**
     * 获取7日内任务类型趋势数据
     */
    @ApiOperation("获取7日内任务类型趋势数据")
    @GetMapping("/taskTypeTrend")
    public Result getTaskTypeTrend(@ApiParam("仓库编码") @RequestParam(required = false) String wareCode)
    {
        Map<String, Object> trendData = dashboardService.getTaskTypeTrend(wareCode);
        return Result.success(trendData);
    }

    /**
     * 获取综合统计数据
     */
    @ApiOperation("获取综合统计数据")
    @GetMapping("/overview")
    public Result getOverview(@ApiParam("仓库编码") @RequestParam(required = false) String wareCode)
    {
        Map<String, Object> overview = dashboardService.getOverview(wareCode);
        return Result.success(overview);
    }
}
