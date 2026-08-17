package com.deer.wcs.system.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

/**
 * 接口调用对象 system_requst
 * 
 * @author deer
 * @date 2023-11-16
 */
public class SystemRequst
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    private Integer requestId;

    /** 调用方 */
    @Excel(name = "调用方")
    private String fromSystem;

    /** 被调用方 */
    @Excel(name = "被调用方")
    private String toSystem;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 接口名 */
    @Excel(name = "接口名")
    private String interfaceName;

    /** 参数 */
    @Excel(name = "参数")
    private String param;

    /** 返回值 */
    @Excel(name = "返回值")
    private String result;

    /** 状态 */
    @Excel(name = "状态")
    private Long state;

    /** 调用时间 */
    private String createTime;

    public void setRequestId(Integer requestId) 
    {
        this.requestId = requestId;
    }

    public Integer getRequestId() 
    {
        return requestId;
    }
    public void setFromSystem(String fromSystem) 
    {
        this.fromSystem = fromSystem;
    }

    public String getFromSystem() 
    {
        return fromSystem;
    }
    public void setToSystem(String toSystem) 
    {
        this.toSystem = toSystem;
    }

    public String getToSystem() 
    {
        return toSystem;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setInterfaceName(String interfaceName) 
    {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceName() 
    {
        return interfaceName;
    }
    public void setParam(String param) 
    {
        this.param = param;
    }

    public String getParam() 
    {
        return param;
    }
    public void setResult(String result) 
    {
        this.result = result;
    }

    public String getResult() 
    {
        return result;
    }
    public void setState(Long state) 
    {
        this.state = state;
    }

    public Long getState() 
    {
        return state;
    }
    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }

    public String getCreateTime()
    {
        return createTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("requestId", getRequestId())
            .append("fromSystem", getFromSystem())
            .append("toSystem", getToSystem())
            .append("type", getType())
            .append("interfaceName", getInterfaceName())
            .append("param", getParam())
            .append("result", getResult())
            .append("state", getState())
            .append("createTime", getCreateTime())
            .toString();
    }
}
