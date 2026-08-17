package com.deer.wcs.task.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

/**
 * AGV交管对象 agv_zone
 * 
 * @author deer
 * @date 2024-11-26
 */
public class AgvZone
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 编码 */
    @Excel(name = "编码")
    private String code;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 仓库名 */
    @Excel(name = "仓库名")
    private String wareName;

    /** AGV厂家 */
    @Excel(name = "AGV厂家")
    private String agvType;

    /** 当前agv数量 */
    @Excel(name = "当前agv数量")
    private Integer agvCount;

    /** 更新时间 */
    private String updateTime;

    /** 版本号 */
    @Excel(name = "版本号")
    private Long version;

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
    public void setAgvType(String agvType) 
    {
        this.agvType = agvType;
    }

    public String getAgvType() 
    {
        return agvType;
    }
    public void setAgvCount(Integer agvCount) 
    {
        this.agvCount = agvCount;
    }

    public Integer getAgvCount() 
    {
        return agvCount;
    }
    public void setUpdateTime(String updateTime) 
    {
        this.updateTime = updateTime;
    }

    public String getUpdateTime() 
    {
        return updateTime;
    }
    public void setVersion(Long version) 
    {
        this.version = version;
    }

    public Long getVersion() 
    {
        return version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("wareCode", getWareCode())
            .append("wareName", getWareName())
            .append("agvType", getAgvType())
            .append("agvCount", getAgvCount())
            .append("updateTime", getUpdateTime())
            .append("version", getVersion())
            .toString();
    }
}
