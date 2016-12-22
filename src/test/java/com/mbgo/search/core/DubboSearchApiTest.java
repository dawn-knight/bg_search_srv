/*
* 2014-11-5 下午2:14:31
* 吴健 HQ01U8435
*/

package com.mbgo.search.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.mbgo.search.api.service.SearchApiService;
import com.mbgo.search.core.bean.keyword.HotWordQuery;
import com.mbgo.search.core.bean.query.ApiParameter;
import com.mbgo.search.core.bean.query.ProductQueryResult;
import com.mbgo.search.core.bean.update.UpdateOption;
import com.mbgo.search.core.filter.FilterData;
import com.mbgo.search.core.service.BaseTest;

public class DubboSearchApiTest extends BaseTest {
	@Autowired
	private SearchApiService searchApiService;
	public ApiParameter ap = new ApiParameter();
	public void out(long b) {
		float cost = System.nanoTime() - b;
		float cst = cost / 1000000;
		System.out.println(cst);
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
	}
	@Test
	public void testProductSearch() {
		long b = System.nanoTime();
		ProductQueryResult result = searchApiService.searchProduct(ap);
		ap.setThemeCode("AMPM_ZHUTI10");
		out(b);
		System.out.println(JSON.toJSONString(result));
	}
	@Test
	public void testFilterData() {
		long b = System.nanoTime();
		ap.setStock("1");
		FilterData filterData = searchApiService.searchFilterData(ap);
		out(b);
		System.out.println(JSON.toJSONString(filterData));
		
	}
	
	@Test
	public void testAutokey() {
		long b = System.nanoTime();
		System.out.println(searchApiService.queryAutokey("nz", 11));
		out(b);
	}
	@Test
	public void testSpellCheck() {
		long b = System.nanoTime();
		System.out.println(searchApiService.spellCheck("男装 村山"));
		out(b);
	}
	@Test
	public void testHotSearch() {
		long b = System.nanoTime();
		HotWordQuery hq = new HotWordQuery();
		System.out.println(searchApiService.hotSearchWord(hq));
		out(b);
	}
	@Test
	public void testUpdateProductIndex() {
		System.out.println(searchApiService.updateProductIndex("100000039", "update"));
	}

	@Test
	public void testUpdateProductIndexByList() {
		long b = System.nanoTime();
		List<UpdateOption> uos = new ArrayList<UpdateOption>();
		uos.add(UpdateOption.update("100000039"));
		uos.add(UpdateOption.update("100000035"));
		System.out.println(searchApiService.updateProductIndexByList(uos));
		out(b);
	}
	@Test
	public void testUpdateTags() {
		long b = System.nanoTime();
		List<String> pids = new ArrayList<String>();
		pids.add("100000039");
		pids.add("100000035");
		System.out.println(searchApiService.updateProductTags(pids));
		out(b);
	}
	
	@Test
	public void testUpdate() {
		testUpdateProductIndexByList();
		testUpdateTags();
	}
}
