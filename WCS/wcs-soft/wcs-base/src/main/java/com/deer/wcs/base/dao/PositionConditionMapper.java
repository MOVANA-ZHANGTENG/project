package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.PositionCondition;
import com.deer.wcs.base.model.PositionConditionDto;
import com.deer.wcs.base.model.PositionConditionCriteria;

/**
 * 路径Mapper接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface PositionConditionMapper  extends Mapper<PositionCondition>
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
     * @param positionCondition 路径
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
     * 删除路径
     * 
     * @param id 路径主键
     * @return 结果
     */
    public int deletePositionConditionById(Long id);

    /**
     * 批量删除路径
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePositionConditionByIds(Long[] ids);
}
