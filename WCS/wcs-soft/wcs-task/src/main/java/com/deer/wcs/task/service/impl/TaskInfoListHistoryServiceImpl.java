package com.deer.wcs.task.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.task.dao.TaskInfoListHistoryMapper;
import com.deer.wcs.task.model.TaskInfoListHistory;
import com.deer.wcs.task.model.TaskInfoListHistoryDto;
import com.deer.wcs.task.model.TaskInfoListHistoryCriteria;
import com.deer.wcs.task.service.TaskInfoListHistoryService;

/**
 * 用于记录任务执行的每一步历史Service业务层处理
 * 
 * @author deer
 * @date 2024-06-06
 */
@Service
public class TaskInfoListHistoryServiceImpl  extends AbstractService<TaskInfoListHistory, Long>  implements TaskInfoListHistoryService
{
    @Autowired
    private TaskInfoListHistoryMapper taskInfoListHistoryMapper;

    /**
     * 查询用于记录任务执行的每一步历史
     *
     * @param id 用于记录任务执行的每一步历史主键
     * @return 用于记录任务执行的每一步历史
     */
    @Override
    public TaskInfoListHistory selectTaskInfoListHistoryById(Long id)
    {
        return taskInfoListHistoryMapper.selectTaskInfoListHistoryById(id);
    }

    /**
     * 查询用于记录任务执行的每一步历史列表
     * 
     * @param criteria
     * @return 用于记录任务执行的每一步历史
     */
    @Override
    public List<TaskInfoListHistoryDto> findList(TaskInfoListHistoryCriteria criteria)
    {
        return taskInfoListHistoryMapper.findList(criteria);
    }

    /**
     * 新增用于记录任务执行的每一步历史
     *
     * @param taskInfoListHistory 用于记录任务执行的每一步历史
     * @return 结果
     */
    @Override
    public int insertTaskInfoListHistory(TaskInfoListHistory taskInfoListHistory)
    {
        taskInfoListHistory.setCreateTime(DateUtil.getNowDateTimeString());
        return taskInfoListHistoryMapper.insertTaskInfoListHistory(taskInfoListHistory);
    }

    /**
     * 修改用于记录任务执行的每一步历史
     *
     * @param taskInfoListHistory 用于记录任务执行的每一步历史
     * @return 结果
     */
    @Override
    public int updateTaskInfoListHistory(TaskInfoListHistory taskInfoListHistory)
    {
        return taskInfoListHistoryMapper.updateTaskInfoListHistory(taskInfoListHistory);
    }

    /**
     * 批量删除用于记录任务执行的每一步历史
     * 
     * @param ids 需要删除的用于记录任务执行的每一步历史主键
     * @return 结果
     */
    @Override
    public int deleteTaskInfoListHistoryByIds(Long[] ids)
    {
        return taskInfoListHistoryMapper.deleteTaskInfoListHistoryByIds(ids);
    }

    /**
     * 删除用于记录任务执行的每一步历史信息
     * 
     * @param id 用于记录任务执行的每一步历史主键
     * @return 结果
     */
    @Override
    public int deleteTaskInfoListHistoryById(Long id)
    {
        return taskInfoListHistoryMapper.deleteTaskInfoListHistoryById(id);
    }
}
