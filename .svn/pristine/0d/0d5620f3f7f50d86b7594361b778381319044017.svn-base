/*
* 2014-10-13 下午2:21:34
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.util.List;

import org.junit.Test;

import com.mbgo.search.core.filter.price.PriceCaculator;
import com.mbgo.search.core.filter.price.TestRange;

public class TestPriceRange {

	@Test
	public void testCompartPrice() {
		long b = System.currentTimeMillis();
		List<Integer> prices = TestRange.createPriceList(10000);
		for(Integer p : prices) {
			System.out.println(p + " : " + PriceCaculator.compartPrice(p));
		}
		System.out.println(prices.size() + " total cost : " + (System.currentTimeMillis() - b));
	}
}
