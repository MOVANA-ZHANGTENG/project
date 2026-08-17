package com.deer.wcs.task.model;

/**
 * 扫描失败率对象 sao_ma_successDto
 * 
 * @author deer
 * @date 2025-10-15
 */
public class SaoMaSuccessDto extends SaoMaSuccess
{
    private String startDate;
    private String endDate;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
