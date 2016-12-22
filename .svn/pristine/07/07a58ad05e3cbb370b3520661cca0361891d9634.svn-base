/*
* 2014-10-22 下午4:51:51
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.mbgo.search.core.bean.query.ProductQuery;
import com.mbgo.search.core.bean.query.ProductQueryResult;
import com.mbgo.search.core.filter.FilterData;
import com.mbgo.search.core.service.use4.CacheService;

public class SearchServiceTest extends BaseTest {

	@Resource(name = "apiSearchService")
	private SearchService searchService;
	
	@Resource(name = "cacheService")
	private CacheService cacheService;
	
	@Test
	public void searchProduct() {
		cacheService.initAll();
		ProductQuery query = new ProductQuery("", null, null);
		
//		query.setSearchType(1);
//		query.setCid("153");
		/*query.setSortType(1);
		query.setSortFieldNum(3);
		query.setThemeCode("AMPM-ZHUTI155");*/
		query.setStoreId("HQ01S116");
		query.setProductId("523051");
		query.init();
		ProductQueryResult result = searchService.search(query);
		System.out.println(result.getTotal());
		System.out.println(result.getConstrutor());

		System.out.println(JSON.toJSONString(result));
	}
	
	@Test
	public void searchFilterData() {
		cacheService.initAll();
		ProductQuery query = new ProductQuery("", null, null);
//		query.setSearchType(0);
//		query.setSizeCode("0126");
//		query.setStock(1);
		query.setCid("1");
		
		query.init();
		FilterData result = searchService.searchFilterData(query);
		System.out.println(result.getTotal());
		System.out.println(result.getConstrutor());
		System.out.println(result.getBrand());
		System.out.println(JSON.toJSONString(result));
	}
	
	@Test
	public void searchOrder() {
		cacheService.initAll();
		ProductQuery query = new ProductQuery("", null, null);
		
		query.setThemeCode("AMPM-ZHUTI155");
//		query.setStock(-1);
//		query.setPageNo(1);
//		query.setPageSize(30);
		
		query.init();
		ProductQueryResult result = searchService.search(query);
		System.out.println(result.getTotal());
		System.out.println(result.getConstrutor());

		System.out.println(JSON.toJSONString(result));
	}
	
}
