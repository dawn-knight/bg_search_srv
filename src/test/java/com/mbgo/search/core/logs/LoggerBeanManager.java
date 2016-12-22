/*
* 2014-12-10 下午2:21:31
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.logs;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class LoggerBeanManager {

	private Map<String, LoggerBean> logMap = new HashMap<String, LoggerBean>();
	public LoggerBeanManager() {
	}
	public void cost(String line) {
		String[] vs = line.split("[=|\\s]");
		if(vs.length == 3) {
			String key = vs[1].trim();
			String v = vs[2].trim();
			if(StringUtils.isNumeric(v)) {
				LoggerBean bean = logMap.get(key);
				if(bean != null) {
					bean.cost(Long.parseLong(v));
				} else {
					bean = new LoggerBean(key);
					bean.cost(Long.parseLong(v));
					logMap.put(key, bean);
				}
				
				int hIndex = line.indexOf(':');
				String hourStr = line.substring(0, hIndex);
				bean.logHourTimes(hourStr);
			}
		}
	}
	
	public void show() {
		for(LoggerBean b : logMap.values()) {
			b.show();
			b.showHourInfo();
			System.out.println("********************************************************");
		}
	}
}
