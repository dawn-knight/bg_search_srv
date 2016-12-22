/*
 * 2014-9-24 下午4:08:57 吴健 HQ01U8435
 */

package com.mbgo.search.core.service;

import java.util.List;

import org.apache.solr.common.SolrInputDocument;

import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.bean.update.UpdateOption;

public interface ProductIndexService {
  
  /**
   * 重建更新索引开关
   */
  public String rebuildAndUpdateSwitch(String password, int openFlag);
  /**
   * 重建索引
   */
  public void rebuildProductIndex();

  /**
   * 商品信息转solr文档
   * 
   * @param p
   * @param currentVersion
   * @return
   */
  public SolrInputDocument createProductDocument(Product p, long currentVersion) throws Exception;

  // public List<SolrInputDocument> createColorProductDocument(Product p, long currentVersion);

  public void addDocumentToServer(List<SolrInputDocument> productIndex, List<SolrInputDocument> colorProductIndex);

  /**
   * 增量更新索引
   */
  public void updateProductIndex();

  /**
   * 指定商品code，更新索引
   * 
   * @param productCode
   */
  public void updateProductIndexByPids(List<String> pids, long currentVersion) throws Exception;

  public List<UpdateOption> updateProductIndex(List<String> pids)
      throws Exception;

  /**
   * 删除指定商品code的索引
   * 
   * @param productCode
   */
  public boolean deleteProductIndex(List<String> pids) throws Exception;

  /**
   * 更新商品标签
   */
  public void updateProductTags(List<String> pids);

  public void commit();

  /**
   * 更新库存信息
   * 
   * @param pids
   * @return
   */
  public void sycnProductStocks(List<String> pids);

  /**
   * 更新商品促销信息
   * 
   * @param pids
   * @return
   */
  public void sycnProductPromotions(List<String> pids);
}
