/*
* 2014-12-16 下午2:39:48
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.outer;

import java.util.List;

public interface IdsManager {

	public List<String> get();
	
	public void clear();
	
	public void add(String id);
}

