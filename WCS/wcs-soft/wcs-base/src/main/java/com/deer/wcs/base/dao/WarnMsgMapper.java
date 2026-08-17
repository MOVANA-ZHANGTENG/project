package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.WarnMsg;
import com.deer.wcs.base.model.WarnMsgDto;
import com.deer.wcs.base.model.WarnMsgCriteria;
import org.apache.ibatis.annotations.Param;

/**
 * 报警代码Mapper接口
 * 
 * @author deer
 * @date 2025-09-24
 */
public interface WarnMsgMapper  extends Mapper<WarnMsg>
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
     * @param warnMsg 报警代码
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
     * 删除报警代码
     * 
     * @param id 报警代码主键
     * @return 结果
     */
    public int deleteWarnMsgById(Long id);

    /**
     * 批量删除报警代码
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWarnMsgByIds(Long[] ids);

    WarnMsg findByCodeAndType(@Param("troubleCode") int troubleCode, @Param("type") Integer type);
}
