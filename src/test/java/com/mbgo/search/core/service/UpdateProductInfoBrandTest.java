/*
* 2014-10-24 上午10:17:19
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mbgo.mybatis.mbshop.bean.ProductInfo;
import com.mbgo.mybatis.mbshop.bean.ProductLibBrand;
import com.mbgo.mybatis.mbshop.mapper.ProductInfoMapper;
import com.mbgo.mybatis.mbshop.mapper.ProductLibBrandMapper;
import com.mbgo.search.util.NullPathException;
import com.mbgo.search.util.ResourceReader;

public class UpdateProductInfoBrandTest extends BaseTest {
	@Autowired
	private ProductLibBrandMapper productLibBrandMapper;
	@Autowired
	private ProductInfoMapper productInfoMapper;

	public static Map<String, Integer> GOODS_SN_BRAND = new HashMap<String, Integer>();
	public static Map<String, Integer> CODE_ID = new HashMap<String, Integer>();
	@Before
	public void init() throws NullPathException, IOException {
		List<ProductLibBrand> brands = productLibBrandMapper.selectList();
		for(ProductLibBrand b : brands) {
			CODE_ID.put(b.getBrandCode().toUpperCase(), b.getBrandId());
		}
		
		ResourceReader reader = new ResourceReader(new File("G:/mbsearch/doc/product_brand.txt"));
		reader.load();
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] vals = line.split(",");
			if(vals.length > 1) {
				String brandCode = vals[1].toUpperCase();
				Integer id = CODE_ID.get(brandCode);
				if(id == null) {
					id = 16;
				}
				GOODS_SN_BRAND.put(vals[0], id);
			}
		}
	}
	
	@Test
	public void updateProductBrand() {
		List<ProductInfo> products = productInfoMapper.selectAllAsList();
		for(ProductInfo p : products) {
			String sn = p.getProductCode();
			Integer id = GOODS_SN_BRAND.get(sn);
			if(id == null) {
				id = 16;
			}
			p.setBrandId(id);
			productInfoMapper.updateByPrimaryKeySelective(p);
		}
	}
}
