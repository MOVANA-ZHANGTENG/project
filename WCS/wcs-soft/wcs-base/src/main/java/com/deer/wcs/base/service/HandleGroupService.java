package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.HandleGroup;
import com.deer.wcs.base.model.HandleGroupDto;
import com.deer.wcs.base.model.HandleGroupCriteria;

/**
 * 分组管理Service接口
 * 
 * @author deer
 * @date 2024-05-15
 */
public interface HandleGroupService   extends Service<HandleGroup, Long>
{
    /**
     * 查询分组管理
     *
     * @param id 分组管理主键
     * @return 分组管理
     */
    public HandleGroup selectHandleGroupById(Long id);

    /**
     * 查询分组管理列表
     * 
     * @param criteria
     * @return 分组管理集合
     */
    public List<HandleGroupDto> findList(HandleGroupCriteria criteria);

    /**
     * 新增分组管理
     *
     * @param handleGroup 分组管理
     * @return 结果
     */
    public int insertHandleGroup(HandleGroup handleGroup);

    /**
     * 修改分组管理
     *
     * @param handleGroup 分组管理
     * @return 结果
     */
    public int updateHandleGroup(HandleGroup handleGroup);

    /**
     * 批量删除分组管理
     * 
     * @param ids 需要删除的分组管理主键集合
     * @return 结果
     */
    public int deleteHandleGroupByIds(Long[] ids);

    /**
     * 删除分组管理信息
     * 
     * @param id 分组管理主键
     * @return 结果
     */
    public int deleteHandleGroupById(Long id);
}
