/*
* 2014-11-7 上午9:28:17
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.use4;

import org.junit.Test;

public class NanoTimeTest {
	public static float eps = 0.00000000001f;
	public float sqrt1(float a) {
		if(a <= 0) {
			return -1;
		}
		float left = 0, right = a;
		float rs = (left + right) / 2;
		float lastRs = 0;
		do {
			if(rs * rs > a) {
				right = rs;
			} else {
				left = rs;
			}
			lastRs = rs;
			
			rs = (left + right) / 2;
			
		} while (Math.abs(rs - lastRs) > eps);
		return rs;
	}
	
	public float sqrtByNewton(float x) {

//		i = 0x5f375a86- (i>>1); // gives initial guess y0
//		1597463174
		float val = x;// 最终
		float last;// 保存上一个计算的值
		do {
			last = val;
			val = (val + x / val) / 2;
		} while (Math.abs(val - last) > eps);
		return val;
	}
	
//	float InvSqrt(float x)
//	{
//		float xhalf = 0.5f*x;
//		int i = *(int*)&x; // get bits for floating VALUE 
//		i = 0x5f375a86- (i>>1); // gives initial guess y0
//		x = *(float*)&i; // convert bits BACK to float
//		x = x*(1.5f-xhalf*x*x); // Newton step, repeating increases accuracy
//		return x;
//	} 

	@Test
	public void test1() {
		float num = 65535;
		long beginTime = System.nanoTime();
		System.out.print(sqrt1(num));
		System.out.print("; 二分法: " + (System.nanoTime() - beginTime) + " ns");
		System.out.println();

		beginTime = System.nanoTime();
		System.out.print(Math.sqrt(num));
		System.out.print("; Math.sqrt: " + (System.nanoTime() - beginTime) + " ns");
		System.out.println();
		
		beginTime = System.nanoTime();
		System.out.print(sqrtByNewton(num));
		System.out.print("; sqrtByNewton: " + (System.nanoTime() - beginTime) + " ns");
		System.out.println();
		
	}
}
