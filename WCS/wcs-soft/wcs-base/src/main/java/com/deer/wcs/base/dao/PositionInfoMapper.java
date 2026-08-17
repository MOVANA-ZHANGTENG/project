package com.deer.wcs.base.dao;

import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.model.PositionInfoCriteria;
import com.deer.wcs.base.model.PositionInfoDto;
import com.deer.wcs.common.core.mapper.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 站台Mapper接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface PositionInfoMapper  extends Mapper<PositionInfo>
{
    /**
     * 查询站台
     *
     * @param id 站台主键
     * @return 站台
     */
    public PositionInfo selectPositionInfoById(Long id);

    /**
     * 查询站台列表
     * 
     * @param criteria 站台
     * @return 站台集合
     */
    public List<PositionInfoDto> findList(PositionInfoCriteria criteria);

    /**
     * 新增站台
     *
     * @param positionInfo 站台
     * @return 结果
     */
    public int insertPositionInfo(PositionInfo positionInfo);

    /**
     * 修改站台
     *
     * @param positionInfo 站台
     * @return 结果
     */
    public int updatePositionInfo(PositionInfo positionInfo);

    /**
     * 删除站台
     * 
     * @param id 站台主键
     * @return 结果
     */
    public int deletePositionInfoById(Long id);

    /**
     * 批量删除站台
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePositionInfoByIds(Long[] ids);

    List<PositionInfo> findByWareCode(@Param("wareCode") String wareCode);

    List<PositionInfo> findByParentCode(@Param("parentCode") String parentCode);

    PositionInfo findByIdForUpdate(@Param("id")Long id);
}
