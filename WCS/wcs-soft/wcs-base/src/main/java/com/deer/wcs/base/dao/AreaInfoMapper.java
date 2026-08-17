package com.deer.wcs.base.dao;

import com.deer.wcs.base.model.AreaInfo;
import com.deer.wcs.base.model.AreaInfoCriteria;
import com.deer.wcs.base.model.AreaInfoDto;
import com.deer.wcs.base.model.WareInfoUpdate;
import com.deer.wcs.common.core.mapper.Mapper;

import java.util.List;

/**
 * 库区Mapper接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface AreaInfoMapper  extends Mapper<AreaInfo>
{
    /**
     * 查询库区
     *
     * @param id 库区主键
     * @return 库区
     */
    public AreaInfo selectAreaInfoById(Long id);

    /**
     * 查询库区列表
     * 
     * @param areaInfo 库区
     * @return 库区集合
     */
    public List<AreaInfoDto> findList(AreaInfoCriteria criteria);

    /**
     * 新增库区
     *
     * @param areaInfo 库区
     * @return 结果
     */
    public int insertAreaInfo(AreaInfo areaInfo);

    /**
     * 修改库区
     *
     * @param areaInfo 库区
     * @return 结果
     */
    public int updateAreaInfo(AreaInfo areaInfo);

    /**
     * 删除库区
     * 
     * @param id 库区主键
     * @return 结果
     */
    public int deleteAreaInfoById(Long id);

    /**
     * 批量删除库区
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAreaInfoByIds(Long[] ids);

    /**
     * 更新库区关联数据
     *
     * @param update
     * @return 结果
     */
    public void updateAllLinkAreaId(WareInfoUpdate update);
}
