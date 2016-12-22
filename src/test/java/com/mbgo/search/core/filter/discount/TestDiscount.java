/*
* 2015-1-19 下午5:10:24
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.filter.discount;

import java.util.List;

import org.junit.Test;

import com.mbgo.search.core.filter.itf.FilterBean;
import com.mbgo.search.core.filter.price.PriceCaculator;
import com.mbgo.search.core.filter.price.PriceRange;

public class TestDiscount {


	
	@Test
	public void testDiscount() {
		PriceCaculator pc = new PriceCaculator(4);
		pc.addPriceRange(new PriceRange("0-0", 344, "折"));
		pc.addPriceRange(new PriceRange("1-1", 533, "折"));
		pc.addPriceRange(new PriceRange("2-2", 722, "折"));
		pc.addPriceRange(new PriceRange("3-3", 41, "折"));
		pc.addPriceRange(new PriceRange("4-4", 111, "折"));
		pc.addPriceRange(new PriceRange("5-5", 241, "折"));
		pc.addPriceRange(new PriceRange("6-6", 51, "折"));
		pc.addPriceRange(new PriceRange("7-7", 231, "折"));
		pc.addPriceRange(new PriceRange("8", 231, "折以上"));

		long b = System.currentTimeMillis();
		List<FilterBean> result = pc.execute();
		
		for(FilterBean fb : result) {
			System.out.println(fb);
		}
//		String json = JSON.toJSONString(result);
		System.out.println("cost : " + (System.currentTimeMillis() - b));
	}
}
