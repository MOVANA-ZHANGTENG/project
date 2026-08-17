package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.PalletInfoMapper;
import com.deer.wcs.base.model.PalletInfo;
import com.deer.wcs.base.model.PalletInfoCriteria;
import com.deer.wcs.base.model.PalletInfoDto;
import com.deer.wcs.base.service.PalletInfoService;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * 托盘信息Service业务层处理
 * 
 * @author deer
 * @date 2024-05-29
 */
@Service
public class PalletInfoServiceImpl  extends AbstractService<PalletInfo, Long>  implements PalletInfoService
{
    @Autowired
    private PalletInfoMapper palletInfoMapper;


    @Override
    public void save(PalletInfo model) {
        if(model.getIsEmpty()==null){
            model.setIsEmpty("0");
        }
        PalletInfo palletInfo = findBy("code",model.getCode());
        if(palletInfo==null){
            super.save(model);
        }else{
            palletInfo.setCellCode(model.getCellCode());
            super.update(palletInfo);
        }
    }

    @Override
    public PalletInfo findByCellCode(String wareCode, String cellCode) {
        Condition condition = new Condition(PalletInfo.class);
        condition.createCriteria().andEqualTo("wareCode", wareCode).andEqualTo("cellCode", cellCode);
        List<PalletInfo> list = palletInfoMapper.selectByCondition(condition);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public PalletInfo findByCode(String code) {
        return palletInfoMapper.findByCode(code);
    }

    @Override
    public PalletInfo findByCode(String wareCode, String code) {
        Condition condition = new Condition(PalletInfo.class);
        condition.createCriteria().andEqualTo("wareCode", wareCode).andEqualTo("code", code);
        List<PalletInfo> list = palletInfoMapper.selectByCondition(condition);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public PalletInfo getAEmptyPallet(String wareCode) {
       return palletInfoMapper.getAEmptyPallet(wareCode);
    }

    @Override
    public PalletInfo getAEmptyPalletByLineCode(String wareCode, String lineCode) {
        return palletInfoMapper.getAEmptyPalletByLineCode(wareCode,lineCode);
    }

    /**
     * 查询托盘信息
     *
     * @param id 托盘信息主键
     * @return 托盘信息
     */
    @Override
    public PalletInfo selectPalletInfoById(Long id)
    {
        return palletInfoMapper.selectPalletInfoById(id);
    }

    /**
     * 查询托盘信息列表
     * 
     * @param criteria
     * @return 托盘信息
     */
    @Override
    public List<PalletInfoDto> findList(PalletInfoCriteria criteria)
    {
        return palletInfoMapper.findList(criteria);
    }

    /**
     * 新增托盘信息
     *
     * @param palletInfo 托盘信息
     * @return 结果
     */
    @Override
    public int insertPalletInfo(PalletInfo palletInfo)
    {
        return palletInfoMapper.insertPalletInfo(palletInfo);
    }

    /**
     * 修改托盘信息
     *
     * @param palletInfo 托盘信息
     * @return 结果
     */
    @Override
    public int updatePalletInfo(PalletInfo palletInfo)
    {
        return palletInfoMapper.updatePalletInfo(palletInfo);
    }

    /**
     * 批量删除托盘信息
     * 
     * @param ids 需要删除的托盘信息主键
     * @return 结果
     */
    @Override
    public int deletePalletInfoByIds(Long[] ids)
    {
        return palletInfoMapper.deletePalletInfoByIds(ids);
    }

    /**
     * 删除托盘信息信息
     * 
     * @param id 托盘信息主键
     * @return 结果
     */
    @Override
    public int deletePalletInfoById(Long id)
    {
        return palletInfoMapper.deletePalletInfoById(id);
    }
}
