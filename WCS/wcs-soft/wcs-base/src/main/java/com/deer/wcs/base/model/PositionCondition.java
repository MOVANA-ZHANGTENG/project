package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 路径对象 position_condition
 * 
 * @author deer
 * @date 2024-04-28
 */
public class PositionCondition
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 流程编码 */
    @Excel(name = "流程编码")
    private String stepCode;

    /** 流程编码 */
    @Excel(name = "流程名称")
    private String stepName;

    /** 起始站台编码 */
    @Excel(name = "起始站台编码")
    private String fromCode;

    /** 目标站台编码 */
    @Excel(name = "目标站台编码")
    private String toCode;

    /** 通过时间（秒） */
    @Excel(name = "通过时间", readConverterExp = "秒=")
    private Integer taskTime;

    /** 堵塞时间（秒） */
    @Excel(name = "堵塞时间", readConverterExp = "秒=")
    private Integer blockingTime;
    private String wareCode;

    private String wareName;
    private String templateCode;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public PositionCondition(){

    }
    public PositionCondition(String fromCode, String toCode, int taskTime) {
        this.fromCode = fromCode;
        this.toCode = toCode;
        this.taskTime = taskTime;
    }

    public String getStepCode() {
        return stepCode;
    }

    public void setStepCode(String stepCode) {
        this.stepCode = stepCode;
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

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setFromCode(String fromCode) 
    {
        this.fromCode = fromCode;
    }

    public String getFromCode() 
    {
        return fromCode;
    }
    public void setToCode(String toCode) 
    {
        this.toCode = toCode;
    }

    public String getToCode() 
    {
        return toCode;
    }
    public void setTaskTime(Integer taskTime) 
    {
        this.taskTime = taskTime;
    }

    public Integer getTaskTime() 
    {
        return taskTime;
    }
    public void setBlockingTime(Integer blockingTime) 
    {
        this.blockingTime = blockingTime;
    }

    public Integer getBlockingTime() 
    {
        return blockingTime;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("fromCode", getFromCode())
            .append("toCode", getToCode())
            .append("taskTime", getTaskTime())
            .append("blockingTime", getBlockingTime())
            .toString();
    }
}
