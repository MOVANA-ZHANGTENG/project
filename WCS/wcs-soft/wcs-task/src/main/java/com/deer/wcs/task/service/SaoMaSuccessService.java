package com.deer.wcs.task.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.task.model.SaoMaSuccess;
import com.deer.wcs.task.model.SaoMaSuccessDto;
import com.deer.wcs.task.model.SaoMaSuccessCriteria;

/**
 * 扫描失败率Service接口
 * 
 * @author deer
 * @date 2025-10-15
 */
public interface SaoMaSuccessService   extends Service<SaoMaSuccess, Long>
{
    /**
     * 查询扫描失败率
     *
     * @param id 扫描失败率主键
     * @return 扫描失败率
     */
    public SaoMaSuccess selectSaoMaSuccessById(Long id);

    /**
     * 查询扫描失败率列表
     * 
     * @param criteria
     * @return 扫描失败率集合
     */
    public List<SaoMaSuccessDto> findList(SaoMaSuccessCriteria criteria);

    /**
     * 新增扫描失败率
     *
     * @param saoMaSuccess 扫描失败率
     * @return 结果
     */
    public int insertSaoMaSuccess(SaoMaSuccess saoMaSuccess);

    /**
     * 修改扫描失败率
     *
     * @param saoMaSuccess 扫描失败率
     * @return 结果
     */
    public int updateSaoMaSuccess(SaoMaSuccess saoMaSuccess);

    /**
     * 批量删除扫描失败率
     * 
     * @param ids 需要删除的扫描失败率主键集合
     * @return 结果
     */
    public int deleteSaoMaSuccessByIds(Long[] ids);

    /**
     * 删除扫描失败率信息
     * 
     * @param id 扫描失败率主键
     * @return 结果
     */
    public int deleteSaoMaSuccessById(Long id);

    SaoMaSuccess findByTypeAndTime(String classTime,Integer type, String beginTime,String endTime,Integer currentHour);
    List<SaoMaSuccess> findByTypeAndTime2(String classTime,Integer type, String beginTime,String endTime);
}
