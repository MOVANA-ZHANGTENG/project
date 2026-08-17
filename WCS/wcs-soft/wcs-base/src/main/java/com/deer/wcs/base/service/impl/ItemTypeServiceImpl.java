package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.ItemTypeMapper;
import com.deer.wcs.base.model.ItemType;
import com.deer.wcs.base.model.ItemTypeDto;
import com.deer.wcs.base.model.ItemTypeCriteria;
import com.deer.wcs.base.service.ItemTypeService;

/**
 * 物料类型Service业务层处理
 * 
 * @author deer
 * @date 2025-09-22
 */
@Service
public class ItemTypeServiceImpl  extends AbstractService<ItemType, Long>  implements ItemTypeService
{
    @Autowired
    private ItemTypeMapper itemTypeMapper;

    /**
     * 查询物料类型
     *
     * @param id 物料类型主键
     * @return 物料类型
     */
    @Override
    public ItemType selectItemTypeById(Long id)
    {
        return itemTypeMapper.selectItemTypeById(id);
    }

    /**
     * 查询物料类型列表
     * 
     * @param criteria
     * @return 物料类型
     */
    @Override
    public List<ItemTypeDto> findList(ItemTypeCriteria criteria)
    {
        return itemTypeMapper.findList(criteria);
    }

    /**
     * 新增物料类型
     *
     * @param itemType 物料类型
     * @return 结果
     */
    @Override
    public int insertItemType(ItemType itemType)
    {
        itemType.setCreateTime(DateUtil.getNowDateTimeString());
        return itemTypeMapper.insertItemType(itemType);
    }

    /**
     * 修改物料类型
     *
     * @param itemType 物料类型
     * @return 结果
     */
    @Override
    public int updateItemType(ItemType itemType)
    {
        return itemTypeMapper.updateItemType(itemType);
    }

    /**
     * 批量删除物料类型
     * 
     * @param ids 需要删除的物料类型主键
     * @return 结果
     */
    @Override
    public int deleteItemTypeByIds(Long[] ids)
    {
        return itemTypeMapper.deleteItemTypeByIds(ids);
    }

    /**
     * 删除物料类型信息
     * 
     * @param id 物料类型主键
     * @return 结果
     */
    @Override
    public int deleteItemTypeById(Long id)
    {
        return itemTypeMapper.deleteItemTypeById(id);
    }
}
