package com.deer.wcs.base.dao;

import com.deer.wcs.base.model.PositionHandle;
import com.deer.wcs.base.model.PositionHandleCriteria;
import com.deer.wcs.base.model.PositionHandleDto;
import com.deer.wcs.common.core.mapper.Mapper;

import java.util.List;

/**
 * 路径执行器Mapper接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface PositionHandleMapper extends Mapper<PositionHandle>
{
    /**
     * 查询路径执行器
     *
     * @param id 路径执行器主键
     * @return 路径执行器
     */
    public PositionHandle selectPositionHandleById(Long id);

    /**
     * 查询路径执行器列表
     * 
     * @param  criteria 路径执行器
     * @return 路径执行器集合
     */
    public List<PositionHandleDto> findList(PositionHandleCriteria criteria);

    /**
     * 新增路径执行器
     *
     * @param pisitionHandle 路径执行器
     * @return 结果
     */
    public int insertPositionHandle(PositionHandle positionHandle);

    /**
     * 修改路径执行器
     *
     * @param positionHandle 路径执行器
     * @return 结果
     */
    public int updatePositionHandle(PositionHandle positionHandle);

    /**
     * 删除路径执行器
     * 
     * @param id 路径执行器主键
     * @return 结果
     */
    public int deletePositionHandleById(Long id);

    /**
     * 批量删除路径执行器
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePositionHandleByIds(Long[] ids);

    void deleteByStepCode(String code);
}
