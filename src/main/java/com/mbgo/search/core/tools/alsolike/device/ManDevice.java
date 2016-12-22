package com.mbgo.search.core.tools.alsolike.device;

import com.mbgo.search.core.tools.alsolike.device.sttic.DeviceWeight;

/**
 * 男装分类加权器
 * @author HQ01U8435
 *
 */
public class ManDevice extends Device {
	
	public ManDevice() {
		super(TXT_BASE_PATH + "man.txt", "男");
	}
	
	public String toString() {
		return "ManDevice 男士";
	}
	@Override
	public int getQueryWeight() {
		return DeviceWeight.PEOPLE;
	}
	@Override
	public int getKeyWeight() {
		return DeviceWeight.KeyWeight.PEOPLE;
	}
}
