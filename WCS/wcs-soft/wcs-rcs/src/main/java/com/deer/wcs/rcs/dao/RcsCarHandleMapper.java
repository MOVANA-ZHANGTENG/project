package com.deer.wcs.rcs.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.rcs.model.RcsCarHandle;
import com.deer.wcs.rcs.model.RcsCarHandleDto;
import com.deer.wcs.rcs.model.RcsCarHandleCriteria;

/**
 * RcsCarHandleMapper接口
 * 
 * @author deer
 * @date 2025-10-14
 */
public interface RcsCarHandleMapper  extends Mapper<RcsCarHandle>
{
    /**
     * 查询RcsCarHandle
     *
     * @param id RcsCarHandle主键
     * @return RcsCarHandle
     */
    public RcsCarHandle selectRcsCarHandleById(Long id);

    /**
     * 查询RcsCarHandle列表
     * 
     * @param rcsCarHandle RcsCarHandle
     * @return RcsCarHandle集合
     */
    public List<RcsCarHandleDto> findList(RcsCarHandleCriteria criteria);

    /**
     * 新增RcsCarHandle
     *
     * @param rcsCarHandle RcsCarHandle
     * @return 结果
     */
    public int insertRcsCarHandle(RcsCarHandle rcsCarHandle);

    /**
     * 修改RcsCarHandle
     *
     * @param rcsCarHandle RcsCarHandle
     * @return 结果
     */
    public int updateRcsCarHandle(RcsCarHandle rcsCarHandle);

    /**
     * 删除RcsCarHandle
     * 
     * @param id RcsCarHandle主键
     * @return 结果
     */
    public int deleteRcsCarHandleById(Long id);

    /**
     * 批量删除RcsCarHandle
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRcsCarHandleByIds(Long[] ids);
}
