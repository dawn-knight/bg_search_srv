/*
 * 2014-12-16 下午4:00:15 吴健 HQ01U8435
 */

package com.mbgo.search.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import com.mbgo.search.common.SolrCollectionNameDefineBean;
import com.mbgo.search.core.tools.FieldUtil;

public class SolrUpdateTest extends BaseTest {

  /*
   * @Resource(name = "lbIndexColorGoodsSolrServer") private LBHttpSolrServer
   * IndexColorGoodsSolrServer;
   */
  @Resource(name = "cloudSearchColorGoodsSolrClient")
  private CloudSolrClient cloudSearchColorGoodsSolrClient;

  // 100020758

  @Test
  public void test() throws SolrServerException, IOException {
    List<String> tags = new ArrayList<String>();

    tags.add("wap");
    tags.add("测试");
    tags.add("new0");

    Map<String, Object> ky = new HashMap<String, Object>();

    SolrInputDocument doc = new SolrInputDocument();

    ky.put("set", tags);

    doc.setField(FieldUtil.PRODUCT_TAG, ky);

    cloudSearchColorGoodsSolrClient.add(SolrCollectionNameDefineBean.GOODSCOLOR, doc);
    cloudSearchColorGoodsSolrClient.commit(SolrCollectionNameDefineBean.GOODSCOLOR);
  }
}
