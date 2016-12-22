package com.mbgo.search.core.tools.alsolike.device;

/**
 * 秋冬装分类加权器
 * @author HQ01U8435
 *
 */
@Deprecated
public class SeasonColdDevice extends Device {

	public SeasonColdDevice() {
		super(TXT_BASE_PATH + "season_qiudong.txt", "秋, 冬, 羽绒");
	}

	public String toString() {
		return "SeasonColdDevice 秋冬";
	}
}
