package com.deer.wcs.system.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.system.model.SystemRequst;
import com.deer.wcs.system.model.SystemRequstDto;
import com.deer.wcs.system.model.SystemRequstCriteria;

/**
 * 接口调用Mapper接口
 * 
 * @author deer
 * @date 2023-11-16
 */
public interface SystemRequstMapper  extends Mapper<SystemRequst>
{
    /**
     * 查询接口调用
     *
     * @param requestId 接口调用主键
     * @return 接口调用
     */
    public SystemRequst selectSystemRequstByRequestId(Integer requestId);

    /**
     * 查询接口调用列表
     * 
     * @param systemRequst 接口调用
     * @return 接口调用集合
     */
    public List<SystemRequstDto> findList(SystemRequstCriteria criteria);

    /**
     * 新增接口调用
     *
     * @param systemRequst 接口调用
     * @return 结果
     */
    public int insertSystemRequst(SystemRequst systemRequst);

    /**
     * 修改接口调用
     *
     * @param systemRequst 接口调用
     * @return 结果
     */
    public int updateSystemRequst(SystemRequst systemRequst);

    /**
     * 删除接口调用
     * 
     * @param requestId 接口调用主键
     * @return 结果
     */
    public int deleteSystemRequstByRequestId(Integer requestId);

    /**
     * 批量删除接口调用
     * 
     * @param requestIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSystemRequstByRequestIds(Integer[] requestIds);
}
