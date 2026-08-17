package com.deer.wcs.base.model;

public class Node{
    private Integer key;
    private String code;
    private String text;
    private String name;
    private String category;




    //position
    private Long id;
    private String type;
    private Integer state;
    private String parentCode;
    private Integer isGroup;
    private Integer Group;
    private Integer isDelete;
    private String createTime;
    private Long createUserId;
    private String createUserName;
    private String updateTime;
    private Long updateUserId;
    private String updateUserName;
    private Integer version;




    //line
    private String wareCode;
    private String wareName;
    private String areaCode;
    private String areaName;
    private Integer invenState;
    private Integer taskState;
    private Integer disableState;


    public Integer getGroup() {
        return Group;
    }

    public void setGroup(Integer group) {
        Group = group;
    }

    public Integer getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Integer isGroup) {
        this.isGroup = isGroup;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getInvenState() {
        return invenState;
    }

    public void setInvenState(Integer invenState) {
        this.invenState = invenState;
    }

    public Integer getTaskState() {
        return taskState;
    }

    public void setTaskState(Integer taskState) {
        this.taskState = taskState;
    }

    public Integer getDisableState() {
        return disableState;
    }

    public void setDisableState(Integer disableState) {
        this.disableState = disableState;
    }
}
