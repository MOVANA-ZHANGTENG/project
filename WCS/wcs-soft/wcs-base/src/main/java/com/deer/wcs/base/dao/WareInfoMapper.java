package com.deer.wcs.base.dao;

import com.deer.wcs.base.model.WareInfo;
import com.deer.wcs.base.model.WareInfoCriteria;
import com.deer.wcs.base.model.WareInfoDto;
import com.deer.wcs.base.model.WareInfoUpdate;
import com.deer.wcs.common.core.mapper.Mapper;

import java.util.List;

/**
 * 仓库设置Mapper接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface WareInfoMapper  extends Mapper<WareInfo>
{
    /**
     * 查询仓库设置
     *
     * @param id 仓库设置主键
     * @return 仓库设置
     */
    public WareInfo selectWareInfoById(Long id);
    public void deleteModel(String wareCode);

    /**
     * 查询仓库设置列表
     * 
     * @param wareInfo 仓库设置
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
     * 删除仓库设置
     * 
     * @param id 仓库设置主键
     * @return 结果
     */
    public int deleteWareInfoById(Long id);

    /**
     * 批量删除仓库设置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWareInfoByIds(Long[] ids);
    /**
     * 更新与仓库关联的一系列信息
     *
     * @param update 仓库修改信息
     * @return 结果
     */
    public void updateAllLinkWareId(WareInfoUpdate update);

    List<WareInfoDto> findAllWareInfos();
}
