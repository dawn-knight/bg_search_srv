/*
* 2014-9-2 下午2:07:13
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service.impl;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mbgo.search.core.service.SolrTestService;

@Service("solrTestService")
public class SolrTestServiceImpl implements SolrTestService {
	
	@Override
	public void testService() {
//		log.debug("testService at : {}", System.currentTimeMillis());
//
//		System.out.println(productInfoBuffMapper.selectByPrimaryKey(1));
//		
//		System.out.println(searchSolrServer);
	}
	
	/*@Resource(name = "lbSearchGoodsSolrServer")
	private LBHttpSolrClient searchSolrServer;*/
	@Resource(name = "cloudSearchGoodsSolrClient")
  private CloudSolrClient cloudSearchGoodsSolrClient;
//	@Autowired
//	private ProductInfoBuffMapper productInfoBuffMapper;
	
	private static Logger log = LoggerFactory.getLogger(SolrTestServiceImpl.class);
}

