package com.mbgo.search.core.tools.alsolike;

import com.mbgo.search.core.tools.alsolike.device.Device;

public class StopWords extends Device {

	public StopWords() {
		super(TXT_BASE_PATH + "stopWords.txt", "");
	}
	
	public boolean isStop(String word) {
		if(word == null || word.trim().isEmpty()) {
			return false;
		}
		return contain(word) > 0;
	}

}
