package com.deer.wcs.task.service.impl;

import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.BillRecordService;
import com.deer.wcs.task.dao.JobInfoMapper;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.JobInfoCriteria;
import com.deer.wcs.task.model.JobInfoDto;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.util.Collections;
import java.util.List;

/**
 * 执行步骤Service业务层处理
 * 
 * @author deer
 * @date 2024-05-10
 */
@Service
@Transactional
public class JobInfoServiceImpl  extends AbstractService<JobInfo, Long>  implements JobInfoService
{
    @Autowired
    private JobInfoMapper jobInfoMapper;

    @Autowired
    private BillRecordService billRecordService;

    @Override
    public void save(JobInfo model) {
        model.setState(0);
        if(model.getId()==null){
            model.setId(Long.parseLong(model.getTaskId()+"0"+model.getJobIndex()));
        }
        model.setCreateTime(DateUtil.getNowDateTimeString());
        super.save(model);
    }

    @Autowired
    private TaskInfoService taskInfoService;

    @Override
    public void updateMemo(JobInfo jobInfo, String msg) {
        if(msg==null){
            msg="";
        }
        if(jobInfo==null || jobInfo.getMemo()==null || !jobInfo.getMemo().equals(msg)){
            JobInfo model = new JobInfo();
            model.setId(jobInfo.getId());
            model.setMemo(msg);
            billRecordService.createTaskRecord(jobInfo.getTaskId(),"【"+jobInfo.getName()+"】："+msg);
            super.update(model);
            Integer jobState = jobInfo.getState();
            String stateStr = "";
            if(jobState==0){
                stateStr="未执行";
            }
            if(jobState==1){
                stateStr="允许执行";
            }
            if(jobState==2){
                stateStr="执行中";
            }
            if(jobState==3){
                stateStr="允许完成";
            }
            if(jobState==4){
                stateStr="完成";
            }
            TaskInfo taskInfo =taskInfoService.findById(jobInfo.getTaskId());
            taskInfo.setId( jobInfo.getTaskId());
            taskInfo.setMemo(jobInfo.getName()+" 【"+stateStr+"】 "+msg);
            taskInfoService.update(taskInfo);
        }
    }

    /**
     * 查询执行步骤
     *
     * @param id 执行步骤主键
     * @return 执行步骤
     */
    @Override
    public JobInfo selectJobInfoById(Long id)
    {
        return jobInfoMapper.selectJobInfoById(id);
    }

    @Override
    public JobInfo findByIndex(Long taskId, Integer index) {
        return jobInfoMapper.findByIndex(taskId,index);
    }

    /**
     * 查询执行步骤列表
     * 
     * @param criteria
     * @return 执行步骤
     */
    @Override
    public List<JobInfoDto> findList(JobInfoCriteria criteria)
    {
        return jobInfoMapper.findList(criteria);
    }

    @Override
    public List<JobInfo> findInJobByLineCode(String lineCode) {
        return jobInfoMapper.findInJobByLineCode(lineCode);
    }

    @Override
    public int updatePalletTask(String cellCode, String taskNo) {
        return jobInfoMapper.updatePalletTask(cellCode,taskNo);
    }

    /**
     * 新增执行步骤
     *
     * @param jobInfo 执行步骤
     * @return 结果
     */
    @Override
    public int insertJobInfo(JobInfo jobInfo)
    {
        jobInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return jobInfoMapper.insertJobInfo(jobInfo);
    }

    /**
     * 修改执行步骤
     *
     * @param jobInfo 执行步骤
     * @return 结果
     */
    @Override
    public int updateJobInfo(JobInfo jobInfo)
    {
        return jobInfoMapper.updateJobInfo(jobInfo);
    }

    /**
     * 批量删除执行步骤
     * 
     * @param ids 需要删除的执行步骤主键
     * @return 结果
     */
    @Override
    public int deleteJobInfoByIds(Long[] ids)
    {
        return jobInfoMapper.deleteJobInfoByIds(ids);
    }

    /**
     * 删除执行步骤信息
     * 
     * @param id 执行步骤主键
     * @return 结果
     */
    @Override
    public int deleteJobInfoById(Long id)
    {
        return jobInfoMapper.deleteJobInfoById(id);
    }

    @Override
    public List<JobInfo> findJobListByTaskNo(Long taskId) {
        return jobInfoMapper.findJobListByTaskNo(taskId);
    }
    @Override
    public List<JobInfo> findByTaskId(Long taskId) {
        Condition condition = new Condition(JobInfo.class);
        condition.createCriteria().andEqualTo("taskId",taskId);
        return jobInfoMapper.selectByCondition(condition);
    }

    @Override
    public List<JobInfo> findJobHisListByTaskNo(Long taskId) {
        return jobInfoMapper.findJobHisListByTaskNo(taskId);
    }
}
