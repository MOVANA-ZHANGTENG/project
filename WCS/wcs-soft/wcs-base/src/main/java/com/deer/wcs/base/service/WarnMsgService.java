package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.WarnMsg;
import com.deer.wcs.base.model.WarnMsgDto;
import com.deer.wcs.base.model.WarnMsgCriteria;

/**
 * 报警代码Service接口
 * 
 * @author deer
 * @date 2025-09-24
 */
public interface WarnMsgService   extends Service<WarnMsg, Long>
{
    /**
     * 查询报警代码
     *
     * @param id 报警代码主键
     * @return 报警代码
     */
    public WarnMsg selectWarnMsgById(Long id);

    /**
     * 查询报警代码列表
     * 
     * @param criteria
     * @return 报警代码集合
     */
    public List<WarnMsgDto> findList(WarnMsgCriteria criteria);

    /**
     * 新增报警代码
     *
     * @param warnMsg 报警代码
     * @return 结果
     */
    public int insertWarnMsg(WarnMsg warnMsg);

    /**
     * 修改报警代码
     *
     * @param warnMsg 报警代码
     * @return 结果
     */
    public int updateWarnMsg(WarnMsg warnMsg);

    /**
     * 批量删除报警代码
     * 
     * @param ids 需要删除的报警代码主键集合
     * @return 结果
     */
    public int deleteWarnMsgByIds(Long[] ids);

    /**
     * 删除报警代码信息
     * 
     * @param id 报警代码主键
     * @return 结果
     */
    public int deleteWarnMsgById(Long id);

    WarnMsg findByCodeAndType(int troubleCode, Integer warnMsgTypeSc);
}
