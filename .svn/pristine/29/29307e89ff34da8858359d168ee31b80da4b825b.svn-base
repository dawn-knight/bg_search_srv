/*
 * 2014-10-15 下午2:07:04 吴健 HQ01U8435
 */

package com.mbgo.search.core.service;

import java.util.List;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.dataetl.stock.ProductStockManager;

public interface DataEtlService {

  public List<String> getProductIds(long first, long max) throws Exception;

  /**
   * 获得详细的product信息：分成四个sql进行信息合并 如果指定了productId，则分页limit信息无效，直接根据pids进行信息查询
   * 
   * @param first
   *          limit 的 first
   * @param max
   *          limit 的 max
   * @param pids
   *          指定了productId
   * @return
   */
  public List<Product> getProductList(long first, long max, List<String> pids) throws Exception;

  public void saveProductToRedis(Product product);

  /**
   * 保存商品信息product；key是colorCodeId
   * 
   * @param colorCodeId
   * @param p
   * @param seconds
   */
  public void saveColorProductToRedis(String colorCodeId, Product p);

  public List<Product> readProductFromRedis(List<String> productIds);

  /**
   * 根据12位码（颜色商品码）获得商品Product信息
   * 
   * @param colorProductIds
   * @return
   */
  public List<Product> readColorProductFromRedis(List<String> colorProductIds);

  public List<Product> getGoodsTags(MybatisBean option);

  /**
   * 更新库存信息
   * 
   * @param pids
   * @return
   */
  public ProductStockManager sycnProductStocks(List<String> pids);

  /**
   * 更新商品促销信息
   * 
   * @param pids
   * @return
   */
  public List<Product> sycnProductPromotions(List<String> pids);

}
