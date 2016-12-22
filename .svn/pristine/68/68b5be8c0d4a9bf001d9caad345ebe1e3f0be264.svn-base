package com.mbgo.mybatis.mbshop.mapper;

import java.util.List;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbshop.bean.ProductStoreGoodsStatistic;
import com.mbgo.search.core.bean.index.Product;

public interface ProductStoreGoodsStatisticMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductStoreGoodsStatistic record);

    int insertSelective(ProductStoreGoodsStatistic record);

    ProductStoreGoodsStatistic selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductStoreGoodsStatistic record);

    int updateByPrimaryKey(ProductStoreGoodsStatistic record);
    
    List<Product> getProductStatistic(MybatisBean option);
}