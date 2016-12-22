/*
* 2014-9-22 上午10:30:22
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.filter.price;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mbgo.search.core.filter.itf.FilterBeanConvertor;
import com.mbgo.search.core.filter.itf.FilterCaculater;

public class PriceCaculator extends FilterCaculater {

	private List<PriceRange> prices = new ArrayList<PriceRange>();
	
	private int rangeNum = 7;
	
	public PriceCaculator() {
		
	}
	public PriceCaculator(int rangeNum) {
		this.rangeNum = rangeNum;
	}

	public void addPriceRange(PriceRange pr) {
		if(pr.isValidate()) {
			this.prices.add(pr);
		}
	}
	
	@Override
	public List<? extends FilterBeanConvertor> getConvertor() {

		Collections.sort(prices, new Comparator<PriceRange>() {
			@Override
			public int compare(PriceRange o1, PriceRange o2) {
				if (o1.getLeft() > o2.getLeft()) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		
		int total  = 0;
		for(PriceRange pr : prices) {
			total += pr.getCount();
		}
		int perRange = total / rangeNum;
		List<PriceRange> result = new ArrayList<PriceRange>();
		result.add(new PriceRange());
		for(PriceRange pr : prices) {
			int currentIndex = result.size() - 1;
			PriceRange temp = result.get(currentIndex);
			long curentCount = temp.getCount(), thisCount = pr.getCount();
			if((curentCount + thisCount) <= perRange * 1.05 || currentIndex == rangeNum - 1) {
				result.set(currentIndex, pr.merge(temp));
			} else {
				result.add(pr);
			}
		}
		return result;
	}
	
	/**
	 * 价格分段
	 * @param price
	 * @return
	 */
	public static String compartPrice(double price) {

		int mode = 70;

		if (price > 1000) {
			mode = (int) (price /300) * 100;
		} else if (price > 500) {
			mode = 200;
		} else if (price > 200) {
			mode = 100;
		}
		int modeRange = mode - 1;
		int rs = (int) (price / mode);
		int l = -1;
		int r = -1;
		l = rs * mode;
		r = l + modeRange;
		
		return l + "-" + r;
	}
	
}
