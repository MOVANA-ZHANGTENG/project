package com.deer.wcs.task.handle.hik;

public class NotifyTaskInfo {
    private String actionTask;
    private String deviceIndex;
    private String deviceType;
    private String type;
    private String uuid;
    private String direction;

    public String getActionTask() {
        return actionTask;
    }

    public void setActionTask(String actionTask) {
        this.actionTask = actionTask;
    }

    public String getDeviceIndex() {
        return deviceIndex;
    }

    public void setDeviceIndex(String deviceIndex) {
        this.deviceIndex = deviceIndex;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
