package com.deer.wcs.task.dao;

import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.JobInfoCriteria;
import com.deer.wcs.task.model.JobInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 执行步骤Mapper接口
 * 
 * @author deer
 * @date 2024-05-10
 */
public interface JobInfoMapper  extends Mapper<JobInfo>
{

    JobInfo findByIndex(@Param("taskId") Long taskId, @Param("index") Integer index);
    /**
     * 查询执行步骤
     *
     * @param id 执行步骤主键
     * @return 执行步骤
     */
    public JobInfo selectJobInfoById(Long id);

    /**
     * 查询执行步骤列表
     * 
     * @param jobInfo 执行步骤
     * @return 执行步骤集合
     */
    public List<JobInfoDto> findList(JobInfoCriteria criteria);
    public List<JobInfo> findInJobByLineCode(@Param("lineCode") String lineCode);

    /**
     * 新增执行步骤
     *
     * @param jobInfo 执行步骤
     * @return 结果
     */
    public int insertJobInfo(JobInfo jobInfo);
    public int updatePalletTask(@Param("cellCode")String cellCode,@Param("taskNo")String  taskNo);

    /**
     * 修改执行步骤
     *
     * @param jobInfo 执行步骤
     * @return 结果
     */
    public int updateJobInfo(JobInfo jobInfo);

    /**
     * 删除执行步骤
     * 
     * @param id 执行步骤主键
     * @return 结果
     */
    public int deleteJobInfoById(Long id);

    /**
     * 批量删除执行步骤
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJobInfoByIds(Long[] ids);

    List<JobInfo> findJobListByTaskNo(Long taskId);

    List<JobInfo> findJobHisListByTaskNo(Long taskId);

}
