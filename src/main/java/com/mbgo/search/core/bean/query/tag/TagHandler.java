package com.mbgo.search.core.bean.query.tag;

import java.util.Set;

/** 
 * @author 吴健  (HQ01U8435)	Email : wujian@metersbonwe.com 
 * @version 创建时间：2012-9-11 上午10:33:00 
 */
public class TagHandler extends AbstractTag {
	
	public TagHandler(String n) {
		super(n);
	}

	@Override
	public String handle(Set<String> tags) {
		//将这个条件判断，移到TagChainUtil.getDisplayTag 方法，在源头过滤，提高效率；将耗时从5ms 降低到 0.1ms
//		if(tags == null || StringUtils.isBlank(tagName)) {
//			return "";
//		}
		if(tags.contains(tagName)) {
			return tagName;
		} else {
			return nexter.handle(tags);
		}
	}

}
