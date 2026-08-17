package com.deer.wcs.task.model.result;

/**
 * @description: 杭州海康机器人返回结果
 * @author:zfj
 * @date:2024/7/16 21:30
 */

/**
 * 参数名 是否必填 描述
 * reqCode 必填 请求编号返回，形成一一对应
 * code 必填 返回编号， “0”：成功， 1~N：失败
 * message 必填 “0”：成功 ,1~N：其他的详细描述
 * data 选填 返回的数据结
 */
public class HaiKangServiceResult {
    private String reqCode;
    private Integer code;
    private String message;
    private String data;

    public String getReqCode() {
        return reqCode;
    }

    public void setReqCode(String reqCode) {
        this.reqCode = reqCode;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
