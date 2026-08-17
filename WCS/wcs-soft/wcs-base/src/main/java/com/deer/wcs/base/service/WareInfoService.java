package com.deer.wcs.base.service;

import com.deer.wcs.base.model.WareInfo;
import com.deer.wcs.base.model.WareInfoCriteria;
import com.deer.wcs.base.model.WareInfoDto;
import com.deer.wcs.common.core.service.Service;

import java.util.List;

/**
 * 仓库设置Service接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface WareInfoService   extends Service<WareInfo, Long>
{
    /**
     * 查询仓库设置
     *
     * @param id 仓库设置主键
     * @return 仓库设置
     */
    public WareInfo selectWareInfoById(Long id);

    /**
     * 查询仓库设置列表
     * 
     * @param criteria
     * @return 仓库设置集合
     */
    public List<WareInfoDto> findList(WareInfoCriteria criteria);

    /**
     * 新增仓库设置
     *
     * @param wareInfo 仓库设置
     * @return 结果
     */
    public int insertWareInfo(WareInfo wareInfo);

    /**
     * 修改仓库设置
     *
     * @param wareInfo 仓库设置
     * @return 结果
     */
    public int updateWareInfo(WareInfo wareInfo);

    /**
     * 修改仓库模型
     *
     * @param wareInfo 模型设置
     * @return 结果
     */
    public int updateWareModel(WareInfo wareInfo);

    /**
     * 批量删除仓库设置
     * 
     * @param ids 需要删除的仓库设置主键集合
     * @return 结果
     */
    public int deleteWareInfoByIds(Long[] ids);

    /**
     * 删除仓库设置信息
     * 
     * @param id 仓库设置主键
     * @return 结果
     */
    public int deleteWareInfoById(Long id);

    List<WareInfoDto> findAllWareInfos();
}
