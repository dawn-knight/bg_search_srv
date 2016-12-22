/*
* 2014-12-9 上午9:38:47
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mbgo.search.core.bean.update.UpdateOption;

public class TestUpdateProductIndex extends BaseTest {

	@Autowired
	ProductIndexService productIndexService;
	@Test
	public void test() throws Exception {
		List<String> list = new ArrayList<String>();
		
		list.add("100020675");
		List<UpdateOption> rs = productIndexService.updateProductIndex(list);
		for(UpdateOption uo : rs) {
			System.out.println(uo);
		}
	}
	
	@Test
	public void testUpdateTag() {
		List<String> list = new ArrayList<String>();
		list.add("100022637");
		
		productIndexService.updateProductTags(list);
		
	}
}
