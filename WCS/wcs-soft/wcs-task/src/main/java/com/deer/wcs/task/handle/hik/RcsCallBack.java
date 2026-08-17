package com.deer.wcs.task.handle.hik;

public class RcsCallBack {

    private String reqCode;

    private String reqTime;

    private String cooX;

    private String cooY;

    private String currentPositionCode;

    private String data;

    private String mapCode;

    private String mapDataCode;

    private String stgBinCode;

    private String method;

    private String podCode;

    private String podDir;

    private String robotCode;

    private String taskCode;

    private String wbCode;

    private String materialLot;

    private String materialType;

    private String ctnrCode;

    private String ctnrType;

    private String roadWayCode;

    private String seq;

    private String eqpCode;

    public String getReqCode() {
        return reqCode;
    }

    public void setReqCode(String reqCode) {
        this.reqCode = reqCode;
    }

    public String getReqTime() {
        return reqTime;
    }

    public void setReqTime(String reqTime) {
        this.reqTime = reqTime;
    }

    public String getCooX() {
        return cooX;
    }

    public void setCooX(String cooX) {
        this.cooX = cooX;
    }

    public String getCooY() {
        return cooY;
    }

    public void setCooY(String cooY) {
        this.cooY = cooY;
    }

    public String getCurrentPositionCode() {
        return currentPositionCode;
    }

    public void setCurrentPositionCode(String currentPositionCode) {
        this.currentPositionCode = currentPositionCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMapCode() {
        return mapCode;
    }

    public void setMapCode(String mapCode) {
        this.mapCode = mapCode;
    }

    public String getMapDataCode() {
        return mapDataCode;
    }

    public void setMapDataCode(String mapDataCode) {
        this.mapDataCode = mapDataCode;
    }

    public String getStgBinCode() {
        return stgBinCode;
    }

    public void setStgBinCode(String stgBinCode) {
        this.stgBinCode = stgBinCode;
    }

    /**
     * 方法名, 可使用任务类型做为方法
     * 名
     * 由 RCS-2000 任务模板配置后并告
     * 知上层系统
     * 默认使用方式:
     * start : 任务开始
     * outbin : 走出储位
     * end : 任务结束
     * cancel : 任务单取消
     * apply：CTU 料箱取放申请
     * @return
     */
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPodCode() {
        return podCode;
    }

    public void setPodCode(String podCode) {
        this.podCode = podCode;
    }

    public String getPodDir() {
        return podDir;
    }

    public void setPodDir(String podDir) {
        this.podDir = podDir;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getWbCode() {
        return wbCode;
    }

    public void setWbCode(String wbCode) {
        this.wbCode = wbCode;
    }

    public String getMaterialLot() {
        return materialLot;
    }

    public void setMaterialLot(String materialLot) {
        this.materialLot = materialLot;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getCtnrCode() {
        return ctnrCode;
    }

    public void setCtnrCode(String ctnrCode) {
        this.ctnrCode = ctnrCode;
    }

    public String getCtnrType() {
        return ctnrType;
    }

    public void setCtnrType(String ctnrType) {
        this.ctnrType = ctnrType;
    }

    public String getRoadWayCode() {
        return roadWayCode;
    }

    public void setRoadWayCode(String roadWayCode) {
        this.roadWayCode = roadWayCode;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getEqpCode() {
        return eqpCode;
    }

    public void setEqpCode(String eqpCode) {
        this.eqpCode = eqpCode;
    }
}
