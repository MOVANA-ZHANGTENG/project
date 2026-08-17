package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.ProRouteProcess;
import com.deer.wcs.base.model.ProRouteProcessDto;
import com.deer.wcs.base.model.ProRouteProcessCriteria;

/**
 * 工艺流程工序关联Mapper接口
 * 
 * @author deer
 * @date 2024-11-21
 */
public interface ProRouteProcessMapper  extends Mapper<ProRouteProcess>
{
    /**
     * 查询工艺流程工序关联
     *
     * @param id 工艺流程工序关联主键
     * @return 工艺流程工序关联
     */
    public ProRouteProcess selectProRouteProcessById(Long id);

    /**
     * 查询工艺流程工序关联列表
     * 
     * @param proRouteProcess 工艺流程工序关联
     * @return 工艺流程工序关联集合
     */
    public List<ProRouteProcessDto> findList(ProRouteProcessCriteria criteria);

    /**
     * 新增工艺流程工序关联
     *
     * @param proRouteProcess 工艺流程工序关联
     * @return 结果
     */
    public int insertProRouteProcess(ProRouteProcess proRouteProcess);

    /**
     * 修改工艺流程工序关联
     *
     * @param proRouteProcess 工艺流程工序关联
     * @return 结果
     */
    public int updateProRouteProcess(ProRouteProcess proRouteProcess);

    /**
     * 删除工艺流程工序关联
     * 
     * @param id 工艺流程工序关联主键
     * @return 结果
     */
    public int deleteProRouteProcessById(Long id);

    /**
     * 批量删除工艺流程工序关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProRouteProcessByIds(Long[] ids);
}
