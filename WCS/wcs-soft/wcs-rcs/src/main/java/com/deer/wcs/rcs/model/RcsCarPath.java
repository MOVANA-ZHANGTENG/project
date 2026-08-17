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
 * 车路径对象 rcs_car_path
 * 
 * @author deer
 * @date 2025-10-15
 */
public class RcsCarPath
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long rcsCarId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long jobId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long taskId;

    /** 起点Cell ID */
    @Excel(name = "起点Cell ID")
    private Long fromCellId;
    private String fromCellCode;

    /** 终点Cell ID */
    @Excel(name = "终点Cell ID")
    private Long toCellId;
    private String toCellCode;

    /** 路径状态：0-未执行 1-已占用 2-执行中 3-已完成 */
    @Excel(name = "路径状态")
    private Integer state;


    private String createTime;
    private String allotTime;
    private String cmdTime;
    private String finishTime;

    private String wareCode;

    private Integer z;

    public String getFromCellCode() {
        return fromCellCode;
    }

    public void setFromCellCode(String fromCellCode) {
        this.fromCellCode = fromCellCode;
    }

    public String getToCellCode() {
        return toCellCode;
    }

    public void setToCellCode(String toCellCode) {
        this.toCellCode = toCellCode;
    }

    public String getWareCode() {
        return wareCode;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAllotTime() {
        return allotTime;
    }

    public void setAllotTime(String allotTime) {
        this.allotTime = allotTime;
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

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setRcsCarId(Long rcsCarId) 
    {
        this.rcsCarId = rcsCarId;
    }

    public Long getRcsCarId() 
    {
        return rcsCarId;
    }
    public void setJobId(Long jobId)
    {
        this.jobId = jobId;
    }

    public Long getJobId()
    {
        return jobId;
    }
    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }
    public void setFromCellId(Long fromCellId) 
    {
        this.fromCellId = fromCellId;
    }

    public Long getFromCellId() 
    {
        return fromCellId;
    }
    
    public void setToCellId(Long toCellId) 
    {
        this.toCellId = toCellId;
    }

    public Long getToCellId() 
    {
        return toCellId;
    }
    
    public void setState(Integer state) 
    {
        this.state = state;
    }

    public Integer getState() 
    {
        return state;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("rcsCarId", getRcsCarId())
            .append("jobId", getJobId())
            .append("taskId", getTaskId())
            .append("fromCellId", getFromCellId())
            .append("toCellId", getToCellId())
            .append("state", getState())
            .toString();
    }
}
