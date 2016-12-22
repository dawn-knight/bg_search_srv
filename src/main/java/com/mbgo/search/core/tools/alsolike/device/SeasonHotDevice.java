package com.mbgo.search.core.tools.alsolike.device;


/**
 * 春夏装分类加权器
 * @author HQ01U8435
 *
 */
@Deprecated
public class SeasonHotDevice extends Device {
	
	public SeasonHotDevice() {
		super(TXT_BASE_PATH + "season_chunxia.txt", "春,夏");
	}

	public String toString() {
		return "SeasonHotDevice 春夏";
	}
}
