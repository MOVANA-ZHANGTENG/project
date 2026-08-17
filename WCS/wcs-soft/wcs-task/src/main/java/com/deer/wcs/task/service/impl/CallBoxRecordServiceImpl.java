package com.deer.wcs.task.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.task.dao.CallBoxRecordMapper;
import com.deer.wcs.task.model.CallBoxRecord;
import com.deer.wcs.task.model.CallBoxRecordDto;
import com.deer.wcs.task.model.CallBoxRecordCriteria;
import com.deer.wcs.task.service.CallBoxRecordService;

/**
 * 呼叫盒记录Service业务层处理
 * 
 * @author deer
 * @date 2024-12-02
 */
@Service
public class CallBoxRecordServiceImpl  extends AbstractService<CallBoxRecord, Long>  implements CallBoxRecordService
{
    @Autowired
    private CallBoxRecordMapper callBoxRecordMapper;

    /**
     * 查询呼叫盒记录
     *
     * @param id 呼叫盒记录主键
     * @return 呼叫盒记录
     */
    @Override
    public CallBoxRecord selectCallBoxRecordById(Long id)
    {
        return callBoxRecordMapper.selectCallBoxRecordById(id);
    }

    /**
     * 查询呼叫盒记录列表
     * 
     * @param criteria
     * @return 呼叫盒记录
     */
    @Override
    public List<CallBoxRecordDto> findList(CallBoxRecordCriteria criteria)
    {
        return callBoxRecordMapper.findList(criteria);
    }

    /**
     * 新增呼叫盒记录
     *
     * @param callBoxRecord 呼叫盒记录
     * @return 结果
     */
    @Override
    public int insertCallBoxRecord(CallBoxRecord callBoxRecord)
    {
        return callBoxRecordMapper.insertCallBoxRecord(callBoxRecord);
    }

    /**
     * 修改呼叫盒记录
     *
     * @param callBoxRecord 呼叫盒记录
     * @return 结果
     */
    @Override
    public int updateCallBoxRecord(CallBoxRecord callBoxRecord)
    {
        return callBoxRecordMapper.updateCallBoxRecord(callBoxRecord);
    }

    /**
     * 批量删除呼叫盒记录
     * 
     * @param ids 需要删除的呼叫盒记录主键
     * @return 结果
     */
    @Override
    public int deleteCallBoxRecordByIds(Long[] ids)
    {
        return callBoxRecordMapper.deleteCallBoxRecordByIds(ids);
    }

    /**
     * 删除呼叫盒记录信息
     * 
     * @param id 呼叫盒记录主键
     * @return 结果
     */
    @Override
    public int deleteCallBoxRecordById(Long id)
    {
        return callBoxRecordMapper.deleteCallBoxRecordById(id);
    }
}
