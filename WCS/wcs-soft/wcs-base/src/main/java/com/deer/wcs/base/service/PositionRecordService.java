package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.PositionRecord;
import com.deer.wcs.base.model.PositionRecordDto;
import com.deer.wcs.base.model.PositionRecordCriteria;

/**
 * 站台日志Service接口
 * 
 * @author deer
 * @date 2025-04-02
 */
public interface PositionRecordService   extends Service<PositionRecord, Long>
{
    /**
     * 查询站台日志
     *
     * @param id 站台日志主键
     * @return 站台日志
     */
    public PositionRecord selectPositionRecordById(Long id);

    /**
     * 查询站台日志列表
     * 
     * @param criteria
     * @return 站台日志集合
     */
    public List<PositionRecordDto> findList(PositionRecordCriteria criteria);

    /**
     * 新增站台日志
     *
     * @param positionRecord 站台日志
     * @return 结果
     */
    public int insertPositionRecord(PositionRecord positionRecord);

    /**
     * 修改站台日志
     *
     * @param positionRecord 站台日志
     * @return 结果
     */
    public int updatePositionRecord(PositionRecord positionRecord);

    /**
     * 批量删除站台日志
     * 
     * @param ids 需要删除的站台日志主键集合
     * @return 结果
     */
    public int deletePositionRecordByIds(Long[] ids);

    /**
     * 删除站台日志信息
     * 
     * @param id 站台日志主键
     * @return 结果
     */
    public int deletePositionRecordById(Long id);
}
