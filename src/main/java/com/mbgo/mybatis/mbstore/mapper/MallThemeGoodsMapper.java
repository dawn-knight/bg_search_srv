package com.mbgo.mybatis.mbstore.mapper;

import java.util.List;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.search.core.bean.index.ProductTheme;

public interface MallThemeGoodsMapper {

  List<ProductTheme> getProductThemes(MybatisBean option);

  List<String> getUpdatedThemeInfoProductIds(String updateTime);

  List<String> getUpdatedThemeCodes(String updateTime);
}