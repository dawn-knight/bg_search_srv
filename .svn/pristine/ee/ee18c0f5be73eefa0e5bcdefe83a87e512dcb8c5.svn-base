package com.mbgo.search.core.tools.alsolike.device;

import com.mbgo.search.core.tools.alsolike.device.sttic.DeviceWeight;


/**
 * 童装分类加权器
 * @author HQ01U8435
 *
 */
public class ChildrenDevice extends Device {
	
	public ChildrenDevice() {
		super(TXT_BASE_PATH + "children.txt",
				"童装,男童,女童,儿童,童");
	}
	
	public String toString() {
		return "ChildrenDevice 儿童";
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
