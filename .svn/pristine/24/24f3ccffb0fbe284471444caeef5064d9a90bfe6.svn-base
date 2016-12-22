/*
* 2015-2-26 上午10:32:26
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.dataetl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;

public class GoodsNameAndSalePointProcessor implements IProductProcessor {
	
	private Map<String, String> codeToId = new HashMap<String, String>();
	private List<Product> products = null;

	@Override
	public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
		if(codeToId == null || codeToId.size() < 1 || products == null) {
			return;
		}
		if(pm == null || pm.size() < 1) {
			return;
		}
		
		for(Product p : products) {
			String pcode = p.getProductCode();
			String pid = codeToId.get(pcode);
			if(StringUtils.isBlank(pid)) {
				continue;
			}
			Product oldP = pm.get(pid);
			if(oldP != null) {
				oldP.setGoodsName(p.getGoodsName());
//				oldP.setSalePoint(p.getSalePoint());
				oldP.setCreateTime(p.getCreateTime());
			}
		}
	}

	public GoodsNameAndSalePointProcessor(Map<String, String> codeToId, List<Product> products) {
		this.codeToId = codeToId;
		this.products = products;
	}
	
}
