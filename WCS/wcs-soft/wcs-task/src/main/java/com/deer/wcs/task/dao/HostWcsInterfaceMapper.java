package com.deer.wcs.task.dao;


import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.HostWcsInterface;
import com.deer.wcs.task.model.HostWcsInterfaceCriteria;
import com.deer.wcs.task.model.HostWcsInterfaceDto;

import java.util.List;

/**
 * 接口记录Mapper接口
 * 
 * @author deer
 * @date 2024-05-23
 */
public interface HostWcsInterfaceMapper  extends Mapper<HostWcsInterface>
{
    /**
     * 查询接口记录
     *
     * @param id 接口记录主键
     * @return 接口记录
     */
    public HostWcsInterface selectHostWcsInterfaceById(Long id);

    /**
     * 查询接口记录列表
     * 
     * @param criteria 接口记录
     * @return 接口记录集合
     */
    public List<HostWcsInterfaceDto> findList(HostWcsInterfaceCriteria criteria);

    /**
     * 新增接口记录
     *
     * @param hostWcsInterface 接口记录
     * @return 结果
     */
    public int insertHostWcsInterface(HostWcsInterface hostWcsInterface);

    /**
     * 修改接口记录
     *
     * @param hostWcsInterface 接口记录
     * @return 结果
     */
    public int updateHostWcsInterface(HostWcsInterface hostWcsInterface);

    /**
     * 删除接口记录
     * 
     * @param id 接口记录主键
     * @return 结果
     */
    public int deleteHostWcsInterfaceById(Long id);

    /**
     * 批量删除接口记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteHostWcsInterfaceByIds(Long[] ids);
}
