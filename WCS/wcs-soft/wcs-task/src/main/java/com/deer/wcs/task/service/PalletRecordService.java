package com.deer.wcs.task.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.task.model.PalletRecord;
import com.deer.wcs.task.model.PalletRecordDto;
import com.deer.wcs.task.model.PalletRecordCriteria;

/**
 * 托盘记录Service接口
 * 
 * @author deer
 * @date 2025-07-23
 */
public interface PalletRecordService   extends Service<PalletRecord, Long>
{


    /**
     * 查询托盘记录
     *
     * @param id 托盘记录主键
     * @return 托盘记录
     */
    public PalletRecord selectPalletRecordById(Long id);

    /**
     *  托盘记录-----------
     *  type:
     *  0---信息
     *  1---成功
     *  2---报警
     *  3---错误
     * @param palletCode
     * @param wareCode
     * @param type
     * @param content
     */
    public void  record(String palletCode,String wareCode,Integer type,String content);

    /**
     * 查询托盘记录列表
     * 
     * @param criteria
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
     * 批量删除托盘记录
     * 
     * @param ids 需要删除的托盘记录主键集合
     * @return 结果
     */
    public int deletePalletRecordByIds(Long[] ids);

    /**
     * 删除托盘记录信息
     * 
     * @param id 托盘记录主键
     * @return 结果
     */
    public int deletePalletRecordById(Long id);
}
