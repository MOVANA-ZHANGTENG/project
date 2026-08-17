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
 * 工艺流程工序关联对象 pro_route_process
 * 
 * @author deer
 * @date 2024-11-21
 */
public class ProRouteProcess
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 工艺流程ID */
    private Long proRouteId;

    /** 工序ID */
    private Long proProcessId;

    /** 工序编码 */
    @Excel(name = "工序编码")
    private String proProcessCode;

    /** 工序名称 */
    @Excel(name = "工序名称")
    private String proProcessName;

    /** NEXT工序ID */
    @Excel(name = "NEXT工序ID")
    private Long nextProPorcessId;

    /** NEXT工序编码 */
    @Excel(name = "NEXT工序编码")
    private String nextProProcessCode;

    /** NEXT工序名称 */
    @Excel(name = "NEXT工序名称")
    private String nextProProcessName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setProRouteId(Long proRouteId) 
    {
        this.proRouteId = proRouteId;
    }

    public Long getProRouteId() 
    {
        return proRouteId;
    }
    public void setProProcessId(Long proProcessId) 
    {
        this.proProcessId = proProcessId;
    }

    public Long getProProcessId() 
    {
        return proProcessId;
    }
    public void setProProcessCode(String proProcessCode) 
    {
        this.proProcessCode = proProcessCode;
    }

    public String getProProcessCode() 
    {
        return proProcessCode;
    }
    public void setProProcessName(String proProcessName) 
    {
        this.proProcessName = proProcessName;
    }

    public String getProProcessName() 
    {
        return proProcessName;
    }
    public void setNextProPorcessId(Long nextProPorcessId) 
    {
        this.nextProPorcessId = nextProPorcessId;
    }

    public Long getNextProPorcessId() 
    {
        return nextProPorcessId;
    }
    public void setNextProProcessCode(String nextProProcessCode) 
    {
        this.nextProProcessCode = nextProProcessCode;
    }

    public String getNextProProcessCode() 
    {
        return nextProProcessCode;
    }
    public void setNextProProcessName(String nextProProcessName) 
    {
        this.nextProProcessName = nextProProcessName;
    }

    public String getNextProProcessName() 
    {
        return nextProProcessName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("proRouteId", getProRouteId())
            .append("proProcessId", getProProcessId())
            .append("proProcessCode", getProProcessCode())
            .append("proProcessName", getProProcessName())
            .append("nextProPorcessId", getNextProPorcessId())
            .append("nextProProcessCode", getNextProProcessCode())
            .append("nextProProcessName", getNextProProcessName())
            .toString();
    }
}
