package com.deer.wcs.task.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 执行步骤对象 job_info
 * 
 * @author deer
 * @date 2024-05-10
 */
public class JobInfo
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 任务ID */
    @Excel(name = "任务ID")
    private Long taskId;

    /** 任务编号 */
    @Excel(name = "任务编号")
    private String taskNo;

    private Long rcsCarId;
    private String deviceCode ;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 优先级 */
    @Excel(name = "优先级")
    private Integer jobIndex;

    /** 上一个步骤的Job ID */
    @Excel(name = "上一个步骤的Job ID")
    private Long lastJobId;

    /** 是否为判断步骤（0-否，1-是） */
    @Excel(name = "是否为判断步骤", readConverterExp = "0=否,1=是")
    private Integer isJudgeStep;

    /** 判断分支类型（yes-成功分支，no-失败分支，null-普通连接） */
    @Excel(name = "判断分支类型")
    private String judgeBranchType;
    private String judgeResult;

    /** 任务类型 */
    @Excel(name = "任务类型")
    private String type;

    /** 托盘编码 */
    @Excel(name = "托盘编码")
    private String palletCode;

    /** 起点位置 */
    @Excel(name = "起点位置")
    private String fromCellCode;

    /** 终点位置 */
    @Excel(name = "终点位置")
    private String toCellCode;

    /** 创建时间 */
    @Excel(name = "创建时间")
    private String createTime;

    /** 当前状态 */
    @Excel(name = "当前状态")
    private Integer state;

    /** 执行时间 */
    @Excel(name = "执行时间")
    private String cmdTime;

    /** 完成时间 */
    @Excel(name = "完成时间")
    private String finishTime;

    @Excel(name = "信息")
    private String memo;

    public String getJudgeResult() {
        return judgeResult;
    }

    public void setJudgeResult(String judgeResult) {
        this.judgeResult = judgeResult;
    }

    public JobInfo() {
    }
    public JobInfo(String fromCellCode, String toCellCode) {
        this.fromCellCode = fromCellCode;
        this.toCellCode = toCellCode;
    }

    public Integer getIsJudgeStep() {
        return isJudgeStep;
    }

    public void setIsJudgeStep(Integer isJudgeStep) {
        this.isJudgeStep = isJudgeStep;
    }

    public String getJudgeBranchType() {
        return judgeBranchType;
    }

    public void setJudgeBranchType(String judgeBranchType) {
        this.judgeBranchType = judgeBranchType;
    }

    public Long getRcsCarId() {
        return rcsCarId;
    }

    public void setRcsCarId(Long rcsCarId) {
        this.rcsCarId = rcsCarId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }
    public void setTaskNo(String taskNo) 
    {
        this.taskNo = taskNo;
    }

    public String getTaskNo() 
    {
        return taskNo;
    }

    public Integer getJobIndex() {
        return jobIndex;
    }

    public void setJobIndex(Integer jobIndex) {
        this.jobIndex = jobIndex;
    }

    public Long getLastJobId() {
        return lastJobId;
    }

    public void setLastJobId(Long lastJobId) {
        this.lastJobId = lastJobId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPalletCode(String palletCode)
    {
        this.palletCode = palletCode;
    }

    public String getPalletCode() 
    {
        return palletCode;
    }
    public void setFromCellCode(String fromCellCode) 
    {
        this.fromCellCode = fromCellCode;
    }

    public String getFromCellCode() 
    {
        return fromCellCode;
    }
    public void setToCellCode(String toCellCode) 
    {
        this.toCellCode = toCellCode;
    }

    public String getToCellCode() 
    {
        return toCellCode;
    }
    public void setCreateTime(String createTime) 
    {
        this.createTime = createTime;
    }

    public String getCreateTime() 
    {
        return createTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public void setCmdTime(String cmdTime)
    {
        this.cmdTime = cmdTime;
    }

    public String getCmdTime() 
    {
        return cmdTime;
    }
    public void setFinishTime(String finishTime) 
    {
        this.finishTime = finishTime;
    }

    public String getFinishTime() 
    {
        return finishTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("taskId", getTaskId())
            .append("taskNo", getTaskNo())
            .append("jobIndex", getJobIndex())
            .append("lastJobId", getLastJobId())
            .append("type", getType())
            .append("palletCode", getPalletCode())
            .append("fromCellCode", getFromCellCode())
            .append("toCellCode", getToCellCode())
            .append("createTime", getCreateTime())
            .append("state", getState())
            .append("cmdTime", getCmdTime())
            .append("finishTime", getFinishTime())
            .toString();
    }
}
