package com.mbgo.search.core.tools.alsolike.device;

@Deprecated
public class NoSeasonDevice implements IWeightingDevice {

	@Override
	public int contain(String word) {
		return 0;
	}

	@Override
	public String getQueryWord() {
		return "";
	}

	@Override
	public int getQueryWeight() {
		return 0;
	}

	@Override
	public void loadWord(String w) {
	}
	@Override
	public int getKeyWeight() {
		return 1;
	}
}
