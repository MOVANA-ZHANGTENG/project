package com.deer.wcs.task.model;

import lombok.Data;
import java.util.List;

/**
 * 任务统计DTO
 * @author deer
 * @date 2024-10-17
 */
@Data
public class TaskStatisticsDto {
    
    /**
     * 日期列表（格式：MM-dd）
     */
    private List<String> dates;
    
    /**
     * 任务类型统计列表
     */
    private List<TaskTypeStatistics> taskTypes;
    
    @Data
    public static class TaskTypeStatistics {
        /**
         * 任务类型编码
         */
        private String code;
        
        /**
         * 任务类型名称
         */
        private String name;
        
        /**
         * 每天的数量（与dates数组对应）
         */
        private List<Integer> data;
    }
}

