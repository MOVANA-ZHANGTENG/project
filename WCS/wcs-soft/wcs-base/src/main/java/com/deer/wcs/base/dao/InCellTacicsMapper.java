package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.InCellTacics;
import com.deer.wcs.base.model.InCellTacicsDto;
import com.deer.wcs.base.model.InCellTacicsCriteria;

/**
 * 策略配置Mapper接口
 * 
 * @author deer
 * @date 2024-09-09
 */
public interface InCellTacicsMapper  extends Mapper<InCellTacics>
{
    /**
     * 查询策略配置
     *
     * @param id 策略配置主键
     * @return 策略配置
     */
    public InCellTacics selectInCellTacicsById(Long id);

    /**
     * 查询策略配置列表
     * 
     * @param inCellTacics 策略配置
     * @return 策略配置集合
     */
    public List<InCellTacicsDto> findList(InCellTacicsCriteria criteria);

    /**
     * 新增策略配置
     *
     * @param inCellTacics 策略配置
     * @return 结果
     */
    public int insertInCellTacics(InCellTacics inCellTacics);

    /**
     * 修改策略配置
     *
     * @param inCellTacics 策略配置
     * @return 结果
     */
    public int updateInCellTacics(InCellTacics inCellTacics);

    /**
     * 删除策略配置
     * 
     * @param id 策略配置主键
     * @return 结果
     */
    public int deleteInCellTacicsById(Long id);

    /**
     * 批量删除策略配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteInCellTacicsByIds(Long[] ids);
}
