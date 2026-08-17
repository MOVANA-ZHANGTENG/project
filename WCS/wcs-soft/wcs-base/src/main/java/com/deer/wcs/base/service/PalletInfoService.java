package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.PalletInfo;
import com.deer.wcs.base.model.PalletInfoDto;
import com.deer.wcs.base.model.PalletInfoCriteria;
import org.apache.ibatis.annotations.Param;

/**
 * 托盘信息Service接口
 * 
 * @author deer
 * @date 2024-05-29
 */
public interface PalletInfoService   extends Service<PalletInfo, Long>
{
    /**
     * 根据库位查询托盘信息
     * @param wareCode 仓库编码
     * @param cellCode 库位编码
     * @return 托盘信息
     */
    PalletInfo findByCellCode(@Param("wareCode") String wareCode,@Param("cellCode") String cellCode);


    PalletInfo findByCode(@Param("code") String code);

    PalletInfo findByCode(@Param("wareCode") String wareCode,@Param("code") String code);

    PalletInfo getAEmptyPallet(@Param("wareCode") String wareCode);

    PalletInfo getAEmptyPalletByLineCode(@Param("wareCode") String wareCode,@Param("lineCode") String lineCode);
    /**
     * 查询托盘信息
     *
     * @param id 托盘信息主键
     * @return 托盘信息
     */
    public PalletInfo selectPalletInfoById(Long id);

    /**
     * 查询托盘信息列表
     * 
     * @param criteria
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
     * 批量删除托盘信息
     * 
     * @param ids 需要删除的托盘信息主键集合
     * @return 结果
     */
    public int deletePalletInfoByIds(Long[] ids);

    /**
     * 删除托盘信息信息
     * 
     * @param id 托盘信息主键
     * @return 结果
     */
    public int deletePalletInfoById(Long id);

}
