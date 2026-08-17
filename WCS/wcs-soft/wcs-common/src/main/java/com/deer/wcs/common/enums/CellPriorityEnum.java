package com.deer.wcs.common.enums;

/**
 * 操作状态
 * 
 * @author ruoyi
 *
 */
public enum CellPriorityEnum {

    FIRST_PRIORITY(1, "第1优先级"),
    SECOND_PRIORITY(2, "第2优先级"),
    THIRD_PRIORITY(3, "第3优先级"),
    FOURTH_PRIORITY(4, "第4优先级"),
    FIFTH_PRIORITY(5, "第5优先级"),
    SIXTH_PRIORITY(6, "第6优先级");

    /** 编码，和前端value保持一致 */
    private final Integer code;
    /** 展示名称 */
    private final String name;

    CellPriorityEnum(Integer code, String name) {
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
    public static CellPriorityEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CellPriorityEnum value : CellPriorityEnum.values()) {
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
        CellPriorityEnum enumObj = getByCode(code);
        return enumObj != null ? enumObj.getName() : "";
    }
}
