package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.TaskTypeMapper;
import com.deer.wcs.base.model.TaskType;
import com.deer.wcs.base.model.TaskTypeCriteria;
import com.deer.wcs.base.model.TaskTypeDto;
import com.deer.wcs.base.service.TaskTypeService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * 任务类型Service业务层处理
 * 
 * @author deer
 * @date 2024-08-06
 */
@Service
public class TaskTypeServiceImpl  extends AbstractService<TaskType, Long>  implements TaskTypeService
{
    @Autowired
    private TaskTypeMapper taskTypeMapper;

    @Override
    public void save(TaskType model) {
        //设置默认优先级为10
        if(model.getPriority()==null){
            model.setPriority(10);
        }
        model.setDelFlag(0);
        super.save(model);
    }

    @Override
    public TaskType findByCode(String wareCode, String code) {
        Condition condition = new Condition(TaskType.class);
        condition.createCriteria().andEqualTo("wareCode",wareCode)
                .andEqualTo("code",code);
        List<TaskType> list = super.findByCondition(condition);
        if(list.isEmpty()){
            throw new ServiceException("未查询到类型为："+code+" 的任务模板");
        }
        return list.get(0);
    }

    /**
     * 查询任务类型
     *
     * @param id 任务类型主键
     * @return 任务类型
     */
    @Override
    public TaskType selectTaskTypeById(Long id)
    {
        return taskTypeMapper.selectTaskTypeById(id);
    }

    /**
     * 查询任务类型列表
     * 
     * @param criteria
     * @return 任务类型
     */
    @Override
    public List<TaskTypeDto> findList(TaskTypeCriteria criteria)
    {
        return taskTypeMapper.findList(criteria);
    }

    /**
     * 新增任务类型
     *
     * @param taskType 任务类型
     * @return 结果
     */
    @Override
    public int insertTaskType(TaskType taskType)
    {
        taskType.setCreateTime(DateUtil.getNowDateTimeString());
        return taskTypeMapper.insertTaskType(taskType);
    }

    /**
     * 修改任务类型
     *
     * @param taskType 任务类型
     * @return 结果
     */
    @Override
    public int updateTaskType(TaskType taskType)
    {
        taskType.setUpdateTime(DateUtil.getNowDateTimeString());
        return taskTypeMapper.updateTaskType(taskType);
    }

    /**
     * 批量删除任务类型
     * 
     * @param ids 需要删除的任务类型主键
     * @return 结果
     */
    @Override
    public int deleteTaskTypeByIds(Long[] ids)
    {
        return taskTypeMapper.deleteTaskTypeByIds(ids);
    }

    /**
     * 删除任务类型信息
     * 
     * @param id 任务类型主键
     * @return 结果
     */
    @Override
    public int deleteTaskTypeById(Long id)
    {
        return taskTypeMapper.deleteTaskTypeById(id);
    }
}
