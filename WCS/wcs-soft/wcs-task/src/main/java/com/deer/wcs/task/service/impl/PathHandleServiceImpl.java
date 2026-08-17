package com.deer.wcs.task.service.impl;

import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.task.dao.PathHandleMapper;
import com.deer.wcs.task.model.PathHandle;
import com.deer.wcs.task.model.PathHandleCriteria;
import com.deer.wcs.task.model.PathHandleDto;
import com.deer.wcs.task.service.PathHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 路径方法Service业务层处理
 * 
 * @author deer
 * @date 2024-05-10
 */
@Service
public class PathHandleServiceImpl  extends AbstractService<PathHandle, Long>  implements PathHandleService
{
    @Autowired
    private PathHandleMapper pathHandleMapper;

    @Autowired
    private AutoService autoService;

    @Override
    public void save(PathHandle model) {
        model.setCreateTime(DateUtil.getNowDateTimeString());
        model.setIsDelete(0);
        model.setVersion(0);
        model.setState(0);
        model.setId(autoService.getPathHandleId());
        super.save(model);
    }

    /**
     * 查询路径方法
     *
     * @param id 路径方法主键
     * @return 路径方法
     */
    @Override
    public PathHandle selectPathHandleById(Long id)
    {
        return pathHandleMapper.selectPathHandleById(id);
    }

    /**
     * 查询路径方法列表
     * 
     * @param criteria
     * @return 路径方法
     */
    @Override
    public List<PathHandleDto> findList(PathHandleCriteria criteria)
    {
        return pathHandleMapper.findList(criteria);
    }

    /**
     * 新增路径方法
     *
     * @param pathHandle 路径方法
     * @return 结果
     */
    @Override
    public int insertPathHandle(PathHandle pathHandle)
    {
        pathHandle.setCreateTime(DateUtil.getNowDateTimeString());
        return pathHandleMapper.insertPathHandle(pathHandle);
    }

    /**
     * 修改路径方法
     *
     * @param pathHandle 路径方法
     * @return 结果
     */
    @Override
    public int updatePathHandle(PathHandle pathHandle)
    {
        pathHandle.setUpdateTime(DateUtil.getNowDateTimeString());
        return pathHandleMapper.updatePathHandle(pathHandle);
    }

    /**
     * 批量删除路径方法
     * 
     * @param ids 需要删除的路径方法主键
     * @return 结果
     */
    @Override
    public int deletePathHandleByIds(Long[] ids)
    {
        return pathHandleMapper.deletePathHandleByIds(ids);
    }

    /**
     * 删除路径方法信息
     * 
     * @param id 路径方法主键
     * @return 结果
     */
    @Override
    public int deletePathHandleById(Long id)
    {
        return pathHandleMapper.deletePathHandleById(id);
    }
}
