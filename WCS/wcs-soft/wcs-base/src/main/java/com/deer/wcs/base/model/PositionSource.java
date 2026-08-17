package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Id;

/**
 * 站台资源对象 position_source
 *
 * @author deer
 * @date 2024-07-09
 */
public class PositionSource {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 名称
     */
    @Excel(name = "名称")
    private String name;

    /**
     * 图片路径
     */
    @Excel(name = "图片路径")
    private String imgUrl;

    private Integer delFlag;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("imgUrl", getImgUrl())
                .append("delFlag", getDelFlag())
                .toString();
    }
}
