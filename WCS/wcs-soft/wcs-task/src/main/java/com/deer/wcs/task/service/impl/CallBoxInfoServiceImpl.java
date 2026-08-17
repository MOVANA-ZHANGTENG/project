package com.deer.wcs.task.service.impl;

import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.dao.CallBoxInfoMapper;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfo;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfoCriteria;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfoDto;
import com.deer.wcs.task.service.CallBoxInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 呼叫盒Service业务层处理
 * 
 * @author deer
 * @date 2024-08-02
 */
@Service
public class CallBoxInfoServiceImpl  extends AbstractService<CallBoxInfo, Long>  implements CallBoxInfoService
{
    @Autowired
    private CallBoxInfoMapper callBoxInfoMapper;

    @Override
    public void save(CallBoxInfo model) {
        model.setDelFlag(0);
        super.save(model);
    }

    @Override
    public int update(CallBoxInfo model) {
        model.setUpdateTime(DateUtil.getNowDateTimeString());
        return super.update(model);
    }

    /**
     * 查询呼叫盒
     *
     * @param id 呼叫盒主键
     * @return 呼叫盒
     */
    @Override
    public CallBoxInfo selectCallBoxInfoById(Long id)
    {
        return callBoxInfoMapper.selectCallBoxInfoById(id);
    }

    /**
     * 查询呼叫盒列表
     * 
     * @param criteria
     * @return 呼叫盒
     */
    @Override
    public List<CallBoxInfoDto> findList(CallBoxInfoCriteria criteria)
    {
        return callBoxInfoMapper.findList(criteria);
    }

    /**
     * 新增呼叫盒
     *
     * @param callBoxInfo 呼叫盒
     * @return 结果
     */
    @Override
    public int insertCallBoxInfo(CallBoxInfo callBoxInfo)
    {
        callBoxInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return callBoxInfoMapper.insertCallBoxInfo(callBoxInfo);
    }

    /**
     * 修改呼叫盒
     *
     * @param callBoxInfo 呼叫盒
     * @return 结果
     */
    @Override
    public int updateCallBoxInfo(CallBoxInfo callBoxInfo)
    {
        callBoxInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        return callBoxInfoMapper.updateCallBoxInfo(callBoxInfo);
    }

    /**
     * 批量删除呼叫盒
     * 
     * @param ids 需要删除的呼叫盒主键
     * @return 结果
     */
    @Override
    public int deleteCallBoxInfoByIds(Long[] ids)
    {
        return callBoxInfoMapper.deleteCallBoxInfoByIds(ids);
    }

    /**
     * 删除呼叫盒信息
     * 
     * @param id 呼叫盒主键
     * @return 结果
     */
    @Override
    public int deleteCallBoxInfoById(Long id)
    {
        return callBoxInfoMapper.deleteCallBoxInfoById(id);
    }
}
