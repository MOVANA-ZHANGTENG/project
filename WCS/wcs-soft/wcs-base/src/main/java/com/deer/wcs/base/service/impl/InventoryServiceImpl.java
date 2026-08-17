package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.InventoryMapper;
import com.deer.wcs.base.model.Inventory;
import com.deer.wcs.base.model.InventoryDto;
import com.deer.wcs.base.model.InventoryCriteria;
import com.deer.wcs.base.service.InventoryService;

/**
 * 库存信息Service业务层处理
 * 
 * @author deer
 * @date 2024-08-22
 */
@Service
public class InventoryServiceImpl  extends AbstractService<Inventory, Long>  implements InventoryService
{
    @Autowired
    private InventoryMapper inventoryMapper;

    /**
     * 查询库存信息
     *
     * @param id 库存信息主键
     * @return 库存信息
     */
    @Override
    public Inventory selectInventoryById(Long id)
    {
        return inventoryMapper.selectInventoryById(id);
    }

    /**
     * 查询库存信息列表
     * 
     * @param criteria
     * @return 库存信息
     */
    @Override
    public List<InventoryDto> findList(InventoryCriteria criteria)
    {
        return inventoryMapper.findList(criteria);
    }

    /**
     * 新增库存信息
     *
     * @param inventory 库存信息
     * @return 结果
     */
    @Override
    public int insertInventory(Inventory inventory)
    {
        return inventoryMapper.insertInventory(inventory);
    }

    /**
     * 修改库存信息
     *
     * @param inventory 库存信息
     * @return 结果
     */
    @Override
    public int updateInventory(Inventory inventory)
    {
        return inventoryMapper.updateInventory(inventory);
    }

    /**
     * 批量删除库存信息
     * 
     * @param ids 需要删除的库存信息主键
     * @return 结果
     */
    @Override
    public int deleteInventoryByIds(Long[] ids)
    {
        return inventoryMapper.deleteInventoryByIds(ids);
    }

    /**
     * 删除库存信息信息
     * 
     * @param id 库存信息主键
     * @return 结果
     */
    @Override
    public int deleteInventoryById(Long id)
    {
        return inventoryMapper.deleteInventoryById(id);
    }
}
