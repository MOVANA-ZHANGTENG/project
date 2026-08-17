package com.deer.wcs.base.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;

/**
 * 站台日志对象 position_recordDto
 * 
 * @author deer
 * @date 2025-04-02
 */
public class PositionRecordDto extends PositionRecord
{
    /** 站台编码 */
    private String positionCode;
    
    /** 仓库编码 */
    private String wareCode;
    
    /** 仓库名称 */
    private String wareName;

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
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
}
