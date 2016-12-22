/*
* 2014-9-28 下午4:16:12
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.dataetl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;

public class ProductStatisticProcessor implements IProductProcessor {
	private List<Product> products = new ArrayList<Product>();

	@Override
	public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
		if(products == null || products.size() < 1) {
			return;
		}
		if(pm == null || pm.size() < 1) {
			return;
		}
		for(Product p : products) {
			//String productId = p.getProductId();
		  String productId = p.getProductUuid();
			Product baseProduct = pm.get(productId);
			if(baseProduct != null) {
				baseProduct.setSaleCount(p.getSaleCount());
				baseProduct.setMonthSaleCount(p.getMonthSaleCount());
				baseProduct.setGsiRank(p.getGsiRank());
			}
		}
	}

	public ProductStatisticProcessor(List<Product> products) {
		super();
		this.products = products;
	}
}
