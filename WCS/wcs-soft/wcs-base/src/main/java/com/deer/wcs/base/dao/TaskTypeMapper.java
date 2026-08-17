package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.TaskType;
import com.deer.wcs.base.model.TaskTypeDto;
import com.deer.wcs.base.model.TaskTypeCriteria;

/**
 * 任务类型Mapper接口
 * 
 * @author deer
 * @date 2024-08-06
 */
public interface TaskTypeMapper  extends Mapper<TaskType>
{
    /**
     * 查询任务类型
     *
     * @param id 任务类型主键
     * @return 任务类型
     */
    public TaskType selectTaskTypeById(Long id);

    /**
     * 查询任务类型列表
     * 
     * @param taskType 任务类型
     * @return 任务类型集合
     */
    public List<TaskTypeDto> findList(TaskTypeCriteria criteria);

    /**
     * 新增任务类型
     *
     * @param taskType 任务类型
     * @return 结果
     */
    public int insertTaskType(TaskType taskType);

    /**
     * 修改任务类型
     *
     * @param taskType 任务类型
     * @return 结果
     */
    public int updateTaskType(TaskType taskType);

    /**
     * 删除任务类型
     * 
     * @param id 任务类型主键
     * @return 结果
     */
    public int deleteTaskTypeById(Long id);

    /**
     * 批量删除任务类型
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTaskTypeByIds(Long[] ids);
}
