/*
* 2014-9-2 下午2:44:36
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.banggo.common.redis.RedisTemplate;

public class TestSolrServiceTest extends BaseTest {

	@Autowired
	private SolrTestService solrTestService;

	@Autowired
	private RedisTemplate redisTemplate;
	@Test
	public void testJuint() {
		System.out.println(solrTestService);
		solrTestService.testService();
		
		System.out.println(redisTemplate.get("search_test"));
		redisTemplate.set("search_test", System.currentTimeMillis() + "_1");
		System.out.println("value : " + redisTemplate.get("search_test"));
	}
	
	@Test
	public void testRedisReader() {
		String key = "testRead_";
		int num = 100;
		
		String[] keys = new String[num];
		for(int i = 0; i < num; i ++) {
//			redisClient.setExPojo(key + i, 86400, "testReadValue_" + i);
			keys[i] = key + i;
		}
		
		long total = System.currentTimeMillis();
		for(int i = 0; i < num; i ++) {
			redisTemplate.getPojo(key + i);
		}
		
		System.out.println("total cost : " + (System.currentTimeMillis() - total));
		
		long b = System.currentTimeMillis();
		List<String> rs = redisTemplate.mGetPojo(keys);
		long delt = System.currentTimeMillis() - b;
		for(String s : rs) {
			System.out.println(s);
		}
		System.out.println("cost : " + delt);
	}
	@Test
	public void testIncre() {
		String k = "increase";
//		redisTemplate.set(k, "1");
		redisTemplate.inc(k);
		System.out.println(redisTemplate.get(k));
	}
}
