package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.ProRoute;
import com.deer.wcs.base.model.ProRouteDto;
import com.deer.wcs.base.model.ProRouteCriteria;

/**
 * 工艺流程Mapper接口
 * 
 * @author deer
 * @date 2024-11-21
 */
public interface ProRouteMapper  extends Mapper<ProRoute>
{
    /**
     * 查询工艺流程
     *
     * @param id 工艺流程主键
     * @return 工艺流程
     */
    public ProRoute selectProRouteById(Long id);

    public void deleteModel(Long proRouteId);
    /**
     * 查询工艺流程列表
     * 
     * @param proRoute 工艺流程
     * @return 工艺流程集合
     */
    public List<ProRouteDto> findList(ProRouteCriteria criteria);

    /**
     * 新增工艺流程
     *
     * @param proRoute 工艺流程
     * @return 结果
     */
    public int insertProRoute(ProRoute proRoute);

    /**
     * 修改工艺流程
     *
     * @param proRoute 工艺流程
     * @return 结果
     */
    public int updateProRoute(ProRoute proRoute);

    /**
     * 删除工艺流程
     * 
     * @param id 工艺流程主键
     * @return 结果
     */
    public int deleteProRouteById(Long id);

    /**
     * 批量删除工艺流程
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProRouteByIds(Long[] ids);
}
