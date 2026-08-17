package com.deer.wcs.task.model.recvInterface;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description:
 * @author:zfj
 * @date:2024/5/28 14:48
 */
@Data
@NoArgsConstructor
@ToString
public class HttpResult {
    private String code;
    private String msg;
    private String data;
}
