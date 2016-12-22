/*
* 2015-3-13 下午4:41:36
* 吴健 HQ01U8435
*/

package com.mbgo.bootstest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

public class TestBoots {
	private static String url = "http://localhost:8080/solr/goods";
	public CloudSolrClient client = null;
	@Before
	public void init() {
	    client = new CloudSolrClient(url);
	    /*
	     * try { client = new CloudSolrClient(url); } catch (MalformedURLException e) {
	     * e.printStackTrace(); }
	     */
	  }
	
	private SolrInputDocument createDocument(String gsn, String id, float boosts) {
		SolrInputDocument d = new SolrInputDocument();
		
		String k = "这款衬衫采用高品质细腻牛津纺面料，触感细腻，穿着舒适透气。合体修身的版型完美勾勒姣好身形。经典百搭的款式结合精致的细节，百搭又有型，是今季必备的时髦单品哦~";
		d.setField("goodsSn", gsn);
		d.setField("title", k);
		d.setField("c", 1);
		d.setField("cid", 1);
		d.setField("pt", 1);
		d.setField("attrValue", 1);
		d.setField("size", 1);
		d.setField("tag", id);
		d.setField("price", boosts);
		
		d.setField("keyword", id + " test", boosts);
		
		return d;
	}
	@Test
	public void testCreate() throws SolrServerException, IOException {
		int max = 100;
		List<SolrInputDocument> indocs = new ArrayList<SolrInputDocument>();
		for(int i = 0; i < max; i ++) {
			String gsn = "11100" + i;
			
			int v = i % 10 + 1;
			String id = "HQ01_" + v;
			float boost = v * 9000;
			
			SolrInputDocument d = createDocument(gsn, id, boost);
			
			indocs.add(d);
		}
		
		client.add(indocs);
		client.commit();
		client.optimize();
	}
	
	@Test
	public void testSearch() throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();
		query.setQuery("(title : \"修身\"~10000) OR (keyword : \"test\")");
		query.setRows(200);
		
		QueryResponse rs = client.query(query);
		
		SolrDocumentList list = rs.getResults();
		
		System.out.println(list.getNumFound());
		
		for(SolrDocument d : list) {
			System.out.println(d.getFieldValues("goodsSn") + " : " + d.getFieldValues("tag") + " : boosts=" + d.getFirstValue("price"));
    }
  }
}
