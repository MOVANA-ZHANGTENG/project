package com.deer.wcs.task.model;

/**
 * @description:
 * @author:zfj
 * @date:2024/9/5 10:12
 */
public class ThreeData {

    //
    //  sc_qu  sc_fang  agv  agv_shangliao  agv_xialiao
    private String type;
    private String fromNode;
    private String toNode;
    private Short scY;
    private Short scZ;
    private String ssxType;
    private String palletCode;

    public String getPalletCode() {
        return palletCode;
    }

    public void setPalletCode(String palletCode) {
        this.palletCode = palletCode;
    }

    public String getSsxType() {
        return ssxType;
    }

    public void setSsxType(String ssxType) {
        this.ssxType = ssxType;
    }

    public Short getScY() {
        return scY;
    }

    public void setScY(Short scY) {
        this.scY = scY;
    }

    public Short getScZ() {
        return scZ;
    }

    public void setScZ(Short scZ) {
        this.scZ = scZ;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromNode() {
        return fromNode;
    }

    public void setFromNode(String fromNode) {
        this.fromNode = fromNode;
    }

    public String getToNode() {
        return toNode;
    }

    public void setToNode(String toNode) {
        this.toNode = toNode;
    }
}
