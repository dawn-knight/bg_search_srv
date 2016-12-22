package com.mbgo.search.core.tools.alsolike.device.sttic;
/** 
 * @author 吴健  (HQ01U8435)	Email : wujian@metersbonwe.com 
 * @version 创建时间：2012-4-6 下午4:16:31 
 * 分类器中的关键字的 索引搜素权重，关键字排序权重
 */
public interface DeviceWeight {

	/**
	 * 类别的搜索权重
	 */
	public static final int CATEGORY_NAME = 200;
	/**
	 * 人群的搜索权重
	 */
	public static final int PEOPLE = 100;
	
	/**
	 * 关键字最终排序的权重
	 * @author 吴健 (HQ01U8435)
	 *
	 */
	public static class KeyWeight {
		public static final int CATEGORY = 4;
		public static final int PEOPLE = 5;
	}
}
