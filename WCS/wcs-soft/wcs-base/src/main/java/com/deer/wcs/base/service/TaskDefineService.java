package com.deer.wcs.base.service;

import com.deer.wcs.base.model.TaskDefine;
import com.deer.wcs.base.model.TaskDefineCriteria;
import com.deer.wcs.base.model.TaskDefineDto;
import com.deer.wcs.base.model.TaskTypePriority;
import com.deer.wcs.common.core.service.Service;

import java.util.List;

/**
 * 任务定义Service接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface TaskDefineService  extends Service<TaskDefine, Long>
{
    /**
     * 查询任务定义
     *
     * @param id 任务定义主键
     * @return 任务定义
     */
    public TaskDefine selectTaskDefineById(Long id);

    /**
     * 查询任务定义列表
     * 
     * @param criteria
     * @return 任务定义集合
     */
    public List<TaskDefineDto> findList(TaskDefineCriteria criteria);

    /**
     * 新增任务定义
     *
     * @param taskDefine 任务定义
     * @return 结果
     */
    public int insertTaskDefine(TaskDefine taskDefine);

    /**
     * 修改任务定义
     *
     * @param taskDefine 任务定义
     * @return 结果
     */
    public int updateTaskDefine(TaskDefine taskDefine);

    /**
     * 批量删除任务定义
     * 
     * @param ids 需要删除的任务定义主键集合
     * @return 结果
     */
    public int deleteTaskDefineByIds(Long[] ids);

    /**
     * 删除任务定义信息
     * 
     * @param id 任务定义主键
     * @return 结果
     */
    public int deleteTaskDefineById(Long id);

    /**
     * 更新任务定义连线
     * 
     * @param fromStepId 起始步骤ID
     * @param toStepId 目标步骤ID
     * @param branchType 分支类型（success/fail/null）
     * @return 结果
     */
    public int updateTaskDefineLink(Long fromStepId, Long toStepId, String branchType);

    /**
     * 删除任务定义连线
     * 
     * @param stepId 步骤ID
     * @return 结果
     */
    public int deleteTaskDefineLink(Long stepId);

    /**
     * 更新步骤位置
     * 
     * @param stepId 步骤ID
     * @param positionX X坐标
     * @param positionY Y坐标
     * @return 结果
     */
    public int updateStepPosition(Long stepId, Double positionX, Double positionY);

    /**
     * 验证判断步骤的连线完整性
     * 
     * @param wareCode 仓库编码
     * @param taskType 任务类型
     */
    public void validateJudgeStepLinks(String wareCode, String taskType);

    public List<TaskTypePriority> getTaskPriorityList();
}
