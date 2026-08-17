package com.deer.wcs.task.service.impl;

import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.dao.CodeScannerInfoMapper;
import com.deer.wcs.task.model.CodeScanner.CodeScannerInfo;
import com.deer.wcs.task.model.CodeScanner.CodeScannerInfoCriteria;
import com.deer.wcs.task.model.CodeScanner.CodeScannerInfoDto;
import com.deer.wcs.task.service.CodeScannerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 扫码器Service业务层处理
 * 
 * @author deer
 * @date 2024-07-31
 */
@Service
public class CodeScannerInfoServiceImpl extends AbstractService<CodeScannerInfo, Long>  implements CodeScannerInfoService
{
    @Autowired
    private CodeScannerInfoMapper smqInfoMapper;

    @Override
    public void save(CodeScannerInfo model) {
        model.setDelFlag(0);
        model.setState(0);
        model.setIsNext(0);
        model.setCreateTime(DateUtil.getNowDateTimeString());
        super.save(model);
    }

    @Override
    public int update(CodeScannerInfo model) {
        model.setUpdateTime(DateUtil.getNowDateTimeString());
        return super.update(model);
    }

    /**
     * 查询扫码器
     *
     * @param id 扫码器主键
     * @return 扫码器
     */
    @Override
    public CodeScannerInfo selectSmqInfoById(Long id)
    {
        return smqInfoMapper.selectSmqInfoById(id);
    }

    /**
     * 查询扫码器列表
     * 
     * @param criteria
     * @return 扫码器
     */
    @Override
    public List<CodeScannerInfoDto> findList(CodeScannerInfoCriteria criteria)
    {
        return smqInfoMapper.findList(criteria);
    }

    /**
     * 新增扫码器
     *
     * @param codeScannerInfo 扫码器
     * @return 结果
     */
    @Override
    public int insertSmqInfo(CodeScannerInfo codeScannerInfo)
    {
        codeScannerInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return smqInfoMapper.insertSmqInfo(codeScannerInfo);
    }

    /**
     * 修改扫码器
     *
     * @param codeScannerInfo 扫码器
     * @return 结果
     */
    @Override
    public int updateSmqInfo(CodeScannerInfo codeScannerInfo)
    {
        codeScannerInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        return smqInfoMapper.updateSmqInfo(codeScannerInfo);
    }

    /**
     * 批量删除扫码器
     * 
     * @param ids 需要删除的扫码器主键
     * @return 结果
     */
    @Override
    public int deleteSmqInfoByIds(Long[] ids)
    {
        return smqInfoMapper.deleteSmqInfoByIds(ids);
    }

    /**
     * 删除扫码器信息
     * 
     * @param id 扫码器主键
     * @return 结果
     */
    @Override
    public int deleteSmqInfoById(Long id)
    {
        return smqInfoMapper.deleteSmqInfoById(id);
    }
}
