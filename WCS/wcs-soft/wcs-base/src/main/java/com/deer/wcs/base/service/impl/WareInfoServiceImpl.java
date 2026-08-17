package com.deer.wcs.base.service.impl;

import com.alibaba.fastjson2.JSON;
import com.deer.wcs.base.dao.WareInfoMapper;
import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.*;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 仓库设置Service业务层处理
 * 
 * @author deer
 * @date 2024-04-28
 */
@Service
@Transactional
public class WareInfoServiceImpl  extends AbstractService<WareInfo, Long>  implements WareInfoService
{
    @Autowired
    private WareInfoMapper wareInfoMapper;
    @Autowired
    private AreaInfoService areaInfoService;
    @Autowired
    private LineInfoService lineInfoService;
    @Autowired
    private PositionInfoService positionInfoService;
    @Autowired
    private PositionConditionService positionConditionService;
    @Autowired
    private PositionHandleService positionHandleService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private PositionStepService positionStepService;

    @Override
    public void save(WareInfo model) {
        WareInfo ware = super.findBy("code",model.getCode());
        if(ware!=null){
            throw new ServiceException("仓库编码重复");
        }
          ware = super.findBy("name",model.getName());
        if(ware!=null){
            throw new ServiceException("仓库名称重复");
        }
        model.setIsTest("0");
        model.setIsDelete(0);
        model.setDisableState(0);
        model.setVersion(0);
        super.save(model);

    }


    /**
     * 只更新wareInfo
     * @param model
     * @return
     */
    @Override
    public int update(WareInfo model) {
        WareInfo ware = super.findBy("code",model.getCode());
        if(ware!=null && !ware.getId().equals(model.getId())){
            throw new ServiceException("仓库编码重复");
        }
        ware = super.findBy("name",model.getName());
        if(ware!=null && !ware.getId().equals(model.getId())){
            throw new ServiceException("仓库名称重复");
        }

        WareInfo oldWare = findById(model.getId());
        if(oldWare==null){
            throw new RuntimeException("找不到对应的仓库数据");
        }
        if(!model.getCode().equals(oldWare.getCode())||!model.getName().equals(oldWare.getName())){
            WareInfoUpdate update = new WareInfoUpdate();
            update.setOldWareCode(oldWare.getCode());
            update.setNewWareCode(model.getCode());
            update.setNewWareName(model.getName());
            wareInfoMapper.updateAllLinkWareId(update);
        }
        return super.update(model);
    }

    /**
     * 更新wareInfo和其他相关联的表信息
     * @param model 模型设置
     * @return
     */
    public int updateWareModel(WareInfo model) {
        WareInfo ware = super.findBy("code",model.getCode());
        if(ware!=null && !ware.getId().equals(model.getId())){
            throw new ServiceException("仓库编码重复");
        }
        ware = super.findBy("name",model.getName());
        if(ware!=null && !ware.getId().equals(model.getId())){
            throw new ServiceException("仓库编码重复");
        }
        saveModel(model);
        redisCache.deleteObject("position-condition:"+model.getCode());
        return super.update(model);
    }


    public void saveModel(WareInfo model){
        /*
            删除 lineInfo
            删除 positionInfo
            删除 positionCondition
         */
        wareInfoMapper.deleteModel(model.getCode());
        WareModel wareModel = JSON.parseObject(model.getModelData(), WareModel.class);
          wareModel = JSON.parseObject(model.getModelData(), WareModel.class);
          List<Node> nodes = wareModel.getNodeDataArray();
          List<Link> links = wareModel.getLinkDataArray();
          //保存站台信息
        for (Node node: nodes) {
            if("line".equals(node.getCategory())){
                LineInfo lineInfo = new LineInfo();
                lineInfo.setCode(node.getCode());
                lineInfo.setName(node.getName());
                lineInfo.setType(node.getCategory());
                lineInfo.setWareCode(model.getCode());
                lineInfo.setWareName(model.getName());
//                lineInfo.setAreaCode(node.getAreaCode());
//                lineInfo.setAreaName(node.getAreaName());
                lineInfo.setCreateUserId(model.getCreateUserId());
                lineInfo.setCreateUserName(model.getCreateUserName());
                lineInfoService.save(lineInfo);
                continue;
            }
            PositionInfo positionInfo = new PositionInfo();
            if(node.getGroup()!=null){
                List<Node> parentNodes = nodes.stream().filter(s->s.getKey()==node.getGroup()).collect(Collectors.toList());
                if(parentNodes.size()==1){
                    positionInfo.setParentCode(parentNodes.get(0).getCode());
                }
            }
            positionInfo.setCode(node.getCode());
            positionInfo.setName(node.getName());
            positionInfo.setType(node.getCategory());
            positionInfo.setIsGroup(node.getIsGroup());
            positionInfo.setState(0);
            positionInfo.setIsDelete(0);
            positionInfo.setCreateTime(DateUtil.getNowDateTimeString());
            positionInfo.setCreateUserId(model.getCreateUserId());
            positionInfo.setCreateUserName(model.getCreateUserName());
            positionInfo.setVersion(0);
            positionInfo.setWareCode(model.getCode());
            positionInfo.setWareName(model.getName());
            positionInfoService.save(positionInfo);
        }
        //保存流程信息
        for (Link link:links) {
            PositionCondition pCondition = new PositionCondition();
            pCondition.setStepCode(link.getCode());
            if(link.getTemplateCode()!=null&&!link.getTemplateCode().equals("")){
                PositionStep step = positionStepService.findBy("code",link.getTemplateCode());
                if(step!=null){
                    pCondition.setStepName(step.getName());
                }
            }
            for(Node node:nodes){
                if(link.getFrom().equals(node.getKey().toString())){
                    pCondition.setFromCode(node.getCode());
                }
            }
            for(Node node:nodes){
                if(link.getTo().equals(node.getKey().toString())){
                    pCondition.setToCode(node.getCode());
                }
            }
            pCondition.setTaskTime(link.getTaskTime());
            pCondition.setBlockingTime(link.getBlockingTime());
            pCondition.setWareCode(model.getCode());
            pCondition.setWareName(model.getName());
            pCondition.setTemplateCode(link.getTemplateCode());
            positionConditionService.save(pCondition);
        }


    }


    /**
     * 查询仓库设置
     *
     * @param id 仓库设置主键
     * @return 仓库设置
     */
    @Override
    public WareInfo selectWareInfoById(Long id)
    {
        return wareInfoMapper.selectWareInfoById(id);
    }

    /**
     * 查询仓库设置列表
     * 
     * @param criteria
     * @return 仓库设置
     */
    @Override
    public List<WareInfoDto> findList(WareInfoCriteria criteria)
    {
        return wareInfoMapper.findList(criteria);
    }

    /**
     * 新增仓库设置
     *
     * @param wareInfo 仓库设置
     * @return 结果
     */
    @Override
    public int insertWareInfo(WareInfo wareInfo)
    {
        wareInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return wareInfoMapper.insertWareInfo(wareInfo);
    }

    /**
     * 修改仓库设置
     *
     * @param wareInfo 仓库设置
     * @return 结果
     */
    @Override
    public int updateWareInfo(WareInfo wareInfo)
    {
        wareInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        return wareInfoMapper.updateWareInfo(wareInfo);
    }

    /**
     * 批量删除仓库设置
     * 
     * @param ids 需要删除的仓库设置主键
     * @return 结果
     */
    @Override
    public int deleteWareInfoByIds(Long[] ids)
    {
        return wareInfoMapper.deleteWareInfoByIds(ids);
    }

    /**
     * 删除仓库设置信息
     * 
     * @param id 仓库设置主键
     * @return 结果
     */
    @Override
    public int deleteWareInfoById(Long id)
    {
        return wareInfoMapper.deleteWareInfoById(id);
    }

    @Override
    public List<WareInfoDto> findAllWareInfos() {
        return wareInfoMapper.findAllWareInfos();
    }
}
