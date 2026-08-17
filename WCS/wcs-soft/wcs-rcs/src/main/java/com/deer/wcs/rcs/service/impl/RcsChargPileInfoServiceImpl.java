package com.deer.wcs.rcs.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.rcs.dao.RcsChargPileInfoMapper;
import com.deer.wcs.rcs.model.RcsChargPileInfo;
import com.deer.wcs.rcs.model.RcsChargPileInfoDto;
import com.deer.wcs.rcs.model.RcsChargPileInfoCriteria;
import com.deer.wcs.rcs.service.RcsChargPileInfoService;

/**
 * 充电桩Service业务层处理
 * 
 * @author deer
 * @date 2025-10-14
 */
@Service
public class RcsChargPileInfoServiceImpl  extends AbstractService<RcsChargPileInfo, Long>  implements RcsChargPileInfoService
{
    @Autowired
    private RcsChargPileInfoMapper rcsChargPileInfoMapper;

    /**
     * 查询充电桩
     *
     * @param id 充电桩主键
     * @return 充电桩
     */
    @Override
    public RcsChargPileInfo selectRcsChargPileInfoById(Integer id)
    {
        return rcsChargPileInfoMapper.selectRcsChargPileInfoById(id);
    }

    /**
     * 查询充电桩列表
     * 
     * @param criteria
     * @return 充电桩
     */
    @Override
    public List<RcsChargPileInfoDto> findList(RcsChargPileInfoCriteria criteria)
    {
        return rcsChargPileInfoMapper.findList(criteria);
    }

    /**
     * 新增充电桩
     *
     * @param rcsChargPileInfo 充电桩
     * @return 结果
     */
    @Override
    public int insertRcsChargPileInfo(RcsChargPileInfo rcsChargPileInfo)
    {
        return rcsChargPileInfoMapper.insertRcsChargPileInfo(rcsChargPileInfo);
    }

    /**
     * 修改充电桩
     *
     * @param rcsChargPileInfo 充电桩
     * @return 结果
     */
    @Override
    public int updateRcsChargPileInfo(RcsChargPileInfo rcsChargPileInfo)
    {
        return rcsChargPileInfoMapper.updateRcsChargPileInfo(rcsChargPileInfo);
    }

    /**
     * 批量删除充电桩
     * 
     * @param ids 需要删除的充电桩主键
     * @return 结果
     */
    @Override
    public int deleteRcsChargPileInfoByIds(Integer[] ids)
    {
        return rcsChargPileInfoMapper.deleteRcsChargPileInfoByIds(ids);
    }

    /**
     * 删除充电桩信息
     * 
     * @param id 充电桩主键
     * @return 结果
     */
    @Override
    public int deleteRcsChargPileInfoById(Integer id)
    {
        return rcsChargPileInfoMapper.deleteRcsChargPileInfoById(id);
    }
}
