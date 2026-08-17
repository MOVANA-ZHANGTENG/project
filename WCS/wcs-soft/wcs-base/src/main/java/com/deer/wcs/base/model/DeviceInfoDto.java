package com.deer.wcs.base.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;

/**
 * 设备对象 device_infoDto
 * 
 * @author deer
 * @date 2024-05-16
 */
public class DeviceInfoDto extends DeviceInfo {

    // 地址偏移量--复制新增时，根据此偏移量进行计算
    private Integer offset;

    public Integer getOffset() {
        return offset;
    }

    public void Integer(Integer offset) {
        this.offset = offset;
    }
}
