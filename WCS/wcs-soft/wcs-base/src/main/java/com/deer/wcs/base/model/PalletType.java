package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 托盘类型对象 pallet_type
 * 
 * @author deer
 * @date 2024-05-29
 */
public class PalletType
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 类型编码 */
    @Excel(name = "类型编码")
    private String code;

    /** 类型名称 */
    @Excel(name = "类型名称")
    private String name;

    /** 托盘重量 */
    @Excel(name = "托盘重量")
    private Double weight;

    /** 托盘承重 */
    @Excel(name = "托盘承重")
    private Double maxWeight;

    /** 托盘长度 */
    @Excel(name = "托盘长度")
    private Double length;

    /** 托盘宽度 */
    @Excel(name = "托盘宽度")
    private Double width;

    /** 托盘高度 */
    @Excel(name = "托盘高度")
    private Double height;

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
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
    public void setWeight(Double weight) 
    {
        this.weight = weight;
    }

    public Double getWeight() 
    {
        return weight;
    }
    public void setLength(Double length) 
    {
        this.length = length;
    }

    public Double getLength() 
    {
        return length;
    }
    public void setWidth(Double width) 
    {
        this.width = width;
    }

    public Double getWidth() 
    {
        return width;
    }
    public void setHeight(Double height) 
    {
        this.height = height;
    }

    public Double getHeight() 
    {
        return height;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("weight", getWeight())
            .append("length", getLength())
            .append("width", getWidth())
            .append("height", getHeight())
            .toString();
    }
}
