package com.deer.wcs.task.dao;

import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfo;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfoCriteria;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfoDto;

import java.util.List;

/**
 * 呼叫盒Mapper接口
 * 
 * @author deer
 * @date 2024-08-02
 */
public interface CallBoxInfoMapper  extends Mapper<CallBoxInfo>
{
    /**
     * 查询呼叫盒
     *
     * @param id 呼叫盒主键
     * @return 呼叫盒
     */
    public CallBoxInfo selectCallBoxInfoById(Long id);

    /**
     * 查询呼叫盒列表
     * 
     * @param criteria 呼叫盒
     * @return 呼叫盒集合
     */
    public List<CallBoxInfoDto> findList(CallBoxInfoCriteria criteria);

    /**
     * 新增呼叫盒
     *
     * @param callBoxInfo 呼叫盒
     * @return 结果
     */
    public int insertCallBoxInfo(CallBoxInfo callBoxInfo);

    /**
     * 修改呼叫盒
     *
     * @param callBoxInfo 呼叫盒
     * @return 结果
     */
    public int updateCallBoxInfo(CallBoxInfo callBoxInfo);

    /**
     * 删除呼叫盒
     * 
     * @param id 呼叫盒主键
     * @return 结果
     */
    public int deleteCallBoxInfoById(Long id);

    /**
     * 批量删除呼叫盒
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCallBoxInfoByIds(Long[] ids);
}
