package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.PalletType;
import com.deer.wcs.base.model.PalletTypeDto;
import com.deer.wcs.base.model.PalletTypeCriteria;

/**
 * 托盘类型Service接口
 * 
 * @author deer
 * @date 2024-05-29
 */
public interface PalletTypeService   extends Service<PalletType, Long>
{
    /**
     * 查询托盘类型
     *
     * @param id 托盘类型主键
     * @return 托盘类型
     */
    public PalletType selectPalletTypeById(Long id);

    /**
     * 查询托盘类型列表
     * 
     * @param criteria
     * @return 托盘类型集合
     */
    public List<PalletTypeDto> findList(PalletTypeCriteria criteria);

    /**
     * 新增托盘类型
     *
     * @param palletType 托盘类型
     * @return 结果
     */
    public int insertPalletType(PalletType palletType);

    /**
     * 修改托盘类型
     *
     * @param palletType 托盘类型
     * @return 结果
     */
    public int updatePalletType(PalletType palletType);

    /**
     * 批量删除托盘类型
     * 
     * @param ids 需要删除的托盘类型主键集合
     * @return 结果
     */
    public int deletePalletTypeByIds(Long[] ids);

    /**
     * 删除托盘类型信息
     * 
     * @param id 托盘类型主键
     * @return 结果
     */
    public int deletePalletTypeById(Long id);
}
