package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.CellLinkMapper;
import com.deer.wcs.base.model.CellLink;
import com.deer.wcs.base.model.CellLinkDto;
import com.deer.wcs.base.model.CellLinkCriteria;
import com.deer.wcs.base.service.CellLinkService;
import tk.mybatis.mapper.entity.Condition;

/**
 * 库位邻接关系，存储四向车调度系统的节点联通关系Service业务层处理
 * 
 * @author deer
 * @date 2025-10-14
 */
@Service
public class CellLinkServiceImpl  extends AbstractService<CellLink, Long>  implements CellLinkService
{
    @Autowired
    private CellLinkMapper cellLinkMapper;

    @Override
    public void save(CellLink cellLink){
        Condition condition = new Condition(CellLink.class);
        condition.createCriteria().andEqualTo("fromCellId", cellLink.getFromCellId())
            .andEqualTo("toCellId", cellLink.getToCellId())
                        .andEqualTo("wareCode", cellLink.getWareCode());

        List<CellLink> list = cellLinkMapper.selectByCondition(condition);
        if(!list.isEmpty()){
            return;
        }
        cellLink.setCreateTime(DateUtil.getNowDateTimeString());
        super.save(cellLink);
    }

    /**
     * 查询库位邻接关系，存储四向车调度系统的节点联通关系
     *
     * @param id 库位邻接关系，存储四向车调度系统的节点联通关系主键
     * @return 库位邻接关系，存储四向车调度系统的节点联通关系
     */
    @Override
    public CellLink selectCellLinkById(Long id)
    {
        return cellLinkMapper.selectCellLinkById(id);
    }

    /**
     * 查询库位邻接关系，存储四向车调度系统的节点联通关系列表
     * 
     * @param criteria
     * @return 库位邻接关系，存储四向车调度系统的节点联通关系
     */
    @Override
    public List<CellLinkDto> findList(CellLinkCriteria criteria)
    {
        return cellLinkMapper.findList(criteria);
    }

    /**
     * 新增库位邻接关系，存储四向车调度系统的节点联通关系
     *
     * @param cellLink 库位邻接关系，存储四向车调度系统的节点联通关系
     * @return 结果
     */
    @Override
    public int insertCellLink(CellLink cellLink)
    {
        cellLink.setCreateTime(DateUtil.getNowDateTimeString());
        return cellLinkMapper.insertCellLink(cellLink);
    }

    /**
     * 修改库位邻接关系，存储四向车调度系统的节点联通关系
     *
     * @param cellLink 库位邻接关系，存储四向车调度系统的节点联通关系
     * @return 结果
     */
    @Override
    public int updateCellLink(CellLink cellLink)
    {
        return cellLinkMapper.updateCellLink(cellLink);
    }

    /**
     * 批量删除库位邻接关系，存储四向车调度系统的节点联通关系
     * 
     * @param ids 需要删除的库位邻接关系，存储四向车调度系统的节点联通关系主键
     * @return 结果
     */
    @Override
    public int deleteCellLinkByIds(Long[] ids)
    {
        return cellLinkMapper.deleteCellLinkByIds(ids);
    }

    /**
     * 删除库位邻接关系，存储四向车调度系统的节点联通关系信息
     * 
     * @param id 库位邻接关系，存储四向车调度系统的节点联通关系主键
     * @return 结果
     */
    @Override
    public int deleteCellLinkById(Long id)
    {
        return cellLinkMapper.deleteCellLinkById(id);
    }
}
