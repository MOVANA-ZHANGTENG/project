package com.deer.wcs.base.dao;

import com.deer.wcs.base.model.LineInfo;
import com.deer.wcs.base.model.LineInfoCriteria;
import com.deer.wcs.base.model.LineInfoDto;
import com.deer.wcs.base.model.WareInfoUpdate;
import com.deer.wcs.common.core.mapper.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 巷道Mapper接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface LineInfoMapper  extends Mapper<LineInfo>
{
    LineInfo allotLine(@Param("wareCode") String wareCode);
    /**
     * 查询巷道
     *
     * @param id 巷道主键
     * @return 巷道
     */
    public LineInfo selectLineInfoById(Integer id);

    /**
     * 查询巷道列表
     * 
     * @param lineInfo 巷道
     * @return 巷道集合
     */
    public List<LineInfoDto> findList(LineInfoCriteria criteria);

    /**
     * 新增巷道
     *
     * @param lineInfo 巷道
     * @return 结果
     */
    public int insertLineInfo(LineInfo lineInfo);

    /**
     * 修改巷道
     *
     * @param lineInfo 巷道
     * @return 结果
     */
    public int updateLineInfo(LineInfo lineInfo);

    /**
     * 删除巷道
     * 
     * @param id 巷道主键
     * @return 结果
     */
    public int deleteLineInfoById(Integer id);

    /**
     * 批量删除巷道
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public Integer deleteLineInfoByIds(Long[] ids);

    /**
     * 更新关联数据
     *
     * @param update
     */
    public void updateAllLinkLine(WareInfoUpdate update);
}
