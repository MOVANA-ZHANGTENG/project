package com.deer.wcs.task.dao;

import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.CodeScanner.CodeScannerInfo;
import com.deer.wcs.task.model.CodeScanner.CodeScannerInfoCriteria;
import com.deer.wcs.task.model.CodeScanner.CodeScannerInfoDto;

import java.util.List;

/**
 * 扫码器Mapper接口
 * 
 * @author deer
 * @date 2024-07-31
 */
public interface CodeScannerInfoMapper extends Mapper<CodeScannerInfo>
{
    /**
     * 查询扫码器
     *
     * @param id 扫码器主键
     * @return 扫码器
     */
    public CodeScannerInfo selectSmqInfoById(Long id);

    /**
     * 查询扫码器列表
     * 
     * @param criteria 扫码器
     * @return 扫码器集合
     */
    public List<CodeScannerInfoDto> findList(CodeScannerInfoCriteria criteria);

    /**
     * 新增扫码器
     *
     * @param codeScannerInfo 扫码器
     * @return 结果
     */
    public int insertSmqInfo(CodeScannerInfo codeScannerInfo);

    /**
     * 修改扫码器
     *
     * @param codeScannerInfo 扫码器
     * @return 结果
     */
    public int updateSmqInfo(CodeScannerInfo codeScannerInfo);

    /**
     * 删除扫码器
     * 
     * @param id 扫码器主键
     * @return 结果
     */
    public int deleteSmqInfoById(Long id);

    /**
     * 批量删除扫码器
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSmqInfoByIds(Long[] ids);
}
