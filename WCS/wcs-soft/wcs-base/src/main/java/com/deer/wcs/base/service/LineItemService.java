package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.LineItem;
import com.deer.wcs.base.model.LineItemDto;
import com.deer.wcs.base.model.LineItemCriteria;

/**
 * 产线物料Service接口
 * 
 * @author deer
 * @date 2024-12-21
 */
public interface LineItemService   extends Service<LineItem, Long>
{
    /**
     * 查询产线物料
     *
     * @param id 产线物料主键
     * @return 产线物料
     */
    public LineItem selectLineItemById(Integer id);

    /**
     * 查询产线物料列表
     * 
     * @param criteria
     * @return 产线物料集合
     */
    public List<LineItemDto> findList(LineItemCriteria criteria);

    /**
     * 新增产线物料
     *
     * @param lineItem 产线物料
     * @return 结果
     */
    public int insertLineItem(LineItem lineItem);

    /**
     * 修改产线物料
     *
     * @param lineItem 产线物料
     * @return 结果
     */
    public int updateLineItem(LineItem lineItem);

    /**
     * 批量删除产线物料
     * 
     * @param ids 需要删除的产线物料主键集合
     * @return 结果
     */
    public int deleteLineItemByIds(Integer[] ids);

    /**
     * 删除产线物料信息
     * 
     * @param id 产线物料主键
     * @return 结果
     */
    public int deleteLineItemById(Integer id);
}
