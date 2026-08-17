package com.deer.wcs.task.dao;

import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.TaskInfoHistory;
import com.deer.wcs.task.model.TaskInfoHistoryCriteria;
import com.deer.wcs.task.model.TaskInfoHistoryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务历史Mapper接口
 * 
 * @author deer
 * @date 2024-06-04
 */
public interface TaskInfoHistoryMapper  extends Mapper<TaskInfoHistory>
{
    /**
     * 查询任务历史
     *
     * @param id 任务历史主键
     * @return 任务历史
     */
    public TaskInfoHistory selectTaskInfoHistoryById(Long id);

    /**
     * 查询任务历史列表
     * 
     * @param taskInfoHistory 任务历史
     * @return 任务历史集合
     */
    public List<TaskInfoHistoryDto> findList(TaskInfoHistoryCriteria criteria);

    /**
     * 新增任务历史
     *
     * @param taskInfoHistory 任务历史
     * @return 结果
     */
    public int insertTaskInfoHistory(TaskInfoHistory taskInfoHistory);

    /**
     * 修改任务历史
     *
     * @param taskInfoHistory 任务历史
     * @return 结果
     */
    public int updateTaskInfoHistory(TaskInfoHistory taskInfoHistory);

    /**
     * 删除任务历史
     * 
     * @param id 任务历史主键
     * @return 结果
     */
    public int deleteTaskInfoHistoryById(Long id);

    /**
     * 批量删除任务历史
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTaskInfoHistoryByIds(Long[] ids);

    Integer findByTimeAllTask(@Param("beginTime") String beginTime, @Param("endTime")String endTime,@Param("classTime") String classTime,@Param("currentHour") Integer currentHour);

    Integer findByTimeAllTask2(@Param("beginTime") String beginTime, @Param("endTime")String endTime);
}
