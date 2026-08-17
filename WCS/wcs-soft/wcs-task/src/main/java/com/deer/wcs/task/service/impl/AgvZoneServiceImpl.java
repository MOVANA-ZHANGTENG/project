package com.deer.wcs.task.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.task.dao.AgvZoneMapper;
import com.deer.wcs.task.model.AgvZone;
import com.deer.wcs.task.model.AgvZoneDto;
import com.deer.wcs.task.model.AgvZoneCriteria;
import com.deer.wcs.task.service.AgvZoneService;

/**
 * AGV交管Service业务层处理
 * 
 * @author deer
 * @date 2024-11-26
 */
@Service
public class AgvZoneServiceImpl  extends AbstractService<AgvZone, Long>  implements AgvZoneService
{
    @Autowired
    private AgvZoneMapper agvZoneMapper;

    /**
     * 查询AGV交管
     *
     * @param id AGV交管主键
     * @return AGV交管
     */
    @Override
    public AgvZone selectAgvZoneById(Long id)
    {
        return agvZoneMapper.selectAgvZoneById(id);
    }

    /**
     * 查询AGV交管列表
     * 
     * @param criteria
     * @return AGV交管
     */
    @Override
    public List<AgvZoneDto> findList(AgvZoneCriteria criteria)
    {
        return agvZoneMapper.findList(criteria);
    }

    /**
     * 新增AGV交管
     *
     * @param agvZone AGV交管
     * @return 结果
     */
    @Override
    public int insertAgvZone(AgvZone agvZone)
    {
        return agvZoneMapper.insertAgvZone(agvZone);
    }

    /**
     * 修改AGV交管
     *
     * @param agvZone AGV交管
     * @return 结果
     */
    @Override
    public int updateAgvZone(AgvZone agvZone)
    {
        agvZone.setUpdateTime(DateUtil.getNowDateTimeString());
        return agvZoneMapper.updateAgvZone(agvZone);
    }

    /**
     * 批量删除AGV交管
     * 
     * @param ids 需要删除的AGV交管主键
     * @return 结果
     */
    @Override
    public int deleteAgvZoneByIds(Long[] ids)
    {
        return agvZoneMapper.deleteAgvZoneByIds(ids);
    }

    /**
     * 删除AGV交管信息
     * 
     * @param id AGV交管主键
     * @return 结果
     */
    @Override
    public int deleteAgvZoneById(Long id)
    {
        return agvZoneMapper.deleteAgvZoneById(id);
    }
}
