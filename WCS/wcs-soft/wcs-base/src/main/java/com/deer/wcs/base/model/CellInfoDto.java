package com.deer.wcs.base.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;

/**
 * 库位对象 cell_infoDto
 * 
 * @author deer
 * @date 2024-04-28
 */
public class CellInfoDto extends CellInfo
{
    private Double invenState1;

    private String palletCode;

    /**
     * 托盘是否为空箱（从 pallet_info 表关联查询）
     * "1"=空箱，"0"=有货，null=无托盘
     */
    @Excel(name = "是否空箱")
    private String isEmpty;

    private Long bagId;

    public String getPalletCode() {
        return palletCode;
    }

    public void setPalletCode(String palletCode) {
        this.palletCode = palletCode;
    }

    public String getIsEmpty() {
        return isEmpty;
    }

    public void setIsEmpty(String isEmpty) {
        this.isEmpty = isEmpty;
    }

    public Long getBagId() {
        return bagId;
    }

    public void setBagId(Long bagId) {
        this.bagId = bagId;
    }

    public Double getInvenState1() {
        return invenState1;
    }

    public void setInvenState1(Double invenState1) {
        this.invenState1 = invenState1;
    }
}
