/*
* 2014-10-13 下午3:43:31
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.filter.size;

import org.apache.commons.lang.StringUtils;

import com.mbgo.search.core.filter.itf.FilterBean;
import com.mbgo.search.core.filter.itf.FilterBeanConvertor;

public class SizeBean extends FilterBeanConvertor {
	private String sizeCode;
	private String sizeName;
	private long count;
	private Integer sort = 0;
	
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Override
	public FilterBean toFilterBean() {
		FilterBean fb = new FilterBean(sizeName, sizeCode, count);
		fb.setSortIndex(sort);
		return fb;
	}

	@Override
	public boolean isValidate() {
		return StringUtils.isNotBlank(sizeCode) && StringUtils.isNotBlank(sizeName) && count > 0;
	}

	public String getSizeCode() {
		return sizeCode;
	}

	public void setSizeCode(String sizeCode) {
		this.sizeCode = sizeCode;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getSizeName() {
		return sizeName;
	}

	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}
}
