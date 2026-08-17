package com.deer.wcs.task.handle.hik;

public class CallBoxData {
    private String msgType;
    //呼叫器在系统中定义编号
    private String deviceId;
    //呼叫器 IP
    private String deviceIp;
    //引脚号 0-7 按钮
    private String pinIndex;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getPinIndex() {
        return pinIndex;
    }

    public void setPinIndex(String pinIndex) {
        this.pinIndex = pinIndex;
    }
}
