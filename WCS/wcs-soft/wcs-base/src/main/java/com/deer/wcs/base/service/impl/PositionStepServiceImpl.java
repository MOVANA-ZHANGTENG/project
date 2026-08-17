package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.PositionStepMapper;
import com.deer.wcs.base.model.PositionStep;
import com.deer.wcs.base.model.PositionStepCriteria;
import com.deer.wcs.base.model.PositionStepDto;
import com.deer.wcs.base.service.PositionStepService;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程Service业务层处理
 * 
 * @author deer
 * @date 2024-07-01
 */
@Service
public class PositionStepServiceImpl  extends AbstractService<PositionStep, Long>  implements PositionStepService
{
    @Autowired
    private PositionStepMapper positionStepMapper;

    /**
     * 查询流程
     *
     * @param id 流程主键
     * @return 流程
     */
    @Override
    public PositionStep selectPositionStepById(Long id)
    {
        return positionStepMapper.selectPositionStepById(id);
    }

    /**
     * 查询流程列表
     * 
     * @param criteria
     * @return 流程
     */
    @Override
    public List<PositionStepDto> findList(PositionStepCriteria criteria)
    {
        return positionStepMapper.findList(criteria);
    }

    /**
     * 新增流程
     *
     * @param positionStep 流程
     * @return 结果
     */
    @Override
    public int insertPositionStep(PositionStep positionStep)
    {
        return positionStepMapper.insertPositionStep(positionStep);
    }

    /**
     * 修改流程
     *
     * @param positionStep 流程
     * @return 结果
     */
    @Override
    public int updatePositionStep(PositionStep positionStep)
    {
        return positionStepMapper.updatePositionStep(positionStep);
    }

    /**
     * 批量删除流程
     * 
     * @param ids 需要删除的流程主键
     * @return 结果
     */
    @Override
    public int deletePositionStepByIds(Long[] ids)
    {
        return positionStepMapper.deletePositionStepByIds(ids);
    }

    /**
     * 删除流程信息
     * 
     * @param id 流程主键
     * @return 结果
     */
    @Override
    public int deletePositionStepById(Long id)
    {
        return positionStepMapper.deletePositionStepById(id);
    }
}
