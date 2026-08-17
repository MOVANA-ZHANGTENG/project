package com.deer.wcs.task.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.task.dao.AgvZoneRecordMapper;
import com.deer.wcs.task.model.AgvZoneRecord;
import com.deer.wcs.task.model.AgvZoneRecordDto;
import com.deer.wcs.task.model.AgvZoneRecordCriteria;
import com.deer.wcs.task.service.AgvZoneRecordService;

/**
 * 交管日志Service业务层处理
 * 
 * @author deer
 * @date 2024-11-26
 */
@Service
public class AgvZoneRecordServiceImpl  extends AbstractService<AgvZoneRecord, Long>  implements AgvZoneRecordService
{
    @Autowired
    private AgvZoneRecordMapper agvZoneRecordMapper;

    /**
     * 查询交管日志
     *
     * @param id 交管日志主键
     * @return 交管日志
     */
    @Override
    public AgvZoneRecord selectAgvZoneRecordById(Long id)
    {
        return agvZoneRecordMapper.selectAgvZoneRecordById(id);
    }


    @Override
    public AgvZoneRecord findFirstState0( )
    {
        return agvZoneRecordMapper.findFirstState0( );
    }

    /**
     * 查询交管日志列表
     * 
     * @param criteria
     * @return 交管日志
     */
    @Override
    public List<AgvZoneRecordDto> findList(AgvZoneRecordCriteria criteria)
    {
        return agvZoneRecordMapper.findList(criteria);
    }

    /**
     * 新增交管日志
     *
     * @param agvZoneRecord 交管日志
     * @return 结果
     */
    @Override
    public int insertAgvZoneRecord(AgvZoneRecord agvZoneRecord)
    {
        agvZoneRecord.setCreateTime(DateUtil.getNowDateTimeString());
        return agvZoneRecordMapper.insertAgvZoneRecord(agvZoneRecord);
    }

    /**
     * 修改交管日志
     *
     * @param agvZoneRecord 交管日志
     * @return 结果
     */
    @Override
    public int updateAgvZoneRecord(AgvZoneRecord agvZoneRecord)
    {
        return agvZoneRecordMapper.updateAgvZoneRecord(agvZoneRecord);
    }

    /**
     * 批量删除交管日志
     * 
     * @param ids 需要删除的交管日志主键
     * @return 结果
     */
    @Override
    public int deleteAgvZoneRecordByIds(Long[] ids)
    {
        return agvZoneRecordMapper.deleteAgvZoneRecordByIds(ids);
    }

    /**
     * 删除交管日志信息
     * 
     * @param id 交管日志主键
     * @return 结果
     */
    @Override
    public int deleteAgvZoneRecordById(Long id)
    {
        return agvZoneRecordMapper.deleteAgvZoneRecordById(id);
    }
}
