package com.deer.wcs.base.model;

import java.util.List;

/**
 * @description:
 * @author:zfj
 * @date:2024/7/1 18:58
 */
public class PositionStepVo extends PositionStep{
    private List<PositionHandle> cmdPreList;
    private List<PositionHandle> cmdList;
    private List<PositionHandle> successPreList;
    private List<PositionHandle> successList;

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
