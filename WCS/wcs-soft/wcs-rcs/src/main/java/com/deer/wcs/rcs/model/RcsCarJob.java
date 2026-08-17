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
 * 小车任务详情对象 rcs_car_job
 * 
 * @author deer
 * @date 2025-07-07
 */
public class RcsCarJob
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 任务ID */
    @Excel(name = "任务ID")
    private Long taskId;

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

    /** 任务状态（待分配、执行中、暂停、完成、失败） */
    @Excel(name = "任务状态", readConverterExp = "待=分配、执行中、暂停、完成、失败")
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
    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
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
            .append("taskId", getTaskId())
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
