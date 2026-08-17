package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.CacheToAgv;
import com.deer.wcs.base.model.CacheToAgvDto;
import com.deer.wcs.base.model.CacheToAgvCriteria;

/**
 * AGV缓存Service接口
 * 
 * @author deer
 * @date 2025-02-14
 */
public interface CacheToAgvService   extends Service<CacheToAgv, Long>
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
     * @param criteria
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
     * 批量删除AGV缓存
     * 
     * @param ids 需要删除的AGV缓存主键集合
     * @return 结果
     */
    public int deleteCacheToAgvByIds(Integer[] ids);

    /**
     * 删除AGV缓存信息
     * 
     * @param id AGV缓存主键
     * @return 结果
     */
    public int deleteCacheToAgvById(Integer id);

    //根据条件查找
    CacheToAgv findCondition(String agvCode,String itemCode,Long taskId);
}
