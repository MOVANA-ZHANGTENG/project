package com.deer.wcs.task.service;

import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.task.model.TaskInfoHistory;
import com.deer.wcs.task.model.TaskInfoHistoryCriteria;
import com.deer.wcs.task.model.TaskInfoHistoryDto;

import java.util.List;

/**
 * 任务历史Service接口
 * 
 * @author deer
 * @date 2024-06-04
 */
public interface TaskInfoHistoryService   extends Service<TaskInfoHistory, Long>
{
    /**
     * 查询任务历史
     *
     * @param id 任务历史主键
     * @return 任务历史
     */
    public TaskInfoHistory selectTaskInfoHistoryById(Long id);

    /**
     * 查询任务历史列表
     * 
     * @param criteria
     * @return 任务历史集合
     */
    public List<TaskInfoHistoryDto> findList(TaskInfoHistoryCriteria criteria);

    /**
     * 新增任务历史
     *
     * @param taskInfoHistory 任务历史
     * @return 结果
     */
    public int insertTaskInfoHistory(TaskInfoHistory taskInfoHistory);

    /**
     * 修改任务历史
     *
     * @param taskInfoHistory 任务历史
     * @return 结果
     */
    public int updateTaskInfoHistory(TaskInfoHistory taskInfoHistory);

    /**
     * 批量删除任务历史
     * 
     * @param ids 需要删除的任务历史主键集合
     * @return 结果
     */
    public int deleteTaskInfoHistoryByIds(Long[] ids);

    /**
     * 删除任务历史信息
     * 
     * @param id 任务历史主键
     * @return 结果
     */
    public int deleteTaskInfoHistoryById(Long id);

    Integer findByTimeAllTask(String beginTime, String endTime,String classTime,Integer currentHour);

    Integer findByTimeAllTask2(String beginTime, String endTime);
}
