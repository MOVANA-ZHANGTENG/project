package com.deer.wcs.base.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;


/**
 * 站台对象 position_infoCriteria
 *
 * @author deer
 * @date 2024-04-28
 */
public class PositionInfoCriteria extends PositionInfo
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
