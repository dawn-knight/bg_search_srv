/*
* 2014-9-28 上午11:44:26
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.mapper;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbshop.mapper.ProductInfoMapper;
import com.mbgo.mybatis.mbshop.mapper.ProductStoreBarcodeListMapper;
import com.mbgo.mybatis.mbshop.mapper.ProductStoreGoodsStatisticMapper;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.service.BaseTest;

public class ProductStoreBarcodeListMapperTest extends BaseTest {

	@Autowired
	private ProductStoreBarcodeListMapper mapper;
	@Autowired
	private ProductStoreGoodsStatisticMapper statisticMapper;
	@Autowired
	private ProductInfoMapper productInfoMapper;
	
	@Test
	public void testColorCodeIdSizes() {
		MybatisBean option = new MybatisBean();
		List<Product> list = mapper.getColorProductSizes(option);
		for(Product p : list) {
			System.out.println(p.getProductId() + " " + p.getColorProducts().size());
		}
	}
	
	@Test
	public void testGoodsStatistic() {
		MybatisBean option = new MybatisBean();
		List<Product> ps = statisticMapper.getProductStatistic(option);
		for(Product p : ps) {
			System.out.println(p.getProductId() + " " + p.getSaleCount() + " " + p.getMonthSaleCount());
		}
	}
	
	@Test
	public void testProductInfo() {
		MybatisBean option = new MybatisBean();
		List<Product> products = productInfoMapper.getProductDetails(option);
		for(Product p : products) {
			System.out.println(p.getProductId() + " " + p.getProductName() + " " +p.getProductCode());
		}
	}
}
