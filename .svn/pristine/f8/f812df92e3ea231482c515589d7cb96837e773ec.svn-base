/*
* 2014-12-16 下午3:31:16
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.dataetl.stock;

import java.util.HashMap;
import java.util.Map;

import com.mbgo.mybatis.mbsearch.bean.StockDetailInfo;

public class ProductStockManager {

	private Map<String, StockProduct> productStockMap = new HashMap<String, StockProduct>();
	public ProductStockManager() {
		
	}
	
	public void add(StockDetailInfo sdi) {
		if(sdi == null) {
			return;
		}
		String pid = sdi.getProductId();
		StockProduct sp = productStockMap.get(pid);
		if(sp == null) {
			sp = new StockProduct();
			sp.setProductId(pid);
			sp.add(sdi);
			productStockMap.put(pid, sp);
		} else {
			sp.add(sdi);
		}
	}

	public Map<String, StockProduct> getProductStockMap() {
		return productStockMap;
	}

}
