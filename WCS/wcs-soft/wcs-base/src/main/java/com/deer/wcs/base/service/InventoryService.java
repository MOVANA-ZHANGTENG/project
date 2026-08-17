package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.Inventory;
import com.deer.wcs.base.model.InventoryDto;
import com.deer.wcs.base.model.InventoryCriteria;

/**
 * 库存信息Service接口
 * 
 * @author deer
 * @date 2024-08-22
 */
public interface InventoryService   extends Service<Inventory, Long>
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
     * @param criteria
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
     * 批量删除库存信息
     * 
     * @param ids 需要删除的库存信息主键集合
     * @return 结果
     */
    public int deleteInventoryByIds(Long[] ids);

    /**
     * 删除库存信息信息
     * 
     * @param id 库存信息主键
     * @return 结果
     */
    public int deleteInventoryById(Long id);
}
