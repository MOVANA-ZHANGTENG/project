package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.ItemInfoMapper;
import com.deer.wcs.base.model.ItemInfo;
import com.deer.wcs.base.model.ItemInfoDto;
import com.deer.wcs.base.model.ItemInfoCriteria;
import com.deer.wcs.base.service.ItemInfoService;

/**
 * 物料Service业务层处理
 * 
 * @author deer
 * @date 2024-11-21
 */
@Service
public class ItemInfoServiceImpl  extends AbstractService<ItemInfo, Long>  implements ItemInfoService
{
    @Autowired
    private ItemInfoMapper itemInfoMapper;

    /**
     * 查询物料
     *
     * @param id 物料主键
     * @return 物料
     */
    @Override
    public ItemInfo selectItemInfoById(Long id)
    {
        return itemInfoMapper.selectItemInfoById(id);
    }

    /**
     * 查询物料列表
     * 
     * @param criteria
     * @return 物料
     */
    @Override
    public List<ItemInfoDto> findList(ItemInfoCriteria criteria)
    {
        return itemInfoMapper.findList(criteria);
    }

    /**
     * 新增物料
     *
     * @param itemInfo 物料
     * @return 结果
     */
    @Override
    public int insertItemInfo(ItemInfo itemInfo)
    {
        return itemInfoMapper.insertItemInfo(itemInfo);
    }

    /**
     * 修改物料
     *
     * @param itemInfo 物料
     * @return 结果
     */
    @Override
    public int updateItemInfo(ItemInfo itemInfo)
    {
        return itemInfoMapper.updateItemInfo(itemInfo);
    }

    /**
     * 批量删除物料
     * 
     * @param ids 需要删除的物料主键
     * @return 结果
     */
    @Override
    public int deleteItemInfoByIds(Long[] ids)
    {
        return itemInfoMapper.deleteItemInfoByIds(ids);
    }

    /**
     * 删除物料信息
     * 
     * @param id 物料主键
     * @return 结果
     */
    @Override
    public int deleteItemInfoById(Long id)
    {
        return itemInfoMapper.deleteItemInfoById(id);
    }
}
