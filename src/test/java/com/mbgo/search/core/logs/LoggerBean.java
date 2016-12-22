/*
* 2014-12-10 下午2:11:57
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.logs;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoggerBean {

	private Map<String, Integer> hourMap = new LinkedHashMap<String, Integer>();
	private String name = "";
	private long totalCost = 0;
	private long counter = 0;
	private long minCost = 10000;
	private long maxCost = 0;
	public LoggerBean(String name) {
		this.name = name;
	}
	public void cost(long cost) {
		totalCost += cost;
		counter ++;
		if(minCost > cost) {
			minCost = cost;
		}
		if(maxCost < cost) {
			maxCost = cost;
		}
	}
	
	public void logHourTimes(String hour) {
		Integer times = hourMap.get(hour);
		if(times == null) {
			hourMap.put(hour, 1);
		} else {
			hourMap.put(hour, times + 1);
		}
	}
	public void show() {
		System.out.println("");
		System.out.println("接口," + name);
		System.out.println("总次数," + counter);
		System.out.println("总耗时（ms）," + totalCost);
		System.out.println("最小耗时（ms）," + minCost);
		System.out.println("最大耗时（ms）," + maxCost);
		System.out.println("平均耗时（ms/次）," + totalCost / (float)counter);
		System.out.println();
	}
	public void showHourInfo() {
		System.out.println("小时,次数");
		for(Map.Entry<String, Integer> en : hourMap.entrySet()) {
			System.out.println(en.getKey() + "," + en.getValue());
		}
	}
	public long getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(long totalCost) {
		this.totalCost = totalCost;
	}
	public long getCounter() {
		return counter;
	}
	public void setCounter(long counter) {
		this.counter = counter;
	}
	public long getMinCost() {
		return minCost;
	}
	public void setMinCost(long minCost) {
		this.minCost = minCost;
	}
	public long getMaxCost() {
		return maxCost;
	}
	public void setMaxCost(long maxCost) {
		this.maxCost = maxCost;
	}
	
}
