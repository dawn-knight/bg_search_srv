/*
* 2014-10-28 下午3:54:14
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mbgo.search.core.bean.keyword.HotWordQuery;
import com.mbgo.search.core.service.use4.RedisCacheService;

public class HotWordServiceTest extends BaseTest {

	@Autowired
	private RedisCacheService redisCacheService;
	
	@Test
	public void test() {
		HotWordQuery query = new HotWordQuery();
		query.setNum(11);
//		query.setIsCache("0");
		List<String> rs = redisCacheService.findHotWord(query);
		System.out.println(rs);
	}
}
