package com.deer.wcs.base.model;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;


/**
 * 库存信息对象 inventoryCriteria
 *
 * @author deer
 * @date 2024-08-22
 */
public class InventoryCriteria extends Inventory
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
