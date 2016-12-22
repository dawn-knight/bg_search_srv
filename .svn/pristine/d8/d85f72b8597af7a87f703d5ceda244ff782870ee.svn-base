package com.mbgo.search.core.tools.alsolike.device;

import com.mbgo.search.core.tools.alsolike.device.sttic.DeviceWeight;

/** 
 * @author 吴健  (HQ01U8435)	Email : wujian@metersbonwe.com 
 * @version 创建时间：2012-4-6 下午1:22:21 
 * 商品类别分类器
 */
public class CategoryDevice extends Device {

	public CategoryDevice() {
		super(TXT_BASE_PATH + "catename.txt",
				"");
	}
	public void loadWord(String c) {
		_stock.loadExternal(c);
	}
	@Override
	public int getQueryWeight() {
		return DeviceWeight.CATEGORY_NAME;
	}

	@Override
	public int getKeyWeight() {
		return DeviceWeight.KeyWeight.CATEGORY;
	}
}
