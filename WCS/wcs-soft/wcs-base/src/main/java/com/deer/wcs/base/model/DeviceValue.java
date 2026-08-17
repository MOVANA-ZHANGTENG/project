package com.deer.wcs.base.model;

import com.deer.wcs.common.annotation.Excel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 属性对象 device_value
 * 
 * @author deer
 * @date 2024-05-16
 */
@Data
@NoArgsConstructor
@ToString
public class DeviceValue
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Excel(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 属性编码 */
    @Excel(name = "属性编码")
    private String code;

    /** 属性名称 */
    @Excel(name = "属性名称")
    private String name;

    @Excel(name="方向")
    private Integer type;

    /** 设备ID */
    @Excel(name = "设备ID")
    private Long deviceId;

    /** 设备编码 */
    @Excel(name = "设备编码")
    private String deviceCode;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 通讯方式 */
    @Excel(name = "通讯方式")
    private Integer comType;

    /** s7类型 */
    @Excel(name = "s7类型")
    private Integer s7Type;

    /** 通讯地址 */
    @Excel(name = "通讯地址")
    private String ip;

    /** 通讯端口 */
    @Excel(name = "通讯端口")
    private Integer port;

    /** 属性地址 */
    @Excel(name = "属性地址")
    private String address;

    /** 专用于boolean属性 */
    @Excel(name = "位索引")
    private Integer bitIndex;

    /** 属性对应modbus类型
     * 0---输出线圈
     * 1---输入线圈
     * 2---输入寄存器
     * 3---保持寄存器
     * */
    @Excel(name = "modbus类型")
    private Integer modbusType;

    /** 属性对应的plc类型 */
    @Excel(name = "plc属性类型")
    private Integer plcType;

    /** 属性对应的java类型 */
    @Excel(name = "java属性类型")
    private Integer javaType;

    /** 属性长度 */
    @Excel(name = "属性长度")
    private Integer length;

    /** 读取属性 */
    @Excel(name = "读取属性")
    private String readValue;

    /** 读取时间 */
    @Excel(name = "读取时间")
    private String readTime;

    /** 写入属性 */
    @Excel(name = "写入属性")
    private String writeValue;

    /** 写入时间 */
    @Excel(name = "写入时间")
    private String writeTime;

    /** 创建时间 */
    @Excel(name = "创建时间")
    private String createTime;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    private Long createUserId;

    /** 创建人姓名 */
    @Excel(name = "创建人姓名")
    private String createUserName;

    /** 更新时间 */
    @Excel(name = "更新时间")
    private String updateTime;

    /** 更新人ID */
    @Excel(name = "更新人ID")
    private Long updateUserId;

    /** 更新人姓名 */
    @Excel(name = "更新人姓名")
    private String updateUserName;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    /** 删除标志 */
    @Excel(name = "删除标志")
    private Integer isDelete;

}
