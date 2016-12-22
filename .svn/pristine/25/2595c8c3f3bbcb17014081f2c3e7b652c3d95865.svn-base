/*
* 2014-12-16 上午9:36:53
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbsearch.bean.SearchErrorLog;
import com.mbgo.mybatis.mbsearch.mapper.SearchErrorLogMapper;
import com.mbgo.search.core.service.SearchErrorLogService;

@Service("searchErrorLogService")
public class SearchErrorLogServiceImpl implements SearchErrorLogService {
	
	@Override
	public void log(String msg, int type) {
		logInfo("", msg, type);
	}
	
	@Override
	public void log(String id, String msg, int type) {
		logInfo(id, msg, type);
	}
	
	private void logInfo(String id, String msg, int type) {
		try {
			if(StringUtils.isBlank(msg)) {
				return;
			}
			if(msg.length() > 1000) {
				msg = msg.substring(0, 1000);
			}
			
			SearchErrorLog errorLog = new SearchErrorLog();
			
			errorLog.setAddTime(new Date());
			errorLog.setLogMsg(msg);
			errorLog.setLogType(type);
			errorLog.setProductId(id);
			
			searchErrorLogMapper.insert(errorLog);
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	@Override
	public List<SearchErrorLog> getErrorLogs(int pageNo, int pageSize,
			String pid, String bt, String et) {
		try {
			long max = pageSize > 0 && pageSize < 100 ? pageSize : 50;
			long first = pageNo > 0 ? (pageNo - 1) * max : 0;
			MybatisBean option = new MybatisBean(first, max);
			
			if(StringUtils.isNotBlank(pid)) {
				if(pid.indexOf(",") > 0) {
					String[] pids = pid.split(",");
					option.setParams(Arrays.asList(pids));
				} else {
					option.setPid(pid);
				}
			}
			
			option.setBt(bt);
			option.setEt(et);
			
			return searchErrorLogMapper.getErrorLogs(option);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return new ArrayList<SearchErrorLog>(0);
	}
	
	@Autowired
	private SearchErrorLogMapper searchErrorLogMapper;

	private static Logger log = LoggerFactory.getLogger(SearchErrorLogServiceImpl.class);
}
