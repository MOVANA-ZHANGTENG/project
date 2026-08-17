package com.deer.wcs.jxg.car_brand_model;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.service.RcsCarInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 小车遥测数据 Redis 管理工具类
 * 统一管理小车遥测数据在 Redis 中的存储和获取
 *
 * @author Deer WCS Team
 * @date 2025-10-28
 */
@Component("ZkCarRedisUtil")
public class ZkCarRedisUtil {

    private static final Logger log = LoggerFactory.getLogger(ZkCarRedisUtil.class);

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private RcsCarInfoService rcsCarInfoService;

    // Redis key 前缀：用于存储小车实时状态
    private static final String REDIS_CAR_STATE_KEY = "wcs:car:state:";

    // Redis 过期时间：5分钟（单位：秒）- 防止状态数据长期占用内存
    private static final int REDIS_CAR_STATE_EXPIRE = 300;

    // 默认坐标容差：50mm
    private static final int DEFAULT_COORDINATE_TOLERANCE = 50;

    /**
     * 保存小车遥测数据到 Redis
     *
     * @param robotId   机器人ID（小车编码）
     * @param stateData 遥测状态数据
     * @return true-保存成功，false-保存失败
     */
    public boolean saveCarState(Integer robotId, Map<String, Object> stateData) {
        if (robotId == null) {
            log.warn("robotId 为空，无法保存小车遥测数据");
            return false;
        }

        try {
            String redisKey = REDIS_CAR_STATE_KEY + robotId;

            // 添加更新时间戳
            stateData.put("updateTime", System.currentTimeMillis());

            // 保存到 Redis，设置过期时间
            redisCache.setCacheObject(redisKey, stateData, REDIS_CAR_STATE_EXPIRE, TimeUnit.SECONDS);

            log.debug("保存小车遥测数据成功，robotId: {}, key: {}", robotId, redisKey);
            return true;
        } catch (Exception e) {
            log.error("保存小车遥测数据失败，robotId: {}", robotId, e);
            return false;
        }
    }

    /**
     * 保存小车遥测数据到 Redis（通过小车编码）
     *
     * @param carCode   小车编码
     * @param stateData 遥测状态数据
     * @return true-保存成功，false-保存失败
     */
    public boolean saveCarState(String carCode, Map<String, Object> stateData) {
        if (carCode == null || carCode.trim().isEmpty()) {
            log.warn("carCode 为空，无法保存小车遥测数据");
            return false;
        }

        try {
            String redisKey = REDIS_CAR_STATE_KEY + carCode;

            // 添加更新时间戳
            stateData.put("updateTime", System.currentTimeMillis());

            // 保存到 Redis，设置过期时间
            redisCache.setCacheObject(redisKey, stateData, REDIS_CAR_STATE_EXPIRE, TimeUnit.SECONDS);

            log.debug("保存小车遥测数据成功，carCode: {}, key: {}", carCode, redisKey);
            return true;
        } catch (Exception e) {
            log.error("保存小车遥测数据失败，carCode: {}", carCode, e);
            return false;
        }
    }

    /**
     * 获取小车遥测数据
     *
     * @param robotId 机器人ID（小车编码）
     * @return 遥测状态数据，如果不存在则返回null
     */
    public Map<String, Object> getCarState(Integer robotId) {
        if (robotId == null) {
            log.warn("robotId 为空，无法获取小车遥测数据");
            return null;
        }

        try {
            String redisKey = REDIS_CAR_STATE_KEY + robotId;
            Map<String, Object> stateData = redisCache.getCacheObject(redisKey);

            if (stateData != null) {
                log.debug("获取小车遥测数据成功，robotId: {}", robotId);
            } else {
                log.debug("Redis 中无小车遥测数据，robotId: {}", robotId);
            }

            return stateData;
        } catch (Exception e) {
            log.error("获取小车遥测数据失败，robotId: {}", robotId, e);
            return null;
        }
    }

    /**
     * 获取小车遥测数据（通过小车编码）
     *
     * @param carCode 小车编码
     * @return 遥测状态数据，如果不存在则返回null
     */
    public Map<String, Object> getCarState(String carCode) {
        if (carCode == null || carCode.trim().isEmpty()) {
            log.warn("carCode 为空，无法获取小车遥测数据");
            return null;
        }

        try {
            String redisKey = REDIS_CAR_STATE_KEY + carCode;
            Map<String, Object> stateData = redisCache.getCacheObject(redisKey);

            if (stateData != null) {
                log.debug("获取小车遥测数据成功，carCode: {}", carCode);
            } else {
                log.debug("Redis 中无小车遥测数据，carCode: {}", carCode);
            }

            return stateData;
        } catch (Exception e) {
            log.error("获取小车遥测数据失败，carCode: {}", carCode, e);
            return null;
        }
    }

    /**
     * 检查小车遥测数据是否在指定时间内更新过
     *
     * @param carCode       小车编码
     * @param timeoutMillis 超时时间（毫秒），例如 5000 表示5秒
     * @return true-有最近的数据，false-数据超时或不存在
     */
    public boolean hasRecentData(String carCode, long timeoutMillis) {
        if (carCode == null || carCode.trim().isEmpty()) {
            log.warn("carCode 为空，无法检查小车遥测数据");
            return false;
        }

        try {
            Map<String, Object> stateData = getCarState(carCode);

            if (stateData == null || stateData.isEmpty()) {
                log.debug("Redis 中无小车遥测数据，carCode: {}", carCode);
                return false;
            }

            Object updateTimeObj = stateData.get("updateTime");
            if (updateTimeObj == null) {
                log.debug("小车遥测数据缺少更新时间，carCode: {}", carCode);
                return false;
            }

            Long updateTime = updateTimeObj instanceof Long ? (Long) updateTimeObj :
                    Long.parseLong(updateTimeObj.toString());

            long currentTime = System.currentTimeMillis();
            long diff = currentTime - updateTime;

            boolean isRecent = diff <= timeoutMillis;

            log.debug("检查小车遥测数据时效，carCode: {}, 距上次更新: {}ms, 超时阈值: {}ms, 结果: {}",
                    carCode, diff, timeoutMillis, isRecent ? "有效" : "超时");

            return isRecent;
        } catch (Exception e) {
            log.error("检查小车遥测数据时效失败，carCode: {}", carCode, e);
            return false;
        }
    }

    /**
     * 获取小车遥测数据的更新时间
     *
     * @param carCode 小车编码
     * @return 更新时间戳（毫秒），如果不存在则返回null
     */
    public Long getUpdateTime(String carCode) {
        if (carCode == null || carCode.trim().isEmpty()) {
            return null;
        }

        try {
            Map<String, Object> stateData = getCarState(carCode);

            if (stateData == null) {
                return null;
            }

            Object updateTimeObj = stateData.get("updateTime");
            if (updateTimeObj == null) {
                return null;
            }

            return updateTimeObj instanceof Long ? (Long) updateTimeObj :
                    Long.parseLong(updateTimeObj.toString());
        } catch (Exception e) {
            log.error("获取小车遥测数据更新时间失败，carCode: {}", carCode, e);
            return null;
        }
    }

    /**
     * 获取小车遥测数据距今的时间差
     *
     * @param carCode 小车编码
     * @return 时间差（毫秒），如果不存在则返回 -1
     */
    public long getTimeSinceUpdate(String carCode) {
        Long updateTime = getUpdateTime(carCode);

        if (updateTime == null) {
            return -1;
        }

        return System.currentTimeMillis() - updateTime;
    }

    /**
     * 获取所有小车的遥测数据
     *
     * @return 所有小车的遥测数据列表
     */
    public List<Map<String, Object>> getAllCarStates() {
        try {
            List<Map<String, Object>> stateList = new ArrayList<>();
            Collection<String> keys = redisCache.keys(REDIS_CAR_STATE_KEY + "*");

            if (keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    Map<String, Object> stateData = redisCache.getCacheObject(key);
                    if (stateData != null) {
                        stateList.add(stateData);
                    }
                }
            }

            log.debug("获取所有小车遥测数据，总数: {}", stateList.size());
            return stateList;
        } catch (Exception e) {
            log.error("获取所有小车遥测数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 删除小车遥测数据
     *
     * @param carCode 小车编码
     * @return true-删除成功，false-删除失败
     */
    public boolean deleteCarState(String carCode) {
        if (carCode == null || carCode.trim().isEmpty()) {
            log.warn("carCode 为空，无法删除小车遥测数据");
            return false;
        }

        try {
            String redisKey = REDIS_CAR_STATE_KEY + carCode;
            boolean result = redisCache.deleteObject(redisKey);

            if (result) {
                log.info("删除小车遥测数据成功，carCode: {}", carCode);
            } else {
                log.warn("删除小车遥测数据失败（可能不存在），carCode: {}", carCode);
            }

            return result;
        } catch (Exception e) {
            log.error("删除小车遥测数据失败，carCode: {}", carCode, e);
            return false;
        }
    }

    /**
     * 从遥测数据中提取指定字段的值
     *
     * @param carCode   小车编码
     * @param fieldName 字段名
     * @return 字段值，如果不存在则返回null
     */
    public Object getFieldValue(String carCode, String fieldName) {
        Map<String, Object> stateData = getCarState(carCode);

        if (stateData == null) {
            return null;
        }

        return stateData.get(fieldName);
    }

    /**
     * 从遥测数据中提取 Integer 类型的字段值
     *
     * @param carCode   小车编码
     * @param fieldName 字段名
     * @return Integer 值，如果不存在或类型不匹配则返回null
     */
    public Integer getIntValue(String carCode, String fieldName) {
        Object value = getFieldValue(carCode, fieldName);

        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            log.warn("字段值无法转换为 Integer，carCode: {}, fieldName: {}, value: {}",
                    carCode, fieldName, value);
            return null;
        }
    }

    /**
     * 构建小车遥测数据对象（用于保存）
     *
     * @param robotId 机器人ID
     * @param carCode 小车编码
     * @param carId   小车数据库ID
     * @return 遥测数据 Map
     */
    public Map<String, Object> buildCarStateData(Integer robotId, String carCode, Long carId) {
        Map<String, Object> stateData = new HashMap<>();
        stateData.put("robotId", robotId);
        stateData.put("carCode", carCode);
        stateData.put("carId", carId);
        stateData.put("updateTime", System.currentTimeMillis());
        return stateData;
    }

    /**
     * 获取 Redis Key 前缀
     *
     * @return Redis Key 前缀
     */
    public String getRedisKeyPrefix() {
        return REDIS_CAR_STATE_KEY;
    }

    /**
     * 构建完整的 Redis Key
     *
     * @param carCode 小车编码
     * @return 完整的 Redis Key
     */
    public String buildRedisKey(String carCode) {
        return REDIS_CAR_STATE_KEY + carCode;
    }

    /**
     * 验证小车系统位置与遥测位置是否一致
     *
     * @param carInfo       小车信息
     * @param wareCode      仓库编码
     * @param timeoutMillis 遥测数据超时时间（毫秒），例如 3000 表示3秒
     * @return 位置验证结果对象
     */
    public CarPositionVerifyResult verifyCarPosition(RcsCarInfo carInfo, String wareCode, long timeoutMillis) {
        CarPositionVerifyResult result = new CarPositionVerifyResult();
        result.setCarInfo(carInfo);

        if (carInfo == null) {
            result.setSuccess(false);
            result.setErrorMessage("小车信息为空");
            log.warn("验证小车位置失败：小车信息为空");
            return result;
        }

        String carCode = carInfo.getCode();
        if (carCode == null || carCode.trim().isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage("小车编码为空");
            log.warn("验证小车位置失败：小车编码为空，carId: {}", carInfo.getId());
            return result;
        }

        result.setCarCode(carCode);

        try {
            // 1. 检查遥测数据时效性
            boolean hasRecentData = hasRecentData(carCode, timeoutMillis);
            if (!hasRecentData) {
                result.setSuccess(false);
                result.setErrorMessage(String.format("小车[%s]遥测数据超时（阈值%dms）", carCode, timeoutMillis));
                log.warn("小车遥测数据超时，carCode: {}, 阈值: {}ms", carCode, timeoutMillis);
                return result;
            }

            // 2. 获取遥测坐标
            Integer telemetryX = getIntValue(carCode, "x");
            Integer telemetryY = getIntValue(carCode, "y");
            Integer telemetryZ = getIntValue(carCode, "z");

            result.setTelemetryX(telemetryX);
            result.setTelemetryY(telemetryY);
            result.setTelemetryZ(telemetryZ);

            if (telemetryX == null || telemetryY == null || telemetryZ == null) {
                result.setSuccess(false);
                result.setErrorMessage(String.format("小车[%s]遥测坐标数据不完整：x=%s, y=%s, z=%s",
                        carCode, telemetryX, telemetryY, telemetryZ));
                log.warn("小车遥测坐标数据不完整，carCode: {}, x={}, y={}, z={}",
                        carCode, telemetryX, telemetryY, telemetryZ);
                return result;
            }

            // 3. 获取系统记录的位置
            String systemCellCode = carInfo.getFromCellCode();
            result.setSystemCellCode(systemCellCode);

            if (systemCellCode == null || systemCellCode.trim().isEmpty()) {
                result.setSuccess(false);
                result.setErrorMessage(String.format("小车[%s]系统位置为空", carCode));
                log.warn("小车系统位置为空，carCode: {}", carCode);
                return result;
            }

            // 4. 根据遥测坐标查找库位
            CellInfo telemetryCell = findCellByCoordinates(wareCode, telemetryX, telemetryY, telemetryZ);
            result.setTelemetryCell(telemetryCell);

            if (telemetryCell == null) {
                result.setSuccess(false);
                result.setErrorMessage(String.format("小车[%s]遥测坐标(%d,%d,%d)未匹配到库位",
                        carCode, telemetryX, telemetryY, telemetryZ));
                log.warn("遥测坐标未匹配到库位，carCode: {}, 坐标:({},{},{})",
                        carCode, telemetryX, telemetryY, telemetryZ);
                return result;
            }

            result.setTelemetryCellCode(telemetryCell.getCode());

            // 5. 比较位置是否一致
            boolean isConsistent = telemetryCell.getCode().equals(systemCellCode);
            result.setConsistent(isConsistent);
            result.setSuccess(true);

            if (isConsistent) {
                result.setErrorMessage(null);
                log.info("✓ 小车位置一致，carCode: {}, 位置: {}, 坐标:({},{},{})",
                        carCode, systemCellCode, telemetryX, telemetryY, telemetryZ);
            } else {
                result.setErrorMessage(String.format("小车[%s]位置不一致：系统记录[%s]，遥测位置[%s]",
                        carCode, systemCellCode, telemetryCell.getCode()));
                log.warn("小车位置不一致，carCode: {}, 系统记录: {}, 遥测位置: {}, 坐标:({},{},{})",
                        carCode, systemCellCode, telemetryCell.getCode(), telemetryX, telemetryY, telemetryZ);
            }

            return result;

        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(String.format("验证小车[%s]位置异常: %s", carCode, e.getMessage()));
            log.error("验证小车位置异常，carCode: {}", carCode, e);
            return result;
        }
    }

    /**
     * 验证小车位置并自动更新不一致的位置
     *
     * @param carInfo       小车信息
     * @param wareCode      仓库编码
     * @param timeoutMillis 遥测数据超时时间（毫秒）
     * @return 位置验证结果对象
     */
    public CarPositionVerifyResult verifyAndUpdateCarPosition(RcsCarInfo carInfo, String wareCode, long timeoutMillis) {
        CarPositionVerifyResult result = verifyCarPosition(carInfo, wareCode, timeoutMillis);

        // 如果验证成功但位置不一致，自动更新位置
        if (result.isSuccess() && !result.isConsistent() && result.getTelemetryCell() != null) {
            try {
                String newCellCode = result.getTelemetryCell().getCode();
                carInfo.setFromCellCode(newCellCode);
                carInfo.setToCellCode(newCellCode);
                rcsCarInfoService.update(carInfo);

                result.setPositionUpdated(true);
                log.info("已自动更新小车位置，carCode: {}, 旧位置: {}, 新位置: {}",
                        result.getCarCode(), result.getSystemCellCode(), newCellCode);
            } catch (Exception e) {
                result.setPositionUpdated(false);
                log.error("更新小车位置失败，carCode: {}", result.getCarCode(), e);
            }
        }


        // 验证载货状态
        try {
            Integer loadState = getIntValue(carInfo.getCode(), "loadState");
            if(loadState!=carInfo.getLoadState()){
                carInfo.setLoadState(loadState);
                rcsCarInfoService.update(carInfo);
                result.setPositionUpdated(true);
                log.info("已自动更新小车{}载货状态"+loadState);
            }

        } catch (Exception e) {
            result.setPositionUpdated(false);
            log.error("更新小车位置失败，carCode: {}", result.getCarCode(), e);
        }

        return result;
    }

    /**
     * 根据遥测坐标查找对应的库位
     *
     * @param wareCode 仓库编码
     * @param x        遥测X坐标
     * @param y        遥测Y坐标
     * @param z        遥测Z坐标
     * @return 匹配的库位，如果未找到返回null
     */
    private CellInfo findCellByCoordinates(String wareCode, Integer x, Integer y, Integer z) {
        return findCellByCoordinates(wareCode, x, y, z, DEFAULT_COORDINATE_TOLERANCE);
    }

    /**
     * 根据遥测坐标查找对应的库位
     *
     * @param wareCode  仓库编码
     * @param x         遥测X坐标
     * @param y         遥测Y坐标
     * @param z         遥测Z坐标
     * @param tolerance 坐标容差（mm）
     * @return 匹配的库位，如果未找到返回null
     */
    private CellInfo findCellByCoordinates(String wareCode, Integer x, Integer y, Integer z, int tolerance) {
        if (wareCode == null || x == null || y == null || z == null) {
            return null;
        }

        try {
            Condition condition = new Condition(CellInfo.class);
            Condition.Criteria criteria = condition.createCriteria();

            criteria.andEqualTo("wareCode", wareCode);
            criteria.andEqualTo("subZ", z);
            criteria.andBetween("subX", x - tolerance, x + tolerance);
            criteria.andBetween("subY", y - tolerance, y + tolerance);
            criteria.andEqualTo("isDelete", 0);

            List<CellInfo> cellList = cellInfoService.findByCondition(condition);

            if (cellList == null || cellList.isEmpty()) {
                log.debug("未找到匹配坐标的库位，wareCode: {}, 坐标:({},{},{}), 容差: ±{}mm",
                        wareCode, x, y, z, tolerance);
                return null;
            }

            if (cellList.size() > 1) {
                log.warn("根据遥测坐标找到多个匹配的库位，wareCode: {}, 坐标:({},{},{}), 匹配数量: {}",
                        wareCode, x, y, z, cellList.size());
            }

            return cellList.get(0);

        } catch (Exception e) {
            log.error("根据坐标查找库位失败，wareCode: {}, 坐标:({},{},{})", wareCode, x, y, z, e);
            return null;
        }
    }

    /**
     * 小车位置验证结果类
     */
    public static class CarPositionVerifyResult {
        private boolean success;              // 验证是否成功执行
        private boolean consistent;           // 位置是否一致
        private boolean positionUpdated;      // 位置是否已更新
        private String errorMessage;          // 错误信息

        private RcsCarInfo carInfo;           // 小车信息
        private String carCode;               // 小车编码
        private Integer loadState;          //是否载货

        private String systemCellCode;        // 系统记录的位置
        private String telemetryCellCode;     // 遥测对应的位置

        private Integer telemetryX;           // 遥测X坐标
        private Integer telemetryY;           // 遥测Y坐标
        private Integer telemetryZ;           // 遥测Z坐标

        private CellInfo telemetryCell;       // 遥测对应的库位对象

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public boolean isConsistent() {
            return consistent;
        }

        public void setConsistent(boolean consistent) {
            this.consistent = consistent;
        }

        public boolean isPositionUpdated() {
            return positionUpdated;
        }

        public void setPositionUpdated(boolean positionUpdated) {
            this.positionUpdated = positionUpdated;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public RcsCarInfo getCarInfo() {
            return carInfo;
        }

        public void setCarInfo(RcsCarInfo carInfo) {
            this.carInfo = carInfo;
        }

        public String getCarCode() {
            return carCode;
        }

        public void setCarCode(String carCode) {
            this.carCode = carCode;
        }

        public Integer getLoadState() {
            return loadState;
        }

        public void setLoadState(Integer loadState) {
            this.loadState = loadState;
        }

        public String getSystemCellCode() {
            return systemCellCode;
        }

        public void setSystemCellCode(String systemCellCode) {
            this.systemCellCode = systemCellCode;
        }

        public String getTelemetryCellCode() {
            return telemetryCellCode;
        }

        public void setTelemetryCellCode(String telemetryCellCode) {
            this.telemetryCellCode = telemetryCellCode;
        }

        public Integer getTelemetryX() {
            return telemetryX;
        }

        public void setTelemetryX(Integer telemetryX) {
            this.telemetryX = telemetryX;
        }

        public Integer getTelemetryY() {
            return telemetryY;
        }

        public void setTelemetryY(Integer telemetryY) {
            this.telemetryY = telemetryY;
        }

        public Integer getTelemetryZ() {
            return telemetryZ;
        }

        public void setTelemetryZ(Integer telemetryZ) {
            this.telemetryZ = telemetryZ;
        }

        public CellInfo getTelemetryCell() {
            return telemetryCell;
        }

        public void setTelemetryCell(CellInfo telemetryCell) {
            this.telemetryCell = telemetryCell;
        }

        @Override
        public String toString() {
            if (!success) {
                return String.format("验证失败: %s", errorMessage);
            }

            if (consistent) {
                return String.format("位置一致: %s, 坐标:(%d,%d,%d)",
                        systemCellCode, telemetryX, telemetryY, telemetryZ);
            } else {
                return String.format("位置不一致: 系统[%s] vs 遥测[%s], 坐标:(%d,%d,%d)%s",
                        systemCellCode, telemetryCellCode,
                        telemetryX, telemetryY, telemetryZ,
                        positionUpdated ? " [已更新]" : "");
            }
        }
    }

}
