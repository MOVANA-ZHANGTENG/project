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
 * 产品对象 product_info
 * 
 * @author deer
 * @date 2024-12-25
 */
public class ProductInfo
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 产品编码 */
    @Excel(name = "产品编码")
    private String code;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String name;

    /** 规格 */
    @Excel(name = "规格")
    private String spec;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 上架策略 */
    @Excel(name = "上架策略")
    private String inCellTactics;

    /** a-频率高  b-中等 c-频率低 */
    @Excel(name = "a-频率高  b-中等 c-频率低")
    private String abc;

    /** 类型 */
    @Excel(name = "类型")
    private String itemTypeCode;

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
    public void setSpec(String spec) 
    {
        this.spec = spec;
    }

    public String getSpec() 
    {
        return spec;
    }
    public void setModel(String model) 
    {
        this.model = model;
    }

    public String getModel() 
    {
        return model;
    }
    public void setInCellTactics(String inCellTactics) 
    {
        this.inCellTactics = inCellTactics;
    }

    public String getInCellTactics() 
    {
        return inCellTactics;
    }
    public void setAbc(String abc) 
    {
        this.abc = abc;
    }

    public String getAbc() 
    {
        return abc;
    }
    public void setItemTypeCode(String itemTypeCode) 
    {
        this.itemTypeCode = itemTypeCode;
    }

    public String getItemTypeCode() 
    {
        return itemTypeCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("spec", getSpec())
            .append("model", getModel())
            .append("inCellTactics", getInCellTactics())
            .append("abc", getAbc())
            .append("itemTypeCode", getItemTypeCode())
            .toString();
    }
}
