/*
* 2014-11-27 上午11:34:57
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mbgo.mybatis.mbshop.mapper.ProductLibColorMapper;
import com.mbgo.search.core.filter.color.ColorCaculator;
import com.mbgo.search.core.service.BaseTest;
import com.mbgo.search.core.service.use4.CacheService;

public class ColorMapperTest extends BaseTest {

	@Autowired
	private ProductLibColorMapper productLibColorMapper;
	@Autowired
	private CacheService cacheService;
	@Before
	public void init() {
		cacheService.initAll();
	}
	@Test
	public void testSeries() {
		System.out.println(productLibColorMapper.getSeriesMap());
	}
	@Test
	public void testColorFilter() {
		ColorCaculator colorCaculator = new ColorCaculator();
		
		colorCaculator.addSeriesInfo("01", 123);
		colorCaculator.addSeriesInfo("03", 22);
		colorCaculator.addSeriesInfo("04", 44);
		colorCaculator.addSeriesInfo("08", 67);
		colorCaculator.addSeriesInfo("10", 74);
		colorCaculator.addSeriesInfo("11", 99);
		
		System.out.println(colorCaculator.execute());
	}
}
