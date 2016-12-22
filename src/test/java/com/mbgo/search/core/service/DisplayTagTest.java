/*
* 2014-12-9 下午4:04:07
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mbgo.mybatis.mbsearch.mapper.MgrGoodsTagsMapper;
import com.mbgo.search.core.bean.index.TagInfo;
import com.mbgo.search.core.bean.query.tag.TagChainUtil;

public class DisplayTagTest extends BaseTest {
	
	@Autowired
	private MgrGoodsTagsMapper mgrGoodsTagsMapper;

	@Test
	public void test() {
		List<TagInfo> disPlayTags = mgrGoodsTagsMapper.getDisplayTags();
		
		TagChainUtil.initChain(disPlayTags);

		Set<String> tags = new HashSet<String>();
		tags.add("普通");
		tags.add("热卖");
		tags.add("新品");
		tags.add("平价");
		long b = System.currentTimeMillis();
		System.out.println(TagChainUtil.getDisplayTag(tags));
		System.out.println("cost " + (System.currentTimeMillis() - b));
	}
}
