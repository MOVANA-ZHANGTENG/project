package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 分组管理对象 handle_group
 * 
 * @author deer
 * @date 2024-05-15
 */
public class HandleGroup
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 分组名称 */
    @Excel(name = "分组名称")
    private String name;

    @Excel(name = "分组类型")
    private Integer type;

    /** 分组描述 */
    @Excel(name = "分组描述")
    private String memo;

    /** 是否禁用 */
    @Excel(name = "是否禁用")
    private String disableState;

    /** 删除标志 */
    @Excel(name = "删除标志")
    private Integer delFlag;

    /** 创建时间 */
    private String createTime;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    private Long createUserId;

    /** 创建人姓名 */
    @Excel(name = "创建人姓名")
    private String createUserName;

    /** 更新时间 */
    private String updateTime;

    /** 更新人ID */
    @Excel(name = "更新人ID")
    private Long updateUserId;

    /** 更新人姓名 */
    @Excel(name = "更新人姓名")
    private String updateUserName;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setMemo(String memo) 
    {
        this.memo = memo;
    }

    public String getMemo() 
    {
        return memo;
    }
    public void setDisableState(String disableState) 
    {
        this.disableState = disableState;
    }

    public String getDisableState() 
    {
        return disableState;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
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
    public void setUpdateTime(String updateTime) 
    {
        this.updateTime = updateTime;
    }

    public String getUpdateTime() 
    {
        return updateTime;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public void setUpdateUserName(String updateUserName)
    {
        this.updateUserName = updateUserName;
    }

    public String getUpdateUserName() 
    {
        return updateUserName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("memo", getMemo())
            .append("disableState", getDisableState())
            .append("delFlag", getDelFlag())
            .append("createTime", getCreateTime())
            .append("createUserId", getCreateUserId())
            .append("createUserName", getCreateUserName())
            .append("updateTime", getUpdateTime())
            .append("updateUserId", getUpdateUserId())
            .append("updateUserName", getUpdateUserName())
            .append("version", getVersion())
            .toString();
    }
}
