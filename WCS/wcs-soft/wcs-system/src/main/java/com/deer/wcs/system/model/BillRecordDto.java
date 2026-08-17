package com.deer.wcs.system.model;

/**
 * 单据记录对象 bill_recordDto
 * 
 * @author deer
 * @date 2023-10-13
 */
public class BillRecordDto extends BillRecord
{
    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
