/*
* 2014-10-19 下午12:34:09
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.util.List;

public interface KeywordDicService {

	/**
	 * 重建词典索引
	 * @return
	 */
	public boolean rebuildDicIndex();
	/**
	 * 相似关键字建议
	 * @param word
	 * @param num
	 * @param len
	 * @return
	 */
	public String alsoLike(String word, int num, int len);
	
	/**
	 * 关键字重组
	 * @param word
	 * @param num
	 * @return
	 */
	public List<String> rebuildKeyword(String word, int limit);
}
