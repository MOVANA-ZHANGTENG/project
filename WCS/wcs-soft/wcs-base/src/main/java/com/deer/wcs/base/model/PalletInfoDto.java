package com.deer.wcs.base.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;

/**
 * 托盘信息对象 pallet_infoDto
 * 
 * @author deer
 * @date 2024-05-29
 */
public class PalletInfoDto extends PalletInfo
{

    private String wareName;

    public String getWareName() {
        return wareName;
    }

    public void setWareName(String wareName) {
        this.wareName = wareName;
    }
}
