package com.deer.wcs.base.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.base.dao.ProductInfoMapper;
import com.deer.wcs.base.model.ProductInfo;
import com.deer.wcs.base.model.ProductInfoDto;
import com.deer.wcs.base.model.ProductInfoCriteria;
import com.deer.wcs.base.service.ProductInfoService;

/**
 * 产品Service业务层处理
 * 
 * @author deer
 * @date 2024-12-25
 */
@Service
public class ProductInfoServiceImpl  extends AbstractService<ProductInfo, Long>  implements ProductInfoService
{
    @Autowired
    private ProductInfoMapper productInfoMapper;

    /**
     * 查询产品
     *
     * @param id 产品主键
     * @return 产品
     */
    @Override
    public ProductInfo selectProductInfoById(Long id)
    {
        return productInfoMapper.selectProductInfoById(id);
    }

    /**
     * 查询产品列表
     * 
     * @param criteria
     * @return 产品
     */
    @Override
    public List<ProductInfoDto> findList(ProductInfoCriteria criteria)
    {
        return productInfoMapper.findList(criteria);
    }

    /**
     * 新增产品
     *
     * @param productInfo 产品
     * @return 结果
     */
    @Override
    public int insertProductInfo(ProductInfo productInfo)
    {
        return productInfoMapper.insertProductInfo(productInfo);
    }

    /**
     * 修改产品
     *
     * @param productInfo 产品
     * @return 结果
     */
    @Override
    public int updateProductInfo(ProductInfo productInfo)
    {
        return productInfoMapper.updateProductInfo(productInfo);
    }

    /**
     * 批量删除产品
     * 
     * @param ids 需要删除的产品主键
     * @return 结果
     */
    @Override
    public int deleteProductInfoByIds(Long[] ids)
    {
        return productInfoMapper.deleteProductInfoByIds(ids);
    }

    /**
     * 删除产品信息
     * 
     * @param id 产品主键
     * @return 结果
     */
    @Override
    public int deleteProductInfoById(Long id)
    {
        return productInfoMapper.deleteProductInfoById(id);
    }
}
