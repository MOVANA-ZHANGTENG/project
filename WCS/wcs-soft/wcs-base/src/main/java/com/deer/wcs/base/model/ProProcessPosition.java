package com.deer.wcs.base.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

/**
 * 工序站台对象 pro_process_position
 * 
 * @author deer
 * @date 2024-12-25
 */
public class ProProcessPosition
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long proRouteId;
    /** 工序编码 */
    @Excel(name = "工序编码")
    private Long proProcessId;

    /** 站台编码 */
    @Excel(name = "站台编码")
    private String positionCode;

    public Long getProRouteId() {
        return proRouteId;
    }

    public void setProRouteId(Long proRouteId) {
        this.proRouteId = proRouteId;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setProProcessId(Long proProcessId) 
    {
        this.proProcessId = proProcessId;
    }

    public Long getProProcessId() 
    {
        return proProcessId;
    }
    public void setPositionCode(String positionCode)
    {
        this.positionCode = positionCode;
    }

    public String getPositionCode()
    {
        return positionCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("proProcessId", getProProcessId())
            .append("positionCode", getPositionCode())
            .toString();
    }
}
