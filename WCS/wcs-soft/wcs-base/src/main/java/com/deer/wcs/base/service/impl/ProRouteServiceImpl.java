package com.deer.wcs.base.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.ProProcessPositionService;
import com.deer.wcs.base.service.ProProcessService;
import com.deer.wcs.base.service.ProRouteProcessService;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.ProRouteMapper;
import com.deer.wcs.base.service.ProRouteService;

/**
 * 工艺流程Service业务层处理
 * 
 * @author deer
 * @date 2024-11-21
 */
@Service
public class ProRouteServiceImpl  extends AbstractService<ProRoute, Long>  implements ProRouteService
{
    @Autowired
    private ProRouteMapper proRouteMapper;

    @Override
    public void save(ProRoute model) {
        ProRoute route = super.findBy("code",model.getCode());
        if(route!=null){
            throw new ServiceException("流程编码重复");
        }
        route = super.findBy("name",model.getName());
        if(route!=null){
            throw new ServiceException("流程名称重复");
        }


        super.save(model);

    }

    /**
     * 只更新wareInfo
     * @param model
     * @return
     */
    @Override
    public int update(ProRoute model) {
        ProRoute ware = super.findBy("code",model.getCode());
        if(ware!=null && !ware.getId().equals(model.getId())){
            throw new ServiceException("流程编码重复");
        }
        ware = super.findBy("name",model.getName());
        if(ware!=null && !ware.getId().equals(model.getId())){
            throw new ServiceException("流程名称重复");
        }

        ProRoute oldWare = findById(model.getId());
        if(oldWare==null){
            throw new RuntimeException("找不到对应的流程数据");
        }
        updateRouteModel(model);
        return super.update(model);
    }

    /**
     * 更新wareInfo和其他相关联的表信息
     * @param model 模型设置
     * @return
     */
    public int updateRouteModel(ProRoute model) {
        ProRoute ware = super.findById( model.getId());

        saveModel(ware);

        return super.update(ware);
    }

    @Autowired
    private ProRouteProcessService proRouteProcessService;

    @Autowired
    private ProProcessService proProcessService;

    @Autowired
    private ProProcessPositionService proProcessPositionService;

    /**
     * 保存模型数据，包括删除旧数据、解析 JSON 数据、保存节点和链接信息
     *
     * @param model 工艺流程模型
     */
    public void saveModel(ProRoute model){

        // 删除指定 ID 的模型数据
        proRouteMapper.deleteModel(model.getId());
        // 将模型数据解析为 WareModel 对象
        WareModel wareModel  = JSON.parseObject(model.getModelData(), WareModel.class);
        // 获取节点数据列表
        List<Node> nodes = wareModel.getNodeDataArray();
        // 获取链接数据列表
        List<Link> links = wareModel.getLinkDataArray();
        // 保存站台信息
        for (Node node: nodes) {
            // 保存工序表
            if("group".equals(node.getCategory())){
                // 创建工序对象
                ProProcess proProcess = new ProProcess();
                // 设置工序代码
                proProcess.setCode(node.getCode());
                // 设置工序名称
                proProcess.setName(node.getName());
                // 设置工序所属的工艺流程 ID
                proProcess.setProRouteId(model.getId());
                // 保存工序信息
                proProcessService.save(proProcess);
                // 设置节点的 ID 为工序的 ID
                node.setId(proProcess.getId());
                continue;
            }

        }

        for (Node node: nodes) {

            // 保存工序站台关联
            if("position".equals(node.getCategory())){
                // 创建工序站台关联对象
                ProProcessPosition proProcessPosition = new ProProcessPosition();
                // 获取节点的分组键
                Integer key =node.getGroup();
                // 设置节点的 ID 为工序站台关联的 ID
                node.setId(proProcessPosition.getId());
                if(key==null){
                    // 如果分组键为空，抛出异常
                    throw new ServiceException("站台应该放入工序");
                }
                // 遍历所有节点，找到与当前节点分组键相同的节点
                for(Node node2:nodes){
                    if(node.getGroup().equals(node2.getKey())){
                        // 设置工序站台关联的工序 ID
                        proProcessPosition.setProProcessId(node2.getId());
                    }
                }
                // 设置工序站台关联的工艺流程 ID
                proProcessPosition.setProRouteId(model.getId());
                // 设置工序站台关联的站台代码
                proProcessPosition.setPositionCode(node.getCode());
                // 保存工序站台关联信息
                proProcessPositionService.save(proProcessPosition);
                // 设置节点的 ID 为工序站台关联的 ID
                node.setId(proProcessPosition.getId());
            }
        }
        // 保存流程信息
        for (Link link:links) {
            // 创建工艺流程工序对象
            ProRouteProcess proRouteProcess = new ProRouteProcess();
            // 遍历节点列表，找到与链接起始节点键匹配的节点
            for(Node node:nodes){
                if(link.getFrom().equals(node.getKey().toString())){
                    // 设置工艺流程工序的工序 ID
                    proRouteProcess.setProProcessId(node.getId());
                    // 设置工艺流程工序的工序代码
                    proRouteProcess.setProProcessCode(node.getCode());
                }
            }
            // 遍历节点列表，找到与链接目标节点键匹配的节点
            for(Node node:nodes){
                if(link.getTo().equals(node.getKey().toString())){
                    // 设置工艺流程工序的下一个工序 ID
                    proRouteProcess.setNextProPorcessId(node.getId());
                    // 设置工艺流程工序的工序代码
                    proRouteProcess.setProProcessCode(node.getCode());
                }
            }
            // 设置工艺流程工序的工艺流程 ID
            proRouteProcess.setProRouteId(model.getId());
            // 保存工艺流程工序信息
            proRouteProcessService.save(proRouteProcess);

        }


    }


    /**
     * 查询工艺流程
     *
     * @param id 工艺流程主键
     * @return 工艺流程
     */
    @Override
    public ProRoute selectProRouteById(Long id)
    {
        return proRouteMapper.selectProRouteById(id);
    }

    /**
     * 查询工艺流程列表
     * 
     * @param criteria
     * @return 工艺流程
     */
    @Override
    public List<ProRouteDto> findList(ProRouteCriteria criteria)
    {
        return proRouteMapper.findList(criteria);
    }

    /**
     * 新增工艺流程
     *
     * @param proRoute 工艺流程
     * @return 结果
     */
    @Override
    public int insertProRoute(ProRoute proRoute)
    {
        return proRouteMapper.insertProRoute(proRoute);
    }

    /**
     * 修改工艺流程
     *
     * @param proRoute 工艺流程
     * @return 结果
     */
    @Override
    public int updateProRoute(ProRoute proRoute)
    {
        return proRouteMapper.updateProRoute(proRoute);
    }

    /**
     * 批量删除工艺流程
     * 
     * @param ids 需要删除的工艺流程主键
     * @return 结果
     */
    @Override
    public int deleteProRouteByIds(Long[] ids)
    {
        return proRouteMapper.deleteProRouteByIds(ids);
    }

    /**
     * 删除工艺流程信息
     * 
     * @param id 工艺流程主键
     * @return 结果
     */
    @Override
    public int deleteProRouteById(Long id)
    {
        return proRouteMapper.deleteProRouteById(id);
    }
}
