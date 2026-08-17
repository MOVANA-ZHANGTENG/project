package com.deer.wcs.base.dao;

import com.deer.wcs.base.model.TaskDefine;
import com.deer.wcs.base.model.TaskDefineCriteria;
import com.deer.wcs.base.model.TaskDefineDto;
import com.deer.wcs.base.model.TaskTypePriority;
import com.deer.wcs.common.core.mapper.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务定义Mapper接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface TaskDefineMapper  extends Mapper<TaskDefine>
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
     * @param taskDefine 任务定义
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
     * 删除任务定义
     * 
     * @param id 任务定义主键
     * @return 结果
     */
    public int deleteTaskDefineById(Long id);

    /**
     * 批量删除任务定义
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTaskDefineByIds(Long[] ids);

    /**
     * 清理指向目标步骤的 last_id 和 judge_branch_type
     * 
     * @param targetId 目标步骤ID
     * @return 结果
     */
    public int clearLastIdByTargetId(Long targetId);

    /**
     * 清空步骤的连线信息（last_id 和 judge_branch_type）
     * 
     * @param stepId 步骤ID
     * @return 结果
     */
    public int clearStepLink(Long stepId);

    /**
     * 更新步骤的连线信息（支持设置为null）
     * 
     * @param stepId 步骤ID
     * @param lastId 上一步骤ID
     * @param judgeBranchType 判断分支类型
     * @return 结果
     */
    public int updateStepLink(@Param("stepId") Long stepId, @Param("lastId") Long lastId, @Param("judgeBranchType") String judgeBranchType);

    /**
     * 更新步骤的位置
     * 
     * @param stepId 步骤ID
     * @param positionX X坐标
     * @param positionY Y坐标
     * @return 结果
     */
    public int updateStepPosition(@Param("stepId") Long stepId, @Param("positionX") Double positionX, @Param("positionY") Double positionY);

    List<TaskTypePriority> getTaskPriorityList();
}
