package com.deer.wcs.rcs.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.rcs.model.RcsCarJob;
import com.deer.wcs.rcs.model.RcsCarJobDto;
import com.deer.wcs.rcs.model.RcsCarJobCriteria;

/**
 * 小车任务详情Service接口
 * 
 * @author deer
 * @date 2025-07-07
 */
public interface RcsCarJobService   extends Service<RcsCarJob, Long>
{
    /**
     * 查询小车任务详情
     *
     * @param id 小车任务详情主键
     * @return 小车任务详情
     */
    public RcsCarJob selectRcsCarJobById(Long id);

    /**
     * 查询小车任务详情列表
     * 
     * @param criteria
     * @return 小车任务详情集合
     */
    public List<RcsCarJobDto> findList(RcsCarJobCriteria criteria);

    /**
     * 新增小车任务详情
     *
     * @param rcsCarJob 小车任务详情
     * @return 结果
     */
    public int insertRcsCarJob(RcsCarJob rcsCarJob);

    /**
     * 修改小车任务详情
     *
     * @param rcsCarJob 小车任务详情
     * @return 结果
     */
    public int updateRcsCarJob(RcsCarJob rcsCarJob);

    /**
     * 批量删除小车任务详情
     * 
     * @param ids 需要删除的小车任务详情主键集合
     * @return 结果
     */
    public int deleteRcsCarJobByIds(Long[] ids);

    /**
     * 删除小车任务详情信息
     * 
     * @param id 小车任务详情主键
     * @return 结果
     */
    public int deleteRcsCarJobById(Long id);
}
