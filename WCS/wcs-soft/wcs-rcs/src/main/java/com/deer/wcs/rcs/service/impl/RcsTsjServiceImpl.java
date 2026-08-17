package com.deer.wcs.rcs.service.impl;

import java.util.List;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.rcs.dao.RcsTsjMapper;
import com.deer.wcs.rcs.model.RcsTsj;
import com.deer.wcs.rcs.model.RcsTsjDto;
import com.deer.wcs.rcs.model.RcsTsjCriteria;
import com.deer.wcs.rcs.service.RcsTsjService;
import tk.mybatis.mapper.entity.Condition;

/**
 * 提升机Service业务层处理
 * 
 * @author deer
 * @date 2026-05-10
 */
@Service
public class RcsTsjServiceImpl  extends AbstractService<RcsTsj, Long>  implements RcsTsjService
{
    @Autowired
    private RcsTsjMapper rcsTsjMapper;

    @Override
    public int update(RcsTsj model) {
        Condition condition = new Condition(RcsTsj.class);
        condition.createCriteria()
                .andEqualTo("id", model.getId())
                .andEqualTo("version", model.getVersion());

        // 将版本号加1
        if (model.getVersion() == null) {
            model.setVersion(1);
        } else {
            model.setVersion(model.getVersion() + 1);
        }

        int count = super.updateByConditionSelective(model, condition);
        if (count == 0) {
            throw new ServiceException("乐观锁冲突，请重试", 500);
        }
        return super.update(model);
    }

    /**
     * 查询提升机
     *
     * @param id 提升机主键
     * @return 提升机
     */
    @Override
    public RcsTsj selectRcsTsjById(Long id)
    {
        return rcsTsjMapper.selectRcsTsjById(id);
    }

    /**
     * 查询提升机列表
     * 
     * @param criteria
     * @return 提升机
     */
    @Override
    public List<RcsTsjDto> findList(RcsTsjCriteria criteria)
    {
        return rcsTsjMapper.findList(criteria);
    }

    /**
     * 新增提升机
     *
     * @param rcsTsj 提升机
     * @return 结果
     */
    @Override
    public int insertRcsTsj(RcsTsj rcsTsj)
    {
        rcsTsj.setCreateTime(DateUtil.getNowDateTimeString());
        return rcsTsjMapper.insertRcsTsj(rcsTsj);
    }

    /**
     * 修改提升机
     *
     * @param rcsTsj 提升机
     * @return 结果
     */
    @Override
    public int updateRcsTsj(RcsTsj rcsTsj)
    {
        return rcsTsjMapper.updateRcsTsj(rcsTsj);
    }

    /**
     * 批量删除提升机
     * 
     * @param ids 需要删除的提升机主键
     * @return 结果
     */
    @Override
    public int deleteRcsTsjByIds(Long[] ids)
    {
        return rcsTsjMapper.deleteRcsTsjByIds(ids);
    }

    /**
     * 删除提升机信息
     * 
     * @param id 提升机主键
     * @return 结果
     */
    @Override
    public int deleteRcsTsjById(Long id)
    {
        return rcsTsjMapper.deleteRcsTsjById(id);
    }
}
