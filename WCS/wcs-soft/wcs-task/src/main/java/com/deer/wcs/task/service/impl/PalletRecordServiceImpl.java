package com.deer.wcs.task.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.task.dao.PalletRecordMapper;
import com.deer.wcs.task.model.PalletRecord;
import com.deer.wcs.task.model.PalletRecordDto;
import com.deer.wcs.task.model.PalletRecordCriteria;
import com.deer.wcs.task.service.PalletRecordService;

/**
 * 托盘记录Service业务层处理
 * 
 * @author deer
 * @date 2025-07-23
 */
@Service
public class PalletRecordServiceImpl  extends AbstractService<PalletRecord, Long>  implements PalletRecordService
{
    @Autowired
    private PalletRecordMapper palletRecordMapper;

    /**
     * 查询托盘记录
     *
     * @param id 托盘记录主键
     * @return 托盘记录
     */
    @Override
    public PalletRecord selectPalletRecordById(Long id)
    {
        return palletRecordMapper.selectPalletRecordById(id);
    }


    @Override
    public void record(String palletCode, String wareCode, Integer type, String content) {
        PalletRecord palletRecord =new PalletRecord();
        palletRecord.setPalletCode(palletCode);
        palletRecord.setWareCode(wareCode);
        palletRecord.setCreateTime(DateUtil.getNowDateTimeString());
        palletRecord.setContent(content);
        palletRecord.setType(type);
        super.save(palletRecord);
    }

    /**
     * 查询托盘记录列表
     * 
     * @param criteria
     * @return 托盘记录
     */
    @Override
    public List<PalletRecordDto> findList(PalletRecordCriteria criteria)
    {
        return palletRecordMapper.findList(criteria);
    }

    /**
     * 新增托盘记录
     *
     * @param palletRecord 托盘记录
     * @return 结果
     */
    @Override
    public int insertPalletRecord(PalletRecord palletRecord)
    {
        palletRecord.setCreateTime(DateUtil.getNowDateTimeString());
        return palletRecordMapper.insertPalletRecord(palletRecord);
    }

    /**
     * 修改托盘记录
     *
     * @param palletRecord 托盘记录
     * @return 结果
     */
    @Override
    public int updatePalletRecord(PalletRecord palletRecord)
    {
        return palletRecordMapper.updatePalletRecord(palletRecord);
    }

    /**
     * 批量删除托盘记录
     * 
     * @param ids 需要删除的托盘记录主键
     * @return 结果
     */
    @Override
    public int deletePalletRecordByIds(Long[] ids)
    {
        return palletRecordMapper.deletePalletRecordByIds(ids);
    }

    /**
     * 删除托盘记录信息
     * 
     * @param id 托盘记录主键
     * @return 结果
     */
    @Override
    public int deletePalletRecordById(Long id)
    {
        return palletRecordMapper.deletePalletRecordById(id);
    }
}
