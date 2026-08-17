package com.deer.wcs.task.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.task.model.AgvZone;
import com.deer.wcs.task.model.AgvZoneDto;
import com.deer.wcs.task.model.AgvZoneCriteria;

/**
 * AGV交管Service接口
 * 
 * @author deer
 * @date 2024-11-26
 */
public interface AgvZoneService   extends Service<AgvZone, Long>
{
    /**
     * 查询AGV交管
     *
     * @param id AGV交管主键
     * @return AGV交管
     */
    public AgvZone selectAgvZoneById(Long id);

    /**
     * 查询AGV交管列表
     * 
     * @param criteria
     * @return AGV交管集合
     */
    public List<AgvZoneDto> findList(AgvZoneCriteria criteria);

    /**
     * 新增AGV交管
     *
     * @param agvZone AGV交管
     * @return 结果
     */
    public int insertAgvZone(AgvZone agvZone);

    /**
     * 修改AGV交管
     *
     * @param agvZone AGV交管
     * @return 结果
     */
    public int updateAgvZone(AgvZone agvZone);

    /**
     * 批量删除AGV交管
     * 
     * @param ids 需要删除的AGV交管主键集合
     * @return 结果
     */
    public int deleteAgvZoneByIds(Long[] ids);

    /**
     * 删除AGV交管信息
     * 
     * @param id AGV交管主键
     * @return 结果
     */
    public int deleteAgvZoneById(Long id);
}
