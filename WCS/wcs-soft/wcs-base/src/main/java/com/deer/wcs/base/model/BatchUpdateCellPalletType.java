package com.deer.wcs.base.model;

import java.util.List;

/**
 * 批量更新库位适用托盘类型
 */
public class BatchUpdateCellPalletType {

    private String wareCode;

    private List<String> cellCodes;

    private String palletType;

    public String getWareCode() {
        return wareCode;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public List<String> getCellCodes() {
        return cellCodes;
    }

    public void setCellCodes(List<String> cellCodes) {
        this.cellCodes = cellCodes;
    }

    public String getPalletType() {
        return palletType;
    }

    public void setPalletType(String palletType) {
        this.palletType = palletType;
    }
}
