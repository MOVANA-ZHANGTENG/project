package com.deer.wcs.base.service;

import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.model.PositionInfoCriteria;
import com.deer.wcs.base.model.PositionInfoDto;
import com.deer.wcs.common.core.service.Service;

import java.util.List;

/**
 * 站台Service接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface PositionInfoService   extends Service<PositionInfo, Long>
{
    void updateMemo(PositionInfo positionInfo, String msg,Integer type);
    public PositionInfo findByCode(String wareCode, String code);
    public PositionInfo findBySubCode(String wareCode, String code);
    /**
     * 查询站台
     *
     * @param id 站台主键
     * @return 站台
     */
    public PositionInfo selectPositionInfoById(Long id);

    /**
     * 查询站台列表
     * 
     * @param criteria
     * @return 站台集合
     */
    public List<PositionInfoDto> findList(PositionInfoCriteria criteria);

    /**
     * 新增站台
     *
     * @param positionInfo 站台
     * @return 结果
     */
    public int insertPositionInfo(PositionInfo positionInfo);

    /**
     * 修改站台
     *
     * @param positionInfo 站台
     * @return 结果
     */
    public int updatePositionInfo(PositionInfo positionInfo);

    /**
     * 批量删除站台
     * 
     * @param ids 需要删除的站台主键集合
     * @return 结果
     */
    public int deletePositionInfoByIds(Long[] ids);

    /**
     * 删除站台信息
     * 
     * @param id 站台主键
     * @return 结果
     */
    public int deletePositionInfoById(Long id);

    List<PositionInfo> findByWareCode(String xlWare);

    List<PositionInfo> findByParentCode(String parentCode);

    //为了防止线程并发,使用行锁,只有当查询这一行数据的时候才会加锁,为了保证查询到的是最新数据
    PositionInfo findByIdForUpdate(Long positionId);
}
