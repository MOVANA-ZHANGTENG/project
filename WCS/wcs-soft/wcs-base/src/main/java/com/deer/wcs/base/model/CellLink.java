package com.deer.wcs.base.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

/**
 * 库位邻接关系，存储四向车调度系统的节点联通关系对象 cell_link
 * 
 * @author deer
 * @date 2025-10-14
 */
public class CellLink
{
    private static final long serialVersionUID = 1L;

    /** 主键ID，自增长 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 起始库位ID */
    @Excel(name = "起始库位ID")
    private Long fromCellId;

    /** 目标库位ID */
    @Excel(name = "目标库位ID")
    private Long toCellId;

    /** 节点间距离，默认1.0 */
    @Excel(name = "节点间距离，默认1.0")
    private BigDecimal distance;

    /** 是否阻塞，0-正常通行，1-阻塞不可通行 */
    @Excel(name = "是否阻塞，0-正常通行，1-阻塞不可通行")
    private Integer isBlocked;

    /** 仓库信息，可存储仓库编号、区域标识等 */
    @Excel(name = "仓库信息，可存储仓库编号、区域标识等")
    private String wareCode;

    /** 记录创建时间 */
    private String createTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setFromCellId(Long fromCellId) 
    {
        this.fromCellId = fromCellId;
    }

    public Long getFromCellId() 
    {
        return fromCellId;
    }
    public void setToCellId(Long toCellId) 
    {
        this.toCellId = toCellId;
    }

    public Long getToCellId() 
    {
        return toCellId;
    }
    public void setDistance(BigDecimal distance) 
    {
        this.distance = distance;
    }

    public BigDecimal getDistance() 
    {
        return distance;
    }
    public void setIsBlocked(Integer isBlocked) 
    {
        this.isBlocked = isBlocked;
    }

    public Integer getIsBlocked() 
    {
        return isBlocked;
    }

    public String getWareCode() {
        return wareCode;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }

    public String getCreateTime() 
    {
        return createTime;
    }


}
