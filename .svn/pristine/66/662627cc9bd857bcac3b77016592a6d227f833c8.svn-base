/*
 * 2014-9-24 下午4:05:09 吴健 HQ01U8435
 */

package com.mbgo.search.core.service;

import java.util.List;

import com.mbgo.search.core.bean.query.ParameterNewlyProductBean;
import com.mbgo.search.core.bean.query.ProductQuery;
import com.mbgo.search.core.bean.query.ProductQueryResult;
import com.mbgo.search.core.bean.query.ReturnNewlyProductBean;
import com.mbgo.search.core.bean.update.UpdateOption;
import com.mbgo.search.core.filter.FilterData;

public interface SearchService {

  /**
   * 新上架商品搜索
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
   */
  public FilterData searchFilterData(ProductQuery query);

  /**
   * 更新商品索引
   * 
   * @param pid
   * @param type
   *          update；delete
   * @return
   */
  public int updateProductIndex(String pid, String type);

  /**
   * 批量更新商品索引
   * 
   * @param uos
   * @return
   */
  public List<UpdateOption> updateProductIndexByList(List<UpdateOption> uos);

  /**
   * 更新商品标签信息
   * 
   * @param pids
   * @return
   */
  public boolean updateProductTags(List<String> pids);

}
