package com.mbgo.search.core.tools.alsolike.device;

/**
 * 无法分析出关键字中的人群时，默认人群分类加权器
 * @author HQ01U8435
 *
 */
public class NoPeopleDevice implements IWeightingDevice {

	@Override
	public int contain(String word) {
		return 0;
	}

	public String toString() {
		return "NoPeopleDevice 未确定人群";
	}

	@Override
	public String getQueryWord() {
		return "";
	}

	@Override
	public int getQueryWeight() {
		
		return 5;
	}
	@Override
	public void loadWord(String w) {
	}
	@Override
	public int getKeyWeight() {
		return 1;
	}
}
