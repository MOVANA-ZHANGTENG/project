package com.deer.wcs.rcs.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.constant.WebSocketCacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.rcs.dao.RcsCarInfoMapper;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.model.RcsCarInfoDto;
import com.deer.wcs.rcs.model.RcsCarInfoCriteria;
import com.deer.wcs.rcs.service.RcsCarInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 四向车/AGVService业务层处理
 * 
 * @author deer
 * @date 2025-10-14
 */
@Service
public class RcsCarInfoServiceImpl  extends AbstractService<RcsCarInfo, Long>  implements RcsCarInfoService
{
    private static final Logger log = LoggerFactory.getLogger(RcsCarInfoServiceImpl.class);
    
    @Autowired
    private RcsCarInfoMapper rcsCarInfoMapper;
    
    @Autowired
    private RedisCache redisCache;

    /**
     * 查询四向车/AGV
     *
     * @param id 四向车/AGV主键
     * @return 四向车/AGV
     */
    @Override
    public RcsCarInfo selectRcsCarInfoById(Long id)
    {
        return rcsCarInfoMapper.selectRcsCarInfoById(id);
    }

    /**
     * 查询四向车/AGV列表
     * 
     * @param criteria
     * @return 四向车/AGV
     */
    @Override
    public List<RcsCarInfoDto> findList(RcsCarInfoCriteria criteria)
    {
        return rcsCarInfoMapper.findList(criteria);
    }

    /**
     * 新增四向车/AGV
     *
     * @param rcsCarInfo 四向车/AGV
     * @return 结果
     */
    @Override
    public int insertRcsCarInfo(RcsCarInfo rcsCarInfo)
    {
        rcsCarInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return rcsCarInfoMapper.insertRcsCarInfo(rcsCarInfo);
    }

    /**
     * 修改四向车/AGV
     *
     * @param rcsCarInfo 四向车/AGV
     * @return 结果
     */
    @Override
    public int updateRcsCarInfo(RcsCarInfo rcsCarInfo)
    {
        rcsCarInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        return rcsCarInfoMapper.updateRcsCarInfo(rcsCarInfo);
    }

    /**
     * 批量删除四向车/AGV
     * 
     * @param ids 需要删除的四向车/AGV主键
     * @return 结果
     */
    @Override
    public int deleteRcsCarInfoByIds(Long[] ids)
    {
        return rcsCarInfoMapper.deleteRcsCarInfoByIds(ids);
    }

    /**
     * 删除四向车/AGV信息
     * 
     * @param id 四向车/AGV主键
     * @return 结果
     */
    @Override
    public int deleteRcsCarInfoById(Long id)
    {
        return rcsCarInfoMapper.deleteRcsCarInfoById(id);
    }
    
    // ==================== Redis标志位集成 ====================
    
    /**
     * 重写save方法 - 设置Redis更新标志
     * 继承自AbstractService<RcsCarInfo, Long>
     */
    @Override
    public void save(RcsCarInfo model) {
        super.save(model);
        

            // 设置Redis更新标志
            setCarUpdateFlag(model);
            log.debug("小车新增成功，设置Redis更新标志: carId={}, wareCode={}", 
                     model.getId(), model.getWareCode());

    }
    
    /**
     * 重写update方法 - 设置Redis更新标志
     * 继承自AbstractService<RcsCarInfo, Long>
     */
    @Override
    public int update(RcsCarInfo model) {
        int result = super.update(model);
        
        if (result > 0) {
            // 设置Redis更新标志
            setCarUpdateFlag(model);
            log.debug("小车更新成功，设置Redis更新标志: carId={}, wareCode={}", 
                     model.getId(), model.getWareCode());
        }
        
        return result;
    }
    
    /**
     * 重写deleteById方法 - 设置Redis更新标志
     * 继承自AbstractService<RcsCarInfo, Long>
     */
    @Override
    public void deleteById(Long id) {
        // 先查询小车信息，获取wareCode用于设置标志
        RcsCarInfo carInfo = this.findById(id);
        
        super.deleteById(id);
        

            // 设置Redis更新标志
            setCarUpdateFlag(carInfo);
            log.debug("小车删除成功，设置Redis更新标志: carId={}, wareCode={}", 
                     id, carInfo.getWareCode());

    }
    
    /**
     * 设置小车更新标志到Redis
     * 
     * @param carInfo 小车信息
     */
    private void setCarUpdateFlag(RcsCarInfo carInfo) {
        if (carInfo == null || carInfo.getWareCode() == null) {
            return;
        }
        
        try {
            // 构建Redis Key（按仓库和楼层分组）
            // 注意：如果carInfo没有z字段，可能需要通过fromCellCode关联查询
            // 这里假设有currentZ字段，如果没有需要调整
            Integer floor = carInfo.getCurrentZ();
            
            if (floor == null) {
                // 如果没有楼层信息，使用通配符标记所有楼层都需要更新
                // 推送任务会遍历所有活跃的仓库楼层组合
                floor = -1;  // -1表示所有楼层
            }
            
            String flagKey = WebSocketCacheConstants.buildCarUpdateFlagKey(
                carInfo.getWareCode(), 
                floor
            );
            
            // 设置标志位，值为当前时间戳
            redisCache.setCacheObject(
                flagKey, 
                System.currentTimeMillis(), 
                (int) WebSocketCacheConstants.FLAG_EXPIRE_SECONDS, 
                TimeUnit.SECONDS
            );
            
            log.info("✓ 设置小车更新标志: key={}, wareCode={}, floor={}", 
                    flagKey, carInfo.getWareCode(), floor);
            
        } catch (Exception e) {
            // Redis操作失败不影响主业务
            log.error("设置小车更新标志失败", e);
        }
    }
    
    /**
     * 更新小车遥测数据（坐标、电量、速度等），并推送 WebSocket
     * 
     * @param rcsCarInfo 四向车/AGV（只更新遥测相关字段）
     * @return 结果
     */
    @Override
    public int updateCarTelemetryData(RcsCarInfo rcsCarInfo) {
        try {
            // 1、执行数据库更新（只更新遥测字段）
            int result = rcsCarInfoMapper.updateCarTelemetryData(rcsCarInfo);
            
            if (result > 0) {
                log.debug("遥测数据更新成功，carId: {}, code: {}", rcsCarInfo.getId(), rcsCarInfo.getCode());
                
                // 2、查询完整的小车信息（用于 WebSocket 推送）
                RcsCarInfo fullCarInfo = this.findById(rcsCarInfo.getId());
                
                if (fullCarInfo != null) {
                    // 3、推送 WebSocket 通知前端
                    try {
                        // 使用 MonitorWebSocketHandler 推送（新版）
                        com.deer.wcs.rcs.websocket.MonitorWebSocketHandler.pushCarPosition(fullCarInfo);
                        log.info("✓ WebSocket 推送成功，carCode: {}, from: {}, to: {}, ratio: {}", 
                                fullCarInfo.getCode(), 
                                fullCarInfo.getFromCellCode(), 
                                fullCarInfo.getToCellCode(), 
                                fullCarInfo.getPositionRatio());
                    } catch (Exception e) {
                        // WebSocket 推送失败不影响主流程
                        log.warn("WebSocket 推送失败，carCode: {}, error: {}", fullCarInfo.getCode(), e.getMessage());
                    }
                } else {
                    log.warn("未查询到完整小车信息，无法推送 WebSocket，carId: {}", rcsCarInfo.getId());
                }
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("更新遥测数据失败，carId: {}", rcsCarInfo.getId(), e);
            throw e;
        }
    }
    
    /**
     * 更新小车位置（静止状态）
     * 同时更新fromCellCode和toCellCode为相同值，表示小车静止在该库位
     * 
     * @param carId 小车ID
     * @param carCode 小车编码
     * @param fromCellCode 新的库位编码（起点和终点都会设为此值）
     * @return 是否成功
     */
    @Override
    public boolean updateFromCellCode(Long carId, String carCode, String fromCellCode) {
        try {
            // 1. 查询小车信息
            RcsCarInfo carInfo = null;
            if (carId != null) {
                carInfo = this.findById(carId);
            } else if (carCode != null && !carCode.trim().isEmpty()) {
                // 通过code查询
                RcsCarInfoCriteria criteria = new RcsCarInfoCriteria();
                criteria.setCode(carCode);
                List<RcsCarInfoDto> list = this.findList(criteria);
                if (list != null && !list.isEmpty()) {
                    carInfo = this.findById(list.get(0).getId());
                }
            }
            
            if (carInfo == null) {
                log.warn("未找到小车信息: carId={}, carCode={}", carId, carCode);
                return false;
            }
            
            // 记录修改前的信息
            String oldFromCellCode = carInfo.getFromCellCode();
            String oldToCellCode = carInfo.getToCellCode();
            log.info("准备修改小车位置: carId={}, carCode={}, 旧位置=[{}->{}], 新位置={}", 
                    carInfo.getId(), carInfo.getCode(), oldFromCellCode, oldToCellCode, fromCellCode);
            
            // 2. 更新fromCellCode和toCellCode（保持相等，表示小车静止在该位置）
            carInfo.setFromCellCode(fromCellCode);
            carInfo.setToCellCode(fromCellCode);  // 同时更新toCellCode，保持相等
            carInfo.setPositionRatio(BigDecimal.valueOf(0.0));  // 位置比例设为0，表示在起点
            carInfo.setUpdateTime(DateUtil.getNowDateTimeString());
            
            // 3. 执行更新
            int result = this.update(carInfo);
            
            if (result > 0) {
                log.info("✓ 小车位置修改成功: carId={}, carCode={}, 旧位置=[{}->{}], 新位置={}", 
                        carInfo.getId(), carInfo.getCode(), oldFromCellCode, oldToCellCode, fromCellCode);
                
                // 4. 推送 WebSocket 通知前端
                try {
                    RcsCarInfo updatedCarInfo = this.findById(carInfo.getId());
                    if (updatedCarInfo != null) {
                        com.deer.wcs.rcs.websocket.MonitorWebSocketHandler.pushCarPosition(updatedCarInfo);
                        log.info("✓ WebSocket 推送成功，通知前端小车位置已更新: carCode={}", updatedCarInfo.getCode());
                    }
                } catch (Exception e) {
                    // WebSocket 推送失败不影响主流程
                    log.warn("WebSocket 推送失败，carCode: {}, error: {}", carInfo.getCode(), e.getMessage());
                }
                
                return true;
            } else {
                log.warn("小车起点位置修改失败: carId={}, carCode={}", carInfo.getId(), carInfo.getCode());
                return false;
            }
            
        } catch (Exception e) {
            log.error("更新小车起点位置异常: carId={}, carCode={}, fromCellCode={}", 
                    carId, carCode, fromCellCode, e);
            return false;
        }
    }

    @Override
    public List<RcsCarInfo> findCanUse() {
        return rcsCarInfoMapper.findCanUse();
    }

    @Override
    public List<RcsCarInfo> findByWareCode(String wareCode) {
        return rcsCarInfoMapper.findByWareCode(wareCode);
    }

    @Override
    public boolean hasCar(String wareCode, String fromCellCode) {
        return rcsCarInfoMapper.hasCar(wareCode,fromCellCode);
    }
}
