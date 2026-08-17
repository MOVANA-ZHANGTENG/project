package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * 仓库对象 ware_info
 *
 * @author deer
 * @date 2025-07-22
 */
public class WareInfo
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 编码 */
    @Excel(name = "编码")
    private String code;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 逻辑json数据 */
    @Excel(name = "逻辑json数据")
    private String modelData;

    /** 监控json数据 */
    @Excel(name = "监控json数据")
    private String monitorData;

    /** 创建时间 */
    private String createTime;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    private Long createUserId;

    /** 创建人名字 */
    @Excel(name = "创建人名字")
    private String createUserName;

    /** 更新时间 */
    private String updateTime;

    /** 更新人ID */
    @Excel(name = "更新人ID")
    private Long updateUserId;

    /** 更新人名字 */
    @Excel(name = "更新人名字")
    private String updateUserName;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    /** 删除标志 0-正常 1-删除 */
    @Excel(name = "删除标志 0-正常 1-删除")
    private Integer isDelete;

    /** 测试模式 0-关闭  1-打开 */
    @Excel(name = "测试模式 0-关闭  1-打开")
    private String isTest;

    /** 是否禁用 0-启用  1-禁用 */
    @Excel(name = "是否禁用 0-启用  1-禁用")
    private Integer disableState;

    /** 上架策略 */
    @Excel(name = "上架策略")
    private String inCellTactics;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String backgroundImg;

    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
    public void setModelData(String modelData)
    {
        this.modelData = modelData;
    }

    public String getModelData()
    {
        return modelData;
    }
    public void setMonitorData(String monitorData)
    {
        this.monitorData = monitorData;
    }

    public String getMonitorData()
    {
        return monitorData;
    }
    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }

    public String getCreateTime()
    {
        return createTime;
    }
    public void setCreateUserId(Long createUserId)
    {
        this.createUserId = createUserId;
    }

    public Long getCreateUserId()
    {
        return createUserId;
    }
    public void setCreateUserName(String createUserName)
    {
        this.createUserName = createUserName;
    }

    public String getCreateUserName()
    {
        return createUserName;
    }
    public void setUpdateTime(String updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getUpdateTime()
    {
        return updateTime;
    }
    public void setUpdateUserId(Long updateUserId)
    {
        this.updateUserId = updateUserId;
    }

    public Long getUpdateUserId()
    {
        return updateUserId;
    }
    public void setUpdateUserName(String updateUserName)
    {
        this.updateUserName = updateUserName;
    }

    public String getUpdateUserName()
    {
        return updateUserName;
    }
    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public Integer getVersion()
    {
        return version;
    }
    public void setIsDelete(Integer isDelete)
    {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete()
    {
        return isDelete;
    }
    public void setIsTest(String isTest)
    {
        this.isTest = isTest;
    }

    public String getIsTest()
    {
        return isTest;
    }
    public void setDisableState(Integer disableState)
    {
        this.disableState = disableState;
    }

    public Integer getDisableState()
    {
        return disableState;
    }
    public void setInCellTactics(String inCellTactics)
    {
        this.inCellTactics = inCellTactics;
    }

    public String getInCellTactics()
    {
        return inCellTactics;
    }
    public void setBackgroundImg(String backgroundImg)
    {
        this.backgroundImg = backgroundImg;
    }

    public String getBackgroundImg()
    {
        return backgroundImg;
    }


}
