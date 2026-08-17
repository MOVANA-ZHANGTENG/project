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
 * 策略配置对象 in_cell_tacics
 * 
 * @author deer
 * @date 2024-09-09
 */
public class InCellTacics
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Excel(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 策略编码 */
    @Excel(name = "策略编码")
    private String code;

    /** 策略名称 */
    @Excel(name = "策略名称")
    private String name;

    /** 是否匹配abc */
    @Excel(name = "是否匹配abc")
    private String isAbc;

    /** 巷道策略 */
    @Excel(name = "巷道策略")
    private Integer lineBalanceType;

    /** X向策略 */
    @Excel(name = "X向策略")
    private String xType;

    /** Y向策略 */
    @Excel(name = "Y向策略")
    private String yType;

    /** Z向策略 */
    @Excel(name = "Z向策略")
    private String zType;

    /** 是否默认 */
    @Excel(name = "是否默认")
    private Integer isSys;

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
    public void setIsAbc(String isAbc) 
    {
        this.isAbc = isAbc;
    }

    public String getIsAbc() 
    {
        return isAbc;
    }
    public void setLineBalanceType(Integer lineBalanceType) 
    {
        this.lineBalanceType = lineBalanceType;
    }

    public Integer getLineBalanceType() 
    {
        return lineBalanceType;
    }
    public void setxType(String xType) 
    {
        this.xType = xType;
    }

    public String getxType() 
    {
        return xType;
    }
    public void setyType(String yType) 
    {
        this.yType = yType;
    }

    public String getyType() 
    {
        return yType;
    }
    public void setzType(String zType) 
    {
        this.zType = zType;
    }

    public String getzType() 
    {
        return zType;
    }
    public void setIsSys(Integer isSys) 
    {
        this.isSys = isSys;
    }

    public Integer getIsSys() 
    {
        return isSys;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("isAbc", getIsAbc())
            .append("lineBalanceType", getLineBalanceType())
            .append("xType", getxType())
            .append("yType", getyType())
            .append("zType", getzType())
            .append("isSys", getIsSys())
            .toString();
    }
}
