package com.deer.wcs.task.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.PalletRecord;
import com.deer.wcs.task.model.PalletRecordDto;
import com.deer.wcs.task.model.PalletRecordCriteria;

/**
 * 托盘记录Mapper接口
 * 
 * @author deer
 * @date 2025-07-23
 */
public interface PalletRecordMapper  extends Mapper<PalletRecord>
{
    /**
     * 查询托盘记录
     *
     * @param id 托盘记录主键
     * @return 托盘记录
     */
    public PalletRecord selectPalletRecordById(Long id);

    /**
     * 查询托盘记录列表
     * 
     * @param palletRecord 托盘记录
     * @return 托盘记录集合
     */
    public List<PalletRecordDto> findList(PalletRecordCriteria criteria);

    /**
     * 新增托盘记录
     *
     * @param palletRecord 托盘记录
     * @return 结果
     */
    public int insertPalletRecord(PalletRecord palletRecord);

    /**
     * 修改托盘记录
     *
     * @param palletRecord 托盘记录
     * @return 结果
     */
    public int updatePalletRecord(PalletRecord palletRecord);

    /**
     * 删除托盘记录
     * 
     * @param id 托盘记录主键
     * @return 结果
     */
    public int deletePalletRecordById(Long id);

    /**
     * 批量删除托盘记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePalletRecordByIds(Long[] ids);
}
