package com.deer.wcs.system.service.impl;

import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.dao.BillRecordMapper;
import com.deer.wcs.system.model.BillRecord;
import com.deer.wcs.system.model.BillRecordCriteria;
import com.deer.wcs.system.model.BillRecordDto;
import com.deer.wcs.system.service.BillRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 单据记录Service业务层处理
 * 
 * @author deer
 * @date 2023-10-13
 */
@Service
@Transactional
public class BillRecordServiceImpl extends AbstractService<BillRecord, Integer> implements BillRecordService
{
    @Autowired
    private BillRecordMapper billRecordMapper;


    @Override
    public void createTaskRecord(Long taskId, String content) {
        BillRecord billRecord = new BillRecord();
        billRecord.setBillNo(taskId.toString());
        billRecord.setBillType("task_info");
        billRecord.setCreateTime(DateUtil.getNowDateTimeString());
        billRecord.setContent(content);
        super.save(billRecord);
    }

    @Override
    public void createBillRecord(String billNo, String type, Long userId, String userName, String content) {
        BillRecord billRecord = new BillRecord();
        billRecord.setBillNo(billNo);
        billRecord.setBillType(type);
        billRecord.setCreateTime(DateUtil.getNowDateTimeString());
        billRecord.setCreateUserId(userId);
        billRecord.setCreateUserName(userName);
        billRecord.setContent(content);
        super.save(billRecord);
    }

    /**
     * 查询单据记录
     *
     * @param billRecordId 单据记录主键
     * @return 单据记录
     */
    @Override
    public BillRecord selectBillRecordByBillRecordId(Integer billRecordId)
    {
        return billRecordMapper.selectBillRecordByBillRecordId(billRecordId);
    }

    /**
     * 查询单据记录列表
     * 
     * @param criteria
     * @return 单据记录
     */
    @Override
    public List<BillRecordDto> findList(BillRecordCriteria criteria)
    {
        return billRecordMapper.findList(criteria);
    }
    @Override
    public List<BillRecord> findByBillNo(String billNo)
    {
        return billRecordMapper.findByBillNo(billNo);
    }

    @Override
    public List<BillRecordDto> findCount(BillRecordCriteria criteria)
    {
        return billRecordMapper.findCount(criteria);
    }

    /**
     * 新增单据记录
     *
     * @param billRecord 单据记录
     * @return 结果
     */
    @Override
    public int insertBillRecord(BillRecord billRecord)
    {
        billRecord.setCreateTime(DateUtil.getNowDateTimeString());
        return billRecordMapper.insertBillRecord(billRecord);
    }

    /**
     * 修改单据记录
     *
     * @param billRecord 单据记录
     * @return 结果
     */
    @Override
    public int updateBillRecord(BillRecord billRecord)
    {
        return billRecordMapper.updateBillRecord(billRecord);
    }

    /**
     * 批量删除单据记录
     * 
     * @param billRecordIds 需要删除的单据记录主键
     * @return 结果
     */
    @Override
    public int deleteBillRecordByBillRecordIds(Integer[] billRecordIds)
    {
        return billRecordMapper.deleteBillRecordByBillRecordIds(billRecordIds);
    }

    /**
     * 删除单据记录信息
     * 
     * @param billRecordId 单据记录主键
     * @return 结果
     */
    @Override
    public int deleteBillRecordByBillRecordId(Integer billRecordId)
    {
        return billRecordMapper.deleteBillRecordByBillRecordId(billRecordId);
    }
}
