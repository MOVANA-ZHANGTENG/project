package com.deer.wcs.base.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description:
 * @author:zfj
 * @date:2024/5/22 9:43
 */
@Data
@NoArgsConstructor
@ToString
public class ValueData<T>{
    private T data;
    public ValueData(T data){
        this.data = data;
    }
}
