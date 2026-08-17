package com.deer.wcs.rcs.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.rcs.model.RcsChargPileInfo;
import com.deer.wcs.rcs.model.RcsChargPileInfoDto;
import com.deer.wcs.rcs.model.RcsChargPileInfoCriteria;

/**
 * 充电桩Service接口
 * 
 * @author deer
 * @date 2025-10-14
 */
public interface RcsChargPileInfoService   extends Service<RcsChargPileInfo, Long>
{
    /**
     * 查询充电桩
     *
     * @param id 充电桩主键
     * @return 充电桩
     */
    public RcsChargPileInfo selectRcsChargPileInfoById(Integer id);

    /**
     * 查询充电桩列表
     * 
     * @param criteria
     * @return 充电桩集合
     */
    public List<RcsChargPileInfoDto> findList(RcsChargPileInfoCriteria criteria);

    /**
     * 新增充电桩
     *
     * @param rcsChargPileInfo 充电桩
     * @return 结果
     */
    public int insertRcsChargPileInfo(RcsChargPileInfo rcsChargPileInfo);

    /**
     * 修改充电桩
     *
     * @param rcsChargPileInfo 充电桩
     * @return 结果
     */
    public int updateRcsChargPileInfo(RcsChargPileInfo rcsChargPileInfo);

    /**
     * 批量删除充电桩
     * 
     * @param ids 需要删除的充电桩主键集合
     * @return 结果
     */
    public int deleteRcsChargPileInfoByIds(Integer[] ids);

    /**
     * 删除充电桩信息
     * 
     * @param id 充电桩主键
     * @return 结果
     */
    public int deleteRcsChargPileInfoById(Integer id);
}
