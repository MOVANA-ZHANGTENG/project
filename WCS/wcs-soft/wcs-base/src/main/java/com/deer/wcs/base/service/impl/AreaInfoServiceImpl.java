package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.AreaInfoMapper;
import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.AreaInfoService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 库区Service业务层处理
 * 
 * @author deer
 * @date 2024-04-28
 */
@Service
public class AreaInfoServiceImpl  extends AbstractService<AreaInfo, Long>  implements AreaInfoService
{
    @Autowired
    private AreaInfoMapper areaInfoMapper;

    @Override
    public int update(AreaInfo model) {
        AreaInfo oldArea = findById(model.getId());
        if(oldArea==null){
            throw new RuntimeException("找不到对应的库区数据");
        }
        if(!model.getCode().equals(oldArea.getCode())||!model.getName().equals(oldArea.getName())){
            WareInfoUpdate update = new WareInfoUpdate();
            update.setOldAreaCode(oldArea.getCode());
            update.setNewAreaCode(model.getCode());
            update.setNewAreaName(model.getName());
            areaInfoMapper.updateAllLinkAreaId(update);
        }
        return super.update(model);
    }

    /**
     * 查询库区
     *
     * @param id 库区主键
     * @return 库区
     */
    @Override
    public AreaInfo selectAreaInfoById(Long id)
    {
        return areaInfoMapper.selectAreaInfoById(id);
    }

    /**
     * 查询库区列表
     * 
     * @param criteria
     * @return 库区
     */
    @Override
    public List<AreaInfoDto> findList(AreaInfoCriteria criteria)
    {
        return areaInfoMapper.findList(criteria);
    }

    /**
     * 新增库区
     *
     * @param areaInfo 库区
     * @return 结果
     */
    @Override
    public int insertAreaInfo(AreaInfo areaInfo)
    {
        areaInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return areaInfoMapper.insertAreaInfo(areaInfo);
    }

    /**
     * 修改库区
     *
     * @param areaInfo 库区
     * @return 结果
     */
    @Override
    public int updateAreaInfo(AreaInfo areaInfo)
    {
        areaInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        return areaInfoMapper.updateAreaInfo(areaInfo);
    }

    /**
     * 批量删除库区
     * 
     * @param ids 需要删除的库区主键
     * @return 结果
     */
    @Override
    public int deleteAreaInfoByIds(Long[] ids)
    {
        return areaInfoMapper.deleteAreaInfoByIds(ids);
    }

    /**
     * 删除库区信息
     * 
     * @param id 库区主键
     * @return 结果
     */
    @Override
    public int deleteAreaInfoById(Long id)
    {
        return areaInfoMapper.deleteAreaInfoById(id);
    }
}
