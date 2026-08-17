package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.AreaInfo;
import com.deer.wcs.base.model.AreaInfoDto;
import com.deer.wcs.base.model.AreaInfoCriteria;

/**
 * 库区Service接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface AreaInfoService   extends Service<AreaInfo, Long>
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
     * @param criteria
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
     * 批量删除库区
     * 
     * @param ids 需要删除的库区主键集合
     * @return 结果
     */
    public int deleteAreaInfoByIds(Long[] ids);

    /**
     * 删除库区信息
     * 
     * @param id 库区主键
     * @return 结果
     */
    public int deleteAreaInfoById(Long id);
}
