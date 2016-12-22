package com.mbgo.search.common;

import java.util.HashMap;
import java.util.Map;

import com.mbgo.search.util.PropertieUtil;

/** 
 * @author 吴健  (HQ01U8435)	Email : wujian@metersbonwe.com 
 * @version 创建时间：2012-12-7 上午9:46:00 
 * solr接口请求url类
 */
public class SolrUrlReader {
	/**
	 * replication url
	 */
	public final static String MASTER_URL = "master.url";
	/**
	 * replication block，索引模块名字
	 */
	public final static String MASTER_BLOCK = "master.block";
	/**
	 * command参数值，封闭master对所有slaver的复制功能
	 */
	private final static String DISABLE = "disablereplication";
	/**
	 * command参数值，启用master对所有slaver的复制功能
	 */
	private final static String ENABLE = "enablereplication";
	private int index = -1;
	private int max = 0;
	private String url;
	private String[] blockArr;
	/**
	 * 禁用replication功能的 url
	 */
	private Map<String, String> disableUrlMaps = new HashMap<String, String>();
	/**
	 * 启用replication功能的 url
	 */
	private Map<String, String> enableUrlMaps = new HashMap<String, String>();
	
	public SolrUrlReader() {
		
	}
	public SolrUrlReader(String url, String blocks) {
		super();
		this.url = url;
		blockArr = blocks == null ? new String[]{} : blocks.split(",");
		max = blockArr.length;
		initUrlMap();
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setBlockArr(String[] blockArr) {
		this.blockArr = blockArr;
	}
	/**
	 * 重置遍历url的数组索引
	 */
	public void reset() {
		this.index = -1;
		max = blockArr.length;
	}
	public boolean hasNext() {
		return ++ index < max;
	}
	/**
	 * 获得关闭复制功能的url
	 * @return http://localhost:port/solr/block/replication?command=disablereplication
	 */
	public String getDisableUrl() {
		String url = this.url.replaceAll("\\{block\\}", blockArr[index]);
		return url + DISABLE;
	}
	/**
	 * 获得开启复制功能的url
	 * @return http://localhost:port/solr/block/replication?command=enablereplication
	 */
	public String getEnableUrl() {
		String url = this.url.replaceAll("\\{block\\}", blockArr[index]);
		return url + ENABLE;
	}
	/**
	 * 初始化replication相关接口的url
	 */
	private void initUrlMap() {
		reset();
		while(hasNext()) {
			String key = blockArr[index];
			disableUrlMaps.put(key, getDisableUrl());
			enableUrlMaps.put(key, getEnableUrl());
		}
		reset();
	}
	/**
	 * 根据指定搜索模块名称，获取对应封闭复制功能接口url
	 * @param block 搜索模块名称
	 * @return
	 */
	public String getDisableUrlByBlock(String block) {
		return disableUrlMaps.get(block);
	}
	/**
	 * 根据指定搜索模块名称，获取对应开启复制功能接口url
	 * @param block 搜索模块名称
	 * @return
	 */
	public String getEnableUrlByBlock(String block) {
		return enableUrlMaps.get(block);
	}
	
	public static void main(String[] args) {
		SolrUrlReader solrUrlReader = new SolrUrlReader(PropertieUtil.getStrProp("resource", MASTER_URL), 
				PropertieUtil.getStrProp("resource", MASTER_BLOCK));
		while(solrUrlReader.hasNext()) {
			System.out.println(solrUrlReader.getDisableUrl());
		}
		solrUrlReader.reset();
		while(solrUrlReader.hasNext()) {
			System.out.println(solrUrlReader.getEnableUrl());
		}
		
		System.out.println(solrUrlReader.getDisableUrlByBlock("goods"));
	}
}
