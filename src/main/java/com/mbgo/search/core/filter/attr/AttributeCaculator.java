/*
* 2014-12-2 下午5:15:36
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.filter.attr;

import java.util.ArrayList;
import java.util.List;

import com.mbgo.search.core.filter.itf.FilterBeanConvertor;
import com.mbgo.search.core.filter.itf.FilterCaculater;

public class AttributeCaculator extends FilterCaculater {
	private List<AttributeBean> abbs = new ArrayList<AttributeBean>();
	@Override
	public List<? extends FilterBeanConvertor> getConvertor() {
		
		return abbs;
	}
	public void addAttrBean(String valueCode, String valueName, long count) {
		abbs.add(new AttributeBean(valueCode, valueName, count));
	}
	@Override
	public boolean isSortedByCount() {
		return true;
	}
}
