package com.deer.wcs.base.service;

import java.util.List;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.ProductInfo;
import com.deer.wcs.base.model.ProductInfoDto;
import com.deer.wcs.base.model.ProductInfoCriteria;

/**
 * 产品Service接口
 * 
 * @author deer
 * @date 2024-12-25
 */
public interface ProductInfoService   extends Service<ProductInfo, Long>
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
     * @param criteria
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
     * 批量删除产品
     * 
     * @param ids 需要删除的产品主键集合
     * @return 结果
     */
    public int deleteProductInfoByIds(Long[] ids);

    /**
     * 删除产品信息
     * 
     * @param id 产品主键
     * @return 结果
     */
    public int deleteProductInfoById(Long id);
}
