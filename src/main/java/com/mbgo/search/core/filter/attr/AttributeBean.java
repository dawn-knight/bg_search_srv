/*
* 2014-12-2 下午5:15:59
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.filter.attr;

import org.apache.commons.lang.StringUtils;

import com.mbgo.search.core.filter.itf.FilterBean;
import com.mbgo.search.core.filter.itf.FilterBeanConvertor;

public class AttributeBean extends FilterBeanConvertor {
	private String code;
	private String name;
	private long count = -1;
	private int sortIndex;
	public AttributeBean() {
	}
	@Override
	public FilterBean toFilterBean() {
		
		return new FilterBean(name, code, count);
	}
	public int getSortIndex() {
		return sortIndex;
	}
	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}
	public AttributeBean(String code, String name, long count) {
		super();
		this.code = code;
		this.name = name;
		this.count = count;
	}

	@Override
	public boolean isValidate() {
		
		return count > 0 && StringUtils.isNotBlank(name);
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

}
