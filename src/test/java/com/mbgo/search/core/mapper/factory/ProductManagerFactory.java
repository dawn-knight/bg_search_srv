/*
* 2014-12-25 上午9:29:39
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.mapper.factory;

import java.util.HashSet;
import java.util.Set;

import com.mbgo.search.core.dataetl.ProductManager;

public class ProductManagerFactory {

	@SuppressWarnings("unused")
	private static ProductManager productManager = null;
	
	private static volatile Set<String> creator = new HashSet<String>();
	
	public synchronized void startQuery(IDataQuery query) {
		creator.add(query.queryFlag());
	}
	public synchronized void finishQuery(IDataQuery query) {
		creator.remove(query.queryFlag());
	}
}
