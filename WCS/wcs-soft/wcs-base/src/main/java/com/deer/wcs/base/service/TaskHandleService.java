package com.deer.wcs.base.service;

import com.deer.wcs.base.model.TaskHandle;
import com.deer.wcs.base.model.TaskHandleCriteria;
import com.deer.wcs.base.model.TaskHandleDto;
import com.deer.wcs.common.core.service.Service;

import java.util.List;

/**
 * 任务执行器Service接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface TaskHandleService   extends Service<TaskHandle, Long>
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
     * @param criteria
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
     * 批量删除任务执行器
     * 
     * @param ids 需要删除的任务执行器主键集合
     * @return 结果
     */
    public int deleteTaskHandleByIds(Long[] ids);

    /**
     * 删除任务执行器信息
     * 
     * @param id 任务执行器主键
     * @return 结果
     */
    public int deleteTaskHandleById(Long id);

    /**
     * 根据任务定义id删除任务
     */
    void deleteByTaskDefineId(Long taskDefineId);
}
