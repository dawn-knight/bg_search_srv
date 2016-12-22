/*
* 2014-10-29 下午4:17:24
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.dataetl.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mbgo.mybatis.mbsearch.bean.StockDetailInfo;

public class StockProduct {

	/**
	 * 商品id
	 */
	private String productId;
	
	/**
	 * 颜色库存信息
	 * 颜色码：库存值
	 */
	private Map<String, Integer> colorStockMap = new HashMap<String, Integer>();
	
	/**
	 * 该款式下sku库存信息
	 */
	Map<String, List<String>> colorProductSizeList = new HashMap<String, List<String>>();
	
	private List<String> productSizeCode = new ArrayList<String>();
	
	/**
	 * 产品总库存
	 */
	private Integer stock = 0;
	
	public List<String> getColorProductSizeList(String colorCodeId) {
		return colorProductSizeList.get(colorCodeId);
	}
	
	public StockProduct() {
	}
	
	public List<String> getProductSize() {
		return productSizeCode;
	}
	
	public void add(StockDetailInfo sdi) {
		try {

			int stock = sdi.getStock();
			this.stock += stock;
			
			String colorCodeId = sdi.getColorCodeId();
			Integer colorStock = colorStockMap.get(colorCodeId);
			if(colorStock == null) {
				colorStockMap.put(colorCodeId, stock);
			} else {
				colorStockMap.put(colorCodeId, colorStock + stock);
			}
			
			List<String> colorSizeList = colorProductSizeList.get(colorCodeId);
			String sizeCode = sdi.getSizeCode();
			if(colorSizeList != null) {
				colorSizeList.add(sizeCode);
			} else {
				colorSizeList = new ArrayList<String>();
				colorSizeList.add(sizeCode);
				colorProductSizeList.put(colorCodeId, colorSizeList);
			}
			
			if(!productSizeCode.contains(sizeCode)) {
				productSizeCode.add(sizeCode);
			}
		} catch (Exception e) {
			
		}
	}

	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public Map<String, Integer> getColorStockMap() {
		return colorStockMap;
	}
	public void setColorStockMap(Map<String, Integer> colorStockMap) {
		this.colorStockMap = colorStockMap;
	}
	public Integer getStock() {
		return stock;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}
	public Map<String, List<String>> getColorProductSizeList() {
		return colorProductSizeList;
	}
	public void setColorProductSizeList(
			Map<String, List<String>> colorProductSizeList) {
		this.colorProductSizeList = colorProductSizeList;
	}
}
