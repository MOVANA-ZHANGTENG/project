package com.deer.wcs.task.service;

import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.model.TaskInfoCriteria;
import com.deer.wcs.task.model.TaskInfoDto;
import com.deer.wcs.task.model.TaskStatisticsDto;
import com.deer.wcs.task.model.TaskBasicStatisticsDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务Service接口
 * 
 * @author deer
 * @date 2024-04-30
 */
public interface TaskInfoService   extends Service<TaskInfo, Long>
{

    void updateMemo(TaskInfo taskInfo, String msg);
    List<TaskInfo> findTaskInfoState(@Param("wareCode")String wareCode, @Param("state")Integer state);
    /**
     * 查询任务
     *
     * @param id 任务主键
     * @return 任务
     */
    public TaskInfo selectTaskInfoById(Long id);
    public TaskInfo getTaskInfoByPalletCode(String palletCode);

    /**
     * 查询任务列表
     * 
     * @param criteria
     * @return 任务集合
     */
    public List<TaskInfoDto> findList(TaskInfoCriteria criteria);

    /**
     * 新增任务
     *
     * @param taskInfo 任务
     * @return 结果
     */
    public int insertTaskInfo(TaskInfo taskInfo);

    /**
     * 修改任务
     *
     * @param taskInfo 任务
     * @return 结果
     */
    public int updateTaskInfo(TaskInfo taskInfo);

    /**
     * 批量删除任务
     * 
     * @param ids 需要删除的任务主键集合
     * @return 结果
     */
    public int deleteTaskInfoByIds(Long[] ids);

    /**
     * 删除任务信息
     * 
     * @param id 任务主键
     * @return 结果
     */
    public int deleteTaskInfoById(Long id);

    /**
     * 用于保存已经完成的任务
     * @param taskId
     */
    public void recordHistory(Long taskId);

    /**
     * 统计指定仓库7天内任务类型分布
     * @param wareCode 仓库编码
     * @return 统计结果
     */
    TaskStatisticsDto getSevenDaysStatistics(String wareCode);

    /**
     * 统计指定仓库的任务基础数据（总数、今日新增等）
     * @param wareCode 仓库编码
     * @return 统计结果
     */
    TaskBasicStatisticsDto getBasicStatistics(String wareCode);
}
