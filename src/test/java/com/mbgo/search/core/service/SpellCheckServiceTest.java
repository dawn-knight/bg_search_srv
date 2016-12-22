/*
* 2014-10-17 下午4:46:43
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import javax.annotation.Resource;

import org.junit.Test;

public class SpellCheckServiceTest extends BaseTest {

	@Resource(name = "spellCheckService")
	private SpellCheckService spellCheckService;
	
	@Test
	public void testIndex() {
		spellCheckService.rebuildIndex();
	}
	@Test
	public void testSearch() {
		System.out.println(spellCheckService.spellCheckWord("男撞 诚意"));
	}
}
