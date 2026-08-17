package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 物料类型对象 item_type
 * 
 * @author deer
 * @date 2025-09-22
 */
public class ItemType
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 类型编码 */
    @Excel(name = "类型编码")
    private String code;

    /** 类型名称 */
    @Excel(name = "类型名称")
    private String name;

    /** 父级ID */
    @Excel(name = "父级ID")
    private Long parentId;

    /** 父级名称 */
    @Excel(name = "父级名称")
    private String parentCode;

    /** $column.columnComment */
    @Excel(name = "创建时间")
    private String createTime;

    /** $column.columnComment */
    @Excel(name = "创建人ID")
    private Long createUserId;

    /** $column.columnComment */
    @Excel(name = "创建人")
    private String createUserName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }
    public void setParentCode(String parentCode) 
    {
        this.parentCode = parentCode;
    }

    public String getParentCode() 
    {
        return parentCode;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("parentId", getParentId())
            .append("parentCode", getParentCode())
            .append("createTime", getCreateTime())
            .append("createUserId", getCreateUserId())
            .append("createUserName", getCreateUserName())
            .toString();
    }
}
