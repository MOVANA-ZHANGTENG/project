package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.PalletInfo;
import com.deer.wcs.base.model.PalletInfoDto;
import com.deer.wcs.base.model.PalletInfoCriteria;
import org.apache.ibatis.annotations.Param;

/**
 * 托盘信息Mapper接口
 * 
 * @author deer
 * @date 2024-05-29
 */
public interface PalletInfoMapper  extends Mapper<PalletInfo>
{
    /**
     * 查询托盘信息
     *
     * @param id 托盘信息主键
     * @return 托盘信息
     */
    public PalletInfo selectPalletInfoById(Long id);
    public PalletInfo getAEmptyPallet(@Param("wareCode") String wareCode);
    public PalletInfo getAEmptyPalletByLineCode(@Param("wareCode") String wareCode,@Param("lineCode") String lineCode);

    /**
     * 查询托盘信息列表
     * 
     * @param palletInfo 托盘信息
     * @return 托盘信息集合
     */
    public List<PalletInfoDto> findList(PalletInfoCriteria criteria);

    /**
     * 新增托盘信息
     *
     * @param palletInfo 托盘信息
     * @return 结果
     */
    public int insertPalletInfo(PalletInfo palletInfo);

    /**
     * 修改托盘信息
     *
     * @param palletInfo 托盘信息
     * @return 结果
     */
    public int updatePalletInfo(PalletInfo palletInfo);

    /**
     * 删除托盘信息
     * 
     * @param id 托盘信息主键
     * @return 结果
     */
    public int deletePalletInfoById(Long id);

    /**
     * 批量删除托盘信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePalletInfoByIds(Long[] ids);

    PalletInfo findByCode(@Param("palletCode") String code);
}
