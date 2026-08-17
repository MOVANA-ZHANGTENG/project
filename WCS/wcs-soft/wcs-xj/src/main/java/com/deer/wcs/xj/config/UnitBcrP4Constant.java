package com.deer.wcs.xj.config;

import java.util.HashMap;
import java.util.Map;

public class UnitBcrP4Constant {
    private static final Map<Integer, String> UNIT_BCR_MAP;

    static {
        UNIT_BCR_MAP = new HashMap<>();
        UNIT_BCR_MAP.put(2, "bcr1");
        UNIT_BCR_MAP.put(4, "bcr2");
        UNIT_BCR_MAP.put(5, "bcr10");

        UNIT_BCR_MAP.put(8, "bcr3");
        UNIT_BCR_MAP.put(10, "bcr4");
        UNIT_BCR_MAP.put(11, "bcr11");

        UNIT_BCR_MAP.put(12, "bcr5");
        UNIT_BCR_MAP.put(24, "bcr6");
        UNIT_BCR_MAP.put(27, "bcr11");
        UNIT_BCR_MAP.put(32, "bcr12");
        UNIT_BCR_MAP.put(36, "bcr13");
        UNIT_BCR_MAP.put(42, "bcr14");
        UNIT_BCR_MAP.put(44, "bcr15");
        UNIT_BCR_MAP.put(45, "bcr16");

        UNIT_BCR_MAP.put(49, "bcr17");
        UNIT_BCR_MAP.put(50, "bcr19");

        UNIT_BCR_MAP.put(52, "bcr18");
        UNIT_BCR_MAP.put(54, "bcr20");
    }

    public static String getBcrCode(int unitId) {
        return UNIT_BCR_MAP.get(unitId);
    }
}