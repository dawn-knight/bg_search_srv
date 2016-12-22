package com.mbgo.search.core.bean.query.tag;

import java.util.Set;

/** 
 * @author 吴健  (HQ01U8435)	Email : wujian@metersbonwe.com 
 * @version 创建时间：2012-9-11 上午10:27:25 
 */
public abstract class AbstractTag {
	protected AbstractTag nexter;
	protected String tagName;
	public AbstractTag(String n) {
		tagName = n;
	}
	public void setNext(AbstractTag at) {
		nexter = at;
	}
	public abstract String handle(Set<String> tags);
}
