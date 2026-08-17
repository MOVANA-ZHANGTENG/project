package com.deer.wcs.system.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Id;

/**
 * 当日自增长对象 auto
 * 
 * @author deer
 * @date 2023-10-23
 */
public class Auto
{
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    /** 1-入库单号  2-出库单号 3-批次号 */
    @Excel(name = "1-入库单号  2-出库单号 3-批次号")
    private Integer type;


    private String date;

    private Integer no;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    public void setDate(String date) 
    {
        this.date = date;
    }

    public String getDate() 
    {
        return date;
    }
    public void setNo(Integer no) 
    {
        this.no = no;
    }

    public Integer getNo() 
    {
        return no;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("type", getType())
            .append("date", getDate())
            .append("no", getNo())
            .toString();
    }
}
