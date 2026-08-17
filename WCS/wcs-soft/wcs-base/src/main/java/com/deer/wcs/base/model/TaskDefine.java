package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 任务定义对象 task_define
 * 
 * @author deer
 * @date 2024-04-28
 */
public class TaskDefine
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 库区编码 */
    @Excel(name = "库区编码")
    private String areaCode;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 排序 */
    @Excel(name = "排序")
    private Integer jobIndex;

    @Excel(name = "起点位置")
    private String fromCellCode;

    @Excel(name = "终点位置")
    private String toCellCode;

    /** 上一个步骤ID（用于连线） */
    @Excel(name = "上一个步骤ID")
    private Long lastId;

    /** 是否为判断步骤（0-否，1-是） */
    @Excel(name = "是否为判断步骤", readConverterExp = "0=否,1=是")
    private Integer isJudgeStep;

    /** 判断分支类型（yes-成功分支，no-失败分支，null-普通连接） */
    @Excel(name = "判断分支类型")
    private String judgeBranchType;

    /** 节点X坐标 */
    private Double positionX;

    /** 节点Y坐标 */
    private Double positionY;

    @Excel(name = "创建时间")
    private String createTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setWareCode(String wareCode) 
    {
        this.wareCode = wareCode;
    }

    public String getWareCode() 
    {
        return wareCode;
    }
    public void setAreaCode(String areaCode) 
    {
        this.areaCode = areaCode;
    }

    public String getAreaCode() 
    {
        return areaCode;
    }
    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public Integer getJobIndex() {
        return jobIndex;
    }

    public void setJobIndex(Integer jobIndex) {
        this.jobIndex = jobIndex;
    }

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

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public Double getPositionX() {
        return positionX;
    }

    public void setPositionX(Double positionX) {
        this.positionX = positionX;
    }

    public Double getPositionY() {
        return positionY;
    }

    public void setPositionY(Double positionY) {
        this.positionY = positionY;
    }

    /**
     * 判断是否为判断步骤
     */
    public boolean isJudgeStep() {
        return isJudgeStep != null && isJudgeStep == 1;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("wareCode", getWareCode())
            .append("areaCode", getAreaCode())
            .append("type", getType())
            .append("name", getName())
            .append("jobIndex", getJobIndex())
            .append("fromCellCode", getFromCellCode())
            .append("toCellCode", getToCellCode())
            .append("lastId", getLastId())
            .append("isJudgeStep", getIsJudgeStep())
            .append("judgeBranchType", getJudgeBranchType())
            .append("positionX", getPositionX())
            .append("positionY", getPositionY())
            .append("createTime", getCreateTime())
            .toString();
    }
}
