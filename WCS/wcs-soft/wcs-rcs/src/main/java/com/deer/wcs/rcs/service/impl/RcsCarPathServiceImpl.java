package com.deer.wcs.rcs.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.constant.WebSocketCacheConstants;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.model.CellInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.rcs.dao.RcsCarPathMapper;
import com.deer.wcs.rcs.model.RcsCarPath;
import com.deer.wcs.rcs.model.RcsCarPathDto;
import com.deer.wcs.rcs.model.RcsCarPathCriteria;
import com.deer.wcs.rcs.service.RcsCarPathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 车路径Service业务层处理
 * 
 * @author deer
 * @date 2025-10-15
 */
@Service
public class RcsCarPathServiceImpl  extends AbstractService<RcsCarPath, Long>  implements RcsCarPathService
{
    private static final Logger log = LoggerFactory.getLogger(RcsCarPathServiceImpl.class);
    
    @Autowired
    private RcsCarPathMapper rcsCarPathMapper;
    
    @Autowired
    private RedisCache redisCache;
    
    @Autowired
    private CellInfoService cellInfoService;

    /**
     * 查询车路径
     *
     * @param id 车路径主键
     * @return 车路径
     */
    @Override
    public RcsCarPath selectRcsCarPathById(Long id)
    {
        return rcsCarPathMapper.selectRcsCarPathById(id);
    }

    /**
     * 查询车路径列表
     * 
     * @param criteria
     * @return 车路径
     */
    @Override
    public List<RcsCarPathDto> findList(RcsCarPathCriteria criteria)
    {
        return rcsCarPathMapper.findList(criteria);
    }
    @Override
    public List<RcsCarPath > hasAllot(RcsCarPath  criteria)
    {
        return rcsCarPathMapper.hasAllot(criteria);
    }

    /**
     * 新增车路径
     *
     * @param rcsCarPath 车路径
     * @return 结果
     */
    @Override
    public int insertRcsCarPath(RcsCarPath rcsCarPath)
    {
        return rcsCarPathMapper.insertRcsCarPath(rcsCarPath);
    }

    /**
     * 修改车路径
     *
     * @param rcsCarPath 车路径
     * @return 结果
     */
    @Override
    public int updateRcsCarPath(RcsCarPath rcsCarPath)
    {
        return rcsCarPathMapper.updateRcsCarPath(rcsCarPath);
    }

    /**
     * 批量删除车路径
     * 
     * @param ids 需要删除的车路径主键
     * @return 结果
     */
    @Override
    public int deleteRcsCarPathByIds(Long[] ids)
    {
        return rcsCarPathMapper.deleteRcsCarPathByIds(ids);
    }

    /**
     * 删除车路径信息
     * 
     * @param id 车路径主键
     * @return 结果
     */
    @Override
    public int deleteRcsCarPathById(Long id)
    {
        return rcsCarPathMapper.deleteRcsCarPathById(id);
    }
    
    /**
     * 删除jobId不存在于job_info表中的路径记录（清理孤立路径）
     * 
     * @return 删除的记录数
     */
    @Override
    public int deleteOrphanedPaths()
    {
        return rcsCarPathMapper.deleteOrphanedPaths();
    }
    
    // ==================== Redis标志位集成 ====================
    
    /**
     * 重写save方法 - 设置Redis更新标志
     * 继承自AbstractService<RcsCarPath, Long>
     */
    @Override
    public void save(RcsCarPath model) {
        super.save(model);
        setPathUpdateFlag(model);

    }
    
    /**
     * 重写update方法 - 设置Redis更新标志
     * 继承自AbstractService<RcsCarPath, Long>
     */
    @Override
    public int update(RcsCarPath model) {
        int result = super.update(model);
        
        if (result > 0) {
            // 设置Redis更新标志
            setPathUpdateFlag(model);

        }

        return result;
    }
    
    /**
     * 重写deleteById方法 - 设置Redis更新标志
     * 继承自AbstractService<RcsCarPath, Long>
     */
    @Override
    public void deleteById(Long id) {
        // 先查询路径信息，获取cellId用于设置标志
        RcsCarPath pathInfo = this.findById(id);
        
     super.deleteById(id);
        

            // 设置Redis更新标志
            setPathUpdateFlag(pathInfo);


    }
    
    /**
     * 设置路径更新标志到Redis
     * 
     * @param pathInfo 路径信息
     */
    private void setPathUpdateFlag(RcsCarPath pathInfo) {
        if (pathInfo == null || pathInfo.getFromCellId() == null) {
            return;
        }
        
        try {

            
            String wareCode = pathInfo.getWareCode();
            Integer floor = pathInfo.getZ();
            
            if (floor == null) {

                return;
            }
            
            // 构建Redis Key
            String flagKey = WebSocketCacheConstants.buildPathUpdateFlagKey(wareCode, floor);
            
            // 设置标志位，值为当前时间戳
            redisCache.setCacheObject(
                flagKey, 
                System.currentTimeMillis(), 
                (int) WebSocketCacheConstants.FLAG_EXPIRE_SECONDS, 
                TimeUnit.SECONDS
            );
            
            log.debug("✓ 设置路径更新标志: key={}, wareCode={}, floor={}", 
                     flagKey, wareCode, floor);
            
        } catch (Exception e) {
            // Redis操作失败不影响主业务
            log.error("设置路径更新标志失败", e);
        }
    }
}
