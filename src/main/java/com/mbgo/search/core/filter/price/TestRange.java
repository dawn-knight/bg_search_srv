/*
 * 2014-9-13 下午2:14:32
 * 吴健 HQ01U8435
 */

package com.mbgo.search.core.filter.price;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.mbgo.search.core.filter.itf.FilterBean;

public class TestRange {

	public static List<Integer> createPriceList(int num) {
		List<Integer> rs = new ArrayList<Integer>();

		for (int i = 0; i < num; i++) {
			rs.add(new Random(System.currentTimeMillis() + i).nextInt(1001) + 10);
		}

		return rs;
	}

	public static PriceRange setRange(int p) {
		int mode = 10;

		if (p > 1000) {
			mode = 40;
		} else if (p > 500) {
			mode = 30;
		} else if (p > 200) {
			mode = 20;
		}
		int modeRange = mode - 1;
		int rs = p / mode;
		int l = -1;
		int r = -1;
		l = rs * mode;
		r = l + modeRange;
		PriceRange pr = new PriceRange(l, r);

		pr.setCount(new Random(System.currentTimeMillis() + p).nextInt(110) + 2);
		return pr;
	}

	public static void main(String[] args) {
		List<Integer> prices = createPriceList(1000);
		Map<String, PriceRange> map = new HashMap<String, PriceRange>();
		for (Integer i : prices) {
			PriceRange pr = setRange(i);
			if (map.get(pr.toString()) != null) {
				map.get(pr.toString()).addCount(pr.getCount());
			} else {
				map.put(pr.toString(), pr);
			}
		}

		List<PriceRange> ranges = new ArrayList<PriceRange>();
		int total = 0;
		for(Map.Entry<String, PriceRange> en : map.entrySet()) {
			total += en.getValue().getCount();
			ranges.add(en.getValue());
		}
		

		long b = System.currentTimeMillis();
		PriceCaculator pc = new PriceCaculator();
		for(PriceRange pr : ranges) {
			pc.addPriceRange(pr);
		}
		List<FilterBean> result = pc.execute();
		
		System.out.println("total : " + total);
		for(FilterBean fb : result) {
			System.out.println(fb);
		}
//		String json = JSON.toJSONString(result);
		System.out.println("cost : " + (System.currentTimeMillis() - b));
	}
}
