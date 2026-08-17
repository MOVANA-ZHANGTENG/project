package com.deer.wcs.task.model.CodeScanner;

import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 扫码器对象 smq_info
 *
 * @author deer
 * @date 2024-07-31
 */
public class CodeScannerInfo
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    /** 扫描值 */
    @Excel(name = "扫描值")
    private String value;

    private String ip;
    private Integer port;
    private String address;
    private String wareCode;
    private String wareName;
    private String positionCode;

    /** 执行器 */
    @Excel(name = "执行器")
    private Integer handId;

    /** 事件状态 */
    @Excel(name = "事件状态")
    private Integer state;

    /** 是否下一步 */
    @Excel(name = "是否下一步")
    private Integer isNext;

    private String createTime;

    private String updateTime;
    private Integer delFlag;

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWareCode() {
        return wareCode;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public String getWareName() {
        return wareName;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public void setWareName(String wareName) {
        this.wareName = wareName;
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

    public String getName()
    {
        return name;
    }
    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
    public void setHandId(Integer handId)
    {
        this.handId = handId;
    }

    public Integer getHandId()
    {
        return handId;
    }
    public void setState(Integer state)
    {
        this.state = state;
    }

    public Integer getState()
    {
        return state;
    }
    public void setIsNext(Integer isNext)
    {
        this.isNext = isNext;
    }

    public Integer getIsNext()
    {
        return isNext;
    }
    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }

    public String getCreateTime()
    {
        return createTime;
    }
    public void setUpdateTime(String updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getUpdateTime()
    {
        return updateTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("code", getCode())
                .append("name", getName())
                .append("value", getValue())
                .append("wareCode", getWareCode())
                .append("wareName", getWareName())
                .append("positionCode", getPositionCode())
                .append("handId", getHandId())
                .append("state", getState())
                .append("isNext", getIsNext())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
