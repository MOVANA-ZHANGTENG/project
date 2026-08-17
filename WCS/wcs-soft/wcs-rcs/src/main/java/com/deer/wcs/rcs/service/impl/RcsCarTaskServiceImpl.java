package com.deer.wcs.rcs.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.rcs.dao.RcsCarTaskMapper;
import com.deer.wcs.rcs.model.RcsCarTask;
import com.deer.wcs.rcs.model.RcsCarTaskDto;
import com.deer.wcs.rcs.model.RcsCarTaskCriteria;
import com.deer.wcs.rcs.service.RcsCarTaskService;

/**
 * 小车任务主体Service业务层处理
 * 
 * @author deer
 * @date 2025-07-07
 */
@Service
public class RcsCarTaskServiceImpl  extends AbstractService<RcsCarTask, Long>  implements RcsCarTaskService
{
    @Autowired
    private RcsCarTaskMapper rcsCarTaskMapper;

    /**
     * 查询小车任务主体
     *
     * @param id 小车任务主体主键
     * @return 小车任务主体
     */
    @Override
    public RcsCarTask selectRcsCarTaskById(Long id)
    {
        return rcsCarTaskMapper.selectRcsCarTaskById(id);
    }

    /**
     * 查询小车任务主体列表
     * 
     * @param criteria
     * @return 小车任务主体
     */
    @Override
    public List<RcsCarTaskDto> findList(RcsCarTaskCriteria criteria)
    {
        return rcsCarTaskMapper.findList(criteria);
    }

    /**
     * 新增小车任务主体
     *
     * @param rcsCarTask 小车任务主体
     * @return 结果
     */
    @Override
    public int insertRcsCarTask(RcsCarTask rcsCarTask)
    {
        rcsCarTask.setCreateTime(DateUtil.getNowDateTimeString());
        return rcsCarTaskMapper.insertRcsCarTask(rcsCarTask);
    }

    /**
     * 修改小车任务主体
     *
     * @param rcsCarTask 小车任务主体
     * @return 结果
     */
    @Override
    public int updateRcsCarTask(RcsCarTask rcsCarTask)
    {
        return rcsCarTaskMapper.updateRcsCarTask(rcsCarTask);
    }

    /**
     * 批量删除小车任务主体
     * 
     * @param ids 需要删除的小车任务主体主键
     * @return 结果
     */
    @Override
    public int deleteRcsCarTaskByIds(Long[] ids)
    {
        return rcsCarTaskMapper.deleteRcsCarTaskByIds(ids);
    }

    /**
     * 删除小车任务主体信息
     * 
     * @param id 小车任务主体主键
     * @return 结果
     */
    @Override
    public int deleteRcsCarTaskById(Long id)
    {
        return rcsCarTaskMapper.deleteRcsCarTaskById(id);
    }
}
