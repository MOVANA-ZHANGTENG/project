package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.ProLine;
import com.deer.wcs.base.model.ProLineDto;
import com.deer.wcs.base.model.ProLineCriteria;

/**
 * 产线Mapper接口
 * 
 * @author deer
 * @date 2024-11-21
 */
public interface ProLineMapper  extends Mapper<ProLine>
{
    /**
     * 查询产线
     *
     * @param id 产线主键
     * @return 产线
     */
    public ProLine selectProLineById(Long id);

    /**
     * 查询产线列表
     * 
     * @param proLine 产线
     * @return 产线集合
     */
    public List<ProLineDto> findList(ProLineCriteria criteria);

    /**
     * 新增产线
     *
     * @param proLine 产线
     * @return 结果
     */
    public int insertProLine(ProLine proLine);

    /**
     * 修改产线
     *
     * @param proLine 产线
     * @return 结果
     */
    public int updateProLine(ProLine proLine);

    /**
     * 删除产线
     * 
     * @param id 产线主键
     * @return 结果
     */
    public int deleteProLineById(Long id);

    /**
     * 批量删除产线
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProLineByIds(Long[] ids);
}
