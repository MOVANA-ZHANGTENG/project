package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 物料对象 item_info
 *
 * @author deer
 * @date 2024-11-21
 */
public class ItemInfo {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 物料编码
     */
    @Excel(name = "物料编码")
    private String itemCode;

    /**
     * 物料名称
     */
    @Excel(name = "物料名称")
    private String itemName;

    /**
     * 规格
     */
    @Excel(name = "规格")
    private String spec;

    /**
     * 型号
     */
    @Excel(name = "型号")
    private String model;

    /**
     * 上架策略
     */
    @Excel(name = "上架策略")
    private String inCellTactics;

    /**
     * a-频率高  b-中等 c-频率低
     */
    @Excel(name = "a-频率高  b-中等 c-频率低")
    private String abc;

    private String itemTypeCode;


    public String getItemTypeCode() {
        return itemTypeCode;
    }

    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getSpec() {
        return spec;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setInCellTactics(String inCellTactics) {
        this.inCellTactics = inCellTactics;
    }

    public String getInCellTactics() {
        return inCellTactics;
    }

    public void setAbc(String abc) {
        this.abc = abc;
    }

    public String getAbc() {
        return abc;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("itemCode", getItemCode())
                .append("itemName", getItemName())
                .append("spec", getSpec())
                .append("model", getModel())
                .append("inCellTactics", getInCellTactics())
                .append("abc", getAbc())
                .toString();
    }
}
