package com.deer.wcs.base.model;

import java.util.List;

/**
 * 任务定义对象 task_defineDto
 * 
 * @author deer
 * @date 2024-04-28
 */
public class TaskDefineDto extends TaskDefine {
    private List<TaskHandle> cmdPreList;
    private List<TaskHandle> cmdList;
    private List<TaskHandle> successPreList;
    private List<TaskHandle> successList;
    private List<TaskHandle> deleteList;

     /**
     * 删除前处理
     */

    public List<TaskHandle> getDeleteList() {
        return deleteList;
    }

    public void setDeleteList(List<TaskHandle> deleteList) {
        this.deleteList = deleteList;
    }

    public List<TaskHandle> getCmdPreList() {
        return cmdPreList;
    }

    public void setCmdPreList(List<TaskHandle> cmdPreList) {
        this.cmdPreList = cmdPreList;
    }

    public List<TaskHandle> getCmdList() {
        return cmdList;
    }

    public void setCmdList(List<TaskHandle> cmdList) {
        this.cmdList = cmdList;
    }

    public List<TaskHandle> getSuccessPreList() {
        return successPreList;
    }

    public void setSuccessPreList(List<TaskHandle> successPreList) {
        this.successPreList = successPreList;
    }

    public List<TaskHandle> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<TaskHandle> successList) {
        this.successList = successList;
    }
}
