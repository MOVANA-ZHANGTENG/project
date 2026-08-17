package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.PlcReadStationMapper;
import com.deer.wcs.base.model.PlcReadStation;
import com.deer.wcs.base.model.PlcReadStationDto;
import com.deer.wcs.base.model.PlcReadStationCriteria;
import com.deer.wcs.base.service.PlcReadStationService;

/**
 * plc读取站台信号Service业务层处理
 * 
 * @author deer
 * @date 2025-06-04
 */
@Service
public class PlcReadStationServiceImpl  extends AbstractService<PlcReadStation, Long>  implements PlcReadStationService
{
    @Autowired
    private PlcReadStationMapper plcReadStationMapper;

    /**
     * 查询plc读取站台信号
     *
     * @param id plc读取站台信号主键
     * @return plc读取站台信号
     */
    @Override
    public PlcReadStation selectPlcReadStationById(Long id)
    {
        return plcReadStationMapper.selectPlcReadStationById(id);
    }

    /**
     * 查询plc读取站台信号列表
     * 
     * @param criteria
     * @return plc读取站台信号
     */
    @Override
    public List<PlcReadStationDto> findList(PlcReadStationCriteria criteria)
    {
        return plcReadStationMapper.findList(criteria);
    }

    /**
     * 新增plc读取站台信号
     *
     * @param plcReadStation plc读取站台信号
     * @return 结果
     */
    @Override
    public int insertPlcReadStation(PlcReadStation plcReadStation)
    {
        return plcReadStationMapper.insertPlcReadStation(plcReadStation);
    }

    /**
     * 修改plc读取站台信号
     *
     * @param plcReadStation plc读取站台信号
     * @return 结果
     */
    @Override
    public int updatePlcReadStation(PlcReadStation plcReadStation)
    {
        return plcReadStationMapper.updatePlcReadStation(plcReadStation);
    }

    /**
     * 批量删除plc读取站台信号
     * 
     * @param ids 需要删除的plc读取站台信号主键
     * @return 结果
     */
    @Override
    public int deletePlcReadStationByIds(Long[] ids)
    {
        return plcReadStationMapper.deletePlcReadStationByIds(ids);
    }

    /**
     * 删除plc读取站台信号信息
     * 
     * @param id plc读取站台信号主键
     * @return 结果
     */
    @Override
    public int deletePlcReadStationById(Long id)
    {
        return plcReadStationMapper.deletePlcReadStationById(id);
    }

    @Override
    public PlcReadStation findByLastSmall(List<String> ids) {
        return plcReadStationMapper.findByLastSmall(ids);
    }
}
