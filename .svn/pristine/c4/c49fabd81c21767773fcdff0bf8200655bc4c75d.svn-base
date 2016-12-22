/*
* 2014-10-19 下午1:19:16
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import javax.annotation.Resource;

import org.junit.Test;

import com.mbgo.search.core.tools.DebugUtil;

public class KeywordDicServiceTest extends BaseTest {

	@Resource(name = "keywordDicService")
	private KeywordDicService keywordDicService;
	
	@Test
	public void testBuild() {
		keywordDicService.rebuildDicIndex();
	}
	
	@Test
	public void testSearch() {
		long b = System.currentTimeMillis();
		System.out.println(keywordDicService.alsoLike("女装 外套", 40, 20));
		System.out.println("keywordDicService.alsoLike : " + (System.currentTimeMillis() - b));
		System.out.println();
		DebugUtil.clear();
//		b = System.currentTimeMillis();
//		System.out.println(keywordDicService.alsoLike("男装 外套", 10, 200));
//		System.out.println("keywordDicService.alsoLike : " + (System.currentTimeMillis() - b));
	}
}
