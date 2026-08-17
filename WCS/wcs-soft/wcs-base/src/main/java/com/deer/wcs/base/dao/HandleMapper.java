package com.deer.wcs.base.dao;

import com.deer.wcs.base.model.Handle;
import com.deer.wcs.base.model.HandleCriteria;
import com.deer.wcs.base.model.HandleDto;
import com.deer.wcs.common.core.mapper.Mapper;

import java.util.List;

/**
 * 执行器Mapper接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface HandleMapper  extends Mapper<Handle>
{
    /**
     * 查询执行器
     *
     * @param id 执行器主键
     * @return 执行器
     */
    public Handle selectHandleById(Long id);

    /**
     * 查询执行器列表
     * 
     * @param handle 执行器
     * @return 执行器集合
     */
    public List<HandleDto> findList(HandleCriteria criteria);

    /**
     * 新增执行器
     *
     * @param handle 执行器
     * @return 结果
     */
    public int insertHandle(Handle handle);

    /**
     * 修改执行器
     *
     * @param handle 执行器
     * @return 结果
     */
    public int updateHandle(Handle handle);

    /**
     * 删除执行器
     * 
     * @param id 执行器主键
     * @return 结果
     */
    public int deleteHandleById(Long id);

    /**
     * 批量删除执行器
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteHandleByIds(Long[] ids);

    List<HandleDto> findHandleByType(HandleCriteria criteria);
}
