package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.CellRecord;
import com.deer.wcs.base.model.CellRecordDto;
import com.deer.wcs.base.model.CellRecordCriteria;

/**
 * 库位日志记录Mapper接口
 * 
 * @author deer
 * @date 2025-11-04
 */
public interface CellRecordMapper  extends Mapper<CellRecord>
{
    /**
     * 查询库位日志记录
     *
     * @param id 库位日志记录主键
     * @return 库位日志记录
     */
    public CellRecord selectCellRecordById(Long id);

    /**
     * 查询库位日志记录列表
     * 
     * @param cellRecord 库位日志记录
     * @return 库位日志记录集合
     */
    public List<CellRecordDto> findList(CellRecordCriteria criteria);

    /**
     * 新增库位日志记录
     *
     * @param cellRecord 库位日志记录
     * @return 结果
     */
    public int insertCellRecord(CellRecord cellRecord);

    /**
     * 修改库位日志记录
     *
     * @param cellRecord 库位日志记录
     * @return 结果
     */
    public int updateCellRecord(CellRecord cellRecord);

    /**
     * 删除库位日志记录
     * 
     * @param id 库位日志记录主键
     * @return 结果
     */
    public int deleteCellRecordById(Long id);

    /**
     * 批量删除库位日志记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCellRecordByIds(Long[] ids);
}
