package com.deer.wcs.base.service;

import java.util.List;

import com.deer.wcs.base.model.ContentReport;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.ProPositionContent;
import com.deer.wcs.base.model.ProPositionContentDto;
import com.deer.wcs.base.model.ProPositionContentCriteria;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.entity.Condition;

/**
 * 站台扩展Service接口
 * 
 * @author deer
 * @date 2024-11-21
 */
public interface ProPositionContentService   extends Service<ProPositionContent, Long>
{
    public ProPositionContent findByType(String itemCode,Integer type,Long invenState,Integer palletState);
    public ProPositionContent findByTypes(String itemCode,Long invenState,Integer palletState,List<Integer> types);
    public ProPositionContent findByCode(String wareCode, String code);

    ProPositionContent selectType1(@Param("itemCode") String itemCode, @Param("proLineCode")  String proLineCode);

    /**
     * 查询站台扩展
     *
     * @param id 站台扩展主键
     * @return 站台扩展
     */
    public ProPositionContent selectProPositionContentById(Long id);
    public List<ContentReport> report( );

    public List<ContentReport> report2(String wareCode);
    /**
     * 查询站台扩展列表
     * 
     * @param criteria
     * @return 站台扩展集合
     */
    public List<ProPositionContentDto> findList(ProPositionContentCriteria criteria);

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
     * 批量删除站台扩展
     * 
     * @param ids 需要删除的站台扩展主键集合
     * @return 结果
     */
    public int deleteProPositionContentByIds(Long[] ids);

    /**
     * 删除站台扩展信息
     * 
     * @param id 站台扩展主键
     * @return 结果
     */
    public int deleteProPositionContentById(Long id);

    int updateNull(ProPositionContent ProPositionContent);

    List<ProPositionContent> findByYangJIDeviceCode();

    ProPositionContent findByKong(List<String> codes);
}
