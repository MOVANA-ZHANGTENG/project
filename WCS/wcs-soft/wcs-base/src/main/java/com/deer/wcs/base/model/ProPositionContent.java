package com.deer.wcs.base.model;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;

/**
 * 站台扩展对象 pro_position_content
 * 
 * @author deer
 * @date 2024-11-21
 */
public class ProPositionContent
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String code2;
    private String backCode;

    private String wareCode;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Long productId;


    /** 位置Id */
    @Excel(name = "位置Id")
    private Long positionId;


    /** 工序ID */
    @Excel(name = "工序ID")
    private Long proProcessId;

    /** 设备编码 */
    @Excel(name = "设备编码")
    private String deviceCode;

    /** 扫码点位 */
    @Excel(name = "扫码点位")
    private String deviceScanCodeAddress;

    /** 是否有点位 */
    @Excel(name = "是否有点位")
    private String devceGuangdianAddress;

    /** wifi模块IP */
    @Excel(name = "wifi模块IP")
    private String wifiModeIp;

    /** wifi模块PORT */
    @Excel(name = "wifi模块PORT")
    private String wifiModePort;
    private Integer wifiModeOffset;
    private Integer wifiModeIsConnect;

    /** 托盘编码 */
    @Excel(name = "托盘编码")
    private String palletCode;
    //托盘状态 0-空托盘 1-满托盘
    private Integer  palletState;
    private Integer type;
    private String itemCode;

    private String proLineCode;

    private String itemTypeCode;

    private Integer codeGroup;
    private Integer isOk;

    //托盘放置时间
    private String inTime;

    private String memo;
    private String materialName;
    private String materialId;
    private String last;

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getWareCode() {
        return wareCode;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public Integer getPalletState() {
        return palletState;
    }

    public void setPalletState(Integer palletState) {
        this.palletState = palletState;
    }

    public Integer getCodeGroup() {
        return codeGroup;
    }

    public void setCodeGroup(Integer codeGroup) {
        this.codeGroup = codeGroup;
    }

    public Integer getIsOk() {
        return isOk;
    }

    public void setIsOk(Integer isOk) {
        this.isOk = isOk;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getBackCode() {
        return backCode;
    }

    public void setBackCode(String backCode) {
        this.backCode = backCode;
    }

    public String getItemTypeCode() {
        return itemTypeCode;
    }

    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    public String getProLineCode() {
        return proLineCode;
    }

    public void setProLineCode(String proLineCode) {
        this.proLineCode = proLineCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }


    public String getCode2() {
        return code2;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }

    public Integer getWifiModeIsConnect() {
        return wifiModeIsConnect;
    }

    public void setWifiModeIsConnect(Integer wifiModeIsConnect) {
        this.wifiModeIsConnect = wifiModeIsConnect;
    }

    public Integer getWifiModeOffset() {
        return wifiModeOffset;
    }

    public void setWifiModeOffset(Integer wifiModeOffset) {
        this.wifiModeOffset = wifiModeOffset;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }
    public void setProProcessId(Long proProcessId) 
    {
        this.proProcessId = proProcessId;
    }

    public Long getProProcessId() 
    {
        return proProcessId;
    }
    public void setDeviceCode(String deviceCode) 
    {
        this.deviceCode = deviceCode;
    }

    public String getDeviceCode() 
    {
        return deviceCode;
    }
    public void setDeviceScanCodeAddress(String deviceScanCodeAddress) 
    {
        this.deviceScanCodeAddress = deviceScanCodeAddress;
    }

    public String getDeviceScanCodeAddress() 
    {
        return deviceScanCodeAddress;
    }
    public void setDevceGuangdianAddress(String devceGuangdianAddress) 
    {
        this.devceGuangdianAddress = devceGuangdianAddress;
    }

    public String getDevceGuangdianAddress() 
    {
        return devceGuangdianAddress;
    }
    public void setWifiModeIp(String wifiModeIp) 
    {
        this.wifiModeIp = wifiModeIp;
    }

    public String getWifiModeIp() 
    {
        return wifiModeIp;
    }
    public void setWifiModePort(String wifiModePort) 
    {
        this.wifiModePort = wifiModePort;
    }

    public String getWifiModePort() 
    {
        return wifiModePort;
    }
    public void setPalletCode(String palletCode) 
    {
        this.palletCode = palletCode;
    }

    public String getPalletCode() 
    {
        return palletCode;
    }


}
