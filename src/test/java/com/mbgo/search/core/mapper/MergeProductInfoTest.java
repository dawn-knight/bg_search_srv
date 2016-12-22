/*
* 2014-9-28 下午3:02:11
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
import com.mbgo.mybatis.mbstore.mapper.MallThemeGoodsMapper;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.bean.index.ProductTheme;
import com.mbgo.search.core.dataetl.ColorProductProcessor;
import com.mbgo.search.core.dataetl.ProductAttributeValueProcessor;
import com.mbgo.search.core.dataetl.ProductManager;
import com.mbgo.search.core.dataetl.ProductStatisticProcessor;
import com.mbgo.search.core.dataetl.ProductThemeProcessor;
import com.mbgo.search.core.dataetl.page.PageManager;
import com.mbgo.search.core.service.BaseTest;
import com.mbgo.search.core.service.DataEtlService;

public class MergeProductInfoTest extends BaseTest {
	@Autowired
	private MallThemeGoodsMapper mapper; //获得款式级别的主题活动信息
	@Autowired
	private ProductStoreBarcodeListMapper barcodeListMapper; //获得颜色级别信息以及颜色尺寸信息
	@Autowired
	private ProductStoreGoodsStatisticMapper statisticMapper; //获得款式级别的统计信息
	@Autowired
	private ProductInfoMapper productInfoMapper;//获得款式级别详细信息
	
	@Test
	public void test() {
		MybatisBean option = new MybatisBean(0, 1000);
		List<Product> products = productInfoMapper.getProductDetails(option);
		ProductManager manager = new ProductManager(products);
		option.setMax(-1);
		option.setParams(manager.getProductIds());
		
		List<ProductTheme> themes = mapper.getProductThemes(option);
		ProductThemeProcessor themeProcessor = new ProductThemeProcessor(themes);
		
		List<Product> colorProducts = barcodeListMapper.getColorProductSizes(option);
		List<Product> statisticProduct = statisticMapper.getProductStatistic(option);
		List<Product> attrValues = productInfoMapper.getProductAttrValue(option);
		
		manager.merge(themeProcessor)
		.merge(new ColorProductProcessor(colorProducts))
		.merge(new ProductStatisticProcessor(statisticProduct))
		.merge(new ProductAttributeValueProcessor(attrValues));
		
		for(Product p : products) {
			System.out.println(p.getProductId());
			System.out.println(p.getThemes());
			System.out.println(p.getColorProducts());
			System.out.println(p.getValues());
			System.out.println();
		}
		
		System.out.println(productInfoMapper.countAll());
	}
	@Test
	public void testAttr() {
		MybatisBean option = new MybatisBean(0, 1000);
		List<Product> ps = productInfoMapper.getProductAttrValue(option);
		for(Product p : ps) {
			System.out.println(p.getProductId() + " : " + p.getValues());
		}
	}
	@Test
	public void testUpdateProduct() {
		List<String> pids = productInfoMapper.getUpdatedProductIds(null);
		
		System.out.println(pids);
	}
	@Autowired
	private DataEtlService dataEtlService;
	@Test
	public void testMergeProductInfo() throws Exception {
		//分页信息
		long totalRecord = productInfoMapper.countAll();
		if(totalRecord < 10) {
			System.out.println("没有足够的数据");
			return;
		}
		System.out.println("total : " + totalRecord);
		PageManager pageManager = new PageManager(totalRecord, 4000);
		
		long beginTime = System.currentTimeMillis();
		while(pageManager.hasNextPage()) {
			
			List<Product> products = dataEtlService.getProductList(pageManager.getFirst(), pageManager.getMax(), null);
			
			System.out.println(products.size());
		}
		long delt = System.currentTimeMillis() - beginTime;
		System.out.println("cost : " + delt);
		
	}
}
