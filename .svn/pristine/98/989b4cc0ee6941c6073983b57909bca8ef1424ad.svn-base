package com.mbgo.mybatis.mbshop.mapper;

import java.util.List;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbshop.bean.ProductStoreBarcodeList;
import com.mbgo.search.core.bean.index.Product;

public interface ProductStoreBarcodeListMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductStoreBarcodeList record);

    int insertSelective(ProductStoreBarcodeList record);

    ProductStoreBarcodeList selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProductStoreBarcodeList record);

    int updateByPrimaryKey(ProductStoreBarcodeList record);
    
    List<Product> getColorProductSizes(MybatisBean option);
    
    /**
     * 获取最低价格
     * @param option
     * @return
     */
    List<Product> getMinSalePrice(MybatisBean option);
}