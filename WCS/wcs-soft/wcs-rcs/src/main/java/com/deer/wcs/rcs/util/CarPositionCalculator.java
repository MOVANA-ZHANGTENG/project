package com.deer.wcs.rcs.util;

import com.deer.wcs.base.model.CellInfo;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 小车位置计算工具类
 * 用于计算小车在两个库位之间的插值位置
 * 
 * @author Deer WCS Team
 * @date 2024-10-16
 */
public class CarPositionCalculator {
    
    private static final Logger log = LoggerFactory.getLogger(CarPositionCalculator.class);
    
    /**
     * 根据下位地图坐标计算位置插值（两个库位+系数）
     * 
     * @param currentX 小车X坐标(mm，下位地图坐标)
     * @param currentY 小车Y坐标(mm，下位地图坐标)
     * @param currentZ 小车Z坐标(mm，下位地图坐标)
     * @param allCells 所有库位列表
     * @return 位置结果（包含fromCell、toCell、ratio）
     */
    public static CellPositionResult calculateCellPosition(Integer currentX, Integer currentY, 
                                                           Integer currentZ, List<CellInfo> allCells) {
        if (currentX == null || currentY == null) {
            log.warn("小车坐标为空");
            return null;
        }
        
        if (allCells == null || allCells.isEmpty()) {
            log.warn("库位列表为空");
            return null;
        }
        
        // 1. 过滤并计算距离
        List<CellDistanceInfo> distances = new ArrayList<>();
        
      /*  for (CellInfo cell : allCells) {
            // 直接使用库位的下位地图坐标（无需转换）
            Integer cellSubX = cell.getSubX();
            Integer cellSubY = cell.getSubY();
            Integer cellSubZ = cell.getSubZ();

            // 跳过未配置xub坐标的库位
            if (cellSubX == null || cellSubY == null) {
                continue;
            }
            
            // 只比较同一地图层（z坐标接近，容差100mm）
            if (cellSubZ != null && currentZ != null && Math.abs(cellSubZ - currentZ) > 100) {
                continue; // z坐标差距超过100mm，视为不同层
            }
            
            // 计算距离（直接使用mm单位计算）
            double distance = Math.sqrt(
                Math.pow(cellSubX - currentX, 2) + 
                Math.pow(cellSubY - currentY, 2)
            );
            
            distances.add(new CellDistanceInfo(cell, cellSubX, cellSubY, distance));
        }*/
        
        if (distances.size() < 2) {
            log.warn("找到的库位少于2个（已配置xub坐标），无法计算插值");
            return null;
        }
        
        // 2. 按距离排序，取最近的两个库位
        distances.sort(Comparator.comparingDouble(CellDistanceInfo::getDistance));
        
        CellDistanceInfo cell1 = distances.get(0);
        CellDistanceInfo cell2 = distances.get(1);
        
        // 3. 计算插值系数
        BigDecimal ratio = calculateRatio(
            currentX, currentY, 
            cell1.getSubX(), cell1.getSubY(),
            cell2.getSubX(), cell2.getSubY()
        );
        
        // 4. 构建结果
        CellPositionResult result = new CellPositionResult();
        result.setFromCellCode(cell1.getCell().getCode());
        result.setToCellCode(cell2.getCell().getCode());
        result.setRatio(ratio);
        
        log.debug("小车位置插值计算: ({},{}) → {}→{} ratio={}", 
                  currentX, currentY, 
                  cell1.getCell().getCode(), 
                  cell2.getCell().getCode(), 
                  ratio);
        
        return result;
    }
    
    /**
     * 计算插值系数
     * 使用向量投影方法计算小车在两个库位连线上的位置
     * 
     * @param carX 小车X坐标
     * @param carY 小车Y坐标
     * @param cell1X 库位1 X坐标
     * @param cell1Y 库位1 Y坐标
     * @param cell2X 库位2 X坐标
     * @param cell2Y 库位2 Y坐标
     * @return 插值系数(0-1)
     */
    private static BigDecimal calculateRatio(int carX, int carY, 
                                             int cell1X, int cell1Y,
                                             int cell2X, int cell2Y) {
        // 向量：库位1 → 库位2
        double vectorX = cell2X - cell1X;
        double vectorY = cell2Y - cell1Y;
        
        // 向量：库位1 → 小车
        double carVectorX = carX - cell1X;
        double carVectorY = carY - cell1Y;
        
        // 向量长度平方
        double vectorLengthSq = vectorX * vectorX + vectorY * vectorY;
        
        if (vectorLengthSq == 0) {
            // 两个库位重合，返回0
            log.warn("两个最近库位坐标相同: ({},{})", cell1X, cell1Y);
            return BigDecimal.ZERO;
        }
        
        // 点积 / 向量长度平方 = 投影比例 = 插值系数
        double dotProduct = carVectorX * vectorX + carVectorY * vectorY;
        double ratio = dotProduct / vectorLengthSq;
        
        // 限制在[0, 1]范围内
        ratio = Math.max(0.0, Math.min(1.0, ratio));
        
        return BigDecimal.valueOf(ratio).setScale(4, RoundingMode.HALF_UP);
    }
    
    /**
     * 库位距离信息辅助类
     */
    @Data
    public static class CellDistanceInfo {
        private CellInfo cell;
        private int subX;
        private int subY;
        private double distance;
        
        public CellDistanceInfo(CellInfo cell, int subX, int subY, double distance) {
            this.cell = cell;
            this.subX = subX;
            this.subY = subY;
            this.distance = distance;
        }
    }
    
    /**
     * 位置计算结果类
     */
    @Data
    public static class CellPositionResult {
        private String fromCellCode;  // 起始库位
        private String toCellCode;    // 目标库位
        private BigDecimal ratio;     // 插值系数(0-1)
    }
}

