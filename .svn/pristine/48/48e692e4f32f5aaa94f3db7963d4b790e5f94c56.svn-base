package com.mbgo.search.core.bean.indexthrd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.service.DataEtlService;
import com.mbgo.search.core.service.ProductIndexService;

public class RebuildProductIndexThread implements Callable<Boolean> {

  private static Logger log = LoggerFactory
      .getLogger(RebuildProductIndexThread.class);
  private long startPosition = 0;
  private long limitSize = 0;
  private long dataVerson = -1;
  private DataEtlService dataEtlService;
  private ProductIndexService productIndexService;

  public RebuildProductIndexThread(long startPosition, long limitSize, long dataVerson, DataEtlService dataEtlService, ProductIndexService productIndexService) {
    this.startPosition = startPosition;
    this.limitSize = limitSize;
    this.dataVerson = dataVerson;
    this.dataEtlService = dataEtlService;
    this.productIndexService = productIndexService;
  }

  @Override
  public Boolean call() throws Exception {
    Boolean createSuccess = Boolean.FALSE;
    try {
      List<String> productIds = dataEtlService.getProductIds(
          startPosition, limitSize);
      if (productIds == null || productIds.size() < 1) {
        createSuccess = Boolean.TRUE;
        return createSuccess;

      }
      List<Product> products = dataEtlService.getProductList(-1, -1, productIds);
      if (products != null) {
        try {
          List<SolrInputDocument> productDocs = new ArrayList<SolrInputDocument>();
          for (Product p : products) {
            try {
              if (p.getColorProducts().size() < 1) {
                log.debug("good [{}] has no valid color and size",
                    p.getProductUuid());
                continue;
              }

              SolrInputDocument sd = productIndexService
                  .createProductDocument(p, dataVerson);
              productDocs.add(sd);
            } catch (Exception e) {
              log.debug("fail to create index 4 good [{}]",
                  p.getProductId());
              log.error(e.getMessage(), e);
            }
          }
          productIndexService.addDocumentToServer(productDocs, null);
        } catch (Throwable t) {
          log.debug("catch any exception to prevent main thread keep waiting for the termination signal of sub-thread");
          log.error(t.getMessage(), t);
        }
      }
      createSuccess = Boolean.TRUE;
    } catch (Exception e) {
      log.debug("rebuild product index thread error");
      log.error(e.getMessage());
    }
    return createSuccess;
  }

}
