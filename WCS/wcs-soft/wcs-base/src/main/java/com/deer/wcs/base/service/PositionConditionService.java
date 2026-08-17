package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.PositionCondition;
import com.deer.wcs.base.model.PositionConditionDto;
import com.deer.wcs.base.model.PositionConditionCriteria;

/**
 * 路径Service接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface PositionConditionService   extends Service<PositionCondition, Long>
{
    /**
     * 查询路径
     *
     * @param id 路径主键
     * @return 路径
     */
    public PositionCondition selectPositionConditionById(Long id);

    /**
     * 查询路径列表
     * 
     * @param criteria
     * @return 路径集合
     */
    public List<PositionConditionDto> findList(PositionConditionCriteria criteria);

    /**
     * 新增路径
     *
     * @param positionCondition 路径
     * @return 结果
     */
    public int insertPositionCondition(PositionCondition positionCondition);

    /**
     * 修改路径
     *
     * @param positionCondition 路径
     * @return 结果
     */
    public int updatePositionCondition(PositionCondition positionCondition);

    /**
     * 批量删除路径
     * 
     * @param ids 需要删除的路径主键集合
     * @return 结果
     */
    public int deletePositionConditionByIds(Long[] ids);

    /**
     * 删除路径信息
     * 
     * @param id 路径主键
     * @return 结果
     */
    public int deletePositionConditionById(Long id);
}
