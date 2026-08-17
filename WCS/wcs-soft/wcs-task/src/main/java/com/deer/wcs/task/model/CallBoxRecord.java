package com.deer.wcs.task.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

/**
 * 呼叫盒记录对象 call_box_record
 * 
 * @author deer
 * @date 2024-12-02
 */
public class CallBoxRecord
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 呼叫盒ID */
    @Excel(name = "呼叫盒ID")
    private Long callBoxInfoId;

    /** 呼叫盒编码 */
    @Excel(name = "呼叫盒编码")
    private String code;


    /** 呼叫盒IP */
    @Excel(name = "呼叫盒IP")
    private String ip;

    /** 0-按钮按下  1-控制灯 */
    @Excel(name = "0-按钮按下  1-控制灯")
    private Integer type;

    /** 状态  0-初始化 1-已经处理 */
    @Excel(name = "状态  0-初始化 1-已经处理")
    private Integer state;

    /** 按钮index */
    @Excel(name = "按钮index")
    private Integer btn;

    /** 灯index */
    @Excel(name = "灯index")
    private String lampColor;

    /** 0 - 常量 1-闪烁 */
    @Excel(name = "0 - 常量 1-闪烁")
    private Integer lampMode;

    /** 亮灯持续时间 ms */
    @Excel(name = "亮灯持续时间 ms")
    private Long lampTime;

    /** 控制 0-灭灯 1-亮灯 */
    @Excel(name = "控制 0-灭灯 1-亮灯")
    private Integer lampCtrl;

    /** 备注 */
    @Excel(name = "备注")
    private String memo;

    private String createTime;

    private String updateTime;



    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCallBoxInfoId(Long callBoxInfoId) 
    {
        this.callBoxInfoId = callBoxInfoId;
    }

    public Long getCallBoxInfoId() 
    {
        return callBoxInfoId;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    public void setState(Integer state) 
    {
        this.state = state;
    }

    public Integer getState() 
    {
        return state;
    }
    public void setBtn(Integer btn) 
    {
        this.btn = btn;
    }

    public Integer getBtn() 
    {
        return btn;
    }
    public void setLampColor(String lampColor) 
    {
        this.lampColor = lampColor;
    }

    public String getLampColor() 
    {
        return lampColor;
    }
    public void setLampMode(Integer lampMode) 
    {
        this.lampMode = lampMode;
    }

    public Integer getLampMode() 
    {
        return lampMode;
    }
    public void setLampTime(Long lampTime) 
    {
        this.lampTime = lampTime;
    }

    public Long getLampTime() 
    {
        return lampTime;
    }
    public void setLampCtrl(Integer lampCtrl) 
    {
        this.lampCtrl = lampCtrl;
    }

    public Integer getLampCtrl() 
    {
        return lampCtrl;
    }
    public void setMemo(String memo) 
    {
        this.memo = memo;
    }

    public String getMemo() 
    {
        return memo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("callBoxInfoId", getCallBoxInfoId())
            .append("code", getCode())
            .append("ip", getIp())
            .append("type", getType())
            .append("state", getState())
            .append("btn", getBtn())
            .append("lampColor", getLampColor())
            .append("lampMode", getLampMode())
            .append("lampTime", getLampTime())
            .append("lampCtrl", getLampCtrl())
            .append("memo", getMemo())
            .toString();
    }
}
