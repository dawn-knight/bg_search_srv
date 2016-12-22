/*
* 2014-10-14 下午1:31:45
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.use4;

import org.junit.Test;

public class StringIndexOfTest {
	public int[] preProcess(String sb) {
		int[] rs = new int[sb.length()];
		int len = sb.length();
		for(int i = len - 1; i > 0; i --) {
			int maxJ = 0;
			for(int j = 0, delt = i - 1; j < i && i - delt + j < i && delt >= 0;) {
				char main = sb.charAt(j);
				int secd = i - delt + j;
				char mainAfter = sb.charAt(secd);
				if(main == mainAfter) {
					if(secd == i - 1 && delt > maxJ) {
						maxJ = delt;
					}
					j ++;
				} else {
					j = 0;
					delt --;
				}
			}
			rs[i] = maxJ;
		}
		return rs;
	}

	static long totalTimes = 0;
	public int indexOf(String src, String sub, int fromIndex) {
		int[] test = preProcess(sub);
		
		int len = src.length();
		int subLen = sub.length();
		int j = 0;
		int lastFrom = 0;
		int j1 = 0;
		for(int i = Math.max(0, fromIndex); i < len;) {
			lastFrom = i;//记录此次主字符串开始遍历的下标
			while(j < subLen && i < len) {
				totalTimes ++;
				char srcChar = src.charAt(i);
				char subChar = sub.charAt(j);
				if(srcChar != subChar) {
//					System.out.println("lastFrom="+lastFrom+";src["+i+"]" + src.charAt(i) + " - sub["+j+"]" + sub.charAt(j) + " -----");
					
					/* *******kmp方法********************/
					j = test[j];
					if(j == 0 && j1 == 0) {//如果当前的j`是0，而且上一次的j`也是0
						i = lastFrom + 1;
					}
					j1 = j;
					/* ***************************/

					/* *********完全回溯方法******************/
//					j = 0;
//					i = lastFrom + 1;
					/* ***************************/
					
					
					break;
				} else {
//					System.out.println("lastFrom="+lastFrom+";src["+i+"]" + src.charAt(i) + " - sub["+j+"]" + sub.charAt(j) + "");
					if(j == subLen - 1) {	//匹配结束
						int start = i - subLen + 1;
						return start;
					}
					i ++;
					j ++;
				}
			}
		}
		return -1;
	}

	String src1 = "ababaabaababacbbacbababacbbababacbaababacbbaababacbcbababacbbacbbaabababacbabacbbababacb" +
				"ababaabaababacbbacbababacbbababacbaababacbbaababacbcbababacbbacbbaabababacbabacbbababacb" +
				"ababaabaababacbbacbababacbbababacbaababacbbaababacbcbababacbbacbbaabababacbabacbbababacb" +
				"ababaabaababacbbacbababacbbababacbaababacbbaababacbcbababacbbacbbaabababacbabacbbababacb" +
				"ababaabaababacbbacbababacbbababacbaababacbbaababacbcbababacbbacbbaabababacbabacbbababacb" +
				"ababaabaababacbbacbababacbbababacbaababacbbaababacbcbababacbbacbbaabababacbabacbbababacb";
	String sub = "bbababacbaababacbbaaba";
	@Test
	public void testString1() {
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 10000; i ++) {
			sb.append(src1);
		}
		src1 = sb.toString();
		int fromIndex = 0;
		int rs = 0;
		int count = 0;
		int total = 0;
		do {
			total ++;
			rs = src1.indexOf(sub, fromIndex);
			if(indexOf(src1, sub, fromIndex) == rs) {
				count ++;
			}
			fromIndex = rs + 1;
		} while(rs >= 0);
		System.out.println("一共比对"+total+"次，相同结果"+count+"次");
	}

	@Test
	public void testTime() {
		String src = "bbababacbaababacbbababacbaababacbbaaba";
		String sub = "bbababacbaababacbbaaba";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100000; i++) {
			sb.append(src);
		}
		src = sb.toString();
		
		int fromIndex = 0;
		int rs = 0;
		int count = 0;
		int total = 0;
		long totalTime = 0;
		do {
			total++;
			rs = src.indexOf(sub, fromIndex);
			long b = System.nanoTime();
			int temp = indexOf(src, sub, fromIndex);
			totalTime += (System.nanoTime() - b);
			if (temp == rs) {
				count++;
			}
			fromIndex = rs + 1;
		} while (rs >= 0);
		System.out.println("一共比对" + total + "次，相同结果" + count + "次");
		System.out.println("源字符串长度：" + src.length());
		System.out.println("目标字符串长度：" + sub.length());
		System.out.println("耗时：" + totalTime);
		System.out.println("比较次数：" + totalTimes);
	}
	
	@Test
	public void testSys() {
		long b = System.nanoTime();
		System.out.println(src1.indexOf(sub, 0));
		System.out.println(System.nanoTime() - b);

		b = System.nanoTime();
		System.out.println(indexOf(src1, sub, 0));
		System.out.println(System.nanoTime() - b);
	}
}
