package com.deer.wcs.system.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Id;

/**
 * 单据记录对象 bill_record
 * 
 * @author deer
 * @date 2023-10-13
 */
public class BillRecord
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    private Integer billRecordId;

    /** 单据编码 */
    @Excel(name = "单据编码")
    private String billNo;

    /** 单据类型 */
    @Excel(name = "单据类型")
    private String billType;

    /** 时间 */
    private String createTime;

    /** 操作人ID */
    @Excel(name = "操作人ID")
    private Long createUserId;

    /** 操作人 */
    @Excel(name = "操作人")
    private String createUserName;

    /** 操作内容 */
    @Excel(name = "操作内容")
    private String content;

    public void setBillRecordId(Integer billRecordId) 
    {
        this.billRecordId = billRecordId;
    }

    public Integer getBillRecordId() 
    {
        return billRecordId;
    }
    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    public String getBillNo()
    {
        return billNo;
    }
    public void setBillType(String billType) 
    {
        this.billType = billType;
    }

    public String getBillType() 
    {
        return billType;
    }
    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }

    public String getCreateTime()
    {
        return createTime;
    }
    public void setCreateUserId(Long createUserId)
    {
        this.createUserId = createUserId;
    }

    public Long getCreateUserId()
    {
        return createUserId;
    }
    public void setCreateUserName(String createUserName) 
    {
        this.createUserName = createUserName;
    }

    public String getCreateUserName() 
    {
        return createUserName;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("billRecordId", getBillRecordId())
            .append("billNo", getBillNo())
            .append("billType", getBillType())
            .append("createTime", getCreateTime())
            .append("createUserId", getCreateUserId())
            .append("createUserName", getCreateUserName())
            .append("content", getContent())
            .toString();
    }



}
