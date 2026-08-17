package com.deer.wcs.rcs.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

/**
 * 小车任务主体对象 rcs_car_task
 * 
 * @author deer
 * @date 2025-07-07
 */
public class RcsCarTask
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    @Excel(name = "任务ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 任务编号 */
    @Excel(name = "任务编号")
    private String taskCode;

    /** 任务类型 */
    @Excel(name = "任务类型")
    private String taskType;

    /** 任务优先级 */
    @Excel(name = "任务优先级")
    private Integer priority;

    /** 创建时间 */
    @Excel(name = "创建时间")
    private String createTime;

    /** 创建用户 */
    @Excel(name = "创建用户")
    private String createUserName;

    /** 小车ID */
    @Excel(name = "小车ID")
    private Long allotCarId;

    /** 任务起点 */
    @Excel(name = "任务起点")
    private String fromCellCode;

    /** 任务终点 */
    @Excel(name = "任务终点")
    private String toCellCode;

    /** 搬运托盘 */
    @Excel(name = "搬运托盘")
    private String palletCode;

    /** 任务状态 */
    @Excel(name = "任务状态")
    private String status;

    /** 开始时间 */
    @Excel(name = "开始时间")
    private String startTime;

    /** 结束时间 */
    @Excel(name = "结束时间")
    private String endTime;

    /** 持续时间 */
    @Excel(name = "持续时间")
    private String duration;

    /** 错误代码 */
    @Excel(name = "错误代码")
    private String errorCode;

    /** 错误详情 */
    @Excel(name = "错误详情")
    private String errorMessage;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setTaskCode(String taskCode) 
    {
        this.taskCode = taskCode;
    }

    public String getTaskCode() 
    {
        return taskCode;
    }
    public void setTaskType(String taskType) 
    {
        this.taskType = taskType;
    }

    public String getTaskType() 
    {
        return taskType;
    }
    public void setPriority(Integer priority) 
    {
        this.priority = priority;
    }

    public Integer getPriority() 
    {
        return priority;
    }
    public void setCreateTime(String createTime) 
    {
        this.createTime = createTime;
    }

    public String getCreateTime() 
    {
        return createTime;
    }
    public void setCreateUserName(String createUserName) 
    {
        this.createUserName = createUserName;
    }

    public String getCreateUserName() 
    {
        return createUserName;
    }
    public void setAllotCarId(Long allotCarId) 
    {
        this.allotCarId = allotCarId;
    }

    public Long getAllotCarId() 
    {
        return allotCarId;
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
    public void setPalletCode(String palletCode) 
    {
        this.palletCode = palletCode;
    }

    public String getPalletCode() 
    {
        return palletCode;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setStartTime(String startTime) 
    {
        this.startTime = startTime;
    }

    public String getStartTime() 
    {
        return startTime;
    }
    public void setEndTime(String endTime) 
    {
        this.endTime = endTime;
    }

    public String getEndTime() 
    {
        return endTime;
    }
    public void setDuration(String duration) 
    {
        this.duration = duration;
    }

    public String getDuration() 
    {
        return duration;
    }
    public void setErrorCode(String errorCode) 
    {
        this.errorCode = errorCode;
    }

    public String getErrorCode() 
    {
        return errorCode;
    }
    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("taskCode", getTaskCode())
            .append("taskType", getTaskType())
            .append("priority", getPriority())
            .append("createTime", getCreateTime())
            .append("createUserName", getCreateUserName())
            .append("allotCarId", getAllotCarId())
            .append("fromCellCode", getFromCellCode())
            .append("toCellCode", getToCellCode())
            .append("palletCode", getPalletCode())
            .append("status", getStatus())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("duration", getDuration())
            .append("errorCode", getErrorCode())
            .append("errorMessage", getErrorMessage())
            .toString();
    }
}
