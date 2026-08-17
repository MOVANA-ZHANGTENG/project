package com.deer.wcs.base.model;

import java.util.List;

public class Link {
    private String from;
    private String to;
    private String code;
    private String text;
    private String name;

    //positionCondition
    private Long id;
    private List<Long> handelIds;
    private String fromCode;
    private String toCode;
    private Integer taskTime;
    private Integer blockingTime;
    private String templateCode;

//    private List<PositionHandle> cmdPreList;
//    private List<PositionHandle> cmdList;
//    private List<PositionHandle> successPreList;
//    private List<PositionHandle> successList;

//    public List<PositionHandle> getCmdPreList() {
//        return cmdPreList;
//    }
//
//    public void setCmdPreList(List<PositionHandle> cmdPreList) {
//        this.cmdPreList = cmdPreList;
//    }
//
//    public List<PositionHandle> getCmdList() {
//        return cmdList;
//    }
//
//    public void setCmdList(List<PositionHandle> cmdList) {
//        this.cmdList = cmdList;
//    }
//
//    public List<PositionHandle> getSuccessPreList() {
//        return successPreList;
//    }
//
//    public void setSuccessPreList(List<PositionHandle> successPreList) {
//        this.successPreList = successPreList;
//    }
//
//    public List<PositionHandle> getSuccessList() {
//        return successList;
//    }
//
//    public void setSuccessList(List<PositionHandle> successList) {
//        this.successList = successList;
//    }


    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getHandelIds() {
        return handelIds;
    }

    public void setHandelIds(List<Long> handelIds) {
        this.handelIds = handelIds;
    }

    public String getFromCode() {
        return fromCode;
    }

    public void setFromCode(String fromCode) {
        this.fromCode = fromCode;
    }

    public String getToCode() {
        return toCode;
    }

    public void setToCode(String toCode) {
        this.toCode = toCode;
    }

    public Integer getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(Integer taskTime) {
        this.taskTime = taskTime;
    }

    public Integer getBlockingTime() {
        return blockingTime;
    }

    public void setBlockingTime(Integer blockingTime) {
        this.blockingTime = blockingTime;
    }
}
