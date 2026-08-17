package com.deer.wcs.base.model;

import lombok.Data;

@Data
public class BatchAddCell {
    //仓库编码
    private String wareCode;
    //区域编码
    private String areaCode;
    //巷道编码
    private String lineCode;
    //区域类型，用于区分巷道的两侧货架位置
    private String ab;
    //伸位：1最靠近巷道的货架 2 次靠近巷道的货架 3 ... 以此类推
    private Integer priority;
    //列数 ：货架有多少列
    private Integer x;
    //排数: 用于向堆垛机传输信息
    private Integer y;
    //层数: 货架有多少层
    private Integer z;

    private Integer xy;

    public String getWareCode() {
        return wareCode;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getAb() {
        return ab;
    }

    public void setAb(String ab) {
        this.ab = ab;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public Integer getXy() {
        return xy;
    }

    public void setXy(Integer xy) {
        this.xy = xy;
    }
}
