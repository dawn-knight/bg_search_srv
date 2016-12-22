package com.mbgo.mybatis.mbshop.mapper;

import java.util.List;

import com.mbgo.mybatis.mbshop.bean.ProductLibBrand;
import com.mbgo.search.core.filter.brand.BrandBean;

public interface ProductLibBrandMapper {
    int deleteByPrimaryKey(Integer brandId);

    int insert(ProductLibBrand record);

    int insertSelective(ProductLibBrand record);

    ProductLibBrand selectByPrimaryKey(Integer brandId);

    int updateByPrimaryKeySelective(ProductLibBrand record);

    int updateByPrimaryKeyWithBLOBs(ProductLibBrand record);

    int updateByPrimaryKey(ProductLibBrand record);
    
    List<ProductLibBrand> selectList();
    
    List<BrandBean> getBrandInfo();
    List<BrandBean> getStoreInfo();
}