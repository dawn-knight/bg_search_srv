/*
* 2014-12-16 上午9:36:39
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.util.List;

import com.mbgo.mybatis.mbsearch.bean.SearchErrorLog;

public interface SearchErrorLogService {

	public void log(String msg, int type);
	
	public void log(String id, String msg, int type);
	
	public List<SearchErrorLog> getErrorLogs(int pageNo, int pageSize, String pid, String bt, String et);
}
