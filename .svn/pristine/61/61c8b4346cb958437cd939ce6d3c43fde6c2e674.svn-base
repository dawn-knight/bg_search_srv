/*
* 2014-12-16 下午2:39:07
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ProductIdsManager implements IdsManager {
	private String name = "";

	private List<String> productIds = new ArrayList<String>();
	
	public ProductIdsManager(String name) {
		this.name = name;
	}

	@Override
	public List<String> get() {
		
		List<String> temp = productIds;
		
		productIds = new ArrayList<String>();
		return temp;
	}

	@Override
	public void clear() {
		productIds.clear();
	}

	@Override
	public void add(String id) {
		if(StringUtils.isBlank(id) || id.length() != 9) {
			return;
		}
		if(!productIds.contains(id)) {
			productIds.add(id);
			
		}
	}
	
	public String toString() {
		return name;
	}
}
