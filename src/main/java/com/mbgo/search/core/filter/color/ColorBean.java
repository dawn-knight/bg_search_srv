/*
* 2014-10-13 下午4:06:58
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.filter.color;

import org.apache.commons.lang.StringUtils;

import com.mbgo.search.core.filter.itf.FilterBean;
import com.mbgo.search.core.filter.itf.FilterBeanConvertor;

public class ColorBean extends FilterBeanConvertor {
	//色系代码
	private String colorInterval;
	private long count;
	//色系名称
	private String colorName;
	private int sortIndex;
	public ColorBean() {
	}
	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	public String getColorInterval() {
		return colorInterval;
	}

	public void setColorInterval(String colorInterval) {
		this.colorInterval = colorInterval;
	}

	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getColorName() {
		return colorName;
	}
	public void setColorName(String colorName) {
		this.colorName = colorName;
	}
	
	@Override
	public FilterBean toFilterBean() {
		return new FilterBean(this.colorName, this.colorInterval , count);
	}
	@Override
	public boolean isValidate() {
		return StringUtils.isNotBlank(colorInterval) && count > 0;
	}
}
