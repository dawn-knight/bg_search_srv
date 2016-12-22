/*
 * 2014-11-17 下午4:05:59 吴健 HQ01U8435
 */

package com.mbgo.search.core.bean.indexthrd;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.service.ProductIndexService;

public class DocumentCreatorManager {

  private static Logger log = LoggerFactory.getLogger(DocumentCreatorManager.class);

  private ProductIndexService productIndexService;
  private List<Product> products = null;
  private int max = 10;
  private long dataVerson = -1;

  private Set<Integer> creators = new HashSet<Integer>();

  public DocumentCreatorManager(ProductIndexService service, long dv, List<Product> list) {
    this.productIndexService = service;
    this.dataVerson = dv;
    this.products = list;
  }

  // 开始时，记录线程
  synchronized private void beginCreator(Integer no) {
    creators.add(no);
  }

  // 结束时，删除标识符
  synchronized public void finishCreator(Integer no) {
    creators.remove(no);
  }

  public void run() {
    if (products == null || products.size() < 1) {
      return;
    }
    int totalProducts = products.size();
    int perTime = Math.round(totalProducts / max) + 1;
    for (int i = 0; i < max; i++) {
      int fromIndex = i * perTime;
      int toIndex = Math.min(fromIndex + perTime, totalProducts);
      if (fromIndex >= toIndex) {
        break;
      }
      List<Product> tempProduct = products.subList(fromIndex, toIndex);
      if (tempProduct == null || tempProduct.size() < 1) {
        continue;
      }
      beginCreator(i);
      new Thread(new DocumentCreator(i, this, tempProduct, productIndexService, dataVerson)).start();
    }
  }

  public void addProductIndex(List<SolrInputDocument> products) {
    productIndexService.addDocumentToServer(products, null);
  }

  // 处理结束
  synchronized private boolean isOk() {
    return creators.size() < 1;
  }

  public void waiFinish() {
    while (true) {
      if (isOk()) {
        break;
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}
