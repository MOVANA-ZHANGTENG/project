package com.deer.wcs.rcs.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.rcs.dao.RcsCarTypeMapper;
import com.deer.wcs.rcs.model.RcsCarType;
import com.deer.wcs.rcs.model.RcsCarTypeDto;
import com.deer.wcs.rcs.model.RcsCarTypeCriteria;
import com.deer.wcs.rcs.service.RcsCarTypeService;

/**
 * 车型号Service业务层处理
 * 
 * @author deer
 * @date 2025-10-14
 */
@Service
public class RcsCarTypeServiceImpl  extends AbstractService<RcsCarType, Long>  implements RcsCarTypeService
{
    @Autowired
    private RcsCarTypeMapper rcsCarTypeMapper;

    /**
     * 查询车型号
     *
     * @param id 车型号主键
     * @return 车型号
     */
    @Override
    public RcsCarType selectRcsCarTypeById(Long id)
    {
        return rcsCarTypeMapper.selectRcsCarTypeById(id);
    }

    /**
     * 查询车型号列表
     * 
     * @param criteria
     * @return 车型号
     */
    @Override
    public List<RcsCarTypeDto> findList(RcsCarTypeCriteria criteria)
    {
        return rcsCarTypeMapper.findList(criteria);
    }

    /**
     * 新增车型号
     *
     * @param rcsCarType 车型号
     * @return 结果
     */
    @Override
    public int insertRcsCarType(RcsCarType rcsCarType)
    {
        rcsCarType.setCreateTime(DateUtil.getNowDateTimeString());
        return rcsCarTypeMapper.insertRcsCarType(rcsCarType);
    }

    /**
     * 修改车型号
     *
     * @param rcsCarType 车型号
     * @return 结果
     */
    @Override
    public int updateRcsCarType(RcsCarType rcsCarType)
    {
        rcsCarType.setUpdateTime(DateUtil.getNowDateTimeString());
        return rcsCarTypeMapper.updateRcsCarType(rcsCarType);
    }

    /**
     * 批量删除车型号
     * 
     * @param ids 需要删除的车型号主键
     * @return 结果
     */
    @Override
    public int deleteRcsCarTypeByIds(Long[] ids)
    {
        return rcsCarTypeMapper.deleteRcsCarTypeByIds(ids);
    }

    /**
     * 删除车型号信息
     * 
     * @param id 车型号主键
     * @return 结果
     */
    @Override
    public int deleteRcsCarTypeById(Long id)
    {
        return rcsCarTypeMapper.deleteRcsCarTypeById(id);
    }
}
