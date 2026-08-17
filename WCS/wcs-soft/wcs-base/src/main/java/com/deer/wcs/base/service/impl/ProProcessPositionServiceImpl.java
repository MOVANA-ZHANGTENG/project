package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.ProProcessPositionMapper;
import com.deer.wcs.base.model.ProProcessPosition;
import com.deer.wcs.base.model.ProProcessPositionDto;
import com.deer.wcs.base.model.ProProcessPositionCriteria;
import com.deer.wcs.base.service.ProProcessPositionService;

/**
 * 工序站台Service业务层处理
 * 
 * @author deer
 * @date 2024-12-25
 */
@Service
public class ProProcessPositionServiceImpl  extends AbstractService<ProProcessPosition, Long>  implements ProProcessPositionService
{
    @Autowired
    private ProProcessPositionMapper proProcessPositionMapper;

    /**
     * 查询工序站台
     *
     * @param id 工序站台主键
     * @return 工序站台
     */
    @Override
    public ProProcessPosition selectProProcessPositionById(Long id)
    {
        return proProcessPositionMapper.selectProProcessPositionById(id);
    }

    /**
     * 查询工序站台列表
     * 
     * @param criteria
     * @return 工序站台
     */
    @Override
    public List<ProProcessPositionDto> findList(ProProcessPositionCriteria criteria)
    {
        return proProcessPositionMapper.findList(criteria);
    }

    /**
     * 新增工序站台
     *
     * @param proProcessPosition 工序站台
     * @return 结果
     */
    @Override
    public int insertProProcessPosition(ProProcessPosition proProcessPosition)
    {
        return proProcessPositionMapper.insertProProcessPosition(proProcessPosition);
    }

    /**
     * 修改工序站台
     *
     * @param proProcessPosition 工序站台
     * @return 结果
     */
    @Override
    public int updateProProcessPosition(ProProcessPosition proProcessPosition)
    {
        return proProcessPositionMapper.updateProProcessPosition(proProcessPosition);
    }

    /**
     * 批量删除工序站台
     * 
     * @param ids 需要删除的工序站台主键
     * @return 结果
     */
    @Override
    public int deleteProProcessPositionByIds(Long[] ids)
    {
        return proProcessPositionMapper.deleteProProcessPositionByIds(ids);
    }

    /**
     * 删除工序站台信息
     * 
     * @param id 工序站台主键
     * @return 结果
     */
    @Override
    public int deleteProProcessPositionById(Long id)
    {
        return proProcessPositionMapper.deleteProProcessPositionById(id);
    }
}
