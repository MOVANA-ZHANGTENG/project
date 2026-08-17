package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.PositionRecordMapper;
import com.deer.wcs.base.model.PositionRecord;
import com.deer.wcs.base.model.PositionRecordDto;
import com.deer.wcs.base.model.PositionRecordCriteria;
import com.deer.wcs.base.service.PositionRecordService;

/**
 * 站台日志Service业务层处理
 * 
 * @author deer
 * @date 2025-04-02
 */
@Service
public class PositionRecordServiceImpl  extends AbstractService<PositionRecord, Long>  implements PositionRecordService
{
    @Autowired
    private PositionRecordMapper positionRecordMapper;

    /**
     * 查询站台日志
     *
     * @param id 站台日志主键
     * @return 站台日志
     */
    @Override
    public PositionRecord selectPositionRecordById(Long id)
    {
        return positionRecordMapper.selectPositionRecordById(id);
    }

    /**
     * 查询站台日志列表
     * 
     * @param criteria
     * @return 站台日志
     */
    @Override
    public List<PositionRecordDto> findList(PositionRecordCriteria criteria)
    {
        return positionRecordMapper.findList(criteria);
    }

    /**
     * 新增站台日志
     *
     * @param positionRecord 站台日志
     * @return 结果
     */
    @Override
    public int insertPositionRecord(PositionRecord positionRecord)
    {
        positionRecord.setCreateTime(DateUtil.getNowDateTimeString());
        return positionRecordMapper.insertPositionRecord(positionRecord);
    }

    /**
     * 修改站台日志
     *
     * @param positionRecord 站台日志
     * @return 结果
     */
    @Override
    public int updatePositionRecord(PositionRecord positionRecord)
    {
        return positionRecordMapper.updatePositionRecord(positionRecord);
    }

    /**
     * 批量删除站台日志
     * 
     * @param ids 需要删除的站台日志主键
     * @return 结果
     */
    @Override
    public int deletePositionRecordByIds(Long[] ids)
    {
        return positionRecordMapper.deletePositionRecordByIds(ids);
    }

    /**
     * 删除站台日志信息
     * 
     * @param id 站台日志主键
     * @return 结果
     */
    @Override
    public int deletePositionRecordById(Long id)
    {
        return positionRecordMapper.deletePositionRecordById(id);
    }
}
