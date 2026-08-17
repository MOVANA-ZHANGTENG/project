package com.deer.wcs.system.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.system.dao.SystemRequstMapper;
import com.deer.wcs.system.model.SystemRequst;
import com.deer.wcs.system.model.SystemRequstDto;
import com.deer.wcs.system.model.SystemRequstCriteria;
import com.deer.wcs.system.service.SystemRequstService;

/**
 * 接口调用Service业务层处理
 * 
 * @author deer
 * @date 2023-11-16
 */
@Service
public class SystemRequstServiceImpl  extends AbstractService<SystemRequst, Integer>  implements SystemRequstService
{
    @Autowired
    private SystemRequstMapper systemRequstMapper;

    /**
     * 查询接口调用
     *
     * @param requestId 接口调用主键
     * @return 接口调用
     */
    @Override
    public SystemRequst selectSystemRequstByRequestId(Integer requestId)
    {
        return systemRequstMapper.selectSystemRequstByRequestId(requestId);
    }

    /**
     * 查询接口调用列表
     * 
     * @param criteria
     * @return 接口调用
     */
    @Override
    public List<SystemRequstDto> findList(SystemRequstCriteria criteria)
    {
        return systemRequstMapper.findList(criteria);
    }

    /**
     * 新增接口调用
     *
     * @param systemRequst 接口调用
     * @return 结果
     */
    @Override
    public int insertSystemRequst(SystemRequst systemRequst)
    {
        systemRequst.setCreateTime(DateUtil.getNowDateTimeString());
        return systemRequstMapper.insertSystemRequst(systemRequst);
    }

    /**
     * 修改接口调用
     *
     * @param systemRequst 接口调用
     * @return 结果
     */
    @Override
    public int updateSystemRequst(SystemRequst systemRequst)
    {
        return systemRequstMapper.updateSystemRequst(systemRequst);
    }

    /**
     * 批量删除接口调用
     * 
     * @param requestIds 需要删除的接口调用主键
     * @return 结果
     */
    @Override
    public int deleteSystemRequstByRequestIds(Integer[] requestIds)
    {
        return systemRequstMapper.deleteSystemRequstByRequestIds(requestIds);
    }

    /**
     * 删除接口调用信息
     * 
     * @param requestId 接口调用主键
     * @return 结果
     */
    @Override
    public int deleteSystemRequstByRequestId(Integer requestId)
    {
        return systemRequstMapper.deleteSystemRequstByRequestId(requestId);
    }
}
