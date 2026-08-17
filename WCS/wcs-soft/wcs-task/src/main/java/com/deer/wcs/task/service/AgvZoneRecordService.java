package com.deer.wcs.task.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.task.model.AgvZoneRecord;
import com.deer.wcs.task.model.AgvZoneRecordDto;
import com.deer.wcs.task.model.AgvZoneRecordCriteria;

/**
 * 交管日志Service接口
 * 
 * @author deer
 * @date 2024-11-26
 */
public interface AgvZoneRecordService   extends Service<AgvZoneRecord, Long>
{
    /**
     * 查询交管日志
     *
     * @param id 交管日志主键
     * @return 交管日志
     */
    public AgvZoneRecord selectAgvZoneRecordById(Long id);

    public AgvZoneRecord findFirstState0();
    /**
     * 查询交管日志列表
     * 
     * @param criteria
     * @return 交管日志集合
     */
    public List<AgvZoneRecordDto> findList(AgvZoneRecordCriteria criteria);

    /**
     * 新增交管日志
     *
     * @param agvZoneRecord 交管日志
     * @return 结果
     */
    public int insertAgvZoneRecord(AgvZoneRecord agvZoneRecord);

    /**
     * 修改交管日志
     *
     * @param agvZoneRecord 交管日志
     * @return 结果
     */
    public int updateAgvZoneRecord(AgvZoneRecord agvZoneRecord);

    /**
     * 批量删除交管日志
     * 
     * @param ids 需要删除的交管日志主键集合
     * @return 结果
     */
    public int deleteAgvZoneRecordByIds(Long[] ids);

    /**
     * 删除交管日志信息
     * 
     * @param id 交管日志主键
     * @return 结果
     */
    public int deleteAgvZoneRecordById(Long id);
}
