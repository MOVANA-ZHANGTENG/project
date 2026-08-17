package com.deer.wcs.system.dao;


import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.system.model.BillRecord;
import com.deer.wcs.system.model.BillRecordCriteria;
import com.deer.wcs.system.model.BillRecordDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单据记录Mapper接口
 * 
 * @author deer
 * @date 2023-10-13
 */
public interface BillRecordMapper extends Mapper<BillRecord>
{
    /**
     * 查询单据记录
     *
     * @param billRecordId 单据记录主键
     * @return 单据记录
     */
    public BillRecord selectBillRecordByBillRecordId(Integer billRecordId);

    /**
     * 查询单据记录列表
     * 
     * @param criteria 单据记录
     * @return 单据记录集合
     */
    public List<BillRecordDto> findList(BillRecordCriteria criteria);

    List<BillRecordDto> findCount(BillRecordCriteria criteria);
    List<BillRecord> findByBillNo(@Param("billNo") String  billNo);

    /**
     * 新增单据记录
     *
     * @param billRecord 单据记录
     * @return 结果
     */
    public int insertBillRecord(BillRecord billRecord);

    /**
     * 修改单据记录
     *
     * @param billRecord 单据记录
     * @return 结果
     */
    public int updateBillRecord(BillRecord billRecord);

    /**
     * 删除单据记录
     * 
     * @param billRecordId 单据记录主键
     * @return 结果
     */
    public int deleteBillRecordByBillRecordId(Integer billRecordId);

    /**
     * 批量删除单据记录
     * 
     * @param billRecordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBillRecordByBillRecordIds(Integer[] billRecordIds);
}
