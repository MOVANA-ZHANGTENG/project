package com.deer.wcs.base.model;

import java.util.List;

public class ContentReportData {
    private String lineCode;
    private String lineName;
    private String wareCode;
    private String wareName;

    private List<ContentReport> details;

    public List<ContentReport> getDetails() {
        return details;
    }

    public void setDetails(List<ContentReport> details) {
        this.details = details;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
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
