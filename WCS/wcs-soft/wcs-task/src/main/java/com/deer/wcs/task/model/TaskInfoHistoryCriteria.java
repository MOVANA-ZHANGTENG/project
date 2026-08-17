package com.deer.wcs.task.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;


/**
 * 任务历史对象 task_info_historyCriteria
 *
 * @author deer
 * @date 2024-06-04
 */
public class TaskInfoHistoryCriteria extends TaskInfoHistory
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
