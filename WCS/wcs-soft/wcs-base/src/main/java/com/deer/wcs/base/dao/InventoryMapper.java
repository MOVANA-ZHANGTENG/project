package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.Inventory;
import com.deer.wcs.base.model.InventoryDto;
import com.deer.wcs.base.model.InventoryCriteria;

/**
 * 库存信息Mapper接口
 * 
 * @author deer
 * @date 2024-08-22
 */
public interface InventoryMapper  extends Mapper<Inventory>
{
    /**
     * 查询库存信息
     *
     * @param id 库存信息主键
     * @return 库存信息
     */
    public Inventory selectInventoryById(Long id);

    /**
     * 查询库存信息列表
     * 
     * @param inventory 库存信息
     * @return 库存信息集合
     */
    public List<InventoryDto> findList(InventoryCriteria criteria);

    /**
     * 新增库存信息
     *
     * @param inventory 库存信息
     * @return 结果
     */
    public int insertInventory(Inventory inventory);

    /**
     * 修改库存信息
     *
     * @param inventory 库存信息
     * @return 结果
     */
    public int updateInventory(Inventory inventory);

    /**
     * 删除库存信息
     * 
     * @param id 库存信息主键
     * @return 结果
     */
    public int deleteInventoryById(Long id);

    /**
     * 批量删除库存信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteInventoryByIds(Long[] ids);
}
