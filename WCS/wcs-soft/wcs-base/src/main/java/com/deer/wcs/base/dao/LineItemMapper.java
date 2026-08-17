package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.LineItem;
import com.deer.wcs.base.model.LineItemDto;
import com.deer.wcs.base.model.LineItemCriteria;

/**
 * 产线物料Mapper接口
 * 
 * @author deer
 * @date 2024-12-21
 */
public interface LineItemMapper  extends Mapper<LineItem>
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
     * @param lineItem 产线物料
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
     * 删除产线物料
     * 
     * @param id 产线物料主键
     * @return 结果
     */
    public int deleteLineItemById(Integer id);

    /**
     * 批量删除产线物料
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLineItemByIds(Integer[] ids);
}
