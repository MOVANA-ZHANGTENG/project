package com.deer.wcs.task.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.task.model.CallBoxRecord;
import com.deer.wcs.task.model.CallBoxRecordDto;
import com.deer.wcs.task.model.CallBoxRecordCriteria;

/**
 * 呼叫盒记录Service接口
 * 
 * @author deer
 * @date 2024-12-02
 */
public interface CallBoxRecordService   extends Service<CallBoxRecord, Long>
{
    /**
     * 查询呼叫盒记录
     *
     * @param id 呼叫盒记录主键
     * @return 呼叫盒记录
     */
    public CallBoxRecord selectCallBoxRecordById(Long id);

    /**
     * 查询呼叫盒记录列表
     * 
     * @param criteria
     * @return 呼叫盒记录集合
     */
    public List<CallBoxRecordDto> findList(CallBoxRecordCriteria criteria);

    /**
     * 新增呼叫盒记录
     *
     * @param callBoxRecord 呼叫盒记录
     * @return 结果
     */
    public int insertCallBoxRecord(CallBoxRecord callBoxRecord);

    /**
     * 修改呼叫盒记录
     *
     * @param callBoxRecord 呼叫盒记录
     * @return 结果
     */
    public int updateCallBoxRecord(CallBoxRecord callBoxRecord);

    /**
     * 批量删除呼叫盒记录
     * 
     * @param ids 需要删除的呼叫盒记录主键集合
     * @return 结果
     */
    public int deleteCallBoxRecordByIds(Long[] ids);

    /**
     * 删除呼叫盒记录信息
     * 
     * @param id 呼叫盒记录主键
     * @return 结果
     */
    public int deleteCallBoxRecordById(Long id);
}
