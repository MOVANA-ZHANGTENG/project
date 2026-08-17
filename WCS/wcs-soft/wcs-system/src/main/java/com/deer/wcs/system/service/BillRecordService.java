package com.deer.wcs.system.service;

import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.system.model.BillRecord;
import com.deer.wcs.system.model.BillRecordCriteria;
import com.deer.wcs.system.model.BillRecordDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 单据记录Service接口
 * 
 * @author deer
 * @date 2023-10-13
 */
@Transactional
public interface BillRecordService extends Service<BillRecord, Integer>
{
    void createTaskRecord(Long taskId, String content);
    void createBillRecord(String billNo,String type,Long userId,String userName,String content);
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
     * @param criteria
     * @return 单据记录集合
     */
    public List<BillRecordDto> findList(BillRecordCriteria criteria);
    List<BillRecord> findByBillNo(String billNo);
    List<BillRecordDto> findCount(BillRecordCriteria criteria);

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
     * 批量删除单据记录
     * 
     * @param billRecordIds 需要删除的单据记录主键集合
     * @return 结果
     */
    public int deleteBillRecordByBillRecordIds(Integer[] billRecordIds);

    /**
     * 删除单据记录信息
     * 
     * @param billRecordId 单据记录主键
     * @return 结果
     */
    public int deleteBillRecordByBillRecordId(Integer billRecordId);
}
