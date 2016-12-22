package com.mbgo.search.core.tools.alsolike.device;

import com.mbgo.search.core.tools.alsolike.device.sttic.DeviceWeight;

/**
 * 女装分类加权器
 * @author HQ01U8435
 *
 */
public class WomanDevice extends Device {
	
	public WomanDevice() {
		super(TXT_BASE_PATH + "woman.txt", "女");
	}

	public String toString() {
		return "WomanDevice 女士";
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
