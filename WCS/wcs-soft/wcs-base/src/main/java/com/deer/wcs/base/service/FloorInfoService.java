package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.FloorInfo;
import com.deer.wcs.base.model.FloorInfoDto;
import com.deer.wcs.base.model.FloorInfoCriteria;

/**
 * 层Service接口
 * 
 * @author deer
 * @date 2025-09-18
 */
public interface FloorInfoService   extends Service<FloorInfo, Long>
{
    /**
     * 查询层
     *
     * @param id 层主键
     * @return 层
     */
    public FloorInfo selectFloorInfoById(Long id);

    /**
     * 查询层列表
     * 
     * @param criteria
     * @return 层集合
     */
    public List<FloorInfoDto> findList(FloorInfoCriteria criteria);

    /**
     * 新增层
     *
     * @param floorInfo 层
     * @return 结果
     */
    public int insertFloorInfo(FloorInfo floorInfo);

    /**
     * 修改层
     *
     * @param floorInfo 层
     * @return 结果
     */
    public int updateFloorInfo(FloorInfo floorInfo);

    /**
     * 批量删除层
     * 
     * @param ids 需要删除的层主键集合
     * @return 结果
     */
    public int deleteFloorInfoByIds(Long[] ids);

    /**
     * 删除层信息
     * 
     * @param id 层主键
     * @return 结果
     */
    public int deleteFloorInfoById(Long id);

    public FloorInfo findByZ(String wareCode,Integer z);
}
