package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.ProProcess;
import com.deer.wcs.base.model.ProProcessDto;
import com.deer.wcs.base.model.ProProcessCriteria;

/**
 * 工序Service接口
 * 
 * @author deer
 * @date 2024-11-21
 */
public interface ProProcessService   extends Service<ProProcess, Long>
{
    /**
     * 查询工序
     *
     * @param id 工序主键
     * @return 工序
     */
    public ProProcess selectProProcessById(Long id);

    /**
     * 查询工序列表
     * 
     * @param criteria
     * @return 工序集合
     */
    public List<ProProcessDto> findList(ProProcessCriteria criteria);

    /**
     * 新增工序
     *
     * @param proProcess 工序
     * @return 结果
     */
    public int insertProProcess(ProProcess proProcess);

    /**
     * 修改工序
     *
     * @param proProcess 工序
     * @return 结果
     */
    public int updateProProcess(ProProcess proProcess);

    /**
     * 批量删除工序
     * 
     * @param ids 需要删除的工序主键集合
     * @return 结果
     */
    public int deleteProProcessByIds(Long[] ids);

    /**
     * 删除工序信息
     * 
     * @param id 工序主键
     * @return 结果
     */
    public int deleteProProcessById(Long id);
}
