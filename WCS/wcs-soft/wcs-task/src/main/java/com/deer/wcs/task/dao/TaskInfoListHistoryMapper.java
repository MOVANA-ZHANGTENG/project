package com.deer.wcs.task.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.TaskInfoListHistory;
import com.deer.wcs.task.model.TaskInfoListHistoryDto;
import com.deer.wcs.task.model.TaskInfoListHistoryCriteria;

/**
 * 用于记录任务执行的每一步历史Mapper接口
 * 
 * @author deer
 * @date 2024-06-06
 */
public interface TaskInfoListHistoryMapper  extends Mapper<TaskInfoListHistory>
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
     * @param taskInfoListHistory 用于记录任务执行的每一步历史
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
     * 删除用于记录任务执行的每一步历史
     * 
     * @param id 用于记录任务执行的每一步历史主键
     * @return 结果
     */
    public int deleteTaskInfoListHistoryById(Long id);

    /**
     * 批量删除用于记录任务执行的每一步历史
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTaskInfoListHistoryByIds(Long[] ids);
}
