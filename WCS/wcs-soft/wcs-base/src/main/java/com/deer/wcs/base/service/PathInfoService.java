package com.deer.wcs.base.service;

import com.deer.wcs.base.model.PathInfo;
import com.deer.wcs.base.model.PathInfoCriteria;
import com.deer.wcs.base.model.PathInfoDto;
import com.deer.wcs.common.core.service.Service;

import java.util.List;

/**
 * 执行路径Service接口
 *
 * @author deer
 * @date 2024-05-10
 */
public interface PathInfoService   extends Service<PathInfo, Long>
{

    public void updateMemo(PathInfo pathInfo,String memo);
    /**
     * 查询执行路径
     *
     * @param id 执行路径主键
     * @return 执行路径
     */
    public PathInfo selectPathInfoById(Long id);

    /**
     * 查询执行路径列表
     *
     * @param criteria
     * @return 执行路径集合
     */
    public List<PathInfoDto> findList(PathInfoCriteria criteria);

    /**
     * 新增执行路径
     *
     * @param pathInfo 执行路径
     * @return 结果
     */
    public int insertPathInfo(PathInfo pathInfo);

    /**
     * 修改执行路径
     *
     * @param pathInfo 执行路径
     * @return 结果
     */
    public int updatePathInfo(PathInfo pathInfo);

    /**
     * 批量删除执行路径
     *
     * @param ids 需要删除的执行路径主键集合
     * @return 结果
     */
    public int deletePathInfoByIds(Long[] ids);

    /**
     * 删除执行路径信息
     *
     * @param id 执行路径主键
     * @return 结果
     */
    public int deletePathInfoById(Long id);

    List<PathInfo> findPathListByJobId(PathInfoCriteria criteria);

    List<PathInfo> findPathHisListByJobId(PathInfoCriteria criteria);
}
