package com.metersbonwe.pcs.dao;

import java.util.List;

import com.mbgo.mybatis.commonbean.GettingUpdatedChannelGoodIdListParam;
import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.bean.index.SizeInfo;

public interface SkuLevelProductInfoMapper extends
    ProductChannelGoodsBarcodeMapper {

  public List<Product> getProductList(MybatisBean paramBean);

  public List<Product> getListOfProductWithMinSalePrice(MybatisBean paramBean);

  public List<String> getIdListOfGoodsSkuLevelInfoChanged(GettingUpdatedChannelGoodIdListParam param);

  public List<SizeInfo> getSizeSeriesInfo();
}
