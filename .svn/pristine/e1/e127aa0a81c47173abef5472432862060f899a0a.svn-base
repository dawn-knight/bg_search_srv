/*
* 2014-10-15 上午11:29:32
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.mapper;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbsearch.mapper.MgrGoodsTagsMapper;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.service.BaseTest;

public class MgrGoodsTagsMapperTest extends BaseTest {

	@Autowired
	private MgrGoodsTagsMapper mgrGoodsTagsMapper;
	@Test
	public void test() {
		List<Product> pgs = mgrGoodsTagsMapper.getProductTags(new MybatisBean());
		for(Product p : pgs) {
			if(p.getProductId().equalsIgnoreCase("100000035")) {
				System.out.println(p.getProductId() + " : " + p.getTags());
			}
		}
	}
}
