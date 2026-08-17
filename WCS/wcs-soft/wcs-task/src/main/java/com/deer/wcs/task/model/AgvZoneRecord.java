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
 * 交管日志对象 agv_zone_record
 * 
 * @author deer
 * @date 2024-11-26
 */
public class AgvZoneRecord
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 仓库名 */
    @Excel(name = "仓库名")
    private String wareName;

    /** 交管区编码 */
    @Excel(name = "交管区编码")
    private String code;
    /** uuid */
    @Excel(name = "uuid")
    private String uuid;

    /** AGV厂家 */
    @Excel(name = "AGV厂家")
    private String agvType;

    /** 事件类型 
0-请求进入
1-进入
2-离开 */
    @Excel(name = "事件类型  ")
    private Integer eventType;

    /** 状态
0-初始化
1-已处理 */
    @Excel(name = "状态 ")
    private Integer state;

    /** 时间 */
    private String createTime;

    /** 备注 */
    @Excel(name = "备注")
    private String memo;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setWareCode(String wareCode) 
    {
        this.wareCode = wareCode;
    }

    public String getWareCode() 
    {
        return wareCode;
    }
    public void setWareName(String wareName) 
    {
        this.wareName = wareName;
    }

    public String getWareName() 
    {
        return wareName;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setAgvType(String agvType) 
    {
        this.agvType = agvType;
    }

    public String getAgvType() 
    {
        return agvType;
    }
    public void setEventType(Integer eventType) 
    {
        this.eventType = eventType;
    }

    public Integer getEventType() 
    {
        return eventType;
    }
    public void setState(Integer state) 
    {
        this.state = state;
    }

    public Integer getState() 
    {
        return state;
    }
    public void setCreateTime(String createTime) 
    {
        this.createTime = createTime;
    }

    public String getCreateTime() 
    {
        return createTime;
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
            .append("wareCode", getWareCode())
            .append("wareName", getWareName())
            .append("code", getCode())
            .append("agvType", getAgvType())
            .append("eventType", getEventType())
            .append("state", getState())
            .append("createTime", getCreateTime())
            .append("memo", getMemo())
            .toString();
    }
}
