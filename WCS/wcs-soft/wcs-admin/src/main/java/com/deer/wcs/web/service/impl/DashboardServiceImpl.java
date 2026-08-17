package com.deer.wcs.web.service.impl;

import com.deer.wcs.web.service.IDashboardService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements IDashboardService {
    @Override
    public Map<String, Object> getTaskStats(String wareCode) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> getDeviceStats(String wareCode) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> getCellStats(String wareCode) {
        return Collections.emptyMap();
    }

    @Override
    public List<Map<String, Object>> getRecentTasks(String wareCode, Integer limit) {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getDeviceStatus(String wareCode, Integer limit) {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> getTaskTypeTrend(String wareCode) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> getOverview(String wareCode) {
        return Collections.emptyMap();
    }
}
