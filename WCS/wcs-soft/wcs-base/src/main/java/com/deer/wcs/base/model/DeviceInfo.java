package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 设备对象 device_info
 * 
 * @author deer
 * @date 2024-05-16
 */
public class DeviceInfo
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Excel(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 设备编码 */
    @Excel(name = "设备编码")
    private String code;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String name;

    /** 设备类型 */
    @Excel(name = "设备类型")
    private Integer type;

    /** 通讯方式 */
    @Excel(name = "通讯方式")
    private Integer comType;

    /** s7类型 */
    @Excel(name = "s7类型")
    private Integer s7Type;

    /** 通讯地址 */
    @Excel(name = "通讯地址")
    private String ip;

    /** 通讯端口 */
    @Excel(name = "通讯端口")
    private Integer port;

    /** 是否在线 */
    @Excel(name = "是否在线")
    private Integer isOnline;

    /** 运行状态 */
    @Excel(name = "运行状态")
    private Integer state;

    /** 创建时间 */
    @Excel(name = "创建时间")
    private String createTime;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    private Long createUserId;

    /** 创建人姓名 */
    @Excel(name = "创建人姓名")
    private String createUserName;

    /** 更新时间 */
    @Excel(name = "更新时间")
    private String updateTime;

    /** 跟新人ID */
    @Excel(name = "跟新人ID")
    private Long updateUserId;

    /** 更新人姓名 */
    @Excel(name = "更新人姓名")
    private String updateUserName;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    /** 删除标志 */
    @Excel(name = "删除标志")
    private Integer isDelete;

    public Integer getS7Type() {
        return s7Type;
    }

    public void setS7Type(Integer s7Type) {
        this.s7Type = s7Type;
    }

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
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    public void setComType(Integer comType) 
    {
        this.comType = comType;
    }

    public Integer getComType() 
    {
        return comType;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setIsOnline(Integer isOnline)
    {
        this.isOnline = isOnline;
    }

    public Integer getIsOnline() 
    {
        return isOnline;
    }
    public void setState(Integer state) 
    {
        this.state = state;
    }

    public Integer getState() 
    {
        return state;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("type", getType())
            .append("comType", getComType())
            .append("ip", getIp())
            .append("port", getPort())
            .append("isOnline", getIsOnline())
            .append("state", getState())
            .append("createTime", getCreateTime())
            .append("createUserId", getCreateUserId())
            .append("createUserName", getCreateUserName())
            .append("updateTime", getUpdateTime())
            .append("updateUserId", getUpdateUserId())
            .append("updateUserName", getUpdateUserName())
            .append("version", getVersion())
            .append("isDelete", getIsDelete())
            .toString();
    }
}
