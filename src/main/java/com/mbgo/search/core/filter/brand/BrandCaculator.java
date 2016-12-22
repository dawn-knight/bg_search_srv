/*
* 2014-12-3 上午11:18:15
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.filter.brand;

import java.util.ArrayList;
import java.util.List;

import com.mbgo.search.core.filter.itf.FilterBeanConvertor;
import com.mbgo.search.core.filter.itf.FilterCaculater;

public class BrandCaculator extends FilterCaculater {
	private List<BrandBean> brands = new ArrayList<BrandBean>();

	@Override
	public List<? extends FilterBeanConvertor> getConvertor() {
		
		return brands;
	}

	public void addBrandInfo(String code, String name, long count) {
		brands.add(new BrandBean(code, name, count));
	}

	public void addBrandInfo(BrandBean b) {
		if(b != null) {
			brands.add(b);
		}
	}

	@Override
	public boolean isSortedByCount() {
		return true;
	}
}
