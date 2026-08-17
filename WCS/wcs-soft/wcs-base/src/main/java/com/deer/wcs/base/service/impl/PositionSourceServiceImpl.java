package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.PositionSourceMapper;
import com.deer.wcs.base.model.PositionSource;
import com.deer.wcs.base.model.PositionSourceCriteria;
import com.deer.wcs.base.model.PositionSourceDto;
import com.deer.wcs.base.service.PositionSourceService;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 站台资源Service业务层处理
 * 
 * @author deer
 * @date 2024-07-09
 */
@Service
public class PositionSourceServiceImpl  extends AbstractService<PositionSource, Long>  implements PositionSourceService
{
    @Autowired
    private PositionSourceMapper positionSourceMapper;

    /**
     * 查询站台资源
     *
     * @param id 站台资源主键
     * @return 站台资源
     */
    @Override
    public PositionSource selectPositionSourceById(Long id)
    {
        return positionSourceMapper.selectPositionSourceById(id);
    }

    /**
     * 查询站台资源列表
     * 
     * @param criteria
     * @return 站台资源
     */
    @Override
    public List<PositionSourceDto> findList(PositionSourceCriteria criteria)
    {
        return positionSourceMapper.findList(criteria);
    }

    /**
     * 新增站台资源
     *
     * @param positionSource 站台资源
     * @return 结果
     */
    @Override
    public int insertPositionSource(PositionSource positionSource)
    {
        return positionSourceMapper.insertPositionSource(positionSource);
    }

    /**
     * 修改站台资源
     *
     * @param positionSource 站台资源
     * @return 结果
     */
    @Override
    public int updatePositionSource(PositionSource positionSource)
    {
        return positionSourceMapper.updatePositionSource(positionSource);
    }

    /**
     * 批量删除站台资源
     * 
     * @param ids 需要删除的站台资源主键
     * @return 结果
     */
    @Override
    public int deletePositionSourceByIds(Long[] ids)
    {
        return positionSourceMapper.deletePositionSourceByIds(ids);
    }

    /**
     * 删除站台资源信息
     * 
     * @param id 站台资源主键
     * @return 结果
     */
    @Override
    public int deletePositionSourceById(Long id)
    {
        return positionSourceMapper.deletePositionSourceById(id);
    }
}
