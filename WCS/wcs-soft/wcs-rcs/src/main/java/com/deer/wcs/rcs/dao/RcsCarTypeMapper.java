package com.deer.wcs.rcs.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.rcs.model.RcsCarType;
import com.deer.wcs.rcs.model.RcsCarTypeDto;
import com.deer.wcs.rcs.model.RcsCarTypeCriteria;

/**
 * 车型号Mapper接口
 * 
 * @author deer
 * @date 2025-10-14
 */
public interface RcsCarTypeMapper  extends Mapper<RcsCarType>
{
    /**
     * 查询车型号
     *
     * @param id 车型号主键
     * @return 车型号
     */
    public RcsCarType selectRcsCarTypeById(Long id);

    /**
     * 查询车型号列表
     * 
     * @param rcsCarType 车型号
     * @return 车型号集合
     */
    public List<RcsCarTypeDto> findList(RcsCarTypeCriteria criteria);

    /**
     * 新增车型号
     *
     * @param rcsCarType 车型号
     * @return 结果
     */
    public int insertRcsCarType(RcsCarType rcsCarType);

    /**
     * 修改车型号
     *
     * @param rcsCarType 车型号
     * @return 结果
     */
    public int updateRcsCarType(RcsCarType rcsCarType);

    /**
     * 删除车型号
     * 
     * @param id 车型号主键
     * @return 结果
     */
    public int deleteRcsCarTypeById(Long id);

    /**
     * 批量删除车型号
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRcsCarTypeByIds(Long[] ids);
}
