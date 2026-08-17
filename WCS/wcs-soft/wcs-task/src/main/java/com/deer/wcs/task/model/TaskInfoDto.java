package com.deer.wcs.task.model;

/**
 * 任务对象 task_infoDto
 * 
 * @author deer
 * @date 2024-04-30
 */
public class TaskInfoDto extends TaskInfo
{

    private String taskTypeName;
    
    private String rcsCarName;


    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }

    public String getRcsCarName() {
        return rcsCarName;
    }

    public void setRcsCarName(String rcsCarName) {
        this.rcsCarName = rcsCarName;
    }
}
