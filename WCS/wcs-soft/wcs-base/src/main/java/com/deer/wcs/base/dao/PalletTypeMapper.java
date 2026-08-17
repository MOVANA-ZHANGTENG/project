package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.PalletType;
import com.deer.wcs.base.model.PalletTypeDto;
import com.deer.wcs.base.model.PalletTypeCriteria;

/**
 * 托盘类型Mapper接口
 * 
 * @author deer
 * @date 2024-05-29
 */
public interface PalletTypeMapper  extends Mapper<PalletType>
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
     * @param palletType 托盘类型
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
     * 删除托盘类型
     * 
     * @param id 托盘类型主键
     * @return 结果
     */
    public int deletePalletTypeById(Long id);

    /**
     * 批量删除托盘类型
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePalletTypeByIds(Long[] ids);
}
