package com.deer.wcs.rcs.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.rcs.dao.RcsCarHandleMapper;
import com.deer.wcs.rcs.model.RcsCarHandle;
import com.deer.wcs.rcs.model.RcsCarHandleDto;
import com.deer.wcs.rcs.model.RcsCarHandleCriteria;
import com.deer.wcs.rcs.service.RcsCarHandleService;

/**
 * RcsCarHandleService业务层处理
 * 
 * @author deer
 * @date 2025-10-14
 */
@Service
public class RcsCarHandleServiceImpl  extends AbstractService<RcsCarHandle, Long>  implements RcsCarHandleService
{
    @Autowired
    private RcsCarHandleMapper rcsCarHandleMapper;

    /**
     * 查询RcsCarHandle
     *
     * @param id RcsCarHandle主键
     * @return RcsCarHandle
     */
    @Override
    public RcsCarHandle selectRcsCarHandleById(Long id)
    {
        return rcsCarHandleMapper.selectRcsCarHandleById(id);
    }

    /**
     * 查询RcsCarHandle列表
     * 
     * @param criteria
     * @return RcsCarHandle
     */
    @Override
    public List<RcsCarHandleDto> findList(RcsCarHandleCriteria criteria)
    {
        return rcsCarHandleMapper.findList(criteria);
    }

    /**
     * 新增RcsCarHandle
     *
     * @param rcsCarHandle RcsCarHandle
     * @return 结果
     */
    @Override
    public int insertRcsCarHandle(RcsCarHandle rcsCarHandle)
    {
        rcsCarHandle.setCreateTime(DateUtil.getNowDateTimeString());
        return rcsCarHandleMapper.insertRcsCarHandle(rcsCarHandle);
    }

    /**
     * 修改RcsCarHandle
     *
     * @param rcsCarHandle RcsCarHandle
     * @return 结果
     */
    @Override
    public int updateRcsCarHandle(RcsCarHandle rcsCarHandle)
    {
        return rcsCarHandleMapper.updateRcsCarHandle(rcsCarHandle);
    }

    /**
     * 批量删除RcsCarHandle
     * 
     * @param ids 需要删除的RcsCarHandle主键
     * @return 结果
     */
    @Override
    public int deleteRcsCarHandleByIds(Long[] ids)
    {
        return rcsCarHandleMapper.deleteRcsCarHandleByIds(ids);
    }

    /**
     * 删除RcsCarHandle信息
     * 
     * @param id RcsCarHandle主键
     * @return 结果
     */
    @Override
    public int deleteRcsCarHandleById(Long id)
    {
        return rcsCarHandleMapper.deleteRcsCarHandleById(id);
    }
}
