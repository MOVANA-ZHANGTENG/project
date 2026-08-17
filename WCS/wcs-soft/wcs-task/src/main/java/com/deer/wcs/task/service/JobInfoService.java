package com.deer.wcs.task.service;

import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.JobInfoCriteria;
import com.deer.wcs.task.model.JobInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 执行步骤Service接口
 * 
 * @author deer
 * @date 2024-05-10
 */
public interface JobInfoService   extends Service<JobInfo, Long> {
    void updateMemo(JobInfo jobInfo, String msg);

    /**
     * 查询执行步骤
     *
     * @param id 执行步骤主键
     * @return 执行步骤
     */
    public JobInfo selectJobInfoById(Long id);

    public JobInfo findByIndex(Long taskId, Integer index);

    /**
     * 查询执行步骤列表
     *
     * @param criteria
     * @return 执行步骤集合
     */
    public List<JobInfoDto> findList(JobInfoCriteria criteria);

    List<JobInfo> findInJobByLineCode(@Param("lineCode") String lineCode);

    int updatePalletTask(@Param("cellCode") String cellCode, @Param("taskNo") String taskNo);

    /**
     * 新增执行步骤
     *
     * @param jobInfo 执行步骤
     * @return 结果
     */
    public int insertJobInfo(JobInfo jobInfo);

    /**
     * 修改执行步骤
     *
     * @param jobInfo 执行步骤
     * @return 结果
     */
    public int updateJobInfo(JobInfo jobInfo);

    /**
     * 批量删除执行步骤
     *
     * @param ids 需要删除的执行步骤主键集合
     * @return 结果
     */
    public int deleteJobInfoByIds(Long[] ids);

    /**
     * 删除执行步骤信息
     *
     * @param id 执行步骤主键
     * @return 结果
     */
    public int deleteJobInfoById(Long id);

    List<JobInfo> findJobListByTaskNo(Long taskId);

    List<JobInfo> findJobHisListByTaskNo(Long taskId);

    public List<JobInfo> findByTaskId(Long taskId);

}
