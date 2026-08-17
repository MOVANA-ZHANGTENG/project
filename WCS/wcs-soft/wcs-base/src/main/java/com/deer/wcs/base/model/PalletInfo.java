package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 托盘信息对象 pallet_info
 * 
 * @author deer
 * @date 2024-05-29
 */
public class PalletInfo
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Excel(name = "ID",type = Excel.Type.EXPORT)
    private Long id;

    /** 托盘编码 */
    @Excel(name = "托盘编码")
    private String code;

    /** rfid编码 */
    @Excel(name = "rfid编码")
    private String rfidCode;

    /** 托盘名称 */
    @Excel(name = "托盘名称")
    private String name;

    /** 当前位置 */
    @Excel(name = "当前位置",type = Excel.Type.EXPORT)
    private String cellCode;
    private String wareCode;

    /** 类型编码 */
    @Excel(name = "类型编码",type = Excel.Type.EXPORT)
    private String typeCode;

    /** 类型名称 */
    @Excel(name = "类型名称",type = Excel.Type.EXPORT)
    private String typeName;

    /** 托盘状态 */
    @Excel(name = "托盘状态",type = Excel.Type.EXPORT,dictType = "is_empty")
    private String isEmpty;

    /** 父级托盘 */
    @Excel(name = "父级托盘",type = Excel.Type.EXPORT)
    private String parentCode;

    /** 托盘承重 */
    @Excel(name = "托盘承重",type = Excel.Type.EXPORT)
    private Double realWeight;

    private String state;

    //准备要去的目的地，但是还没给PLC发送目的地
    private String toCode;

    //什么时候有了 toCode 值的
    private String toCodeCreateTime;

    //什么时候给PLC发的
    private String toCodeSendTime;


    ////已经发了目的地，但是还没到的
    private String toCode2;

    // 托盘方向（用途） IN（入库）/OUT（出库）/ROLL（流转）
    private String direction;


    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getToCodeSendTime() {
        return toCodeSendTime;
    }

    public void setToCodeSendTime(String toCodeSendTime) {
        this.toCodeSendTime = toCodeSendTime;
    }

    public String getToCodeCreateTime() {
        return toCodeCreateTime;
    }

    public void setToCodeCreateTime(String toCodeCreateTime) {
        this.toCodeCreateTime = toCodeCreateTime;
    }

    public String getToCode2() {
        return toCode2;
    }

    public void setToCode2(String toCode2) {
        this.toCode2 = toCode2;
    }

    public String getWareCode() {
        return wareCode;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public String getToCode() {
        return toCode;
    }

    public void setToCode(String toCode) {
        this.toCode = toCode;
    }

    public String getRfidCode() {
        return rfidCode;
    }

    public void setRfidCode(String rfidCode) {
        this.rfidCode = rfidCode;
    }

    public Double getRealWeight() {
        return realWeight;
    }

    public void setRealWeight(Double realWeight) {
        this.realWeight = realWeight;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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
    public void setCellCode(String cellCode) 
    {
        this.cellCode = cellCode;
    }

    public String getCellCode() 
    {
        return cellCode;
    }
    public void setTypeCode(String typeCode) 
    {
        this.typeCode = typeCode;
    }

    public String getTypeCode() 
    {
        return typeCode;
    }
    public void setTypeName(String typeName) 
    {
        this.typeName = typeName;
    }

    public String getTypeName() 
    {
        return typeName;
    }
    public void setIsEmpty(String isEmpty) 
    {
        this.isEmpty = isEmpty;
    }

    public String getIsEmpty() 
    {
        return isEmpty;
    }
    public void setParentCode(String parentCode) 
    {
        this.parentCode = parentCode;
    }

    public String getParentCode() 
    {
        return parentCode;
    }


}
