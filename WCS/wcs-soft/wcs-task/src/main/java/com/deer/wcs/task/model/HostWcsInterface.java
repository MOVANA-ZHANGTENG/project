package com.deer.wcs.task.model;


import com.deer.wcs.common.annotation.Excel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 接口记录对象 host_wcs_interface
 * 
 * @author deer
 * @date 2024-05-23
 */
@Data
@NoArgsConstructor
@ToString
public class HostWcsInterface
{
    private static final long serialVersionUID = 1L;

    public static final int SEND_STATUS_NOT_SEND = 0;
    public static final int SEND_STATUS_SEND = 1;
    public static final int SEND_RESULT_SEND_ERROR = 0;
    public static final int SEND_RESULT_SEND_SUCCESS = 1;
    public static final int TYPE_SEND = 0;
    public static final int TYPE_RECV = 1;
    public static final int HANDLE_RESULT_INIT = 0;
    public static final int HANDLE_RESULT_HANDLE = 1;
    public static final int HANDLE_RESULT_EXCEPTION = 2;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 功能码 */
    /*
        001  搬运任务  host->wcs
        002  作业取消  host->wcs state=0时取消
        003  作业取消  wcs->host
        004  托盘移动报告  wcs->Host
        005  目的地申请  wcs->host
        006  目的地变更
        007  搬运任务状态更新  wcs->host
        008  拣选/盘点任务完成通知  host->wcs
        009  库存同步  host->wcs

        hkAgv01 海康agv上报信息
        hkAgv02 创建任务
        hkAgv03 继续任务
        hkAgv04 取消任务
     */
    @Excel(name = "功能码")
    private String code;

    /** 接口名称 */
    @Excel(name = "接口名称")
    private String interfaceName;

    /** 发送方 */
    @Excel(name = "发送方")
    private String sendFrom;

    /** 接收方 */
    @Excel(name = "接收方")
    private String sendTo;

    /** 通讯时间 */
    @Excel(name = "通讯时间")
    private String startTime;

    /** 结束时间 */
    @Excel(name = "结束时间")
    private String endTime;

    /** 报文内容 */
    @Excel(name = "报文内容")
    private String content;

    /** 应答内容 */
    @Excel(name = "应答内容")
    private String recv;

    /** 发送次数 */
    @Excel(name = "发送次数")
    private Integer sendCount;

    /** 发送状态 */
    @Excel(name = "发送状态")
    private Integer sendStatus;

    /** 发送结果 */
    @Excel(name = "发送结果")
    private Integer sendResult;

    private String displayContent;

    private String displayRecv;

    private Integer type;

    private Integer handleResult;

    private String url;

    private Long jobId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getSendFrom() {
        return sendFrom;
    }

    public void setSendFrom(String sendFrom) {
        this.sendFrom = sendFrom;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecv() {
        return recv;
    }

    public void setRecv(String recv) {
        this.recv = recv;
    }

    public Integer getSendCount() {
        return sendCount;
    }

    public void setSendCount(Integer sendCount) {
        this.sendCount = sendCount;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Integer getSendResult() {
        return sendResult;
    }

    public void setSendResult(Integer sendResult) {
        this.sendResult = sendResult;
    }

    public String getDisplayContent() {
        return displayContent;
    }

    public void setDisplayContent(String displayContent) {
        this.displayContent = displayContent;
    }

    public String getDisplayRecv() {
        return displayRecv;
    }

    public void setDisplayRecv(String displayRecv) {
        this.displayRecv = displayRecv;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(Integer handleResult) {
        this.handleResult = handleResult;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
