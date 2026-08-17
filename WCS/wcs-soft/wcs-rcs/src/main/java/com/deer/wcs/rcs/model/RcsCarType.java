package com.deer.wcs.rcs.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

/**
 * 车型号对象 rcs_car_type
 * 
 * @author deer
 * @date 2025-10-14
 */
public class RcsCarType
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 最大行驶速度 */
    @Excel(name = "最大行驶速度")
    private Long maxSpeed;

    /** 载重能力 kg */
    @Excel(name = "载重能力 kg")
    private Long loadCapacity;

    /** 最小充电电量 */
    @Excel(name = "最小充电电量")
    private Integer minChargeLevel;

    /** 空闲时充电电量 */
    @Excel(name = "空闲时充电电量")
    private Integer freeChargeLevel;

    /** 创建时间 */
    private String createTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long createUserId;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createUserName;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long updateUserId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String updateUserName;

    /** 更新人 */
    private String updateTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setBrand(String brand) 
    {
        this.brand = brand;
    }

    public String getBrand() 
    {
        return brand;
    }
    public void setModel(String model) 
    {
        this.model = model;
    }

    public String getModel() 
    {
        return model;
    }
    public void setMaxSpeed(Long maxSpeed) 
    {
        this.maxSpeed = maxSpeed;
    }

    public Long getMaxSpeed() 
    {
        return maxSpeed;
    }
    public void setLoadCapacity(Long loadCapacity) 
    {
        this.loadCapacity = loadCapacity;
    }

    public Long getLoadCapacity() 
    {
        return loadCapacity;
    }
    public void setMinChargeLevel(Integer minChargeLevel) 
    {
        this.minChargeLevel = minChargeLevel;
    }

    public Integer getMinChargeLevel() 
    {
        return minChargeLevel;
    }
    public void setFreeChargeLevel(Integer freeChargeLevel) 
    {
        this.freeChargeLevel = freeChargeLevel;
    }

    public Integer getFreeChargeLevel() 
    {
        return freeChargeLevel;
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
    public void setUpdateTime(String updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getUpdateTime()
    {
        return updateTime;
    }


}
