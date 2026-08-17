package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.FloorInfoMapper;
import com.deer.wcs.base.model.FloorInfo;
import com.deer.wcs.base.model.FloorInfoDto;
import com.deer.wcs.base.model.FloorInfoCriteria;
import com.deer.wcs.base.service.FloorInfoService;
import tk.mybatis.mapper.entity.Condition;

/**
 * 层Service业务层处理
 * 
 * @author deer
 * @date 2025-09-18
 */
@Service
public class FloorInfoServiceImpl  extends AbstractService<FloorInfo, Long>  implements FloorInfoService
{
    @Autowired
    private FloorInfoMapper floorInfoMapper;

    /**
     * 查询层
     *
     * @param id 层主键
     * @return 层
     */
    @Override
    public FloorInfo selectFloorInfoById(Long id)
    {
        return floorInfoMapper.selectFloorInfoById(id);
    }

    /**
     * 查询层列表
     * 
     * @param criteria
     * @return 层
     */
    @Override
    public List<FloorInfoDto> findList(FloorInfoCriteria criteria)
    {
        return floorInfoMapper.findList(criteria);
    }

    /**
     * 新增层
     *
     * @param floorInfo 层
     * @return 结果
     */
    @Override
    public int insertFloorInfo(FloorInfo floorInfo)
    {
        floorInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return floorInfoMapper.insertFloorInfo(floorInfo);
    }

    /**
     * 修改层
     *
     * @param floorInfo 层
     * @return 结果
     */
    @Override
    public int updateFloorInfo(FloorInfo floorInfo)
    {
        return floorInfoMapper.updateFloorInfo(floorInfo);
    }

    /**
     * 批量删除层
     * 
     * @param ids 需要删除的层主键
     * @return 结果
     */
    @Override
    public int deleteFloorInfoByIds(Long[] ids)
    {
        return floorInfoMapper.deleteFloorInfoByIds(ids);
    }

    /**
     * 删除层信息
     * 
     * @param id 层主键
     * @return 结果
     */
    @Override
    public int deleteFloorInfoById(Long id)
    {
        return floorInfoMapper.deleteFloorInfoById(id);
    }


    @Override
    public FloorInfo findByZ(String wareCode,Integer z) {
        Condition condition = new Condition(FloorInfo.class);
        condition.createCriteria().andEqualTo("wareCode",wareCode).andEqualTo("z",z);
        List<FloorInfo> list = floorInfoMapper.selectByCondition(condition);
        if(list.size()>0){
            return list.get(0);
        }
        return null;
    }
}
