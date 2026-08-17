package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.CacheToAgvMapper;
import com.deer.wcs.base.model.CacheToAgv;
import com.deer.wcs.base.model.CacheToAgvDto;
import com.deer.wcs.base.model.CacheToAgvCriteria;
import com.deer.wcs.base.service.CacheToAgvService;

/**
 * AGV缓存Service业务层处理
 * 
 * @author deer
 * @date 2025-02-14
 */
@Service
public class CacheToAgvServiceImpl  extends AbstractService<CacheToAgv, Long>  implements CacheToAgvService
{
    @Autowired
    private CacheToAgvMapper cacheToAgvMapper;

    /**
     * 查询AGV缓存
     *
     * @param id AGV缓存主键
     * @return AGV缓存
     */
    @Override
    public CacheToAgv selectCacheToAgvById(Integer id)
    {
        return cacheToAgvMapper.selectCacheToAgvById(id);
    }

    /**
     * 查询AGV缓存列表
     * 
     * @param criteria
     * @return AGV缓存
     */
    @Override
    public List<CacheToAgvDto> findList(CacheToAgvCriteria criteria)
    {
        return cacheToAgvMapper.findList(criteria);
    }

    /**
     * 新增AGV缓存
     *
     * @param cacheToAgv AGV缓存
     * @return 结果
     */
    @Override
    public int insertCacheToAgv(CacheToAgv cacheToAgv)
    {
        return cacheToAgvMapper.insertCacheToAgv(cacheToAgv);
    }

    /**
     * 修改AGV缓存
     *
     * @param cacheToAgv AGV缓存
     * @return 结果
     */
    @Override
    public int updateCacheToAgv(CacheToAgv cacheToAgv)
    {
        return cacheToAgvMapper.updateCacheToAgv(cacheToAgv);
    }

    /**
     * 批量删除AGV缓存
     * 
     * @param ids 需要删除的AGV缓存主键
     * @return 结果
     */
    @Override
    public int deleteCacheToAgvByIds(Integer[] ids)
    {
        return cacheToAgvMapper.deleteCacheToAgvByIds(ids);
    }

    /**
     * 删除AGV缓存信息
     * 
     * @param id AGV缓存主键
     * @return 结果
     */
    @Override
    public int deleteCacheToAgvById(Integer id)
    {
        return cacheToAgvMapper.deleteCacheToAgvById(id);
    }

    @Override
    public CacheToAgv findCondition(String agvCode, String itemCode, Long taskId) {
        return cacheToAgvMapper.findCondition(agvCode,itemCode,taskId);
    }
}
