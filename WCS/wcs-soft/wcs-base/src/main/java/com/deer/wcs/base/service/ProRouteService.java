package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.ProRoute;
import com.deer.wcs.base.model.ProRouteDto;
import com.deer.wcs.base.model.ProRouteCriteria;

/**
 * 工艺流程Service接口
 * 
 * @author deer
 * @date 2024-11-21
 */
public interface ProRouteService   extends Service<ProRoute, Long>
{
    /**
     * 查询工艺流程
     *
     * @param id 工艺流程主键
     * @return 工艺流程
     */
    public ProRoute selectProRouteById(Long id);

    /**
     * 查询工艺流程列表
     * 
     * @param criteria
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
     * 批量删除工艺流程
     * 
     * @param ids 需要删除的工艺流程主键集合
     * @return 结果
     */
    public int deleteProRouteByIds(Long[] ids);

    /**
     * 删除工艺流程信息
     * 
     * @param id 工艺流程主键
     * @return 结果
     */
    public int deleteProRouteById(Long id);
}
