package com.deer.wcs.rcs.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.model.RcsCarInfoDto;
import com.deer.wcs.rcs.model.RcsCarInfoCriteria;
import org.apache.ibatis.annotations.Param;

/**
 * 四向车/AGVMapper接口
 * 
 * @author deer
 * @date 2025-10-14
 */
public interface RcsCarInfoMapper  extends Mapper<RcsCarInfo>
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
     * @param rcsCarInfo 四向车/AGV
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
     * 删除四向车/AGV
     * 
     * @param id 四向车/AGV主键
     * @return 结果
     */
    public int deleteRcsCarInfoById(Long id);

    /**
     * 批量删除四向车/AGV
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRcsCarInfoByIds(Long[] ids);
    
    /**
     * 更新小车遥测数据（坐标、电量、速度等）
     * 
     * @param rcsCarInfo 四向车/AGV（只更新遥测相关字段）
     * @return 结果
     */
    public int updateCarTelemetryData(RcsCarInfo rcsCarInfo);

    List<RcsCarInfo> findCanUse();

    List<RcsCarInfo> findByWareCode(@Param("wareCode") String wareCode);

    boolean hasCar(@Param("wareCode") String wareCode, @Param("fromCellCode") String fromCellCode);
}
