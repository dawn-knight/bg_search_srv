package com.mbgo.search.core.tools.alsolike.device.sttic;

import com.mbgo.search.core.tools.alsolike.device.CategoryDevice;
import com.mbgo.search.core.tools.alsolike.device.ChildrenDevice;
import com.mbgo.search.core.tools.alsolike.device.IWeightingDevice;
import com.mbgo.search.core.tools.alsolike.device.ManDevice;
import com.mbgo.search.core.tools.alsolike.device.WomanDevice;

/** 
 * @author 吴健  (HQ01U8435)	Email : wujian@metersbonwe.com 
 * @version 创建时间：2012-4-6 下午4:18:30 
 * 
 * 分类器控制中心
 * 类别、人群的分类器
 */
public class DeviceHandler {

	public static IWeightingDevice cateNameDevice = null;
	public static IWeightingDevice manDevice = null;
	public static IWeightingDevice womanDevice = null;
	public static IWeightingDevice childDevice = null;
	
	static {
		/*
		 * 一些分类器初始化
		 * 商品类别
		 * 人群 男
		 * 人群 女
		 * 人群 儿童
		 */
		cateNameDevice = new CategoryDevice();
		manDevice = new ManDevice();
		womanDevice = new WomanDevice();
		childDevice = new ChildrenDevice();
	}
}
