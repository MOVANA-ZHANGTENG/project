package com.deer.wcs.task.model;

import com.deer.wcs.common.annotation.Excel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 目的地申请对象 box_target_apply
 *
 * @author deer
 * @date 2023-10-16
 */
public class BoxTargetApply
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @Excel(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 托盘号 */
    @Excel(name = "托盘号")
    private String palletCode;
    private String taskNo;


    /** 申请时位置 */
    @Excel(name = "申请时位置")
    private String pointCode;

    private String wareCode;

    private Double weight;

    /** 时间 */
    private String createTime;

    /** 状态 0-初始化 1-处理中 2-已处理 */
    @Excel(name = "状态 0-初始化 1-处理中 2-已处理")
    private Integer state;

    /** 类型 1-入库申请 2-出库口目的地申请 */
    @Excel(name = "类型 1-入库申请 2-出库口目的地申请")
    private Integer type;


    public String getWareCode() {
        return wareCode;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPalletCode() {
        return palletCode;
    }

    public void setPalletCode(String palletCode) {
        this.palletCode = palletCode;
    }

    public void setPointCode(String pointCode)
    {
        this.pointCode = pointCode;
    }

    public String getPointCode()
    {
        return pointCode;
    }
    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }

    public String getCreateTime()
    {
        return createTime;
    }
    public void setState(Integer state)
    {
        this.state = state;
    }

    public Integer getState()
    {
        return state;
    }
    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getType()
    {
        return type;
    }


}
