/*
 * 2014-9-24 下午3:59:25 吴健 HQ01U8435
 */

package com.mbgo.search.core.service;

import java.io.IOException;
import java.util.List;

import com.mbgo.search.core.bean.query.ParameterNewlyProductBean;
import com.mbgo.search.core.bean.query.ProductQuery;
import com.mbgo.search.core.bean.query.ProductQueryResult;
import com.mbgo.search.core.bean.query.ReturnNewlyProductBean;
import com.mbgo.search.core.filter.FilterData;

public interface ProductSearchService {

  /**
   * 最近上架商品搜索
   * 
   * @param query
   * @return
   */
  public ReturnNewlyProductBean searchNewlyProduct(ParameterNewlyProductBean p);

  /**
   * 商品搜索
   * 
   * @param query
   * @return
   */
  public ProductQueryResult search(ProductQuery query);

  /**
   * 筛选器
   * 
   * @param query
   * @return
   * @throws IOException 
   */
  public FilterData searchFilterData(ProductQuery query) throws IOException;

  /**
   * 查询关键字结果数量
   * 
   * @param word
   * @return
   */
  public long countForWord(String word);

  public List<String> analyzeWord(String word);
  /**
	 * 商品搜索
	 * @param query
	 * @return
	 */
	public ProductQueryResult searchColor(ProductQuery query);
}
