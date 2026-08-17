package com.deer.wcs.base.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

/**
 * plc读取站台信号对象 plc_read_station
 * 
 * @author deer
 * @date 2025-06-04
 */
public class PlcReadStation
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 设备编码 */
    @Excel(name = "设备编码")
    private String deviceCodeName;
    private String deviceCodeId;
    private String deviceCodeLast;

    /** 位置编码 */
    @Excel(name = "位置编码")
    private String code;

    /** plc读取的原料名 */
    @Excel(name = "plc读取的原料名")
    private String name;

    /** plc读取的原料id */
    @Excel(name = "plc读取的原料id")
    private String materialId;

    /** plc读取的原料的余料 */
    @Excel(name = "plc读取的原料的余料")
    private String last;
    private Long disableState;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public String getDeviceCodeName() {
        return deviceCodeName;
    }

    public void setDeviceCodeName(String deviceCodeName) {
        this.deviceCodeName = deviceCodeName;
    }

    public String getDeviceCodeId() {
        return deviceCodeId;
    }

    public void setDeviceCodeId(String deviceCodeId) {
        this.deviceCodeId = deviceCodeId;
    }

    public String getDeviceCodeLast() {
        return deviceCodeLast;
    }

    public void setDeviceCodeLast(String deviceCodeLast) {
        this.deviceCodeLast = deviceCodeLast;
    }

    public Long getDisableState() {
        return disableState;
    }

    public void setDisableState(Long disableState) {
        this.disableState = disableState;
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
    public void setMaterialId(String materialId) 
    {
        this.materialId = materialId;
    }

    public String getMaterialId() 
    {
        return materialId;
    }
    public void setLast(String last) 
    {
        this.last = last;
    }

    public String getLast() 
    {
        return last;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceCodeName", getDeviceCodeName())
            .append("deviceCodeId", getDeviceCodeId())
            .append("deviceCodeLast", getDeviceCodeLast())
            .append("code", getCode())
            .append("name", getName())
            .append("materialId", getMaterialId())
            .append("last", getLast())
            .append("disableState", getDisableState())
            .toString();
    }
}
