/*
* 2014-10-16 下午4:20:34
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.mbgo.search.core.bean.keyword.Autokey;

public class AutokeyServiceTest extends BaseTest {

	@Resource(name = "autoKeyService")
	private AutoKeyService autoKeyService;
	
	@Test
	public void testBuildIndex() {
		autoKeyService.rebuildAutokeyIndex();
	}
	
	@Test
	public void testSearch() {
		String rss = autoKeyService.searchAutoKey("衬衫", 20);
		
		List<Autokey> rs = JSON.parseArray(rss, Autokey.class);
		for(Autokey ak : rs) {
			System.out.println(ak.getWord() + " : " + ak.getCount());
		}
	}
}
