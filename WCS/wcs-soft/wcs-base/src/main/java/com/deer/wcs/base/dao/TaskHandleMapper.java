package com.deer.wcs.base.dao;

import com.deer.wcs.base.model.TaskHandle;
import com.deer.wcs.base.model.TaskHandleCriteria;
import com.deer.wcs.base.model.TaskHandleDto;
import com.deer.wcs.common.core.mapper.Mapper;

import java.util.List;

/**
 * 任务执行器Mapper接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface TaskHandleMapper  extends Mapper<TaskHandle>
{
    /**
     * 查询任务执行器
     *
     * @param id 任务执行器主键
     * @return 任务执行器
     */
    public TaskHandle selectTaskHandleById(Long id);

    /**
     * 查询任务执行器列表
     * 
     * @param taskHandle 任务执行器
     * @return 任务执行器集合
     */
    public List<TaskHandleDto> findList(TaskHandleCriteria criteria);

    /**
     * 新增任务执行器
     *
     * @param taskHandle 任务执行器
     * @return 结果
     */
    public int insertTaskHandle(TaskHandle taskHandle);

    /**
     * 修改任务执行器
     *
     * @param taskHandle 任务执行器
     * @return 结果
     */
    public int updateTaskHandle(TaskHandle taskHandle);

    /**
     * 删除任务执行器
     * 
     * @param id 任务执行器主键
     * @return 结果
     */
    public int deleteTaskHandleById(Long id);

    /**
     * 批量删除任务执行器
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTaskHandleByIds(Long[] ids);

    void deleteByTaskDefineId(Long taskDefineId);
}
