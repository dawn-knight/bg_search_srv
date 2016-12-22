/*
* 2014-12-14 下午4:24:14
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

public class LockService {

	private Boolean isTaskRunning = false;
	
	public boolean beginTask() {
		synchronized (isTaskRunning) {
			if(isTaskRunning) {	//如果已经执行，则返回false，表示启动失败
				return false;
			} else {
				isTaskRunning = true;
				return true;
			}
		}
	}
	
	public void finishTask() {
		synchronized (isTaskRunning) {
			isTaskRunning = false;
		}
	}
}
