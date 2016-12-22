/*
* 2014-10-29 下午4:30:35
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.dataetl.stock;

import org.apache.commons.lang.StringUtils;

public class SkuSpliter {

	private String pid;
	private String colorId;
	private String sizeCode;
	private String sku;
	public SkuSpliter(String sku) {
		if(StringUtils.isNotBlank(sku)) {
			if(sku.length() == 16) {
				pid = sku.substring(0, 9);
				colorId = sku.substring(0, 12);
				sizeCode = sku.substring(12, 16);
			} else if(sku.length() == 11) {
				pid = sku.substring(0, 6);
				colorId = sku.substring(0, 8);
				sizeCode = sku.substring(8, sku.length());
			}
			this.sku = sku;
		}
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getColorId() {
		return colorId;
	}
	public void setColorId(String colorId) {
		this.colorId = colorId;
	}
	public String getSizeCode() {
		return sizeCode;
	}
	public void setSizeCode(String sizeCode) {
		this.sizeCode = sizeCode;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
}
