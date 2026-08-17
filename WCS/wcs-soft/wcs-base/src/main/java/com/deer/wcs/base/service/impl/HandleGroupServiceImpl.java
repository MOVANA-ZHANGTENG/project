package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.HandleGroupMapper;
import com.deer.wcs.base.model.HandleGroup;
import com.deer.wcs.base.model.HandleGroupCriteria;
import com.deer.wcs.base.model.HandleGroupDto;
import com.deer.wcs.base.service.HandleGroupService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分组管理Service业务层处理
 * 
 * @author deer
 * @date 2024-05-15
 */
@Service
public class HandleGroupServiceImpl  extends AbstractService<HandleGroup, Long>  implements HandleGroupService
{
    @Autowired
    private HandleGroupMapper handleGroupMapper;

    @Override
    public void save(HandleGroup model) {
        model.setDisableState("0");
        model.setDelFlag(0);
        model.setVersion(0);
        super.save(model);
    }

    /**
     * 查询分组管理
     *
     * @param id 分组管理主键
     * @return 分组管理
     */
    @Override
    public HandleGroup selectHandleGroupById(Long id)
    {
        return handleGroupMapper.selectHandleGroupById(id);
    }

    /**
     * 查询分组管理列表
     * 
     * @param criteria
     * @return 分组管理
     */
    @Override
    public List<HandleGroupDto> findList(HandleGroupCriteria criteria)
    {
        return handleGroupMapper.findList(criteria);
    }

    /**
     * 新增分组管理
     *
     * @param handleGroup 分组管理
     * @return 结果
     */
    @Override
    public int insertHandleGroup(HandleGroup handleGroup)
    {
        handleGroup.setCreateTime(DateUtil.getNowDateTimeString());
        return handleGroupMapper.insertHandleGroup(handleGroup);
    }

    /**
     * 修改分组管理
     *
     * @param handleGroup 分组管理
     * @return 结果
     */
    @Override
    public int updateHandleGroup(HandleGroup handleGroup)
    {
        handleGroup.setUpdateTime(DateUtil.getNowDateTimeString());
        return handleGroupMapper.updateHandleGroup(handleGroup);
    }

    /**
     * 批量删除分组管理
     * 
     * @param ids 需要删除的分组管理主键
     * @return 结果
     */
    @Override
    public int deleteHandleGroupByIds(Long[] ids)
    {
        return handleGroupMapper.deleteHandleGroupByIds(ids);
    }

    /**
     * 删除分组管理信息
     * 
     * @param id 分组管理主键
     * @return 结果
     */
    @Override
    public int deleteHandleGroupById(Long id)
    {
        return handleGroupMapper.deleteHandleGroupById(id);
    }
}
