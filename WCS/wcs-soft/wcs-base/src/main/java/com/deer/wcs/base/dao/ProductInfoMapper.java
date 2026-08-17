package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.ProductInfo;
import com.deer.wcs.base.model.ProductInfoDto;
import com.deer.wcs.base.model.ProductInfoCriteria;

/**
 * 产品Mapper接口
 * 
 * @author deer
 * @date 2024-12-25
 */
public interface ProductInfoMapper  extends Mapper<ProductInfo>
{
    /**
     * 查询产品
     *
     * @param id 产品主键
     * @return 产品
     */
    public ProductInfo selectProductInfoById(Long id);

    /**
     * 查询产品列表
     * 
     * @param productInfo 产品
     * @return 产品集合
     */
    public List<ProductInfoDto> findList(ProductInfoCriteria criteria);

    /**
     * 新增产品
     *
     * @param productInfo 产品
     * @return 结果
     */
    public int insertProductInfo(ProductInfo productInfo);

    /**
     * 修改产品
     *
     * @param productInfo 产品
     * @return 结果
     */
    public int updateProductInfo(ProductInfo productInfo);

    /**
     * 删除产品
     * 
     * @param id 产品主键
     * @return 结果
     */
    public int deleteProductInfoById(Long id);

    /**
     * 批量删除产品
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProductInfoByIds(Long[] ids);
}
