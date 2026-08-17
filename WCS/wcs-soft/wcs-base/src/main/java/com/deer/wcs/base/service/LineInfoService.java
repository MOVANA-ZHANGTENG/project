package com.deer.wcs.base.service;

import com.deer.wcs.base.model.LineInfo;
import com.deer.wcs.base.model.LineInfoCriteria;
import com.deer.wcs.base.model.LineInfoDto;
import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.common.core.service.Service;

import java.util.List;

/**
 * 巷道Service接口
 * 
 * @author deer
 * @date 2024-04-28
 */
public interface LineInfoService   extends Service<LineInfo, Long>
{
    LineInfo allotLine(String wareCode);
    LineInfo findByCode(String wareCode, String lineCode);
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
     * @param criteria
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
     * 批量删除巷道
     * 
     * @param ids 需要删除的巷道主键集合
     * @return 结果
     */
    public Integer deleteLineInfoByIds(Long[] ids);

    /**
     * 删除巷道信息
     * 
     * @param id 巷道主键
     * @return 结果
     */
    public int deleteLineInfoById(Integer id);


}
