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
 * 工艺流程对象 pro_route
 * 
 * @author deer
 * @date 2024-11-21
 */
public class ProRoute
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

    /** 状态 */
    @Excel(name = "状态")
    private Integer state;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String wareName;
    private String lineCode;
    private String lineName;
    private String modelData;

    public String getModelData() {
        return modelData;
    }

    public void setModelData(String modelData) {
        this.modelData = modelData;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getLineName() {
        return lineName;
    }
    public void setLineName(String lineName) {
        this.lineName = lineName;
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
    public void setState(Integer state) 
    {
        this.state = state;
    }

    public Integer getState() 
    {
        return state;
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
            .append("state", getState())
            .append("wareCode", getWareCode())
            .append("wareName", getWareName())
            .toString();
    }
}
