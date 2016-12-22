/*
* 2014-12-11 下午10:59:32
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.tags;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.mbgo.search.core.bean.index.TagInfo;
import com.mbgo.search.core.bean.query.tag.TagChainUtil;
import com.mbgo.search.util.ResourceReader;

public class TagChainTest {
	public List<TagInfo> createData() {
		List<TagInfo> rs = new ArrayList<TagInfo>();
		ResourceReader r = null;
		try {
			r = new ResourceReader(new File("G:/mbsearch/txt/测试标签.txt"), "gbk");
			r.load();
			String line = null;
			while((line = r.readLine()) != null) {
				String[] vals = line.split(",");
				if(vals.length == 2) {
					TagInfo ti = new TagInfo(vals[0], Integer.parseInt(vals[1]));
					rs.add(ti);
				}
			}
			
			Collections.sort(rs, new Comparator<TagInfo>() {

				@Override
				public int compare(TagInfo o1, TagInfo o2) {
					if(o1.getSort() > o2.getSort()) {
						return -1;
					}
					return 1;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return rs;
	}

	private Set<String> tags = null;
	@Before
	public void init() {
		tags = new HashSet<String>();
		tags.add("普通");
		tags.add("热卖");
		tags.add("yeshihuodong");
	}
	@Test
	public void testOldTagChain() {
		TagChainUtil.initChain(createData());
		long b = System.nanoTime();
		System.out.println(TagChainUtil.getDisplayTag(tags));
		System.out.println((System.nanoTime() - b) / 1000000f + " ms");
	}
}
