package com.deer.wcs.common.enums;

/**
 * 操作状态
 * 
 * @author ruoyi
 *
 */
public enum CellTypeEnum {

    NORMAL(0, "普通库位"),
    IN_DOCK(1, "入库接驳位"),
    OUT_DOCK(2, "出库接驳位"),
    COMMON_DOCK(3, "通用接驳位"),
    SHUTTLE_PASSAGE(4, "四向车通道"),
    LIFTER_POS(5, "提升机位置"),
    CHARGER_POS(6, "充电桩位置");

    /** 编码，和前端value保持一致 */
    private final Integer code;
    /** 展示名称 */
    private final String name;

    CellTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据code获取枚举
     */
    public static CellTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CellTypeEnum value : CellTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据code获取名称
     */
    public static String getNameByCode(Integer code) {
        CellTypeEnum enumObj = getByCode(code);
        return enumObj != null ? enumObj.getName() : "";
    }
}
