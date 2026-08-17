package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.LineItemMapper;
import com.deer.wcs.base.model.LineItem;
import com.deer.wcs.base.model.LineItemDto;
import com.deer.wcs.base.model.LineItemCriteria;
import com.deer.wcs.base.service.LineItemService;

/**
 * 产线物料Service业务层处理
 * 
 * @author deer
 * @date 2024-12-21
 */
@Service
public class LineItemServiceImpl  extends AbstractService<LineItem, Long>  implements LineItemService
{
    @Autowired
    private LineItemMapper lineItemMapper;

    /**
     * 查询产线物料
     *
     * @param id 产线物料主键
     * @return 产线物料
     */
    @Override
    public LineItem selectLineItemById(Integer id)
    {
        return lineItemMapper.selectLineItemById(id);
    }

    /**
     * 查询产线物料列表
     * 
     * @param criteria
     * @return 产线物料
     */
    @Override
    public List<LineItemDto> findList(LineItemCriteria criteria)
    {
        return lineItemMapper.findList(criteria);
    }

    /**
     * 新增产线物料
     *
     * @param lineItem 产线物料
     * @return 结果
     */
    @Override
    public int insertLineItem(LineItem lineItem)
    {
        return lineItemMapper.insertLineItem(lineItem);
    }

    /**
     * 修改产线物料
     *
     * @param lineItem 产线物料
     * @return 结果
     */
    @Override
    public int updateLineItem(LineItem lineItem)
    {
        return lineItemMapper.updateLineItem(lineItem);
    }

    /**
     * 批量删除产线物料
     * 
     * @param ids 需要删除的产线物料主键
     * @return 结果
     */
    @Override
    public int deleteLineItemByIds(Integer[] ids)
    {
        return lineItemMapper.deleteLineItemByIds(ids);
    }

    /**
     * 删除产线物料信息
     * 
     * @param id 产线物料主键
     * @return 结果
     */
    @Override
    public int deleteLineItemById(Integer id)
    {
        return lineItemMapper.deleteLineItemById(id);
    }
}
