package com.deer.wcs.rcs.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.rcs.model.RcsTsj;
import com.deer.wcs.rcs.model.RcsTsjDto;
import com.deer.wcs.rcs.model.RcsTsjCriteria;

/**
 * 提升机Service接口
 * 
 * @author deer
 * @date 2026-05-10
 */
public interface RcsTsjService   extends Service<RcsTsj, Long>
{
    /**
     * 查询提升机
     *
     * @param id 提升机主键
     * @return 提升机
     */
    public RcsTsj selectRcsTsjById(Long id);

    /**
     * 查询提升机列表
     * 
     * @param criteria
     * @return 提升机集合
     */
    public List<RcsTsjDto> findList(RcsTsjCriteria criteria);

    /**
     * 新增提升机
     *
     * @param rcsTsj 提升机
     * @return 结果
     */
    public int insertRcsTsj(RcsTsj rcsTsj);

    /**
     * 修改提升机
     *
     * @param rcsTsj 提升机
     * @return 结果
     */
    public int updateRcsTsj(RcsTsj rcsTsj);

    /**
     * 批量删除提升机
     * 
     * @param ids 需要删除的提升机主键集合
     * @return 结果
     */
    public int deleteRcsTsjByIds(Long[] ids);

    /**
     * 删除提升机信息
     * 
     * @param id 提升机主键
     * @return 结果
     */
    public int deleteRcsTsjById(Long id);
}
