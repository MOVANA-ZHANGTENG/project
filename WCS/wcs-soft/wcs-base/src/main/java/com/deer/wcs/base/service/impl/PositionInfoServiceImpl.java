package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.PositionInfoMapper;
import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.model.PositionInfoCriteria;
import com.deer.wcs.base.model.PositionInfoDto;
import com.deer.wcs.base.model.PositionRecord;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.base.service.PositionRecordService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * 站台Service业务层处理
 * 
 * @author deer
 * @date 2024-04-28
 */
@Service
public class PositionInfoServiceImpl  extends AbstractService<PositionInfo, Long>  implements PositionInfoService
{
    @Autowired
    private PositionInfoMapper positionInfoMapper;

    @Override
    public PositionInfo findByCode(String wareCode, String code) {
        Condition condition = new Condition(PositionInfo.class);
        condition.createCriteria().andEqualTo("wareCode", wareCode)
                .andEqualTo("code", code);
        List<PositionInfo> list = this.findByCondition(condition);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1)
            throw new RuntimeException(wareCode+"重复的code"+code);
        return list.get(0);
    }

    @Override
    public PositionInfo findBySubCode(String wareCode, String code) {
        Condition condition = new Condition(PositionInfo.class);
        condition.createCriteria().andEqualTo("wareCode", wareCode)
                .andEqualTo("subCode", code);
        List<PositionInfo> list = this.findByCondition(condition);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1)
            throw new RuntimeException(wareCode+"重复的subCode"+code);
        return list.get(0);
    }

    @Autowired
    private PositionRecordService positionRecordService;

    @Override
    public void updateMemo(PositionInfo positionInfo, String msg,Integer type) {
        if(msg==null || msg.trim().isEmpty()){
           throw new RuntimeException();
        }
        if(positionInfo.getMemo()==null || !positionInfo.getMemo().equals(msg)){
            positionInfo.setMemo(msg);
            PositionRecord positionRecord = new PositionRecord();
            positionRecord.setContent(msg);
            positionRecord.setType(type);
            positionRecord.setPositionId(positionInfo.getId());
            positionRecord.setCreateTime(DateUtil.getNowDateTimeString());
            positionRecordService.save(positionRecord);
            super.update(positionInfo);
        }
    }

    /**
     * 查询站台
     *
     * @param id 站台主键
     * @return 站台
     */
    @Override
    public PositionInfo selectPositionInfoById(Long id)
    {
        return positionInfoMapper.selectPositionInfoById(id);
    }

    /**
     * 查询站台列表
     * 
     * @param criteria
     * @return 站台
     */
    @Override
    public List<PositionInfoDto> findList(PositionInfoCriteria criteria)
    {
        return positionInfoMapper.findList(criteria);
    }

    /**
     * 新增站台
     *
     * @param positionInfo 站台
     * @return 结果
     */
    @Override
    public int insertPositionInfo(PositionInfo positionInfo)
    {
        positionInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return positionInfoMapper.insertPositionInfo(positionInfo);
    }

    /**
     * 修改站台
     *
     * @param positionInfo 站台
     * @return 结果
     */
    @Override
    public int updatePositionInfo(PositionInfo positionInfo)
    {
        positionInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        return positionInfoMapper.updatePositionInfo(positionInfo);
    }

    /**
     * 批量删除站台
     * 
     * @param ids 需要删除的站台主键
     * @return 结果
     */
    @Override
    public int deletePositionInfoByIds(Long[] ids)
    {
        return positionInfoMapper.deletePositionInfoByIds(ids);
    }

    /**
     * 删除站台信息
     * 
     * @param id 站台主键
     * @return 结果
     */
    @Override
    public int deletePositionInfoById(Long id)
    {
        return positionInfoMapper.deletePositionInfoById(id);
    }

    @Override
    public List<PositionInfo> findByWareCode(String wareCode) {
        return positionInfoMapper.findByWareCode(wareCode);
    }

    @Override
    public List<PositionInfo> findByParentCode(String parentCode) {
        return positionInfoMapper.findByParentCode(parentCode);
    }

    @Override
    public PositionInfo findByIdForUpdate(Long positionId) {
        return positionInfoMapper.findByIdForUpdate(positionId);
    }
}
