/*
* 2014-10-10 下午3:19:25
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.bean.query.tag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mbgo.search.core.bean.index.TagInfo;

public class TagChainUtil {
	private static Logger log = LoggerFactory.getLogger(TagChainUtil.class);
	
	private static AbstractTag tagChain = null;
	public static void initChain(List<TagInfo> displayTags) {
		if(displayTags == null || displayTags.size() < 1) {
			tagChain = new TagHandler("");
			return;
		}
		
		AbstractTag mainTag = new TagHandler(displayTags.get(0).getName());
		AbstractTag header = mainTag;
		for(int i = 1; i < displayTags.size(); i ++) {
			AbstractTag newHandler = new TagHandler(displayTags.get(i).getName());
			header.setNext(newHandler);
			header = newHandler;
		}
		header.setNext(new LastTagHandler(null));
		tagChain = mainTag;
	}
	
	public static String getDisplayTag(Set<String> tags) {
		try {
			if(tagChain == null || tags == null) {
				return null;
			}
			return tagChain.handle(tags);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return "";
		}
	}
	
	public static void main(String[] args) {
		Set<String> tags = new HashSet<String>();
		tags.add("普通");
		tags.add("热卖");
		tags.add("yeshihuodong");
		long b = System.currentTimeMillis();
		System.out.println(TagChainUtil.getDisplayTag(tags));
		System.out.println("cost " + (System.currentTimeMillis() - b));
	}
}
