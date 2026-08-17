package com.deer.wcs.task.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.JobHandle;
import com.deer.wcs.task.model.JobHandleDto;
import com.deer.wcs.task.model.JobHandleCriteria;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * 步骤执行器Mapper接口
 * 
 * @author deer
 * @date 2024-05-10
 */
public interface JobHandleMapper  extends Mapper<JobHandle>
{
    /**
     * 查询步骤执行器
     *
     * @param id 步骤执行器主键
     * @return 步骤执行器
     */
    public JobHandle selectJobHandleById(Long id);

    /**
     * 查询步骤执行器列表
     * 
     * @param jobHandle 步骤执行器
     * @return 步骤执行器集合
     */
    public List<JobHandleDto> findList(JobHandleCriteria criteria);

    /**
     * 新增步骤执行器
     *
     * @param jobHandle 步骤执行器
     * @return 结果
     */
    public int insertJobHandle(JobHandle jobHandle);

    /**
     * 修改步骤执行器
     *
     * @param jobHandle 步骤执行器
     * @return 结果
     */
    public int updateJobHandle(JobHandle jobHandle);

    /**
     * 删除步骤执行器
     * 
     * @param id 步骤执行器主键
     * @return 结果
     */
    public int deleteJobHandleById(Long id);

    /**
     * 批量删除步骤执行器
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJobHandleByIds(Long[] ids);

    /**
     * 根据任务ID查询步骤执行器列表
     *
     * @param jobId 任务ID
     * @return 步骤执行器集合
     */
    List<JobHandle> findByJobId(@Param("jobId") Long jobId);
}
