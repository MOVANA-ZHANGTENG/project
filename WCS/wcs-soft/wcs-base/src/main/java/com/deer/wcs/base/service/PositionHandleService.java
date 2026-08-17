package com.deer.wcs.base.service;

import com.deer.wcs.base.model.PositionHandle;
import com.deer.wcs.base.model.PositionHandleCriteria;
import com.deer.wcs.base.model.PositionHandleDto;
import com.deer.wcs.common.core.service.Service;

import java.util.List;

/**
 * 路径执行器Service接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface PositionHandleService extends Service<PositionHandle, Long>
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
     * @param criteria
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
     * @param pisitionHandle 路径执行器
     * @return 结果
     */
    public int updatePositionHandle(PositionHandle positionHandle);

    /**
     * 批量删除路径执行器
     * 
     * @param ids 需要删除的路径执行器主键集合
     * @return 结果
     */
    public int deletePositionHandleByIds(Long[] ids);

    /**
     * 删除路径执行器信息
     * 
     * @param id 路径执行器主键
     * @return 结果
     */
    public int deletePositionHandleById(Long id);

    void deleteByStepCode(String code);
}
