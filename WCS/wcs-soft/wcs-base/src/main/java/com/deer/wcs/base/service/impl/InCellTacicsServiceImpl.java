package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.InCellTacicsMapper;
import com.deer.wcs.base.model.InCellTacics;
import com.deer.wcs.base.model.InCellTacicsDto;
import com.deer.wcs.base.model.InCellTacicsCriteria;
import com.deer.wcs.base.service.InCellTacicsService;

/**
 * 策略配置Service业务层处理
 * 
 * @author deer
 * @date 2024-09-09
 */
@Service
public class InCellTacicsServiceImpl  extends AbstractService<InCellTacics, Long>  implements InCellTacicsService
{
    @Autowired
    private InCellTacicsMapper inCellTacicsMapper;

    /**
     * 查询策略配置
     *
     * @param id 策略配置主键
     * @return 策略配置
     */
    @Override
    public InCellTacics selectInCellTacicsById(Long id)
    {
        return inCellTacicsMapper.selectInCellTacicsById(id);
    }

    /**
     * 查询策略配置列表
     * 
     * @param criteria
     * @return 策略配置
     */
    @Override
    public List<InCellTacicsDto> findList(InCellTacicsCriteria criteria)
    {
        return inCellTacicsMapper.findList(criteria);
    }

    /**
     * 新增策略配置
     *
     * @param inCellTacics 策略配置
     * @return 结果
     */
    @Override
    public int insertInCellTacics(InCellTacics inCellTacics)
    {
        return inCellTacicsMapper.insertInCellTacics(inCellTacics);
    }

    /**
     * 修改策略配置
     *
     * @param inCellTacics 策略配置
     * @return 结果
     */
    @Override
    public int updateInCellTacics(InCellTacics inCellTacics)
    {
        return inCellTacicsMapper.updateInCellTacics(inCellTacics);
    }

    /**
     * 批量删除策略配置
     * 
     * @param ids 需要删除的策略配置主键
     * @return 结果
     */
    @Override
    public int deleteInCellTacicsByIds(Long[] ids)
    {
        return inCellTacicsMapper.deleteInCellTacicsByIds(ids);
    }

    /**
     * 删除策略配置信息
     * 
     * @param id 策略配置主键
     * @return 结果
     */
    @Override
    public int deleteInCellTacicsById(Long id)
    {
        return inCellTacicsMapper.deleteInCellTacicsById(id);
    }
}
