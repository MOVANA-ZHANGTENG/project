package com.deer.wcs.base.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;

/**
 * 物料对象 item_infoDto
 * 
 * @author deer
 * @date 2024-11-21
 */
public class ItemInfoDto extends ItemInfo
{

    private String itemTypeName;

    public String getItemTypeName() {
        return itemTypeName;
    }

    public void setItemTypeName(String itemTypeName) {
        this.itemTypeName = itemTypeName;
    }
}
