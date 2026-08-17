package com.deer.wcs.base.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;

/**
 * 产线物料对象 line_itemDto
 * 
 * @author deer
 * @date 2024-12-21
 */
public class LineItemDto extends LineItem
{

    private String itemName;
    private String lineName;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }



}
