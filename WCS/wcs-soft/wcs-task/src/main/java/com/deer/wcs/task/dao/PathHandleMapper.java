package com.deer.wcs.task.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.PathHandle;
import com.deer.wcs.task.model.PathHandleDto;
import com.deer.wcs.task.model.PathHandleCriteria;

/**
 * 路径方法Mapper接口
 * 
 * @author deer
 * @date 2024-05-10
 */
public interface PathHandleMapper  extends Mapper<PathHandle>
{
    /**
     * 查询路径方法
     *
     * @param id 路径方法主键
     * @return 路径方法
     */
    public PathHandle selectPathHandleById(Long id);

    /**
     * 查询路径方法列表
     * 
     * @param pathHandle 路径方法
     * @return 路径方法集合
     */
    public List<PathHandleDto> findList(PathHandleCriteria criteria);

    /**
     * 新增路径方法
     *
     * @param pathHandle 路径方法
     * @return 结果
     */
    public int insertPathHandle(PathHandle pathHandle);

    /**
     * 修改路径方法
     *
     * @param pathHandle 路径方法
     * @return 结果
     */
    public int updatePathHandle(PathHandle pathHandle);

    /**
     * 删除路径方法
     * 
     * @param id 路径方法主键
     * @return 结果
     */
    public int deletePathHandleById(Long id);

    /**
     * 批量删除路径方法
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePathHandleByIds(Long[] ids);
}
