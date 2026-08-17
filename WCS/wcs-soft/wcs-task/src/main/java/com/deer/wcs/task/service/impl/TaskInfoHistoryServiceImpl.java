package com.deer.wcs.task.service.impl;

import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.dao.TaskInfoHistoryMapper;
import com.deer.wcs.task.model.TaskInfoHistory;
import com.deer.wcs.task.model.TaskInfoHistoryCriteria;
import com.deer.wcs.task.model.TaskInfoHistoryDto;
import com.deer.wcs.task.service.TaskInfoHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务历史Service业务层处理
 * 
 * @author deer
 * @date 2024-06-04
 */
@Service
public class TaskInfoHistoryServiceImpl  extends AbstractService<TaskInfoHistory, Long>  implements TaskInfoHistoryService
{
    @Autowired
    private TaskInfoHistoryMapper taskInfoHistoryMapper;

    /**
     * 查询任务历史
     *
     * @param id 任务历史主键
     * @return 任务历史
     */
    @Override
    public TaskInfoHistory selectTaskInfoHistoryById(Long id)
    {
        return taskInfoHistoryMapper.selectTaskInfoHistoryById(id);
    }

    /**
     * 查询任务历史列表
     * 
     * @param criteria
     * @return 任务历史
     */
    @Override
    public List<TaskInfoHistoryDto> findList(TaskInfoHistoryCriteria criteria)
    {
        return taskInfoHistoryMapper.findList(criteria);
    }

    /**
     * 新增任务历史
     *
     * @param taskInfoHistory 任务历史
     * @return 结果
     */
    @Override
    public int insertTaskInfoHistory(TaskInfoHistory taskInfoHistory)
    {
        taskInfoHistory.setCreateTime(DateUtil.getNowDateTimeString());
        return taskInfoHistoryMapper.insertTaskInfoHistory(taskInfoHistory);
    }

    /**
     * 修改任务历史
     *
     * @param taskInfoHistory 任务历史
     * @return 结果
     */
    @Override
    public int updateTaskInfoHistory(TaskInfoHistory taskInfoHistory)
    {
        return taskInfoHistoryMapper.updateTaskInfoHistory(taskInfoHistory);
    }

    /**
     * 批量删除任务历史
     * 
     * @param ids 需要删除的任务历史主键
     * @return 结果
     */
    @Override
    public int deleteTaskInfoHistoryByIds(Long[] ids)
    {
        return taskInfoHistoryMapper.deleteTaskInfoHistoryByIds(ids);
    }

    /**
     * 删除任务历史信息
     * 
     * @param id 任务历史主键
     * @return 结果
     */
    @Override
    public int deleteTaskInfoHistoryById(Long id)
    {
        return taskInfoHistoryMapper.deleteTaskInfoHistoryById(id);
    }

    @Override
    public Integer findByTimeAllTask(String beginTime, String endTime,String classTime,Integer currentHour) {
        return taskInfoHistoryMapper.findByTimeAllTask( beginTime,  endTime,classTime,currentHour);
    }

    @Override
    public Integer findByTimeAllTask2(String beginTime, String endTime) {
        return taskInfoHistoryMapper.findByTimeAllTask2( beginTime,  endTime);
    }
}
