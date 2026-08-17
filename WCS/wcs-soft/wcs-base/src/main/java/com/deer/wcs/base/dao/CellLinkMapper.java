package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.CellLink;
import com.deer.wcs.base.model.CellLinkDto;
import com.deer.wcs.base.model.CellLinkCriteria;

/**
 * 库位邻接关系，存储四向车调度系统的节点联通关系Mapper接口
 * 
 * @author deer
 * @date 2025-10-14
 */
public interface CellLinkMapper  extends Mapper<CellLink>
{
    /**
     * 查询库位邻接关系，存储四向车调度系统的节点联通关系
     *
     * @param id 库位邻接关系，存储四向车调度系统的节点联通关系主键
     * @return 库位邻接关系，存储四向车调度系统的节点联通关系
     */
    public CellLink selectCellLinkById(Long id);

    /**
     * 查询库位邻接关系，存储四向车调度系统的节点联通关系列表
     * 
     * @param cellLink 库位邻接关系，存储四向车调度系统的节点联通关系
     * @return 库位邻接关系，存储四向车调度系统的节点联通关系集合
     */
    public List<CellLinkDto> findList(CellLinkCriteria criteria);

    /**
     * 新增库位邻接关系，存储四向车调度系统的节点联通关系
     *
     * @param cellLink 库位邻接关系，存储四向车调度系统的节点联通关系
     * @return 结果
     */
    public int insertCellLink(CellLink cellLink);

    /**
     * 修改库位邻接关系，存储四向车调度系统的节点联通关系
     *
     * @param cellLink 库位邻接关系，存储四向车调度系统的节点联通关系
     * @return 结果
     */
    public int updateCellLink(CellLink cellLink);

    /**
     * 删除库位邻接关系，存储四向车调度系统的节点联通关系
     * 
     * @param id 库位邻接关系，存储四向车调度系统的节点联通关系主键
     * @return 结果
     */
    public int deleteCellLinkById(Long id);

    /**
     * 批量删除库位邻接关系，存储四向车调度系统的节点联通关系
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCellLinkByIds(Long[] ids);
}
