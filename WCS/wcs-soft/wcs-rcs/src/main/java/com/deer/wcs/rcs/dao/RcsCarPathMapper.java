package com.deer.wcs.rcs.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.rcs.model.RcsCarPath;
import com.deer.wcs.rcs.model.RcsCarPathDto;
import com.deer.wcs.rcs.model.RcsCarPathCriteria;

/**
 * 车路径Mapper接口
 * 
 * @author deer
 * @date 2025-10-15
 */
public interface RcsCarPathMapper  extends Mapper<RcsCarPath>
{
    /**
     * 查询车路径
     *
     * @param id 车路径主键
     * @return 车路径
     */
    public RcsCarPath selectRcsCarPathById(Long id);

    /**
     * 查询车路径列表
     * 
     * @param rcsCarPath 车路径
     * @return 车路径集合
     */
    public List<RcsCarPathDto> findList(RcsCarPathCriteria criteria);
    public List<RcsCarPath> hasAllot(RcsCarPath  criteria);

    /**
     * 新增车路径
     *
     * @param rcsCarPath 车路径
     * @return 结果
     */
    public int insertRcsCarPath(RcsCarPath rcsCarPath);

    /**
     * 修改车路径
     *
     * @param rcsCarPath 车路径
     * @return 结果
     */
    public int updateRcsCarPath(RcsCarPath rcsCarPath);

    /**
     * 删除车路径
     * 
     * @param id 车路径主键
     * @return 结果
     */
    public int deleteRcsCarPathById(Long id);

    /**
     * 批量删除车路径
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRcsCarPathByIds(Long[] ids);
    
    /**
     * 删除jobId不存在于job_info表中的路径记录
     * 
     * @return 删除的记录数
     */
    public int deleteOrphanedPaths();
}
