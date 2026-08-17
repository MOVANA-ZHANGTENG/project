package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.PositionConditionMapper;
import com.deer.wcs.base.model.PositionCondition;
import com.deer.wcs.base.model.PositionConditionDto;
import com.deer.wcs.base.model.PositionConditionCriteria;
import com.deer.wcs.base.service.PositionConditionService;

/**
 * 路径Service业务层处理
 * 
 * @author deer
 * @date 2024-04-28
 */
@Service
public class PositionConditionServiceImpl  extends AbstractService<PositionCondition, Long>  implements PositionConditionService
{
    @Autowired
    private PositionConditionMapper positionConditionMapper;

    /**
     * 查询路径
     *
     * @param id 路径主键
     * @return 路径
     */
    @Override
    public PositionCondition selectPositionConditionById(Long id)
    {
        return positionConditionMapper.selectPositionConditionById(id);
    }

    /**
     * 查询路径列表
     * 
     * @param criteria
     * @return 路径
     */
    @Override
    public List<PositionConditionDto> findList(PositionConditionCriteria criteria)
    {
        return positionConditionMapper.findList(criteria);
    }

    /**
     * 新增路径
     *
     * @param positionCondition 路径
     * @return 结果
     */
    @Override
    public int insertPositionCondition(PositionCondition positionCondition)
    {
        return positionConditionMapper.insertPositionCondition(positionCondition);
    }

    /**
     * 修改路径
     *
     * @param positionCondition 路径
     * @return 结果
     */
    @Override
    public int updatePositionCondition(PositionCondition positionCondition)
    {
        return positionConditionMapper.updatePositionCondition(positionCondition);
    }

    /**
     * 批量删除路径
     * 
     * @param ids 需要删除的路径主键
     * @return 结果
     */
    @Override
    public int deletePositionConditionByIds(Long[] ids)
    {
        return positionConditionMapper.deletePositionConditionByIds(ids);
    }

    /**
     * 删除路径信息
     * 
     * @param id 路径主键
     * @return 结果
     */
    @Override
    public int deletePositionConditionById(Long id)
    {
        return positionConditionMapper.deletePositionConditionById(id);
    }
}
