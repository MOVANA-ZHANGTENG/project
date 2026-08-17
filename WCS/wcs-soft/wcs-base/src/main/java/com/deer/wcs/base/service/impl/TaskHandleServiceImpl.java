package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.TaskHandleMapper;
import com.deer.wcs.base.model.TaskHandle;
import com.deer.wcs.base.model.TaskHandleCriteria;
import com.deer.wcs.base.model.TaskHandleDto;
import com.deer.wcs.base.service.TaskHandleService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务执行器Service业务层处理
 * 
 * @author deer
 * @date 2024-04-28
 */
@Service
public class TaskHandleServiceImpl  extends AbstractService<TaskHandle, Long>  implements TaskHandleService
{
    @Autowired
    private TaskHandleMapper taskHandleMapper;

    /**
     * 查询任务执行器
     *
     * @param id 任务执行器主键
     * @return 任务执行器
     */
    @Override
    public TaskHandle selectTaskHandleById(Long id)
    {
        return taskHandleMapper.selectTaskHandleById(id);
    }

    /**
     * 查询任务执行器列表
     * 
     * @param criteria
     * @return 任务执行器
     */
    @Override
    public List<TaskHandleDto> findList(TaskHandleCriteria criteria)
    {
        return taskHandleMapper.findList(criteria);
    }

    @Override
    public void save(TaskHandle model) {
        model.setCreateTime(DateUtil.getNowDateTimeString());
        model.setVersion(0);
        model.setIsDelete(0);
        super.save(model);
    }

    /**
     * 新增任务执行器
     *
     * @param taskHandle 任务执行器
     * @return 结果
     */
    @Override
    public int insertTaskHandle(TaskHandle taskHandle)
    {
        taskHandle.setCreateTime(DateUtil.getNowDateTimeString());
        return taskHandleMapper.insertTaskHandle(taskHandle);
    }

    /**
     * 修改任务执行器
     *
     * @param taskHandle 任务执行器
     * @return 结果
     */
    @Override
    public int updateTaskHandle(TaskHandle taskHandle)
    {
        taskHandle.setUpdateTime(DateUtil.getNowDateTimeString());
        return taskHandleMapper.updateTaskHandle(taskHandle);
    }

    /**
     * 批量删除任务执行器
     * 
     * @param ids 需要删除的任务执行器主键
     * @return 结果
     */
    @Override
    public int deleteTaskHandleByIds(Long[] ids)
    {
        return taskHandleMapper.deleteTaskHandleByIds(ids);
    }

    /**
     * 删除任务执行器信息
     * 
     * @param id 任务执行器主键
     * @return 结果
     */
    @Override
    public int deleteTaskHandleById(Long id)
    {
        return taskHandleMapper.deleteTaskHandleById(id);
    }


    /**
     * 根据任务定义id删除任务
     */
    @Override
    public void deleteByTaskDefineId(Long taskDefineId) {
        taskHandleMapper.deleteByTaskDefineId(taskDefineId);
    }

}
