package com.mbgo.search.core.bean.indexthrd;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.service.ProductIndexService;


public class DocumentUpdate implements Runnable{

  static private Logger log = LoggerFactory.getLogger(DocumentUpdate.class);

  private int no;
  private long dataVerson = -1;
  private List<Product> products = null;
  private List<String> deleteproductIds;
  private DocumentUpdateManager manager = null;
  private ProductIndexService indexService;

  public DocumentUpdate(int no, DocumentUpdateManager m, List<Product> ps, ProductIndexService service, long dv, List<String> deleteproductIds) {
    super();
    this.no = no;
    this.manager = m;
    this.products = ps;
    this.indexService = service;
    this.dataVerson = dv;
    this.deleteproductIds = deleteproductIds;
  }

  @Override
  public void run() {
    if (products != null) {
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
            SolrInputDocument doc = indexService.createProductDocument(p, dataVerson);
            productDocs.add(doc);
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
