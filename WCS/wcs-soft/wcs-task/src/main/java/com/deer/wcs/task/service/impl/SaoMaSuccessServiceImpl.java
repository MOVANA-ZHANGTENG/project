package com.deer.wcs.task.service.impl;

import java.util.List;

import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.task.dao.SaoMaSuccessMapper;
import com.deer.wcs.task.model.SaoMaSuccess;
import com.deer.wcs.task.model.SaoMaSuccessDto;
import com.deer.wcs.task.model.SaoMaSuccessCriteria;
import com.deer.wcs.task.service.SaoMaSuccessService;

/**
 * 扫描失败率Service业务层处理
 * 
 * @author deer
 * @date 2025-10-15
 */
@Service
public class SaoMaSuccessServiceImpl  extends AbstractService<SaoMaSuccess, Long>  implements SaoMaSuccessService
{
    @Autowired
    private SaoMaSuccessMapper saoMaSuccessMapper;

    /**
     * 查询扫描失败率
     *
     * @param id 扫描失败率主键
     * @return 扫描失败率
     */
    @Override
    public SaoMaSuccess selectSaoMaSuccessById(Long id)
    {
        return saoMaSuccessMapper.selectSaoMaSuccessById(id);
    }

    /**
     * 查询扫描失败率列表
     * 
     * @param criteria
     * @return 扫描失败率
     */
    @Override
    public List<SaoMaSuccessDto> findList(SaoMaSuccessCriteria criteria)
    {
        return saoMaSuccessMapper.findList(criteria);
    }

    /**
     * 新增扫描失败率
     *
     * @param saoMaSuccess 扫描失败率
     * @return 结果
     */
    @Override
    public int insertSaoMaSuccess(SaoMaSuccess saoMaSuccess)
    {
        return saoMaSuccessMapper.insertSaoMaSuccess(saoMaSuccess);
    }

    /**
     * 修改扫描失败率
     *
     * @param saoMaSuccess 扫描失败率
     * @return 结果
     */
    @Override
    public int updateSaoMaSuccess(SaoMaSuccess saoMaSuccess)
    {
        return saoMaSuccessMapper.updateSaoMaSuccess(saoMaSuccess);
    }

    /**
     * 批量删除扫描失败率
     * 
     * @param ids 需要删除的扫描失败率主键
     * @return 结果
     */
    @Override
    public int deleteSaoMaSuccessByIds(Long[] ids)
    {
        return saoMaSuccessMapper.deleteSaoMaSuccessByIds(ids);
    }

    /**
     * 删除扫描失败率信息
     * 
     * @param id 扫描失败率主键
     * @return 结果
     */
    @Override
    public int deleteSaoMaSuccessById(Long id)
    {
        return saoMaSuccessMapper.deleteSaoMaSuccessById(id);
    }

    @Override
    public SaoMaSuccess findByTypeAndTime(String classTime,Integer type, String beginTime,String endTime, Integer currentHour) {
        return saoMaSuccessMapper.findByTypeAndTime(classTime,type,beginTime,endTime,currentHour);
    }

    @Override
    public List<SaoMaSuccess> findByTypeAndTime2(String classTime,Integer type, String beginTime,String endTime) {
        return saoMaSuccessMapper.findByTypeAndTime2(classTime,type,beginTime,endTime);
    }
}
