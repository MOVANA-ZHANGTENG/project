package com.deer.wcs.base.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 层对象 floor_info
 * 
 * @author deer
 * @date 2025-09-18
 */
public class FloorInfo
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String wareName;

    /** 库区编码 */
    @Excel(name = "库区编码")
    private String areaCode;

    /** Z */
    @Excel(name = "Z")
    private Integer z;

    /** 禁用状态 */
    @Excel(name = "禁用状态")
    private Long disableState;

    /** 创建时间 */
    private String createTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long createUserId;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createUserName;

    /** total_x */
    @Excel(name = "total_x")
    private Integer totalX;

    /** total_y */
    @Excel(name = "total_y")
    private Integer totalY;

    /** xy */
    @Excel(name = "xy")
    private Integer xy;

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
    public void setWareName(String wareName) 
    {
        this.wareName = wareName;
    }

    public String getWareName() 
    {
        return wareName;
    }
    public void setAreaCode(String areaCode) 
    {
        this.areaCode = areaCode;
    }

    public String getAreaCode() 
    {
        return areaCode;
    }

    public void setDisableState(Long disableState)
    {
        this.disableState = disableState;
    }

    public Long getDisableState()
    {
        return disableState;
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
    public void setTotalX(Integer totalX) 
    {
        this.totalX = totalX;
    }

    public Integer getTotalX() 
    {
        return totalX;
    }
    public void setTotalY(Integer totalY) 
    {
        this.totalY = totalY;
    }

    public Integer getTotalY() 
    {
        return totalY;
    }
    public void setXy(Integer xy) 
    {
        this.xy = xy;
    }

    public Integer getXy() 
    {
        return xy;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
