package com.mbgo.search.core.bean.indexthrd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.service.DataEtlService;
import com.mbgo.search.core.service.ProductIndexService;

public class UpdateProductIndexThread implements Callable<Boolean> {

  private static Logger log = LoggerFactory
      .getLogger(UpdateProductIndexThread.class);
  private List<String> productIds;
  private long dataVerson = -1;
  private DataEtlService dataEtlService;
  private ProductIndexService productIndexService;

  public UpdateProductIndexThread(List<String> productIds, long dataVerson, DataEtlService dataEtlService, ProductIndexService productIndexService) {
    this.productIds = productIds;
    this.dataVerson = dataVerson;
    this.dataEtlService = dataEtlService;
    this.productIndexService = productIndexService;
  }

  @Override
  public Boolean call() throws Exception {
    Boolean createSuccess = Boolean.FALSE;
    List<String> deleteproductIds = new ArrayList<String>();
    try {
      // 商品上下架，处理productIds，找出uuid
      for (String pid : productIds) {
        if (StringUtils.isNotBlank(pid) && pid.length() != 6) {
          deleteproductIds.add(pid);
        }
      }

      for (String cPid : deleteproductIds) {
        if (StringUtils.isNotBlank(cPid)) {
          productIds.remove(cPid);
          String temp = cPid.substring(cPid.length() - 6);
          if (!productIds.contains(temp)) {
            productIds.add(temp);
          }
        }
      }

      List<Product> products = dataEtlService
          .getProductList(-1, -1, productIds);

      if (products == null || products.size() < 1) {
        createSuccess = Boolean.TRUE;
        return createSuccess;
      }
      try {
        List<SolrInputDocument> productDocs = new ArrayList<SolrInputDocument>();
        for (Product p : products) {
          try {
            // 过滤掉状态正常的商品
            deleteproductIds.remove(p.getProductUuid());
            // 全下架[全色全码下架]状态的商品
            if (p.getpStatus() != 1 || p.getColorProducts().size() < 1) {
              log.debug("good [{}] has no valid color and size",
                  p.getProductUuid());
              deleteproductIds.add(p.getProductUuid());
              continue;
            }
            SolrInputDocument doc = productIndexService.createProductDocument(p, dataVerson);
            productDocs.add(doc);
          } catch (Exception e) {
            log.debug("fail to create index 4 good [{}]",
                p.getProductId());
            log.error(e.getMessage(), e);
          }
        }
        productIndexService.addDocumentToServer(productDocs, null);
        // 需要删除的商品
        if (deleteproductIds.size() > 0) {
          log.debug("updateProductIndex delete goods: {}, {}", deleteproductIds.size(), deleteproductIds);
          productIndexService.deleteProductIndex(deleteproductIds);
        }
      } catch (Throwable t) {
        log.debug("catch any exception to prevent main thread keep waiting for the termination signal of sub-thread");
        log.error(t.getMessage(), t);
      }
      createSuccess = Boolean.TRUE;
    } catch (Exception e) {
      log.debug("update product index thread error");
      log.error(e.getMessage());
    }
    return createSuccess;
  }
}
