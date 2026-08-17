package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.ProLineMapper;
import com.deer.wcs.base.model.ProLine;
import com.deer.wcs.base.model.ProLineDto;
import com.deer.wcs.base.model.ProLineCriteria;
import com.deer.wcs.base.service.ProLineService;

/**
 * 产线Service业务层处理
 * 
 * @author deer
 * @date 2024-11-21
 */
@Service
public class ProLineServiceImpl  extends AbstractService<ProLine, Long>  implements ProLineService
{
    @Autowired
    private ProLineMapper proLineMapper;

    /**
     * 查询产线
     *
     * @param id 产线主键
     * @return 产线
     */
    @Override
    public ProLine selectProLineById(Long id)
    {
        return proLineMapper.selectProLineById(id);
    }

    /**
     * 查询产线列表
     * 
     * @param criteria
     * @return 产线
     */
    @Override
    public List<ProLineDto> findList(ProLineCriteria criteria)
    {
        return proLineMapper.findList(criteria);
    }

    /**
     * 新增产线
     *
     * @param proLine 产线
     * @return 结果
     */
    @Override
    public int insertProLine(ProLine proLine)
    {
        proLine.setCreateTime(DateUtil.getNowDateTimeString());
        return proLineMapper.insertProLine(proLine);
    }

    /**
     * 修改产线
     *
     * @param proLine 产线
     * @return 结果
     */
    @Override
    public int updateProLine(ProLine proLine)
    {
        proLine.setUpdateTime(DateUtil.getNowDateTimeString());
        return proLineMapper.updateProLine(proLine);
    }

    /**
     * 批量删除产线
     * 
     * @param ids 需要删除的产线主键
     * @return 结果
     */
    @Override
    public int deleteProLineByIds(Long[] ids)
    {
        return proLineMapper.deleteProLineByIds(ids);
    }

    /**
     * 删除产线信息
     * 
     * @param id 产线主键
     * @return 结果
     */
    @Override
    public int deleteProLineById(Long id)
    {
        return proLineMapper.deleteProLineById(id);
    }
}
