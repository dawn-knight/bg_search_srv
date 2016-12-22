package com.mbgo.mybatis.mbshop.mapper;

import java.util.List;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbshop.bean.ProductInfo;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.filter.attr.AttributeBean;
import com.mbgo.search.core.filter.category.Category;

public interface ProductInfoMapper {
    int deleteByPrimaryKey(Integer productId);

    int insert(ProductInfo record);

    int insertSelective(ProductInfo record);

    ProductInfo selectByPrimaryKey(Integer productId);

    int updateByPrimaryKeySelective(ProductInfo record);

    int updateByPrimaryKey(ProductInfo record);
    
    List<Product> getProductDetails(MybatisBean option);
    
    long countAll();
    
    List<Category> getAllCategorys();
    
    Category selectCategoryById(int categoryId);
    
    List<ProductInfo> selectAllAsList();
    
    List<Product> getProductAttrValue(MybatisBean option);
    
    List<AttributeBean> getAttributeKeyInfo();
    
    List<AttributeBean> getAttributeValueInfo();
    
    List<String> getUpdatedProductIds(String updateTime);
    
    List<Product> getProductStoreIdByProductId(MybatisBean option);
}