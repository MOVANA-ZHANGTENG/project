package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;

/**
 * 站台对象 position_info
 * 
 * @author deer
 * @date 2024-04-28
 */
public class PositionInfo
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 编码 */
    @Excel(name = "编码")
    private String code;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 名称 ---------------------------*/
    @Excel(name = "是否为组")
    private Integer isGroup;


    /** 名称 -----------------------------------*/
    @Excel(name = "父级code")
    private String parentCode;

    /** 仓库编码 */
    @Excel(name = "编码")
    private String wareCode;

    /** 仓库名称 */
    @Excel(name = "名称")
    private String wareName;

    /** 下位站台编码 */
    @Excel(name = "下位站台编码")
    private String subCode;

    /** 线体编码 */
    @Excel(name = "巷道编码")
    private String lineCode;



    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 状态 */
    @Excel(name = "状态")
    private Integer state;

    /** 删除标志 */
    @Excel(name = "删除标志")
    private Integer isDelete;

    /** 创建时间 */
    private String createTime;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    private Long createUserId;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createUserName;

    /** 更新时间 */
    private String updateTime;

    /** 更新人 */
    @Excel(name = "更新人")
    private Long updateUserId;

    /** 更新人 */
    @Excel(name = "更新人")
    private String updateUserName;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    private Long invenState;
    private Long taskState;
    private Long disableState;

    private String memo;

    private String plcIp;

    private Double address1;

    private Double address2;

    private Double address3;

    public String getPlcIp() {
        return plcIp;
    }

    public void setPlcIp(String plcIp) {
        this.plcIp = plcIp;
    }

    public Double getAddress1() {
        return address1;
    }

    public void setAddress1(Double address1) {
        this.address1 = address1;
    }

    public Double getAddress2() {
        return address2;
    }

    public void setAddress2(Double address2) {
        this.address2 = address2;
    }

    public Double getAddress3() {
        return address3;
    }

    public void setAddress3(Double address3) {
        this.address3 = address3;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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



    public Integer getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Integer isGroup) {
        this.isGroup = isGroup;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getWareCode() {
        return wareCode;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public String getWareName() {
        return wareName;
    }

    public void setWareName(String wareName) {
        this.wareName = wareName;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
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
    public void setState(Integer state) 
    {
        this.state = state;
    }

    public Integer getState() 
    {
        return state;
    }
    public void setIsDelete(Integer isDelete) 
    {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() 
    {
        return isDelete;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("type", getType())
            .append("subCode", getSubCode())
            .append("state", getState())
            .append("isDelete", getIsDelete())
            .append("createTime", getCreateTime())
            .append("createUserId", getCreateUserId())
            .append("createUserName", getCreateUserName())
            .append("updateTime", getUpdateTime())
            .append("updateUserId", getUpdateUserId())
            .append("updateUserName", getUpdateUserName())
            .append("version", getVersion())
            .toString();
    }

    /**
     * 设备对象 device_info
     *
     * @author deer
     * @date 2024-04-28
     */
    public static class DeviceInfo
    {
        private static final long serialVersionUID = 1L;

        /** ID */
        @Id
        //@GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        /** 编码 */
        @Excel(name = "编码")
        private String code;

        /** 名称 */
        @Excel(name = "名称")
        private String name;

        /** 类型 0-堆垛机 1-扫码器  */
        @Excel(name = "类型 0-堆垛机 1-扫码器 ")
        private Integer type;

        /** 通讯方式0-s7 1-plc tyc 2-mtqq 3-opc da  4-opc ua 5-mc */
        @Excel(name = "通讯方式0-s7 1-plc tyc 2-mtqq 3-opc da  4-opc ua 5-mc")
        private Integer comType;

        /** 是否在线  0-否 1-是 */
        @Excel(name = "是否在线  0-否 1-是")
        private Integer isOnline;

        /** 状态0-空闲 1-运行中 2-故障 */
        @Excel(name = "状态0-空闲 1-运行中 2-故障")
        private Integer state;

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
        public void setType(Integer type)
        {
            this.type = type;
        }

        public Integer getType()
        {
            return type;
        }
        public void setComType(Integer comType)
        {
            this.comType = comType;
        }

        public Integer getComType()
        {
            return comType;
        }
        public void setIsOnline(Integer isOnline)
        {
            this.isOnline = isOnline;
        }

        public Integer getIsOnline()
        {
            return isOnline;
        }
        public void setState(Integer state)
        {
            this.state = state;
        }

        public Integer getState()
        {
            return state;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("code", getCode())
                .append("name", getName())
                .append("type", getType())
                .append("comType", getComType())
                .append("isOnline", getIsOnline())
                .append("state", getState())
                .toString();
        }
    }

    /**
     * 设备对象 device_infoCriteria
     *
     * @author deer
     * @date 2024-04-28
     */
    public static class DeviceInfoCriteria extends DeviceInfo
    {
         /** 请求参数 */
        @Transient
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Map<String, Object> params;

        public Map<String, Object> getParams() {
                if(this.params==null){
                    this.params = new HashMap<>();
                }
                return this.params;
            }

            public void setParams(Map<String, Object> params) {
                if(params==null){
                    params = new HashMap<>();
                }
                this.params = params;
            }

    }

    /**
     * 设备对象 device_infoDto
     *
     * @author deer
     * @date 2024-04-28
     */
    public static class DeviceInfoDto extends DeviceInfo
    {

    }
}
