package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.HandleMapper;
import com.deer.wcs.base.model.Handle;
import com.deer.wcs.base.model.HandleCriteria;
import com.deer.wcs.base.model.HandleDto;
import com.deer.wcs.base.service.HandleService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 执行器Service业务层处理
 * 
 * @author deer
 * @date 2024-04-28
 */
@Service
public class HandleServiceImpl  extends AbstractService<Handle, Long>  implements HandleService
{
    @Autowired
    private HandleMapper handleMapper;

    @Override
    public void save(Handle model) {
        model.setIsDelete(0);
        model.setVersion(0);
        super.save(model);
    }

    /**
     * 查询执行器
     *
     * @param id 执行器主键
     * @return 执行器
     */
    @Override
    public Handle selectHandleById(Long id)
    {
        return handleMapper.selectHandleById(id);
    }

    /**
     * 查询执行器列表
     * 
     * @param criteria
     * @return 执行器
     */
    @Override
    public List<HandleDto> findList(HandleCriteria criteria)
    {
        return handleMapper.findList(criteria);
    }

    /**
     * 新增执行器
     *
     * @param handle 执行器
     * @return 结果
     */
    @Override
    public int insertHandle(Handle handle)
    {
        handle.setCreateTime(DateUtil.getNowDateTimeString());
        return handleMapper.insertHandle(handle);
    }

    /**
     * 修改执行器
     *
     * @param handle 执行器
     * @return 结果
     */
    @Override
    public int updateHandle(Handle handle)
    {
        return handleMapper.updateHandle(handle);
    }

    /**
     * 批量删除执行器
     * 
     * @param ids 需要删除的执行器主键
     * @return 结果
     */
    @Override
    public int deleteHandleByIds(Long[] ids)
    {
        return handleMapper.deleteHandleByIds(ids);
    }

    /**
     * 删除执行器信息
     * 
     * @param id 执行器主键
     * @return 结果
     */
    @Override
    public int deleteHandleById(Long id)
    {
        return handleMapper.deleteHandleById(id);
    }

    @Override
    public List<HandleDto> findHandleByType(HandleCriteria criteria) {
        return handleMapper.findHandleByType(criteria);
    }
}
