package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.TaskType;
import com.deer.wcs.base.model.TaskTypeDto;
import com.deer.wcs.base.model.TaskTypeCriteria;

/**
 * 任务类型Service接口
 * 
 * @author deer
 * @date 2024-08-06
 */
public interface TaskTypeService   extends Service<TaskType, Long>
{
    public TaskType findByCode(String wareCode,String code);

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
     * @param criteria
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
     * 批量删除任务类型
     * 
     * @param ids 需要删除的任务类型主键集合
     * @return 结果
     */
    public int deleteTaskTypeByIds(Long[] ids);

    /**
     * 删除任务类型信息
     * 
     * @param id 任务类型主键
     * @return 结果
     */
    public int deleteTaskTypeById(Long id);
}
