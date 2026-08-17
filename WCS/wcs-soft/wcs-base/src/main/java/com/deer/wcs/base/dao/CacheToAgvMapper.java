package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.CacheToAgv;
import com.deer.wcs.base.model.CacheToAgvDto;
import com.deer.wcs.base.model.CacheToAgvCriteria;

/**
 * AGV缓存Mapper接口
 * 
 * @author deer
 * @date 2025-02-14
 */
public interface CacheToAgvMapper  extends Mapper<CacheToAgv>
{
    /**
     * 查询AGV缓存
     *
     * @param id AGV缓存主键
     * @return AGV缓存
     */
    public CacheToAgv selectCacheToAgvById(Integer id);

    /**
     * 查询AGV缓存列表
     * 
     * @param criteria AGV缓存
     * @return AGV缓存集合
     */
    public List<CacheToAgvDto> findList(CacheToAgvCriteria criteria);

    /**
     * 新增AGV缓存
     *
     * @param cacheToAgv AGV缓存
     * @return 结果
     */
    public int insertCacheToAgv(CacheToAgv cacheToAgv);

    /**
     * 修改AGV缓存
     *
     * @param cacheToAgv AGV缓存
     * @return 结果
     */
    public int updateCacheToAgv(CacheToAgv cacheToAgv);

    /**
     * 删除AGV缓存
     * 
     * @param id AGV缓存主键
     * @return 结果
     */
    public int deleteCacheToAgvById(Integer id);

    /**
     * 批量删除AGV缓存
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCacheToAgvByIds(Integer[] ids);

    CacheToAgv findCondition(String agvCode, String itemCode, Long taskId);
}
