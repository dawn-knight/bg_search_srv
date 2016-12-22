package com.mbgo.search.core.filter.price;

import org.apache.commons.lang.StringUtils;

import com.mbgo.search.core.filter.itf.FilterBean;
import com.mbgo.search.core.filter.itf.FilterBeanConvertor;


/** 
 * @author 吴健  (HQ01U8435)	Email : wujian@metersbonwe.com 
 * @version 创建时间：2013-6-13 下午4:10:50 
 */
public class PriceRange extends FilterBeanConvertor {

	private int left = -1;
	private int right = -1;
	private long count = 0;
	private String appendString = null;
	public PriceRange() {
	}
	public PriceRange(int left, int right) {
		super();
		this.left = left;
		this.right = right;
	}
	public PriceRange(String pin, long c) {
		super();
		this.count = (int) c;
		int sp = pin.indexOf("-");
		this.left = Integer.parseInt(pin.substring(0, sp));
		this.right = Integer.parseInt(pin.substring(sp + 1));
	}

	public PriceRange(String pin, long c, String ap) {
		super();
		this.count = (int) c;
		int sp = pin.indexOf("-");
		this.left = (int)Math.ceil(Double.parseDouble(pin.substring(0, sp)));
		this.right = (int)Math.ceil(Double.parseDouble(pin.substring(sp + 1)));
		this.appendString = ap;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getRight() {
		return right;
	}
	public void setRight(int right) {
		this.right = right;
	}
	public String toString() {
		return left + "-" + right;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + left;
		result = prime * result + right;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PriceRange other = (PriceRange) obj;
		if (left != other.left)
			return false;
		if (right != other.right)
			return false;
		return true;
	}
	
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public PriceRange merge(PriceRange no) {
		if(no == null || no.left == -1 || no.right == -1) {
			return this;
		}
		PriceRange r = new PriceRange();
		r.setLeft(Math.min(this.left, no.left));
		r.setRight(Math.max(this.right, no.right));
		r.setCount(this.count + no.count);
		r.appendString = this.appendString;
		return r;
	}
	public boolean isValidate() {
		return left >= 0 && right > 0 && count > 0;
	}
	public void addCount(long c) {
		if(c > 0) {
			this.count += c;
		}
	}
	public FilterBean toFilterBean() {
		String code = this.left + "-" + this.right;
		String name = code;
		if(this.left == this.right) {
			name = this.left + "";
		}
		if(StringUtils.isNotBlank(appendString)) {
			name += appendString;
		}
		FilterBean filterBean = new FilterBean(name, code, this.count);
		return filterBean;
	}
}
