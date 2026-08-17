package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.WarnMsgMapper;
import com.deer.wcs.base.model.WarnMsg;
import com.deer.wcs.base.model.WarnMsgDto;
import com.deer.wcs.base.model.WarnMsgCriteria;
import com.deer.wcs.base.service.WarnMsgService;

/**
 * 报警代码Service业务层处理
 * 
 * @author deer
 * @date 2025-09-24
 */
@Service
public class WarnMsgServiceImpl  extends AbstractService<WarnMsg, Long>  implements WarnMsgService
{
    @Autowired
    private WarnMsgMapper warnMsgMapper;

    /**
     * 查询报警代码
     *
     * @param id 报警代码主键
     * @return 报警代码
     */
    @Override
    public WarnMsg selectWarnMsgById(Long id)
    {
        return warnMsgMapper.selectWarnMsgById(id);
    }

    /**
     * 查询报警代码列表
     * 
     * @param criteria
     * @return 报警代码
     */
    @Override
    public List<WarnMsgDto> findList(WarnMsgCriteria criteria)
    {
        return warnMsgMapper.findList(criteria);
    }

    /**
     * 新增报警代码
     *
     * @param warnMsg 报警代码
     * @return 结果
     */
    @Override
    public int insertWarnMsg(WarnMsg warnMsg)
    {
        return warnMsgMapper.insertWarnMsg(warnMsg);
    }

    /**
     * 修改报警代码
     *
     * @param warnMsg 报警代码
     * @return 结果
     */
    @Override
    public int updateWarnMsg(WarnMsg warnMsg)
    {
        return warnMsgMapper.updateWarnMsg(warnMsg);
    }

    /**
     * 批量删除报警代码
     * 
     * @param ids 需要删除的报警代码主键
     * @return 结果
     */
    @Override
    public int deleteWarnMsgByIds(Long[] ids)
    {
        return warnMsgMapper.deleteWarnMsgByIds(ids);
    }

    /**
     * 删除报警代码信息
     * 
     * @param id 报警代码主键
     * @return 结果
     */
    @Override
    public int deleteWarnMsgById(Long id)
    {
        return warnMsgMapper.deleteWarnMsgById(id);
    }

    @Override
    public WarnMsg findByCodeAndType(int troubleCode, Integer type) {
        return warnMsgMapper.findByCodeAndType(troubleCode, type);
    }
}
