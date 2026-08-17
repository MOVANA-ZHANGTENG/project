package com.deer.wcs.system.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.system.model.SystemRequst;
import com.deer.wcs.system.model.SystemRequstDto;
import com.deer.wcs.system.model.SystemRequstCriteria;

/**
 * 接口调用Service接口
 * 
 * @author deer
 * @date 2023-11-16
 */
public interface SystemRequstService   extends Service<SystemRequst, Integer>
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
     * @param criteria
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
     * 批量删除接口调用
     * 
     * @param requestIds 需要删除的接口调用主键集合
     * @return 结果
     */
    public int deleteSystemRequstByRequestIds(Integer[] requestIds);

    /**
     * 删除接口调用信息
     * 
     * @param requestId 接口调用主键
     * @return 结果
     */
    public int deleteSystemRequstByRequestId(Integer requestId);
}
