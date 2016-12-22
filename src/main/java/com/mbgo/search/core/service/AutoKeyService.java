/*
* 2014-10-16 下午3:12:16
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;


public interface AutoKeyService {

	/**
	 * 重建自动补全关键字的索引
	 * @return
	 */
	public boolean rebuildAutokeyIndex();
	
	/**
	 * 搜索关键字自动补全搜索
	 * @param word
	 * @param limit
	 * @return
	 */
	public String searchAutoKey(String word, int limit);
}
