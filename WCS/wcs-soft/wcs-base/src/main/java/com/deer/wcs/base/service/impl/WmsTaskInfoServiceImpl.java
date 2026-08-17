package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.WmsTaskInfoMapper;
import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.AreaInfoService;
import com.deer.wcs.base.service.WareInfoService;
import com.deer.wcs.base.service.WmsTaskInfoService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * wms任务Service业务层处理
 * 
 * @author deer
 * @date 2024-05-10
 */
@Service
public class WmsTaskInfoServiceImpl  extends AbstractService<WmsTaskInfo, Long>  implements WmsTaskInfoService
{
    @Autowired
    private WmsTaskInfoMapper wmsTaskInfoMapper;
    @Autowired
    private WareInfoService wareInfoService;
    @Autowired
    private AreaInfoService areaInfoService;
    @Autowired
    private AutoService autoService;

    @Override
    public void save(WmsTaskInfo model) {
        model.setCreateTime(DateUtil.getNowDateTimeString());
        model.setState(0);
        model.setVersion(0);
        if(model.getTaskNo()==null||model.getTaskNo().equals("")){
            model.setTaskNo(autoService.getTaskNo());
        }

        if(model.getWareCode()!=null && !model.getWareCode().equals("")){
            WareInfo wareInfo = wareInfoService.findBy("code",model.getWareCode());
            if(wareInfo!=null){
                model.setWareName(wareInfo.getName());
            }
        }
        if(model.getAreaCode()!=null && !model.getAreaCode().equals("")){
            AreaInfo areaInfo = areaInfoService.findBy("code",model.getAreaCode());
            if(areaInfo!=null){
                model.setAreaName(areaInfo.getName());
            }
        }
        if(model.getPriority()==null){
            model.setPriority(10);
        }
        super.save(model);
    }

    /**
     * 查询wms任务
     *
     * @param id wms任务主键
     * @return wms任务
     */
    @Override
    public WmsTaskInfo selectWmsTaskInfoById(Long id)
    {
        return wmsTaskInfoMapper.selectWmsTaskInfoById(id);
    }

    /**
     * 查询wms任务列表
     * 
     * @param criteria
     * @return wms任务
     */
    @Override
    public List<WmsTaskInfoDto> findList(WmsTaskInfoCriteria criteria)
    {
        return wmsTaskInfoMapper.findList(criteria);
    }

    /**
     * 新增wms任务
     *
     * @param wmsTaskInfo wms任务
     * @return 结果
     */
    @Override
    public int insertWmsTaskInfo(WmsTaskInfo wmsTaskInfo)
    {
        wmsTaskInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return wmsTaskInfoMapper.insertWmsTaskInfo(wmsTaskInfo);
    }

    /**
     * 修改wms任务
     *
     * @param wmsTaskInfo wms任务
     * @return 结果
     */
    @Override
    public int updateWmsTaskInfo(WmsTaskInfo wmsTaskInfo)
    {
        return wmsTaskInfoMapper.updateWmsTaskInfo(wmsTaskInfo);
    }

    /**
     * 批量删除wms任务
     * 
     * @param ids 需要删除的wms任务主键
     * @return 结果
     */
    @Override
    public int deleteWmsTaskInfoByIds(Long[] ids)
    {
        return wmsTaskInfoMapper.deleteWmsTaskInfoByIds(ids);
    }

    /**
     * 删除wms任务信息
     * 
     * @param id wms任务主键
     * @return 结果
     */
    @Override
    public int deleteWmsTaskInfoById(Long id)
    {
        return wmsTaskInfoMapper.deleteWmsTaskInfoById(id);
    }
}
