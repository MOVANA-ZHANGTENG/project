package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.CellRecordMapper;
import com.deer.wcs.base.model.CellRecord;
import com.deer.wcs.base.model.CellRecordDto;
import com.deer.wcs.base.model.CellRecordCriteria;
import com.deer.wcs.base.service.CellRecordService;

/**
 * 库位日志记录Service业务层处理
 * 
 * @author deer
 * @date 2025-11-04
 */
@Service
public class CellRecordServiceImpl  extends AbstractService<CellRecord, Long>  implements CellRecordService
{
    @Autowired
    private CellRecordMapper cellRecordMapper;

    /**
     * 查询库位日志记录
     *
     * @param id 库位日志记录主键
     * @return 库位日志记录
     */
    @Override
    public CellRecord selectCellRecordById(Long id)
    {
        return cellRecordMapper.selectCellRecordById(id);
    }

    /**
     * 查询库位日志记录列表
     * 
     * @param criteria
     * @return 库位日志记录
     */
    @Override
    public List<CellRecordDto> findList(CellRecordCriteria criteria)
    {
        return cellRecordMapper.findList(criteria);
    }

    /**
     * 新增库位日志记录
     *
     * @param cellRecord 库位日志记录
     * @return 结果
     */
    @Override
    public int insertCellRecord(CellRecord cellRecord)
    {
        cellRecord.setCreateTime(DateUtil.getNowDateTimeString());
        return cellRecordMapper.insertCellRecord(cellRecord);
    }

    /**
     * 修改库位日志记录
     *
     * @param cellRecord 库位日志记录
     * @return 结果
     */
    @Override
    public int updateCellRecord(CellRecord cellRecord)
    {
        return cellRecordMapper.updateCellRecord(cellRecord);
    }

    /**
     * 批量删除库位日志记录
     * 
     * @param ids 需要删除的库位日志记录主键
     * @return 结果
     */
    @Override
    public int deleteCellRecordByIds(Long[] ids)
    {
        return cellRecordMapper.deleteCellRecordByIds(ids);
    }

    /**
     * 删除库位日志记录信息
     * 
     * @param id 库位日志记录主键
     * @return 结果
     */
    @Override
    public int deleteCellRecordById(Long id)
    {
        return cellRecordMapper.deleteCellRecordById(id);
    }
}
