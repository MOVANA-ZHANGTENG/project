package com.deer.wcs.task.model;

import lombok.Data;

/**
 * 任务基础统计DTO
 * @author deer
 * @date 2024-10-17
 */
@Data
public class TaskBasicStatisticsDto {
    
    /**
     * 总任务数
     */
    private Integer total;
    
    /**
     * 今日新增任务数
     */
    private Integer today;
    
    /**
     * 运行中任务数
     */
    private Integer running;
    
    /**
     * 已完成任务数
     */
    private Integer completed;
    
    /**
     * 失败任务数
     */
    private Integer failed;
}

