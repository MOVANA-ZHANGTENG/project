package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.WmsTaskInfo;
import com.deer.wcs.base.model.WmsTaskInfoDto;
import com.deer.wcs.base.model.WmsTaskInfoCriteria;

/**
 * wms任务Mapper接口
 * 
 * @author deer
 * @date 2024-05-10
 */
public interface WmsTaskInfoMapper  extends Mapper<WmsTaskInfo>
{
    /**
     * 查询wms任务
     *
     * @param id wms任务主键
     * @return wms任务
     */
    public WmsTaskInfo selectWmsTaskInfoById(Long id);

    /**
     * 查询wms任务列表
     * 
     * @param wmsTaskInfo wms任务
     * @return wms任务集合
     */
    public List<WmsTaskInfoDto> findList(WmsTaskInfoCriteria criteria);

    /**
     * 新增wms任务
     *
     * @param wmsTaskInfo wms任务
     * @return 结果
     */
    public int insertWmsTaskInfo(WmsTaskInfo wmsTaskInfo);

    /**
     * 修改wms任务
     *
     * @param wmsTaskInfo wms任务
     * @return 结果
     */
    public int updateWmsTaskInfo(WmsTaskInfo wmsTaskInfo);

    /**
     * 删除wms任务
     * 
     * @param id wms任务主键
     * @return 结果
     */
    public int deleteWmsTaskInfoById(Long id);

    /**
     * 批量删除wms任务
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWmsTaskInfoByIds(Long[] ids);
}
