package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.PlcReadStation;
import com.deer.wcs.base.model.PlcReadStationDto;
import com.deer.wcs.base.model.PlcReadStationCriteria;

/**
 * plc读取站台信号Service接口
 * 
 * @author deer
 * @date 2025-06-04
 */
public interface PlcReadStationService   extends Service<PlcReadStation, Long>
{
    /**
     * 查询plc读取站台信号
     *
     * @param id plc读取站台信号主键
     * @return plc读取站台信号
     */
    public PlcReadStation selectPlcReadStationById(Long id);

    /**
     * 查询plc读取站台信号列表
     * 
     * @param criteria
     * @return plc读取站台信号集合
     */
    public List<PlcReadStationDto> findList(PlcReadStationCriteria criteria);

    /**
     * 新增plc读取站台信号
     *
     * @param plcReadStation plc读取站台信号
     * @return 结果
     */
    public int insertPlcReadStation(PlcReadStation plcReadStation);

    /**
     * 修改plc读取站台信号
     *
     * @param plcReadStation plc读取站台信号
     * @return 结果
     */
    public int updatePlcReadStation(PlcReadStation plcReadStation);

    /**
     * 批量删除plc读取站台信号
     * 
     * @param ids 需要删除的plc读取站台信号主键集合
     * @return 结果
     */
    public int deletePlcReadStationByIds(Long[] ids);

    /**
     * 删除plc读取站台信号信息
     * 
     * @param id plc读取站台信号主键
     * @return 结果
     */
    public int deletePlcReadStationById(Long id);

    PlcReadStation findByLastSmall(List<String> ids);
}
