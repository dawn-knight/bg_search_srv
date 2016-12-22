/*
 * 2014-11-17 下午4:05:29 吴健 HQ01U8435
 */

package com.mbgo.search.core.bean.indexthrd;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.service.ProductIndexService;

public class DocumentCreator implements Runnable {

  static private Logger log = LoggerFactory.getLogger(DocumentCreator.class);

  private int no;
  private long dataVerson = -1;
  private List<Product> products = null;
  private DocumentCreatorManager manager = null;
  private ProductIndexService indexService;

  public DocumentCreator(int no, DocumentCreatorManager m, List<Product> ps, ProductIndexService service, long dv) {
    super();
    this.no = no;
    this.manager = m;
    this.products = ps;
    this.indexService = service;
    this.dataVerson = dv;
  }

  @Override
  public void run() {
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

            SolrInputDocument sd = indexService
                .createProductDocument(p, dataVerson);
            productDocs.add(sd);
          } catch (Exception e) {
            log.debug("fail to create index 4 good [{}]",
                p.getProductId());
            log.error(e.getMessage(), e);
          }
        }
        manager.addProductIndex(productDocs);
      } catch (Throwable t) {
        log.debug("catch any exception to prevent main thread keep waiting for the termination signal of sub-thread");
        log.error(t.getMessage(), t);
      }
    }
    manager.finishCreator(no);
  }

  public int getNo() {
    return no;
  }

  public void setNo(int no) {
    this.no = no;
  }

}
