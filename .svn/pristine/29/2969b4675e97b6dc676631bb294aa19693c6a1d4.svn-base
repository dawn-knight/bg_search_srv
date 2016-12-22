package com.mbgo.mybatis.mbshop.mapper;

import java.util.List;

import com.mbgo.mybatis.mbshop.bean.ProductLibColor;
import com.mbgo.search.core.filter.color.ColorBean;
import com.mbgo.search.core.filter.size.SizeBean;

public interface ProductLibColorMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductLibColor record);

    int insertSelective(ProductLibColor record);

    ProductLibColor selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductLibColor record);

    int updateByPrimaryKey(ProductLibColor record);
    
    List<ProductLibColor> getAllColorValues();
    
    List<ColorBean> getSeriesMap();
    List<SizeBean> getSizeCodeNameMap();
}