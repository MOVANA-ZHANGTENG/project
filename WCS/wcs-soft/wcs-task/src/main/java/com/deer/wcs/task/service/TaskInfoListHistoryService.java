package com.deer.wcs.task.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.task.model.TaskInfoListHistory;
import com.deer.wcs.task.model.TaskInfoListHistoryDto;
import com.deer.wcs.task.model.TaskInfoListHistoryCriteria;

/**
 * 用于记录任务执行的每一步历史Service接口
 * 
 * @author deer
 * @date 2024-06-06
 */
public interface TaskInfoListHistoryService   extends Service<TaskInfoListHistory, Long>
{
    /**
     * 查询用于记录任务执行的每一步历史
     *
     * @param id 用于记录任务执行的每一步历史主键
     * @return 用于记录任务执行的每一步历史
     */
    public TaskInfoListHistory selectTaskInfoListHistoryById(Long id);

    /**
     * 查询用于记录任务执行的每一步历史列表
     * 
     * @param criteria
     * @return 用于记录任务执行的每一步历史集合
     */
    public List<TaskInfoListHistoryDto> findList(TaskInfoListHistoryCriteria criteria);

    /**
     * 新增用于记录任务执行的每一步历史
     *
     * @param taskInfoListHistory 用于记录任务执行的每一步历史
     * @return 结果
     */
    public int insertTaskInfoListHistory(TaskInfoListHistory taskInfoListHistory);

    /**
     * 修改用于记录任务执行的每一步历史
     *
     * @param taskInfoListHistory 用于记录任务执行的每一步历史
     * @return 结果
     */
    public int updateTaskInfoListHistory(TaskInfoListHistory taskInfoListHistory);

    /**
     * 批量删除用于记录任务执行的每一步历史
     * 
     * @param ids 需要删除的用于记录任务执行的每一步历史主键集合
     * @return 结果
     */
    public int deleteTaskInfoListHistoryByIds(Long[] ids);

    /**
     * 删除用于记录任务执行的每一步历史信息
     * 
     * @param id 用于记录任务执行的每一步历史主键
     * @return 结果
     */
    public int deleteTaskInfoListHistoryById(Long id);
}
