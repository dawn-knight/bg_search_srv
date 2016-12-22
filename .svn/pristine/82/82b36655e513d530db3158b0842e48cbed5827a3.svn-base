/*
* 2014-9-30 下午1:55:30
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.dataetl.page;

public class PageManager {

	private long totalRecords = 0;
	private long oneTimeNum = 3000;
	private long currentFirst = -1;
	public PageManager(long t) {
		this.totalRecords = t;
	}
	public PageManager(long total, long perNum) {
		this.totalRecords = total;
		this.oneTimeNum = perNum;
	}
	
	public boolean hasNextPage() {
		if(currentFirst < 0) {
			currentFirst = 0;
		} else {
			currentFirst += oneTimeNum;
		}
		return currentFirst < totalRecords;
	}
	
	public long getFirst() {
		return currentFirst;
	}
	
	public long getMax() {
		return oneTimeNum;
	}
	
	public int getToIndex() {
		long temp = currentFirst + oneTimeNum;
		return (int) Math.min(temp, totalRecords);
	}
}
