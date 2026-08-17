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
 * 充电桩对象 rcs_charg_pile_info
 * 
 * @author deer
 * @date 2025-10-14
 */
public class RcsChargPileInfo
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 编码 */
    @Excel(name = "编码")
    private String code;

    /** 位置ID */
    @Excel(name = "位置ID")
    private Long cellId;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 使用的车子类型 */
    @Excel(name = "使用的车子类型")
    private Long rcsCarTypeId;

    /** 当前充电的车子 */
    @Excel(name = "当前充电的车子")
    private Long carId;

    /** 禁用状态 */
    @Excel(name = "禁用状态")
    private Long disableState;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
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
    public void setCellId(Long cellId) 
    {
        this.cellId = cellId;
    }

    public Long getCellId() 
    {
        return cellId;
    }
    public void setWareCode(String wareCode) 
    {
        this.wareCode = wareCode;
    }

    public String getWareCode() 
    {
        return wareCode;
    }
    public void setRcsCarTypeId(Long rcsCarTypeId) 
    {
        this.rcsCarTypeId = rcsCarTypeId;
    }

    public Long getRcsCarTypeId() 
    {
        return rcsCarTypeId;
    }
    public void setCarId(Long carId) 
    {
        this.carId = carId;
    }

    public Long getCarId() 
    {
        return carId;
    }
    public void setDisableState(Long disableState) 
    {
        this.disableState = disableState;
    }

    public Long getDisableState() 
    {
        return disableState;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("cellId", getCellId())
            .append("wareCode", getWareCode())
            .append("rcsCarTypeId", getRcsCarTypeId())
            .append("carId", getCarId())
            .append("disableState", getDisableState())
            .toString();
    }
}
