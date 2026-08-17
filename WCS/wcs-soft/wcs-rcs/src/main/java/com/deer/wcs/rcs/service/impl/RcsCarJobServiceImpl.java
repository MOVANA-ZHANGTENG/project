package com.deer.wcs.rcs.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.rcs.dao.RcsCarJobMapper;
import com.deer.wcs.rcs.model.RcsCarJob;
import com.deer.wcs.rcs.model.RcsCarJobDto;
import com.deer.wcs.rcs.model.RcsCarJobCriteria;
import com.deer.wcs.rcs.service.RcsCarJobService;

/**
 * 小车任务详情Service业务层处理
 * 
 * @author deer
 * @date 2025-07-07
 */
@Service
public class RcsCarJobServiceImpl  extends AbstractService<RcsCarJob, Long>  implements RcsCarJobService
{
    @Autowired
    private RcsCarJobMapper rcsCarJobMapper;

    /**
     * 查询小车任务详情
     *
     * @param id 小车任务详情主键
     * @return 小车任务详情
     */
    @Override
    public RcsCarJob selectRcsCarJobById(Long id)
    {
        return rcsCarJobMapper.selectRcsCarJobById(id);
    }

    /**
     * 查询小车任务详情列表
     * 
     * @param criteria
     * @return 小车任务详情
     */
    @Override
    public List<RcsCarJobDto> findList(RcsCarJobCriteria criteria)
    {
        return rcsCarJobMapper.findList(criteria);
    }

    /**
     * 新增小车任务详情
     *
     * @param rcsCarJob 小车任务详情
     * @return 结果
     */
    @Override
    public int insertRcsCarJob(RcsCarJob rcsCarJob)
    {
        rcsCarJob.setCreateTime(DateUtil.getNowDateTimeString());
        return rcsCarJobMapper.insertRcsCarJob(rcsCarJob);
    }

    /**
     * 修改小车任务详情
     *
     * @param rcsCarJob 小车任务详情
     * @return 结果
     */
    @Override
    public int updateRcsCarJob(RcsCarJob rcsCarJob)
    {
        return rcsCarJobMapper.updateRcsCarJob(rcsCarJob);
    }

    /**
     * 批量删除小车任务详情
     * 
     * @param ids 需要删除的小车任务详情主键
     * @return 结果
     */
    @Override
    public int deleteRcsCarJobByIds(Long[] ids)
    {
        return rcsCarJobMapper.deleteRcsCarJobByIds(ids);
    }

    /**
     * 删除小车任务详情信息
     * 
     * @param id 小车任务详情主键
     * @return 结果
     */
    @Override
    public int deleteRcsCarJobById(Long id)
    {
        return rcsCarJobMapper.deleteRcsCarJobById(id);
    }
}
