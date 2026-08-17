package com.deer.wcs.base.model;

import java.util.List;

/**
 * 流程对象 position_stepDto
 * 
 * @author deer
 * @date 2024-07-01
 */
public class PositionStepDto extends PositionStep
{
    private List<PositionHandle> cmdPreList;
    private List<PositionHandle> cmdList;
    private List<PositionHandle> successPreList;
    private List<PositionHandle> successList;
    private List<PositionHandle> deleteList;


    public List<PositionHandle> getDeleteList() {
        return deleteList;
    }

    public void setDeleteList(List<PositionHandle> deleteList) {
        this.deleteList = deleteList;
    }

    public List<PositionHandle> getCmdPreList() {
        return cmdPreList;
    }

    public void setCmdPreList(List<PositionHandle> cmdPreList) {
        this.cmdPreList = cmdPreList;
    }

    public List<PositionHandle> getCmdList() {
        return cmdList;
    }

    public void setCmdList(List<PositionHandle> cmdList) {
        this.cmdList = cmdList;
    }

    public List<PositionHandle> getSuccessPreList() {
        return successPreList;
    }

    public void setSuccessPreList(List<PositionHandle> successPreList) {
        this.successPreList = successPreList;
    }

    public List<PositionHandle> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<PositionHandle> successList) {
        this.successList = successList;
    }
}
