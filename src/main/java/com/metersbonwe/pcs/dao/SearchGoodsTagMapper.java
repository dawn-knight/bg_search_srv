package com.metersbonwe.pcs.dao;

import java.util.List;

import com.mbgo.mybatis.commonbean.GettingUpdatedChannelGoodIdListParam;
import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.search.core.bean.index.Product;

public interface SearchGoodsTagMapper {

  List<Product> getProductTags(MybatisBean params);

  List<Product> getDisplayTag(MybatisBean params);
  
  List<String> getAllProductIds(GettingUpdatedChannelGoodIdListParam param);
}
