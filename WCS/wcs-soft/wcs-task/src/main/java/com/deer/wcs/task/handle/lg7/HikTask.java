package com.deer.wcs.task.handle.lg7;

public class HikTask {

    //"taskCode": "234",
    //"taskStatus": "2",
    //"agvCode": "",
    //"taskTyp": "F01"

    private String taskCode;
    private String taskStatus;
    private String agvCode;
    private String taskType;

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getAgvCode() {
        return agvCode;
    }

    public void setAgvCode(String agvCode) {
        this.agvCode = agvCode;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
}
