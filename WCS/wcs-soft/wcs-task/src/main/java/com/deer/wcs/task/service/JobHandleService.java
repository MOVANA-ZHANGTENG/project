package com.deer.wcs.task.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.task.model.JobHandle;
import com.deer.wcs.task.model.JobHandleDto;
import com.deer.wcs.task.model.JobHandleCriteria;

/**
 * 步骤执行器Service接口
 * 
 * @author deer
 * @date 2024-05-10
 */
public interface JobHandleService   extends Service<JobHandle, Long>
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
     * @param criteria
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
     * 批量删除步骤执行器
     * 
     * @param ids 需要删除的步骤执行器主键集合
     * @return 结果
     */
    public int deleteJobHandleByIds(Long[] ids);

    /**
     * 删除步骤执行器信息
     * 
     * @param id 步骤执行器主键
     * @return 结果
     */
    public int deleteJobHandleById(Long id);

    List<JobHandle> findByJobId(Long id);
}
