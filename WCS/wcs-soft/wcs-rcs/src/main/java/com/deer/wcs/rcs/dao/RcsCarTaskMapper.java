package com.deer.wcs.rcs.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.rcs.model.RcsCarTask;
import com.deer.wcs.rcs.model.RcsCarTaskDto;
import com.deer.wcs.rcs.model.RcsCarTaskCriteria;

/**
 * 小车任务主体Mapper接口
 * 
 * @author deer
 * @date 2025-07-07
 */
public interface RcsCarTaskMapper  extends Mapper<RcsCarTask>
{
    /**
     * 查询小车任务主体
     *
     * @param id 小车任务主体主键
     * @return 小车任务主体
     */
    public RcsCarTask selectRcsCarTaskById(Long id);

    /**
     * 查询小车任务主体列表
     * 
     * @param rcsCarTask 小车任务主体
     * @return 小车任务主体集合
     */
    public List<RcsCarTaskDto> findList(RcsCarTaskCriteria criteria);

    /**
     * 新增小车任务主体
     *
     * @param rcsCarTask 小车任务主体
     * @return 结果
     */
    public int insertRcsCarTask(RcsCarTask rcsCarTask);

    /**
     * 修改小车任务主体
     *
     * @param rcsCarTask 小车任务主体
     * @return 结果
     */
    public int updateRcsCarTask(RcsCarTask rcsCarTask);

    /**
     * 删除小车任务主体
     * 
     * @param id 小车任务主体主键
     * @return 结果
     */
    public int deleteRcsCarTaskById(Long id);

    /**
     * 批量删除小车任务主体
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRcsCarTaskByIds(Long[] ids);
}
