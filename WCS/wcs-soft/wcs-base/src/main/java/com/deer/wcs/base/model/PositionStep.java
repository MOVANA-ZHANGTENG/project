package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Id;

/**
 * 流程对象 position_step
 * 
 * @author deer
 * @date 2024-07-01
 */
public class PositionStep
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 流程编码 */
    @Excel(name = "流程编码")
    private String code;

    /** 流程名称 */
    @Excel(name = "流程名称")
    private String name;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String wareName;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("wareCode", getWareCode())
            .append("wareName", getWareName())
            .toString();
    }
}
