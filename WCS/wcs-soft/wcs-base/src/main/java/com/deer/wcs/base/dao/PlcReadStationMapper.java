package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.PlcReadStation;
import com.deer.wcs.base.model.PlcReadStationDto;
import com.deer.wcs.base.model.PlcReadStationCriteria;
import org.apache.ibatis.annotations.Param;

/**
 * plc读取站台信号Mapper接口
 * 
 * @author deer
 * @date 2025-06-04
 */
public interface PlcReadStationMapper  extends Mapper<PlcReadStation>
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
     * @param plcReadStation plc读取站台信号
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
     * 删除plc读取站台信号
     * 
     * @param id plc读取站台信号主键
     * @return 结果
     */
    public int deletePlcReadStationById(Long id);

    /**
     * 批量删除plc读取站台信号
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePlcReadStationByIds(Long[] ids);

    PlcReadStation findByLastSmall(@Param("codeList")List<String> codeList);
}
