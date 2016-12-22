package com.metersbonwe.pcs.dao;

import java.util.List;

import com.mbgo.mybatis.commonbean.GettingUpdatedChannelGoodIdListParam;
import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.search.core.bean.index.Product;

public interface ExtendedProductChannelGoodsMapper extends
		ProductChannelGoodsMapper {
  public List<String> getChannelProductIdList(MybatisBean paramBean);
	public List<Product> getChannelProductList(MybatisBean paramBean);
	
	public List<Product> getChannelGoodAttributeList(MybatisBean paramBean);
	
	public List<String> getUpdatedChannelGoodIdList(GettingUpdatedChannelGoodIdListParam param);
	
	public List<String> getIdListOfGoodsPriceChanged(GettingUpdatedChannelGoodIdListParam param);
	
	public List<String> getIdListOfGoodsAttributeChanged(GettingUpdatedChannelGoodIdListParam param);
	
	public List<String> getIdListOfGoodsColorMapChanged(GettingUpdatedChannelGoodIdListParam param);
	
	public int countAll();
}
