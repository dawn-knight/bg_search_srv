/*
* 2014-11-18 上午11:55:51
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.bean.indexthrd;

public class TimeCaculator {

	private String name;
	
	private long max = 0;
	private long min = 10000;
	private int counter = 0;
	private long total = 0;
	private long temp;
	public TimeCaculator(String name) {
		this.name = name;
	}
	public void start() {
		temp = System.currentTimeMillis();
	}
	public void end() {
		long delt = System.currentTimeMillis() - temp;
		total += delt;
		if(delt > max) {
			max = delt;
		} else {
			if(delt < min) {
				min = delt;
			}
		}
		counter ++;
	}
	public void show() {
		System.out.println("*************************************");
		System.out.println(name);
		System.out.println("总数量：" + counter);
		System.out.println("总耗时：" + total);
		System.out.println("最大耗时：" + max);
		System.out.println("最小耗时：" + min);
		System.out.println("平均耗时：" + (total / counter));
		System.out.println("*************************************");
	}
}
