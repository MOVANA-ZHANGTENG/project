package com.deer.wcs.base.model;

public class PositionData {
    private Boolean hasPallet;
    private Boolean toApply;
    private Boolean palletCode;

    private Boolean canQu;
    private Boolean canFang;

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getHasPallet() {
        return hasPallet;
    }

    public void setHasPallet(Boolean hasPallet) {
        this.hasPallet = hasPallet;
    }

    public Boolean getToApply() {
        return toApply;
    }

    public void setToApply(Boolean toApply) {
        this.toApply = toApply;
    }

    public Boolean getPalletCode() {
        return palletCode;
    }

    public void setPalletCode(Boolean palletCode) {
        this.palletCode = palletCode;
    }

    public Boolean getCanQu() {
        return canQu;
    }

    public void setCanQu(Boolean canQu) {
        this.canQu = canQu;
    }

    public Boolean getCanFang() {
        return canFang;
    }

    public void setCanFang(Boolean canFang) {
        this.canFang = canFang;
    }
}
