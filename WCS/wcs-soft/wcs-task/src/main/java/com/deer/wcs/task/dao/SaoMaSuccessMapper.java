package com.deer.wcs.task.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.SaoMaSuccess;
import com.deer.wcs.task.model.SaoMaSuccessDto;
import com.deer.wcs.task.model.SaoMaSuccessCriteria;
import org.apache.ibatis.annotations.Param;

/**
 * 扫描失败率Mapper接口
 * 
 * @author deer
 * @date 2025-10-15
 */
public interface SaoMaSuccessMapper  extends Mapper<SaoMaSuccess>
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
     * @param
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
     * 删除扫描失败率
     * 
     * @param id 扫描失败率主键
     * @return 结果
     */
    public int deleteSaoMaSuccessById(Long id);

    /**
     * 批量删除扫描失败率
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSaoMaSuccessByIds(Long[] ids);

    SaoMaSuccess findByTypeAndTime(@Param("classTime") String classTime,@Param("type") Integer type, @Param("beginTime")String beginTime, @Param("endTime")String endTime,@Param("currentHour") Integer currentHour );
    List<SaoMaSuccess> findByTypeAndTime2(@Param("classTime") String classTime,@Param("type") Integer type, @Param("beginTime")String beginTime, @Param("endTime")String endTime);
}
