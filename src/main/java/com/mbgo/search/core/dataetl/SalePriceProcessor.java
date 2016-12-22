/*
* 2014-10-30 下午3:23:21
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.dataetl;

import java.util.List;
import java.util.Map;

import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;

public class SalePriceProcessor implements IProductProcessor {
	private List<Product> products = null;

	@Override
	public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
		if(products != null) {
			for(Product p : products) {
				//Product old = pm.get(p.getProductId());
        Product old = pm.get(p.getProductUuid());
				if(old != null) {
					old.setSalesPrice(p.getSalesPrice());
				}
			}
		}
	}

	public SalePriceProcessor(List<Product> products) {
		super();
		this.products = products;
	}

}
