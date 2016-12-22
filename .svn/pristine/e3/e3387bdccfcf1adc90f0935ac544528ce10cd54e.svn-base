/*
* 2014-12-12 下午2:15:00
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.tools.queue;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import com.mbgo.search.core.dataetl.page.PageManager;
import com.mbgo.search.util.HttpClientUtil;
import com.mbgo.search.util.PropertieUtil;

@Service("keywordLogQueueManager")
public class KeywordLogQueueManager {

	private volatile static List<String> KEYWORD_COUNT_LIST = new ArrayList<String>();
	
	private long sleepTime = 5000;
	
	private static WriteLogThread logThread = null;
	
	private static final String url = PropertieUtil.getStrProp("resource", "SEARCH_MANAGER_LOG_URL");
	
	public KeywordLogQueueManager() {
		logThread = new WriteLogThread();
		logThread.start();
	}
	
	public void addLogWord(String w, long c) {
		KEYWORD_COUNT_LIST.add(w + "#:#" + c);
	}
	
	public void saveToMysql(List<String> list) {
		if(list == null || list.size() < 1) {
			return;
		}
		StringBuilder sb = new StringBuilder("");
		for(String s : list) {
			sb.append(s).append("#;#");
		}
		List<NameValuePair> pms = new ArrayList<NameValuePair>();
		pms.add(new BasicNameValuePair("keywordCount", sb.toString()));
		HttpClientUtil.post(url, pms);
	}
	
	class WriteLogThread extends Thread {
		public void run() {
			while(true) {
				try {
					
					Thread.sleep(sleepTime);
					
					List<String> result = KEYWORD_COUNT_LIST;
					KEYWORD_COUNT_LIST = new ArrayList<String>();

					PageManager pageManager = new PageManager(result.size(), 200);
					
					while(pageManager.hasNextPage()) {
						saveToMysql(result.subList((int)pageManager.getFirst(), pageManager.getToIndex()));
					}
				} catch (Exception e) {
				}
			}
		}
		
		@Override
		public void setUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
			if(logThread == null || !logThread.isAlive()) {
				logThread = new WriteLogThread();
				logThread.start();
			}
			super.setUncaughtExceptionHandler(eh);
		}
	}
	
	public static void main(String[] args) {

		List<NameValuePair> pms = new ArrayList<NameValuePair>();
		long b = System.currentTimeMillis();
		HttpClientUtil.post("http://www.baidu.com", pms);
		System.out.println(System.currentTimeMillis() - b);
	}
}
