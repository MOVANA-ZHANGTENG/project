package com.deer.wcs.web.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;

/**
 * 仪表板统计Service接口
 * 
 * @author deer
 * @date 2024-01-15
 */
@Service
public interface IDashboardService
{
    /**
     * 获取任务统计信息
     * 
     * @param wareCode 仓库编码
     * @return 任务统计信息
     */
    Map<String, Object> getTaskStats(String wareCode);

    /**
     * 获取设备统计信息
     * 
     * @param wareCode 仓库编码
     * @return 设备统计信息
     */
    Map<String, Object> getDeviceStats(String wareCode);

    /**
     * 获取库位统计信息
     * 
     * @param wareCode 仓库编码
     * @return 库位统计信息
     */
    Map<String, Object> getCellStats(String wareCode);

    /**
     * 获取最近任务列表
     * 
     * @param wareCode 仓库编码
     * @param limit 数量限制
     * @return 最近任务列表
     */
    List<Map<String, Object>> getRecentTasks(String wareCode, Integer limit);

    /**
     * 获取设备状态列表
     * 
     * @param wareCode 仓库编码
     * @param limit 数量限制
     * @return 设备状态列表
     */
    List<Map<String, Object>> getDeviceStatus(String wareCode, Integer limit);

    /**
     * 获取7日内任务类型趋势数据
     * 
     * @param wareCode 仓库编码
     * @return 趋势数据
     */
    Map<String, Object> getTaskTypeTrend(String wareCode);

    /**
     * 获取综合统计数据
     * 
     * @param wareCode 仓库编码
     * @return 综合统计数据
     */
    Map<String, Object> getOverview(String wareCode);
}
