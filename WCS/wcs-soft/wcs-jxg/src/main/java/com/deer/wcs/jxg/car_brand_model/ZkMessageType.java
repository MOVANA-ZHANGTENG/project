package com.deer.wcs.jxg.car_brand_model;

/**
 * 消息类型枚举
 * 定义智库设备支持的所有消息类型
 */
public class ZkMessageType {
    public static final String

        // 指令发送消息 wcs-car 发送指令给设备
        CMD_SEND_MSG = "InstructionRequestMsg",
        // 指令响应消息 car-wcs 接收设备指令响应
        CMD_RES_MSG = "InstructionResponseMsg",
        // 事件发送消息 car-wcs 上报事件给wcs
        EVENT_SEND_MSG = "EventRequestMsg",
        // 事件响应消息 wcs-car 发送设备事件响应
        EVENT_RES_MSG = "EventResponseMsg",
        // 心跳消息 wcs-car 发送心跳给car
        PING_MSG = "PingRequestMsg",
        // 状态消息 AGV实时上报状态数据给WCS
        STATE_MSG = "StateRequestMsg";



}

