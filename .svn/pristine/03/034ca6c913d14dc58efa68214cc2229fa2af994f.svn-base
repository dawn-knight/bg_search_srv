/*
* 2014-10-13 下午4:06:27
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.filter.color;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mbgo.search.core.filter.itf.FilterBeanConvertor;
import com.mbgo.search.core.filter.itf.FilterCaculater;
import com.mbgo.search.core.quartz.AuxiliaryDataRefresher;

public class ColorCaculator extends FilterCaculater {
	private List<ColorBean> colorBeans = new ArrayList<ColorBean>();
	
	/**
	 * 存放色系code到色系名称的映射值
	 */
//	private static Map<String, String> SERIES_CODE_TO_NAME = new HashMap<String, String>();
	
//	public static void initSeriesMapper(List<ColorBean> cbs) {
//		if(cbs != null) {
//			for(ColorBean cb : cbs) {
//				SERIES_CODE_TO_NAME.put(cb.getColorInterval(), cb.getColorName());
//			}
//		}
//	}
	
	@Override
	public List<? extends FilterBeanConvertor> getConvertor() {
		
		return colorBeans;
	}
	
	public void addSeriesInfo(String code, long count) {
		ColorBean colorBean = new ColorBean();
		colorBean.setColorInterval(code);
		colorBean.setCount(count);
		String seriesName = AuxiliaryDataRefresher.getColorSystemNameByCode(code);
		if(StringUtils.isNotBlank(seriesName)) {
			colorBean.setColorName(seriesName);
		} else {
			colorBean.setColorName("其它");
		}
		colorBeans.add(colorBean);
	}

	public static ColorBean findColorBean(String colorSeriesName) {
		return null;
	}
	public static String getColorSeriesName(String color) {
		return null;
	}

	@Override
	public boolean isSortedByCount() {
		return true;
	}
}
