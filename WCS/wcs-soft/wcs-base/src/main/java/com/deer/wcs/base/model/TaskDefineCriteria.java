package com.deer.wcs.base.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;


/**
 * 任务定义对象 task_defineCriteria
 *
 * @author deer
 * @date 2024-04-28
 */
public class TaskDefineCriteria extends TaskDefine
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
