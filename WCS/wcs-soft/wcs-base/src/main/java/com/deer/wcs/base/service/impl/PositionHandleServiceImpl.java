package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.PositionHandleMapper;
import com.deer.wcs.base.model.PositionHandle;
import com.deer.wcs.base.model.PositionHandleCriteria;
import com.deer.wcs.base.model.PositionHandleDto;
import com.deer.wcs.base.service.PositionHandleService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 路径执行器Service业务层处理
 * 
 * @author deer
 * @date 2024-04-28
 */
@Service
public class PositionHandleServiceImpl extends AbstractService<PositionHandle, Long>  implements PositionHandleService
{
    @Autowired
    private PositionHandleMapper positionHandleMapper;

    /**
     * 查询路径执行器
     *
     * @param id 路径执行器主键
     * @return 路径执行器
     */
    @Override
    public PositionHandle selectPositionHandleById(Long id)
    {
        return positionHandleMapper.selectPositionHandleById(id);
    }

    /**
     * 查询路径执行器列表
     * 
     * @param criteria
     * @return 路径执行器
     */
    @Override
    public List<PositionHandleDto> findList(PositionHandleCriteria criteria)
    {
        return positionHandleMapper.findList(criteria);
    }

    /**
     * 新增路径执行器
     *
     * @param positionHandle 路径执行器
     * @return 结果
     */
    @Override
    public int insertPositionHandle(PositionHandle positionHandle)
    {
        positionHandle.setCreateTime(DateUtil.getNowDateTimeString());
        return positionHandleMapper.insertPositionHandle(positionHandle);
    }

    /**
     * 修改路径执行器
     *
     * @param positionHandle 路径执行器
     * @return 结果
     */
    @Override
    public int updatePositionHandle(PositionHandle positionHandle)
    {
        positionHandle.setUpdateTime(DateUtil.getNowDateTimeString());
        return positionHandleMapper.updatePositionHandle(positionHandle);
    }

    /**
     * 批量删除路径执行器
     * 
     * @param ids 需要删除的路径执行器主键
     * @return 结果
     */
    @Override
    public int deletePositionHandleByIds(Long[] ids)
    {
        return positionHandleMapper.deletePositionHandleByIds(ids);
    }

    /**
     * 删除路径执行器信息
     * 
     * @param id 路径执行器主键
     * @return 结果
     */
    @Override
    public int deletePositionHandleById(Long id)
    {
        return positionHandleMapper.deletePositionHandleById(id);
    }

    @Override
    public void deleteByStepCode(String code) {
        positionHandleMapper.deleteByStepCode(code);
    }
}
