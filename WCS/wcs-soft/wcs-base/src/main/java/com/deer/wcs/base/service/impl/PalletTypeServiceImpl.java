package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.PalletTypeMapper;
import com.deer.wcs.base.model.PalletType;
import com.deer.wcs.base.model.PalletTypeDto;
import com.deer.wcs.base.model.PalletTypeCriteria;
import com.deer.wcs.base.service.PalletTypeService;

/**
 * 托盘类型Service业务层处理
 * 
 * @author deer
 * @date 2024-05-29
 */
@Service
public class PalletTypeServiceImpl  extends AbstractService<PalletType, Long>  implements PalletTypeService
{
    @Autowired
    private PalletTypeMapper palletTypeMapper;

    /**
     * 查询托盘类型
     *
     * @param id 托盘类型主键
     * @return 托盘类型
     */
    @Override
    public PalletType selectPalletTypeById(Long id)
    {
        return palletTypeMapper.selectPalletTypeById(id);
    }

    /**
     * 查询托盘类型列表
     * 
     * @param criteria
     * @return 托盘类型
     */
    @Override
    public List<PalletTypeDto> findList(PalletTypeCriteria criteria)
    {
        return palletTypeMapper.findList(criteria);
    }

    /**
     * 新增托盘类型
     *
     * @param palletType 托盘类型
     * @return 结果
     */
    @Override
    public int insertPalletType(PalletType palletType)
    {
        return palletTypeMapper.insertPalletType(palletType);
    }

    /**
     * 修改托盘类型
     *
     * @param palletType 托盘类型
     * @return 结果
     */
    @Override
    public int updatePalletType(PalletType palletType)
    {
        return palletTypeMapper.updatePalletType(palletType);
    }

    /**
     * 批量删除托盘类型
     * 
     * @param ids 需要删除的托盘类型主键
     * @return 结果
     */
    @Override
    public int deletePalletTypeByIds(Long[] ids)
    {
        return palletTypeMapper.deletePalletTypeByIds(ids);
    }

    /**
     * 删除托盘类型信息
     * 
     * @param id 托盘类型主键
     * @return 结果
     */
    @Override
    public int deletePalletTypeById(Long id)
    {
        return palletTypeMapper.deletePalletTypeById(id);
    }
}
