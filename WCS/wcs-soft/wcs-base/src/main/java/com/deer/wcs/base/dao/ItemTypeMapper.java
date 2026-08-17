package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.ItemType;
import com.deer.wcs.base.model.ItemTypeDto;
import com.deer.wcs.base.model.ItemTypeCriteria;

/**
 * 物料类型Mapper接口
 * 
 * @author deer
 * @date 2025-09-22
 */
public interface ItemTypeMapper  extends Mapper<ItemType>
{
    /**
     * 查询物料类型
     *
     * @param id 物料类型主键
     * @return 物料类型
     */
    public ItemType selectItemTypeById(Long id);

    /**
     * 查询物料类型列表
     * 
     * @param itemType 物料类型
     * @return 物料类型集合
     */
    public List<ItemTypeDto> findList(ItemTypeCriteria criteria);

    /**
     * 新增物料类型
     *
     * @param itemType 物料类型
     * @return 结果
     */
    public int insertItemType(ItemType itemType);

    /**
     * 修改物料类型
     *
     * @param itemType 物料类型
     * @return 结果
     */
    public int updateItemType(ItemType itemType);

    /**
     * 删除物料类型
     * 
     * @param id 物料类型主键
     * @return 结果
     */
    public int deleteItemTypeById(Long id);

    /**
     * 批量删除物料类型
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteItemTypeByIds(Long[] ids);
}
