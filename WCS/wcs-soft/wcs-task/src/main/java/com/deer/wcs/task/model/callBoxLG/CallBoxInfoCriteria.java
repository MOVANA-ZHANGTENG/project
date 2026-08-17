package com.deer.wcs.task.model.callBoxLG;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;


/**
 * 呼叫盒对象 call_box_infoCriteria
 *
 * @author deer
 * @date 2024-07-30
 */
public class CallBoxInfoCriteria extends CallBoxInfo
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
