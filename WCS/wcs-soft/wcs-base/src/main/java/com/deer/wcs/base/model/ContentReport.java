package com.deer.wcs.base.model;

public class ContentReport {
    private String lineCode;
    private String itemCode;
    private String itemName;
    private Integer totalNum;
    private Integer hasNum;
    private Integer limitNum;
    private String lineName;
    private String itemTypeName;
    private String wareCode;
    private String wareName;

    public String getWareCode() {
        return wareCode;
    }
    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }
    public String getWareName() {
        return wareName;
    }
    public void setWareName(String wareName) {
        this.wareName = wareName;
    }

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

    public String getItemTypeName() {
        return itemTypeName;
    }

    public void setItemTypeName(String itemTypeName) {
        this.itemTypeName = itemTypeName;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getHasNum() {
        return hasNum;
    }

    public void setHasNum(Integer hasNum) {
        this.hasNum = hasNum;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    //pro_line_code,pro_position_content.item_code,COUNT(1) as totalNum,sum(is_ok) as hasNum,line_item.quantity as limit_quantity
}
