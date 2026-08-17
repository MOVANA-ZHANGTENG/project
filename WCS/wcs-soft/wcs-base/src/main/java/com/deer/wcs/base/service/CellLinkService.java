package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.CellLink;
import com.deer.wcs.base.model.CellLinkDto;
import com.deer.wcs.base.model.CellLinkCriteria;

/**
 * 库位邻接关系，存储四向车调度系统的节点联通关系Service接口
 * 
 * @author deer
 * @date 2025-10-14
 */
public interface CellLinkService   extends Service<CellLink, Long>
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
     * @param criteria
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
     * 批量删除库位邻接关系，存储四向车调度系统的节点联通关系
     * 
     * @param ids 需要删除的库位邻接关系，存储四向车调度系统的节点联通关系主键集合
     * @return 结果
     */
    public int deleteCellLinkByIds(Long[] ids);

    /**
     * 删除库位邻接关系，存储四向车调度系统的节点联通关系信息
     * 
     * @param id 库位邻接关系，存储四向车调度系统的节点联通关系主键
     * @return 结果
     */
    public int deleteCellLinkById(Long id);
}
