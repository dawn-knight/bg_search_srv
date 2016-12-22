/*
* 2014-10-30 上午10:07:47
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.util.List;

import com.mbgo.mybatis.mbsearch.bean.StockDetailInfo;
import com.mbgo.search.core.bean.index.Product;

/**
 * 库存中心对接相关service
 * @author 吴健 HQ01U8435
 */
public interface StockHandlerService {

	/**
	 * 根据product中的productId，productCode和storeId信息，查询对应的详细库存
	 * @param products 
	 * @return
	 */
	public List<StockDetailInfo> getProductStockInfo(List<Product> products);
}
