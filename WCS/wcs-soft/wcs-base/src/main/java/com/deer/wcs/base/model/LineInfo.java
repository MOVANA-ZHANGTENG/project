package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 巷道对象 line_info
 * 
 * @author deer
 * @date 2024-04-28
 */
public class LineInfo
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

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String wareName;

    /** 库区编码 */
    @Excel(name = "库区编码")
    private String areaCode;

    /** 库区名称 */
    @Excel(name = "库区名称")
    private String areaName;

    /** 库存状态 */
    @Excel(name = "库存状态")
    private Long invenState;

    /** 任务状态 */
    @Excel(name = "任务状态")
    private Long taskState;

    /** 禁用状态 */
    @Excel(name = "禁用状态")
    private Long disableState;

    /** 删除标志 */
    @Excel(name = "删除标志")
    private Integer isDelete;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    /** 创建时间 */
    private String createTime;

    /** 创建人 */
    @Excel(name = "创建人")
    private Long createUserId;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createUserName;

    /** 更新时间 */
    private String updateTime;

    /** 跟新人 */
    @Excel(name = "跟新人")
    private Long updateUserId;

    /** 跟新人 */
    @Excel(name = "跟新人")
    private String updateUserName;

    private Integer maxY;
    private Integer minY;

    private Integer maxZ;


    //设备编码
    private String deviceCode;



    private String modelData;

    private String startDirection;

    public String getModelData() {
        return modelData;
    }

    public void setModelData(String modelData) {
        this.modelData = modelData;
    }

    public String getStartDirection() {
        return startDirection;
    }

    public void setStartDirection(String startDirection) {
        this.startDirection = startDirection;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public Integer getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(Integer maxZ) {
        this.maxZ = maxZ;
    }

    public Integer getMaxY() {
        return maxY;
    }

    public void setMaxY(Integer maxY) {
        this.maxY = maxY;
    }

    public Integer getMinY() {
        return minY;
    }

    public void setMinY(Integer minY) {
        this.minY = minY;
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
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
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
    public void setAreaCode(String areaCode) 
    {
        this.areaCode = areaCode;
    }

    public String getAreaCode() 
    {
        return areaCode;
    }
    public void setAreaName(String areaName) 
    {
        this.areaName = areaName;
    }

    public String getAreaName() 
    {
        return areaName;
    }

    public void setIsDelete(Integer isDelete) 
    {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() 
    {
        return isDelete;
    }
    public void setVersion(Integer version) 
    {
        this.version = version;
    }

    public Integer getVersion() 
    {
        return version;
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

    public Long getInvenState() {
        return invenState;
    }

    public void setInvenState(Long invenState) {
        this.invenState = invenState;
    }

    public Long getTaskState() {
        return taskState;
    }

    public void setTaskState(Long taskState) {
        this.taskState = taskState;
    }

    public Long getDisableState() {
        return disableState;
    }

    public void setDisableState(Long disableState) {
        this.disableState = disableState;
    }
}
