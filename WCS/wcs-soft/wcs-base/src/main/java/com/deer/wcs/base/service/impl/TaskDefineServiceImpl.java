package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.TaskDefineMapper;
import com.deer.wcs.base.model.TaskDefine;
import com.deer.wcs.base.model.TaskDefineCriteria;
import com.deer.wcs.base.model.TaskDefineDto;
import com.deer.wcs.base.model.TaskTypePriority;
import com.deer.wcs.base.service.TaskDefineService;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务定义Service业务层处理
 * 
 * @author deer
 * @date 2024-04-28
 */
@Service
public class TaskDefineServiceImpl  extends AbstractService<TaskDefine, Long>  implements TaskDefineService
{
    @Autowired
    private TaskDefineMapper taskDefineMapper;

    /**
     * 查询任务定义
     *
     * @param id 任务定义主键
     * @return 任务定义
     */
    @Override
    public TaskDefine selectTaskDefineById(Long id)
    {
        return taskDefineMapper.selectTaskDefineById(id);
    }

    /**
     * 查询任务定义列表
     * 
     * @param criteria
     * @return 任务定义
     */
    @Override
    public List<TaskDefineDto> findList(TaskDefineCriteria criteria)
    {
        return taskDefineMapper.findList(criteria);
    }

    /**
     * 新增任务定义
     *
     * @param taskDefine 任务定义
     * @return 结果
     */
    @Override
    public int insertTaskDefine(TaskDefine taskDefine)
    {
        return taskDefineMapper.insertTaskDefine(taskDefine);
    }

    /**
     * 修改任务定义
     *
     * @param taskDefine 任务定义
     * @return 结果
     */
    @Override
    public int updateTaskDefine(TaskDefine taskDefine)
    {
        return taskDefineMapper.updateTaskDefine(taskDefine);
    }

    /**
     * 批量删除任务定义
     * 
     * @param ids 需要删除的任务定义主键
     * @return 结果
     */
    @Override
    public int deleteTaskDefineByIds(Long[] ids)
    {
        return taskDefineMapper.deleteTaskDefineByIds(ids);
    }

    /**
     * 删除任务定义信息
     * 
     * @param id 任务定义主键
     * @return 结果
     */
    @Override
    public int deleteTaskDefineById(Long id)
    {
        // 清理所有 last_id 指向该步骤的步骤
        taskDefineMapper.clearLastIdByTargetId(id);
        
        // 删除步骤本身
        return taskDefineMapper.deleteTaskDefineById(id);
    }

    /**
     * 更新任务定义连线
     * 
     * @param fromStepId 起始步骤ID
     * @param toStepId 目标步骤ID
     * @param branchType 分支类型（success/fail/null）
     * @return 结果
     */
    @Override
    public int updateTaskDefineLink(Long fromStepId, Long toStepId, String branchType)
    {
        // 验证起始步骤是否存在
        TaskDefine fromStep = taskDefineMapper.selectTaskDefineById(fromStepId);
        if (fromStep == null) {
            throw new RuntimeException("起始步骤不存在");
        }
        
        // 验证目标步骤是否存在
        TaskDefine toStep = taskDefineMapper.selectTaskDefineById(toStepId);
        if (toStep == null) {
            throw new RuntimeException("目标步骤不存在");
        }
        
        // 如果起始步骤是判断步骤，验证分支类型
        if (fromStep.isJudgeStep()) {
            if (branchType == null) {
                throw new RuntimeException("判断步骤连线必须指定分支类型");
            }
            if (!"yes".equals(branchType) && !"no".equals(branchType)) {
                throw new RuntimeException("分支类型必须是 yes 或 no");
            }
            
            // 验证该分支是否已经有连线（查找是否已有其他步骤连接到该分支）
            TaskDefineCriteria criteria = new TaskDefineCriteria();
            criteria.setWareCode(fromStep.getWareCode());
            criteria.setType(fromStep.getType());
            List<TaskDefineDto> allSteps = taskDefineMapper.findList(criteria);
            
            for (TaskDefineDto step : allSteps) {
                if (step.getLastId() != null && step.getLastId().equals(fromStepId) 
                    && branchType.equals(step.getJudgeBranchType())
                    && !step.getId().equals(toStepId)) {
                    throw new RuntimeException("该分支已存在连线，一个分支只能连接一个步骤");
                }
            }
        }
        
        // 使用专门的SQL更新连线信息（支持设置为null）
        return taskDefineMapper.updateStepLink(toStepId, fromStepId, branchType);
    }

    /**
     * 删除任务定义连线
     * 
     * @param stepId 步骤ID
     * @return 结果
     */
    @Override
    public int deleteTaskDefineLink(Long stepId)
    {
        // 验证步骤是否存在
        TaskDefine step = taskDefineMapper.selectTaskDefineById(stepId);
        if (step == null) {
            throw new RuntimeException("步骤不存在");
        }
        
        // 使用专门的SQL清空连线信息（包括null值）
        return taskDefineMapper.clearStepLink(stepId);
    }

    /**
     * 更新步骤位置
     * 
     * @param stepId 步骤ID
     * @param positionX X坐标
     * @param positionY Y坐标
     * @return 结果
     */
    @Override
    public int updateStepPosition(Long stepId, Double positionX, Double positionY)
    {
        return taskDefineMapper.updateStepPosition(stepId, positionX, positionY);
    }

    /**
     * 验证判断步骤的连线完整性
     * 
     * @param wareCode 仓库编码
     * @param taskType 任务类型
     */
    @Override
    public void validateJudgeStepLinks(String wareCode, String taskType)
    {
        // 查找所有判断步骤
        TaskDefineCriteria criteria = new TaskDefineCriteria();
        criteria.setWareCode(wareCode);
        criteria.setType(taskType);
        List<TaskDefineDto> allSteps = taskDefineMapper.findList(criteria);
        
        for (TaskDefineDto step : allSteps) {
            if (step.getIsJudgeStep() != null && step.getIsJudgeStep() == 1) {
                // 查找该判断步骤的成功分支
                boolean hasSuccessBranch = false;
                boolean hasFailBranch = false;
                
                for (TaskDefineDto targetStep : allSteps) {
                    if (targetStep.getLastId() != null && targetStep.getLastId().equals(step.getId())) {
                        if ("success".equals(targetStep.getJudgeBranchType())) {
                            hasSuccessBranch = true;
                        }
                        if ("fail".equals(targetStep.getJudgeBranchType())) {
                            hasFailBranch = true;
                        }
                    }
                }
                
                if (!hasSuccessBranch || !hasFailBranch) {
                    throw new RuntimeException("判断步骤【" + step.getName() + "】必须设置成功和失败两个分支");
                }
            }
        }
    }

    @Override
    public List<TaskTypePriority> getTaskPriorityList() {
        return taskDefineMapper.getTaskPriorityList();
    }

}
