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
 * 产线物料对象 line_item
 * 
 * @author deer
 * @date 2024-12-21
 */
public class LineItem
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 产线 */
    @Excel(name = "产线")
    private String lineCode;

    /** 物料 */
    @Excel(name = "物料")
    private String itemCode;

    /** 最低数量 */
    @Excel(name = "最低数量")
    private Integer quantity;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setLineCode(String lineCode) 
    {
        this.lineCode = lineCode;
    }

    public String getLineCode() 
    {
        return lineCode;
    }
    public void setItemCode(String itemCode) 
    {
        this.itemCode = itemCode;
    }

    public String getItemCode() 
    {
        return itemCode;
    }
    public void setQuantity(Integer quantity) 
    {
        this.quantity = quantity;
    }

    public Integer getQuantity() 
    {
        return quantity;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("lineCode", getLineCode())
            .append("itemCode", getItemCode())
            .append("quantity", getQuantity())
            .toString();
    }
}
