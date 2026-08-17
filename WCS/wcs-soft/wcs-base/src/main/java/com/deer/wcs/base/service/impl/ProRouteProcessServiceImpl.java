package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.ProRouteProcessMapper;
import com.deer.wcs.base.model.ProRouteProcess;
import com.deer.wcs.base.model.ProRouteProcessDto;
import com.deer.wcs.base.model.ProRouteProcessCriteria;
import com.deer.wcs.base.service.ProRouteProcessService;

/**
 * 工艺流程工序关联Service业务层处理
 * 
 * @author deer
 * @date 2024-11-21
 */
@Service
public class ProRouteProcessServiceImpl  extends AbstractService<ProRouteProcess, Long>  implements ProRouteProcessService
{
    @Autowired
    private ProRouteProcessMapper proRouteProcessMapper;

    /**
     * 查询工艺流程工序关联
     *
     * @param id 工艺流程工序关联主键
     * @return 工艺流程工序关联
     */
    @Override
    public ProRouteProcess selectProRouteProcessById(Long id)
    {
        return proRouteProcessMapper.selectProRouteProcessById(id);
    }

    /**
     * 查询工艺流程工序关联列表
     * 
     * @param criteria
     * @return 工艺流程工序关联
     */
    @Override
    public List<ProRouteProcessDto> findList(ProRouteProcessCriteria criteria)
    {
        return proRouteProcessMapper.findList(criteria);
    }

    /**
     * 新增工艺流程工序关联
     *
     * @param proRouteProcess 工艺流程工序关联
     * @return 结果
     */
    @Override
    public int insertProRouteProcess(ProRouteProcess proRouteProcess)
    {
        return proRouteProcessMapper.insertProRouteProcess(proRouteProcess);
    }

    /**
     * 修改工艺流程工序关联
     *
     * @param proRouteProcess 工艺流程工序关联
     * @return 结果
     */
    @Override
    public int updateProRouteProcess(ProRouteProcess proRouteProcess)
    {
        return proRouteProcessMapper.updateProRouteProcess(proRouteProcess);
    }

    /**
     * 批量删除工艺流程工序关联
     * 
     * @param ids 需要删除的工艺流程工序关联主键
     * @return 结果
     */
    @Override
    public int deleteProRouteProcessByIds(Long[] ids)
    {
        return proRouteProcessMapper.deleteProRouteProcessByIds(ids);
    }

    /**
     * 删除工艺流程工序关联信息
     * 
     * @param id 工艺流程工序关联主键
     * @return 结果
     */
    @Override
    public int deleteProRouteProcessById(Long id)
    {
        return proRouteProcessMapper.deleteProRouteProcessById(id);
    }
}
