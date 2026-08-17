package com.deer.wcs.task.handle.hik;

public class AgvResult {
    //返回编号， “0”：成功， 1~N：失败
    private String code;
    //0”：成功
    //1~N：其他的详细描述
    private String message;
    private String Success;
    // 请求编号返回，形成一一对应
    private String reqCode;
    //返回的数据结构
    private AGVRes data;

    public String getSuccess() {
        return Success;
    }

    public void setSuccess(String success) {
        Success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReqCode() {
        return reqCode;
    }

    public void setReqCode(String reqCode) {
        this.reqCode = reqCode;
    }

    public AGVRes getData() {
        return data;
    }

    public void setData(AGVRes data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AgvResult{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", reqCode='" + reqCode + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    class AGVRes{
        private String robotTaskCode;

        public String getRobotTaskCode() {
            return robotTaskCode;
        }

        public void setRobotTaskCode(String robotTaskCode) {
            this.robotTaskCode = robotTaskCode;
        }
    }
}
