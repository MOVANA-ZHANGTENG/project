package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.PositionStep;
import com.deer.wcs.base.model.PositionStepDto;
import com.deer.wcs.base.model.PositionStepCriteria;

/**
 * 流程Service接口
 * 
 * @author deer
 * @date 2024-07-01
 */
public interface PositionStepService   extends Service<PositionStep, Long>
{
    /**
     * 查询流程
     *
     * @param id 流程主键
     * @return 流程
     */
    public PositionStep selectPositionStepById(Long id);

    /**
     * 查询流程列表
     * 
     * @param criteria
     * @return 流程集合
     */
    public List<PositionStepDto> findList(PositionStepCriteria criteria);

    /**
     * 新增流程
     *
     * @param positionStep 流程
     * @return 结果
     */
    public int insertPositionStep(PositionStep positionStep);

    /**
     * 修改流程
     *
     * @param positionStep 流程
     * @return 结果
     */
    public int updatePositionStep(PositionStep positionStep);

    /**
     * 批量删除流程
     * 
     * @param ids 需要删除的流程主键集合
     * @return 结果
     */
    public int deletePositionStepByIds(Long[] ids);

    /**
     * 删除流程信息
     * 
     * @param id 流程主键
     * @return 结果
     */
    public int deletePositionStepById(Long id);
}
