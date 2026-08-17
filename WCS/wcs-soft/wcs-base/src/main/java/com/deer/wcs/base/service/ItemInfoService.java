package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.ItemInfo;
import com.deer.wcs.base.model.ItemInfoDto;
import com.deer.wcs.base.model.ItemInfoCriteria;

/**
 * 物料Service接口
 * 
 * @author deer
 * @date 2024-11-21
 */
public interface ItemInfoService   extends Service<ItemInfo, Long>
{
    /**
     * 查询物料
     *
     * @param id 物料主键
     * @return 物料
     */
    public ItemInfo selectItemInfoById(Long id);

    /**
     * 查询物料列表
     * 
     * @param criteria
     * @return 物料集合
     */
    public List<ItemInfoDto> findList(ItemInfoCriteria criteria);

    /**
     * 新增物料
     *
     * @param itemInfo 物料
     * @return 结果
     */
    public int insertItemInfo(ItemInfo itemInfo);

    /**
     * 修改物料
     *
     * @param itemInfo 物料
     * @return 结果
     */
    public int updateItemInfo(ItemInfo itemInfo);

    /**
     * 批量删除物料
     * 
     * @param ids 需要删除的物料主键集合
     * @return 结果
     */
    public int deleteItemInfoByIds(Long[] ids);

    /**
     * 删除物料信息
     * 
     * @param id 物料主键
     * @return 结果
     */
    public int deleteItemInfoById(Long id);
}
