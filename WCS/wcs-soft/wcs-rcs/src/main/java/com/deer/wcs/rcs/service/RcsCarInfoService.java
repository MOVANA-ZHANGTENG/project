package com.deer.wcs.rcs.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.model.RcsCarInfoDto;
import com.deer.wcs.rcs.model.RcsCarInfoCriteria;

/**
 * 四向车/AGVService接口
 * 
 * @author deer
 * @date 2025-10-14
 */
public interface RcsCarInfoService   extends Service<RcsCarInfo, Long>
{
    /**
     * 查询四向车/AGV
     *
     * @param id 四向车/AGV主键
     * @return 四向车/AGV
     */
    public RcsCarInfo selectRcsCarInfoById(Long id);

    /**
     * 查询四向车/AGV列表
     * 
     * @param criteria
     * @return 四向车/AGV集合
     */
    public List<RcsCarInfoDto> findList(RcsCarInfoCriteria criteria);

    /**
     * 新增四向车/AGV
     *
     * @param rcsCarInfo 四向车/AGV
     * @return 结果
     */
    public int insertRcsCarInfo(RcsCarInfo rcsCarInfo);

    /**
     * 修改四向车/AGV
     *
     * @param rcsCarInfo 四向车/AGV
     * @return 结果
     */
    public int updateRcsCarInfo(RcsCarInfo rcsCarInfo);

    /**
     * 批量删除四向车/AGV
     * 
     * @param ids 需要删除的四向车/AGV主键集合
     * @return 结果
     */
    public int deleteRcsCarInfoByIds(Long[] ids);

    /**
     * 删除四向车/AGV信息
     * 
     * @param id 四向车/AGV主键
     * @return 结果
     */
    public int deleteRcsCarInfoById(Long id);
    
    /**
     * 更新小车遥测数据（坐标、电量、速度等），并推送 WebSocket
     * 
     * @param rcsCarInfo 四向车/AGV（只更新遥测相关字段）
     * @return 结果
     */
    public int updateCarTelemetryData(RcsCarInfo rcsCarInfo);
    
    /**
     * 更新小车位置（静止状态）
     * 同时更新fromCellCode和toCellCode为相同值，表示小车静止在该库位
     * 
     * @param carId 小车ID
     * @param carCode 小车编码
     * @param fromCellCode 新的库位编码（起点和终点都会设为此值）
     * @return 是否成功
     */
    public boolean updateFromCellCode(Long carId, String carCode, String fromCellCode);

    List<RcsCarInfo> findCanUse();

    /**
     *  查看仓库下所有的小车
     *  1. 未禁用，未删除
     * @param wareCode
     * @return
     */
    List<RcsCarInfo> findByWareCode(String wareCode);

    boolean hasCar(String wareCode, String fromCellCode);
}
