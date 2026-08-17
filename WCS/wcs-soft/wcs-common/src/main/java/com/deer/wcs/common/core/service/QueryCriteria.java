package com.deer.wcs.common.core.service;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 查询条件
 * <p>
 * Created by guo on 2017/9/30.
 */
public abstract class QueryCriteria {

    private Integer wareId;
    private Integer userId;

    private Integer organizationId;

    private String startTime;

    private String endTime;

    private String keyWords;

    private List<String> keyWordList;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "QueryCriteria{" +
                "wareId=" + wareId +
                ", organizationId=" + organizationId +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", keyWords='" + keyWords + '\'' +
                ", keyWordList=" + keyWordList +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }

    public String getKeyWords() {
        return keyWords;
    }

    // 字符串分割加入 keyWordList 参数
    public void setKeyWords(String keyWords) {
        keyWords = StringUtils.trim(keyWords);
        this.keyWords = keyWords;

        if (keyWords != "" && keyWords != null) {
            this.keyWordList = Arrays.asList(keyWords.split("\\s+"));
        }

    }


    public List<String> getKeyWordList() {
        return keyWordList;
    }

    public void setKeyWordList(List<String> keyWordList) {
        this.keyWordList = keyWordList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getWareId() {
        return wareId;
    }

    public void setWareId(Integer wareId) {
        this.wareId = wareId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页显示的条数
     */
    private Integer pageSize = 199999;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
