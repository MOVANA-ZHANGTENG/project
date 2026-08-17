package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.LineInfoMapper;
import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.LineInfoService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * 巷道Service业务层处理
 * 
 * @author deer
 * @date 2024-04-28
 */
@Service
public class LineInfoServiceImpl  extends AbstractService<LineInfo, Long>  implements LineInfoService
{
    @Autowired
    private LineInfoMapper lineInfoMapper;

    @Override
    public void save(LineInfo model) {
        model.setInvenState(0L);
        model.setTaskState(0L);
        model.setDisableState(0L);
        model.setIsDelete(0);
        model.setVersion(0);
        model.setType("line");
        model.setCreateTime(DateUtil.getNowDateTimeString());
        super.save(model);
    }

    @Override
    public int update(LineInfo model) {
        LineInfo oldLine = findById(model.getId());
        if(oldLine==null){
            throw new RuntimeException("找不到对应的库区数据");
        }
        if(!model.getCode().equals(oldLine.getCode())||!model.getName().equals(oldLine.getName())){
            WareInfoUpdate update = new WareInfoUpdate();
            update.setOldLineCode(oldLine.getCode());
            update.setNewLineCode(model.getCode());
            update.setNewLineName(model.getName());
            lineInfoMapper.updateAllLinkLine(update);
        }
        return super.update(model);
    }

    @Override
    public LineInfo allotLine(String wareCode) {

        return lineInfoMapper.allotLine(wareCode);
    }

    @Override
    public LineInfo findByCode(String wareCode, String lineCode) {
        Condition condition = new Condition(LineInfo.class);
        condition.createCriteria().andEqualTo("wareCode", wareCode).andEqualTo("code", lineCode);
        List<LineInfo> list = lineInfoMapper.selectByCondition(condition);
        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    /**
     * 查询巷道
     *
     * @param id 巷道主键
     * @return 巷道
     */
    @Override
    public LineInfo selectLineInfoById(Integer id)
    {
        return lineInfoMapper.selectLineInfoById(id);
    }

    /**
     * 查询巷道列表
     * 
     * @param criteria
     * @return 巷道
     */
    @Override
    public List<LineInfoDto> findList(LineInfoCriteria criteria)
    {
        return lineInfoMapper.findList(criteria);
    }

    /**
     * 新增巷道
     *
     * @param lineInfo 巷道
     * @return 结果
     */
    @Override
    public int insertLineInfo(LineInfo lineInfo)
    {
        lineInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return lineInfoMapper.insertLineInfo(lineInfo);
    }

    /**
     * 修改巷道
     *
     * @param lineInfo 巷道
     * @return 结果
     */
    @Override
    public int updateLineInfo(LineInfo lineInfo)
    {
        lineInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        return lineInfoMapper.updateLineInfo(lineInfo);
    }

    /**
     * 批量删除巷道
     * 
     * @param ids 需要删除的巷道主键
     * @return 结果
     */
    @Override
    public Integer deleteLineInfoByIds(Long[] ids)
    {
        return lineInfoMapper.deleteLineInfoByIds(ids);
    }

    /**
     * 删除巷道信息
     * 
     * @param id 巷道主键
     * @return 结果
     */
    @Override
    public int deleteLineInfoById(Integer id)
    {
        return lineInfoMapper.deleteLineInfoById(id);
    }
}
