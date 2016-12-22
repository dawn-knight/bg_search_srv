/*
* 2014-9-30 下午2:08:44
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

public class ProductIndexServiceTest extends BaseTest {
	@Resource(name = "productIndexService")
	private ProductIndexService productIndexService;

	@Test
	public void testBuild() {
		long b = System.currentTimeMillis();
		productIndexService.rebuildProductIndex();
		
		System.out.println("productIndexService.rebuildProductIndex : " + (System.currentTimeMillis() - b));
	}
	
	@Test
	public void testUpdateProductIndex() {
		List<String> prods = new ArrayList<String>();
		prods.add("556507");
		prods.add("549583");
		prods.add("598002");
		
		try {
			productIndexService.updateProductIndex(prods);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
