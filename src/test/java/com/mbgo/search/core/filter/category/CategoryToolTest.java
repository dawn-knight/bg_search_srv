/*
* 2014-9-26 下午3:24:54
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.filter.category;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mbgo.mybatis.mbshop.mapper.ProductInfoMapper;
import com.mbgo.search.core.service.BaseTest;
import com.mbgo.search.core.service.use4.CacheService;

public class CategoryToolTest extends BaseTest {
	@Resource
	private CacheService cacheService;

	@Test
	public void testCurrentCategory() {

		List<Category> list = CategoryTools.getCategorys();
		
		System.out.println(list.size());
		long b = System.currentTimeMillis();
		CategoryCaculator categoryCaculator = new CategoryCaculator(list);

		Category current = categoryCaculator.getCurrentCategory();
		System.out.println("total cost : " + (System.currentTimeMillis() - b));
		System.out.println(current);
	}
	@Autowired
	private ProductInfoMapper productInfoMapper;
	
//	@Test
//	public void testParentCategoryId() {
//		CategoryCaculator caculator = new CategoryCaculator().initParentCategorys(productInfoMapper.getAllCategorys());
//
//		long b = System.currentTimeMillis();
//		long delt = System.currentTimeMillis() - b;
//		System.out.println(caculator.getParentIds(94));
//		System.out.println(caculator.getAllCategoryId(94));
//		System.out.println(caculator.getSubs("1"));
//		System.out.println(caculator.getSameLevelCids("1"));
//		System.out.println(cacheService.getAllCategoryIds(94));
//		System.out.println("total cost : " + delt);
//	}
}
