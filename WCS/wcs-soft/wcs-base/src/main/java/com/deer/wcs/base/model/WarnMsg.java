package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 报警代码对象 warn_msg
 * 
 * @author deer
 * @date 2025-09-24
 */
public class WarnMsg
{
    private static final long serialVersionUID = 1L;

    public static final Integer WARN_MSG_TYPE_SC = 0;
    public static final Integer WARN_MSG_TYPE_CODE_SCAN = 1;
    public static final Integer WARN_MSG_TYPE_SSX = 3;
    public static final Integer WARN_MSG_TYPE_RGV = 4;


    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 报警编码 */
    @Excel(name = "报警编码")
    private String code;

    /** 设备类型 */
    @Excel(name = "设备类型",dictType = "device_type")
    private String type;

    /** 报警内容 */
    @Excel(name = "报警内容")
    private String warnMsg;

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
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setWarnMsg(String warnMsg) 
    {
        this.warnMsg = warnMsg;
    }

    public String getWarnMsg() 
    {
        return warnMsg;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("type", getType())
            .append("warnMsg", getWarnMsg())
            .toString();
    }
}
