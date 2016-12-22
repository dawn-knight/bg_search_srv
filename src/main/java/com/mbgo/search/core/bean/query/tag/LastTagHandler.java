package com.mbgo.search.core.bean.query.tag;

import java.util.Set;

/** 
 * @author 吴健  (HQ01U8435)	Email : wujian@metersbonwe.com 
 * @version 创建时间：2012-12-20 下午4:41:10
 * 放置在标签访问链的末尾 
 */
public class LastTagHandler extends AbstractTag {

	public LastTagHandler(String n) {
		super(n);
	}

	@Override
	public String handle(Set<String> tags) {
		return "";
	}

}
