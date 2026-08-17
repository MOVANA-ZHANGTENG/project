package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.ProProcessMapper;
import com.deer.wcs.base.model.ProProcess;
import com.deer.wcs.base.model.ProProcessDto;
import com.deer.wcs.base.model.ProProcessCriteria;
import com.deer.wcs.base.service.ProProcessService;

/**
 * 工序Service业务层处理
 * 
 * @author deer
 * @date 2024-11-21
 */
@Service
public class ProProcessServiceImpl  extends AbstractService<ProProcess, Long>  implements ProProcessService
{
    @Autowired
    private ProProcessMapper proProcessMapper;

    /**
     * 查询工序
     *
     * @param id 工序主键
     * @return 工序
     */
    @Override
    public ProProcess selectProProcessById(Long id)
    {
        return proProcessMapper.selectProProcessById(id);
    }

    /**
     * 查询工序列表
     * 
     * @param criteria
     * @return 工序
     */
    @Override
    public List<ProProcessDto> findList(ProProcessCriteria criteria)
    {
        return proProcessMapper.findList(criteria);
    }

    /**
     * 新增工序
     *
     * @param proProcess 工序
     * @return 结果
     */
    @Override
    public int insertProProcess(ProProcess proProcess)
    {
        return proProcessMapper.insertProProcess(proProcess);
    }

    /**
     * 修改工序
     *
     * @param proProcess 工序
     * @return 结果
     */
    @Override
    public int updateProProcess(ProProcess proProcess)
    {
        return proProcessMapper.updateProProcess(proProcess);
    }

    /**
     * 批量删除工序
     * 
     * @param ids 需要删除的工序主键
     * @return 结果
     */
    @Override
    public int deleteProProcessByIds(Long[] ids)
    {
        return proProcessMapper.deleteProProcessByIds(ids);
    }

    /**
     * 删除工序信息
     * 
     * @param id 工序主键
     * @return 结果
     */
    @Override
    public int deleteProProcessById(Long id)
    {
        return proProcessMapper.deleteProProcessById(id);
    }
}
