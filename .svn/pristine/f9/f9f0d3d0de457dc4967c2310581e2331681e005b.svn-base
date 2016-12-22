/*
* 2014-12-3 上午11:22:02
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.filter.brand;

import org.apache.commons.lang.StringUtils;

import com.mbgo.search.core.filter.itf.FilterBean;
import com.mbgo.search.core.filter.itf.FilterBeanConvertor;

public class BrandBean extends FilterBeanConvertor {
	private String code;
	private String name;
	private long count;
	private int sortIndex;
	@Override
	public FilterBean toFilterBean() {
		
		return new FilterBean(name, code, count, sortIndex);
	}

	@Override
	public boolean isValidate() {
		
		return count > 0 && StringUtils.isNotBlank(name);
	}

	public BrandBean(String code, String name, long count) {
		super();
		this.code = code;
		this.name = name;
		this.count = count;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
	public BrandBean() {
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}
}
