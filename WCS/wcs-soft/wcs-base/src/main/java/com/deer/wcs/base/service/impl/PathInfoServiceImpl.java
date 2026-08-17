package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.PathInfoMapper;
import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.PathInfoService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.system.service.BillRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 执行路径Service业务层处理
 *
 * @author deer
 * @date 2024-05-10
 */
@Service
public class PathInfoServiceImpl  extends AbstractService<PathInfo, Long>  implements PathInfoService
{
    @Autowired
    private PathInfoMapper pathInfoMapper;

    @Autowired
    private AutoService autoService;

    @Autowired
    private BillRecordService billRecordService;

    @Override
    public void save(PathInfo model) {
        model.setId(autoService.getPathInfoId());
        super.save(model);
    }

    /**
     * 更新步骤的提示信息
     * @param pathInfo
     * @param memo
     */
    @Override
    public void updateMemo(PathInfo pathInfo, String memo) {
        PathInfo pathInfo2 = super.findById(pathInfo.getId());
        if(pathInfo2.getMemo()!=null && pathInfo2.getMemo().equals(memo)){
            return;
        }
        Long taskId = pathInfo.getTaskId();
        pathInfo2 = new PathInfo();
        pathInfo2.setId(pathInfo.getId());
        pathInfo2.setMemo(memo);
        super.update(pathInfo2);

        billRecordService.createTaskRecord(taskId,memo);
    }

    /**
     * 查询执行路径
     *
     * @param id 执行路径主键
     * @return 执行路径
     */
    @Override
    public PathInfo selectPathInfoById(Long id)
    {
        return pathInfoMapper.selectPathInfoById(id);
    }

    /**
     * 查询执行路径列表
     *
     * @param criteria
     * @return 执行路径
     */
    @Override
    public List<PathInfoDto> findList(PathInfoCriteria criteria)
    {
        return pathInfoMapper.findList(criteria);
    }

    /**
     * 新增执行路径
     *
     * @param pathInfo 执行路径
     * @return 结果
     */
    @Override
    public int insertPathInfo(PathInfo pathInfo)
    {
        pathInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return pathInfoMapper.insertPathInfo(pathInfo);
    }

    /**
     * 修改执行路径
     *
     * @param pathInfo 执行路径
     * @return 结果
     */
    @Override
    public int updatePathInfo(PathInfo pathInfo)
    {
        return pathInfoMapper.updatePathInfo(pathInfo);
    }

    /**
     * 批量删除执行路径
     *
     * @param ids 需要删除的执行路径主键
     * @return 结果
     */
    @Override
    public int deletePathInfoByIds(Long[] ids)
    {
        return pathInfoMapper.deletePathInfoByIds(ids);
    }

    /**
     * 删除执行路径信息
     *
     * @param id 执行路径主键
     * @return 结果
     */
    @Override
    public int deletePathInfoById(Long id)
    {
        return pathInfoMapper.deletePathInfoById(id);
    }

    @Override
    public List<PathInfo> findPathListByJobId(PathInfoCriteria criteria) {
        return pathInfoMapper.findPathListByJobId(criteria);
    }

    @Override
    public List<PathInfo> findPathHisListByJobId(PathInfoCriteria criteria) {
        return pathInfoMapper.findPathHisListByJobId(criteria);
    }
}
