package com.deer.wcs.rcs.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;


/**
 * 车型号对象 rcs_car_typeCriteria
 *
 * @author deer
 * @date 2025-10-14
 */
public class RcsCarTypeCriteria extends RcsCarType
{
     /** 请求参数 */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
            if(this.params==null){
                this.params = new HashMap<>();
            }
            return this.params;
        }

        public void setParams(Map<String, Object> params) {
            if(params==null){
                params = new HashMap<>();
            }
            this.params = params;
        }

}
