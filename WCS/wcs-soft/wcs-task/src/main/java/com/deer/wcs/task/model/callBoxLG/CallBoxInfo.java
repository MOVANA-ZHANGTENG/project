package com.deer.wcs.task.model.callBoxLG;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 呼叫盒对象 call_box_info
 * 
 * @author deer
 * @date 2024-07-30
 */
public class CallBoxInfo
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String code;


    private String name;

    /** 按钮 0-无 */
    @Excel(name = "按钮 0-无")
    private String btn;

    /** 按钮是否被按下 1-按下 0-无 */
    @Excel(name = "按钮是否被按下 1-按下 0-无")
    private Integer btnState;

    /** 事件处理状态 */
    @Excel(name = "事件处理状态")
    private Integer state;

    /** 执行器 */
    @Excel(name = "执行器")
    private Long handId;

    /** 位置编码 */
    @Excel(name = "位置编码")
    private String positionCode;
    private String ip;
    private String wareCode;
    private String wareName;
    private String createTime;
    private String updateTime;
    private Integer delFlag;
    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getWareCode() {
        return wareCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public String getWareName() {
        return wareName;
    }

    public void setWareName(String wareName) {
        this.wareName = wareName;
    }

    public String getName()
    {
        return name;
    }
    public void setBtn(String btn) 
    {
        this.btn = btn;
    }

    public String getBtn() 
    {
        return btn;
    }
    public void setBtnState(Integer btnState) 
    {
        this.btnState = btnState;
    }

    public Integer getBtnState() 
    {
        return btnState;
    }
    public void setState(Integer state) 
    {
        this.state = state;
    }

    public Integer getState() 
    {
        return state;
    }
    public void setHandId(Long handId)
    {
        this.handId = handId;
    }

    public Long getHandId()
    {
        return handId;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("btn", getBtn())
            .append("ip", getIp())
            .append("btnState", getBtnState())
            .append("state", getState())
            .append("handId", getHandId())
            .append("positionCode", getPositionCode())
            .append("wareCode", getWareCode())
            .append("wareName", getWareName())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
