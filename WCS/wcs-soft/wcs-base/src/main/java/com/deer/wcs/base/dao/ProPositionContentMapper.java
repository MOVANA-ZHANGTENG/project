package com.deer.wcs.base.dao;

import java.util.List;

import com.deer.wcs.base.model.ContentReport;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.ProPositionContent;
import com.deer.wcs.base.model.ProPositionContentDto;
import com.deer.wcs.base.model.ProPositionContentCriteria;
import org.apache.ibatis.annotations.Param;

/**
 * 站台扩展Mapper接口
 * 
 * @author deer
 * @date 2024-11-21
 */
public interface ProPositionContentMapper  extends Mapper<ProPositionContent>
{

    /**
     * 查询站台扩展
     *
     * @param id 站台扩展主键
     * @return 站台扩展
     */
    public ProPositionContent selectProPositionContentById(Long id);
    ProPositionContent findByType(@Param("itemCode") String itemCode,@Param("type")  Integer type,@Param("invenState")  Long invenState, @Param("palletState") Integer palletState);
    ProPositionContent findByTypes(@Param("itemCode") String itemCode,@Param("invenState")  Long invenState, @Param("palletState") Integer palletState,List<Integer> types);
    ProPositionContent selectType1(@Param("itemCode") String itemCode,@Param("proLineCode")  String proLineCode);

    /**
     * 查询站台扩展列表
     * 
     * @param ProPositionContent 站台扩展
     * @return 站台扩展集合
     */
    public List<ProPositionContentDto> findList(ProPositionContentCriteria criteria);
    public List<ContentReport> report( );

    public List<ContentReport> report2(@Param("wareCode") String wareCode);
    /**
     * 新增站台扩展
     *
     * @param ProPositionContent 站台扩展
     * @return 结果
     */
    public int insertProPositionContent(ProPositionContent ProPositionContent);

    /**
     * 修改站台扩展
     *
     * @param ProPositionContent 站台扩展
     * @return 结果
     */
    public int updateProPositionContent(ProPositionContent ProPositionContent);

    /**
     * 删除站台扩展
     * 
     * @param id 站台扩展主键
     * @return 结果
     */
    public int deleteProPositionContentById(Long id);

    /**
     * 批量删除站台扩展
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProPositionContentByIds(Long[] ids);

    int updateNull(ProPositionContent model);

    List<ProPositionContent> findByYangJIDeviceCode();

    ProPositionContent findByKong(@Param("codes") List<String> codes);
}
