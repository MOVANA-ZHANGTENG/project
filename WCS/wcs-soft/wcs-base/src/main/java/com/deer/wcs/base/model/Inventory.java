package com.deer.wcs.base.model;

import com.deer.wcs.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 库存信息对象 inventory
 * 
 * @author deer
 * @date 2024-08-22
 */
public class Inventory
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Excel(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 物料信息 */
    @Excel(name = "物料信息")
    private String productCode;

    /** 物料名称 */
    @Excel(name = "物料名称")
    private String productName;

    /** 批次号 */
    @Excel(name = "批次号")
    private String batchNo;

    /** 物料规格 */
    @Excel(name = "物料规格")
    private String spec;

    /** 物料数量 */
    @Excel(name = "物料数量")
    private Double quantity;

    /** 托盘编码 */
    @Excel(name = "托盘编码")
    private String palletCode;

    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期", width = 30, dateFormat = "yyyy-MM-dd")
    private String productDate;

    /** 入库日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    private String inDate;

    /** 过期日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "过期日期", width = 30, dateFormat = "yyyy-MM-dd")
    private String exDate;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setProductCode(String productCode) 
    {
        this.productCode = productCode;
    }

    public String getProductCode() 
    {
        return productCode;
    }
    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }
    public void setBatchNo(String batchNo) 
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo() 
    {
        return batchNo;
    }
    public void setSpec(String spec) 
    {
        this.spec = spec;
    }

    public String getSpec() 
    {
        return spec;
    }
    public void setQuantity(Double quantity)
    {
        this.quantity = quantity;
    }

    public Double getQuantity()
    {
        return quantity;
    }
    public void setPalletCode(String palletCode) 
    {
        this.palletCode = palletCode;
    }

    public String getPalletCode() 
    {
        return palletCode;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public String getExDate() {
        return exDate;
    }

    public void setExDate(String exDate) {
        this.exDate = exDate;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public Integer getVersion() 
    {
        return version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("productCode", getProductCode())
            .append("productName", getProductName())
            .append("batchNo", getBatchNo())
            .append("spec", getSpec())
            .append("quantity", getQuantity())
            .append("palletCode", getPalletCode())
            .append("productDate", getProductDate())
            .append("inDate", getInDate())
            .append("exDate", getExDate())
            .append("version", getVersion())
            .toString();
    }
}
