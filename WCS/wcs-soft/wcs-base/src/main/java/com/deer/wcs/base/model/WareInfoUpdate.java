package com.deer.wcs.base.model;

import lombok.Data;
import lombok.ToString;

/**
 * @description:
 * @author:zfj
 * @date:2024/8/21 14:21
 */

@Data
@ToString
public class WareInfoUpdate {
    private String oldWareCode;
    private String newWareCode;
    private String newWareName;

    private String oldAreaCode;
    private String newAreaCode;
    private String newAreaName;

    private String oldLineCode;
    private String newLineCode;
    private String newLineName;
}
