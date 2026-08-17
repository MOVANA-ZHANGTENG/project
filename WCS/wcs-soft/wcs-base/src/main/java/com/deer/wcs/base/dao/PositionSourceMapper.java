package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.PositionSource;
import com.deer.wcs.base.model.PositionSourceDto;
import com.deer.wcs.base.model.PositionSourceCriteria;

/**
 * 站台资源Mapper接口
 * 
 * @author deer
 * @date 2024-07-09
 */
public interface PositionSourceMapper  extends Mapper<PositionSource>
{
    /**
     * 查询站台资源
     *
     * @param id 站台资源主键
     * @return 站台资源
     */
    public PositionSource selectPositionSourceById(Long id);

    /**
     * 查询站台资源列表
     * 
     * @param positionSource 站台资源
     * @return 站台资源集合
     */
    public List<PositionSourceDto> findList(PositionSourceCriteria criteria);

    /**
     * 新增站台资源
     *
     * @param positionSource 站台资源
     * @return 结果
     */
    public int insertPositionSource(PositionSource positionSource);

    /**
     * 修改站台资源
     *
     * @param positionSource 站台资源
     * @return 结果
     */
    public int updatePositionSource(PositionSource positionSource);

    /**
     * 删除站台资源
     * 
     * @param id 站台资源主键
     * @return 结果
     */
    public int deletePositionSourceById(Long id);

    /**
     * 批量删除站台资源
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePositionSourceByIds(Long[] ids);
}
