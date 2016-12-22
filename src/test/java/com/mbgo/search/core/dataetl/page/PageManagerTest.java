/*
* 2014-9-30 下午2:00:49
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.dataetl.page;

import org.junit.Test;

import com.mbgo.search.core.service.BaseTest;

public class PageManagerTest extends BaseTest {

	@Test
	public void testPage() {
		PageManager pageManager = new PageManager(10, 3);
		while(pageManager.hasNextPage()) {
			System.out.println(pageManager.getFirst()+", "+pageManager.getMax());
		}
	}
}
