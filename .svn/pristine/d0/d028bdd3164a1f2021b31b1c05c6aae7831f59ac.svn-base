/*
* 2014-11-17 下午3:00:16
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service.use4;

import java.util.ArrayList;
import java.util.List;

import com.mbgo.search.core.bean.index.Product;

public class ProdcutInfoCreator {

	private List<Product> old = new ArrayList<Product>();

	public ProdcutInfoCreator(List<Product> old) {
		super();
		this.old = old;
	}
	
	public List<Product> createAll() {
		List<Product> ps = new ArrayList<Product>();
		if(old != null) {
			for(Product p : old) {
				if(!p.isValidate()) {
					continue;
				}
				ps.addAll(newProducts(p));
			}
		}
		return ps;
	}
	
	private Product newProduct(Product p, String lst) {
		if(!p.isValidate()) {
			return null;
		}
		String npid = p.getProductId() + lst;
		Product np = p.clone(npid);
		return np;
	}
	private List<Product> newProducts(Product p) {
		List<Product> ps = new ArrayList<Product>();
		for(int i = 0; i < 1500; i ++) {
			Product np = newProduct(p, "0131" + i);
			if(np != null) {
				ps.add(np);
			}
		}
		return ps;
	}

	public List<Product> getOld() {
		return old;
	}

	public void setOld(List<Product> old) {
		this.old = old;
	}
}
