package com.deer.wcs.task.model;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;

/**
 * 扫描失败率对象 sao_ma_success
 * 
 * @author deer
 * @date 2025-10-15
 */
public class SaoMaSuccess
{
    private static final long serialVersionUID = 1L;

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 类型 */
    @Excel(name = "类型")
    private Integer type;

    /** 班次，8-8 */
    @Excel(name = "班次")
    private String classTime;

    /** 班次的总数量 */
    @Excel(name = "班次的总数量")
    private Integer classNumber;

    /** 班次成功率 */
    @Excel(name = "班次成功率")
    private String classSuccess;

    @Excel(name = "班次失败任务数")
    private Integer taskNumber;

    @Excel(name = "班次失败任务率")
    private String taskSuccess;

    @Excel(name = "班次总任务数量")
    private String taskNumberAll;

    @Excel(name = "创建时间")
    private String createTime;

    public String getTaskNumberAll() {
        return taskNumberAll;
    }

    public void setTaskNumberAll(String taskNumberAll) {
        this.taskNumberAll = taskNumberAll;
    }

    public String getCreateTime() {
        return createTime;
    }

    public Integer getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(Integer taskNumber) {
        this.taskNumber = taskNumber;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }


    public String getTaskSuccess() {
        return taskSuccess;
    }

    public void setTaskSuccess(String taskSuccess) {
        this.taskSuccess = taskSuccess;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    public void setClassTime(String classTime) 
    {
        this.classTime = classTime;
    }

    public String getClassTime() 
    {
        return classTime;
    }
    public void setClassNumber(Integer classNumber) 
    {
        this.classNumber = classNumber;
    }

    public Integer getClassNumber() 
    {
        return classNumber;
    }
    public void setClassSuccess(String classSuccess) 
    {
        this.classSuccess = classSuccess;
    }

    public String getClassSuccess() 
    {
        return classSuccess;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("type", getType())
            .append("classTime", getClassTime())
            .append("classNumber", getClassNumber())
            .append("classSuccess", getClassSuccess())
            .toString();
    }
}
