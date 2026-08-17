package com.deer.wcs.jxg.car_brand_model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

/**
 * 智库设备通信消息类
 * 统一处理请求和响应消息，使用内部类组织代码
 *
 * @author deer
 * @date 2025-01-20
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class ZkDeviceMessage {

    /** 消息类型 */
    @JsonProperty("msgType")
    private String msgType;



    /** 机器人ID */
    @JsonProperty("robotId")
    private Integer robotId;

    /** 请求内容（请求消息时使用） */
    @JsonProperty("request")
    private ZkRequest request;

    /** 响应内容（响应消息时使用） */
    @JsonProperty("response")
    private ZkResponse response;


    // 无参构造函数
    public ZkDeviceMessage() {
    }

    // 请求消息构造函数
    public ZkDeviceMessage(String  msgType, Integer robotId, ZkRequest request) {
        this.msgType = msgType;
        this.robotId = robotId;
        this.request = request;
        this.response = null;
    }

    // 响应消息构造函数
    public ZkDeviceMessage(String  msgType, Integer robotId, ZkResponse response) {
        this.msgType = msgType;
        this.robotId = robotId;
        this.request = null;
        this.response = response;
    }

    // Getter和Setter方法
    public String  getMsgType() {
        return msgType;
    }

    public void setMsgType(String  msgType) {
        this.msgType = msgType;
    }

    public Integer getRobotId() {
        return robotId;
    }

    public void setRobotId(Integer robotId) {
        this.robotId = robotId;
    }

    public ZkRequest getRequest() {
        return request;
    }

    public void setRequest(ZkRequest request) {
        this.request = request;
    }

    public ZkResponse getResponse() {
        return response;
    }

    public void setResponse(ZkResponse response) {
        this.response = response;
    }

    /**
     * 判断是否为请求消息
     */
    public boolean isRequest() {
        return request != null;
    }

    /**
     * 判断是否为响应消息
     */
    public boolean isResponse() {
        return response != null;
    }

    @Override
    public String toString() {
        return "ZkDeviceMessage{" +
                "msgType='" + msgType + '\'' +
                ", robotId=" + robotId +
                ", request=" + request +
                ", response=" + response +
                '}';
    }

    /**
     * 请求内容内部类
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class ZkRequest {

        /** 请求头 */
        @JsonProperty("header")
        private ZkRequestHeader header;

        /** 请求体 */
        @JsonProperty("body")
        private Object body;

        public ZkRequest() {
        }

        public ZkRequest(ZkRequestHeader header, Object body) {
            this.header = header;
            this.body = body;
        }

        public ZkRequestHeader getHeader() {
            return header;
        }

        public void setHeader(ZkRequestHeader header) {
            this.header = header;
        }

        public Object getBody() {
            return body;
        }

        public void setBody(Object body) {
            this.body = body;
        }

        @Override
        public String toString() {
            return "ZkRequest{" +
                    "header=" + header +
                    ", body=" + body +
                    '}';
        }
    }

    /**
     * 请求头内部类
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class ZkRequestHeader {

        /** 请求ID */
        @JsonProperty("requestId")
        private Long requestId;

        /** 版本号 */
        @JsonProperty("version")
        private String version;

        /** 扩展参数 */
        @JsonProperty("extParam")
        private String extParam;

        public ZkRequestHeader() {
        }

        public ZkRequestHeader(Long requestId, String version, String extParam) {
            this.requestId = requestId;
            this.version = version;
            this.extParam = extParam;
        }

        public Long getRequestId() {
            return requestId;
        }

        public void setRequestId(Long requestId) {
            this.requestId = requestId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getExtParam() {
            return extParam;
        }

        public void setExtParam(String extParam) {
            this.extParam = extParam;
        }

        @Override
        public String toString() {
            return "ZkRequestHeader{" +
                    "requestId='" + requestId + '\'' +
                    ", version='" + version + '\'' +
                    ", extParam='" + extParam + '\'' +
                    '}';
        }
    }


    /**
     * 响应内容内部类
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class ZkResponse {

        /** 响应头 */
        @JsonProperty("header")
        private ZkResponseHeader header;

        /** 响应体 */
        @JsonProperty("body")
        private Object body;

        public ZkResponse() {
        }

        public ZkResponse(ZkResponseHeader header, Object body) {
            this.header = header;
            this.body = body;
        }

        public ZkResponseHeader getHeader() {
            return header;
        }

        public void setHeader(ZkResponseHeader header) {
            this.header = header;
        }

        public Object getBody() {
            return body;
        }

        public void setBody(Object body) {
            this.body = body;
        }

        @Override
        public String toString() {
            return "ZkResponse{" +
                    "header=" + header +
                    ", body=" + body +
                    '}';
        }
    }

    /**
     * 响应头内部类
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class ZkResponseHeader {

        /** 响应ID */
        @JsonProperty("responseId")
        private Long responseId;

        /** 响应码 */
        @JsonProperty("code")
        private Integer code;

        /** 响应消息 */
        @JsonProperty("msg")
        private String msg;

        /** 扩展参数 */
        @JsonProperty("extParam")
        private String extParam;

        /** 任务ID（小车可能会返回） */
        @JsonProperty("taskId")
        private Long taskId;

        public ZkResponseHeader() {
        }


        public static void main(String[] args) {

        }

        public ZkResponseHeader(Long responseId, Integer code, String msg, String extParam) {
            this.responseId = responseId;
            this.code = code;
            this.msg = msg;
            this.extParam = extParam;
        }

        public Long getResponseId() {
            return responseId;
        }

        public void setResponseId(Long responseId) {
            this.responseId = responseId;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getExtParam() {
            return extParam;
        }

        public void setExtParam(String extParam) {
            this.extParam = extParam;
        }

        public Long getTaskId() {
            return taskId;
        }

        public void setTaskId(Long taskId) {
            this.taskId = taskId;
        }

        @Override
        public String toString() {
            return "ZkResponseHeader{" +
                    "responseId='" + responseId + '\'' +
                    ", code=" + code +
                    ", msg='" + msg + '\'' +
                    ", extParam='" + extParam + '\'' +
                    ", taskId=" + taskId +
                    '}';
        }
    }

    /**
     * 响应体内部类
     * 使用Map存储动态的响应数据
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ZkResponseBody {

        /** 响应数据 */
        private Map<String, Object> data;

        public ZkResponseBody() {
            this.data = new HashMap<>();
        }

        public ZkResponseBody(Map<String, Object> data) {
            this.data = data != null ? data : new HashMap<>();
        }

        /**
         * 添加响应数据
         */
        public void put(String key, Object value) {
            data.put(key, value);
        }

        /**
         * 获取响应数据
         */
        public Object get(String key) {
            return data.get(key);
        }

        /**
         * 获取所有响应数据
         */
        public Map<String, Object> getData() {
            return data;
        }

        /**
         * 设置响应数据
         */
        public void setData(Map<String, Object> data) {
            this.data = data != null ? data : new HashMap<>();
        }

        @Override
        public String toString() {
            return "ZkResponseBody{" +
                    "data=" + data +
                    '}';
        }
    }

    /**
     * 工具方法内部类
     * 提供JSON序列化和反序列化功能
     */
    public static class ZkMessageUtil {

        private static final ObjectMapper objectMapper = new ObjectMapper();

        /**
         * 将对象转换为JSON字符串
         */
        public static String toJson(Object obj) {
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON序列化失败", e);
            }
        }

        /**
         * 将JSON字符串转换为ZkDeviceMessage对象
         */
        public static ZkDeviceMessage fromJson(String json) {
            try {
                return objectMapper.readValue(json, ZkDeviceMessage.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON反序列化失败", e);
            }
        }
        /**
         * 创建请求消息
         */
        public static ZkDeviceMessage createRequest(String  msgType, Integer robotId,
                                                    Long requestId, String version,
                                                    Map<String, Object> bodyData) {
            ZkRequestHeader header = new ZkRequestHeader(requestId, version, null);

            ZkRequest request = new ZkRequest(header, bodyData);
            return new ZkDeviceMessage(msgType, robotId, request);
        }

        /**
         * 创建响应消息
         */
        public static ZkDeviceMessage createResponse(String  msgType, Integer robotId,
                                                     Long responseId, Integer code,
                                                     String msg, Map<String, Object> bodyData) {
            ZkResponseHeader header = new ZkResponseHeader(responseId, code, msg, null);

            ZkResponse response = new ZkResponse(header, bodyData);
            return new ZkDeviceMessage(msgType, robotId, response);
        }
        /**
         * 创建成功响应消息
         */
        public static ZkDeviceMessage createSuccessResponse(String  msgType, Integer robotId,
                                                            Long responseId) {
            return createResponse(msgType, robotId, responseId, 0, "success", null);
        }

        /**
         * 创建错误响应消息
         */
        public static ZkDeviceMessage createErrorResponse(String  msgType, Integer robotId,
                                                          Long responseId, Integer errorCode,
                                                          String errorMsg) {
            return createResponse(msgType, robotId, responseId, errorCode, errorMsg, null);
        }

        /**
         * 创建任务请求消息
         */
        public static ZkDeviceMessage createTaskRequest(String  msgType, Integer robotId, Long requestId,
                                                        String taskId, String targetLocation,
                                                        Integer priority) {
            Map<String, Object> bodyData = new HashMap<>();
            bodyData.put("taskId", taskId);
            bodyData.put("targetLocation", targetLocation);
            bodyData.put("priority", priority);

            return createRequest(msgType, robotId, requestId, "2.0.0", bodyData);
        }

        /**
         * 创建任务响应消息
         */
        public static ZkDeviceMessage createTaskResponse(String  msgType,Integer robotId, Long responseId,
                                                         Integer code, String msg,
                                                         String executionTime, Integer estimatedDuration) {
            Map<String, Object> bodyData = new HashMap<>();
            bodyData.put("executionTime", executionTime);
            bodyData.put("estimatedDuration", estimatedDuration);

            return createResponse(msgType, robotId, responseId, code, msg, bodyData);
        }
    }
}