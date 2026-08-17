package com.deer.wcs.rcs.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.rcs.model.RcsCarHandle;
import com.deer.wcs.rcs.model.RcsCarHandleDto;
import com.deer.wcs.rcs.model.RcsCarHandleCriteria;

/**
 * RcsCarHandleService接口
 * 
 * @author deer
 * @date 2025-10-14
 */
public interface RcsCarHandleService   extends Service<RcsCarHandle, Long>
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
     * @param criteria
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
     * 批量删除RcsCarHandle
     * 
     * @param ids 需要删除的RcsCarHandle主键集合
     * @return 结果
     */
    public int deleteRcsCarHandleByIds(Long[] ids);

    /**
     * 删除RcsCarHandle信息
     * 
     * @param id RcsCarHandle主键
     * @return 结果
     */
    public int deleteRcsCarHandleById(Long id);
}
