package com.deer.wcs.base.service.impl;

import java.util.Collections;
import java.util.List;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.ProPositionContentMapper;
import com.deer.wcs.base.service.ProPositionContentService;
import tk.mybatis.mapper.entity.Condition;

/**
 * 站台扩展Service业务层处理
 * 
 * @author deer
 * @date 2024-11-21
 */
@Service
public class ProPositionContentServiceImpl  extends AbstractService<ProPositionContent, Long>  implements ProPositionContentService
{



    @Autowired
    private ProPositionContentMapper ProPositionContentMapper;

    @Autowired
    private PositionInfoService positionInfoService;

    @Override
    public ProPositionContent findByType(String itemCode, Integer type, Long invenState, Integer palletState) {
        return ProPositionContentMapper.findByType(itemCode,type,invenState,palletState);
    }

    @Override
    public ProPositionContent findByTypes(String itemCode, Long invenState, Integer palletState, List<Integer> types) {
        return ProPositionContentMapper.findByTypes(itemCode, invenState, palletState, types);
    }
    @Override
    public  ProPositionContent selectType1(@Param("itemCode") String itemCode, @Param("proLineCode")  String proLineCode){
        return ProPositionContentMapper.selectType1(itemCode, proLineCode);
    }

    @Override
    public ProPositionContent findByCode(String wareCode, String code) {
        PositionInfo positionInfo = positionInfoService.findByCode(wareCode, code);
        if(positionInfo == null){
            return null;
        }
        return  super.findBy("positionId", positionInfo.getId());
    }

    /**
     * 查询站台扩展
     *
     * @param id 站台扩展主键
     * @return 站台扩展
     */
    @Override
    public ProPositionContent selectProPositionContentById(Long id)
    {
        return ProPositionContentMapper.selectProPositionContentById(id);
    }

    @Override
    public List<ContentReport> report() {
        return ProPositionContentMapper.report();
    }

    @Override
    public List<ContentReport> report2(String wareCode) {
        return ProPositionContentMapper.report2(wareCode);
    }

    /**
     * 查询站台扩展列表
     * 
     * @param criteria
     * @return 站台扩展
     */
    @Override
    public List<ProPositionContentDto> findList(ProPositionContentCriteria criteria)
    {
        return ProPositionContentMapper.findList(criteria);
    }

    /**
     * 新增站台扩展
     *
     * @param ProPositionContent 站台扩展
     * @return 结果
     */
    @Override
    public int insertProPositionContent(ProPositionContent ProPositionContent)
    {
        return ProPositionContentMapper.insertProPositionContent(ProPositionContent);
    }

    /**
     * 修改站台扩展
     *
     * @param ProPositionContent 站台扩展
     * @return 结果
     */
    @Override
    public int updateProPositionContent(ProPositionContent ProPositionContent)
    {
        return ProPositionContentMapper.updateProPositionContent(ProPositionContent);
    }

    /**
     * 批量删除站台扩展
     * 
     * @param ids 需要删除的站台扩展主键
     * @return 结果
     */
    @Override
    public int deleteProPositionContentByIds(Long[] ids)
    {
        return ProPositionContentMapper.deleteProPositionContentByIds(ids);
    }

    /**
     * 删除站台扩展信息
     * 
     * @param id 站台扩展主键
     * @return 结果
     */
    @Override
    public int deleteProPositionContentById(Long id)
    {
        return ProPositionContentMapper.deleteProPositionContentById(id);
    }

    @Override
    public int updateNull(ProPositionContent model) {
        return ProPositionContentMapper.updateNull(model);
    }

    @Override
    public List<ProPositionContent> findByYangJIDeviceCode() {
        return ProPositionContentMapper.findByYangJIDeviceCode();
    }

    @Override
    public ProPositionContent findByKong(List<String> codes) {
        return ProPositionContentMapper.findByKong(codes);
    }
}
