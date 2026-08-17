package com.deer.wcs.base.model;

import com.deer.wcs.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 库区对象 area_info
 * 
 * @author deer
 * @date 2024-04-28
 */
public class AreaInfo
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 编码 */
    @Excel(name = "编码")
    private String code;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String wareName;

    /** 0-堆垛机  1-四向车 2-料箱车 3-AGV 4-人工区域 */
    @Excel(name = "0-堆垛机  1-四向车 2-料箱车 3-AGV 4-人工区域")
    private Integer type;

    /** 创建时间 */
    private String createTime;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    private Long createUserId;

    /** 创建人名字 */
    @Excel(name = "创建人名字")
    private String createUserName;

    /** 更新时间 */
    private String updateTime;

    /** 更新人ID */
    @Excel(name = "更新人ID")
    private Long updateUserId;

    /** 更新人名字 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新人名字", width = 30, dateFormat = "yyyy-MM-dd")
    private String updateUserName;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    /** 删除标志 */
    @Excel(name = "删除标志")
    private Integer isDelete;

    /** 巷道分配方式  1-任务数平衡 2-物料平衡 3-空货位平衡 */
    @Excel(name = "巷道分配方式  1-任务数平衡 2-物料平衡 3-空货位平衡")
    private Integer lineAllotType;

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
    public void setWareCode(String wareCode) 
    {
        this.wareCode = wareCode;
    }

    public String getWareCode() 
    {
        return wareCode;
    }
    public void setWareName(String wareName) 
    {
        this.wareName = wareName;
    }

    public String getWareName() 
    {
        return wareName;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
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
    public void setUpdateUserId(Long updateUserId) 
    {
        this.updateUserId = updateUserId;
    }

    public Long getUpdateUserId() 
    {
        return updateUserId;
    }
    public void setUpdateUserName(String updateUserName)
    {
        this.updateUserName = updateUserName;
    }

    public String getUpdateUserName()
    {
        return updateUserName;
    }
    public void setVersion(Integer version) 
    {
        this.version = version;
    }

    public Integer getVersion() 
    {
        return version;
    }
    public void setIsDelete(Integer isDelete) 
    {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() 
    {
        return isDelete;
    }
    public void setLineAllotType(Integer lineAllotType) 
    {
        this.lineAllotType = lineAllotType;
    }

    public Integer getLineAllotType() 
    {
        return lineAllotType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("wareCode", getWareCode())
            .append("wareName", getWareName())
            .append("type", getType())
            .append("createTime", getCreateTime())
            .append("createUserId", getCreateUserId())
            .append("createUserName", getCreateUserName())
            .append("updateTime", getUpdateTime())
            .append("updateUserId", getUpdateUserId())
            .append("updateUserName", getUpdateUserName())
            .append("version", getVersion())
            .append("isDelete", getIsDelete())
            .append("lineAllotType", getLineAllotType())
            .toString();
    }
}
