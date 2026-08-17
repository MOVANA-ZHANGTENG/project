package com.deer.wcs.system.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;


/**
 * 单据记录对象 bill_recordCriteria
 *
 * @author deer
 * @date 2023-10-13
 */
public class BillRecordCriteria extends BillRecord
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
