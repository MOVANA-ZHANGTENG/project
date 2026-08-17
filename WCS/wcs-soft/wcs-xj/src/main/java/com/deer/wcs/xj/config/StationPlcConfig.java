package com.deer.wcs.xj.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单个站台PLC通讯地址配置
 * 数据来源：position_info 数据库表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationPlcConfig {

    private String wareCode;

    /**
     * 站台编号 UNIT1 / UNIT2 ...
     */
    private String stationCode;

    /**
     *  上位编码：对应的库位编码
     */
    private String subCode;
    /*
     *数字编号
     */
    private short no;

    private String type;

    private String ip;

    // ===================== PLC -> PC (WCS读取区域) =====================
    /** PLC下发WCS区域基础地址：DB101. */
    private String readBaseAddr;
    /** PLC→PC信号起始字节偏移 */
    private double readByteOffset;

    // ===================== PC -> PLC (WCS写入区域) =====================
    /** WCS下发PLC区域基础地址：DB100. */
    private String writeBaseAddr;
    /** PC→PLC信号起始字节偏移 */
    private double writeByteOffset;
}