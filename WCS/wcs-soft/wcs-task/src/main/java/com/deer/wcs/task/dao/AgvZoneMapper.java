package com.deer.wcs.task.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.AgvZone;
import com.deer.wcs.task.model.AgvZoneDto;
import com.deer.wcs.task.model.AgvZoneCriteria;

/**
 * AGV交管Mapper接口
 * 
 * @author deer
 * @date 2024-11-26
 */
public interface AgvZoneMapper  extends Mapper<AgvZone>
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
     * @param agvZone AGV交管
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
     * 删除AGV交管
     * 
     * @param id AGV交管主键
     * @return 结果
     */
    public int deleteAgvZoneById(Long id);

    /**
     * 批量删除AGV交管
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAgvZoneByIds(Long[] ids);
}
