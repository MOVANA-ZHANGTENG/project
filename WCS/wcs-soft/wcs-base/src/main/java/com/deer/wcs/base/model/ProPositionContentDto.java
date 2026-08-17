package com.deer.wcs.base.model;

/**
 * 站台扩展对象 pro_position_contentDto
 * 
 * @author deer
 * @date 2024-11-21
 */
public class ProPositionContentDto extends ProPositionContent
{
    private String name;
    private String wareCode;
    private String wareName;

    private Long invenState;
    private Long taskState;
    private Long disableState;
    private String itemTypeName;

    private String lineName;
    private String itemName;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getInvenState() {
        return invenState;
    }

    public void setInvenState(Long invenState) {
        this.invenState = invenState;
    }

    public Long getTaskState() {
        return taskState;
    }

    public void setTaskState(Long taskState) {
        this.taskState = taskState;
    }

    public Long getDisableState() {
        return disableState;
    }

    public void setDisableState(Long disableState) {
        this.disableState = disableState;
    }

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





}
