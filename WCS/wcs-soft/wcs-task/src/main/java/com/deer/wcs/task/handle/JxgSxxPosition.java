package com.deer.wcs.task.handle;

import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.task.model.ThreeData;
import com.deer.wcs.task.websocket.WebSocketUsers;

public class JxgSxxPosition {
    public String code;
    public String hasPallet;
    public String palletQuantity;
    public String gdUp;
    public String gdDown;
    public String yzUp;
    public String yzDown;
    public String toNode;
    public String arrive;
    public String leave;
    public String action;
    public String palletCode;




    public JxgSxxPosition(String subCode) {
        Integer codeInt = Integer.parseInt(subCode);
        this.code = address(codeInt,0);
        this.hasPallet = address(codeInt,1);
        this.palletQuantity = address(codeInt,2);
        this.gdUp = address(codeInt,3);
        this.gdDown = address(codeInt,4);
        this.yzUp = address(codeInt,5);
        this.yzDown = address(codeInt,6);
        this.toNode = address(codeInt,7);
        this.arrive = address(codeInt,8);
        this.leave = address(codeInt,9);
        this.action = address(codeInt,10);
        this.palletCode = address(codeInt,50);
    }

    private static String address(Integer address,Integer index){
        Integer newAddress = address+index;
        return newAddress.toString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHasPallet() {
        return hasPallet;
    }

    public void setHasPallet(String hasPallet) {
        this.hasPallet = hasPallet;
    }

    public String getPalletQuantity() {
        return palletQuantity;
    }

    public void setPalletQuantity(String palletQuantity) {
        this.palletQuantity = palletQuantity;
    }

    public String getGdUp() {
        return gdUp;
    }

    public void setGdUp(String gdUp) {
        this.gdUp = gdUp;
    }

    public String getGdDown() {
        return gdDown;
    }

    public void setGdDown(String gdDown) {
        this.gdDown = gdDown;
    }

    public String getYzUp() {
        return yzUp;
    }

    public void setYzUp(String yzUp) {
        this.yzUp = yzUp;
    }

    public String getYzDown() {
        return yzDown;
    }

    public void setYzDown(String yzDown) {
        this.yzDown = yzDown;
    }

    public String getToNode() {
        return toNode;
    }

    public void setToNode(String toNode) {
        this.toNode = toNode;
    }

    public String getArrive() {
        return arrive;
    }

    public void setArrive(String arrive) {
        this.arrive = arrive;
    }

    public String getLeave() {
        return leave;
    }

    public void setLeave(String leave) {
        this.leave = leave;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPalletCode() {
        return palletCode;
    }

    public void setPalletCode(String palletCode) {
        this.palletCode = palletCode;
    }
}
