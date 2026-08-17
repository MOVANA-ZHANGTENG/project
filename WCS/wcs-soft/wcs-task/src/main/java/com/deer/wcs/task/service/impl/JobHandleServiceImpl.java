package com.deer.wcs.task.service.impl;

import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.task.dao.JobHandleMapper;
import com.deer.wcs.task.model.JobHandle;
import com.deer.wcs.task.model.JobHandleCriteria;
import com.deer.wcs.task.model.JobHandleDto;
import com.deer.wcs.task.service.JobHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 步骤执行器Service业务层处理
 * 
 * @author deer
 * @date 2024-05-10
 */
@Service
public class JobHandleServiceImpl  extends AbstractService<JobHandle, Long>  implements JobHandleService
{
    @Autowired
    private JobHandleMapper jobHandleMapper;


    @Autowired
    AutoService autoService;

    @Override
    public void save(JobHandle model) {
        model.setCreateTime(DateUtil.getNowDateTimeString());
        model.setIsDelete(0);
        model.setVersion(0);
        model.setState(0);
        model.setId(autoService.getJobHandleId());
        super.save(model);
    }

    /**
     * 查询步骤执行器
     *
     * @param id 步骤执行器主键
     * @return 步骤执行器
     */
    @Override
    public JobHandle selectJobHandleById(Long id)
    {
        return jobHandleMapper.selectJobHandleById(id);
    }

    /**
     * 查询步骤执行器列表
     * 
     * @param criteria
     * @return 步骤执行器
     */
    @Override
    public List<JobHandleDto> findList(JobHandleCriteria criteria)
    {
        return jobHandleMapper.findList(criteria);
    }

    /**
     * 新增步骤执行器
     *
     * @param jobHandle 步骤执行器
     * @return 结果
     */
    @Override
    public int insertJobHandle(JobHandle jobHandle)
    {
        jobHandle.setCreateTime(DateUtil.getNowDateTimeString());
        return jobHandleMapper.insertJobHandle(jobHandle);
    }

    /**
     * 修改步骤执行器
     *
     * @param jobHandle 步骤执行器
     * @return 结果
     */
    @Override
    public int updateJobHandle(JobHandle jobHandle)
    {
        jobHandle.setUpdateTime(DateUtil.getNowDateTimeString());
        return jobHandleMapper.updateJobHandle(jobHandle);
    }

    /**
     * 批量删除步骤执行器
     * 
     * @param ids 需要删除的步骤执行器主键
     * @return 结果
     */
    @Override
    public int deleteJobHandleByIds(Long[] ids)
    {
        return jobHandleMapper.deleteJobHandleByIds(ids);
    }

    /**
     * 删除步骤执行器信息
     * 
     * @param id 步骤执行器主键
     * @return 结果
     */
    @Override
    public int deleteJobHandleById(Long id)
    {
        return jobHandleMapper.deleteJobHandleById(id);
    }

    @Override
    public List<JobHandle> findByJobId(Long jobId) {
        return jobHandleMapper.findByJobId(jobId);
    }
}
