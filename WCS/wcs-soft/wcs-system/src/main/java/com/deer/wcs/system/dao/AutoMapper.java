package com.deer.wcs.system.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.system.model.Auto;
import com.deer.wcs.system.model.AutoDto;
import com.deer.wcs.system.model.AutoCriteria;
import org.apache.ibatis.annotations.Param;

/**
 * 当日自增长Mapper接口
 * 
 * @author deer
 * @date 2023-10-23
 */
public interface AutoMapper  extends Mapper<Auto>
{
    void deleteLast(@Param("type") Integer type , @Param("thisId") Integer thisId);
    Auto getByDateAndType(@Param("date") String date, @Param("type") Integer type);
    /**
     * 查询当日自增长
     *
     * @param id 当日自增长主键
     * @return 当日自增长
     */
    public Auto selectAutoById(Integer id);

    /**
     * 查询当日自增长列表
     * 
     * @param
     * @return 当日自增长集合
     */
    public List<AutoDto> findList(AutoCriteria criteria);

    /**
     * 新增当日自增长
     *
     * @param auto 当日自增长
     * @return 结果
     */
    public int insertAuto(Auto auto);

    /**
     * 修改当日自增长
     *
     * @param auto 当日自增长
     * @return 结果
     */
    public int updateAuto(Auto auto);

    /**
     * 删除当日自增长
     * 
     * @param id 当日自增长主键
     * @return 结果
     */
    public int deleteAutoById(Integer id);

    /**
     * 批量删除当日自增长
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAutoByIds(Integer[] ids);
}
