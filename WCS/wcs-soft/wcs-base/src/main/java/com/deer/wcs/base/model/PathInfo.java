package com.deer.wcs.base.model;

import com.deer.wcs.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 执行路径对象 path_info
 * 
 * @author deer
 * @date 2024-05-10
 */
public class PathInfo
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** name */
    @Excel(name = "name")
    private String name;
    /** 任务ID */
    @Excel(name = "任务ID")
    private Long taskId;

    /** 任务编号 */
    @Excel(name = "任务编号")
    private String taskNo;

    /** 步骤ID */
    @Excel(name = "步骤ID")
    private Long jobId;

    /** 优先级 */
    @Excel(name = "优先级")
    private Integer pathIndex;

    /** 任务类型 */
    @Excel(name = "任务类型")
    private String type;

    /** 托盘编码 */
    @Excel(name = "托盘编码")
    private String palletCode;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String wareName;

    /** 起点位置 */
    @Excel(name = "起点位置")
    private String fromCellCode;

    /** 终点位置 */
    @Excel(name = "终点位置")
    private String toCellCode;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private String createTime;

    /** 当前状态 */
    @Excel(name = "当前状态")
    private Integer state;

    /** 执行时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "执行时间", width = 30, dateFormat = "yyyy-MM-dd")
    private String cmdTime;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private String finishTime;

    @Excel(name = "信息")
    private String memo;

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
    public void setJobId(Long jobId) 
    {
        this.jobId = jobId;
    }

    public Long getJobId() 
    {
        return jobId;
    }
    public void setPathIndex(Integer pathIndex) 
    {
        this.pathIndex = pathIndex;
    }

    public Integer getPathIndex() 
    {
        return pathIndex;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getCmdTime() {
        return cmdTime;
    }

    public void setCmdTime(String cmdTime) {
        this.cmdTime = cmdTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getWareCode() {
        return wareCode;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public String getWareName() {
        return wareName;
    }

    public void setWareName(String wareName) {
        this.wareName = wareName;
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
            .append("jobId", getJobId())
            .append("pathIndex", getPathIndex())
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
