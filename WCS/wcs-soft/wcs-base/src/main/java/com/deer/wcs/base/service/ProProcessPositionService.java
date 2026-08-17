package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.ProProcessPosition;
import com.deer.wcs.base.model.ProProcessPositionDto;
import com.deer.wcs.base.model.ProProcessPositionCriteria;

/**
 * 工序站台Service接口
 * 
 * @author deer
 * @date 2024-12-25
 */
public interface ProProcessPositionService   extends Service<ProProcessPosition, Long>
{
    /**
     * 查询工序站台
     *
     * @param id 工序站台主键
     * @return 工序站台
     */
    public ProProcessPosition selectProProcessPositionById(Long id);

    /**
     * 查询工序站台列表
     * 
     * @param criteria
     * @return 工序站台集合
     */
    public List<ProProcessPositionDto> findList(ProProcessPositionCriteria criteria);

    /**
     * 新增工序站台
     *
     * @param proProcessPosition 工序站台
     * @return 结果
     */
    public int insertProProcessPosition(ProProcessPosition proProcessPosition);

    /**
     * 修改工序站台
     *
     * @param proProcessPosition 工序站台
     * @return 结果
     */
    public int updateProProcessPosition(ProProcessPosition proProcessPosition);

    /**
     * 批量删除工序站台
     * 
     * @param ids 需要删除的工序站台主键集合
     * @return 结果
     */
    public int deleteProProcessPositionByIds(Long[] ids);

    /**
     * 删除工序站台信息
     * 
     * @param id 工序站台主键
     * @return 结果
     */
    public int deleteProProcessPositionById(Long id);
}
