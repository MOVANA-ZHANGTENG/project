package com.deer.wcs.task.dao;

import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.model.TaskInfoCriteria;
import com.deer.wcs.task.model.TaskInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 任务Mapper接口
 * 
 * @author deer
 * @date 2024-04-30
 */
public interface TaskInfoMapper  extends Mapper<TaskInfo>
{
    /**
     * 查询任务
     *
     * @param id 任务主键
     * @return 任务
     */
    public TaskInfo selectTaskInfoById(Long id);

    /**
     * 查询任务列表
     * 
     * @param criteria 任务
     * @return 任务集合
     */
    public List<TaskInfoDto> findList(TaskInfoCriteria criteria);
    public List<TaskInfo> findTaskInfoState(@Param("wareCode")String wareCode, @Param("state")Integer state);

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
     * 删除任务
     * 
     * @param id 任务主键
     * @return 结果
     */
    public int deleteTaskInfoById(Long id);

    /**
     * 批量删除任务
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTaskInfoByIds(Long[] ids);

    void recordHistory(Long taskId);

    /**
     * 统计指定仓库7天内任务类型分布
     * @param wareCode 仓库编码
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    List<Map<String, Object>> selectSevenDaysStatistics(@Param("wareCode") String wareCode,
                                                         @Param("startDate") String startDate,
                                                         @Param("endDate") String endDate);

    /**
     * 统计指定仓库的任务基础数据（总数、今日新增等）
     * @param wareCode 仓库编码
     * @param todayStart 今日开始时间
     * @param todayEnd 今日结束时间
     * @return 统计结果
     */
    Map<String, Object> selectBasicStatistics(@Param("wareCode") String wareCode,
                                               @Param("todayStart") String todayStart,
                                               @Param("todayEnd") String todayEnd);
}
