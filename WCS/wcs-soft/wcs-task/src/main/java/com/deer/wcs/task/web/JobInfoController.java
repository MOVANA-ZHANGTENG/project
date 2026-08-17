package com.deer.wcs.task.web;

import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.bean.BeanUtils;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.task.model.JobHandle;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.JobInfoCriteria;
import com.deer.wcs.task.model.JobInfoDto;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfo;
import com.deer.wcs.task.service.JobHandleService;
import com.deer.wcs.task.service.JobInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 执行步骤Controller
 * 
 * @author deer
 * @date 2024-05-10
 */
@Api("执行步骤")
@RestController
@RequestMapping("/wcs-task/job")
public class JobInfoController extends BaseController {
    @Autowired
    private JobInfoService jobInfoService;

    /**
     * 查询执行步骤列表
     */
    @ApiOperation("查询执行步骤列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:job:list')")
    @GetMapping("/list")
    public TableDataInfo list(JobInfoCriteria Criteria) {
        startPage();
        List<JobInfoDto> list = jobInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 根据当前任务号查询执行步骤
     */
    @ApiOperation("根据当前任务号查询执行步骤")
//    @PreAuthorize("@ss.hasPermi('wcs-task:job:list')")
    @GetMapping("/findJobListByTaskNo")
    public Result findJobListByTaskNo(JobInfoCriteria criteria) {
        return Result.success(jobInfoService.findJobListByTaskNo(criteria.getTaskId()));
    }

    /**
     * 根据当前任务号查询执行步骤
     */
    @ApiOperation("根据当前任务号查询执行步骤")
//    @PreAuthorize("@ss.hasPermi('wcs-task:job:list')")
    @GetMapping("/findByTaskId")
    public Result findByTaskId(Long taskId) {
        Condition condition = new Condition(JobInfo.class);
        condition.createCriteria().andEqualTo("taskId", taskId);
        condition.orderBy("id");
        return Result.success(jobInfoService.findByCondition(condition));
    }


    /**
     * 根据历史任务号查询执行步骤
     */
    @ApiOperation("根据历史任务号查询执行步骤")
//    @PreAuthorize("@ss.hasPermi('wcs-task:job:list')")
    @GetMapping("/findJobHisListByTaskNo")
    public Result findJobHisListByTaskNo(JobInfoCriteria criteria) {
        return Result.success(jobInfoService.findJobHisListByTaskNo(criteria.getTaskId()));
    }

    /**
     * 导出执行步骤列表
     */
    @ApiOperation("导出执行步骤列表")
    @PreAuthorize("@ss.hasPermi('wcs-task:job:export')")
    @Log(title = "执行步骤", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JobInfoCriteria criteria) {
        List<JobInfoDto> list = jobInfoService.findList(criteria);
        ExcelUtil<JobInfoDto> util = new ExcelUtil<JobInfoDto>(JobInfoDto.class);
        util.exportExcel(response, list, "执行步骤数据");
    }

    /**
     * 获取执行步骤详细信息
     */
    @ApiOperation("获取执行步骤详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-task:job:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(jobInfoService.findById(id));
    }


    @Autowired
    private JobHandleService jobHandleService;

    @GetMapping(value = "/cmd")
    public Result cmd(  Long id) {
        JobInfo jobInfo = jobInfoService.findById(id);
        Condition condition = new Condition(JobHandle.class);
        condition.createCriteria().andEqualTo("jobId", jobInfo.getId())
                .andEqualTo("type", 1);
        List<JobHandle> jobHandles1 = jobHandleService.findByCondition(condition);
        for (JobHandle jobHandle : jobHandles1) {
            jobHandle.setState(1);
            jobHandleService.update(jobHandle);
        }

        condition = new Condition(JobHandle.class);
        condition.createCriteria().andEqualTo("jobId", jobInfo.getId())
                .andEqualTo("type", 2);
        List<JobHandle> jobHandles2 = jobHandleService.findByCondition(condition);
        for (JobHandle jobHandle : jobHandles2) {
            jobHandle.setState(0);
            jobHandleService.update(jobHandle);
        }
        jobInfo.setState(1);
        jobInfo.setMemo("强制执行");
        jobInfo.setFinishTime(DateUtil.getNowDateTimeString());
        jobInfoService.update(jobInfo);
        return success(jobInfoService.findById(id));
    }

    @GetMapping(value = "/successPre")
    public Result successPre(  Long id) {
        JobInfo jobInfo = jobInfoService.findById(id);
        Condition condition = new Condition(JobHandle.class);
        condition.createCriteria().andEqualTo("jobId", jobInfo.getId())
                .andEqualTo("type", 3);
        List<JobHandle> jobHandles = jobHandleService.findByCondition(condition);
        for (JobHandle jobHandle : jobHandles) {
            jobHandle.setState(1);
            jobHandleService.update(jobHandle);
        }
        jobInfo.setState(3);
        jobInfo.setMemo("强制完成");
        jobInfo.setFinishTime(DateUtil.getNowDateTimeString());
        jobInfoService.update(jobInfo);
        return success(jobInfoService.findById(id));
    }

    /**
     * 新增执行步骤
     */
    @ApiOperation("新增执行步骤")
    @PreAuthorize("@ss.hasPermi('wcs-task:job:add')")
    @Log(title = "执行步骤", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody JobInfo jobInfo) {
        jobInfo.setCreateTime(DateUtil.getNowDateTimeString());

        jobInfoService.save(jobInfo);
        return toAjax(true);
    }

    /**
     * 修改执行步骤
     */
    @ApiOperation("修改执行步骤")
    @PreAuthorize("@ss.hasPermi('wcs-task:job:edit')")
    @Log(title = "执行步骤", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody JobInfo jobInfo) {


        return toAjax(jobInfoService.update(jobInfo));
    }

    /**
     * 删除执行步骤
     */
    @ApiOperation("删除执行步骤")
    @PreAuthorize("@ss.hasPermi('wcs-task:job:remove')")
    @Log(title = "执行步骤", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        return toAjax(jobInfoService.deleteJobInfoByIds(ids));
    }

}
