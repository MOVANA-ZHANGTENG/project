package com.deer.wcs.system.service;

import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.system.model.Auto;
import com.deer.wcs.system.model.AutoCriteria;
import com.deer.wcs.system.model.AutoDto;

import java.util.List;

/**
 * 当日自增长Service接口
 * 
 * @author deer
 * @date 2023-10-23
 */
public interface AutoService   extends Service<Auto, Integer>
{
    //丹式机器人获取自增长
    String getDsPalletCode();
    String getReqCode();
    String getTaskNo();
    Long getTaskInfoId();
    Long getJobInfoId();
    Long getPathInfoId();
    Long getJobHandleId();
    Long getPathHandleId();
    String getHtCrnTaskNo();
    String getId();

    Integer getTodayTaskNo();


    /**
     * 查询当日自增长
     *
     * @param id 当日自增长主键
     * @return 当日自增长
     */
    public Auto selectAutoById(Integer id);

    /**
     * 查询当日自增长列表
     * 
     * @param criteria
     * @return 当日自增长集合
     */
    public List<AutoDto> findList(AutoCriteria criteria);

    /**
     * 新增当日自增长
     *
     * @param auto 当日自增长
     * @return 结果
     */
    public int insertAuto(Auto auto);

    /**
     * 修改当日自增长
     *
     * @param auto 当日自增长
     * @return 结果
     */
    public int updateAuto(Auto auto);

    /**
     * 批量删除当日自增长
     * 
     * @param ids 需要删除的当日自增长主键集合
     * @return 结果
     */
    public int deleteAutoByIds(Integer[] ids);

    /**
     * 删除当日自增长信息
     * 
     * @param id 当日自增长主键
     * @return 结果
     */
    public int deleteAutoById(Integer id);
}
