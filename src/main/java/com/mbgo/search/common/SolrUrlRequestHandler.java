package com.mbgo.search.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mbgo.search.util.HttpClientUtil;
import com.mbgo.search.util.PropertieUtil;

/** 
 * @author 吴健  (HQ01U8435)	Email : wujian@metersbonwe.com 
 * @version 创建时间：2012-12-7 上午9:58:13 
 * solr master服务器是否允许slaver同步数据控制类
 * 
 * 当solr重建索引时，关闭master对外提供数据同步接口
 * 重建结束时，重新开启master对外提供数据同步的接口
 */
public class SolrUrlRequestHandler {
	private static SolrUrlReader solrUrlReader = new SolrUrlReader(PropertieUtil.getStrProp("resource", SolrUrlReader.MASTER_URL), 
			PropertieUtil.getStrProp("resource", SolrUrlReader.MASTER_BLOCK));

	private static Logger log = LoggerFactory.getLogger(SolrUrlRequestHandler.class);
	private static List<NameValuePair> params = new ArrayList<NameValuePair>();
	/**
	 * 关闭solr主服务器【所有搜索模块】对外提供索引复制的接口
	 */
	public static void disableCopyFromMaster() {
		String logPrev = "[solr command] ";
		reset();
		log.debug(logPrev + "send disablereplication command to master for all block");
		while(solrUrlReader.hasNext()) {
			String url = solrUrlReader.getDisableUrl();
			HttpClientUtil.post(url, params);
			log.debug(logPrev + "disablereplication : " + url);
		}
	}

	/**
	 * 打开solr主服务器【所有搜索模块】对外提供索引复制的接口
	 */
	public static void enableCopyFromMaster() {
		String logPrev = "[solr command] ";
		reset();
		log.debug(logPrev + "send enablereplication command to master for all block");
		while(solrUrlReader.hasNext()) {
			String url = solrUrlReader.getEnableUrl();
			HttpClientUtil.post(url, params);
			log.debug(logPrev + "enablereplication : " + url);
		}
	}
	
	/**
	 * 关闭solr主服务器【指定名称（block）的搜索模块】对外提供索引复制的接口
	 * @param block
	 */
	public static void disableCopyFromMaster(String block) {
		String url = solrUrlReader.getDisableUrlByBlock(block);
		log.debug("[solr command] disablereplication : " + url);
		HttpClientUtil.post(url, params);
	}
	public static void disableCopyFromMaster(String ... blocks) {
		for(String block : blocks) {
			disableCopyFromMaster(block);
		}
	}
	/**
	 * 打开solr主服务器【指定名称（block）的搜索模块】对外提供索引复制的接口
	 * @param block 搜索模块
	 */
	public static void enableCopyFromMaster(String block) {
		String url = solrUrlReader.getEnableUrlByBlock(block);
		log.debug("[solr command] enablereplication : " + url);
		HttpClientUtil.post(url, params);
	}
	
	public static void enableCopyFromMaster(String ... blocks) {
		for(String block : blocks) {
			enableCopyFromMaster(block);
		}
	}
	
	private static void reset() {
		solrUrlReader.reset();
	}
	
	public static void main(String[] args) {
//		while(solrUrlReader.hasNext()) {
//			String url = solrUrlReader.getEnableUrl();
//			System.out.println(url);
//		}
		
//		disableCopyFromMaster();
		enableCopyFromMaster();
	}
}
